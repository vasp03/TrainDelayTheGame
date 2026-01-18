var map = L.map("map");

function getAllMaps() {
	fetch("/api/v1/gamemaps")
		.then((response) => response.json())
		.then((data) => {
			console.log("Maps data:", data);

			var gameMapList = document.getElementById("gameMapsList");
			gameMapList.innerHTML = "";

			data.forEach((gameMap) => {
				var div = document.createElement("div");

				var getButton = document.createElement("button");
				var deleteButton = document.createElement("button");

				getButton.onclick = () => {
					map.eachLayer(function (layer) {
						if (layer instanceof L.Polygon) {
							map.removeLayer(layer);
						}
					});

					window.location.href = `map?id=${encodeURIComponent(gameMap.id)}`;
				};

				deleteButton.onclick = () => {
					deleteMap(gameMap.name);
				};

				getButton.innerText = "Get " + gameMap.name;
				getButton.className = "mapButton";

				deleteButton.innerText = "Delete " + gameMap.name;
				deleteButton.className = "mapButton";

				div.appendChild(getButton);
				div.appendChild(deleteButton);

				gameMapList.appendChild(div);
			});
		})
		.catch((error) => console.error("Error fetching maps:", error));
}

function getSelectedMapsPolygons(mapName) {
	console.log("Fetching polygons for map:", mapName);

	fetch(`/api/v1/playarea?map=${encodeURIComponent(mapName)}`)
		.then((response) => {
			if (!response.ok) {
				throw new Error(`HTTP error! status: ${response.status}`);
			}
			return response.text();
		})
		.then((data) => {
			var splitData = data.split(";");
			var polygons = [];
			splitData.forEach((point) => {
				var latLon = point.split(",");
				if (latLon.length === 2) {
					var lat = parseFloat(latLon[0]);
					var lon = parseFloat(latLon[1]);
					if (!isNaN(lat) && !isNaN(lon)) {
						polygons.push([lat, lon]);
					}
				}
			});
			L.polygon(polygons, { color: "blue" }).addTo(map);
		})
		.catch((error) => {
			console.error("Error fetching selected map polygons:", error);
			alert(`Error fetching selected map polygons: ${error.message}`);
		});
}

function createMap() {
	var cordString = "";
	for (let i = 0; i < cordListArray.length; i++) {
		cordString += cordListArray[i][0] + "," + cordListArray[i][1] + ";";
	}

	// Remove trailing semicolon
	if (cordString.endsWith(";")) {
		cordString = cordString.slice(0, -1);
	}

	var mapName = document.getElementById("mapNameInput").value;

	if (mapName === "") {
		alert("Please enter a name for the map.");
		return;
	}

	var url = `/api/v1/gamemap?name=${encodeURIComponent(mapName)}&polygonPoints=${encodeURIComponent(cordString)}`;

	fetch(url, {
		method: "POST",
	})
		.then((response) => {
			if (response.ok) {
				resetCords();
				document.getElementById("mapNameInput").value = "";
			} else {
				alert("Error creating map");
			}

			getAllMaps();
		})
		.catch((error) => {
			console.error("Error:", error);
			alert("Error creating map.");
		});
}

function deleteMap(name) {
	fetch(`/api/v1/gamemap?name=${encodeURIComponent(name)}`, {
		method: "DELETE",
	})
		.then((response) => {
			if (response.ok) {
				getAllMaps();
			} else {
				alert("Error deleting map");
			}
		})
		.catch((error) => {
			console.error("Error:", error);
			alert("Error deleting map.");
		});
	resetCords();
}

var addCords = false;

var cordListArray = [];

var Stadia_AlidadeSmooth = L.tileLayer("https://tiles.stadiamaps.com/tiles/alidade_smooth/{z}/{x}/{y}{r}.{ext}", {
	minZoom: 0,
	maxZoom: 20,
	attribution: '&copy; <a href="https://www.stadiamaps.com/" target="_blank">Stadia Maps</a> &copy; <a href="https://openmaptiles.org/" target="_blank">OpenMapTiles</a> &copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors',
	ext: "png",
}).addTo(map);

var areaPolygon;

map.on("move", function () {
	const center = map.getCenter();
	document.cookie = `maplat=${center.lat}; path=/`;
	document.cookie = `maplon=${center.lng}; path=/`;
	document.cookie = `mapzoom=${map.getZoom()}; path=/`;
});

map.on("click", function (e) {
	if (!addCords) return;

	cordListArray.push([e.latlng.lat, e.latlng.lng]);

	var cordList = document.getElementById("cordsList").innerHTML;
	cordList += e.latlng.lat + "," + e.latlng.lng + ";\n";
	document.getElementById("cordsList").innerHTML = cordList;

	L.marker([e.latlng.lat, e.latlng.lng]).addTo(map);

	if (cordListArray.length > 2) {
		if (areaPolygon) {
			map.removeLayer(areaPolygon);
		}
		areaPolygon = L.polygon(cordListArray, { color: "red" }).addTo(map);
	}
});

map.on("zoomend", function (e) {
	getPublicTransportStopsInView();
});

map.on("moveend", function (e) {
	getPublicTransportStopsInView();
});

function resetCords() {
	cordListArray = [];
	document.getElementById("cordsList").innerHTML = "";
	if (areaPolygon) {
		map.removeLayer(areaPolygon);
	}
	map.eachLayer(function (layer) {
		if (layer instanceof L.Marker) {
			map.removeLayer(layer);
		}
	});
}

function getPublicTransportStopsInView() {
	if (map.getZoom() >= 12) {
		minLong = map.getBounds().getWest();
		minLat = map.getBounds().getSouth();
		maxLong = map.getBounds().getEast();
		maxLat = map.getBounds().getNorth();

		map.eachLayer(function (layer) {
			if (layer instanceof L.Marker) {
				map.removeLayer(layer);
			}
		});

		fetch(`/api/v1/stops?minLatitude=${Math.min(minLat, maxLat)}&maxLatitude=${Math.max(minLat, maxLat)}&minLongitude=${Math.min(minLong, maxLong)}&maxLongitude=${Math.max(minLong, maxLong)}`)
			.then((response) => response.json())
			.then((data) => {
				data.forEach((stop) => {
					L.marker([stop.latitude, stop.longitude]).addTo(map).bindPopup(`<b>${stop.name}</b><br>Type: ${stop.type}`);
				});
			});
	} else {
		map.eachLayer(function (layer) {
			if (layer instanceof L.Marker) {
				map.removeLayer(layer);
			}
		});
	}
}

function toggleAddCords() {
	addCords = !addCords;
	document.getElementById("toggleAddCords").innerText = addCords ? "Stop Adding Coordinates" : "Start Adding Coordinates";
	document.getElementById("toggleAddCords").style.backgroundColor = addCords ? "#f44336" : "#4CAF50";
}

window.onload = () => {
	const cookies = document.cookie.split("; ").reduce((acc, cookie) => {
		const [name, value] = cookie.split("=");
		acc[name] = value;
		return acc;
	}, {});

	if (cookies.maplat && cookies.maplon && cookies.mapzoom) {
		map.setView([parseFloat(cookies.maplat), parseFloat(cookies.maplon)], parseInt(cookies.mapzoom));
	} else {
		map.setView([0, 0], 2);
	}

	getAllMaps();
};
