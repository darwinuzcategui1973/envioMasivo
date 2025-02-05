<jsp:include page="richeditDocType.jsp" />
<%@ page
	import="java.util.ResourceBundle,com.desige.webDocuments.persistent.managers.HandlerStruct,com.desige.webDocuments.utils.ToolsHTML,com.desige.webDocuments.utils.beans.SuperActionForm,com.desige.webDocuments.enlaces.forms.UrlsActionForm"%>

<!--/**
 * Title: editFieldNormFromDocument.jsp<br>
 * Copyright: (c) 2004 Desige Servicios de Computación<br/>
 *
 * @author Ing. Nelson Crespo   (NC)
 * @author Ing. Simón Rodriguéz (SR)
 * @version WebDocuments v1.0
 * <br>
 *     Changes:<br>
 * <ul>
 *      <li> 22-09-2005 (SR)  se agrego el bean data para la session  typeOtroFormato</li>
 *      <li> 07-07-2005 (SR)  Se valido si existe una session descript para
                              MsgInEditor(document.editNorms.descript,document.editNorms.richedit);</li>
 *      <li> 18-08-2005 (SR)  se agrego doc.upFile  para la norma</li>
 *      <li> 20-04-2006 (SR)  Se modifico el border del link igual a cero, en caso de tener un documento enlazado.</li>
 *      <li> 24-04-2006 (SR)  Se coca un punto en esta direccion ./estilo/funciones.js</li>
 *      <li> 25/04/2006 (SR) Se sustituyo /WebDocuments/.. por ./..
 *      <li> 26-06-2006 (SR)  Se coloca un espacio vacio</li>
 * <ul>
 */-->
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic"%>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>

<%@ page language="java"%>
<%
	ResourceBundle rb = ToolsHTML.getBundle(request);
	StringBuffer onLoad = new StringBuffer("");
	String info = (String) request.getAttribute("info");
	String idNormClass = null;
	boolean isMaster = false;
	
	String loadForm = (String) session.getAttribute("loadManager");
	if (ToolsHTML.isEmptyOrNull(loadForm)) {
		loadForm = "";
	}
	
	if (!ToolsHTML.isEmptyOrNull(info)) {
		onLoad.append(" onLoad=\"alert('").append(info).append("')\"");
	}
	
	String err = (String) session.getAttribute("error");
	if (!ToolsHTML.isEmptyOrNull(err)) {
		onLoad.append(" onLoad=\"alert('").append(err).append("')\"");
		session.removeAttribute("error");
	}

	boolean isInput = true;
	String inputReturn = (String) session.getAttribute("input");
	if (inputReturn == null) {
		inputReturn = "";
		isInput = false;
	}
	
	String valueReturn = (String) session.getAttribute("value");
	if (valueReturn == null) {
		valueReturn = "";
	}
	String input = (String) ToolsHTML.getAttribute(request, "input");
	String value = (String) ToolsHTML.getAttribute(request, "value");

	String nameForma = ToolsHTML.getAttribute(request, "nameForma") != null
			? (String) ToolsHTML.getAttribute(request, "nameForma")
			: "";

	String cmd = (String) request.getAttribute("cmd");
	if (cmd == null) {
		cmd = SuperActionForm.cmdLoad;
	}
	pageContext.setAttribute("cmd", cmd);
	
	String selected = (String) request.getAttribute("selected");
%>
<html>
<head>
<title><%=rb.getString("principal.title")%></title>
<jsp:include page="meta.jsp" />
<link href="estilo/estilo.css" rel="stylesheet" type="text/css">
<script type="text/javascript" src="./estilo/funciones.js"></script>
<jsp:include page="richeditHead.jsp" />
<style>
.active0 {
	background-color: transparent;
	color: #afafaf;
	text-align: center;
}

.active1 {
	background-color: #00ceb4;
	color: #000;
	text-align: center;
}
</style>

<script type="text/javascript">
	var parentHiddenField = "<%=request.getAttribute("hiddenField")%>";
    
	<%if(selected==null || selected.equals("")){%>
    var idSeleccionados = new Array();
    <%} else {%>
    var idSeleccionados = '<%=selected%>'.split(","); 
    <%}%>
    
    String.prototype.trim = function() { return this.replace(/^\s*|\s*$/g,""); };

    function load() {
    	document.getElementById("divSelected").innerHTML=idSeleccionados.length;
		for(var i=0;i<idSeleccionados.length;i++) {
			document.getElementById("ACTIVE_"+idSeleccionados[i]).className="active1";
			document.getElementById("ACTIVE_"+idSeleccionados[i]).innerHTML="<%=rb.getString("sistema.yes")%>";
		}
    }
    
   	function selectNorm(idNorm) {
   		
		console.log(idNorm);
		var found = false;
		for(var i=0;i<idSeleccionados.length;i++) {
			if(idSeleccionados[i]==idNorm){
				idSeleccionados.splice(i,1);
				found=true;
			}
		}
		if(!found) {
			idSeleccionados[idSeleccionados.length]=idNorm;
		}
		//console.log(idSeleccionados.join(","));
		
		document.getElementById("ACTIVE_"+idNorm).className=(found?"active0":"active1");
		document.getElementById("ACTIVE_"+idNorm).innerHTML=(found?"<%=rb.getString("sistema.no")%>":"<%=rb.getString("sistema.yes")%>");
		document.getElementById("divSelected").innerHTML=idSeleccionados.length;
   	}

   	function verHijos(obj,clase) {
   		var els = document.getElementsByClassName(clase);

   		obj.innerHTML=(obj.innerHTML=='+'?'-':'+');
   		Array.prototype.forEach.call(els, function(el) {
   			el.style.display=el.style.display==""?"none":"";
   		});
   	}
   	
    function closeWindow(){
    	if(window.opener != null){
    		//debemos regresarle a la ventana padre, los valores seleccionados de la lista de normas
    		//alert(obtenerIdsSeleccionados(document.getElementById("normsisoSelected")));
    		window.opener.setNormas(parentHiddenField,idSeleccionados.join(","));
    		if(window.opener.setNormasAfter) {
    			window.opener.setNormasAfter();
    		}
    		window.close();
    	}
    }

