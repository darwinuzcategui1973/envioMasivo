<!-- recordFilters.jsp -->
<%@ page import="java.util.ResourceBundle,
                 com.desige.webDocuments.enlaces.forms.UrlsActionForm,
                 com.desige.webDocuments.utils.beans.SuperActionForm,
                 com.desige.webDocuments.utils.beans.Users,
                 com.desige.webDocuments.utils.ToolsHTML"%>
                 
<%@ page import="com.desige.webDocuments.persistent.managers.HandlerBD" %>

<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<%@ page language="java" %>
<%
	if(request.getAttribute("visible")==null || !String.valueOf(request.getAttribute("visible")).equals("true") ) {
		response.sendRedirect("recordFilters.do");
	}

    ResourceBundle rb = ToolsHTML.getBundle(request);
    StringBuffer onLoad = new StringBuffer("");
    Users usuario = (Users)session.getAttribute("user");
    String ok = (String) ToolsHTML.getAttributeSession(session, "usuario", false);
    String info = (String) ToolsHTML.getAttribute(request, "info", true);
    
%>
<html>
<head>
<title><%=rb.getString("principal.title")%></title>
<jsp:include page="meta.jsp" /> 
<link href="estilo/estilo.css" rel="stylesheet" type="text/css">
<script type="text/javascript">
function ver(etiqueta,valor) {
	if(valor=="dat"){
		abrirDat();
	} else if(valor=="url"){
		abrirUrl();
	}
	
	window.open(etiqueta,"bandeja");
}
function agregarNuevoUrl() {
	agregarUrl();
	document.getElementById("urlAgregar").style.display="";
	document.getElementById("urlEliminar").style.display="";
}
function agregarUrl() {
	document.getElementById("urlAgregar").style.display="none";
	document.getElementById("urlCancelar").style.display="";
	document.getElementById("urlGuardar").style.display="";
	document.getElementById("urlEliminar").style.display="none";
	if((typeof window.bandeja.document.newUrls)!="undefined") {
		window.bandeja.incluir(window.bandeja.document.newUrls);
	} else {
		window.bandeja.incluir(window.bandeja.document.editarUrls);
	}		
}
function cancelarUrl() {
	document.getElementById("urlAgregar").style.display="";
	document.getElementById("urlCancelar").style.display="none";
	document.getElementById("urlGuardar").style.display="none";
	document.getElementById("urlEliminar").style.display="none";
	if((typeof window.bandeja.document.newUrls)!="undefined") {
		window.bandeja.cancelar(window.bandeja.document.newUrls,'loadUrls.do');
	} else {
		window.bandeja.cancelar(window.bandeja.document.editarUrls,'loadUrls.do');
	}
}
function guardarUrl() {
	if((typeof window.bandeja.document.newUrls)!="undefined") {
		window.bandeja.salvar(window.bandeja.document.newUrls);
	} else {
		window.bandeja.salvar(window.bandeja.document.editarUrls);
	}
}
function eliminarUrl(){
	window.bandeja.youAreSure(window.bandeja.document.editarUrls);
}

function guardarDat() {
	window.bandeja.salvar();
}

function abrirDat(){
	document.getElementById("datGuardar").style.display="";
	document.getElementById("urlAgregar").style.display="none";
	document.getElementById("urlCancelar").style.display="none";
	document.getElementById("urlGuardar").style.display="none";
	document.getElementById("urlEliminar").style.display="none";
}
function abrirUrl(){
	document.getElementById("datGuardar").style.display="none";
	document.getElementById("urlAgregar").style.display="";
	document.getElementById("urlCancelar").style.display="none";
	document.getElementById("urlGuardar").style.display="none";
	document.getElementById("urlEliminar").style.display="none";
}

function permission(type,nodeType) {
	  var forma = document.getElementById("Selection");
      forma.action = "loadAllUserRecord.do";
      if (forma.nodeType) {
          forma.nodeType.value = nodeType;
      }
      if (type==1) {
          forma.action = "loadAllGroupsRecord.do";
      }
      forma.submit();
}

</script>
</head>
<body class="bodyInternas" <%=onLoad.toString()%>>
<table cellSpacing="1" cellPadding="0" align="center" border="0" width="100%"  height="100%" >
	<tr>
		<td width="<%=ToolsHTML.ANCHO_MENU%>px" valign="top" align="left" >
			<table cellSpacing="0" cellPadding="2"  border="0" width="<%=ToolsHTML.ANCHO_MENU%>px"  height="100%" style="/*border-collapse:collapse;*/border-color:#ofofof;border:1px solid #efefef">
				<tr>
					<td height="30px" class="ppalBoton" onmouseover="this.className='ppalBoton ppalBoton2'" onmouseout="this.className='ppalBoton'">
						<table border="0" cellspacing="0" cellpadding="2" height="100%">
							<tr>
								<td class="ppalTextBold" width="100%">
									<%=rb.getString("enl.record")%>
								</td>
								<td>
								    &nbsp;
								</td>
							</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td height="99%" bgcolor="white" valign="top" background="menu-images/backgd.png">
						<table class="ppalText" style="cursor:pointer;" cellspacing="5">
							<logic:present name="securityRecord">
								<logic:equal name="securityRecord" property="toUpdate"  value="1">
							<tr onclick="permission(0,3);">
								<td><img src="icons/user_green.png"/></td>
								<td>&nbsp;<%=rb.getString("btn.permission")%></td>
							</tr>
							<tr onclick="permission(1,3);">
								<td><img src="icons/group.png"/></td>
								<td>&nbsp;<%=rb.getString("btn.permission1")%></td>
							</tr>
								</logic:equal>
							</logic:present>
							<tr onclick="ver('recordFiltersFrame.jsp');">
								<td><img src="icons/chart_curve.png"/></td>
								<td>&nbsp;<%=rb.getString("lst.reporte")%></td>
							</tr>
							<form name="Selection" id="Selection" action="loadStructMain.do" method="post" target="bandeja">
							    <input type="hidden" name="idNode" value='4'/>
							    <input type="hidden" name="idNodeSelected" value='4'/>
							    <input type="hidden" name="idNodeChange" value=''/>
							    <input type="hidden" name="isCopy" value=''/>
							    <input type="hidden" name="movDocument" value='0'/>
							    <input type="hidden" name="cmd"/>
							    <input type="hidden" name="nexPage" value="loadStructMain.do"/>
							    <input type="hidden" name="idDocument" value=''/>
							    <input type="hidden" name="idVersion" value=""/>
							    <input type="hidden" name="toStruct" value="0"/>
							    <input type="hidden" name="expandir" value=""/>
							    <input type="hidden" name="nodeType" value=""/>
							    <input type="hidden" name="securityPorStructura" value="true"/>
							</form>
						</table>
					</td>
				</tr>
						<%if (usuario!=null) {
                            out.println(ToolsHTML.printMenu(rb,usuario.getUser(),usuario.getIdGroup(),true,request));
                        }%>
			</table>
		</td>
		<td align="center" >
			<table border="0" cellspacing="0" cellpadding="0" width="100%" height="100%">
				<tr>
					<td height="20px">
						<iframe name="bandejaTit" width="100%" height="20px" src="recordFiltersMainTit.jsp" border="0px" marginwidth="0px" marginheight="0px" frameborder="0" scrolling="no"></iframe>
					</td>
				</tr>
				<tr>
					<td>
						<iframe name="bandeja" width="100%" height="100%" src="recordFiltersFrame.jsp" border="0px" marginwidth="0px" marginheight="0px" frameborder="0"></iframe>
					</td>
				</tr>
			</table>
		</td>
    </tr>
</table>
</body>
</html>
