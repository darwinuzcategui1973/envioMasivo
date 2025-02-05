<%@ page import="java.util.ResourceBundle,com.desige.webDocuments.utils.ToolsHTML" %>
<%@ page language="java" %>

<jsp:include page="richeditDocType.jsp" /> 
<!--/**
 * Title: upploadFile.jsp <br>
 * Copyright: (c) 2004 Desige Servicios de Computación<br/>
 *
 * @author Ing. Nelson Crespo   (NC)
 * @author Ing. Simón Rodriguéz (SR)
 * @version WebDocuments v1.0
 * <br>
 *     Changes:<br>
 * <ul>
 *      <li> 25/04/2006 (SR) Se sustituyo /WebDocuments/.. por ./..</li>
 *      <li> 08/05/2006 (SR) Se valido los botones para cuando se pulse aceptar, se invaliden en upploadFile.jsp</li>
 *      <li> 14/07/2006 (NC) Cambios para el correcto manejo de la Versión según el estatu del Documento </li>
 * <ul>
 */-->
<%@ page import="java.util.ResourceBundle,
                 com.desige.webDocuments.utils.ToolsHTML,
                 com.desige.webDocuments.utils.beans.SuperActionForm,
                 com.desige.webDocuments.persistent.managers.HandlerParameters,
                 java.util.Hashtable,
                 com.desige.webDocuments.persistent.managers.HandlerBD,
                 com.desige.webDocuments.structured.forms.BaseStructForm"%>
<%@ page import="com.desige.webDocuments.document.forms.BaseDocumentForm"%>
<%@ page import="com.desige.webDocuments.utils.DesigeConf"%>

<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<%@ page language="java" %>
<%
    String docPublic = "1";
%>
<logic:equal value="0" name="newDocument" property="docPublic">
    <%
        docPublic = "0";
    %>
</logic:equal>
<%
    ResourceBundle rb = ToolsHTML.getBundle(request);
    String cmd = (String)session.getAttribute("cmd");
    if (cmd==null){
        cmd = SuperActionForm.cmdLoad;
    }
    String nodeActive = (String)session.getAttribute("nodeActive");
    boolean swborradorCorrelativo=false;
    if (session.getAttribute("borradorCorrelativo")!=null){
           swborradorCorrelativo="1".equalsIgnoreCase((String)session.getAttribute("borradorCorrelativo"));
    }
    pageContext.setAttribute("cmd",cmd);
    Hashtable tree = (Hashtable)session.getAttribute("tree");
    BaseStructForm dataForm = (BaseStructForm)tree.get(nodeActive);
    String rout = dataForm.getRout();
    boolean validDatePublic = false;
    String dateSystem = ToolsHTML.date.format(new java.util.Date());       
    String dateExpiresMax = ToolsHTML.calculateDateExpires(dateSystem,docPublic,"0");
    //System.out.println("[upploadFile] dateExpiresMax = " + dateExpiresMax);
    boolean readOnly=false;
%>
<html>
<head>
<title><%=rb.getString("principal.title")%></title>
<jsp:include page="meta.jsp" /> 
<link href="estilo/estilo.css" rel="stylesheet" type="text/css" />

<script type="text/javascript" src="./estilo/popcalendar.js"></script>
<script type="text/javascript" src="./estilo/fechas.js"></script>

<jsp:include page="richeditHead.jsp" /> 

