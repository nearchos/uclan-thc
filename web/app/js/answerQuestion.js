function updateScore() {
	var xhttp = new XMLHttpRequest();
  	xhttp.onreadystatechange = function() {
    if (xhttp.readyState == 4 && xhttp.status == 200) {
		var jsonData = JSON.parse(xhttp.responseText);
		var statusItem = jsonData.status;
		if (statusItem == "OK") {
			var score = jsonData.score;
			var scoreLabel = document.getElementById("score");
			scoreLabel.innerHTML = + score;
			if (score > 0) scoreLabel.style.color = "green";
			else if (score == 0) scoreLabel.style.color = "orange";
			else scoreLabel.style.color = "red";
		}//end if OK
		else if (statusItem != "OK") alert(jsonData.status + " " + jsonData.message);
    }//end if ready
  	};//end if function()
  	xhttp.open("GET", "https://uclan-thc.appspot.com/api/json/secure/score?session=" + sessionID, true);
  	xhttp.send();	
}//end updateScore();

/********************************************************************/

function updateQuestion() {
	var xhttp = new XMLHttpRequest();
  	xhttp.onreadystatechange = function() {
    if (xhttp.readyState == 4 && xhttp.status == 200) {
		var jsonData = JSON.parse(xhttp.responseText);
		var statusItem = jsonData.status;
		if (statusItem == "OK") {
			document.getElementById("questionContainer").style.display = "inline";
			var question = jsonData.question;
			document.getElementById("question").innerHTML = question;
			if (question.substring(0,5) == "MCQ: ") {
			//Multiple Choice Question
				question = question.substring(5, question.length);
				document.getElementById("question").innerHTML = question;
				document.getElementById("mcqForm").style.display = "inline";
				document.getElementById("textForm").style.display = "none";
			}//end if
			else {
			//Text Question
				document.getElementById("question").innerHTML = question;
				document.getElementById("mcqForm").style.display = "none";
				document.getElementById("textForm").style.display = "inline";
			}//end else
		}//end if OK
		else if (statusItem != "OK") {
			if (statusItem == "Finished session") {
				document.getElementById("questionContainer").style.display = "none";	
				window.location.href="scoreboard.html?sessionID=" + sessionID;
			}//end if finished
			else createSnackbar(jsonData.status);
		}//end if not OK
		else alert("Unexpected error - " + statusItem);
    }//end if ready
  	};//end if function()
  	xhttp.open("GET", "https://uclan-thc.appspot.com/api/json/secure/currentQuestion?session=" + sessionID, true);
  	xhttp.send();
	updateScore();	
}//end updateQuestion();

/********************************************************************/

function answerQuestionMCQ(answer) {
	updateLocation();
	var xhttp = new XMLHttpRequest();
  	xhttp.onreadystatechange = function() {
    if (xhttp.readyState == 4 && xhttp.status == 200) {
		var jsonData = JSON.parse(xhttp.responseText);
		if (jsonData.status == "OK") {
			if (jsonData.feedback == "correct,unfinished") {
				createSnackbar('✔ Correct ✔');
				updateQuestion();	
			}//end if unfinished
			else if (jsonData.feedback == "correct,finished") {
				updateQuestion();	
				window.location.href="scoreboard.html?sessionID=" + sessionID;
			}//end if finished
			else if (jsonData.feedback == "incorrect") {
				createSnackbar('✘ Incorrect ✘');
				updateScore();
			}//end if incorrect
			else if (jsonData.feedback == "unknown or incorrect location") createSnackbar('✜ Incorrect Location ✜');
			else alert("Unexpected Problem");
		}//end if ok
		else alert(jsonData.status + " - " + jsonData.message);
    } //end if ready
  	}; //end function()
  	xhttp.open("GET", "https://uclan-thc.appspot.com/api/json/secure/answerQuestion?answer=" + answer + "&session=" + sessionID, true);
  	xhttp.send();
}//end answerQuestionMCQ()

/********************************************************************/
function answerQuestionTxt() {
	updateLocation();
	var answer = document.getElementById("answer").value;
	var xhttp = new XMLHttpRequest();
  	xhttp.onreadystatechange = function() {
    if (xhttp.readyState == 4 && xhttp.status == 200) {
		var jsonData = JSON.parse(xhttp.responseText);
		if (jsonData.status == "OK") {
			if (jsonData.feedback == "correct,unfinished") {
				createSnackbar('✔ Correct ✔');
				updateQuestion();	
			}//end if unfinished
			else if (jsonData.feedback == "correct,finished") {
				updateQuestion();
				window.location.href="scoreboard.html?sessionID=" + sessionID;
			}//end if finished
			else if (jsonData.feedback == "incorrect") {
				createSnackbar('✘ Incorrect ✘');
				updateScore();
			}//end if incorrect
			else if (jsonData.feedback == "unknown or incorrect location") createSnackbar('✜ Incorrect Location ✜');
			else alert("Unexpected Problem");
		}//end if ok
		else if (jsonData.status == "Invalid or missing parameters") createSnackbar('Your answer cannot be empty');
		else alert(jsonData.status + " - " + jsonData.message);
    }//end if ready
  	};//end if function()
  	xhttp.open("GET", "https://uclan-thc.appspot.com/api/json/secure/answerQuestion?answer=" + answer + "&session=" + sessionID, true);
  	xhttp.send();
	document.getElementById("answer").value = "";
}//end answerQuestionTxt()

/********************************************************************/

function skipQuestion() {
	var doSkip = confirm('Are you sure you would like to skip?');
	if (doSkip) {
		var xhttp = new XMLHttpRequest();
		xhttp.onreadystatechange = function() {
		if (xhttp.readyState == 4 && xhttp.status == 200) {
			var jsonData = JSON.parse(xhttp.responseText);
			if (jsonData.status == "OK") {
				if (jsonData.hasMoreQuestions) updateQuestion();
				else {
					updateQuestion();
					window.location.href="scoreboard.html?sessionID=" + sessionID;
				}
			}//end if ok
			else {
				updateQuestion();
				alert(jsonData.status + " - " + jsonData.message);
			}
		}//end if ready
		};//end if function()
		xhttp.open("GET", "https://uclan-thc.appspot.com/api/json/secure/skipQuestion?session=" + sessionID, true);
		xhttp.send();
		document.getElementById("answer").value = "";
	}//end if skip
}//end skipQuestion()

/********************************************************************/