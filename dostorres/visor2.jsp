<HTML>
<HEAD>
	<TITLE>Imprimir Documento PDF</TITLE>
	<meta http-equiv="Cache-Control" Content="no-cache">
	<meta http-equiv="Pragma" content="no-cache">
	<meta http-equiv="Expires" content="0">
	
	<style>
		.tabs-newtab-button {display: none !important}
	</style>
</HEAD>
<BODY bgcolor="#ece9d8">
<script language="javascript">
function cancelar(){
	alert("Cancelar");
	window.close();
}
function aceptar(){
	alert("Se consume la impresion");
	window.close();
}
function inicio() {
	var w = 1175+(document.all?0:10);
	var h = 650+(document.all?0:45);
	var sW = screen.availWidth;
	var sH = screen.availHeight;
	var left=(sW-w)/2;
	var top=((sH-h)/2);
	top = (top>20?top-20:(top>10?top-10:top));

	window.parent.moveTo(left,top);
	window.parent.resizeTo(w, h);
}
</script>

<body onload="inicio()">
<center>
	<applet code="Visor" archive="visor.jar" width="1154" height="600" style="position:absolute;left:0px;top:0px">
		<param name="nameFile" value="<%=request.getParameter("nombreArchivo")%>">
		<param name="info" value="<%=request.getParameter("info")%>">
		<param name="item" value="5">
		
		<PARAM NAME="cache_archive" VALUE="visor.jar">
		<PARAM NAME="cache_version" VALUE="1.3">
		
	</applet>
</center>
<!--
<center>
	<applet code="Visor" archive="visor.jar" width="1154" height="600" style="position:absolute;left:0px;top:0px">
		<param name="nameFile" value="<%=com.desige.webDocuments.utils.ToolsHTML.codificar(String.valueOf(request.getAttribute("nombreArchivo")))%>">
		<param name="info" value="<%=request.getParameter("info")%>">
		<param name="item" value="5">
	</applet>
</center>
-->
</BODY>
</HTML>