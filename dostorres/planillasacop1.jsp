<!--  planillasacop1.jsp -->
<%@page import="com.desige.webDocuments.persistent.managers.HandlerProcesosSacop"%>
<%@page import="com.desige.webDocuments.persistent.managers.HandlerDBUser"%>
<%@page import="java.util.Collection"%>
<%@page import="org.apache.poi.util.SystemOutLogger"%>
<%@page import="com.itextpdf.text.log.SysoLogger"%>
<%@ page import="java.util.Locale,
                 java.util.ResourceBundle,
                 java.text.SimpleDateFormat,
                 com.desige.webDocuments.utils.ToolsHTML,
                 com.desige.webDocuments.workFlows.forms.DataUserWorkFlowForm,
                 com.desige.webDocuments.persistent.managers.HandlerDocuments,
                 com.desige.webDocuments.persistent.managers.HandlerNorms,
                 com.focus.qweb.to.PossibleCauseTO,
                 com.desige.webDocuments.document.forms.BaseDocumentForm,
                 java.util.Collection,
                 com.focus.qweb.to.ActionRecommendedTO,
                 java.util.ArrayList,
                 com.desige.webDocuments.utils.Constants,
                 com.desige.webDocuments.sacop.actions.LoadSacop"%>
<%@ page import="java.util.Date"%>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<%@ page language="java" %>
<bean:define id="data" name="sacoplantilla" type="com.desige.webDocuments.sacop.forms.plantilla1" scope="request" />
<%

    //cambio uno darwinuzcategui
     Collection norms = null;
    ResourceBundle rb = ToolsHTML.getBundle(request);
    StringBuffer onLoad = new StringBuffer("");
    String info = (String) ToolsHTML.getAttribute(request,"info",true);
    if (!ToolsHTML.isEmptyOrNull(info)) {
        onLoad.append(" onLoad=\"alert('").append(info).append("')\"");
    }

    if (ToolsHTML.checkValue(info)){
        onLoad.append(";alert('").append(info).append("')");
    }
    if (onLoad.length()>0){
        onLoad.append("\"");
    }
    ToolsHTML.clearSession(session,"application.sacop");
    String styleTxt = "width: 300px; height: 19px";
    String numGo = request.getParameter("numGo");
    String responsable = request.getParameter("responsable")!=null?request.getParameter("responsable"):"";
    if (ToolsHTML.isEmptyOrNull(responsable)){
       responsable = (String) ToolsHTML.getAttribute(request,"responsable",true);
    }
    String areaAfectada = (String) ToolsHTML.getAttribute(request,"areaAfectada",true);
    if (ToolsHTML.isEmptyOrNull(numGo)) {
        numGo = "1";
    }
    String titleForm="";
    String mostrardetrestext=request.getAttribute("mostrardetrestext")!=null?(String)request.getAttribute("mostrardetrestext"):"0";
    int cuantosText=Integer.parseInt(mostrardetrestext);
    java.util.Date fech= new java.util.Date();
    String fechaHoy=ToolsHTML.date.format(fech);
    String edoBorradorenbd=request.getParameter("edoBorradorenbd")!=null?request.getParameter("edoBorradorenbd"):"";
    String nodoActual=request.getParameter("nodoActual")!=null?request.getParameter("nodoActual"):"0";
    if (ToolsHTML.isEmptyOrNull(edoBorradorenbd)){
       edoBorradorenbd = (String) ToolsHTML.getAttribute(request,"edoBorradorenbd",true);
    }
    
    DataUserWorkFlowForm docRel = (DataUserWorkFlowForm)request.getAttribute("documentoRelacionado");
    
    String tipoSacop = data.getCorrecpreven();
    int nTipoSacop = -1;
    if(data.getCorrecpreven()!=null && data.getCorrecpreven().trim().length()>0){
    	nTipoSacop = Integer.parseInt(data.getCorrecpreven());
    }

    String selected;
    
    ArrayList listPossibleCause = (ArrayList)request.getAttribute("listPossibleCause");
    PossibleCauseTO possibleTO = null;
 // todos lo processos cuando es nuevo Darwin Uzcategui 
 	Collection procesosSacop = HandlerProcesosSacop.getAllProcesosSacop();
     request.setAttribute("procesosSacop", procesosSacop);
 
    
%>
<html>
<head>
<title><%=rb.getString("principal.title")%></title>

<jsp:include page="meta.jsp" />

<style>
body {
	font-family:Arial;
}
.table-head td {
	border-collapse: collapse;
	border:2px solid #cdcdcd;
	font-family:Arial;
	padding:10px;
}
</style>
 
