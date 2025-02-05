<!--/**
 * Title: recibos.jsp <br>
 * Copyright: (c) 2024 Darwin Desarrollo personal<br/>
 *
 * @author Ing. Darwin Uzcategui
 * @version Gestion@Envio v1.0
 */-->
<%@page import="org.apache.poi.util.SystemOutLogger"%>
<%@page import="com.itextpdf.text.log.SysoCounter"%>
<%@ page import="java.util.ResourceBundle,
				 com.desige.webDocuments.files.forms.DocumentForm,
				 java.util.ArrayList,
				 java.util.List,
				 java.util.Collection,
                 com.desige.webDocuments.utils.ToolsHTML, 
                 com.desige.webDocuments.persistent.managers.HandlerParameters,
                 com.desige.webDocuments.utils.beans.Users,
                 com.desige.webDocuments.utils.beans.SuperActionForm,
                 com.desige.webDocuments.utils.beans.PaginPage,
                 com.desige.webDocuments.persistent.managers.HandlerBD,
                 com.desige.webDocuments.persistent.managers.HandlerDBUser,
				 com.gestionEnvio.custon.dostorres.forms.BaseReciboForm,
                 com.desige.webDocuments.utils.Constants"%>


<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<%@ page language="java" %>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";

	Users usuarioActual = session.getAttribute("user")!=null?(Users)session.getAttribute("user"):null;
	Users users = session.getAttribute("user")!=null?(Users)session.getAttribute("user"):null;
	
	PaginPage Pagingbean = null;
	int lineas=Constants.LINEAS_POR_PAGINA;
	//paginacion fin
	int nameWidth=60;
	int rootWidth=100;
	int item = 0;
	String ruta = "";
	int contador = 0;
	
	boolean isPopUp = request.getParameter("popup")!=null && request.getParameter("popup").equals("true");

	if(session.getAttribute("rec")==null) {
		BaseReciboForm rec = new BaseReciboForm();
		request.setAttribute("rec",rec);
	}
	
	
    ResourceBundle rb = ToolsHTML.getBundle(request);
    String onLoad = ToolsHTML.getMensajePages(request,rb);
    Users usuario = (Users)session.getAttribute("user");
    String ok = (String)session.getAttribute("usuario");

  /*  
    if (onLoad==null){
        response.sendRedirect(rb.getString("href.logout"));
    }
  */
    String aviso = ToolsHTML.getAttribute(request,"aviso")!=null?(String)ToolsHTML.getAttribute(request,"aviso"):"";
    String codigo = (String)ToolsHTML.getAttribute(request,"codigo");
       if (ToolsHTML.isEmptyOrNull(codigo)) {
    	   codigo = "";
    	   }
    String unNombre = ToolsHTML.getAttribute(request,"unNombre")!=null?(String)ToolsHTML.getAttribute(request,"unNombre"):"";
    String unLocal = ToolsHTML.getAttribute(request,"unLocal")!=null?(String)ToolsHTML.getAttribute(request,"unLocal"):"";
    String periodo = ToolsHTML.getAttribute(request,"periodo")!=null?(String)ToolsHTML.getAttribute(request,"periodo"):"";
    
    String ordenVersion = request.getParameter("ordenVersion")!=null?"checked":"";
    String ordenNombre = request.getParameter("ordenNombre")!=null?"checked":"";
    String ordenTypeDocument = request.getParameter("ordenTypeDocument")!=null?"checked":"";
    String ordenNumber = request.getParameter("ordenNumber")!=null?"checked":"";
    String ordenPropietario = request.getParameter("ordenPropietario")!=null?"checked":"";
    String ordenApproved = request.getParameter("ordenApproved")!=null?"checked":"";
    String accion = request.getParameter("accion")!=null?request.getParameter("accion"):"";
    String botonWhastsapp2 = request.getParameter("botonWhastsapp2")!=null?request.getParameter("botonWhastsapp2"):"";
    
    String from = request.getParameter("from")!=null?request.getParameter("from"):"";
    String size = String.valueOf(session.getAttribute("size")); 
  
  
%>
<html>
<head>
<title><%=rb.getString("principal.title")%></title>
<jsp:include page="meta.jsp" />
<link href="estilo/estilo.css" rel="stylesheet" type="text/css">


</style>

<script language=javascript src="./estilo/funciones.js?xxsdf"></script>
<script language=javascript src="./estilo/popcalendar2inner.js"></script>


<script language=javascript src="./estilo/fechas.js"></script>
<%-- Luis Cisneros 23-02-07 --%>

