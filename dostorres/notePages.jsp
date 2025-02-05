<%@ page import="java.util.ResourceBundle,
                 com.desige.webDocuments.utils.ToolsHTML"%>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix = "logic"%>
<%@ taglib uri="WEB-INF/struts-bean.tld" prefix="bean" %>
<%
    ResourceBundle rb = ToolsHTML.getBundle(request);
%>
<!--/**
 * Title: ShowDocument.java<br>
 * Copyright: (c) 2003 Consis International<br>
 * Company: Consis International (CON)<br>
 * @author Nelson Crespo (NC)
 * @author Simón Rodriguez (SR)
 * @version Acsel-e v2.2
 * <br>
 * Changes:<br>
 * <ul>
 *      <li> 16/06/2005 (SR) Se invalida la tecla ctrl en java script para imprimir en la forma </li>
 *      <li> 11/08/2005 (NC) Cambios para mostrar los firmantes del documento
 * <ul>
 */-->

<html>
<head>
<title></title>
<jsp:include page="meta.jsp" /> 
 <script language="JavaScript">
     document.onkeydown = function() {
        if(window.event && (window.event.keyCode == 122 || window.event.keyCode == 116 || window.event.ctrlKey)) {
            window.event.keyCode = 505;
        }
        if(window.event.keyCode == 505){
            return false;
        }
     }
 </script>

<script languaje="javascript">


</script>

<link href="estilo/estilo.css" rel="stylesheet" type="text/css">
</head>
<body class="bodyInternas">
    <table cellSpacing=0 cellPadding=2 align=center border=0 width="100%">
        <tr>
            <td width="50%" class="td_titulo_C">
                <logic:present name="showCharge" scope="session">
                   <%=rb.getString("charge.charge")%>
               </logic:present>
               <logic:notPresent name="showCharge" scope="session">
                   <%=rb.getString("charge.userName")%>
               </logic:notPresent>
            </td>
            <td width="*" class="td_titulo_C">
               <%=rb.getString("showDoc.dateapproved")%>
            </td>
        </tr>
        <logic:present name="firmas" scope="session">
            <logic:iterate id="participation" name="firmas" type="com.desige.webDocuments.document.forms.BeanFirmsDoc" scope="session">
                <tr>
                    <td class="td_gris_l">
                        <logic:present name="showCharge" scope="session">
                            <%=participation.getCharge()%>
                        </logic:present>
                        <logic:notPresent name="showCharge" scope="session">
                            <%=participation.getNameUser()%>
                        </logic:notPresent>
                    </td>
                    <td class="td_gris_l">
	                    <%=participation.getDateReplied()%>
                    </td>
                </tr>
            </logic:iterate>
        </logic:present>
<%--        <tr>--%>
<%--            <td class="td_vino">--%>
<%--                <%=rb.getString("showDoc.noteTitle")%>--%>
<%--            </td>--%>
<%--        </tr>--%>
<%--        <tr>--%>
<%--            <td class="td_gris_l" width="100%">--%>
<%--                <center>--%>
<%--                    <%=rb.getString("showDoc.note")%>--%>
<%--                </center>--%>
<%--            </td>--%>
<%--        </tr>        --%>
<%--    <form name="uppload" method="post" action="servletloaddocuments?cmd=updateVersion" enctype="multipart/form-data">--%>
<%--        <input type="file" name="nameFile" value="C:\doc.doc" class="classText" style="display:none">--%>
<%--        <input type="hidden" name="idDocument" value="<%=request.getParameter("idDocument")%>"/>--%>
<%--        <input type="hidden" name="numVer" value="<%=request.getParameter("idVersion")%>"/>--%>
<%--        <input type=hidden name="cmd" value='updateVersion'>--%>
<%--    </form>--%>
    </table>
</body>
</html>
