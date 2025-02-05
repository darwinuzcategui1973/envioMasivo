<!--  planillasacop4.jsp -->
<!-- relacionadas: planillasacop2.jsp,planillasacop3_0.jsp,planillasacop4.jsp,planillasacop5.jsp,planillasacop6.jsp,planillasacop10.jsp --> 
<%@ page import="java.util.Locale,
                 java.util.ResourceBundle,
                 java.text.SimpleDateFormat,
                 java.util.Map,
                 com.focus.qweb.to.PossibleCauseTO,
                 com.focus.qweb.to.ActionRecommendedTO,
                 com.desige.webDocuments.utils.ToolsHTML"%>
<%@ page import="java.util.Calendar"%>

<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<%@ page language="java" %>
<%
    ToolsHTML.clearSession(session,"application.sacop");
    ResourceBundle rb = ToolsHTML.getBundle(request);
    StringBuffer onLoad = new StringBuffer("");
    String sacoprelacionada=request.getAttribute("sacoprelacionada")!=null?(String)request.getAttribute("sacoprelacionada"):"0";
    String info = (String) ToolsHTML.getAttribute(request,"info",true);
    String noHanFirmado= session.getAttribute("noHanFirmado")!=null?rb.getString("scp.nocerrarsacop"):"";

    info=noHanFirmado;
    if (!ToolsHTML.isEmptyOrNull(info)) {
        //onLoad.append(" onLoad=\"alert('").append(info).append("')\"");
        onLoad.append(" onLoad=\"alert('").append(info).append("')");
    }

   // if (ToolsHTML.checkValue(info)){
   //     onLoad.append(";alert('").append(info).append("')");
   // }
    if (onLoad.length()>0){
        onLoad.append("\";");
    }

    String styleTxt = "width: 300px; height: 19px";
    String numGo = request.getParameter("numGo");

    String mostrardetrestext=request.getAttribute("mostrardetrestext")!=null?(String)request.getAttribute("mostrardetrestext"):"0";
    String comentariosGenerales=request.getAttribute("comentariosGenerales")!=null?((String)request.getAttribute("comentariosGenerales")).replaceAll("\\n","").replaceAll("\\r","").replaceAll("'","\""):"";
    int cuantosText=Integer.parseInt(mostrardetrestext);
    String fechaEmision=request.getAttribute("fechaEmision")!=null?(String)request.getAttribute("fechaEmision"):"";
    if (ToolsHTML.isEmptyOrNull(numGo)) {
        numGo = "1";
    }
    String fechaEstimada=request.getAttribute("fechaEstimada")!=null?(String)request.getAttribute("fechaEstimada"):"";
    Calendar dateobj = Calendar.getInstance();
    int dia=dateobj.get(Calendar.DAY_OF_MONTH);
    int mes=dateobj.get(Calendar.MONTH);
    int anio=dateobj.get(Calendar.YEAR);
    String mesStr=String.valueOf((mes+1));
    mesStr=mesStr.length()==1?"0"+mesStr:mesStr;
    String diaStr=String.valueOf((dia));
    diaStr=diaStr.length()==1?"0"+diaStr:diaStr;

    String fechaHoy=String.valueOf(anio)+"-"+mesStr.toString()+"-" +diaStr.toString();
    String title="";

    PossibleCauseTO possible = null; 
    ActionRecommendedTO cause = null;
    Map<String,PossibleCauseTO> listOrderPossibleCause = (Map)request.getAttribute("ListOrderPossibleCause");

%>
<html>
<head>
<title><%=rb.getString("principal.title")%></title>
<jsp:include page="meta.jsp" />
<link href="estilo/sacop.css" rel="stylesheet" type="text/css">
<script type="text/javascript" src="./estilo/funciones.js"></script>
<link href="estilo/sacop.css" rel="stylesheet" type="text/css">
<script type="text/javascript" src="./estilo/popcalendar.js"></script>
<script type="text/javascript" src="./estilo/fechas.js"></script>
<script type="text/javascript" src="./estilo/funciones.js"></script>

<script type="text/javascript">
function loadSacops(number,input,nameForm,value,type) {
    abrirVentana("Sacop_Relacinadas.do?number="+number+"&input="+input+"&nameForma="+nameForm+"&value="+value+"&type="+type+"&userAssociate="+document.sacoplantilla.sacop_relacionadas.value,600,400)
}

function getDateExpire(dateField,nameForm,dateValue,text){
    window.open("getDate.jsp?field=" + dateField + "&nameForm="+nameForm+"&valueForm="+dateValue+"&text="+'scp.fecha',"","width=450,height=250,resizable=no,scrollbars=yes,left=300,top=250");
}

