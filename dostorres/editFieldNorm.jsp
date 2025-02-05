<jsp:include page="richeditDocType.jsp" />
<%@ page
	import="java.util.ResourceBundle,com.desige.webDocuments.persistent.managers.HandlerStruct,com.desige.webDocuments.utils.ToolsHTML,com.desige.webDocuments.utils.beans.SuperActionForm,com.desige.webDocuments.utils.beans.Users,com.desige.webDocuments.enlaces.forms.UrlsActionForm"%>

<!--/**
 * Title: editFieldNorm.jsp<br>
 * Copyright: (c) 2004 Desige Servicios de Computación<br/>
 *
 * @author Ing. Nelson Crespo   (NC)
 * @author Ing. Simón Rodriguéz (SR)
 * @version WebDocuments v1.0
 * <br>
 *     Changes:<br>
 * <ul>
 *      <li> 22-09-2005 (SR)  se agrego el bean data para la session  typeOtroFormato</li>
 *      <li> 07-07-2005 (SR)  Se valido si existe una session descript para
                              MsgInEditor(document.editNorms.descript,document.editNorms.richedit);</li>
 *      <li> 18-08-2005 (SR)  se agrego doc.upFile  para la norma</li>
 *      <li> 20-04-2006 (SR)  Se modifico el border del link igual a cero, en caso de tener un documento enlazado.</li>
 *      <li> 24-04-2006 (SR)  Se coca un punto en esta direccion ./estilo/funciones.js</li>
 *      <li> 25/04/2006 (SR) Se sustituyo /WebDocuments/.. por ./..
 *      <li> 26-06-2006 (SR)  Se coloca un espacio vacio</li>
 * <ul>
 */-->
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic"%>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>

<%@ page language="java"%>
<%
	ResourceBundle rb = ToolsHTML.getBundle(request);
	StringBuffer onLoad = new StringBuffer("");
	String info = (String) request.getAttribute("info");
	String idNormClass = null;
	boolean isMaster = false;
	
	String loadForm = (String) session.getAttribute("loadManager");
	if (ToolsHTML.isEmptyOrNull(loadForm)) {
		loadForm = "";
	}
	
	if (!ToolsHTML.isEmptyOrNull(info)) {
		onLoad.append(" onLoad=\"alert('").append(info).append("')\"");
	}
	
	String err = (String) session.getAttribute("error");
	if (!ToolsHTML.isEmptyOrNull(err)) {
		onLoad.append(" onLoad=\"alert('").append(err).append("')\"");
		session.removeAttribute("error");
	}

	boolean isInput = true;
	String inputReturn = (String) session.getAttribute("input");
	if (inputReturn == null) {
		inputReturn = "";
		isInput = false;
	}
	
	String valueReturn = (String) session.getAttribute("value");
	if (valueReturn == null) {
		valueReturn = "";
	}
	String input = (String) ToolsHTML.getAttribute(request, "input");
	String value = (String) ToolsHTML.getAttribute(request, "value");

	String nameForma = ToolsHTML.getAttribute(request, "nameForma") != null
			? (String) ToolsHTML.getAttribute(request, "nameForma")
			: "";

	String cmd = (String) request.getAttribute("cmd");
	if (cmd == null) {
		cmd = SuperActionForm.cmdLoad;
	}
	pageContext.setAttribute("cmd", cmd);
%>
<html>
<head>
<title><%=rb.getString("principal.title")%></title>
<jsp:include page="meta.jsp" />
<link href="estilo/estilo.css" rel="stylesheet" type="text/css">
<script type="text/javascript" src="./estilo/funciones.js"></script>
<jsp:include page="richeditHead.jsp" />
<style>
.active0 {
	background-color: transparent;
	color: #afafaf;
	text-align: center;
}

.active1 {
	background-color: #00ceb4;
	color: #000;
	text-align: center;
}
</style>

