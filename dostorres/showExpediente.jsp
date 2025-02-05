<%@ page import="com.desige.webDocuments.utils.Constants,java.util.ResourceBundle,com.desige.webDocuments.utils.ToolsHTML" %>
<%	ResourceBundle rb = ToolsHTML.getBundle(request); %>


<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"> 
<!--/**
 * Title: ShowFile.jsp<br>
 * Copyright: (c) 2003 Consis International<br>
 * Company: Consis International (CON)<br>
 * @author Nelson Crespo (NC)
 * @author Simón Rodriguez (SR)
 * @version Acsel-e v2.2
 * <br>
 * Changes:<br>
 * <ul>
 *      <li> 16/06/2005 (SR) Se invalida la tecla ctrl en java script para imprimir en la forma </li>
 *      <li> 30/06/2006 (NC) Cambios para correcto formato de los documentos a Mostrar </li>
 * <ul>
 */-->

<html>
<head>
<title> Qwebdocuments </title>
<jsp:include page="meta.jsp" /> 
<link href="estilo/estilo.css" rel="stylesheet" type="text/css">
<script language="JavaScript">
	var key = new Array();
	key['p'] = "No imprimirás.";
	key['c'] = "No copiarás.";
	key['f'] = "No buscarás.";
	
	function getKey(keyStroke) {
		if(isIE7()){
			return;			
		}
		var isNetscape=(window.clipboardData?false:true);
		var hacer = (isNetscape? keyStroke.ctrlKey : window.event.ctrlKey);
		if (hacer) {
			eventChooser = (isNetscape) ? keyStroke.which : event.keyCode;
			which = String.fromCharCode(eventChooser).toLowerCase();
			alert("Opción no valida");
		}
	}
	document.onkeydown = getKey;

	// ini para la imagen en espera
	var imagen = new Image();
	imagen.src="img/loading2.gif";
	function main() {
		if(navigator.appVersion.indexOf("MSIE")>0) {
			asignar();
		}
	}
	function init() {
		if(navigator.appVersion.indexOf("MSIE")>0){
			document.getElementById("carga").className="centerbox2";
		} else {
			document.getElementById("carga").className="centerboxFirefox2";
		}
	}
	function asignar() {
		document.getElementById("carga").src = imagen.src;
	}
	// fin para la imagen en espera

    function sendForm(){
        document.showDocument.submit();
    }
</script>
</head>
<body onLoad="init();setTimeout('main()',1000);sendForm();" >
<%--    <form name="showDocument" action="showDocument.do" method="Post" >--%>
<script language="javascript">
	if(navigator.appVersion.indexOf("MSIE")>0 || <%=Constants.PRINTER_PDF  || request.getAttribute("filesPreview")!=null?"true":"false"%>) {
	    document.write('<form name="showDocument" action="srShowFiles" method="Post" >');
	    document.write('    <input type="hidden" name="nameFile" value="<%=request.getParameter("nameFile")%>"/>');
	    document.write('    <input type="hidden" name="idDocument" value="<%=request.getParameter("idDocument")%>"/>');
	    document.write('    <input type="hidden" name="idVersion" value="<%=request.getParameter("idVersion")%>"/>');
	    document.write('    <input type="hidden" name="filesPreview" value="true"/>');
	    document.write('</form>');
	} else {
		alert("<%=rb.getString("E0127")%>");
	}
</script>    
<img id="carga" class="centerbox" src="img/loading2.gif" >
</body>
</html>
