
<!--
 * Title: invokeJNPL.jsp <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * @author Luis Cisneros
 * @version WebDocuments v4.0

 * </ul>
-->
<%@ page import="java.util.ResourceBundle,
				 com.desige.webDocuments.utils.beans.Users,
				 com.desige.webDocuments.document.forms.BaseDocumentForm,
                 com.desige.webDocuments.utils.ToolsHTML"%>

<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<%@ page language="java" %>
<%
    Users users = (Users)session.getAttribute("user");

    ResourceBundle rb = ToolsHTML.getBundle(request);
    String width = "12%";
    if (request.getAttribute("width")!=null) {
        width = "25%";
    }
    
    String nodeActive = (String)session.getAttribute("nodeActive");
    
    BaseDocumentForm registro = (BaseDocumentForm) request.getAttribute("registroGenerado");
%>
<html>
<head>
<title><%=rb.getString("principal.title")%></title>
<jsp:include page="meta.jsp" /> 
<link href="estilo/estilo.css" rel="stylesheet" type="text/css">
<script language=javascript src="./estilo/funciones.js"></script>
<script language="JavaScript">

    setDimensionScreen();

	function getPathFromLocation() {
		var cad = document.location.href;
		cad = cad.replace("http://","");
		var arr = cad.split("/");
		if(arr[1].indexOf(".do") > 0){
			//estamos en presencia de un link del tipo
			//http://servidor:puerto/accion.do
			return "http://"+arr[0]+"/";
		} else {
			//estamos en presencia de un link del tipo
            //http://servidor:puerto/appname/accion.do
			return "http://"+arr[0]+"/"+arr[1]+"/";
		}
	}

	function getServerLocation() {
		var cad = document.location.href;
		cad = cad.replace("http://","");
		var arr="";
		if(cad.indexOf(":")!=-1) {
			arr = cad.split(":");
		} else {
			var arr = cad.split("/");
		}
		return arr[0];
	}
   
    <logic:present name="info" scope="session">
        showInfo('<%=session.getAttribute("info")%>');
        <%
            session.removeAttribute("info");
        %>
    </logic:present>

    <logic:present name="error" scope="session">
        showInfo('<%=session.getAttribute("error")%>');
        <%
            session.removeAttribute("error");
        %>
    </logic:present>

    function cancelar() {
    	var forma = document.getElementById("Selection");
        
        if (!parent.frames['code']) {
            forma.action = "loadAllStruct.do";
        }
        forma.submit();
    }
    
	var http = false;
    
    function abrir(obj) {
    	respuesta = "false";
		if(navigator.appName == "Microsoft Internet Explorer") {
		  http = new ActiveXObject("Microsoft.XMLHTTP");
		} else {
		  http = new XMLHttpRequest();
		}
		http.open("POST", "markCheckOutAjax.do?registro=true&idDocument=<%=request.getParameter("newIdDocument")%>&idVersion=<%=request.getParameter("newNumVer")%>&server="+getServerLocation());
		http.onreadystatechange=function() {
		  if(http.readyState == 4) {
		  		document.location.href=getPathFromLocation()+"editorPorInternet/qwds4Registro<%= users.getIdPerson()%>.jnlp";
				setTimeout("eliminar()",60000);
		  }
		}
		http.send(null);
		//pause(2000);
		//obj.href="editorPorInternet/qwds4Registro<%= users.getIdPerson()%>.jnlp";
		//obj.style.display="none";
		//setTimeout("eliminar()",60000);
    }
    
    // edicion desde bloqueado en la ficha del documento
    function abrirOLD(obj) {
    	respuesta = "false";
		if(navigator.appName == "Microsoft Internet Explorer") {
		  http = new ActiveXObject("Microsoft.XMLHTTP");
		} else {
		  http = new XMLHttpRequest();
		}
		http.open("POST", "markCheckOutAjax.do?idCheckOut=<%=registro.getCheckOut()%>&registro=false&editar=true&server="+getServerLocation()+"&actualizarOriginal=true");
		http.onreadystatechange=function() {
		  if(http.readyState == 4) {
		  		document.location.href=getPathFromLocation()+"editorPorInternet/qwds4<%= users.getIdPerson()%>.jnlp";
		  		//document.editor.action=getPathFromLocation()+"editorPorInternet/qwds4<%= users.getIdPerson()%>.jnlp";
			  	//document.editor.submit();
				setTimeout("eliminar()",60000);
		  }
		}
		http.send(null);
		//pause(2000);
		//obj.href="editorPorInternet/qwds4<%= users.getIdPerson()%>.jnlp";
		//setTimeout("eliminar()",60000);
    }
    
    
	function pause(millis) {
		var date = new Date();
		var curDate = null;
	
		do { curDate = new Date(); }
		while(curDate-date < millis);
	}
    
    function eliminar() {
		if(navigator.appName == "Microsoft Internet Explorer") {
		  http = new ActiveXObject("Microsoft.XMLHTTP");
		} else {
		  http = new XMLHttpRequest();
		}
		http.open("POST", "markCheckOutAjax.do?eliminar=true&registro=true");
		http.onreadystatechange=function() {
		  if(http.readyState == 4) {
		  	//editar(http.responseText);
		  }
		}
		http.send(null);
    }
    
