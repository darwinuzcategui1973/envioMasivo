<jsp:include page="richeditDocType.jsp" /> 
<!--
 * Title: editFlexFlow.jsp <br/>
 * Copyright: (c) 2004 Desige Servicios de Computación<br/>
 * @author Ing. Simón Rodriguez.
 * @author Ing. Nelson Crespo.
 * @version WebDocuments v1.0
 * <br/>
 *       Changes:<br/>
 *           17/01/2006 (NC) Creado 
 * </ul>
-->
<%@ page import="java.util.ResourceBundle,
				 com.desige.webDocuments.utils.beans.Users,
                 com.desige.webDocuments.utils.ToolsHTML,
                 com.desige.webDocuments.utils.beans.SuperActionForm,
                 com.desige.webDocuments.persistent.managers.HandlerWorkFlows,
                 com.desige.webDocuments.persistent.managers.HandlerBD,
                 com.desige.webDocuments.persistent.managers.HandlerDBUser,
                 com.desige.webDocuments.persistent.managers.HandlerParameters,
                 com.focus.jndi.ldapfastbind,
                 com.desige.webDocuments.utils.Constants"%>
<jsp:directive.page import="com.desige.webDocuments.seguridad.forms.PermissionUserForm"/>

<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<%@ page language="java" %>
<%
	Users users = (Users)session.getAttribute("user");
//ydavila Elmor - Verificar si check de autenticación para responder flujos está prendido o apagado
	String autenticar = String.valueOf(HandlerParameters.PARAMETROS.getAutenticarflujo());
    //out.println("autenticar = " + autenticar);
    
    // ydavila Elmor - Verificar si debo validar contra LDAP (no hay check, así que valido si el campo LDAPURL tiene data   
    String ldapurl = HandlerParameters.getLDAPurl();
    //out.println("ldapurl = " + ldapurl);
    
    String clave=HandlerDBUser.getClaveUser(users);
    //out.println("clave = " + clave);
    boolean changeUsr = false; // permite cambiar a los firmantes

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
    
    boolean reasigneUserFTP = String.valueOf(HandlerParameters.PARAMETROS.getReasigneUserFTP()).equals("1");
	//Si se toma en cuenta permiso por usuario se descomentan estas lineas
    //PermissionUserForm permission = (PermissionUserForm)session.getAttribute("permission");
    //if (reasigneUserFTP && (permission!=null && permission.getToReasigneFlow() == Constants.permission)) 
    if (reasigneUserFTP){
    	pageContext.setAttribute("reasigneUser","1");
    }else {
    	pageContext.setAttribute("reasigneUser","0");
    }	
    String origen =	(String)request.getAttribute("origen");
	if(ToolsHTML.isEmptyOrNull(origen)) origen = "";
%>
<html>
<head>
<title><%=rb.getString("principal.title")%></title>
<jsp:include page="meta.jsp" /> 
<script language=javascript src="./estilo/funciones.js"></script>
<link href="estilo/estilo.css" rel="stylesheet" type="text/css">
<jsp:include page="richeditHead.jsp" /> 

