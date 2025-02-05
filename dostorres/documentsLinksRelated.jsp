<!--/**
 * Title: documentsLinksRelated.jsp <br>
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
                 com.desige.webDocuments.utils.Constants,
                 com.desige.webDocuments.utils.beans.SuperActionForm,
                 com.desige.webDocuments.utils.ToolsHTML,
                 com.desige.webDocuments.utils.beans.PaginPage,
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

    int level = 0;
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
    	var formaSelection = document.getElementById("selection");
        document.formPagingPage.from.value = pageFrom;
        formaSelection.input.value=document.formPagingPage.input.value
        formaSelection.value.value=document.formPagingPage.value.value
        formaSelection.nameForma.value=document.formPagingPage.nameForma.value
        validateCheck();
        document.formPagingPage.docRelations.value = formaSelection.docRelations.value;
        document.formPagingPage.desSelect.value = formaSelection.desSelect.value;
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
    	var formaSelection = document.getElementById("selection");
    	
        if (item && !item.checked) {
            if (formaSelection.desSelect.value.length > 0) {
            	formaSelection.desSelect.value = formaSelection.desSelect.value + "," + valor;
            } else {
            	formaSelection.desSelect.value = valor;
            }

            //Se Mantiene los ID Seleccionados
            if (formaSelection.docRelations.value.length > 0) {
                var it = formaSelection.docRelations.value.split(",");
                formaSelection.docRelations.value = "";
                for (i = 0 ; i < it.length ; i++) {
                    if (valor != it[i]) {
                        if (formaSelection.docRelations.value.length > 0) {
                        	formaSelection.docRelations.value = formaSelection.docRelations.value + "," + it[i];
                        } else {
                        	formaSelection.docRelations.value = it[i];
                        }
                    }
                }
            }
        } else {
            //Si se han Deseleccionado algún Valor
            //se procede a Verificar si el actual había sido previamente deseleccionado :D
            if (formaSelection.desSelect.value.length > 0) {
                var it = formaSelection.desSelect.value.split(",");
                for (i = 0 ; i < it.length ; i++) {
                    if (valor != it[i]) {
                        if (formaSelection.desSelect.value.length > 0) {
                        	formaSelection.desSelect.value = formaSelection.desSelect.value + "," + it[i];
                        } else {
                        	formaSelection.desSelect.value = it[i];
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
        forma = document.getElementById("selection");
        forma.numDoc.value = idDoc;
        forma.action = "genXLSRelations.do";
        forma.submit();
    }
    
    var nivel = 0;
	function show(idDoc,linea,level,ant) {
		nivel = level;
		if(document.getElementById("img"+linea+"_"+idDoc+"_"+level).src==minus.src){
			document.getElementById("img"+linea+"_"+idDoc+"_"+level).src=more.src;
			document.getElementById("det"+linea+"_"+idDoc+"_"+level).innerHTML="";		
			document.getElementById(linea+"_"+idDoc+"_"+level).style.display="none";		
			return;
		}
		cuadro="subNodo";
		pages="loadAllDocumentsRelatedSingle.do";
		title="Documentos";
		value="";
	    window.open(pages+"?read=true&value="+value+"&idDoc="+idDoc+"&title="+title+"&linea="+linea+"&level="+level+"&ant="+ant,cuadro);
	}

	function listo(idDoc,text,linea,level,isDatos){
		level = nivel;
		if(isDatos){
			document.getElementById("img"+linea+"_"+idDoc+"_"+level).src=minus.src;		
			document.getElementById(linea+"_"+idDoc+"_"+level).style.display="";		
			document.getElementById("det"+linea+"_"+idDoc+"_"+level).innerHTML=text;		
		} else {
			document.getElementById("img"+linea+"_"+idDoc+"_"+level).src=vacio.src;		
			document.getElementById("ancla"+linea+"_"+idDoc+"_"+level).onclick="";		
			document.getElementById("ancla"+linea+"_"+idDoc+"_"+level).style.cursor="";		
		}
	}
	
	var more = new Image();
	var minus = new Image();
	var vacio = new Image();
	more.src="menu-images/menu_corner_plus.gif";
	minus.src="menu-images/menu_corner_minus.gif";
	vacio.src="menu-images/menu_corner_empty.gif";
    
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
                <td>
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
                <td class="td_gris_l">
                    &nbsp;
                </td>
            </tr>
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
                    <logic:iterate id="document" name="documentsLinks" indexId="ind"
                                 type="com.desige.webDocuments.document.forms.DocumentsRelation" scope="session">
                    <%
                        int item = ind.intValue()+1;
                        int num = item%2;
						level++;
                    %>
                    <!--<tr class='td_<%=num%>'>-->
                    <tr class='td_<%=num%> ppaText'>
                            <td nowrap>
                            	<a id="ancla<%=item%>_<%=document.getId()%>_<%=level%>" style="cursor:pointer" onclick="show(<%=document.getId()%>,<%=item%>,<%=level%>)">
                                <img id="img<%=item%>_<%=document.getId()%>_<%=level%>" src="menu-images/menu_corner_plus.gif" align="left" border="0" vspace="0" hspace="0" width="18" height="18">
                                </a>
                                &nbsp;
                                <logic:present name="onlyRead" scope="session">
                                    <!-- Si el Usuario tiene Permiso de Impresión -->
                                    <logic:equal name="document" property="toPrintDoc" value="1">
                                    
                                        <!-- Permiso para ver el Documento jairo -->
                                        <logic:equal name="document" property="toViewDocs" value="1">
                                            <a href="javascript:showDocumentimprimir('<%=document.getId()%>','<%=document.getNumVer()%>','<%=document.getId()/*document.getNameFile()*/%>','1','QwebAnexo<%=document.getId()%>',<%=Constants.PRINTER_PDF%>)" class="ahref_b">
                                                <%=document.getNumber()%></a>
                                        </logic:equal>
                                        <!-- No tiene permiso para ver el Documento -->
                                        <logic:notEqual name="document" property="toViewDocs" value="1">
                                            <a href="javascript:seguridad();" class="ahref_b">
                                                <%=document.getNumber()%></a>
                                        </logic:notEqual>
                                    </logic:equal>
                                    <!-- Si el Usuario no tiene Permiso de Impresión -->
                                    <logic:notEqual name="document" property="toPrintDoc" value="1">
                                        <!-- Permiso para ver el Documento -->
                                        <logic:equal name="document" property="toViewDocs" value="1">
                                            <a href="javascript:showDocumentimprimir('<%=document.getId()%>','<%=document.getNumVer()%>','<%=document.getId()/*document.getNameFile()*/%>','0','QwebAnexo<%=document.getId()%>',<%=Constants.PRINTER_PDF%>)" class="ahref_b">
                                                <%=document.getNumber()%></a>
                                        </logic:equal>
                                        <!-- No tiene permiso para ver el Documento -->
                                        <logic:notEqual name="document" property="toViewDocs" value="1">
                                            <a href="javascript:seguridad();" class="ahref_b">
                                                <%=document.getNumber()%></a>
                                        </logic:notEqual>
                                    </logic:notEqual>
                                </logic:present>

                                <span style="color:blue">(<%=document.getVer()%>)</span>
                                
                                <%=document.getNameDocument()%>
                            </td>
                        </tr>
                        <tr id='<%=item%>_<%=document.getId()%>_<%=level%>' style="display:none">
	                        <td >
	                        	<table>
			                        <tr>
				                        <td style="width:30px">&nbsp;</td>
				                        <td id='det<%=item%>_<%=document.getId()%>_<%=level%>'>Documentos</td>
			                        </tr>
	                        	</table>
	                        </td>
                        </tr>
                    </logic:iterate>
               </logic:notPresent>

            </form>
            <tr>
                <td>
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
    <iframe id="subNodo" name="subNodo" class="none"></iframe>
</body>
</html>
<script type="text/javascript" event="onload" for="window">
	<%session.setAttribute("level",level);%>
</script>
