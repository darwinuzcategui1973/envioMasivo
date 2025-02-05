<!-- modulos.jsp -->
<%@ page import="java.util.ResourceBundle,
                 com.desige.webDocuments.utils.ToolsHTML,
                 com.desige.webDocuments.utils.beans.Users"%>
<%
    ResourceBundle rb = ToolsHTML.getBundle(request);
    Users usuario = (Users)session.getAttribute("user");
%>
<html>
    <head>
        <meta content="MSHTML 6.00.2800.1106" name=generator/>
        <link href="estilo/estilo.css" rel="stylesheet" type="text/css"/>
	</head>
    <body class="bodyInternas">
					
    
		<table cellSpacing="1" cellPadding="0" align="center" border="0" width="100%"  height="100%" >
			<tr>
				<td width="100%"  valign="top" align="left" >
					<table cellSpacing="0" cellPadding="2"  border="0" width="100%"  height="100%" style="border-collapse:collapse;border-color:#ofofof;border:1px solid #efefef">
						<%if (usuario!=null) {
                            out.println(ToolsHTML.printMenu(rb,usuario.getUser(),usuario.getIdGroup(),true,request));
                        }%>
					</table>
				</td>
			</tr>
		</table>
    </body>
</html>
<script language="javascript">
	<%int item = Integer.parseInt(ToolsHTML.isEmptyOrNull(String.valueOf(session.getAttribute("numeroDeModulos")),"0"));%>
	window.parent.document.getElementById("marcoEOC").rows="32,*,<%=(item*20)+4%>";
</script>