<style>
.clsMessageView
{
  background: white;
  color: black;
  border: 1px solid #FFFFFF;
  padding: 2;
  font-size: 12px;
  font-family: Geneva, Verdana, Arial, Helvetica;
  font-weight: normal;
  overflow: scroll;
}
</style>
<script type="text/javascript">
	window.enviarFormulario = true;
	
    function botones(forma) {
        if (forma.btnOK) {
            forma.btnOK.disabled = true;
        }
        if (forma.btnCancel) {
            forma.btnCancel.disabled = true;
        }
        if (forma.btnRejectedAndReassign) {
            forma.btnRejectedAndReassign.disabled = true;
        }
        if (forma.btnRejectAndCreate) {
            forma.btnRejectAndCreate.disabled = true;
        }
    }
    <%-- Luis Cisneros
    09-apr-07
        Permite regresar a la página princial si no se desea trabajar con el flujo en este momento
            --%>
    function regresar(form) {
		top.frames['info'].location.href = "loadAllWF.do?goTo=reload";
		//location.href = "loadAllWF.do?goTo=reload";
    }

    function ocultarBotones(){
    	forma = document.responseWF;
    	for(var k=0; k<forma.elements.length;k++) {
    		if (forma.elements[k].type == "button") {
    			forma.elements[k].style.display = "none";
    		}
    	}
    }
    function habilitarBotones(){
    	forma = document.responseWF;
    	for(var k=0; k<forma.elements.length;k++) {
    		if (forma.elements[k].type == "button") {
    			forma.elements[k].style.display = "";
    		}
    	}
    }

    function rechazar(forma,result,statu){
        updateRTEs();
        forma.commentsUser.value = forma.richedit.value;
        var texto = forma.commentsUser.value.replace(/&nbsp;/g,"").replace(/<br>/g,"").replace(/^\s*|\s*$/g,"");
    	salvar(forma,result,statu,(texto.length == 0?"<%=rb.getString("wf.viewDocumentReject")%>":""));
    }

    <%-- Fin 09-april-07 --%>
    function salvar(forma,result,statu,rechazarDocumento){
        forma.result.value =  result;
        forma.statu.value = statu;
        updateRTEs();
        if ( rechazarDocumento=="" ) {
	        forma.commentsUser.value = forma.richedit.value;
	    } else {
	        forma.commentsUser.value = rechazarDocumento;
	    }
        if (result=='<%=HandlerWorkFlows.canceled%>') {
            if (forma.commentsUser.value.length == 0) {
                <logic:equal value="1" name="responseWF" property="statu">
                       alert('<%=rb.getString("err.causeCanceledWF")%>');
                </logic:equal>
                <logic:notEqual value="1" name="responseWF" property="statu">
                    <logic:equal value="true" name="toCanceled">
                       alert('<%=rb.getString("err.closeWFUser")%>');
                    </logic:equal>
                </logic:notEqual>
                return false;
            }
        }
        var texto = forma.commentsUser.value.replace(/&nbsp;/g,"").replace(/<br>/g,"").replace(/^\s*|\s*$/g,"");
        <%-- ydavila Ticket 001-00-003023 --%>
        if (texto.length == 0) {
            alert('<%=rb.getString("err.invalidComment")%>');
            return false;
        }
        <%-- ydavila Ticket Elmor - Verificar si valido clave de QWEB o de LDAP  --%>
        if ('<%=String.valueOf(HandlerParameters.PARAMETROS.getAutenticarflujo())%>'=="1") {
            if (document.getElementById("pass").value==""){
               alert('<%=rb.getString("notPass")%>'); return false;
            }else{
	        	validarClave(document.getElementById("pass").value,forma,true);
            }
        } else {
			botones(forma);
			forma.submit();
		}
    }
    
    var isUsuarioValido = "false";
    function validarClave(pass,forma, isReload) {
    	window.enviarFormulario = isReload;
    	isUsuarioValido = "false";
		if(navigator.appName == "Microsoft Internet Explorer") {
		  http = new ActiveXObject("Microsoft.XMLHTTP");
		} else {
		  http = new XMLHttpRequest();
		}
		http.open("POST", "validateUserAjax.do?token="+pass);
		http.onreadystatechange=function() {
		  if(http.readyState == 4) {
			isUsuarioValido = http.responseText;
			if(isUsuarioValido=='true') {
				if(window.enviarFormulario==true) {
					botones(forma);
					forma.submit();
				}
			} else {
                 alert('<%=rb.getString("badPass")%>');
                 return false;
			}
		  }
		}
		http.send(null);
    }
    

    function salvarReasignar(forma,result,statu,rechazarDocumento){
        forma.result.value =  result;
        forma.statu.value = statu;
        updateRTEs();
        if ( rechazarDocumento=="" ) {
	        forma.commentsUser.value = forma.richedit.value;
	    } else {
	        forma.commentsUser.value = rechazarDocumento;
	    }
        if (result=='<%=HandlerWorkFlows.canceled%>') {
            if (forma.commentsUser.value.length == 0) {
                <logic:equal value="1" name="responseWF" property="statu">
                       alert('<%=rb.getString("err.causeCanceledWF")%>');
                </logic:equal>
                <logic:notEqual value="1" name="responseWF" property="statu">
                    <logic:equal value="true" name="toCanceled">
                       alert('<%=rb.getString("err.closeWFUser")%>');
                    </logic:equal>
                </logic:notEqual>
                return false;
            }
        }
        <%-- ydavila Ticket 001-00-003023 --%>
        var texto = forma.commentsUser.value.replace(/&nbsp;/g,"").replace(/<br>/g,"").replace(/^\s*|\s*$/g,"");
        if (texto.length == 0) {
            alert('<%=rb.getString("err.invalidComment")%>');
            return false;
        }
                <%-- ydavila Ticket Elmor - Verificar si valido clave de QWEB o de LDAP  --%>
        if ('<%=String.valueOf(HandlerParameters.PARAMETROS.getAutenticarflujo())%>'=="1") {
            if (document.getElementById("pass").value==""){
               alert('<%=rb.getString("notPass")%>'); return false;
            }else{
	        	validarClave(document.getElementById("pass").value,forma,false);
            }
        } else {
			botones(forma);
			forma.submit();
		}
        
        <logic:equal value="1" name="reasigneUser">
        if (result=='<%=HandlerWorkFlows.rechazado%>') {         
        	var idDocument = forma.idDocument.value;
        	var idWorkFlow = forma.idWorkFlow.value;
        	var idFlexFlow = forma.idFlexFlow.value;
        	var numVersion = forma.numVersion.value;
        	var msg = forma.commentsUser.value;
        	var row = forma.row.value;
        	var idMovement = forma.idMovement.value;
	        abrirVentana("showUsersFlexFlow.do?idDocument="+idDocument+"&idWorkFlow="+idWorkFlow+"&idFlexFlow="+idFlexFlow+"&numVersion="+numVersion+"&result="+result+"&statu="+statu+"&row="+row+"&idMovement="+idMovement+"&comments="+msg,700,550,"ReiniciarFTP");
        }else{
        	forma.submit();
        }
        </logic:equal>
		
        <logic:equal value="0" name="reasigneUser">
        forma.submit();
        </logic:equal>
    }
                
    function canceledWF(forma,result,statu) {
        forma.result.value =  result;
        forma.statu.value = statu;
        <%if (request.getAttribute("toCompleted")!=null && String.valueOf(request.getAttribute("toCompleted")).equals("true") ) {%>
       	forma.action = "completedWF.do";
        <%} else {%>
      	forma.action = "closeWF.do";
        <%}%>
        updateRTEs();
        forma.commentsUser.value = forma.richedit.value;
        if (result=='<%=HandlerWorkFlows.canceled%>') {
			//            if (forma.commentsUser.value.length == 0) {
        	<%-- ydavila Ticket 001-00-003023--%>         
             if ((forma.commentsUser.value.length == 0) || (stringVacio(forma.commentsUser.value))) {
	        	<%if (request.getAttribute("toCompleted")!=null && String.valueOf(request.getAttribute("toCompleted")).equals("true") ) {%>
            	 alert('<%=rb.getString("err.causeCompletedWF")%>');
    		    <%} else {%>
    	         alert('<%=rb.getString("err.causeCanceledWF")%>');
	        	<%}%>
                return false;
            }
	        <%-- ydavila Ticket Elmor - Verificar si valido clave de QWEB o de LDAP  --%>
	        if ('<%=String.valueOf(HandlerParameters.PARAMETROS.getAutenticarflujo())%>'=="1") {
	            if (document.getElementById("pass").value==""){
	               alert('<%=rb.getString("notPass")%>');
	            }else{
		        	validarClave(document.getElementById("pass").value,forma,true);
	            }
	        } else {
				botones(forma);
				forma.submit();
			}
		}
    }
    
    function editField(pages,input,value,forma) {  
        <%-- ydavila Ticket 001-00-003023 --%> 
   <%-- ydavila Ticket Elmor - Verificar si valido clave de QWEB o de LDAP  --%>
        if ('<%=String.valueOf(HandlerParameters.PARAMETROS.getAutenticarflujo())%>'=="1") {
            if (document.getElementById("pass").value==""){
               alert('<%=rb.getString("notPass")%>'); return false;
            }else{
	        	validarClave(document.getElementById("pass").value,forma,false);
            }
        } else {
    		ocultarBotones(forma)    
		}
        
        var hWnd = null;
        hWnd = window.open(pages+"?input="+input+"&value="+value+"&nameForma="+forma.name+"&idDocument="+forma.idDocument.value+"&idFlexFlow="+forma.idFlexFlow.value,"CatalogoRegistro","width=600,height=350,resizable=yes,scrollbars=yes,left=212,top=250");
        if ((document.window != null) && (!hWnd.opener)){
            hWnd.opener = document.window;
        }
    }
    
