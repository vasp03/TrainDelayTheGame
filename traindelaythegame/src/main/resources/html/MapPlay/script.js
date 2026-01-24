var map = L.map("map").setView([0, 0], 2);

var toolsLowered = false;
var deleteAreas = false;
var showStopsOutsidePlayArea = false;
var alwaysShowStops = false;
var zoomLevel = map.getZoom();
var minZoomToShowStops = 14;
var showCoordinatesMarker = false;

var playArea = null;
var invertedPlayArea = null;
var radiusMarker = null;
var radiusCircle = null;
var stopMarkers = [];
var customArea = [];

var busStopsCheckbox = document.getElementById("busStopsCheckbox");
var trainStopsCheckbox = document.getElementById("trainStopsCheckbox");
var tramsStopsCheckbox = document.getElementById("tramStopsCheckbox");
var metrosStopsCheckbox = document.getElementById("metroStopsCheckbox");
var ferriesStopsCheckbox = document.getElementById("ferryStopsCheckbox");
var waterStopsCheckbox = document.getElementById("waterStopsCheckbox");
var otherStopsCheckbox = document.getElementById("otherStopsCheckbox");

var gpsMarker = null;
var gpsUpdater = null;
var gpsToggleButton = document.getElementById("toggleGPSButton");

var markerIcon = L.icon({
	iconUrl: "img/pin.png",
	iconSize: [40, 40], // size of the icon
	iconAnchor: [20, 40], // point of the icon which will correspond to marker's location
	popupAnchor: [0, -40], // point from which the popup should open relative to the iconAnchor
});

var coordinatesMarker = L.marker([0, 0], {
	riseOnHover: true,
	draggable: true,
});

var world = [
	[-90, -180],
	[90, -180],
	[90, 180],
	[-90, 180],
];

var latLabel = document.getElementById("coordsOutputLat");
var lonLabel = document.getElementById("coordsOutputLon");
var radiuesInput = document.getElementById("circleRadius");

async function getSelectedMapsPolygons(id) {
	const response = await fetch(`/api/v1/playarea?map=${encodeURIComponent(id)}`);

	if (!response.ok) {
		throw new Error(`HTTP error! status: ${response.status}`);
	}

	const data = await response.text();
	var splitData = data.split(";");
	var polygons = [];

	splitData.forEach((point) => {
		var latLon = point.split(",");
		if (latLon.length == 2) {
			var lat = parseFloat(latLon[0]);
			var lon = parseFloat(latLon[1]);
			if (!isNaN(lat) && !isNaN(lon)) {
				polygons.push([lat, lon]);
			}
		} else {
			console.warn("Invalid point data:", point);
		}
	});

	playArea = L.polygon(polygons, {
		color: "blue",
		fillOpacity: 0.0,
	}).addTo(map);

	invertedPlayArea = L.polygon([world, polygons], {
		color: "black",
		fillColor: "black",
		fillOpacity: 0.15,
	}).addTo(map);

	getPublicTransportStopsInView();
}

function getPublicTransportStopsInView() {
	if (playArea == null) {
		alert("Play area not loaded yet. Please choose a valid map id.");
		return;
	}

	if (map.getZoom() >= minZoomToShowStops || alwaysShowStops) {
		minLong = map.getBounds().getWest();
		minLat = map.getBounds().getSouth();
		maxLong = map.getBounds().getEast();
		maxLat = map.getBounds().getNorth();

		stopMarkers.forEach((marker) => {
			map.removeLayer(marker);
		});

		stopMarkers = [];

		var typesToExclude = [];
		if (!busStopsCheckbox.checked) typesToExclude.push("bus");
		if (!trainStopsCheckbox.checked) typesToExclude.push("rail");
		if (!tramsStopsCheckbox.checked) typesToExclude.push("tram");
		if (!metrosStopsCheckbox.checked) typesToExclude.push("metro");
		if (!ferriesStopsCheckbox.checked) typesToExclude.push("ferry");
		if (!waterStopsCheckbox.checked) typesToExclude.push("water");
		if (!otherStopsCheckbox.checked) typesToExclude.push("other");

		var excludeParam = typesToExclude.join(",");

		fetch(`/api/v1/stops?minLatitude=${Math.min(minLat, maxLat)}&maxLatitude=${Math.max(minLat, maxLat)}&minLongitude=${Math.min(minLong, maxLong)}&maxLongitude=${Math.max(minLong, maxLong)}&exclude=${encodeURIComponent(excludeParam)}`)
			.then((response) => response.json())
			.then((data) => {
				data.forEach((stop) => {
					var marker = L.marker([stop.latitude, stop.longitude], { icon: markerIcon });

					if ((playArea && playArea.contains(marker.getLatLng())) || showStopsOutsidePlayArea) {
						marker.addTo(map).bindPopup(`<b>${stop.name}</b><br>Type: ${stop.type}`);
						stopMarkers.push(marker);
					}
				});
			});
	} else {
		stopMarkers.forEach((marker) => {
			map.removeLayer(marker);
		});

		stopMarkers = [];
	}
}