<script type="text/javascript">
    var docLinks;

    function validarFechaAprobacion(valor) {
        if (!validarFecha(valor)){
            alert('<%=rb.getString("err.badDateEndWF")%>');
            return false;
        }
        if (valor > '<%=dateSystem%>') {
            alert('<%=rb.getString("err.badDateEndWF")%>');
            return false;
        }
        return true;
    }

    function validarFechaExpiracion(valor) {
        if (!validarFecha(valor)) {
            alert('<%=rb.getString("err.badDateExpiresDoc")%>');
            return false;
        }

        if (valor < '<%=dateSystem%>') {
            alert('<%=rb.getString("err.badDateExpiresDoc")%>');
            return false;
        }
        <%
            if (dateExpiresMax!=null) {
        %>
                if (valor > '<%=dateExpiresMax%>') {
                    alert('<%=rb.getString("err.badDateExpiresDocI")%>');
                }
        <%
            }
        %>
        return true;
    }
 //salva documentos que no estan en linea inicio
    function validar(forma,validate,typeMay,typeMin){
          //chequeamos si el borrador lleva correlativo
            <%if (swborradorCorrelativo){%>
                 //verificamos si el campo numero correlativo tiene numero porque se exige el numero
                 //si es borrador y en propiedades de localidad se pide colocar numero
                  if (forma.number.value.length<=0){
                      alert('<%=rb.getString("doc.numerborrador")%>');
                      forma.number.focus();
                      return false;
                  }
            <%}%>
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
        if (forma.expiresI) { //Primero Verifico que el Check Existe en el Formulario
            //Si hay una fecha tipeada por el usuario
             //se Verifica si el Check está seleccionado
             if (forma.dateExpires.value.length > 0) {
                 forma.expiresI.checked = true;
                 forma.expires.value = "0";
             }
            if (forma.expiresI.checked) { //Si esta Seleccionado
                if (forma.approvedI.checked) {
                    if (!validarFechaExpiracion(forma.dateExpires.value)) {
                        return false;
                    }
                } else {
                    alert('<%=rb.getString("err.mustSelectExpire")%>');
                    return false;
                }
            }
        }
       
        if (forma.nameFile.value.length == 0) {
            alert('<%=rb.getString("err.notFile")%>');
            return false;
        }
        
        if (forma.mayorVer) {
            if (!valVersion(forma.mayorVer.value,typeMay,'<%=rb.getString("E0114")%>')) {
                return false;
            }
        }
        
        if (forma.minorVer) {
            if (!valVersion(forma.minorVer.value,typeMin,'<%=rb.getString("E0115")%>',false)) {
                return false;
            }
        }
        
        return true;
    }

    function valVersion(valor,type,msg,isMayor) {
    	isMayor = (isMayor==undefined?true:isMayor);
        if (type == 0) {
            if (!(/^([0-9])*$/.test(valor))) {
                alert(msg);
                return false;
            }
        } else {
            if (!(/^([A-Z])*$/.test(valor))) {
                alert(msg);
                return false;
            }
            if (isMayor) {
	            if (!valor || valor=="" || valor.replace(/ /g,"").length==0) {
	                alert(msg);
	                return true;
	            }
            }
        }
        return true;
    }
    
    function validarExtensionDoble(forma) {
    	if(forma.nameFile.value.length==0) {
    		return false;
    	}
    	cad = forma.nameFile.value;
    	if(isDoblePunto(cad)) {
    		alert("<%=rb.getString("err.isNotValidFileExt")%>");
    		return false;
    	}
    	while(cad.indexOf(".")!=-1) {
    		cad = cad.substring(cad.indexOf(".")+1);
    	}
    	cad = "."+cad;
    	
   		document.getElementById("paralelo").style.display="none";
    	if(extensionDouble.toLowerCase().indexOf(cad.toLowerCase())!=-1 && forma.nameFileParalelo.value.length==0 ) {
    		alert("<%=rb.getString("err.insertViewFile")%>");
    		document.getElementById("paralelo").style.display="";
    		return false;
    	}
		if(extensionDouble.indexOf(cad)!=-1) {
    		cad = forma.nameFileParalelo.value;
    		if(isDoblePunto(cad)) {
	    		alert("<%=rb.getString("err.isNotValidFileExt")%>");
	    		document.getElementById("paralelo").style.display="";
    			return false;
	    	}
	    }
    	
    	return true;
    }
    
    function isDoblePunto(name) {
    	name = name.replace(/\\/g,"/");
    	var arr = name.split("/");
    	name = arr[arr.length-1];
    	var nPunto = 0;
    	while(name.indexOf(".")!=-1 && nPunto<2) {
    		nPunto++;
    		name = name.substring(name.indexOf(".")+1);
    	}
		return (nPunto!=1);
    }
    
    function salvar(formulario,validate,typeMay,typeMin) {
    	if (!validarExtensionDoble(formulario)) { 
    		return;
    	}

    	
    	
        formulario.action = "servletloaddocuments?cmd=upFile";
        
        updateRTEs();
        if (validar(formulario,validate,typeMay,typeMin)) {
            formulario.comments.value = formulario.richedit.value;
            formulario.btnsalvar.style.display="none";
            document.getElementById("btncancelar").style.display="none";
            formulario.submit();
        }
    }
//salva documentos que no estan en linea fin

