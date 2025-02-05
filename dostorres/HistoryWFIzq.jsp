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
<title><%=rb.getString("principal.title")%></title>
<jsp:include page="meta.jsp" /> 
<link href="estilo/estilo.css" rel="stylesheet" type="text/css">
<script type="text/javascript">
    var history = true;
    function showDocument(item){
        var hWnd = null;
        hWnd = window.open("showDocument.do?idDocument="+item,"Catalogo","width=800,height=600,resizable=yes,scrollbars=yes,left=100,top=150");
        if ((document.window != null) && (!hWnd.opener)) {
            hWnd.opener = document.window;
        }
    }

    function load(idMovement,idWorkFlow,typeMovement,isFlexFlow,typeWF,subtypeWF) {
    	var formaSelection = document.getElementById("Selection");
        formaSelection.target="_parent";
        formaSelection.idWorkFlow.value = idWorkFlow;
        formaSelection.idMovement.value = idMovement;
        formaSelection.typeMovement.value = typeMovement;
        formaSelection.isFlexFlow.value = isFlexFlow;
        //ydavila Ticket 001-00-003023
        formaSelection.typeWF.value = typeWF;
        formaSelection.subtypeWF.value = subtypeWF;
        formaSelection.submit();
    }
</script>
</head>

<body class="bodyInternas">
<logic:present name="history" >
    <!-- Formulario para Obtener el nodo Seleccionado por el usuario -->
            <form name="Selection" id="Selection" action="loadHistoryWF.do">
                <input type="hidden" name="idWorkFlow" value=""/>
                <input type="hidden" name="idMovement" value=""/>
                <input type="hidden" name="typeMovement" value=""/>
                <input type="hidden" name="isFlexFlow" value=""/>
                <input type="hidden" name="idDocument" value="<bean:write name="showDocument" property="numberGen"/>"/>
                <%--ydavila Ticket 001-00-003023--%>
                <input type="hidden" name="typeWF" value=""/>
                <input type="hidden" name="subtypeWF" value=""/>
            </form>
    <!-- END Form -->
    <table align=center border=0 width="100%">
        <tr>
            <td class="td_title_hist" height="21" bgcolor="#B3B3B3" align="center" width="100%">
                <bean:write name="showDocument" property="nameDocument"/>
            </td>
        </tr>
        <logic:iterate id="doc" name="history" type="com.desige.webDocuments.workFlows.forms.DataWorkFlowForm" scope="session" >
            <tr>
                <td width="100%" class="td_gris_l">
                    <% if (!doc.isFlexFlow()) { %>
                    	<a class="ahref_b" href="javascript:load('<%=doc.getIdMovement()%>','<%=doc.getIdWorkFlow()%>','<%=doc.getTypeMovement()%>',false);">
                    	
                    	<%=doc.getEliminar()!=0?rb.getString("wf.titleWF4"):""%> <%=doc.getCambiar()!=0?rb.getString("wf.titleWF3"):""%>                     	                    
                             <%=rb.getString("wf.typeMovement"+doc.getTypeMovement()) +"<br/>" + doc.getDateBegin() +"<br/>" + doc.getIdWorkFlow()%> <%=doc.getEliminar()!=0?rb.getString("wf.titleWF4"):""%> <%=doc.getCambiar()!=0?rb.getString("wf.titleWF3"):""%> 
                        </a>
                    <% } else {%>
                        <a class="ahref_b" href="javascript:load('<%=doc.getIdMovement()%>','<%=doc.getIdWorkFlow()%>','<%=doc.getTypeMovement()%>',true);">
                            <%=doc.getNameWF() + "<br/>" + doc.getDateBegin()%>
                        </a>
                    <% } %>
                </td>
            </tr>
            <tr>
                <td>
                    &nbsp;
                </td>
            </tr>
        </logic:iterate>
    </table>
</logic:present>
<logic:notPresent name="history">
    <table cellSpacing=0 cellPadding=0 align=center border=0 width="100%">
        <tr>
            <td class="td_orange_l_b">
                <%=rb.getString("wf.selecItem")%>
            </td>
        </tr>
    </table>
</logic:notPresent>
</body>
</html>
