<%@ page import="com.desige.webDocuments.utils.Constants" %>
<%@ page import="java.util.ResourceBundle,com.desige.webDocuments.utils.ToolsHTML" %>
<%@ page language="java" %>
<%
    ResourceBundle rb = ToolsHTML.getBundle(request);
%>

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
 * cambio en Para Visiluzar pantalla popup
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
		if(document.all) {
			asignar();
		}
	}
	function asignar() {
		document.getElementById("carga").src = imagen.src;
	}
	// fin para la imagen en espera

    function sendForm(){
    	<%if(request.getAttribute("DocumentNotAuthorizedForFiles")!=null){%>
	    	alert("El documento \""+request.getAttribute("DocumentNotAuthorizedForFiles")+"\" no esta autorizado para ser visualizado en los expedientes");
    	<%}%>
        document.showDocument.submit();
    }
	function init() {
		document.getElementById("carga").className=(document.all?"centerbox2":"centerboxFirefox2");
		if(document.all) { // windows
			if(window.parent && window.parent.parent && window.parent.parent.document.getElementById('documentoPDF')) {
				var ifr = window.parent.parent.document.getElementById('documentoPDF');
				//alert(ifr.width);
				//alert(ifr.height);
				window.resizeTo(ifr.width+37,ifr.height);
			} else {
				window.resizeTo(812,600);
			}
		} else {
			window.resizeTo(812,638);
		}
		window.document.body.scroll = 'no';  
	}
</script>
</head>
<body onLoad="init();setTimeout('main()',1000);sendForm();" >
<script language="javascript">
	if(document.all || <%=Constants.PRINTER_PDF?"true":"false"%>) {
	    document.write('<form name="showDocument" action="srShowDoc" method="Post" >');
	    document.write('    <input type="hidden" name="nameFile" value="<%=request.getParameter("nameFile")%>"/>');
	    document.write('    <input type="hidden" name="idDocument" value="<%=request.getParameter("idDocument")%>"/>');
	    document.write('    <input type="hidden" name="copyContents" value="<%=request.getParameter("copyContents")%>"/>');
	    document.write('    <input type="hidden" name="idVersion" value="<%=request.getParameter("idVersion")%>"/>');
	    document.write('    <input type="hidden" name="controlada" value="<%=request.getParameter("controlada")!=null?request.getParameter("controlada"):"false"%>"/>');
	    document.write('</form>');
	} else {
		alert("<%=rb.getString("E0127")%>");
	}
</script>    
<img id="carga" class="centerbox" src="img/loading2.gif" >
</body>
</html>
