<!-- /**
 * Title: searchDocument.jsp<br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * @author Ing. Nelson Crespo (NC)
 * @author Ing. Simon Rodríguez (SR)
 * @version WebDocuments v3.0
 * <br/>
 * Changes:<br/> 
 * <ul>
 *      <li> 25/05/2005 Se creo el parametro de busqueda TypesStatus,TypeStatuSession(SR)</li>
 *      <li> 01/06/2005 Se creo una session showCharge, para qaue validara por cargo
 *      <li> 19/04/2006 Add label to show count documents </li>
 *      <li> 27/04/2006 (NC) buggs en las búsquedas por cargos & Normas ISO corregido </li>
 *      <li> 30/05/2006 (NC) Cambios para Mostrar la Estructura
 </ul>
 */ -->
<%@ page import="java.util.ResourceBundle,
				 com.desige.webDocuments.files.forms.DocumentForm,
				 java.util.ArrayList,
                 com.desige.webDocuments.utils.ToolsHTML,
                 com.desige.webDocuments.persistent.managers.HandlerDBUser,
                 com.desige.webDocuments.persistent.managers.HandlerNorms,
	             com.desige.webDocuments.utils.beans.Users,
                 com.desige.webDocuments.utils.beans.SuperActionForm,
                 com.desige.webDocuments.utils.beans.PaginPage,
                 com.desige.webDocuments.utils.Constants,
                 com.desige.webDocuments.persistent.managers.HandlerBD,
                 com.desige.webDocuments.persistent.managers.HandlerNorms,
                 com.desige.webDocuments.persistent.managers.HandlerParameters,
				 com.desige.webDocuments.document.forms.BaseDocumentForm,
				 com.desige.webDocuments.persistent.managers.HandlerDocuments,
				 com.desige.webDocuments.utils.beans.Search,
                 java.util.Collection"%>
                 
                 
                 
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
	Users usuarioActual = session.getAttribute("user")!=null?(Users)session.getAttribute("user"):null;
	PaginPage Pagingbean = null;
	int lineas=Constants.LINEAS_POR_PAGINA;
	//paginacion fin
	int nameWidth=60;
	int rootWidth=100;
	String ruta = "";
	int contador = 0;

    //if(request.getAttribute("visible")==null || !String.valueOf(request.getAttribute("visible")).equals("true") ) {
    	//response.sendRedirect("searchDocument.do");
    //}

	Collection users = null;
	Collection norms = null;

    ResourceBundle rb = ToolsHTML.getBundle(request);
    String onLoad = ToolsHTML.getMensajePages(request,rb);
    Users usuario = (Users)session.getAttribute("user");
    String ok = (String)session.getAttribute("usuario");
    
	ArrayList conf = (ArrayList)session.getAttribute("confDocument");
	DocumentForm obj;
	
	if(session.getAttribute("doc")==null) {
		BaseDocumentForm doc = new BaseDocumentForm();
		request.setAttribute("doc",doc);
	}
    

    if (onLoad==null){
        response.sendRedirect(rb.getString("href.logout"));
    }
   // ToolsHTML.clearSession(session,"application.search");
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
    String ordenTipo=request.getParameter("ordenTipo")!=null?"checked":"";
    String ordenNombre=request.getParameter("ordenNombre")!=null?"checked":"";
    String ordenNumero=request.getParameter("ordenNumero")!=null?"checked":"";
    String ordenPropietario=request.getParameter("ordenPropietario")!=null?"checked":"";
    String ordenCreate=request.getParameter("ordenCreate")!=null?"checked":"";
    String ordenISO=request.getParameter("ordenISO")!=null?"checked":"";
