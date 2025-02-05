<%@ page import="java.util.ResourceBundle,
                 com.desige.webDocuments.utils.ToolsHTML"%>

<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<%@ page language="java" %>

<%
    ResourceBundle rb = ToolsHTML.getBundle(request);
%>
<html>
<head>
<title></title>
<jsp:include page="meta.jsp" /> 
<link href="estilo/estilo.css" rel="stylesheet" type="text/css">
<script language="JavaScript">
    function closeWin() {
        top.window.close();
    }
</script>
</head>

<body class="bodyInternas">
<br/>
<form name="getDate">
    <center>
        <table align=center border=0 width="100%">
            <tr>
                <td colspan="2">
                    <table class="clsTableTitle" width="100%" cellSpacing=0 cellPadding=2 align=center border=0>
                        <tbody>
                            <tr>
                                <td class="td_title_bc" height="21">
                                    <%=rb.getString("charge.title")%>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </td>
            </tr>
            <tr>
                <td class="titleLeft" style="background: url(img/btn120.png); background-repeat: no-repeat" width="30%" valign="middle">
                    <%=rb.getString("charge.userName")%>
                </td>
                <td class="td_gris_l" width="*">
                    <%=request.getParameter("userName").replaceAll("ampersand","&")%>
                </td>
            </tr>
            <tr>
                <td class="titleLeft" style="background: url(img/btn120x2.png); background-repeat: no-repeat" valign="middle">
                    <%=rb.getString("charge.charge")%>
                </td>
                <td class="td_gris_l">
                    <%=request.getParameter("charge")%>
                </td>
            </tr>
            <tr>
                <td colspan="2">
                    &nbsp;
                </td>
            </tr>
            <tr>
                <td colspan="2" align="center" >
                    <input type="button" class="boton" value="<%=rb.getString("btn.ok")%>" onClick="javascript:closeWin();" />
                </td>
            </tr>
        </table>
    </center>
</form>
</body>
</html>
