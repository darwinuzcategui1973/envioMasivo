<!-- filesVersion.jsp -->
<%@ page import="java.util.ResourceBundle,
                 com.desige.webDocuments.enlaces.forms.UrlsActionForm,
                 com.desige.webDocuments.utils.beans.SuperActionForm,
                 com.desige.webDocuments.utils.beans.Users,
                 sun.jdbc.rowset.CachedRowSet,
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
    
    CachedRowSet lista = (CachedRowSet)session.getAttribute("lista");
    CachedRowSet versiones = (CachedRowSet)session.getAttribute("versiones");
    
    lista.beforeFirst();
    versiones.beforeFirst();
        
	int color=0;
	
	if ( !ToolsHTML.isEmptyOrNull(request.getParameter("f1")) ) {
		request.setAttribute("f1",request.getParameter("f1"));
	}
	
	String id=null;
	if(request.getParameter("filesVersion")!=null){
		id=request.getParameter("filesVersion");
	}
	
    
%>
<html>
<head>
<title><%=rb.getString("principal.title")%></title>
<jsp:include page="meta.jsp" /> 
<link href="estilo/estilo.css" rel="stylesheet" type="text/css">
<script language="JavaScript" src="estilo/funciones.js"></script>
<script language="JavaScript">


function cancelar(){
	document.forma.action="filesView.do";
	document.forma.submit();
}

function hideDetail(id){
	document.getElementById("up_"+id).style.display="none";
	document.getElementById("down_"+id).style.display="";
	document.getElementById("relacionados").style.display="none";
}

function showDetail(id){
	document.getElementById("up_"+id).style.display="";
	document.getElementById("down_"+id).style.display="none";
	document.getElementById("relacionados").style.display="";
	document.getElementById("version_"+id).appendChild(document.getElementById("relacionados"));
}

function send(id){
	document.forma.filesVersion.value=id;
	document.forma.submit();
}

</script>
</head>
<body class="bodyInternas" <%=onLoad.toString()%>>
<form name="forma" action="filesVersion.jsp" method="POST">
	<input type="hidden" name="f1" value="<%=request.getAttribute("f1")%>"/>
	<input type="hidden" name="filesVersion" value=""/>

<table border="0" width="100%">	
<tr>
	<td align="center" width="25%">&nbsp;</td>
	<td align="center" width="50%">
		<span class="ppalTextBig" style="color:blue;">Codigo Interno Nro.&nbsp;</span>
		<span class="ppalTextBoldBig" style="color:red;"><%=request.getAttribute("f1")%></span>
	</td>
	<td align="right" width="25%"><input type="button" class="boton" onclick="cancelar()" value="<bean:message key="btn.back"/>" ></td>
</tr>
</table>
<table cellSpacing="1" cellPadding="1" align="center" border="0" width="100%">
	<tr  class="barraBlue">
		<td style="1%">&nbsp;</td>
		<td style="4%">Version</td>
		<td style="60%">Propietario</td>
		<td style="15%">Creado</td>
		<td style="20%">Usuario</td>
	</tr>
	<%color=0;
	boolean isOwnerFile = false;
	while(versiones.next()){
		isOwnerFile = usuario.getUser().equals(versiones.getString("ownerFiles"));%>
	<tr class="fondo_<%=(color=(color==0?1:0))%> texto">
		<td>
			<img id="up_<%=versiones.getString("filesVersion")%>" onclick="hideDetail(<%=versiones.getString("filesVersion")%>)" src="images/up2.gif" title="Ocultar" style="display:none">
	    	<img id="down_<%=versiones.getString("filesVersion")%>" onclick="send(<%=versiones.getString("filesVersion")%>)" src="images/down2.gif" title="Mostrar">
		</td>
		<td>
			<%if(isOwnerFile) {%>
			<a href="#" onclick="showFilesimprimir(<%=request.getAttribute("f1")%>,<%=versiones.getString("filesVersion")%>,true);" class="ahref_c" style="width:100%">
				<%=versiones.getString("filesVersion")%>
			</a>
			<%} else {%>
				<%=versiones.getString("filesVersion")%>
			<%}%>
		</td>
		<td><%=versiones.getString("ownerFiles")%></td>
		<td><%=versiones.getString("datePrint")%></td>
		<td><%=versiones.getString("nameUser")%></td>
	</tr>
	<tr class="texto" style="background-color:white">
		<td id="version_<%=versiones.getString("filesVersion")%>" colspan="5"></td>
	</tr>
	<%}%>
</table>

<div id="relacionados" style="display:none;border:0px solid blue;padding-bottom:5px;padding-top:5px">
<fieldset>
<legend><bean:message key="files.security.toFilesRelated"/></legend>
<table cellSpacing="1" cellPadding="1" align="center" border="0" width="99%">
	<tr  class="barraBlue">
		<td><bean:message key="files.name"/></td>
		<td><bean:message key="files.verbose"/></td>
		<td><bean:message key="files.type"/></td>
		<td><bean:message key="files.id"/></td>
		<td><bean:message key="files.owner"/></td>
		<td><bean:message key="files.status"/></td>
		<td><bean:message key="files.order"/></td>
	</tr>
	<%color=0;
	while(lista.next()){
		if(id!=null && id.equals(lista.getString("filesVersion"))){%>
		<tr class="fondo_<%=(color=(color==0?1:0))%> texto">
		<td>
			<span style="color:green;font-weight:bold;font-family: Sans-serif, Helvetica, Arial, Verdana;">
				<%if(lista.getString("toForFiles")==null || lista.getString("toForFiles").equals("true") || lista.getString("toForFiles").equals("1")) {%>
					<img src="img/docu-del-18.gif" alt="<bean:message key="files.notEnable"/>" title="<bean:message key="files.notEnable"/>">
				<%}%>
			</span>
			<%if(isOwnerFile) {%>
			<a href="javascript:showDocumentPublishImp('<%=lista.getString("numgen")%>',<%=lista.getString("numver")%>,'<%=lista.getString("numgen")%>',0)" class="info">
				<%=lista.getString("namedocument")%>
			</a>
			<%} else {%>
				<%=lista.getString("namedocument")%>
			<%}%>
		</td>
		<td><%=lista.getString("mayorver").concat(".").concat(lista.getString("minorver"))%></td>
		<td><%=lista.getString("typedoc")%></td>
		<td><%=ToolsHTML.isEmptyOrNull(lista.getString("prefix"),"").concat(lista.getString("number"))%></td>
		<td><%=lista.getString("apellidos").concat(" ").concat(lista.getString("nombres")).trim()%></td>
		<td><%=ToolsHTML.getStatusDocumento(lista.getString("statu"),lista.getString("statuVer"),rb,dateSystem,lista.getString("dateExpiresDrafts"),lista.getString("dateExpires"))%></td>
		<td id="td_<%=lista.getString("numgen")%>" onclick="organizar(<%=lista.getString("numgen")%>);"><%=lista.getString("orden")%></td>
	</tr>
	<%	}
	}%>
</table>
</fieldset>
</div>
</form>
</body>
</html>
<script language="javascript">
<%if(id!=null){%>
	showDetail(<%=id%>);
<%}%>
</script>