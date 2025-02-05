<!-- principalSacop1Rechazado.jsp -->
<%@ page import="java.util.ResourceBundle,
                 com.desige.webDocuments.utils.ToolsHTML,
                 com.desige.webDocuments.persistent.managers.HandlerBD,
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
    int lineas=500;
    Users usuarioActual = session.getAttribute("user")!=null?(Users)session.getAttribute("user"):null;
	Users users = session.getAttribute("user")!=null?(Users)session.getAttribute("user"):null;
	
%>
<html>
<head>
<title><%=rb.getString("principal.title")%></title>
<jsp:include page="meta.jsp" /> 
<link href="estilo/estilo.css" rel="stylesheet" type="text/css">
<script type="text/javascript">
	function showPlanilla(forma,id,edo){
        //alert('id='+id+' edo='+edo+' forma.action='+forma.action);
        forma.Idplanillasacop1.value=='';
         if ((edo=='<%=LoadSacop.edoBorrador%>')) {    //edoBorrador
        	 forma.goTo.value="planillauno";
             forma.edodelsacop.value=edo;
             forma.Idplanillasacop1.value=id;
             forma.target="sacopCenter";
             window.parent.parent.info.frames.ir('loadBorrador',"loadSACOPMain.do?goTo=planillauno&edodelsacop="+edo+"&Idplanillasacop1="+id);
        } else if ((edo=='<%=LoadSacop.edoEmitida%>')||(edo=='<%=LoadSacop.edoRechazado%>')) {    //edoEmitida o edoRechazado
        	forma.edodelsacop.value=edo;
            forma.goTo.value="planillados";
            forma.Idplanillasacop1.value=id;
            forma.target="sacopCenter";
            window.parent.parent.info.frames.ir('loadSACOPMain.do',"loadSACOPMain.do?goTo=planillados&edodelsacop="+edo+"&Idplanillasacop1="+id);
        } else if((edo=='<%=LoadSacop.edoAprobado%>')) {  //edoAprobado
        	forma.edodelsacop.value=edo;
            forma.goTo.value="planilla30";
            forma.Idplanillasacop1.value=id;
            forma.target="sacopCenter";
            window.parent.parent.info.frames.ir('loadSACOPMain.do', encodeURIComponent("loadSACOPMain.do?goTo=planilla30&edodelsacop="+edo+"&Idplanillasacop1="+id));
        } else if((edo=='<%=LoadSacop.edoPendienteVerifSeg%>')||(edo=='<%=LoadSacop.edoEnEjecucion%>')) {
            forma.edodelsacop.value=edo;
            forma.goTo.value="planilla4";
            forma.Idplanillasacop1.value=id;
            forma.target="sacopCenter";
            window.parent.parent.info.frames.ir('loadSACOPMain.do',"loadSACOPMain.do?goTo=planilla4&edodelsacop="+edo+"&Idplanillasacop1="+id);
        } else if((edo=='<%=LoadSacop.edoVerificacion%>')) {
            //forma.goTo.value="planilla5";
            //forma.Idplanillasacop1.value=id;
            //forma.submit();
            forma.edodelsacop.value=edo;
            forma.goTo.value="planilla5";
            forma.Idplanillasacop1.value=id;
            forma.target="sacopCenter";
            window.parent.parent.info.frames.ir('loadSACOPMain.do',"loadSACOPMain.do?goTo=planilla5&edodelsacop="+edo+"&Idplanillasacop1="+id);
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
    	return "Pendiente";
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
        <tr>
            <table cellSpacing="0" cellPadding="0" align="center" border="0" width="100%">
        <tr>
             <td id="COL_A"  class="td_titulo_C borderLeft" width="10%">
                <%=rb.getString("scp.num")%>
            </td>
            <td id="COL_B"  class="td_titulo_C borderLeft" width="10%">
                <%=rb.getString("scp.fechae")%>
            </td>
            <td id="COL_C" class="td_titulo_C borderLeft" width="15%">
                <%=rb.getString("scp.applicant")%>
            </td>
            
            <td id="COL_D" class="td_titulo_C borderLeft" width="15%">
                <%=rb.getString("scp.emisor")%>
            </td>
            
            <td id="COL_E" class="td_titulo_C borderLeft" width="15%">
                <%=rb.getString("scp.responsable")%>
            </td>
            
            <td id="COL_F" class="td_titulo_C borderLeft" width="15%">
                <%=rb.getString("scp.origin")%>
            </td>
            
            <td id="COL_G" class="td_titulo_C borderLeft" width="20%">
                <%=rb.getString("requestSacop.origen")%>
            </td>
            <td id="COL_H" class="td_titulo_C borderLeft" width="10%">
                <%=rb.getString("scp.fechastimada")%>
            </td>
            <td id="COL_I" class="td_titulo_C borderLeft" width="15%">
                <%=rb.getString("scp.estado")%>
            </td>
            <td id="COL_J" class="td_titulo_C borderLeft" width="10%">
                <%=rb.getString("scp.tracking")%>
            </td>
    
          </tr>
		<logic:present name="rechazadas" scope="session">
        	<%int cont=0;%>
        	<%int indice=-1;%>
			<logic:iterate id="pendientes1" name="rechazadas" type="com.desige.webDocuments.sacop.forms.Plantilla1BD" scope="session" >
                <tr class='fondo_<%=(cont==0?cont++:cont--)%>'>
					<td id="LIN_A<%=++indice%>" >
						<img src="img/chequeado.gif" width="17" height="17">
						 <a href="javascript:showPlanilla(document.solicitudsacopplanilla,<%=pendientes1.getIdplanillasacop1()%>,<%=pendientes1.getEstado()%>);" class="ahreMenu  info" >
						   <%=pendientes1.getSacopnum()%>
						   <span>
                              <%= pendientes1.getNoconformidades() %>
                           </span>
						</a>
					</td>
				   <td id="LIN_B<%=indice%>" class="txt_b">
				   		<%=ToolsHTML.getSdfShowWithoutHour(pendientes1.getFechaemision())%>
				   </td>

				   <td id="LIN_C<%=indice%>" class="txt_b">
				   <a href="javascript:showCharge('<%=pendientes1.getSolicitantetxt()%>','<%=pendientes1.getCargoSolicitante()%>')" class="ahref_b">
					  <logic:equal name="pendientes1" property="mostrarCargo" value="true">
							<%=pendientes1.getCargoSolicitante()%>
					  </logic:equal>
					  <logic:equal name="pendientes1" property="mostrarCargo" value="false">
							<%=pendientes1.getSolicitantetxt()%>
					  </logic:equal>
				   </a>
				  </td>
				  <td id="LIN_D<%=indice%>" class="txt_b">
				   <a href="javascript:showCharge('<%=pendientes1.getEmisortxt()%>','<%=pendientes1.getCargoEmisor()%>')" class="ahref_b">
					  <logic:equal name="pendientes1" property="mostrarCargo" value="true">
							<%=pendientes1.getCargoEmisor()%>
					  </logic:equal>
					  <logic:equal name="pendientes1" property="mostrarCargo" value="false">
							<%=pendientes1.getEmisortxt()%>
					  </logic:equal>
				   </a>
				  </td>
				  <td id="LIN_E<%=indice%>" class="txt_b">
				   <a href="javascript:showCharge('<%=pendientes1.getRespbleareatxt()%>','<%=pendientes1.getCargo()%>')" class="ahref_b">
					  <logic:equal name="pendientes1" property="mostrarCargo" value="true">
							<%=pendientes1.getCargo()%>
					  </logic:equal>
					  <logic:equal name="pendientes1" property="mostrarCargo" value="false">
							<%=pendientes1.getRespbleareatxt()%>
					  </logic:equal>
				   </a>
				  </td>
				  
				  <td id="LIN_F<%=indice%>" class="txt_b">
				  	<%=pendientes1.getTitulosplanillas()%>
				  </td>
				  <td id="LIN_G<%=indice%>" class="txt_b">
				  	 <%=HandlerBD.findOrigenSacop(pendientes1.getSacopnum())%>
				  </td>
				  
				  <td id="LIN_H<%=indice%>" class="txt_b">
				        <%=pendientes1.getFechaEstimada()==null?"":pendientes1.getFechaEstimada()%>
				   </td>		   

					<td id="LIN_I<%=indice%>" class="txt_b">&nbsp;
						 <%=pendientes1.getEstadotxt()%>
					</td>
					<td id="LIN_J<%=indice%>" class="txt_b">&nbsp;
						 <%=pendientes1.getNumberTrackingSacop()%>
					</td>
				</tr>
			</logic:iterate>
		</logic:present>
	</table>
</form>
</logic:notPresent>
</body>
</html>
<script type="text/javascript" event="onload" for="window">
	try{
	window.parent.updateCounter(<%=sacopRechazado%>,<%=sacopPendiente%>,<%=sacopCerrado%>);
	}catch(e){}
	
	function visualizar(col){
		alert("principalSacop1Pendiente.jsp2 funcion visualizar darwinuzcategui");
		all="ABCDEFGHIJKZ";
		for(var x=0; x<all.length; x++){
			var c=all.substring(x,x+1);
			document.getElementById("COL_"+c).style.display=(col.indexOf(c)!=-1?"":"none");
			try{
				for(var n=0;n<<%=lineas%>;n++){
					document.getElementById("LIN_"+c+n).style.display=(col.indexOf(c)!=-1?"":"none");
				}
			}catch(e){
			}
		}
	}
	visualizar("<%=HandlerBD.getOptionSacop((int)usuarioActual.getIdPerson())%>");	
	</script>