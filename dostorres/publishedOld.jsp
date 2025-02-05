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
                 com.desige.webDocuments.utils.ToolsHTML,
                 com.desige.webDocuments.persistent.managers.HandlerDocuments,
                 com.desige.webDocuments.utils.beans.Users,
                 com.desige.webDocuments.utils.beans.SuperActionForm,
                 com.desige.webDocuments.utils.beans.PaginPage,
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
	Users users = session.getAttribute("user")!=null?(Users)session.getAttribute("user"):null;
	//paginacion fin
	
    ResourceBundle rb = ToolsHTML.getBundle(request);
    String onLoad = ToolsHTML.getMensajePages(request,rb);
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

    <script language=javascript src="./estilo/funciones.js"></script>


    <script language=javascript src="./estilo/fechas.js"></script><%-- Luis Cisneros 23-02-07 --%>

    <script language="JavaScript">
    setDimensionScreen();

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

</script>
<!-- paginacion ini -->
<script type="text/javascript">
    function paging_OnClick(pageFrom){
        document.formPagingPage.from.value = pageFrom;
        document.formPagingPage.submit();
    }
</script>
<!-- paginacion fin -->
</head>

<body class="bodyInternas" <%=onLoad%>>
<!-- Formulario para Obtener el nodo Seleccionado por el usuario -->

<%--<%--%>
<!--if ("1".equalsIgnoreCase(accion)) {-->
<%--%>--%>
<!--<a href="javascript:reporteLista();" class="ahreMenu">-->
    <%--<%=rb.getString("lst.reporte")%>--%>
<!--</a>-->
<%--<%}%>--%>

<!--<form name="reporte" action="CrearReporte.do">-->
    <%--<input type="hidden" name="queryReporte" value="<%=queryReporte%>"/>--%>
    <%--<input type="hidden" name="nombre" value="<%=request.getAttribute("nombre")!=null?request.getAttribute("nombre"):""%>"/>--%>
    <%--<input type="hidden" name="number" value="<%=request.getAttribute("number")!=null?request.getAttribute("number"):""%>"/>--%>
    <%--<input type="hidden" name="version" value="<%=request.getAttribute("version")!=null?request.getAttribute("version"):""%>"/>--%>
    <%--<input type="hidden" name="nameRout" value="<%=request.getAttribute("nameRout")!=null?request.getAttribute("nameRout"):""%>"/>--%>
    <!--<input type="hidden" name="typeDocument" value=""/>-->
    <!--<input type="hidden" name="propietario" value=""/>-->
    <%--<input type="hidden" name="keys" value="<%=request.getAttribute("keys")!=null?request.getAttribute("keys"):""%>"/>--%>
<!--</form>-->

<form name="Selection" id="Selection" action="loadStructMain.do">
    <input type="hidden" name="idDocument" value=""/>
    <input type="hidden" name="idVersion" value=""/>
    <input type="hidden" name="showStruct" value="true"/>
    <input type="hidden" name="from" value="published.jsp"/>
