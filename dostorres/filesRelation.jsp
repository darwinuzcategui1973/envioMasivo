<!-- filesRelation.jsp -->
<%@ page import="java.util.ResourceBundle,
                 com.desige.webDocuments.enlaces.forms.UrlsActionForm,
                 com.desige.webDocuments.utils.beans.SuperActionForm,
                 com.desige.webDocuments.utils.beans.Users,
                 sun.jdbc.rowset.CachedRowSet,
                 java.util.List,
                 com.desige.webDocuments.bean.ExpedienteRequest,
                 com.desige.webDocuments.utils.ToolsHTML"%>
                 
<%@ page import="com.desige.webDocuments.persistent.managers.HandlerBD" %>

<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<%@ page language="java" %>
<%
    ResourceBundle rb = ToolsHTML.getBundle(request);
    StringBuffer onLoad = new StringBuffer("");
    Users usuario = (Users)session.getAttribute("user");
    String ok = (String) ToolsHTML.getAttributeSession(session, "usuario", false);
    String info = (String) ToolsHTML.getAttribute(request, "info", true);
    String dateSystem = ToolsHTML.sdf.format(new java.util.Date());
    
    List<ExpedienteRequest> lista = (List<ExpedienteRequest>)request.getAttribute("lista");
    boolean isOwnerFile = (boolean) request.getAttribute("isOwnerFile");
    
    String listDocument="";
	if(!ToolsHTML.isEmptyOrNull((String)request.getAttribute("listDocument"))){
		listDocument=String.valueOf(request.getAttribute("listDocument"));
	}    
%>
<html>
<head>
<title><%=rb.getString("principal.title")%></title>
<jsp:include page="meta.jsp" /> 
<link href="estilo/estilo.css" rel="stylesheet" type="text/css">
<script type="text/javascript" src="estilo/funciones.js"></script>
<script type="text/javascript">
var listDocument=new Array();
<%if(!listDocument.trim().equals("")){%>
	listDocument="<%=listDocument%>".split(",");
<%}%>

function agregar() {
	abrirVentana("searchDocument.do?expand=false&popup=true",1000,600);
}

function addDocument(idDocument) {
	var isFound=false;
	for(var i=0;i<listDocument.length;i++){
		if(listDocument[i]==idDocument){
			isFound=true;
			listDocument.splice(i,1);
			break;
		}
	}
	if(!isFound){
		listDocument[listDocument.length]=idDocument;
	}
	//alert(listDocument);
}

function getListDocument(){
	return listDocument;
}
function getObjectListDocument(){
	return document.forma.listDocument.value;
}

function notSaveRelation(){
	listDocument=document.forma.listDocument.value.split(",");
	document.forma.submit();
}
function saveRelation(){
	document.forma.listDocument.value=getListDocument();
	document.forma.submit();
}
function borrar(){
	var entro=false;
	var anterior=listDocument.join(",");
	if(document.forma.deleteRow.length>1){
		document.forma.eliminar.value="true";
		for(var k=1;k<document.forma.deleteRow.length;k++){
			for(var m=0;m<listDocument.length;m++){
				if(document.forma.deleteRow[k].checked && listDocument[m]==document.forma.deleteRow[k].value){
					listDocument.splice(m,1);
					entro=true;
					break;
				}
			}
		}
		if(entro){
			if(confirm("<bean:message key="areYouSure"/>")){
				document.forma.listDocument.value=getListDocument();
				document.forma.submit();
			} else {
				listDocument=anterior.split(",");
			}
		} else {
			alert("<bean:message key="wf.selecItem"/>");
		}
	}
}
function cancelar(){
	document.forma.action="filesView.do";
	document.forma.submit();
}
var colAntOnClick="";
function organizar(id){
	var sel=document.getElementById("selector");
	var col=document.getElementById("td_"+id);
	var valor=col.innerHTML;
	col.innerHTML="";
	col.appendChild(sel);
	if(sel.name!='selector'){
		document.getElementById("td_"+sel.name).innerHTML=sel.value;
		document.getElementById("td_"+sel.name).onclick=colAntOnClick;
	}
	colAntOnClick=col.onclick;
	col.onclick="";
	sel.value=valor;
	sel.name=id;
}

function ordenar() {
	var valor=document.getElementById("selector").name;
	var pos=parseInt(document.getElementById("selector").value)-1;
	for(var m=0;m<listDocument.length;m++){
		if(listDocument[m]==valor){
			listDocument.splice(m,1);
			break;
		}
	}
	listDocument.splice(pos,0,valor);
	saveRelation();
}

