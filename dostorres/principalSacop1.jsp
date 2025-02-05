<!-- principalSacop1.jsp -->
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
	String path = request.getContextPath();
	String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";

    ResourceBundle rb = ToolsHTML.getBundle(request);
    StringBuffer onLoad = new StringBuffer("");
    Users usuario = (Users)session.getAttribute("user");
    String ok = (String)session.getAttribute("usuario");
    
	String nameAtt = "viewAll_".concat(String.valueOf(usuario.getIdPerson()));
    
    boolean viewAll = request.getSession().getServletContext().getAttribute(nameAtt)!=null && request.getSession().getServletContext().getAttribute(nameAtt).toString().equals("1");
    
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

    //int sacopBorrador  = Integer.parseInt(ToolsHTML.isEmptyOrNull(String.valueOf(request.getAttribute("borradorSize")),"0"));
    //int sacopEmitido   = Integer.parseInt(ToolsHTML.isEmptyOrNull(String.valueOf(request.getAttribute("emitidoSize")),"0"));
    int sacopPendiente = Integer.parseInt(ToolsHTML.isEmptyOrNull(String.valueOf(request.getAttribute("pendientesSize")),"0"));
    int sacopRechazado   = Integer.parseInt(ToolsHTML.isEmptyOrNull(String.valueOf(request.getAttribute("rechazadasSize")),"0"));
    int sacopCerrado   = Integer.parseInt(ToolsHTML.isEmptyOrNull(String.valueOf(request.getAttribute("cerradoSize")),"0"));
    int registrosPendiente  = Integer.parseInt(ToolsHTML.isEmptyOrNull(String.valueOf(request.getAttribute("registrosPendienteSize")),"0"));
    
    String bandejaToReload = "";
    if(request.getSession().getAttribute("bandejaToReload") != null){
        bandejaToReload = (String) request.getSession().getAttribute("bandejaToReload"); 
    }
%>
<html>
<head>
<title><%=rb.getString("principal.title")%></title>
<jsp:include page="meta.jsp?das" /> 
<link href="<%=basePath%>estilo/estilo.css" rel="stylesheet" type="text/css"></link>
<link href="<%=basePath%>estilo/sacop.css" rel="stylesheet" type="text/css"></link>
<script type="text/javascript" src="<%=basePath%>estilo/funciones.js"></script>
<script type="text/javascript" src="<%=basePath%>script/sacopJS.js"> </script>
<script type="text/javascript" src="<%=basePath%>estilo/popcalendar2inner.js"></script>

<script type="text/javascript">
function todos(etiqueta){
	if(document.getElementById("imgTodos").src===original.src) {
		document.getElementById("imgTodos").src = todos2.src;
		document.getElementById("imgTodos").alt = todos2.alt;
		document.getElementById("imgTodos").text = todos2.text;
	} else {
		document.getElementById("imgTodos").src = original.src;
		document.getElementById("imgTodos").alt = original.alt;
		document.getElementById("imgTodos").text = original.text;
	}

	var opcion;
	try {
		opcion=window.bandeja.getOption();
	}catch(e){}
	
	if(opcion == null){
		window.open(etiqueta + "?goTo=enBlanco", "bandeja");	
	} else {
		window.open("principalSacop1"+((typeof opcion)=='undefined'?"":opcion)+"Tit.jsp","bandejaTit");
	    window.open(etiqueta+((typeof opcion)=='undefined' ? "" : "?goTo="+opcion.toLowerCase()), "bandeja");
	}
}
	<!-- variables globales -->
	var original = new Image();
	var todos2 = new Image();
	original.src = "images/todos.gif";
	original.alt = "Visualizar todos";
	original.text = "Visualizar todos";
	todos2.src = "images/todos2.gif";
	todos2.alt = "No visualizar todos";
	todos2.text = "No visualizar todos";

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
    
	function updateCounter(sRechazados,sPendiente,sCerrado){
		document.getElementById("lblPendiente").innerHTML="("+sPendiente+")";
		document.getElementById("lblCerrado").innerHTML="("+sCerrado+")";
		document.getElementById("lblRechazados").innerHTML="("+sRechazados+")";
        
		document.getElementById("lblRechazados").style.display=(sRechazados!=0?"":"none");
		document.getElementById("lblPendiente").style.display=(sPendiente!=0?"":"none");
		document.getElementById("lblCerrado").style.display=(sCerrado!=0?"":"none");
	}    
	
	function probar() {
		alert("probar");
	}
	
	function downloadReport(){
		console.log(document.getSacopReport.action);
		document.getSacopReport.submit();
	}
	
