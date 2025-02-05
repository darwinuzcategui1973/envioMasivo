<!--  planillasacop6.jsp -->
<!-- relacionadas: planillasacop2.jsp,planillasacop3_0.jsp,planillasacop4.jsp,planillasacop5.jsp,planillasacop6.jsp,planillasacop10.jsp --> 
<%@ page import="java.util.Locale,
                 java.util.ResourceBundle,
                 java.text.SimpleDateFormat,
                 java.util.Map,
                 com.focus.qweb.to.PossibleCauseTO,
                 com.focus.qweb.to.ActionRecommendedTO,
                 com.desige.webDocuments.utils.ToolsHTML"%>

<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<%@ page language="java" %>
<% 
	String path = request.getContextPath();
	String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";

    ResourceBundle rb = ToolsHTML.getBundle(request);
    StringBuffer onLoad = new StringBuffer("");
    String sacoprelacionada=request.getAttribute("sacoprelacionada")!=null?(String)request.getAttribute("sacoprelacionada"):"0";
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

    String fechaEmision=request.getAttribute("fechaEmision")!=null?(String)request.getAttribute("fechaEmision"):"";
    String mostrardetrestext=request.getAttribute("mostrardetrestext")!=null?(String)request.getAttribute("mostrardetrestext"):"0";
    int cuantosText=Integer.parseInt(mostrardetrestext);
    String title="";

    if (ToolsHTML.isEmptyOrNull(numGo)) {
        numGo = "1";
    }

    PossibleCauseTO possible = null; 
    ActionRecommendedTO cause = null;
    Map<String,PossibleCauseTO> listOrderPossibleCause = (Map)request.getAttribute("ListOrderPossibleCause");
    
%>
<html>
<head>
<title><%=rb.getString("principal.title")%></title>

<jsp:include page="meta.jsp?adsf" /> 
<link href="estilo/sacop.css" rel="stylesheet" type="text/css"></link>
<script type="text/javascript" src="<%=basePath%>estilo/funciones.js"></script>
<script type="text/javascript" src="<%=basePath%>estilo/popcalendar.js"></script>
<script type="text/javascript" src="<%=basePath%>estilo/fechas.js"></script>
<script type="text/javascript" src="<%=basePath%>estilo/funciones.js"></script>


<script type="text/javascript">
	var obj = {
		titleSacop: "",
		idSacop: "",
		emisor: "",
		estado: "",
		tipo: "",
		fechaEmision: "",
		responsableArea: "",
		areaAfectada: "",
		fechaEstimada: "",
		fechaReal: "",
		fechaHallazgo: "",
		clasificacion: "",
		fechaVerificacion: "",
		fechaCierre: "",
		solicitante: "",
		relacionado: "",
		listSacops: new Array(),
	
		noconformidadesref: "",
		noconformidades: "",
		 
		comentario: "",
		posibleCausa: "",
		posibleCausaTitle: "",
		accionesRecomendadas: "",
		accionesCorrectivaInformaTitle: "",
		accionesCorrectivaInforma: "",
		requisitoAplicable: "",
		procesos: "",
		causaRaizUno: "",
		causaRaizDos: "",
		causaRaizTres: "",
		acciones: "",
		accionesObservaciones: "",
		cumplieron: "",
		cumplieronDetalle: "",
		eficaz: "",
		eficazDetalle: "",
		seguimiento: "",
		seguimientoFecha: ""
	
	};
	
	var objDetail = {
		sacopsNumber:"",
		sacopsDescription:"",
		sacopsDate:"",
		sacopsSecuencia:""
	}

	function getHtml(idName) {
		if(document.getElementById(idName)) {
			// console.log(idName);
			// console.log(document.getElementById(idName).textContent);
			return document.getElementById(idName).textContent;
		} else {
			return " ";
		}
	}

	function getPdfData() {
		
		obj.titleSacop = getHtml("data_titleSacop");
		
		obj.idSacop = document.getElementById("idplanillasacop1").value;
		
		obj.emisor = getHtml("data_emisor");
		obj.estado = getHtml("data_estado");
		obj.tipo = getHtml("data_tipo");

		obj.fechaEmision = getHtml("data_fechaEmision");
		obj.responsableArea = getHtml("data_responsableArea");
		obj.areaAfectada = getHtml("data_areaAfectada");

		obj.fechaEstimada = getHtml("data_fechaEstimada");
		obj.fechaReal = getHtml("data_fechaReal");
		obj.fechaHallazgo = getHtml("data_fechaHallazgo");

		obj.clasificacion = getHtml("data_clasificacion");
		obj.fechaVerificacion = getHtml("data_fechaVerificacion");
		obj.fechaCierre = getHtml("data_fechaCierre");
		
		obj.solicitante = getHtml("data_solicitante");
		
		obj.relacionado = getHtml("data_relacionado");
		
		var pre = "data_sacopsNumber_";
		
		var detail;
		for(var i=0; i<100;i++) {
			if(document.getElementById(pre+i)) {
				detail = objDetail;
				
				
				detail.sacopsNumber = getHtml(pre+i);
				detail.sacopsDescription = getHtml("data_sacopsDescription_"+i);
				detail.sacopsDate = getHtml("data_sacopsDate_"+i);
				detail.sacopsSecuencia = getHtml(pre+0);
				
				
				 obj.listSacops[obj.listSacops.length] = detail;	
			} else {
				break;
			}
		}
		
		obj.noconformidadesref = getHtml("data_noconformidadesref");
		obj.noconformidades = getHtml("data_noconformidades");
		 
		obj.comentario = getHtml("data_comentario");
		obj.posibleCausa = getHtml("data_posibleCausa");
		obj.posibleCausaTitle = getHtml("data_posibleCausaTitle");
		obj.accionesRecomendadas = getHtml("data_accionesRecomendadas");
		obj.accionesCorrectivaInformaTitle = getHtml("data_accionesCorrectivaInformaTitle");
		obj.accionesCorrectivaInforma = getHtml("data_accionesCorrectivaInforma");
		obj.requisitoAplicable = getHtml("data_requisitoAplicable");
		obj.procesos = getHtml("data_procesos");
		obj.causaRaizUno = getHtml("data_causaRaizUno");
		obj.causaRaizDos = getHtml("data_causaRaizDos");
		obj.causaRaizTres = getHtml("data_causaRaizTres");
		obj.acciones = getHtml("data_acciones");
		obj.accionesObservaciones = getHtml("data_accionesObservaciones");
		obj.cumplieron = getHtml("data_cumplieron");
		obj.cumplieronDetalle = getHtml("data_cumplieronDetalle");
		obj.eficaz = getHtml("data_eficaz");
		obj.eficazDetalle = getHtml("data_eficazDetalle");
		obj.seguimiento = getHtml("data_seguimiento");
		obj.seguimientoFecha = getHtml("data_seguimientoFecha");
		
		return obj;
	}

  function loadSacops(number,input,nameForm,value,type) {
        abrirVentana("Sacop_Relacinadas.do?number="+number+"&input="+input+"&nameForma="+nameForm+"&value="+value+"&type="+type+"&userAssociate="+document.sacoplantilla.sacop_relacionadas.value,800,600)
 }

