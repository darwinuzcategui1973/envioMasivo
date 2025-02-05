<!-- principalSacop1Borrador.jsp -->
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
    
    int sacopBorrador  = Integer.parseInt(ToolsHTML.isEmptyOrNull(String.valueOf(request.getAttribute("borradorSize")),"0"));
    int sacopPendiente = Integer.parseInt(ToolsHTML.isEmptyOrNull(String.valueOf(request.getAttribute("pendientesSize")),"0"));
    int sacopEmitido   = Integer.parseInt(ToolsHTML.isEmptyOrNull(String.valueOf(request.getAttribute("emitidoSize")),"0"));
    int sacopCerrado   = Integer.parseInt(ToolsHTML.isEmptyOrNull(String.valueOf(request.getAttribute("cerradoSize")),"0"));
%>
<html>
<head>
<title><%=rb.getString("principal.title")%></title>
<jsp:include page="meta.jsp" /> 
<link href="estilo/estilo.css" rel="stylesheet" type="text/css">
<script type="text/javascript" src="./script/sacopJS.js"></script>

<script type="text/javascript">
	function showPlanilla(forma,id,edo){
		//alert('id='+id+' edo='+edo+' forma.action='+forma.action);
	    forma.Idplanillasacop1.value=='';
	    window.parent.parent.info.document.getElementById("printSacopBtn").style.display = "";
	    
	    if ((edo=='<%=LoadSacop.edoBorrador%>')) {    //edoBorrador
	    	forma.goTo.value="planillauno";
	        forma.edodelsacop.value=edo;
	        forma.Idplanillasacop1.value=id;
	        forma.target="sacopCenter";
	        //alert(window.parent.parent.info.frames.name);
	        //alert(window.parent.location);
	        //alert(window.parent.parent.location);
	        window.parent.parent.info.frames.ir('loadBorrador',"loadSACOPMain.do?goTo=planillauno&edodelsacop="+edo+"&Idplanillasacop1="+id);
	        //alert(window.parent.parent.info.frames.probar());            
	        //alert(window.parent.parent.info.frames[0].name);
	        //alert(window.parent.parent.info.frames[1].name);
	        //alert(window.parent.parent.info.frames[2].name);
	        //alert(window.parent.parent.frames[1].name);            
	        //alert(window.parent.frames[1].name);            // frmConfig
	        //forma.submit();
	        //alert(forma.action); //"loadSACOPMain.do?goTo=planillauno&edodelsacop="+edo+"&Idplanillasacop1="+id
	    } else if ((edo=='<%=LoadSacop.edoEmitida%>')||(edo=='<%=LoadSacop.edoRechazado%>')) {    //edoEmitida o edoRechazado
	        forma.edodelsacop.value=edo;
	        forma.goTo.value="planillados";
	        forma.Idplanillasacop1.value=id;
	        forma.submit();
	    } else if((edo=='<%=LoadSacop.edoAprobado%>')) {  //edoAprobado
	        forma.goTo.value="planilla3_0";
	        forma.Idplanillasacop1.value=id;
	        forma.submit();
	    } else if((edo=='<%=LoadSacop.edoPendienteVerifSeg%>')||(edo=='<%=LoadSacop.edoEnEjecucion%>')) {
	        forma.goTo.value="planilla4";
	        forma.Idplanillasacop1.value=id;
	        forma.submit();
	    } else if((edo=='<%=LoadSacop.edoVerificacion%>')) {
	        forma.goTo.value="planilla5";
	        forma.Idplanillasacop1.value=id;
	        forma.submit();
	
	    } else if((edo=='<%=LoadSacop.edoCerrado%>')) {
	        forma.goTo.value="planilla6";
	        forma.Idplanillasacop1.value=id;
	        forma.submit();
	    }
	}

	function validar(){
        if (document.login.docSearch.value.length>0){
            alert('<%=rb.getString("err.notDocumentSearch")%>');
        }else{
		    document.login.submit();
        }
	}

    function showWF(idWorkFlow,row,owner){
    	var forma = document.getElementById("selection");
        forma.idWorkFlow.value = idWorkFlow;
        forma.row.value = row;
        forma.owner.value = owner;
        forma.submit();
    }

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
    function visualizarTodos(){
    	alert("visualizarTodo darwinuzcategui principalSacopBorrador.jsp");
         document.frmvisualizarTodos.submit();
    }
    function showCharge(userName,charge) {
	    charge = charge.replace(/\[/gi,"-").replace(/\]/gi,"-");
	    charge = charge.replace(/\(|\)/g, "");
	    charge = charge.replace(/<br>/g, " - ");
        window.open("showCharge.jsp?userName="+userName+"&charge="+charge, "WebDocuments", "resizable=no,scrollbars=yes,statusbar=yes,width=300,height=200,left=410,top=250");
    }
    function deletePlanilla(forma,id,idPerson,borrarAtodos){
         if (confirm("<%=rb.getString("scp.eliminarask")%>")) {
             forma.userdlt.value= idPerson;
             forma.idpdlt.value=id;
             forma.borrarAtodos.value= borrarAtodos;
             forma.action= "DeleteSacopAction.do";
             forma.submit();
        }
    }
    function reporteListaParent(){
		reporteLista(document.reporte);
    }
    function reporteDetalladoParent(valor){
	    document.reporte.conjuntosacop.value=valor;
		reporteLista(document.reporte);
    }
	function reporteLista(forma) {
        forma.submit();
    }
    
    function cancelar() {
   		location.href = "loadSACOPMain.do?goTo=reload";
    }
    
    function getOption(){
    	return "Borrador";
    }
