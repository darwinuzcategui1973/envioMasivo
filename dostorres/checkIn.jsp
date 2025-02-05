<jsp:include page="richeditDocType.jsp" />   

<!--
 * Title: checkIn.jsp
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * @author Ing. Nelson Crespo (NC)
 * @version WebDocuments v3.0
 * <br/>
 * Changes:<br/>
 * <ul>
 *      <li> 01/06/2005 (NC) Cambios para redirigir a la estructura una vez cargada la versión del Documento </li>
 *      <li> 14/06/2005 (SR) Cambios en comentarios por  la etiqueta wf.razondelcambio</li>
 *      <li> 18/07/2005 (NC) Cambios en el manejo del Check In & Check Out
 *      <li> 24/07/2005 Cambios para redigirir correctamente una vez realizado el check In </li>
   </ul>
-->

<%@ page import="java.util.ResourceBundle,
                 com.desige.webDocuments.utils.ToolsHTML,
                 com.desige.webDocuments.utils.beans.Users,
                 com.desige.webDocuments.persistent.managers.HandlerBD,
                 com.desige.webDocuments.utils.beans.SuperActionForm,
                 java.util.Hashtable,
                 com.desige.webDocuments.structured.forms.BaseStructForm,
                 com.desige.webDocuments.utils.Constants,
                 com.desige.webDocuments.utils.DesigeConf,
                 com.desige.webDocuments.persistent.managers.HandlerParameters,
                 com.desige.webDocuments.persistent.managers.HandlerStruct,
                 com.desige.webDocuments.persistent.managers.HandlerDBUser"%>

<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<%@ page language="java" %>
<%
    ResourceBundle rb = ToolsHTML.getBundle(request);
    String dateSystem = ToolsHTML.date.format(new java.util.Date());
    StringBuffer onLoad = new StringBuffer(100);
    Users users = (Users)session.getAttribute("user");
    String ok = (String)session.getAttribute("usuario");
    String info = (String)session.getAttribute("info");
    onLoad.append(" onLoad=\"self.status='").append(rb.getString("pagin.welcome")).append(" ");
    onLoad.append(ok).append("'");
    if (ToolsHTML.checkValue(info)) {
        onLoad.append(";alert('").append(info).append("')");
        session.removeAttribute("info");
    }
    if (onLoad.length()>0){
        onLoad.append("\"");
    }
    String regID = DesigeConf.getProperty("typeDocs.docRegister");
