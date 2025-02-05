<!-- /**
 * Title: vacio.jsp<br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * @author Ing. Nelson Crespo (NC)
 * @author Ing. Simon Rodríguez (SR)
 * @version WebDocuments v3.0
 * <br/>
 * Changes:<br/>
 * <ul>
 *      <li>Nothing</li>
 </ul>
 */ -->
<html>
    <head>
        <meta content="MSHTML 6.00.2800.1106" name=generator/>
        <link href="estilo/estilo.css" rel="stylesheet" type="text/css"/>
        <script>
        	var LISTA_ANEXO = window.parent.opener.getListaAnexo();
        	var maximo = LISTA_ANEXO.length-1;
        	var itemActual = <%=request.getParameter("item")%>;
        	function cargar(item) {
				document.getElementById("btnLeft").style.display="";
				document.getElementById("btnRight").style.display="";

        		if(item<=0) {
					document.getElementById("btnLeft").style.display="none";
        		} else if(item>=maximo) {
					document.getElementById("btnRight").style.display="none";
        		}

				itemActual=item;
				itemActual = itemActual<0?0:itemActual;
				itemActual = itemActual>=maximo?maximo:itemActual;

				window.open("showAttached.do?prefijo="+LISTA_ANEXO[itemActual][5]+"&numero=1","anexo2");
				window.open("showAttached.do?prefijo="+LISTA_ANEXO[itemActual][5]+"&numero=0","anexo1");
				window.open("loadDataDoc.do"+getRuta(itemActual),"documento");
        	}
        	function getRuta(item) {
        		var cad = "";
				cad+="?accion=loadDataDoc.do";
				cad+="&idDocument="+LISTA_ANEXO[item][0];
				cad+="&idVersion="+LISTA_ANEXO[item][1];
				cad+="&nameFile="+escape(LISTA_ANEXO[item][2]);
				cad+="&imprimir="+LISTA_ANEXO[item][3];
				cad+="&nameFileToPrint="+escape(LISTA_ANEXO[item][2]);
				cad+="&idLogoImp="+LISTA_ANEXO[item][4];
				return cad;
        	}
        	function ver(pos){
        		if(pos==0) {
        			window.parent.document.getElementById('frmDocumentos').cols='50%,49%,1%';
        		} else if(pos==1) {
        			window.parent.document.getElementById('frmDocumentos').cols='37%, 37%, 26%';
        		}
        	}
        </script>
	</head>
<body style="margin:0;font-family:Tahoma;">
<table align="center" cellspacing="0" cellpadding="0" width="100%" border="0">
<tr>
	<td width="37%" style="text-align:center;font-size:8px;"><a href="#" onclick="ver(0)"><img src="icons/application_tile_horizontal.png" border="0"></td>
	<td width="37%" style="text-align:center;font-size:8px;"><a href="#" onclick="ver(1)"><img src="icons/application_tile_horizontaltriple.png" border="0"></td>
</tr>
<tr>
	<td colspan="3">
		<table align="center"  cellspacing="3" cellpadding="3">
		<tr>
			<td id="btnLeft"><a href="#" onclick="cargar(itemActual-1)"><img src="images/left.jpg" border="0"></td>
			<td id="btnRight"><a href="#" onclick="cargar(itemActual+1)"><img src="images/right.jpg" border="0"></td>
		</tr>
		</table>
	</td>
</tr>
</table>
</body>
<script>
	if(itemActual==0){
		document.getElementById("btnLeft").style.display="none";
	}
	if(itemActual>=maximo){
		document.getElementById("btnRight").style.display="none";
	}
</script>
</html>
