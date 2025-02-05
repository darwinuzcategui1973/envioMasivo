<!--  planillasacop2.jsp -->
<!-- relacionadas: planillasacop2.jsp,planillasacop3_0.jsp,planillasacop4.jsp,planillasacop5.jsp,planillasacop6.jsp,planillasacop10.jsp --> 
<%@ page import="java.util.Locale,
                 java.util.ResourceBundle,
                 java.text.SimpleDateFormat,
                 com.desige.webDocuments.persistent.managers.HandlerDocuments, 
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

    String sacoprelacionada=request.getAttribute("sacoprelacionada")!=null?(String)request.getAttribute("sacoprelacionada"):"0";

    String mostrardetrestext=request.getAttribute("mostrardetrestext")!=null?(String)request.getAttribute("mostrardetrestext"):"0";
    String rechazoapruebo=request.getAttribute("rechazoapruebo")!=null?(String)request.getAttribute("rechazoapruebo"):"";
    int cuantosText=Integer.parseInt(mostrardetrestext);
    String title="";
    String fechaEmision=request.getAttribute("fechaEmision")!=null?(String)request.getAttribute("fechaEmision"):"";
    if (ToolsHTML.isEmptyOrNull(numGo)) {
        numGo = "1";
    }
    
    PossibleCauseTO possible = null; 
    ActionRecommendedTO cause = null;
    Map<String,PossibleCauseTO> listOrderPossibleCause = (Map)request.getAttribute("ListOrderPossibleCause");
%>
<%--
    Esta pagina llena la solicitud si es aceptada o no es aceptado el sacop.
    Una vez que se acepte, esta pagina es  enviada a /planillasacop2.do y luego es forward a LoadSacopMain.do con
    la variable goTo inicializada en reload si es negada la aceptacion o
    la variable goTo inicializada en planilla3_0 si es aceptada y permite continuar a cargar los valores
    para mostrar la pagina planillasacop3_0.jsp
--%>
<html>
<head>
<title><%=rb.getString("principal.title")%></title>
<jsp:include page="meta.jsp" />

<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1"> 
<link href="estilo/sacop.css" rel="stylesheet" type="text/css">
<script type="text/javascript" src="./estilo/funciones.js"></script>
<script type="text/javascript">

function loadSacops(number,input,nameForm,value,type) {
       abrirVentana("Sacop_Relacinadas.do?number="+number+"&input="+input+"&nameForma="+nameForm+"&value="+value+"&type="+type+"&userAssociate="+document.sacoplantilla.sacop_relacionadas.value,600,400)
}