<%--    function cancelar() {--%>
<%--        form = document.getElementById("Selection");;--%>
<%--        form.action="loadStructMain.do";--%>
<%--        form.cmd.value="<%=SuperActionForm.cmdLoad%>";--%>
<%--        form.submit();--%>
<%--    }--%>
<%--    function edit(){--%>
<%--        document.showDocument.submit();--%>
<%--    }--%>
<%--    function updateCheck(check,field) {--%>
<%--        if (check.checked){--%>
<%--            field.value = "0";--%>
<%--        } else{--%>
<%--            field.value = 1;--%>
<%--        }--%>
<%--    }--%>
<%--    function createWF(item) {--%>
<%--        document.newWF.typeWF.value = item;--%>
<%--        document.newWF.target = "_parent";--%>
<%--        document.newWF.submit();--%>
<%--    }--%>
    function showDocument(idDocument,idVersion) {
    	if(idVersion=='0') {
	        alert('<%=rb.getString("err.beforeVeboseNotFound")%>');
    		return;
    	}
        var hWnd = null;
        hWnd = window.open("loadDataDoc.do?idDocument="+idDocument+"&idVersion="+idVersion,"","width=800,height=600,resizable=yes,scrollbars=yes,left=100,top=100");
        if ((document.window != null) && (!hWnd.opener)) {
            hWnd.opener = document.window;
        }
    }
    function editRegister(idDocument,idVersion,nameFile) {
        var hWnd = null;
        hWnd = window.open("editRegister.jsp?closeWindow=true&idDocument="+idDocument+"&nameFile="+escape(nameFile)+"&idVersion="+idVersion,"","width=800,height=600,resizable=yes,scrollbars=yes,left=100,top=100");
        if ((document.window != null) && (!hWnd.opener)) {
            hWnd.opener = document.window;
        }
    }
    
    
	var http = false;
    
    function abrir(idDocument,idVersion) {
    	respuesta = "false";
		if(navigator.appName == "Microsoft Internet Explorer") {
		  http = new ActiveXObject("Microsoft.XMLHTTP");
		} else {
		  http = new XMLHttpRequest();
		}
		http.open("POST", "markCheckOutAjax.do?registro=true&idDocument="+idDocument+"&idVersion="+idVersion+"&server="+getServerLocation());
		http.onreadystatechange=function() {
		  if(http.readyState == 4) {
		  		document.location.href=getPathFromLocation()+"editorPorInternet/qwds4Registro<%= users.getIdPerson()%>.jnlp";
				setTimeout("eliminar()",60000);
		  }
		}
		http.send(null);
		//pause(2000);
		//obj.href="editorPorInternet/qwds4Registro<%= users.getIdPerson()%>.jnlp";
		//obj.style.display="none";
		//setTimeout("eliminar()",60000);
    }
    
    function eliminar() {
		if(navigator.appName == "Microsoft Internet Explorer") {
		  http = new ActiveXObject("Microsoft.XMLHTTP");
		} else {
		  http = new XMLHttpRequest();
		}
		http.open("POST", "markCheckOutAjax.do?eliminar=true&registro=true");
		http.onreadystatechange=function() {
		  if(http.readyState == 4) {
		  	//editar(http.responseText);
		  }
		};
		http.send(null);
    }
    

	function getPathFromLocation() {
		var cad = document.location.href;
		cad = cad.replace("http://","");
		var arr = cad.split("/");
		return "http://"+arr[0]+"/"+arr[1]+"/";
	}

	function getServerLocation() {
		var cad = document.location.href;
		cad = cad.replace("http://","");
		var arr="";
		if(cad.indexOf(":")!=-1) {
			arr = cad.split(":");
		} else {
			var arr = cad.split("/");
		}
		return arr[0];
	}
        
    function showDataDocument(id) {
    	var formaSelection = document.getElementById("Selection");
    	formaSelection.target="info";
        formaSelection.idDocument.value = id;
        formaSelection.submit();
    }
    function showCharge(userName,charge) {
	    charge = charge.replace(/\[/gi,"-").replace(/\]/gi,"-");
        window.open("showCharge.jsp?userName="+userName+"&charge="+charge, "WebDocuments", "resizable=yes,scrollbars=yes,statusbar=yes,width=300,height=200,left=410,top=250");
    }
    function toggleDiv(gn)  {
    	document.getElementById(gn).style.display = ( document.getElementById(gn).style.display == "" ? "none" : "" );
    }
    
    function loadActUsers(number,input,nameForm,value,type,idWorkFlow) {
        abrirVentana("loadActUsers.do?number="+number+"&input="+input+"&nameForma="+nameForm+"&value="+value+"&type="+type+"&idWorkFlow="+idWorkFlow+"&cambiarFirmante=true",600,400)
    }    
    
    function regresarFlow(){
     <%if("inboxWFs".equals(origen)){%>
    	//info.location.href = "loadInboxWF.do";
    	window.open("loadInboxWF.do","info");
     <%}else if ("principal".equals(origen)){%>
    	//location.href = "principal.jsp";
    	window.open("principal.jsp","info");
     <%}else if ("showDataDocument".equals(origen)){%>
    	history.back();
     <%}%>
    }
