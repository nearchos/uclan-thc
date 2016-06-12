function getScoreboard() {
	
	deleteCookie("THCWebApp-sessionID");
	deleteCookie("THCWebApp-playerName");
	
	var xhttp = new XMLHttpRequest();
  	xhttp.onreadystatechange = function() {
    if (xhttp.readyState == 4 && xhttp.status == 200) {
		var jsonData = JSON.parse(xhttp.responseText);
		var statusItem = jsonData.status;
		if (statusItem == "OK") {
			var data = jsonData.scoreBoard;
			var list = document.getElementById('scoreboardList');
			for(var i in data) {
				var entry = document.createElement('li');
				var scorebox = document.createElement('div');
				var playerName = document.createElement('div');
				playerName.innerHTML = data[i].playerName;
				var timeFinished = document.createElement('small');
				timeFinished.innerHTML = "<br>Finished at: " + data[i].finishTime;
				var clearfloat = document.createElement('div');
				scorebox.innerHTML = data[i].score + " Pts";
				scorebox.className += "scoreBox";
				playerName.className += "playerName";
				timeFinished.className += "timeFinished";
				clearfloat.className += "clearFloat";
				entry.appendChild(playerName);
				entry.appendChild(timeFinished);
				entry.appendChild(scorebox);
				entry.appendChild(clearfloat);
				list.appendChild(entry);
			}//end for
		}//end if OK
		else if (statusItem != "OK") alert(jsonData.status + " " + jsonData.message);
    }//end if ready
  	};//end if function()
  	xhttp.open("GET", "https://uclan-thc.appspot.com/api/json/secure/scoreBoard?session=" + sessionID + "&sorted", true);
  	xhttp.send();	
}//end getScoreboard()

/***********************************************************************/

function getScoreboardAsPopup() {
	var sessionID;
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
			for(var i in data) {
				var entry = document.createElement('li');
				var scorebox = document.createElement('div');
				var playerName = document.createElement('div');
				playerName.innerHTML = data[i].playerName;
				var clearfloat = document.createElement('div');
				scorebox.innerHTML = data[i].score + " Pts";
				scorebox.className += "scoreBox";
				playerName.className += "playerName";
				clearfloat.className += "clearFloat";
				entry.appendChild(playerName);
				entry.appendChild(scorebox);
				entry.appendChild(clearfloat);
				list.appendChild(entry);
			}//end for
		}//end if OK
		else if (statusItem != "OK") alert(jsonData.status + " " + jsonData.message);
    }//end if ready
  	};//end if function()
  	xhttp.open("GET", "https://uclan-thc.appspot.com/api/json/secure/scoreBoard?session=" + sessionID + "&sorted", true);
  	xhttp.send();	
}//end getScoreboardAsPopup()