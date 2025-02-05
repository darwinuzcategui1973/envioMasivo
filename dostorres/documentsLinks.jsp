<!--/**
 * Title: documentsLinks.jsp <br>
 * Copyright: (c) 2003 Consis International<br>
 * Company: Consis International (CON)<br>
 * @author Nelson Crespo (NC)
 * @version Acsel-e v2.2
 * <br>
 * Changes:<br>
 * <ul>
 *      <li> 2005-11-10 (NC) Bug in check method Fixed </li>
 *      <li> 2006-06-30 (NC) Cambios para mostrar los Documentos Vinculados </li>
 * <ul>
 */-->

<%@ page import="java.util.ResourceBundle,
                 com.desige.webDocuments.utils.beans.SuperActionForm,
                 com.desige.webDocuments.utils.ToolsHTML,
                 com.desige.webDocuments.utils.beans.PaginPage,
                 com.desige.webDocuments.utils.Constants,
                 com.desige.webDocuments.utils.beans.Users"%>

<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<%@ page language="java" %>
<%
    ResourceBundle rb = ToolsHTML.getBundle(request);
    StringBuffer onLoad = new StringBuffer("");
    String ok = (String)session.getAttribute("usuario");
    String info = (String)request.getAttribute("info");
    if (ToolsHTML.checkValue(ok)) {
        onLoad.append(" onLoad=\"self.status='").append(rb.getString("pagin.welcome")).append(" ");
        onLoad.append(ok).append("'");
    } else {
        response.sendRedirect(rb.getString("href.logout"));
    }
    if (ToolsHTML.checkValue(info)) {
        onLoad.append(";alert('").append(info).append("')");
    }
    if (onLoad.length()>0) {
        onLoad.append("\"");
    }
    String inputReturn = (String)session.getAttribute("input");
    if (inputReturn==null) {
        inputReturn = "";
    }
    String valueReturn = (String)session.getAttribute("value");
    if (valueReturn==null) {
        valueReturn = "";
    }
    String numero=request.getParameter("numero")!=null?request.getParameter("numero"):"";
    if (ToolsHTML.isEmptyOrNull(numero)) {
        numero = "";
    }

//    Luis Cisneros
//    24-03-07
//    Busqueda por prefijo y nombre
    String prefijo=request.getParameter("prefijo")!=null?request.getParameter("prefijo"):"";
    if (ToolsHTML.isEmptyOrNull(prefijo)) {
        prefijo = "";
    }

    String nombre=request.getParameter("nombre")!=null?request.getParameter("nombre"):"";
    if (ToolsHTML.isEmptyOrNull(nombre)) {
        nombre = "";
    }

    String docRelations = (String)request.getAttribute("docRelations");//request.getParameter("docRelations");
    if (ToolsHTML.isEmptyOrNull(docRelations)) {
        docRelations = "";
    }
