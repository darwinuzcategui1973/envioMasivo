<!--
 * Title: infoIframe.jsp <br/>
 * Copyright: (c) 2007 Focus Consulting C.A.<br/>
 * @author YSA
 * @version WebDocuments v4.3
 * <br/>
 * Changes:<br/>
 * <ul>
 *      <li> 19/12/2007 (YS) Creacion </li>
 </ul>
-->

<%@ page contentType="text/html; charset=iso-8859-1"
language="java" import="java.util.ResourceBundle,
                        com.desige.webDocuments.utils.ToolsHTML"%>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title></title>
<jsp:include page="meta.jsp" /> 
<link href="estilo/estilo.css" rel="stylesheet" type="text/css">
</head>
<body class="bodyInternas">
    <table width="100%" border="0" align="center">
        <logic:present name="error" scope="session">
            <tr>
                <td class="td_gris_c">
                    <%=session.getAttribute("error")%>
                    <%session.removeAttribute("error");%>
                </td>
            </tr>
        </logic:present>
    </table>
</body>
</html>
