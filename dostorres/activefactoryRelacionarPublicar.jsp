<!--/**
 * Title: activefactoryRelacionarPublicar.jsp <br>
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
 * <ul>
 */-->
<%@ page import="java.util.ResourceBundle,
                 com.desige.webDocuments.utils.ToolsHTML,
                 com.desige.webDocuments.persistent.managers.HandlerDocuments"%>

<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<%@ page language="java" %>
<%
    ResourceBundle rb = ToolsHTML.getBundle(request);
    String onLoad = ToolsHTML.getMensajePages(request,rb);
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
    String ordenVersion=request.getParameter("ordenVersion")!=null?"checked":"";
    String ordenNombre=request.getParameter("ordenNombre")!=null?"checked":"";
    String ordenTypeDocument=request.getParameter("ordenTypeDocument")!=null?"checked":"";
    String ordenNumber=request.getParameter("ordenNumber")!=null?"checked":"";
    String ordenPropietario=request.getParameter("ordenPropietario")!=null?"checked":"";
    String ordenApproved=request.getParameter("ordenApproved")!=null?"checked":"";
    String accion=request.getParameter("accion")!=null?request.getParameter("accion"):"";
%>

<html>
<head>
<title><%=rb.getString("principal.title")%></title>
<jsp:include page="meta.jsp" /> 
<link href="estilo/estilo.css" rel="stylesheet" type="text/css">
<script type="text/javascript" src="./estilo/funciones.js"></script>
<script type="text/javascript">
    setDimensionScreen();

    function seguridad() {
        alert('<%=rb.getString("sch.notDocsView")%>');
    }

    function searchItem(forma,action,type) {
        if (type==0) {
            forma.toSearch.value = "";
        }
        if (forma.keys.value.length ==0 && forma.version.value.length ==0 &&
            forma.nombre.value.length==0 && forma.number.value.length ==0 &&
            forma.typeDocument.value == "0" && forma.propietario.value == "0" &&
            forma.nameRout.value.length == 0) {
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
        var hWnd = null;
        hWnd = window.open(pages+"?input="+input+"&value="+value+"&nameForma="+forma.name,"Catalogo","width=600,height=450,resizable=yes,statubar=yes,scrollbars=yes,left=200,top=150");
        if ((document.window != null) && (!hWnd.opener)){
            hWnd.opener = document.window;
        }
    }
    function showCharge(userName,charge,nosecuencial) {
        window.open("showCharge.jsp?userName="+userName+"&nosecuencial="+nosecuencial+"&charge="+charge, "WebDocuments", "resizable=no,scrollbars=yes,statusbar=yes,width=300,height=200,left=410,top=250");
    }
    
    function showDocument(idDoc) {
    	var formaSelection = document.getElementById("Selection"); 
        formaSelection.idDocument.value = idDoc;
        formaSelection.action = "showDataDocument.do";
        formaSelection.submit();
    }
    function cancelar(form){
       location.href = "loadActiveFactory.do";
    }
</script>
</head>

<body class="bodyInternas" <%=onLoad%>>


<form name="Selection" id="Selection" action="loadStructMain.do">
    <input type="hidden" name="idDocument" value=""/>
    <input type="hidden" name="showStruct" value="true"/>
    <input type="hidden" name="vengoDeActiveFactory" value="1"/>
    <input type="hidden" name="from" value="searchDocument.jsp?expand=false"/>

</form>

    <form name="search" method="post" action="documentActivefactoryRelacionar.do">
    <input type="hidden" name="accion" value="1"/>
    <table align=center border=0 width="100%">
        <tr>
            <td colspan="4">
                <table class="clsTableTitle" width="100%" cellSpacing=0 cellPadding=2 align=center border=0>
                    <tbody>
                        <tr>
                            <td class="td_title_bc" height="21">
                                <%=rb.getString("wonderware.aciveFactoryRel")%>
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
                             <%--<option value="<%=bean.getId()%>"><%=bean.getDescript()%></option>--%>
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
                    <!--<input type="hidden" name="typeDocument" value=""/>-->
                <input type="hidden" name="nameOwner" value=""/>
            </td>
        </tr>

         <tr>
             <td class="titleLeft" height="26" style="background: url(img/btn120.png); background-repeat: no-repeat" width="14%" valign="middle">
              <%=rb.getString("sch.arbol")%>
            </td>
            <td width="36%">
                &nbsp;
                <input type="text" name="nameRout"  style="width: 250 px;height: 19px" value="<%=request.getAttribute("nameRout")!=null?request.getAttribute("nameRout"):""%>"/>
                <input type="hidden" name="idRout" value="<%=request.getAttribute("idRout")!=null?request.getAttribute("idRout"):""%>"/>
                <input type="button" class="boton" value=" ... " onClick="javascript:editField('loadAllStruct.do?toSelectValue=true','estado','nameState',this.form);" style="width:20px;" />
            </td>
        </tr>

        <tr>
            <td colspan="3">
            </td>
            <td>
                <p align="center">
                   <input type="button" class="boton" value="<%=rb.getString("btn.search")%>" onClick="javascript:searchItem(document.search,'documentActivefactoryRelacionar.do',1);"/>
                    &nbsp;
                    <input type="button" class="boton" value="<%=rb.getString("btn.cancel")%>" name="btnCancelar" onClick="javascript:cancelar(this.form);"/>
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

            </td>
        </tr>
        <tr>
            <td>
                &nbsp;
            </td>
        </tr>
        <tr>
            <td>
                &nbsp;
            </td>
        </tr>
        <tr>
            <td>
                <table align=center border=0 width="100%">
                    <tr>
                        <td class="td_orange_l_b" width="20%">
                            <%=rb.getString("showDoc.Ver")%>  <input name="ordenVersion" type="checkbox" <%=ordenVersion%> onClick="javascript:searchItem(document.search,'documentActivefactoryRelacionar.do',1);"/>
                        </td>
                        <td class="td_orange_l_b" width="20%">
                            <%=rb.getString("cbs.name")%>    <input name="ordenNombre" type="checkbox" <%=ordenNombre%> onClick="javascript:searchItem(document.search,'documentActivefactoryRelacionar.do',1);"/>
                        </td>
                        <td class="td_orange_l_b" width="20%">
                            <%=rb.getString("cbs.ubicacion")%>
                        </td>
                        <td class="td_orange_l_b" width="15%">
                            <%=rb.getString("doc.type")%>   <input name="ordenTypeDocument" type="checkbox" <%=ordenTypeDocument%> onClick="javascript:searchItem(document.search,'documentActivefactoryRelacionar.do',1);"/>
                        </td>
						<td class="td_orange_l_b" width="10%">
							<%=rb.getString("doc.number")%> <input name="ordenNumber" type="checkbox" <%=ordenNumber%> onClick="javascript:searchItem(document.search,'documentActivefactoryRelacionar.do',1);"/>
						</td>
                        <td class="td_orange_l_b" width="15%">
                            <%=rb.getString("doc.links")%>
                        </td>
                        <td class="td_orange_l_b" width="20%">
                            <%=rb.getString("cbs.owner")%> <input name="ordenPropietario" type="checkbox" <%=ordenPropietario%> onClick="javascript:searchItem(document.search,'documentActivefactoryRelacionar.do',1);"/>
                        </td>
                        <td class="td_orange_l_b" width="20%">
                            <%=rb.getString("showDoc.approved")%><input name="ordenApproved" type="checkbox" <%=ordenApproved%> onClick="javascript:searchItem(document.search,'documentActivefactoryRelacionar.do',1);"/>

                        </td>
                          <td class="td_orange_l_b" width="15%">
                             <%=rb.getString("showDoc.Firmantes")%>
                        </td>
                    </tr>
        </form>

                    <logic:present name="published" scope="session">
                        <logic:iterate id="bean" name="published" scope="session" type="com.desige.webDocuments.document.forms.BaseDocumentForm">
                            <tr>
                                <td class="td_gris_l">
                                    <%=bean.getMayorVer().trim()+"."+bean.getMinorVer().trim()%>
                                    <!-- Permitir impresion a nivel de carpetas-->
                                    <!--<a href="javascript:showSolicitudImpresion('<%=bean.getIdDocument()%>',<%=bean.getNumVer()%>,'<%=bean.getIdperson()%>')" class="ahreMenu">-->
                                        <!--&nbsp;+-->
                                    <!--</a>-->
                                    <%--<logic:notEqual name="bean" property="isflowusuario" value="-1">--%>
                                      <!--<a href="javascript:showCharge('<%=bean.getIsflowusuario()%>',''+'<%=rb.getString("wf.pend")%>','<%=bean.getIsNotflowsecuencial()%>')" class="ahref_b">-->
                                         <!--<img src="img/talkbubble2_exclaim.gif" width="23" height="23" border="0">-->
                                      <!--</a>-->
                                   <%--</logic:notEqual>--%>
                                </td>
                                <td class="td_gris_l">
                                    <a href="javascript:showDocument('<%=bean.getIdDocument()%>')" class="ahref_b">
                                    <!--<a href="javascript:showDocumentPublishImp('< %=bean.getIdDocument()%>',< %=bean.getNumVer()%>,'< %=bean.getNameFile()%>','1')" class="ahref_b">-->
                                       <p align="left">
                                            <%=bean.getNameDocument()%>
                                            <%--<logic:equal name="bean" value="imprimir" property="imprimir">--%>
                                                <!--<img src="icons/printer.png" border="0">-->
                                            <%--</logic:equal>--%>
                                       </p>
                                   </a>                                
                                </td>
                                 <td class="td_gris_l">
                                    <%=bean.getRout()%>
                                </td>
                                <td class="td_gris_l">
                                    <%=bean.getDescriptTypeDoc()%>
                                </td>
								<td class="td_gris_l">
                                    <%=bean.getPrefix() + bean.getNumber()%>
                                </td>
                                <td class="td_gris_l">
                                    <a href="javascript:showDocRelations('loadAllDocuments.do','<%=rb.getString("doc.links")%>','<%=bean.getIdDocument()%>','',500,400);" class="ahref_b" target="_self">
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
                                     <img src="img/user-group-18.gif" width="23" height="23" border="0">  <%--<%=rb.getString("showDoc.Firmantes")%>--%>
                                   </a>
                                </td>

                            </tr>
                        </logic:iterate>
                    </logic:present>
                </table>
            </td>
        </tr>
    </table>
</body>
</html>