</script>
</head>

<body class="bodyInternas" <%=onLoad%>>

<logic:present name="permission" scope="session">
    <logic:equal name="permission" property="toChangeUsr" value="1">
        <%
            changeUsr = true;
        %>
    </logic:equal>
</logic:present>
<logic:present name="showDataWF" scope="session">
    <bean:define id ="doc" name="showDataWF" type="com.desige.webDocuments.workFlows.forms.DataWorkFlowForm" scope="session"/>
    <!-- Formulario para Obtener el nodo Seleccionado por el usuario -->
        <form name="Selection" id="Selection" action="showDataDocument.do" style="margin:0;padding:0;">
            <input type="hidden" name="idDocument" value=""/>
            <input type="hidden" name="idWF" value="<%=doc.getIdWorkFlow()%>"/>
            <input type="hidden" name="from" value="loadWF.do?idWorkFlow=<%=doc.getIdWorkFlow()%>&row=<%=doc.getIdMovement()%>&owner=false&isFlexFlow=true"/>
            <input type="hidden" name="isFlexFlow" value="true"/>
            <input type="hidden" name="toCompleted" value="<%= request.getAttribute("toCompleted") %>"/>
        </form>
    <!-- END Form -->
    <table align=center border=0 width="100%">
        <tr>
            <td valign="top" colspan="4">
                <table class="clsTableTitle" width="100%" cellSpacing=0 cellPadding=2 align=center border=0>
                    <tbody>
                        <tr>
                            <td class="td_title_bc" height="21"><a name="inicio">&nbsp;</inicio>
                               		<%=rb.getString("wf.titleFlexFlow") + " " + doc.getNameWF() + " " + rb.getString("wf.request")%>
<%--                                <%=rb.getString("wf.titleWF"+doc.getTypeWF())%> <%=doc.getEliminar()!=0?rb.getString("wf.delete"):""%>--%>
                            </td>
                        </tr>
                    </tbody>
                </table>
            </td>
        </tr>
        <tr>
            <td width="16%" class="td_orange_l_b" height="26" style="background: url(img/btn160.png); background-repeat: no-repeat" height="20 px"  valign="middle">
                <%=rb.getString("doc.title")%>
            </td>
            <td class="td_gris_l" width="*" colspan="3">
                <bean:write name="showDataWF" property="nameDocument"/> &nbsp; <%=doc.getPrefix()+doc.getNumber()%>
	           	<%if(!String.valueOf(users.getIdGroup()).equals(com.desige.webDocuments.utils.Constants.ID_GROUP_VIEWER)){%>
                <a href="javascript:showDataDocument('<%=doc.getNumDocument()%>')" class="ahref_b">
                    [<%=rb.getString("wf.details")%>]
                </a>
                <%}%>
                <a href="javascript:showDocument(<%=doc.getNumDocument()%>,'<%=doc.getVersion()%>')" class="ahref_b">
                    [<%=rb.getString("wf.viewDocAnt")%>]
                </a>
            </td>
        </tr>
        <tr>
             <td class="td_orange_l_b" height="26" style="background: url(img/btn160.png); background-repeat: no-repeat" height="20 px" valign="middle">
                    <%=rb.getString("doc.Ver")%>
             </td>
             <td class="td_gris_l" colspan="3">
                   <bean:write name="showDataWF" property="idVersion"/>
             </td>
        </tr>



        <tr>
            <td width="16%" height="26" class="td_orange_l_b" style="background: url(img/btn160.png); background-repeat: no-repeat" height="20 px" valign="middle">
                <%=rb.getString("wf.request")%>
            </td>
            <td width="34%" class="td_gris_l">
