/********************************************************************************
	File Name: 
		answerQuestion.js
	Description:
		This file contains functions used to answer, skip, update the questions
		and the score.
*********************************************************************************/

/**
 * Makes a request to update the score.
 */
function updateScore() {
	var xhttp = new XMLHttpRequest();
  	xhttp.onreadystatechange = function() {
		if (xhttp.readyState == 4 && xhttp.status == 200) {
			var jsonData = JSON.parse(xhttp.responseText);
			var statusItem = jsonData.status;
			if (statusItem == "OK") {
				var score = jsonData.score;
				var scoreLabel = document.getElementById("score");
				scoreLabel.innerHTML = score;
                changeScoreLabelColor();
			}//end if OK
			else {
                var errorMessages = "";
                for (var i = 0; i < jsonData.errorMessages.length; i++) {
                    errorMessages += jsonData.errorMessages[i] + "\n";
                }
                var errorStr = jsonData.status + ":\n" + errorMessages;
                alert(errorStr);
            }
		}//end if ready
  	};//end if function()
  	xhttp.open("GET", API_SCORE + "?session=" + sessionID, true);
  	xhttp.send();
}

/**
 * Changes the color in the score label from green to orange to red.
 */
function changeScoreLabelColor() {
	var score = document.getElementById("score");
	if (score.innerHTML > 0) score.style.color = "green";
	else if (score.innerHTML == 0) score.style.color = "#FF5100";
	else score.style.color = "red";
}

/**
 * Makes a server request to update the current question.
 */
function updateQuestion() {
	document.getElementById("loader").style.display = "block";
	document.getElementById("container").style.display = "none";

	resetAllAnswerFields();

	var xhttp = new XMLHttpRequest();
  	xhttp.onreadystatechange = function() {
		if (xhttp.readyState == 4 && xhttp.status == 200) {
			var jsonData = JSON.parse(xhttp.responseText);
			var statusItem = jsonData.status;

			if (statusItem == "OK") {

                //If quiz all questions answered, redirect:
			    if (jsonData.currentQuestionIndex >= jsonData.numOfQuestions) {
                    window.location.href="scoreboard.html?sessionID=" + sessionID + "&playerName=" + getCookie(COOKIE_PLAYER_NAME); //TODO
                }

                //Show the container and question:
				document.getElementById("container").style.display = "inline";
				document.getElementById("question").innerHTML = jsonData.questionText;

				//Skip option:
                document.getElementById("skipBtn").style.display = jsonData.canBeSkipped ? "inline" : "none";

				//Decide which form to display:
                var mcqForm = document.getElementById("mcqForm");
                var textForm = document.getElementById("textForm");
                var booleanForm = document.getElementById("booleanForm");
                var numberForm = document.getElementById("numberForm");
                var integerForm = document.getElementById("integerForm");

				switch (jsonData.questionType) {
                    case QUESTION_INTEGER:
                        integerForm.style.display = "inline";
                        mcqForm.style.display = "none";
                        numberForm.style.display = "none";
                        textForm.style.display = "none";
                        booleanForm.style.display = "none";
                        break;
                    case QUESTION_MCQ:
                        integerForm.style.display = "none";
                        mcqForm.style.display = "inline";
                        numberForm.style.display = "none";
                        textForm.style.display = "none";
                        booleanForm.style.display = "none";
                        break;
                    case QUESTION_NUMERIC:
                        integerForm.style.display = "none";
                        mcqForm.style.display = "none";
                        numberForm.style.display = "inline";
                        textForm.style.display = "none";
                        booleanForm.style.display = "none";
                        break;
                    case QUESTION_TEXT:
                        integerForm.style.display = "none";
                        mcqForm.style.display = "none";
                        numberForm.style.display = "none";
                        textForm.style.display = "inline";
                        booleanForm.style.display = "none";
                        break;
                    case QUESTION_BOOLEAN:
                        integerForm.style.display = "none";
                        mcqForm.style.display = "none";
                        numberForm.style.display = "none";
                        textForm.style.display = "none";
                        booleanForm.style.display = "inline";
                        break;
                }
			}//end if OK
			else {
				var errorMessages = "";
                for (var i = 0; i < jsonData.errorMessages.length; i++) {
                    errorMessages += jsonData.errorMessages[i] + "\n";
                }
                var errorStr = jsonData.status + ":\n" + errorMessages;
                alert(errorStr);
                document.location = "selectCategory.html";
			}//end if not OK

            //Hide the loader and display the page:
			document.getElementById("loader").style.display = "none";
			document.getElementById("container").style.display = "block";

			//Set location bubble:
			if (jsonData.requiresLocation) document.getElementById("isLocationRelevant").style.display = "inline";
			else document.getElementById("isLocationRelevant").style.display = "none";


		}//end if ready
  	};//end if function()
  	xhttp.open("GET", API_QUESTION + "?session=" + sessionID, true);
  	xhttp.send();
}