<script type="text/javascript">
function showNorms(idNorms) {
    var idNorms = escape(idNorms);
    abrirVentana("viewDocumentNorms.jsp?idNorms=" + idNorms ,800,600);

}
  function botones(forma) {
        if (forma.btnDelete) {
            forma.btnDelete.disabled = true;
        }
        if (forma.btnIncluir) {
            forma.btnIncluir.disabled = true;
        }
        if (forma.btnSalvar) {
            forma.btnSalvar.disabled = true;
        }
        if (forma.btnCancelar) {
            forma.btnCancelar.disabled = true;
        }
    }
    function setValue(id,desc){
        <%=inputReturn%>
        <%=valueReturn%>
        window.close();
    }

    function cancelar(form){
        form.action = "<%=loadForm%>";
        form.cmd.value="<%=SuperActionForm.cmdLoad%>";
        form.submit();
    }

   function validateRadio() {
	    var formaSelection = document.getElementById("selection");
        for (var i=1; i < formaSelection.length;i++) {
            if (formaSelection.elements[i].checked){
                msg = formaSelection.elements[i].value;
                p = msg.indexOf(",");
                formaSelection.id.value = msg.substring(0,p);
                formaSelection.desc.value = msg.substring(p+1,msg.length);
                return true;
            }
        }
        return false;
    }
    function edit(num){
        forma = document.<%=(String) session.getAttribute("formEdit")%>;
        var formEdit='<%=(String) session.getAttribute("formEdit")%>';
        forma.id.value = num;
        forma.cmd.value == '<%=SuperActionForm.cmdEdit%>';
        forma.submit();
    }
    function salvar(form){
    	if(form.richedit != null){
            updateRTEs();
            //alert("form.richedit.value " + form.richedit.value);
            form.descript.value = form.richedit.value;
        }
    	
    	if (validar(form)){
           //en tal caso de agregar una norma, este metodo de botonoes deshabilita las opciones de eliminar,agregar,cancelar
           botones(form);
           form.submit();
        } else {
            alert("<%=rb.getString("err.notValueInField")%>");
        }
    }
    
    String.prototype.trim = function() { return this.replace(/^\s*|\s*$/g,""); };

    function validar(forma){
	    //forma.name.value = forma.name.value.trim();
//        if (forma.name.value.length==0) {
    	var value = document.getElementsByName("name")[0].value;
	    if (value==null || value.trim()=="") {
            return false;
        }
        
        if (forma.descript) {
        	//alert("forma.descript.value " + forma.descript.value);
            //forma.descript.value = forma.richedit.docXHtml;
            if (isEmptyRicheditValue(forma.descript.value)) {
                return false;
            }
        }
        
        // Chequea si sistema de gestion no esta vacio
        if (forma.sistemaGestion) {
        	 var val = forma.sistemaGestion.value;
         	  if (val==null || val.trim()=="") { 
				 return false;
         	  }
        }
        
        // Chequea si indice no esta vacio
        if (forma.indice) {
        	 var val = forma.indice.value; 
         	  if (val==null || val.trim()=="") { 
				 return false;
         	  }
        }
        
        return true;
    }

    function incluir(form) {
        form.cmd.value = "<%=SuperActionForm.cmdNew%>";
        form.submit();
    }
    function Buscar(form){
        form.submit();
    }
    function check() {
    	var formaSelection = document.getElementById("selection");
        if (!validateRadio()) {
            alert('<%=rb.getString("error.selectValue")%>');
            return false;
        } else {
            setValue(formaSelection.id.value,formaSelection.desc.value);
        }
     }

    function youAreSure(form){
      if (confirm("<%=rb.getString("areYouSure")%>")) {
         form.cmd.value = "<%=SuperActionForm.cmdDelete%>";
         form.submit();
      }
    }


     function updateCheck(check,field) {
        if (check.checked){
            field.value = "0";
        } else{
            field.value = "1";
        }
    }

    function editField(pages,input,value,forma) {
        var hWnd = null;
        hWnd = window.open(pages+"?input="+input+"&value="+value+"&nameForma="+forma.attributes['name'].value,"Catalogo","width=600,height=450,resizable=yes,statubar=yes,scrollbars=yes,left=200,top=150");
        if ((document.window != null) && (!hWnd.opener)){
            hWnd.opener = document.window;
        }
    }

   	function activeDocumentRequired(idNorm) {
   		
   		window.http = false;
		if(navigator.appName == "Microsoft Internet Explorer") {
		  window.http = new ActiveXObject("Microsoft.XMLHTTP");
		} else {
		  window.http = new XMLHttpRequest();
		}
		var valor = document.getElementById('ACTIVE_'+idNorm).innerHTML;
		var documentRequired = valor=='<%=rb.getString("sistema.no")%>'?0:1;
		
		var cad = "";
		cad += "idNorm="+idNorm;
		cad += "&documentRequired="+documentRequired;
		window.http.open("POST", "updateNormAjax.do?"+cad);
		window.http.onreadystatechange=function(resp,txt) {
		  try {
			  if(window.http.readyState == 4) {
				if(window.http.responseText){
					var resp = eval("("+window.http.responseText+")");
					if(resp.success=='true') {
						document.getElementById('ACTIVE_'+resp.idNorm).className='active'+resp.documentRequired;
						document.getElementById('ACTIVE_'+resp.idNorm).innerHTML = (resp.documentRequired==1?'<%=rb.getString("sistema.yes")%>':'<%=rb.getString("sistema.no")%>');
					}
				} else {
					alert("<%=rb.getString("E0040")%>");
				}
			  }
		  } catch(e){
		  }
		}
		http.send(null);
   	}

   	
   	function activeAuditProcess(idNorm) {
   		window.http = false;
		if(navigator.appName == "Microsoft Internet Explorer") {
		  window.http = new ActiveXObject("Microsoft.XMLHTTP");
		} else {
		  window.http = new XMLHttpRequest();
		}
		var valor = document.getElementById('ACTIVE_AUDIT_'+idNorm).innerHTML;
		var auditProcess = valor=='<%=rb.getString("sistema.no")%>'?0:1;
		
		var cad = "";
		cad += "idNorm="+idNorm;
		cad += "&auditProcess="+auditProcess;
		window.http.open("POST", "updateNormAjax.do?"+cad);
		window.http.onreadystatechange=function(resp,txt) {
		  try {
			  if(window.http.readyState == 4) {
				if(window.http.responseText){
					var resp = eval("("+window.http.responseText+")");
					if(resp.success=='true') {						
						document.getElementById('ACTIVE_AUDIT_'+resp.idNorm).className='active'+resp.auditProcess;
						document.getElementById('ACTIVE_AUDIT_'+resp.idNorm).innerHTML = (resp.auditProcess==1?'<%=rb.getString("sistema.yes")%>':'<%=rb.getString("sistema.no")%>');
					}
				} else {
					alert("<%=rb.getString("E0040")%>");
				}
			  }
		  } catch(e){
		  }
		}
		http.send(null);
   	}

   	function verHijos(obj,clase) {
   		var els = document.getElementsByClassName(clase);

   		obj.innerHTML=(obj.innerHTML=='+'?'-':'+');
   		Array.prototype.forEach.call(els, function(el) {
   			el.style.display=el.style.display==""?"none":"";
   		});
   	}