function ver(etiqueta) {
	
	alert("principaSacop1.jsp funcion ver darwinuzcategui");

	if(document.getElementById('pageExcel')) {
		document.getElementById('pageExcel').style.display=(etiqueta=='Registro'?'none':'');
	}

	var extraParameters = "";
	
	//colocamos los datos respectivos en el formulario del reporte
	var reporteForm = document.getElementById("getSacopReport");
	reporteForm.bandejaReporte.value = etiqueta.toLowerCase();
	
	//abrimos el ancabezado
	window.open("principalSacop1"+etiqueta+"Tit.jsp","bandejaTit");
	
	if(etiqueta.toLowerCase() == "buscar"){
		//se desea buscar SACOPs, entonces colocamos en el request
		//los parametros respectivos
		extraParameters += "&buscar=buscar";
		extraParameters += "&idSacop=" + document.getElementById("idSacop").value;
		extraParameters += "&emisorSacop=" + document.getElementById("emisorSacop").value;
		extraParameters += "&responsableSacop=" + document.getElementById("responsableSacop").value;
		extraParameters += "&areaResponsableSacop=" + document.getElementById("areaResponsableSacop").value;
		extraParameters += "&efectivaSacop=" + document.getElementById("efectivaSacop").value;
		extraParameters += "&estadoSacop=" + document.getElementById("estadoSacop").value;
		extraParameters += "&fechaEmisionSacop=" + document.getElementById("fechaEmisionSacop").value;
		extraParameters += "&tipoSacop=" + document.getElementById("tipoSacop").value;
		extraParameters += "&origenSacop=" + document.getElementById("origenSacop").value;
		extraParameters += "&fechaEstimadaSacop=" + document.getElementById("fechaEstimadaSacop").value;
		
		
		reporteForm.idSacopReporte.value = document.getElementById("idSacop").value;
		reporteForm.emisorSacopReporte.value = document.getElementById("emisorSacop").value;
		reporteForm.responsableSacopReporte.value = document.getElementById("responsableSacop").value;
		reporteForm.areaResponsableSacopReporte.value = document.getElementById("areaResponsableSacop").value;
		reporteForm.efectivaSacopReporte.value = document.getElementById("efectivaSacop").value;
		reporteForm.estadoSacopReporte.value = document.getElementById("estadoSacop").value;
		reporteForm.fechaEmisionSacopReporte.value = document.getElementById("fechaEmisionSacop").value;
		reporteForm.fechaEstimadaSacopReporte.value = document.getElementById("fechaEstimadaSacop").value;
		
	}
	
	//buscamos el detalle
	window.open("loadSACOPMain.do?getList=true&goTo=" + etiqueta.toLowerCase() + extraParameters, "bandeja");
}

function irNew(etiqueta) {
	var id=null;
	try {
		id=window.parent.frames[1].frames[1].document.getElementById('idDocRelated').value;
	}catch(e) {
		id=null;
	}
	if(id!=null && id.length>0) {
		ir(etiqueta,"",id);
	} else {
		alert("<%=rb.getString("scp.selectDocPending")%>");
	}
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
.fondoOscuro{
	top:0px;
	left:0px;
	margin: 0px auto;
	background-color: #000000;
	position: absolute;
	opacity: 0.6;
	-moz-opacity: 0.6;
	-khtml-opacity: 0.6;
	filter: alpha(opacity=60);
	z-index:50;	
}
#caja2 {
	color:white;
	font-weight:bold;
	font-family: Sans-serif, Helvetica, Arial, Verdana ;
	font-size:12px;
	position: absolute;
	z-index:100;	
	border:1px solid white;
}
#caja3 {
	margin: 0px auto;
	background-color: #ffffff;
	position: absolute;
	opacity: 0.6;
	-moz-opacity: 0.6;
	-khtml-opacity: 0.6;
	filter: alpha(opacity=60);
	z-index:60;	
}
p.negro {color: #000;}
	
	
}