/**
 * Answers a question with a given answer.
 * @param answer
 */
function answerQuestion(answer) {

    //Displays a message when the user attempts to answer a question with no internet connection:
    if (!navigator.onLine) {
        createSnackbar("Connection error - Please make sure you have an internet connection");
    }
    else {

        //Update the location first:
        updateLocation();

        //Make the call to answer question:
        var xhttp = new XMLHttpRequest();
        xhttp.onreadystatechange = function () {
            if (xhttp.readyState == 4 && xhttp.status == 200) {
                var jsonData = JSON.parse(xhttp.responseText);
                if (jsonData.status == "OK") {

                    updateScore();

                    //If correct & not completed:
                    if (jsonData.correct && !jsonData.completed) {
                        createSnackbar(jsonData.message);
                        updateQuestion();
                    }

                    //If correct & completed:
                    else if (jsonData.correct && jsonData.completed) {
                        createSnackbar(jsonData.message);
                        updateQuestion();
                        setTimeout(function () {
                        }, 1000);
                        window.location.href = "scoreboard.html?sessionID=" + sessionID + "&playerName=" + getCookie(COOKIE_PLAYER_NAME);
                    }

                    //Not correct, Not completed:
                    else if (!jsonData.correct && !jsonData.completed) {
                        createSnackbar(jsonData.message);
                        updateQuestion();
                    }

                    //Not correct, completed:
                    else if (!jsonData.correct && jsonData.completed) {
                        createSnackbar(jsonData.message);
                        setTimeout(function () {
                        }, 1000);
                        window.location.href = "scoreboard.html?sessionID=" + sessionID + "&playerName=" + getCookie(COOKIE_PLAYER_NAME);
                    }

                    // else if (jsonData.feedback == "unknown or incorrect location") { //TODO LOCATION?
                    // 	createSnackbar('✜ Incorrect Location ✜');
                    // 	updateQuestion();
                    // }//end if bad location

                    else alert("Unexpected Problem");

                }//end if ok
                else {
                    var errorMessages = "";
                    for (var i = 0; i < jsonData.errorMessages.length; i++) {
                        errorMessages += jsonData.errorMessages[i] + "\n";
                    }
                    var errorStr = jsonData.status + ":\n" + errorMessages;
                    createSnackbar(errorStr);
                    setTimeout(function () {
                    }, 1000);
                    window.location.href = "scoreboard.html?sessionID=" + sessionID + "&playerName=" + getCookie(COOKIE_PLAYER_NAME);
                }
            } //end if ready
        }; //end function()
        xhttp.open("GET", API_ANSWER + "?answer=" + answer + "&session=" + sessionID, true);
        xhttp.send();
    }
}

/**
 * Makes a server request to skip the current question.
 */
function skipQuestion() {
	var xhttp = new XMLHttpRequest();
	xhttp.onreadystatechange = function() {
		if (xhttp.readyState == 4 && xhttp.status == 200) {
			var jsonData = JSON.parse(xhttp.responseText);
			if (jsonData.status == "OK") {

			    updateScore();

				//Check if completed:
                if (!jsonData.completed) {
                    updateQuestion();
                    createSnackbar("Skipped question", 2000);
                }
                else {
                    updateQuestion();
                    window.location.href="scoreboard.html?sessionID=" + sessionID + "&playerName=" + getCookie(COOKIE_PLAYER_NAME);
                }
			}//end if ok
			else {
                var errorMessages = "";
                for (var i = 0; i < jsonData.errorMessages.length; i++) {
                    errorMessages += jsonData.errorMessages[i] + "\n";
                }
                var errorStr = jsonData.status + ":\n" + errorMessages;
                createSnackbar(errorStr);
                setTimeout(function() { }, 1000);
			}//end if not OK
		}//end if ready
	};//end if function()
	xhttp.open("GET", API_SKIP + "?session=" + sessionID, true);
	xhttp.send();
	resetAllAnswerFields();
}

/**
 * Resets all text-based fields:
 */
function resetAllAnswerFields() {
    document.getElementById("answerNumber").value = "";
    document.getElementById("answerInteger").value = "";
    document.getElementById("answerText").value = "";
}