<script language="JavaScript">
    <!-- variable global -->
    
	
    setDimensionScreen();
    

    function seguridad() {
        alert('<%=rb.getString("sch.notDocsView")%>');
    }

    function searchItem(forma,action,type) {
    	
    	 
        if (type==0) {
            forma.toSearch.value = "";
        }

        
        if (forma.emisionFrom.value.length != 0 && forma.emisionTo.value.length != 0 ){
            if (getDate(forma.emisionFrom.value) > getDate(forma.emisionTo.value) ) {
                alert('<%=rb.getString("sch.dateFromGreaterThanTo")%>');                
               return false;
            }
        }
        

    
        forma.emisionFromHIDDEN.value = forma.emisionFrom.value;
        forma.emisionToHIDDEN.value = forma.emisionTo.value;
    
        
        if (forma.aviso.value.length ==0 && forma.nombre.value.length==0 && 
        	forma.unNombre.value == "0" &&  forma.codigo.value.length==0 &&
        	forma.emisionFrom.value.length==0  &&
        	forma.periodo.value.length==0  &&
            forma.local.value.length==0 ) {
            
	        var filtrosLleno=false;
	       	try {
	            for(var i=1;i<=100;i++){
	        		//alert("d"+document.getElementsByName("d"+i).length+" = "+document.getElementsByName("d"+i)[0].value);
	        		if(document.getElementsByName("d"+i)[0].value.length>0) {
	        			filtrosLleno=true;	
	        		}
	            }
	        } catch(e) {}
	        
	        if(!filtrosLleno) {
	            if (!confirm('<%=rb.getString("sch.notParam2")%>')) {
	                return false;
	               // alert("Por razones de rendimiento, por favor seleccione un filtro de busqueda ante de realizar la busqueda");
	            }
            }
             
           // return false;
        }
        if (type==0) {
            forma.toSearch.value = "";
        }
        forma.action = action;
       
   	 
        forma.submit();
    }

    function enviarEmailDarwin(frm,action) {
   
    	frm = document.search;
        frm.emisionFromHIDDEN.value = frm.emisionFrom.value;
        frm.emisionToHIDDEN.value = frm.emisionTo.value;

    	//alert(frm.emisionFrom.value.length);
    	if (frm.emisionFrom.value.length != 0 && frm.emisionTo.value.length != 0 ){
            if (getDate(frm.emisionFrom.value) > getDate(frm.emisionTo.value) ) {
                alert('<%=rb.getString("sch.dateFromGreaterThanTo")%>');                
               return false;
            }
        }
        
        
        frm.action = action;//"EnviarRecibo.do";
        frm.action = "EnviarRecibo.do";
        frm.submit();
    }
    
   
    function editField(pages,input,value,forma) {
//        var hWnd = null;
		//alert("pages="+pages);
        hWnd = window.open(pages+"?input="+input+"&value="+value+"&nameForma="+forma.name,"Catalogo","width=600,height=450,resizable=yes,statubar=yes,scrollbars=yes,left=200,top=150");
        if ((document.window != null) && (!hWnd.opener)){
            hWnd.opener = document.window;
        }
    }

    function showCharge(userName,charge,nosecuencial) {
    	charge = charge.replace(/\[/gi,"-").replace(/\]/gi,"-");
    	charge = charge.replace(/\(/gi,"-").replace(/\)/gi,"-");
        window.open("showCharge.jsp?userName="+userName+"&nosecuencial="+nosecuencial+"&charge="+charge, "WebDocuments", "resizable=no,scrollbars=yes,statusbar=yes,width=300,height=200,left=410,top=250");
    }

    function ordenar(orderBy) {

        forma = document.search;
        forma.orderBy.value = orderBy;
        forma.action = "orderRecibo.do";
        forma.pages.value = "recibo";
        //es necesario copiar en el hidden el valor, ya que como el campo visible es readonly y disabled no se enviara al form.
        forma.emisionFromHIDDEN.value = forma.emisionFrom.value;
        forma.emisionToHIDDEN.value = forma.emisionTo.value;

        forma.submit();
    }

    function getDateEmision(dateField,text){
        //window.open("getDate.jsp?field=" + dateField + "&nameForm="+nameForm+"&valueForm="+dateValue+"&text="+text,"","width=350,height=300,resizable=no,scrollbars=yes,left=300,top=250");
        //window.open("getDate.jsp?field=" + dateField + "&nameForm="+nameForm+"&valueForm="+dateValue+"&text="+text,"","width=275,height=170,resizable=no,scrollbars=yes,left=460,top=250");
        //showCalendarDDMMYYYY(dateField,"calendario");
        showCalendar(dateField,"calendario");
    }
    
    function getDateShowFormatoMMYYYY(dateField,text){
    	showCalendarFormato(dateField,"calendario");
        //window.open("getDate.jsp?field=" + dateField + "&nameForm="+nameForm+"&valueForm="+dateValue+"&text="+text,"","width=275,height=170,resizable=no,scrollbars=yes,left=460,top=250");
    }

    function validarFechaExpiracion(valor, msg) {
        if (!validarFecha(valor)) {
            alert(msg);
            return false;
        }
        return true;
    }

    function enviarWhastAppDarwin(action,boton,frm) {
    	
    	var formaSelection = document.getElementById("Selection");
        formaSelection.botonWhatsapp21.value = boton;
        forma = document.search;
        var forma = document.getElementById("search");
        forma.botonWhastsapp2.value = formaSelection.botonWhatsapp21.value;
		 
        if (forma.emisionFrom.value.length != 0 && forma.emisionTo.value.length != 0 ){
            if (getDate(forma.emisionFrom.value) > getDate(forma.emisionTo.value) ) {
                alert('<%=rb.getString("sch.dateFromGreaterThanTo")%>');                
               return false;
            }
        }
        
        forma.emisionFromHIDDEN.value = forma.emisionFrom.value;
        forma.emisionToHIDDEN.value = forma.emisionTo.value;
         
        frm.action = action;//"EnviarRecibo.do";
        frm.submit();
    }
    
    function more(valor) {
    	document.search.moreP.value=(valor==''?true:false);
    	document.getElementById("mas").style.display=(valor==''?"none":"");
    	document.getElementById("flechaArriba").style.display=valor;
    	document.getElementById("flechaAbajo").style.display=valor;
    	document.getElementById("signoMenos").style.display=valor;
    	
    	try{
    		for(var x=1; x<=100;x++){
    			if(typeof document.getElementById("mas_"+x)!='undefined') {
			    	document.getElementById("mas_"+x).style.display=valor;
			    }
    		}
    	} catch(e){}
    
    }

	
	/**
	*
	*/
	function viewField(tipo) {
		tipo=','+tipo+',';
		for(var i=7; i<200; i++) {
			if(document.getElementById('mas_'+i)) {
				//alert(document.getElementById('mas_'+i).className+" --- ("+tipo+")");
				if(tipo==',,' || tipo==',0,' || document.getElementById('mas_'+i).className.indexOf(tipo)!=-1) {
					document.getElementById('mas_'+i).style.display="";
				} else {
					document.getElementById('mas_'+i).style.display="none";
				}
			} else {
				break;
			}
		}
	}
	/*
	function setNormaLabel(hiddenFieldId, normaValue, normaLabel) {
		console.log("setNormas(aqui darwin set norma) ... ");
		var hiddenField = document.getElementById(hiddenFieldId);
		if (hiddenField != null) {
			hiddenField.value = normaValue;
		}
		console.log(hiddenFieldId+'Label');
		var hiddenFieldLabel = document.getElementById(hiddenFieldId+'Label');
		if (hiddenFieldLabel != null) {
			console.log('seteando label '+normaLabel);
			hiddenFieldLabel.value = normaLabel;
			setTimeout('setNormasAfterChange()',500);
		}
	} 
	*/