//salva documentos que estan en linea inicio
 <!--simon inicio 13 de junio 2005 -->
    function validar1(forma,validate,typeMay,typeMin) {
      //chequeamos si el borrador lleva correlativo
            <%if (swborradorCorrelativo){%>
           //verificamos si el campo numero correlativo tiene numero porque se exige el numero
           //si es borrador y en propiedades de localidad se pide colocar numero
            if (forma.number.value.length<=0){
                alert('<%=rb.getString("doc.numerborrador")%>');
                forma.number.focus();
                return false;
            }
            <%}%>
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

         if (forma.expiresI) { //Primero Verifico que el Check Existe en el Formulario
             //Si hay una fecha tipeada por el usuario
             //se Verifica si el Check está seleccionado
             if (forma.dateExpires.value.length > 0) {
                 forma.expiresI.checked = true;
                 forma.expires.value = "0";
             }
             if (!forma.expiresI.checked) {
                 alert('<%=rb.getString("err.badDateExpiresDoc")%>');
                 return false;
             }
             if (forma.expiresI.checked) { //Si esta Seleccionado
                if (forma.approvedI.checked){
                     if (!validarFechaExpiracion(forma.dateExpires.value)){
                         return false;
                     }
               } else {
                    alert('<%=rb.getString("err.mustSelectExpire")%>');
                    return false;
                }
             }
         }
        if (forma.mayorVer) {
            if (!valVersion(forma.mayorVer.value,typeMay,'<%=rb.getString("E0114")%>')) {
                return false;
            }
        }

        if (forma.minorVer) {
            if (!valVersion(forma.minorVer.value,typeMin,'<%=rb.getString("E0115")%>')) {
                return false;
            }
        }
        
       if (isEmptyRicheditValue(forma.richedit.value)) {
          alert('<%=rb.getString("E0126")%>');
          return false;
       }
        
        return true;
     }

     function salvar1(formulario,validate,typeMay,typeMin){
        formulario.action = "servletloaddocuments?cmd=upFile";
        
        updateRTEs();
        if (validar1(formulario,validate,typeMay,typeMin)) {
            formulario.comments.value = formulario.richedit.value;
          // alert("archivo en linea:"+formulario.action);
            formulario.btnsalvar.disabled=true;
            document.getElementById("btncancelar").style.disabled=true;
            formulario.submit();
        }
    }

 <!--simon fin 13 de junio 2005 -->
//salva documentos que  estan en linea fin


    function getDateExpire(dateField,nameForm,dateValue,text){
    	if(document.all) {
        	window.open("getDate.jsp?field=" + dateField + "&nameForm="+nameForm+"&valueForm="+dateValue+"&text="+text,"","width=280,height=170,resizable=no,scrollbars=yes,left=300,top=250");
        } else {
        	window.open("getDate.jsp?field=" + dateField + "&nameForm="+nameForm+"&valueForm="+dateValue+"&text="+text,"","width=260,height=170,resizable=no,scrollbars=yes,left=300,top=250");
        }
    }

    function showWindow(pages,input,nameForm,value,width,height){
        var hWnd = null;
        hWnd = window.open(pages+"?input="+input+"&nameForma="+nameForm+"&value="+value,"","width="+width+",height="+height+",resizable=no,scrollbars=yes,statusbar=yes,left=250,top=250");
        if ((document.window != null) && (!hWnd.opener)){
            hWnd.opener = document.window;
        }
    }

    function updateCheck(check,field,texto) {
        if (check.checked) {
            field.value = "0";
            texto.readOnly = false;
            texto.focus();
        } else{
            texto.readOnly = true;
            field.value = 1;
        }
    }
    function cancelar(){
    	var formaSelection = document.getElementById("Selection");
        formaSelection.submit();
    }
    window.onmove="alert(1)";
    
    function regresar(){
		location.href="newDocument.jsp?publicado=<%=docPublic%>";
    }
</script>

<script type="text/javascript">
	var extensionDouble="";
	<%
	try {
		// validar o no el navegador - para este caso ie7
		String valor = HandlerParameters.PARAMETROS.getFileDoubleVersion();
		out.println("extensionDouble=\"".concat(valor==null?"":valor.toLowerCase()).concat("\";"));
	} catch (Exception e) {
		e.printStackTrace();
	}
	%>
</script>

</head>

<body class="bodyInternas" >

<form name="Selection" id="Selection" action="loadStructMain.do">
    <input type="hidden" name="cmd" property="<%=SuperActionForm.cmdLoad%>"/>
    <input type="hidden" name="idNodeSelected" value="<%=nodeActive%>"/>
</form>

