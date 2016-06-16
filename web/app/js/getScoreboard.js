/********************************************************************************
 ********************************************************************************
	File Name: 
		getScoreboard.js
	Created by: 
		Nicos Kasenides (nkasenides@uclan.ac.uk / hfnovember@hotmail.com) 
		For InSPIRE - UCLan Cyprus
		June 2016
	Description:
		Contains functions to get scoreboard data for the final (post-finish)
		and intermediate (pre-finish) scoreboards.
*********************************************************************************		
*********************************************************************************/




/********************************************************************************/
//Makes a server request and dynamically creates the scoreboard for the final scoreboard page.
function getScoreboard() {
	
	deleteCookie("THCWebApp-sessionID");
	deleteCookie("THCWebApp-playerName");
	deleteCookie("THCWebApp-categoryName");
	
	var currentPlayerName = fetchGetVariable("playerName");
	var currentPlayerRank; var currentPlayerScore;
	
	document.getElementById("loader").style.display = "block";
	document.getElementById("container").style.display = "none";
	
	var xhttp = new XMLHttpRequest();
  	xhttp.onreadystatechange = function() {
		if (xhttp.readyState == 4 && xhttp.status == 200) {
			var jsonData = JSON.parse(xhttp.responseText);
			var statusItem = jsonData.status;
			if (statusItem == "OK") {
				var data = jsonData.scoreBoard;
				var list = document.getElementById('scoreboardList');
				for(var i in data) {
					if (data[i].playerName == currentPlayerName) {
						currentPlayerRank = data[i].rank;
						currentPlayerScore = data[i].score;
					}//end if same player
					var entry = document.createElement('li');
					var scorebox = document.createElement('div');
					var playerName = document.createElement('div');
					var rank = document.createElement('div');
					var appID = document.createElement('span');
					appID.innerHTML = data[i].appID;
					appID.className = "tooltipText";
					playerName.innerHTML = data[i].playerName;
					rank.innerHTML = data[i].rank;
					if (data[i].rank > 11) entry.style.borderTopStyle = "dotted";
					if (data[i].playerName == currentPlayerName) entry.className += "currentPlayer tooltip";
					else entry.className += "tooltip";
					var timeFinished = document.createElement('small');
					timeFinished.innerHTML = "<br>Time since start: " + timestampToTime(data[i].finishTime);
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
					entry.appendChild(appID);
					list.appendChild(entry);
				}//end for
				var rankSuffix = getSuffix(currentPlayerRank);
				document.getElementById("message").innerHTML = "You scored " + currentPlayerScore + " points and ranked " + rankSuffix + ".";
			}//end if OK
			else alert(jsonData.status + " " + jsonData.message);
			document.getElementById("loader").style.display = "none";
			document.getElementById("container").style.display = "block";
		}//end if ready
  	};//end if function()
  	xhttp.open("GET", "https://uclan-thc.appspot.com/api/json/secure/scoreBoard?session=" + sessionID + "&sorted", true);
  	xhttp.send();	
}//end getScoreboard()
/********************************************************************************/

/********************************************************************************/
//Makes a server request and dynamically creates the intermediary scoreboard (pre-finish).
function getScoreboardAsPopup() {
	var sessionID;
	var currentPlayerName = getCookie("THCWebApp-playerName");
	if (cookieExists("THCWebApp-sessionID")) sessionID = getCookie("THCWebApp-sessionID");
	else document.location.href = "index.html";
	
	var xhttp = new XMLHttpRequest();
  	xhttp.onreadystatechange = function() {
		if (xhttp.readyState == 4 && xhttp.status == 200) {
			var jsonData = JSON.parse(xhttp.responseText);
			var statusItem = jsonData.status;
			if (statusItem == "OK") {
				var data = jsonData.scoreBoard;
				var list = document.getElementById('scoreboardList');
				while(list.firstChild) list.removeChild(list.firstChild); //Empty the list first (for update)
				for(var i in data) {
					var entry = document.createElement('li');
					var scorebox = document.createElement('div');
					var playerName = document.createElement('div');	
					var rank = document.createElement('div');
					playerName.innerHTML = data[i].playerName;
					rank.innerHTML = data[i].rank;
					if (data[i].rank > 11) entry.style.borderTopStyle = "dotted";
					if (data[i].playerName == currentPlayerName) entry.className += "currentPlayer";
					var clearfloat = document.createElement('div');
					scorebox.innerHTML = data[i].score + " Pts";
					scorebox.className = "scoreBox";
					playerName.className = "playerName";
					clearfloat.className = "clearFloat";
					rank.className = "rank";
					entry.appendChild(playerName);
					entry.appendChild(rank);
					entry.appendChild(scorebox);
					entry.appendChild(clearfloat);
					list.appendChild(entry);
				}//end for
			}//end if OK
			else alert(jsonData.status + " " + jsonData.message);
		}//end if ready
  	};//end if function()
  	xhttp.open("GET", "https://uclan-thc.appspot.com/api/json/secure/scoreBoard?session=" + sessionID + "&sorted", true);
  	xhttp.send();	
}//end getScoreboardAsPopup()
/********************************************************************************/