</script>
</head>

<body class="bodyInternas" <%=onLoad.toString()%> style="margin: 0;">
	<table cellSpacing=0 cellPadding=2 align=center border=0 width="100%">
		<logic:present name="dataTable" scope="session">
			<logic:equal name="cmd" value="<%=SuperActionForm.cmdLoad%>">
				<!-- Paginación -->
				<%
					Users users = (Users) session.getAttribute("user");

				/*
				String from = request.getParameter("from");
							String size = (String) session.getAttribute("sizeParam");
							
							PaginPage Pagingbean = ToolsHTML.getBeanPaginPage(from,
									Integer.parseInt(size),
									String.valueOf(users.getNumRecord())); */
				%>
				<html:form
					action='<%=(String) session
								.getAttribute("loadEditManager")%>'
					style="margin:0px;">
					<html:hidden property="id" value="" />
					<html:hidden property="cmd" value="<%=SuperActionForm.cmdEdit%>" />
					<%
						if (isInput) {
					%>
					<html:hidden property="input" value="<%=input%>" />
					<html:hidden property="value" value="<%=value%>" />
					<html:hidden property="nameForma" value="<%=nameForma%>" />
					<%
						}
					%>
				</html:form>
				<form name="selection" id="selection" action="" style="margin: 0;">
					<input type="hidden" name="id" /> <input type="hidden" name="desc" />
					<%
						if (isInput) {
					%>
					<html:hidden property="input" value="<%=input%>" />
					<html:hidden property="value" value="<%=value%>" />
					<html:hidden property="nameForma" value="<%=nameForma%>" />
					<%
						}
					%>
					<tr>
						<td>
							<table cellSpacing="3" cellPadding="3" align=center border=0
								width="100%"
								style="position:fixed;top:0;background-color:white;"
								>
								<tr>
									<td colspan="3">
										<table class="clsTableTitle" width="100%" cellSpacing="0"
											cellPadding="2" align=center border=0>
											<tbody>
												<tr>
													<td class="td_title_bc"><%=rb.getString("cat.title")%>
													</td>
												</tr>
											</tbody>
										</table>
									</td>
								</tr>
								<tr>
									<td width="60%" class="td_titulo_C"><%=rb.getString("url.name")%>
									</td>
									<td width="20%" class="td_titulo_C"><%=rb.getString("url.docRequired")%>
									</td>
									<td width="20%" class="td_titulo_C"><%=rb.getString("url.auditProcess")%>
									</td>
								</tr>
							</table>
							<div style="padding-top:35px;"></div>
							<table cellSpacing="3" cellPadding="3" align=center border=0
								width="100%">

								<logic:iterate id="bean" name="dataTable" indexId="ind"
									type="com.desige.webDocuments.utils.beans.Search"
									scope="session">
									<%
										int item = ind.intValue() + 1;
										int num = item % 2;
										isMaster = bean.getIndice().equals("0");
										idNormClass = isMaster?bean.getId():idNormClass;
									%>
									<tr class="<%=isMaster?"":idNormClass%>" <%= bean.getIndice().equals("0") ? "" : "style='display:none;'" %>>
										<td width="60%" class='td_<%=num%>'>
											<b style="cursor:pointer;font-size:16px;" onclick="verHijos(this,'<%=idNormClass%>')"><%= bean.getIndice().equals("0") ? "+" : "" %></b>
											<%= bean.getIndice().equals("0") ? "" : "&nbsp;&nbsp;&nbsp;&nbsp;" %>
											<%
												if (isInput) {
											%> <input name="idRadio" type="radio"
											value="<%=bean.getId()%>,<%=bean.getDescript()%>" /> <%
 	}
 %> <a href="javascript:edit('<%=bean.getId()%>')" class="ahref_b">
												<%=ToolsHTML.cortarCadena(bean.getDescript() != null
								? bean.getDescript()
								: "")%>
										</a> <%
 	if (!ToolsHTML.isEmptyOrNull(bean.getAditionalInfo())) {		
 %> <a href="javascript:showNorms('<%=bean.getId()%>')" class="ahreMenu">
												&nbsp;&nbsp;&nbsp; <%--ydavila Ticket 001-00-003190 cambio en la funcion administrar normas - no muestra el clip para ver documento anexo 
											<img
												src="img/pendings.gif" width="16" height="16" border=0>
ydavila --%>
										</a> <%										
 	}
 %>
										</td>
										<td width="20%" class='td_<%=num%>'><a id="" href="#"
											onclick="activeDocumentRequired(<%=bean.getId()%>)">
												<div id="ACTIVE_<%=bean.getId()%>"
													class="active<%=bean.getDocumentRequired()%>"><%=rb.getString(bean.getDocumentRequired().equals("1")?"sistema.yes":"sistema.no")%></div>
										</a></td>

										<td width="20%" class='td_<%=num%>'><a id="" href="#"
											onclick="activeAuditProcess(<%=bean.getId()%>)">
												<div id="ACTIVE_AUDIT_<%=bean.getId()%>"
													class="active<%=bean.getAuditProcess()%>"><%=rb.getString(bean.getAuditProcess().equals("1")?"sistema.yes":"sistema.no")%></div>
										</a></td>

									</tr>
								</logic:iterate>
							</table>
						</td>
					</tr>
				</form>
				<tr>
					<td>
						<center>
							<html:form
								action='<%=(String) session.getAttribute("newManager")%>'>
								<html:hidden property="cmd" value="<%=SuperActionForm.cmdNew%>" />
								<%
									if (isInput) {
								%>
								<html:hidden property="input" value="<%=input%>" />
								<html:hidden property="value" value="<%=value%>" />
								<html:hidden property="nameForma" value="<%=nameForma%>" />
								<%
									}
								%>
								<%--<html:hidden property="input" value="<%=input%>"/>--%>
								<%--<html:hidden property="value" value="<%=value%>"/>--%>
								<%--<html:hidden property="nameForma" value="<%=nameForma%>"/>--%>


								<%--Ticket 001-00-003190 cambio en la funcion administrar normas - no muestra botón Agregar o Cancelar primera pantalla
 
						<input type="button" class="boton"
									value="<%=rb.getString("btn.incluir")%>"
									onClick="javascript:incluir(this.form);" name="btnNew" />
									&nbsp;	
					    <input type="button" class="boton"
									value="<%=rb.getString("btn.ok")%>"
									onClick="javascript:check();" name="btnOK" />
			        	            &nbsp;

				        <input type="button" class="boton"
									value="<%=rb.getString("btn.cancel")%>"
									onClick="window.close();" name="btnCancel" />
