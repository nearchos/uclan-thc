/********************************************************************************
	 File Name:
	 	addRemoveSecondPirate.js
	 Description:
	 	Contains legacy code (left for future reference) which adds additional
 		players to the same team.
 *********************************************************************************/


/**
 * Adds or removes elements from the registration form for the second pirate.
 */
function addRemoveSecondPirate() {
	var secondPirateDiv = document.getElementById("secondPirate");
	var secondPirateBtn = document.getElementById("addRemoveSecondPirateBtn");
	var secondPirateName = document.getElementById("secondPirateName");
	var secondPirateEmail = document.getElementById("secondPirateEmail");
	
	if (secondPirateDiv.style.display == "none") {
		secondPirateDiv.style.display = "inline";
		secondPirateName.disabled = false;
		secondPirateEmail.disabled = false;
		secondPirateBtn.textContent = "Remove Second Pirate";
	}//end if was off
	else if (secondPirateDiv.style.display == "inline") {
		secondPirateDiv.style.display = "none";
		secondPirateName.disabled = true;
		secondPirateName.disabled = true;
		secondPirateBtn.textContent = "Add Second Pirate";
	}//end if was on
}