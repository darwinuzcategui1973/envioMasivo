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
<body class="bodyInternas" style="margin:10px;background:#c2d2e0;">
<table cellspacing="0" cellpadding="0"  style="border:0px solid #afafaf;" width="100%" height="100%" >
<tr>
	<td style="padding-bottom:5px;font-size:1em;text-align:center;" id="titulo" class="titleLeftBlack" ><%=rb.getString("transaction.option").toUpperCase()%></td>
</tr>
<tr>
	<td>
		<table width="100%" >
			<tr class="td_gris_l">
				<th ><%=rb.getString("transaction.title.name").toUpperCase()%></td>
				<th width="10%"><%=rb.getString("transaction.title.save").toUpperCase()%></td>
				<th width="10%"><%=rb.getString("transaction.title.notify").toUpperCase()%></td>
			</tr>
		</table>
	</td>
</tr>
</table>
</body>
</html>