</script>
</head>

<body class="bodyInternas" onload="load()" style="margin: 0;">
	<table cellSpacing=0 cellPadding=2 align=center border=0 width="100%">
		<logic:present name="dataTable" scope="session">
			<logic:equal name="cmd" value="<%=SuperActionForm.cmdLoad%>">
				<!-- Paginación -->
				<form name="selection" id="selection" action="" style="margin: 0;">
					<input type="hidden" name="id" /> <input type="hidden" name="desc" />
					<%
						if (isInput) {
					%>
					<html:hidden property="input" value="<%=input%>" />
					<html:hidden property="value" value="<%=value%>" />
					<html:hidden property="nameForma" value="<%=nameForma%>" />
					<%
						}
					%>
					<tr>
						<td>
							<table cellSpacing="3" cellPadding="3" align=center border=0
								width="100%"
								style="position:fixed;top:0;background-color:white;"
								>
								<tr>
									<td colspan="3">
										<table class="clsTableTitle" width="100%" cellSpacing="0"
											cellPadding="2" align=center border=0>
											<tbody>
												<tr>
													<td class="td_title_bc" width="25%"></td>
													<td class="td_title_bc"><%=rb.getString("cat.title")%></td>
													<td class="td_title_bc" width="25%">
														<div style="text-align:right;padding-right:20px;" ><%=rb.getString("url.selected")%>: (<b id="divSelected"></b>)</div>
													</td>
													<td class="td_title_bc" width="10%">
														<input type="button" class="boton"
															name="guardar" value="<%=rb.getString("btn.save")%>"
															onclick="closeWindow();" />
													</td>
												</tr>
											</tbody>
										</table>
									</td>
								</tr>
								<tr>
									<td width="60%" class="td_titulo_C"><%=rb.getString("url.name")%>
									</td>
									<td width="20%" class="td_titulo_C"><%=rb.getString("url.selected")%>
									</td>
								</tr>
							</table>
							<div style="padding-top:35px;" ></div>
							<table cellSpacing="3" cellPadding="3" align=center border=0
								width="100%">

								<logic:iterate id="bean" name="dataTable" indexId="ind"
									type="com.desige.webDocuments.utils.beans.Search"
									scope="session">
									<%
										int item = ind.intValue() + 1;
										int num = item % 2;
										isMaster = bean.getIndice().equals("0");
										idNormClass = isMaster?bean.getId():idNormClass;
									%>
									<tr class="<%=isMaster?"":idNormClass%>" <%= bean.getIndice().equals("0") ? "" : "style='display:none;'" %>>
										<td width="60%" class='td_<%=num%> <%=bean.getAuditProcess().equals("1")?"td_2":""%>' >
											<b style="cursor:pointer;font-size:16px;" onclick="verHijos(this,'<%=idNormClass%>')"><%= bean.getIndice().equals("0") ? "+" : "" %></b>
											<%= bean.getIndice().equals("0") ? "" : "&nbsp;&nbsp;&nbsp;&nbsp;" %>
											<%
												if (isInput) {
											%> <input name="idRadio" type="radio"
											value="<%=bean.getId()%>,<%=bean.getDescript()%>" /> <%
 	}
 %> 
												<%=ToolsHTML.cortarCadena(bean.getDescript() != null ? bean.getDescript() : "")%>
										 
										</td>
										<td width="20%" class='td_<%=num%>'><a id="" href="#"
											onclick="selectNorm(<%=bean.getId()%>)">
												<div id="ACTIVE_<%=bean.getId()%>"
													class="active0"><%=rb.getString("sistema.no")%></div>
										</a></td>


									</tr>
								</logic:iterate>
							</table>
						</td>
					</tr>
				</form>
				<tr >
					<td>
						<center>
							<form>
									<tr>
									</tr>
							</form>
							&nbsp;
						</center>
					</td>
				</tr>

			</logic:equal>
		</logic:present>
	</table>
	</table>
</body>
</html>