</script>
</head>

<body class="bodyInternas">

  
        <table align=center border=0 width="100%">
            <tr>
                <td class="pagesTitle" colspan="2">
                    <%=rb.getString("btn.createDocument")%>
                </td>
            </tr>
            <tr>
                <td colspan="2">
                    <table class="clsTableTitle" width="100%" cellSpacing=0 cellPadding=2 align=center border=0>
                        <tbody>
                            <tr>
                                <td class="td_title_bc">
                                    <%=rb.getString("btn.createDocument")%>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </td>
            </tr>
            <tr>
                <td class="td_gris_l" colspan="2">
                    &nbsp;
                </td>
            </tr>       
            <tr>
                <td align="center" colspan="2">
                	<table cellpadding="10" cellspacing="10">
                		<tr>
                			<td align="center" style="border:1px solid #CDCDCD;">
			                   <input type="button" class="boton" onclick="abrir(this);" value="<%=rb.getString("wf.editDoc")%>" >
			                </td>
				                <%if(session.getAttribute("rejectDocument")!=null && !String.valueOf(session.getAttribute("rejectDocument")).trim().equals("")){%>
                			<td align="center" style="border:1px solid #CDCDCD;">
			                   <input type="button" class="boton" onclick="document.location.href='updateRegisterRejectOnDemand.do'" value="<%=rb.getString("btn.saveChanges")%>" >
							</td >
				              	<%} else {%>       
                			<td align="center" style="border:1px solid #CDCDCD;">
			                   <input type="button" class="boton" onclick="document.location.href='showDataDocument.do?idDocument=<%=request.getParameter("idDocument")%>'" value="<%=rb.getString("btn.back")%>" >
							</td>
                			<td align="center" style="border:1px solid #CDCDCD;">
			                   <input type="button" class="boton" onclick="document.location.href='showDataDocument.do?idDocument=<%=registro.getNumberGen()%>'" value="<%=rb.getString("btn.gotoRegister")%>" >
							</td>
				              	<%}%>
				        </tr>
				   </table>       
                </td>
            </tr>
          
        </table>
        <%if(registro!=null){%>
        <br>
        <br>
        <br>
        <table cellpadding="5" cellSpacing="3" style="border-collapse:collapse;border:1 solid black;background-color:#ffffff;" border="1" align="center" >
	       	<tr>
	       		<td>
					<table cellpadding="5" cellSpacing="3" style="border-collapse:collapse;border:1 solid black;background-color:#ffffff;" border="0" >
						<caption style="text-transform: uppercase"><%=rb.getString("reg.new.data")%></caption>
						<tr>
							<td class="td_gris_l"><b><%=rb.getString("reg.new.name")%></b></td><td>:</td><td class="td_gris_l"><%=registro.getNameDocument()%></td>
						</tr>
						<tr>
							<td class="td_gris_l"><b><%=rb.getString("reg.new.code")%></b></td><td>:</td><td class="td_gris_l"><%=registro.getPrefix()%><%=registro.getNumber()%></td>
						</tr>
						<tr>
							<td class="td_gris_l"><b><%=rb.getString("reg.new.owner")%></b></td><td>:</td><td class="td_gris_l"><%=registro.getNameOwner()%></td>
						</tr>
					</table>
				</td>
			</tr>
		</table>        
        <%}%>
</body>
</html>