%>
<html>
<head>
<title><%=rb.getString("principal.title")%></title>
<jsp:include page="meta.jsp" /> 
<link href="estilo/estilo.css" rel="stylesheet" type="text/css">
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
#caja4 {
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
</style>
<script type="text/javascript" src="./estilo/funciones.js"></script>
<script type="text/javascript"><!--
document.onkeypress=function(e){
var esIE=(document.all);
var esNS=(document.layers);
tecla=(esIE) ? event.keyCode : e.which;
if(tecla==13){
    //alert("Ud. ha presionado la tecla Enter"); return false;
	searchDocument(document.search);
  }
};
//--></script>
<script type="text/javascript">

	var isClose=true;

    function seguridad() {
        alert('<%=rb.getString("sch.notDocsView")%>');
    }

    function editField(pages,input,value,forma) {
        var hWnd = null;
        hWnd = window.open(pages+"?input="+input+"&value="+value+"&nameForma="+forma.name,"Catalogo","width=600,height=450,resizable=yes,statubar=yes,scrollbars=yes,left=200,top=150");
        if ((document.window != null) && (!hWnd.opener)){
            hWnd.opener = document.window;
        }
    }

    function showDocument(idDoc,idVersion) {
    	var formaSelection = document.getElementById("Selection");
	    if((typeof window.opener)=='undefined' || !(window.opener?true:false) || (typeof window.opener.addDocument)=='undefined' ){
	        formaSelection.idDocument.value = idDoc;
	        formaSelection.action = "showDataDocument.do";
	        formaSelection.submit();
		} else {
			window.opener.addDocument(idDoc);
			colorear(idDoc);		
		}
    }
    function colorear(idDoc){
    	if(window.opener){
		    var lista = window.opener.getListDocument();
			obj=document.getElementById("row_"+idDoc);
			if(obj){
				obj.className=obj.className.replace(/fondo_2/g,"");
			}
			for(var m=0;m<lista.length;m++){
				obj=document.getElementById("row_"+lista[m]);
				if(obj){
					if(obj.className.indexOf("fondo_2")==-1){
						obj.className=obj.className+" fondo_2";
					}
				}
			}
		}
    }
    

    function checkField(field) {
        if (field){
            if (field.value.length > 0) {
                return true;
            }
        }
        return false;
    }

    function searchDocument(forma) {
        if (forma.prefix) {
            noParam = true;
            if (forma.owner) {
                noParam = forma.owner.value == "0";
            } else {
                noParam = forma.cargo.value == "0";
            }
            noNormISO = true;
            if (forma.normISO) {
                noNormISO = forma.normISO.value == "0";
            }
            
            if (forma.prefix.value.length == 0 && noParam && forma.cargo.value == "0" &&
                noNormISO && forma.public.value == "2" &&
                forma.keys.value.length == 0 && forma.nombre.value.length == 0 &&
                forma.number.value.length == 0 && forma.typeDocument.value == "0" &&
                forma.TypesStatus.value == "0" && forma.nameRout.value.length == 0) {
            	if (!confirm('<%=rb.getString("sch.notParam")%>')) {
                    return false;
                }
                <%-- ydavila se coloca en comentario para habilitar mensaje 
                return false;--%>
            }
        } else {
        	alert("else forma.prefix");
            if (forma.keys.value.length ==0 &&
                forma.nombre.value.length==0 && forma.number.value.length ==0 &&
                forma.typeDocument.value == "0" && forma.TypesStatus.value == "0" &&
                forma.nameRout.value.length == 0) {
                if (!confirm('<%=rb.getString("sch.notParam")%>')) {
                    return false;
                }
            }
        }
        
        if (checkField(forma.nombre)||checkField(forma.number)||checkField(forma.typeDocument)||checkField(forma.TypesStatus)) {
        	if((typeof window.opener)=='undefined'){
            	forma.target = "info";
            }
            forma.action = "searchDocument.do";
            isClose=false;
            
            forma.submit();
        } else {
            alert('<%=rb.getString("sch.notDesc")%>');
        }
    }

    function showStatus(ind){
        if (ind==0) {
            self.status = '';
        }
    }

    function refresh(forma,expand) {
        forma.action = "searchDocument.jsp?expand=" + expand;
        forma.submit();
    }
    function showCharge(userName,charge,nosecuencial) {
        window.open("showCharge.jsp?userName="+userName+"&nosecuencial="+nosecuencial+"&charge="+charge, "WebDocuments", "resizable=no,scrollbars=yes,statusbar=yes,width=300,height=200,left=410,top=250");
    }

    function ordenar(orderBy) {
        forma = document.search;
        forma.orderBy.value = orderBy;
        forma.action = "orderDocument.do";
        forma.pages.value = "searchDocs";
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
        frm.action = "CrearReporteSearch.do";
        frm.submit();
    }
    
    function mas(v) {
    	more(v);
    }
    
    function more(valor) {
	    valor = ( (typeof valor)=='undefined'?"":valor );
    	document.search.more.value=(valor==''?true:false);
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


	
	function administrarCancelar(){
		document.getElementById("caja").style.display="none";
		document.getElementById("caja2").style.display="none";
		document.getElementById("caja3").style.display="none";
	}

	function publicarCancelar(){
		document.getElementById("caja").style.display="none";
		document.getElementById("caja4").style.display="none";
		document.getElementById("caja3").style.display="none";
	}
	
	function probar() {
		alert("probar");
	}

	function administrar(){

		medidas = getWindowXY();
		ancho=medidas[0]-400;
		largo=medidas[1]-400;

    	var frm = document.getElementById("frmUbicacion");
    	frm.style.width='';
    	frm.style.height='';
    	
    	// recargamos la pagina
    	window.open("searchDocumentUbicacion.jsp?tipoDocumento="+document.search.typeDocument.value,"frmUbicacion");
    	
    	var obj = document.getElementById("caja");
    	obj.style.display="";
    	obj.style.width=medidas[0]+"px";
    	obj.style.height=medidas[1]+"px";
    	
    	var obj2 = document.getElementById("caja2");
    	obj2.style.display="";
    	obj2.style.left=((medidas[0]-ancho)/2)+"px";
    	obj2.style.top=((medidas[1]-largo)/2)+"px";
    	obj2.style.width=ancho+"px";
    	obj2.style.height=largo+"px";
    	obj2.className="bodyInternas";
    	//obj2.style.backgroundColor="gray";
    	//obj2.style.backgroundImage.src=url("img/fondoGris2.jpg");
    	
    	var marco = document.getElementById("caja3");
    	margen=10;
    	marco.style.display="";
    	marco.style.left=((medidas[0]-ancho)/2)-margen+"px";
    	marco.style.top=((medidas[1]-largo)/2)-margen+"px";
    	marco.style.width=ancho+(margen*2)+"px";
    	marco.style.height=largo+(margen*2)+"px";
    	
    	
    	var obj3 = document.getElementById("frmUbicacion");
    	obj3.style.left="0px";
    	obj3.style.top="0px";
    	//obj3.width=obj2.style.width;
    	obj3.width="100%";
    	obj3.height="100%";
    }
    
	function publicarEraser(){

		medidas = getWindowXY();
		ancho=medidas[0]-400;
		largo=medidas[1]-400;

    	var frm = document.getElementById("frmPublicEraser");
    	frm.style.width='';
    	frm.style.height='';
    	
    	var obj = document.getElementById("caja");
    	obj.style.display="";
    	obj.style.width=medidas[0]+"px";
    	obj.style.height=medidas[1]+"px";
    	
    	var obj2 = document.getElementById("caja4");
    	obj2.style.display="";
    	obj2.style.left=((medidas[0]-ancho)/2)+"px";
    	obj2.style.top=((medidas[1]-largo)/2)+"px";
    	obj2.style.width=ancho+"px";
    	obj2.style.height=largo+"px";
    	obj2.className="bodyInternas";
    	//obj2.style.backgroundColor="gray";
    	//obj2.style.backgroundImage.src=url("img/fondoGris2.jpg");
    	
    	var marco = document.getElementById("caja3");
    	margen=10;
    	marco.style.display="";
    	marco.style.left=((medidas[0]-ancho)/2)-margen+"px";
    	marco.style.top=((medidas[1]-largo)/2)-margen+"px";
    	marco.style.width=ancho+(margen*2)+"px";
    	marco.style.height=largo+(margen*2)+"px";
    	
    	
    	var obj3 = document.getElementById("frmPublicEraser");
    	obj3.style.left="0px";
    	obj3.style.top="0px";
    	//obj3.width=obj2.style.width;
    	obj3.width="100%";
    	obj3.height="100%";
    }
    
	function viewField(tipo) {
		tipo=','+tipo+',';
		verMas = document.search.more.value;
		for(var i=7; i<200; i++) {
			if(document.getElementById('mas_'+i)) {
				//alert(document.getElementById('mas_'+i).className+" --- "+tipo);
				if(tipo==',0,' || document.getElementById('mas_'+i).className.indexOf(tipo)!=-1) {
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
	
function msgAlert(msg) {
	alert(msg);
	history.back();
}	
</script>

</head>

<body class="bodyInternas" <%=onLoad.replaceAll("alert","msgAlert")%> >
<table cellSpacing="1" cellPadding="0" align="center" border="0" width="100%"  height="100%" >
	<tr>
		<td width="<%=ToolsHTML.ANCHO_MENU%>px" valign="top" align="left" >
			<table cellSpacing="0" cellPadding="2"  border="0" width="<%=ToolsHTML.ANCHO_MENU%>px"  height="100%" style="/*border-collapse:collapse;*/border-color:#ofofof;border:1px solid #efefef">
				<tr>
					<td height="30px" class="ppalBoton" onmouseover="this.className='ppalBoton ppalBoton2'" onmouseout="this.className='ppalBoton'">
						<table border="0" cellspacing="0" cellpadding="2">
							<tr>
								<td>
									<a id="opt" href="#" class="menutip enlace_imagen">
									<img src="images/rightDark.gif" border="0" onClick="" onmouseover="document.getElementById('x').style.display='';">
								    <span id="x" onmouseover="limitar('opt')" class="nueva" style="display:none;position:absolute;left:11px;top:9px;">
								    	<iframe src="optionSearch.jsp" frameborder="0" style="height:230;width:150;border: 1px solid #0cf"></iframe>
								    </span>
								    </a>
								</td>
								<td class="ppalTextBold" width="100%">
									<%=rb.getString("enl.searchs")%>
								</td>
								<td>
								    &nbsp;
								</td>
								<%if("1".equals(String.valueOf(HandlerParameters.PARAMETROS.getPublicEraser()))) {%>
									<%if(session.getAttribute("querySearchReport")!=null && usuario.getIdGroup().equals(com.desige.webDocuments.utils.Constants.ID_GROUP_ADMIN)){%>
									<td>
									    <span ><img src="icons/thumb_up.png" title="<%=rb.getString("param.publicEraser")%>" onClick="javascript:publicarEraser();"></span>&nbsp;
									</td>
									<%}%>
								<%}%>
								<%if(session.getAttribute("querySearchLocation")!=null && usuario.getIdGroup().equals(com.desige.webDocuments.utils.Constants.ID_GROUP_ADMIN)){%>
								<td id="iconLocation">
								    <span ><img src="icons/page_white_go.png" title="<%=rb.getString("btn.changeLocation")%>" onClick="javascript:administrar();"></span>&nbsp;
								</td>
								<%}%>
								<td id="disk" style="display:none">
								    <span ><img src="icons/disk.png" title="<%=rb.getString("btn.save")%>" onClick="javascript:salir();"></span>&nbsp;
								</td>
								<td>
								    <span ><img src="icons/find.png" title="<%=rb.getString("btn.search")%>" onClick="javascript:searchDocument(document.search);"></span>&nbsp;
								</td>
								<%if(session.getAttribute("querySearchReport")!=null){%>
								<td>
								    <span ><img src="icons/page_excel.png" title="<%=rb.getString("lst.reporte")%>" onClick="javascript:reporteLista();"></span>&nbsp;
								</td>
								<%}%>
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
					<form name="search" method="post" action="searchDocument.do" style="margin:0;">
					    <input type="hidden" name="more" value="<%=session.getAttribute("more")%>"/>
					    <input type="hidden" name="accion" value="1"/>
					    <input type="hidden" name="orderBy" value=""/>
					    <input type="hidden" name="pages" value=""/>
					    <input id="public" name="public"   type="hidden" value="2"/>
					    
					<td id="panelFiltro" height="99%" bgcolor="white" valign="top" background="menu-images/backgd.jpg" class="scrollEnabled">
						<div id="listaOpcion" style="height:100%" class="scrollEnabled" >
						<table class="ppalText" style="cursor:pointer;" cellspacing="0" border="0" valign="top">
							<tr>
								<td>
					                <%=rb.getString("sch.keys")%><br/>
					                <input type="text" id="keys" name="keys" style="width:180px;height: 19px" value="<%=request.getAttribute("keys")!=null?request.getAttribute("keys"):""%>"/>
								    <script type="text/javascript">
                                         document.getElementById("keys").focus();
                                    </script>
								</td>
							</tr>
							<tr>
								<td>
					                <%=rb.getString("cbs.nameLM")%><br/>
					                <input type="text" name="nombre" style="width:180px;height: 19px" value="<%=request.getAttribute("nombre")!=null?request.getAttribute("nombre"):""%>"/>
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
					                <%=rb.getString("sch.number")%><br/>
					                <input type="text" name="number" style="width:180px;height: 19px" value="<%=request.getAttribute("number")!=null?request.getAttribute("number"):""%>"/>
								</td>
							</tr>
							<tr>
								<td>
					                <%=rb.getString("enl.estatus")%></br>
					                <logic:present name="TypeStatuSession" scope="session">
					                    <select name="TypesStatus" style="width:180px; height: 19px" styleClass="classText">
					                        <option value="0">&nbsp;</option>
					                        <logic:iterate id="bean" name="TypeStatuSession" scope="session" type="com.desige.webDocuments.utils.beans.Search">
					                            <logic:equal value="<%=TypesStatus%>" name="bean" property="id">
					                                <option value="<%=bean.getId()%>" selected><%=bean.getDescript()%> </option>
					                            </logic:equal>
					
					                            <logic:notEqual value="<%=TypesStatus%>" name="bean" property="id">
					                                <option value="<%=bean.getId()%>"><%=bean.getDescript()%></option>
					                            </logic:notEqual>
					                        </logic:iterate>
					
					                    </select>
					                </logic:present>
					                <logic:notPresent name="TypeStatuSession" scope="session">
					                    <%=rb.getString("E0006")%>
					                </logic:notPresent>
								</td>
							</tr>
							<tr>
								<td>
					                <%=rb.getString("doc.typeLM")%><br/>
					                <logic:present name="typesDocuments" scope="session">
					                    <select name="typeDocument" style="width:180px; height: 19px" styleClass="classText" onchange="viewField(this.value)">
					                        <option value="0">&nbsp;</option>
					                        <logic:iterate id="bean" name="typesDocuments" scope="session" type="com.desige.webDocuments.utils.beans.Search">
					                            <logic:equal value="<%=typeDocument%>" name="bean" property="id">
					                                <option value="<%=bean.getId()%>" selected><%=bean.getDescript()%></option>
					                            </logic:equal>
					
					                            <logic:notEqual value="<%=typeDocument%>" name="bean" property="id">
					                                <option value="<%=bean.getId()%>"><%=bean.getDescript()%></option>
					                            </logic:notEqual>
					                        </logic:iterate>
					
					                    </select>
					                </logic:present>
					                <logic:notPresent name="typesDocuments" scope="session">
					                    <%=rb.getString("E0006")%>
					                </logic:notPresent>
								</td>
							</tr>
							<tr id="mas">
								<td align="right">
									<a herf="#" onclick="javascript:mas()"><b><%=rb.getString("seeMore")%>...</b></a>
								</td>
							</tr>
							<tr id="mas_1" style="display:none">
								<td>
						            <%=rb.getString("sch.arbol")%>
		                            <table cellpadding="0" cellspacing="0" border="0">
		                            <tr>
								        <td>
						                    <input type="text" name="nameRout"  style="width:155px;height: 19px" value="<%=request.getAttribute("nameRout")!=null?request.getAttribute("nameRout"):""%>"/>
								        </td>
								        <td>
						                    &nbsp;<input type="button" class="boton" value=" ... " onClick="javascript:editField('loadAllStruct.do?toSelectValue=true','estado','nameState',this.form);" style="width:20px;"/>
						                    <input type="hidden" name="idRout" value="<%=request.getAttribute("idRout")!=null?request.getAttribute("idRout"):""%>"/>
						                </td>
						            </tr>
						            </table>
								</td>
							</tr>

			                <logic:present name="showCharge" scope="session">
							<tr id="mas_2" style="display:none">
								<td>
									<%=rb.getString("sch.cargo")%><br/>
                                    <%users = HandlerDBUser.getAllCargos();
                                      if (users!=null&&users.size() > 0) {
                                          pageContext.setAttribute("usuarios",users);%>
                                         <select name="cargo" size="1" style="width:180px; height: 19px" styleClass="classText">
                                             <option value="0">&nbsp;</option>
                                             <logic:present name="usuarios">
                                                 <logic:iterate id="bean" name="usuarios" type="com.desige.webDocuments.utils.beans.Search">
                                                     <logic:equal value="<%=cargo%>" property="id" name="bean" >
                                                         <option value="<%=bean.getId()%>" selected="true"><%=bean.getDescript()%></option>
                                                     </logic:equal>
                                                     <logic:notEqual value="<%=cargo%>" property="id" name="bean" >
                                                         <option value="<%=bean.getId()%>"><%=bean.getDescript()%></option>
                                                     </logic:notEqual>
                                                 </logic:iterate>
                                             </logic:present>
                                         </select>
                                     <%}%>
								</td>
							</tr>
							<tr id="mas_3" style="display:none">
								<td>
	                                <%=rb.getString("sch.prefix")%><br/>
	                                <input type="text" name="prefix" style="width:180px;height: 19px" value="<%=request.getAttribute("prefix")!=null?request.getAttribute("prefix"):""%>"/>
								</td>
							</tr>
							<tr id="mas_4" style="display:none">
								<td>
					                <%=rb.getString("sch.owner")%><br/>
   	                                <%
	                                  users = HandlerDBUser.getAllUsers();
	                                 if (users!=null&&users.size() > 0) {
	                                     pageContext.setAttribute("usuarios",users);
	                                %>
			                            <select name="owner" size="1" style="width:180px; height: 19px" styleClass="classText">
			                                <option value="0">&nbsp;</option>
			                                <logic:present name="usuarios">
			                                    <logic:iterate id="bean" name="usuarios" type="com.desige.webDocuments.utils.beans.Search">
			                                        <logic:equal value="<%=owner%>" property="id" name="bean" >
			                                            <option value="<%=bean.getId()%>" selected="true"><%=bean.getDescript()%></option>
			                                        </logic:equal>
			                                        <logic:notEqual value="<%=owner%>" property="id" name="bean" >
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
							<tr id="mas_5" style="display:none">
                                <td>
                                    <%=rb.getString("sch.version")%><br/>
                                    <input type="text" name="version" style="width:180px;height: 19px" value="<%=request.getAttribute("version")!=null?request.getAttribute("version"):""%>"/>                              </td>
                                </td>
                            </tr>
							<tr id="mas_6" style="display:none">
								<td>
			                         <%=rb.getString("sch.normISO")%><br/>
	                                 <%
	                                     norms = HandlerNorms.getAllNorms();
	                                     if (norms!=null&&norms.size() > 0) {
	                                         pageContext.setAttribute("norms",norms);
	                                 %>
	                                     <select name="normISO" size="1" style="width:180px; height: 19px" styleClass="classText">
	                                         <option value="0">&nbsp;</option>
	                                         <logic:present name="norms">
	                                             <logic:iterate id="bean" name="norms" type="com.desige.webDocuments.utils.beans.Search">
	                                                 <logic:equal value="<%=normISO%>" property="id" name="bean" >
	                                                     <option value="<%=bean.getId()%>" selected="true"><%=bean.getDescript()%></option>
	                                                 </logic:equal>
	                                                 <logic:notEqual value="<%=normISO%>" property="id" name="bean" >
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
							
							</logic:present>
							
						</table>
						</div>
						</form>
						<form name="Selection" id="Selection" action="loadStructMain.do" style="margin:0px;">
						    <input type="hidden" name="idDocument" value=""/>
						    <input type="hidden" name="idVersion" value=""/>
						    <input type="hidden" name="showStruct" value="true"/>
						    <input type="hidden" name="from" value="searchDocument.jsp?expand=false"/>
						</form>
					    <!-- paginacion ini -->
					    <form name="formPagingPage" method="post" action="searchDocument.jsp" style="margin:0px;">
					      <input type="hidden" name="visible"  value="true">
					      <input type="hidden" name="from"  value="">
					      <input type="hidden" name="cmd"  value="<%=SuperActionForm.cmdLoad%>">
					    </form>
					    <!-- paginacion fin -->
					</td>
				</tr>
				<tr id="opciones" style="border:0px;margin:0px;padding:0;">
					<td style="border:0px;">
						<table cellspacing="0" cellpadding="0" width="100%" border="0" style="margin:0;padding:0;">
							<%if (true || usuario!=null) {
	                            out.println(ToolsHTML.printMenu(rb,usuario.getUser(),usuario.getIdGroup(),true,request));
	                        }%>
	                    </table>
                	</td>
                </tr>
			</table>
		</td>
		<td align="center" valign="top">
		    <table align=center border="0" width="100%" cellspacing="0" cellpadding="0">
		        <tr>
		            <td colspan="4">
		                <table class="clsTableTitle" width="100%" cellSpacing="0" cellPadding="0" align=center border="0">
	                        <tbody>
	                            <tr>
	                                <td class="td_title_bc" height="21" width="30%">
	                                	<!-- paginador -->
					                    <logic:present name="searchDocs" scope="session">
					                        <!-- paginacion ini -->
					                        <%
					
					                            String from = request.getParameter("from")!=null?request.getParameter("from"):"";
					                            String size = session.getAttribute("size")!=null?(String)session.getAttribute("size").toString():"0";
					
					                            if (!ToolsHTML.isNumeric(size)) {
					                                size = "1";
					                            }
					                            if (!ToolsHTML.isNumeric(from)) {
					                                from = "0";
					                            }
					                            //PaginPage Pagingbean = ToolsHTML.getBeanPaginPage(from,Integer.parseInt(size),String.valueOf(usuarioActual.getNumRecord()));
					                            Pagingbean = ToolsHTML.getBeanPaginPage(from,Integer.parseInt(size),String.valueOf(lineas));
					                            String routImgs = "menu-images/";
					                        %>
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
	                                <td class="td_title_bc" height="21"  width="40%">
		                                <%=rb.getString("btn.search")%>
	                                </td>
	                                <td class="td_title_bc" height="21"  width="30%">
								        <logic:present name="size" scope="session">
			                            <span class="tituloSuave2">
			                                    <b>(<%=rb.getString("sch.totalDoc") + " " + session.getAttribute("size") + " " + rb.getString("cbs.documents")%>)</b>
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
		    <table align=center border="0" width="100%"  height="96%" cellspacing="0" cellpadding="0">
		        <tr>
		            <td><!--titulos-->
		                <table align="center" border="0" cellspacing="1" cellpadding="2" width="100%" height="100%">
		                    <tr >

	<%-- ydavila Ticket 001-00-003265 --%>		                        
	                        <td id="COL_A" class="td_orange_l_b barraBlue" width="10%" style="height:14px" nowrap>
                                <%=rb.getString("doc.numberLM")%><a href="javascript:ordenar('number');"><img border=0 src="img/<%=ToolsHTML.orden(session,"number")?"asc":"desc"%>.gif"/></a>
                            </td>
	                        <td id="COL_B" class="td_orange_l_b barraBlue" width="8%" style="height:14px" nowrap>
                            <%=rb.getString("doc.typeLM")%> <a href="javascript:ordenar('type');"><img border=0 src="img/<%=ToolsHTML.orden(session,"type")?"asc":"desc"%>.gif"/></a>
                            </td>
                            <td id="COL_C" class="td_orange_l_b barraBlue" width="18%" style="height:14px" nowrap>
	                            <%=rb.getString("cbs.nameLM")%> <a href="javascript:ordenar('name');"><img border=0 src="img/<%=ToolsHTML.orden(session,"name")?"asc":"desc"%>.gif"/></a>                            
	                        </td>

	                        <td id="COL_D" class="td_orange_l_b barraBlue" width="20%" style="height:14px" nowrap>
	                            <%=rb.getString("cbs.commentLM")%>
	                       </td>
	                        <td id="COL_E" class="td_orange_l_b barraBlue" width="15%" style="height:14px" nowrap>
	                            <%=rb.getString("doc.normISO")%><a href="javascript:ordenar('normISO');"><img border=0 src="img/<%=ToolsHTML.orden(session,"normISO")?"asc":"desc"%>.gif"/></a>
	                        </td>
	                        <td id="COL_F" class="td_orange_l_b barraBlue" width="15%" style="height:14px" nowrap>
	                            <%=rb.getString("cbs.owner")%><a href="javascript:ordenar('owner');"><img border=0 src="img/<%=ToolsHTML.orden(session,"owner")?"asc":"desc"%>.gif"/></a>
	                        </td>
	                        <td id="COL_G" class="td_orange_l_b barraBlue" width="4%" style="height:14px" nowrap>
	                            <%=rb.getString("showDoc.create")%><a href="javascript:ordenar('create');"><img border=0 src="img/<%=ToolsHTML.orden(session,"create")?"asc":"desc"%>.gif"/></a>
	                        </td>
	                        <td id="COL_H" class="td_orange_l_b barraBlue" width="4%" style="height:14px" nowrap>
	                        	<%=rb.getString("doc.status")%><a href="javascript:ordenar('status');"><img border=0 src="img/<%=ToolsHTML.orden(session,"status")?"asc":"desc"%>.gif"/></a>
	                        </td>
	                        <td id="COL_I" class="td_orange_l_b barraBlue" width="4%" style="height:14px" nowrap>
	                        	<%=rb.getString("doc.firmpending")%>
							</td>	
	<%-- ydavila Ticket 001-00-003265 --%>	
		                    </tr>
							<logic:notPresent name="searchDocs" scope="session">
			                        <tr>
			                            <td colspan="8"  valign="top" height="100%">
			                                <table class="clsTableTitle" width="100%" cellSpacing="0" cellPadding="0" align=center border="0">
			                                    <tbody>
			                                        <tr>
			                                            <td class="td_title_bc" height="19">
			                                            	<%if(session.getAttribute("more")==null){%>
										                    	<%=rb.getString("pb.found")%>
			                                            	<%} else {%>
										                    	<%=rb.getString("sch.notFound")%>
			                                            	<%}%>
			                                            </td>
			                                      </tr>
			                                    </tbody>
			                                </table>
			                            </td>
			                        </tr>
							</logic:notPresent>
		                    <logic:present name="searchDocs" scope="session">
		                    	<% int pintada=0;
		                    	   int cont=0; 
		                    	   int indice=-1;%>
		                    	<!-- paginacion ini -->
		                        <logic:iterate id="bean" name="searchDocs" indexId="ind" offset="<%=Pagingbean.getDesde()%>" length="<%=Pagingbean.getCuantos()%>"
		                        	type="com.desige.webDocuments.document.forms.BaseDocumentForm"  scope="session">
		                        	<%pintada++;%>
		                        <!-- paginacion fin -->
		                        
	<%-- ydavila Ticket 001-00-003265 --%>
		                        
		                        <tr id="row_<%=bean.getIdDocument()%>" class='fondo_<%=cont==0?cont++:cont--%>'>
	                                 <td id="LIN_A<%=indice%>" class="td_gris_l" nowrap>
                                        <%=bean.getPrefix() + bean.getNumber()%>
                                     </td>
                                     
		                            <td id="LIN_B<%=indice%>" class="td_gris_l" nowrap>
	                                    <%=bean.getDescriptTypeDoc()%>
	                                </td>
	                                
 		                        	<td id="LIN_C<%=++indice%>" class="td_gris_l" nowrap >
 		                        	
                                    <logic:present name="showLink" scope="session">
                                        <logic:equal name="showLink" value="true">
                                            <a href="javascript:showDocument('<%=bean.getIdDocument()%>',<%=bean.getNumVer()%>)" class="info<%=indice>=lineas-5?"up":""%>">
                                        </logic:equal>
                                        <logic:notEqual name="showLink" value="true">
                                            <a href="#" class="info<%=indice>=lineas-5?"up":""%>"></a>
                                        </logic:notEqual>
                                    </logic:present>
                                    
                                    <logic:notPresent name="showLink" scope="session">
                                    <a href="#" class="info<%=indice>=lineas-5?"up":""%>"></a>
                                </logic:notPresent>                                   
                                    <!-- contenido de la celda -->
                                    <font class="td_gris_c">
                                        <%=bean.getNameDocument().substring(0,nameWidth>bean.getNameDocument().length()?bean.getNameDocument().length():nameWidth)%>
                                    </font>
                                    <%=nameWidth>bean.getNameDocument().length()?"":"<b> . . .</b>"%>
                                            
                                    <logic:notEqual name="bean" property="isflowusuario" value="-1">
                                        <img src="icons/comment2.png" title="<%=rb.getString("wf.pend")%>" border="0">
                                    </logic:notEqual>
                                        
                                    <span id="jairo">
                                        <b><%=bean.getNameDocument()%></b><br/>
                                        <logic:notEqual name="bean" property="isflowusuario" value="-1">
                                            <div>
                                                <img src="icons/comment2.png" title="<%=rb.getString("wf.pend")%>" border="0">
                                                <%=bean.getIsflowusuario().replace(")",")&nbsp;-&nbsp;")%>
                                            </div>
                                            <%=ToolsHTML.estructuraWrap(bean.getRout(false))%>
                                        </logic:notEqual>
                                        <logic:equal name="bean" property="isflowusuario" value="-1">
                                            <%=ToolsHTML.estructuraWrap(bean.getRout(false))%>
                                        </logic:equal>
                                    </span>
                                    
                                    </a>                             
                       <%--columna COMENTARIOS ydavila Ticket 001-00-003265 --%>
                        	       <td id="LIN_D<%=indice%>" class="td_gris_l" unwrap  > 
                        	       <a class="info" style="cursor:none" readonly>
                        	       <%=HandlerDocuments.getDescriptCommentContextBuscar(bean.getcomment(),bean.getIdDocument(),bean.getNumVer(),bean.getStatuDoc()) %><br/> 
                        	       <span id="jairo" style="cursor:none"> 
                        	       <%=HandlerDocuments.getDescriptcommentBuscar(bean.getcomment(),bean.getIdDocument(),bean.getNumVer(),bean.getStatuDoc()) %>
                        	       </span>
                        	       </a>
                        	       </td>    
                        	       
                                    <td id="LIN_E<%=indice%>" class="td_gris_l" unwrap>
                                    <a class="info" style="cursor:none" readonly>
                                    <%=bean.getNormISODescript() %><br/>
                                    <span id="jairo" style="cursor:none">
                                    <%=HandlerNorms.getDescriptNormIso(bean.getNormISO()) %>
                                    </span>                            		
                                    </a>
                                    </td>

		                                <td id="LIN_F<%=indice%>" class="td_gris_l" nowrap>
		                                    <%=bean.getOwner()%>
		                                </td>
		                                
		                                <td id="LIN_G<%=indice%>" class="td_gris_l" nowrap>
		                                    <%=bean.getDateCreation()%>
		                                </td>
		                                
		                                <td id="LIN_H<%=indice%>" class="td_gris_l" nowrap>
		                                	<%=bean.getStatuDoc()%>
		                                </td>
		                                
		                                <td id="LIN_I<%=indice%>" class="td_gris_l" nowrap >
		                                    <logic:notEqual name="bean" property="isflowusuario" value="-1">
		                                        <a href="javascript:showCharge('<%=bean.getIsflowusuario()%>',''+'<%=rb.getString("wf.pend")%>','<%=bean.getIsNotflowsecuencial()%>')" class="ahref_b" style="font-size: 11px;">
		                                                <%=bean.getIsflowusuario().replace(")",")<br/>")%>
		                                        </a>
		                                    </logic:notEqual>
		                                </td>
 <%-- ydavila Ticket 001-00-003265 --%>
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

     </tr>
</table>
<div id="caja" class="fondoOscuro" style="display:none;">
</div>
<div id="caja2" style="position:absolute;top:0;left:0;">
	<iframe id="frmUbicacion" name="frmUbicacion" src="searchDocumentUbicacion.jsp" frameborder="no" style="width:0px;height:0px;"></iframe>
</div>
<div id="caja4" style="position:absolute;top:0;left:0;">
	<iframe id="frmPublicEraser" name="frmPublicEraser" src="searchDocumentPublic.jsp" frameborder="no" style="width:0px;height:0px;"></iframe>
</div>
<div id="caja3" style="display:none;">
</div>   
</body>
</html>
<script type="text/javascript">
function visualizar(col){
	all="ABCDEFGHI";
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
visualizar("<%=HandlerBD.getOptionSearch((int)usuarioActual.getIdPerson())%>");

function salir(){
	//if(isClose){
		//if((typeof window.opener)!='undefined'){
			if(window.opener.getObjectListDocument()!=window.opener.getListDocument()) {
				if(confirm("<bean:message key="btn.applyChanges"/>?")){
					window.opener.saveRelation();
				} else {
					window.opener.notSaveRelation();
				}
			}
		//}
	//}
}
function grabar(){
	if(window.opener.getObjectListDocument()!=window.opener.getListDocument()) {
		if(confirm("<bean:message key="btn.applyChanges"/>?")){
			window.opener.saveRelation();
		} else {
			window.opener.notSaveRelation();
		}
	}
}

if(window.opener!=null && (typeof window.opener)!='undefined' && window.opener.name=='bandeja'){
	document.getElementById("opciones").style.display="none";
	more('');
	document.getElementById("disk").style.display="";
	document.getElementById("iconLocation").style.display="none";
	//window.onunload=grabar;
}


colorear("");
more('<%=session.getAttribute("more")!=null && session.getAttribute("more").equals("true")?"":"none"%>');
</script> 