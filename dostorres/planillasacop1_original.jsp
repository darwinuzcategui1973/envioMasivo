<!--  planillasacop1.jsp -->
<%@ page import="java.util.Locale,
                 java.util.ResourceBundle,
                 java.text.SimpleDateFormat,
                 com.desige.webDocuments.utils.ToolsHTML,
                 com.desige.webDocuments.workFlows.forms.DataUserWorkFlowForm,
                 com.desige.webDocuments.sacop.actions.LoadSacop"%>
<%@ page import="java.util.Date"%>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<%@ page language="java" %>
<bean:define id="data" name="sacoplantilla" type="com.desige.webDocuments.sacop.forms.plantilla1" scope="request" />
<%
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
    
    boolean isCorrectiva = data.getCorrecpreven().equals("0");
    String selected;
%>
<html>
<head>
<title><%=rb.getString("principal.title")%></title>
<jsp:include page="meta.jsp" /> 
<link href="estilo/estilo.css" rel="stylesheet" type="text/css">
<style>select {background-color:#ebebeb;height:320;font-size:9pt;}</style>

<script type="text/javascript" src="./estilo/funciones.js"></script>
<script type="text/javascript" src="./estilo/popcalendar.js"></script>
<script type="text/javascript" src="./estilo/fechas.js"></script>
<script type="text/javascript" src="./script/sacopJS.js"></script>

<script type="text/javascript">
var nodo1=false;
var nodo2=false;
var nodo7=false;
var nodo8=false;
var nodo9=false;
var nodo11=false;

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
	if(nodo==1 && !nodo1 && openWnd) {
		evaluaChequeo(document.sacoplantilla,'n','<%=rb.getString("scp.noconformidades")%>');
		nodo1=true;
	}
	if(nodo==2 && !nodo2) {
		loadNoconformidadesRefFrame('0','exec','sacoplantilla','noconformidadesref',0);
		nodo2=true;
	}

	if(nodo==7 && !nodo7 && openWnd) {
		evaluaChequeo(document.sacoplantilla,'d','<%=rb.getString("scp.desc")%>'); //7
		nodo7=true;
	}
	if(nodo==8 && !nodo8 && openWnd) {
		evaluaChequeo(document.sacoplantilla,'f','<%= (isCorrectiva?rb.getString("scp.pblecausasnoconformidad"):rb.getString("scp.pblecausasnoconformidad0"))%>'); //8
		nodo8=true;
	}
	if(nodo==9 && !nodo9 && openWnd) {
		evaluaChequeo(document.sacoplantilla,'a','<%=rb.getString("scp.acrecomendadas")%>'); //9
		nodo9=true;
	}
	if(nodo==11) {
		window.parent.frames['sacopSouth'].showSiguienteBtn(false);
		loadSacops('0','exec','sacoplantilla','sacop_relacionadas',0);  //11
        nodo11=true;
		//alert(window.parent.frames['sacopEast'].document.getElementById("nodo11Form"));
	}
}    
function ocultar(nodo){
	document.getElementById("nodo"+nodo).className="none";
}


