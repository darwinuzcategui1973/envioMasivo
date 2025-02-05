<!--/**
 * Title: published.jsp <br>
 * Copyright: (c) 2004 Desige Servicios de Computación<br/>
 *
 * @author Ing. Nelson Crespo   (NC)
 * @author Ing. Simón Rodriguéz (SR)
 * @version WebDocuments v1.0
 * <br>
 *     Changes:<br>
 * <ul>
 *      <li> 20-04-2006 (SR)  Cambios en lista maestra</li>
 *      <li> 25/04/2006 (SR) Se sustituyo /WebDocuments/.. por ./..</li>
 *      <li>20/07/2005  *Se llama desde published.jsp la funcion showDocumentFirmantes (SR)</li>
 * <ul>
 */-->
<%@ page import="java.util.ResourceBundle,
				 com.desige.webDocuments.files.forms.DocumentForm,
				 java.util.ArrayList,
				 java.util.Collection,
                 com.desige.webDocuments.utils.ToolsHTML,
                 com.desige.webDocuments.persistent.managers.HandlerDocuments,
                 com.desige.webDocuments.utils.beans.Users,
                 com.desige.webDocuments.utils.beans.SuperActionForm,
                 com.desige.webDocuments.utils.beans.PaginPage,
                 com.desige.webDocuments.persistent.managers.HandlerBD,
                 com.desige.webDocuments.persistent.managers.HandlerNorms,
				 com.desige.webDocuments.document.forms.BaseDocumentForm,
                 com.desige.webDocuments.utils.Constants"%>


<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<%@ page language="java" %>
<%

	//paginacion ini
	/* estos import son necesarios para paginar
	             com.desige.webDocuments.utils.beans.Users,
                 com.desige.webDocuments.utils.beans.SuperActionForm,
                 com.desige.webDocuments.utils.beans.PaginPage,
	*/
	Collection norms = null;
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
	
	boolean isAttached = ToolsHTML.getAttachedField()!=null && !ToolsHTML.getAttachedField().equals("");
	boolean isAttachedField0 = ToolsHTML.getAttachedField()!=null && ToolsHTML.getAttachedField().equals("0");
	boolean isAttachedField1 = ToolsHTML.getAttachedField()!=null && ToolsHTML.getAttachedField().equals("1");
	
	if(session.getAttribute("doc")==null) {
		BaseDocumentForm doc = new BaseDocumentForm();
		request.setAttribute("doc",doc);
	}
	
	
    ResourceBundle rb = ToolsHTML.getBundle(request);
    String onLoad = ToolsHTML.getMensajePages(request,rb);
    Users usuario = (Users)session.getAttribute("user");
    String ok = (String)session.getAttribute("usuario");

	ArrayList conf = (ArrayList)session.getAttribute("confDocument");
	DocumentForm obj;

    String[] carpetas=null;
    if (onLoad==null){
        response.sendRedirect(rb.getString("href.logout"));
    }
    String keys = ToolsHTML.getAttribute(request,"keys")!=null?(String)ToolsHTML.getAttribute(request,"keys"):"";
    String TypesStatus = (String)ToolsHTML.getAttribute(request,"TypesStatus");
    if (ToolsHTML.isEmptyOrNull(TypesStatus)) {
        TypesStatus = "";
    }
    String typeDocument = (String)ToolsHTML.getAttribute(request,"typeDocument");
    if (ToolsHTML.isEmptyOrNull(typeDocument)) {
        typeDocument = "";
    }
    String cargo = (String)ToolsHTML.getAttribute(request,"cargo");
    if (ToolsHTML.isEmptyOrNull(cargo)) {
        cargo = "";
    }
    String owner = (String)ToolsHTML.getAttribute(request,"owner");
    if (ToolsHTML.isEmptyOrNull(owner)) {
        owner = "";
    }
    String normISO = (String)ToolsHTML.getAttribute(request,"normISO");
    if (ToolsHTML.isEmptyOrNull(normISO)) {
        normISO = "";
    }
    String docPublic = (String)ToolsHTML.getAttribute(request,"public");
    if (ToolsHTML.isEmptyOrNull(docPublic)) {
        docPublic = "";
    }
    String queryReporte=ToolsHTML.getAttribute(request,"queryReporte")!=null?(String)ToolsHTML.getAttribute(request,"queryReporte"):"";
//    String version = ToolsHTML.getAttribute(request,"version")!=null?(String)ToolsHTML.getAttribute(request,"version"):"";
    String propietario = ToolsHTML.getAttribute(request,"propietario")!=null?(String)ToolsHTML.getAttribute(request,"propietario"):"";
    String ordenVersion = request.getParameter("ordenVersion")!=null?"checked":"";
    String ordenNombre = request.getParameter("ordenNombre")!=null?"checked":"";
    String ordenTypeDocument = request.getParameter("ordenTypeDocument")!=null?"checked":"";
    String ordenNumber = request.getParameter("ordenNumber")!=null?"checked":"";
    String ordenPropietario = request.getParameter("ordenPropietario")!=null?"checked":"";
    String ordenApproved = request.getParameter("ordenApproved")!=null?"checked":"";
    String accion = request.getParameter("accion")!=null?request.getParameter("accion"):"";
%>
<html>
<head>
<title><%=rb.getString("principal.title")%></title>
<jsp:include page="meta.jsp" /> 
<link href="estilo/estilo.css" rel="stylesheet" type="text/css">

