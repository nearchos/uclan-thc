/********************************************************************************
 ********************************************************************************
	File Name: 
		addRemoveSecondPirate.js
	Created by: 
		Nicos Kasenides (nkasenides@uclan.ac.uk / hfnovember@hotmail.com) 
		For InSPIRE - UCLan Cyprus
		June 2016
	Description:
		This file is defunct. It is meant to add or remove elements for adding
		a second pirate for the quiz. Kept for future reference/use.
*********************************************************************************		
*********************************************************************************/




/********************************************************************************/
//Adds or removes elements from the registration form for the second pirate.
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
}//end addRemoveSecondPirate()
/********************************************************************************/