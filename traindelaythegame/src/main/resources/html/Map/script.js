var map = L.map("map");

var stopMarkers = [];

var addCords = false;
var areaPolygon;
var cordListArray = [];
var markers = []; // keep references to markers so we can update/remove them

var Stadia_AlidadeSmooth = L.tileLayer("https://tiles.stadiamaps.com/tiles/alidade_smooth/{z}/{x}/{y}{r}.{ext}", {
	minZoom: 0,
	maxZoom: 20,
	attribution: '&copy; <a href="https://www.stadiamaps.com/" target="_blank">Stadia Maps</a> &copy; <a href="https://openmaptiles.org/" target="_blank">OpenMapTiles</a> &copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors',
	ext: "png",
}).addTo(map);

function getAllMaps() {
	fetch("/api/v1/gamemaps")
		.then((response) => response.json())
		.then((data) => {
			console.log("Maps data:", data);

			var gameMapList = document.getElementById("gameMapsList");
			gameMapList.innerHTML = "";

			data.forEach((gameMap) => {
				var div = document.createElement("div");
				div.className = "mapItem";

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

function addCordsFunction(e) {
	var lat = e.latlng.lat;
	var lng = e.latlng.lng;

	// Add to our coordinates array
	cordListArray.push([lat, lng]);

	// Create a draggable marker and keep reference
	var marker = L.marker([lat, lng], { draggable: true }).addTo(map);
	markers.push(marker);

	// When marker is dragged, update the corresponding coordinate and polygon
	marker.on("drag", function (ev) {
		var m = ev.target;
		var pos = m.getLatLng();
		var idx = markers.indexOf(m);
		if (idx !== -1) {
			cordListArray[idx] = [pos.lat, pos.lng];
			updateCordsDisplay();
			redrawPolygon();
		}
	});

	updateCordsDisplay();
	redrawPolygon();
}

function updateCordsDisplay() {
	var list = document.getElementById("cordsList");
	if (!list) return;
	list.innerHTML = "";
	cordListArray.forEach(function (c, i) {
		var item = document.createElement("div");
		item.className = "cordItem";
		item.innerText = i + 1 + ": " + c[0].toFixed(6) + ", " + c[1].toFixed(6);
		list.appendChild(item);
	});
}

function redrawPolygon() {
	if (areaPolygon) {
		map.removeLayer(areaPolygon);
		areaPolygon = null;
	}
	if (cordListArray.length > 2) {
		areaPolygon = L.polygon(cordListArray, { color: "red" }).addTo(map);
	}
}

function resetCords() {
	cordListArray = [];
	document.getElementById("cordsList").innerHTML = "";
	if (areaPolygon) {
		map.removeLayer(areaPolygon);
	}
	// remove markers we created
	markers.forEach(function (m) {
		map.removeLayer(m);
	});
	markers = [];
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
					var marker = L.marker([stop.latitude, stop.longitude]).addTo(map).bindPopup(`<b>${stop.name}</b><br>Type: ${stop.type}`);
					stopMarkers.push(marker);
				});
			});
	} else {
		stopMarkers.forEach(function (marker) {
			map.removeLayer(marker);
		});
		stopMarkers = [];
	}
}

function toggleAddCords() {
	addCords = !addCords;
	document.getElementById("toggleAddCords").innerText = addCords ? "Stop Adding Coordinates" : "Start Adding Coordinates";
	document.getElementById("toggleAddCords").style.backgroundColor = addCords ? "#f44336" : "#4CAF50";
}

map.on("move", function () {
	const center = map.getCenter();
	document.cookie = `maplat=${center.lat}; path=/`;
	document.cookie = `maplon=${center.lng}; path=/`;
	document.cookie = `mapzoom=${map.getZoom()}; path=/`;
});

map.on("click", function (e) {
	console.log("AddCords is", addCords);

	if (!addCords) return;

	addCordsFunction(e);
});

map.on("zoomend", function (e) {
	getPublicTransportStopsInView();
});

map.on("moveend", function (e) {
	getPublicTransportStopsInView();
});

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