function lowerTools() {
	var toolsDiv = document.getElementById("items");

	var toggleButton = document.getElementById("lowerTools");

	var upIcon = document.getElementById("up");
	var downIcon = document.getElementById("down");

	if (toolsLowered) {
		toolsDiv.style.display = "flex";

		toggleButton.style.bottom = "calc(30vh + 10px)";

		upIcon.style.display = "none";
		downIcon.style.display = "inline";
	} else {
		toolsDiv.style.display = "none";

		toggleButton.style.bottom = "10px";

		upIcon.style.display = "inline";
		downIcon.style.display = "none";
	}

	toolsLowered = !toolsLowered;
}

function toggleDelete() {
	var deleteBtn = document.getElementById("deleteButton");

	if (deleteAreas) {
		deleteBtn.style.backgroundColor = "white";
		deleteBtn.style.color = "black";
	} else {
		deleteBtn.style.backgroundColor = "red";
		deleteBtn.style.color = "white";
	}

	deleteAreas = !deleteAreas;
}

function toggleAlwaysShowStops() {
	var stopsBtn = document.getElementById("toggleStopButton");

	if (alwaysShowStops) {
		stopsBtn.style.backgroundColor = "white";
		stopsBtn.style.color = "black";
	} else {
		stopsBtn.style.backgroundColor = "green";
		stopsBtn.style.color = "white";
	}

	alwaysShowStops = !alwaysShowStops;

	if (map.getZoom() < minZoomToShowStops) {
		getPublicTransportStopsInView();
	}
}

function toggleShowStopsOutsidePlayArea() {
	var stopsBtn = document.getElementById("toggleShowOutsideStopsButton");

	if (showStopsOutsidePlayArea) {
		stopsBtn.style.backgroundColor = "white";
		stopsBtn.style.color = "black";
	} else {
		stopsBtn.style.backgroundColor = "green";
		stopsBtn.style.color = "white";
	}

	showStopsOutsidePlayArea = !showStopsOutsidePlayArea;

	if (map.getZoom() < minZoomToShowStops) {
		getPublicTransportStopsInView();
	}
}

function addSquare() {
	var center = map.getCenter();
	var bounds = map.getBounds();
	var sideLengthLat = (bounds.getNorth() - bounds.getSouth()) / 4;
	var sideLengthLon = (bounds.getEast() - bounds.getWest()) / 4;

	var squareCoords = [
		[center.lat - sideLengthLat / 2, center.lng - sideLengthLon / 2],
		[center.lat - sideLengthLat / 2, center.lng + sideLengthLon / 2],
		[center.lat + sideLengthLat / 2, center.lng + sideLengthLon / 2],
		[center.lat + sideLengthLat / 2, center.lng - sideLengthLon / 2],
	];

	customArea = L.polygon(squareCoords, {
		color: "blue",
		fillOpacity: 0.0,
	}).addTo(map);

	const corners = squareCoords.map((coord, index) => {
		const marker = L.marker(coord, { icon: markerIcon, draggable: true, fillColor: "red", fillOpacity: 1.0, riseOnHover: true }).addTo(map);
		const constCustomArea = customArea;

		marker.on("drag", () => {
			squareCoords[index] = [marker.getLatLng().lat, marker.getLatLng().lng];
			constCustomArea.setLatLngs(squareCoords);
		});

		return marker;
	});
}

