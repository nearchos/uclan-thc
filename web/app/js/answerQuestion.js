/********************************************************************************
 ********************************************************************************
	File Name: 
		answerQuestion.js
	Created by: 
		Nicos Kasenides (nkasenides@uclan.ac.uk / hfnovember@hotmail.com) 
		For InSPIRE - UCLan Cyprus
		June 2016
	Description:
		This file contains functions used to answer, skip, update the questions
		and the score.
*********************************************************************************		
*********************************************************************************/




/********************************************************************************/
//Makes a server request to update the score.
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
			}//end if OK
			else alert(jsonData.status + " " + jsonData.message);
		}//end if ready
  	};//end if function()
  	xhttp.open("GET", "https://uclan-thc.appspot.com/api/json/secure/score?session=" + sessionID, true);
  	xhttp.send();	
	changeScoreLabelColor();
}//end updateScore();
/********************************************************************************/

/********************************************************************************/
//Changes the color in the score label from green to orange to red.
function changeScoreLabelColor() {
	var score = document.getElementById("score");
	if (score.innerHTML > 0) score.style.color = "green";
	else if (score.innerHTML == 0) score.style.color = "#FF5100";
	else score.style.color = "red";
}//end changeScoreLabelColor()
/********************************************************************************/

/********************************************************************************/
//Makes a server request to update the current question.
function updateQuestion() {
	document.getElementById("loader").style.display = "block";
	document.getElementById("container").style.display = "none";
	var xhttp = new XMLHttpRequest();
  	xhttp.onreadystatechange = function() {
		if (xhttp.readyState == 4 && xhttp.status == 200) {
			var jsonData = JSON.parse(xhttp.responseText);
			var statusItem = jsonData.status;
			if (statusItem == "OK") {
				document.getElementById("container").style.display = "inline";
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
			else {
				if (statusItem == "Finished session") {
					document.getElementById("container").style.display = "none";	
					window.location.href="scoreboard.html?sessionID=" + sessionID + "&playerName=" + getCookie("THCWebApp-playerName");
				}//end if finished
				else createSnackbar(jsonData.status);
			}//end if not OK
			document.getElementById("loader").style.display = "none";
			document.getElementById("container").style.display = "block";
		}//end if ready
  	};//end if function()
  	xhttp.open("GET", "https://uclan-thc.appspot.com/api/json/secure/currentQuestion?session=" + sessionID, true);
  	xhttp.send();
}//end updateQuestion();
/********************************************************************************/

/********************************************************************************/
//Makes a server request to answer a Multiple Choice Question.
function answerQuestionMCQ(answer) {
	updateLocation();
	var xhttp = new XMLHttpRequest();
  	xhttp.onreadystatechange = function() {
		if (xhttp.readyState == 4 && xhttp.status == 200) {
			var jsonData = JSON.parse(xhttp.responseText);
			if (jsonData.status == "OK") {
				document.getElementById("score").innerHTML = jsonData.score;
				changeScoreLabelColor();
				if (jsonData.feedback == "correct,unfinished") {
					createSnackbar('✔ Correct ✔');
					updateQuestion();	
				}//end if unfinished
				else if (jsonData.feedback == "correct,finished") {
					updateQuestion();	
					window.location.href="scoreboard.html?sessionID=" + sessionID + "&playerName=" + getCookie("THCWebApp-playerName");
				}//end if finished
				else if (jsonData.feedback == "incorrect") {
					createSnackbar('✘ Incorrect ✘');
				}//end if incorrect
				else if (jsonData.feedback == "unknown or incorrect location") createSnackbar('✜ Incorrect Location ✜');
				else alert("Unexpected Problem");
			}//end if ok
			else createSnackbar(jsonData.status + " - " + jsonData.message);
		} //end if ready
  	}; //end function()
  	xhttp.open("GET", "https://uclan-thc.appspot.com/api/json/secure/answerQuestion?answer=" + answer + "&session=" + sessionID, true);
  	xhttp.send();
}//end answerQuestionMCQ()
/********************************************************************************/

/********************************************************************************/
//Makes a server request to answer a Text Question.
function answerQuestionTxt() {
	updateLocation();
	var answer = document.getElementById("answer").value;
	var xhttp = new XMLHttpRequest();
  	xhttp.onreadystatechange = function() {
		if (xhttp.readyState == 4 && xhttp.status == 200) {
			var jsonData = JSON.parse(xhttp.responseText);
			if (jsonData.status == "OK") {
				document.getElementById("score").innerHTML = jsonData.score;
				changeScoreLabelColor();
				if (jsonData.feedback == "correct,unfinished") {
					createSnackbar('✔ Correct ✔');
					updateQuestion();	
				}//end if unfinished
				else if (jsonData.feedback == "correct,finished") {
					updateQuestion();
					window.location.href="scoreboard.html?sessionID=" + sessionID + "&playerName=" + getCookie("THCWebApp-playerName");
				}//end if finished
				else if (jsonData.feedback == "incorrect") {
					createSnackbar('✘ Incorrect ✘');
				}//end if incorrect
				else if (jsonData.feedback == "unknown or incorrect location") createSnackbar('✜ Incorrect Location ✜');
				else alert("Unexpected Problem");
			}//end if ok
			else if (jsonData.status == "Invalid or missing parameters") createSnackbar('Your answer cannot be empty');
			else createSnackbar(jsonData.status + " - " + jsonData.message);
		}//end if ready
  	};//end if function()
  	xhttp.open("GET", "https://uclan-thc.appspot.com/api/json/secure/answerQuestion?answer=" + answer + "&session=" + sessionID, true);
  	xhttp.send();
	document.getElementById("answer").value = "";
}//end answerQuestionTxt()
/********************************************************************************/

/********************************************************************************/
//Makes a server request to skip the current question.
function skipQuestion() {
	var xhttp = new XMLHttpRequest();
	xhttp.onreadystatechange = function() {
		if (xhttp.readyState == 4 && xhttp.status == 200) {
			var jsonData = JSON.parse(xhttp.responseText);
			if (jsonData.status == "OK") {
				document.getElementById("score").innerHTML = jsonData.score;
				changeScoreLabelColor();
				if (jsonData.hasMoreQuestions) updateQuestion();
				else {
					updateQuestion();
					window.location.href="scoreboard.html?sessionID=" + sessionID + "&playerName=" + getCookie("THCWebApp-playerName");
				}//end if no more questions
			}//end if ok
			else {
				updateQuestion();
				alert(jsonData.status + " - " + jsonData.message);
			}//end if not OK
		}//end if ready
	};//end if function()
	xhttp.open("GET", "https://uclan-thc.appspot.com/api/json/secure/skipQuestion?session=" + sessionID, true);
	xhttp.send();
	document.getElementById("answer").value = "";
	createSnackbar("Skipped question", 2000);
}//end skipQuestion()
/********************************************************************************/