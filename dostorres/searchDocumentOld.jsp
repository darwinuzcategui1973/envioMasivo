<!-- /**
 * Title: searchDocumentOld.jsp<br/>
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
                 com.desige.webDocuments.utils.ToolsHTML,
                 com.desige.webDocuments.persistent.managers.HandlerDBUser,
                 com.desige.webDocuments.persistent.managers.HandlerNorms,
	             com.desige.webDocuments.utils.beans.Users,
                 com.desige.webDocuments.utils.beans.SuperActionForm,
                 com.desige.webDocuments.utils.beans.PaginPage,
                 com.desige.webDocuments.utils.Constants,
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
	//paginacion fin

    //if(request.getAttribute("visible")==null || !String.valueOf(request.getAttribute("visible")).equals("true") ) {
    	//response.sendRedirect("searchDocument.do");
    //}

	Collection users = null;
	Collection norms = null;

    ResourceBundle rb = ToolsHTML.getBundle(request);
    String onLoad = ToolsHTML.getMensajePages(request,rb);
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
            }
        } else {
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
            forma.target = "info";
            forma.action = "searchDocument.do";
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
    

</script>
<!-- paginacion ini -->
<script language="javascript">
    function paging_OnClick(pageFrom){
        document.formPagingPage.from.value = pageFrom;
        document.formPagingPage.submit();
    }
</script>
<!-- paginacion fin -->
</head>

<body class="bodyInternas" <%=onLoad%>>
<!-- Formulario para Obtener el nodo Seleccionado por el usuario -->
<form name="Selection" id="Selection" action="loadStructMain.do">
    <input type="hidden" name="idDocument" value=""/>
    <input type="hidden" name="idVersion" value=""/>
    <input type="hidden" name="showStruct" value="true"/>
    <input type="hidden" name="from" value="searchDocument.jsp?expand=false"/>
</form>
<form name="search" method="post" action="searchDocument.do">
    <input type="hidden" name="accion" value="1"/>
    <input type="hidden" name="orderBy" value=""/>
    <input type="hidden" name="pages" value=""/>
    <table align=center border=0 width="100%">
        <tr>
            <td colspan="4">
                <table class="clsTableTitle" width="100%" cellSpacing=0 cellPadding=2 align=center border=0>
                    <tbody>
                        <tr>
                            <td class="td_title_bc" height="21">
                                <%=rb.getString("btn.search")%>
                            </td>
                        </tr>
                    </tbody>
                </table>
            </td>
        </tr>
        <tr>
            <td colspan="4">&nbsp;
            </td>
        </tr>
        <tr>
            <td class="titleLeft" height="26" style="background: url(img/btn120.png); background-repeat: no-repeat" width="14%" valign="middle">
                <%=rb.getString("sch.keys")%>
            </td>
            <td width="36%">
                &nbsp;
                <input type="text" name="keys" style="width: 250 px;height: 19px" value="<%=request.getAttribute("keys")!=null?request.getAttribute("keys"):""%>"/>
            </td>
            <td class="titleLeft" height="26" style="background: url(img/btn120.png); background-repeat: no-repeat" width="14%" valign="middle">
              <%=rb.getString("sch.arbol")%>
            </td>
            <td width="36%">
                &nbsp;
                    <input type="text" name="nameRout"  style="width: 250 px;height: 19px" value="<%=request.getAttribute("nameRout")!=null?request.getAttribute("nameRout"):""%>"/>
                    <input type="hidden" name="idRout" value="<%=request.getAttribute("idRout")!=null?request.getAttribute("idRout"):""%>"/>
                    <input type="button" class="boton" value=" ... " onClick="javascript:editField('loadAllStruct.do?toSelectValue=true','estado','nameState',this.form);" style="width:20px;"/>
            </td>
        </tr>
        <tr>
            <td class="titleLeft" height="26" style="background: url(img/btn120.png); background-repeat: no-repeat" width="14%" valign="middle">
                <%=rb.getString("sch.name")%>
            </td>
            <td width="36%">
                &nbsp;
                <input type="text" name="nombre" style="width: 250 px;height: 19px" value="<%=request.getAttribute("nombre")!=null?request.getAttribute("nombre"):""%>"/>
            </td>
            <td class="titleLeft" height="26" style="background: url(img/btn120.png); background-repeat: no-repeat" width="14%" valign="middle">
                <%=rb.getString("sch.number")%>
            </td>
            <td width="36%">
                &nbsp;
                <input type="text" name="number" style="height: 19px" value="<%=request.getAttribute("number")!=null?request.getAttribute("number"):""%>"/>
            </td>
        </tr>
        <tr>
            <td class="titleLeft" height="26" style="background: url(img/btn120.png); background-repeat: no-repeat" valign="middle">
                <%=rb.getString("sch.tipo")%>
            </td>
            <td width="36%">
                <logic:present name="typesDocuments" scope="session">
                    &nbsp;
                    <select name="typeDocument" style="width: 250px; height: 19px" styleClass="classText">
                        <option value="0">-----------------------------------------------------------</option>
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
            <td class="titleLeft" height="26" style="background: url(img/btn120.png); background-repeat: no-repeat" valign="middle">
                    <%=rb.getString("enl.estatus")%>
               </td>
                <td width="36%">
                <logic:present name="TypeStatuSession" scope="session">
                    &nbsp;
                    <select name="TypesStatus" style="width: 250px; height: 19px" styleClass="classText">
                        <option value="0">-----------------------------------------------------------</option>
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
            <td colspan="4">
                <logic:equal value="true" parameter="expand">
                    <a href="javascript:refresh(document.search,'false');" class="ahref_b" onmouseover="javascript:showStatus(0);return true;"
                       onmouseout="showStatus(1);return true;">
                       <%=rb.getString("bsq.ocultaop")%>
                    </a>
                    <input type="hidden" name="expand" value="true"/>
                </logic:equal>
                <logic:notEqual value="true" parameter="expand" >
                    <a href="javascript:refresh(document.search,'true');" class="ahref_b" onmouseover="javascript:showStatus(0);return true;"
                       onmouseout="showStatus(1);return true;">
                       <%=rb.getString("bsq.masop")%>
                    </a>
                    <input type="hidden" name="expand" value="false"/>
                </logic:notEqual>
            </td>
        </tr>

        <logic:equal value="true" parameter="expand">
            <tr>
                <!-- SIMON 1 DE JUNIO DEL 2005 INICIO -->
                <logic:present name="showCharge" scope="session">
                    <td class="titleLeft" height="26" style="background: url(img/btn120.png); background-repeat: no-repeat" valign="middle">
                        <%=rb.getString("sch.cargo")%>
                    </td>
                </logic:present>
                <!-- SIMON 1 DE JUNIO DEL 2005 FIN -->
                    <td width="36%">
                        &nbsp;

                   <!-- SIMON 1 DE JUNIO DEL 2005 INICIO -->
                   <logic:present name="showCharge" scope="session">
                                               <%
                                                   users = HandlerDBUser.getAllCargos();
                                                  if (users!=null&&users.size() > 0) {
                                                      pageContext.setAttribute("usuarios",users);
                                                %>
                                             <select name="cargo" size="1" style="width: 250px; height: 19px" styleClass="classText">
                                                 <option value="0">-----------------------------------------------------------</option>
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
                                         <%
                                             }
                                         %>
                                     </td>
                                     <td height="26" class="titleLeft" style="background: url(img/btn120.png); background-repeat: no-repeat" valign="middle">
                                         <%=rb.getString("sch.prefix")%>
                                     </td>
                                     <td width="36%">
                                         &nbsp;
                                         <input type="text" name="prefix" style="height: 19px" value="<%=request.getAttribute("prefix")!=null?request.getAttribute("prefix"):""%>"/>
                                     </td>
                                 </tr>
                                 <tr>
				                    <td class="titleLeft" height="26" style="background: url(img/btn120.png); background-repeat: no-repeat" valign="middle">
				                        <%=rb.getString("sch.owner")%>
				                    </td>
					                <td>&nbsp;                   
							                              <%
							                                  users = HandlerDBUser.getAllUsers();
							                                 if (users!=null&&users.size() > 0) {
							                                     pageContext.setAttribute("usuarios",users);
							                               %>
							                            <select name="owner" size="1" style="width: 250px; height: 19px" styleClass="classText">
							                                <option value="0">-----------------------------------------------------------</option>
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
							                    
							                    <td height="26" class="titleLeft" style="background: url(img/btn120.png); background-repeat: no-repeat" valign="middle">
			                                         <%=rb.getString("sch.normISO")%>
							                    </td>
							                    <td width="36%">
			                                         <%
			                                             norms = HandlerNorms.getAllNorms();
			                                             if (norms!=null&&norms.size() > 0) {
			                                                 pageContext.setAttribute("norms",norms);
			                                         %>
			                                             <select name="normISO" size="1" style="width: 250px; height: 19px" styleClass="classText">
			                                                 <option value="0">-----------------------------------------------------------</option>
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
							            <tr>
                                 
                   </logic:present>
                   <!-- SIMON 1 DE JUNIO DEL 2005 FIN -->
                </td>

	            <input id="public" name="public"   type="hidden" value="2"/>
            </tr>
        </logic:equal>
        <tr>
            <td colspan="3">
            </td>
            <td>
                <p align="center">
                    <input type="button" class="boton" value="<%=rb.getString("btn.search")%>" onClick="javascript:searchDocument(this.form);"/>
                    &nbsp;
                    <%if(request.getSession().getAttribute("querySearchReport")!=null) {%>
                    <input type="button" class="boton" value="<%=rb.getString("lst.reporte")%>" onClick="javascript:reporteLista();" />
                    <%}%>
                </p>
            </td>
        </tr>
    </table>
<%--</form>--%>
<br/>
<%--<logic:present name="searchDocs" >--%>
    <table align=center border=0 width="100%">
        <tr>
            <td>
                <table class="clsTableTitle" width="100%" cellSpacing="0" cellPadding="0" align=center border=0>
                    <tbody>
                        <tr>
                            <td class="td_title_bc">
                                <logic:present name="size" scope="session">
                                    <%=rb.getString("sch.totalDoc")+ " " + session.getAttribute("size")%> <%=rb.getString("sch.docTitle")%>
                                </logic:present>
                            </td>
                        </tr>
                    </tbody>
                </table>
            </td>
        </tr>
        <tr>
            <td>
                <table align=center border=0 width="100%">
                    <tr>

                        <td class="td_orange_l_b barraBlue" width="18%">
                            <%--<%=rb.getString("cbs.name")%> <a href="orderDocument.do?orderBy=name"><img border=0 src="img/asc.gif"/></a>--%>
                            <%=rb.getString("cbs.name")%> <a href="javascript:ordenar('name');"><img border=0 src="img/asc.gif"/></a>                            
                        </td>
                        <td class="td_orange_l_b barraBlue" width="20%">
                            <%=rb.getString("cbs.ubicacion")%>
                       </td>
                        <td class="td_orange_l_b barraBlue" width="10%">
                            <%--<%=rb.getString("doc.type")%> <a href="orderDocument.do?orderBy=type"><img border=0 src="img/asc.gif"/></a>--%>
                            <%=rb.getString("doc.type")%> <a href="javascript:ordenar('type');"><img border=0 src="img/asc.gif"/></a>
                        </td>
						<td class="td_orange_l_b barraBlue" width="10%">
							<%--<%=rb.getString("doc.number")%><a href="orderDocument.do?orderBy=number"><img border=0 src="img/asc.gif"/></a>--%>
                            <%=rb.getString("doc.number")%><a href="javascript:ordenar('number');"><img border=0 src="img/asc.gif"/></a>
                        </td>
                        <td class="td_orange_l_b barraBlue" width="15%">
                            <%--<%=rb.getString("doc.normISO")%><a href="orderDocument.do?orderBy=normISO"><img border=0 src="img/asc.gif"/></a>--%>
                            <%=rb.getString("doc.normISO")%><a href="javascript:ordenar('normISO');"><img border=0 src="img/asc.gif"/></a>
                        </td>
                        <td class="td_orange_l_b barraBlue" width="15%">
                            <%--<%=rb.getString("cbs.owner")%>   <a href="orderDocument.do?orderBy=owner"><img border=0 src="img/asc.gif"/></a>--%>
                            <%=rb.getString("cbs.owner")%><a href="javascript:ordenar('owner');"><img border=0 src="img/asc.gif"/></a>
                        </td>
                        <td class="td_orange_l_b barraBlue" width="6%">
                            <%--<%=rb.getString("showDoc.create")%> <a href="orderDocument.do?orderBy=create"><img border=0 src="img/asc.gif"/></a>--%>
                            <%=rb.getString("showDoc.create")%><a href="javascript:ordenar('create');"><img border=0 src="img/asc.gif"/></a>
                        </td>
                        <td class="td_orange_l_b barraBlue" width="6%">
                        	<%=rb.getString("doc.status")%><a href="javascript:ordenar('status');"><img border=0 src="img/asc.gif"/></a>
                        </td>
                    </tr>
  <!--</form>-->
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
                            PaginPage Pagingbean = ToolsHTML.getBeanPaginPage(from,Integer.parseInt(size),String.valueOf(usuarioActual.getNumRecord()));
                            String routImgs = "menu-images/";
                        %>
                        <!-- paginacion fin -->
                    
                    	<% int cont=0; %>
                    	<!-- paginacion ini -->
                        <logic:iterate id="bean" name="searchDocs" indexId="ind" offset="<%=Pagingbean.getDesde()%>" length="<%=Pagingbean.getCuantos()%>"
                        	type="com.desige.webDocuments.document.forms.BaseDocumentForm"  scope="session">
                        <!-- paginacion fin -->
                        	<tr class='fondo_<%=cont==0?cont++:cont--%>'>
                                <td class="td_gris_l">
                                    <a href="javascript:showDocument('<%=bean.getIdDocument()%>',<%=bean.getNumVer()%>)" class="ahref_b">
                                        <p align="left">
                                            <%=bean.getNameDocument()%>
                                        </p>
                                    </a>                                    
                                </td>
                                <td class="td_gris_l">
                                    <logic:notEqual name="bean" property="isflowusuario" value="-1">
                                        <a href="javascript:showCharge('<%=bean.getIsflowusuario()%>',''+'<%=rb.getString("wf.pend")%>','<%=bean.getIsNotflowsecuencial()%>')" class="ahref_b">
                                            <img src="img/talkbubble2_exclaim.gif" width="23" height="23" border="0">
                                                <%=bean.getIsflowusuario()%>
                                        </a>
                                    </logic:notEqual>
                                    <logic:equal name="bean" property="isflowusuario" value="-1">
                                        <%=bean.getRout()%>
                                    </logic:equal>
                                </td>
                                <td class="td_gris_l">
                                    <%=bean.getDescriptTypeDoc()%>
                                </td>
								<td class="td_gris_l">
                                    <%=bean.getPrefix() + bean.getNumber()%>
                                </td>
                                <td class="td_gris_l">
                                    <%=bean.getNormISODescript()%>
                                </td>
                                <td class="td_gris_l">
                                    <%=bean.getOwner()%>
                                </td>
                                <td class="td_gris_l">
                                    <%=bean.getDateCreation()%>
                                </td>
                                <td class="td_gris_l">
                                	<%=bean.getStatuDoc()%>
                                </td>
                            </tr>
                        </logic:iterate>
                        <tr>
                            <td colspan="8" align="center">
                                            <!-- paginacion ini -->
                                            <table class="paginador">
                                                <tr>
                                                    <td align="center" colspan="4">
                                                        <br/>
                                                            <font size="1" color="#000000">
                                                                <%=rb.getString("pagin.title")+ " "%>
                                                                <%=Integer.parseInt(Pagingbean.getPages())+1%>
                                                                <%=rb.getString("pagin.of")%>
                                                                <%=Pagingbean.getNumPages()%>
                                                            </font>
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <td align="center"> <img src="img/First.gif" width="24" height="24"
                                                       class="GraphicButton" title="<%=rb.getString("pagin.First")%>"
                                                       onclick="paging_OnClick(0)">
                                                    </td>
                                                    <td align="center"> <img src="img/left.gif" width="24" height="24"
                                                       class="GraphicButton" title="<%=rb.getString("pagin.Previous")%>"
                                                       onclick="paging_OnClick(<%=(Integer.parseInt(Pagingbean.getPages())-1)%>)">
                                                    </td>
                                                    <td align="center"> <img src="img/right.gif" width="24" height="24"
                                                       class="GraphicButton" title="<%=rb.getString("pagin.next")%>"
                                                       onclick="paging_OnClick(<%=(Integer.parseInt(Pagingbean.getPages())+1)%>)">
                                                    </td>
                                                    <td align="center"> <img src="img/End.gif" width="24" height="24"
                                                       class="GraphicButton" title="<%=rb.getString("pagin.Last")%>"
                                                       onclick="paging_OnClick(<%=Pagingbean.getNumPages()%>)">
                                                    </td>
                                                </tr>
                                            </table>
										    <!-- paginacion fin -->
                            </td>
                        </tr>
                    </logic:present>
                </table>
            </td>
        </tr>
    </table>
</form>
    <!-- paginacion ini -->
    <form name="formPagingPage" method="post" action="searchDocument.jsp">
      <input type="hidden" name="visible"  value="true">
      <input type="hidden" name="from"  value="">
      <input type="hidden" name="cmd"  value="<%=SuperActionForm.cmdLoad%>">
    </form>
    <!-- paginacion fin -->

<%--</logic:present>--%>
<logic:notPresent name="searchDocs" scope="session">
    <table class="clsTableTitle" width="100%" cellSpacing="0" cellPadding="0" align=center border=0>
        <tbody>
            <tr>
                <td class="td_title_bc">
                    <%=rb.getString("sch.notFound")%>
                </td>
            </tr>
        </tbody>
    </table>
</logic:notPresent>
</body>
</html>
