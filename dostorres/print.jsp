<%
	// SERVERIP: si en linux no da la direccion correcta editar el archivo etc/hosts y agregar la ip correcta y el servidor
	StringBuffer b = new StringBuffer();
	String serverIp = java.net.InetAddress.getByName(request.getServerName()).getHostAddress();
	String serverName = request.getServerName();
	//b.append("http://").append(request.getServerName()).append(":").append(request.getServerPort()).append(request.getContextPath());
	b.append("http://").append(serverIp).append(":").append(request.getServerPort()).append(request.getContextPath());
%>
<%
com.desige.webDocuments.utils.beans.Users usuario = (com.desige.webDocuments.utils.beans.Users) request.getSession().getAttribute("user");
if(false && !serverName.equals(serverIp)){
%>
<html>
  <head>
	<script>
		document.location.href = "<%=b.toString()%>/print.jsp"+document.location.search+"&idPerson=<%=usuario.getIdPerson()%>";
	</script>
  </head>
</html>
<%} else {%>
	<HTML>
	<HEAD>
	<TITLE>Imprimir Documento PDF</TITLE>
	</HEAD>
	<BODY bgcolor="#ece9d8" unload="eliminar();">
	<script language="javascript">
	document.body.style.overflow = "hidden";
	var http = false;
	function cancelar(){
		window.close();
	}
	function aceptar(){
		if(navigator.appName == "Microsoft Internet Explorer") {
		  http = new ActiveXObject("Microsoft.XMLHTTP");
		} else {
		  http = new XMLHttpRequest();
		}
		//alert("updateImpresionAjax.do?idDocument=<%=request.getParameter("idDocument")%>&idVersion=<%=request.getParameter("idVersion")%>&idPerson=<%=(usuario!=null && usuario.getIdPerson()>0?usuario.getIdPerson():request.getParameter("idPerson"))%>");
		http.open("POST", "updateImpresionAjax.do?idDocument=<%=request.getParameter("idDocument")%>&idVersion=<%=request.getParameter("idVersion")%>&idPerson=<%=(usuario!=null && usuario.getIdPerson()>0?usuario.getIdPerson():request.getParameter("idPerson"))%>");
		http.onreadystatechange=function() {
		  if(http.readyState == 4) {
			setTimeout("window.close()",1000);
		  }
		}
		http.send(null);
	}
	function eliminar(){
		alert("eliminamos el archivo");
	}
	</script>
	<applet codebase="<%=b.toString()%>" code="Printer" archive="print.jar" width=460 height=380 style="position:absolute;left:0px;top:0px">
		<param name="nameFile" value="<%=com.desige.webDocuments.utils.ToolsHTML.codificar(request.getParameter("nameFile"))%>">
		<param name="info" value="<%=request.getParameter("info")%>">
		<param name="item" value="<%=request.getParameter("item")%>">
		<param name="firstPage" value="<%=request.getParameter("firstPage")%>">
		<param name="isExpediente" value="<%=request.getParameter("isExpediente")%>">
	</applet>
	</BODY>
	</HTML>
<%}%>