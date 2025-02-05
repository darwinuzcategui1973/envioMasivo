<!-- /**
 * Title: sacopEast3.jsp<br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 */ -->
<%@ page import="java.util.ResourceBundle,
                 com.desige.webDocuments.utils.ToolsHTML,
                 com.desige.webDocuments.utils.DesigeConf"%>
 
 <%
    ResourceBundle rb = ToolsHTML.getBundle(request);
 %>
 
<html>
    <head>
        <meta content="MSHTML 6.00.2800.1106" name=generator/>
        <link href="estilo/estilo.css" rel="stylesheet" type="text/css"/>
        <link rel="StyleSheet" href="css/dtree.css" type="text/css" />
		<style>
			.corregir {
				border:0px solid #afafaf;padding:5px;background: url(images/corregirBig.gif) no-repeat bottom center;		
			}
			.prevenir {
				border:0px solid #afafaf;padding:5px;background: url(images/prevenirBig.gif) no-repeat bottom center;		
			}
		</style>
        <script type="text/javascript" src="script/dtree.js"></script>
        <script>
			var d = '';  // tree
			var preTitulo = "";
			var isCorrectiva = true;
			var nCorrectiva = 0;
			var titulos=new Array();
			titulos[0] =['Registro             ','Registro             '];
			titulos[1] =['No Confomidades      ','No Confomidades Potenciales'];
			titulos[2] =['Referencias a no Conformidades','Referencias a no Conformidades'];
			titulos[3] =['Responsable del Area. ','Responsable del Area. '];
			titulos[4] =['Requisitos Aplicables','Requisitos Aplicables'];
			titulos[5] =['Procesos             ','Procesos             '];
			titulos[6] =['Informar             ','Informar             '];
			titulos[7] =['Comentarios          ','Comentarios          '];
			titulos[8] =['Causas               ','Causas               '];
			titulos[9] =['Acciones Recomendadas','Acciones Recomendadas'];
			titulos[10]=['Archivo Relacionado  ','Archivo Relacionado  '];
			titulos[11]=['Sacop Relacionados   ','Sacop Relacionados   '];
			
        	function ir(nodo) {

        		try {
	        		window.parent.frames['sacopCenter'].ir(nodo);
    	    		//window.parent.frames['sacopNorth'].setTitulo(preTitulo+" - "+titulos[nodo][nCorrectiva]);
    	    		window.parent.frames['sacopNorth'].setTitulo(titulos[nodo][nCorrectiva]);
    	    		for(var k=1;k<=titulos.length;k++){
    	    			document.getElementById("sd"+k).className="node";
    	    		}
    	    		nodoActual=nodo;
    	    		document.getElementById("sd"+(nodo+1)).className="nodesel";
        		} catch(e) {
    	    		for(var k=1;k<=titulos.length;k++){
    	    			document.getElementById("sd"+k).className="node";
    	    		}
    	    		document.getElementById("sd1").className="nodesel";
        		}
        	}
        	function fondo(tipo) {
        		if(tipo==0) {
        			isCorrectiva = true;
        			nCorrectiva = 0;
        			document.getElementById("menuSacop").className="corregir";
        			preTitulo="<%=rb.getString("scp.ac")%>";
        		} else {
        			isCorrectiva = false;
        			nCorrectiva = 1;
        			document.getElementById("menuSacop").className="prevenir";
        			preTitulo="<%=rb.getString("scp.ap")%>";
        		}
    	    	//window.parent.frames['sacopNorth'].setTitulo(preTitulo+" - "+titulos[0][0]);
    	    	window.parent.frames['sacopNorth'].setTitulo(titulos[0][0]);
        	}
			var nodoActual=0;
			function anterior() {
				if(nodoActual>0) {
					nodoActual--;
					ir(nodoActual);
				}
			}
			function siguiente() {
				if(nodoActual<11) {
					nodoActual++;
					ir(nodoActual);
				}
			}
        </script>
	</head>
<body class="bodyInternas" style="margin:0px;">
<table id="menuSacop" cellspacing="0" cellpadding="0"  class="corregir" width="100%" height="100%" >
<tr>
	<td valign="top" style="textDecoration:none;">
			<div class="item">
				<script type="text/javascript">
				<!--
				d = new dTree('d');
				d.add( 0, -1, 'SACOP');   //1
				d.add( 1,  0, 'Datos Basicos               ',"javascript:ir( 0)"); //0
				d.add( 2,  0, 'Fechas                      ',"javascript:ir( 1)"); //1
				d.add( 3,  0, 'No Conformidades            ',"javascript:ir( 2)"); //2
				d.add( 4,  0, 'Comentarios adicionales     ',"javascript:ir( 3)"); //3
				d.add( 5,  0, 'Causas Potenciales          ',"javascript:ir( 4)"); //4
				d.add( 6,  0, 'Acciones establecidas       ',"javascript:ir( 5)"); //5
				d.add( 7,  0, 'Efectividad de acciones     ',"javascript:ir( 6)"); //6
				d.add( 8,  0, 'Acciones Recomendadas       ',"javascript:ir( 7)"); //7
				d.add( 9,  0, 'Accion Correctiva/Preventiva',"javascript:ir( 8)"); //8
				d.add(10,  0, 'Requisitos aplicables       ',"javascript:ir( 9)"); //9
				d.add(11,  0, 'Causas no Conformidad       ',"javascript:ir(10)"); //10
				d.add(12,  0, 'Acciones                    ',"javascript:ir(11)"); //11
				document.write(d);
	    		for(var k=1;k<=titulos.length;k++){
	    			document.getElementById("sd"+k).className="node";
	    		}
    			document.getElementById("sd1").className="nodeSel";
				//-->
				</script>
			</div>
	</td>
</tr>
</table>
</body>
</html>
