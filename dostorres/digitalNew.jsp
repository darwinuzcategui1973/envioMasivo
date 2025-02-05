<!-- Title: digitalNew.jsp -->
<%@ page import="java.util.ResourceBundle,
				 com.desige.webDocuments.files.forms.FilesForm,
                 com.desige.webDocuments.utils.beans.Users,
                 com.desige.webDocuments.utils.ToolsHTML"%>
                 
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
                 
<%
	ResourceBundle rb = ToolsHTML.getBundle(request);
    Users usuario = (Users)session.getAttribute("user");
%>
<html>
    <head>    
        <link href="estilo/estilo.css" rel="stylesheet" type="text/css"/>
		<script language="JavaScript" src="estilo/funciones.js"></script>
        <script language="javascript">
        </script>
	</head>
<body class="bodyInternas" style="margin:0;">
<table border="0" height="100%" width="100%" cellspacing="0" cellpadding="10" align="center">
	<tr>
		<td  valign="top">
			<html:form action="/filesNewSave">
			<table class="texto" border="0" width="100%">
				<tr>
					<td width="100">
						<bean:message key="files.field"/> :
					</td>
					<td>
						<html:text name="campo" property="id" size="4" readonly="true" />

						&nbsp;&nbsp;&nbsp;						
						<bean:message key="files.orden"/> :
						
					</td>
				</tr>
			</table>
			</html:form>
		</td>
	</tr>
</table>
</body>
</html>
