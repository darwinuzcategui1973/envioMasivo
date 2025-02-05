<jsp:include page="richeditDocType.jsp" /> 
<!--
 * Title: publicDocument.jsp
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * @author Ing. Nelson Crespo (NC)
 * @version WebDocuments v3.0
 * <br/>
 * Changes:<br/>
 * <ul>
 *      <li> 01/06/2005 (NC) Cambios para mostrar los comentario </li>
 *      <li> 24/04/2006 (NC) Cambios para usar request.getParameter("titleComments") </li>
 *      <li> 25/04/2006 (SR) Se sustituyo /WebDocuments/.. por ./..
 </ul>
-->

<%@ page import="java.util.ResourceBundle,
                 com.desige.webDocuments.utils.ToolsHTML"%>

<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<%@ page language="java" %>
<%
    ResourceBundle rb = ToolsHTML.getBundle(request);
    String width = "12%";
    if (request.getAttribute("width")!=null) {
        width = "25%";
    }
    String titleBack = request.getParameter("titleBack");
%>
<html>
<head>
<title><%=rb.getString("principal.title")%></title>
<jsp:include page="meta.jsp" /> 
<link href="estilo/estilo.css" rel="stylesheet" type="text/css">
<script language=javascript src="./estilo/funciones.js"></script>
<jsp:include page="richeditHead.jsp" />
<script language="JavaScript">

    setDimensionScreen();

    function send(forma){
    	updateRTEs();
        forma.comments.value = forma.richedit.value;
        if (forma.comments.value.length == 0) {
            alert ("<%=rb.getString("err.notCommentsBack")%>");
            return false;
        }
        forma.submit();
    }

    function cancelar(forma) {
        forma.action = "showDataDocument.do";
        forma.submit();
    }

    function save() {
    }
</script>
</head>

<body class="bodyInternas">
    <form name="rollBack" action="<%=request.getParameter("nexPage")%>">
        <input type="hidden" name="titleBack" value="<%=titleBack%>"/>
        <input type="hidden" name="idDocument" value="<%=request.getParameter("idDocument")%>"/>
        <input type="hidden" name="idCheckOut" value="<%=request.getParameter("idCheckOut")%>"/>

        <table align=center border=0 width="100%">
            <tr>
                <td class="pagesTitle" colspan="2">
                    <%=rb.getString(titleBack)%>
                </td>
            </tr>
            <tr>
                <td colspan="2">
                    <table class="clsTableTitle" width="100%" cellSpacing=0 cellPadding=2 align=center border=0>
                        <tbody>
                            <tr>
                                <td class="td_title_bc" height="21">
                                    <%=rb.getString(titleBack)%>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </td>
            </tr>
            <tr>
                <td class="td_gris_l" colspan="2">
                    &nbsp;
                </td>
            </tr>
            <tr>
                <td colspan="4" heigth="22" class="titleCenter" style="background: url(img/BgComments.gif); background-repeat: no-repeat" valign="middle">
                    <%--<%=rb.getString("back.titleComments")%>--%>
                    <%=rb.getString(request.getParameter("titleComments"))%>

                </td>
            </tr>
            <input type="textarea" name="comments" style="display:none"/>
<%--            <html:textarea property="mensaje" style="display:none" />--%>
            <tr>
                <td class="fondoEditor" colspan="2" valign="top">
					  <jsp:include page="richedit.jsp">
						<jsp:param name="richedit" value="richedit"/>
					  </jsp:include>                
                </td>
            </tr>
            <tr>
                <td colspan="2" align="center">
                    <input type="button" class="boton" value="<%=rb.getString("btn.send")%>" onClick="javascript:send(this.form);" />
                    &nbsp;
                    <input type="button" class="boton" value="<%=rb.getString("btn.cancel")%>" onClick="javascript:cancelar(this.form);" />
                </td>
            </tr>
        </table>
    </form>
    <script language="JavaScript" event="onload" for="window">
        <%  String mensaje = (String)request.getSession().getAttribute("info");
            if (mensaje!=null) {
                out.println("alert('"+mensaje+"');");
                request.getSession().removeAttribute("info");
            }
        %>
    </script>
</body>
</html>
