<!-- /**
 * Title: parameters.jsp<br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * @author Ing. Nelson Crespo (NC)
 * @author Ing. Simon RodrÃ­guez (SR)
 * @version WebDocuments v3.0
 * prueba de svn
 * <br/>
 * Changes:<br/>
 * <ul>
 *      <li> 25/05/2005 Se agrego parametros de revision, aprobacion, y cancelados(SR) </li>
 *      <li> 29/05/2009 Se eliminÃ³ el uso del parÃ¡metro typePrefix                     </li>
 *      <li> 15/06/2005 Se agregaron los cambios para manipular el campo NumByLocation </li>
 *      <li> 22/07/2005 Se agregaron mensaje msgDocExpirado (SR)                       </li>
 *      <li> 25/04/2006 (SR) Se sustituyo /WebDocuments/.. por ./..
 *      <li> 25/04/2006 (SR) quitar opcion de contraseÃ±as duplicadas,tiempo session variable </li>
 *      <li> 14/06/2007 (YS) Se agrega opciÃ³n para seÃ±alar si se desean ver o no revisores cuando se imprime un documento aprobado </li>
 *      <li> 02/07/2007 (YS) Se agrega opciÃ³n para seÃ±alar si se desean reasignar Usuario al cancelar un Flujo de Trabajo ParamÃ©trico </li>
 *      <li> 20/07/2007 (YS) Se agrega opciÃ³n para seÃ±alar si se desea Permitir descarga de Ãºltima VersiÃ³n </li>
 *      <li> 01/08/2007 (YS) Se agrega opciÃ³n para elegir valores de impresion </li>
 *      <li> 15/10/2007 (YS) Se obtiene email del remitente, para notificaciones del sistema </li>
 *      <li> 23/11/2007 (YS) Se obtiene tiempo se expiracion para edicion de documentos </li>
   </ul>
 */ -->
<%@ page import="java.util.ResourceBundle,
                 com.desige.webDocuments.utils.ToolsHTML,
                 com.desige.webDocuments.utils.NumberSacop,
                 com.desige.webDocuments.utils.beans.Users,
                 com.desige.webDocuments.utils.beans.SuperActionForm,
                 com.desige.webDocuments.utils.beans.PaginPage,
                 com.desige.webDocuments.parameters.forms.BaseParametersForm,
                 com.desige.webDocuments.structured.forms.BaseStructForm,
				 com.desige.webDocuments.utils.beans.Search,
                 java.util.HashMap,
				 java.util.TreeMap,
				 java.util.Collection,
				 	
                 com.desige.webDocuments.utils.DesigeConf"%>

<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<%@ page language="java" %>
<bean:define id="data" name="loadParameters" type="com.desige.webDocuments.parameters.forms.BaseParametersForm" scope="session" />
<%
//ydavila Ticket 001-00-003023
Users usuarioActual = session.getAttribute("user")!=null?(Users)session.getAttribute("user"):null;
TreeMap listUsers = (TreeMap)session.getAttribute("listUsers");
Collection listaNormas = (Collection)request.getAttribute("normasPadre");

Collection users = null;
BaseParametersForm editForm = null;
Search search = null;

Users usuario = (Users)session.getAttribute("user");
String respsolcambio = ToolsHTML.getAttribute(request,"respsolcambio")!=null?(String)ToolsHTML.getAttribute(request,"respsolcambio"):"";
String respsolelimin = ToolsHTML.getAttribute(request,"respsolelimin")!=null?(String)ToolsHTML.getAttribute(request,"respsolelimin"):"";
String respsolimpres = ToolsHTML.getAttribute(request,"respsolimpres")!=null?(String)ToolsHTML.getAttribute(request,"respsolimpres"):"";
//System.out.println("propietario="+propietario);   
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
    NumberSacop ns = new NumberSacop(data.getNumberSacop());
    