<%--                <bean:write name="showDataWF" property="request"/>--%>
                <logic:present name="showCharge" scope="session">
                    <a href="javascript:showCharge('<bean:write name="showDataWF" property="request"/>','<bean:write name="showDataWF" property="charge"/>')" class="ahref_b">
                        <bean:write name="showDataWF" property="charge" />
                    </a>
                </logic:present>
                <logic:notPresent name="showCharge" scope="session">
                    <a href="javascript:showCharge('<bean:write name="showDataWF" property="request"/>','<bean:write name="showDataWF" property="charge"/>')" class="ahref_b">
                        <bean:write name="showDataWF" property="request"/>
                    </a>
                </logic:notPresent>
            </td>
            <td width="16%" class="td_orange_l_b" width="20%" style="background: url(img/btn160.png); background-repeat: no-repeat" height="20 px" valign="middle">
                <%=rb.getString("wf.dateExpire")%>
            </td>
            <td width="*" class="td_gris_l">
                <bean:write name="showDataWF" property="dateExpire"/>
            </td>
        </tr>

        <tr>
            <td class="td_orange_l_b" height="26" style="background: url(img/btn160.png); background-repeat: no-repeat" height="20 px" valign="middle">
                <%=rb.getString("wf.dateBegin")%>
            </td>
            <td class="td_gris_l">
                <bean:write name="showDataWF" property="dateBegin"/>
            </td>
            <td class="td_orange_l_b" style="background: url(img/btn160.png); background-repeat: no-repeat" height="20 px" valign="middle">
                <%=rb.getString("wf.dateEnd")%>
            </td>
            <td class="td_gris_l">
                <%=doc.getDateEnd()!=null?doc.getDateEnd():rb.getString("wf.inProgress")%>
            </td>
        </tr>

        <tr>
            <%=ToolsHTML.dibujarCheck("td_orange_l_b",rb.getString("cbs.sequential"),"td_gris_l",doc.getSequential(),0,null)%>
            <%=ToolsHTML.dibujarCheck("td_orange_l_b",rb.getString("wf.conditional"),"td_gris_l",doc.getConditional(),0,null)%>
        </tr>

        <tr>
            <td class="td_orange_l_b" height="26" style="background: url(img/btn160.png); background-repeat: no-repeat" height="20 px" valign="middle">
                <%=rb.getString("wf.statu")%>
            </td>
            <td class="td_gris_l">
                <%=rb.getString("wf.wfStatu"+doc.getStatu())%>
            </td>
            <td class="td_orange_l_b" style="background: url(img/btn160.png); background-repeat: no-repeat" height="20 px" valign="middle">
                <%=rb.getString("wf.result")%>
            </td>
            <td class="td_gris_l">
                <%=rb.getString("wf.result"+doc.getResult())%>
            </td>
        </tr>
        <tr>
            <td colspan="4">
                &nbsp;
            </td>
        </tr>
        <tr>