function showCharge(userName,charge) {
    charge = charge.replace(/\[/gi,"-").replace(/\]/gi,"-");
    window.open("showCharge.jsp?userName="+userName+"&charge="+charge, "WebDocuments", "resizable=no,scrollbars=yes,statusbar=yes,width=300,height=200,left=410,top=250");
}

function validar(forma){
    if (!validarFecha(<%out.print("forma.fechaReal.value");%>)){
    	alert('<%=rb.getString("scp.fecharealvacio")%>');
        <%--  <%out.print("forma.fechaReal.focus();");%>--%>
        return false;
    }
    if (forma.fechaReal.value>'<%=fechaHoy%>'){
        alert('<%=rb.getString("scp.fecharealnomaniordhoy")%>');
        return false;
    }
    if (forma.fechaReal.value<forma.fechaEmision.value){
        alert('<%=rb.getString("scp.fecharealmenoremitida")%>');
        return false;
    }

    return true;
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

    function salvar(forma,comentario,title) {
        var row = 0;
         if (comentario=='1'){
           if (Trim(forma.comentariosGenerales.value)==""){
              alert('<%=rb.getString("scp.val11")%>');
              evaluaComentario(forma.comentariosGenerales,forma,'<%=rb.getString("scp.comm")%>');
              return false;
           }
        }
        if (validar(forma)){
            //preguntamos si desea modificar el comentario
            if (confirm("<%=rb.getString("scp.val12")%>")) {
                evaluaComentario(forma.comentariosGenerales,forma,'<%=rb.getString("scp.comm")%>');
                return false;
            }else{
                forma.btnOK.disabled=true;
                forma.btnCancel.disabled=true;
                //si hay inf es porque anteriormente no pudo cerrarse la sacop porque las acciones
                //no estaban completamente firmadas y te recuerda que si no esta firmada , no se podra completar.
                alert('<%=rb.getString("scp.nocerrarsacop1")%>');
                forma.submit();
                window.parent.parent.administrarCancelar();
            }
        }
    }
    function evaluaComentario(comentario,forma,title){
       // if (rechazo.checked){
            showWindow('SacopMiniText.do','comentariosGenerales','sacoplantilla',forma.comentariosGenerales.value,title,800,500,'0');
      //  }
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
        forma.submit();
        window.parent.parent.administrarCancelar();
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
  function evaluaChequeo(forma,parametro){
                abrirVentanaAccionSacop('LoadAccionesTomarSacop.do?idplanillasacop='+parametro+'&userponecomentario=1',690,450);
      }
   function showCharge(userName,charge) {
	    charge = charge.replace(/\[/gi,"-").replace(/\]/gi,"-");
        window.open("showCharge.jsp?userName="+userName+"&charge="+charge, "WebDocuments", "resizable=no,scrollbars=yes,statusbar=yes,width=300,height=200,left=410,top=250");
    }

 function nextp(forma){
      var i=parseInt(forma.mostrardetrestext.value);

      if (i<6){
         forma.btnatras.disabled=false;
         forma.action="loadSACOPMain.do?goTo=planilla4";
         forma.mostrardetrestext.value=parseInt(forma.mostrardetrestext.value) + 3;
         forma.submit();
      }else{
            forma.btnsig.disabled=true;
            forma.btnatras.disabled=false;
      }
   }
function backp(forma){
      forma.action="loadSACOPMain.do?goTo=planilla4";
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

function refrescarPaginaPadre() {
	document.location.href=document.location.href+"&toBottom=true";
}
</script>
</head>
<body class="bodyInternas" <%=onLoad.toString()%>>

	<bean:define id="dataresponsable" name="usuarioResponsable"
		type="com.desige.webDocuments.sacop.forms.plantilla1" scope="request" />
	<bean:define id="data" name="sacoplantilla" type="com.desige.webDocuments.sacop.forms.plantilla1" scope="request" />
<table cellSpacing=0 cellPadding=2 align=center border=0 width="100%">
    <tr>
        <td width="86%" valign="top">
            <html:form action="/planillasacop4.do">
                <html:hidden property="idplanillasacop1" value='<%=dataresponsable.getIdplanillasacop1()%>'/>
                <input type='hidden' name=dataresponsable value="">
                <input type='hidden' name=goTo value="">
                <input type="hidden" name="mostrardetrestext" value='<%=cuantosText%>'>
                <input type="hidden" name="sacoprelacionada" value='<%=sacoprelacionada%>'>
                
                <%= ToolsHTML.getHTMLImgElementIfExists(request, "img/logos/empresa.gif") %>
                <table align="center" border="0" width="100%">
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
                                    <b><%=rb.getString("scp.orgsacop")%>:</b>
                                    <br/>
                                    <div class="alignRight">
                                        <%=dataresponsable.getOrigensacoptxt()!=null?dataresponsable.getOrigensacoptxt():""%>
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
                                        <%=fechaEmision!=null?fechaEmision:""%>
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
                                <td width="30%" class="td_gris_l">
                                        <b><%= rb.getString("scp.receivingArea") %>:</b>
                                        <br/>
                                        <div class="alignRight">
                                            <%=dataresponsable.getCargo().indexOf("[")!=-1?dataresponsable.getCargo().replaceAll("\\[|\\]", "-").split("-")[1]:""%>
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
                                        <%=fechaEstimada%>
                                    </div>
                                    <br/>
                                </td>
                                <%
                                if(dataresponsable.getFechaWhenDiscovered() != null){
                                %>
                                    <td width="30%" class="td_gris_l">
	                                    <b><%= rb.getString("scp.fechaWhenDiscovered") %>:</b>
	                                    <br/>
	                                    <div class="alignRight">
	                                        <html:hidden property="fechaWhenDiscovered" value="<%=dataresponsable.getFechaWhenDiscovered()%>" />
	                                        <%=dataresponsable.getFechaWhenDiscovered()%>
	                                    </div>
	                                    <br/>
                                    </td>
                                <%
                                } else {
                                %>
                                    <td>
                                        <html:hidden property="fechaWhenDiscovered" value="" />
                                    </td>
                                <%
                                }
                                %>
                                <td>
                                    <logic:present name="modificando" scope="session">
                                        <b><%= rb.getString("scp.fechareal") %>:</b>
                                        <br />
                                        <div class="alignRight">
                                            <html:text property="fechaReal"  readonly="true" value='<%=dataresponsable.getFechaReal()!=null ? dataresponsable.getFechaReal() : ToolsHTML.date.format(Calendar.getInstance().getTime())%>' size="10"  styleClass="classText"/>
                                            <input type="button" onclick='getDateExpire("fechaReal",this.form.name,this.form.fechaReal.value,"scp.fecha")' value='>>' class="sacopButton" />
                                            <%--<input type=button onclick='this.form.fechaReal.value=""; this.form.initialDay.value="";  this.form.initialMonth.value="";  this.form.initialYear.value=""; ' value='<%=rb.getString("btn.clear")%>' class="boton">--%>
                                        </div>
                                    </logic:present>
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
                                <logic:present name="sacopfile" scope="request">
                                <td>
                                    <b><%= rb.getString("doc.link") %>:</b>
                                    <br />
                                    <div class="alignRight">
                                        <a href="javascript:showFileSacop('<%=dataresponsable.getIdplanillasacop1()%>')" class="ahref_b">
                                            <%= dataresponsable.getNameRelatedDocument() %>
                                            <img src="img/pendings.gif" width="16" height="16" border=0>
                                        </a>
                                    </div>
                                    <br />
                                </td>
                                </logic:present>
                                <logic:notPresent name="sacopfile" scope="request">
                                <td>
                                </td>
                                </logic:notPresent>
                                <td>
					         		<b><%=rb.getString("scp.applicant")%></b>
					         		<br>
                                       <div class="alignRight">
			                             <a href="javascript:showCharge('<%=data.getSolicitantetxt()%>','<%=data.getCargoSolicitante()%>')" class="ahref_b">
			                             <logic:present name="showCharge" scope="session">
			                                   <%=data.getCargoSolicitante()%>
			                             </logic:present>
			                             <logic:notPresent name="showCharge" scope="session">
			                                  <%=data.getSolicitantetxt()%>
			                             </logic:notPresent>
			                             </a>
			                        </div>
                                </td>
                            </tr>
                            </table>
                            <br>
                                <logic:present name="existesacops_relacionadas" scope="session">
                                <div>
                                    <b><%= rb.getString("scp.sacoprelacionados") %>:</b>
                                    <br />
                                    <div >
                                        <input type="hidden" name="exec" value=""/>
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
                                    <br />
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
	                            <%=dataresponsable.getNoconformidades()%>
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
                           <div align="left" class="clsMessageViewI" style="width: 100%">
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
                    <tr>
                        <td  class="titleLeft">
                            <%=rb.getString("scp.acrecomendadas")%>
                        </td>
                    </tr>
                    <tr>
                       <td colspan="3" align="left" class="td_gris_l infoTD">
                           <div align="left" class="clsMessageViewI" style="width: 100%">
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
                                <logic:present name="usuarios1" scope="request">
                                    <logic:iterate id="bean" name="usuarios1" scope="request" type="com.desige.webDocuments.utils.beans.Search">
                                        <%=bean.getDescript()%><br>
                                    </logic:iterate>
                                </logic:present>
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
                        <td  class="titleLeft">
                            <%=rb.getString("scp.prcafectados")%>
                        </td>
                    </tr>
                    <tr>
                        <td colspan="3" align="left" class="td_gris_l infoTD">
                            <div align="left" class="clsMessageViewI" style="width: 100%">
                                <logic:present name="procesosSacop" scope="request">
                                    <logic:iterate id="bean" name="procesosSacop" scope="request" type="com.desige.webDocuments.utils.beans.Search">
                                        <%=bean.getDescript()%><br>
                                    </logic:iterate>
                                </logic:present>
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
                            <html:textarea property="accionobservacion" style="display:none" />
                            <div align="left" class="clsMessageViewI" style="width: 100%">
                                <%=dataresponsable.getAccionobservacion()%>
                                <%
                                if(! ToolsHTML.isEmptyOrNull(dataresponsable.getArchivoTecnica().getFileName())){
                                %>
                                    <br />
                                    <br />
                                    <b><%=rb.getString("scp.tecnica")%>:</b>
                                    <a href="javascript:showFileSacop('<%=dataresponsable.getIdplanillasacop1() + "/" + dataresponsable.getArchivoTecnica().getFileName() %>');">
                                        <%= dataresponsable.getArchivoTecnica().getFileName() %>
                                        &nbsp;&nbsp;
                                        <img src="img/pendings.gif" width="16" height="16" border="0"></a>
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
                            <%=rb.getString("scp.accion")%>
                        </td>
                    </tr>
                    <tr>
                        <td colspan="3" align="left" class="td_gris_l infoTD">
                            <html:textarea property="accionobservacion" style="display:none" />
                            <div align="left" class="clsMessageViewI" style="width: 100%">
                                <%=dataresponsable.getAccionobservaciontxt().replaceAll(rb.getString("scp.accion").concat(":"),
                                		"<b>".concat(rb.getString("scp.accion")).concat(":").concat("</b>")) %>
                            </div>
                            <br />
                        </td>
                    </tr>
                </logic:present>
                <tr id="botoneraSacop">
                    <td></td>
                    <td colspan="3">
                        &nbsp;
                        <logic:present name="sacoprelacionada" scope="request">
                            <logic:notEqual  name="sacoprelacionada" scope="request" value="1">
                                <logic:present name="tieneAccionesPendientes" scope="request">
                                    <input type="button" class="boton sacopButton" value="<%=rb.getString("scp.accpndt")%>" onClick="javascript:evaluaChequeo(this.form,<%=dataresponsable.getIdplanillasacop1()%>);" name="btnpend" />
                                </logic:present>
                                &nbsp;
                                <logic:present name="modificando" scope="session">
                                    <logic:notEqual value="1" name="dataresponsable" property="rechazoapruebo">
                                        <input type="hidden" name="comentariosGenerales"  value='<%=comentariosGenerales%>' />
                                        <input type="button" class="boton sacopButton" value="<%=rb.getString("scp.completarsacop")%>" onClick="javascript:salvar(this.form,'1','<%=title%>');" name="btnOK" />
                                        &nbsp;
                                        <input type="button" class="boton sacopButton" value="<%=rb.getString("btn.cancel")%>" onClick="javascript:cancelar(this.form);" name="btnCancel" />
                                    </logic:notEqual>
                                </logic:present>
                                <logic:notPresent name="modificando" scope="session">
                                    <input type="button" class="boton" value="<%=rb.getString("scp.completarsacop")%>" disabled onClick="javascript:salvar(this.form,'1','<%=title%>');" name="btnOK" />
                                    &nbsp;
                                    <input type="button" class="boton sacopButton" value="<%=rb.getString("btn.cancel")%>" onClick="javascript:cancelar(this.form);" name="btnCancel" />
                                </logic:notPresent>
                            </logic:notEqual>
                            <logic:equal  name="sacoprelacionada" scope="request" value="1">
                                &nbsp;
                                <input type="button" class="boton sacopButton" value="<%=rb.getString("btn.cancel")%>" onClick="javascript:cancelarsacoprelacionada(this.form);" name="btnCancel" />
                            </logic:equal>
                        </logic:present>
                    </td>
                </tr>
            </table>
        </html:form>
            <script type="text/javascript" event="onload" for="window">
                /*document.sacoplantilla.richdescripcion.docHtml = '< %=dataresponsable.getDescripcion()%>'
                document.sacoplantilla.richcausasnoconformidad.docHtml ='< %=dataresponsable.getCausasnoconformidad()%>'
                document.sacoplantilla.richaccionrecomendada.docHtml ='< %=dataresponsable.getAccionrecomendada()%>'
                document.sacoplantilla.richaccionobservacion.docHtml = '< %=dataresponsable.getAccionobservacion()%>'
                document.sacoplantilla.richacciones.docHtml ='< %=dataresponsable.getAccionobservaciontxt()%>'*/
            </script>
        </td>
    </tr>
</table>
</body>
</html>
<%if(request.getParameter("toBottom")!=null){%>
	<script>window.scrollTo(0,document.body.scrollHeight);</script>
<%}%>