<!-- inboxOLD.jsp -->
<%@ page import="java.util.ResourceBundle,
                 com.desige.webDocuments.utils.ToolsHTML,
                 com.desige.webDocuments.utils.beans.SuperActionForm"%>
<%@ page import="com.desige.webDocuments.utils.beans.PaginPage"%>

<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<%@ page language="java" %>
<%
	if(request.getAttribute("visible")==null || !String.valueOf(request.getAttribute("visible")).equals("true") ) {
		response.sendRedirect("loadInbox.do");
	}

	ResourceBundle rb = ToolsHTML.getBundle(request);
    String onLoad = ToolsHTML.getMensajePages(request,rb);
    if (onLoad==null){
        response.sendRedirect(rb.getString("href.logout"));
    }          
    String cmd = (String)session.getAttribute("cmd");
    if (cmd==null){
        cmd = SuperActionForm.cmdLoad;
    }
    pageContext.setAttribute("cmd",cmd);
    
%>
<html>
<head>
<title><%=rb.getString("principal.title")%></title>
<jsp:include page="meta.jsp" /> 
<link href="estilo/estilo.css" rel="stylesheet" type="text/css">
<style>
.clsMessageView
{
  background: white;
  color: black;
  border: 1px solid #FFFFFF;
  padding: 2;
  font-size: 12px;
  font-family: Verdana, Arial, Helvetica;
  font-weight: normal;
  overflow: scroll;
}
</style>
<script type="text/javascript">
    function cancelar() {
    	var form = document.getElementById("Selection");
        form.action="loadStructMain.do";
        form.cmd.value="<%=SuperActionForm.cmdLoad%>";
        form.submit();
    }
   function updateCheck(check,field) {
        if (check.checked){
            field.value = "0";
        } else{
            field.value = 1;
        }
    }

    function showDetailsMail(idMessage,idMessageUser){
    	var formaSelection = document.getElementById("Selection");
    	formaSelection.idMessage.value = idMessage;
        formaSelection.idMessageUser.value = idMessageUser;
        formaSelection.submit();
    }

    function validateCheck() {
    	var formaSelection = document.getElementById("Selection");
        for (var i=3; i < formaSelection.length;i++) {
            if (formaSelection.elements[i].checked) {
                return true;
            }
        }
        return false;
    }

    function youAreSure() {
    	var forma = document.getElementById("Selection");
        if (!validateCheck()) {
            alert('<%=rb.getString("error.selectValue")%>');
        } else {
            if (confirm("<%=rb.getString("areYouSure")%>")) {
                if (forma) {
                    forma.action="deleteMail.do";
                    forma.submit();
                }
            }
        }
    }

    function checkAll() {
    	var formaSelection = document.getElementById("Selection");
        if (formaSelection.length > 3) {
            for (row = 3; row < formaSelection.length; row++) {
                if (formaSelection.elements[row].value.length > 0) {
                    formaSelection.elements[row].checked = formaSelection.all.checked;
                }
            }
        }
    }

    function paging_OnClick(pageFrom){
        document.formPagingPage.from.value = pageFrom;
        document.formPagingPage.submit();
    }

</script>
</head>

<body class="bodyInternas" <%=onLoad%>>