/*
	function setNormasAfterChange(normSeleted) {
		console.log("setNormasAfterChange...");
		var normSelected = document.getElementById('normISO').value; 
		var normSelectedIndiceCero = ""; 
		
		var proSelect = document.getElementById('idProgramAudit');
		var plaSelect = document.getElementById('idPlanAudit');
		
		document.getElementById('rowIdPlanAudit').style.display='none';
		proSelect.options.length = 0; // limpiamos el select de program
		plaSelect.options.length = 0; // limpiamos el select de plan
		// norma cero cambio pre cargada
				for(var x=0;x<normaCero.length;x++) {
					if(normaCero[x][0]==normSelected) {
						normSelectedIndiceCero = normaCero[x][2]+",";
					}
				}
			
		if(normSelected && normSelected.length>0) {
			var arr = normSelectedIndiceCero.split(",");
			proSelect.options[proSelect.options.length] = new Option("Selecciona un programa","");
			for(var i=0;i<arr.length;i++) {
				for(var x=0;x<programas.length;x++) {
					if(programas[x][2]==arr[i]) {
						proSelect.options[proSelect.options.length] = new Option(programas[x][1],programas[x][0]);
					}
				}
			}
			document.getElementById('rowIdProgramAudit').style.display=(proSelect.options.length>0?'':'none');
			document.getElementById('rowIdPlanAudit').style.display=(proSelect.options.length>0?'':'none');
		} else {
			document.getElementById('rowIdProgramAudit').style.display='none';
			document.getElementById('rowIdPlanAudit').style.display='none';
		}
		
	}
	*/
	/*
	function fillPlan(idProgram) {
		
		var plaSelect = document.getElementById('idPlanAudit');
		var normSelected = document.getElementById('normISO').value; 
		var normSelectedIndiceCero = ""; 
		// norma cero cambio pre cargada
		for(var x=0;x<normaCero.length;x++) {
			if(normaCero[x][0]==normSelected) {
				normSelectedIndiceCero = normaCero[x][2];
			}
		}
		plaSelect.options.length = 0;
		if(idProgram) {
			plaSelect.options[plaSelect.options.length] = new Option("Selecciona un plan","");
			for(var x=0;x<planes.length;x++) {
				if(planes[x][2]==idProgram  && planes[x][3]== normSelectedIndiceCero) {
					plaSelect.options[plaSelect.options.length] = new Option(planes[x][1],planes[x][0]);
				}
			}
		}
	}
	*/


