<jsp:include page="richeditDocType.jsp" /> 

<!-- /**
 * Title: parametersCopy.jsp<br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * @author Ing. Nelson Crespo (NC)
 * @author Ing. Simon Rodríguez (SR)
 * @version WebDocuments v3.0
 * <br/>
 * Changes:<br/>
 * <ul>
 *      <li> 25/05/2005 Se agrego parametros de revision, aprobacion, y cancelados(SR) </li>
 *      <li> 29/05/2009 Se eliminó el uso del parámetro typePrefix                     </li>
 *      <li> 15/06/2005 Se agregaron los cambios para manipular el campo NumByLocation </li>
 *      <li> 22/07/2005 Se agregaron mensaje msgDocExpirado (SR)                       </li>
 *      <li> 25/04/2006 (SR) Se sustituyo /WebDocuments/.. por ./..
 *      <li> 25/04/2006 (SR) quitar opcion de contraseñas duplicadas,tiempo session variable </li>
 *      <li> 14/06/2007 (YS) Se agrega opción para señalar si se desean ver o no revisores cuando se imprime un documento aprobado </li>
 *      <li> 02/07/2007 (YS) Se agrega opción para señalar si se desean reasignar Usuario al cancelar un Flujo de Trabajo Paramétrico </li>
 *      <li> 20/07/2007 (YS) Se agrega opción para señalar si se desea Permitir descarga de última Versión </li>
 *      <li> 01/08/2007 (YS) Se agrega opción para elegir valores de impresion </li>
 *      <li> 15/10/2007 (YS) Se obtiene email del remitente, para notificaciones del sistema </li>
 *      <li> 23/11/2007 (YS) Se obtiene tiempo se expiracion para edicion de documentos </li>
   </ul>
 */ -->
<%@ page import="java.util.ResourceBundle,
                 com.desige.webDocuments.utils.ToolsHTML,
                 com.desige.webDocuments.utils.DesigeConf"%>

<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<%@ page language="java" %>
<%
    ResourceBundle rb = ToolsHTML.getBundle(request);
    StringBuffer onLoad = new StringBuffer("");
    String ok = (String)session.getAttribute("usuario");
    String info = (String)request.getAttribute("info");
    if (ToolsHTML.checkValue(ok)) {
        onLoad.append(" onLoad=\"self.status='").append(rb.getString("pagin.welcome")).append(" ");
        onLoad.append(ok).append("'");
    } else{// El Usuario No se ha Logeado.... => Lo reboto...
        response.sendRedirect(rb.getString("href.logout"));
    }
    if (ToolsHTML.checkValue(info)){
        onLoad.append(";alert('").append(info).append("')");
    }
    if (onLoad.length()>0){
        onLoad.append("\"");
    }
    //ToolsHTML.clearSession(session,"application.parameters");
%>
<html>
<head>
<title><%=rb.getString("principal.title")%></title>
<jsp:include page="meta.jsp" /> 
<link href="estilo/estilo.css" rel="stylesheet" type="text/css">
<jsp:include page="richeditHead.jsp" /> 

<script language="JavaScript">
    function botones(forma) {
        if (forma.btnOK) {
            forma.btnOK.disabled = true;
        }
        if (forma.btnCancel) {
            forma.btnCancel.disabled = true;
        }
    }

	function validar() {
        if (document.login.docSearch.value.length>0) {
            alert('<%=rb.getString("err.notDocumentSearch")%>');
        }else{
		    document.login.submit();
        }
	}

    function salvar(forma) {
        if(document.loadParameters.fileNotOpenSave.value==0) {
        	alert('<%=rb.getString("param.warningFileNotOpenSave")%>');
        }
    
        if (!(parseInt(forma.endSession.value))) {
            alert('<%=rb.getString("param.timeSession")%>'+' '+'<%=rb.getString("param.timeSession2")%>');
            forma.endSession.value='<%=rb.getString("param.timeSession2")%>';
            return false;
        }
        if (!(parseInt(forma.endSessionEdit.value))) {
            alert('<%=rb.getString("param.timeSessionEdit")%>'+' '+'<%=rb.getString("param.timeSessionEdit2")%>');
            forma.endSessionEdit.value='<%=rb.getString("param.timeSessionEdit2")%>';
            return false;
        }
        if (forma.smtpMail.value.length == 0) {
            alert('<%=rb.getString("param.notSMPT")%>');
            return false;
        }
        if (forma.mailAccount.value.length == 0) {
            alert('<%=rb.getString("param.notMailAccount")%>');
            return false;
        }
        if (!(/^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/.test(forma.mailAccount.value))) {
            alert('<%=rb.getString("param.notValidMailAccount")%>');
            return false;
        }
        
        var i='0';
        i= forma.endSession.value-'<%=rb.getString("param.timeSession2")%>';
        if (i>='0')  {
            botones(forma);
            updateRTEs();
            forma.msgDocExpirado.value = forma.richeditdocexpirado.value;
            forma.msgWFExpires.value = forma.richedit.value;
            forma.msgWFRevision.value = forma.richeditrev.value;
            forma.msgWFAprobados.value = forma.richeditaprob.value;
            forma.msgWFCancelados.value = forma.richeditcancel.value;
            forma.msgWFBorrador.value = forma.richeditborrador.value;
            forma.submit();
        } else {
            alert('<%=rb.getString("param.timeSession")%>'+' '+'<%=rb.getString("param.timeSession2")%>');
            forma.endSession.value='<%=rb.getString("param.timeSession2")%>';
        }
    }

    function cancelar(form) {
        botones(form);
        location.href = "administracionMain.do";
    }

    function editField(pages,input,value,forma) {
        var hWnd = null;
        hWnd = window.open(pages+"?input="+input+"&value="+value+"&nameForma="+forma.name,"Catalogo","width=400,height=300,resizable=yes,scrollbars=yes,left=300,top=250");
        if ((document.window != null) && (!hWnd.opener)) {
            hWnd.opener = document.window;
        }
    }

    function updateCheck(check,field) {
        if (check.checked){
            field.value = "0";
        } else{
            field.value = 1;
        }
    }

    function updateCheckReverse(check,field) {
        if (check.checked){
            field.value = 1;
        } else{
            field.value = "0";
        }
    }

    function refreshButton(forma) {
        forma.lengthDocNumber.disabled = !forma.docNumberGenerator[1].checked;
        forma.resetDocNumber.disabled = !forma.docNumberGenerator[1].checked;

        if (forma.docNumberGenerator[1].checked) {
            forma.lengthDocNumber.focus();
        }
    }
