<HTML>
<HEAD>
	<TITLE>Imprimir Documento PDF</TITLE>
	<META HTTP-EQUIV="Cache-Control" CONTENT ="no-cache">
	<META HTTP-EQUIV="Pragma" CONTENT="no-cache"> 
	<META HTTP-EQUIV="expires" CONTENT="0">
	<style>
		.tabs-newtab-button {display: none !important}
	</style>
</HEAD>
<BODY bgcolor="#ece9d8">
<script language="javascript">

function getWidth() {
        var x = 0;
        if (self.innerHeight){
                x = self.innerWidth;
        } else if (document.documentElement && document.documentElement.clientHeight) {
                x = document.documentElement.clientWidth;
        } else if (document.body) {
                x = document.body.clientWidth;
        }
        return x;
}
 
function getHeight() {
        var y = 0;
        if (self.innerHeight) {
                y = self.innerHeight;
        } else if (document.documentElement && document.documentElement.clientHeight) {
                y = document.documentElement.clientHeight;
        } else if (document.body) {
                y = document.body.clientHeight;
        }
        return y;
}

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
function ajustar() {
	var w = getWidth();
	var h = getHeight();
	//alert("ajustar "+w+" "+h);
	document.getElementById("visor").style.width=w+(document.all?20:0);
	document.getElementById("visor").style.height=h-(document.all?30:0);;
}

window.onresize=ajustar;
</script>

<body onload="inicio()">  
<!--
<br>
<br>
<br>
<br>
<br>
<br>
<br>
<a href="visor2.jsp?nombreArchivo=<%=com.desige.webDocuments.utils.ToolsHTML.codificar(String.valueOf(request.getAttribute("nombreArchivo")))%>" target="_blanck">visor</a>
-->
<center>
	<applet id="visor" code="Visor" archive="visor.jar" width="1154" height="600" style="position:absolute;left:0px;top:0px">
		<param name="nameFile" value="<%=com.desige.webDocuments.utils.ToolsHTML.codificar(String.valueOf(request.getAttribute("nombreArchivo")))%>">
		<param name="info" value="<%=request.getParameter("info")%>">
		<param name="item" value="5">
	</applet>
</center>
</BODY>
</HTML>