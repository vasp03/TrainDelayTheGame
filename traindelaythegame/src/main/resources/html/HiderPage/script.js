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

var map = L.map("map").setView([51.505, -0.09], 13);

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
};