%>
<html>
<head>
<title><%=rb.getString("principal.title")%></title>
<jsp:include page="meta.jsp" /> 
<link href="estilo/estilo.css" rel="stylesheet" type="text/css">
<script type="text/javascript">
	String.prototype.trim = function() { return this.replace(/^\s*|\s*$/g,""); };
	String.prototype.endsWith = function(str) {return (this.match(str+"$")==str)}
	
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
    	if(!forma) {
    		forma = document.forms[0];
    		forma.target="bandeja";
    	}
    	
        if(document.loadParameters.fileNotOpenSave.value==0) {
        	alert('<%=rb.getString("param.warningFileNotOpenSave")%>');
        }
    
        if (!(parseInt(forma.endSession.value))) {
            alert('<%=rb.getString("param.timeSession")%> <%=rb.getString("param.timeSession2")%>');
            forma.endSession.value='<%=rb.getString("param.timeSession2")%>';
            return false;
        }
        if (!(parseInt(forma.endSessionEdit.value))) {
            alert('<%=rb.getString("param.timeSessionEdit")%> <%=rb.getString("param.timeSessionEdit2")%>');
            forma.endSessionEdit.value='<%=rb.getString("param.timeSessionEdit2")%>';
            return false;
        }
        if (forma.smtpMail.value.trim().length == 0) {
            alert('<%=rb.getString("param.notSMPT")%>');
            return false;
        }
        if (forma.mailAccount.value.trim().length == 0) {
            alert('<%=rb.getString("param.notMailAccount")%>');
            return false;
        }
        if (!(/^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/.test(forma.mailAccount.value))) {
            alert('<%=rb.getString("param.notValidMailAccount")%>');
            return false;
        }
        // aqui la validacion del dominio
        //alert(forma.validateEmail.value);
        
        if (forma.validateEmail.value==0){
        	if (forma.domainEmail.value.trim().length == 0) {
        		alert("<%=rb.getString("err.NotValidDomain")%>"+". No Puede quedar en Blanco o Vacio");
               return true;
           } 
        	
        
        	 //if (!(/^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/.test(forma.domainEmail.value))) {
        	
       	//	if (!(/^\w+([\.-]?\w+)*(\.\w{2,3})+$/.test(forma.domainEmail.value))) {       		 
       
       			//alert("<%=rb.getString("err.NotValidDomain")%>");
                 //return false;
            //     return true;
           //  }
        	
        }
        	 
        if (forma.validateEmail.value==1){
        	forma.domainEmail.value="";
        }
         
       
        
        
        
        if (forma.repository.value.trim().length == 0) {
            alert('<%=rb.getString("param.notRepositoryPath")%>');
            return false;
        }
        
        if(!forma.repository.value.trim().endsWith('<%=request.getContextPath()%>')) {
            alert('<%=rb.getString("param.notRepositoryMatch")%> \"<%=request.getContextPath()%>\"');
            return false;
        }
        
        var i='0';
        i= forma.endSession.value-'<%=rb.getString("param.timeSession2")%>'; 
        if (i>='0')  {
            botones(forma);
            
            if(typeof window.frames['frmMsgDocExpirado'].getValor!='undefined')
	            forma.msgDocExpirado.value = window.frames['frmMsgDocExpirado'].getValor();

            if(typeof window.frames['frmMsgWFBorrador'].getValor!='undefined')
	            forma.msgWFBorrador.value = window.frames['frmMsgWFBorrador'].getValor();

            if(typeof window.frames['frmMsgWFExpires'].getValor!='undefined')
	            forma.msgWFExpires.value = window.frames['frmMsgWFExpires'].getValor();

            if(typeof window.frames['frmMsgWFRevision'].getValor!='undefined')
	            forma.msgWFRevision.value = window.frames['frmMsgWFRevision'].getValor();

            if(typeof window.frames['frmMsgWFAprobados'].getValor!='undefined')
	            forma.msgWFAprobados.value = window.frames['frmMsgWFAprobados'].getValor();
	        
            if(typeof window.frames['frmMsgWFCancelados'].getValor!='undefined')
	            forma.msgWFCancelados.value = window.frames['frmMsgWFCancelados'].getValor();

            if(typeof window.frames['frmMsgSacopAccionesVencidas'].getValor!='undefined')
                forma.msgSacopAccionesVencidas.value = window.frames['frmMsgSacopAccionesVencidas'].getValor();

            if(typeof window.frames['frmMsgSacopAccionesPorVencer'].getValor!='undefined')
                forma.msgSacopAccionesPorVencer.value = window.frames['frmMsgSacopAccionesPorVencer'].getValor();

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
    
	function ir(nodo){
		for(var i=0; i<20; i++) {
			try{
				document.getElementById("nodo"+i).className="none";
			}catch(e){break;}
		}
		document.getElementById("nodo"+nodo).className="";
	}
	
	function ocultar(nodo){
		document.getElementById("nodo"+nodo).className="none";
	}
	
	function setChar(sString, nPosition, nCharCode) {
		if (typeof sString == "string" && sString.length > nPosition) {
			var c = sString.split('');
			c[nPosition]=nCharCode;
			sString=c.join('');
		}
		return sString;
	}
	
	function validarNumberSacop(obj,pos) {
		var cad=document.loadParameters.numberSacop.value;
		if(typeof cad=='undefined' || cad=='' || cad.length!=7) { cad='1000010'; }
		if(pos==6) {
			cad = setChar(cad,pos,obj.value);
		} else {
			cad = setChar(cad,pos,obj.checked?"1":"0");
			if(!obj.checked) {
				document.loadParameters.renumerar[pos].disabled = true;
				if(cad.charAt(6)==(pos+1)) {
					cad = setChar(cad,6,'0');
				}
				document.loadParameters.renumerar[pos].checked = false;
				if(pos==2){
					document.loadParameters.prefijoMes.checked = false;
					document.loadParameters.prefijoDia.checked = false;
					document.loadParameters.renumerar[3].disabled = true;
					document.loadParameters.renumerar[4].disabled = true;
					document.loadParameters.renumerar[3].checked = false;
					document.loadParameters.renumerar[4].checked = false;
					cad = setChar(cad,3,'0');
					cad = setChar(cad,4,'0');
					if(cad.charAt(6)>3) {
						cad = setChar(cad,6,'0');
					}
				}
				if(pos==3){
					document.loadParameters.prefijoDia.checked = false;
					document.loadParameters.renumerar[4].disabled = true;
					document.loadParameters.renumerar[4].checked = false;
					cad = setChar(cad,4,'0');
					if(cad.charAt(6)>4) {
						cad = setChar(cad,6,'0');
					}
				}
			} else {
				document.loadParameters.renumerar[pos].disabled = false;
				if(pos==3){
					document.loadParameters.prefijoAno.checked = true;
					document.loadParameters.renumerar[2].disabled = false;
					cad = setChar(cad,2,'1');
				}
				if(pos==4){
					document.loadParameters.prefijoAno.checked = true;
					document.loadParameters.prefijoMes.checked = true;
					document.loadParameters.renumerar[2].disabled = false;
					document.loadParameters.renumerar[3].disabled = false;
					cad = setChar(cad,2,'1');
					cad = setChar(cad,3,'1');
				}
			}
		}
		document.loadParameters.numberSacop.value=cad;
	}
	
	function combinarValores(lista){

		var campo = document.getElementById("listUserAddressee");
		var cad = "";
		var sep = "";
		for(var i=0;i<lista.length;i++) {
			if(lista[i].selected) {
				cad = cad + sep + lista[i].value;
				sep = ",";
			}
		}
		campo.value=cad;
	}
</script>
</head>
<!--<body class="bodyInternas" <%=onLoad.toString()%>>-->
<body class="bodyInternas" style="background:url('images/engranaje.gif');background-repeat:no-repeat;background-position:center;background-attachment:fixed;">
<div id="nodo0"  class="td_gris_l" style="padding:20;"> 
	<%=rb.getString("param.panel")%>
	<p style="padding:20;">
		<%=rb.getString("param.selection")%>
	</p>
</div>
<table cellPadding=2 align=center border=0 width="100%">
    <html:form action="saveParameters">
      <tr id="nodo1" class="none">
        <td width="86%" valign="top">
                <table width="100%" border="0">
                    <tr>
                        <td class="td_gris_l">
                            <%=ToolsHTML.showCheckBox("expirePassI","updateCheck(this.form.expirePassI,this.form.expirePass)",
                                                       String.valueOf(data.getExpirePass()),"0")%>
                            <%=rb.getString("param.expirePass")%>:
                         </td>
                        <td class="td_gris_l">
                            <html:hidden property="expirePass"/>
                            <%=ToolsHTML.getSelectList(rb,"param.daysEndPass","daysEndPass",data.getDaysEndPass())%>
                            <%=rb.getString("param.days")%>
                        </td>
                    </tr>
                    <tr>
                        <td class="td_gris_l" colspan="2">&nbsp;</td>
                    </tr>
                    <tr>
                        <td class="td_gris_l" width="50%">
                            <%=rb.getString("param.minLength")%>:
                        </td>
                        <td class="td_gris_l">
                            <html:text property="lengthPass" size="2" styleClass="classText" style="width:48;"/>
                        </td>
                    </tr>
                    <tr>
                        <td class="td_gris_l">
                            <%=rb.getString("param.endSession")%>:
                        </td>
                        <td class="td_gris_l">
                            <html:text property="endSession" size="2" styleClass="classText" maxlength="3" style="width:48;"/>&nbsp;&nbsp;<%=rb.getString("param.Minutos")%>
                        </td>
                    </tr>
                    <tr>
                        <td class="td_gris_l">
                            <%=rb.getString("param.endSessionEdit")%>:
                        </td>
                        <td class="td_gris_l">
                            <html:text property="endSessionEdit" size="2" styleClass="classText" maxlength="3"  style="width:48;"/>&nbsp;&nbsp;<%=rb.getString("param.Minutos")%>
                        </td>
                    </tr>
                    
                    <tr style="display:<%=ToolsHTML.showActiveDirectory()?"":"none"%>">
                    	<td colspan="2" class="td_gris_l" style="padding-top:10px;">
                         	<fieldset>
	                         	<legend>
		                            Active Directory:
	                         	</legend>
	                         	<table border="0">
	                         		<tr>
				                        <td class="td_gris_l">
				                            <%=rb.getString("param.domain")%>:
				                        </td>
				                        <td>
				                            <html:text property="domain" size="40" maxlength="250" styleClass="classText"/>
				                        </td>
			                        </tr>
				                    <tr>
				                         <td class="td_gris_l">
				                            <%=rb.getString("param.ldapUrl")%>&nbsp;&nbsp;&nbsp;(Ej. ldap://servidor:389):
				                        </td>
				                        <td>
				                            <html:text property="ldapUrl" size="40" maxlength="250" styleClass="classText"/> 
				                        </td>
				                    </tr>
	                         	</table>
                         	</fieldset>
                    	</td>
                    </tr>
                    
                </table>
                <br/>

        </td>
      </tr>
      <tr id="nodo2" class="none">
        <td width="86%" valign="top">

               <%-- LUIS CISNEROS 19/04/07 SACOP ES COMPRABLE --%>
                 <%if (ToolsHTML.showSACOP()){ %>
                    <table width="100%" border="0">
                    <tr>
                        <td class="td_gris_l" colspan="2">
                            <html:text property="diasAlertaAccionesSacopPorVencer" size="1" maxlength="2"/>
                            <%=rb.getString("scp.timeActionVenc")%>
                        </td>
                    </tr>

                    <tr>
                        <td class="td_gris_l" colspan="2">
                            <html:text property="numberDaysInactivitySACOP" size="1" maxlength="2"/>
                            <%=rb.getString("scp.numberDaysInactivitySACOP")%>
                        </td>
                    </tr>
                    <tr>
                        <td class="td_gris_l" colspan="2">
                            <html:text property="numberDaysWithoutActionSACOP" size="1" maxlength="2"/>
                            <%=rb.getString("scp.numberDaysWithoutActionSACOP")%>
                        </td>
                    </tr>
                    <tr>
                        <td class="td_gris_l" colspan="2">
                            <%=rb.getString("scp.listUserAddressee")%><br>
                            <% String cadAdd = ",".concat(data.getListUserAddressee()).concat(",");%>
                            <html:hidden styleId="listUserAddressee" property="listUserAddressee" />
                            <select name="cbolistUserAddressee" onchange="combinarValores(this)" class="classText" size="10" style="width:400px" multiple>
                            	<option value="">Todos los usuarios</option>
                                <logic:iterate id="listUserAddressee" name="listUserAddressee" type="com.desige.webDocuments.utils.beans.Search">
                                    <option value="<%=listUserAddressee.getId()%>" <%=cadAdd.indexOf(",".concat(listUserAddressee.getId()).concat(","))!=-1?"selected":""%>>
                                        <%=listUserAddressee.getDescript()%>
                                    </option>
                                </logic:iterate>
                            </select>
                            
                        </td>
                    </tr>

                    <tr>
                        <td class="td_gris_l" colspan="2">
                        	<br>
                        </td>
                    </tr>

                    <tr>
                        <td class="td_gris_l" colspan="2">
                         	<fieldset>
	                         	<legend>
		                            <%=ToolsHTML.showCheckBox("validitySacopI","updateCheckReverse(this.form.validitySacopI,this.form.validitySacop)",
		                                                       String.valueOf(data.getValiditySacop()),"1")%>
		                            <html:hidden property="validitySacop"/>&nbsp;
		                            <%=rb.getString("param.validitySacop")%>:
	                         	</legend>
	                            
	                            <html:select property="validitySacopMonth" styleClass="classText"  >
	                            	<html:option value="1">01</html:option>
	                            	<html:option value="2">02</html:option>
	                            	<html:option value="3">03</html:option>
	                            	<html:option value="4">04</html:option>
	                            	<html:option value="5">05</html:option>
	                            	<html:option value="6">06</html:option>
	                            	<html:option value="7">07</html:option>
	                            	<html:option value="8">08</html:option>
	                            	<html:option value="9">09</html:option>
	                            	<html:option value="10">10</html:option>
	                            	<html:option value="11">11</html:option>
	                            	<html:option value="12">12</html:option>
	                            </html:select>
	                            
	                            <%=ToolsHTML.getRadioButton(rb,"param.validitySacopType","validitySacopType",(int) data.getValiditySacopType(),null,0)%>
							</fieldset>
                        </td>
                    </tr>

                    <tr>
                        <td class="td_gris_l" colspan="2">
                            <%=ToolsHTML.showCheckBox("cargosacopI","updateCheck(this.form.cargosacopI,this.form.cargosacop)",
                                                       String.valueOf(data.getCargosacop()),"0")%>
                             <%--<%=ToolsHTML.showCheckBox("notifyEmailI","updateCheck(this.form.notifyEmailI,this.form.notifyEmail)",
                             String.valueOf(data.getNotifyEmail()),"0")%>--%>
                            <html:hidden property="cargosacop"/>
                           <%=rb.getString("param.cargomostrarsacop")%>&nbsp;<%=rb.getString("scp.sacop")%>
                        </td>
                    </tr>

                    <tr>
                        <td class="td_gris_l">
                        	<fieldset>
                        		<legend><%=rb.getString("scp.numerar")%></legend>
                        		<table cellspacing="0" cellpadding="0"  class="td_gris_l">
                        			<tr>
                        				<td width="200">
			                        		<input type="checkbox" value="1" <%=ns.isPrefijoSistema()?"checked":""%> disabled  onclick="validarNumberSacop(this,0)"> <%=rb.getString("scp.prefijo")%><br>
                        				</td>
                        				<td>
			                        		<input type="radio" name="renumerar" value="1" onclick="validarNumberSacop(this,6)"  onclick="validarNumberSacop(this,6)" <%=ns.isRenumerarSistema()?"checked":""%> ><%=rb.getString("scp.renumerar.p")%><br>
                        				</td>
                        			</tr>
                        			<tr>
                        				<td>
			                        		<input type="checkbox" value="1" <%=ns.isPrefijoArea()?"checked":""%> onclick="validarNumberSacop(this,1)"> <%=rb.getString("scp.prefijo.area")%><br>
                        				</td>
                        				<td>
			                        		<input type="radio" name="renumerar" value="2" onclick="validarNumberSacop(this,6)"  onclick="validarNumberSacop(this,6)" <%=ns.isRenumerarArea()?"checked":""%> <%=!ns.isPrefijoArea()?"disabled":""%> ><%=rb.getString("scp.renumerar.pa")%><br>
                        				</td>
                        			</tr>
                        			<tr>
                        				<td>
			                        		<input type="checkbox" name="prefijoAno" value="1" <%=ns.isPrefijoAno()?"checked":""%> onclick="validarNumberSacop(this,2)"> <%=rb.getString("scp.anio")%><br>
                        				</td>
                        				<td>
			                        		<input type="radio" name="renumerar" value="3" onclick="validarNumberSacop(this,6)"  onclick="validarNumberSacop(this,6)" <%=ns.isRenumerarAno()?"checked":""%> <%=!ns.isPrefijoAno()?"disabled":""%> ><%=rb.getString("scp.renumerar.paa")%><br>
                        				</td>
                        			</tr>
                        			<tr>
                        				<td>
			                        		<input type="checkbox" name="prefijoMes" value="1" <%=ns.isPrefijoMes()?"checked":""%> onclick="validarNumberSacop(this,3)"> <%=rb.getString("scp.mes")%><br>
                        				</td>
                        				<td>
			                        		<input type="radio" name="renumerar" value="4" onclick="validarNumberSacop(this,6)" <%=ns.isRenumerarMes()?"checked":""%> <%=!ns.isPrefijoMes()?"disabled":""%> ><%=rb.getString("scp.renumerar.paam")%><br>
                        				</td>
                        			</tr>
                        			<tr>
                        				<td>
			                        		<input type="checkbox" name="prefijoDia" value="1" <%=ns.isPrefijoDia()?"checked":""%> onclick="validarNumberSacop(this,4)"> <%=rb.getString("scp.dia")%><br>
                        				</td>
                        				<td>
			                        		<input type="radio" name="renumerar" value="5" onclick="validarNumberSacop(this,6)" <%=ns.isRenumerarDia()?"checked":""%> <%=!ns.isPrefijoDia()?"disabled":""%> ><%=rb.getString("scp.renumerar.paamd")%><br>
                        				</td>
                        			</tr>
                        			<tr>
                        				<td colspan="2">
			                        		<input type="checkbox" value="1" <%=ns.isCorrelativo()?"checked":""%> disabled  onclick="validarNumberSacop(this,5)"> <%=rb.getString("scp.correlativo")%><br> 
	                            <html:hidden property="numberSacop"/>
                        				</td>
                        			</tr>
                        		</table>
                            </fieldset>
                        </td>
                    </tr>
                    </table>
                    <br/>
                    
                    <%} else {%>
                        <input type="hidden" name="cargosacopI" id="cargosacopI" value="1" />
                        <input type="hidden" name="cargosacop" id="cargosacop" value="1" />
                    <%}%>

        </td>
      </tr>
      <tr id="nodo3" class="none">
        <td width="86%" valign="top">

                    <table width="100%" border="0">
                    <tr>
                        <td class="td_gris_l" colspan="2">
                            <%=ToolsHTML.showCheckBox("notifyEmailI","updateCheck(this.form.notifyEmailI,this.form.notifyEmail)",
                                                       String.valueOf(data.getNotifyEmail()),"0")%>
                            <html:hidden property="notifyEmail"/>
                            <%=rb.getString("param.notifyEmail")%>
                        </td>
                    </tr>
                        <!--Luis Cisneros-->
                        <!--Campo para validar si el mail debe estar en un formato valido o no-->
                    <tr>
                        <td class="td_gris_l" colspan="2">
                            <%=ToolsHTML.showCheckBox("validateEmailI","updateCheck(this.form.validateEmailI,this.form.validateEmail)",
                                                       String.valueOf(data.getValidateEmail()),"0")%>
                            <html:hidden property="validateEmail"/>
                            <%=rb.getString("param.validateEmail")%>
                        </td>
                    </tr>
                    <tr>
                        <td class="td_gris_l" colspan="2">
                            <%=ToolsHTML.showCheckBox("debugEmailI","updateCheckReverse(this.form.debugEmailI,this.form.debugEmail)",
                                                       data.getDebugEmail(),"1")%>
                            <html:hidden property="debugEmail"/>
                            <%=rb.getString("param.debugEmail")%>
                        </td>
                    </tr>

                    <tr>
                        <td class="td_gris_l" colspan="2">&nbsp;</td>
                    </tr>

                    <tr>
                        <td class="td_gris_l" width="50%">
                            <%=rb.getString("param.domainMail")%>:
                        </td>
                        <td>
                            <html:text property="domainEmail" size="30" maxlength="250" styleClass="classText"/>
                        </td>
                    </tr>

                    <tr>
                        <td class="td_gris_l">
                            <%=rb.getString("param.serverMail")%>:
                        </td>
                        <td>
                            <html:text property="smtpMail" size="30" styleClass="classText"/>
                        </td>
                    </tr>
                    <tr>
                        <td class="td_gris_l" width="50%">
                            <%=rb.getString("param.portMail")%>:
                        </td>
                        <td>
                            <html:text property="portEmail" size="30" maxlength="250" styleClass="classText"/>
                        </td>
                    </tr>

                    <tr>
                        <td class="td_gris_l">
                            <%=rb.getString("param.mailAccount")%>:
                        </td>
                        <td>
                            <html:text property="mailAccount" size="30" maxlength="200" styleClass="classText"/>
                        </td>
                    </tr>
                    
                    <tr>
                        <td class="td_gris_l">
                            <%=rb.getString("param.passwordMail")%>:
                        </td>
                        <td>
                            <html:password property="passwordEmail" size="30" maxlength="200" styleClass="classText"/>
                        </td>
                    </tr>
                    
                    <tr>
                        <td class="td_gris_l">
                            <%=rb.getString("param.authProtocolMail")%>:
                        </td>
                        <td>
                            <html:select property="authProtocolEmail" styleClass="classText">
                                <html:option value="NO_AUTH"><%=rb.getString("param.noAuthMail")%></html:option>
                                <html:option value="SSL">SSL</html:option>
                                <html:option value="TLS">TLS</html:option>
                            </html:select>
                        </td>
                    </tr>

                    <tr>
                        <td class="td_gris_l">
                            <%=rb.getString("param.postfixMail")%>:
                        </td>
                        <td>
                            <html:text property="postfixMail" size="30" styleClass="classText"/>
                        </td>
                    </tr>

                   </table>
        </td>
      </tr>
      <tr id="nodo4" class="none" valign="top">
        <td width="86%" valign="top">

                    <table width="100%" border="0">
                    <!-- SIMON 16 DE JUNIO 2005 2_04PM INICIO -->
                    <tr>
                         <td class="td_gris_l" width="50%">
                            <%=ToolsHTML.showCheckBox("versionComI","updateCheck(this.form.versionComI,this.form.versionCom)",
                                                       String.valueOf(data.getVersionCom()),"0")%>
                            <%=rb.getString("param.coment")%>:
                         </td>
                        <td>
                            <html:hidden property="versionCom"/>
                        </td>
                    </tr>
                    <html:hidden property="logDoc"/>
                    
                    <!-- Permisologia para el cambios de nombre y propietario de un documento aprobado -->
                    <tr>
                        <td class="td_gris_l" colspan="2">
                            <%=ToolsHTML.showCheckBox("changePropertiesI","updateCheckReverse(this.form.changePropertiesI,this.form.changeProperties)",
                                                       String.valueOf(data.getChangeProperties()),"1")%>
                            <html:hidden property="changeProperties"/>                            
                            <%=rb.getString("param.changePropertiesApproved")%>
                        </td>
                    </tr>

                    <tr>
                         <td class="td_gris_l">
                            <%=ToolsHTML.showCheckBox("allowWFI","updateCheck(this.form.allowWFI,this.form.allowWF)",
                                                       String.valueOf(data.getAllowWF()),"0")%>
                            <%=rb.getString("param.allowWF")%>
                         </td>
                        <td>
                            <html:hidden property="allowWF"/>
                        </td>
                    </tr>

                    <tr>
                         <td class="td_gris_l">
                            <%=ToolsHTML.showCheckBox("expireDocI","updateCheck(this.form.expireDocI,this.form.expireDoc)",
                                                       String.valueOf(data.getExpireDoc()),"0")%>
                            <%=rb.getString("param.dateExpire")%>
                        </td>
                        <td class="td_gris_l">
                            <html:hidden property="expireDoc"/>
                            <%=ToolsHTML.getSelectList(rb,"param.monthExpireDoc","monthsExpireDocs",data.getMonthsExpireDocs())%>
                            <%=ToolsHTML.getSelectList(rb,"param.vigItems","param.vigItemsValue","unitTimeExpire",data.getUnitTimeExpire())%>
                        </td>
                    </tr>

                    <tr>
                         <td class="td_gris_l">
                            <%=ToolsHTML.showCheckBox("expireDraftsI","updateCheck(this.form.expireDraftsI,this.form.expireDrafts)",
                                          String.valueOf(data.getExpireDrafts()),"0")%>
                           <%=rb.getString("param.dateDrafts")%>
                        </td>
                        <td class="td_gris_l">
                            <html:hidden property="expireDrafts"/>
                             <%=ToolsHTML.getSelectList(rb,"param.monthExpireDoc","monthsExpireDrafts",data.getMonthsExpireDrafts())%>
                             <%=ToolsHTML.getSelectList(rb,"param.vigItems","param.vigItemsValue","unitTimeExpireDrafts",data.getUnitTimeExpireDrafts())%>
                        </td>
                    </tr>
                    
                    <tr>
                         <td class="td_gris_l">
                            <%=ToolsHTML.showCheckBox("deadDocI","updateCheck(this.form.deadDocI,this.form.deadDoc)",
                                                       String.valueOf(data.getDeadDoc()),"0")%>
                            <%=rb.getString("param.dateDead")%>
                        </td>
                        <td class="td_gris_l">
                            <html:hidden property="deadDoc"/>
                            <%=ToolsHTML.getSelectList(rb,"param.monthExpireDoc","monthsDeadDocs",data.getMonthsDeadDocs())%>
                            <%=ToolsHTML.getSelectList(rb,"param.vigItems","param.vigItemsValue","unitTimeDead",data.getUnitTimeDead())%>
                        </td>
                    </tr>

                    <tr>
                        <td class="td_gris_l" colspan="2">&nbsp;
                        </td>
                    </tr>
                    

                    <tr>
                        <td class="td_gris_l" colspan="2">
                        	<fieldset>
                        		<legend><%=rb.getString("param.quality")%></legend>
                            <%=ToolsHTML.getRadioButton(rb,"param.numQuality","qualitySystem",(int) data.getQualitySystem(),null).replaceAll("<br/>","&nbsp;&nbsp;&nbsp;&nbsp;")%>
                            </fieldset>
                        </td>
                    </tr>

                    <logic:equal name="data" property="typeNumByLocation" value="false">
                        <tr>
	                        <td class="td_gris_l" colspan="2">
	                        	<fieldset>
	                        		<legend><%=rb.getString("param.genNumber")%></legend>
	                                <%=rb.getString("param.lengthDocNumber")+" "%>&nbsp;<html:text property="lengthDocNumber" size="2" styleClass="classText"/>
	                                <br/>
	                                <%=rb.getString("param.resetDocNumber")%>&nbsp;<html:text property="resetDocNumber" size="2" styleClass="classText"/>
		                        </fieldset>
                            </td>
                        </tr>
	
                        <tr>
	                        <td class="td_gris_l" colspan="2">
	                        	<fieldset>
	                        		<legend><%=rb.getString("param.numDocLocation")%></legend>
	                                <%=ToolsHTML.getRadioButton(rb,"param.numDocLoc","numDocLoc",
	                                                            data.getNumDocLoc(),null)%>
	                                <html:hidden property="numDocLoc"/>
		                        </fieldset>
                            </td>
                        </tr>
                      </logic:equal>
                      <logic:notEqual name="data" property="typeNumByLocation" value="false">
                        <tr>
	                        <td class="td_gris_l" colspan="2">
	                        	<fieldset>
	                        		<legend><%=rb.getString("param.genNumber")%></legend>
	                                <%=rb.getString("param.lengthDocNumber")+" "%>&nbsp;<html:text property="lengthDocNumber" size="2" disabled="true" styleClass="classText"/>
	                                <br/>
	                                <%=rb.getString("param.resetDocNumber")%>&nbsp;<html:text property="resetDocNumber" disabled="true" size="2" styleClass="classText"/>
		                        </fieldset>
                            </td>
                        </tr>
                        <tr>
	                        <td class="td_gris_l" colspan="2">
	                        	<fieldset>
	                        		<legend><%=rb.getString("param.numDocLocation")%></legend>
	                                <%=ToolsHTML.getRadioButton(rb,"param.numDocLoc","numDocLoc",
	                                                            data.getNumDocLoc(),null,true).replaceAll("<br/>","&nbsp;&nbsp;&nbsp;&nbsp;")%>
	                                <html:hidden property="numDocLoc"/>
	                            </fieldset>
                            </td>
                        </tr>
                    </logic:notEqual>

                    <!-- Tipo de Ordenamiento a Usar por el usuario -->
                    <tr>
                        <td class="td_gris_l" colspan="2">
                        	<fieldset>
                        		<legend><%=rb.getString("param.typeOrderTxt")%></legend>
	                            <%=ToolsHTML.getRadioButton(rb,"param.typeOrder","typeOrder",
	                                                            data.getTypeOrder(),null,false).replaceAll("<br/>","&nbsp;&nbsp;&nbsp;&nbsp;")%>
	                            <html:hidden property="typeOrder"/>                            
	                        </fieldset>
                        </td>
                    </tr>
                                        
					</table>
					
        </td>
      </tr>
      <tr id="nodo5" class="none">
        <td width="86%" valign="top">
					<%if (ToolsHTML.showFTP()) {%>
                    <table width="100%" border="0">
                    <!-- JRivero -->
                    <!--Se provee un flujo parametrico especial para completar flujos no terminados-->
                    <tr>
                         <td class="td_gris_l" width="90%">
                            <%=ToolsHTML.showCheckBox("completeFTPI","updateCheckReverse(this.form.completeFTPI,this.form.completeFTP)",
                                                       String.valueOf(data.getCompleteFTP()),"1")%>
                            <html:hidden property="completeFTP"/>
                            <%=rb.getString("param.completeFTP")%>
                        </td>
                        <td>
                        </td>
                    </tr>

                    <!-- YSA 02/07/07 -->
                    <!--Permite definir si se desea que en un FTP cancelado se reinicie o no el flujo en un firmante o actividad determinada-->
                    <tr>
                         <td class="td_gris_l">
                            <%=ToolsHTML.showCheckBox("reasigneUserFTPI","updateCheckReverse(this.form.reasigneUserFTPI,this.form.reasigneUserFTP)",
                                                       String.valueOf(data.getReasigneUserFTP()),"1")%>
                            <html:hidden property="reasigneUserFTP"/>
                            <%=rb.getString("param.resigneUserFTP")%>
                        </td>
                        <td>
                        </td>
                    </tr>

                    <tr>
                         <td class="td_gris_l">
		                    <!--Permite habilitar la opcion crear documentos de rechazo en los FTP-->
                            <%=ToolsHTML.showCheckBox("documentRejectI","updateCheckReverse(this.form.documentRejectI,this.form.documentReject)",
                                                       String.valueOf(data.getDocumentReject()),"1")%>
                            <html:hidden property="documentReject"/>
                            <%=rb.getString("param.documentReject")%>
                        </td>
                        <td>
                        </td>
                    </tr>


                    <tr>
                         <td class="td_gris_l" colspan="2">
                            <%=ToolsHTML.showCheckBox("copyContentsI","updateCheckReverse(this.form.copyContentsI,this.form.copyContents)",
                                                       String.valueOf(data.getCopyContents()),"1")%>
                            <html:hidden property="copyContents"/>
                            <%=rb.getString("param.copyContents")%>
                        </td>
                    </tr>

                    <tr>
                         <td class="td_gris_l" colspan="2">
                            <%=ToolsHTML.showCheckBox("editOriginatorWFI","updateCheckReverse(this.form.editOriginatorWFI,this.form.editOriginatorWF)",
                                                       String.valueOf(data.getEditOriginatorWF()),"1")%>
                            <html:hidden property="editOriginatorWF"/>
                            <%=rb.getString("param.editOriginatorWF")%>
                        </td>
                    </tr>

              		<%--ydavila Ticket 001-00-003023 param.autenticarflujo --%>
				    <tr>
                       <td class="td_gris_l">
                            <%=ToolsHTML.showCheckBox("autenticarflujoI","updateCheckReverse(this.form.autenticarflujoI,this.form.autenticarflujo)",
                            String.valueOf(data.getautenticarflujo()),"1")%>
                            <html:hidden property="autenticarflujo"/>
                            <%=rb.getString("param.autenticarflujo")%> 
                        </td>
                     </tr>   

                    <!-- dias de vigencia de flujos cancelados en la pestaÃ±a principal -->
                    <tr>
                         <td class="td_gris_l" colspan="2">
                           <%=rb.getString("param.vigenciaFlujosCanceladosPrincipal")%>&nbsp;:&nbsp;
                            <html:select property="vigenciaFlujosCanceladosPrincipal" styleClass="classText">
                            <%
                            for(int days=1; days < 61; days++){
                            %>
                                <option value="<%= days %>" <%= data.getVigenciaFlujosCanceladosPrincipal() == days ? " selected" : "" %>><%= days %></option>
                            <%
                            }
                            %>
                            </html:select>
                        </td>
                    </tr>
                    
					<%--ydavila Ticket 001-00-003023 Responsable Solicitudes de Cambio --%>                 	                  
				    <tr> 
						 <td class="td_gris_l" colspan="2"> <br/> 
                            <%=rb.getString("cbs.solicitudcambio")%>          
                            <%sun.jdbc.rowset.CachedRowSet crsUsuario = com.desige.webDocuments.persistent.managers.HandlerDBUser.getListUsersByAreaCargo();%>
                            <tr> 
						      <td class="td_gris_l" colspan="2"> 
                                <html:select property="respsolcambio" styleClass="classText">
                                  <option value="">&nbsp;</option>
                                  <%while(crsUsuario.next()){%>
                                    <html:option value='<%=crsUsuario.getString("nameUser")%>'><%=rb.getString("user.area2")%><%=crsUsuario.getString("area")%> - <%=rb.getString("user.cargo2")%><%=crsUsuario.getString("Cargo")%> - <%=rb.getString("session.usuario2")%>:<%=crsUsuario.getString("apellidos")%> <%=crsUsuario.getString("nombres")%></html:option>
                                  <%}%>
                                </html:select>
                             </td>
                           </tr> 
                         </td>
                     </tr> 
                    <%--ydavila Ticket 001-00-003023 Responsable Solicitudes de EliminaciÃ³n --%>                 	                  
				    <tr> 
					  <td class="td_gris_l" colspan="2"><br/> 						
                          <%=rb.getString("cbs.solicitudeliminacion")%>
                          <%crsUsuario.beforeFirst();%>
                            <tr> 
						      <td class="td_gris_l" colspan="2"> <html:select property="respsolelimin" styleClass="classText">
                                <option value="">&nbsp;</option>
                                <%while(crsUsuario.next()){%>
                                 <html:option value='<%=crsUsuario.getString("nameUser")%>'><%=rb.getString("user.area2")%><%=crsUsuario.getString("area")%> - <%=rb.getString("user.cargo2")%><%=crsUsuario.getString("Cargo")%> - <%=rb.getString("session.usuario2")%>:<%=crsUsuario.getString("apellidos")%> <%=crsUsuario.getString("nombres")%></html:option>
                                <%}%>
                                 </html:select>
                             </td>
                           </tr> 
                         </td>
                      </tr>   
                      <%--ydavila Ticket 001-00-003023 Responsable Solicitudes de ImpresiÃ³n --%>                 	                  
				      <tr> 
					    <td class="td_gris_l" colspan="2"><br/> 						
                          <%=rb.getString("cbs.solicitudimpresion")%>
                          <%crsUsuario.beforeFirst();%>
                            <tr> 
						      <td class="td_gris_l" colspan="2"> <html:select property="respsolimpres" styleClass="classText">
                                <option value="">&nbsp;</option>
                                <%while(crsUsuario.next()){%>
                                 <html:option value='<%=crsUsuario.getString("nameUser")%>'><%=rb.getString("user.area2")%><%=crsUsuario.getString("area")%> - <%=rb.getString("user.cargo2")%><%=crsUsuario.getString("Cargo")%> - <%=rb.getString("session.usuario2")%>:<%=crsUsuario.getString("apellidos")%> <%=crsUsuario.getString("nombres")%></html:option>
                                <%}%>
                                 </html:select>
                             </td>
                           </tr> 
                         </td>
                      </tr>     
					</table>
					<%}%>
					
        </td>
      </tr>
      <tr id="nodo6" class="none">
        <td width="86%" valign="top">
                    <table width="100%" border="0">
                    <!--Luis Cisneros-->
                    <!--23-04-07-->
                    <!--Se permite la libre impresion al Administrador y al dueÃ±o del documento?-->
                    <tr>
                         <td class="td_gris_l">
                            <%=ToolsHTML.showCheckBox("printOwnerAdminI","updateCheck(this.form.printOwnerAdminI,this.form.printOwnerAdmin)",
                                                       String.valueOf(data.getPrintOwnerAdmin()),"0")%>
                            <html:hidden property="printOwnerAdmin"/>
                            <%=rb.getString("param.printOwnerAdmin")%>:
                        </td>
                        <td rowspan="7" align="right" width="1%">
                        	<div style="overflow:auto;width:200px;height:200px;border:0px solid #efefef;">
	                        	<fieldset>
	                        	<legend class="td_gris_l">Imagen (Max. sugerido 100x100)</legend>
	                        		<img id="logoEmpresa" src="./img/logos/empresa.gif" border="1"/>
	                        	</fieldset>
                        	</div>
                        </td>
                    </tr>

                    <!-- YSA 01/08/08 -->
                    <!--Se permite seleccionar la opcion para imprimir o no el creador de un documento-->
                    <tr>
						<td class="td_gris_l">
                            <%=ToolsHTML.showCheckBox("viewCreatorI","updateCheckReverse(this.form.viewCreatorI,this.form.viewCreator)",
                                                       String.valueOf(data.getViewCreator()),"1")%>
                            <html:hidden property="viewCreator"/>
                            <%=rb.getString("param.creator")%>
                        </td>
                    </tr>

                    <!--Se permite seleccionar la opcion para imprimir o no el editor de un documento-->
                    <tr>
						<td class="td_gris_l">
                            <%=ToolsHTML.showCheckBox("viewCollaboratorI","updateCheckReverse(this.form.viewCollaboratorI,this.form.viewCollaborator)",
                                                       String.valueOf(data.getViewCollaborator()),"1")%>
                            <html:hidden property="viewCollaborator"/>
                            <%=rb.getString("param.collaborator")%>
                        </td>
                    </tr>
                    
                    <!-- YSA 15/06/07 -->
                    <!--Se permite seleccionar la opcion para imprimir o no los revisores de un documento-->
                    <tr>
						<td class="td_gris_l">
                            <%=ToolsHTML.showCheckBox("viewReadersI","updateCheckReverse(this.form.viewReadersI,this.form.viewReaders)",
                                                       String.valueOf(data.getViewReaders()),"1")%>
                            <html:hidden property="viewReaders"/>
                            <%=rb.getString("param.readers")%>
                        </td>
                    </tr>
                    
                    <tr>
                         <td class="td_gris_l">
                            <%=ToolsHTML.showCheckBox("printNumberI","updateCheckReverse(this.form.printNumberI,this.form.printNumber)",
                                                       String.valueOf(data.getPrintNumber()),"1")%>
                            <html:hidden property="printNumber"/>
                            <%=rb.getString("param.printNumber")%>
                        </td>
                    </tr>
					
					<!--   permite especificar si qwebdocuments imprimira la version en los documentos  -->
                    <tr>
                         <td class="td_gris_l">
                            <%=ToolsHTML.showCheckBox("printVersionI","updateCheckReverse(this.form.printVersionI,this.form.printVersion)",
                                                       String.valueOf(data.getPrintVersion()),"1")%>
                            <html:hidden property="printVersion"/>
                            <%=rb.getString("param.printVersion")%>
                        </td>
                    </tr>
                    
                    <tr>
                         <td class="td_gris_l">
                            <%=ToolsHTML.showCheckBox("printApprovedDateI","updateCheckReverse(this.form.printApprovedDateI,this.form.printApprovedDate)",
                                                       String.valueOf(data.getPrintApprovedDate()),"1")%>
                            <html:hidden property="printApprovedDate"/>
                            <%=rb.getString("param.printApprovedDate")%>
                        </td>
                    </tr>
                    
                    <tr>
                         <td class="td_gris_l">
                            <%=ToolsHTML.showCheckBox("piePaginaWaterMarkI","updateCheckReverse(this.form.piePaginaWaterMarkI,this.form.piePaginaWaterMark)",
                                                       String.valueOf(data.getPiePaginaWaterMark()),"1")%>
                            <html:hidden property="piePaginaWaterMark"/>
                            <%=rb.getString("param.printPiePaginaWaterMark")%>
                        </td>
                    </tr>
                    
                    <tr>
                         <td class="td_gris_l" style="display:none;">
                            <%=ToolsHTML.showCheckBox("disabledCacheI","updateCheckReverse(this.form.disabledCacheI,this.form.disabledCache)",
                                                       String.valueOf(data.getDisabledCache()),"1")%>
                            <html:hidden property="disabledCache"/>
                            <%=rb.getString("param.convertAlways")%>
                        </td>
                    </tr>

                    <tr>
						<td class="td_gris_l" colspan="2">&nbsp;
                        </td>
                    </tr>
                    
                    <!--   permite especificar si qwebdocuments sera usado utilizando office o openoffice  -->
                    <tr>
                         <td class="td_gris_l" colspan="2">
                         	<fieldset>
	                         	<legend>
		                            <%=ToolsHTML.showCheckBox("openofficeI","updateCheckReverse(this.form.openofficeI,this.form.openoffice)",
		                                                       String.valueOf(data.getOpenoffice()),"1")%>
		                            <html:hidden property="openoffice"/>&nbsp;
		                            <%=rb.getString("param.openoffice")%>:
		                         </legend>   
		                         <legend>
		 <%-- GRG 23 agosto 2020, se agrega texto para facilitar parametrizacion --%>  
		               <%--              <html:hidden property="openoffice"/>&nbsp; --%>
		                            <%=rb.getString("param.openofficeconvertidor")%>:
	                         	</legend>
	 	
	                            <%=ToolsHTML.getRadioButton(rb,"param.numViewPdfTool","viewpdftool",(int) data.getViewpdftool(),null,0)%>
	                            <html:text property="oooExeFolder" size="60" maxlength="300" styleClass="classText" onchange="this.value=this.value.replace(/\134/g,'/')"/>
							</fieldset>
                        </td>
                    </tr>
                    

                    <tr>
						<td class="td_gris_l" colspan="2">&nbsp;
                        </td>
                    </tr>

                    <!-- YSA 01/08/07 -->
                    <!--Se permite seleccionar valores para impresion-->
                    <tr>
						<td class="td_gris_l" colspan="2">
							<fieldset>
								<legend><%=rb.getString("param.showNameCharge")%></legend>
	                               <%=ToolsHTML.getRadioButton(rb,"param.impNameCharge","impNameCharge",
	                                                           data.getImpNameCharge(),null,false).replace("<br/>","&nbsp;&nbsp;&nbsp;")%>
	                              <html:hidden property="impNameCharge"/>
	                        </fieldset>
                        </td>                        
                    </tr>


                    <tr>
						<td class="td_gris_l" colspan="2">&nbsp;
                        </td>
                    </tr>

                    <!-- YSA 03/08/07 -->
                    <!--Valores para el encabezado de impresion-->
                    <tr>
						<td class="td_gris_l" colspan="2">
							<fieldset>
								<legend><%=rb.getString("param.headImp")%></legend>
								<table border="0" width="100%" align="left">
								<tr>
									<td class="td_gris_l" width="80"><%=rb.getString("param.headImp1")%></td>
									<td><html:text property="headImp1" size="60" maxlength="200" styleClass="classText"/></td>
								</tr>
								<tr>
									<td class="td_gris_l" ><%=rb.getString("param.headImp2")%></td>
									<td><html:text property="headImp2" size="25" maxlength="50" styleClass="classText"/></td>
								</tr>
								<tr>
									<td class="td_gris_l" ><%=rb.getString("param.headImp3")%></td>
									<td><html:text property="headImp3" size="25" maxlength="50" styleClass="classText"/></td>
								</tr>
								<tr>
									<td class="td_gris_l">Imagen (.gif)</td>
									<td><iframe src="./uploadImage.jsp" frameborder="0" width="350" height="50" scrolling="no"></iframe></td>
								</tr>
								</table>
							</fieldset>
                        </td>                        
                    </tr>

					</table>
                    <br/>
					
        </td>
      </tr>
      <tr id="nodo7" class="none">
        <td width="86%" valign="top">
                    <table width="100%" border="0">
                    <tr>
                         <td class="td_gris_l" colspan="2">
	                    <!-- YSA 20/07/07 -->
	                    <!--Permite habilitar opcion para bajar ultima version de un documento-->
                            <%=ToolsHTML.showCheckBox("downloadLastVerI","updateCheckReverse(this.form.downloadLastVerI,this.form.downloadLastVer)",
                                                       String.valueOf(data.getDownloadLastVer()),"1")%>
                            <html:hidden property="downloadLastVer"/>
                            <%=rb.getString("param.downloadLastVer")%>
                        </td>
                    </tr>




					<!--   permite eliminar la seguridad implementada para el IE6                    -->
                    <tr style="display:none;">
                         <td class="td_gris_l" colspan="2">
                            <%=ToolsHTML.showCheckBox("fileNotOpenSaveI","updateCheckReverse(this.form.fileNotOpenSaveI,this.form.fileNotOpenSave)",
                                                       String.valueOf(data.getFileNotOpenSave()),"1")%>
                            <html:hidden property="fileNotOpenSave"/>
                            <%=rb.getString("param.fileNotOpenSave")%>
                        </td>
                    </tr>


					<!--   permite especificar los tipos de archivo no manejados por qweb y que permiten subir una segundo formato de archivo   -->
                    <tr>
                         <td class="td_gris_l" width="50%">
                            <%=rb.getString("param.fileDoubleVersion")%>:
                        </td>
                        <td>
                            <html:text property="fileDoubleVersion" size="25" maxlength="50" styleClass="classText"/>
                        </td>
                    </tr>

					<!--   permite especificar los tipos de archivo no editables por qweb y que permiten subir la version completa del archivo   -->
                    <tr>
                         <td class="td_gris_l" width="50%">
                            <%=rb.getString("param.fileUploadVersion")%>:
                        </td>
                        <td>
                            <html:text property="fileUploadVersion" size="25" maxlength="50" styleClass="classText"/>
                        </td>
                    </tr>

                    <tr>
                         <td class="td_gris_l" width="50%">
                            <%=rb.getString("param.extensionsInNativeViewer")%>:
                        </td>
                        <td>
                            <html:text property="fileNativeViewer" size="25" maxlength="50" styleClass="classText"/>
                        </td>
                    </tr>

                    <tr>
                         <td class="td_gris_l" width="50%">
                            <%=rb.getString("param.folderCmp")%>:
                        </td>
                        <td>
                            <html:text property="folderCmp" size="30" maxlength="250" styleClass="classText"/>
                        </td>
                    </tr>
                    
                    <tr>
                         <td class="td_gris_l">
                            <%=rb.getString("param.repository")%>:
                        </td>
                        <td>
                            <html:text property="repository" size="30" maxlength="250" styleClass="classText" onchange="this.value=this.value.replace(/\134/g,'/')" />
                        </td>
                    </tr>

					<!--   permite especificar si qwebdocuments trabajara en modo editor externo  -->
                    <tr style="display:none">
                         <td class="td_gris_l" colspan="2">
                            <%=ToolsHTML.showCheckBox("editorOnDemandI","updateCheckReverse(this.form.editorOnDemandI,this.form.editorOnDemand)",
                                                       String.valueOf(data.getEditorOnDemand()),"1")%>
                            <html:hidden property="editorOnDemand"/>
                            <%=rb.getString("param.editorOnDemand")%>
                        </td>
                    </tr>

<%--  ydavila Deshabilitar idioma en AdministraciÃ³n  
 				<!-- Idioma por defecto del usuario -->
                    <tr>
                         <td class="td_gris_l" >
                           <%=rb.getString("param.idiomasTxt")%>
                        </td>
                        <td class="td_gris_l">
                            
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
--%>                   
  
 <!--  ydavila Deshabilitar idioma en AdministraciÃ³n -->                  
                    
					
					<!-- Desincorporacion de documentos -->
					<!--
	                <tr>
                        <td colspan="2"><br/></td>
                    </tr>  
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
                    -->
                    
                    <tr>
                         <td class="td_gris_l" width="50%">
                            <%=rb.getString("param.attachedFolder0")%>:
                        </td>
                        <td>
                            <html:text property="attachedFolder0" size="30" maxlength="250" styleClass="classText" onchange="this.value=this.value.replace(/\134/g,'/')" />
                        </td>
                    </tr>
                    
                    <tr>
                         <td class="td_gris_l" width="50%">
                            <%=rb.getString("param.attachedFolder1")%>:
                        </td>
                        <td>
                            <html:text property="attachedFolder1" size="30" maxlength="250" styleClass="classText" onchange="this.value=this.value.replace(/\134/g,'/')" />
                        </td>
                    </tr>

                    <tr>
                         <td class="td_gris_l" width="50%">
                            <%=rb.getString("param.attachedField")%>:
                        </td>
                        <td class="td_gris_l">
                            <html:select property="attachedField" styleClass="classText">
                                    <html:option value=""></html:option>
                                    <html:option value="0"><%=rb.getString("param.attachedField0")%></html:option>
                                    <html:option value="1"><%=rb.getString("param.attachedField1")%></html:option>
                            </html:select>
                        </td>
                    </tr>

                    <tr>
                         <td class="td_gris_l" width="50%">
                            <%=rb.getString("param.publicEraser")%>:
                        </td>
                        <td class="td_gris_l">
                            <html:select property="publicEraser" styleClass="classText">
                                    <html:option value="0"><%=rb.getString("sistema.no")%></html:option>
                                    <html:option value="1"><%=rb.getString("sistema.yes")%></html:option>
                            </html:select>
                        </td>
                    </tr>

                    <tr>
                         <td class="td_gris_l" width="50%">
                            <%=rb.getString("param.changeAditionalField")%>:
                        </td>
                        <td class="td_gris_l">
                            <html:select property="changeAditionalField" styleClass="classText">
                                    <html:option value="0"><%=rb.getString("param.changeAditionalField0")%></html:option>
                                    <html:option value="1"><%=rb.getString("param.changeAditionalField1")%></html:option>
                            </html:select>
                        </td>
                    </tr>


                    <tr>
						<td class="td_gris_l" colspan="2">&nbsp;
                        </td>
                    </tr>

                    <tr>
                         <td class="td_gris_l" colspan="2">
                            <fieldset>
	                         	<legend><%=rb.getString("param.idNormAudit")%>:</legend>
		                         <html:select property="idNormAudit" style="width:100%">
		                         	<option value="0" > </option>
		                         	<%if(listaNormas!=null) {
		                         		for( java.util.Iterator<Search> i=listaNormas.iterator(); i.hasNext(); ){
		                         		search =  i.next();%>
		                         		<option value="<%=search.getId()%>" <%=String.valueOf(data.getIdNormAudit()).equals(search.getId())?"selected":""%>><%=search.getDescript()%></option>
		                         		<%}
		                         	}%>
		                         </html:select>
	                        </fieldset>
                    </tr>

					</table>
        </td>
      </tr>
      
      <tr id="nodo8" class="none">
        <td width="86%" valign="top">
            <table class="clsTableTitle" width="110" cellSpacing="0" cellPadding="0" align="center" border="0">
             <html:textarea property="msgDocExpirado" style="display:none" />
             <tr>
                  <td colspan="2" heigth="26" class="titleCenter" bgcolor="#7396b5">
                      <%=rb.getString("param.msgDOCExpirados")%>
                  </td>
             </tr>
             <tr>
                  <td class="fondoEditor" colspan="2" valign="top">
                  		<div style="padding:20;height:300;border:1px dotted blue;" 
                  			onclick="window.open('parametersMessage.jsp?numero=1','frmMsgDocExpirado');this.style.display='none';document.getElementById('frmMsgDocExpirado').style.display='';">
                  			<%=String.valueOf(data.getMsgDocExpirado())%>
                  		</div>
                  		<iframe id="frmMsgDocExpirado" name='frmMsgDocExpirado' width="100%" style="height:300px;display:none;" frameborder="none" ></iframe>
                  </td>
             </tr>
             </table>
        </td>
      </tr>
      
      <tr id="nodo9" class="none">
        <td width="86%" valign="top">
            <table class="clsTableTitle" width="110" cellSpacing="0" cellPadding="0" align="center" border="0">
             <html:textarea property="msgWFBorrador" style="display:none" />
             <tr>
                  <td colspan="2" heigth="26" class="titleCenter" bgcolor="#7396b5">
                      <%=rb.getString("param.msgWFBorrador")%>
                  </td>
             </tr>
             <tr>
                  <td class="fondoEditor" colspan="2" valign="top">
                  		<div style="padding:20;height:300;border:1px dotted blue;" 
                  			onclick="window.open('parametersMessage.jsp?numero=2','frmMsgWFBorrador');this.style.display='none';document.getElementById('frmMsgWFBorrador').style.display='';">
                  			<%=String.valueOf(data.getMsgWFBorrador())%>
                  		</div>
                  		<iframe id="frmMsgWFBorrador" name='frmMsgWFBorrador' width="100%" style="height:300px;display:none;" frameborder="nones" ></iframe>
                  </td>
             </tr>
             </table>
        </td>
      </tr>
      
      <tr id="nodo10" class="none">
        <td width="86%" valign="top">
            <table class="clsTableTitle" width="110" cellSpacing="0" cellPadding="0" align="center" border="0">
             <html:textarea property="msgWFExpires" style="display:none" />
             <tr>
                  <td colspan="2" heigth="26" class="titleCenter" bgcolor="#7396b5">
                      <%=rb.getString("param.msgWFExpires")%>
                  </td>
             </tr>
             <tr>
                  <td class="fondoEditor" colspan="2" valign="top">
                  		<div style="padding:20;height:300;border:1px dotted blue;" 
                  			onclick="window.open('parametersMessage.jsp?numero=3','frmMsgWFExpires');this.style.display='none';document.getElementById('frmMsgWFExpires').style.display='';">
                  			<%=String.valueOf(data.getMsgWFExpires())%>
                  		</div>
                  		<iframe id="frmMsgWFExpires" name='frmMsgWFExpires' width="100%" style="height:300px;display:none;" frameborder="nones" ></iframe>
                  </td>
             </tr>
             </table>
        </td>
      </tr>
      
      <tr id="nodo11" class="none">
        <td width="86%" valign="top">
            <table class="clsTableTitle" width="110" cellSpacing="0" cellPadding="0" align="center" border="0">
             <html:textarea property="msgWFRevision" style="display:none" />
             <tr>
                  <td colspan="2" heigth="26" class="titleCenter" bgcolor="#7396b5">
                      <%=rb.getString("param.msgWFRevision")%>
                  </td>
             </tr>
             <tr>
                  <td class="fondoEditor" colspan="2" valign="top">
                  		<div style="padding:20;height:300;border:1px dotted blue;" 
                  			onclick="window.open('parametersMessage.jsp?numero=4','frmMsgWFRevision');this.style.display='none';document.getElementById('frmMsgWFRevision').style.display='';">
                  			<%=String.valueOf(data.getMsgWFRevision())%>
                  		</div>
                  		<iframe id="frmMsgWFRevision" name='frmMsgWFRevision' width="100%" style="height:300px;display:none;" frameborder="nones" ></iframe>
                  </td>
             </tr>
             </table>
        </td>
      </tr>
      
      <tr id="nodo12" class="none">
        <td width="86%" valign="top">
            <table class="clsTableTitle" width="110" cellSpacing="0" cellPadding="0" align="center" border="0">
             <html:textarea property="msgWFAprobados" style="display:none" />
             <tr>
                  <td colspan="2" heigth="26" class="titleCenter" bgcolor="#7396b5">
                      <%=rb.getString("param.msgWFAprobados")%>
                  </td>
             </tr>
             <tr>
                  <td class="fondoEditor" colspan="2" valign="top">
                  		<div style="padding:20;height:300;border:1px dotted blue;" 
                  			onclick="window.open('parametersMessage.jsp?numero=5','frmMsgWFAprobados');this.style.display='none';document.getElementById('frmMsgWFAprobados').style.display='';">
                  			<%=String.valueOf(data.getMsgWFAprobados())%>
                  		</div>
                  		<iframe id="frmMsgWFAprobados" name='frmMsgWFAprobados' width="100%" style="height:300px;display:none;" frameborder="nones" ></iframe>
                  </td>
             </tr>
             </table>
        </td>
      </tr>
      
      <tr id="nodo13" class="none">
        <td width="86%" valign="top">
            <table class="clsTableTitle" width="110" cellSpacing="0" cellPadding="0" align="center" border="0">
             <html:textarea property="msgWFCancelados" style="display:none" />
             <tr>
                  <td colspan="2" heigth="26" class="titleCenter" bgcolor="#7396b5">
                      <%=rb.getString("param.msgWFCancelados")%>
                  </td>
             </tr>
             <tr>
                  <td class="fondoEditor" colspan="2" valign="top">
                  		<div style="padding:20;height:300;border:1px dotted blue;" 
                  			onclick="window.open('parametersMessage.jsp?numero=6','frmMsgWFCancelados');this.style.display='none';document.getElementById('frmMsgWFCancelados').style.display='';">
                  			<%=String.valueOf(data.getMsgWFCancelados())%>
                  		</div>
                  		<iframe id="frmMsgWFCancelados" name='frmMsgWFCancelados' width="100%" style="height:300px;display:none;" frameborder="nones" ></iframe>
                  </td>
             </tr>
             </table>
        </td>
      </tr>
      <tr id="nodo14" class="none">
        <td width="86%" valign="top">
            <table class="clsTableTitle" width="110" cellSpacing="0" cellPadding="0" align="center" border="0">
             <html:textarea property="msgSacopAccionesPorVencer" style="display:none" />
             <tr>
                  <td colspan="2" heigth="26" class="titleCenter" bgcolor="#7396b5">
                      <%=rb.getString("param.msgSacopAccionesPorVencer")%>
                  </td>
             </tr>
             <tr>
                  <td class="fondoEditor" colspan="2" valign="top">
                        <div style="padding:20;height:300;border:1px dotted blue;" 
                            onclick="window.open('parametersMessage.jsp?numero=7','frmMsgSacopAccionesPorVencer');this.style.display='none';document.getElementById('frmMsgSacopAccionesPorVencer').style.display='';">
                            <%=String.valueOf(data.getMsgSacopAccionesPorVencer())%>
                        </div>
                        <iframe id="frmMsgSacopAccionesPorVencer" name='frmMsgSacopAccionesPorVencer' width="100%" style="height:300px;display:none;" frameborder="nones" ></iframe>
                  </td>
             </tr>
             </table>
        </td>
      </tr>
      <tr id="nodo15" class="none">
        <td width="86%" valign="top">
            <table class="clsTableTitle" width="110" cellSpacing="0" cellPadding="0" align="center" border="0">
             <html:textarea property="msgSacopAccionesVencidas" style="display:none" />
             <tr>
                  <td colspan="2" heigth="26" class="titleCenter" bgcolor="#7396b5">
                      <%=rb.getString("param.msgSacopAccionesVencidas")%>
                  </td>
             </tr>
             <tr>
                  <td class="fondoEditor" colspan="2" valign="top">
                        <div style="padding:20;height:300;border:1px dotted blue;" 
                            onclick="window.open('parametersMessage.jsp?numero=8','frmMsgSacopAccionesVencidas');this.style.display='none';document.getElementById('frmMsgSacopAccionesVencidas').style.display='';">
                            <%=String.valueOf(data.getMsgSacopAccionesVencidas())%>
                        </div>
                        <iframe id="frmMsgSacopAccionesVencidas" name='frmMsgSacopAccionesVencidas' width="100%" style="height:300px;display:none;" frameborder="nones" ></iframe>
                  </td>
             </tr>
             </table>
        </td>
      </tr>
    </html:form>
</table>
</body>
</html>

<script type="text/javascript" event="onload" for="window">
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
</script>

