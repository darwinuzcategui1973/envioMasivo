<!-- /**
 * Title: searchDigital.jsp<br/>
 */ -->
<%@ page import="java.util.ResourceBundle,
				 java.util.HashMap,
				 java.util.TreeMap,
				 com.desige.webDocuments.to.DigitalTO,
                 com.desige.webDocuments.utils.ToolsHTML,
				 com.desige.webDocuments.persistent.managers.HandlerStruct,
                 com.desige.webDocuments.persistent.managers.HandlerDBUser,
                 com.desige.webDocuments.persistent.managers.HandlerDocuments,
                 com.desige.webDocuments.persistent.managers.HandlerNorms,
	             com.desige.webDocuments.utils.beans.Users,
                 com.desige.webDocuments.utils.beans.SuperActionForm,
                 com.desige.webDocuments.utils.beans.PaginPage,
                 com.desige.webDocuments.utils.Constants,
                 com.desige.webDocuments.persistent.managers.HandlerBD,
                 java.util.ArrayList,                 
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
	
	HashMap typesDocuments = (HashMap)session.getAttribute("typesDocuments");
	TreeMap listUsers = (TreeMap)session.getAttribute("listUsers");
	
	if(request.getParameter("all")!=null && request.getParameter("all").equals("true")){
		request.setAttribute("all",request.getParameter("all"));
	}
	
	PaginPage Pagingbean = null;
	int lineas=Constants.LINEAS_POR_PAGINA;
	//paginacion fin
	int nameWidth=60;
	int rootWidth=100;
	String ruta = "";
	
	
	Collection users = null;
	Collection norms = null;

    ResourceBundle rb = ToolsHTML.getBundle(request);
    String onLoad = ToolsHTML.getAlertInfo(request,rb);
    Users usuario = (Users)session.getAttribute("user");
    String ok = (String)session.getAttribute("usuario");

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
<script language=javascript src="./estilo/funciones.js"></script>
<script language="JavaScript">

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
        formaSelection.idDocument.value = idDoc;
        formaSelection.action = "showDataDocument.do";
        formaSelection.submit();
    }

    function checkField(field) {
        if (field){
            if (field.value.length > 0) {
                return true;
            }
        }
        return false;
    }

    function searchDigital(all) {
    	forma = document.search;
        forma.target = "info";
        forma.action = "searchDigital.do?all="+all;
        forma.submit();
    }

    function showStatus(ind){
        if (ind==0) {
            self.status = '';
        }
    }

    function refresh(forma,expand) {
        forma.action = "searchDigital.jsp?expand=" + expand;
        forma.submit();
    }

    function ordenar(orderBy) {
        forma = document.search;
        forma.orderBy.value = orderBy;
        forma.action = "orderDigital.do";
        forma.pages.value = "searchDigital";
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
        frm.action = "CrearReporteDigital.do";
        frm.submit();
    }
    
	function ver(etiqueta,valor) {
		var forma = document.search;
		
		//alert(forma.typeDocument.options[forma.typeDocument.selectedIndex].text);
		
		if(valor=="new"){
			crear();
		}
		if(valor=="mod"){
			crear();
		}
		limpiar();
		
		window.open(etiqueta,"bandeja");
	}

	function limpiar() {
		document.getElementById("titleLeft0").style.display="";
		document.getElementById("titleLeft1").style.display="none";
		document.getElementById("titleRight0").style.display="";
		document.getElementById("titleRight1").style.display="none";
	}
	
	function crear() {
		document.getElementById("screenMain").style.display="none";
		document.getElementById("frameWork").style.display="";
	}
	
	function permission(type,nodeType) {
		  crear();
		  limpiar();
		  var forma = document.getElementById("Selection");
	      forma.target="bandeja";
	      forma.action = "loadAllUserDigital.do";
	      
	      if (forma.nodeType) {
	          forma.nodeType.value = nodeType;
	      }
	      if (type==1) {
	          forma.action = "loadAllGroupsDigital.do";
	      }
	      forma.submit();
	}
	
	function main(){
		redimensionarIframe("0");
	}
	
	function seleccionar(valor) {
    	for(var i=1; i<document.listado.seleccionados.length;i++) {
    		document.listado.seleccionados[i].checked=valor;
    	}
	}
    
    function eliminar(accion) {
    	if(!document.listado.seleccionados){
    		alert("<%=rb.getString("imp.selecelemlista")%>")
    		return;
    	}
    	var valido=false;
    	for(var i=1; i<document.listado.seleccionados.length;i++) {
    		if(document.listado.seleccionados[i].checked) {
    			valido=true;
    			break;
    		}
    	}
		if(!valido) {
    		alert("<%=rb.getString("imp.selecelemlista")%>..")
    		return;
		}
    	
		if (confirm("<%=rb.getString("areYouSure")%>")) {
         document.listado.eliminar.value="true";
         document.listado.submit();
    	}
    }
	function abrirVentanaFull(pagina,nameWin) {
	    var hWnd = null;
	    setDimensionScreen();
	    var left = 0;
	    var top = 0;
	    hWnd = window.open(pagina, nameWin, "resizable=yes,scrollbars=yes,statusbar=yes,width="+winWidth+",height="+winHeight+",left="+left+",top="+top);
	}

    function editField(pages,input,value,forma) {
        var hWnd = null;
        hWnd = window.open(pages+"?input="+input+"&value="+value+"&nameForma="+forma.attributes['name'].value,"Catalogo","width=600,height=450,resizable=yes,statubar=yes,scrollbars=yes,left=200,top=150");
        if ((document.window != null) && (!hWnd.opener)){
            hWnd.opener = document.window;
        }
    }
    
	window.http = null;
	function buscarTipo(idTypeDoc) {
		if(navigator.appName == "Microsoft Internet Explorer") {
		  window.http = new ActiveXObject("Microsoft.XMLHTTP");
		} else {
		  window.http = new XMLHttpRequest();
		}
		window.http.open("POST", "findTypeDocumentAjax.do?idTypeDoc="+idTypeDoc);
		window.http.onreadystatechange=function() {
		  //try {
			  if(window.http.readyState == 4) {
			  	var tipo = eval(window.http.responseText);
			  	if(tipo.idNodeTypeDoc!='null'){
        			document.search.idNode.value=tipo.idNodeTypeDoc;
        		}
			  	if(tipo.idNodeName!='null'){
        			document.search.nameRout.value=tipo.idNodeName;
        		}
			  	if(tipo.ownerTypeDocUserName!='null'){
        			document.search.ownerTypeDoc.value=tipo.ownerTypeDoc;
        		}
			  	if(tipo.typesetter!='null'){
        			document.search.typesetter.value=tipo.typesetter;
        		}
			  	if(tipo.checker!='null'){
        			document.search.checker.value=tipo.checker;
        		}
			  	
			  }
		  //} catch(e){
		  //}
		}
		window.http.send(null);
	}
	
	function aplicarTipo() {
		var cad = "digitalizarLoad.do?typeDocuments="+document.search.typeDocument.value;
		cad += "&ownerTypeDoc="+document.search.ownerTypeDoc.value;
		cad += "&idNodeTypeDoc="+document.search.idNode.value;
		cad += "&typesetter="+document.search.typesetter.value;
		cad += "&checker="+document.search.checker.value;
		cad += "&lote="+document.search.lote.value;
		cad += "&correlativo="+document.search.correlativo.value;
		cad += "&aplicarParameter=true";
		if(navigator.appName == "Microsoft Internet Explorer") {
		  window.http = new ActiveXObject("Microsoft.XMLHTTP");
		} else {
		  window.http = new XMLHttpRequest();
		}

		window.http.open("POST",cad);
		window.http.onreadystatechange=function() {
		  //try {
			  if(window.http.readyState == 4) {
			  	var valor = window.http.responseText;
			  	if(valor=='true') {
			  		alert('<%=rb.getString("sistema.applied")%>');
			  	} else {
			  		alert(valor);
			  	}
			  }
		  //} catch(e){
		  //}
		}
		window.http.send(null);
	}
	
    