function vertexto(normsiso){
		var items = normsiso.length;
        for (row = 0; row < items; row++){
                   var valor = normsiso[row];
                   if (valor.selected){
                       alert(normsiso[row].text);
                       row +=items+1;
                   }
        }
}
    function salvar(forma) {
        var row = 0;
          if (!(Trim(forma.accionesEstablecidastxt.value)=="") && !(Trim(forma.eliminarcausaraiztxt.value)=="")){
           if ((hayComillas(forma.accionesEstablecidastxt.value)) || (hayComillas(forma.eliminarcausaraiztxt.value))){
             alert('<%=rb.getString("scp.vald2")%>');
             return false;
           }else{
              forma.btnOK.disabled=true;
              forma.btnCancel.disabled=true;
              forma.submit();
           }

          }else{
              alert('<%=rb.getString("scp.vald7")%>');
          }


    }

    function editField(pages,input,value,forma){
        var hWnd = null;
        hWnd = window.open(pages+"?input="+input+"&value="+value+"&nameForma="+forma.name,"Catalogo","width=400,height=300,resizable=yes,scrollbars=yes,left=300,top=250");
        if ((document.window != null) && (!hWnd.opener)){
            hWnd.opener = document.window;
        }
    }

    function cancelar() {
    	// alert(document.forms[0].name);
        //history.back();
        try {
        	window.parent.parent.administrarCancelar();
        } catch(e) {
        	window.close();
        }
    }
    
    function cancelarsacoprelacionada(forma){
        window.close();
    }
    
  function toggleDiv(gn)  {
	var g = document.all(gn);

	if( g.style.display == "none")
		g.style.display = "";
	else
		g.style.display = "none";
    }
    function evaluaRechazo(rechazo,forma){
        if (rechazo.checked){
            showWindow('SacopMiniText.do','noaceptada','sacoplantilla',forma.noaceptada.value,'Rechazo',800,500,'0');
        }
    }
   function showCharge(userName,charge) {
	    charge = charge.replace(/\[/gi,"-").replace(/\]/gi,"-");
        window.open("showCharge.jsp?userName="+userName+"&charge="+charge, "WebDocuments", "resizable=no,scrollbars=yes,statusbar=yes,width=300,height=200,left=410,top=250");
    }

    function evaluaChequeo(chequeo,forma,parametro){
        if (chequeo.checked){
            if (parametro=='a'){
                showWindow('SacopMiniText.do','accionesEstablecidastxt','sacoplantilla',forma.accionesEstablecidastxt.value,'Observaciones',800,500,'0');
            }else if (parametro=='e'){
                showWindow('SacopMiniText.do','eliminarcausaraiztxt','sacoplantilla',forma.eliminarcausaraiztxt.value,'Observaciones',800,500,'0');
            }

        }
    }
     function cancelar(forma) {
        forma.action="loadSACOPMain.do?goTo=reload";
        forma.submit();
        try {
        	window.parent.parent.administrarCancelar();
        } catch(e) {
        	window.close();
        }
    }
     function nextp(forma){
      var i=parseInt(forma.mostrardetrestext.value);
      if (i<9){
         forma.btnatras.disabled=false;
         forma.mostrardetrestext.value=parseInt(forma.mostrardetrestext.value) + 3;
         forma.submit();
      }else{
            forma.btnsig.disabled=true;
            forma.btnatras.disabled=false;
      }


   }
   function backp(forma){
      var i=parseInt(forma.mostrardetrestext.value);
      if (i>0){
         forma.btnsig.disabled=false;
         forma.mostrardetrestext.value=parseInt(forma.mostrardetrestext.value) - 3;
         forma.submit();
      }else{
            forma.btnatras.disabled=true;
            forma.btnsig.disabled=false;
      }
   }
     function showFileSacop(idplanillasacop1) {
        var idplanillasacop1 = escape(idplanillasacop1);
        abrirVentana("viewDocumentSacop.jsp?idplanillasacop1=" + idplanillasacop1 ,800,600);
    }
     
     function generatePdf() {

    	    var xhr = new XMLHttpRequest();
    	    xhr.open('POST', '<%=basePath%>/createSacopPdfAjax.do', true);
    	    xhr.setRequestHeader("Content-Type", "text/html; charset=utf-8");
    	    xhr.onreadystatechange = function(resp) { // Call a function when the state changes.
    	        if (this.readyState === XMLHttpRequest.DONE && this.status === 200) {
    	        	
    	            console.log(resp);
    	            console.log("resp = "+resp.target.response);
    	            
    	        	var responseJson = JSON.parse(resp.target.response); 

    	        	alert(responseJson.message);
    	        }
    	    }
    	    console.log("iniciando..");
    	    var pdfData = getPdfData();
    	    console.log(pdfData);
    	    
    	    contenido = JSON.stringify(pdfData);
    	    contenido = String(contenido).replace(/á/gi,'a').replace(/é/gi,'e').replace(/í/gi,'i').replace(/ó/gi,'o').replace(/ú/gi,'u');
    	    console.log(contenido);
    	    
    	    xhr.send(contenido);
    	    
   	}
     