%>
<html>
<head>
<title><%=rb.getString("principal.title")%></title>
<jsp:include page="meta.jsp" /> 
<link href="estilo/estilo.css" rel="stylesheet" type="text/css">
<script type="text/javascript" src="./estilo/funciones.js"></script>
<script type="text/javascript">
    setDimensionScreen();
    function setValue(id) {
        <%=inputReturn%>
         window.close();
    }

    function seguridad() {
        alert('<%=rb.getString("sch.notDocsView")%>');
    }

    function cancelar(){
        window.close();
    }

    function check() {
    	var forma = document.getElementById("selection");
    	
    	if (!validateCheck()){
    	    if (forma.docRelations.value == '') {
                setValue('');    
            }
            //se hace con la finalidad de refrescar los scripts, asi no c halla seleccionado nada
            //en caso de deseleccionarlos, el actualiza , antes no lo hacia porque no staba refrescando
            paging_OnClick("1");
            window.close();
        } else {
            paging_OnClick("1");       
            setValue(forma.docRelations.value);
        }
    }

    function validateCheck() {
    	var formaSelection = document.getElementById("selection");
        var set = false;
        var valueSelect = false;
        var it = new Array();
        it = formaSelection.desSelect.value.split(",");
        for (var i=0; i < formaSelection.length;i++) {
            if (formaSelection.elements[i].checked){
                msg = formaSelection.elements[i].value;
                if (formaSelection.docRelations.value.length > 0) {
                	formaSelection.docRelations.value += "," + msg;
                } else {
                	formaSelection.docRelations.value = msg;
                }
                valueSelect = true;
            }
        }
        return valueSelect;
    }

    function paging_OnClick(pageFrom) {
    	var formSelection = document.getElementById("selection");
    	
    	document.formPagingPage.from.value = pageFrom;
        formSelection.input.value=document.formPagingPage.input.value
        formSelection.value.value=document.formPagingPage.value.value
        formSelection.nameForma.value=document.formPagingPage.nameForma.value
        validateCheck();
        document.formPagingPage.docRelations.value = formSelection.docRelations.value;
        document.formPagingPage.desSelect.value = formSelection.desSelect.value;
        document.formPagingPage.action = "loadAllDocuments.do";
        document.formPagingPage.submit();
    }

    function toSearch(value1,value2, value3, forma) {
        forma.numero.value = value1;
        forma.prefijo.value = value2;
        forma.nombre.value = value3;

        validateCheck();
        if (forma.docRelations.value.length > 0) {
            forma.aux.value = forma.docRelations.value;
        }
        forma.action = "loadAllDocuments.do";
        forma.submit();
    }

    function desSelectValue(item,valor) {
    	var forma = document.getElementById("selection");
        if (item && !item.checked) {
            if (forma.desSelect.value.length > 0) {
                forma.desSelect.value = forma.desSelect.value + "," + valor;
            } else {
                forma.desSelect.value = valor;
            }

            //Se Mantiene los ID Seleccionados
            if (forma.docRelations.value.length > 0) {
                var it = forma.docRelations.value.split(",");
                forma.docRelations.value = "";
                for (i = 0 ; i < it.length ; i++) {
                    if (valor != it[i]) {
                        if (forma.docRelations.value.length > 0) {
                            forma.docRelations.value = forma.docRelations.value + "," + it[i];
                        } else {
                            forma.docRelations.value = it[i];
                        }
                    }
                }
            }
        } else {
            //Si se han Deseleccionado algún Valor
            //se procede a Verificar si el actual había sido previamente deseleccionado :D
            if (forma.desSelect.value.length > 0) {
                var it = forma.desSelect.value.split(",");
                for (i = 0 ; i < it.length ; i++) {
                    if (valor != it[i]) {
                        if (forma.desSelect.value.length > 0) {
                            forma.desSelect.value = forma.desSelect.value + "," + it[i];
                        } else {
                            forma.desSelect.value = it[i];
                        }
                    }
                }
            }
        }
    }
    
    function checkKey(evt,value1,forma) {
        var charCode = (evt.which) ? ect.which : event.keyCode;
        if (charCode == 13) {
            toSearch(value1,forma);
        }
    }
    function genExcel(idDoc) {
        forma = document.getElementById("selection");;
        forma.numDoc.value = idDoc;
        forma.action = "genXLSRelations.do";
        forma.submit();
    }
