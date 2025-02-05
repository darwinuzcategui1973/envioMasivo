<html>
<head>
<title> Qwebdocuments </title>
<style>
</style>
<script language="javascript" src="estilo/funciones.js" ></script>
<link href="estilo/estilo.css" rel="stylesheet" type="text/css">
<script language="javascript">
	var imagen = new Image();
	imagen.src="img/loading.gif";
	function main() {
		//alert(document.location.search);
		var cad = "<%=request.getParameter("accion")%>"+document.location.search;
		//alert(cad);
		//document.location=cad;
		var hventana = window.open(cad,"_self");
		hventana.document.title="Qwebdocuments";
		if(document.all) {
			asignar();
		}
	}
	function init() {
		window.opener.document.title='Qwebdocuments';
		document.getElementById("carga").className=(document.all?"centerbox":"centerboxFirefox");
	}
	function asignar() {
		document.getElementById("carga").src = imagen.src;
	}
</script>
</head>
<body onload="init();setTimeout('main()',1000);" oncontextmenu="return false" onkeydown="return false" style="background: url(img/barraSup2.png); background-repeat: no-repeat">
<script language="javascript">
document.oncontextmenu = function(){return false;}
</script>
<img id="carga" class="centerbox" src="img/loading.gif" >
</body>
</html>