<%-- ydavila Elmor --%>  
<% if ("1".equals(String.valueOf(HandlerParameters.PARAMETROS.getAutenticarflujo()))) { 
	boolean mostrarLogin = false;%>
	<logic:equal value="1" name="responseWF" property="statu">
		<%mostrarLogin=true;%>
	</logic:equal>     
	<logic:notEqual value="1" name="responseWF" property="statu">
		<logic:equal value="true" name="toCanceled">
			<%mostrarLogin=true;%>
		</logic:equal>
	</logic:notEqual>		
	<%if(mostrarLogin) {%>
		<tr> 
		 <td class="td_orange_l_b" style="background: url(img/btn160.png); background-repeat: no-repeat" width:"50 px"  height="30 px" valign="middle">
		   <%=rb.getString("firmausuario")%>
		 </td>
		 <td class="td_orange_l_b" style="background: url(img/btn160.png); background-repeat: no-repeat" width:"50 px"  height="30 px" valign="middle">
		  <bean:write name="user" property="user"/>
		 </td>
		</tr> 
		<tr> 
		 <td class="td_orange_l_b" style="background: url(img/btn160.png); background-repeat: no-repeat" height="30 px" valign="middle">
		   <%=rb.getString("firmaclave")%>
		 </td>
		 <td>
		   <input type="password" name="clave" property="clave" id="pass" style="width:245px; height: "30 px" autocomplete="off" value="" "/>
		 <%--   <%=HandlerDBUser.getClaveUSer(user) %><br/>--%> 
		 </td>
		</tr>
	<%}
}%>                     
<%-- ydavila Elmor --%> 
            <td colspan="4" align="left" class="txt_title">
                <span style="cursor: hand;" onclick='toggleDiv("comments"); if(comments.style.display == "none"){ commentsImg.src="menu-images/mas.gif";} else { commentsImg.src="menu-images/menos.gif";}'>
                    <img id='commentsImg' src='menu-images/mas.gif' />
                    <%=rb.getString("wf.requestDetail")%>
                </span>
                <logic:present name="showEditReg" scope="session" >
                  <logic:present name="usersWFOwner" scope="session" >
                     <logic:iterate id="particiOwner" name="usersWFOwner" type="com.desige.webDocuments.workFlows.forms.ParticipationForm" scope="session">
                       <logic:equal value="1" property="statu" name="particiOwner">
                        &nbsp;
                         <bean:write name="showDataWF" property="request"/>
                                <a class="ahref_b" href="javascript:<%=(!ToolsHTML.abrirEditorWebStart((HttpServletRequest)request)?"editRegister":"abrir")%>('<%=doc.getNumDocument()%>','<%=doc.getNumVersion()%>','<%=doc.getNameFile()%>')">
                                    <%=rb.getString("wf.editRegister")%>
                                </a>
                       </logic:equal>          
                     </logic:iterate>
                   </logic:present>
                </logic:present>
            </td>
        </tr>
       <tr>
            <td colspan="4" align="left" style="padding: 0px" class="td_gris_l">
                <logic:present name="documentOfRejection" scope="session" >
                	<% int cont=0; %>
                    <logic:iterate id="rejection" name="documentOfRejection" type="com.desige.webDocuments.utils.beans.DocOfRejection" scope="session">
		            	<a href="javascript:showDocumentimprimir('<%=rejection.getNumgen()%>','<%=rejection.getNumver()%>','<%=rejection.getNumgen()%>','0','Qweb',<%=Constants.PRINTER_PDF%>);" class="ahref_b"><%=rb.getString("wf.viewDocumentRejection")%> <%= (++cont==1?"":cont) %></a><br> 
                    </logic:iterate>
                </logic:present>
            </td>
       </tr>
       <tr>
            <td colspan="4" align="left" style="padding: 0px" class="td_gris_l">
                <div align="left" class="clsMessageView" id='comments' style="width: 100%; height: 160px">
                    <%=doc.getComments()%>
                </div>
                <script language="javascript">
                    <% if (doc.getComments()!=null) { %>
                        document.getElementById("comments").style.display = "none";
                    <% } %>
                </script>
            </td>
       </tr>

        <tr>
            <td colspan="4">&nbsp;
                <%
                    //System.out.println("[II]");
                %>
            </td>
        </tr>

        <tr>
            <td valign="top" colspan="4" >
                <table class="clsTableTitle" width="100%" cellSpacing="0" cellPadding="2" align="center" border="0">
                    <tr align="center">
                        <td width="35%">
                                <%=rb.getString("wf.participantion")%> 
                        </td>
                        <td width="15%">
                                <%=rb.getString("subAct.titleI")%>
                        </td>
                        <td width="15%">
                                <%=rb.getString("cbs.statu")%>
                        </td>
                        <td width="15%">
                                <%=rb.getString("wf.result")%>
                        </td>
                        <td width="20%">
                                <%=rb.getString("wf.dateDoc")%>
                        </td>
                    </tr>
                </table>
                <%//System.out.println("[III]");%>
                <logic:present name="usersWF" scope="session" >
                    <%//System.out.println("[III.I]");%>
                    <table cellSpacing=0 cellPadding=0 align=center border=0 width="100%">
                        <logic:iterate id="participation" name="usersWF" type="com.desige.webDocuments.workFlows.forms.ParticipationForm" scope="session">
                            <tr>
                                <td class="td_gris_l" width="35%">
                                    <logic:present name="showCharge" scope="session">
                                        <%//System.out.println("[III.II]");%>
                                        <a href="javascript:showCharge('<%=participation.getNameUser()%>','<%=participation.getCharge()%>')" class="ahref_b">
                                            <bean:write name="participation" property="charge" />
                                        </a>
                                         <logic:equal value="1" property="statu" name="participation">
                                              <logic:present name="showEditReg" scope="session" >
                                               &nbsp;
                                               <a class="ahref_b" href="javascript:<%=(!ToolsHTML.abrirEditorWebStart((HttpServletRequest)request)?"editRegister":"abrir")%>('<%=doc.getNumDocument()%>','<%=doc.getNumVersion()%>','<%=doc.getNameFile()%>')">
                                                  <%=rb.getString("wf.editRegister")%>
                                               </a>
                                              </logic:present>
                                          </logic:equal>
                                    </logic:present>
                                    <logic:notPresent name="showCharge" scope="session">
                                        <%//System.out.println("[III.III]");%>
                                        <a href="javascript:showCharge('<%=participation.getNameUser()%>','<%=participation.getCharge()%>')" class="ahref_b">
                                            <bean:write name="participation" property="nameUser" />
                                        </a>
                                         <logic:equal value="1" property="statu" name="participation">
                                              <logic:present name="showEditReg" scope="session" >
                                               &nbsp;
                                               <a class="ahref_b" href="javascript:<%=(!ToolsHTML.abrirEditorWebStart((HttpServletRequest)request)?"editRegister":"abrir")%>('<%=doc.getNumDocument()%>','<%=doc.getNumVersion()%>','<%=doc.getNameFile()%>')">
                                                  <%=rb.getString("wf.editRegister")%>
                                               </a>
                                              </logic:present>
                                          </logic:equal>
                                    </logic:notPresent>
                                </td>
                                <td class="td_gris_l" width="15%">
                                	<% if ( changeUsr && (String.valueOf(participation.getStatu()).equals(HandlerWorkFlows.wfuPending) || String.valueOf(participation.getStatu()).equals(HandlerWorkFlows.wfuQueued)) ) {%>
                                		<logic:equal name="participante" value="true">
		                                	<logic:notPresent name="documentOfRejection" scope="session" >
		                                		<%if(!participation.getActivity().toLowerCase().equals("reiniciar")){%>
	    	                                		<a href="javascript:loadActUsers('<%=participation.getNumAct()%>','exec','subActivity','executant',0,<%=participation.getIdWorkFlow()%>);"> <%=participation.getActivity()%></a>
	    	                                	<%} else {%>
			                                    	<%=participation.getActivity()%>
	    	                                	<%}%>
	    	                                </logic:notPresent>
		                                	<logic:present name="documentOfRejection" scope="session" >
		                                    	<%=participation.getActivity()%>
		                                	</logic:present>
                                    	</logic:equal>
                                		<logic:notEqual name="participante" value="true">
	                                    	<%=participation.getActivity()%>
                                		</logic:notEqual>
                                    <%} else {%>
                                    	<%=participation.getActivity()%>
                                    <%}%>
                                </td>

                                <%//System.out.println("[III] " + participation.getStatu());%> 
                                <%//System.out.println("[III] " + participation.getResult());%>
                                <td class="td_gris_l" width="15%">
                                    <%=rb.getString("wf.statuUserWF"+participation.getStatu())%>
                                    <%if (String.valueOf(participation.getStatu()).equals(HandlerWorkFlows.wfuReplaced)) { %>
                                    	<a href="javascript:alert('<%=participation.getCommentsUser()%>');">?</a>
                                    <%}%>
                                </td>
                                <td class="td_gris_l" width="15%">
                                    <%=rb.getString("wf.resultUserWF"+participation.getResult())%>
                                </td>
                                <td class="td_gris_l" width="20%">
                                    <bean:write name="participation" property="dateReply" />
                                </td>
                            </tr>
                        </logic:iterate>
                    </table>
                </logic:present>
                <%//System.out.println("[IV]");%>
                <logic:present name="comments" scope="session" >
                    <logic:iterate id="replies" name="comments" type="com.desige.webDocuments.utils.beans.Search3" scope="session">
                        <tr>
                            <td colspan="4">
                                &nbsp;
                            </td>
                        </tr>
                        <tr>
                            <td colspan="4" align="left" class="txt_title">
                                <span style="cursor: hand;" onclick='toggleDiv("<%=replies.getId()%>"); if(<%=replies.getId()%>.style.display == "none"){ <%=replies.getId()%>Img.src="menu-images/mas.gif";} else { <%=replies.getId()%>Img.src="menu-images/menos.gif";}'>
                                    <img id='<%=replies.getId()%>Img' src='menu-images/mas.gif' />
                                    <%=rb.getString("wf.result")%>: <%=replies.getName() + ": " + replies.getAditionalInfo()%>
                                </span>
                            </td>
                        </tr>
                        <tr>
                            <td colspan="4" align="left" style="padding: 0px" class="td_gris_l">
                                <div align="left" class="clsMessageView" id='<%=replies.getId()%>' style="width: 100%; height: 160px">
                                    <%=replies.getDescript()%>
                                </div>
                                <script language="javascript">
                                    <% if (replies.getDescript()!=null){ %>
				                        document.getElementById("<%=replies.getId()%>").style.display = "none";
                                    <%}%>
                                </script>
                            </td>
                       </tr>
                   </logic:iterate>
                </logic:present>
                    <logic:equal value="1" name="responseWF" property="statu">
                        <html:form action="/responseFlexFlow.do" >
                        <html:hidden property="result" />
                        <html:hidden property="row"/>
                        <html:hidden property="statu"/>
                        <html:hidden property="idMovement"/>
                        <html:hidden property="numVersion"/>
                        <html:hidden property="idDocument"/>
                        <html:hidden property="idWorkFlow"/>
                        <html:hidden property="idFlexFlow"/>
                        <html:hidden property="isFlexFlow" value="true"/>
                        <html:textarea property="commentsUser" style="display:none"/>
