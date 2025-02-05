
<!--/**
 * Title: showMSG.jsp <br>
 * Copyright: (c) 2004 Desige Servicios de Computación<br/>
 *
 * @author Ing. Nelson Crespo   (NC)
 * @author Ing. Simón Rodriguéz (SR)
 * @version WebDocuments v1.0
 * <br>
 *     Changes:<br>
 * <ul>
  *      <li> 25/04/2006 (SR) Se sustituyo /WebDocuments/.. por ./..</li>
 * <ul>
 */-->
<%@ page import="com.desige.webDocuments.utils.ToolsHTML,
                 java.util.ResourceBundle"%>

<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<%@ page language="java" %>
<%
    ResourceBundle rb = ToolsHTML.getBundle(request);
    String mensaje = (String)ToolsHTML.getAttributeSession(session,"error",true);
    if (ToolsHTML.isEmptyOrNull(mensaje)) {
        mensaje = rb.getString("error.general");
    }
%>
<html>
<head>
<title><%=rb.getString("showMSG.title")%></title>
<jsp:include page="meta.jsp" /> 
<link href="estilo/estilo.css" rel="stylesheet" type="text/css">
<script language=javascript src="./estilo/popcalendar.js"></script>
<script language=javascript src="./estilo/fechas.js"></script>
<script language="JavaScript">
</script>
</head>

<body class="bodyInternas">
    <table cellSpacing=0 cellPadding=2 align=center border=0 width="100%">
        <tr>
            <td colspan="2" valign="top">
                <table class="clsTableTitle" width="100%" cellSpacing=0 cellPadding=2 align=center border=0>
                    <tbody>
                        <tr>
                            <td class="td_title_bc">
                                <%=rb.getString("showMSG.title")%>
                            </td>
                        </tr>
                    </tbody>
                </table>
            </td>
        </tr>
        <tr>
            <td>
                &nbsp;
            </td>
        </tr>
        <tr>
            <td class="td_gris_l">
                <center>
                    <%=mensaje%>
                </center>
            </td>
        </tr>
    </table>
</body>
</html>