%>
<html>
<head>
<title><%=rb.getString("principal.title")%></title>
<jsp:include page="meta.jsp" /> 
<link href="estilo/estilo.css" rel="stylesheet" type="text/css">
<script type="text/javascript" src="./estilo/popcalendar2.js"></script>
<script type="text/javascript" src="./estilo/fechas.js"></script>
<jsp:include page="richeditHead.jsp" />
<script type="text/javascript">
    var docLinks;
    var hWnd = null;
    
    String.prototype.endsWith = function(str){return (this.match(str+"$")==str);}

    function getPathFromLocation() {
            var cad = document.location.href;
            cad = cad.replace("http://","");
            var arr = cad.split("/");
            cad = "http://"+arr[0]+"/";
            if(!arr[1].endsWith(".do")) {
                    cad=cad+arr[1]+"/";
            }
            return cad;
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
    function validarFechaAprobacion(valor){
        if (!validarFecha(valor)){
            alert('<%=rb.getString("err.badDateEndWF")%>');
            return false;
        }
        if (valor > '<%=dateSystem%>'){
            alert('<%=rb.getString("err.badDateEndWF")%>');
            return false;
        }
        return true;
    }

    function validarFechaExpiracion(valor){
        if (!validarFecha(valor)){
            alert('<%=rb.getString("err.badDateExpiresDoc")%>');
            return false;
        }
        if (valor < '<%=dateSystem%>'){
            alert('<%=rb.getString("err.badDateExpiresDoc")%>');
            return false;
        }
        return true;
    }

    function validar(forma,validate){
        if (validate) {
            if (forma.approvedI) { //Primero Verifico que el Check Existe en el Formulario
                if (forma.approvedI.checked) { //Si esta Seleccionado
                    if (!validarFechaAprobacion(forma.dateApproved.value)){
                        return false;
                    }
                } else { //Si el CheckBox no esta Seleccionado
                    var datos = forma.dateApproved.value;
                    if (datos.length > 0){
                        alert('<%=rb.getString("err.mustSelect")%>');
                        return false;
                    }
                }
            }
        }
//        if (forma.expiresI) { //Primero Verifico que el Check Existe en el Formulario
//            if (forma.expiresI.checked) { //Si esta Seleccionado
//                if (!validarFechaExpiracion(forma.dateExpires.value)){
//                    return false;
//                }
//            }
//        }
        if (forma.nameFile.value.length == 0){
            alert('<%=rb.getString("err.notFile")%>');
            return false;
        }

    	updateRTEs();
        forma.comments.value = forma.richedit.value;

        if (isEmptyRicheditValue(forma.richedit.value)) {
            alert('<%=rb.getString("err.invalidCommentDoc")%>');
            return false;
        }
        
        return true;
    }

    function getDateExpire(dateField,nameForm,dateValue,text){
        window.open("getDate.jsp?field=" + dateField + "&nameForm="+nameForm+"&valueForm="+dateValue+"&text="+text,"","width=450,height=250,resizable=no,scrollbars=yes,left=300,top=250");
    }

    function showWindow(pages,input,nameForm,value,width,height){
        var hWnd = null;
        hWnd = window.open(pages+"?input="+input+"&nameForma="+nameForm+"&value="+value,"","width="+width+",height="+height+",resizable=no,scrollbars=yes,statusbar=yes,left=250,top=250");
        if ((document.window != null) && (!hWnd.opener)) {
            hWnd.opener = document.window;
        }
    }

    function applyChanges(msg) {
        if (hWnd!=null && !hWnd.closed && hWnd.name == "editDocument") {
            alert(msg);
            hWnd.focus();
            return false;
        }
        return true;
    }        

    function setNull() {
        hWnd = null;
    }

    //<!-- TODO: CAMBIO PARA DOBLE VERSION -->
    function validarExtensionDoble(forma) {
    	if(forma.nameFileParalelo.value.length!=0) {
    		return true;
    	}
    	cad = forma.nombreArchivo.value;
    	while(cad.indexOf(".")!=-1) {
    		cad = cad.substring(cad.indexOf(".")+1);
    	}
    	cad = "."+cad;
    	
    	if(extensionDouble.indexOf(cad)!=-1) {
			alert('<%=rb.getString("err.insertViewFile")%>');
    		document.getElementById("paralelo").style.display="";
    		return false;
    	}
    	return true;
    }
    
    //<!-- TODO: CAMBIO PARA DOBLE VERSION -->
    function requiereView(forma) {
    	cad = forma.nombreArchivo.value;
    	while(cad.indexOf(".")!=-1) {
    		cad = cad.substring(cad.indexOf(".")+1);
    	}
    	cad = "."+cad;

   		document.getElementById("paralelo").style.display="none";
    	if(extensionDouble.indexOf(cad)!=-1) {
    		document.getElementById("paralelo").style.display="";
    	}
    }

    function salvar(formulario,validate) {
	    //<!-- TODO: CAMBIO PARA DOBLE VERSION -->
    	if (!validarExtensionDoble(formulario)) { 
    		return;
    	}
    
    	formulario.action = "servletVersionDocuments?cmd=upFile";
        
        if (applyChanges('<%=rb.getString("err.notApplyChanges")%>')) {
            if (validar(formulario,validate)) {
            	updateRTEs();
                formulario.comments.value = formulario.richedit.value;

                if (isEmptyRicheditValue(formulario.richedit.value)) {
                	alert('<%=rb.getString("err.invalidCommentDoc")%>');
		            return false;
		        }
		        
                //formulario.target.value = "info";
                if (parent) {
                    if ((parent.frames['text'])||(parent.frames['opciones'])) {
                        formulario.target.value = "_self";
                    }
                }
                document.getElementById('botonera').style.display="none";
                document.getElementById('botonEditar').style.display="none";
                document.getElementById('botoneraMensaje').style.display="";
                
                formulario.submit();
                return;
            }
        }
    }
    
    function saveReason() {
    	alert("boton guardar");
    	formulario = document.razon;
    	formulario.id.value=document.newDocument.idCheckOut.value;
    	formulario.nameDocCheckout.value=document.newDocument.nameDocument.value;
    	formulario.ownerCheckout.value=document.newDocument.owner.value;
    	formulario.razonNombre.value=document.newDocument.razonNameDocument.value;
    	formulario.razonPropietario.value=document.newDocument.razonOwner.value;
        formulario.submit();
        
        
    }
    
    
    function updateCheck(check,field,texto){
        if (check.checked) {
            field.value = "0";
            texto.readOnly = false;
            texto.focus();
        } else {
            texto.readOnly = true;
            field.value = 1;
        }
    }

    function updateCheck(check,field){
        if (check.checked){
            field.value = "0";
        } else{
            field.value = 1;
        }
    }

    function cancelar() {
    	if (document.newDocument.changeVerI.disabled==false && document.newDocument.changeVerI.checked ) {
    		alert('<%=rb.getString("err.notChangeVersion")%>');
    		return false;
    	}
        forma = document.getElementById("Selection");
        if (applyChanges('<%=rb.getString("err.notApplyChanges")%>')) {
        	 if (!parent.frames['code']) {
            	 forma.action = "loadAllStruct.do";
            }
            forma.submit();
        }
    }

    function editDocumento(idCheckOut,nameFile,idDocument,numVersion) {
        //document.newDocument.btnBack.disabled = true;
        document.newDocument.btnBack.style.display="none";
        if (hWnd!=null && hWnd.name == "editDocument") {
            hWnd.focus();
        } else {
        	editar();
        	//alert("editDocument.jsp?closeWindow=true&idCheckOut="+idCheckOut+"&nameFile="+escape(nameFile)+"&idDocument="+idDocument+"&numVersion="+numVersion);
            hWnd = window.open("editDocument.jsp?closeWindow=true&idCheckOut="+idCheckOut+"&nameFile="+escape(nameFile)+"&idDocument="+idDocument+"&numVersion="+numVersion,"editDocument","width=800,height=600,resizable=yes,scrollbars=yes,left=100,top=100");
            if ((document.window != null) && (!hWnd.opener)) {
                hWnd.opener = document.window;
            }
        }
    }
    
    function cargarDocumento(idCheckOut,nameFile,idDocument,numVersion) {
        document.newDocument.btnBack.disabled = true;
        hWnd=null;
        if (hWnd!=null && hWnd.name == "editDocument") {
            hWnd.focus();
        } else {
        	editar();
        	//alert("editDocument.jsp?closeWindow=true&idCheckOut="+idCheckOut+"&nameFile="+escape(nameFile)+"&idDocument="+idDocument+"&numVersion="+numVersion);
            hWnd = window.open("loadDocument.jsp?closeWindow=true&idCheckOut="+idCheckOut+"&nameFile="+escape(nameFile)+"&idDocument="+idDocument+"&numVersion="+numVersion,"editDocument","width=800,height=600,resizable=yes,scrollbars=no,left=100,top=100");
            if ((document.window != null) && (!hWnd.opener)) {
                hWnd.opener = document.window;
            }
        }
    }
    
    
	var http = false;
    
    function abrir(obj) {
    	respuesta = "false";
		if(navigator.appName == "Microsoft Internet Explorer") {
		  http = new ActiveXObject("Microsoft.XMLHTTP");
		} else {
		  http = new XMLHttpRequest();
		}
		http.open("POST", "markCheckOutAjax.do?idCheckOut="+document.newDocument.idCheckOut.value+"&registro=false&editar=true&server="+getServerLocation());
		http.onreadystatechange=function() {
		  if(http.readyState == 4) {
		  		document.editor.action=getPathFromLocation()+"editorPorInternet/qwds4<%= users.getIdPerson()%>.jnlp";
			  	document.editor.submit();
				setTimeout("eliminar()",60000);
		  }
		}
		http.send(null);
		//pause(2000);
		//obj.href="editorPorInternet/qwds4<%= users.getIdPerson()%>.jnlp";
		//setTimeout("eliminar()",60000);
    }
    
    function editar() {
	    //alert("editar");
    	respuesta = "false";
		if(navigator.appName == "Microsoft Internet Explorer") {
		  http = new ActiveXObject("Microsoft.XMLHTTP");
		} else {
		  http = new XMLHttpRequest();
		}
		http.open("POST", "markCheckOutAjax.do?registro=false&editar=true&salir=true");
		http.onreadystatechange=function() {
		  if(http.readyState == 4) {
			
		  }
		}
		http.send(null);
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
		http.open("POST", "markCheckOutAjax.do?eliminar=true&registro=false");
		http.onreadystatechange=function() {
		  if(http.readyState == 4) {
		  	//editar(http.responseText);
		  }
		}
		http.send(null);
    }
    
    function noEditar(){
		if(navigator.appName == "Microsoft Internet Explorer") {
		  http = new ActiveXObject("Microsoft.XMLHTTP");
		} else {
		  http = new XMLHttpRequest();
		}
		http.open("POST", "markCheckOutAjax.do?noEditar=true&registro=false&salir=true");
		http.onreadystatechange=function() {
		  if(http.readyState == 4) {
		  	//editar(http.responseText);
		  }
		}
		http.send(null);    
    }
    
	function wnd(pages,input,nameForm,value,title,width,height,read){
	    var hWnd = null;
	    //alert(pages+"&input="+input+"&nameForma="+nameForm+"&value="+value+"&title="+title+"&read="+read);
	    hWnd = window.open(pages+"&input="+input+"&nameForma="+nameForm+"&value="+value+"&title="+title+"&read="+read,"","width="+width+",height="+height+",resizable=no,scrollbars=yes,statusbar=yes,left=100,top=150");
	    if ((document.window != null) && (!hWnd.opener)){
	        hWnd.opener = document.window;
	    }
	}
    
	function evaluaChequeo(forma,parametro,titleForm){
            if (parametro=='n'){
                wnd('changeProperties.jsp?tipo=n','razonNameDocument','newDocument',forma.nameDocument.value,titleForm ,800,470,'0');
            }else if (parametro=='p'){
                wnd('changeProperties.jsp?tipo=p','razonOwner','newDocument',forma.owner.value,titleForm ,800,470,'0');    
            }

    }

</script>

<!-- TODO: CAMBIO PARA DOBLE VERSION -->
<script language="javascript">
	var extensionDouble="";
	<%
	try {
		// validar o no el navegador - para este caso ie7
		String valor = HandlerParameters.PARAMETROS.getFileDoubleVersion();
		out.println("extensionDouble=\"".concat(valor==null?"":valor).concat("\";"));
	} catch (Exception e) {
		e.printStackTrace();
	}

	String[] fileUploadVersion = null;
	try {
		// pedimos las extensiones no editables
		String valor = HandlerParameters.PARAMETROS.getFileUploadVersion();
		out.println("//valor="+valor);
		fileUploadVersion = valor.split(",");
	} catch (Exception e) {
		e.printStackTrace();
	}
	
	%>
</script>

</head>

<body class="bodyInternas" <%=onLoad%> onUnLoad="javascript:noEditar();">

<form name="razon" action="saveReason.do">
	<input type="hidden" name="id" value=""/>
	<input type="hidden" name="nameDocCheckout" value=""/>
	<input type="hidden" name="ownerCheckout" value=""/>
	<input type="hidden" name="razonNombre" value=""/>
	<input type="hidden" name="razonPropietario" value=""/>
</form>

<form name="Selection" id="Selection" action="loadStructMain.do">
</form>
<logic:present name="checkInDoc" scope="session">
    <bean:define id="doc" name="checkInDoc" scope="session" type="com.desige.webDocuments.document.forms.CheckOutDocForm" />
    <%
    boolean isEditable=true;
    if(fileUploadVersion!=null) {
	    for(int i=0; i<fileUploadVersion.length;i++) {
	    	if (fileUploadVersion[i]!=null && fileUploadVersion[i].trim().length()>0 && doc.getNameFile().endsWith(fileUploadVersion[i])) {
				isEditable=false;
	    	}
	    }
    }
    %>
    <html:form action="/upploadFile.do" method="post" enctype="multipart/form-data">
        <input type="hidden" name="cmd" value="upFile" property="upFile"/>
        <input type="hidden" name="numVersion" value="<%=doc.getNumVersion()%>"/>
        <input type="hidden" name="idCheckOut" value="<%=doc.getIdCheckOut()%>"/>
        <input type="hidden" name="target" value=""/>
        <input type="hidden" name="nameDocReason" value=""/>
        <input type="hidden" name="ownerReason" value=""/>
        <!-- TODO: CAMBIO PARA DOBLE VERSION -->
        <input type="hidden" name="nombreArchivo" value="<%=doc.getNameFile()%>" />
        <table align=center border=0 width="100%">
            <tr>
                <td colspan="2" class="pagesTitle">
                    <%=rb.getString("btn.checkIn")%>
                </td>
            </tr>
            <tr>
                <td colspan="2" valign="top">
                    <table class="clsTableTitle" width="100%" cellSpacing=0 cellPadding=2 align=center border=0>
                        <tbody>
                            <tr>
                                <td class="td_title_bc">
                                    <%=rb.getString("cbs.location") + ": " + HandlerStruct.getRout(doc.getNameDocument(),doc.getIdNode(),doc.getIdNode())%>
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
                <td height="26" class="titleLeft" style="background: url(img/btn140.png); background-repeat: no-repeat" width="14%" valign="middle">
                    <%=rb.getString("cbs.name")%>:
                </td>
                <td class="td_gris_l">
                	<input type="hidden" name="nameDocument" value="<%=doc.getNameDocument()%>"/>
                	<span id="lblNameDocument">
                    <%=doc.getNameDocument()%>
                    </span>
                </td>
            </tr>
            <tr>
                <td height="26" class="titleLeft" style="background: url(img/btn140.png); background-repeat: no-repeat" valign="middle">
                    <%=rb.getString("doc.Ver")%>:
                </td>
                <td class="td_gris_l">
                    <%=doc.getVersion()%>
                </td>
            </tr>

            <tr>
                <td height="26" class="titleLeft" style="background: url(img/btn140.png); background-repeat: no-repeat" valign="middle">
                    <%=rb.getString("doc.type")%>:
                </td>
                <td class="td_gris_l">
                    <%=doc.getTypeDocument()%>
                </td>
            </tr>
            <tr>
                <td height="26" class="titleLeft" style="background: url(img/btn140.png); background-repeat: no-repeat" valign="middle">
                    <%=rb.getString("doc.number")%>:
                </td>
                <td class="td_gris_l">
                    <%=doc.getNumber()%>
                </td>
            </tr>
            <tr>
                <td height="26" class="titleLeft" style="background: url(img/btn140.png); background-repeat: no-repeat" valign="middle">
                    <%=rb.getString("doc.checkOut")%>:
                </td>
                <td class="td_gris_l">
                    <%=doc.getDateCheckOut()%>
                </td>
            </tr>
            <tr>
                <td height="44" class="titleLeft" style="background: url(img/btn120x2.png); background-repeat: no-repeat" valign="middle">
                    <%=rb.getString("chk.changeVer")%>:
                </td>
                <td class="td_gris_l">
                     <input type="hidden" name="changeMinorVersion" value="<%=doc.getChangeMinorVersion()!=null?doc.getChangeMinorVersion():"1"%>">
                    <%=ToolsHTML.showCheckBox("changeVerI","updateCheck(this.form.changeVerI,this.form.changeMinorVersion)",
                                              Integer.parseInt(doc.getChangeMinorVersion()),0,doc.getChangeMinorVersion().equalsIgnoreCase("0"))%>
                </td>
            </tr>
            <tr>
                <td height="26" class="titleLeft" style="background: url(img/btn140.png); background-repeat: no-repeat" valign="middle">
                    <%=rb.getString("doc.expires")%>:
                </td>
                <td class="td_gris_l">
                    <%=ToolsHTML.showCheckBox("expiresI","updateCheck(this.form.expiresI,this.form.expires,this.form.dateExpires)",Integer.parseInt(doc.getExpires()),0,doc.getExpires().equalsIgnoreCase("0"))%>
                    <input type="hidden" name="expires" value="<%=doc.getExpires()%>"/><%=rb.getString("wf.dateDoc")%>:<html:text property="dateExpires" readonly="true" maxlength="10" size="12" value="<%=doc.getDateExpires()%>" />
					<span id="calendario"></span>
	                <a href="#" onclick='showCalendar(document.newDocument.dateExpires,"calendario")' ><img src="images/calendario2.gif" border="1" ></a>
                    <logic:equal value="1" name="doc" property="expires">
                        <input type=button onclick='newDocument.dateExpires.value=""; newDocument.initialDay.value="";  newDocument.initialMonth.value="";  newDocument.initialYear.value=""; ' value='<%=rb.getString("btn.clear")%>' class="boton">
                    </logic:equal>
                    <input type=hidden name="initialYear" value='<%= ((request.getParameter("initialYear") != null) ? request.getParameter("initialYear") : "")%>'>
                    <input type=hidden name="initialMonth" value='<%= ((request.getParameter("initialMonth") != null) ? request.getParameter("initialMonth") : "")%>'>
                    <input type=hidden name="initialDay" value='<%= ((request.getParameter("initialDay") != null) ? request.getParameter("initialDay") : "")%>'>
                </td>
            </tr>
            <%
                if ("0".equalsIgnoreCase(DesigeConf.getProperty("serverType"))) {
            %>
            <tr id="botonEditar">
                <td class="td_gris_l" colspan="2" valign="middle">
						&nbsp;
                    <center>
                    <%if(isEditable) {%>
	                    <% if (!ToolsHTML.abrirEditorWebStart((HttpServletRequest)request)) { %>
							<a  class="boton botonLink" id="lnkEditar" name="lnkEditar"  onClick="javascript:this.style.display='none';editDocumento(<%=doc.getIdCheckOut()%>,'<%=doc.getNameFile()%>',<%=doc.getIdDocument()%>,<%=doc.getNumVersion()%>);" >
	                            <logic:equal name="checkInDoc" property="idTypeDoc" value="<%=regID%>" scope="session">
	                                <%=rb.getString("wf.editRegister")%>
	                            </logic:equal>
	                            <logic:notEqual name="checkInDoc" property="idTypeDoc" value="<%=regID%>" scope="session">
	                                <%=rb.getString("wf.editDoc")%>
	                            </logic:notEqual>
							</a>
						<%}else {%>
						    <%if( doc.getNameFile().equals("EnLinea.html") ) {%>
	                            <logic:notEqual name="checkInDoc" property="idTypeDoc" value="<%=regID%>" scope="session">
									<a  class="boton botonLink" id="lnkEditar" name="lnkEditar"  onClick="javascript:this.style.display='none';editDocumento(<%=doc.getIdCheckOut()%>,'<%=doc.getNameFile()%>',<%=doc.getIdDocument()%>,<%=doc.getNumVersion()%>);" >
	    	                            <%=rb.getString("wf.editDoc")%>
	    	                        </a>
	                            </logic:notEqual>
						    <%} else {%>
								<a  class="boton botonLink" id="lnkEditar" name="lnkEditar"  title="Haga click en abrir cuando el navegador se lo pregunte."  onClick="this.style.display='none';abrir(this)" >
										<%=rb.getString("wf.editDoc")%>
								</a>
							<%}%>
						<%} %>
					<%} else {%>
						<a  class="boton botonLink" id="lnkEditar" name="lnkEditar"  title="Haga click en abrir cuando el navegador se lo pregunte."  onClick="javascript:this.style.display='none';cargarDocumento(<%=doc.getIdCheckOut()%>,'<%=doc.getNameFile()%>',<%=doc.getIdDocument()%>,<%=doc.getNumVersion()%>);" >
								<%=rb.getString("wf.editDoc")%>
						</a>
					<%}%>
                    </center>
					<br/>
                </td>
            </tr>
            <html:hidden property="nameFile" value="intranet"/>
            <%
                } else {
            %>
                <tr>
                    <td height="26" class="titleLeft" style="background: url(img/btn140.png); background-repeat: no-repeat" valign="middle">
                        <%=rb.getString("doc.upFile")%>:
                    </td>
                    <td class="td_gris_l">
                        <html:file property="nameFile" styleClass="classText"/>
                    </td>
                </tr>
             <%
                }
             %>
            <textarea name="comments" style="display:none">
                <%=doc.getComments()%>
            </textarea>
            <tr>
                <td colspan="2" heigth="22" class="titleCenter" style="background: url(img/BgComments.gif); background-repeat: no-repeat" valign="middle">
                    <%=rb.getString("wf.razondelcambio")%>
                    
                </td>
            </tr>
            <tr>
                <td class="fondoEditor" width="100%" colspan="2" >
					  <jsp:include page="richedit.jsp">
						<jsp:param name="richedit" value="richedit"/>
						<jsp:param name="defaultValue" value="${beforeComments}"/>
						<jsp:param name="deserialize" value="${deserialize}"/>
					  </jsp:include>
                </td>
            </tr>
            <tr>
                 <td  colspan="2" valign="top" >
                 	&nbsp;
                 </td>
            </tr>            
            <tr id="paralelo" style="display:none">
                <td height="26" class="titleLeft" style="background: url(img/btn140.png); background-repeat: no-repeat" valign="middle">
                    <%=rb.getString("doc.upFile2")%>:
                </td>
                <td class="td_gris_l">
                    <html:file property="nameFileParalelo" styleClass="classText" style="width:300"/>
                </td>
            </tr>            
            <tr id="botonera">
                <td colspan="2" align="center">
					<a id="btnOK" class="boton botonLinkBack" id="lnkEditar" name="lnkEditar" onClick="javascript:salvar(document.newDocument)" >
                        <%=rb.getString("btn.savecheckin")%>
					</a>
                   <!--  
                       &nbsp;
					<a id="btnCancel" class="boton botonLinkBack" id="lnkEditar" name="lnkEditar"  onClick="javascript:cancelar()" >
                        <%=rb.getString("btn.save")%>
					</a>
					    -->
					   &nbsp;
					
               		<input type="button" class="boton" id="btnBack" name="btnBack" value="<%=rb.getString("btn.back")%>" onClick="javascript:history.back();" />
                </td>
            </tr>
            <tr id="botoneraMensaje" style="display:none;">
            	<td align="center" colspan="2"><b><%=rb.getString("wf.mensajeEspera")%></b>
            	</td>
            </tr>
        </table>
    </html:form>
</logic:present>
<form name="editor" >
</form>
</body>
</html>
<script language="javascript">
requiereView(document.newDocument);
</script>
