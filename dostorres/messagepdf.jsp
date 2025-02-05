<%@ page import="com.desige.webDocuments.utils.beans.Users"%>
<%
    Users usuario = (Users)session.getAttribute("user");
%>
<html>
<head>
<script language="javascript" src="./estilo/funciones.js"></script>
<script>


	function ver() {
		//try{
		document.getElementById("mensaje").style.display='';
		medidas = window.parent.getWindowXY();
		window.moveTo(0,medidas[1]-220);
		window.resizeTo(medidas[0]+20,300);
		//}catch(e){};
	}
	function ocultar() {
		setTimeout("minimizar()",500);
	}
	function minimizar() {
		//try{
		document.getElementById('mensaje').style.display='none';
		medidas = window.parent.getWindowXY();
		window.moveTo(0,medidas[1]-20);
		window.resizeTo(medidas[0]+20,20);
		//}catch(e){};
	}
</script>
</head>
<body id="body" onload="ocultar()" style="padding:0;margin-top:0;background: url(img/fondoGris.jpg);border:1px solid blue;" bgcolor="#0022ff" onMouseOver="ver()">
<form>
<center><font color="blue" >Comentarios</font><center>
	<textarea id="mensaje" style="width:100%;height:180;display:none;" onMouseOut="ocultar()"></textarea>
</form>
</body>
</html>
