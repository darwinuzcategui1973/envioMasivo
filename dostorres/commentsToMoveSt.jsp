<jsp:include page="richeditDocType.jsp" /> 
<!--
 * Title: commentsToMoveSt.jsp
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * @author Ing. Nelson Crespo (NC)
 * @version WebDocuments v3.0
 * <br/>
 * Changes:<br/>
 * <ul>
 *      <li> 20/07/2005 (NC) Creaci�n </li>
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
%>
<html>
<head>
<title><%=rb.getString("principal.title")%></title>
<jsp:include page="meta.jsp" /> 
<link href="estilo/estilo.css" rel="stylesheet" type="text/css">
<script type="text/javascript" src="./estilo/funciones.js"></script>
<jsp:include page="richeditHead.jsp" /> 

<script type="text/javascript">

    setDimensionScreen();

    function send(forma) {
    	updateRTEs();
        forma.comments.value = forma.richedit.value;
        if (validar(forma)) {
           //alert(forma.action);
            forma.submit();
        }
    }

    function cancelar(forma) {
    	forma.action="loadStructMain.do";
    	forma.submit();
    }

    function validar(forma){
        if (forma.comments.value.length==0){
            alert ("<%=rb.getString("moveDoc.notComments")%>");
            return false;
        }
        return true;
    }

    function save() {
    }
</script>
</head>

<body class="bodyInternas">
    <html:form action="/moveStruct.do">
        <logic:present name="width" scope="session">
            <input type="hidden" name="goTo" value="loadStructMain.do"/>
            <input type="hidden" name="idNodeSelected" value="<%=session.getAttribute("idDocument")%>"/>
        </logic:present>
        <table align=center border=0 width="100%">
            <html:hidden name="moveStructForm" property="idDocument"/>
            <tr>
                <td class="pagesTitle" colspan="2">
                    <%=rb.getString("moveDoc.TitleSt")%>
                </td>
            </tr>
            <tr>
                <td colspan="2">
                    <table class="clsTableTitle" width="100%" cellSpacing=0 cellPadding=2 align=center border=0>
                        <tbody>
                            <tr>
                                <td class="td_title_bc" height="21">
                                    <%=rb.getString("moveDoc.TitleSt")%>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </td>
            </tr>
            <tr>
                <td class="td_gris_l" colspan="2">&nbsp;

                </td>
            </tr>
            <tr>
                <td height="42" class="titleLeft" style="background: url(img/btn120x2.png); background-repeat: no-repeat" width="12%" valign="middle">
                    <%=rb.getString("moveDoc.from")%>:
                </td>
                <td class="td_gris_l">
                    <bean:write name="moveStructForm" property="fromNoHref" />
                    <html:hidden name="moveStructForm" property="fromID"/>
                </td>
            </tr>
                <tr>
                    <td height="42" class="titleLeft" style="background: url(img/btn120x2.png); background-repeat: no-repeat" valign="middle">
                        <%=rb.getString("moveDoc.to")%>:
                    </td>
                    <td class="td_gris_l">
                        <bean:write name="moveStructForm" property="toNoHref"/>
                        <html:hidden name="moveStructForm" property="toID"/>
                    </td>
                </tr>
            <html:textarea property="comments" style="display:none" />
            <tr>
                 <td colspan="2" heigth="26" class="titleCenter" style="background: url(img/BgComments.gif); background-repeat: no-repeat">
                     <%=rb.getString("moveDoc.notComments")%>
                 </td>
            </tr>
            <tr>
                <td class="fondoEditor" colspan="2" valign="top">
                	  <jsp:include page="richedit.jsp">
                	  	<jsp:param name="richedit" value="richedit"/>
					  </jsp:include>
                </td>
            </tr>
            <tr>
                <td colspan="2" align="center" valign="top">
                    <input type="button" class="boton" value="<%=rb.getString("btn.copy")%>" onClick="javascript:send(this.form);" />
                    &nbsp;
                    <input type="button" class="boton" value="<%=rb.getString("btn.cancel")%>" onClick="javascript:cancelar(this.form);" />
                </td>
            </tr>
        </table>
    </html:form>
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
