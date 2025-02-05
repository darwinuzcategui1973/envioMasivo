<!-- principalCan.jsp -->
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
	var http;
	

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
    
	function ajaxobj() {
		try {
			_ajaxobj = new ActiveXObject("Msxml2.XMLHTTP");
		} catch (e) {
			try {
				_ajaxobj = new ActiveXObject("Microsoft.XMLHTTP");
			} catch (E) {
				_ajaxobj = false;
			}
		}
		if (!_ajaxobj && typeof XMLHttpRequest!='undefined') {
			_ajaxobj = new XMLHttpRequest();
		}
		return _ajaxobj;
	}    
	
    function llenarCuadrante(cuadro) {
    	if(cuadro=="docpen") {
			ajax = ajaxobj();
			ajax.open("POST", "fillMainAjax.do?cuadrante="+cuadro, true);
			ajax.onreadystatechange=function() {
				if (ajax.readyState == 4) {
					var arr = eval(ajax.responseText);
					
					var box = document.getElementById(cuadro);
					var tit =box.firstChild;
					while (box.firstChild) {
					  box.removeChild(box.firstChild);
					}
					document.getElementById(cuadro+"Ver").style.display="none";
					box.appendChild(tit);
					
					for(var x=0;x<arr.length;x++) {
					    var tr    = document.createElement('TR');
					    var img    = document.createElement('IMG');
					    var ref    = document.createElement('a');
					    var td0   = document.createElement('TD');
					    var td1   = document.createElement('TD');
					    var td2   = document.createElement('TD');
					    var td3   = document.createElement('TD');
	
						img.src = arr[x].img;
						ref.value = arr[x].action;
						ref.href = "#";
						ref.onclick = function() {
							showDocument(this.value);
						}
						ref.appendChild(document.createTextNode(arr[x].col2));
						
						ref.className = "ahref_b";
					    td1.className = "txt_b";
					    td2.className = "txt_b";
					    td3.className = "txt_b";
					    
					    td0.appendChild(img);
					    td1.appendChild(document.createTextNode(arr[x].col1));
					    td2.appendChild(ref);
					    td3.appendChild(document.createTextNode(arr[x].col3));
	
					    tr.appendChild(td0);
					    tr.appendChild(td1);
					    tr.appendChild(td2);
					    tr.appendChild(td3);
					    
					    box.appendChild(tr);
				    }
					
				}
			}
			ajax.send(null);    		
    	}
    }
    
function deleterow(node)
{
// Obtain a reference to the containing tr. Use a while loop
// so the function can be called by passing any node contained by
// the tr node.
var tr = node.parentNode;
while (tr.tagName.toLowerCase() != "tr")
tr = tr.parentNode;

// Remove the tr node and all children.
tr.parentNode.removeChild(tr);
}    
</script>
</head>

<body class="bodyInternas" <%=onLoad.toString()%>>
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
    
<table cellSpacing="0" cellPadding="0" align=center border=0 width="100%">
   	<tr>
	<td width="100%" valign="top">
		<table width="100%" border="0" cellSpacing="0" cellPadding="0">
                <tr>
                    <td>
                        <!-- Flujos Pendientes -->
                        <table width="100%" border="0" cellspacing="1" class="borderWindowPrincipal" >
                            <logic:present name="wfCanceled" scope="session" >
                            	<% 
                            	    int cont=0;
                            	    Integer previousValue = 0;
                            	%>
                                <logic:iterate id="wfReq" name="wfCanceled" type="com.desige.webDocuments.workFlows.forms.DataUserWorkFlowForm" scope="session" >
                                    <%
                                        if (PerfilAdministrador.userIsInAdminGroup(usuario)
                                                && !previousValue.equals(wfReq.getPersonBean().getIdPerson())) {
                                            previousValue = wfReq.getPersonBean().getIdPerson();
                                    %>
                                    <tr class='fondo_header'>
                                    <td colspan="4">
                                        <span class="txt_b">
                                            &nbsp;
                                            <b><%= wfReq.getPersonBean().getApellido() + ", " + wfReq.getPersonBean().getNombre() %></b>
                                            &nbsp;
                                            <a href="javascript: sendMassiveMail('<%= confirmJSMsg %>');">
                                                <img border="0" src="icons/email.png" title="<%= rb.getString("sendEmail.htmlTitle") %>"/>
                                            </a>
                                        </span>
                                    </td>
                                    </tr>
                                    <%
                                        }
                                    %>
                                    <tr class='fondo_<%=cont==0?cont++:cont--%>'>
                                        <td width="7%">
                                            <img src="<%=wfReq.getToImpresion()==1?"icons/printer.png":"img/pendings.gif"%>">
                                            <%=wfReq.getIdVersion()%>
                                        </td>
                                        <td width="60%">
                                            <a href="javascript:showWF('<%=wfReq.getIdWorkFlow()%>','<%=wfReq.getRow()%>',<%=wfReq.isOwner()%>,<%=wfReq.isFlexFlow()%>)" target="info" class="ahref_b">
                                                <%=wfReq.getNameDocument()+" " +wfReq.getPrefix()+wfReq.getNumber()%>
                                            </a>
                                        </td>
                                        <td class="txt_b" width="13%">
                                        <%--ydavila Ticket 001-00-003023--%>
                                        <%--<%=rb.getString("wf.type"+wfReq.getTypeWF())%>--%>
                                            <%=rb.getString("wf.type"+wfReq.getTypeWF())%> <%=wfReq.getsubTypeWFdesc()%>
                                            
                                        </td>
                                        <td class="txt_b" width="20%" align="center">
                                            <%=wfReq.getDateCompleted()%>
                                        </td>
                                    </tr>
                                </logic:iterate>
                            </logic:present>
                        </table>
                    </td>
                </tr>
            </table>
	</td>
  </tr>
 </table>
</body>
</html>
