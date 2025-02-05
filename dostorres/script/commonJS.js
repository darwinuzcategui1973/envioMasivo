function ajaxobj() {
	try {
		_ajaxobj = new ActiveXObject("Msxml2.XMLHTTP");
	} catch (e) {
		try {
			_ajaxobj = new ActiveXObject("Microsoft.XMLHTTP");
		} catch (E) {
			_ajaxobj = false;
		}
	}
	if (!_ajaxobj && typeof XMLHttpRequest!='undefined') {
		_ajaxobj = new XMLHttpRequest();
	}
	
	return _ajaxobj;
}

function sendMassiveMail(confirmMsg){
	var response = confirm(confirmMsg);
	
	if(response == true){
		ajax = ajaxobj();
		ajax.open("POST", "sendMassiveMail.do", true);
		ajax.send(null);
		//document.sendEmail.submit();
	}
}