<logic:present name="inbox" scope="session">
    <table align=center border=0 width="100%" >
        <tr>
            <td class="pagesTitle">
                   <table cellspacing="0" cellpadding="0" border="0">
                   	<tr></td>
	                    <%=ToolsHTML.printMenuInnerToolBar(ToolsHTML.getMenuMailsII(rb),"",true,false, rb)%>
                   	</td></tr>
                   </table>
            </td>
        </tr>
        <tr>
            <td valign="top">
                <table class="clsTableTitle" width="100%" cellSpacing="0" cellPadding="2" align=center border=0>
                    <tbody>
                        <tr>
                            <td class="td_title_bc" width="80%" height="21">
                                <%=rb.getString("mail.inbox")%>
                            </td>
                        </tr>
                    </tbody>
                </table>
            </td>
        </tr>
        <form name="Selection" id="Selection" action="showDetailsMail.do">
            <input type="hidden" name="idMessage" value=""/>
            <input type="hidden" name="idMessageUser" value=""/>
            <tr>
                <td width="100%" valign="top">
                    <table align=center border=0 width="100%" cellSpacing="2" cellPadding="1">
                        <tr>
                            <td class="td_orange_l_b barraBlue" width="7%">
                                <p align="center">
                                    <input type="checkbox" name="all" onClick="javascript:checkAll();"/>
                                </p>
                            </td>
                            <td class="td_orange_l_b barraBlue" width="28%">
                                <%=rb.getString("mail.from")%>
                            </td>
                            <td class="td_orange_l_b barraBlue" width="38%">
                                <%=rb.getString("mail.subject")%>
                            </td>
                            <td class="td_orange_l_b barraBlue" width="27%">
                                <%=rb.getString("mail.date")%>
                            </td>
                        </tr>
                        <!-- Paginación -->
                        <%
                            String from = request.getParameter("from");
                            String size = (String)session.getAttribute("size");
                            if (!ToolsHTML.isNumeric(size)) {
                                size = "1";
                            }
                            if (!ToolsHTML.isNumeric(from)) {
                                from = "0";
                            }
                            PaginPage Pagingbean = ToolsHTML.getBeanPaginPage(from,Integer.parseInt(size),"30");
                        %>
                        <!-- Formulario para Obtener el nodo Seleccionado por el usuario -->
                        <logic:iterate id="mail" name="inbox" indexId="ind" offset="<%=Pagingbean.getDesde()%>" length="<%=Pagingbean.getCuantos()%>"
                         type="com.desige.webDocuments.mail.forms.MailForm" scope="session">
		                    <%
		                        int item = ind.intValue()+1;
		                        int num = item%2;
		                    %>
		                    <tr class='fondo_<%=num%>'>
                                <td class="td_gris_l" valign="top">
                                    <center>
    <%--                                    <input name="mail" type="checkbox" value="<%=mail.getIdMessage()%>,<%=mail.getIdMessageUser()%>">--%>
                                        <% if (mail.isNew()) { %>
                                           <%-- <input name="mail" type="checkbox" disabled value="">--%>
                                            <input name="mail" type="checkbox" value="<%=mail.getIdMessage()%>,<%=mail.getIdMessageUser()%>">
                                            <img src="img/newMail.gif" border="0">
                                        <% } else { %>
                                            <input name="mail" type="checkbox" value="<%=mail.getIdMessage()%>,<%=mail.getIdMessageUser()%>">
                                            <img src="img/mailReviced.gif" border="0">
                                        <% } %>
                                    </center>
                                </td>
                                <td class="td_gris_l">
                                    <%=mail.getNameFrom()%>
                                </td>
                                <td class="td_gris_l">
                                    <a class="ahref_b" href="javascript:showDetailsMail('<%=mail.getIdMessage()%>','<%=mail.getIdMessageUser()%>');">
                                        <%=mail.getSubject()%>
                                    </a>
                                </td>
                                <td class="td_gris_l">
                                    <%=mail.getDateMail()%>
                                </td>
                            </tr>
                        </logic:iterate>
                    </form>
                    <%
                        if (size.compareTo("0") > 0) {
                    %>
                            <tr>
                                <td align="center" colspan="4">
                                    <!-- Inicio de Paginación -->
                                    <table class="paginador">
                                        <tr>
                                            <td align="center" colspan="4">
                                                <br/>
                                                    <font size="1" color="#000000">
                                                        <%=rb.getString("pagin.title")+ " "%>
                                                        <%=Integer.parseInt(Pagingbean.getPages())+1%>
                                                        <%=rb.getString("pagin.of")%>
                                                        <%=Pagingbean.getNumPages()%>
                                                    </font>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td align="center"> <img src="img/First.gif" width="24" height="24"
                                               class="GraphicButton" title="<%=rb.getString("pagin.First")%>"
                                               onclick="paging_OnClick(0)">
                                            </td>
                                            <td align="center"> <img src="img/left.gif" width="24" height="24"
                                               class="GraphicButton" title="<%=rb.getString("pagin.Previous")%>"
                                               onclick="paging_OnClick(<%=(Integer.parseInt(Pagingbean.getPages())-1)%>)">
                                            </td>
                                            <td align="center"> <img src="img/right.gif" width="24" height="24"
                                               class="GraphicButton" title="<%=rb.getString("pagin.next")%>"
                                               onclick="paging_OnClick(<%=(Integer.parseInt(Pagingbean.getPages())+1)%>)">
                                            </td>
                                            <td align="center"> <img src="img/End.gif" width="24" height="24"
                                               class="GraphicButton" title="<%=rb.getString("pagin.Last")%>"
                                               onclick="paging_OnClick(<%=Pagingbean.getNumPages()%>)">
                                            </td>
                                        </tr>
                                    </table>

                                    <form name="formPagingPage" method="post" action="inbox.jsp">
                                      <input type="hidden" name="from"  value="">
                                      <input type="hidden" name="cmd"  value="<%=SuperActionForm.cmdLoad%>">
                                    </form>
                                    <!-- Fin de Paginación -->
                                </td>
                            </tr>
                        <%
                            }
                        %>
                </table>
            </td>
        </tr>
    </table>
</logic:present>
<logic:notPresent name="inbox" scope="session">
    <table align=center border=0 width="100%">
        <tr>
            <td class="pagesTitle">
                <%=rb.getString("mail.inbox")%>
            </td>
        </tr>
        <tr>
            <td valign="top">
                <table class="clsTableTitle" width="100%" cellSpacing="0" cellPadding="2" align=center border=0>
                    <tbody>
                        <tr>
                            <td class="td_title_bc" width="80%" height="21">
                                <%=rb.getString("mail.inbox")%>
                            </td>
                        </tr>
                    </tbody>
                </table>
            </td>
        </tr>
         <tr>
            <td>
                <%=ToolsHTML.printMenuInner(ToolsHTML.getMenuMailsII(rb),"")%>
            </td>
        </tr>
    </table>
</logic:notPresent>
</body>
</html>
