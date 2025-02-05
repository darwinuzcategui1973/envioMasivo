<!-- principalDocReq.jsp --> 
<%@page import="com.focus.util.PerfilAdministrador"%>
<%@ page import="java.util.ResourceBundle,
                 com.desige.webDocuments.utils.ToolsHTML,
                 com.desige.webDocuments.utils.beans.Users,
                 com.desige.webDocuments.persistent.managers.HandlerDocuments"%>
<%@ page import="org.apache.struts.taglib.html.Constants"%>

<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<%@ page language="java" %>

<%
    ResourceBundle rb = ToolsHTML.getBundle(request);
    StringBuffer onLoad = new StringBuffer("");
    String ok = (String)session.getAttribute("usuario");
    Users usuario = (Users)session.getAttribute("user");
    int docTotal = 0;
    int docComplete = 0;
    int docMissing = 0;
    
    int MAXIMO = 2;

    //Luis Cisneros 12/03/07 Para mostrar informaci'on de info en la pestana ppal
    String info = (String) ToolsHTML.getAttribute(request, "info", true);
    //Fin 12/03/07

    if ((ok != null) && (ok.compareTo("") != 0)) {
        onLoad.append(" onLoad=\"self.status='").append(rb.getString("pagin.welcome")).append(" ");
        onLoad.append(ok);
        onLoad.append("'");

        //Luis Cisneros 12/03/07 Para mostrar informaci'on de info en la pestana ppal
        if (ToolsHTML.checkValue(info)) {
            onLoad.append(";alert('").append(info).append("');");
        }
        //Fin 12/03/07
        
        onLoad.append("\"");
    } else{
        response.sendRedirect(rb.getString("href.logout"));
    }


    
    //ToolsHTML.clearSession(session,"application.ppal");
    String tipoImpresion=HandlerDocuments.TypeDocumentsImpresion;
    String confirmJSMsg = rb.getString("sendEmail.jsAlert");
%>
<html>
<head>
<title><%=rb.getString("principal.title")%></title>
<jsp:include page="meta.jsp" /> 
<link href="estilo/estilo.css" rel="stylesheet" type="text/css">
<script type="text/javascript" src="script/commonJS.js"></script>
<script type="text/javascript">
	<!-- variables globales -->

	function showDocument(idDoc){
		var forma = document.getElementById("selection");
		forma.target='info';
		forma.idDocument.value = idDoc;
		forma.action = "showDataDocument.do";
		forma.submit();
	}
	function validar(){
        if (document.login.docSearch.value.length>0){
            alert('<%=rb.getString("err.notDocumentSearch")%>');
        }else{
		    document.login.submit();
        }
	}

    function showWF(idWorkFlow,row,owner,isFlexFlow){
    	var forma = document.getElementById("selection");
        forma.idWorkFlow.value = idWorkFlow;
        forma.row.value = row;
        forma.owner.value = owner;
        forma.isFlexFlow.value = isFlexFlow;
        forma.showStruct.value = 'false';
        forma.submit();
    }
</script>
</head>

<body class="bodyInternas" <%=onLoad.toString()%>>
<table cellSpacing="0" cellPadding="0" align=center border=0 width="100%">
	<caption>
		<%if(request.getAttribute("planDateFrom")!=null){%>
			<span style="font-family:Arial;"><b><%=rb.getString("sch.from")%>: <%=ToolsHTML.formatDateShow((String)request.getAttribute("planDateFrom"),false)%> - <%=rb.getString("sch.to")%>: <%=ToolsHTML.formatDateShow((String)request.getAttribute("planDateUntil"),false)%></b></span>
			<br> 
		<%}%>
	</caption>
    <form name="selection" id="selection" action="loadWF.do">
        <input type="hidden" name="idWorkFlow" value="" />
        <input type="hidden" name="row" value="" />
        <input type="hidden" name="idMovement" value="" />
		<input type="hidden" name="idDocument" value="" />
        <input type="hidden" name="owner" value="" />
        <input type="hidden" name="showStruct" value="true"/>
        <input type="hidden" name="isFlexFlow" value="" />
        <input type="hidden" name="origen" value="principal" />
    </form>

	<tr>
	<td width="100%" valign="top">
		<table width="100%" border="0" cellSpacing="0" cellPadding="0">
            <tr>
                <td>
                    <!-- Documentos Pendientes -->
                    <table width="100%" border="0" cellspacing="1" class="borderWindowPrincipal" >
                    
                        <logic:present name="docRequired" scope="request" >
                            <% 
                                int cont=0; 
                                Integer previousValue = 0;
                            %>
                            
                            <logic:iterate id="docCheck" name="docRequired" type="com.desige.webDocuments.document.forms.DocumentsCheckOutsBean" scope="request" >
                                <%
                                        if (PerfilAdministrador.userIsInAdminGroup(usuario)
                                                && !previousValue.equals(docCheck.getPersonBean().getIdPerson())) {
                                            previousValue = docCheck.getPersonBean().getIdPerson();
                                    %>
                                    <tr class='fondo_header'>
                                    <td colspan="4">
                                        <span class="txt_b">
                                            &nbsp;
                                            <b><%= docCheck.getPersonBean().getApellido() + ", " + docCheck.getPersonBean().getNombre() %> <%docTotal++;%> </b>
                                        </span>
                                    </td>
                                    </tr>
                                    <%
                                        }
                                    %>
                                <tr class='fondo_<%=cont==0?cont++:cont--%>'>
                                    <%if(docCheck.getMayorVer().equals("")){
                                    	docMissing++;%>
                                    <td colspan="4">
                                        &nbsp;&nbsp;&nbsp;&nbsp;
                                    </td>
                                    <%} else {
                                    	docComplete++;%>
                                    <td width="3%">
                                        <img src="images/nuevo.gif" width="15px" height="15px" style="margin-left:30px;padding-right:10px;"/> 
                                    </td>
                                    <td class="txt_b" width="5%">
                                        <%=docCheck.getMayorVer()+"."+docCheck.getMinorVer()%>
                                    </td>
                                    <td class="txt_b" width="73%">
                                        <%=docCheck.getNameDocument()+" " + docCheck.getPrefix()+docCheck.getNumber()%>
                                    </td>
                                    <td class="txt_b" width="23%" align="center">
                                        <%=docCheck.getDateCheckOut()%>
                                    </td>
                                	<%}%>
                                </tr>
                            </logic:iterate>
                        </logic:present>
                    </table>
                    <hr>
                    <div> <%=rb.getString("admin.required.assignedRequirements")%> = <%=docTotal-docMissing%> - <%=rb.getString("admin.required.missingRequirements")%> = <%=docMissing%></div>
                </td>
            </tr>
        </table>
	</td>
  </tr>
 </table>
</body>
</html>