</form>

    <form name="search" method="post" action="showPublished.do">
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
                                    <%=rb.getString("pb.title")%>
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
                    <%=rb.getString("sch.version")%>
                </td>
                <td width="36%">
                    &nbsp;
                    <input type="text" name="version" style="width: 250 px;height: 19px" value="<%=request.getAttribute("version")!=null?request.getAttribute("version"):""%>"/>
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
                    <input type="text" name="number" style="width: 250 px;height: 19px" value="<%=request.getAttribute("number")!=null?request.getAttribute("number"):""%>"/>
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
                                    <logic:notEqual value="<%=HandlerDocuments.TypeDocumentsRegistro%>" name="bean" property="id">
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
                <td class="titleLeft" height="26" style="background: url(img/btn120.png); background-repeat: no-repeat" valign="middle">
                Propietarios
            </td>
            <td width="36%">
                &nbsp;
                <select name="propietario" size="1" style="width:250px; height: 19px" styleClass="classText">
                    <logic:present name="allUsers" scope="session">
                        <option value="0">-----------------------------------------------------------</option>
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
        <tr>
            <td class="titleLeft" height="26" style="background: url(img/btn120.png); background-repeat: no-repeat" width="14%" valign="middle">
                <%=rb.getString("sch.arbol")%>
            </td>
            <td width="36%">
                &nbsp;
                <input type="text" name="nameRout"  style="width: 250px;height: 19px" value="<%=request.getAttribute("nameRout")!=null?request.getAttribute("nameRout"):""%>"/>
                <input type="hidden" name="idRout" value="<%=request.getAttribute("idRout")!=null?request.getAttribute("idRout"):""%>"/>
                <input type="button" class="boton" value=" ... " onClick="javascript:editField('loadAllStruct.do?toSelectValue=true','estado','nameState',this.form);" style="width:20px;" />
            </td>

            <%--
                Luis Cisneros
                23-03-07
                Filtrar Por fecha de vencimiento.

            --%>
             <td class="titleLeft" height="26" style="background: url(img/btn120.png); background-repeat: no-repeat" valign="middle">
                <%=rb.getString("sch.expiresFrom")%>
            </td>
            <td width="36%">
                &nbsp;
                <input type="hidden" name="expiredFromHIDDEN" value=""></input>
                                        <input type="text" name="expiredFrom" readonly disabled maxlength="10" size="12" value="<%=request.getAttribute("expiredFromHIDDEN") == null ?"":request.getAttribute("expiredFromHIDDEN")%>" />
                                        <input type="button" onclick='getDateExpire("expiredFrom",this.form.name,this.form.expiredFrom.value,"sch.from")' value='>>' class="boton" style="width:20px;"></input>

               <input type=button onclick='this.form.expiredFromHIDDEN.value=""; this.form.expiredFrom.value="";' value='<%=rb.getString("btn.clear")%>' class="boton">              
            </td>
                   
        </tr>
            <tr>
            <td height="26">

            </td>
            <td width="36%">
              
            </td>
             <td class="titleLeft" height="26" style="background: url(img/btn120.png); background-repeat: no-repeat" valign="middle">
                <%=rb.getString("sch.expiresTo")%>
            </td>
            <td width="36%">
                          &nbsp;
                            <input type="hidden" name="expiredToHIDDEN" value=""></input>
                            <input type="text" name="expiredTo" readonly disabled maxlength="10" size="12"  value="<%=request.getAttribute("expiredToHIDDEN") == null ?"":request.getAttribute("expiredToHIDDEN")%>" />
                            <input type="button" onclick='getDateExpire("expiredTo",this.form.name,this.form.expiredTo.value,"sch.to")' value='>>' class="boton" style="width:20px;"></input>


                           <input type=button onclick='  this.form.expiredToHIDDEN.value="";  this.form.expiredTo.value="";' value='<%=rb.getString("btn.clear")%>' class="boton">
            </td>

        </tr>

            <tr>
                <td colspan="4">
                       &nbsp;
                </td>
            </tr>
              <%--
                     Fin 23-03-07
                     --%>
        <tr>
            <td colspan="3">
            </td>
            <td>
                <p align="center">
                   <input type="button" class="boton" value="<%=rb.getString("btn.search")%>" onClick="javascript:searchItem(document.search,'showPublished.do',1);" />
                    &nbsp;
                    <input type="button" class="boton" value="<%=rb.getString("lst.reporte")%>" onClick="javascript:reporteLista();" />
                </p>
            </td>
        </tr>
    </table>
    <table align=center border=0 width="100%">
        <logic:present name="size" scope="session">
            <tr>
                <td>
                    <table class="clsTableTitle" width="100%" cellSpacing="0" cellPadding="0" align=center border=0>
                        <tbody>
                            <tr>
                                <td class="td_title_bc" height="21">
                                    <%=rb.getString("sch.totalDoc") + " " + session.getAttribute("size") + " " + rb.getString("sch.totalDoc1")%>                                    
                                </td>
                          </tr>
                        </tbody>
                    </table>
                </td>
            </tr>
        </logic:present>
        <tr>
            <td>
                <table align=center border=0 width="100%">
                    <tr>
                        <td class="td_orange_l_b barraBlue" width="5%">
                            <%--<%=rb.getString("showDoc.Ver")%><a href="orderDocument.do?orderBy=ver&pages=published"><img border=0 src="img/asc.gif"/></a>--%>
                            <%=rb.getString("showDoc.Ver")%><a href="javascript:ordenar('ver');"><img border=0 src="img/asc.gif"/></a>
                        </td>
                        <td class="td_orange_l_b barraBlue" width="20%">
                            <%--<%=rb.getString("cbs.name")%><a href="orderDocument.do?orderBy=name&pages=published"><img border=0 src="img/asc.gif"/></a>--%>
                            <%=rb.getString("cbs.name")%><a href="javascript:ordenar('name');"><img border=0 src="img/asc.gif"/></a>
                        </td>
                        <td class="td_orange_l_b barraBlue" width="17%">
                            <%=rb.getString("cbs.ubicacion")%>
                        </td>
                        <td class="td_orange_l_b barraBlue" width="13%">
                            <%--<%=rb.getString("doc.type")%><a href="orderDocument.do?orderBy=type&pages=published"><img border=0 src="img/asc.gif"/></a>--%>
                            <%=rb.getString("doc.type")%><a href="javascript:ordenar('type');"><img border=0 src="img/asc.gif"/></a>
                        </td>
						<td class="td_orange_l_b barraBlue" width="10%">
							<%--<%=rb.getString("doc.number")%><a href="orderDocument.do?orderBy=number&pages=published"><img border=0 src="img/asc.gif"/></a>--%>
                            <%=rb.getString("doc.number")%><a href="javascript:ordenar('number');"><img border=0 src="img/asc.gif"/></a>
                        </td>
                        <td class="td_orange_l_b barraBlue" width="10%">
                        	<span title="<%=rb.getString("doc.links")%>">
                            	<%=rb.getString("doc.linksSmall")%>
                            </span>
                        </td>
                        <td class="td_orange_l_b barraBlue" width="15%">
                            <%--<%=rb.getString("cbs.owner")%> <a href="orderDocument.do?orderBy=owner&pages=published"><img border=0 src="img/asc.gif"/></a>--%>
                            <%=rb.getString("cbs.owner")%> <a href="javascript:ordenar('owner');"><img border=0 src="img/asc.gif"/></a>
                        </td>
                        <td class="td_orange_l_b barraBlue" width="22%">
                            <%--<%=rb.getString("showDoc.approved")%><a href="orderDocument.do?orderBy=approved&pages=published"><img border=0 src="img/asc.gif"/></a>--%>
                            <%=rb.getString("showDoc.approved")%><a href="javascript:ordenar('approved');"><img border=0 src="img/asc.gif"/></a>
                        </td>
                          <td class="td_orange_l_b barraBlue" width="15%">
                             <%=rb.getString("showDoc.Firmantes")%>
                        </td>
                    </tr>
                    <!--</table>-->
                <!--</td>-->
            <!--</tr>-->
        <!--</form>-->
                    <logic:notPresent name="published" scope="session">
                        <tr>
                            <td colspan="9">
                                <table class="clsTableTitle" width="100%" cellSpacing="0" cellPadding="0" align=center border=0>
                                    <tbody>
                                        <tr>
                                            <td class="td_title_bc" height="21">
                                                <%=rb.getString("sch.notFound")%>
                                            </td>
                                      </tr>
                                    </tbody>
                                </table>
                            </td>
                        </tr>
                    </logic:notPresent>
                    <logic:present name="published" scope="session">


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
                            PaginPage Pagingbean = ToolsHTML.getBeanPaginPage(from,Integer.parseInt(size),String.valueOf(users.getNumRecord()));
                            String routImgs = "menu-images/";
                        %>
                        <!-- paginacion fin -->
                    
                    
                    
                    	<% int cont=0; %>
                    	<% int contImg=0; %>
                    	<!-- paginacion ini -->
                        <logic:iterate id="bean" name="published" indexId="ind" offset="<%=Pagingbean.getDesde()%>" length="<%=Pagingbean.getCuantos()%>"
                        	type="com.desige.webDocuments.document.forms.BaseDocumentForm"  scope="session">
                        <!-- paginacion fin -->
                        	<% contImg++; %>
                            <tr class='fondo_<%=cont==0?cont++:cont--%>'>
                                <td class="td_gris_l">
                                    <%=bean.getMayorVer().trim()+"."+bean.getMinorVer().trim()%>
                                    <!-- Permitir impresion a nivel de carpetas-->
                                    <a href="javascript:showSolicitudImpresion('<%=bean.getIdDocument()%>',<%=bean.getNumVer()%>,'<%=bean.getIdperson()%>')" class="ahreMenu">
                                        &nbsp;+
                                    </a>
                                    <logic:notEqual name="bean" property="isflowusuario" value="-1">
                                      <a href="javascript:showCharge('<%=bean.getIsflowusuario()%>',''+'<%=rb.getString("wf.pend")%>','<%=bean.getIsNotflowsecuencial()%>')" class="ahref_b">
                                         <img src="img/talkbubble2_exclaim.gif" width="23" height="23" border="0">
                                      </a>
                                   </logic:notEqual>
                                </td>
                                <td class="td_gris_l">
                                    <%--<a href="javascript:showDocumentPublishImp('<%=bean.getIdDocument()%>',<%=bean.getNumVer()%>,'<%=bean.getNameFile()%>','1')" class="ahref_b">--%>
                                    <a href="#" onclick="javascript:showDocumentPublishImp('<%=bean.getIdDocument()%>',<%=bean.getNumVer()%>,'<%=bean.getIdDocument()%>','1')<%= Constants.PRINTER_PDF && bean.getImprimir().equalsIgnoreCase("imprimir") ? ";document.getElementById('imgImpresora"+contImg+"').style.display='none'" : "" %>" class="ahref_b">
                                     <p align="left">
                                            <%=bean.getNameDocument()%>
                                            <logic:equal name="bean" value="imprimir" property="imprimir">
                                                <img id="imgImpresora<%=contImg%>" src="icons/printer.png" border="0">
                                            </logic:equal>
                                       </p>
                                   </a>                                
                                </td>
                                <!--
                                <td class="td_gris_l">
                                 	<% carpetas= String.valueOf(bean.getRout().replace((char)92,'/')).split("/"); %>
                                    <span title="<%=bean.getRout().replace((char)92,'/')%>" onclick="this.innerHTML='<%=bean.getRout().replace((char)92,'/')%>'" style="cursor:pointer">
                                    	<%= carpetas[carpetas.length-1].concat(bean.getRout().length()>15?"...":"")%>
                                    </span>
