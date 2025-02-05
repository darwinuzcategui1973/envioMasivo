<jsp:include page="richeditDocType.jsp" /> 
<!--
 * Title: editWorkFlow.jsp <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * @author Ing. Simón Rodriguez.
 * @author Ing. Nelson Crespo.
 * @version WebDocuments v3.0
 * <br/>
 *       Changes:<br/> 
 *           se le agrego logic:equal value="0" name="showDataWF" property="typeWF" para validar si esta De Revision
 *           o si esta Aprobado
 *           14/05/2005 (NC) Cambios en manejo de Registros
 *           11/07/2005 (SR) Se cambio el movimiento de registros para que lo vea solo los suarios
 *                           que tienen pendiente el flujo.
 *           19/06/2005 (SR) Se valida por la session particiOwner
 *           03/08/2005 (SR) Se muestra la version del documento
 *      <li> 25/04/2006 (SR) Se sustituyo /WebDocuments/.. por ./..
 * </ul>
-->
<%@ page import="java.util.ResourceBundle,
                 com.desige.webDocuments.utils.ToolsHTML,
				 com.desige.webDocuments.utils.beans.Users,
                 com.desige.webDocuments.utils.beans.SuperActionForm,
                 com.desige.webDocuments.persistent.managers.HandlerDBUser,
                 com.desige.webDocuments.persistent.managers.HandlerParameters,
                 com.focus.jndi.ldapfastbind,
                 com.desige.webDocuments.persistent.managers.HandlerWorkFlows"%>

<%@ page import="com.desige.webDocuments.workFlows.forms.ParticipationForm" %>

<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<%@ page import="com.desige.webDocuments.persistent.managers.HandlerBD" %>
<%@ page language="java" %>
<%
	Users users = (Users)session.getAttribute("user");
    
    //ydavila Elmor - Verificar si check de autenticación para responder flujos está prendido o apagado
	String autenticar = String.valueOf(HandlerParameters.PARAMETROS.getAutenticarflujo());
    //out.println("autenticar = " + autenticar);
    
    // ydavila Elmor - campo LDAPURL tiene data?  
    String ldapurl = com.desige.webDocuments.persistent.managers.HandlerParameters.getLDAPurl();
    //out.println("ldapurl = " + ldapurl);
    
    String clave=HandlerDBUser.getClaveUser(users);
    //out.println("clave = " + clave);
     
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

    String origen =	(String)request.getAttribute("origen");
	if(ToolsHTML.isEmptyOrNull(origen)) origen = "";
	
	String cadenaComentario = "";
	if(request.getAttribute("comentariosDelUsuario")!=null){
		cadenaComentario = request.getAttribute("comentariosDelUsuario").toString();
	}
	
	String cambiar = (String) request.getAttribute("cambiar");
%>
<html>
<head>
<title><%=rb.getString("principal.title")%></title>
<jsp:include page="meta.jsp" /> 
<script type="text/javascript" src="./estilo/funciones.js"></script>
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

    var isUsuarioValido = "false";
    function validarClave(pass,forma) {
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
				botones(forma);
				forma.submit();
			} else {
                 alert('<%=rb.getString("badPass")%>');
                 return false;
			}
		  }
		}
		http.send(null);
    }

    function botones(forma) {
        if (forma.btnOK) {
            forma.btnOK.disabled = true;
        }
        if (forma.btnCancel) {
            forma.btnCancel.disabled = true;
        }
    }