</script>
</head>
<body class="bodyInternas" <%=onLoad%>>
    <logic:present name="documentsLinks" scope="session">
        <%
            String from = request.getParameter("from");
            String size = (String)session.getAttribute("size");
            Users users = (Users)session.getAttribute("user");
            PaginPage Pagingbean = ToolsHTML.getBeanPaginPage(from,Integer.parseInt(size),String.valueOf(users.getNumRecord()));
        %>
         <table cellSpacing=0 cellPadding=0 align=center border=0 width="100%">
            <tr>
                <td colspan="3">
                    <table class="clsTableTitle" width="100%" cellSpacing=0 cellPadding=2 align=center border=0>
                        <tbody>
                            <tr>
                                <td class="td_title_bc">
                                    <%=request.getParameter("title")!=null?request.getParameter("title"):rb.getString("doc.docAprroveds")%>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </td>
            </tr>
            <tr>
                <td class="td_gris_l" colspan="3">
                    &nbsp;
                </td>
            </tr>
            <logic:notPresent name="onlyRead" scope="session">
                <form name="search" action="">
                    <tr>
                      <td colspan="3">
                          <table>
                              <tr>
                                 <td class="td_gris_l">
                                   <%=rb.getString("doc.correlativonum")%>
                                 </td>
                                  <td class="td_gris_l">
                                   <%=rb.getString("doc.prefix")%>
                                 </td>
                                  <td class="td_gris_l">
                                   <%=rb.getString("doc.name")%>
                                 </td>
                              </tr>

                               <tr>
                                 <td>
                                   <input type="text" name="buscar1" value="<%=numero%>" onkeypress="javascript:checkKey(event,this.form.buscar1.value,document.getElementById('selection'));" />
                                 </td>
                                  <td>
                                    <input type="text" name="buscar2" value="<%=prefijo%>" onkeypress="javascript:checkKey(event,this.form.buscar1.value,document.getElementById('selection'));" />
                                 </td>
                                  <td>
                                     <input type="text" name="buscar3" value="<%=nombre%>" onkeypress="javascript:checkKey(event,this.form.buscar1.value,document.getElementById('selection'));" />
                                 </td>
                                   <td> <input type="button" class="boton" value="<%=rb.getString("btn.search")%>" onClick="javascript:toSearch(this.form.buscar1.value, this.form.buscar2.value,this.form.buscar3.value, document.getElementById('selection'));" />
                                 </td>
                              </tr>
                          </table>

                      </td>
                    </tr>
                    <logic:present name="noHayBusqueda" scope="request">
                        <tr>
                            <td colspan="3">
                                <%=rb.getString("doc.busqfallida")%>
                            </td>
                        </tr>
                    </logic:present>
                </form>   
            </logic:notPresent>
            <logic:notPresent name="noHayBusqueda" scope="request">
                <tr>
                    <td class="td_orange_l_b" width="35%">
                        <%=rb.getString("doc.number")%>
                    </td>
                    <td class="td_orange_l_b" width="10%">
                        <%=rb.getString("showDoc.Ver")%>
                    </td>
                    <td class="td_orange_l_b" width="*">
                        <%=rb.getString("doc.name")%>
                    </td>
                </tr>
            </logic:notPresent>
            <!--</form>-->
            <form name="selection" id="selection">
                <input type="hidden" name="numero" value="<%=numero%>"/>
                <input type="hidden" name="nombre" value="<%=nombre%>"/>
                <input type="hidden" name="prefijo" value="<%=prefijo%>"/>
                <input type="hidden" name="docRelations" value="<%=docRelations%>"/>
                <input type="hidden" name="input" value="<%=request.getParameter("input")%>"/>
                <input type="hidden" name="value" value="<%=request.getAttribute("value")!=null?request.getAttribute("value"):request.getParameter("value")%>"/>
                <input type="hidden" name="aux" value="<%=request.getParameter("aux")%>"/>
                <input type="hidden" name="nameForma" value="<%=request.getParameter("nameForma")%>"/>
                <input type="hidden" name="desSelect" value=""/>
                <input type="hidden" name="numDoc" value="<%=request.getParameter("idDoc")%>"/>
                <logic:notPresent name="noHayBusqueda" scope="request">
                    <logic:iterate id="document" name="documentsLinks" indexId="ind" offset="<%=Pagingbean.getDesde()%>" length="<%=Pagingbean.getCuantos()%>"
                                 type="com.desige.webDocuments.document.forms.DocumentsRelation" scope="session">
                    <%
                        int item = ind.intValue()+1;
                        int num = item%2;
                    %>
                    <tr class='td_<%=num%>'>
                            <td>
                                <logic:notPresent name="onlyRead" scope="session">
                                    <input name="docLinks" onClick="javascript:desSelectValue(this,this.value);" type="checkbox" value="<%=document.getId()%>_<%=document.getNumVer()%>" <%=document.isSelected()?"checked":""%>>
                                    <%=document.getNumber()%>
                                </logic:notPresent>
                                <logic:present name="onlyRead" scope="session">
                                    <!-- Si el Usuario tiene Permiso de Impresión -->
                                    <logic:equal name="document" property="toPrintDoc" value="1">
                                    
                                        <!-- Permiso para ver el Documento jairo -->
                                        <logic:equal name="document" property="toViewDocs" value="1">
                                            <a href="javascript:showDocumentimprimir('<%=document.getId()%>','<%=document.getNumVer()%>','<%=document.getNameFile()%>','1','Qweb',<%=Constants.PRINTER_PDF%>)" class="ahref_b">
                                                <%=document.getNumber()%>
                                            </a>
                                        </logic:equal>
                                        <!-- No tiene permiso para ver el Documento -->
                                        <logic:notEqual name="document" property="toViewDocs" value="1">
                                            <a href="javascript:seguridad();" class="ahref_b">
                                                <%=document.getNumber()%>
                                            </a>
                                        </logic:notEqual>
                                        
                                        
                                    </logic:equal>
                                        
                                        
                                        
                                    <!-- Si el Usuario no tiene Permiso de Impresión -->
                                    <logic:notEqual name="document" property="toPrintDoc" value="1">
                                        <!-- Permiso para ver el Documento -->
                                        <logic:equal name="document" property="toViewDocs" value="1">
                                            <a href="javascript:showDocumentimprimir('<%=document.getId()%>','<%=document.getNumVer()%>','<%=document.getNameFile()%>','0','Qweb',<%=Constants.PRINTER_PDF%>)" class="ahref_b">
                                                <%=document.getNumber()%>
                                            </a>
                                        </logic:equal>
                                        <!-- No tiene permiso para ver el Documento -->
                                        <logic:notEqual name="document" property="toViewDocs" value="1">
                                            <a href="javascript:seguridad();" class="ahref_b">
                                                <%=document.getNumber()%>
                                            </a>
                                        </logic:notEqual>
                                    </logic:notEqual>
                                </logic:present>
                            </td>
                            <td>
                                <%=document.getVer()%>
                            </td>
                            <td>
                                <%=document.getNameDocument()%>
                            </td>
                        </tr>
                    </logic:iterate>
               </logic:notPresent>

                <logic:notPresent name="noHayBusqueda" scope="request">
                <%
                    if (size.compareTo("0") > 0) {
                %>
                        <tr>
                            <td align="center" colspan="3">
                                <!-- Inicio de Paginación -->
                                <table class="paginador">
                                  <tr>
                                    <td align="center" colspan="4">
                                      <br>
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
                                <!-- Fin de Paginación -->
                            </td>
                        </tr>
                        <tr>
                            <td colspan="3" class="td_gris_l">
                                &nbsp;
                            </td>
                        </tr>
                    <%
                        }
                    %>
                </logic:notPresent>
            </form>
            <form name="formPagingPage" id="formPagingPage" method="post" action="documentsLinks.jsp">
                <logic:present name="onlyRead" scope="session">
                    <input type="hidden" name="read" value="<%=request.getParameter("read")%>"/>
                    <input type="hidden" name="idDoc" value="<%=request.getParameter("idDoc")%>"/>
                </logic:present>
                <input type="hidden" name="from"  value="">
                <input type="hidden" name="cmd"  value="<%=SuperActionForm.cmdLoad%>">
                <input type="hidden" name="docRelations"  value="<%=docRelations%>">

                <input type="hidden" name="numero" value="<%=numero%>"/>
                <input type="hidden" name="prefijo" value="<%=prefijo%>"/>
                <input type="hidden" name="nombre" value="<%=nombre%>"/>

                <input type="hidden" name="input" value="<%=request.getParameter("input")%>"/>
                <input type="hidden" name="value" value="<%=request.getParameter("value")%>"/>
                <input type="hidden" name="nameForma" value="<%=request.getParameter("nameForma")%>"/>
                <input type="hidden" name="desSelect" value=""/>
            </form>         
            <tr>
                <td colspan="3">
                    <center>
                        <logic:notPresent name="onlyRead" scope="session">
                            <input type="button" class="boton" value="<%=rb.getString("btn.ok")%>" onClick="javascript:check();" />
                            &nbsp;
                        </logic:notPresent>     
                        <logic:present name="genXLS" scope="session">
                            <!-- Botón para Generar el Reporte en Excel de los Documentos Vinculados al Documento -->
                            <input type="button" class="boton" value="<%=rb.getString("lst.reporte")%>" onClick="javascript:genExcel('<%=request.getParameter("idDoc")%>');" />
                            &nbsp;
                        </logic:present>
                        <input type="button" class="boton" value="<%=rb.getString("btn.cancel")%>" onClick="javascript:cancelar();" />
                    </center>
                </td>
            </tr>
        </table>
    </logic:present>
    <logic:notPresent name="documentsLinks" scope="session">
        <table cellSpacing=0 cellPadding=0 align=center border=0 width="100%">
            <tr>
                <td colspan="2">
                    &nbsp;
                </td>
            </tr>
            <tr>
                <td colspan="2">
                    <logic:notPresent name="onlyRead" scope="session">
                        <table class="clsTableTitle2" width="100%" cellSpacing=0 cellPadding=2 align=center border=0>
                            <tbody>
                                <tr>
                                    <td class="td_title_bc" height="22">
                                        <%=rb.getString("doc.notPermitions")%>                                        
                                    </td>
                                </tr>
                            </tbody>
                        </table>
                    </logic:notPresent>
                    <logic:present name="onlyRead" scope="session">
                        <table class="clsTableTitle" width="100%" cellSpacing=0 cellPadding=2 align=center border=0>
                            <tbody>
                                <tr>
                                    <td class="td_title_bc" height="22">                                                                                                                                                                                                                
                                        <%=rb.getString("doc.notLinks")%>
                                    </td>
                                </tr>
                            </tbody>
                        </table>
                    </logic:present>
                </td>
            </tr>
        </table>
    </logic:notPresent>
</body>
</html>