<%--                                   <%= HandlerStruct.getRout(bean.getNameDocument(),bean.getIdNode())%>--%>
                                </td>
                                 -->
                                <td class="td_gris_l">
				                    <logic:present name="showLink" scope="session">
				                    <logic:equal name="showLink" value="true">
	                                    <a href="javascript:showDocument('<%=bean.getIdDocument()%>',<%=bean.getNumVer()%>)" class="ahref_b">
	                                    <%=bean.getRout()%>
										</a>
				                    </logic:equal>
				                    <logic:notEqual name="showLink" value="true">
	                                    <%=bean.getRout()%>
				                    </logic:notEqual>
	                                </logic:present>
	                                
	                                <logic:notPresent name="showLink" scope="session">
	                                    <%=bean.getRout()%>
	                                </logic:notPresent>
                                </td>
                                <!-- 
                                <td class="td_gris_l">
                                    <span title="<%=bean.getDescriptTypeDoc()%>" onclick="this.innerHTML='<%=bean.getDescriptTypeDoc()%>'" style="cursor:pointer">
                                    	<%=bean.getDescriptTypeDoc().substring(0,bean.getDescriptTypeDoc().length()>10?10:bean.getDescriptTypeDoc().length()).concat(bean.getDescriptTypeDoc().length()>10?"...":"")%>
                                    </span>
                                </td>
                                 -->
                                <td class="td_gris_l">
                                   	<%=bean.getDescriptTypeDoc()%>
                                </td>
								<td class="td_gris_l">
                                    <%=bean.getPrefix() + bean.getNumber()%>
                                </td>
                                <td class="td_gris_l">
                                    <a href="javascript:showDocRelations('loadAllDocuments.do','<%=rb.getString("doc.links")%>','<%=bean.getIdDocument()%>','genXLS',500,400);" class="ahref_b" target="_self">
                                        <%=rb.getString("showDoc.show")%>
                                    </a>
                                </td>
                                <td class="td_gris_l">
                                    <%=bean.getOwner()%>
                                </td>
                                <td class="td_gris_l">
                                    <%=bean.getDateApproved()%>
                                </td>
                                <td class="td_gris_l">
                                   <a href="javascript:showDocumentFirmantes('<%=bean.getIdDocument()%>',<%=bean.getNumVer()%>,'<%=rb.getString("cbs.name")%>')" class="ahref_b">
                                     <img src="img/user-group-18.gif" width="23" height="23" border="0">
                                   </a>
                                </td>
                            </tr>
                        </logic:iterate>
                        <tr>
                            <td colspan="9" align="center">
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
    <form name="formPagingPage" method="post" action="published.jsp">
      <input type="hidden" name="from"  value="">
      <input type="hidden" name="cmd"  value="<%=SuperActionForm.cmdLoad%>">
    </form>
    <!-- paginacion fin -->
    
</body>
</html>