</script>
<!-- paginacion ini -->
<script type="text/javascript">
    function paging_OnClick(pageFrom){
        document.formPagingPage.from.value = pageFrom;
        document.formPagingPage.submit();
    }
</script>
<!-- paginacion fin -->

<script type="text/javascript" src="estilo/scroll.js"></script>
<script type="text/javascript">
	registraScroll('flechaAbajo','flechaArriba','listaOpcion',8,-8);
</script>
</head>

<body class="bodyInternas" <%=onLoad%>>
	<table cellSpacing="1" cellPadding="0" align="center" border="0"
		width="100%" height="100%">


		<!-- Formulario para Obtener el nodo Seleccionado por el usuario -->
		<form name="search" method="post" action="showRecibos.do" id="search">
			<input type="hidden" name="moreP"
				value="<%=session.getAttribute("moreP")%>" /> <input type="hidden"
				name="botonWhastsapp2" value="1" /> <input type="hidden"
				name="popup" value="<%=isPopUp%>" />
			<tr>
				<td width="<%=ToolsHTML.ANCHO_MENU%>px" valign="top" align="left">
					<table cellSpacing="0" cellPadding="2" border="0"
						width="<%=ToolsHTML.ANCHO_MENU%>px" height="100%"
						style="border-color: #ofofof; border: 1px solid #efefef">
						<tr>
							<td height="30px" class="ppalBoton"
								onmouseover="this.className='ppalBoton ppalBoton2'"
								onmouseout="this.className='ppalBoton'">
								<table border="0" cellspacing="0" cellpadding="0">
									<tr>
										<td>
											<!-- 
										<a id="opt" href="#" class="menutip enlace_imagen">
												<img src="images/rightDark.gif" border="0" onClick=""
												onmouseover="document.getElementById('x').style.display='';">
												<span id="x" onmouseover="limitar('opt')" class="nueva"
												style="display: none; position: absolute; left: 11px; top: 9px;">
													<iframe name="optionP" src="optionPublished.jsp"
														frameborder="0"
														style="height: 230; width: 150; border: 1px solid #0cf"></iframe>
											</span>
										</a>
										 -->
										</td>
										<td class="ppalTextBold" width="100%">&nbsp;<%=rb.getString("enl.docPublic")%>
										</td>

										<td><span><img src="icons/buscar32.svg"
												title="<%=rb.getString("btn.search")%>"
												onClick="javascript:searchItem(document.search,'showRecibos.do',1);"></span>&nbsp;
										</td>
										<%if(usuario.getIdGroup()!=null && usuario.getIdGroup().equals(Constants.ID_GROUP_ADMIN)) {%>
										<td><span><img src="icons/whatsapp32.svg"
												title="<%=rb.getString("btn.whatsapp")%>"
												onClick="javascript:enviarWhastAppDarwin('EnviarRecibo.do','2',document.search);"></span>&nbsp;
											<!--  	onClick="javascript:enviarWhastAppDarwin(document.search,'EnviarRecibo.do','1','55');"></span>&nbsp;-->
										</td>
										<%}%>
										<td><span><img src="icons/email32.svg"
												title="<%=rb.getString("btn.email")%>"
												onClick="javascript:enviarEmailDarwin(document.search,'EnviarRecibo.do');"></span>&nbsp;</td>
										<td class="none">
											<table border="0" cellspacing="1" cellpadding="0">
												<tr>
													<td height="1px">
														<div id="flechaArriba" style="display: none">
															<img src="images/up3.gif">
														</div>
														<div id="flechaAbajo" style="display: none">
															<img src="images/down3.gif">
														</div>
													</td>
												</tr>
											</table>
										</td>
										<td>&nbsp;<span id="signoMenos" style="display: none"><img
												src="images/replegar.gif"
												title="<%=rb.getString("seeLess")%>"
												onClick="javascript:more('none');"></span>&nbsp;
										</td>
									</tr>
								</table>
							</td>
						</tr>
						<tr>
							<td height="99%" bgcolor="white" valign="top"
								background="menu-images/backgd.png">
								<div id="listaOpcion" style="height: 100%" class="scrollEnabled">
									<table class="ppalText" style="cursor: pointer;"
										cellspacing="0" border="0">
										<tr>
											<td>Aviso</br> <input type="text" name="aviso"
												style="width: 180px; height: 19px"
												value="<%=request.getAttribute("aviso")!=null?request.getAttribute("aviso"):""%>" />
											</td>
										</tr>
										<tr>
											<td>Codigo<br /> <input type="text" name="codigo"
												style="width: 180px; height: 19px"
												value="<%=request.getAttribute("codigo")!=null?request.getAttribute("codigo"):""%>" />
											</td>
										</tr>
										<tr>
											<td>local<br /> <input type="text" name="local"
												style="width: 180px; height: 19px"
												value="<%=request.getAttribute("local")!=null?request.getAttribute("local"):""%>" />
											</td>
										</tr>

										<tr>
											<td><%=rb.getString("sch.name")%><br /> <input
												type="text" name="nombre" style="width: 180px; height: 19px"
												value="<%=request.getAttribute("nombre")!=null?request.getAttribute("nombre"):""%>" />
											</td>
										</tr>
										<tr>
											<td>Emision desde
												<table cellpadding="0" cellspacing="0" border="0"
													style="margin: 0px;">
													<tr>
														<td><input type="hidden" name="emisionFromHIDDEN"
															value=""></input> <input type="text" name="emisionFrom"
															readonly disabled maxlength="10"
															style="width: 140px; height: 19px"
															value="<%=request.getAttribute("emisionFromHIDDEN") == null ?"":request.getAttribute("emisionFromHIDDEN")%>" />
														</td>
														<td><a href="#"
															onclick='getDateEmision(document.search.emisionFrom,"calendario")'><img
																src="images/calendario2.gif" border="1"></a></td>
														<td><a href='#'
															onclick='document.search.emisionFromHIDDEN.value=""; document.search.emisionFrom.value="";'
															value='<%=rb.getString("btn.clear")%>'><img
																src="images/eraser.gif" border="0"
																alt='<%=rb.getString("btn.clear")%>'
																text='<%=rb.getString("btn.clear")%>'></a></td>
													</tr>
												</table>
											</td>
										</tr>
										<tr>
											<td>emision hasta
												<table cellpadding="0" cellspacing="0" border="0"
													style="margin: 0px;">
													<tr>
														<td><input type="hidden" name="emisionToHIDDEN"
															value=""></input> <input type="text" name="emisionTo"
															readonly disabled maxlength="10"
															style="width: 140px; height: 19px"
															value="<%=request.getAttribute("emisionToHIDDEN") == null ?"":request.getAttribute("emisionToHIDDEN")%>" />
														</td>
														<td><a href="#"
															onclick='getDateEmision(document.search.emisionTo,"calendario")'><img
																src="images/calendario2.gif" border="1"></a></td>
														<td><a href='#'
															onclick='document.search.emisionToHIDDEN.value=""; document.search.emisionTo.value="";'
															value='<%=rb.getString("btn.clear")%>'><img
																src="images/eraser.gif" border="0"
																alt='<%=rb.getString("btn.clear")%>'
																text='<%=rb.getString("btn.clear")%>'></a></td>
													</tr>
												</table>
											</td>
										</tr>

										<tr>
											<td><%=rb.getString("admin.planAudit.peridoDD")%>:&nbsp;<%=rb.getString("scp.mes")%>/&nbsp;<%=rb.getString("scp.anio")%>
												<table cellpadding="0" cellspacing="0" border="0"
													style="margin: 0px;">
													<tr>
														<td><input type="text" name="periodo" maxlength="10"
															size="12"
															value="<%=request.getAttribute("periodo") == null ?"":request.getAttribute("periodo")%>" />
															<span id="calendario"></span></td>
														<td><a href="#"
															onclick='getDateShowFormatoMMYYYY(document.search.periodo,"calendario")'><img
																src="images/calendario2.gif" border="1" /></a></td>

														<td><a href='#'
															onclick='document.search.periodo.value=""; document.search.periodo.value="";'
															value='<%=rb.getString("btn.clear")%>'><img
																src="images/eraser.gif" border="0"
																alt='<%=rb.getString("btn.clear")%>'
																text='<%=rb.getString("btn.clear")%>'></a>
													</tr>
												</table></td>
										</tr>

										<tr id="mas">
											<td align="right"><a herf="#" onclick="more('')"><b><%=rb.getString("seeMore")%>...</b></a>
											</td>
										</tr>
										<tr id="mas_1" style="display: none">
											<td>UN nombre <br /> <select name="unNombre" size="1"
												style="width: 180px; height: 19px" styleClass="classText">
													<logic:present name="allUsers" scope="session">
														<option value="0">&nbsp;</option>
														<logic:iterate id="bean" name="allUsers" scope="session"
															type="com.desige.webDocuments.utils.beans.Search">
															<logic:equal value="<%=unNombre%>" property="id"
																name="bean">
																<option value="<%=bean.getId()%>" selected="true"><%=bean.getDescript()%></option>
															</logic:equal>
															<logic:notEqual value="<%=unNombre%>" property="id"
																name="bean">
																<logic:notEqual value="<%=unNombre%>" property="id"
																	name="bean">
																	<option value="<%=bean.getId()%>"><%=bean.getDescript()%></option>
																</logic:notEqual>
															</logic:notEqual>
														</logic:iterate>
													</logic:present>
											</select> <logic:notPresent name="allUsers" scope="session">
													<%=rb.getString("E0006")%>
												</logic:notPresent> <input type="hidden" name="nameOwner" value="" />
											</td>
										</tr>

										<tr id="mas_2" style="display: none">
											<td>Un local<br /> <select name="unLocal" size="1"
												style="width: 180px; height: 19px" styleClass="classText">
													<logic:present name="allLocales" scope="session">
														<option value="0">&nbsp;</option>
														<logic:iterate id="bean" name="allLocales" scope="session"
															type="com.desige.webDocuments.utils.beans.Search">
															<logic:equal value="<%=unLocal%>" property="id"
																name="bean">
																<option value="<%=bean.getId()%>" selected="true"><%=bean.getDescript()%></option>
															</logic:equal>
															<logic:notEqual value="<%=unLocal%>" property="id"
																name="bean">
																<logic:notEqual value="<%=unLocal%>" property="descript"
																	name="bean">
																	<option value="<%=bean.getId()%>"><%=bean.getDescript()%></option>
																</logic:notEqual>
															</logic:notEqual>
														</logic:iterate>
													</logic:present>
											</select> <logic:notPresent name="allLocales" scope="session">
													<%=rb.getString("E0006")%>
												</logic:notPresent> <input type="hidden" name="nameOwner" value="" />
											</td>
										</tr>

										<!-- INI CAMPOS ADICIONALES -->
										<!-- FIN CAMPOS ADICIONALES -->

									</table>
								</div> <input type="hidden" name="accion" value="1" /> <input
								type="hidden" name="orderBy" value="" /> <input type="hidden"
								name="pages" value="" />
								</form>

								<form name="Selection" id="Selection" action="EnviarRecibo.do">
									<input type="hidden" name="botonWhatsapp21" value="1" /> <input
										type="hidden" name="idVersion" value="" /> <input
										type="hidden" name="showStruct" value="true" /> <input
										type="hidden" name="from" value="recibos.jsp" />
								</form>
							</td>
						</tr>
						<%if( !isPopUp && usuario!=null && !size.equals("null")) {
                            out.println(ToolsHTML.printMenu(rb,usuario.getUser(),usuario.getIdGroup(),true,request));
                        }%>
					</table>
				</td>
				<td align="center" valign="top">
					<table align=center border="0" width="100%" cellSpacing="0"
						cellPadding="0">
						<tr>
							<td colspan="4">
								<table class="clsTableTitle" width="100%" cellSpacing="0"
									cellPadding="0" align=center border="0">
									<tbody>
										<tr>
											<td class="td_title_bc" height="19" width="30%">
												<!-- paginador --> <logic:present name="recibo">

													<!-- paginacion ini -->
													<%
							
							                            if(size != null && !size.equals("null")){
							                            	//vemos su valor
							                            	if(ToolsHTML.isNumeric(request.getParameter("size"))){
							                            		//tengo valor de size en el request
							                            		//el size de session no es nulo, vemos si es numerico y mayor que cero
							                            		if(!(ToolsHTML.isNumeric(size) && Integer.parseInt(size) > 0)){
							                            			//size es numero, pero no es mayor que cero, usamos entonces el valor del request
							                            			size = request.getParameter("size");
							                            		}
							                            	}
							                            }
							                            
							                            if (!ToolsHTML.isNumeric(size)) {
							                                size = "1";
							                            }
							                            if (!ToolsHTML.isNumeric(from)) {
							                                from = "0";
							                            }
							                            //PaginPage Pagingbean = ToolsHTML.getBeanPaginPage(from,Integer.parseInt(size),String.valueOf(users.getNumRecord()));
							                            Pagingbean = ToolsHTML.getBeanPaginPage(from,Integer.parseInt(size),String.valueOf(lineas));
							                            String routImgs = "menu-images/";
							                        %>
													<!-- paginacion fin -->

													<!-- paginacion ini -->
													<form name="formPagingPage" method="post"
														action="recibos.jsp">
														<input type="hidden" name="from" value=""> <input
															type="hidden" name="size" value="<%=size%>"> <input
															type="hidden" name="cmd"
															value="<%=SuperActionForm.cmdLoad%>">
													</form>
													<!-- paginacion fin -->

													<!-- paginacion ini -->
													<table class="paginadorNuevo" align="left">
														<tr>
															<td align="center">&nbsp;<img
																src="images/inicio2.gif" class="GraphicButton"
																title="<%=rb.getString("pagin.First")%>"
																onclick="paging_OnClick(0)">&nbsp;
															</td>
															<td align="center"><img src="images/left2.gif"
																class="GraphicButton"
																title="<%=rb.getString("pagin.Previous")%>"
																onclick="paging_OnClick(<%=(Integer.parseInt(Pagingbean.getPages())-1)%>)">
															</td>
															<td align="center" width="120px"><font size="1"
																color="#000000"> <%=rb.getString("pagin.title")+ " "%>
																	<%=Integer.parseInt(Pagingbean.getPages())+1%> <%=rb.getString("pagin.of")%>
																	<%=Pagingbean.getNumPages()%>
															</font></td>
															<td align="center"><img src="images/right2.gif"
																class="GraphicButton"
																title="<%=rb.getString("pagin.next")%>"
																onclick="paging_OnClick(<%=(Integer.parseInt(Pagingbean.getPages())+1)%>)">&nbsp;
															</td>
															<td align="center"><img src="images/fin2.gif"
																class="GraphicButton"
																title="<%=rb.getString("pagin.Last")%>"
																onclick="paging_OnClick(<%=Pagingbean.getNumPages()%>)">
															</td>
														</tr>
													</table>
													<!-- paginacion fin -->

												</logic:present>
											</td>
											<td class="td_title_bc" height="19" width="40%"><%=rb.getString("pb.title")%>
											</td>
											<td class="td_title_bc" height="19" width="30%"><logic:present
													name="size" scope="session">
													<span class="tituloSuave2"> (<%=rb.getString("sch.totalDoc") + " " + session.getAttribute("size") + " " + rb.getString("cbs.documents")%>)
													</span>
												</logic:present> &nbsp;</td>
										</tr>
									</tbody>
								</table>
							</td>
						</tr>
					</table>
					<table align=center border="0" width="100%" height="96%"
						cellspacing="0" cellpadding="0">
						<tr>
							<td nowrap valign="top">
								<!--jairo-->
								<table align="center" border="0" cellspacing="1" cellpadding="2"
									width="100%" height="100%">
									<tr>

										<%--DARWINUZCATEGUI T  TITULOS DE LAS COLUMNAS--%>
										<td id="COL_A" class="td_orange_l_b barraBlue" width="10%"
											nowrap>Aviso<a href="javascript:ordenar('aviso');"><img
												border=0
												src="img/<%=ToolsHTML.orden(session,"aviso")?"asc":"desc"%>.gif" /></a>
										</td>
										<td id="COL_B" class="td_orange_l_b barraBlue" width="5%"
											height="19" nowrap>Código<a
											href="javascript:ordenar('codigo');"><img border=0
												src="img/<%=ToolsHTML.orden(session,"codigo")?"asc":"desc"%>.gif" /></a>
										</td>
										<td id="COL_C" class="td_orange_l_b barraBlue" width="8%"
											nowrap>Local<a href="javascript:ordenar('local');"><img
												border=0
												src="img/<%=ToolsHTML.orden(session,"local")?"asc":"desc"%>.gif" /></a>
										</td>
										<td id="COL_D" class="td_orange_l_b barraBlue" width="13%"
											nowrap>Nombre<a href="javascript:ordenar('nombre');"><img
												border=0
												src="img/<%=ToolsHTML.orden(session,"nombre")?"asc":"desc"%>.gif" /></a>
										</td>
										<td id="COL_E" class="td_orange_l_b barraBlue" nowrap>Emision<a
											href="javascript:ordenar('emision');"><img border=0
												src="img/<%=ToolsHTML.orden(session,"emision")?"asc":"desc"%>.gif" /></a>
										</td>
										<td id="COL_F" class="td_orange_l_b barraBlue" nowrap>
											Vencimiento</td>
										<td id="COL_G" class="td_orange_l_b barraBlue" nowrap>
											Periodo</td>
										<td id="COL_H" class="td_orange_l_b barraBlue" width="10%"
											nowrap>Monto Bs</td>

										<td id="COL_I" class="td_orange_l_b barraBlue" width="10%"
											nowrap>Monto $</td>
										<td id="COL_J" class="td_orange_l_b barraBlue" nowrap>Tasa</td>
										<td id="COL_K" class="td_orange_l_b barraBlue" nowrap
											style="text-align: center">Alicuota</td>
										<%--  ydavila Ticket 001-00-003265  --%>
									</tr>
									<!--</table>-->
									<!--</td>-->
									<!--</tr>-->
									<!--</form>-->
									<logic:notPresent name="recibo">
										<tr>
											<td colspan="9" valign="top" height="100%">
												<table class="clsTableTitle" width="100%" cellSpacing="0"
													cellPadding="0" align=center border="0">
													<tbody>
														<tr>
															<td class="td_title_bc" height="19">
																<%if(session.getAttribute("moreP")==null){%> <%=rb.getString("pb.found")%>
																<%} else {%> <%=rb.getString("pb.notFound")%> <%}%>
															</td>
														</tr>
													</tbody>
												</table>
											</td>
										</tr>
									</logic:notPresent>

									<logic:present name="recibo" scope="session">
										<% int pintada=0;
			                    	   int cont=0; 
			                    	   int expire =0 ;
			                    	   int indice=-1;%>
										<% int contImg=0; %>
										<!-- primera linea -->
										<!-- paginacion ini -->
										<logic:iterate id="bean" name="recibo" indexId="ind"
											offset="<%=Pagingbean.getDesde()%>"
											length="<%=Pagingbean.getCuantos()%>"
											type="com.gestionEnvio.custon.dostorres.forms.BaseReciboForm"
											scope="session">

											<%pintada++;%>
											<!-- paginacion fin -->
											<tr class='fondo_<%=(cont==0?cont++:cont--)%>'>
												<%--   aqui voy impimir los dato de los recibos        --%>
												<td id="LIN_A<%=indice%>" class="td_gris_l" nowrap><%=bean.getNumero() %>

												</td>

												<td id="LIN_B<%=++indice%>" class="td_gris_l" nowrap><%=bean.getCodcli().trim()%>
												</td>
												<td id="LIN_C<%=++indice%>" class="td_gris_l" nowrap><%=bean.getNumlocal().trim()%>
												</td>

												<td id="LIN_D<%=++indice%>" class="td_gris_l" nowrap><%=bean.getNomcli().trim()%>
												</td>

												<td id="LIN_E<%=++indice%>" class="td_gris_l" nowrap
													style="text-align: center"><%=ToolsHTML.formatDateShow(bean.getDateEmitida(),false) %>
												</td>

												<td id="LIN_F<%=indice%>" class="td_gris_l" nowrap
													style="text-align: center"><%=ToolsHTML.formatDateShow(bean.getDateVence(),false) %>
												</td>


												<td id="LIN_H<%=indice%>" class="td_gris_l" nowrap
													style="text-align: center"><%=bean.getMes()%>-<%=bean.getAnio()%></td>

												<td id="LIN_I<%=indice%>" class="td_gris_l" nowrap
													style="text-align: right"><%=bean.getTotal()%></td>
												<td id="LIN_J<%=indice%>" class="td_gris_l" nowrap
													style="text-align: right"><%=bean.getTotal_mm()%></td>
												<td id="LIN_K<%=indice%>" class="td_gris_l" nowrap
													style="text-align: center"><%=bean.getTasa()%></td>
												<td id="LIN_L<%=indice%>" class="td_gris_l" nowrap
													style="text-align: center"><%=bean.getAlicuota()%></td>


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
<script language="javascript">
/*
function showDocumentPublishImp(idDoc,idVersion,nameFile,imprimir,idLogoImp) {
    var hWnd = null;
    var nameFile = escape(nameFile);
    var toPrint = escape(nameFile);
    if (imprimir == '0') {
        toPrint = "protegido.doc";
    }
    abrirVentanaSinScroll("loadDataDoc.do?nameFile=" + nameFile + "&idDocument=" + idDoc + "&idVersion=" + idVersion + "&imprimir=" + imprimir + "&nameFileToPrint="+toPrint+"&idLogoImp="+idLogoImp,800,600);
}
*/
<%-- ydavila Ticket 001-00-003265--%>   
function showCommentImage(idImg) {
    var hWnd = null;
    var nameFile = escape(nameFile);
    var toPrint = escape(nameFile);
    abrirVentanaSinScroll("&idLogoImp="+idImp,800,600);
}



inicializar();
more('<%=session.getAttribute("moreP")!=null && session.getAttribute("moreP").equals("true")?"":"none"%>');

</script>
