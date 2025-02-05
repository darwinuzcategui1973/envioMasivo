<!--
 * Title: info.jsp <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * @author Ing. Nelson Crespo (NC)
  * @version WebDocuments v3.0
 * <br/>
 * Changes:<br/>
 * <ul>
 *      <li> 14/05/2005 (NC) se modificó para cerrar la ventana según parametro enviado por el action </li>
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
<%
    ResourceBundle rb = ToolsHTML.getBundle(request);
    String urlActiveFactorynoExiste = request.getParameter("urlActiveFactorynoExiste")!=null?request.getParameter("urlActiveFactorynoExiste"):"";
    boolean noExisteArchivo=false;
    if ("1".equalsIgnoreCase(urlActiveFactorynoExiste)){
        noExisteArchivo=true;
    }
%>
<html>
<head>
<script language="JavaScript">
    if (<%=noExisteArchivo%>){
         alert('<%=rb.getString("wonderware.url")%>');
    }
    window.opener=''; window.close()
    function cancelar() {
        history.back();
    }
</script>


<title></title>
<jsp:include page="meta.jsp" /> 
<link href="estilo/estilo.css" rel="stylesheet" type="text/css">
</head>
<logic:present name="closeWindow" scope="request">
    <%
        request.removeAttribute("closeWindow");
    %>
<body onLoad="window.close();">
</logic:present>
<body class="bodyInternas">
    <table width="100%" border="0" align="center">
        <tr>
            <td class="pagesTitle">
                <%=rb.getString("application.info")%>
            </td>
        </tr>
        <tr>
            <td>
                <table class="clsTableTitle" width="100%" cellSpacing="0" cellPadding="0" align=center border=0>
                    <tbody>
                        <tr>
                            <td class="td_title_bc" height="21">
                                <%=rb.getString("application.info")%>
                            </td>
                        </tr>
                    </tbody>
                </table>
            </td>
        </tr>
        <logic:notPresent name="info" scope="session">
            <logic:notPresent name="error" scope="session">
                <tr>
                    <td class="td_gris_c">
                        <%=rb.getString("wonderware.activefactory")%>
                    </td>
                </tr>
            </logic:notPresent>
        </logic:notPresent>
        <logic:present name="info" scope="session">
            <tr>
                <td class="td_gris_c">
                    <%=session.getAttribute("info")%>
                    <%session.removeAttribute("info");%>
                </td>
            </tr>
        </logic:present>
        <logic:present name="error" scope="session">
            <tr>
                <td class="td_gris_c">
                    <%=rb.getString("wonderware.activefactory")%>
                    <%session.removeAttribute("error");%>
                </td>
            </tr>
            <tr>
                <td>
                    <p align="center">
                        <input type="button" class="boton" value="<%=rb.getString("btn.ok")%>" onclick="javascript:cancelar()" />
                    </p>
                </td>
            </tr>
        </logic:present>
    </table>
</body>
</html>