function loadSacops(number,input,nameForm,value,type) {
    var searchForm = window.parent.frames['sacopEast'].document.getElementById("nodo11Form");
	var extraParameters = "";
	    
	//se desea buscar SACOPs, entonces colocamos en el request
	//los parametros respectivos
	extraParameters += "&idSacop=" + searchForm.idSacop.value;
	extraParameters += "&emisorSacop=" + searchForm.emisorSacop.value;
	extraParameters += "&responsableSacop=" + searchForm.responsableSacop.value;
	extraParameters += "&areaResponsableSacop=" + searchForm.areaResponsableSacop.value;
	extraParameters += "&efectivaSacop=" + searchForm.efectivaSacop.value;
	
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
                
                if ((""=="<%=responsable%>")) {
                    alert('<%=rb.getString("scp.vald0")%>');
	        		window.parent.frames['sacopEast'].ir(3);
                    return false;
                }

                if ((procesosSacop==0)) {
                    alert('<%=rb.getString("scp.vald3")%>');
	        		window.parent.frames['sacopEast'].ir(5);
                    return false;
                }

                if ((usuarios==0)) {
                    alert('<%=rb.getString("scp.vald3_1")%>');
	        		window.parent.frames['sacopEast'].ir(6);
                    return false;
                }

          //  if (Trim(forma.descripcion.value)==""){
          //      alert(' < %=rb.getString("scp.vald4")%>');
          //      return false;
          //  }
            if (Trim(forma.causasnoconformidad.value)==""){
                alert('<%=rb.getString("scp.vald5")%>');
        		window.parent.frames['sacopEast'].ir(8);
                return false;
            }
            if (Trim(forma.accionrecomendada.value)==""){
                alert('<%=rb.getString("scp.vald6")%>');
        		window.parent.frames['sacopEast'].ir(9);
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
    
        var row = 0;
        var swEnviar=false;
        var swEmitir=edo=='<%=LoadSacop.edoEmitida%>'
        if (swEmitir){
            <%if ("-1".equalsIgnoreCase(areaAfectada)){%>
               alert('<%=rb.getString("scp.val13")%>');
               return false;
            <%}%>
        }
        
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
                //alert(forma.action);
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






function buscarArea(forma){
    var row = 0;
    norms = contSelectInList(forma.normsisoSelected);
    procesosSacop= contSelectInList(forma.proceafectadosSelected);
    usuarios=contSelectInList(forma.usersSelected);
    if (forma.responsable.value.length>0) {
        forma.action="RefreshCrearPlanillaSacop.do?responsablee="+forma.responsable.value+"&nodoActual="+forma.nodoActual.value;
        //alert(forma.action);
        forma.submit();
    }
}
function evaluaChequeo(forma,parametro,titleForm){
            if (parametro=='d'){
                var hWnd = null;
                var title='<%=rb.getString("scp.desc")%>';
                //showIFrame('SacopMiniTextFrame.do','descripcion','sacoplantilla',forma.descripcion.value,'<%=rb.getString("scp.desc")%>','0');
                showWindow('SacopMiniText.do','descripcion','sacoplantilla',forma.descripcion.value,'<%=rb.getString("scp.desc")%>',800,450,'0');
            }else if (parametro=='f'){
                //showIFrame('SacopMiniTextFrame.do','causasnoconformidad','sacoplantilla',forma.causasnoconformidad.value,'<%=rb.getString("scp.pblecausasnoconformidad1")%>','0');
                showWindow('SacopMiniText.do','causasnoconformidad','sacoplantilla',forma.causasnoconformidad.value,'<%=rb.getString("scp.pblecausasnoconformidad1")%>',800,450,'0');
            }else if (parametro=='a'){
                //showIFrame('SacopMiniTextFrame.do','accionrecomendada','sacoplantilla',forma.accionrecomendada.value,'<%=rb.getString("scp.acrecomendadas")%>','0');
                showWindow('SacopMiniText.do','accionrecomendada','sacoplantilla',forma.accionrecomendada.value,'<%=rb.getString("scp.acrecomendadas")%>',800,450,'0');
            }else if (parametro=='n'){
                //showIFrame('SacopMiniTextFrame.do','noconformidades','sacoplantilla',forma.noconformidades.value,'<%=rb.getString("scp.noconformidades")%>','0');
                showWindow('SacopMiniText.do','noconformidades','sacoplantilla',forma.noconformidades.value,'<%=rb.getString("scp.noconformidades")%>',800,450,'0');
            }else if (parametro=='ref'){
                showWindow('SacopMiniText.do','noconformidadesref','sacoplantilla',forma.noconformidadesref.value,'<%=rb.getString("scp.sourceDocument")%>' ,800,450,'0');
            }

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
            <html:hidden property="correcpreven" value='<%=data.getCorrecpreven()%>'/>
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
            <table align=center border="0" width="100%"  cellSpacing=0 cellPadding="0">
            	<tr style="display:none">
                	<td colspan="5">
                    	<table class="clsTableTitle" width="100%" cellSpacing="0" cellPadding="2" align=center border="0">
                        <tbody>
                            <tr>
                                <td class="td_title_bc" width="*" height="18">
                                    <logic:equal value="0" name="data" property="correcpreven">
                                       <%titleForm=rb.getString("scp.ac");%>
                                    </logic:equal>
                                    <logic:equal value="1" name="data" property="correcpreven">
                                       <%titleForm=rb.getString("scp.ap");%>
                                    </logic:equal>
                                    <%=titleForm%>
                                </td>
                            </tr>
                        </tbody>
                    	</table>
                    </td>
                </tr>
                <tr id="nodo0" >
                	<td colspan="5"  align="center">
                    	<table cellSpacing="5" cellPadding="2" align=center border="0" width="70%">
                            <tr>
			                     <td class="td_gris_l" width="26%" height="26" valign="middle">
			                              <%=rb.getString("scp.applicant")%>
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
			                             <a href="javascript:showCharge('<%=nom%>','<%=data.getCargo()%>')" class="ahref_b">
			                             <logic:present name="showCharge" scope="session">
			                                   <%=data.getCargo()%>
			                             </logic:present>
			                             <logic:notPresent name="showCharge" scope="session">
			                                  <%=nom%>
			                             </logic:notPresent>
			                             </a>
						         </td>
			                </tr>
			                <tr>
			                     <td class="td_gris_l" width="26%" height="26" valign="middle">
			                          <%=rb.getString("scp.requestDate")%>:
			   	                 </td>
			                     <td class="td_gris_l caja">
			                         
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
                                        <%= rb.getString("scp.dateOccurrence") %> 
                                    </span>
                                </td>
                                <td class="td_gris_l caja">
                                	<%=data.getFechaWhenDiscovered()!=null?data.getFechaWhenDiscovered():""%>
                                    <html:hidden property="fechaWhenDiscovered" value='<%=data.getFechaWhenDiscovered()!=null?data.getFechaWhenDiscovered():""%>' />
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
			                       <html:hidden property="origensacop" value='<%=data.getOrigensacop()%>'/>
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
                				<td class="td_orange_l_b barraGris22">
                					<%=rb.getString("scp.num")%>
                				</td>
                				<td class="td_orange_l_b barraGris22">
                					<%=rb.getString("scp.name")%>
                				</td>
                			</tr>
                			<tr>
                				<td class="td_gris_l caja">
                					<%--data.getIdDocumentRelated()!=null?data.getIdDocumentRelated():""--%>
                					<%=docRel.getPrefix()%><%=docRel.getPrefix()!=null && !docRel.getPrefix().equals("")?"-":""%><%=docRel.getNumber()%>
                				</td>
                				<td class="td_gris_l caja">
                					<%=docRel.getNameDocument()%>
                				</td>
                			</tr>
			        	</table>
                    </td>
                </tr>
                <tr id="nodo1" class="none">
                	<td colspan="5" >
                			<span class="td_gris_l"><%=rb.getString("scp.changeText")%></span>
	                  		<div id="_noconformidades" class="fondoEditor" style="padding:10;vertical-align:top;border:1px dotted blue;" 
	                  			onclick="nodo1=false;ir(1,true);"><%=data.getNoconformidades()%></div>
	                        <html:textarea property="noconformidades" value='<%=data.getNoconformidades()%>' style="display:none" />
                	</td>
                </tr>
                <tr id="nodo2" class="none" >
	                    <td colspan="5" >
		                        <html:textarea property="noconformidadesref" value='<%=data.getNoconformidadesref()%>' style="display:none" />
	    	            		<iframe name="noconformidadesref" width="100%" height="380" frameborder="0" border="0" scrolling="auto" src="loading.html"></iframe>
	                    </td>
                </tr>
                <tr id="nodo3" class="none">
                	<td colspan="5" class="td_gris_l">
                		<%=rb.getString("scp.payroll.selectedResponsible")%><br/><br/><br/>
	                  	<table width="100%" cellSpacing="0" cellPadding="2" align=center border="0">
                  			<tr>
			                    <input type=hidden name='areafectada' value='<%=areaAfectada%>'>
								<td>
			                       <font class="td_gris_l">
			                          <%=rb.getString("scp.areafectada")%>:
			                       </font>
			                      <%if ("-1".equalsIgnoreCase(areaAfectada)){%>
			                            <%=rb.getString("scp.notienearea")%>
			                      <%}else{%>
			                         <%=areaAfectada%>
			                     <% }%>
		  	                    </td>
		  	                </tr>
		  	                <tr>
								<td class="td_gris_l">
								    <fieldset>
								    	<legend><%=rb.getString("scp.afctresponsable")%></legend>
								   <%
								       String cargo="";
								       boolean sw=false;
								   %>
								    <select <%=swDisabledIntouchAuditoria%> name="responsable" id="responsable" multiple onChange="javascript:buscarArea(this.form);" class="classText" size="10" style="width:90%;">
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
								   </fieldset>
								</td>
							</tr>
						</table>
                     </td>
                 </tr>
                <tr id="nodo4" class="none">
                	<td colspan="5" class="td_gris_l">
                		Seleccione la(s) normas(s) que esta(n) siendo afectada(s) de forma directa o indirecta en la definición 
                		de esta accion. Los normas que se listan pueden ser ampliadas o modificadas en el modulo de administración.
                		<br/><br/><br/>
	                	<table width="70%" cellSpacing="0" cellPadding="2" align=center border="0">
	                        <tr>
			                   <td>
			                   <div style="overflow: auto; width: 300px;">
				                    <select <%=swDisabledIntouchAuditoria%> name="normsiso" size="3" multiple styleClass="classText" onDblClick="javascript:vertexto(this.form.normsiso);" >
				                        <logic:present name="norms" scope="request">
				                            <logic:iterate id="bean" name="norms" scope="request" type="com.desige.webDocuments.utils.beans.Search">
				                                <option value="<%=bean.getId()%>"><%=bean.getDescript()%></option>
				                            </logic:iterate>
				                        </logic:present>
				                   </select>
			                   </div>
			                   </td>
			                    <td width="10%">
			                     <input type="button" value=" > " style="width:28px;"  onClick="javascript:mover(this.form.normsiso,this.form.normsisoSelected);" />
			                        <br/>
			                     <input type="button" value=" < " style="width:28px;" onClick="javascript:mover(this.form.normsisoSelected,this.form.normsiso);" />
			                   </td>
			                   <td>
			                   <div style="overflow: auto; width: 300px;">
				                   <select <%=swDisabledIntouchAuditoria%> name="normsisoSelected" size="3" multiple  styleClass="classText" onDblClick="javascript:vertexto(this.form.normsisoSelected);">
				                           <logic:present name="norms1" scope="request">
				                            <logic:iterate id="bean" name="norms1" scope="request" type="com.desige.webDocuments.utils.beans.Search">
				                                <option value="<%=bean.getId()%>"><%=bean.getDescript()%></option>
				                            </logic:iterate>
				                        </logic:present>
				                   </select>
			                   </div>
			                   </td>
							</tr>
						</table>
	                </td>
                 </tr>
                 <tr id="nodo5" class="none">
                	<td colspan="5" class="td_gris_l">
                		Seleccione el(los) proceso(s) que esta(n) siendo afectado(s) de forma directa o indirecta en la definición 
                		de esta accion. Los procesos que se listan deben estar registrados en la estructura de la empresa y 
                		poseer el permiso correspondiente sobre los mismos.
                		<br/><br/><br/>
	                	<table width="70%" cellSpacing="0" cellPadding="2" align=center border="0">
	                        <tr>
			                    <td>
				                    <select <%=swDisabledIntouchAuditoria%> name="proceafectados" size="3" multiple style="width:300px;" styleClass="classText" onDblClick="javascript:vertexto(this.form.proceafectados);">
			                         <logic:present name="procesosSacop" scope="request">
			                             <logic:iterate id="bean" name="procesosSacop" scope="request" type="com.desige.webDocuments.utils.beans.Search">
			                                 <option value="<%=bean.getId()%>"><%=bean.getDescript()%>&nbsp;</option>
			                             </logic:iterate>
			                         </logic:present>
				                    </select>
			                    </td>
			                    <td width="10%">
				                      <input type="button" value=" > " style="width:28px;" onClick="javascript:mover(this.form.proceafectados,this.form.proceafectadosSelected);" />
				                         <br/>
				                      <input type="button" value=" < " style="width:28px;" onClick="javascript:mover(this.form.proceafectadosSelected,this.form.proceafectados);" class="BUTTON">
			                    </td>
			                    <td>
				                    <select <%=swDisabledIntouchAuditoria%> name="proceafectadosSelected" size="3" multiple style="width:300px;" styleClass="classText" onDblClick="javascript:vertexto(this.form.proceafectadosSelected);">
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
                 <tr id="nodo6" class="none">
                	<td colspan="5" class="td_gris_l">
                		Seleccione la(s) persona(s) que sera(n) notificada(s) de los diferentes estados por los que transitara la SACOP. 
                		La(s) persona(s) indicada(s) recibira(n) via correo electronico los cambios a los que esta sujeta la accion.
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
              <tr id="nodo7" class="none">
                    <td colspan="5">
            			<span class="td_gris_l"><%=rb.getString("scp.changeText")%></span>
                  		<div id="_descripcion" class="fondoEditor" style="padding:10;vertical-align:top;border:1px dotted blue;" 
                  			onclick="nodo7=false;ir(7,true);"><%=data.getDescripcion()%></div>
	                    <html:textarea property="descripcion" value='<%=data.getDescripcion()%>' style="display:none" />
                   </td>
               </tr>
              <tr id="nodo8" class="none">
                    <td colspan="5">
            			<span class="td_gris_l"><%=rb.getString("scp.changeText")%></span>
                  		<div id="_causasnoconformidad" class="fondoEditor" style="padding:10;vertical-align:top;border:1px dotted blue;" 
                  			onclick="nodo8=false;ir(8,true);"><%=data.getCausasnoconformidad()%></div>
 	                    <html:textarea property="causasnoconformidad" value='<%=data.getCausasnoconformidad()%>' style="display:none" />
                   </td>
               </tr>
              <tr id="nodo9" class="none">
                  <td colspan="5">
            			<span class="td_gris_l"><%=rb.getString("scp.changeText")%></span>
                  		<div id="_accionrecomendada" class="fondoEditor" style="padding:10;vertical-align:top;border:1px dotted blue;" 
                  			onclick="nodo9=false;ir(9,true);"><%=data.getAccionrecomendada()%></div>
	                    <html:textarea property="accionrecomendada" value='<%=data.getAccionrecomendada()%>' style="display:none" />
                   </td>
              </tr>

              <tr id="nodo10" class="none">
                  <td colspan="5" align="center" class="td_gris_l">
                  	   Si lo desea, puede seleccionar un archivo adjunto para la SACOP, con el cual podr&aacute; aumentar
                  	   el nivel de detalle del porque de la acci&oacute;n. Por favor, tome en consideraci&oacute;n que el
                  	   archivo solo se guardara cuando emita la sacop.<br/><br/><br/>
                       <%=rb.getString("doc.upFile")%>:
                       <html:file property="nameFile"/>
                   </td>
               </tr>
              <tr id="nodo11" class="none">
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
    	window.parent.frames['sacopEast'].fondo(<%=isCorrectiva?0:1%>);
    } catch(e) {
    	//do nothing
    }
    
    if(document.sacoplantilla.nodoActual.value!="0"){
		window.parent.frames['sacopEast'].ir(document.sacoplantilla.nodoActual.value);
	}else{
		ir(0, false);
	}
	
	document.getElementById('responsable').focus();
</script>