</script>
<!-- paginacion ini -->
<script language="javascript">
    function paging_OnClick(pageFrom,all){
        document.formPagingPage.from.value = pageFrom;
        document.formPagingPage.all.value = all;
        document.formPagingPage.submit();
    }
</script>
<!-- paginacion fin -->
<!--<script type="text/javascript" src="estilo/scroll.js"></script>-->
<script type="text/javascript">
	//registraScroll('flechaAbajo','flechaArriba','listaOpcion',8,-8);
</script>
</head>

<body class="bodyInternas">

<!-- Formulario para Obtener el nodo Seleccionado por el usuario -->
<table cellSpacing="0" cellPadding="0" align="center" border="0" width="100%"  height="100%" >
	<tr>
		<td width="<%=ToolsHTML.ANCHO_MENU%>px" valign="top" align="left" >
			<table cellSpacing="0" cellPadding="0"  border="0" width="<%=ToolsHTML.ANCHO_MENU%>px"  height="100%" style="border-collapse:collapse;border-color:#ofofof;border:1px solid #efefef">
				<tr>
					<td height="30px" class="ppalBoton" onmouseover="this.className='ppalBoton ppalBoton2'" onmouseout="this.className='ppalBoton'">
						<table border="0" cellspacing="0" cellpadding="0">
							<tr>
								<td class="ppalTextBold" width="100%">
									<%=rb.getString("enl.digital")%>
								</td>
								<td>
								    &nbsp;
								</td>

								<td>
								    <span ><img src="icons/application_view_list.png" title="<%=rb.getString("btn.secDocs")%>" onClick="javascript:searchDigital(false);"></span>&nbsp;
								</td>
								<td>
								    <span ><img src="icons/digitalizar.png" title="<%=rb.getString("enl.digital")%>" onClick="javascript:ver('digitalRegister.do','new');"></span>&nbsp;
								</td>
								<td >
								    <span ><img src="icons/folder_find.png" title="<%=rb.getString("sistema.importer")%>" onClick="javascript:ver('digitalImporter.do','new');"></span>&nbsp;
								</td>
								<td>
								    <span ><img src="icons/application_form_magnify.png" title="Procesados" onClick="javascript:searchDigital(true);"></span>&nbsp;
								</td>
								<td>
								    <span ><img src="icons/cancel.png" title="<%=rb.getString("btn.delete")%>" onClick="javascript:eliminar('digitalRegister.do');"></span>&nbsp;
								</td>
							</tr>
						</table>
					</td>
					</td>
				</tr>
				<!-- menu 
		        <logic:equal value="true" parameter="expand">
		        </logic:equal>
		        -->
				<tr>
					<td height="99%" bgcolor="white" valign="top" class="ppalText" background="menu-images/backgd.png">
						<form name="search" method="post" action="searchDigital.do" style="margin:0px;">
					    <input type="hidden" name="accion" value="1"/>
					    <input type="hidden" name="orderBy" value=""/>
					    <input type="hidden" name="pages" value=""/>
					    <input id="public" name="public"   type="hidden" value="2"/>
					    
					   	<table class="texto" style="cursor:pointer;" cellpadding="0" cellspacing="0" border="0" width="100%">
							<tr>
								<td style="padding:5 0 0 0;">
				                    <%=rb.getString("pb.typeDoc")%><br/>
				                    <logic:present name="tiposDoc" scope="session">
				                        <select name="typeDocument" style="width:180px; height: 19px" styleClass="classText" onchange="buscarTipo(this.value)">
				                            <logic:iterate id="bean" name="tiposDoc" scope="session" type="com.desige.webDocuments.utils.beans.Search">
		                                        <option value="<%=bean.getId()%>" ><%=bean.getDescript()%></option>
				                            </logic:iterate>
				                        </select>
				                    </logic:present>
								</td>
							</tr>
							<tr>
								<td style="padding:5 0 0 0;">
		                            <%=rb.getString("typeDoc.owner")%><br/>
					                <select name="ownerTypeDoc" styleClass="" style="width: 180px">
					                    <logic:present name="users" scope="session" >
			                            <logic:iterate id="bean" name="users" scope="session" type="com.desige.webDocuments.utils.beans.Search">
			                                <option value="<%=bean.getId()%>"><%=bean.getDescript()%></option>
			                            </logic:iterate>
					                    </logic:present>
									</select>
								</td>
							</tr>
							<tr>
								<td style="padding:5 0 0 0;">
						            <%=rb.getString("sch.arbol")%><br/>
		                            <table cellpadding="0" cellspacing="0" border="0">
		                            <tr>
								        <td>
							                <input type="text" name="nameRout"  style="width:155px;height: 19px" value=""/>
								        </td>
								        <td>
							                &nbsp;<input type="button" class="boton" value=" ... " onClick="javascript:editField('loadAllStruct.do?toSelectValue=true','estado','nameState',this.form);" style="width:20px;"/>
							                <input type="hidden" id="idRout" name="idNode" />
						                </td>
						            </tr>
						            </table>
								</td>
							</tr>
							<tr>
								<td style="padding:5 0 0 0;">
		                            <%=rb.getString("typeDoc.typesetter")%><br/>
					                <select name="typesetter" styleClass="" style="width: 180px">
					                    <logic:present name="users" scope="session" >
			                            <logic:iterate id="bean" name="users" scope="session" type="com.desige.webDocuments.utils.beans.Search">
			                                <option value="<%=bean.getId()%>"><%=bean.getDescript()%></option>
			                            </logic:iterate>
					                    </logic:present>
									</select>
								</td>
							</tr>
							<tr>
								<td style="padding:5 0 0 0;">
		                            <%=rb.getString("typeDoc.checker")%><br/>
					                <select name="checker" styleClass="" style="width: 180px">
					                    <logic:present name="users" scope="session" >
			                            <logic:iterate id="bean" name="users" scope="session" type="com.desige.webDocuments.utils.beans.Search">
			                                <option value="<%=bean.getId()%>"><%=bean.getDescript()%></option>
			                            </logic:iterate>
					                    </logic:present>
									</select>
								</td>
							</tr>
							<tr>
								<td style="padding:5 0 0 0;">
		                            <%=rb.getString("typeDoc.lot")%><br/>
					                <input type="text" name="lote" styleClass="" style="width: 180px"/>
								</td>
							</tr>
							<tr>
								<td style="padding:5 0 0 0;">
						            Correlativo<br/>
		                            <table cellpadding="0" cellspacing="0" border="0">
		                            <tr>
								        <td>
							                <input type="text" name="correlativo"  style="width:155px;height:19px" value=""/>
								        </td>
								        <td>
							                &nbsp;<input type="button" class="boton" value=" ... " onClick="javascript:abrirVentana('correlativo.do?inicio='+this.form.correlativo.value,600,600)" style="width:20px;"/>
						                </td>
						            </tr>
						            </table>
								</td>
							</tr>
							<tr>
								<td style="padding:5 10 0 0;" align="right">
									<input type="button" value="aplicar" onclick="aplicarTipo()">
								</td>
							</tr>
						</table>					   		
						</form>
						<form name="Selection" id="Selection" action="loadStructMain.do" style="margin:0px;">
						    <input type="hidden" name="idDocument" value=""/>
						    <input type="hidden" name="idVersion" value=""/>
						    <input type="hidden" name="showStruct" value="true"/>
						    <input type="hidden" name="from" value="searchDigital.jsp?expand=false"/>
						</form>
					    <!-- paginacion ini -->
					    <form name="formPagingPage" method="post" action="searchDigital.jsp" style="margin:0px;">
					      <input type="hidden" name="visible"  value="true">
					      <input type="hidden" name="from"  value="">
					      <input type="hidden" name="all"  value="">
					      <input type="hidden" name="cmd"  value="<%=SuperActionForm.cmdLoad%>">
					    </form>
					    <!-- paginacion fin -->
					</td>
				</tr>
						<%if (usuario!=null) {
                            out.println(ToolsHTML.printMenu(rb,usuario.getUser(),usuario.getIdGroup(),true,request));
                        }%>
			</table>
		</td>
		<td align="center" valign="top">

		    <table align=center border="0" width="100%" cellspacing="0" cellpadding="0">
		        <tr>
		            <td colspan="4">
		                <table class="clsTableTitle" width="100%" cellSpacing="0" cellPadding="0" align=center border="0">
	                        <tbody>
	                            <tr>
	                                <td id="titleLeft0" class="td_title_bc" height="21" width="30%" style="display:none">
	                                	&nbsp;
	                                </td>
	                                <td id="titleLeft1" class="td_title_bc" height="21" width="30%">
	                                	<!-- paginador -->
					                    <logic:present name="searchDigital" scope="session">
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
                                                       onclick="paging_OnClick(0,<%=request.getAttribute("all")%>)">&nbsp;
                                                    </td>
                                                    <td align="center"> <img src="images/left2.gif"
                                                       class="GraphicButton" title="<%=rb.getString("pagin.Previous")%>"
                                                       onclick="paging_OnClick(<%=(Integer.parseInt(Pagingbean.getPages())-1)%>,<%=request.getAttribute("all")%>)">
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
                                                       onclick="paging_OnClick(<%=(Integer.parseInt(Pagingbean.getPages())+1)%>,<%=request.getAttribute("all")%>)">&nbsp;
                                                    </td>
                                                    <td align="center"> <img src="images/fin2.gif"
                                                       class="GraphicButton" title="<%=rb.getString("pagin.Last")%>"
                                                       onclick="paging_OnClick(<%=Pagingbean.getNumPages()%>,<%=request.getAttribute("all")%>)">
                                                    </td>
                                                </tr>
                                            </table>
										    <!-- paginacion fin -->
										    
					                    </logic:present>
	                                </td>
	                                <td class="td_title_bc" height="21"  width="40%">
		                                <%=rb.getString("enl.digital")%>
	                                </td>
	                                <td id="titleRight0" class="td_title_bc" height="21"  width="30%" style="display:none">
	                                	&nbsp;
	                                </td>
	                                <td id="titleRight1" class="td_title_bc" height="21"  width="30%">
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
		    <form name="listado" method="post" action="searchDigital.do" style="margin:0;">
		    	<input type="hidden" name="eliminar" value="false" />
		    <table align=center border="0" width="100%"  height="96%" cellspacing="0" cellpadding="0">
                <tr id="frameWork" style="display:none">
                    <td colspan="10"  valign="top" height="100%">
                        <table class="clsTableTitle" width="100%" cellSpacing="0" cellPadding="0" align=center border="0">
                            <tbody>
                                <tr>
                                    <td class="td_title_bc" height="19">
										<iframe name="bandeja" id="bandeja" width="100%" height="100%" border="0px" marginwidth="0px" marginheight="0px" frameborder="0"></iframe>
                                    </td>
                              </tr>
                            </tbody>
                        </table>
                    </td>
                </tr>
		        <tr id="screenMain" >
		            <td><!--titulos-->
		                <table align="center" border="0" cellspacing="1" cellpadding="1" width="100%" height="100%">
		                    <tr>
		                        <td id="COL_A" class="td_orange_l_b barraBlue" style="display:<%=(request.getAttribute("all")==null?"":"none")%>">
		                        	<input type="checkbox" onclick="seleccionar(this.checked)"/>
		                        	<input type="checkbox" name="seleccionados" style="display:none;"/>
                                </td>
		                        <td id="COL_B" class="td_orange_l_b barraBlue" style="height:14px;text-align:center" nowrap>
		                        	<%=rb.getString("dig.nomDocument")%>
                                </td>
		                        <td id="COL_C" class="td_orange_l_b barraBlue" style="height:14px;text-align:center" nowrap>
		                        	<%=rb.getString("lic.tipo0")%>
                                </td>
		                        <td id="COL_D" class="td_orange_l_b barraBlue" style="height:14px;text-align:center" nowrap>
		                        	<%=rb.getString("typeDoc.lot")%>
                                </td>
		                        <td id="COL_D" class="td_orange_l_b barraBlue" style="height:14px;text-align:center" nowrap>
		                        	<%=rb.getString("sch.number")%>
                                </td>
		                        <td id="COL_D" class="td_orange_l_b barraBlue" style="height:14px;text-align:center" nowrap>
		                        	<%=rb.getString("typeDoc.owner")%>
                                </td>
		                        <td id="COL_D" class="td_orange_l_b barraBlue" style="height:14px;text-align:center" nowrap>
		                        	<%=rb.getString("dig.digitalizador")%>
                                </td>
		                        <td id="COL_D" class="td_orange_l_b barraBlue" style="height:14px;text-align:center" nowrap>
		                        	<%=rb.getString("typeDoc.typesetter")%>
                                </td>
		                        <td id="COL_D" class="td_orange_l_b barraBlue" style="height:14px;text-align:center" nowrap>
		                        	<%=rb.getString("typeDoc.checker")%>
                                </td>
		                        <td id="COL_D" class="td_orange_l_b barraBlue" style="height:14px;text-align:center;width:50px;" nowrap>
		                        	<%=rb.getString("showDoc.status")%>
                                </td>
		                        <td id="COL_D" class="td_orange_l_b barraBlue" style="height:14px;text-align:center;width:130px;" nowrap>
		                        	<%=rb.getString("cbs.dateCreation")%>
                                </td>
		                    </tr>
							<logic:notPresent name="searchDigital" scope="session">
			                        <tr>
			                            <td colspan="11"  valign="top" height="100%">
			                                <table class="clsTableTitle" width="100%" cellSpacing="0" cellPadding="0" align=center border="0">
			                                    <tbody>
			                                        <tr>
			                                            <td class="td_title_bc" height="19" >
										                    <%=rb.getString("sch.notFound")%>
			                                            </td>
			                                      </tr>
			                                    </tbody>
			                                </table>
			                            </td>
			                        </tr>
							</logic:notPresent>
		                    <logic:present name="searchDigital" scope="session">
		                    
		                    	<% int pintada=0;
		                    	   int cont=0; 
		                    	   int indice=-1;%>
		                    	   
		                    	<!-- paginacion ini -->
		                        <logic:iterate id="bean" name="searchDigital" indexId="ind" offset="<%=Pagingbean.getDesde()%>" length="<%=Pagingbean.getCuantos()%>"
		                        	type="com.desige.webDocuments.to.DigitalTO"  scope="session">
		                        	<%pintada++;%>
		                        <!-- paginacion fin -->
		                        	<tr class='fondo_<%=cont==0?cont++:cont--%> mano' /> <!-- onclick="ver('filesView.do?f1=<%=bean.getIdDigital()%>','mod');" -->
		                        		<%indice++;%>
		                                <td id="LIN_A<%=indice%>" class="td_gris_l" nowrap style="display:<%=(request.getAttribute("all")==null?"":"none")%>">
		                                	&nbsp;<input type="checkbox" name="seleccionados" value="<%=bean.getIdDigital()%>">
		                                </td>
		                                <td id="LIN_B<%=indice%>" class="td_gris_l" nowrap>
		                                	&nbsp;
		                                	<%if(request.getAttribute("all")==null){%>
			                                	<a href="javascript:abrirVentanaFull('<%=ToolsHTML.getBasePath(request)%>upploadFileFrame.do?idDigital=<%=bean.getIdDigital()%>','_self')" >
			                                		<%=bean.getNameFile().equals("")?bean.getNameFileDisk():bean.getNameFile()%>
			                                	</a>
		                                	<%} else {%>
			                                	<%=bean.getNameFile().equals("")?bean.getNameFileDisk():bean.getNameFile()%>
		                                	<%}%>
		                                </td>
		                                <td id="LIN_C<%=indice%>" class="td_gris_l" nowrap>
		                                	&nbsp;<%=typesDocuments.get(bean.getType())%>&nbsp;
		                                </td>
		                                <td id="LIN_D<%=indice%>" class="td_gris_l" nowrap>
		                                	&nbsp;<%=bean.getLote()%>&nbsp;
		                                </td>
		                                <td id="LIN_D<%=indice%>" class="td_gris_l" nowrap>
		                                	&nbsp;<%=bean.getCodigo()%>&nbsp;
		                                </td>
		                                <td id="LIN_D<%=indice%>" class="td_gris_l" nowrap>
		                                	&nbsp;<%=listUsers.get(bean.getOwnerTypeDoc())%>&nbsp;
		                                </td>
		                                <td id="LIN_D<%=indice%>" class="td_gris_l" nowrap>
		                                	&nbsp;<%=listUsers.get(bean.getIdPerson())%>&nbsp;
		                                </td>
		                                <td id="LIN_D<%=indice%>" class="td_gris_l" nowrap>
		                                	&nbsp;<%=listUsers.get(bean.getTypesetter())%>&nbsp;
		                                </td>
		                                <td id="LIN_D<%=indice%>" class="td_gris_l" nowrap>
		                                	&nbsp;<%=listUsers.get(bean.getChecker())%>&nbsp;
		                                </td>
		                                <td id="LIN_D<%=indice%>" class="td_gris_l" nowrap>
		                                	&nbsp;<%=Constants.STATUS_DIGITAL[Integer.parseInt(bean.getIdStatusDigital())]%>&nbsp;
		                                </td>
		                                <td id="LIN_D<%=indice%>" class="td_gris_l" nowrap>
		                                	&nbsp;<%=bean.getDateCreationToShow()%>&nbsp;
		                                </td>
		                            </tr>
		                            <%if(bean.getComentario()!=null){%>
		                        	<tr class='fondo_<%=cont==0?cont++:cont--%> mano' /> <!-- onclick="ver('filesView.do?f1=<%=bean.getIdDigital()%>','mod');" -->
		                                <td class="td_gris_l" nowrap style="color:red;display:<%=(request.getAttribute("all")==null?"":"none")%>">&nbsp;</td>
		                                <td colspan="11" class="td_gris_l" style="color:red;">
		                                	&nbsp;<%=bean.getComentario()%>&nbsp;
		                                </td>
		                            </tr>
		                            <%}%>
		                        </logic:iterate>
		                        
		                        <%for(pintada++;pintada<=lineas;pintada++){%>
		                        	<tr>
		                        		<td colspan="3">&nbsp;</td>
		                        	</tr>
		                        <%}%>
		                    </logic:present>
		                </table>
		            </td>
		        </tr>
		    </table>
			</form>
     </tr>
