/********************************************************************************
	File Name: 
		getScoreboard.js
	Description:
		Contains functions to get scoreboard data for the final (post-finish)
		and intermediate (pre-finish) scoreboards.
*********************************************************************************/


/**
 * Makes a server request and dynamically creates the scoreboard for the final scoreboard page.
 */
function getScoreboard() {

    var SESSION_ID = getCookie(COOKIE_SESSION_ID);
	
	deleteCookie(COOKIE_SESSION_ID);
	deleteCookie(COOKIE_PLAYER_NAME);
	deleteCookie(COOKIE_CATEGORY_NAME);
    deleteCookie(COOKIE_NUM_OF_QUESTIONS);
	
	var currentPlayerName = fetchGetVariable("playerName");
	var currentPlayerRank; var currentPlayerScore;
	
	document.getElementById("loader").style.display = "block";
	document.getElementById("container").style.display = "none";

	var currentPlayerCompletion = false;
	
	var xhttp = new XMLHttpRequest();
  	xhttp.onreadystatechange = function() {
		if (xhttp.readyState == 4 && xhttp.status == 200) {
			var jsonData = JSON.parse(xhttp.responseText);
			var statusItem = jsonData.status;
			if (statusItem == "OK") {
				var data = jsonData.leaderboard;
				var list = document.getElementById('scoreboardList');
				for(var i in data) {
					if (data[i].player == currentPlayerName) {
						currentPlayerRank = Number(i) + 1;
						currentPlayerScore = data[i].score;
						currentPlayerCompletion = (data[i].completionTime > 0);
					}//end if same player
					var entry = document.createElement('li');
					var scorebox = document.createElement('div');
					var playerName = document.createElement('div');
					var rank = document.createElement('div');
					// var appID = document.createElement('span');
					// appID.innerHTML = data[i].appID;
					// appID.className = "tooltipText";
					playerName.innerHTML = data[i].player;
					rank.innerHTML = Number(i) + 1;
					if (Number(i) + 1 == 11) entry.style.borderTopStyle = "dotted";
					if (data[i].player == currentPlayerName) entry.className += "currentPlayer tooltip";
					else entry.className += "tooltip";
					var timeFinished = document.createElement('small');
					timeFinished.innerHTML = "<br>Time since start: " + timestampToTime(data[i].completionTime);
					var clearfloat = document.createElement('div');
					scorebox.innerHTML = data[i].score + " Pts";
					scorebox.className = "scoreBox";
					playerName.className = "playerName";
					timeFinished.className = "timeFinished";
					clearfloat.className = "clearFloat";
					rank.className = "rank";
					entry.appendChild(playerName);
					entry.appendChild(rank);
					entry.appendChild(timeFinished);
					entry.appendChild(scorebox);
					entry.appendChild(clearfloat);
					// entry.appendChild(appID);
					list.appendChild(entry);
				}//end for
				var rankSuffix = getSuffix(currentPlayerRank);
				document.getElementById("message").innerHTML = "You scored " + currentPlayerScore + " points and ranked " + rankSuffix + ".";

				//Show reward:
                var qrCode = document.getElementById("qrCode");
                if (!currentPlayerCompletion) {
                    qrCode.innerHTML = "<b>You must attempt all questions first.</b>";
                    qrCode.style.display = "block";
                }
                else if (currentPlayerScore < 30) {
                    qrCode.innerHTML = "<b>You have not answered enough questions correctly.</b>";
                    qrCode.style.display = "block";
                }
                else {
                    if (SESSION_ID == null || SESSION_ID === undefined || SESSION_ID.length < 1) {
                        qrCode.innerHTML = "<b>Failed to get QR Code for reward.</b>";
                        qrCode.style.display = "block";
                    }
                    else {
                        qrCode.innerHTML = "<img src=\"https://api.qrserver.com/v1/create-qr-code/?data=" + SESSION_ID + "&amp;size=200x200\" alt=\"\" title=\"\" /><p>Scan to claim your reward!</p>";
                        qrCode.style.display = "block";
                    }
                }

			}//end if OK
			else alert(jsonData.status + " " + jsonData.message);
			document.getElementById("loader").style.display = "none";
			document.getElementById("container").style.display = "block";
		}//end if ready
  	};//end if function()
  	xhttp.open("GET", API_LEADERBOARD + "?session=" + sessionID + "&sorted", true);
  	xhttp.send();	
}

/**
 * Makes a server request and dynamically creates the scoreboard during the quiz.
 */
function getScoreboardAsPopup() {
	var sessionID;
	var currentPlayerName = getCookie(COOKIE_PLAYER_NAME);
	if (cookieExists(COOKIE_SESSION_ID)) sessionID = getCookie(COOKIE_SESSION_ID);
	else document.location.href = "index.html";
	
	var xhttp = new XMLHttpRequest();
  	xhttp.onreadystatechange = function() {
		if (xhttp.readyState == 4 && xhttp.status == 200) {
			var jsonData = JSON.parse(xhttp.responseText);
			var statusItem = jsonData.status;
			if (statusItem == "OK") {
				var data = jsonData.leaderboard;
				var list = document.getElementById('scoreboardList');
				list.className += "noRadius";
				while(list.firstChild) list.removeChild(list.firstChild); //Empty the list first (for update)

                var playersRank = 0;

				for(var i in data) {
					var entry = document.createElement('li');
					var scorebox = document.createElement('div');
					var playerName = document.createElement('div');	
					var rank = document.createElement('div');
					playerName.innerHTML = data[i].player;
					rank.innerHTML = Number(i) + 1;
					if (Number(i) + 1 == 11) entry.style.borderTopStyle = "dotted";
					if (data[i].player == currentPlayerName) {
					    entry.className += "currentPlayer";
					    playersRank = Number(i) + 1;
                    }
					var clearfloat = document.createElement('div');
					scorebox.innerHTML = data[i].score + " Pts";
					scorebox.className = "scoreBox";
					playerName.className = "playerName";
					clearfloat.className = "clearFloat";
					rank.className = "rank rankFix";
					entry.appendChild(rank);
					entry.appendChild(playerName);
					entry.appendChild(scorebox);
					entry.appendChild(clearfloat);
					list.appendChild(entry);
				}//end for

                //Add an additional item showing the player's rank:
                var playersRankItem = document.createElement("li");
				playersRankItem.innerHTML = "Your position: " + getSuffix(playersRank);
				playersRankItem.className += "playerRankingMiniScoreboard";
                if (playersRank >= 4) playersRankItem.style.backgroundColor = "#FFFFFF";
                if (playersRank == 1) playersRankItem.style.backgroundColor = "#CCAC00"; //Gold
                if (playersRank == 2) playersRankItem.style.backgroundColor = "#DDDDDD"; //Silver
                if (playersRank == 3) playersRankItem.style.backgroundColor = "#cd7f32"; //Bronze
                if (playersRank < 4) playersRankItem.style.color = "white";
				list.insertBefore(playersRankItem, list.firstChild);

			}//end if OK
			else {

			}
		}//end if ready
  	};//end if function()
  	xhttp.open("GET", API_LEADERBOARD + "?session=" + sessionID + "&sorted", true);
  	xhttp.send();	
}