</style> 
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
    
    <form name="getSacopReport" id="getSacopReport" action="CrearReporteSacop.do" method="post"> 
        <input type="hidden" id="bandejaReporte" name="bandejaReporte" value="" />
        <input type="hidden" id="efectivaSacopReporte" name="efectivaSacopReporte" value="" />
        <input type="hidden" id="estadoSacopReporte" name="estadoSacopReporte" value="" />
        <input type="hidden" id="fechaEmisionSacopReporte" name="fechaEmisionSacopReporte" value="" />
        <input type="hidden" id="areaResponsableSacopReporte" name="areaResponsableSacopReporte" value="" />
        <input type="hidden" id="responsableSacopReporte" name="responsableSacopReporte" value="" />
        <input type="hidden" id="emisorSacopReporte" name="emisorSacopReporte" value="" />
        <input type="hidden" id="idSacopReporte" name="idSacopReporte" value="" />
        <input type="hidden" id="fechaEstimadaSacopReporte" name="fechaEstimadaSacopReporte" value="" />
    </form>

	<table cellSpacing="1" cellPadding="0" align="center" border="0"
		width="100%" height="100%">
		<tr>
			<td width="<%=ToolsHTML.ANCHO_MENU%>px" valign="top" align="left">
				<table cellSpacing="0" cellPadding="2" border="0"
					width="<%=ToolsHTML.ANCHO_MENU%>px" height="100%"
					style="border-color: #ofofof; border: 1px solid #efefef">
					<tr>
						<td height="30px" class="ppalBoton"
							onmouseover="this.className='ppalBoton ppalBoton2'"
							onmouseout="this.className='ppalBoton'">
							<table border="0" cellspacing="0" cellpadding="2" height="100%">
								<tr>
								<!-- aqui darwin -->
								<td>
									<a id="opt" href="#" class="menutip enlace_imagen">
								    <img src="images/rightDark.gif" border="0" onClick="" onmouseover="document.getElementById('x').style.display='';">
								    <span id="x" onmouseover="limitar('opt')" class="nueva" style="display:none;position:absolute;left:11px;top:9px;">
								    	<iframe name="optionP" src="optionSacop.jsp" frameborder="0" style="height:230;width:150;border: 1px solid #0cf"></iframe>
								    </span>
								    </a>
								</td>
								<!-- fin aqui darwin -->
									<td class="ppalTextBold" width="100%"><%=rb.getString("scp.sacop")%>
									</td>
									<td>&nbsp;</td>
									<td><span><img
											src="<%=basePath%>icons/page_white.png"
											title="<%=rb.getString("scp.new")%>"
											onClick="javascript:irNew('CrearSacop.do');" /></span>&nbsp;</td>
									<td id="pageExcel" style="display: none"><span><img
											src="<%=basePath%>icons/page_excel.png"
											title="<%=rb.getString("lst.reporte")%>"
											onClick="javascript:downloadReport();" /></span>&nbsp;</td>
									<td id="todos" style="display:"><span><img
											id="imgTodos"
											src="<%=basePath%><%=viewAll?"images/todos2.gif":"images/todos.gif"%>"
											width="16" height="16"
											title="<%=rb.getString("scp.visualizartodos")%>"
											onClick="javascript:todos('VisualizarTodos.do');" /></span>&nbsp;</td>
								</tr>
							</table>
						</td>
					</tr>
					<tr>
						<td height="99%" bgcolor="white" valign="top"
							background="menu-images/backgd.jpg">

							<table class="ppalText" style="cursor: pointer;" cellspacing="5">
								<tr onclick="ver('Pendiente')">
									<td><img src="<%=basePath%>menu-images/doc.gif" /></td>
									<td><%=rb.getString("scp.sacop")%>&nbsp;<%=rb.getString("scp.pendientes")%>
										<span id="lblPendiente"
										style="display:<%=sacopPendiente==0?"none":""%>"
										class="cantidad">(<%=sacopPendiente%>)
									</span></td>
								</tr>
								<tr onclick="ver('Rechazado')">
									<td><img src="<%=basePath%>menu-images/doc.gif" /></td>
									<td><%=rb.getString("scp.sacop")%>&nbsp;<%=rb.getString("scp.rechazados")%>
										<span id="lblRechazados"
										style="display:<%=sacopRechazado==0?"none":""%>"
										class="cantidad">(<%=sacopRechazado%>)
									</span></td>
								</tr>
								<tr onclick="ver('Cerrado')">
									<td><img src="<%=basePath%>menu-images/doc.gif" /></td>
									<td><%=rb.getString("scp.sacop")%>&nbsp;<%=rb.getString("scp.cerrado")%>
										<span id="lblCerrado"
										style="display:<%=sacopCerrado==0?"none":""%>"
										class="cantidad">(<%=sacopCerrado%>)
									</span></td>
								</tr>
								<tr onclick="ver('Registro')">
									<td><img src="<%=basePath%>menu-images/doc.gif" /></td>
									<td><%=rb.getString("scp.registrosPendiente")%> <span
										id="lblRegistrosPendiente"
										style="display:<%=registrosPendiente==0?"none":""%>"
										class="cantidad">(<%=registrosPendiente%>)
									</span></td>
								</tr>
								<tr>
									<td colspan="2">
										<form name="reporte" action=""
											onsubmit="ver('Buscar'); return false;">
											<input type="hidden" name="buscar" value="buscar" />
											<table border="0" cellspacing="0" cellpadding="0"
												class="ppalText" style="padding: 0">
												<tr>
													<td><%=rb.getString("scpintouch.numero")%>&nbsp; <%=rb.getString("scpintouch.sacop")%>
													</td>
													<td rowspan="2">&nbsp; <img
														src="<%=basePath%>icons/find.png"
														onclick="ver('Buscar'); return false;"
														title="<%=rb.getString("btn.search")%>" />
													</td>
												</tr>
												<tr>
													<td><input type="text" id="idSacop" name="idSacop"
														style="width: 150px; height: 19px" value="" /></td>
												</tr>
												<!-- filtro -->
												<tr>
													<td><%=rb.getString("scp.actionType")%></td>
												</tr>
												<tr>
													<td><select id="tipoSacop" name="tipoSacop"
														style="width: 150px;">
															<option value=""><%=rb.getString("lista.todas")%></option>
															<logic:present name="todasTipos" scope="request">
																<logic:iterate id="bean" name="todasTipos"
																	scope="request"
																	type="com.desige.webDocuments.sacop.forms.TipoSacop">
																	<option value="<%=bean.getPrefijo()%>"><%=bean.getTipo()%></option>
																</logic:iterate>
															</logic:present>
													</select></td>
												</tr>

												<!-- filtro Origen -->
												<tr>
													<td><%=rb.getString("scp.origin")%></td>
												</tr>
												<tr>
													<td><select id="origenSacop" name="origenSacop"
														style="width: 150px;">
															<option value=""><%=rb.getString("lista.todas")%></option>
															<logic:present name="todasOrigens" scope="request">
																<logic:iterate id="bean" name="todasOrigens"
																	scope="request"
																	type="com.desige.webDocuments.sacop.forms.TitulosPlanillasSacop">
																	<option value="<%=bean.getTitulosplanillas()%>"><%=bean.getTitulosplanillas()%></option>
																</logic:iterate>
															</logic:present>
													</select></td>
												</tr>

												<tr>
													<td><%=rb.getString("scp.emisor")%></td>
												</tr>
												<tr>
													<td><select id="emisorSacop" name="emisorSacop"
														style="width: 150px;">
															<option value=""><%=rb.getString("lista.todos")%></option>
															<logic:present name="todosUsuarios" scope="request">
																<logic:iterate id="bean" name="todosUsuarios"
																	scope="request"
																	type="com.desige.webDocuments.utils.beans.Search">
																	<option value="<%=bean.getId()%>"><%=bean.getDescript()%>(<%=bean.getAditionalInfo()%>)
																	</option>
																</logic:iterate>
															</logic:present>
													</select></td>
												</tr>
												<tr>
													<td><%=rb.getString("scp.responsable")%></td>
												</tr>
												<tr>
													<td><select id="responsableSacop"
														name="responsableSacop" style="width: 150px;">
															<option value=""><%=rb.getString("lista.todos")%></option>
															<logic:present name="todosUsuarios" scope="request">
																<logic:iterate id="bean" name="todosUsuarios"
																	scope="request"
																	type="com.desige.webDocuments.utils.beans.Search">
																	<option value="<%=bean.getId()%>"><%=bean.getDescript()%>(<%=bean.getAditionalInfo()%>)
																	</option>
																</logic:iterate>
															</logic:present>
													</select></td>
												</tr>

												<tr>
													<td><%=rb.getString("scp.receivingArea")%></td>
												</tr>
												<tr>
													<td><select id="areaResponsableSacop"
														name="areaResponsableSacop" style="width: 150px;">
															<option value=""><%=rb.getString("lista.todas")%></option>
															<logic:present name="todasAreas" scope="request">
																<logic:iterate id="bean" name="todasAreas"
																	scope="request"
																	type="com.desige.webDocuments.area.forms.Area">
																	<option value="<%=bean.getIdarea()%>"><%=bean.getArea()%></option>
																</logic:iterate>
															</logic:present>
													</select></td>
												</tr>
												<tr>
													<td><%=rb.getString("scp.fueefectiva")%></td>
												</tr>
												<tr>
													<td><select id="efectivaSacop" name="efectivaSacop"
														style="width: 150px;">
															<option value="">--</option>
															<option value="0"><%=rb.getString("scp.si")%></option>
															<option value="1"><%=rb.getString("scp.no")%></option>
													</select></td>
												</tr>
												<tr>
													<td><%=rb.getString("scp.estado")%></td>
												</tr>
												<tr>
													<td><select id="estadoSacop" name="estadoSacop"
														style="width: 150px;">
															<option value=""><%=rb.getString("lista.todos")%></option>
															<option value="0"><%=rb.getString("scp.borrador")%></option>
															<option value="1"><%=rb.getString("scp.emitido")%></option>
															<option value="2"><%=rb.getString("scp.aprobado1")%></option>
															<option value="3"><%=rb.getString("scp.inejecucion")%></option>
															<!-- <option value="4"><%=rb.getString("scp.pendiente")%></option>  -->
															<option value="5"><%=rb.getString("scp.verificacion")%></option>
															<option value="10"><%=rb.getString("scp.verificado")%></option>
															<option value="6"><%=rb.getString("scp.rechazado1")%></option>
															<option value="7"><%=rb.getString("scp.cerrado1")%></option>
													</select></td>
												</tr>
												<tr>
													<td><%=rb.getString("scp.fechae")%>:&nbsp;<%=rb.getString("scp.mes")%>/&nbsp;<%=rb.getString("scp.anio")%>
														<table cellpadding="0" cellspacing="0" border="0"
															style="margin: 0px;">
															<tr>
																<td><input type="text" id="fechaEmisionSacop"
																	name="fechaEmisionSacop" maxlength="10" size="12"
																	value=""> <span id="calendario"></span></td>
																<td><a href="#"
																	onclick='showCalendarDDMMYYYY(document.reporte.fechaEmisionSacop,"calendario")'><img
																		src="<%=basePath%>images/calendario2.gif" border="1" /></a>
																</td>
																<td><a href='#'
																	onclick='document.reporte.fechaEmisionSacop.value=""; document.reporte.fechaEmisionSacop.value="";'
																	value='<%=rb.getString("btn.clear")%>'><img
																		src="images/eraser.gif" border="0"
																		alt='<%=rb.getString("btn.clear")%>'
																		text='<%=rb.getString("btn.clear")%>'></a>
															</tr>
														</table></td>
												</tr>
												<tr>
													<td><%=rb.getString("scp.fechastimada")%>:&nbsp;<%=rb.getString("scp.mes")%>/&nbsp;<%=rb.getString("scp.anio")%>
														<table cellpadding="0" cellspacing="0" border="0"
															style="margin: 0px;">
															<tr>
																<td><input type="text" id="fechaEstimadaSacop"
																	name="fechaEstimadaSacop" maxlength="10" size="12"
																	value=""> <span id="calendario1"></span></td>
																<td><a href="#"
																	onclick='showCalendarDDMMYYYY(document.reporte.fechaEstimadaSacop,"calendario1")'><img
																		src="<%=basePath%>images/calendario2.gif" border="1" /></a>
																</td>
																<td><a href='#'
																	onclick='document.reporte.fechaEstimadaSacop.value=""; document.reporte.fechaEstimadaSacop.value="";'
																	value='<%=rb.getString("btn.clear")%>'><img
																		src="images/eraser.gif" border="0"
																		alt='<%=rb.getString("btn.clear")%>'
																		text='<%=rb.getString("btn.clear")%>'></a>
															</tr>
														</table></td>
												</tr>
												<tr>
													<td>
														<!-- Este submit esta aqui para permitir que un ENTER envie este form -->
														<input type="submit" style="display: none;" />
													</td>
												</tr>
											</table>
										</form>
									</td>
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
				<table border="0" cellspacing="0" cellpadding="0" width="100%"
					height="100%">
					<tr>
						<td height="20px"><iframe name="bandejaTit" width="100%"
								height="20px" src="<%=basePath%>principalSacop1Tit.jsp"
								border="0px" marginwidth="0px" marginheight="0px"
								frameborder="0" scrolling="no"></iframe></td>
					</tr>
					<tr>
						<td><iframe id="bandeja" name="bandeja" width="100%"
								height="100%" border="0px" marginwidth="0px" marginheight="0px"
								frameborder="0"></iframe></td>
					</tr>
				</table>
			</td>
		</tr>
	</table>

	<script type="text/javascript">
