function toggleDarkMode() {
	document.cookie = "darkmode=" + (getDarkMode() ? "false" : "true") + "; path=/";

	location.reload();
}

function getDarkMode() {
	return (
		document.cookie
			.split("; ")
			.find((row) => row.startsWith("darkmode="))
			?.split("=")[1] === "true"
	);
}

var map = L.map("map");

var darkMode =
	document.cookie
		.split("; ")
		.find((row) => row.startsWith("darkmode="))
		?.split("=")[1] === "true";

if (darkMode) {
	var Stadia_AlidadeSmoothDark = L.tileLayer("https://tiles.stadiamaps.com/tiles/alidade_smooth_dark/{z}/{x}/{y}{r}.{ext}", {
		minZoom: 0,
		maxZoom: 20,
		attribution: '&copy; <a href="https://www.stadiamaps.com/" target="_blank">Stadia Maps</a> &copy; <a href="https://openmaptiles.org/" target="_blank">OpenMapTiles</a> &copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors',
		ext: "png",
	}).addTo(map);
} else {
	var Stadia_AlidadeSmooth = L.tileLayer("https://tiles.stadiamaps.com/tiles/alidade_smooth/{z}/{x}/{y}{r}.{ext}", {
		minZoom: 0,
		maxZoom: 20,
		attribution: '&copy; <a href="https://www.stadiamaps.com/" target="_blank">Stadia Maps</a> &copy; <a href="https://openmaptiles.org/" target="_blank">OpenMapTiles</a> &copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors',
		ext: "png",
	}).addTo(map);
}

map.on("move", function () {
	const center = map.getCenter();
	document.cookie = `maplat=${center.lat}; path=/`;
	document.cookie = `maplon=${center.lng}; path=/`;
	document.cookie = `mapzoom=${map.getZoom()}; path=/`;
});

map.on("zoomend", function (e) {
	getPublicTransportStopsInView();
});

map.on("moveend", function (e) {
	getPublicTransportStopsInView();
});

map.on("click", function (e) {
	console.log("Map clicked at lat: " + e.latlng.lat + "," + e.latlng.lng + ";");
});

function getPublicTransportStopsInView() {
	if (map.getZoom() >= 14) {
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

window.onload = function () {
	const cookies = document.cookie.split("; ").reduce((acc, cookie) => {
		const [name, value] = cookie.split("=");
		acc[name] = value;
		return acc;
	}, {});

	if (cookies.maplat && cookies.maplon && cookies.mapzoom) {
		map.setView([parseFloat(cookies.maplat), parseFloat(cookies.maplon)], parseInt(cookies.mapzoom));
	}

	getMapPolygonFromServer();
};

function getMapPolygonFromServer() {
	fetch("/api/v1/playarea?map=test")
		.then((response) => response.text())
		.then((data) => {
			const points = data.split(";").map((point) => {
				const [lat, lon] = point.split(",");
				return [parseFloat(lat), parseFloat(lon)];
			});

			var world = [
				[-90, -180],
				[90, -180],
				[90, 180],
				[-90, 180],
			];

			L.polygon([world, points], {
				color: "red",
				fillColor: "red",
				fillOpacity: 0.1,
			}).addTo(map);
		});
}

document.getElementById("raiseLowerFooterButton").onclick = function () {
	const footer = document.getElementById("footer");
	if (footer.style.height === "4vh") {
		document.getElementById("arrowUpDown").innerText = "▼";
		footer.style.transition = "height 0.3s ease";
		footer.style.height = "25vh";
		document.getElementById("footerContent").style.display = "flex";
	} else {
		document.getElementById("arrowUpDown").innerText = "▲";
		footer.style.transition = "height 0.3s ease";
		footer.style.height = "4vh";
		document.getElementById("footerContent").style.display = "none";
	}
};
