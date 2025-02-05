<!--/**
 * Title: documentsLinksRelatedSingle.jsp <br>
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
    boolean isDatos=false;
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
    level = Integer.parseInt(String.valueOf(session.getAttribute("level")));
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
    	var formaSelection = document.getElementById("selection");
    	
        if (!validateCheck()){
            if (formaSelection.docRelations.value == '') {
                setValue('');    
            }
            //se hace con la finalidad de refrescar los scripts, asi no c halla seleccionado nada
            //en caso de deseleccionarlos, el actualiza , antes no lo hacia porque no staba refrescando
            paging_OnClick("1");
            window.close();
        } else {
            paging_OnClick("1");       
            setValue(formaSelection.docRelations.value);
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

</script>
</head>
<body class="bodyInternas" >

    <logic:present name="documentsLinks" scope="session">
        <%
            String from = request.getParameter("from");
            String size = (String)session.getAttribute("size");
            Users users = (Users)session.getAttribute("user");
            PaginPage Pagingbean = ToolsHTML.getBeanPaginPage(from,Integer.parseInt(size),String.valueOf(users.getNumRecord()));
        %>
         <table cellSpacing=0 cellPadding=0 align=center border=0 width="100%">

                <logic:notPresent name="noHayBusqueda" scope="request">
                    <logic:iterate id="document" name="documentsLinks" indexId="ind"
                                 type="com.desige.webDocuments.document.forms.DocumentsRelation" scope="session">
                    <%
                        int item = ind.intValue()+1;
                        int num = item%2;
						level++;
						isDatos=true;		
						boolean isAnt = String.valueOf(document.getId()).equals(request.getParameter("ant"));
                    %>
                    <tr class='td_<%=num%> ppaText' style="background-color: transparent;">
                            <td nowrap>
                            	<a  id="ancla<%=request.getParameter("linea")%>_<%=document.getId()%>_<%=level%>"
                            		<%if(!isAnt){%>
                            		style="cursor:pointer" 
                            		onclick="show(<%=document.getId()%>,<%=request.getParameter("linea")%>,<%=level%>,<%=request.getParameter("idDoc")%>)"
                            		<%}%>
                            	>
                                <img id="img<%=request.getParameter("linea")%>_<%=document.getId()%>_<%=level%>" src="menu-images/menu_corner_<%=isAnt?"empty":"plus"%>.gif" align="left" border="0" vspace="0" hspace="0" width="18" height="18">
                                </a>
                                &nbsp;
                                <logic:present name="onlyRead" scope="session">
                                    <logic:equal name="document" property="toPrintDoc" value="1">
                                    
                                        <logic:equal name="document" property="toViewDocs" value="1">
                                            <a href="javascript:showDocumentimprimir('<%=document.getId()%>','<%=document.getNumVer()%>','<%=document.getNameFile()%>','1','QwebAnexo<%=document.getId()%>',<%=Constants.PRINTER_PDF%>)" class="ahref_b">
                                                <%=document.getNumber()%></a>
                                        </logic:equal>
                                        <logic:notEqual name="document" property="toViewDocs" value="1">
                                            <a href="javascript:seguridad();" class="ahref_b">
                                                <%=document.getNumber()%></a>
                                        </logic:notEqual>
                                    </logic:equal>
                                    <logic:notEqual name="document" property="toPrintDoc" value="1">
                                        <logic:equal name="document" property="toViewDocs" value="1">
                                            <a href="javascript:showDocumentimprimir('<%=document.getId()%>','<%=document.getNumVer()%>','<%=document.getNameFile()%>','0','QwebAnexo<%=document.getId()%>',<%=Constants.PRINTER_PDF%>)" class="ahref_b">
                                                <%=document.getNumber()%></a>
                                        </logic:equal>
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
                        <tr id='<%=request.getParameter("linea")%>_<%=document.getId()%>_<%=level%>' style="display:none">
	                        <td >
	                        	<table>
			                        <tr>
				                        <td style="width:30px">&nbsp;</td>
				                        <td id='det<%=request.getParameter("linea")%>_<%=document.getId()%>_<%=level%>'>Documentos</td>
			                        </tr>
	                        	</table>
	                        </td>
                        </tr>
                    </logic:iterate>
               </logic:notPresent>
        </table>
    </logic:present>
</body>
</html>
<script type="text/javascript" event="onload" for="window">
	<%session.setAttribute("level",level);%>
	window.parent.listo(<%=request.getParameter("idDoc")%>, document.body.innerHTML,<%=request.getParameter("linea")%>,<%=level%>,<%=isDatos%>);
</script>
