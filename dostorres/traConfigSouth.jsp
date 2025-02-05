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
				 com.desige.webDocuments.typeDocuments.forms.TypeDocumentsForm,
				 com.desige.webDocuments.utils.Constants,
                 java.util.Collection,
                 java.util.Iterator,
                 java.io.File,
                 com.desige.webDocuments.utils.beans.Search,
			     com.desige.webDocuments.utils.ToolsHTML" %>
<%@ page language="java" %>
<%
ResourceBundle rb = ToolsHTML.getBundle(request);
%>	
<html>
    <head>
        <meta content="MSHTML 6.00.2800.1106" name=generator/>
        <link href="estilo/estilo.css" rel="stylesheet" type="text/css"/>
        <script>
        	function aceptar() {
        		cancelar();
        		window.parent.frames['configCenter'].salvar();
        	}
        	function cancelar() {
        		window.parent.ocultar();
        	}
        	function informe() {
        		window.parent.informe();
        	}
        </script>
	</head>

<body class="bodyInternas" style="margin:0px;">
<table cellspacing="0" cellpadding="0"  style="border:1px solid #afafaf;" width="100%" height="100%" bgcolor="#c2d2e0">
<tr>
	<td align="right">
		<input type="button" value=<%=rb.getString("btn.query")%> onclick="informe()"/>
		&nbsp;&nbsp;&nbsp;&nbsp;
		<input type="button" value=<%=rb.getString("btn.close")%> onclick="aceptar()"/>
		&nbsp;&nbsp;&nbsp;&nbsp;
	</td>
</tr>
</table>
</body>
</html>