</table>
</body>
</html>
<script>

function redimensionarIframe(tipo) {
	// Guardamos el iframe que vamos a redimensionar
	var iFrame = window.document.getElementById('bandeja');

	//alert(document.body.scrollHeight);
	//alert(document.body.offsetHeight);
	//alert(document.body.clientHeight);
	
	var alturaIframe = document.body.scrollHeight-30; //-12
	// Redimensionamos la altura del iframe
	if(typeof tipo== "undefined" && !document.all){
		iFrame.style.height = "100%";
		setTimeout("redimensionarIframe('0')",500);
	} else {
		iFrame.style.height = eval("'"+alturaIframe+"px'");
	}
	
}// Fin de la función

//if(document.all) {
	window.onresize=redimensionarIframe;
//}
</script>

<script language="javascript" event="onload" for="window">

	document.search.typeDocument.value='<%=usuarioActual.getTypeDocuments()%>';
	document.search.idRout.value='<%=usuarioActual.getIdNodeDigital()%>';
	document.search.nameRout.value='<%=ToolsHTML.getNodeName(String.valueOf(usuarioActual.getIdNodeDigital()))%>';
	document.search.ownerTypeDoc.value='<%=usuarioActual.getOwnerTypeDoc()%>';
	document.search.typesetter.value='<%=usuarioActual.getTypesetter()%>';
	document.search.checker.value='<%=usuarioActual.getChecker()%>';
	document.search.lote.value='<%=usuarioActual.getLote()%>';
	document.search.correlativo.value='<%=usuarioActual.getCorrelativo()%>';


//if(document.all) {
	redimensionarIframe("0");
	<%=onLoad%>
//}

</script>