function toggleCoordinatesMarker() {
	if (showCoordinatesMarker) {
		if (map.hasLayer(coordinatesMarker)) {
			map.removeLayer(coordinatesMarker);
		}
	} else {
		coordinatesMarker.addTo(map);
		coordinatesMarker.setLatLng(map.getCenter());
	}

	showCoordinatesMarker = !showCoordinatesMarker;
}

function addCircle() {
	if (radiusMarker != null) {
		map.removeLayer(radiusMarker);
		radiusMarker = null;
	}

	if (radiusCircle != null) {
		map.removeLayer(radiusCircle);
		radiusCircle = null;
	}

	var center = map.getCenter();
	var radius = parseFloat(radiuesInput.value);

	if (isNaN(radius) || radius <= 0) {
		alert("Please enter a valid positive number for the radius.");
		return;
	}

	radiusCircle = L.circle(center, {
		color: "blue",
		fillOpacity: 0.0,
		radius: radius,
	}).addTo(map);

	radiusMarker = L.marker([center.lat, center.lng], {
		draggable: true,
		riseOnHover: true,
	}).addTo(map);

	radiusMarker.on("drag", function (e) {
		radiusCircle.setLatLng(radiusMarker.getLatLng());
	});
}

function updateStopsVisibility() {}

function toggleGPSPosition() {
	if (gpsMarker != null) {
		toggleGPSButton.style.backgroundColor = "white";
		toggleGPSButton.style.color = "black";

		map.removeLayer(gpsMarker);
		gpsMarker = null;

		if (gpsUpdater != null) {
			clearInterval(gpsUpdater);
			gpsUpdater = null;
		}

		return;
	}

	toggleGPSButton.style.backgroundColor = "green";
	toggleGPSButton.style.color = "white";

	updateGPSPosition();

	gpsUpdater = setInterval(() => {
		updateGPSPosition();
	}, 5000); // Update every 5 seconds
}

function updateGPSPosition(position) {
	if (navigator.geolocation) {
		navigator.geolocation.getCurrentPosition((position) => {
			const lat = position.coords.latitude;
			const lon = position.coords.longitude;

			console.log(`GPS Position: Lat ${lat}, Lon ${lon}`);

			if (gpsMarker != null) {
				gpsMarker.setLatLng([lat, lon]);
			} else {
				gpsMarker = L.marker([lat, lon], {
					icon: L.icon({
						iconUrl: "img/gps.png",
						iconSize: [30, 30],
						iconAnchor: [15, 15],
						popupAnchor: [0, -15],
					}),
					riseOnHover: true,
				})
					.addTo(map)
					.bindPopup("GPS Position");
			}
		});
	}
}

window.onload = async () => {
	var urlParams = new URLSearchParams(window.location.search);
	var gameMapId = urlParams.get("id");

	if (!gameMapId) {
		alert("No map id provided in URL");
		return;
	}

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

	await getSelectedMapsPolygons(gameMapId);
};

map.on("zoomend", function (e) {
	if (playArea != null) {
		getPublicTransportStopsInView();
	}
});

map.on("moveend", function (e) {
	if (playArea != null) {
		getPublicTransportStopsInView();

		const center = map.getCenter();
		document.cookie = `maplat=${center.lat}; path=/`;
		document.cookie = `maplon=${center.lng}; path=/`;
		document.cookie = `mapzoom=${map.getZoom()}; path=/`;
	}
});

var Stadia_AlidadeSmooth = L.tileLayer("https://tiles.stadiamaps.com/tiles/alidade_smooth/{z}/{x}/{y}{r}.{ext}", {
	minZoom: 2,
	maxZoom: 20,
	attribution: '&copy; <a href="https://www.stadiamaps.com/" target="_blank">Stadia Maps</a> &copy; <a href="https://openmaptiles.org/" target="_blank">OpenMapTiles</a> &copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors',
	ext: "png",
}).addTo(map);

map.setMaxBounds([
	[-90, -180],
	[90, 180],
]);
map.options.maxBoundsViscosity = 1.0;

coordinatesMarker.on("drag", function (e) {
	var lat = coordinatesMarker.getLatLng().lat;
	var lon = coordinatesMarker.getLatLng().lng;

	latLabel.placeholder = "Lat: " + lat;
	lonLabel.placeholder = "Lon: " + lon;
});