<link href="estilo/estilo.css" rel="stylesheet" type="text/css">
<style>select {background-color:#ebebeb;height:320;font-size:9pt;}</style>

<script type="text/javascript" src="./estilo/funciones.js"></script>
<script type="text/javascript" src="./estilo/fechas.js"></script>
<script type="text/javascript" src="./script/sacopJS.js"></script>
<script type="text/javascript" src="estilo/popcalendar2inner.js"></script>

<script type="text/javascript">
var usuariosArea1 = new Array();

var nodo1=false;
var nodo2=false;
var nodo7=false;
var nodo8=false;
var nodo9=false;
var nodo11=false;

//
var varNormsiso;
var varNormISO;
var varNormsisoSelected;

//cambio dos darwinuzcategui
function showNormasEnviarSeleccion(normsiso,normISO,normsisoSelected){
	
	varNormsiso = normsiso;
	varNormISO = normISO;
	varNormsisoSelected = normsisoSelected;
	// console.log("*****************showNormasEnviarSeleccio***************")
	showNormsPopUpFromDocument("normISO");
	
}

function showMover(normsisoSelected,normsiso){
	varNormsisoSelected = normsisoSelected;
	varNormsiso = normsiso;
	mover(varNormsisoSelected,varNormsiso);
}

function setNormasAfter() {
	console.log("setNormasAfter = ");
	moverMultiple(varNormsiso,varNormISO,varNormsisoSelected );
	normISO.value="";
}


function anterior() {
	window.parent.frames['sacopEast'].anterior();
}
function siguiente() {
	window.parent.frames['sacopEast'].siguiente();
}

function closeMiniText(value,nameInput){
	document.getElementById("_"+nameInput).innerHTML=value;
}

function ir(nodo,openWnd){

	window.parent.frames['sacopSouth'].showAnteriorBtn(true);
	window.parent.frames['sacopSouth'].showSiguienteBtn(true);
    window.parent.frames['sacopSouth'].showEmitirBtn(true);
    window.parent.frames['sacopSouth'].showGuardarBtn(true);
    
	document.sacoplantilla.nodoActual.value=nodo;
	for(var i=0; i<20; i++) {
		try{
			document.getElementById("nodo"+i).className="none";
		}catch(e){break;}
	}
	document.getElementById("nodo"+nodo).className="";
	if(nodo==0){
		window.parent.frames['sacopSouth'].showAnteriorBtn(false);
	}
	if(nodo==1 && !nodo1) {
		loadNoconformidadesRefFrame('0','exec','sacoplantilla','noconformidadesref',0);
		nodo1=true;
	}

	if(nodo==9) {
		window.parent.frames['sacopSouth'].showSiguienteBtn(false);
		loadSacops('0','exec','sacoplantilla','sacop_relacionadas',0);  //11
        nodo9=true;
		//alert(window.parent.frames['sacopEast'].document.getElementById("nodo9Form"));
	}
}    
function ocultar(nodo){
	document.getElementById("nodo"+nodo).className="none";
}


function loadSacops(number,input,nameForm,value,type) {
    var searchForm = window.parent.frames['sacopEast'].document.getElementById("nodo9Form");
	var extraParameters = "";
	    
	//se desea buscar SACOPs, entonces colocamos en el request
	//los parametros respectivos
	extraParameters += "&idSacop=" + searchForm.idSacop.value;
	extraParameters += "&emisorSacop=" + searchForm.emisorSacop.value;
	extraParameters += "&responsableSacop=" + searchForm.responsableSacop.value;
	extraParameters += "&areaResponsableSacop=" + searchForm.areaResponsableSacop.value;
	extraParameters += "&efectivaSacop=" + searchForm.efectivaSacop.value;
	extraParameters += "&idProceso=" + searchForm.idProceso.value;
	extraParameters += "&listRegisterUnique=" + searchForm.listRegisterUnique.value;
	
	abrirVentana("Sacop_RelacinadasFrame.do?number="+number+"&input="+input+"&nameForma="+nameForm+"&value="+value+"&type="+type+"&userAssociate="+document.sacoplantilla.sacop_relacionadas.value+extraParameters,1000,400,value)
 }
 function saveSacopRelacionadas(valor) {
	document.sacoplantilla.sacop_relacionadas.value=valor;
 }
 function saveNoConformidades(valor) {
	document.sacoplantilla.noconformidadesref.value=valor;
 }

function showFileSacop(idplanillasacop1) {
	var idplanillasacop1 = escape(idplanillasacop1);
    abrirVentana("viewDocumentSacop.jsp?idplanillasacop1=" + idplanillasacop1 ,800,600);
}

function getDateExpire(dateField,nameForm,dateValue,text){
    window.open("getDate.jsp?field=" + dateField + "&nameForm="+nameForm+"&valueForm="+dateValue+"&text="+'scp.fecha',"","width=450,height=250,resizable=no,scrollbars=yes,left=300,top=250");
}

function showCharge(userName,charge) {
    charge = charge.replace(/\[/gi,"-").replace(/\]/gi,"-");
        window.open("showCharge.jsp?userName="+userName+"&charge="+charge, "WebDocuments", "resizable=no,scrollbars=yes,statusbar=yes,width=300,height=200,left=410,top=250");
}

function validar(forma,edo){
	

    if (edo=='<%=LoadSacop.edoEmitida%>'){
                norms = contSelectInList(forma.normsisoSelected);
                procesosSacop= contSelectInList(forma.proceafectadosSelected);
                usuarios=contSelectInList(forma.usersSelected);
                
                if ((""==forma.clasificacionSACOP.value)) {
                    alert('<%=rb.getString("scp.val15")%>');
                    window.parent.frames['sacopEast'].ir(0);
                    return false;
                }
                
                if ((procesosSacop==0)) {
                    alert('<%=rb.getString("scp.vald3")%>');
	        		window.parent.frames['sacopEast'].ir(2);
                    return false;
                }

				var isHasResponsable=false;                
				var x=document.getElementById("responsable");
				for (var i = 0; i < x.options.length; i++) {
					if(x.options[i].selected ==true){
						isHasResponsable = true;
						break;
					}
				}
                if (!isHasResponsable) {
                    alert('<%=rb.getString("scp.vald0")%>');
	        		window.parent.frames['sacopEast'].ir(3);
                    return false;
                }


                if ((usuarios==0)) {
                    alert('<%=rb.getString("scp.vald3_1")%>');
	        		window.parent.frames['sacopEast'].ir(5);
                    return false;
                }

          //  if (Trim(forma.descripcion.value)==""){
          //      alert(' < %=rb.getString("scp.vald4")%>');
          //      return false;
          //  }
            if (Trim(forma.causasnoconformidad.value)==""){
                alert('<%=rb.getString("scp.vald5")%>');
        		window.parent.frames['sacopEast'].ir(6);
                return false;
            }
            if (Trim(forma.accionrecomendada.value)==""){
                alert('<%=rb.getString("scp.vald6")%>');
        		window.parent.frames['sacopEast'].ir(7);
                return false;
            }
            if (hayComillas(forma.descripcion.value) ||
                hayComillas(forma.causasnoconformidad.value) ||
                hayComillas(forma.accionrecomendada.value)
               ){
                         alert('<%=rb.getString("scp.vald2")%>');
                         return false;
                }
           }else{
                if (forma.nameFile.value!=""){
                   alert('<%=rb.getString("scp.val14")%>');
                }
                norms = contSelectInList(forma.normsisoSelected);
                procesosSacop= contSelectInList(forma.proceafectadosSelected);
                usuarios=contSelectInList(forma.usersSelected);
           }

           return true;
    }
    
    function vercargo(listDer,listIzq,vertexto){
        var items = listDer.length;
        var items2 = listIzq.length;
 		var	newItem;
			for (row = 0; row < items; row++){
				var valor = listDer[row];
				if (valor.selected) {
                    for(j=0;j<items2;j++){
                        if (listIzq[j].value==listDer[row].value){
                            vertexto[0].text=listIzq[j].text;
                            //para salir del ciclo
                            j=items2+1;
                        }
                    }
                //para salir del ciclo
                 row=items+1;
    			}
	 		}
    }
 <logic:present name="esAuditoria_o_Wonderware" scope="request">
     <logic:notPresent name="EsnuevanoSacop" scope="request">
         function eliminarPreconfigurada(forma,edo,eliminar,deshabilitar) {
            var row = 0;
            var swEnviar=false;
            var swEmitir=edo=='<%=LoadSacop.edoEmitida%>'
             //EN CASO DE QUE SE QUIERA ELIMINAR
             if(eliminar=='1'){
                 forma.continuar_Sacop_Intouch.value="0";
                 if(confirm('<%=rb.getString("scpintouch.cerrarConfiguracionIII")%>')) {
                      forma.continuar_Sacop_Intouch.value="1";
                 }//else{
                   // forma.continuar_Sacop_Intouch.checked=false;
                 //}
            }
            //EN CASO QUE SE QUIERA DESHABILITAR, debe ser en 1
             forma.deshabilitar_Sacop_Intouch.value="0";

             if (deshabilitar=='1'){
                 forma.deshabilitar_Sacop_Intouch.value="1";
             }

             forma.Deshabilitar_o_Eliminar.value="1";
             forma.edodelsacop.value=edo;

             forma.submit();
        }

      </logic:notPresent>
</logic:present>    
	function guardar() {
		salvar(document.sacoplantilla,'0');
	}
	function emitir() {
		console.log("...emitir()");
		salvar(document.sacoplantilla,'1');
	}


    function salvar(forma,edo) {
    
    	// salvamos los comentarios de los iframe ejecutando
    	// closeWin()
    	for(var x=0;x<window.frames.length;x++){
    		try{
    			window.frames[x].closeWin();
    		}catch(e){};
    	}
    
        if (""==forma.origensacop.value || "0"==forma.origensacop.value) {
            alert('<%=rb.getString("scp.val16")%>');
            window.parent.frames['sacopEast'].ir(0);
            return false;
        }
    
        if (""==forma.correcpreven.value) {
            alert('<%=rb.getString("scp.val17")%>');
            window.parent.frames['sacopEast'].ir(0);
            return false;
        }

        var row = 0;
        var swEnviar=false;
        var swEmitir=(edo=='<%=LoadSacop.edoEmitida%>');
        /*
        if (swEmitir){
            <%if ("-1".equalsIgnoreCase(areaAfectada)){%>
               alert('<%=rb.getString("scp.val13")%>');
			   window.parent.frames['sacopEast'].ir(3);
               return false;
            <%}%>
        }
        */
        
        var dateIsValid = false;
        //alert("'" + forma.fechaWhenDiscovered.value + "'");
        if(forma.fechaWhenDiscovered.value == ""){
        	dateIsValid = false;
        } else {
        	var d = getDateSacopModule(forma.fechaWhenDiscovered.value);
        	var today = new Date();
        	//alert(forma.fechaWhenDiscovered.value + " - " + today);
        	//alert(d.getTime() + " - " + today.getTime());
        	
        	if(d.getTime() > today.getTime()){
        		dateIsValid = false;
        	} else {
        		dateIsValid = true;
        	}
        }
        //alert(dateIsValid);
        
        if(dateIsValid){
        	if (validar(forma,edo)) {

                forma.edodelsacop.value=edo;
                forma.btnOK.disabled=true;
                forma.btnsvb.disabled=true;
                forma.btnCancel.disabled=true;
                window.parent.parent.administrarCancelar();
                forma.target="info";
                //alert(forma.action); return false;
                forma.submit();
            }
        } else {
        	if(forma.fechaWhenDiscovered.value == ""){
        		alert("<%= rb.getString("scp.whenDiscoveredNotEmpty") %>");
        	} else {
        		alert("<%= rb.getString("scp.whenDiscoveredGreaterToday") %>");
        	}
        	
        }
    }

    function editField(pages,input,value,forma){
        var hWnd = null;
        hWnd = window.open(pages+"?input="+input+"&value="+value+"&nameForma="+forma.name,"Catalogo","width=400,height=300,resizable=yes,scrollbars=yes,left=300,top=250");
        if ((document.window != null) && (!hWnd.opener)){
            hWnd.opener = document.window;
        }
    }

   function cancelarUnaSacopNormal(forma) {
        forma.action="loadSACOPMain.do?goTo=reload";
        forma.submit();
    }
   function cancelar(forma,irwonderwareintouch) {
        forma.action=irwonderwareintouch;
        forma.submit();
    }
    function cancelarsacoprelacionada(forma){
        window.close();
    }






function buscarArea(forma,isBuscarProceso){

	isBuscarProceso = isBuscarProceso || false;
	
    var row = 0;
    norms = contSelectInList(forma.normsisoSelected);
    procesosSacop= contSelectInList(forma.proceafectadosSelected);
    usuarios=contSelectInList(forma.usersSelected);
    if (forma.responsable.value.length>0) {
        forma.action="RefreshCrearPlanillaSacop.do?responsablee="+forma.responsable.value+"&nodoActual="+forma.nodoActual.value+"&isBuscarProceso="+isBuscarProceso;
        //alert(forma.action);
        forma.submit();
    }
}
function buscarResponsable(forma,isBuscarResponsable){

	while(forma.responsable.length>0) {
		forma.responsable.remove(forma.responsable.length-1);
	}
	for(var i=0; i<forma.proceafectadosSelected.length; i++) {
		for(var x=0; x<usuariosArea1.length; x++) {
			if(usuariosArea1[x][0]==forma.proceafectadosSelected.options[i].value) {
				var option = document.createElement("option");
				option.text = usuariosArea1[x][2];
				option.value = usuariosArea1[x][1];
				forma.responsable.add(option);		
			}	
		}
	}
	
}
function evaluaChequeo(forma,parametro,titleForm){
            if (parametro=='d'){
                var hWnd = null;
                var title='<%=rb.getString("scp.desc")%>';
                showWindow('SacopMiniText.do','descripcion','sacoplantilla',forma.descripcion.value,'<%=rb.getString("scp.desc")%>',800,450,'0');
            }else if (parametro=='f'){
                showWindow('SacopMiniText.do','causasnoconformidad','sacoplantilla',forma.causasnoconformidad.value,'<%=rb.getString("scp.pblecausasnoconformidad1")%>',800,450,'0');
            }else if (parametro=='a'){
                showWindow('SacopMiniText.do','accionrecomendada','sacoplantilla',forma.accionrecomendada.value,'<%=rb.getString("scp.acrecomendadas")%>',800,450,'0');
            }else if (parametro=='n'){
                showWindow('SacopMiniText.do','noconformidades','sacoplantilla',forma.noconformidades.value,'<%=rb.getString("scp.noconformidades")%>',800,450,'0');
            }else if (parametro=='ref'){
                showWindow('SacopMiniText.do','noconformidadesref','sacoplantilla',forma.noconformidadesref.value,'<%=rb.getString("scp.sourceDocument")%>' ,800,450,'0');
            }

}

function selectOptions(cbo,obj) {
	var opt = cbo.options;
	var ids="";
	var sep = ",";
	for(var i=0; i<opt.length;i++) {
		if(opt[i].selected) {
			ids += sep + opt[i].value;
		}
	}
	ids += sep;
	obj.value = ids;	
}

       // Validar para wonderware auditoria, en caso que ya este registrada en planillasacop1 la planilla esqueleto
       // queda como historico y no se puede modificar.   
        <%String swDisabledIntouchAuditoria="";%>
     <logic:present name="tagsnamesSeleccionados" scope="session">
            <logic:present name="NoModificarSacopIntouch" scope="request">
                <%swDisabledIntouchAuditoria="disabled=\"true\";";%>
            </logic:present>
     </logic:present>

</script>
</head>

<body class="bodyInternas" <%=onLoad.toString()%>>

<table cellSpacing=0 cellPadding=0 align=center border="0" width="100%">
  <tr>
     <td width="100%" valign="top">
        <html:form action="/planillasacop1Reload.do" enctype="multipart/form-data" style="margin:0;">
            <html:hidden property="id" value='<%=data.getId()%>'/>
            <html:hidden property="edodelsacop"/>

            <input type="hidden" name="nodoActual" value='<%=nodoActual%>'>

            <input type="hidden" name="edoBorradorenbd" value='<%=edoBorradorenbd%>'>
            <input type="hidden" name="scpdesc" value='<%=rb.getString("scp.desc")%>'>
            <input type="hidden" name="creandosacop" value="1">
            <input type="hidden" name="mostrardetrestext" value='<%=cuantosText%>'>
            <input type="hidden" name="DbeCerrarSacopIntouchAbierta" value="">
            <input type="hidden" name="idplanillasacop1esqueleto" value='<%=data.getIdplanillasacop1esqueleto()%>'>
            <input type="hidden" name="estadoEsqueletoConfiguradoSacop" value="<%=(String)request.getAttribute("estadoEsqueletoConfiguradoSacop")%>">
            <input type="hidden" name="deshabilitar_Sacop_Intouch" value='0'>
            <input type="hidden" name="continuar_Sacop_Intouch" value='0'>
            <input type="hidden"  name="Deshabilitar_o_Eliminar" value='0'>
            <input type="hidden"  name="usuarioSacops1" value='<%=data.getUsuarioSacops1()%>'>
            
            <input type="hidden"  name="idDocumentRelated" value='<%=data.getIdDocumentRelated()!=null?data.getIdDocumentRelated():""%>'>
            
            <table align=center border="0" width="100%"  cellSpacing=0 cellPadding="0">
            	<tr style="display:none">
                	<td colspan="5">
                    	<table class="clsTableTitle" width="100%" cellSpacing="0" cellPadding="2" align=center border="0">
                        <tbody>
                            <tr>
                                <td class="td_title_bc" width="*" height="18">
                                    <%titleForm=rb.getString("scp.actionType".concat(tipoSacop));%>
                                    <%=titleForm%>
                                </td>
                            </tr>
                        </tbody>
                    	</table>
                    </td>
                </tr>
                <tr id="nodo0" >
                	<td colspan="5"  align="center">
                    	<table cellSpacing="5" cellPadding="0" align=center border="0" width="70%">
                            <tr style="display:none;">
			                     <td class="td_gris_l" width="26%" valign="middle">
			                              <%=rb.getString("reg.new.registerClass")%>
			                     </td>
			                     <td class="td_gris_l" width="26%" valign="middle">
			                     	<%--Constants.registerclassTable.get(data.get())--%>
			                     </td>
			                </tr>

							<%if(data.getIdDocumentRelated()!=null && !data.getIdDocumentRelated().equals("")) {%>
                            <tr>
			                     <td class="td_gris_l" width="26%" valign="middle">
			                        <%=rb.getString("reg.new.registerClass")%>
			                     </td>
			                     <td class="td_gris_l caja">
			                     	<%=HandlerDocuments.getTypeRegisterClass(data.getIdDocumentRelated())%>
			                     	
			                     </td>
			                </tr>
			                <%}%>

			                <tr>
			                     <td class="td_gris_l" width="26%" valign="middle">
			                              <%=rb.getString("scp.origin")%> 
			                     </td>
						         <td width="*" class="td_gris_l" >
	                                <select name='origensacop' style="width:90%;background-color:#ebebeb;height:25px;" >
	                                	<%if(data.getOrigensacop()==null || data.getOrigensacop().equals("0")) {%>
	                                	<option value="0" >No definida</option>
	                                	<%}%>
	                                    <logic:present name="titulosplanillassacop" scope="session" >
	                                        <logic:iterate id="bean" name="titulosplanillassacop" type="com.desige.webDocuments.utils.beans.Search" scope="session" >
	                                            <option value="<%=bean.getId()%>" <%=bean.getId().equals(data.getOrigensacop())?"selected":""%> style="background-color:#ebebeb;" ><%=bean.getDescript()%> </option>
	                                        </logic:iterate>
	                                    </logic:present>
	                                </select>
						         	
						         </td>
						    </tr>

                            <tr>
			                     <td class="td_gris_l" width="26%" valign="middle">
			                              <%=rb.getString("scp.actionType")%>
			                     </td>
			                     <td class="td_gris_l" width="26%" valign="middle">
										<select name="correcpreven" style="width:90%;background-color:#ebebeb;height:25px;">
											<%if(nTipoSacop==-1){%>
				                     		<option value="" >No definida</option>
				                     		<%}%>
											<%for(int x=0;x<Constants.ACTION_TYPE.length;x++){%>
												<option value='<%=x%>' <%=nTipoSacop==x?"selected":""%>><%=rb.getString("scp.actionType".concat(String.valueOf(x)))%></option>
											<%}%>
										</select>
			                     </td>
			                </tr>
			                <tr>
			                     <td class="td_gris_l" width="26%"  valign="middle">
			                              <%=rb.getString("scp.applicant")%>.
			                     </td>
						         <td width="*" class="td_gris_l caja" >
			                              <%String nom="";
			                                  nom=data.getNombres()+" " + data.getApellidos();
			                                       nom=nom!=null?nom:"";
			                              %>
			                            <html:hidden property="nombres"  value='<%=data.getNombres()%>'/>
			                            <html:hidden property="apellidos"  value='<%=data.getApellidos()%>'/>
			                            <html:hidden property="cargo"  value='<%=data.getCargo()%>'/>
			                            <html:hidden property="user" value='<%=data.getUser()%>'/>
						         
			                             <a href="javascript:showCharge('<%=data.getSolicitantetxt()%>','<%=data.getCargoSolicitante()%>')" class="ahref_b">
			                             <logic:present name="showCharge" scope="session">
			                                   <%=data.getCargoSolicitante()%>
			                             </logic:present>
			                             <logic:notPresent name="showCharge" scope="session">
			                                  <%=data.getSolicitantetxt()%>
			                             </logic:notPresent>
			                             </a>
						         </td>
			                </tr>
			                <tr>
			                     <td class="td_gris_l" width="26%" height="26" valign="middle">
			                          <%=rb.getString("scp.requestDate")%>:
			   	                 </td>
			                     <td class="td_gris_l caja">
			                         <html:hidden property="fechaSacops1" value='<%=data.getFechaSacops1()!=null?data.getFechaSacops1():""%>'/>
			                         <%=data.getFechaSacops1()!=null?data.getFechaSacops1():""%>
			                     </td>
			                </tr>
			                <tr>
			                     <td class="td_gris_l" width="26%" height="26" valign="middle">
			                          <%=rb.getString("scp.edo")%>:
			   	                 </td>
			                     <td class="td_gris_l caja">
			                         <%=data.getEdodelsacop()!=null?data.getEdodelsacop():""%>
			                     </td>
			                </tr>
			                <tr>
			                    <td class="td_gris_l" width="26%" height="26" valign="middle">
			                          <%=rb.getString("scp.fechae")%>:
			
			                    </td>
			                    <td class="td_gris_l caja">
			                         <html:hidden property="fechaEmision" value='<%=data.getFechaEmision()!=null?data.getFechaEmision():""%>'/>
			                         <%=data.getFechaEmision()!=null?data.getFechaEmision():""%>
			                    </td>
			                </tr>
                            <tr>
                                <td>
                                    <span class="td_gris_l">
                                        <%= rb.getString("scp.fechaWhenDiscovered") %> 
                                    </span>
                                </td>
                                <td class="td_gris_l caja">
                                	<%if(data.getFechaWhenDiscovered()==null || data.getFechaWhenDiscovered().trim().equals("")){%>
										<html:text property="fechaWhenDiscovered" maxlength="10" size="12" value="" readonly="readonly" />
			                			<span id="calendario"></span>
		                				<a href="#" onclick='showCalendarDDMMYYYY(document.sacoplantilla.fechaWhenDiscovered,"calendario")' ><img src="images/calendario2.gif" border="1" ></a>
	                                    
                                	<%} else {%>
	                                	<%=data.getFechaWhenDiscovered()!=null?data.getFechaWhenDiscovered():""%>
	                                    <html:hidden property="fechaWhenDiscovered" value='<%=data.getFechaWhenDiscovered()!=null?data.getFechaWhenDiscovered():""%>' />
                                    <%}%>
                                </td>
                            </tr>
			                <tr>
			                     <td class="td_gris_l" width="26%" height="26" valign="top">
			                          <%=rb.getString("requestSacop.descriptionSacop")%>:
			   	                 </td>
                                <td class="td_gris_l">
                                	<html:textarea property="descripcion" value='<%=data.getDescripcion()%>' style="width:100%;margin:0px;" rows="5" />
			                     </td>
			                </tr>
			                <tr style="display:none;">
			                     <td class="td_gris_l" width="26%" height="26" valign="middle">
			                          <%=rb.getString("scp.comments")%>:
			   	                 </td>
			                     <td class="td_gris_l caja">
			                         
			                     </td>
			                </tr>
			                
			                <tr style="display:none;">
			                    <td class="td_gris_l" width="26%" height="26" valign="middle">
			                                <%=rb.getString("scp.orgsacop")%>:
			                    </td>
			                    <td class="td_gris_l caja">
			                       <html:hidden property="origensacoptxt" value='<%=data.getOrigensacoptxt()%>'/>
			                        <%=data.getOrigensacoptxt()%>
			                     </td>
			                </tr>
			                <tr >
                                <td class="td_gris_l" width="26%" height="26" valign="middle">
                                            <%=rb.getString("scp.clasificacionsacop")%>:
                                </td>
                                <td class="td_gris_l">
                                   <select name="clasificacionSACOP" style="width:100%; height:25px;">
                                        <logic:present name="clasificacionPlanillasSacop" scope="request">
                                        <logic:iterate id="bean" name="clasificacionPlanillasSacop" scope="request" type="com.desige.webDocuments.utils.beans.Search">
                                            <% 
                                                String printSelected = "";
                                                if(data.getIdClasificacion() == Integer.parseInt(bean.getId())){
                                                	printSelected = " selected";
                                                } else {
                                                	if(bean.getId().equals(request.getAttribute("idClasificacion"))){
                                                		printSelected = " selected";
                                                    }
                                                }
                                            %>
                                            <option value="<%=bean.getId()%>" <%=printSelected%>><%=bean.getDescript()%> </option>
                                        </logic:iterate>
                                        </logic:present>
                                   </select>
                                </td>
                            </tr>
                            <tr>
			                     <td class="td_gris_l" colspan="2">
			                          <br>
			   	                 </td>
                            </tr>
                            <tr>
			                     <td class="td_gris_l" colspan="2">
			                          <%=rb.getString("scp.referenceDocument")%>:
			   	                 </td>
                            </tr>
                			<tr>
                				<td class="td_orange_l_b barraBlue" colspan="2">
                					<%=rb.getString("scp.name")%>
                				</td>
                			</tr>
                			<tr>
                				<td class="td_gris_l " colspan="2">
                				<logic:present name="data" property="noConformidadesDetail">
                                <logic:iterate id="bean" name="data" property="noConformidadesDetail" type="com.desige.webDocuments.document.forms.BaseDocumentForm">
                                   <a href="javascript:showDocumentPublishImp('<bean:write name="bean" property="numberGen"/>',<bean:write name="bean" property="numVer"/>,'<bean:write name="bean" property="numberGen"/>','1')" class="ahref_b">
                                        <bean:write name="bean" property="prefix"/>
                                        <bean:write name="bean" property="number"/>
                                        <bean:write name="bean" property="nameDocument"/>
                                        <bean:write name="bean" property="typeDocument"/>
                                   </a>
                                   <br />
                                </logic:iterate>
                                </logic:present>
                				</td>
                			</tr>
			        	</table>
                    </td>
                </tr>
                <html:hidden property="noconformidades" value='<%=data.getNoconformidades()%>' />
                <tr id="nodo1" class="none" >
	                    <td colspan="5" >
		                        <html:textarea property="noconformidadesref" value='<%=data.getNoconformidadesref()%>' style="display:none" />
	    	            		<iframe name="noconformidadesref" width="100%" height="380" frameborder="0" border="0" scrolling="auto" src="loading.html"></iframe>
	                    </td>
                </tr>
                 <tr id="nodo2" class="none">
                	<td colspan="5" class="td_gris_l">
                		<%=rb.getString("scp.payroll.selectedProcess")%>
                		<br/><br/><br/>
	                	<table width="70%" cellSpacing="0" cellPadding="2" align=center border="0">
	                        <tr>
			                    <td>
				                    <select <%=swDisabledIntouchAuditoria%> name="proceafectados" size="3" multiple 
					                    style="width:300px;" styleClass="classText" onDblClick="javascript:vertexto(this.form.proceafectados);">
			                           <logic:present name="procesosSacop" scope="request"> 
			                             <logic:iterate id="bean" name="procesosSacop" scope="request" type="com.desige.webDocuments.utils.beans.Search">
			                                 <option value="<%=bean.getId()%>"><%=bean.getDescript()%>&nbsp;</option>
			                             </logic:iterate>
			                         </logic:present> 
			                          </select>
			                    </td>
			                    <td width="10%">
				                      <input type="button" value=" > " style="width:28px;" onClick="javascript:mover(this.form.proceafectados,this.form.proceafectadosSelected);buscarResponsable(this.form,true)" />
				                         <br/>
				                      <input type="button" value=" < " style="width:28px;" onClick="javascript:mover(this.form.proceafectadosSelected,this.form.proceafectados);buscarResponsable(this.form,true)" class="BUTTON">
			                    </td>
			                    <td>
				                    <select <%=swDisabledIntouchAuditoria%> name="proceafectadosSelected" size="3" multiple 
				                    	style="width:300px;" styleClass="classText" onDblClick="javascript:vertexto(this.form.proceafectadosSelected);">
				                            <logic:present name="procesosSacop1" scope="request">
				                                <logic:iterate id="bean" name="procesosSacop1" scope="request" type="com.desige.webDocuments.utils.beans.Search">
				                                    <option value="<%=bean.getId()%>"><%=bean.getDescript()%></option>
				                                </logic:iterate>
				                            </logic:present>
				                    </select>
			                    </td>
							</tr>
						</table>
	                </td>
                </tr>
                <tr id="nodo3" class="none">
                	<td colspan="5" class="td_gris_l">
                		<%=rb.getString("scp.areafectadaTitle")%>.<br/><br/><br/>
	                  	<table width="100%" cellSpacing="0" cellPadding="2" align=center border="0">
                  			<tr>
			                    <input type="hidden" name='areafectada' value='<%=areaAfectada%>'>
		  	                </tr>
		  	                <tr>
								<td class="td_gris_l">
								    <fieldset>
								    	<legend><%=rb.getString("scp.afctresponsable")%></legend>
								   <%
								       String cargo="";
								       boolean sw=false;
								   %>
								    <select <%=swDisabledIntouchAuditoria%> name="responsable" id="responsable"  
								    	<%--onChange="javascript:buscarArea(this.form,true);"--%> 
								    	class="classText" size="10" style="width:90%;">
								           <logic:present name="usuariosArea" scope="request">
								            <logic:iterate id="bean" name="usuariosArea" scope="request" type="com.desige.webDocuments.utils.beans.Search">
								                <%   selected="";
								                    if (responsable.equalsIgnoreCase(bean.getId())) {
								                        selected="selected";
								                }%>
								                <option value="<%=bean.getId()%>" <%=selected%>><%=bean.getDescript()%>(<%=bean.getAditionalInfo()%>) </option>
								            </logic:iterate>
								        </logic:present>
								   </select>
								   <br/>
						           
						           <logic:present name="usuariosArea1" scope="request">
							            <logic:iterate id="bean" name="usuariosArea1" scope="request" type="com.desige.webDocuments.utils.beans.Search">
							            	<script>
							                	usuariosArea1[usuariosArea1.length] = new Array('<%=bean.getField1()%>','<%=bean.getId()%>','<%=bean.getDescript()%>(<%=bean.getAditionalInfo()%>)');
							                </script>
							            </logic:iterate>
							        </logic:present>
							        
								   </fieldset>
								</td>
							</tr>
						</table>
                     </td>
                 </tr>
               
               
                <tr id="nodo4" class="none">
               
                	<!--   Pulse aqui para seleccionar Requisito(s) aplicable(s) -->
                	<td colspan="5" class="td_gris_l">
                		
                		<br/><br/><br/>
                	   <a href="javascript:showNormasEnviarSeleccion(normsiso,normISO,normsisoSelected)" class="ahref_b"> 
                		<%=rb.getString("src.rqtaplicableOption")%></a>&nbsp;&nbsp;
                		<a href="#" onclick="javascript:showMover(normsisoSelected,normsiso);" class="ahreMenu infoUp2" ><img src="images/papelera.png" border="0" >
                		
                	    <span id="tooltip" class="info">
                            <%=rb.getString("src.rqtaplicableDEl")%>
                        </span>
                    </a>
                		
                	<!--  <button type="button" style="border:none" onClick="javascript:mover(this.form.normsisoSelected,this.form.normsiso);"> <img src="images/papelera.png" /> </button> -->    
                		<table>
                			<td width="0%">
                			
                				</div>
                		
                       	
                			</td>
                			<div>
			    			<td width="100%">
		                		<div>
				                   <div style="overflow: auto;">
					                   <select <%=swDisabledIntouchAuditoria%> name="normsisoSelected"  id="normsisoSelected" size="3" multiple  styleClass="classText" onDblClick="javascript:vertexto(this.form.normsisoSelected);" style="width:100%">
					                           <logic:present name="norms1" scope="request">
					                            <logic:iterate id="bean" name="norms1" scope="request" type="com.desige.webDocuments.utils.beans.Search">
					                                <option value="<%=bean.getId()%>"><%=bean.getDescript()%></option>
					                            </logic:iterate>
					                        </logic:present>
					                   </select>
				                   </div>
		                		</div>
                			</td>
                			<div>
                		             <%
                                         norms = HandlerNorms.getAllNorms();
                                         if (norms!=null&&norms.size() > 0) {
                                             pageContext.setAttribute("norms",norms);
                                     %>
                                   
                                     	<input  name="normISO"  id="normISO"   value=""  type="hidden"   />
                                     
			                         <%
                                         }
                                     %>
            	                   <div width="0%" > 
					                    <select <%=swDisabledIntouchAuditoria%> name="normsiso" id="normsiso"  type="hidden" size="0" multiple styleClass="classText" onDblClick="javascript:vertexto(this.form.normsiso);"  style="width:0px; height: 0px" >
					                        <logic:present name="norms" scope="request">
					                            <logic:iterate id="bean" name="norms" scope="request" type="com.desige.webDocuments.utils.beans.Search">
					                                <option value="<%=bean.getId()%>"><%=bean.getDescript()%></option>
					                            </logic:iterate>
					                        </logic:present>
					                   </select>
				                   </div>
				                    
				                   
		                		</div>
                		</table>
                		
	                </td>
                 </tr>
                 
                 
                 
                 <tr id="nodo5" class="none">
                	<td colspan="5" class="td_gris_l">
                		<%=rb.getString("scp.payroll.selectedPerson")%>
                		<br/><br/><br/>
	                	<table width="70%" cellSpacing="0" cellPadding="2" align=center border="0">
	                        <tr>
				               <td>
					                    <select <%=swDisabledIntouchAuditoria%> name="users" size="3" multiple style="width:300px;" styleClass="classText" onDblClick="javascript:vertexto(this.form.users);">
					                        <logic:present name="usuarios" scope="request">
					                            <logic:iterate id="bean" name="usuarios" scope="request" type="com.desige.webDocuments.utils.beans.Search">
					                                <option value="<%=bean.getId()%>"><%=bean.getDescript()%>(<%=bean.getAditionalInfo()%>)</option>
					                            </logic:iterate>
					                        </logic:present>
					                   </select>
				               </td>
				               <td width="10%">
					                     <input type="button" value=" > " style="width:28px;" onClick="javascript:mover(this.form.users,this.form.usersSelected);" />
					                        <br/>
					                     <input type="button" value=" < " style="width:28px;" onClick="javascript:mover(this.form.usersSelected,this.form.users);" />
				               </td>
				               <td>
					                    <select name="aux" style="display:none" multiple>
					                    </select>
					                    <select name="auxSelec" style="display:none" multiple>
					                    </select>
				               </td>
				               <td>
					                   <select <%=swDisabledIntouchAuditoria%> name="usersSelected" size="3" multiple style="width:300px;" styleClass="classText" onDblClick="javascript:vertexto(this.form.usersSelected);">
					                            <logic:present name="usuarios1" scope="request">
					                                <logic:iterate id="bean" name="usuarios1" scope="request" type="com.desige.webDocuments.utils.beans.Search">
					                                    <option value="<%=bean.getId()%>"><%=bean.getDescript()%></option>
					                                </logic:iterate>
					                            </logic:present>
					                   </select>
			                   </td>
							</tr>
						</table>
	                </td>
                </tr>
              <tr id="nodo6" class="none">
                    <td colspan="5">
            			<span class="td_gris_l"><%=rb.getString("scp.selectPossibleCause")%></span> 
 	                    <html:hidden property="causasnoconformidad" value='<%=data.getCausasnoconformidad()%>'  />
 	                    <select name="cbo_causasnoconformidad" size="20" style="width:99%;" multiple="multiple" onchange="selectOptions(this,this.form.causasnoconformidad)">
 	                    	<%if(listPossibleCause!=null) {
 	                    	for(int i=0; i<listPossibleCause.size();i++) {
 	                    		possibleTO = (PossibleCauseTO)listPossibleCause.get(i);%>
 	                    		<option value="<%=possibleTO.getIdPossibleCause()%>" <%=data.getCausasnoconformidad().indexOf(",".concat(possibleTO.getIdPossibleCause()).concat(","))!=-1?"selected":""%> >
 	                    		<%=possibleTO.getDescPossibleCause()%>
 	                    		</option>
 	                    	<%}
 	                    	}%>
 	                    </select>
                   </td>
               </tr>
              <tr id="nodo7" class="none">
                  <td colspan="5">
            			<div class="td_gris_l"><%=rb.getString("scp.selectActionRecommended")%></div>
	                    <html:textarea property="accionrecomendada" value='<%=data.getAccionrecomendada()%>' style="width:100%;height: 90vh;" />
                   </td>
              </tr>

              <tr id="nodo8" class="none">
                  <td colspan="5" align="center" class="td_gris_l">
                  	   <%=rb.getString("scp.payroll.selectedFileAttached")%><br/><br/><br/>
                       <%=rb.getString("doc.upFile")%>:
                       <html:file property="nameFile"/>
                   </td>
               </tr>
              <tr id="nodo9" class="none">
					<td colspan="5">
						<table width="100%" cellSpacing="0" cellPadding="2" align=center border="0">
					        <tr>
					        	<td>
				                 <input type="hidden" name="exec" value=""/>
				                 <logic:present name="sacoprelacionada" scope="request">
				                   <logic:notEqual  name="sacoprelacionada" scope="request" value="1">
				                       <input type="hidden" name="sacop_relacionadas" value='<%=data.getSacop_relacionadas()!=null?data.getSacop_relacionadas():""%>'/>
				   	            	   <iframe name="sacop_relacionadas" width="100%" height="380" frameborder="0" border="0" scrolling="no" src="loading.html"></iframe>
				                   </logic:notEqual>
				                 </logic:present>
					            </td>
					        </tr>
					     </table>
					 </td>
               </tr>
               <tr class="none">
                   <td></td>
                   <td colspan="4" align="left" style="padding: 0px" class="td_gris_l">
		               <logic:present name="sacoprelacionada" scope="request">
		                   <logic:notEqual  name="sacoprelacionada" scope="request" value="1">
		                       <logic:notPresent name="tagsnamesSeleccionados" scope="session">
		                           &nbsp;
		                           <input type="button" class="boton" value="<%=rb.getString("scp.emitir")%>" onClick="javascript:salvar(this.form,'<%=LoadSacop.edoEmitida%>');" name="btnOK" />
		                           &nbsp;
		                           <input type="button" class="boton" value="<%=rb.getString("scp.guardar")%>" onClick="javascript:salvar(this.form,'<%=LoadSacop.edoBorrador%>');" name="btnsvb" />
		                           &nbsp;
		                           <input type="button" class="boton" value="<%=rb.getString("btn.cancel")%>" onClick="javascript:cancelarUnaSacopNormal(this.form);" name="btnCancel" />
		                       </logic:notPresent>
		                   </logic:notEqual>
		                   <logic:equal  name="sacoprelacionada" scope="request" value="1">
		                     &nbsp;
		                     <input type="button" class="boton" value="<%=rb.getString("btn.cancel")%>" onClick="javascript:cancelarsacoprelacionada(this.form);" name="btnCancel" />
		                   </logic:equal>
		                </logic:present>
                   </td>
               </tr>
            </table>
           </html:form>

<%--
        </td>
    </tr>
</table>
--%>
</body>
</html>
<script type="text/javascript">
    try {
    	window.parent.frames['sacopEast'].fondo(<%=tipoSacop%>);
    } catch(e) {
    	//do nothing
    }
    
    if(document.sacoplantilla.nodoActual.value!="0"){
		window.parent.frames['sacopEast'].ir(document.sacoplantilla.nodoActual.value,<%=nTipoSacop!=-1?tipoSacop:0%>);
	}else{
		ir(0, false);
	}
	
	document.getElementById('responsable').focus();
	
	<%if(request.getParameter("isBuscarProceso")!=null && request.getParameter("isBuscarProceso").equals("true")) {%>
		if(document.sacoplantilla.proceafectados.options.length==0) {
			setTimeout("alert('<%=rb.getString("scp.notProcess")%>')",100);
		}
	<%}%>
</script>