--%>
								<logic:equal value="editNorms" name="formEdit" scope="session">
									<tr>
										<input type="button" class="boton"
											value="<%=rb.getString("btn.cancel")%>"
											onClick="window.close();" name="btnCancel" 
											style="display:none;"
									</tr>
								</logic:equal>
								<logic:equal value="editTypeDoc" name="formEdit" scope="session">
									<tr>
										<input type="button" class="boton"
											value="<%=rb.getString("btn.incluir")%>"
											onClick="javascript:incluir(this.form);" name="btnNew" />
										&nbsp;
										<input type="button" class="boton"
											value="<%=rb.getString("btn.cancel")%>"
											onClick="window.close();" name="btnCancel" &nbsp
									</tr>
								</logic:equal>
								<logic:equal value="state" name="formEdit" scope="session">
									<tr>
										<input type="button" class="boton"
											value="<%=rb.getString("btn.incluir")%>"
											onClick="javascript:incluir(this.form);" name="btnNew" />
										&nbsp;
										<input type="button" class="boton"
											value="<%=rb.getString("btn.ok")%>"
											onClick="javascript:check();" name="btnOK" /> &nbsp;
										<input type="button" class="boton"
											value="<%=rb.getString("btn.cancel")%>"
											onClick="window.close();" name="btnCancel" &nbsp
									</tr>
								</logic:equal>
								<logic:equal value="city" name="formEdit" scope="session">
									<tr>
										<input type="button" class="boton"
											value="<%=rb.getString("btn.incluir")%>"
											onClick="javascript:incluir(this.form);" name="btnNew" />
										&nbsp;
										<input type="button" class="boton"
											value="<%=rb.getString("btn.ok")%>"
											onClick="javascript:check();" name="btnOK" /> &nbsp;
										<input type="button" class="boton"
											value="<%=rb.getString("btn.cancel")%>"
											onClick="window.close();" name="btnCancel"
									</tr>
								</logic:equal>
								<logic:equal value="country" name="formEdit" scope="session">
									<tr>
										<input type="button" class="boton"
											value="<%=rb.getString("btn.incluir")%>"
											onClick="javascript:incluir(this.form);" name="btnNew" />
										&nbsp;
										<input type="button" class="boton"
											value="<%=rb.getString("btn.ok")%>"
											onClick="javascript:check();" name="btnOK" /> &nbsp;
										<input type="button" class="boton"
											value="<%=rb.getString("btn.cancel")%>"
											onClick="window.close();" name="btnCancel"
									</tr>
								</logic:equal>
								<%-- ydavila --%>

							</html:form>
							&nbsp;
						</center>
					</td>
				</tr>
				<tr>
					<td>&nbsp;</td>
				</tr>


			</logic:equal>
		</logic:present>
		<tr>
			<td><html:form
					action='<%=(String) session.getAttribute("newManager")%>'
					style="margin:0;">
					<html:hidden property="cmd" value="<%=SuperActionForm.cmdNew%>" />
					<%
						if (isInput) {
					%>
					<html:hidden property="input" value="<%=input%>" />
					<html:hidden property="value" value="<%=value%>" />
					<html:hidden property="nameForma" value="<%=nameForma%>" />
					<%
						}
					%>
					<logic:notPresent name="dataTable" scope="session">
						<logic:notEqual value="<%=SuperActionForm.cmdInsert%>" name="cmd">
							<table width="100%" cellSpacing=0 cellPadding=0 align=center
								border=0>
								<tr>
									<td><input type="button" class="boton"
										value="<%=rb.getString("btn.incluir")%>"
										onClick="javascript:incluir(this.form);" /></td>
								</tr>
							</table>
						</logic:notEqual>
					</logic:notPresent>
				</html:form></td>
		</tr>
	</table>
	<logic:notEqual name="cmd" value="<%=SuperActionForm.cmdLoad%>">
		<%
			String forma = (String) session.getAttribute("editManager");
				if (UrlsActionForm.cmdInsert.equalsIgnoreCase(cmd)) {
					forma = (String) session.getAttribute("newManager");
				}
		%>

		<tr>
			<td><html:form action="<%=forma%>" enctype='multipart/form-data'
					style="margin:0;">
					<html:hidden property="cmd" />
					<html:hidden property="id" />
					<html:hidden property="generateRequestSacop" value="1" />
					<%
						if (isInput) {
					%>
					<html:hidden property="input" value="<%=input%>" />
					<html:hidden property="value" value="<%=value%>" />
					<html:hidden property="nameForma" value="<%=nameForma%>" />
					<%
						}
					%>
					<!--Se agrega una norma nueva -->
					<table align=center border=0 width="100%">
						<tr>
							<td colspan="2">
								<table class="clsTableTitle" width="100%" cellSpacing="0"
									cellPadding="2" align=center border=0>
									<tbody>
										<tr>
											<td class="td_title_bc"><%=rb.getString("cat.update")%>
												<logic:present name="typeOtroFormato" scope="session">
													<%=rb.getString("pb.typeDoc")%>
												</logic:present></td>
										</tr>
									</tbody>
								</table>
							</td>
						</tr>
						<tr>
							<td width="26%" class="titleLeft" height="26"
								style="background: url(img/btn120.png); background-repeat: no-repeat"
								valign="middle"><%=rb.getString("url.name")%>:</td>

							<%--ydavila Ticket 001-00-003190 cambio en la funcion administrar normas - se habilita NAME sólo para algunas opciones--%>
							<logic:equal value="editTypeDoc" name="formEdit" scope="session">
								<td width="*" class="td_gris_l"><html:text property="name"
										size="90" styleClass="classText" /></td>
							</logic:equal>
							<logic:equal value="state" name="formEdit" scope="session">
								<td width="*" class="td_gris_l"><html:text property="name"
										size="90" styleClass="classText" /></td>
							</logic:equal>
							<logic:equal value="country" name="formEdit" scope="session">
								<td width="*" class="td_gris_l"><html:text property="name"
										size="90" styleClass="classText" /></td>
							</logic:equal>
							<logic:equal value="city" name="formEdit" scope="session">
								<td width="*" class="td_gris_l"><html:text property="name"
										size="90" styleClass="classText" /></td>
							</logic:equal>
							<logic:equal value="editNorms" name="formEdit" scope="session">
								<td width="*" class="td_gris_l"><html:text property="name"
										disabled="true" size="90" styleClass="classText" /></td>
							</logic:equal>
							<%--ydavila--%>

							<!-- SIMON 21 DE JUNIO 2005 IICIO -->
							<logic:present name="typeOtroFormato" scope="session">
								<bean:define id="data" name="typeOtroFormato"
									type="com.desige.webDocuments.typeDocuments.forms.TypeDocumentsForm"
									scope="session" />
								<logic:notPresent name="valor" scope="session">
									<logic:present name="formEdit" scope="session">
										<logic:equal value="editTypeDoc" name="formEdit"
											scope="session">
											<tr>
												<td class="titleLeft" height="26"
													style="background: url(img/btn140x4.png); background-repeat: no-repeat"
													valign="middle"><%=rb.getString("tipoformato")%></td>
												<td class="td_gris_l">
													<%
														if (!ToolsHTML.isNumeric(data.getType())) {
																					data.setType("0");
																				}
													%> <%=ToolsHTML.getRadioButton(rb,
											"tDoc.typeDoc", "type",
											Integer.parseInt(data.getType()),
											null)%>
												</td>
											</tr>

											<tr>
												<td class="titleLeft" height="26"
													style="background: url(img/btn120.png); background-repeat: no-repeat"
													valign="middle"><%=rb.getString("param.dateExpire")%>
												</td>
												<td class="td_gris_l"><%=ToolsHTML
											.showCheckBox(
													"expireDocI",
													"updateCheck(this.form.expireDocI,this.form.expireDoc)",
													String.valueOf(data
															.getExpireDoc()),
													"0")%> <html:hidden property="expireDoc" /> <%=ToolsHTML.getSelectList(rb,
											"param.monthExpireDoc",
											"monthsExpireDocs",
											data.getMonthsExpireDocs())%> <%=ToolsHTML.getSelectList(rb,
											"param.vigItems",
											"param.vigItemsValue",
											"unitTimeExpire",
											data.getUnitTimeExpire())%></td>
											</tr>


											<!-- SIMON 16 DE JUNIO 2005 2_04PM INICIO -->
											<tr>
												<td class="titleLeft" height="26"
													style="background: url(img/btn120.png); background-repeat: no-repeat"
													valign="middle"><%=rb.getString("param.dateDrafts")%>
												</td>
												<td class="td_gris_l"><%=ToolsHTML
											.showCheckBox(
													"expireDraftsI",
													"updateCheck(this.form.expireDraftsI,this.form.expireDrafts)",
													String.valueOf(data
															.getExpireDrafts()),
													"0")%> <html:hidden property="expireDrafts" /> <%=ToolsHTML.getSelectList(rb,
											"param.monthExpireDoc",
											"monthsExpireDrafts",
											data.getMonthsExpireDrafts())%> <%=ToolsHTML.getSelectList(rb,
											"param.vigItems",
											"param.vigItemsValue",
											"unitTimeExpireDrafts",
											data.getUnitTimeExpireDrafts())%></td>
											</tr>

											<tr>
												<td class="titleLeft" height="26"
													style="background: url(img/btn120.png); background-repeat: no-repeat"
													valign="middle"><%=rb.getString("param.dateDead")%></td>
												<td class="td_gris_l"><%=ToolsHTML
											.showCheckBox(
													"deadDocI",
													"updateCheck(this.form.deadDocI,this.form.deadDoc)",
													String.valueOf(data
															.getDeadDoc()), "0")%> <html:hidden property="deadDoc" />
													<%=ToolsHTML.getSelectList(rb,
											"param.monthExpireDoc",
											"monthsDeadDocs",
											data.getMonthsDeadDocs())%> <%=ToolsHTML.getSelectList(rb,
											"param.vigItems",
											"param.vigItemsValue",
											"unitTimeDead",
											data.getUnitTimeDead())%></td>
											</tr>

											<tr>
												<td class="titleLeft" height="26"
													style="background: url(img/btn140x4.png); background-repeat: no-repeat"
													valign="middle"><%=rb
											.getString("param.printFirstPage")%></td>
												<td class="td_gris_l"><%=ToolsHTML
											.showCheckBox(
													"firstPageI",
													"updateCheck(this.form.firstPageI,this.form.firstPage)",
													String.valueOf(data
															.getFirstPage()),
													"0")%> <html:hidden property="firstPage" /></td>
											</tr>

											<tr
												style="display:<%=ToolsHTML.showFlujo()
											? ""
											: "none"%>">
												<td class="titleLeft" height="26"
													style="background: url(img/btn140x4.png); background-repeat: no-repeat"
													valign="middle"><%=rb
											.getString("param.sendToFlexWF")%></td>
												<td class="td_gris_l"><%=ToolsHTML
											.showCheckBox(
													"sendToFlexWFI",
													"updateCheck(this.form.sendToFlexWFI,this.form.sendToFlexWF)",
													String.valueOf(data
															.getSendToFlexWF()),
													"0")%> <html:hidden property="sendToFlexWF" /> <html:select
														property="actNumber" styleClass="" style="width: 400px">
														<html:option value="0">&nbsp;</html:option>
														<logic:present name="activities" scope="session">
															<html:optionsCollection name="activities" value="number"
																label="name" />
														</logic:present>
													</html:select></td>
											</tr>

											<tr>
												<td colspan="2"><br></td>
											</tr>

											<tr
												style="display:<%=ToolsHTML.showDigital()
											? ""
											: "none"%>">
												<td colspan="2">
													<fieldset>
														<legend><%=rb.getString("typeDoc.options")%></legend>
														<div id="parametros">
															<table align=center border=0 width="100%">
																<tr>
																	<td class="titleLeft" height="26"
																		style="background: url(img/btn140x4.png); background-repeat: no-repeat"
																		valign="middle"><%=rb
											.getString("typeDoc.typesetter")%></td>
																	<td class="td_gris_l"><html:select
																			property="typesetter" styleClass=""
																			style="width: 400px">
																			<html:option value="0">&nbsp;</html:option>
																			<logic:present name="users" scope="session">
																				<logic:iterate id="bean" name="users"
																					scope="session"
																					type="com.desige.webDocuments.utils.beans.Search">
																					<html:option value="<%=bean.getId()%>">
																						<%=bean
															.getAditionalInfo()%> ( <%=bean
															.getDescript()%> )
									                                </html:option>
																				</logic:iterate>
																			</logic:present>
																		</html:select></td>
																</tr>

																<tr>
																	<td class="titleLeft" height="26"
																		style="background: url(img/btn140x4.png); background-repeat: no-repeat"
																		valign="middle"><%=rb.getString("typeDoc.owner")%>
																	</td>
																	<td class="td_gris_l"><html:select
																			property="ownerTypeDoc" styleClass=""
																			style="width: 400px">
																			<html:option value="0">&nbsp;</html:option>
																			<logic:present name="users" scope="session">
																				<logic:iterate id="bean" name="users"
																					scope="session"
																					type="com.desige.webDocuments.utils.beans.Search">
																					<html:option value="<%=bean.getId()%>">
																						<%=bean
															.getAditionalInfo()%> ( <%=bean
															.getDescript()%> )
									                                </html:option>
																				</logic:iterate>
																			</logic:present>
																		</html:select></td>
																</tr>

																<tr>
																	<td class="titleLeft" height="26"
																		style="background: url(img/btn140x4.png); background-repeat: no-repeat"
																		valign="middle"><%=rb.getString("typeDoc.checker")%>
																	</td>
																	<td class="td_gris_l"><html:select
																			property="checker" styleClass="" style="width: 400px">
																			<html:option value="0">&nbsp;</html:option>
																			<logic:present name="users" scope="session">
																				<logic:iterate id="bean" name="users"
																					scope="session"
																					type="com.desige.webDocuments.utils.beans.Search">
																					<html:option value="<%=bean.getId()%>">
																						<%=bean
															.getAditionalInfo()%> ( <%=bean
															.getDescript()%> )
									                                </html:option>
																				</logic:iterate>
																			</logic:present>
																		</html:select></td>
																</tr>

																<tr>
																	<td height="25" class="titleLeft"
																		style="background: url(img/btn140.png); background-repeat: no-repeat"
																		valign="middle"><%=rb.getString("sch.arbol")%></td>
																	<td class="td_gris_l"><input type="text"
																		name="nameRout" style="width: 300px; height: 19px"
																		value="<%=HandlerStruct.getNameNodeString(String
											.valueOf(data.getIdNodeTypeDoc()))%>" />

																		&nbsp;<input type="button" class="boton" value=" ... "
																		onClick="javascript:editField('loadAllStruct.do?toSelectValue=true','estado','nameState',this.form);"
																		style="width: 20px;" /> <html:hidden styleId="idRout"
																			property="idNodeTypeDoc" /></td>
																</tr>

																<tr>
																	<td height="25" class="titleLeft"
																		style="background: url(img/btn140.png); background-repeat: no-repeat"
																		valign="middle"><%=rb.getString("btn.public")%></td>
																	<td class="td_gris_l"><%=ToolsHTML
											.showCheckBox(
													"publicDocI",
													"updateCheck(this.form.publicDocI,this.form.publicDoc)",
													String.valueOf(data
															.getPublicDoc()),
													"0")%> <html:hidden property="publicDoc" /><%=rb.getString("typeDoc.publicDoc")%>.
																	</td>
																</tr>

															</table>
														</div>
													</fieldset>
												</td>
											</tr>



										</logic:equal>
									</logic:present>
								</logic:notPresent>
							</logic:present>
							<!-- SIMON 21 DE JUNIO 2005 FIN -->
						</tr>
						<logic:present name="descript" scope="session">
							<tr>
								<td width="26%" class="titleLeft" height="26"
									style="background: url(img/btn120.png); background-repeat: no-repeat"
									valign="middle"><%=rb.getString("norms.sistema.gestion")%>:
								</td>
								<td width="*" class="td_gris_l"><html:text disabled="true"
										property="sistemaGestion" size="90" styleClass="classText" />
									&nbsp; <!--Se agrega una norma nueva (Ej: ISO 9001:2008)--></td>
							</tr>
							<tr>
								<td width="26%" class="titleLeft" height="26"
									style="background: url(img/btn120.png); background-repeat: no-repeat"
									valign="middle"><%=rb.getString("norms.indice")%>:</td>
								<td width="*" class="td_gris_l"><html:text disabled="true"
										property="indice" size="90" styleClass="classText" /> &nbsp;
									<!--(Ej: 4.1.a)--></td>
							</tr>
							<tr>
								<td width="26%" class="titleLeft" height="26"
									style="background: url(img/btn120.png); background-repeat: no-repeat"
									valign="middle"><%=rb.getString("norms.title")%>:</td>
								<td width="*" class="td_gris_l"><html:text property="name"
										disabled="true" size="90" styleClass="classText" /></td>
							</tr>

							<logic:present name="formEdit" scope="session">
								<logic:equal value="editNorms" name="formEdit" scope="session">
									<tr>
										<%--ydavila Ticket 001-00-003190 cambio en la funcion administrar normas - se bloquea la función upload 
										<!-- SIMON 18 AGOSTO 20056 INICIO -->
										<td height="26" class="titleLeft"
											style="background: url(img/btn140.png); background-repeat: no-repeat"
											valign="middle"><%=rb.getString("doc.upFile")%>:</td>
										<td class="td_gris_l"><html:file property="nameFile"
												styleClass="classText" /></td>
										<!--SIMON 18 AGOSTO 2005 FIN -->