</script>
</head>

<body class="bodyInternas" <%=onLoad.toString()%>>
<a name="inicio">&nbsp;</a>
<table cellPadding=2 align=center border=0 width="100%">
    <tr>
        <td colspan="2" class="pagesTitle">
            <%=rb.getString("param.title")%>
        </td>
    </tr>

    <html:form action="saveParameters">
      <tr>
          <td colspan="2" align="center" valign="top">
              <logic:equal value='<%=DesigeConf.getProperty("application.admon")%>' name='user' property='idGroup'>
                  <input type="button" class="boton" value="<%=rb.getString("btn.save")%>" onClick="javascript:salvar(this.form);" name="btnOK"/>
                  &nbsp;
              </logic:equal>
              <input type="button" class="boton" value="<%=rb.getString("btn.cancel")%>" onClick="javascript:cancelar(this.form);" name="btnCancel"/>
          </td>
      </tr>
      <tr>
          <td colspan="2" align="center" height="3">
          </td>
      </tr>
                    
      <tr>
        <td width="86%" valign="top">
                <bean:define id="data" name="loadParameters" type="com.desige.webDocuments.parameters.forms.BaseParametersForm" scope="session" />
                <table class="clsTableTitle" width="110" cellSpacing=0 cellPadding=0 align=center border=0>
                    <tbody>
                        <tr>
                            <td class="td_title_bc" height="21">
                                <%=rb.getString("param.logSettings")%>
                            </td>
                        </tr>
                    </tbody>
                </table>

                <table width="100%" border="0">
                    <tr>
                        <td class="titleLeft" height="44" style="background: url(img/btn260x44.gif); background-repeat: no-repeat" width="26%" valign="middle">
                            <%=rb.getString("param.minLength")%>:
                        </td>
                        <td class="td_gris_l">
                            <html:text property="lengthPass" size="2" styleClass="classText"/>
                        </td>
                    </tr>
                   <%-- <tr>
                        <td class="titleLeft" height="26" style="background: url(img/btn260.gif); background-repeat: no-repeat" valign="middle">
                            <%=rb.getString("param.allowDuplicate")%>
                        </td>
                        <td>
                            <%=ToolsHTML.showCheckBox("allowDuplicatePassI","updateCheck(this.form.allowDuplicatePassI,this.form.allowDuplicatePass)",
                                                       data.getAllowDuplicatePass(),"0")%>
                            <html:hidden property="allowDuplicatePass"/>
                        </td>
                    </tr>--%>
                    <tr>
                        <td class="titleLeft" height="26" style="background: url(img/btn260.gif); background-repeat: no-repeat" valign="middle">
                            <%=rb.getString("param.expirePass")%>:
                         </td>
                        <td class="td_gris_l">
                            <%=ToolsHTML.showCheckBox("expirePassI","updateCheck(this.form.expirePassI,this.form.expirePass)",
                                                       String.valueOf(data.getExpirePass()),"0")%>
                            <html:hidden property="expirePass"/>
                            <%=ToolsHTML.getSelectList(rb,"param.daysEndPass","daysEndPass",data.getDaysEndPass())%>
                            <%=rb.getString("param.days")%>
                        </td>
                    </tr>
                    <tr>
                        <td class="titleLeft" height="44" style="background: url(img/btn260x44.gif); background-repeat: no-repeat" width="26%" valign="middle">
                            <%=rb.getString("param.endSession")%>:
                        </td>
                        <td class="td_gris_l">
                            <html:text property="endSession" size="2" styleClass="classText" maxlength="3"/>&nbsp;&nbsp;<%=rb.getString("param.Minutos")%>
                        </td>
                    </tr>
                    <tr>
                        <td class="titleLeft" height="44" style="background: url(img/btn260x44.gif); background-repeat: no-repeat" width="26%" valign="middle">
                            <%=rb.getString("param.endSessionEdit")%>:
                        </td>
                        <td class="td_gris_l">
                            <html:text property="endSessionEdit" size="2" styleClass="classText" maxlength="3"/>&nbsp;&nbsp;<%=rb.getString("param.Minutos")%>
                        </td>
                    </tr>
                </table>
                <br/>

               <%-- LUIS CISNEROS 19/04/07 SACOP ES COMPRABLE --%>
                 <%if (ToolsHTML.showSACOP()){ %>
                    <table class="clsTableTitle" width="110" cellSpacing=0 cellPadding=0 align=center border=0>
                        <tbody>
                            <tr>
                                <td class="td_title_bc" height="21">
                                    <%=rb.getString("param.cargosacop")%>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                    <table width="100%" border="0">
                    <tr>
                        <td class="titleLeft" height="44" style="background: url(img/btn260x60.gif); background-repeat: no-repeat" width="26%" valign="middle">
                           <%=rb.getString("param.cargomostrarsacop")%>&nbsp;<%=rb.getString("scp.sacop")%>
                        </td>
                        <td>
                            <%=ToolsHTML.showCheckBox("cargosacopI","updateCheck(this.form.cargosacopI,this.form.cargosacop)",
                                                       String.valueOf(data.getCargosacop()),"0")%>
                             <%--<%=ToolsHTML.showCheckBox("notifyEmailI","updateCheck(this.form.notifyEmailI,this.form.notifyEmail)",
                             String.valueOf(data.getNotifyEmail()),"0")%>--%>
                            <html:hidden property="cargosacop"/>
                        </td>
                    </tr>
                 <%--   <tr>
                        <td class="titleLeft" height="26" style="background: url(img/btn260.gif); background-repeat: no-repeat" valign="middle">
                            <%=rb.getString("param.serverMail")%>:
                        </td>
                        <td>
                            <html:text property="smtpMail" size="30" styleClass="classText"/>
                        </td>
                    </tr>--%>
                    </table>
                    <br/>
                    
                    <%} else {%>
                        <input type="hidden" name="cargosacopI" id="cargosacopI" value="1" />
                        <input type="hidden" name="cargosacop" id="cargosacop" value="1" />
                    <%}%>

                    <table class="clsTableTitle" width="110" cellSpacing=0 cellPadding=0 align=center border=0>
                        <tbody>
                            <tr>
                                <td class="td_title_bc" height="21">
                                    <%=rb.getString("yajaira2")%>
                                </td>
                            </tr>
                        </tbody>
                    </table>

                    <table width="100%" border="0">
                    <tr>
                        <td class="titleLeft" width="26%" height="26" style="background: url(img/btn260.gif); background-repeat: no-repeat" valign="middle">
                            <%=rb.getString("param.notifyEmail")%>:
                        </td>
                        <td>
                            <%=ToolsHTML.showCheckBox("notifyEmailI","updateCheck(this.form.notifyEmailI,this.form.notifyEmail)",
                                                       String.valueOf(data.getNotifyEmail()),"0")%>
                            <html:hidden property="notifyEmail"/>
                        </td>
                    </tr>
                        <!--Luis Cisneros-->
                        <!--Campo para validar si el mail debe estar en un formato valido o no-->
                    <tr>
                        <td class="titleLeft" width="26%" height="26" style="background: url(img/btn260.gif); background-repeat: no-repeat" valign="middle">
                            <%=rb.getString("param.validateEmail")%>:
                        </td>
                        <td>
                            <%=ToolsHTML.showCheckBox("validateEmailI","updateCheck(this.form.validateEmailI,this.form.validateEmail)",
                                                       String.valueOf(data.getValidateEmail()),"0")%>
                            <html:hidden property="validateEmail"/>
                        </td>
                    </tr>

                    <tr>
                        <td class="titleLeft" height="26" style="background: url(img/btn260.gif); background-repeat: no-repeat" valign="middle">
                            <%=rb.getString("param.domainMail")%>:
                        </td>
                        <td>
                            <html:text property="domainEmail" size="30" maxlength="250" styleClass="classText"/>
                        </td>
                    </tr>

                    <tr>
                        <td class="titleLeft" height="26" style="background: url(img/btn260.gif); background-repeat: no-repeat" valign="middle">
                            <%=rb.getString("param.serverMail")%>:
                        </td>
                        <td>
                            <html:text property="smtpMail" size="30" styleClass="classText"/>
                        </td>
                    </tr>

                    <tr>
                        <td class="titleLeft" height="44" style="background: url(img/btn260x44.gif); background-repeat: no-repeat" width="26%" valign="middle">
                            <%=rb.getString("param.mailAccount")%>:
                        </td>
                        <td>
                            <html:text property="mailAccount" size="30" maxlength="200" styleClass="classText"/>
                        </td>
                    </tr>
                   </table>
                   
                    <br/>
                    <table class="clsTableTitle" width="110" cellSpacing=0 cellPadding=0 align=center border=0>
                        <tbody>
                            <tr>
                                <td class="td_title_bc" height="21">
                                    <%=rb.getString("param.cbsSetting")%>
                                </td>
                            </tr>
                        </tbody>
                    </table>

                    <table width="100%" border="0">
                    <tr>
                        <td class="titleLeft" height="44" style="background: url(img/btn260x44.gif); background-repeat: no-repeat" width="26%" valign="middle">
                            <%=rb.getString("param.quality")%>
                         </td>
                        <td class="td_gris_l">
                            <%=ToolsHTML.getRadioButton(rb,"param.numQuality","qualitySystem",(int) data.getQualitySystem(),null)%>
                        </td>
                    </tr>

                    <tr>
                        <td colspan="2"></td>
                    </tr>

                    <logic:equal name="data" property="typeNumByLocation" value="false">
                        <tr>
                            <td class="titleLeft" height="44" style="background: url(img/btn260x60.gif); background-repeat: no-repeat" width="26%" valign="middle">
                                <%=rb.getString("param.genNumber")%>
                             </td>
                            <td class="td_gris_l">
                                <%--<%=ToolsHTML.getRadioButton(rb,"param.DocNumber","docNumberGenerator",data.getDocNumberGenerator(),"refreshButton(this.form)")%>--%>
                                <!--<br/>-->
                                <%=rb.getString("param.lengthDocNumber")+" "%>&nbsp;<html:text property="lengthDocNumber" size="2" styleClass="classText"/>
                                <br/>
                                <%=rb.getString("param.resetDocNumber")%>&nbsp;<html:text property="resetDocNumber" size="2" styleClass="classText"/>
                            </td>
                        </tr>
	
		                <tr>
	                        <td colspan="2"></td>
	                    </tr>     
    
                        <tr>
                            <td class="titleLeft" height="44" style="background: url(img/btn260x44.gif); background-repeat: no-repeat" valign="middle">
                                <%=rb.getString("param.numDocLocation")%>:
                             </td>
                            <td class="td_gris_l">
                                <%=ToolsHTML.getRadioButton(rb,"param.numDocLoc","numDocLoc",
                                                            data.getNumDocLoc(),null)%>
                                <html:hidden property="numDocLoc"/>
                            </td>
                        </tr>
                      </logic:equal>
                      <logic:notEqual name="data" property="typeNumByLocation" value="false">
                        <tr>
                            <td class="titleLeft" height="44" style="background: url(img/btn260x60.gif); background-repeat: no-repeat" width="26%" valign="middle">
                                <%=rb.getString("param.genNumber")%>
                             </td>
                            <td class="td_gris_l">
                                <%--<%=ToolsHTML.getRadioButton(rb,"param.DocNumber","docNumberGenerator",data.getDocNumberGenerator(),"refreshButton(this.form)")%>--%>
                                <!--<br/>-->
                                <%=rb.getString("param.lengthDocNumber")+" "%>&nbsp;<html:text property="lengthDocNumber" size="2" disabled="true" styleClass="classText"/>
                                <br/>
                                <%=rb.getString("param.resetDocNumber")%>&nbsp;<html:text property="resetDocNumber" disabled="true" size="2" styleClass="classText"/>
                            </td>
                        </tr>

		                <tr>
	                        <td colspan="2"></td>
	                    </tr>     

                        <tr>
                            <td class="titleLeft" height="44" style="background: url(img/btn260x60.gif); background-repeat: no-repeat" valign="middle">
                                <%=rb.getString("param.numDocLocation")%>:
                             </td>
                            <td class="td_gris_l">
                                <%=ToolsHTML.getRadioButton(rb,"param.numDocLoc","numDocLoc",
                                                            data.getNumDocLoc(),null,true)%>
                                <html:hidden property="numDocLoc"/>
                            </td>
                        </tr>
                    </logic:notEqual>

                    <tr>
                        <td colspan="2"></td>
                    </tr>

                    <tr>
                        <td class="titleLeft" height="44" style="background: url(img/btn260x44.gif); background-repeat: no-repeat" valign="middle">
                            <%=rb.getString("param.coment")%>:
                         </td>
                        <td>
                            <%=ToolsHTML.showCheckBox("versionComI","updateCheck(this.form.versionComI,this.form.versionCom)",
                                                       String.valueOf(data.getVersionCom()),"0")%>
                            <html:hidden property="versionCom"/>
                        </td>
                    </tr>
                    <html:hidden property="logDoc"/>
                    
                    <tr>
                        <td colspan="2"></td>
                    </tr>
                    
                    <tr>
                        <td class="titleLeft" height="44" style="background: url(img/btn260x44.gif); background-repeat: no-repeat" valign="middle">
                            <%=rb.getString("param.allowWF")%>
                         </td>
                        <td>
                            <%=ToolsHTML.showCheckBox("allowWFI","updateCheck(this.form.allowWFI,this.form.allowWF)",
                                                       String.valueOf(data.getAllowWF()),"0")%>
                            <html:hidden property="allowWF"/>
                        </td>
                    </tr>
                    
                    <tr>
                        <td colspan="2"></td>
                    </tr>
                    
                    <tr>
                        <td class="titleLeft" height="44" style="background: url(img/btn260x44.gif); background-repeat: no-repeat" valign="middle">
                            <%=rb.getString("param.dateExpire")%>
                        </td>
                        <td class="td_gris_l">
                            <%=ToolsHTML.showCheckBox("expireDocI","updateCheck(this.form.expireDocI,this.form.expireDoc)",
                                                       String.valueOf(data.getExpireDoc()),"0")%>
                            <html:hidden property="expireDoc"/>
                            <%=ToolsHTML.getSelectList(rb,"param.monthExpireDoc","monthsExpireDocs",data.getMonthsExpireDocs())%>
                            <%=ToolsHTML.getSelectList(rb,"param.vigItems","param.vigItemsValue","unitTimeExpire",data.getUnitTimeExpire())%>
                        </td>
                    </tr>
                    
                    <tr>
                        <td colspan="2"></td>
                    </tr>
                                        
                    <!-- SIMON 16 DE JUNIO 2005 2_04PM INICIO -->
                    <tr>
                        <td class="titleLeft" height="44" style="background: url(img/btn260x44.gif); background-repeat: no-repeat" valign="middle">
                           <%=rb.getString("param.dateDrafts")%>
                        </td>
                        <td class="td_gris_l">
                            <%=ToolsHTML.showCheckBox("expireDraftsI","updateCheck(this.form.expireDraftsI,this.form.expireDrafts)",
                                          String.valueOf(data.getExpireDrafts()),"0")%>
                            <html:hidden property="expireDrafts"/>
                             <%=ToolsHTML.getSelectList(rb,"param.monthExpireDoc","monthsExpireDrafts",data.getMonthsExpireDrafts())%>
                             <%=ToolsHTML.getSelectList(rb,"param.vigItems","param.vigItemsValue","unitTimeExpireDrafts",data.getUnitTimeExpireDrafts())%>
                        </td>
                    </tr>
                    
                    <tr>
                        <td colspan="2"></td>
                    </tr>
                                        
                    <!-- Tipo de Ordenamiento a Usar por el usuario -->
                    <tr>
                        <td class="titleLeft" height="44" style="background: url(img/btn260x44.gif); background-repeat: no-repeat" valign="middle">
                           <%=rb.getString("param.typeOrderTxt")%>
                        </td>
                        <td class="td_gris_l">
                            <%=ToolsHTML.getRadioButton(rb,"param.typeOrder","typeOrder",
                                                            data.getTypeOrder(),null,false)%>
                            <html:hidden property="typeOrder"/>                            
                        </td>
                    </tr>
                    
                    <tr>
                        <td colspan="2"></td>
                    </tr>

                    <!-- Permisologia para el cambios de nombre y propietario de un documento aprobado -->
                    <tr>
                        <td class="titleLeft" height="44" style="background: url(img/btn260x44.gif); background-repeat: no-repeat" valign="middle">
                           <%=rb.getString("param.changePropertiesApproved")%>
                        </td>
                        <td class="td_gris_l">
                            <%=ToolsHTML.showCheckBox("changePropertiesI","updateCheckReverse(this.form.changePropertiesI,this.form.changeProperties)",
                                                       String.valueOf(data.getChangeProperties()),"1")%>
                            <html:hidden property="changeProperties"/>                            
                        </td>
                    </tr>

                    <tr>
                        <td colspan="2"></td>
                    </tr>
                    
                                        
                    <!-- SIMON 16 DE JUNIO 2005 2_04 PM FIN -->

                    <tr>
                        <td colspan="2"></td>
                    </tr>
					</table>
					
					<%if (ToolsHTML.showFTP()) {%>
                    <br/>
                    <table class="clsTableTitle" width="110" cellSpacing=0 cellPadding=0 align=center border=0>
                        <tbody>
                            <tr>
                                <td class="td_title_bc" height="21">
                                    <%=rb.getString("param.confFTP")%>
                                </td>
                            </tr>
                        </tbody>
                    </table>

                    <table width="100%" border="0">
                    <!-- JRivero -->
                    <!--Se provee un flujo parametrico especial para completar flujos no terminados-->
                    <tr>
                        <td class="titleLeft" height="44" style="background: url(img/btn260x60.gif); background-repeat: no-repeat" width="26%" valign="middle">
                            <%=rb.getString("param.completeFTP")%>:
                        </td>
                        <td>
                            <%=ToolsHTML.showCheckBox("completeFTPI","updateCheckReverse(this.form.completeFTPI,this.form.completeFTP)",
                                                       String.valueOf(data.getCompleteFTP()),"1")%>
                            <html:hidden property="completeFTP"/>
                        </td>
                    </tr>

                    <tr>
                        <td colspan="2"></td>
                    </tr>
                    
                    <!-- YSA 02/07/07 -->
                    <!--Permite definir si se desea que en un FTP cancelado se reinicie o no el flujo en un firmante o actividad determinada-->
                    <tr>
                        <td class="titleLeft" height="44" style="background: url(img/btn260x60.gif); background-repeat: no-repeat" width="26%" valign="middle">
                            <%=rb.getString("param.resigneUserFTP")%>:
                        </td>
                        <td>
                            <%=ToolsHTML.showCheckBox("reasigneUserFTPI","updateCheckReverse(this.form.reasigneUserFTPI,this.form.reasigneUserFTP)",
                                                       String.valueOf(data.getReasigneUserFTP()),"1")%>
                            <html:hidden property="reasigneUserFTP"/>
                        </td>
                    </tr>

                    <tr>
                        <td colspan="2"></td>
                    </tr>

                    <tr>
                        <td class="titleLeft" height="44" style="background: url(img/btn260x60.gif); background-repeat: no-repeat" width="26%" valign="middle">
                            <%=rb.getString("param.documentReject")%>:
                        </td>
                        <td>
	                    <!--Permite habilitar la opcion crear documentos de rechazo en los FTP-->
                            <%=ToolsHTML.showCheckBox("documentRejectI","updateCheckReverse(this.form.documentRejectI,this.form.documentReject)",
                                                       String.valueOf(data.getDocumentReject()),"1")%>
                            <html:hidden property="documentReject"/>
                        </td>
                    </tr>

                    <tr>
                        <td colspan="2"></td>
                    </tr>                    
					</table>
					<%}%>
					
                    <br/>
                    <table class="clsTableTitle" width="110" cellSpacing=0 cellPadding=0 align=center border=0>
                        <tbody>
                            <tr>
                                <td class="td_title_bc" height="21">
                                    <%=rb.getString("param.confPrint")%>
                                </td>
                            </tr>
                        </tbody>
                    </table>

                    <table width="100%" border="0">
                    <!--Luis Cisneros-->
                    <!--23-04-07-->
                    <!--Se permite la libre impresion al Administrador y al dueño del documento?-->
                    <tr>
                        <td class="titleLeft" height="44" style="background: url(img/btn260x60.gif); background-repeat: no-repeat" width="26%" valign="middle">
                            <%=rb.getString("param.printOwnerAdmin")%>:
                        </td>
                        <td>
                            <%=ToolsHTML.showCheckBox("printOwnerAdminI","updateCheck(this.form.printOwnerAdminI,this.form.printOwnerAdmin)",
                                                       String.valueOf(data.getPrintOwnerAdmin()),"0")%>
                            <html:hidden property="printOwnerAdmin"/>
                        </td>
                    </tr>
                    
                    <tr>
                        <td colspan="2"></td>
                    </tr>

                    <!-- YSA 01/08/07 -->
                    <!--Se permite seleccionar valores para impresion-->
                    <tr>
                        <td class="titleLeft" height="44" style="background: url(img/btn260x60.gif); background-repeat: no-repeat" width="26%" valign="middle">
                            <%=rb.getString("param.showNameCharge")%>:
                        </td>
						<td class="td_gris_l">
                               <%=ToolsHTML.getRadioButton(rb,"param.impNameCharge","impNameCharge",
                                                           data.getImpNameCharge(),null,false)%>
                              <html:hidden property="impNameCharge"/>
                        </td>                        
                    </tr>

                    <tr>
                        <td colspan="2"></td>
                    </tr>
                    
                    <!-- YSA 03/08/07 -->
                    <!--Valores para el encabezado de impresion-->
                    <tr>
                        <td class="titleLeft" height="44" style="background: url(img/btn260x60.gif); background-repeat: no-repeat" width="26%" valign="middle">
                            <%=rb.getString("param.headImp")%>:
                        </td>
						<td>
							<table border="0" width="100%" align="left">
							<tr>
								<td class="td_gris_l" width="50"><%=rb.getString("param.headImp1")%></td>
								<td><html:text property="headImp1" size="60" maxlength="200" styleClass="classText"/></td>
							</tr>
							<tr>
								<td class="td_gris_l" width="50"><%=rb.getString("param.headImp2")%></td>
								<td><html:text property="headImp2" size="25" maxlength="50" styleClass="classText"/></td>
							</tr>
							<tr>
								<td class="td_gris_l" width="50"><%=rb.getString("param.headImp3")%></td>
								<td><html:text property="headImp3" size="25" maxlength="50" styleClass="classText"/></td>
							</tr>
							</table>
                        </td>                        
                    </tr>

                    <tr>
                        <td colspan="2"></td>
                    </tr>
                                        
                    <!-- YSA 01/08/08 -->
                    <!--Se permite seleccionar la opcion para imprimir o no el creador de un documento-->
                    <tr>
                        <td class="titleLeft" height="44" style="background: url(img/btn260x60.gif); background-repeat: no-repeat" width="26%" valign="middle">
                            <%=rb.getString("param.creator")%>:
                        </td>
                        <td>
                            <%=ToolsHTML.showCheckBox("viewCreatorI","updateCheckReverse(this.form.viewCreatorI,this.form.viewCreator)",
                                                       String.valueOf(data.getViewCreator()),"1")%>
                            <html:hidden property="viewCreator"/>
                        </td>
                    </tr>

                    <tr>
                        <td colspan="2"></td>
                    </tr>
                                                     
                    <!-- YSA 15/06/07 -->
                    <!--Se permite seleccionar la opcion para imprimir o no los revisores de un documento-->
                    <tr>
                        <td class="titleLeft" height="44" style="background: url(img/btn260x60.gif); background-repeat: no-repeat" width="26%" valign="middle">
                            <%=rb.getString("param.readers")%>:
                        </td>
                        <td>
                            <%=ToolsHTML.showCheckBox("viewReadersI","updateCheckReverse(this.form.viewReadersI,this.form.viewReaders)",
                                                       String.valueOf(data.getViewReaders()),"1")%>
                            <html:hidden property="viewReaders"/>
                        </td>
                    </tr>

                    <tr>
                        <td colspan="2"></td>
                    </tr>
                    
					</table>
					
                    <br/>
                    <table class="clsTableTitle" width="110" cellSpacing=0 cellPadding=0 align=center border=0>
                        <tbody>
                            <tr>
                                <td class="td_title_bc" height="21">
                                    <%=rb.getString("param.confSystem")%>
                                </td>
                            </tr>
                        </tbody>
                    </table>

                    <table width="100%" border="0">
                    <tr>
                        <td class="titleLeft" height="44" style="background: url(img/btn260x60.gif); background-repeat: no-repeat" width="26%" valign="middle">
                            <%=rb.getString("param.downloadLastVer")%>:
                        </td>
                        <td>
	                    <!-- YSA 20/07/07 -->
	                    <!--Permite habilitar opcion para bajar ultima version de un documento-->
                            <%=ToolsHTML.showCheckBox("downloadLastVerI","updateCheckReverse(this.form.downloadLastVerI,this.form.downloadLastVer)",
                                                       String.valueOf(data.getDownloadLastVer()),"1")%>
                            <html:hidden property="downloadLastVer"/>
                        </td>
                    </tr>

                    <tr>
                        <td colspan="2"></td>
                    </tr>                    

					<!--   permite eliminar la seguridad implementada para el IE6                    -->
                    <tr>
                        <td class="titleLeft" height="44" style="background: url(img/btn260x60.gif); background-repeat: no-repeat" width="26%" valign="middle">
                            <%=rb.getString("param.fileNotOpenSave")%>:
                        </td>
                        <td>
                            <%=ToolsHTML.showCheckBox("fileNotOpenSaveI","updateCheckReverse(this.form.fileNotOpenSaveI,this.form.fileNotOpenSave)",
                                                       String.valueOf(data.getFileNotOpenSave()),"1")%>
                            <html:hidden property="fileNotOpenSave"/>
                        </td>
                    </tr>

                    <tr>
                        <td colspan="2"></td>
                    </tr>                    
                                                            
					<!--   permite especificar los tipos de archivo no manejados por qweb y que permiten subir una segundo formato de archivo   -->
                    <tr>
                        <td class="titleLeft" height="44" style="background: url(img/btn260x60.gif); background-repeat: no-repeat" width="26%" valign="middle">
                            <%=rb.getString("param.fileDoubleVersion")%>:
                        </td>
                        <td>
                            <html:text property="fileDoubleVersion" size="25" maxlength="50" styleClass="classText"/>
                        </td>
                    </tr>

                    <tr>
                        <td colspan="2"></td>
                    </tr>                    

					<!--   permite especificar los tipos de archivo no editables por qweb y que permiten subir la version completa del archivo   -->
                    <tr>
                        <td class="titleLeft" height="44" style="background: url(img/btn260x60.gif); background-repeat: no-repeat" width="26%" valign="middle">
                            <%=rb.getString("param.fileUploadVersion")%>:
                        </td>
                        <td>
                            <html:text property="fileUploadVersion" size="25" maxlength="50" styleClass="classText"/>
                        </td>
                    </tr>

                    <tr>
                        <td colspan="2"></td>
                    </tr>                    

					<!--   permite especificar si qwebdocuments sera usado utilizando office o openoffice  -->
                    <tr>
                        <td class="titleLeft" height="44" style="background: url(img/btn260x60.gif); background-repeat: no-repeat" width="26%" valign="middle">
                            <%=rb.getString("param.openoffice")%>:
                        </td>
                        <td>
                            <%=ToolsHTML.showCheckBox("openofficeI","updateCheckReverse(this.form.openofficeI,this.form.openoffice)",
                                                       String.valueOf(data.getOpenoffice()),"1")%>
                            <html:hidden property="openoffice"/>
                        </td>
                    </tr>
					
	                <tr>
                        <td colspan="2"></td>
                    </tr>                    

                    <!-- Idioma por defecto del usuario -->
                    <tr>
                        <td class="titleLeft" height="44" style="background: url(img/btn260x44.gif); background-repeat: no-repeat" valign="middle">
                           <%=rb.getString("param.idiomasTxt")%>
                        </td>
                        <td class="td_gris_l">
                            <%--<%=ToolsHTML.getSelectList(rb,"param.idiomas","param.idiomasValue","idioma",data.getIdioma())%>--%>
                            <logic:present name="idiomas" scope="request">
                                <html:select property="idioma" styleClass="classText">
                                    <logic:iterate id="idio" name="idiomas" type="com.desige.webDocuments.utils.beans.Search">
                                        <html:option value="<%=idio.getId()%>">
                                            <%=idio.getDescript()%>
                                        </html:option>
                                    </logic:iterate>
                                </html:select>
                            </logic:present>
                        </td>
                    </tr>
					
	                <tr>
                        <td colspan="2"><br/></td>
                    </tr>  

					<!-- Desincorporacion de documentos -->
	                <tr>
                        <td colspan="2">
		                    <table class="clsTableTitle" width="110" cellSpacing=0 cellPadding=0 align=center border=0>
		                        <tbody>
		                            <tr>
		                                <td class="td_title_bc" height="21">
		                                    <%=rb.getString("param.configFileDead")%>
		                                </td>
		                            </tr>
		                        </tbody>
		                    </table>
                        </td>
                    </tr>  
                    
                    <tr>
                        <td class="titleLeft" height="44" style="background: url(img/btn260x44.gif); background-repeat: no-repeat" valign="middle">
                            <%=rb.getString("param.configFileDeadFile")%>
                        </td>
                        <td class="td_gris_l">
                            <%=ToolsHTML.showCheckBox("fileDeadFileI","updateCheck(this.form.fileDeadFileI,this.form.fileDeadFile)",
                                                       String.valueOf(data.getFileDeadFile()),"0")%>
                            <html:hidden property="fileDeadFile"/>
                            <%=ToolsHTML.getSelectList(rb,"param.monthExpireDoc","fileDeadTime",data.getFileDeadTime())%>
                            <%=ToolsHTML.getSelectList(rb,"param.vigItems","param.vigItemsValue","fileDeadUnit",data.getFileDeadUnit())%>
                        </td>
                    </tr>
					
                    <tr>
                        <td class="titleLeft" height="44" style="background: url(img/btn260x44.gif); background-repeat: no-repeat" valign="middle">
                            <%=rb.getString("param.configFileDeadVerbose")%>
                        </td>
                        <td class="td_gris_l">
                            <%=ToolsHTML.showCheckBox("fileDeadVerboseI","updateCheck(this.form.fileDeadVerboseI,this.form.fileDeadVerbose)",
                                                       String.valueOf(data.getFileDeadVerbose()),"0")%>
                            <html:hidden property="fileDeadVerbose"/>
                        </td>
                    </tr>
                    
					
	                <tr>
                        <td colspan="2">&nbsp;</td>
                    </tr>                    
                    
                    
                     <!-- SIMON 22 DE JULIO 2005 INICIO -->
                     <html:textarea property="msgDocExpirado" style="display:none" />
                     <tr>
                          <td colspan="2" heigth="26" class="titleCenter" style="background: url(img/BgComments.gif); background-repeat: no-repeat">
                              <%=rb.getString("param.msgDOCExpirados")%>
                          </td>
                     </tr>
                     <tr>
                          <td class="fondoEditor" colspan="2" valign="top">
                          	  <jsp:include page="richedit.jsp">
                          	  	<jsp:param name="richedit" value="richeditdocexpirado"/>
                          	  	<jsp:param name="defaultValue" value="<%=String.valueOf(data.getMsgDocExpirado())%>" />
                          	  </jsp:include>
                          </td>
                     </tr>
                     <tr>
                          <td  colspan="2" valign="top" >
                          	&nbsp;
                          </td>
                     </tr>
                      <!-- SIMON 22 DE JULIO 2005 FIN -->
                      <!-- SIMON 16 DE JUNIO 2005 INICIO -->
                          <html:textarea property="msgWFBorrador" style="display:none" />
                     <tr>
                         <td colspan="2" heigth="26" class="titleCenter" style="background: url(img/BgComments.gif); background-repeat: no-repeat">
                             <%=rb.getString("param.msgWFBorrador")%>
                         </td>
                     </tr>
                     <tr>
                         <td colspan="2" valign="top" class="fondoEditor">
                          	  <jsp:include page="richedit.jsp">
                          	  	<jsp:param name="richedit" value="richeditborrador"/>
                          	  	<jsp:param name="defaultValue" value="<%=String.valueOf(data.getMsgWFBorrador())%>" />
                          	  </jsp:include>
                          </td>
                      </tr>
                     <tr>
                          <td  colspan="2" valign="top" >
                          	&nbsp;
                          </td>
                     </tr>
                    <!-- SIMON 16 DE JUNIO2005 FIN -->
                    <html:textarea property="msgWFExpires" style="display:none" />
                    <tr>
                        <td colspan="2" heigth="26" class="titleCenter" style="background: url(img/BgComments.gif); background-repeat: no-repeat">
                            <%=rb.getString("param.msgWFExpires")%>
                        </td>
                    </tr>
                    <tr>
                         <td colspan="2" valign="top" class="fondoEditor">
                          	  <jsp:include page="richedit.jsp">
                          	  	<jsp:param name="richedit" value="richedit"/>
                          	  	<jsp:param name="defaultValue" value="<%=String.valueOf(data.getMsgWFExpires())%>" />
                          	  </jsp:include>
                          </td>
                    </tr>
                     <tr>
                          <td  colspan="2" valign="top" >
                          	&nbsp;
                          </td>
                     </tr>
                    <html:textarea property="msgWFRevision" style="display:none" />
                    <tr>
                       <td colspan="2" heigth="26" class="titleCenter" style="background: url(img/BgComments.gif); background-repeat: no-repeat">
                            <%=rb.getString("param.msgWFRevision")%>
                       </td>
                    </tr>
                    <tr>
                        <td colspan="2" valign="top" class="fondoEditor">
                          	  <jsp:include page="richedit.jsp">
                          	  	<jsp:param name="richedit" value="richeditrev"/>
                          	  	<jsp:param name="defaultValue" value="<%=String.valueOf(data.getMsgWFRevision())%>" />
                          	  </jsp:include>
                        </td>
                    </tr>
                     <tr>
                          <td  colspan="2" valign="top" >
                          	&nbsp;
                          </td>
                     </tr>
                          <html:textarea property="msgWFAprobados" style="display:none" />
                    <tr>
                       <td colspan="2" heigth="26" class="titleCenter" style="background: url(img/BgComments.gif); background-repeat: no-repeat">
                            <%=rb.getString("param.msgWFAprobados")%>
                       </td>
                    </tr>
                    <tr>
                        <td colspan="2" valign="top" class="fondoEditor">
                          	  <jsp:include page="richedit.jsp">
                          	  	<jsp:param name="richedit" value="richeditaprob"/>
                          	  	<jsp:param name="defaultValue" value="<%=String.valueOf(data.getMsgWFAprobados())%>" />
                          	  </jsp:include>
                        </td>
                    </tr>
                     <tr>
                          <td  colspan="2" valign="top" >
                          	&nbsp;
                          </td>
                     </tr>
                            <html:textarea property="msgWFCancelados" style="display:none" />
                    <tr>
                       <td colspan="2" heigth="26" class="titleCenter" style="background: url(img/BgComments.gif); background-repeat: no-repeat">
                            <%=rb.getString("param.msgWFCancelados")%>
                       </td>
                    </tr>
                    <tr>
                        <td colspan="2" valign="top" class="fondoEditor">
                          	  <jsp:include page="richedit.jsp">
                          	  	<jsp:param name="richedit" value="richeditcancel"/>
                          	  	<jsp:param name="defaultValue" value="<%=String.valueOf(data.getMsgWFCancelados())%>" />
                          	  </jsp:include>
                        </td>
                    </tr>
                     <tr>
                          <td  colspan="2" valign="top" >
                          	&nbsp;
                          </td>
                     </tr>

                    <tr>
                        <td colspan="2" align="center">
                            <logic:equal value='<%=DesigeConf.getProperty("application.admon")%>' name='user' property='idGroup'>
                                <input type="button" class="boton" value="<%=rb.getString("btn.save")%>" onClick="javascript:salvar(this.form);" name="btnOK"/>
                                &nbsp;
                            </logic:equal>
                            <input type="button" class="boton" value="<%=rb.getString("btn.cancel")%>" onClick="javascript:cancelar(this.form);" name="btnCancel"/>
                        </td>
                    </tr>
                </table>
            </html:form>
            <script language="JavaScript" event="onload" for="window">
                //document.loadParameters.richedit.docHtml = document.loadParameters.msgWFExpires.innerText;
                //document.loadParameters.richeditrev.docHtml = document.loadParameters.msgWFRevision.innerText;
                //document.loadParameters.richeditaprob.docHtml = document.loadParameters.msgWFAprobados.innerText;
                //document.loadParameters.richeditcancel.docHtml = document.loadParameters.msgWFCancelados.innerText;
                //document.loadParameters.richeditborrador.docHtml = document.loadParameters.msgWFBorrador.innerText;
                //document.loadParameters.richeditdocexpirado.docHtml = document.loadParameters.msgDocExpirado.innerText;
                <logic:present name="info" scope="session" >
                    alert('<%=session.getAttribute("info")%>');
                    <%session.removeAttribute("info");%>
                </logic:present>
                <logic:present name="error" scope="session" >
                    alert('<%=session.getAttribute("error")%>');
                    <%session.removeAttribute("error");%>
                </logic:present>
                document.location="#inicio";
            </script>
            </td>
        </tr>
</table>
</body>
</html>