</style>

    <script language=javascript src="./estilo/funciones.js"></script>


    <script language=javascript src="./estilo/fechas.js"></script><%-- Luis Cisneros 23-02-07 --%>

    <script language="JavaScript">
    <!-- variable global -->
	var LISTA_ANEXO = new Array();
    
    setDimensionScreen();
    
    function getListaAnexo() {
    	return LISTA_ANEXO;
    }

    function seguridad() {
        alert('<%=rb.getString("sch.notDocsView")%>');
    }

    function searchItem(forma,action,type) {
        if (type==0) {
            forma.toSearch.value = "";
        }

        <%-- Luis Cisneros, 23-03-07 Validación del campo de busqueda por fecha de expiracion --%>
        if (forma.expiredFrom.value.length != 0 && forma.expiredTo.value.length != 0 ){
            if (getDate(forma.expiredFrom.value) > getDate(forma.expiredTo.value) ) {
                alert('<%=rb.getString("sch.dateFromGreaterThanTo")%>');                
                return false;
            }
        }

        //es necesario copiar en el hidden el valor, ya que como el campo visible es readonly y disabled no se envía al form.             
        forma.approvedFromHIDDEN.value = forma.approvedFrom.value;
        forma.approvedToHIDDEN.value = forma.approvedTo.value;

        forma.expiredFromHIDDEN.value = forma.expiredFrom.value;
        forma.expiredToHIDDEN.value = forma.expiredTo.value;

        <%--if (forma.expiredFrom.value.length != 0 || forma.expiredTo.value.length != 0 ){
            
            if (!validarFechaExpiracion(forma.expiredFrom.value, '<%=rb.getString("sch.badDateExpiredFrom")%>')){                
                return false;
            }

            if (!validarFechaExpiracion(forma.expiredFrom.value, '<%=rb.getString("sch.badDateExpiredTo")%>')){
                return false;
            }

            if (getDate(forma.expiredFrom.value) > getDate(forma.expiredTo.value) ) {
                alert('<%=rb.getString("sch.dateFromGreaterThanTo")%>');                
                return false;
            }
            //es necesario copiar en el hidden el valor, ya que como el campo visible es readonly y disabled no se envía al form.             
            forma.expiredFromHIDDEN.value = forma.expiredFrom.value;
            forma.expiredToHIDDEN.value = forma.expiredTo.value;

        } --%>
        <%-- fin 23-03-07 --%>
        
        if (forma.keys.value.length ==0 && forma.version.value.length ==0 &&
            forma.nombre.value.length==0 && forma.number.value.length ==0 &&
            forma.typeDocument.value == "0" && forma.propietario.value == "0" &&
            forma.nameRout.value.length == 0 && forma.expiredFrom.length==0) {
            if (!confirm('<%=rb.getString("sch.notParam")%>')) {
                return false;
            }
        }
        if (type==0) {
            forma.toSearch.value = "";
        }
        forma.action = action;
        forma.submit();
    }

    function reporteLista() {
        frm = document.search;
        //Tipo de Documento
        if (frm.nameTypeDoc && frm.typeDocument.options[frm.typeDocument.selectedIndex].value != "0") {
            frm.nameTypeDoc.value = frm.typeDocument.options[frm.typeDocument.selectedIndex].text;
        }
        //Propietario del Documento
        if (frm.nameOwner && frm.propietario.options[frm.propietario.selectedIndex].value != "0") {
            frm.nameOwner.value = frm.propietario.options[frm.propietario.selectedIndex].text;
        }
        frm.action = "CrearReporte.do";
        frm.submit();
    }

    function editField(pages,input,value,forma) {
//        var hWnd = null;
        hWnd = window.open(pages+"?input="+input+"&value="+value+"&nameForma="+forma.name,"Catalogo","width=600,height=450,resizable=yes,statubar=yes,scrollbars=yes,left=200,top=150");
        if ((document.window != null) && (!hWnd.opener)){
            hWnd.opener = document.window;
        }
    }

    function showCharge(userName,charge,nosecuencial) {
        window.open("showCharge.jsp?userName="+userName+"&nosecuencial="+nosecuencial+"&charge="+charge, "WebDocuments", "resizable=no,scrollbars=yes,statusbar=yes,width=300,height=200,left=410,top=250");
    }

    function ordenar(orderBy) {

        forma = document.search;
        forma.orderBy.value = orderBy;
        forma.action = "orderDocument.do";
        forma.pages.value = "published";

        //18/04/07
        //Luis Cisneros
        //es necesario copiar en el hidden el valor, ya que como el campo visible es readonly y disabled no se envía al form.
        forma.expiredFromHIDDEN.value = forma.expiredFrom.value;
        forma.expiredToHIDDEN.value = forma.expiredTo.value;

        forma.submit();
    }

    <%-- Luis Cisneros 23-03-07 --%>
    function getDateExpire(dateField,nameForm,dateValue,text){
        //window.open("getDate.jsp?field=" + dateField + "&nameForm="+nameForm+"&valueForm="+dateValue+"&text="+text,"","width=350,height=300,resizable=no,scrollbars=yes,left=300,top=250");
        window.open("getDate.jsp?field=" + dateField + "&nameForm="+nameForm+"&valueForm="+dateValue+"&text="+text,"","width=275,height=170,resizable=no,scrollbars=yes,left=460,top=250");
    }

    function validarFechaExpiracion(valor, msg) {
        if (!validarFecha(valor)) {
            alert(msg);
            return false;
        }
        return true;
    }

    function showDocument(idDoc,idVersion) {
    	var formaSelection = document.getElementById("Selection");
        formaSelection.idDocument.value = idDoc;
        formaSelection.action = "showDataDocument.do";
        formaSelection.submit();
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
    	if(valor==""){
			viewField(document.search.typeDocument.value);
		}
    }

	function ocultarImpresora(contImg){
		try {
			document.getElementById("imgImpresora"+contImg).style.display='none';
			document.getElementById("imgImpresoraTexto"+contImg).style.display='none';
		} catch(e) { // no importa el error si no esta la imagen 
		}
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
<table cellSpacing="1" cellPadding="0" align="center" border="0" width="100%"  height="100%" >


<!-- Formulario para Obtener el nodo Seleccionado por el usuario -->
	<form name="search" method="post" action="showPublished.do">
    <input type="hidden" name="moreP" value="<%=session.getAttribute("moreP")%>"/>
	<tr>
		<td width="<%=ToolsHTML.ANCHO_MENU%>px"  valign="top" align="left" >
			<table cellSpacing="0" cellPadding="2"  border="0" width="<%=ToolsHTML.ANCHO_MENU%>px"  height="100%" style="/*border-collapse:collapse;*/border-color:#ofofof;border:1px solid #efefef">
				<tr>
					<td height="30px" class="ppalBoton" onmouseover="this.className='ppalBoton ppalBoton2'" onmouseout="this.className='ppalBoton'">
						<table border="0" cellspacing="0" cellpadding="0">
							<tr>
								<td>
									<a id="opt" href="#" class="menutip enlace_imagen">
								    <img src="images/rightDark.gif" border="0" onClick="">
								    <span id="x" onmouseover="limitar('opt')">
								    	<iframe name="optionP" src="optionPublished.jsp" frameborder="0" style="height:<%=ToolsHTML.ANCHO_MENU%>;width:150;border: 1px solid #0cf"></iframe>
								    </span>
								    </a>
								</td>
								<td class="ppalTextBold" width="100%">
									&nbsp;<%=rb.getString("enl.docPublic")%>
								</td>
								<%if(usuario.getIdGroup()!=null && usuario.getIdGroup().equals(Constants.ID_GROUP_ADMIN)) {%>
								<td>
								    <span ><img src="icons/report_user.png" title="<%=rb.getString("btn.docUnRead")%>" onClick="javascript:abrirVentana('listDocUnRead.do',800,600);"></span>&nbsp;
								</td>
								<%}%>
								<td>
								    <span ><img src="icons/find.png" title="<%=rb.getString("btn.search")%>" onClick="javascript:searchItem(document.search,'showPublished.do',1);"></span>&nbsp;
								</td>
								<td>
								    <span ><img src="icons/page_excel.png" title="<%=rb.getString("lst.reporte")%>" onClick="javascript:reporteLista();"></span>&nbsp;
								</td>
								<td class="none">
									<table border="0" cellspacing="1" cellpadding="0">
										<tr>
											<td height="1px">
										    <div id="flechaArriba" style="display:none"><img src="images/up3.gif"></div>
										    <div id="flechaAbajo" style="display:none"><img src="images/down3.gif"></div>
										    </td>
										</tr>
									</table>
								</td>
								<td>
								    &nbsp;<span id="signoMenos" style="display:none"><img src="images/replegar.gif" title="<%=rb.getString("seeLess")%>" onClick="javascript:more('none');"></span>&nbsp;
								</td>
							</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td height="99%" bgcolor="white" valign="top" background="menu-images/backgd.jpg">
						<div id="listaOpcion"  style="height:100%" class="scrollEnabled">
						<table class="ppalText" style="cursor:pointer;" cellspacing="0" border="0">
							<tr>
								<td>
									<%=rb.getString("sch.keys")%></br>
									<input type="text" name="keys" style="width:180px;height: 19px" value="<%=request.getAttribute("keys")!=null?request.getAttribute("keys"):""%>"/>
								</td>
							</tr>
							<tr>
								<td>
				                    <%=rb.getString("sch.name")%><br/>
				                    <input type="text" name="nombre" style="width:180px;height: 19px" value="<%=request.getAttribute("nombre")!=null?request.getAttribute("nombre"):""%>"/>
								</td>
							</tr>
							<tr>
								<td>
	                                <%=rb.getString("sch.prefix")%><br/>
	                                <input type="text" name="prefix" style="width:180px;height: 19px" value="<%=request.getAttribute("prefix")!=null?request.getAttribute("prefix"):""%>"/>
								</td>
							</tr>
							<tr>
								<td>
				                    <%=rb.getString("sch.number")%><br/>
				                    <input type="text" name="number" style="width:180px;height: 19px" value="<%=request.getAttribute("number")!=null?request.getAttribute("number"):""%>"/>
								</td>
							</tr>
							<tr>
                                <td>
                                    <%=rb.getString("sch.lote")%><br/>
                                    <input type="text" name="lote" style="width:180px;height: 19px" value="<%=request.getAttribute("lote")!=null?request.getAttribute("lote"):""%>"/>
                                </td>
                            </tr>
							<tr>
								<td>
					                <%=rb.getString("sch.expiresFrom")%>
		                            <table cellpadding="0" cellspacing="0" border="0" style="margin:0px;">
		                            <tr>
		                            	<td>
							                <input type="hidden" name="expiredFromHIDDEN" value=""></input>
							                <input type="text" name="expiredFrom" readonly disabled maxlength="10" style="width:140px;height: 19px" value="<%=request.getAttribute("expiredFromHIDDEN") == null ?"":request.getAttribute("expiredFromHIDDEN")%>" />
		                            	</td>
		                            	<td>
		                            		<a href="#" onclick='getDateExpire("expiredFrom",document.search.name,document.search.expiredFrom.value,"sch.from")' ><img src="images/calendario2.gif" border="1" ></a>
		                            	</td>
		                            	<td>
		                            		<a href='#' onclick='document.search.expiredFromHIDDEN.value=""; document.search.expiredFrom.value="";' value='<%=rb.getString("btn.clear")%>' ><img src="images/eraser.gif" border="0" alt='<%=rb.getString("btn.clear")%>' text='<%=rb.getString("btn.clear")%>'></a>
		                            	</td>
		                            </tr>
		                            </table>
								</td>
							</tr>
							<tr>
								<td>
		                            <%=rb.getString("sch.expiresTo")%>
		                            <table cellpadding="0" cellspacing="0" border="0" style="margin:0px;">
		                            <tr>
		                            	<td>
				                            <input type="hidden" name="expiredToHIDDEN" value=""></input>
				                            <input type="text" name="expiredTo" readonly disabled maxlength="10" style="width:140px;height: 19px"  value="<%=request.getAttribute("expiredToHIDDEN") == null ?"":request.getAttribute("expiredToHIDDEN")%>" />
		                            	</td>
		                            	<td>
		                            		<a href="#" onclick='getDateExpire("expiredTo",document.search.name,document.search.expiredTo.value,"sch.to")' ><img src="images/calendario2.gif" border="1" ></a>
		                            	</td>
		                            	<td>
		                            		<a href='#' onclick='document.search.expiredToHIDDEN.value=""; document.search.expiredTo.value="";' value='<%=rb.getString("btn.clear")%>' ><img src="images/eraser.gif" border="0" alt='<%=rb.getString("btn.clear")%>' text='<%=rb.getString("btn.clear")%>'></a>
		                            	</td>
		                            </tr>
		                            </table>
								</td>
							</tr>
							<tr>
								<td>
				                    <%=rb.getString("sch.tipo")%><br/>
				                    <logic:present name="typesDocuments" scope="session">
				                        <select name="typeDocument" style="width:180px; height: 119px" styleClass="classText" onchange="viewField(this.value)">
				                            <option value="0">&nbsp;</option>
				                            <logic:iterate id="bean" name="typesDocuments" scope="session" type="com.desige.webDocuments.utils.beans.Search">
				                                <logic:equal value="<%=typeDocument%>" name="bean" property="id">
				                                    <option value="<%=bean.getId()%>" selected><%=bean.getDescript()%></option>
				                                </logic:equal>
				                                <logic:notEqual value="<%=typeDocument%>" name="bean" property="id">
				                                    <logic:notEqual value="<%=HandlerDocuments.TypeDocumentsImpresion%>" name="bean" property="id">
				                                        <option value="<%=bean.getId()%>"><%=bean.getDescript()%></option>
				                                    </logic:notEqual>
				                                </logic:notEqual>
				                            </logic:iterate>
				                        </select>
				                    </logic:present>
				                    <logic:notPresent name="typesDocuments" scope="session">
				                        <%=rb.getString("E0006")%>
				                    </logic:notPresent>
				                    <input type="hidden" name="nameTypeDoc" value=""/>
								</td>
							</tr>
							<tr id="mas">
								<td align="right">
									<a herf="#" onclick="more('')"><b><%=rb.getString("seeMore")%>...</b></a>
								</td>
							</tr>
							<tr id="mas_1" style="display:none;">
								<td>
									<%=rb.getString("sch.owner")%><br/>
					                <select name="propietario" size="1" style="width:180px; height: 119px" styleClass="classText">
					                    <logic:present name="allUsers" scope="session">
					                        <option value="0">&nbsp;</option>
					                        <logic:iterate id="bean" name="allUsers" scope="session" type="com.desige.webDocuments.utils.beans.Search">
					                            <logic:equal value="<%=propietario%>" property="id" name="bean" >
					                                <option value="<%=bean.getId()%>" selected="true"><%=bean.getDescript()%></option>
					                            </logic:equal>
					                            <logic:notEqual value="<%=propietario%>" property="id" name="bean" >
					                                <logic:notEqual value="<%=propietario%>" property="id" name="bean" >
					                                    <option value="<%=bean.getId()%>"><%=bean.getDescript()%></option>
					                                </logic:notEqual>
					                            </logic:notEqual>
					                        </logic:iterate>
					                    </logic:present>
					                </select>
					                <logic:notPresent name="allUsers" scope="session">
					                    <%=rb.getString("E0006")%>
					                </logic:notPresent>
					                <input type="hidden" name="nameOwner" value=""/>
								</td>
							</tr>
							<tr id="mas_2" style="display:none">
								<td>
									<%=rb.getString("sch.version")%><br/>
									<input type="text" name="version" style="width:180px;height: 19px" value="<%=request.getAttribute("version")!=null?request.getAttribute("version"):""%>"/>								</td>
							</tr>
							<tr id="mas_3" style="display:none">
								<td>
					                <%=rb.getString("sch.arbol")%>
		                            <table cellpadding="0" cellspacing="0" border="0">
		                            <tr>
		                            	<td>
							                <input type="text" name="nameRout"  style="width:155px;height: 19px" value="<%=request.getAttribute("nameRout")!=null?request.getAttribute("nameRout"):""%>"/>
							                <input type="hidden" name="idRout" value="<%=request.getAttribute("idRout")!=null?request.getAttribute("idRout"):""%>"/>
		                            	</td>
		                            	<td>
							                &nbsp;<input type="button" class="boton" value=" ... " onClick="javascript:editField('loadAllStruct.do?toSelectValue=true','estado','nameState',this.form);" style="width:20px;height:19px" />
		                            	</td>
		                            </tr>
		                            </table>
								</td>
							</tr>
							<tr id="mas_4" style="display:none">
								<td>
					                <%=rb.getString("sch.publicFrom")%>
		                            <table cellpadding="0" cellspacing="0" border="0" style="margin:0px;">
		                            <tr>
		                            	<td>
							                <input type="hidden" name="approvedFromHIDDEN" value=""/>
							                <input type="text" name="approvedFrom" readonly disabled maxlength="10" style="width:140px;height: 19px" value="<%=request.getAttribute("approvedFromHIDDEN") == null ?"":request.getAttribute("approvedFromHIDDEN")%>" />
		                            	</td>
		                            	<td>
		                            		<a href="#" onclick='getDateExpire("approvedFrom",document.search.name,document.search.approvedFrom.value,"sch.from")' ><img src="images/calendario2.gif" border="1" ></a>
		                            	</td>
		                            	<td>
		                            		<a href='#' onclick='document.search.approvedFromHIDDEN.value=""; document.search.approvedFrom.value="";' value='<%=rb.getString("btn.clear")%>' ><img src="images/eraser.gif" border="0" alt='<%=rb.getString("btn.clear")%>' text='<%=rb.getString("btn.clear")%>'></a>
		                            	</td>
		                            </tr>
		                            </table>
								</td>
							</tr>
							<tr id="mas_5" style="display:none">
								<td>
		                            <%=rb.getString("sch.expiresTo")%>
		                            <table cellpadding="0" cellspacing="0" border="0" style="margin:0px;">
		                            <tr>
		                            	<td>
				                            <input type="hidden" name="approvedToHIDDEN" value=""></input>
				                            <input type="text" name="approvedTo" readonly disabled maxlength="10" style="width:140px;height: 19px"  value="<%=request.getAttribute("approvedToHIDDEN") == null ?"":request.getAttribute("approvedToHIDDEN")%>" />
		                            	</td>
		                            	<td>
		                            		<a href="#" onclick='getDateExpire("approvedTo",document.search.name,document.search.approvedTo.value,"sch.to")' ><img src="images/calendario2.gif" border="1" ></a>
		                            	</td>
		                            	<td>
		                            		<a href='#' onclick='document.search.approvedToHIDDEN.value=""; document.search.approvedTo.value="";' value='<%=rb.getString("btn.clear")%>' ><img src="images/eraser.gif" border="0" alt='<%=rb.getString("btn.clear")%>' text='<%=rb.getString("btn.clear")%>'></a>
		                            	</td>
		                            </tr>
		                            </table>
								</td>
							</tr>
							
							<tr id="mas_6" style="display:none;">
                                <td>
                                     <%=rb.getString("sch.normISO")%><br/>
                                     <%
                                         norms = HandlerNorms.getAllNorms();
                                         if (norms!=null&&norms.size() > 0) {
                                             pageContext.setAttribute("norms",norms);
                                     %>
                                         <select name="normISO" size="1" style="width:180px; height: 19px" styleClass="classText" >
                                             <option value="0">&nbsp;</option>
                                             <logic:present name="norms">
                                                 <logic:iterate id="bean" name="norms" type="com.desige.webDocuments.utils.beans.Search">
                                                     <logic:equal value="<%=normISO%>" property="id" name="bean" >
                                                            <option value="<%=bean.getId()%>" selected="true"><%=bean.getDescript()%></option> 
                                                     </logic:equal>
                                                           <logic:notEqual value="<%=normISO%>" property="id" name="bean" > -
                                                           <option value="<%=bean.getId()%>"><%=bean.getDescript()%></option> 
                                                     </logic:notEqual>
                                                 </logic:iterate>
                                             </logic:present>
                                         </select>
                                     <%
                                         }
                                     %>
                                </td>
                            </tr>
	
		<!-- INI CAMPOS ADICIONALES -->
		<!-- FIN CAMPOS ADICIONALES -->
                    		<%-- 
                    		for(int k=0;k<conf.size();k++){%>
							    <%obj=(DocumentForm)conf.get(k);
								if(obj.getCriterio()==1) {%>
								<tr id="mas_<%=k+8%>" style="display:none">
									<td>
									    <%=obj.getEtiqueta(usuario.getLanguage())%>
						                <br/>
						                <input type="text" 
						                value="<%=request.getAttribute(obj.getId())!=null?request.getAttribute(obj.getId()):""%>"
						                name="<%=obj.getId()%>" style="width:180px;height: 19px" value="<%=request.getAttribute(obj.getId())!=null?request.getAttribute(obj.getId()):""%>" <%=(obj.getTipo()==DocumentForm.TYPE_DATE?Constants.VALID_DATE_JAVASCRIPT:"")%> />
									</td>
								</tr>
                            <%	}
                            }--%>
						</table>
						</div>
					        <input type="hidden" name="accion" value="1"/>
					        <input type="hidden" name="orderBy" value=""/>
					        <input type="hidden" name="pages" value=""/>
						</form>
						<form name="Selection" id="Selection" action="loadStructMain.do">
						    <input type="hidden" name="idDocument" value=""/>
						    <input type="hidden" name="idVersion" value=""/>
						    <input type="hidden" name="showStruct" value="true"/>
						    <input type="hidden" name="from" value="published.jsp"/>
						</form>
					</td>
				</tr>
						<%if (usuario!=null) {
                            out.println(ToolsHTML.printMenu(rb,usuario.getUser(),usuario.getIdGroup(),true,request));
                        }%>
			</table>
		</td>
		<td align="center" valign="top">
			        <table align=center border="0" width="100%" cellSpacing="0" cellPadding="0" >
			            <tr>
			                <td colspan="4">
			                    <table class="clsTableTitle" width="100%" cellSpacing="0" cellPadding="0" align=center border="0">
			                        <tbody>
			                            <tr>
			                                <td class="td_title_bc" height="19" width="90%">
			                                	<!-- paginador -->
							                    <logic:present name="published" scope="session">
							                        <!-- paginacion ini -->
							                        <%
							
							                            String from = request.getParameter("from")!=null?request.getParameter("from"):"";
							                            String size = session.getAttribute("size").toString();
							                            if(size != null){
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
							                        <form name="formPagingPage" method="post" action="published.jsp">
							                          <input type="hidden" name="from"  value="">
							                          <input type="hidden" name="size"  value="<%=size%>">
							                          <input type="hidden" name="cmd"  value="<%=SuperActionForm.cmdLoad%>">
							                        </form>
							                        <!-- paginacion fin -->
							                                             
		                                            <!-- paginacion ini -->
		                                            <table class="paginadorNuevo" align="left">
		                                                <tr>
		                                                    <td align="center">&nbsp;<img src="images/inicio2.gif"
		                                                       class="GraphicButton" title="<%=rb.getString("pagin.First")%>"
		                                                       onclick="paging_OnClick(0)">&nbsp;
		                                                    </td>
		                                                    <td align="center"> <img src="images/left2.gif"
		                                                       class="GraphicButton" title="<%=rb.getString("pagin.Previous")%>"
		                                                       onclick="paging_OnClick(<%=(Integer.parseInt(Pagingbean.getPages())-1)%>)">
		                                                    </td>
		                                                    <td align="center" width="120px">
		                                                            <font size="1" color="#000000">
		                                                                <%=rb.getString("pagin.title")+ " "%>
		                                                                <%=Integer.parseInt(Pagingbean.getPages())+1%>
		                                                                <%=rb.getString("pagin.of")%>
		                                                                <%=Pagingbean.getNumPages()%>
		                                                            </font>
		                                                    </td>
		                                                    <td align="center"> <img src="images/right2.gif"
		                                                       class="GraphicButton" title="<%=rb.getString("pagin.next")%>"
		                                                       onclick="paging_OnClick(<%=(Integer.parseInt(Pagingbean.getPages())+1)%>)">&nbsp;
		                                                    </td>
		                                                    <td align="center"> <img src="images/fin2.gif"
		                                                       class="GraphicButton" title="<%=rb.getString("pagin.Last")%>"
		                                                       onclick="paging_OnClick(<%=Pagingbean.getNumPages()%>)">
		                                                    </td>
		                                                </tr>
		                                            </table>
												    <!-- paginacion fin -->
												    
							                    </logic:present>
			                                </td>
			                                <td class="td_title_bc" height="19"  width="90%">
			                                    <%=rb.getString("pb.title")%>
			                                </td>
			                                <td class="td_title_bc" height="19"  width="90%">
										        <logic:present name="size" scope="session">
					                            <span class="tituloSuave2">
					                                    (<%=rb.getString("sch.totalDoc") + " " + session.getAttribute("size") + " " + rb.getString("cbs.documents")%>)
					                            </span>
										        </logic:present>
										        &nbsp;
			                                </td>
			                            </tr>
			                        </tbody>
			                    </table>
			                </td>
			            </tr>
			    </table>
			    <table align=center border="0" width="100%" height="96%" cellspacing="0" cellpadding="0">
			        <tr>
			            <td nowrap valign="top"><!--jairo-->
			                <table align="center" border="0" cellspacing="1" cellpadding="2" width="100%" height="100%">
			                    <tr>
			                        <td id="COL_A" class="td_orange_l_b barraBlue" width="5%" height="19" nowrap>
			                            <%=rb.getString("showDoc.Ver")%><a href="javascript:ordenar('ver');"><img border=0 src="img/<%=ToolsHTML.orden(session,"ver")?"asc":"desc"%>.gif"/></a>
			                        </td>
			                        <td id="COL_B" class="td_orange_l_b barraBlue" nowrap>
			                            <%=rb.getString("cbs.name")%><a href="javascript:ordenar('name');"><img border=0 src="img/<%=ToolsHTML.orden(session,"name")?"asc":"desc"%>.gif"/></a>
			                        </td>
			                        <td id="COL_C" class="td_orange_l_b barraBlue" nowrap>
			                            <%=rb.getString("cbs.ubicacion")%>
			                        </td>
			                        <td id="COL_D" class="td_orange_l_b barraBlue" width="13%" nowrap>
			                            <%=rb.getString("doc.type")%><a href="javascript:ordenar('type');"><img border=0 src="img/<%=ToolsHTML.orden(session,"type")?"asc":"desc"%>.gif"/></a>
			                        </td>
									<td id="COL_E" class="td_orange_l_b barraBlue" width="10%" nowrap>
			                            <%=rb.getString("doc.number")%><a href="javascript:ordenar('number');"><img border=0 src="img/<%=ToolsHTML.orden(session,"number")?"asc":"desc"%>.gif"/></a>
			                        </td>
			                        <td id="COL_F" class="td_orange_l_b barraBlue" width="10%" nowrap>
			                        	<span title="<%=rb.getString("doc.links")%>">
			                            	<%=rb.getString("doc.linksSmall")%>
			                            </span>
			                        </td>
			                        <td id="COL_G" class="td_orange_l_b barraBlue" width="18%" nowrap>
			                            <%=rb.getString("cbs.owner")%> <a href="javascript:ordenar('owner');"><img border=0 src="img/<%=ToolsHTML.orden(session,"owner")?"asc":"desc"%>.gif"/></a>
			                        </td>
			                        <td id="COL_H" class="td_orange_l_b barraBlue" width="8%" nowrap>
			                            <%=rb.getString("showDoc.approved")%><a href="javascript:ordenar('approved');"><img border=0 src="img/<%=ToolsHTML.orden(session,"approved")?"asc":"desc"%>.gif"/></a>
			                        </td>
			                        <td id="COL_I" class="td_orange_l_b barraBlue" width="10%" nowrap>
			                            <%=rb.getString("showDoc.Firmantes")%>
			                        </td>
		    						<td id="COL_J" class="td_orange_l_b barraBlue" width="15%" style="height:14px" nowrap>     
		    						Norma<a href="javascript:ordenar('normISO');"><img border=0 src="img/asc.gif"/></a>
		                        	</td>
			                        
			                        
			                    </tr>
			                    <!--</table>-->
			                <!--</td>-->
			            <!--</tr>-->
			        <!--</form>-->
			                    <logic:notPresent name="published" scope="session">
			                        <tr>
			                            <td colspan="9"  valign="top"  height="100%">
			                                <table class="clsTableTitle" width="100%" cellSpacing="0" cellPadding="0" align=center border="0">
			                                    <tbody>
			                                        <tr>
			                                            <td class="td_title_bc" height="19">
			                                            	<%if(session.getAttribute("moreP")==null){%>
										                    	<%=rb.getString("pb.found")%>
			                                            	<%} else {%>
										                    	<%=rb.getString("pb.notFound")%>
			                                            	<%}%>
			                                            </td>
			                                      </tr>
			                                    </tbody>
			                                </table>
			                            </td>
			                        </tr>
			                    </logic:notPresent>
			                    <logic:present name="published" scope="session">
			                    	<% int pintada=0;
			                    	   int cont=0; 
			                    	   int expire =0 ;
			                    	   int indice=-1;%>
			                    	<% int contImg=0; %>
			                    	<!-- primera linea -->
			                    	<!-- paginacion ini -->
			                        <logic:iterate id="bean" name="published" indexId="ind" offset="<%=Pagingbean.getDesde()%>" length="<%=Pagingbean.getCuantos()%>"
			                        	type="com.desige.webDocuments.document.forms.BaseDocumentForm"  scope="session">
			                        	<%pintada++;%>
			                        <!-- paginacion fin -->
			                        	<% contImg++; 
			                        	   expire = ToolsHTML.compareDatesWithNow(bean.getDateExpires());
			                        	%>
			                            <tr class='fondo_<%=(cont==0?cont++:cont--)%>'>
			                                <td id="LIN_A<%=++indice%>" class="td_gris_l" nowrap>
			                                    <%=bean.getMayorVer().trim()+"."+bean.getMinorVer().trim()%>
			                                    <!-- Permitir impresion a nivel de carpetas-->
			                                    <%if(ToolsHTML.showFlujo()){%>
			                                    <a href="javascript:showSolicitudImpresion('<%=bean.getIdDocument()%>',<%=bean.getNumVer()%>,'<%=bean.getIdperson()%>')" class="ahreMenu">
			                                        &nbsp;+
			                                    </a>
			                                    <%}%>
		                                        <%if(expire>=0){%>
		                                        <img src="icons/date_error.png" border="0" title="<%=rb.getString("wf.statuUserWF7")%>" alt="documento vencido">
		                                        <%}%>
			                                    <logic:notEqual name="bean" property="isflowusuario" value="-1">
			                                      <a href="javascript:showCharge('<%=bean.getIsflowusuario()%>',''+'<%=rb.getString("wf.pend")%>','<%=bean.getIsNotflowsecuencial()%>')" class="ahref_b">
			                                         <img src="icons/comment2.png" title="<%=rb.getString("wf.pend")%>" border="0">
			                                      </a>
			                                   </logic:notEqual>
			                                </td>
			                                <td id="LIN_B<%=indice%>" class="td_gris_l" nowrap>
				                                    <%--<a href="#" onclick="javascript:showDocumentPublishImp('<%=bean.getIdDocument()%>',<%=bean.getNumVer()%>,'<%=bean.getIdDocument()%>','1')<%= Constants.PRINTER_PDF && bean.getImprimir().equalsIgnoreCase("imprimir") ? ";document.getElementById('imgImpresora"+contImg+"').style.display='none';document.getElementById('imgImpresoraTexto"+contImg+"').style.display='none'" : "" %>" class="info<%=indice>=lineas-5?"up":""%>">--%>
				                                    <a href="javascript:showDocumentimprimir('<%=bean.getIdDocument()%>',<%=bean.getNumVer()%>,'<%=bean.getIdDocument()%>','1','',<%=Constants.PRINTER_PDF%>)" class="info<%=indice>=lineas-5?"up":""%>">
			                                            <font class="td_gris_c">
				                                            <logic:equal name="bean" value="imprimir" property="imprimir">
				                                                <img id="imgImpresora<%=contImg%>" src="icons/printer.png" border="0" style="position:absolute;top:-0.3em;left:0em;">
				                                                <font id="imgImpresoraTexto<%=contImg%>">
				                                                &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				                                                </font>
				                                            </logic:equal>
			                                            	<%=bean.getNameDocument().substring(0,nameWidth>bean.getNameDocument().length()?bean.getNameDocument().length():nameWidth)%>
			                                            </font>
			                                            <%=nameWidth>bean.getNameDocument().length()?"":"<b> . . .</b>"%>
			                                            <span id="jairo">
				                                            <b><%=bean.getNameDocument()%></b><br/><br/>

						                                    <%=ToolsHTML.estructuraWrap(bean.getRout(false))%>
			                                            </span>
			                                   </a>                                
			                                </td>
			                                <td id="LIN_C<%=indice%>" class="td_gris_l" nowrap>
							                    <logic:present name="showLink" scope="session">
								                    <logic:equal name="showLink" value="true">
					                                    <a href="javascript:showDocument('<%=bean.getIdDocument()%>',<%=bean.getNumVer()%>)" class="ahref_b">
					                                    <%=bean.getRout(false)%>
														</a>
								                    </logic:equal>
								                    <logic:notEqual name="showLink" value="true">
					                                    <%=bean.getRout(false)%>
								                    </logic:notEqual>
				                                </logic:present>
				                                <logic:notPresent name="showLink" scope="session">
				                                    <%=bean.getRout(false)%>
				                                </logic:notPresent>
			                                </td>
			                                <td id="LIN_D<%=indice%>" class="td_gris_l" nowrap>
			                                   	<%=bean.getDescriptTypeDoc()%>
			                                </td>
											<td id="LIN_E<%=indice%>" class="td_gris_l" nowrap>
			                                    <%=bean.getPrefix() + bean.getNumber()%>
			                                </td>
			                                <td id="LIN_F<%=indice%>" class="td_gris_l" nowrap style="text-align:center">
			                                    <a href="javascript:showDocRelations('loadAllDocumentsRelated.do','<%=rb.getString("doc.links")%>','<%=bean.getIdDocument()%>','genXLS',800,500);" class="ahref_b" target="_self">
			                                        <%=rb.getString("showDoc.show")%>
			                                    </a>
			                                    <%if(isAttached){%>
			                                    / 
			                                    <a class="ahref_b" href="#" onclick="javascript:showDocumentAttached(<%=item%>,'<%=bean.getIdDocument()%>',<%=bean.getNumVer()%>,'<%=bean.getIdDocument()%>','1',<%=contImg%>,'<%=(isAttachedField0?bean.getNumber():bean.getNameDocument())%>')" >
	                                            	Anexo
	                                            	<script>
	                                            		LISTA_ANEXO[<%=item++%>] = ['<%=bean.getIdDocument()%>',<%=bean.getNumVer()%>,'<%=bean.getIdDocument()%>','1',<%=contImg%>,'<%=(isAttachedField0?bean.getNumber():bean.getNameDocument())%>'];
	                                            	</script>
			                                   </a>              
			                                   <%}%>                  
			                                </td>
			                                <td id="LIN_G<%=indice%>" class="td_gris_l" nowrap>
			                                    <%=bean.getOwner()%>
			                                </td>
			                                <td id="LIN_H<%=indice%>" class="td_gris_l" align="center" nowrap>
			                                    <%=bean.getDateApproved()%>
			                                </td>
			                                <td id="LIN_I<%=indice%>" class="td_gris_l" nowrap align="center" >
			                                   <a href="javascript:showDocumentFirmantes('<%=bean.getIdDocument()%>',<%=bean.getNumVer()%>,'<%=rb.getString("cbs.name")%>')" class="ahref_b">
			                                     <%=rb.getString("showDoc.show")%>
			                                   </a>
			                                </td>
			                                
			                                <td id="LIN_J<%=indice%>" class="td_gris_l" unwrap>
				                                <a class="info" style="cursor:none" readonly>
				                                        <%=bean.getNormISODescript() %><br/> 
													<span id="jairo" style="cursor:none">
													  	<%=HandlerNorms.getDescriptNormIso(bean.getNormISO()) %> 
													</span>
		                                		</a>
		                                	</td>
			                                
			                                
			                            </tr>
			                        </logic:iterate>
			                        <%for(pintada++;pintada<=lineas;pintada++){%>
			                        	<tr>
			                        		<td colspan="8">&nbsp;</td>
			                        	</tr>
			                        <%}%>
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

function showDocumentPublishImp(idDoc,idVersion,nameFile,imprimir,idLogoImp) {
    var hWnd = null;
    var nameFile = escape(nameFile);
    var toPrint = escape(nameFile);
    if (imprimir == '0') {
        toPrint = "protegido.doc";
    }
    abrirVentanaSinScroll("loadDataDoc.do?nameFile=" + nameFile + "&idDocument=" + idDoc + "&idVersion=" + idVersion + "&imprimir=" + imprimir + "&nameFileToPrint="+toPrint+"&idLogoImp="+idLogoImp,800,600);
}

function visualizar(col){
	all="ABCDEFGHIJ";
	for(var x=0; x<all.length; x++){
		var c=all.substring(x,x+1);
		document.getElementById("COL_"+c).style.display=(col.indexOf(c)!=-1?"":"none");
		try{
			for(var n=0;n<<%=lineas%>;n++){
				document.getElementById("LIN_"+c+n).style.display=(col.indexOf(c)!=-1?"":"none");
			}
		}catch(e){
		}
	}
}


inicializar();
visualizar("<%=HandlerBD.getOptionPublished((int)usuarioActual.getIdPerson())%>");
more('<%=session.getAttribute("moreP")!=null && session.getAttribute("moreP").equals("true")?"":"none"%>');

</script>
 