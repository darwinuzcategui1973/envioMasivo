<!-- principalSacop1Main.jsp -->
<%@ page import="java.util.ResourceBundle,
                 com.desige.webDocuments.utils.ToolsHTML,
                 com.desige.webDocuments.utils.Constants,
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
<link href="estilo/estilo.css" rel="stylesheet" type="text/css">

<style>
.cajaDiv {
	float:left;
	border:3px solid #cdcdcd;
	margin:3px;
	width:300px;	
	-moz-border-radius: 15px;
	border-radius: 15px;
}
</style>

<script language="JavaScript">
	function showPlanilla(forma,id,edo){
        //alert('id='+id+' edo='+edo+' forma.action='+forma.action);
        forma.Idplanillasacop1.value=='';
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
    
    function seleccionarTipo(tipo) {
    	var items = <%=Constants.ACTION_TYPE.length%>;
    	<%for(int i=0; i<Constants.ACTION_TYPE.length;i++){%>
    		document.getElementById("action<%=i%>").src=actionImgBlack[<%=i%>].src;
    		if(<%=i%>==tipo) { document.getElementById("action<%=i%>").src=actionImg[<%=i%>].src; }
    	<%}%>
    	
    	document.solicitudsacop.correcpreven.value=tipo;
    	document.solicitudsacop.origensacop.value

   		window.parent.frames['sacopEast'].fondo(tipo);
    }
    
    function anterior() {
		// nada
    }

    function siguiente() {
		crearSacop2(document.solicitudsacop);
    }
    
    var actionImg = new Array();
    var actionImgBlack = new Array();
    var actionImgBig = new Array();
    <%for(int i=0; i<Constants.ACTION_TYPE.length;i++){%>
    	actionImg[<%=i%>] = new Image(); actionImg[<%=i%>].src="images/action<%=i%>.gif";
    	actionImgBlack[<%=i%>] = new Image(); actionImgBlack[<%=i%>].src="images/actionBlack<%=i%>.gif";
    	actionImgBig[<%=i%>] = new Image(); actionImgBig[<%=i%>].src="images/actionBig<%=i%>.gif";
    <%}%>

</script>
</head>

<body class="bodyInternas" <%=onLoad.toString()%>>
<!--<a href="javascript:reporteLista(document.reporte);" class="ahreMenu" >-->
        <%--<%=rb.getString("lst.reporte")%>--%>
<!--</a>-->
 <form name=frmvisualizarTodos action='VisualizarTodos.do'>
 </form>
<logic:notPresent name="crearSacop" scope="request">
<div class="none">
<form name="reporte" action="CrearReporteSacop.do">
<table cellSpacing=0 cellPadding=2 align=center border=0 width="100%">
        <tr>
            <td>
                <input type="hidden" name="conjuntosacop">
                &nbsp;
                &nbsp;
                <input type="button" class="boton" value="<%=rb.getString("scp.crear")%> <%=rb.getString("scp.sacop")%>" onClick="javascript:crearSacop(document.frmcrearSacop);" name="btnscrearr"/>
                &nbsp;
                &nbsp;
                <input type="button" class="boton" value="<%=rb.getString("lst.reporte")%>" onClick="javascript:reporteLista(document.reporte);" name="btnreporte"/>
                &nbsp;
                &nbsp;
                <input type="button" class="boton" value="<%=rb.getString("scp.visualizartodos")%>" onClick="javascript:visualizarTodos();" name="btnvisualizartodos"/>
            </td>
            <td>
            </td>
        </tr>

    </table>
</form>
</div>
</logic:notPresent>
<table cellSpacing=0 cellPadding=2 align=center border=0 width="100%" style="display:none1">
    <logic:notPresent name="crearSacop" scope="request">
        <form name=frmcrearSacop action='CrearSacop.do'>
            <tr>
                <td>

                </td>
            </tr>
       </form>
    </logic:notPresent>
    <logic:present name="crearSacop" scope="request">
    <form name=solicitudsacop action='loadSACOPMain.do' style="margin:0;">
    <input type='hidden' name=goTo value=planillauno>
    <input type="hidden" name="correcpreven" value='0'>
    <input type="hidden" name="idDocRelated" value='<%=session.getAttribute("idDocRelated")%>'>

	<logic:present name="noConformidadesDetail">
    <logic:iterate id="bean" name="noConformidadesDetail" type="com.desige.webDocuments.document.forms.BaseDocumentForm">
    	<div class="td_gris_l" style="font-size:14px;padding-left:10px;">
    		<%=rb.getString("doc.link")%>:
    		<b>
            <bean:write name="bean" property="prefix"/>
            <bean:write name="bean" property="number"/>
            <bean:write name="bean" property="nameDocument"/>
            <bean:write name="bean" property="typeDocument"/>
            </b><br><br>
        </div>
    </logic:iterate>
    </logic:present>
   
	<table border="0" width="100%">
			<tr>
			<td valign="top">
  				<fieldset style="margin-left:6px;margin-right:6px; border: 1px solid #9f9f9f;">
  					<legend class="td_gris_l"><%=rb.getString("scp.orgsacop")%></legend>
  				<div style="border:0px solid black;text-align:center;margin-bottom:6px;">
				    <select name='origensacop' size="10" style="width:100%;" >
				        <logic:present name="titulosplanillassacop" scope="session" >
				            <logic:iterate id="bean" name="titulosplanillassacop" type="com.desige.webDocuments.utils.beans.Search" scope="session" >
				                <option value="<%=bean.getId()%>" style="background-color:#ebebeb;"><%=bean.getDescript()%> </option>
				            </logic:iterate>
				        </logic:present>
				    </select>
                </div>
  				</fieldset>
			</td>
		</tr>

		<tr>
			<td width="70%" valign="top">
				<div >   
  				<%for(int i=0; i<Constants.ACTION_TYPE.length;i++){%>
  					<div class="cajaDiv" onclick="seleccionarTipo(<%=i%>)">
				      	<table border="0" cellspacing="0" cellpadding="0">
				      		<tr>
				      			<td class="roundedcornr_content_697093" >
							   		<img id="action<%=i%>" src="images/actionBlack<%=i%>.gif"  style="cursor:pointer;width:44px;height:44px;"/>
						   		</td>
				      			<td nowrap>
							      	<p style="font-family:Arial;font-size:12px;" >
					      				&nbsp;<%=rb.getString("scp.actionType".concat(String.valueOf(i)))%>
							      	</p>
				      			</td>
				   			</tr>
				   		</table>
				   	</div>
				<%}%>
				</div>
			</td>
			</tr>
	</table>

   <div class="round" style="display:none">
       <span class="top"><span></span></span>
       <div class="contenido">           
           Esto es el contenido del DIV con bordes redondeados
        </div>
        <span class="bottom"><span></span></span>
    </div>
	</form>
   </logic:present>
    <!------------------------------------------------------------------------------>
    
</body>
</html>
<script type="text/javascript" event="onload" for="window">
	
	try{
		//window.parent.updateCounter(<%=sacopRechazado%>,<%=sacopPendiente%>,<%=sacopCerrado%>);
		//tratamos de actualizar la bandeja en caso de que vengamos de una
		
		if(eval("typeof(" + window.parent.parent.updateCounter + ") == typeof(Function)")) {
			window.parent.parent.updateCounter(<%=sacopRechazado%>,<%=sacopPendiente%>,<%=sacopCerrado%>);
		}
		
	    if((window.parent.parent.frames[1].document.location.href != null)
	    		&& (window.parent.parent.frames[1].document.location.href != "")){
	    	window.open(window.parent.parent.frames[1].document.location.href, "bandeja");
	    } else {
	    	window.open("principalSacop1Tit.jsp","bandejaTit");
	    	window.parent.parent.frames[1].document.location.href = "";
	    }
	}catch(e){
		alert("error: " + e);
	}
	
</script>