<html:form action="/upploadFile.do" method="post" enctype="multipart/form-data">
    <input type="hidden" name="cmd" property="upFile"/>
    <table align="center" border="0" width="100%">
        <tr>
            <td class="pagesTitle" colspan="2">
                <%=rb.getString("btn.addDocument")%>
            </td>
        </tr>
        <tr>
            <td colspan="2" valign="top">
                <%
                    String style = "clsTableTitle";
                    String height = "21";
                    if (rout!=null&&rout.length() > 104) {
                        style = "clsTableTitle2";
                        height = "42";
                    }
                %>
                <table class="<%=style%>" width="100%" cellSpacing=0 cellPadding=2 align=center border=0>
                    <tbody>
                        <tr>
                            <td class="td_title_bc" height="<%=height%>">
                                <%=rb.getString("cbs.location") + ": "+rout%>
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
            <td colspan="2" heigth="26" class="titleCenter" style="background: url(img/BgComments.gif); background-repeat: no-repeat;width:500" valign="middle">
                <%=rb.getString("doc.title")%>
            </td>
        </tr>
        <tr>
            <td height="26" class="titleLeft" style="background: url(img/btn140.png); background-repeat: no-repeat" width="18%" valign="middle">
                <%=rb.getString("cbs.name")%>:
            </td>
            <td class="td_gris_l">
                <bean:write name="newDocument" property="nameDocument"/>
            </td>
        </tr>
        <%
            byte typeMay = 0;
            byte typeMin = 0;
            BaseDocumentForm forma = (BaseDocumentForm)session.getAttribute("newDocument");
        %>
            <%
                validDatePublic = true;
                if (ToolsHTML.isNumeric(forma.getMayorVer())) {
                    try {
                        forma.setMayorVer(DesigeConf.getProperty("begin.VersionApproved"));
                    } catch (Exception ex) {
                        forma.setMayorVer("1");
                    }
//                    forma.setMayorVer("1");
                } else {
                    forma.setMayorVer("A");
                    typeMay = 1;
                }
                if (ToolsHTML.isNumeric(forma.getMinorVer())) {
                    forma.setMinorVer("0");
                } else {
                    forma.setMinorVer("");
                    typeMin = 1;
                }
            %>
        <tr>
            <td height="26" class="titleLeft" style="background: url(img/btn140.png); background-repeat: no-repeat" valign="middle">
                <%=rb.getString("doc.Ver")%>:
            </td>
            <td class="td_gris_l">
                <logic:equal value="" name="newDocument" property="minorVer">
                    <%--<html:text property="mayorVer" size="4" styleClass="classText" onchange="valVersion(this.value,<%=typeMay%>,'La versión mayor es inválida')"/>.<html:text property="minorVer" size="4" styleClass="classText" readonly="true" onchange="valVersion(this.value,'<%=typeMin%>','La versión menor es inválida')"/>--%>
                    <input type="text" name="mayorVer" size="4" value="<%=forma.getMayorVer()%>" onchange="valVersion(this.value,'<%=typeMay%>','<%=rb.getString("E0114")%>')" class="classText">.<input type="text" name="minorVer" readonly="true" size="4" value="<%=forma.getMinorVer()%>" onchange="valVersion(this.value,'<%=typeMin%>','<%=rb.getString("E0115")%>')" class="classText">
                </logic:equal>
                <logic:notEqual value="" name="newDocument" property="minorVer">
                    <%--<html:text property="mayorVer" size="4" styleClass="classText" onchange="valVersion(this.value,'<%=typeMay%>','La versión mayor es inválida')"/>.<html:text property="minorVer" size="4" styleClass="classText" onchange="valVersion(this.value,'<%=typeMin%>','La versión menor es inválida')"/>--%>
                    <input type="text" name="mayorVer" size="4" value="<%=forma.getMayorVer()%>" onchange="valVersion(this.value,'<%=typeMay%>','<%=rb.getString("E0114")%>')" class="classText">.<input type="text" name="minorVer" size="4" value="<%=forma.getMinorVer()%>" onchange="valVersion(this.value,'<%=typeMin%>','<%=rb.getString("E0115")%>')" class="classText">
                </logic:notEqual>

            </td>
        </tr>

        <tr>
            <td height="26" class="titleLeft" style="background: url(img/btn140.png); background-repeat: no-repeat" valign="middle">
                <%=rb.getString("doc.type")%>:
            </td>
            <td class="td_gris_l">
                <bean:write name="newDocument" property="descriptTypeDoc"/>
            </td>
        </tr>

        <logic:notEqual value="0" name="newDocument" property="docPublic">
            <logic:present scope="session" name="borradorCorrelativo">
                <logic:equal value="1" name="borradorCorrelativo" scope="session">
                    <%
                        swborradorCorrelativo = true;
                    %>
                  <tr>
                   <td height="26" class="titleLeft" style="background: url(img/btn140.png); background-repeat: no-repeat" valign="middle">
                      <%=rb.getString("doc.number")%>:
                   </td>
                   <td>
                     <html:text property="number"  disabled="false" styleClass="classText"/>
                   </td>
                </tr>
                </logic:equal>
            </logic:present>
        </logic:notEqual>
        <logic:equal value="0" name="newDocument" property="docPublic">
             <!--Genera el link automatico para los documentos, el cero es un uno negado, logica inversa.-->
             <html:hidden property="checkactiveFactory" value="0"/>
            <%
                validDatePublic = true;
            %>
          <tr>
           <td height="26" class="titleLeft" style="background: url(img/btn140.png); background-repeat: no-repeat" valign="middle">
              <%=rb.getString("doc.number")%>:
           </td>
           <td>
             <html:text property="number"  disabled="false" styleClass="classText"/>
           </td>
        </tr>
        <tr>
            <td height="26" class="titleLeft" style="background: url(img/btn140.png); background-repeat: no-repeat" valign="middle">
                <%=rb.getString("doc.approved")%>:
            </td>
            <td class="td_gris_l">

                <%=ToolsHTML.showCheckBox("approvedI","updateCheck(this.form.approvedI,this.form.approved,this.form.dateApproved)",validDatePublic?0:1,0,validDatePublic)%>
                <input type="hidden" name="approved" value="<%=validDatePublic?"0":"1"%>"/><%=rb.getString("wf.dateDoc")%>:<html:text property="dateApproved" readonly="true" maxlength="10" size="12" />
                <input type="button" onclick='getDateExpire("dateApproved",this.form.name,this.form.dateApproved.value,"doc.approved")' value='>>' class="boton" style="width:20px;">
                <%
                    if (validDatePublic) {
                %>
                    <input type=button onclick='document.newDocument.dateApproved.value=""; document.newDocument.initialDay.value="";  document.newDocument.initialMonth.value="";  document.newDocument.initialYear.value="";document.newDocument.approved.value="1";' value='<%=rb.getString("btn.clear")%>' class="boton">
                <%
                    } else {
                %>
                    <input type=button onclick='document.newDocument.dateApproved.value=""; document.newDocument.initialDay.value="";  document.newDocument.initialMonth.value="";  document.newDocument.initialYear.value="";document.newDocument.approved.value="1";document.newDocument.approvedI.checked=false; ' value='<%=rb.getString("btn.clear")%>' class="boton">  
                <%
                    }
                %>
                <input type=hidden name="initialYear" value='<%= ((request.getParameter("initialYear") != null) ? request.getParameter("initialYear") : "")%>'>
                <input type=hidden name="initialMonth" value='<%= ((request.getParameter("initialMonth") != null) ? request.getParameter("initialMonth") : "")%>'>
                <input type=hidden name="initialDay" value='<%= ((request.getParameter("initialDay") != null) ? request.getParameter("initialDay") : "")%>'>
            </td>
        </tr>

        <tr>
            <td height="26" class="titleLeft" style="background: url(img/btn140.png); background-repeat: no-repeat" valign="middle">
                <%=rb.getString("doc.expires")%>:
            </td>
            <td class="td_gris_l">
                <%=ToolsHTML.showCheckBox("expiresI","updateCheck(this.form.expiresI,this.form.expires,this.form.dateExpires)",1,0,false)%>
               <html:hidden property="expires" value="1"/><%=rb.getString("wf.dateDoc")%>:<html:text property="dateExpires" readonly="true" maxlength="10" size="12"/>
                <input type="button" onclick='getDateExpire("dateExpires",this.form.name,this.form.dateExpires.value,"doc.expires")' value='>>' class="boton" style="width:20px;">
                <input type=button onclick='document.newDocument.expiresI.check = false;document.newDocument.dateExpires.value=""; document.newDocument.initialDay.value="";  document.newDocument.initialMonth.value="";  document.newDocument.initialYear.value="";document.newDocument.expires.value="";document.newDocument.expiresI.checked=false; ' value='<%=rb.getString("btn.clear")%>' class="boton">
            </td>
        </tr>
     </logic:equal>
        <tr>
            <!-- SIMOPN INICIO 13 DE JUNIO 2005 -->
            <logic:notEqual value="0" name="newDocument" property="docOnline">
                <td height="26" class="titleLeft" style="background: url(img/btn140.png); background-repeat: no-repeat" valign="middle">
                    <%=rb.getString("doc.upFile")%>:
                </td>
                <td class="td_gris_l">
                    <html:file property="nameFile" styleClass="classText" style="width:300"/>
                </td>
            </logic:notEqual>
            <!-- SIMON 13 DE JUNIO 2005 FIN -->
        </tr>

        <tr id="paralelo" style="display:none">
			<!-- INI: Permite cargar el archivo alterno para ser visualizado -->
            <logic:notEqual value="0" name="newDocument" property="docOnline">
                <td height="26" class="titleLeft" style="background: url(img/btn140.png); background-repeat: no-repeat" valign="middle">
                    <%=rb.getString("doc.upFile2")%>:
                </td>
                <td class="td_gris_l">
                    <html:file property="nameFileParalelo" styleClass="classText" style="width:300"/>
                </td>
            </logic:notEqual>
			<!-- FIN: Permite cargar el archivo alterno para ser visualizado -->
        </tr>
        
        <html:hidden property="docRelations"/>
        <tr>
            <td height="44" class="titleLeft" style="background: url(img/btn120x2.png); background-repeat: no-repeat" valign="middle">
                <%=rb.getString("doc.links")%>:
             </td>
            <td>
                <a href="javascript:showWindow('loadAllDocuments.do','docRelations','newDocument',document.newDocument.docRelations.value,500,400);" class="ahref_b" target="_self">
                    <%=rb.getString("doc.links")%>
                </a>
            </td>
        </tr>
        <%--<%if (validDatePublic){%>--%>
        <!--<tr>-->
              <!--<td class="titleLeft" height="26" style="background: url(img/btn140.png); background-repeat: no-repeat" valign="middle">-->
                 <%--<%=rb.getString("wonderware.activefactory")%>--%>
              <!--</td>-->
              <!--<td class="td_gris_l">-->
                  <%--<%=ToolsHTML.showCheckBox("checkactiveFactoryI","updateCheck(this.form.checkactiveFactoryI,this.form.checkactiveFactory,this.form.checkactiveFactory)",!validDatePublic?0:1,0,readOnly)%>--%>
                  <%--<html:hidden property="checkactiveFactory"/>--%>
                 <%----%>
<%----%>
              <!--</td>-->
        <!--</tr>-->
        <%--<%}%>--%>

        <html:textarea property="comments" style="display:none"/>
        <tr>
            <td colspan="2" heigth="26" class="titleCenter" style="background: url(img/BgComments.gif); background-repeat: no-repeat">
                <%=rb.getString("wf.comments")%>
            </td>
        </tr>

        <tr>
            <td class="fondoEditor" width="100%" colspan="2">
                <jsp:include page="richedit.jsp" > 
                	<jsp:param name="richedit" value="richedit"/>
                </jsp:include>
            </td>
        </tr>
        <script language="JavaScript" event="onload" for="window">
        	document.newDocument.richedit.value = document.newDocument.comments.innerText;
        </script>
        <tr>
            <td colspan="2" align="center">
               <!-- SIMOPN INICIO 13 DE JUNIO 2005 -->
               <logic:notEqual name="newDocument" property="docOnline" value="0">
                  <input type="button" class="boton" value="<%=rb.getString("btn.save")%>" name="btnsalvar" onClick="javascript:salvar(this.form,<%=validDatePublic%>,<%=typeMay%>,<%=typeMin%>);" />
               </logic:notEqual>
               <logic:equal name="newDocument" property="docOnline" value="0">
                  <input type="button" class="boton" value="<%=rb.getString("btn.save")%>" name="btnsalvar" onClick="javascript:salvar1(this.form,<%=validDatePublic%>,<%=typeMay%>,<%=typeMin%>);" />
               </logic:equal>
                <logic:notEqual name="cmd" value="<%=SuperActionForm.cmdInsert%>">
                    <html:hidden property="idNodeParent" value="<%=dataForm.getIdNodeParent()%>"/>
                    &nbsp;
                </logic:notEqual>
                    &nbsp;
                  <input type="button" class="boton" value="<%=rb.getString("btn.back")%>" name="btnregresar" onClick="javascript:regresar();" />
                    &nbsp;
				<a id="btncancelar" class="boton botonLinkBack" onClick="cancelar()" >
					<%=rb.getString("btn.cancel")%>
				</a>
            </td>
        </tr>
    </table>
</html:form>
</body>
</html>
