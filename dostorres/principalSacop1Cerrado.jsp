<!-- principalSacop1Cerrado.jsp -->
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
        
        <%if(request.getParameter("idDocumentRelatedFilter")!=null){%>
        	if((edo!='<%=LoadSacop.edoCerrado%>')) {
        		alert("<%=rb.getString("requestSacop.onlyClose")%>");
        		return ;
        	}
        <%}%>
        
        forma.Idplanillasacop1.value=='';
        try {
        window.parent.parent.info.document.getElementById("printSacopBtn").style.display = "";
        } catch(e){}
        
        if ((edo=='<%=LoadSacop.edoBorrador%>')) {    //edoBorrador
            forma.goTo.value="planillauno";
            forma.edodelsacop.value=edo;
            forma.Idplanillasacop1.value=id;
            forma.submit();
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
            /*forma.goTo.value="planilla6";
            forma.Idplanillasacop1.value=id;
            forma.submit();*/
        	forma.edodelsacop.value=edo;
            forma.goTo.value="planilla6";
            forma.Idplanillasacop1.value=id;
            forma.target="sacopCenter";
            try {
            	window.parent.parent.info.frames.ir('loadSACOPMain.do',"loadSACOPMain.do?goTo=planilla6&edodelsacop="+edo+"&Idplanillasacop1="+id);
            } catch(e) {
            	abrirCentrado("loadSACOPMain.do?goTo=planilla6&edodelsacop="+edo+"&Idplanillasacop1="+id);
            }
        }

	}

	function abrirCentrado(url) {
		var nWidth = screen.availWidth - (100);
		var nHeight = screen.availHeight - (150);
		fullscreen = window
				.open(
						url,
						"fullscreen",
						'top=50,left=50,width='
								+ (nWidth)
								+ ',height ='
								+ (nHeight)
								+ ',fullscreen=no,toolbar=0 ,location=0,directories=0,status=0,menubar=0,resizable=1,scrolling=1,scrollbars=1');
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
    	return "Cerrado";
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
            <td id="COL_A"  class="td_titulo_C borderLeft" width="10%">
                <%=rb.getString("scp.num")%>
            </td>
            <td id="COL_B"  class="td_titulo_C borderLeft" width="10%">
                <%=rb.getString("scp.fechae")%>
            </td>
             <td id="COL_Z"  class="td_titulo_C borderLeft" width="10%">
                <%=rb.getString("scp.fechacierre")%>
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
            <td id="COL_K" class="td_titulo_C borderLeft">
				&nbsp;
            </td>
          </tr>
		<logic:present name="cerrado" scope="session" >
        	<%int cont=0;%>
        	<%int indice=-1;%>
			<logic:iterate id="cerrado1" name="cerrado" type="com.desige.webDocuments.sacop.forms.Plantilla1BD" scope="session" >
                <tr class='fondo_<%=(cont==0?cont++:cont--)%>'>
					<td id="LIN_A<%=++indice%>" >
						<img src="img/chequeado.gif" width="17" height="17">
						 <a href="javascript:showPlanilla(document.solicitudsacopplanilla,<%=cerrado1.getIdplanillasacop1()%>,<%=cerrado1.getEstado()%>);" class="ahreMenu info" >
						   <%=cerrado1.getSacopnum()%>
						   <span>
						   	  <b><%=rb.getString("scp.comments")%>:</b><br>
						   	  <%= cerrado1.getDescripcion() %>
						   	  <br><br>
						   	  <b><%=rb.getString("scp.results")%>:</b><br>
                              <%= cerrado1.getNoconformidades() %>
                           </span>
						</a>
					</td>

				  <td id="LIN_B<%=indice%>" class="txt_b">
				   		<%=ToolsHTML.getSdfShowWithoutHour(cerrado1.getFechaemision())%>
				   </td>
	
				   <td id="LIN_Z<%=indice%>" class="txt_b">
				   		<%=ToolsHTML.getSdfShowWithoutHour(cerrado1.getFechaCierre())%>
				   </td>

					<td id="LIN_C<%=indice%>" class="txt_b">
					<a href="javascript:showCharge('<%=cerrado1.getSolicitantetxt()%>','<%=cerrado1.getCargoSolicitante()%>')" class="ahref_b">
					  <logic:equal name="cerrado1" property="mostrarCargo" value="true">
							<%=cerrado1.getCargoSolicitante()%>
					  </logic:equal>
					  <logic:equal name="cerrado1" property="mostrarCargo" value="false">
							<%=cerrado1.getSolicitantetxt()%>
					  </logic:equal>
					</a>
				  </td>
				  <td id="LIN_D<%=indice%>" class="txt_b">
					<a href="javascript:showCharge('<%=cerrado1.getEmisortxt()%>','<%=cerrado1.getCargoEmisor()%>')" class="ahref_b">
					  <logic:equal name="cerrado1" property="mostrarCargo" value="true">
							<%=cerrado1.getCargoEmisor()%>
					  </logic:equal>
					  <logic:equal name="cerrado1" property="mostrarCargo" value="false">
							<%=cerrado1.getEmisortxt()%>
					  </logic:equal>
					</a>
				  </td>
				  <td id="LIN_E<%=indice%>" class="txt_b">
					<a href="javascript:showCharge('<%=cerrado1.getRespbleareatxt()%>','<%=cerrado1.getCargo()%>')" class="ahref_b">
					  <logic:equal name="cerrado1" property="mostrarCargo" value="true">
							<%=cerrado1.getCargo()%>
					  </logic:equal>
					  <logic:equal name="cerrado1" property="mostrarCargo" value="false">
							<%=cerrado1.getRespbleareatxt()%>
					  </logic:equal>
					</a>
				  </td>
				  <td id="LIN_F<%=indice%>" class="txt_b">
					  <%=cerrado1.getTitulosplanillas()==null?"No definida":cerrado1.getTitulosplanillas()%>
				  </td>
				  <td id="LIN_G<%=indice%>" class="txt_b">
				  	 <%=HandlerBD.findOrigenSacop(cerrado1.getSacopnum())%>
				  </td>
				   <td id="LIN_H<%=indice%>" class="txt_b">
				        <%=cerrado1.getFechaEstimada()==null?"":cerrado1.getFechaEstimada()%>
				   </td>	
				  
					<td id="LIN_I<%=indice%>" class="txt_b">&nbsp;
						 <%=cerrado1.getEstadotxt()%>
						 <logic:notEqual name="cerrado1" property="idplanillasacop1esqueleto" value="-1">
							   &nbsp;<%=rb.getString("scpintouch.intouch")%>
						 </logic:notEqual>
					</td>
					<td id="LIN_J<%=indice%>" class="txt_b">&nbsp;
						 <%=cerrado1.getNumberTrackingSacop()==null?"":cerrado1.getNumberTrackingSacop()%>
					</td>
					 <td id="LIN_K<%=indice%>" class="txt_b">&nbsp;
					   <logic:present name="verHistoricosTambien" scope="request">
						   <logic:equal  name="verHistoricosTambien" value="true">
							   <%=rb.getString("scp.eliminar")%>
						   </logic:equal>
						   <logic:equal  name="verHistoricosTambien" value="false">
								<a href="javascript:deletePlanilla(document.solicitudsacopplanilla,<%=cerrado1.getIdplanillasacop1()%>,<%=idPerson%>,'0');" class="ahreMenu" >
									 <%=rb.getString("scp.eliminar")%>
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
<script type="text/javascript" event="onload" for="window">
	try{
		   window.parent.updateCounter(<%=sacopRechazado%>,<%=sacopPendiente%>,<%=sacopCerrado%>);
	}catch(e){
		/**/
	}
	
	<%if(session.getAttribute("MESSAGE_TRACKING_SACOP")!=null) {%>
		setTimeout("alert('<%=session.getAttribute("MESSAGE_TRACKING_SACOP")%>')",500);	
	<% session.removeAttribute("MESSAGE_TRACKING_SACOP");
	}%>
	
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
