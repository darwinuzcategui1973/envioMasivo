<!-- inboxWFs.jsp -->
<%@ page import="java.util.ResourceBundle,
				 java.util.Collection,
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
    Users usuario = (Users)session.getAttribute("user");
    String ok = (String)session.getAttribute("usuario");
    
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


    
    ToolsHTML.clearSession(session,"application.ppal");
    String tipoImpresion=HandlerDocuments.TypeDocumentsImpresion;
    
    int docMyTask = Integer.parseInt(ToolsHTML.isEmptyOrNull(String.valueOf(request.getAttribute("myTaskSize")),"0"));
    int docOrdersTask = Integer.parseInt(ToolsHTML.isEmptyOrNull(String.valueOf(request.getAttribute("taskRequestSize")),"0"));
%>
<html>
<head>
<title><%=rb.getString("principal.title")%></title>
<jsp:include page="meta.jsp" /> 
<link href="estilo/estilo.css" rel="stylesheet" type="text/css">
<script type="text/javascript">
function ver(etiqueta,accion) {
	window.open("inboxWFs"+etiqueta+"Tit.jsp","bandejaTit");
	window.open(accion,"bandeja");
}
	function showDocument(idDoc){
		var forma = document.getElementById("selection");
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

<style>

.ppalText {
	padding:0 20px 0 10px;
	font-size: 12px;
	margin: 0px;
	color: #000000;
	font-family: Sans-serif, Helvetica, Arial, Verdana ;
    text-decoration:none; 
    font-size:11px; 
}


</style> 
</head>

<body class="bodyInternas" <%=onLoad.toString()%>>

<table cellSpacing="1" cellPadding="0" align="center" border="0" width="100%"  height="100%" >

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
		<td width="<%=ToolsHTML.ANCHO_MENU%>px"  valign="top" align="left" >
			<table cellSpacing="0" cellPadding="2"  border="0" width="<%=ToolsHTML.ANCHO_MENU%>px"  height="100%" style="/*border-collapse:collapse;*/border-color:#ofofof;border:1px solid #efefef">
				<tr>
					<td height="30px" class="ppalBoton" onmouseover="this.className='ppalBoton ppalBoton2'" onmouseout="this.className='ppalBoton'" >
						<table border="0" cellspacing="0" cellpadding="2" height="100%" >
							<tr>
								<td class="ppalTextBold" width="100%">
									<%=rb.getString("enl.workFlow")%>
								</td>
								<td>
								    &nbsp;
								</td>
								<td>
								    &nbsp;
								</td>
								<td>
								    &nbsp;
								</td>
								<td>
								    <span ><img src="icons/cancel.png" title="<%=rb.getString("btn.delete")%>" onClick="window.bandeja.youAreSure()"></span>
								</td>
							</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td height="99%" bgcolor="white" valign="top" background="menu-images/backgd.png">
					
						<table class="ppalText" style="cursor:pointer;" cellspacing="5">
							<tr onclick="ver('MyTask','loadInboxWFMyTask.do')">
								<td><img src="icons/arrow_switch.png"/></td>
								<td><%=rb.getString("inWF.myTask")%> <span id="totalMyTask" style="display:<%=docMyTask==0?"none":""%>" class="cantidad">(<%=docMyTask%>)</span></td>
							</tr>
							<tr onclick="ver('OrdersTask','loadInboxWFOrdersTask.do')">
								<td><img src="icons/arrow_switch_back.png"/></td>
								<td><%=rb.getString("inWF.taskRequest")%> <span id="totalOrdersTask" style="display:<%=docOrdersTask==0?"none":""%>" class="cantidad">(<%=docOrdersTask%>)<span></td>
							</tr>
						</table>
					
					</td>
				</tr>
						<%if (usuario!=null) {
                            out.println(ToolsHTML.printMenu(rb,usuario.getUser(),usuario.getIdGroup(),true,request));
                        }%>
			</table>
		</td>
		<td align="center">
			<table border="0" cellspacing="0" cellpadding="0" width="100%" height="100%">
				<tr>
					<td height="40px">
						<iframe name="bandejaTit" width="100%" height="40px" src="inboxWFsMyTaskTit.jsp" border="0px" marginwidth="0px" marginheight="0px" frameborder="0" scrolling="no"></iframe>
					</td>
				</tr>
				<tr>
					<td>
						<iframe name="bandeja" width="100%" height="100%" src="loadInboxWFMyTask.do" border="0px" marginwidth="0px" marginheight="0px" frameborder="0"></iframe>
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table>
</body>
</html>