function printIframe(id){
	window.frames[2].window.frames[0].document.getElementById('botoneraSacop').style.display = "none";
    window.frames[2].window.frames[0].focus();
    window.frames[2].window.frames[0].print();
    setTimeout(function(){unHideTR();}, 3000);
}

function getFrameContents(iframe){
	   var iFrame = iframe //document.getElementById('id_description_iframe');
	   var iFrameBody;
	   if ( iFrame.document ) { // FF
		 console.log("chrome");
	     iFrameBody = iFrame.document.getElementsByTagName('body')[0];
	   } else if ( iFrame.contentDocument ) { // FF
		 console.log("FF");
	     iFrameBody = iFrame.contentDocument.getElementsByTagName('body')[0];
	   } else if ( iFrame.contentWindow ) { // IE
		console.log("IE");
	     iFrameBody = iFrame.contentWindow.document.getElementsByTagName('body')[0];
	   }
	    console.log(iFrameBody.innerHTML);
	 }

function unHideTR(){
	window.frames[2].window.frames[0].document.getElementById('botoneraSacop').style.display = "";
}


</script>

<div id="caja" class="fondoOscuro" style="display:none;">
</div>
<div id="caja2" style="display:none;position:absolute;top:0;left:0;">
    <iframe id="frmConfig" name="frmConfig" src="" frameborder="no" style="width:0px;height:0px;"></iframe>
</div>
<div id="caja3" style="display:none;">
    <div style="width:100%; text-align:right"> 
        <input id="printSacopBtn" class="sacopButton" type="button" value="Imprimir" onclick="javascript:printIframe('frmConfig');">
        <input class="sacopButton" type="button" value="Cerrar" onclick="javascript: administrarCancelar();">
    </div>
</div>   
</body>
</html>