</script>
</head>

<body class="bodyInternas" <%=onLoad.toString()%>>
<bean:define id="dataresponsable" name="usuarioResponsable" type="com.desige.webDocuments.sacop.forms.plantilla1" scope="request" />
<bean:define id="data" name="sacoplantilla" type="com.desige.webDocuments.sacop.forms.plantilla1" scope="request" />

<table cellspacing=0 cellpadding=2 align=center border=0 width="100%">
    <tr>
        <td width="86%" valign="top">
            <html:form action="/loadSACOPMain.do?goTo=planilla6">
                <html:hidden styleId="idplanillasacop1" property="idplanillasacop1" value='<%=dataresponsable.getIdplanillasacop1()%>'/>
                <input type="hidden" name="idplanillasacop" value='<%=dataresponsable.getIdplanillasacop1()%>' />
                <input type="hidden" name="mostrardetrestext" value='<%=cuantosText%>' />
                <input type="hidden" name="sacoprelacionada" value='<%=sacoprelacionada%>' />
 
                <table width="100%">
                	<tr>
                		<td><img border="0" alt="" src="<%=basePath%>img/logos/empresa.gif"></td>
                		<td align="right"><input class="sacopButton" type="button" value="Generar Registro" onclick="generatePdf()"></td>
                	</tr>
                <%String fechaEstimada=dataresponsable.getFechaEstimada()!=null?dataresponsable.getFechaEstimada():"";%>
                
                <table align=center border=0 width="100%">
                    <tr>
                        <td colspan="4">
                            <table class="clsTableTitle" width="100%" cellSpacing="0" cellPadding="2" align=center border=0>
                                <tr>
	                                <td class="td_title_bc" width="*" height="18">
                                    	<span id="data_titleSacop"><%title=rb.getString("scp.actionType".concat(dataresponsable.getCorrecpreven()));%> <%=title%> <%=rb.getString("scp.num")%>:<bean:write name="data" property="sacopnum"/></span>
	                                </td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                    <tr>
                        <td class="titleLeft">
                            <span class="titleLeftFont">
                                <%=rb.getString("scp.datosEmision")%>
                            </span>
                        </td>
                    </tr>
                    <tr>
                        <td colspan="4" class="infoTD">
                            <table  class="table-head" width="100%">
                            <tr>
                                <td width="30%" class="td_gris_l">
                                    <b><%= rb.getString("scp.emisor") %>:</b>
                                    <br/>
                                    <div class="alignRight">
                                        <%String nom=data.getNombres()+" " + data.getApellidos();
                                            nom=nom!=null?nom:"";
                                        %>
                                        <html:hidden property="nombres"  value='<%=nom%>'/>
                                        <html:hidden property="cargo"  value='<%=data.getCargo()%>'/>
                                        <html:hidden property="user"/>
                                        <a href="javascript:showCharge('<%=nom%>','<%=data.getCargo()%>')" class="ahref_b" >
                                            <logic:present name="showCharge" scope="session">
                                                <span id="data_emisor"><%=data.getCargo()%></span>
			                                </logic:present>
			                                <logic:notPresent name="showCharge" scope="session">
			                                    <span id="data_emisor"><%=nom%></span>
			                                </logic:notPresent>
			                            </a>
                                    </div>
                                </td>
                                <td width="30%" class="td_gris_l">
                                    <b><%=rb.getString("scp.edo")%>:</b>
                                    <html:hidden property="edodelsacop" value='<%=dataresponsable.getEdodelsacop()!=null?dataresponsable.getEdodelsacop():""%>'/>
                                    <br/>
                                    <div class="alignRight">
                                        <span id="data_estado"><%=dataresponsable.getEdodelsacoptxt()!=null?dataresponsable.getEdodelsacoptxt():""%></span>
                                    </div>
                                    <br/>
                                </td>
                                <td class="td_gris_l">
                                    <b><%=rb.getString("scp.orgsacop")%>:</b>
                                    <br/>
                                    <div  class="alignRight">
                                        <span id="data_tipo"><%=dataresponsable.getOrigensacoptxt()!=null?dataresponsable.getOrigensacoptxt():""%></span>
                                    </div>
                                    <br/>
                                </td>
                            </tr>
                            <tr>
                                <td class="td_gris_l">
                                    <b><%=rb.getString("scp.fechae")%>:</b>
                                    <br/>
                                    <div class="alignRight">
                                        <html:hidden property="fechaEmision" value='<%=data.getFechaEmision()!=null?data.getFechaEmision():""%>'/>
                                        <span id="data_fechaEmision"><%=fechaEmision!=null?fechaEmision:""%></span>
                                    </div>
                                    <br/>
                                </td>
                                <td width="30%" class="td_gris_l">
                                    <b><%=rb.getString("scp.afctresponsable")%>:</b>
                                    <br/>
                                    <div class="alignRight">
                                        <% String nomResp=dataresponsable.getNombres()+" " + dataresponsable.getApellidos();%>
                                        <logic:present name="showCharge" scope="session">
                                            <a href="javascript:showCharge('<%=nomResp%>','<%=dataresponsable.getCargo()%>')" class="ahref_b">
                                                <span id="data_responsableArea"><%=dataresponsable.getCargo()!=null?dataresponsable.getCargo():""%></span>
                                            </a>
                                        </logic:present>
                                        <logic:notPresent name="showCharge" scope="session">
                                            <a href="javascript:showCharge('<%=nomResp%>','<%=dataresponsable.getCargo()%>')" class="ahref_b">
                                               <span id="data_responsableArea"><%=nomResp!=null?nomResp:""%></span>
                                            </a>
                                        </logic:notPresent>
                                    </div>
                                    <br/>
                                </td>
                                 <td width="30%" class="td_gris_l">
                                        <b><%= rb.getString("scp.receivingArea") %>:</b>
                                        <br/>
                                        <div class="alignRight">
                                           <span id="data_areaAfectada"><%=dataresponsable.getCargo().indexOf("[")!=-1?dataresponsable.getCargo().replaceAll("\\[|\\]", "-").split("-")[1]:""%></span>
                                        </div>
                                        <br/>
                                    </td>
                            </tr>
                            <tr>
                                <td width="30%" class="td_gris_l">
                                    <b><%= rb.getString("scp.fechastimada") %>:</b>
                                    <br/>
                                    <div class="alignRight">
                                        <html:hidden property="fechaEstimada" value='<%=dataresponsable.getFechaEstimada()!=null?dataresponsable.getFechaEstimada():""%>' />
                                        <span id="data_fechaEstimada"><%=fechaEstimada%></span>
                                    </div>
                                    <br/>
                                </td>
                                <td>
                                    <b><%= rb.getString("scp.fechareal") %>:</b>
                                    <br />
                                    <div class="alignRight">
                                        <span id="data_fechaReal"><%=dataresponsable.getFechaReal()!=null?dataresponsable.getFechaReal():""%></span>
                                    </div>
                                </td>
                                <%
                                if(dataresponsable.getFechaWhenDiscovered() != null){
                                %>
                                    <td width="30%" class="td_gris_l">
                                        <b><%= rb.getString("scp.fechaWhenDiscovered") %>:</b>
                                        <br/>
                                        <div class="alignRight">
                                            <html:hidden property="fechaWhenDiscovered" value="<%=dataresponsable.getFechaWhenDiscovered()%>" />
                                            <span id="data_fechaHallazgo"><%=dataresponsable.getFechaWhenDiscovered()%></span>
                                        </div>
                                        <br/>
                                    </td>
                                <%
                                } else {
                                %>
                                    <td>
                                        <html:hidden property="fechaWhenDiscovered" value="" />
                                        <span id="data_fechaHallazgo"> </span>
                                    </td>
                                <%
                                }
                                %>
                            </tr>
                            <tr>
                                <td width="30%" class="td_gris_l">
                                    <%
                                    if(! ToolsHTML.isEmptyOrNull(dataresponsable.getNombreClasificacionSACOP())){
                                    %>
                                        <b><%=rb.getString("scp.clasificacionsacop")%>:</b>
                                        <br/>
                                        <div class="alignRight">
                                            <html:hidden property="clasificacion" value="<%=dataresponsable.getFechaWhenDiscovered()%>" />
                                            <span id="data_clasificacion"><%=dataresponsable.getNombreClasificacionSACOP()%></span>
                                        </div>
                                    <%
                                    }
                                    %>
                                </td>
                                <%
                                if(dataresponsable.getFechaVerificacion() != null){
                                %>
                                    <td width="30%" class="td_gris_l">
                                        <b><%= rb.getString("scp.fechaverificacion") %>:</b>
                                        <br/>
                                        <div class="alignRight">
                                            <html:hidden property="fechaVerificacion" value="<%=dataresponsable.getFechaVerificacion()%>" />
                                            <span id="data_fechaVerificacion"><%=dataresponsable.getFechaVerificacion()%></span>
                                        </div>
                                        <br/>
                                    </td>
                                <%
                                } else {
                                %>
                                    <td>
                                        <html:hidden property="fechaVerificacion" value="" />
                                        <span id="data_fechaVerificacion"> </span>
                                    </td>
                                <%
                                }
                                %>
                                <%
                                if(dataresponsable.getFechaCierre() != null){
                                %>
                                    <td width="30%" class="td_gris_l">
                                        <b><%= rb.getString("scp.fechacierre") %>:</b>
                                        <br/>
                                        <div class="alignRight">
                                            <html:hidden property="fechaCierre" value="<%=dataresponsable.getFechaCierre()%>" />
                                            <span id="data_fechaCierre"><%=dataresponsable.getFechaCierre()%></span>
                                        </div>
                                        <br/>
                                    </td>
                                <%
                                } else {
                                %>
                                    <td>
                                        <html:hidden property="fechaCierre" value="" />
                                        <span id="data_fechaCierre"> </span>
                                    </td>
                                <%
                                }
                                %>
                            </tr>
                            <tr>
                            	<td colspan="2" >
                            		
                            	</td>
                            	<td>
					         		<b><%=rb.getString("scp.applicant")%></b>
					         		<br>
                                    <div class="alignRight">
			                             <a href="javascript:showCharge('<%=data.getSolicitantetxt()%>','<%=data.getCargoSolicitante()%>')" class="ahref_b">
			                             <logic:present name="showCharge" scope="session">
			                                  <span id="data_solicitante"><%=data.getCargoSolicitante()%></span>
			                             </logic:present>
			                             <logic:notPresent name="showCharge" scope="session">
			                                  <span id="data_solicitante"><%=data.getSolicitantetxt()%></span>
			                             </logic:notPresent>
			                             </a>
			                        </div>
                            	</td>
                            </tr>
                            
                        
                            <tr>
                                <logic:present name="sacopfile" scope="request">
                                <td colspan="3">
                                    <b><%= rb.getString("doc.link") %>:</b>
                                    <br />
                                    <div class="alignRight">
                                        <a href="javascript:showFileSacop('<%=dataresponsable.getIdplanillasacop1()%>')" class="ahref_b">
                                            <span id="data_relacionado"><%= data.getNameRelatedDocument() %></span><img src="<%=basePath%>img/pendings.gif" width="16" height="16" border=0 />
                                        </a>
                                    </div>
                                    <br />
                                </td>
                                </logic:present>
                                
                            </tr>
                            
                            
                            
                            </table>
                            
							<br>
							<logic:present name="existesacops_relacionadas" scope="session">
                                <div >
                                    <b><%= rb.getString("scp.sacoprelacionados") %>:</b>
                                    <br />
                                    <div>
                                        <input type="hidden" name="exec" value=""/>
                                        <input type="hidden" name="sacop_relacionadas" value='<%=dataresponsable.getSacop_relacionadas()!=null?dataresponsable.getSacop_relacionadas():""%>'/>
			                           <table class="table-head-related" width="100%" >
			                           <%int item=-1; %>
                                        <logic:iterate id="bean" name="data" property="sacopRelacionadasDetail" type="com.desige.webDocuments.sacop.forms.Plantilla1BD">
                                        		<%item++;%>
				                           		<tr>
				                           			<td>
			                                           <a href="javascript:abrirVentana('Sacop_RelacionadasIndividual.jsp?edo=<bean:write name="bean" property="estado"/>&id=<bean:write name="bean" property="idplanillasacop1"/>',800, 600);" class="ahref_b">
			                                                <span id="data_sacopsNumber_<%=item%>"><bean:write name="bean" property="sacopnum"/></span>
			                                           </a>
				                           			</td>
				                           			<td>
			                                                <span id="data_sacopsDescription_<%=item%>"><%=bean.getDescripcion()%></span>
				                           			</td>
				                           			<td>
				                           				<span id="data_sacopsDate_<%=item%>"><%=ToolsHTML.formatDateShow(bean.getFechaemision(),false) %></span>
				                           			</td>
				                           		</tr>
                                        </logic:iterate>
			                           </table>
                                    </div>
                                    <br />
                                </div>
                            </logic:present>                        </td>
                    </tr>
                    <logic:present name="existesacops_noconformidadesref" scope="session">
                         <logic:equal value="0" name="dataresponsable" property="correcpreven">
                             <tr>
                                <td class="titleLeft">
                                    <span id="data_noconformidadesref"><%=rb.getString("scp.sourceDocument")%></span>
                                </td>
                             </tr>
                         </logic:equal>
                         <logic:equal value="1" name="dataresponsable" property="correcpreven">
                            <tr>
                                <td class="titleLeft">
                                    <span id="data_noconformidadesref"><%=rb.getString("scp.sourceDocument")%></span>
                                </td>
                            </tr>
                         </logic:equal>
                        <tr>
	                        <td colspan="3" class="td_gris_l">
                                <html:textarea property="noconformidadesref" value='<%=dataresponsable.getNoconformidadesref()%>' style="display:none" />
                                <logic:iterate id="bean" name="data" property="noConformidadesDetail" type="com.desige.webDocuments.document.forms.BaseDocumentForm">
                                   <a href="javascript:showDocumentPublishImp('<bean:write name="bean" property="numberGen"/>',<bean:write name="bean" property="numVer"/>,'<bean:write name="bean" property="numberGen"/>','1')" class="ahref_b">
                                        <bean:write name="bean" property="prefix"/>
                                        <bean:write name="bean" property="number"/>
                                        <bean:write name="bean" property="nameDocument"/>
                                        <bean:write name="bean" property="typeDocument"/>
                                   </a>
                                   <br />
                                </logic:iterate>
                                <br />
                            </td>
                        </tr>
                    </logic:present>
                 <logic:present name= "mostrardetrestext" scope="request">
                    <logic:present name="existesacops_noconformidades" scope="session">
                    <tr>
                        <td  class="titleLeft">
                            <%=rb.getString("scp.noconformidades")%>
                        </td>
                    </tr>
                    <tr>
                       <td colspan="3" align="left" class="td_gris_l infoTD">
                           <html:textarea property="descripcion" style="display:none" />
                           <html:textarea property="noconformidades" value='<%=dataresponsable.getNoconformidades()%>' style="display:none" />
                           <div align="left" class="clsMessageViewI" style="width: 100%">
                                <span id="data_noconformidades"><%=dataresponsable.getNoconformidades()%></span>
                           </div>
                           <br />
                       </td>
                    </tr>
                    </logic:present>
                    <tr>
                        <td  class="titleLeft">
                            <%=rb.getString("scp.desc")%>
                        </td>
                    </tr>
                    <tr>
                        <td colspan="3" align="left" class="td_gris_l infoTD">
                            <html:textarea property="descripcion" style="display:none" />
                            <div align="left" class="clsMessageViewI" style="width: 100%">
                                <span id="data_comentario"><%=dataresponsable.getDescripcion()%></span>
                            </div>
                            <br />
                        </td>
                    </tr>
                    <tr>
                        <td  class="titleLeft">
                            <logic:equal value="0" name="dataresponsable" property="correcpreven">
                                   <span id="data_posibleCausaTitle"><%=rb.getString("scp.pblecausasnoconformidad0")%></span>
                              </logic:equal>
                              <logic:notEqual value="0" name="dataresponsable" property="correcpreven">
                                     <span id="data_posibleCausaTitle"><%=rb.getString("scp.pblecausasnoconformidad1")%></span>
                              </logic:notEqual>
                        </td>
                    </tr>
                    <tr>
                        <td colspan="3" align="left" class="td_gris_l infoTD">
                            <div align="left" class="clsMessageViewI" style="width: 100%">
                            <span id="data_posibleCausa"><%
                            	String[] pos = dataresponsable.getCausasnoconformidad().split(",");
	                         	for(int i=0; i<pos.length; i++) {
	                         		if(listOrderPossibleCause.containsKey(pos[i])) {
	                         			possible = listOrderPossibleCause.get(pos[i]);
	                         			out.print("*- ");
	                         			out.println(possible.getDescPossibleCause());
	                         		}
	                         	}
                            %></span>
                            </div>
                            <br />
                        </td>
                    </tr>
                    <tr>
                        <td  class="titleLeft">
                            <%=rb.getString("scp.acrecomendadas")%>
                        </td>
                    </tr>
                    <tr>
                        <td colspan="3" align="left" class="td_gris_l infoTD">
                            <div align="left" class="clsMessageViewI" style="width: 100%">
                            <span id="data_accionesRecomendadas"><%= dataresponsable.getAccionrecomendada() %></span>
                            </div>
                            <br />
                        </td>
                    </tr>
                    <tr>
                        <td  class="titleLeft">
                        	<span id="data_accionesCorrectivaInformaTitle"><%=title.indexOf("-")!=-1?title.split("-")[0]:title%> <%=rb.getString("scp.informa_a")%>:</span>
                        </td>
                    </tr>
                    <tr>
                        <td colspan="3" align="left" class="td_gris_l infoTD">
                            <div align="left" class="clsMessageViewI" style="width: 100%">
                                <span id="data_accionesCorrectivaInforma">
                                <logic:present name="usuarios1" scope="request">
                                   <logic:iterate id="bean" name="usuarios1" scope="request" type="com.desige.webDocuments.utils.beans.Search">
                                    <%=bean.getDescript()%><br>
                                   </logic:iterate>
                               </logic:present></span>
                            </div>
                            <br />
                        </td>
                    </tr>
                    <tr>
                        <td  class="titleLeft">
                            <%=rb.getString("scp.rqtaplicable")%>
                        </td>
                    </tr>
                    <tr>
                        <td colspan="3" align="left" class="td_gris_l infoTD">
                            <div align="left" class="clsMessageViewI" style="width: 100%">
                            	<span id="data_requisitoAplicable">
                                <logic:present name="norms" scope="request">
                                       <logic:iterate id="bean" name="norms" scope="request" type="com.desige.webDocuments.utils.beans.Search">
                                     <%=bean.getDescript()%><br>
                                       </logic:iterate>
                                  </logic:present>
                                  </span>
                            </div>
                            <br />
                        </td>
                    </tr>
                    <tr>
                        <td  class="titleLeft">
                            <%=rb.getString("scp.prcafectados")%>
                        </td>
                    </tr>
                    <tr>
                        <td colspan="3" align="left" class="td_gris_l infoTD">
                            <div align="left" class="clsMessageViewI" style="width: 100%">
                            	<span id="data_procesos">
                                <logic:present name="procesosSacop" scope="request">
                                     <logic:iterate id="bean" name="procesosSacop" scope="request" type="com.desige.webDocuments.utils.beans.Search">
                                      <%=bean.getDescript()%><br>
                                      </logic:iterate>
                                  </logic:present>
                                  </span>
                            </div>
                            <br />
                        </td>
                    </tr>
                    <tr>
                        <td  class="titleLeft">
                            <%=rb.getString("scp.obs")%>
                        </td>
                    </tr>
                    <tr>
                        <td colspan="3" align="left" class="td_gris_l infoTD">
                            <div align="left" class="clsMessageViewI" style="width: 100%">
                                <html:textarea property="accionobservacion" style="display:none" />
                                <span id="data_causaRaizUno"><%=dataresponsable.getAccionobservacion()%></span>
                                <%
                                if(! ToolsHTML.isEmptyOrNull(dataresponsable.getArchivoTecnica().getFileName())){
                                %>
                                    <br />
                                    <br />
                                    <b><span id="data_causaRaizDos"><%=rb.getString("scp.tecnica")%>:</span></b> 
                                    <a href="javascript:showFileSacop('<%=dataresponsable.getIdplanillasacop1() + "/" + dataresponsable.getArchivoTecnica().getFileName() %>');">
                                        <span id="data_causaRaizTres"><%= dataresponsable.getArchivoTecnica().getFileName() %></span>
                                        &nbsp;&nbsp;
                                        <img src="<%=basePath%>img/pendings.gif" width="16" height="16" border="0" />
                                        </a>
                                <%
                                }
                                %>
                                <%if(dataresponsable.getNameDocumentAssociate()!=null && !dataresponsable.getNameDocumentAssociate().trim().equals("")){%>    
								 | 
                                <a href="#" onclick="showDocumentPublishImp(<%=dataresponsable.getIdDocumentAssociate()%>,<%=dataresponsable.getNumVerDocumentAssociate()%>,<%=dataresponsable.getIdDocumentAssociate()%>)">
                                	<%=dataresponsable.getNameDocumentAssociate()%>
                                </a>
								<%}%>
                                
                            </div>
                            <br />
                        </td>
                    </tr>
                    <tr>
                        <td  class="titleLeft">
                            <%=rb.getString("scp.descriptionActionMainTitle")%>
                        </td>
                    </tr>
                    <tr>
                        <td colspan="3" align="left" class="td_gris_l infoTD">
                            <html:textarea property="descripcionAccionPrincipal" style="display:none" />
                            <div align="left" class="clsMessageViewI" style="width: 100%">
                                <%=dataresponsable.getDescripcionAccionPrincipal()%>
                            </div>
                            <br />
                        </td>
                    </tr>
                    <tr>
                        <td  class="titleLeft">
                           <%=rb.getString("scp.acciones")%>
                        </td>
                    </tr>
                    <tr>
                        <td colspan="3" align="left" class="td_gris_l infoTD">
                            <div align="left" class="clsMessageViewI" style="width: 100%">
                                <span id="data_acciones"><%=dataresponsable.getAccionobservaciontxt().replaceAll(rb.getString("scp.accion").concat(":"),
                                        " <b>".concat(rb.getString("scp.accion")).concat(":").concat("</b>")) %></span>
                            </div>
                           <strong>
                                <%=rb.getString("scp.observacion")%> :
                            </strong>
                            <span id="data_accionesObservaciones"><%= dataresponsable.getComntresponsablecerrar() %></span>
                        </td>
                    </tr>
                    <tr>
                        <td  class="titleLeft">
                           <%=rb.getString("scp.cumplaccstable")%>
                        </td>
                        <td>
                            <logic:equal name='dataresponsable' property='accionesEstablecidas' value="0">
                                <input type="radio" checked disabled name=accionesEstablecidas value='0'/><span id="data_cumplieron"><%=rb.getString("scp.si")%></span>
                            </logic:equal>
                            <logic:notEqual name='dataresponsable' property='accionesEstablecidas' value="0">
                                <input type="radio"  disabled name=accionesEstablecidas value='0'/><span id="data_cumplieron"><%=rb.getString("scp.si")%></span>
                            </logic:notEqual>
                            &nbsp;
                            <logic:equal name='dataresponsable' property='accionesEstablecidas' value="1">
                                <input type="radio" checked disabled name=accionesEstablecidas value='1'/><span id="data_cumplieron"><%=rb.getString("scp.no")%></span>
                            </logic:equal>
                            <logic:notEqual name='dataresponsable' property='accionesEstablecidas' value="1">
                                <input type="radio"  disabled name=accionesEstablecidas value='0'/><span id="data_cumplieron"><%=rb.getString("scp.no")%></span>
                            </logic:notEqual>
                            &nbsp;
                        </td>
                    </tr>
                    <tr>
                        <td colspan="3" align="left" class="td_gris_l infoTD">
                            <div align="left" class="clsMessageViewI" style="width: 100%">
                                <span id="data_cumplieronDetalle"><%=dataresponsable.getAccionesEstablecidastxt()%></span>
                            </div>
                            <br />
                        </td>
                    </tr>
                    <tr>
                        <td  class="titleLeft">
                           <%=rb.getString("scp.accefecraiz")%> .
                        </td>
                        <td>
                            <logic:equal name='dataresponsable' property='eliminarcausaraiz' value="0">
                                <input type="radio" checked disabled name=eliminarcausaraiz value='0'/><span id="data_eficaz"><%=rb.getString("scp.si")%></span>
                            </logic:equal>
                            <logic:notEqual name='dataresponsable' property='eliminarcausaraiz' value="0">
                                <input type="radio" disabled  name=eliminarcausaraiz value='0'/><span id="data_eficaz"><%=rb.getString("scp.si")%></span>
                            </logic:notEqual>
                            &nbsp;
                            <logic:equal name='dataresponsable' property='eliminarcausaraiz' value="1">
                                <input type="radio" checked disabled name=eliminarcausaraiz value='1'/><span id="data_eficaz"><%=rb.getString("scp.no")%></span>
                            </logic:equal>
                            <logic:notEqual name='dataresponsable' property='eliminarcausaraiz' value="1">
                                <input type="radio" disabled name=eliminarcausaraiz value='1'/><span id="data_eficaz"><%=rb.getString("scp.no")%></span>
                            </logic:notEqual>
                        </td>
                    </tr>
                    <tr>
                        <td colspan="3" align="left" class="td_gris_l infoTD">
                            <div align="left" class="clsMessageViewI" style="width: 100%">
                                <span id="data_eficazDetalle"><%=dataresponsable.getEliminarcausaraiztxt()%></span>
                            </div>
                            <br />
                        </td>
                    </tr>
                    
               
                 
                  <%
                                    if(ToolsHTML.isEmptyOrNull(dataresponsable.getNumberTrackingSacop())){
                                    %>
                                    <tr>
                  <td  class="titleLeft">
                      ¿<%=rb.getString("requestSacop.trackingTitle0")%>?
                  </td>
                  <td >
                  	<table>
                  		<tr>
                  			<td>
		                       <span id="data_seguimiento"><%=dataresponsable.getRequireTracking().equals("0")?"SI":ToolsHTML.isSacopPadre("'"+data.getSacopnum()+"'")%></span> 
		                      
                       		</td>
                       	</tr>
                     </table>
                      <%
                                    }
                                    %>
                  </td>
               </tr>
               
             
                <tr>
                    <td colspan="3" align="left" class="td_gris_l infoTD">
                        <div align="left" class="clsMessageViewI" style="width: 100%">
                            <span id="data_seguimientoFecha"><%=ToolsHTML.formatDateShow(dataresponsable.getRequireTrackingDate(),false)%></span>
                        </div>
                        <br />
                    </td>
                </tr>
               <tr>
               		<td colspan="2"><br></td>
               </tr>
                    
                    
                    
                    
                    
                    
                </logic:present>
                    <tr id="botoneraSacop">
                    </tr>
                </table>
                
            </html:form>
        </td>
    </tr>
</table>

</body>
</html>