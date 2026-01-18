function joinGame(userId, gameId) {
	console.log("Joining game with ID:", gameId);

	fetch("/api/v1/join", {
		method: "POST",
		headers: {
			"Content-Type": "application/x-www-form-urlencoded",
		},
		body: "userId=" + encodeURIComponent(userId) + "&gameId=" + encodeURIComponent(gameId),
	})
		.then((response) => {
			if (!response.ok) {
				console.log("Response not ok:", response);
				throw new Error(`HTTP error! status: ${response.status}`);
			}
			return response.text();
		})
		.then((data) => {
			console.log("Joined game successfully:", data);
			window.location.href = "/load?userId=" + encodeURIComponent(userId) + "&gameId=" + encodeURIComponent(gameId);
		})
		.catch((error) => {
			console.error("Error joining game:", error);
			alert("Failed to join game: " + error.message);
		});
}

function login() {
	if (!document.getElementById("playerName").value) {
		alert("Please enter a player name before logging in.");
		return;
	}

	fetch("/api/v1/createUser", {
		method: "POST",
		headers: {
			"Content-Type": "application/x-www-form-urlencoded",
		},
		body: "username=" + encodeURIComponent(document.getElementById("playerName").value),
	})
		.then((response) => {
			if (!response.ok) {
				console.log("Response not ok:", response);
				throw new Error(`HTTP error! status: ${response.status}`);
			}
			return response.text();
		})
		.then((data) => {
			console.log("User created with ID:", data);
			playerId = data;

			document.cookie = "playerId=" + encodeURIComponent(playerId) + "; path=/";

			alert("Logged in successfully! Your Player ID is: " + playerId);
		})
		.catch((error) => {
			console.error("Error creating user:", error);
			alert("Failed to create user: " + error.message);
		});
}

function startNewGame() {
	if (!document.getElementById("playerName").value) {
		alert("Please enter a player name before starting a new game.");
		return;
	}

	fetch("/api/v1/createUser", {
		method: "POST",
		headers: {
			"Content-Type": "application/x-www-form-urlencoded",
		},
		body: "username=" + encodeURIComponent(document.getElementById("playerName").value),
	})
		.then((response) => {
			if (!response.ok) {
				console.log("Response not ok:", response);
				throw new Error(`HTTP error! status: ${response.status}`);
			}
			return response.text();
		})
		.then((data) => {
			console.log("User created with ID:", data);
			playerId = data;

			createGame();
			joinGame(playerId, gameId);
		})
		.catch((error) => {
			console.error("Error creating user:", error);
			alert("Failed to create user: " + error.message);
		});
}

var playerId = -1;
var gameId = -1;

function createGame() {
	fetch("/api/v1/createGame", {
		method: "POST",
	})
		.then((response) => {
			if (!response.ok) {
				console.log("Response not ok:", response);
				throw new Error(`HTTP error! status: ${response.status}`);
			}
			return response.text();
		})
		.then((data) => {
			console.log("Game created:", data);
			gameId = data;
		})
		.catch((error) => {
			console.error("Error creating game:", error);
			alert("Failed to create game: " + error.message);
		});
}

function getAllGames() {
	fetch("/api/v1/getAllGames")
		.then((response) => {
			if (!response.ok) {
				throw new Error(`HTTP error! status: ${response.status}`);
			}
			return response.json();
		})
		.then((data) => {
			console.log("Games fetched:", data);

			const gameListDiv = document.getElementById("gameList");
			gameListDiv.innerHTML = "";

			if (data && Array.isArray(data) && data.length > 0) {
				data.forEach((game) => {
					const gameButton = document.createElement("button");

					gameButton.className = "selectGameButton";
					gameButton.textContent = `Game Name: ${game.gameName},${game.id}`;
					gameButton.onclick = () => joinGame(game.id);
					gameListDiv.appendChild(gameButton);
				});
			} else {
				gameListDiv.innerHTML = "<p>No games available</p>";
			}
		})
		.catch((error) => {
			console.error("Error fetching games:", error);
			const gameListDiv = document.getElementById("gameList");
			gameListDiv.innerHTML = "<p>Error loading games</p>";
		});
}

window.onload = function () {
	getAllGames();
};