function showCharge(userName,charge) {
	    charge = charge.replace(/\[/gi,"-").replace(/\]/gi,"-");
        window.open("showCharge.jsp?userName="+userName+"&charge="+charge, "WebDocuments", "resizable=no,scrollbars=yes,statusbar=yes,width=300,height=200,left=410,top=250");
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
    function salvar(forma,rechazo,title) {
        var row = 0;
        alert("salvar en planillasacop2 darwinuzcategui");

        if (rechazo=='1'){
            //deshabiloitamos el boton acpetar
          //  forma.btnOK.disabled=true;
           if (Trim(forma.noaceptada.value)==""){
              alert('<%=rb.getString("scp.vald8")%>');
              //se abre la forma para colocar los comentarios del rechazo
              evaluaRechazo(forma.rechazoapruebo,forma,'<%=rb.getString("scp.rechazado")%>');
              return false;
           }
        }
    // if (Trim(forma.noaceptada.value)!=""){
      if (rechazo=='1'){
         //acaba de rechazar, se coloca en uno
         forma.rechazoapruebo.value='1';
         if (hayComillas(forma.noaceptada.value)){
                 alert('<%=rb.getString("scp.vald2")%>');
                 evaluaRechazo(forma.rechazoapruebo,forma,'<%=rb.getString("scp.rechazado")%>');
                 return false;
         }

            if (forma.noaceptada.value!=""){
                 if (confirm("<%=rb.getString("scp.val12")%>")) {
                     evaluaRechazo(forma.rechazoapruebo,forma,'<%=rb.getString("scp.rechazado")%>');
                     return false;
                }else{
                    forma.goTo.value="reload";
                    forma.btnOK.disabled=true;
                    forma.btnCancel.disabled=true;
                    forma.btnzo.disabled=true;
                    alert("aqui else darwinuzcategui");
                    forma.submit();
                    window.parent.parent.administrarCancelar();
                }
            }else{
                //showWindow('SacopMiniText.do','noaceptada','sacoplantilla',forma.noaceptada.value,'Rechazo',800,500,'0');
                evaluaRechazo(forma.rechazoapruebo,forma,'<%=rb.getString("scp.rechazado")%>');
              //  alert("De una respuesta de porque no acepto la aprobaciòn.");
                return false;
            }
        }else {
         //cero significa que aprobo
                forma.rechazoapruebo.value='0';
                //forma.goTo.value="planilla3_0";
                forma.goTo.value="reload";
                forma.idplanillasacop1.value=forma.idplanillasacop1.value;
                forma.btnOK.disabled=true;
                forma.btnCancel.disabled=true;
                forma.btnzo.disabled=true;
                alert("aqui else  aprobrado darwinuzcategui en planillasacop2");
               
                //probando esto si funciona
                window.parent.parent.administrarCancelar();
                // window.parent.parent.inicializar();
                //window.parent.visualizar("ABCD")
                
              
                 forma.submit();
        }
    }

    function editField(pages,input,value,forma){
        var hWnd = null;
        hWnd = window.open(pages+"?input="+input+"&value="+value+"&nameForma="+forma.name,"Catalogo","width=400,height=300,resizable=yes,scrollbars=yes,left=300,top=250");
        if ((document.window != null) && (!hWnd.opener)){
            hWnd.opener = document.window;
        }
    }

    function cancelar(forma) {
        forma.action="loadSACOPMain.do?goTo=reload";
        //forma.submit();
        //boton  que no debe hacer  regresa submit
        alert("Prueba aqui darwinuzcategui entra a cancelar en planillaSacop2");
        
        window.parent.parent.administrarCancelar();
        window.parent.parent.inicializar();
        window.parent.parent.visualizar("ABCD")
        
      
         forma.submit();
      
        //ver("SacopPendienteDarwin");
    }
    
  
    function cancelarsacoprelacionada(forma){
           window.close();
           window.parent.parent.administrarCancelar();
    }

  function toggleDiv(gn)  {
	var g = document.all(gn);

	if( g.style.display == "none")
		g.style.display = "";
	else
		g.style.display = "none";
    }
    function evaluaRechazo(rechazo,forma,title){
       // if (rechazo.checked){
            showWindow('SacopMiniText.do','noaceptada','sacoplantilla',forma.noaceptada.value,title,800,500,'0');
      //  }
    }
   function nextp(forma){
      var i=parseInt(forma.mostrardetrestext.value);
      if (i<6){
         forma.btnatras.disabled=false;
         forma.action="loadSACOPMain.do?goTo=planillados";
         forma.mostrardetrestext.value=parseInt(forma.mostrardetrestext.value) + 3;
         forma.submit();
      }else{
            forma.btnsig.disabled=true;
            forma.btnatras.disabled=false;
      }

   }
   function backp(forma){
      forma.action="loadSACOPMain.do?goTo=planillados";
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
</script>

</head>

<body class="bodyInternas" <%=onLoad.toString()%>>

<bean:define id="dataresponsable" name="usuarioResponsable" type="com.desige.webDocuments.sacop.forms.plantilla1" scope="request" />
<bean:define id="data" name="sacoplantilla" type="com.desige.webDocuments.sacop.forms.plantilla1" scope="request" />
<table cellSpacing=0 cellPadding=2 align=center border=0 width="100%">
    <tr>
        <td width="86%" valign="top">
            <html:form action="/planillasacop2.do">
                <html:hidden property="idplanillasacop1" value='<%=dataresponsable.getIdplanillasacop1()%>'/>
                <input type='hidden' name="idplanilla2" value='<%=dataresponsable.getIdplanillasacop1()%>'/>
                <%--Al momento de dar aceptar, el java  script pone el valor para la variable goTo--%>
                <input type='hidden' name=goTo value="">
                <input type="hidden" name="mostrardetrestext" value='<%=cuantosText%>'>
                <input type="hidden" name="sacoprelacionada" value='<%=sacoprelacionada%>'>
                <input type="hidden" name="exec" value=""/>
                
                <%= ToolsHTML.getHTMLImgElementIfExists(request, "img/logos/empresa.gif") %>
                <table align=center border=0 width="100%">
                    <tr>
                        <td colspan="4">
                            <table class="clsTableTitle" width="100%" cellSpacing="0" cellPadding="2" align=center border=0>
                                <tr>
                                    <td class="td_title_bc" width="*" height="18">
                                    	<%title=rb.getString("scp.actionType".concat(dataresponsable.getCorrecpreven()));%>
                                    	<%=title%>&nbsp; <%=rb.getString("scp.num")%>:<bean:write name="data" property="sacopnum"/>
                                    </td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                    <tr></tr>
                    <tr></tr>
                    <tr>
                        <td class="titleLeft">
                            <span class="titleLeftFont">
                                <%=rb.getString("scp.datosEmision")%>
                            </span>
			            </td>
			        </tr>
			        <tr>
                        <td colspan="4" class="infoTD">
                            <table class="table-head" width="100%">
			                    <tr>
			                        <td width="30%" class="td_gris_l">
			                            <b><%= rb.getString("scp.emisor") %>:</b>
			                            <br/>
			                            <div class="alignRight">
			                                <%
	                                            String nom = data.getNombres() + " " + data.getApellidos();
	                                            nom = nom != null ? nom : "";
	                                        %>
	                                        <html:hidden property="nombres" value='<%=nom%>'/>
	                                        <html:hidden property="cargo" value='<%=data.getCargo()%>'/>
	                                        <html:hidden property="user"/>
	                                        <a href="javascript:showCharge('<%=nom%>','<%=data.getCargo()%>')" class="ahref_b alignRight">
		                                        <logic:present name="showCharge" scope="session">
		                                             <%=data.getCargo()%>
		                                        </logic:present>
		                                        <logic:notPresent name="showCharge" scope="session">
		                                             <%=nom%>
		                                        </logic:notPresent>
		                                    </a>
			                            </div>
			                            <br/>
			                        </td>
                                    <td width="30%" class="td_gris_l">
                                        <b><%=rb.getString("scp.edo")%>:</b>
                                        <html:hidden property="edodelsacop" value='<%=dataresponsable.getEdodelsacop()!=null?dataresponsable.getEdodelsacop():""%>'/>
                                        <br/>
                                        <div class="alignRight">
                                            <%=dataresponsable.getEdodelsacoptxt()!=null?dataresponsable.getEdodelsacoptxt():""%>
                                        </div>
                                        <br/>
                                    </td>
                                    <td class="td_gris_l">
                                        <b><%=rb.getString("scp.fechae")%>:</b>
                                        <br/>
                                        <div class="alignRight">
                                            <html:hidden property="fechaEmision" value='<%=data.getFechaEmision()!=null?data.getFechaEmision():""%>'/>
                                            <%=fechaEmision!=null?fechaEmision:""%>
                                        </div>
                                        <br/>
                                    </td>
                                </tr>
                                
                                <tr>
                                   <td width="30%" class="td_gris_l">
                                        <b><%= rb.getString("scp.receivingArea") %>:</b>
                                        <br/>
                                        <div class="alignRight">
                                        	<%if(dataresponsable!=null && dataresponsable.getCargo()!=null) {%>
                                            <%=dataresponsable.getCargo().indexOf("[")!=-1?dataresponsable.getCargo().replaceAll("\\[|\\]", "-").split("-")[1]:""%>
                                            <%}%>
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
                                                    <%=dataresponsable.getCargo()!=null?dataresponsable.getCargo():""%>
                                                </a>
                                            </logic:present>
                                            <logic:notPresent name="showCharge" scope="session">
                                                <a href="javascript:showCharge('<%=nomResp%>','<%=dataresponsable.getCargo()%>')" class="ahref_b">
                                                    <%=nomResp!=null?nomResp:""%>
                                                </a>
                                            </logic:notPresent>
                                        </div>
                                        <br/>
                                    </td>
                                    <td class="td_gris_l">
                                        <b><%=rb.getString("scp.orgsacop")%>:</b>
                                        <br/>
                                        <div class="alignRight">
                                            <%=dataresponsable.getOrigensacoptxt()!=null?dataresponsable.getOrigensacoptxt():""%>
                                            <%--HandlerDocuments.getTypeRegisterClass(dataresponsable.getIdDocumentRelated())--%>
                                        </div>
                                        <br/>
                                    </td>
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
	                                            <%=dataresponsable.getNombreClasificacionSACOP()%>
	                                        </div>
                                        <%
                                        }
                                        %>
                                    </td>
                                    <td width="30%" class="td_gris_l">
                                        <%
		                                if(dataresponsable.getFechaWhenDiscovered() != null){
		                                %>
			                                <b><%= rb.getString("scp.fechaWhenDiscovered") %>:</b>
	                                        <br/>
	                                        <div class="alignRight">
	                                            <html:hidden property="fechaWhenDiscovered" value="<%=dataresponsable.getFechaWhenDiscovered()%>" />
	                                            <%=dataresponsable.getFechaWhenDiscovered()%>
	                                        </div>
	                                        <br/>
	                                    <%
	                                    }
	                                    %>
                                    </td>
                                    <td>
			                                <b><%= rb.getString("scp.applicant") %>:</b>..
	                                        <br/>
	                                        <div class="alignRight">
		                                        <a href="javascript:showCharge('<%=dataresponsable.getSolicitantetxt()%>','<%=dataresponsable.getCargoSolicitante()%>')" class="ahref_b alignRight">
			                                        <logic:present name="showCharge" scope="session">
			                                             <%=dataresponsable.getCargoSolicitante()%>
			                                        </logic:present>
			                                        <logic:notPresent name="showCharge" scope="session">
			                                             <%=dataresponsable.getSolicitantetxt()%>
			                                        </logic:notPresent>
			                                    </a>
	                                        </div>
	                                        <br/>
                                    </td>
                                </tr>
                                
                                <logic:present name="sacopfile" scope="request">
                                <tr>
                                    <td  width="26%" height="26" valign="middle">
                                        <b><%=rb.getString("doc.link")%></b>
                                    </td>
                                    <td colspan="2">
                                        <a border=0 href="javascript:showFileSacop('<%=dataresponsable.getIdplanillasacop1()%>')" class="ahref_b">
                                            <%= dataresponsable.getNameRelatedDocument() %>
                                            <img src="img/pendings.gif" width="16" height="16" border=0>
                                        </a>
                                    </td>
                                </tr>
                                </logic:present>
                                
                               
			                </table>
			                
			                
			                  <logic:present name="existesacops_relacionadas" scope="session">
                                  <div>
                                    <%=rb.getString("scp.sacoprelacionados")%>:
                                    <div>
                                        <input type="hidden" name="sacop_relacionadas" value='<%=dataresponsable.getSacop_relacionadas()!=null?dataresponsable.getSacop_relacionadas():""%>'/>
                                        
                                        <table class="table-head-related" width="100%" >
                                        <logic:iterate id="bean" name="data" property="sacopRelacionadasDetail" type="com.desige.webDocuments.sacop.forms.Plantilla1BD">
				                           		<tr>
				                           			<td>
			                                           <a href="javascript:abrirVentana('Sacop_RelacionadasIndividual.jsp?edo=<bean:write name="bean" property="estado"/>&id=<bean:write name="bean" property="idplanillasacop1"/>',800, 600);" class="ahref_b">
			                                                <bean:write name="bean" property="sacopnum"/>
			                                           </a>
				                           			</td>
				                           			<td>
			                                                <%=bean.getDescripcion()%>
				                           			</td>
				                           			<td>
				                           				<%=ToolsHTML.formatDateShow(bean.getFechaemision(),false) %>
				                           			</td>
				                           		</tr>
                                        </logic:iterate>
			                           </table>
                                    </div>
                                  </div>
                                </logic:present>
			                
			                
			            </td>
			        </tr>
			        
			        <logic:present name="existesacops_noconformidadesref" scope="session">
                         <tr>
                             <td class="titleLeft" colspan="2">
                                 <%=rb.getString("scp.sourceDocument")%>
                             </td>
                         </tr>
	                    <tr>
	                        <td colspan="3" class="td_gris_l td_gris_l infoTD">
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
                        <td class="titleLeft td_gris_l infoTD">
                            <%=rb.getString("scp.noconformidades")%>
                        </td>
                    </tr>
                    <tr>
                        <td colspan="3" align="left" class="td_gris_l infoTD">
                            <div align="left" class="clsMessageViewI" style="width: 100%; ">
                                <%=dataresponsable.getNoconformidades()%>
                            </div>
                            <br />
                        </td>
                    </tr>
                </logic:present>
                <tr>
                    <td class="titleLeft td_gris_l infoTD">
                        <%=rb.getString("scp.desc")%>
                    </td>
                </tr>
                <tr>
                    <td colspan="3" align="left" class="td_gris_l infoTD">
                        <div align="left" class="clsMessageViewI" style="width: 100%">
                            <%=dataresponsable.getDescripcion()%>
                        </div>
                        <br />
                    </td>
                </tr>
                <tr>
                    <td  class="titleLeft">
                    	<%=rb.getString("scp.pblecausasnoconformidad0")%>
                    </td>
                </tr>
                <tr>
                    <td colspan="3" align="left" class="td_gris_l infoTD">
                        <div align="left" class="clsMessageViewI" style="width: 100%;">
                            <%
                            	String[] pos = dataresponsable.getCausasnoconformidad().split(",");
	                         	for(int i=0; i<pos.length; i++) {
	                         		if(listOrderPossibleCause.containsKey(pos[i])) {
	                         			possible = listOrderPossibleCause.get(pos[i]);
	                         			out.print("*- ");
	                         			out.println(possible.getDescPossibleCause());
	                         		}
	                         	}
                            %>
                        </div>
                        <br />
                    </td>
                </tr>
                <logic:equal value="1" name="dataresponsable" property="rechazoapruebo">
                <tr>
                    <td  class="titleLeft">
                        <%=rb.getString("scp.rechazado")%>
                    </td>
                </tr>
                <tr>
                    <td colspan="3" align="left" class="td_gris_l infoTD">
                        <div align="left" class="clsMessageViewI" style="width: 100%;">
                            <%=dataresponsable.getNoaceptada()%>
                        </div>
                        <br />
                    </td>
                </tr>
                </logic:equal>
                <tr>
                    <td  class="titleLeft">
                        <%=rb.getString("scp.acrecomendadas")%>
                    </td>
                </tr>
                <tr>
                    <td colspan="3" align="left" class="td_gris_l infoTD">
                        <div align="left" class="clsMessageViewI" style="width: 100%;">
                            <%= dataresponsable.getAccionrecomendada() %>
                        </div>
                        <br />
                    </td>
                </tr>
                <tr>
                    <td  class="titleLeft">
                    	<%=title.indexOf("-")!=-1?title.split("-")[0]:title%>&nbsp;<%=rb.getString("scp.informa_a")%>:
                    </td>
                </tr>
                <tr>
                    <td colspan="3" align="left" class="td_gris_l infoTD">
                        <div align="left" class="clsMessageViewI" style="width: 100%">
                            <logic:present name="usuarios" scope="request">
                                <logic:iterate id="bean" name="usuarios" scope="request" type="com.desige.webDocuments.utils.beans.Search">
                                    <%=bean.getDescript()%><br>
                                </logic:iterate>
                            </logic:present>
                        </div>
                        <br />
                    </td>
                </tr>
                <tr>
                    <td class="titleLeft">
                        <%=rb.getString("scp.rqtaplicable")%>
                    </td>
                </tr>
                <tr>
                    <td colspan="3" class="td_gris_l infoTD">
                        <div class="clsMessageViewI" style="width: 100%">
                            <logic:present name="norms" scope="request">
                                <logic:iterate id="bean" name="norms" scope="request" type="com.desige.webDocuments.utils.beans.Search">
                                    <%=bean.getDescript()%><br>
                                </logic:iterate>
                            </logic:present>
                        </div>
                        <br />
                    </td>
                </tr>
                <tr>
                    <td class="titleLeft">
                        <%=rb.getString("scp.prcafectados")%>
                    </td>
                </tr>
                <tr>
                     <td colspan="3" class="td_gris_l infoTD">
                             <div class="clsMessageViewI" style="width: 100%;">
                                 <logic:present name="procesosSacop" scope="request">
                                   <logic:iterate id="bean" name="procesosSacop" scope="request" type="com.desige.webDocuments.utils.beans.Search">
                                      <%=bean.getDescript()%><br>
                                    </logic:iterate>
                                </logic:present>
                             </div>
                      </td>
                 </tr>

                <html:textarea property="noaceptada" style="display:none" />
           </logic:present>
                 <tr id="botoneraSacop">
                     <td></td>
                     <td colspan="3">
                       &nbsp;
                       <input type="hidden" name="rechazoapruebo" value="<%=rechazoapruebo%>">
                         <!--en caso que  venga desde la lista de check de valor seleccionadas en sacop relacionadas
                         ,el valor viene en 1 -->
                    <logic:present name="sacoprelacionada" scope="request">
                       <logic:notEqual  name="sacoprelacionada" scope="request" value="1">
                           <!-- si ya este sacop a sido rechazado, no muestra esto -->
                           <logic:present name="modificando" scope="session">

                             <logic:notEqual value="1" name="dataresponsable" property="rechazoapruebo">
                                 <input type="button" class="sacopButton" value="<%=rb.getString("scp.rechazado")%>" onClick="javascript:salvar(this.form,'1','<%=title%>');" name="btnzo" />
                                 &nbsp;
                                 <input type="button" class="sacopButton" value="<%=rb.getString("scp.aprobado")%>" onClick="javascript:salvar(this.form,'0','');" name="btnOK" />
                             </logic:notEqual>
                             <logic:equal value="1" name="dataresponsable" property="rechazoapruebo">
                                   <!--
                                   <input type="button" class="boton" value="<%=rb.getString("scp.aprobado")%>" disabled onClick="javascript:salvar(this.form,'0','');" name="btnOK" />
                                   -->
                              </logic:equal>

                               &nbsp;
                                 <input type="button" class="sacopButton" value="<%=rb.getString("btn.cancel")%>" onClick="javascript:cancelar(this.form);" name="btnCancel" />
                          </logic:present>
                          <logic:notPresent name="modificando" scope="session">
                                <input type="button" class="boton" value="<%=rb.getString("scp.aprobado")%>"  disabled onClick="javascript:salvar(this.form,'0','');" name="btnOK" />
                                 &nbsp;
                                 <input type="button" class="sacopButton" value="<%=rb.getString("btn.cancel")%>" onClick="javascript:cancelar(this.form);" name="btnCancel" />
                          </logic:notPresent>
                       </logic:notEqual>
                        <logic:equal  name="sacoprelacionada" scope="request" value="1">
                                 &nbsp;
                                 <input type="button" class="sacopButton" value="<%=rb.getString("btn.cancel")%>" onClick="javascript:cancelarsacoprelacionada(this.form);" name="btnCancel" />
                        </logic:equal>
                   </logic:present>
                     </td>
                     <td></td>
                </tr>
            </table>
        </html:form>
        </td>
    </tr>
</table>
</body>
</html>
