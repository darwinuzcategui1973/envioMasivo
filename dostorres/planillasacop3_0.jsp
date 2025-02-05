<!--  planillasacop3_0.jsp -->
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
    ResourceBundle rb = ToolsHTML.getBundle(request);
    StringBuffer onLoad = new StringBuffer("");
    //String info = (String) ToolsHTML.getAttribute(request,"info",true);
    String sacoprelacionada=request.getAttribute("sacoprelacionada")!=null?(String)request.getAttribute("sacoprelacionada"):"0";
    String info = (String) ToolsHTML.getAttributeSession(session,"error",true);
    String title="";
    String resu15 = (String) ToolsHTML.getAttribute(request,"info",true);
    int lineas=5000;
    
    // resu15= resu15!="0"?"1":"0";
     //resu15= resu15!="1"?"1":"0";
   
    boolean accionesHay=true;

   if (!ToolsHTML.isEmptyOrNull(info)) {
        onLoad.append(" onLoad=\"alert('").append(info).append("');");
    }


    if (onLoad.length()>0){
        onLoad.append("\"");
    }
    ToolsHTML.clearSession(session,"application.sacop");
    String styleTxt = "width: 300px; height: 19px";
    String numGo = request.getParameter("numGo");

    String mostrardetrestext=request.getAttribute("mostrardetrestext")!=null?(String)request.getAttribute("mostrardetrestext"):"0";
    int cuantosText=Integer.parseInt(mostrardetrestext);

    String fechaEmision=request.getAttribute("fechaEmision")!=null?(String)request.getAttribute("fechaEmision"):"";
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
<jsp:include page="meta.jsp" /> 
<script type="text/javascript" src="./estilo/funciones.js"></script>
<link href="estilo/sacop.css" rel="stylesheet" type="text/css">
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
    function salvar(forma) {
    	alert("funcion salvar en planillasacop3  AQIO ES BOTON OK darwinuzcategui")
    	forma.btnOK.disabled=true;
        forma.btnCancel.disabled=true;
        forma.submit();
        window.parent.parent.administrarCancelar();
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
        // forma.submit();
        //despues REALICE PRUEBAo 
        alert("cancelar plantillasacop3-----------------------jairo aqui le quite el forma.submit() y solucionado------ darwinuzcategui");
        //lo probe y no funciona
        //forma.visualizarTodos();
        //window.parent.parent.visualizarTodos();
        //visualizarTodo();
        //inicializar();
        //visualizar(ABC);
        window.parent.parent.administrarCancelar();
        //inicializar()
        //window.parent.parent.visualizarTodos()
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
   function nextp(forma){
      var i=parseInt(forma.mostrardetrestext.value);

      if (i<6){
         forma.btnatras.disabled=false;
         forma.action="loadSACOPMain.do?goTo=planilla30";
         forma.mostrardetrestext.value=parseInt(forma.mostrardetrestext.value) + 3;
         forma.submit();
      }else{
            forma.btnsig.disabled=true;
            forma.btnatras.disabled=false;
      }

   }
   function backp(forma){
      forma.action="loadSACOPMain.do?goTo=planilla30";
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
      function evaluaChequeo(forma,parametro){
                abrirVentanaAccionSacop('LoadAccionesTomarSacop.do?idplanillasacop='+parametro,1024,560);
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
            <html:form action="/planillasacop3.do">
                <input type='hidden' name="idplanillasacop3_1" value='<%=dataresponsable.getIdplanillasacop1()%>'>
                <input type='hidden' name="Idplanillasacop1" value='<%=dataresponsable.getIdplanillasacop1()%>'>
                <input type='hidden' name=goTo value="">
                <input type="hidden" name="mostrardetrestext" value='<%=cuantosText%>'>
                <input type="hidden" name="sacoprelacionada" value='<%=sacoprelacionada%>'>
                
                <%= ToolsHTML.getHTMLImgElementIfExists(request, "img/logos/empresa.gif") %>
                <table align=center border=0 width="100%">
                    <tr>
                        <td colspan="4">
                            <table class="clsTableTitle" width="100%" cellSpacing="0" cellPadding="2" align=center border=0>
                                <tbody>
                                    <tr>
                                        <td class="td_title_bc" width="*" height="18">
                                            
                                            
                                            <%title=rb.getString("scp.actionType".concat(dataresponsable.getCorrecpreven()));%>
                                    		<%=title%>&nbsp; <%=rb.getString("scp.num")%>:<bean:write name="data" property="sacopnum"/>
                                    	 
                                             
                                        </td>
                                    </tr>
                                </tbody>
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
                                            <%=dataresponsable.getCargo().indexOf("[")!=-1?dataresponsable.getCargo().replaceAll("\\[|\\]", "-").split("-")[1]:""%>
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
                                <div colspan="2">
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

                            <br />
                        </td>
                    </tr>
                    <logic:present name="existesacops_noconformidadesref" scope="session">
                         <logic:equal value="0" name="dataresponsable" property="correcpreven">
                             <tr>
                                <td class="titleLeft">
                                    <%=rb.getString("scp.sourceDocument")%>
                                </td>
                             </tr>
                         </logic:equal>
                         <logic:equal value="1" name="dataresponsable" property="correcpreven">
                            <tr>
                                <td class="titleLeft">
                                    <%=rb.getString("scp.noconformidades1")%>
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
                    <html:textarea property="descripcion" style="display:none" />
                    <tr>
                        <td class="titleLeft">
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
                    <html:textarea property="descripcion" style="display:none" />
                    <tr>
                        <td class="titleLeft">
                            <%=rb.getString("scp.desc")%>
                        </td>
                    </tr>
                    <tr>
                        <td colspan="3" align="left" class="td_gris_l infoTD">
                            <div align="left" class="clsMessageViewI" style="width: 100%; ">
                                <%=dataresponsable.getDescripcion()%>
                            </div>
                            <br />
                        </td>
                    </tr>
                    <tr>
                        <td class="titleLeft">
                            <logic:equal value="0" name="dataresponsable" property="correcpreven">
                                <%=rb.getString("scp.pblecausasnoconformidad0")%>
                            </logic:equal>
                            <logic:equal value="1" name="dataresponsable" property="correcpreven">
                                <%=rb.getString("scp.pblecausasnoconformidad")%>
                            </logic:equal>
                        </td>
                    </tr>
                    <tr>
                        <td colspan="3" align="left" class="td_gris_l infoTD">
                            <div align="left" class="clsMessageViewI" style="width: 100%; ">
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
                        <td class="titleLeft">
                            <%=rb.getString("scp.acrecomendadas")%>
                        </td>
                    </tr>
                    <tr>
                        <td colspan="3" align="left" class="td_gris_l infoTD">
                            <div align="left" class="clsMessageViewI" style="width: 100%; ">
                            <%= dataresponsable.getAccionrecomendada() %>
                            </div>
                            <br />
                        </td>
                    </tr>           
                    <tr>
                        <td class="titleLeft">
                            <logic:equal value="0" name="dataresponsable" property="correcpreven">
                                <%=rb.getString("scp.ac")%>&nbsp;<%=rb.getString("scp.informa_a")%>:
                            </logic:equal>
                            <logic:equal value="1" name="dataresponsable" property="correcpreven">
                                <%=rb.getString("scp.ap")%>&nbsp;<%=rb.getString("scp.informa_a")%>:
                            </logic:equal>
                        </td>
                    </tr>
                    <tr>
                        <td colspan="3" align="left" class="td_gris_l infoTD">
                            <div align="left" class="clsMessageViewI" style="width: 100%; ">
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
                        <td colspan="3" align="left" class="td_gris_l infoTD">
                            <div align="left" class="clsMessageViewI" style="width: 100%; ">
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
                        <td colspan="3" align="left" class="td_gris_l infoTD">
                            <div align="left" class="clsMessageViewI" style="width: 100%; ">
                                <logic:present name="procesosSacop" scope="request">
                                    <logic:iterate id="bean" name="procesosSacop" scope="request" type="com.desige.webDocuments.utils.beans.Search">
                                        <%=bean.getDescript()%><br>
                                    </logic:iterate>
                                </logic:present>
                            </div>
                            <br />
                        </td>
                    </tr>
            </logic:present>
                    <tr id="botoneraSacop">
                        <td colspan="3" align="center">
                            &nbsp;
                            <logic:present name="sacoprelacionada" scope="request">
                                <logic:notEqual  name="sacoprelacionada" scope="request" value="1">
                                    <logic:present name="modificando" scope="session">
                                        <input type="button" class="sacopButton" value="<%=rb.getString("scp.acctomar")%>" onClick="javascript:evaluaChequeo(this.form,<%=dataresponsable.getIdplanillasacop1()%>);" name="btnqueo" />
                                        &nbsp;
                                        <!-- si ya este sacop a sido rechazado, no muestra esto -->
                                        <input type="button" class="sacopButton" value="<%=rb.getString("scp.ejecutar")%>" onClick="javascript:salvar(this.form);" name="btnOK" />
                                        &nbsp;
                                        <input type="button" class="sacopButton" value="<%=rb.getString("btn.back")%>" onClick="javascript:cancelar(this.form);" name="btnCancel" />
                                    </logic:present>
                                    <logic:notPresent name="modificando" scope="session">
                                        <input type="button" class="boton" value="<%=rb.getString("scp.ejecutar")%>" disabled onClick="javascript:salvar(this.form);" name="btnOK" />
                                        &nbsp;
                                        <input type="button" class="sacopButton" value="<%=rb.getString("btn.back")%>" onClick="javascript:cancelar(this.form);" name="btnCancel" />
                                    </logic:notPresent>
                                </logic:notEqual>
                                <logic:equal  name="sacoprelacionada" scope="request" value="1">
                                    &nbsp;
                                    <input type="button" class="sacopButton" value="<%=rb.getString("btn.cancel")%>" onClick="javascript:cancelarsacoprelacionada(this.form);" name="btnCancel" />
                                </logic:equal>
                            </logic:present>
                        </td>
                    </tr>
                </table>
            </html:form>
        </td>
    </tr>
</table>
</body>
</html>
