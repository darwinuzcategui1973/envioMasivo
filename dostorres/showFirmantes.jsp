<%@ page import="java.util.ResourceBundle,
                 com.desige.webDocuments.utils.ToolsHTML"%>

<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ page language="java" %>
<!--/**
 * Title: showFirmantes.jsp <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
  * @author Ing. Simon Rodríguez (SR)
 * @version WebDocuments v3.0
 * <br/>
 * Changes:<br/>
 * <ul>
 * </ul>
 */-->
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
                <td colspan="3">
                    <table class="clsTableTitle" width="100%" cellSpacing=0 cellPadding=2 align=center border=0>
                        <tbody>
                            <tr>
                                <td class="td_title_bc" height="21">
                                   <%=rb.getString("sch.signature")%>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </td>
            </tr>
            <tr>
            </tr>
            <logic:present name="userFirmantes" scope="session">
                <tr>
                    <td class="td_orange_l_b" width="5%">
                        <center><%=rb.getString("user.usuario")%></center>
                    </td>
                    <td class="td_orange_l_b" width="5%">
                        <center><%=rb.getString("user.cargo")%></center>
                    </td>
                    <td class="td_orange_l_b" width="5%">
                        <center><%=rb.getString("doc.dateFirm")%></center>
                    </td>
                </tr>
                <logic:iterate id="participation" name="userFirmantes" indexId="ind" type="com.desige.webDocuments.document.forms.BeanFirmsDoc" scope="session">
                    <%
                        int item = ind.intValue()+1;
                        int num = item%2;
                    %>
                    <tr class='td_<%=num%>'>
                            <td>
                                <bean:write name="participation" property="nameUser"/>
                            </td>
                            <td>
                                <bean:write name="participation" property="charge"/>
                            </td>
                            <td>
                                <bean:write name="participation" property="dateReplied"/>
                            </td>
                        </tr>
                </logic:iterate>
            </logic:present>
            <logic:notPresent name="userFirmantes" scope="session">
                <tr>
                    <td class="td_orange_l_b" width="5%" colspan="3">
                        <center><%=rb.getString("sch.noSignature")%></center>
                    </td>
                </tr>
            </logic:notPresent>
            <tr>
                <td colspan="3" align="center" >
                    <input type="button" value="<%=rb.getString("btn.ok")%>" class="boton" onClick="javascript:closeWin();" />
                </td>
            </tr>
        </table>
    </center>
</form>
</body>
</html>
