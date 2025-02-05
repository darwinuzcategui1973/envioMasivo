<!--
 * Title: listTransaction.jsp <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * @version WebDocuments v3.0
-->
<html>
<head>
<jsp:include page="meta.jsp" />
<link href="estilo/estilo.css" rel="stylesheet" type="text/css">
<script type="text/javascript" src="./estilo/funciones.js"></script>
<style>
.fondoOscuro {
	top: 0px;
	left: 0px;
	margin: 0px auto;
	background-color: #000000;
	position: absolute;
	opacity: 0.6;
	-moz-opacity: 0.6;
	-khtml-opacity: 0.6;
	filter: alpha(opacity = 60);
	z-index: 50;
}

#caja2 {
	color: white;
	font-weight: bold;
	font-family: Sans-serif, Helvetica, Arial, Verdana;
	font-size: 12px;
	position: absolute;
	z-index: 100;
	border: 1px solid white;
}

#caja3 {
	margin: 0px auto;
	background-color: #ffffff;
	position: absolute;
	opacity: 0.6;
	-moz-opacity: 0.6;
	-khtml-opacity: 0.6;
	filter: alpha(opacity = 60);
	z-index: 60;
}

p.negro {
	color: #000;
}
}
.panel td {
	padding:5px;
}
</style>

<script>
function administrar(){
	medidas = getWindowXY();
	ancho = medidas[0]-150;
	largo = medidas[1]-100;

	var frm = document.getElementById("frmConfig");
	frm.style.width='';
	frm.style.height='';
	

	var obj = document.getElementById("caja");
	obj.style.display="";
	obj.style.width=medidas[0]+"px";
	obj.style.height=medidas[1]+"px";
	
	var obj2 = document.getElementById("caja2");
	obj2.style.display="";
	obj2.style.left=((medidas[0]-ancho)/2)+"px";
	obj2.style.top=((medidas[1]-largo)/2)+"px";
	obj2.style.width=ancho+"px";
	obj2.style.height=largo+"px";
	obj2.className="bodyInternas";
	//obj2.style.backgroundColor="gray";
	//obj2.style.backgroundImage.src=url("img/fondoGris2.jpg");
	
	var marco = document.getElementById("caja3");
	margen=10;
	marco.style.display="";
	marco.style.left=((medidas[0]-ancho)/2)-margen+"px";
	marco.style.top=((medidas[1]-largo)/2)-margen+"px";
	marco.style.width=ancho+(margen*2)+"px";
	marco.style.height=largo+(margen*2)+"px";
	
	var obj3 = document.getElementById("frmConfig");
	obj3.style.left="0px";
	obj3.style.top="0px";
	//obj3.width=obj2.style.width;
	obj3.width="100%";
	obj3.height="100%";
}
setTimeout("administrar()",500);
</script>
</head>
<body>
	<div id="caja" class="fondoOscuro" style="display: none;"></div>
	<div id="caja2" style="position: absolute; top: 0; left: 0;">
		<iframe id="frmConfig" name="frmConfig" src="transactionConfig.jsp" frameborder="no"
			style="width: 0px; height: 0px;"></iframe>
	</div>
	<div id="caja3" style="display: none;"></div>
</body>
</html>