</script>
</head>
<body class="bodyInternas" <%=onLoad.toString()%>>
<form name="forma" action="filesSaveRelation.do">
	<input type="hidden" name="f1" value="<%=request.getAttribute("f1")%>"/>
	<input type="hidden" name="listDocument" value="<%=listDocument%>"/>
	<input type="hidden" name="eliminar" value="false"/>
	<span class="none">
		<input type="checkbox" name="deleteRow" value="">
		<select id="selector" name="selector" onchange="ordenar()" >
			<%for(int i=0;i<lista.size();i++){%>
			<option value="<%=i+1%>"><%=i+1%></option>
			<%}%>
		</select>
	</span>
	
<center>
<span class="ppalTextBig" style="color:blue;">Codigo Interno Nro.&nbsp;</span> 
<span class="ppalTextBoldBig" style="color:red;"><%=request.getAttribute("f1")%></span>
</center>
<table cellSpacing="1" cellPadding="1" align="center" border="0" width="100%" >
	<tr  class="barraBlue">
		<td>&nbsp;</td>
		<td><bean:message key="files.name"/></td>
		<td><bean:message key="files.verbose"/></td>
		<td><bean:message key="files.type"/></td>
		<td><bean:message key="files.id"/></td>
		<td><bean:message key="files.owner"/></td>
		<td><bean:message key="files.status"/></td>
		<td><bean:message key="files.order"/></td>
	</tr>
	<%int color=0;
	for(ExpedienteRequest item: lista){%>
	<tr class="fondo_<%=(color=(color==0?1:0))%> texto">
		<td><input type="checkbox" name="deleteRow" style="height:15px" value="<%=item.getNumgen()%>"></td>
		<td>
			<span style="color:green;font-weight:bold;font-family: Sans-serif, Helvetica, Arial, Verdana;">
				<% if(!item.isPdfGenerated()) { %>
					<img src="img/page_white_acrobat_delete.png" alt="<bean:message key="files.document.pdf.notexist"/>" title="<bean:message key="files.document.pdf.notexist"/>">
				<%}%>
				<% if(item.getStatuVer().equals("1") || !item.getStatu().equals("5")) { %>
					<img src="img/medal_bronze_delete.png" alt="<bean:message key="files.document.approved.notexist"/>" title="<bean:message key="files.document.approved.notexist"/>">
				<%}%>
				<% if(!item.isActive()) { %>
					<img src="img/docVenc.gif" alt="<bean:message key="files.document.notexist"/>" title="<bean:message key="files.document.notexist"/>">
				<%} else if(item.getToForFiles()==null || item.getToForFiles().equals("true") || item.getToForFiles().equals("1") ) {%>
                    <img src="img/docu-del-18.gif" alt="<bean:message key="files.notEnable"/>" title="<bean:message key="files.notEnable"/>">
                <%}%>
			</span>
			<%if(isOwnerFile) {%>
			<a href="javascript:showDocumentPublishImp('<%=item.getNumgen()%>',<%=item.getNumver()%>,'<%=item.getNumgen()%>',0)" class="info">
				<%=item.getNamedocument()%>
			</a>
			<%} else {%>
				<%=item.getNamedocument()%>
			<%}%>
		</td>
		<td><%=item.getMayorver().concat(".").concat(item.getMinorver())%></td>
		<td><%=item.getTypedoc()%></td>
		<td><%=ToolsHTML.isEmptyOrNull(item.getPrefix(),"").concat(item.getNumber())%></td>
		<td><%=item.getApellidos().concat(" ").concat(item.getNombres()).trim()%></td>
		<td><%=ToolsHTML.getStatusDocumento(item.getStatu(),item.getStatuVer(),rb,dateSystem,item.getDateExpiresDrafts(),item.getDateExpires())%></td>
		<td id="td_<%=item.getNumgen()%>" onclick="organizar(<%=item.getNumgen()%>);"><%=item.getOrden()%></td>
	</tr>
	<%}%>
</table>
<br/>
<center>
</form>
<input type="button" class="boton" onclick="agregar()" value="<bean:message key="btn.incluir"/>" >
<input type="button" class="boton" onclick="borrar()" value="<bean:message key="btn.delete"/>" >
<input type="button" class="boton" onclick="cancelar()" value="<bean:message key="btn.back"/>" >
</center>
<form name="Selection" action="showDataDocument.do">
    <input type="hidden" name="idDocument" value=""/>
    <input type="hidden" name="idVersion" value=""/>
    <input type="hidden" name="showStruct" value="true"/>
    <input type="hidden" name="from" value="searchFiles.jsp?expand=false"/>
</form>

</body>
</html>
