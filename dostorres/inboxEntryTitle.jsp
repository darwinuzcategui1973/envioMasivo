<!-- inboxEntryTitle.jsp -->
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
<script type="text/javascript">
    function checkAll() {
    	var formaSelection = window.parent.lista.document.getElementById("Selection");
    	var checkAllItems = document.getElementById("all").checked;
    	
    	with(window.parent.lista){
	        if (formaSelection && formaSelection.length > 1) {
	            for (row = 1; row < formaSelection.length; row++) {
	                if (formaSelection.elements[row].value.length > 0) {
	                    formaSelection.elements[row].checked = checkAllItems;
	                }
	            }
	        }
        }
    }

    function paging_OnClick(pageFrom){
    	with(window.parent.lista){
	    	document.formPagingPage.target="lista";
    	    document.formPagingPage.from.value = pageFrom;
        	document.formPagingPage.submit();
        }
    }

</script>

</head>

<body class="bodyInternas" <%=onLoad%>>
<table align=center width="100%" cellspacing="0" cellpadding="0" border="0">
    <tr class="none">
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
            <table class="clsTableTitle" width="100%" cellSpacing="0" cellPadding="0" border="0">
                <tbody>
                    <tr>
	                    <td width="30%" align="left"  height="21px">
	                    	<span id="selector">
	                    	</span>
                        </td>
                        <td class="td_title_bc" width="40%"  height="21px">
                            <%=rb.getString("mail.inbox")%>
                        </td>
	                    <td class="td_title_bc" width="30%"  height="21px">
	                    	&nbsp;
                        </td>
                    </tr>
                </tbody>
            </table>
        </td>
    </tr>
    <tr>
        <td width="100%" valign="top">
        		<form name="forma">
                <table border="0" width="100%" cellSpacing="0" cellPadding="0">
                    <tr>
                        <td class="td_orange_l_b barraBlue" width="6%">
                            <p align="center">
                                <input type="checkbox" name="all" id="all" onClick="javascript:checkAll();"/>
                            </p>
                        </td>
                        <td class="td_orange_l_b barraBlue borderLeft" width="28%">
                            &nbsp;<%=rb.getString("mail.from")%>
                        </td>
                        <td class="td_orange_l_b barraBlue borderLeft" width="50%">
                            &nbsp;<%=rb.getString("mail.subject")%>
                        </td>
                        <td class="td_orange_l_b barraBlue borderLeft" width="16%">
                        	&nbsp;<%=rb.getString("mail.date")%>
                        </td>
                    </tr>
                </table>
                </form>
        </td>
    </tr>
</table>
</body>
</html>
