<%@ page import="java.util.ResourceBundle,
                 com.desige.webDocuments.utils.ToolsHTML"%>
<%
ResourceBundle rb = ToolsHTML.getBundle(request);
%>
<!-- principalEntrada.jsp -->
<html>
<link href="estilo/estilo.css" rel="stylesheet" type="text/css">
<body class="bodyInternas">
<table width="100%">
	<tr>
		<td align="center">
			<%if(!ToolsHTML.isRepositoryDefine(request)){%>
			<br>
			<br>
			<br>
			<div style="width:500; height:100;border:4px solid #afafaf;padding:30;background-color:red;color:white;font-size:24pt;font-family:'Century Gothic',Arial,Times">
           		<%=rb.getString("err.invalidRepository")%>
			</div>
			<%}%>
			<%if(!ToolsHTML.isNumberUserValidForLicence()){%>
			<br>
			<br>
			<br>
			<div style="width:500; height:100;border:4px solid #afafaf;padding:30;background-color:red;color:white;font-size:24pt;font-family:'Century Gothic',Arial,Times">
           		<%=rb.getString("err.invalidNumberForLicenceLite")%> MAX = (<%=com.desige.webDocuments.utils.Constants.MAXIMO_USUARIOS_VERSION_LITE%>)
			</div>
			<%}%>
		</td>
	</tr>
</table>
</body>
</html>
