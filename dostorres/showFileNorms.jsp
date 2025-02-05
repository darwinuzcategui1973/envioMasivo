
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<!--
/**
 * Title: showFileNorms.jsp<br>
 * Copyright: (c) 2003 Consis International<br>
 * Company: Consis International (CON)<br>
 * @author Nelson Crespo (NC)
 * @author Simón Rodriguez (SR)
 * @version Acsel-e v2.2
 * <br>
 * Changes:<br>
 * <ul>
 *      <li> 16/06/2005 (SR) Se invalida la tecla ctrl en java script para imprimir en la forma </li>
 * <ul>
 */-->

<html>
<head>
<title></title>
<jsp:include page="meta.jsp" />
<script language="JavaScript">
	document.onkeydown = function() {
		if (window.event
				&& (window.event.keyCode == 122 || window.event.keyCode == 116 || window.event.ctrlKey)) {
			window.event.keyCode = 505;
		}
		if (window.event.keyCode == 505) {
			return false;
		}
	}
</script>

<script>
	function sendForm() {
		document.showDocumentNorms.submit();
	}
</script>
</head>
<body onLoad="javascript:sendForm();">
	<form name="showDocumentNorms" action="showDocumentNorms.do"
		method="Post">
		<input type="hidden" name="idNorms"
			value="<%=request.getParameter("idNorms")%>" />
	</form>
</body>
</html>