function salvar(forma,result,statu){

    	forma.result.value =  result;
        forma.statu.value = statu;
        updateRTEs();
        forma.commentsUser.value = forma.richedit.value;
        if (result=='<%=HandlerWorkFlows.canceled%>') {
            if (confirm("<%=rb.getString("E0067")%>")) {
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
            } else {
                return false;
            }
        }
        
        <%-- ydavila Ticket Elmor - Validar check de Autenticación de clave  --%>
        if (isEmptyRicheditValue(forma.richedit.value)) {
            alert('<%=rb.getString("err.invalidComment")%>');
            return false;
        }
        
        if ('<%=String.valueOf(HandlerParameters.PARAMETROS.getAutenticarflujo())%>'=="1") {
            if (document.getElementById("pass").value==""){
               alert('<%=rb.getString("notPass")%>'); return false;
            }else{
	        	validarClave(document.getElementById("pass").value,forma);
            }
        } else {
			botones(forma);
			forma.submit();
		}
    }

    function canceledWF(forma,result,statu){
        forma.result.value =  result;
        forma.statu.value = statu;
        forma.action = "closeWF.do";
        updateRTEs();
        forma.commentsUser.value = forma.richedit.value;
       // if (result=='< %=HandlerWorkFlows.canceled%>') {
        
        if (result=='<%=HandlerWorkFlows.ownercancelcloseflow%>') {
	        if (isEmptyRicheditValue(forma.richedit.value)) {
                alert('<%=rb.getString("err.causeCanceledWF")%>');
                return false;
            }
        }
        <%-- ydavila Ticket Elmor - Validar check de Autenticación de clave  --%>
        if ('<%=String.valueOf(HandlerParameters.PARAMETROS.getAutenticarflujo())%>'=="1") {
            if (document.getElementById("pass").value==""){
               alert('<%=rb.getString("notPass")%>'); return false;
            }else{
	        	validarClave(document.getElementById("pass").value,forma);
            }
        } else {
			botones(forma);
			forma.submit();
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
    <%-- Fin 09-april-07 --%>
    function edit(){
        document.showDocument.submit();
    }
    function updateCheck(check,field) {
        if (check.checked){
            field.value = "0";
        } else{
            field.value = 1;
        }
    }

    function createWF(item) {
        document.newWF.typeWF.value = item;
        document.newWF.target = "_parent";
        document.newWF.submit();
    }

    function showDocument(idDocument,idVersion){
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



    function editRegister(idDocument,idVersion,nameFile,obj,flujo){
        var hWnd = null;
        hWnd = window.open("editRegister.jsp?closeWindow=true&idDocument="+idDocument+"&nameFile="+escape(nameFile)+"&idVersion="+idVersion+"&idWF="+flujo,"","width=800,height=600,resizable=yes,scrollbars=yes,left=100,top=100");
        if ((document.window != null) && (!hWnd.opener)) {
            hWnd.opener = document.window;
        }
    }

	var http = false;
    
    function abrir(obj,idDocument,idVersion) {
    	respuesta = "false";
		if(navigator.appName == "Microsoft Internet Explorer") {
		  http = new ActiveXObject("Microsoft.XMLHTTP");
		} else {
		  http = new XMLHttpRequest();
		}
		
		http.onreadystatechange=function() {
		  if(http.readyState == 4) {
			//
		  }
		}
		//para evitar lectura de un jnlp no existente, hacemos la llamada ajax de manera sincrona
		http.open("POST", "markCheckOutAjax.do?registro=true&idDocument="+idDocument+"&idVersion="+idVersion, false);
        http.send(null);
		//pause(2000);
		
		obj.href="editorPorInternet/qwds4Registro<%= users.getIdPerson()%>.jnlp";
		//obj.style.display="none";
		setTimeout("eliminar()",60000);
    }
    
	function pause(millis) {
		var date = new Date();
		var curDate = null;
	
		do { curDate = new Date(); }
		while(curDate-date < millis);
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
		}
		http.send(null);
    }


    function showDataDocument(id){
    	var formaSelection = document.getElementById("Selection");
		formaSelection.target="info"; // ocupa el frame inferior de la pantalla
    	//formaSelection.target="_self";
        formaSelection.idDocument.value = id;
        if (!parent.frames['code']) {
            formaSelection.showStruct.value = "true";
        } else {
            formaSelection.showStruct.value = "false";
        }
        formaSelection.submit();
    }

    function showCharge(userName,charge) {
	    charge = charge.replace(/\[/gi,"-").replace(/\]/gi,"-");
        window.open("showCharge.jsp?userName="+userName+"&charge="+charge, "WebDocuments", "resizable=yes,scrollbars=yes,statusbar=yes,width=300,height=200,left=410,top=250");
    }

    function toggleDiv(gn)  {
    	try {
			//var g = document.all(gn);
			var g = document.getElementById(gn);
			
			if( g.style.display == "none")
				g.style.display = "";
			else
				g.style.display = "none";
		} catch(e) {}
    }

    function regresarFlow(){
     <%if("inboxWFs".equals(origen)){%>
    	window.open("loadInboxWF.do","info");
     <%}else if ("principal".equals(origen)){%>
    	window.open("principal.jsp","info");
     <%}else if ("showDataDocument".equals(origen)){%>
    	history.back();
     <%}%>
    }
</script>
</head>

<body class="bodyInternas" <%=onLoad%>>
<%--request.getAttribute("origen")--%>
<logic:present name="showDataWF" scope="session">

    <bean:define id ="doc" name="showDataWF" type="com.desige.webDocuments.workFlows.forms.DataWorkFlowForm" scope="session"/>
    <!-- Formulario para Obtener el nodo Seleccionado por el usuario -->
        <form name="Selection" id="Selection" action="showDataDocument.do">
            <input type="hidden" name="idDocument" value=""/>
            <input type="hidden" name="idWF" value="<%=doc.getIdWorkFlow()%>"/>
            <%--<input type="hidden" name="from" value="loadWF.do?idWorkFlow=<%=doc.getIdWorkFlow()%>&row=<%=doc.getIdMovement()%>&owner=<%=request.getAttribute("owner").toString()%>"/>--%>
            <%--<input type="hidden" name="from" value="loadWF.do?idWorkFlow=<%=doc.getIdWorkFlow()%>&row=<%=request.getAttribute("row")%>&owner=<%=request.getAttribute("owner").toString()%>&isFlexFlow=<%=request.getAttribute("isFlexFlow").toString()%>"/>--%>
            <input type="hidden" name="from" value="loadWF.do?idWorkFlow=<%=doc.getIdWorkFlow()%>&row=<%=request.getAttribute("row")%>&owner=<%=request.getAttribute("owner")%>&isFlexFlow=<%=request.getAttribute("isFlexFlow")%>"/>
            <input type="hidden" name="showStruct" value="true"/>
            <%--<input type="hidden" name="from" value="loadWF.do?idWorkFlow=<%=doc.getIdWorkFlow()%>&row=<%=doc.getIdMovement()%>&owner=false&isFlexFlow=true"/>--%>
        </form>
    <!-- END Form -->
    <table align=center border=0 width="100%">
        <tr>
            <td valign="top" colspan="4">
                <table class="clsTableTitle" width="100%" cellSpacing=0 cellPadding=2 align=center border=0>
                    <tbody>
                        <tr>
                            <td class="td_title_bc" height="21">
                            <%--ydavila Elmor--%>                               
                            <%--<%=rb.getString("wf.titleWF"+doc.getTypeWF())%> <%=doc.getEliminar()!=0?rb.getString("wf.delete"):""%> <%=Boolean.TRUE.equals((Boolean) request.getAttribute("isPrintWF"))?"<span style='color:blue'>(".concat(rb.getString("imp.histsolicitudimpresion")).concat(")</span>"):""%>--%> 
                            </td>
                        </tr>
                    </tbody>
                </table>
            </td>
        </tr>
        <tr>
            <td width="16%" class="td_orange_l_b" height="26" style="background: url(img/btn160.png); background-repeat: no-repeat" height="20 px"  valign="middle">
                <%=rb.getString("doc.title")%>

                       <%
                //ParticipationForm forma = (ParticipationForm) session.getAttribute("responseWF");
                //out.println(" status");
                //out.println(forma.getStatu());
            %>
            </td>
            <td class="td_gris_l" width="*" colspan="3">
                <bean:write name="showDataWF" property="nameDocument"/> &nbsp; <%=doc.getPrefix()+doc.getNumber()%>
	           	<%if(!com.desige.webDocuments.utils.Constants.ID_GROUP_VIEWER.equals(String.valueOf(users.getIdGroup()))){%>
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
            <td colspan="4" align="left" class="txt_title">
                <span style="cursor: hand;" onclick='toggleDiv("comments"); if(comments.style.display == "none"){ commentsImg.src="menu-images/mas.gif";} else { commentsImg.src="menu-images/menos.gif";}'>
                    <img id='commentsImg' src='menu-images/mas.gif' />
                    <%=rb.getString("wf.requestDetail")%>
                </span>
            </td>
        </tr>
        <logic:present name="showEditReg" scope="session">
            <logic:present name="usersWFOwner" scope="session">
                <logic:iterate id="particiOwner" name="usersWFOwner" type="com.desige.webDocuments.workFlows.forms.ParticipationForm" scope="session">
                    <logic:equal value="1" property="statu" name="particiOwner">
                        <logic:present name="responseWF" scope="session">
                            <logic:equal value="1" name="responseWF" property="statu">
                                <tr>
                                    <td colspan="4" align="left" class="txt_title">
                                        <bean:write name="showDataWF" property="request"/>
		                	    <% if (ToolsHTML.editOriginatorWF() || "0".equals(String.valueOf(doc.getTypeWF()))) { %>  <!-- PENDIENTE -->
			                	    <% if (!ToolsHTML.abrirEditorWebStart((HttpServletRequest)request)) { %>
	                                        <a class="ahref_b" href="javascript:editRegister('<%=doc.getNumDocument()%>','<%=doc.getNumVersion()%>','<%=doc.getNameFile()%>',this,'<%=doc.getIdWorkFlow()%>')">
	                                            <%=rb.getString("wf.editRegister")%>
	                                        </a>
									<%}else {%>
	                                        <a class="ahref_b" onclick="abrir(this,'<%=doc.getNumDocument()%>','<%=doc.getNumVersion()%>')">
	                                            <%=rb.getString("wf.editRegister")%>
	                                        </a>
									<%} %>
								<%} %>
                                    </td>
                                </tr>
                            </logic:equal>
                        </logic:present>
                    </logic:equal>
                </logic:iterate>
            </logic:present>
        </logic:present>
       <tr>
            <td colspan="4" align="left" style="padding: 0px" class="td_gris_l">
                <div align="left" class="clsMessageView" id='comments' style="width: 100%; height: 160px">
                    <%=doc.getComments()%>
                </div>
                <script type="text/javascript">
                    <% if (doc.getComments()!=null) { %>
                        document.getElementById("comments").style.display = "none";
                    <% } %>
                </script>
            </td>
       </tr>

        <tr>
            <td colspan="4">&nbsp;

            </td>
        </tr>

        <tr>
            <td valign="top" colspan="4" height="21">
                <table class="clsTableTitle" width="100%" cellSpacing="0" cellPadding="2" align="center" border="0">
                    <tr>
                        <td width="50%" valign="top">
                                &nbsp;<%=rb.getString("wf.participantion")%>
                        </td>
                        <td width="15%">
                                &nbsp;<%=rb.getString("cbs.statu")%>
                        </td>
                        <td width="15%">
                                <%=rb.getString("wf.result")%>
                        </td>
                        <td width="20%">
                                <%=rb.getString("wf.dateDoc")%>
                        </td>
                    </tr>
                </table>
                
                <logic:present name="usersWF" scope="session" >
                    <table cellSpacing=0 cellPadding=0 align=center border="0" width="100%">
                        <logic:iterate id="participation" name="usersWF" type="com.desige.webDocuments.workFlows.forms.ParticipationForm" scope="session">
                            <tr>
                                <td class="td_gris_l" width="50%">
                                    <logic:present name="showCharge" scope="session">
                                        <a href="javascript:showCharge('<%=participation.getNameUser()%>','<%=participation.getCharge()%>')" class="ahref_b">
                                            <bean:write name="participation" property="charge" />
                                        </a>
                                    </logic:present>
                                    <logic:notPresent name="showCharge" scope="session">
                                        <a href="javascript:showCharge('<%=participation.getNameUser()%>','<%=participation.getCharge()%>')" class="ahref_b">
                                            <bean:write name="participation" property="nameUser" />
                                        </a>
                                    </logic:notPresent>
                                    <!-- Si el Documento es tipo Registro y el Usuario tiene permiso de Edición se permite editar el Mismo -->
                                    <%
                                    Users usuario = (Users)session.getAttribute("user");
                                    %>
                                    <logic:present name="responseWF" scope="session">
                                        <logic:equal value="1" name="responseWF" property="statu">
                                            <logic:equal value="1" property="statu" name="participation">
                                                <logic:present name="showEditReg" scope="session" >
                                                    &nbsp;<!-- jairo -->
				                	    <% if (ToolsHTML.editOriginatorWF() || "0".equals(String.valueOf(doc.getTypeWF()))) { %>  <!-- PENDIENTE -->
		                	    		<% if(usuario.getUser()!=null && participation.getIdUser()!=null && usuario.getUser().equals(participation.getIdUser())) {
		                	    			if (!ToolsHTML.abrirEditorWebStart((HttpServletRequest)request)) { %>
		                                        <a class="ahref_b" href="javascript:editRegister('<%=doc.getNumDocument()%>','<%=doc.getNumVersion()%>','<%=doc.getNameFile()%>',this,'<%=doc.getIdWorkFlow()%>')">
		                                            <%=rb.getString("wf.editRegister")%>
		                                        </a>
											<%}else {%>
			                                    <a class="ahref_b" onclick="abrir(this,'<%=doc.getNumDocument()%>','<%=doc.getNumVersion()%>')">
                                                    <%=rb.getString("wf.editRegister")%>
                                                </a>
											<%} 
										}%>
										<%}%>
                                                </logic:present>
                                            </logic:equal>
                                        </logic:equal>
                                    </logic:present>
                                </td>

                                <td class="td_gris_l" width="15%">
                                    <%=rb.getString("wf.statuUserWF"+participation.getStatu())%>
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

                <logic:present name="comments" scope="session">
                    <logic:iterate id="replies" name="comments" type="com.desige.webDocuments.utils.beans.Search3" scope="session">
                        <tr>
                            <td colspan="4">
                                &nbsp;
                            </td>
                        </tr>
                        <tr>
                            <td colspan="4" align="left" class="txt_title">
                                <span style="cursor: hand;" onclick='toggleDiv("<%=replies.getId()%>"); if(document.getElementById("<%=replies.getId()%>").style.display == "none"){ <%=replies.getId()%>Img.src="menu-images/mas.gif";} else { <%=replies.getId()%>Img.src="menu-images/menos.gif";}'>
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
                                <script type="text/javascript">
                                    <% if (replies.getDescript()!=null){ %>
                                        document.getElementById("<%=replies.getId()%>").style.display = "none";
                                    <%}%>
                                </script>
                            </td>
                       </tr>
                   </logic:iterate>
                </logic:present>
                
                    <logic:equal value="1" name="responseWF" property="statu">
                        <html:form action="/responseWF.do">
                        <html:hidden property="result" />
                        <html:hidden property="row"/>
                        <html:hidden property="statu"/>
                        <html:hidden property="idMovement"/>
                        <html:hidden property="numVersion"/>
                        <html:hidden property="idDocument"/>
                        <html:hidden property="idWorkFlow"/>
                        <html:hidden property="cambiar" value="<%=cambiar%>"/> <!-- POR CAMBIAR -->
                        
                        <html:textarea property="commentsUser" style="display:none"/>                      
<%-- ydavila Elmor --%>
<tr>
 <td colspan="4" heigth="22" class="titleCenter" style="background: url(img/BgComments.gif); background-repeat: no-repeat" valign="middle">
 </td>
</tr> 
<% if ("1".equals(String.valueOf(HandlerParameters.PARAMETROS.getAutenticarflujo()))) { %>
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
   <input type="password" name="pass" property="pass" id="pass" style="width:245px; height: "30 px" autocomplete="off" value="" "/>
   
 </td>
</tr>     
<%}%>                     
<%-- ydavila Elmor --%>                       
                        <tr>
                            <td colspan="4" heigth="22" class="titleCenter" style="background: url(img/BgComments.gif); background-repeat: no-repeat" valign="middle">
                                <%=rb.getString("wf.responseTitle")%>
                            </td>
                        </tr>
                        <tr>
                            <td class="fondoEditor"  colspan="4" valign="top">
                            	  <%if(request.getAttribute("comentariosDelUsuario")!=null) {%>
								  <jsp:include page="richedit.jsp">
									<jsp:param name="richedit" value="richedit"/>
									<jsp:param name="defaultValue" value="<%=cadenaComentario%>" />
								  </jsp:include>
                            	  <%} else {%>
								  <jsp:include page="richedit.jsp">
									<jsp:param name="richedit" value="richedit"/>
									<jsp:param name="defaultValue" value="<%=String.valueOf(doc.getCommentsUsrs())%>" />
								  </jsp:include>
								  <%}%>
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
                                <logic:equal value="0" name="showDataWF" property="typeWF">
                                    <input type="hidden" name="typeWF" value="0"/>
                                    <input type="button" class="boton" value="<%=rb.getString("btn.wf.review")%>" name="btnOK" onClick="javascript:salvar(this.form,'<%=HandlerWorkFlows.response%>','<%=HandlerWorkFlows.wfuAcepted%>');" /> 
                                </logic:equal>
                                <logic:equal value="1" name="showDataWF" property="typeWF">
                                    <input type="hidden" name="typeWF" value="1"/>
                                    <input type="button" class="boton" value="<%=rb.getString("btn.wf.aproved")%>" name="btnOK" onClick="javascript:salvar(this.form,'<%=HandlerWorkFlows.response%>','<%=HandlerWorkFlows.wfuAcepted%>');" />
                                </logic:equal>
                                &nbsp;
                                &nbsp;
                                &nbsp;
                                &nbsp;
                                <input type="button" class="boton" value="<%=rb.getString("btn.rejected")%>" name="btnCancel" onClick="javascript:salvar(this.form,'<%=HandlerWorkFlows.canceled%>','<%=HandlerWorkFlows.wfuCanceled%>');" />

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
                                <input type="button" class="boton" value="<%=rb.getString("btn.back")%>" onClick="javascript:regresarFlow(1);" name="btnBack" />
                                <%}else{%>
                                <input type="button" class="boton" value="<%=rb.getString("btn.back")%>" onClick="javascript:regresar(this.form);" name="btnBack" />
                                <%}%>
                                 <%--fin 09-apr-07 --%>

                            </td>
                        </tr>
                        </html:form>
                    </logic:equal>
                    <logic:notEqual value="1" name="responseWF" property="statu">
                        <logic:equal value="true" name="toCanceled">
                            <html:form action="/responseWF.do" >
                                <html:hidden property="result" />
                                <html:hidden property="row"/>
                                <html:hidden property="statu"/>
                                <html:hidden property="idMovement"/>
                                <html:hidden property="numVersion"/>
                                <html:hidden property="idDocument"/>
                                <html:hidden property="idWorkFlow"/>
                                <html:textarea property="commentsUser" style="display:none"/>
<%-- ydavila Elmor --%>
<tr>
 <td colspan="4" heigth="22" class="titleCenter" style="background: url(img/BgComments.gif); background-repeat: no-repeat" valign="middle">
 </td>
</tr> 
<% if (true && "1".equals(String.valueOf(HandlerParameters.PARAMETROS.getAutenticarflujo()))) { %>
<tr> 
 <td class="td_orange_l_b" style="background: url(img/btn160.png); background-repeat: no-repeat" width:"50 px"  height="30 px" valign="middle">
   USUARIO..
 </td>
 <td class="td_orange_l_b" style="background: url(img/btn160.png); background-repeat: no-repeat" width:"50 px"  height="30 px" valign="middle">
  <bean:write name="user" property="user"/>
 </td>
</tr> 
<tr> 
 <td class="td_orange_l_b" style="background: url(img/btn160.png); background-repeat: no-repeat" height="30 px" valign="middle">
   CLAVE (obligatorio)
 </td>
 <td>
   <input type="password" name="pass" property="pass" id="pass" style="width:245px; height: "30 px" autocomplete="off" value="" "/>
   
 </td>
</tr>     
<%}%>                     
<%-- ydavila Elmor --%>                       
                                <tr>
                                    <td colspan="4" heigth="22" class="titleCenter" style="background: url(img/BgComments.gif); background-repeat: no-repeat" valign="middle">
                                        <%=rb.getString("wf.canceledTitle")%>
                                    </td>
                                </tr>
                                <tr>
                                    <td class="fondoEditor" colspan="4" valign="top">
										  <jsp:include page="richedit.jsp">
											<jsp:param name="richedit" value="richedit"/>
											<jsp:param name="defaultValue" value="<%=String.valueOf(doc.getCommentsUsrs())%>" />
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
                                        <!--<input type="button" class="boton" value="" name="btnCancel" onClick="javascript:canceledWF(this.form,'< %=HandlerWorkFlows.canceled%>','< %=HandlerWorkFlows.wfuCanceled%>');">-->
                                        <input type="button" class="boton" value="<%=rb.getString("btn.ok")%>" name="btnCancel" onClick="javascript:canceledWF(this.form,'<%=HandlerWorkFlows.ownercancelcloseflow%>','<%=HandlerWorkFlows.ownercancelcloseflow%>');" />
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
                                        <input type="button" class="boton" value="<%=rb.getString("btn.back")%>" onClick="javascript:regresarFlow(2);" name="btnBack" />
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
					    	<input type="button" class="boton" value="<%=rb.getString("btn.back")%>" onClick="javascript:regresarFlow(3);" name="btnBack"/>
			            </td>
			        </tr>
					<%}%>
			        </logic:notEqual>
			        </logic:notEqual>
			        					
			</table>
            </td>
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
<script language="javascript">
<%
if(request.getAttribute("ERROR_LOGIN")!=null){
	out.println("alert('".concat(request.getAttribute("ERROR_LOGIN").toString()).concat("');"));
}
%>
</script>