<%-- ydavila Ticket 001-00-003023 --%>
                 
<%-- ydavila Ticket 001-00-003023 --%>                         
                        <tr>
                            <td colspan="4" heigth="22" class="titleCenter" style="background: url(img/BgComments.gif); background-repeat: no-repeat" valign="middle">
                                <%= rb.getString("wf.responseTitle")%>
                            </td>
                        </tr>
                        <tr>
                            <td class="fondoEditor" colspan="4">
								  <jsp:include page="richedit.jsp">
									<jsp:param name="richedit" value="richedit"/>
									<jsp:param name="defaultValue" value="<%= String.valueOf(doc.getCommentsUsrs()) %>"/>
								  </jsp:include>
                            </td>
                        </tr>

                        <script language="JavaScript" event="onload" for="window">
                            <logic:present name="info" scope="session" >
                                alert('<%=session.getAttribute("info")%>');
                                <%session.removeAttribute("info");%>
                            </logic:present>
                            document.body.scrollTop = 0;
                        </script>

                        <tr>
                            <td colspan="4" align="center">
                                <logic:notPresent name="rejected" scope="session">
                                    <input type="button" class="boton" value="<%=rb.getString("btn.ok")%>" name="btnOK" onClick="javascript:salvar(this.form,'<%=HandlerWorkFlows.response%>','<%=HandlerWorkFlows.wfuAcepted%>','');"/>
                                    &nbsp;
    <%--                                <input type="button" class="boton" value="" name="btnCancel" onClick="javascript:salvar(this.form,'<%=HandlerWorkFlows.canceled%>','<%=HandlerWorkFlows.wfuCanceled%>','');">--%>
                                    <input type="button" class="boton" value="<%=rb.getString("btn.rejected")%>" name="btnCancel" onClick="javascript:salvar(this.form,'<%=HandlerWorkFlows.rechazado%>','<%=HandlerWorkFlows.wfuRejected%>','');" title="<%=rb.getString("alt.restartGenerator")%>"/>
                                        &nbsp;
                                    
									<logic:equal value="1" name="reasigneUser">
										<input type="button" class="boton" value="<%=rb.getString("btn.rejectedAndReassign")%>" name="btnRejectedAndReassign" onClick="javascript:salvarReasignar(this.form,'<%=HandlerWorkFlows.rechazado%>','<%=HandlerWorkFlows.wfuRejected%>','');"/>
	                                    &nbsp;
                                    </logic:equal>
									
                                    <logic:present name="rejectAndCreate" scope="session">
                                    <logic:equal value="1" name="rejectAndCreate">
	                                    <input type="button" class="boton" style="width:150px" value="<%=rb.getString("btn.rejectedAndCreate")%>" name="btnRejectAndCreate" onClick="javascript:editField('loadDocumentFormat.do','document','nameDocument',this.form);" />
    	                                    &nbsp;
                                    </logic:equal>
                                    </logic:present>
			       					<%if("inboxWFs".equals(origen) || "principal".equals(origen) || "showDataDocument".equals(origen)){%>	
	                                <input type="button" class="boton" value="<%=rb.getString("btn.back")%>" onClick="javascript:regresarFlow();" name="btnBack" />
	                                <%}else{%>
	                                <input type="button" class="boton" value="<%=rb.getString("btn.back")%>" onClick="javascript:regresar(this.form);" name="btnBack" />
		 							<%}%>      
	                                 <%--fin 09-apr-07 --%>
                                </logic:notPresent>
                                <logic:present name="rejected" scope="session">
                                	<logic:notPresent name="documentOfRejection" scope="session" >
                                	    <input type="button" class="boton" value="<%=rb.getString("btn.reInit")%>" name="btnOK" onClick="javascript:salvar(this.form,'<%=HandlerWorkFlows.wfuReInit%>','<%=HandlerWorkFlows.wfuReInit%>','');"/>
	                                    &nbsp;
                                	</logic:notPresent>
                                    <%--<input type="button" class="boton" value="" name="btnCancel" onClick="javascript:salvar(this.form,'<%=HandlerWorkFlows.rechazado%>','<%=HandlerWorkFlows.wfuRejected%>','');">--%>
                                    <input type="button" class="boton" value="<%=rb.getString("btn.close")%>" name="btnCancel" onClick="javascript:canceledWF(this.form,'<%=HandlerWorkFlows.canceled%>','<%=HandlerWorkFlows.wfuCanceled%>');"/>
                                  <%-- Luis Cisneros
                                        09-apr-07
                                        Permite regresar a la página princial si no se desea trabajar con el flujo en este momento
                                        O si proviene de lista de flujos pendientes, se regresa a ese modulo
									--%>
                                        &nbsp;
						         <%if("inboxWFs".equals(origen) || "principal".equals(origen) || "showDataDocument".equals(origen)){%>	
                                 <input type="button" class="boton" value="<%=rb.getString("btn.back")%>" onClick="javascript:regresarFlow();" name="btnBack" />
                                 <%}else{%>
                                 <input type="button" class="boton" value="<%=rb.getString("btn.back")%>" onClick="javascript:regresar(this.form);" name="btnBack" />
	 							 <%}%>      
                                 <%--fin 09-apr-07 --%>

                                </logic:present>
                            </td>
                        </tr>
                        </html:form>
                    </logic:equal>
                    <logic:notEqual value="1" name="responseWF" property="statu">
                        <logic:equal value="true" name="toCanceled">
                            <html:form action="/responseFlexFlow.do" >
                                <html:hidden property="result" />
                                <html:hidden property="row"/>
                                <html:hidden property="statu"/>
                                <html:hidden property="idMovement"/>
                                <html:hidden property="numVersion"/>
                                <html:hidden property="idDocument"/>
                                <html:hidden property="idWorkFlow"/>
                                <html:hidden property="idFlexFlow"/>
                                <html:hidden property="isFlexFlow" value="true"/>
                                <html:textarea property="commentsUser" style="display:none"/>
                                
                                <tr>
                                    <td colspan="4" heigth="22" class="titleCenter" style="background: url(img/BgComments.gif); background-repeat: no-repeat" valign="middle">
                                        <%=rb.getString("wf.completeTitle")%>
                                    </td>
                                </tr>
                                <tr>
                                    <td class="fondoEditor" colspan="4">
										  <jsp:include page="richedit.jsp">
											<jsp:param name="richedit" value="richedit"/>
											<jsp:param name="defaultValue" value="<%= String.valueOf(doc.getCommentsUsrs()) %>"/>
										  </jsp:include>
                                    </td>
                                </tr>
                                <script language="JavaScript" event="onload" for="window">
                                    <logic:present name="info" scope="session" >
                                        alert('<%=session.getAttribute("info")%>');
                                        <%session.removeAttribute("info");%>
                                    </logic:present>
                                </script>
                                <tr>
                                    <td colspan="4" align="center">
                                        <input type="button" class="boton" value="<%=rb.getString("btn.ok")%>" name="btnCancel" onClick="javascript:canceledWF(this.form,'<%=HandlerWorkFlows.canceled%>','<%=HandlerWorkFlows.wfuCanceled%>');"/>
                                        <%-- Luis Cisneros
                                        09-apr-07
                                        Permite regresar a la página princial si no se desea trabajar con el flujo en este momento
                                        O si proviene de lista de flujos pendientes, se regresa a ese modulo
                                        --%>
                                &nbsp;
                                &nbsp;
                                &nbsp;
                                &nbsp;
						        <%if("inboxWFs".equals(origen) || "principal".equals(origen) || "showDataDocument".equals(origen)){%>	
                                <input type="button" class="boton" value="<%=rb.getString("btn.back")%>" onClick="javascript:regresarFlow();" name="btnBack" />
                                <%}else{%>
                                <input type="button" class="boton" value="<%=rb.getString("btn.back")%>" onClick="javascript:regresar(this.form);" name="btnBack" />
								<%}%>                                
                                 <%--fin 09-apr-07 --%>
                                    </td>
                                </tr>
                                </html:form>
                        </logic:equal>
                    </logic:notEqual>

					<logic:notEqual value="1" name="responseWF" property="statu">
					<logic:notEqual value="true" name="toCanceled">
			        <%if("inboxWFs".equals(origen) || "principal".equals(origen) || "showDataDocument".equals(origen)){%>	
			        <tr>
			        	<td align="center" colspan="4">
					    	<input type="button" class="boton" value="<%=rb.getString("btn.back")%>" onClick="javascript:regresarFlow();" name="btnBack"/>
			            </td>
			        </tr>
					<%}%>
			        </logic:notEqual>
			        </logic:notEqual>
			        
                </table>
            <td>&nbsp;</td>
        </tr>
    </table>
</logic:present>
<logic:notPresent name="showDataWF">
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