</script>
</head>

<body class="bodyInternas" <%=onLoad.toString()%>>
<logic:notPresent name="crearSacop" scope="request">
<form name=solicitudsacopplanilla action='loadSACOPMain.do'>
    <!--Variables para usar en eliminar planillas-->
    <input type='hidden' name=userdlt>
    <input type='hidden' name=idpdlt>
    <input type='hidden' name=goTo>
    <input type='hidden' name="Idplanillasacop1">
    <input type='hidden' name="edodelsacop">
    <input type='hidden' name="borrarAtodos">
	<table cellSpacing="0" cellPadding="0" align="center" border="0" width="100%">
		  <logic:present name="borrador" scope="session" >
        	<%int cont=0;%>
			<logic:iterate id="borrador1" name="borrador" type="com.desige.webDocuments.sacop.forms.Plantilla1BD" scope="session" >
                <tr class='fondo_<%=(cont==0?cont++:cont--)%>'>
					<td width="16%">
						<img src="img/chequeado.gif" width="17" height="17">
						 <a href="javascript:showPlanilla(document.solicitudsacopplanilla,<%=borrador1.getIdplanillasacop1()%>,<%=borrador1.getEstado()%>);" class="ahreMenu" >
						   <%=borrador1.getSacopnum()%>
						</a>
					</td>
					<td width="25%" class="txt_b">
						 <a href="javascript:showCharge('<%=borrador1.getSolicitantetxt()%>','<%=borrador1.getCargoSolicitante()%>')" class="ahref_b">
							<logic:equal name="borrador1" property="mostrarCargo" value="true">
								<%=borrador1.getCargoSolicitante()%>
							</logic:equal>
							<logic:equal name="borrador1" property="mostrarCargo" value="false">
								<%=borrador1.getSolicitantetxt()%>
							</logic:equal>
						</a>
					</td>
					<td width="25%" class="txt_b">
					 <a href="javascript:showCharge('<%=borrador1.getRespbleareatxt()%>','<%=borrador1.getCargo()%>')" class="ahref_b">
						<logic:equal name="borrador1" property="mostrarCargo" value="true">
							<%=borrador1.getCargo()%>
						</logic:equal>
						<logic:equal name="borrador1" property="mostrarCargo" value="false">
							<%=borrador1.getRespbleareatxt()%>
						</logic:equal>
					  </a>
					</td>
					<td width="25%" class="txt_b">&nbsp;
						 <%=borrador1.getEstadotxt()%>

						   <logic:notEqual name="borrador1" property="idplanillasacop1esqueleto" value="-1">
							   &nbsp;<%=rb.getString("scpintouch.intouch")%>
						   </logic:notEqual>
					</td>
					<td width="9%" class="txt_b">&nbsp;
						<logic:present name="verHistoricosTambien" scope="request">
						   <logic:equal  name="verHistoricosTambien" value="true">
							 <%=rb.getString("scp.eliminar1")%>
						   </logic:equal>
						   <logic:equal  name="verHistoricosTambien" value="false">
							  <a href="javascript:deletePlanilla(document.solicitudsacopplanilla,<%=borrador1.getIdplanillasacop1()%>,<%=idPerson%>,'1');" class="ahreMenu" >
										<%=rb.getString("scp.eliminar1")%>
							  </a>
						   </logic:equal>
					   </logic:present>
					</td>
				</tr>
			</logic:iterate>
		</logic:present>
	</table>
</form>
</logic:notPresent>
</body>
</html>
<script language="javascript" event="onload" for="window">
	try{
	window.parent.updateCounter(<%=sacopBorrador%>,<%=sacopEmitido%>,<%=sacopPendiente%>,<%=sacopCerrado%>);
	}catch(e){}
</script>