ydavila --%>
									</tr>
								</logic:equal>
							</logic:present>
							<tr>
								<td colspan="2" class="titleLeft" height="22"
									style="background: url(img/BgComments.gif); background-repeat: no-repeat"
									valign="middle"><%=rb.getString("url.description")%></td>
							</tr>
							<tr>
								<td class="fondoEditor" colspan="2" width="*" class="td_gris_l"
									height="300"><html:textarea property="descript" cols="30"
										rows="5" style="display:none" /> <logic:present
										name="editNorms">
										<jsp:include page="richedit.jsp">
											<jsp:param name="richedit" value="richedit" />
											<jsp:param name="defaultValue"
												value='<%=request.getAttribute("description") != null
									? request.getAttribute("description")
									: ""%>' />
										</jsp:include>
									</logic:present> <logic:notPresent name="editNorms">
										<jsp:include page="richedit.jsp">
											<jsp:param name="richedit" value="richedit" />
										</jsp:include>
									</logic:notPresent></td>
							</tr>
						</logic:present>
						<tr>
							<td colspan="2" align="center"><input type="button"
								class="boton" value="<%=rb.getString("btn.save")%>"
								name="btnSalvar" onClick="javascript:salvar(this.form);" /> <logic:notEqual
									name="cmd" value="<%=SuperActionForm.cmdInsert%>">
                                &nbsp;
 <%--Ticket 001-00-003190 cambio en la funcion administrar normas - no muestra botón Incluir ni Eliminar segunda pantalla--%>
									<%if ("/editNorm.do".equalsIgnoreCase(forma)) {%>

									<%}%>
									<%if ("/editTypeDoc.do".equalsIgnoreCase(forma)) {%>
									<input type="button" class="boton"
										value="<%=rb.getString("btn.incluir")%>" name="btnIncluir"
										onClick="javascript:incluir(this.form);" />
										&nbsp;
										<logic:notPresent name="valor" scope="session">
										<input type="button" class="boton"
											value="<%=rb.getString("btn.delete")%>" name="btnDelete"
											onClick="javascript:youAreSure(this.form);" />
                                			&nbsp;
                                		</logic:notPresent>
									<%}%>
									<%if ("/editGrupo.do".equalsIgnoreCase(forma)) {%>
									<input type="button" class="boton"
										value="<%=rb.getString("btn.incluir")%>" name="btnIncluir"
										onClick="javascript:incluir(this.form);" />
											&nbsp;
										<logic:notPresent name="valor" scope="session">
										<input type="button" class="boton"
											value="<%=rb.getString("btn.delete")%>" name="btnDelete"
											onClick="javascript:youAreSure(this.form);" />
											&nbsp;
										</logic:notPresent>
									<%}%>
									<%if ("/editState.do".equalsIgnoreCase(forma)) {%>
									<input type="button" class="boton"
										value="<%=rb.getString("btn.incluir")%>" name="btnIncluir"
										onClick="javascript:incluir(this.form);" />
											&nbsp;
										<logic:notPresent name="valor" scope="session">
										<input type="button" class="boton"
											value="<%=rb.getString("btn.delete")%>" name="btnDelete"
											onClick="javascript:youAreSure(this.form);" />
											&nbsp;
										</logic:notPresent>
									<%}%>
									<%if ("/editCity.do".equalsIgnoreCase(forma)) {%>
									<input type="button" class="boton"
										value="<%=rb.getString("btn.incluir")%>" name="btnIncluir"
										onClick="javascript:incluir(this.form);" />
											&nbsp;
										<logic:notPresent name="valor" scope="session">
										<input type="button" class="boton"
											value="<%=rb.getString("btn.delete")%>" name="btnDelete"
											onClick="javascript:youAreSure(this.form);" />
											&nbsp;
										</logic:notPresent>
									<%}%>
									<%if ("/editCountry.do".equalsIgnoreCase(forma)) {%>
									<input type="button" class="boton"
										value="<%=rb.getString("btn.incluir")%>" name="btnIncluir"
										onClick="javascript:incluir(this.form);" />
											&nbsp;
										<logic:notPresent name="valor" scope="session">
										<input type="button" class="boton"
											value="<%=rb.getString("btn.delete")%>" name="btnDelete"
											onClick="javascript:youAreSure(this.form);" />
											&nbsp;
										</logic:notPresent>
									<%}%>
									<%--ydavila --%>


								</logic:notEqual> &nbsp; <input type="button" class="boton"
								value="<%=rb.getString("btn.cancel")%>" name="btnCancelar"
								onClick="javascript:cancelar(this.form);" /></td>
						</tr>
					</table>
					<script language="JavaScript" event="onload" for="window">
                   <logic:present name="descript" scope="session" >
                           <%if ("/editNorm.do".equalsIgnoreCase(forma)) {%>
                                MsgInEditor(document.editNorms.descript,document.editNorms.richedit);
                           <%}%>
                   </logic:present>
                    <logic:notEqual name="cmd" value="<%=SuperActionForm.cmdLoad%>">
                        <logic:present name="info" scope="request" >
                            alert('<%=request.getAttribute("info")%>');
                            <%request.removeAttribute("info");%>
                        </logic:present>
                        <logic:present name="error" scope="session" >
                            alert('<%=session.getAttribute("error")%>');
                            <%session.removeAttribute("error");%>
                        </logic:present>
                    </logic:notEqual>
                  </script>
				</html:form></td>
		</tr>
	</logic:notEqual>
	</table>
</body>
</html>
<script type="text/javascript">
	function cerrar(){
		try{
			window.opener.clear();
		}catch(e){}
	}
	window.onunload=cerrar;
	
	<%if (!ToolsHTML.showCreateRegister()) {%>
	try {
		document.editTypeDoc.type[1].disabled="true";
		document.editTypeDoc.type[2].disabled="true";
	} catch(e){}
	<%}%>
</script>
