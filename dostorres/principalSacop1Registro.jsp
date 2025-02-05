<!-- principalSacop1Registro.jsp -->
<%@ page import="java.util.ResourceBundle,
                 com.desige.webDocuments.utils.ToolsHTML,
                 com.desige.webDocuments.utils.beans.Users"%>
<%@ page import="com.desige.webDocuments.sacop.actions.LoadSacop"%>

<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<%@ page language="java" %>
<%
    ResourceBundle rb = ToolsHTML.getBundle(request);
    StringBuffer onLoad = new StringBuffer("");
    String info = (String) ToolsHTML.getAttribute(request,"info",true);
    if (!ToolsHTML.isEmptyOrNull(info)) {
        onLoad.append(" onLoad=\"alert('").append(info).append("')\"");
    }
    String ok = (String)session.getAttribute("usuario");
    String idPerson=(String)ToolsHTML.getAttribute(request,"idPerson",true);
    //ToolsHTML.clearSession(session,"application.sacop");

    //int sacopBorrador  = Integer.parseInt(ToolsHTML.isEmptyOrNull(String.valueOf(request.getAttribute("borradorSize")),"0"));
    //int sacopEmitido   = Integer.parseInt(ToolsHTML.isEmptyOrNull(String.valueOf(request.getAttribute("emitidoSize")),"0"));
    int sacopPendiente = Integer.parseInt(ToolsHTML.isEmptyOrNull(String.valueOf(request.getAttribute("pendientesSize")),"0"));
    int sacopRechazado   = Integer.parseInt(ToolsHTML.isEmptyOrNull(String.valueOf(request.getAttribute("rechazadasSize")),"0"));
    int sacopCerrado   = Integer.parseInt(ToolsHTML.isEmptyOrNull(String.valueOf(request.getAttribute("cerradoSize")),"0"));
%>
<html>
<head>
<title><%=rb.getString("principal.title")%></title>
<jsp:include page="meta.jsp" />
<script type="text/javascript" src="./estilo/funciones.js"></script>
<link href="estilo/estilo.css" rel="stylesheet" type="text/css">
<script type="text/javascript">

     function crearSacop2(forma){
                if (forma.origensacop.value==""){
                   alert('<%=rb.getString("scp.notieneorigensacop")%>');
                }else{
                   forma.submit();
                }

     }
    function crearSacopParent(){
    	crearSacop(document.frmcrearSacop);
    }
    function crearSacop(forma){
        forma.action="CrearSacop.do";
        forma.submit();
    }
    
</script>
</head>

<body class="bodyInternas" <%=onLoad.toString()%>>
<form name=solicitudsacopplanilla action='loadSACOPMain.do'>
	<input id="idDocRelated" type="hidden" value="" >
	<table cellSpacing="1" cellPadding="1" align="center" border="0" width="100%">
	    <tr>
	        <td class="td_titulo_C borderLeft" width="10%">
	            <%=rb.getString("scp.code")%>
	        </td>
	        <td class="td_titulo_C borderLeft" width="30%">
	            <%=rb.getString("scp.name")%>
	        </td>
	        <td class="td_titulo_C borderLeft" width="15%">
	            <%=rb.getString("showDoc.expires")%>
	        </td>
	        <td class="td_titulo_C borderLeft" width="25%">
	            <%=rb.getString("scp.owner")%>
	        </td>
	        <td class="td_titulo_C borderLeft" width="25%">
	            <%=rb.getString("scp.clase")%>
	        </td>
	      </tr>
		<logic:present name="registrosPendiente" scope="session" >
        	<%int cont=0;%>
			<logic:iterate id="registro" name="registrosPendiente" type="com.desige.webDocuments.document.forms.DocumentsCheckOutsBean" scope="session" >
                <tr class='fondo_<%=(cont==0?cont++:cont--)%>'>
					<td width="10%" class="txt_b" nowrap>
					   <input id="rad_<%=registro.getIdDocument()%>" type="radio" name="idDocRelated"  value="<%=registro.getIdDocument()%>" onclick="document.getElementById('idDocRelated').value=<%=registro.getIdDocument()%>"  />
					   <label for="rad_<%=registro.getIdDocument()%>"> 
					   	<%=registro.getPrefix()!=null && registro.getPrefix().trim().length()>0?registro.getPrefix().concat("-"):""%><%=registro.getNumber()%>
					   </label>
					</td>
					<td width="30%" class="txt_b">
					   <a href="javascript:showDocumentPublishImp('<%=registro.getIdDocument()%>',<%=registro.getNumVer()%>,'<%=registro.getIdDocument()%>','1')" class="ahref_b">
					   	<%=registro.getNameDocument()%>
					   </a>
					</td>
					<td width="10%" class="txt_b">
						<%=registro.getDateCheckOut()%>
					</td>
					<td width="25%" class="txt_b">
						<%=registro.getPersonBean().getApellido()%> <%=registro.getPersonBean().getNombre()%>
					</td>
					<td width="25%" class="txt_b">
						<%=ToolsHTML.getRegisterClassActionRecommended(Integer.parseInt(registro.getIdRegisterClass()))%>
					</td>
				</tr>
			</logic:iterate>
		</logic:present>
	</table>
</form>
</body>
</html>
<script type="text/javascript" event="onload" for="window">
	try{
		   window.parent.updateCounter(<%=sacopRechazado%>,<%=sacopPendiente%>,<%=sacopCerrado%>);
	}catch(e){
		/**/
	}
</script>
