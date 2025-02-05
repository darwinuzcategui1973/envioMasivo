<HTML>
<HEAD>
<TITLE>Imprimir Documento PDF</TITLE>
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
</script>
<applet code="Viewer" archive="viewer.jar" width="1084" height="1410" style="position:absolute;left:0px;top:0px">
	<param name="nameFile" value="<%=request.getParameter("nameFile")%>">
	<param name="info" value="<%=request.getParameter("info")%>">
	<param name="item" value="5">
</applet>
</APPLET>
</BODY>
</HTML>