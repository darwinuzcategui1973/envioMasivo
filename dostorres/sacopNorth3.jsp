<!-- /**
 * Title: sacopNorth3.jsp<br/>
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
        <style>
			.titleLeftBlack {
				font-family: Sans-serif, Helvetica, Arial, Verdana;
				font-weight:bold;
				font-size: 9pt;
				color: #000000;
				text-align: left;
				vertical-align: middle;
				padding-left:10pt;
			}        
        </style>
        <script>
        	function setTitulo(titulo) {
        		document.getElementById("titulo").innerHTML=titulo; 
        	}
        </script>
	</head>
<body class="bodyInternas" style="margin:0px;">
<table cellspacing="0" cellpadding="0"  style="border:0px solid #afafaf;" width="100%" height="100%" bgcolor="#c2d2e0">
<tr>
	<td id="titulo" class="titleLeftBlack" >Registro de Solicitud de Accion Correctiva o Preventiva</td>
</tr>
</table>
</body>
</html>
