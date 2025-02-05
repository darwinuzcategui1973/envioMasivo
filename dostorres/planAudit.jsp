<%@page import="java.util.Iterator"%>
<%@ page import="java.util.ResourceBundle,
                 com.desige.webDocuments.utils.ToolsHTML,
                 com.desige.webDocuments.utils.beans.SuperActionForm,
                 com.desige.webDocuments.utils.beans.Users,
                 com.desige.webDocuments.utils.beans.PaginPage,
                 com.desige.webDocuments.enlaces.forms.UrlsActionForm,
                 com.desige.webDocuments.utils.beans.Search,
                 java.util.Comparator,
                 java.util.Collection,
                 java.util.Date"%>

<!--/**
 * Title: planAudit.jsp<br>
 * Copyright: (c) 2005 Desige Servicios de Computación<br/>
 *
 * <br>
 *     Changes:<br>
 * <ul>
 *      <li> 
 * <ul>
 */-->
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<%@ page language="java" %>
<%
	Users usuario = (Users) request.getSession().getAttribute("user");

    ResourceBundle rb = ToolsHTML.getBundle(request);
    StringBuffer onLoad = new StringBuffer("");
    String info = (String)request.getAttribute("info");
    //System.out.println("info = " + info);
    String loadForm = (String)session.getAttribute("loadManagerPlan");
    if (ToolsHTML.isEmptyOrNull(loadForm)) {
        loadForm = "";
    }
    if (!ToolsHTML.isEmptyOrNull(info)) {
        onLoad.append(" onLoad=\"alert('").append(info).append("')\"");
    }
    String err = (String)session.getAttribute("error");
    if (!ToolsHTML.isEmptyOrNull(err)) {
        onLoad.append(" onLoad=\"alert('").append(err).append("')\"");
        session.removeAttribute("error");
    }

    boolean isInput = true;
    String inputReturn = (String)session.getAttribute("input");
    if (inputReturn==null){
        inputReturn = "";
        isInput = false;
    }
    String valueReturn = (String)session.getAttribute("value");
    if (valueReturn==null){
        valueReturn = "";
    }
    String input = (String)ToolsHTML.getAttribute(request,"input");
    String value = (String)ToolsHTML.getAttribute(request,"value");

    String nameForma = ToolsHTML.getAttribute(request,"nameForma")!=null?(String)ToolsHTML.getAttribute(request,"nameForma"):"";


    String cmd = (String)request.getAttribute("cmd");

    if (cmd==null){
        cmd = SuperActionForm.cmdLoad;
    }

    pageContext.setAttribute("cmd",cmd);
    
    int idNormAudit = ((Integer)session.getAttribute("idNormAudit")).intValue();
    Collection<Search> listaNormas = (Collection)session.getAttribute("normasPadrePlan");
    Collection<Search> listaNormasDetalle = (Collection)session.getAttribute("normasPadrePlanDetalle");
    Collection listaUsuarios = (Collection)session.getAttribute("usuariosPlan");
    Search search = null;
%>
<html>
<head>
<title><%=rb.getString("principal.title")%></title>
<jsp:include page="meta.jsp" /> 
<link href="estilo/estilo.css" rel="stylesheet" type="text/css">
<script type="text/javascript" src="./estilo/popcalendar2.js?sdf"></script>
<script type="text/javascript" src="./estilo/funciones.js"></script>
<style>
html,body,form {
	margin:0px;
	padding:0px;
	background-color:#efefef;
}
</style>
<script type="text/javascript">
function showNorms(idNorms) {
    var idNorms = escape(idNorms);
    abrirVentana("viewDocumentNorms.jsp?idNorms=" + idNorms ,800,600);
}
  function botones(forma) {
        if (forma.btnDelete) {
            forma.btnDelete.disabled = true;
        }
        if (forma.btnIncluir) {
            forma.btnIncluir.disabled = true;
        }
        if (forma.btnSalvar) {
            forma.btnSalvar.disabled = true;
        }
        if (forma.btnCancelar) {
            forma.btnCancelar.disabled = true;
        }
    }
    function setValue(id,desc){
        <%=inputReturn%>
        <%=valueReturn%>
        window.close();
    }

    function cancelar(form){
        form.action = "<%=loadForm%>";
        form.cmd.value="<%=SuperActionForm.cmdLoad%>";
        form.submit();
    }

    function validateRadio() {
    	var formaSelection = document.getElementById("selection");
	    for (var i=1; i < formaSelection.length;i++) {
            if (formaSelection.elements[i].checked){
                msg = formaSelection.elements[i].value;
                p = msg.indexOf(",");
                formaSelection.id.value = msg.substring(0,p);
                formaSelection.desc.value = msg.substring(p+1,msg.length);
                return true;
            }
        }
	    
        return false;
    }
    function edit(num){
        var formEdit='<%=(String)session.getAttribute("formEditPlan")%>';
        forma = document.<%=(String)session.getAttribute("formEditPlan")%>;
        forma.idPlanAudit.value=num;
        forma.id.value = num;
        forma.cmd.value = '<%=SuperActionForm.cmdEdit%>';
        forma.submit();
    }
    function salvar(form){
        if (validar(form)){
            botones(form);
            form.submit();
        } else {
            alert("<%=rb.getString("err.notValueInField")%>");
        }
    }
    
	String.prototype.trim = function() { return this.replace(/^\s*|\s*$/g,""); };

    function validar(forma){
    	forma.namePlan.value = forma.namePlan.value.trim();
        if (forma.namePlan.value.length==0) {
        	alert('<%=rb.getString("admin.planAudit.alert.namePlanEdit")%>');
            return false;
        }
        if (forma.dateFromPlan.value.length==0) {
        	alert('<%=rb.getString("admin.planAudit.alert.dateFrom")%>');
            return false;
        }
        if (forma.dateUntilPlan.value.length==0) {
        	alert('<%=rb.getString("admin.planAudit.alert.dateUntil")%>');
            return false;
        }
        
        if (forma.dateFromPlan.value.replace(/-/gi,'')>forma.dateUntilPlan.value.replace(/-/gi,'') ) {
        	alert('<%=rb.getString("admin.planAudit.alert.dateNotLess")%>');
            return false;
        }

        if (forma.dateFromPlan.value.replace(/-/gi,'')<forma.dateFrom.value.replace(/-/gi,'') ) {
        	alert('<%=rb.getString("admin.planAudit.alert.dateNotLessProgram")%>');
            return false;
        }
        if (forma.dateUntilPlan.value.replace(/-/gi,'')>forma.dateUntil.value.replace(/-/gi,'') ) {
        	alert('<%=rb.getString("admin.planAudit.alert.dateNotLessProgram")%>');
            return false;
        }

        
        var listNormCheck = document.getElementById('listNormCheck').getElementsByTagName('input');
    	var sep = '';
    	forma.idNormPlanCheck.value = '';
    	for(var i=0; i<listNormCheck.length; i++) {
    		if(listNormCheck[i].checked) {
	    		forma.idNormPlanCheck.value +=  sep + listNormCheck[i].value;
	    		sep = ',';
	    	}
    	}
    	//if(forma.idNormPlanCheck.value=='') {
        	//alert('<%=rb.getString("admin.planAudit.alert.selectNorm")%>');
    		//return false;
    	//}
        
        return true;
    }

    function incluir(form) {
        form.cmd.value = "<%=SuperActionForm.cmdLoad%>";
        form.submit();
    }
    function guardar(form) {

		if(validar(form)) {    	
	        form.cmd.value = "<%=SuperActionForm.cmdInsert%>";
	        form.submit();
        }
    }
    
    function Buscar(form){
        form.submit();
    }
    function check() {
    	var formaSelection = document.getElementById("selection");
        if (!validateRadio()){
            alert('<%=rb.getString("error.selectValue")%>');
            return false;
        } else {
            setValue(formaSelection.id.value,formaSelection.desc.value);
        }
     }

     function paging_OnClick(pageFrom) {
        document.formPagingPage.from.value = pageFrom;
        document.formPagingPage.submit();
    }
    function youAreSure(form){
      if (confirm("<%=rb.getString("areYouSure")%>")) {
         form.cmd.value = "<%=SuperActionForm.cmdDelete%>";
         //alert("form.action="+form.action);
         form.submit();
      }
    }
    function cerrar(form){
        if (confirm("<%=rb.getString("areYouSure")%>")) {
           form.cmd.value = "<%=SuperActionForm.cmdClosed%>";
           //alert("form.action="+form.action);
           form.submit();
        }
      }


     function updateCheck(check,field) {
        if (check.checked){
            field.value = "7";
        } else{
            field.value = 0;
        }
    }

	function planes(forma) {
		abrirVentana('loadPlanAudit.do',900,500)
	}
	
	function seleccionCompleta(obj,limpiar) {
		var obj = document.getElementById("idNorm");
		var n = obj.selectedIndex;
		var arr = obj.options[n].text.split("/");
		var sistemaGestion = arr[1];
		var clase = document.getElementsByClassName(arr[1]);
		
		var cambio = false;
		
		var chk = document.getElementsByClassName("CHK-"+sistemaGestion.replace(/^\s+|\s+$/gm,'')); // trim()
		for(var i=0; i<chk.length; i++) {
			if(!chk[i].checked) {
				chk[i].checked = true;
				cambio = true;
			}
		}
		if(!cambio) {
			for(var i=0; i<chk.length; i++) {
				if(chk[i].checked) {
					chk[i].checked = false;
				}
			}
		}
	}
	
	function mostrarNorma(obj,limpiar) {
		console.log("mostrarNorma...");
		var n = obj.selectedIndex;
		console.log(obj.options[n].text);
		var arr = obj.options[n].text.split("/");
		var sistemaGestion = arr[1];
		var clase = document.getElementsByClassName(arr[1]);
		var clase2 = "";
		console.log("classname="+arr[1]);
		for(var i=0; i<obj.options.length;i++) {
			arr = obj.options[i].text.split("/");
			divs = document.getElementsByClassName(arr[1]);
			for(var x=0; x < divs.length; x++){
				divs[x].style.display="none";
			}
		}
		
		divs = document.getElementsByClassName(sistemaGestion);
		for(var x=0; x < divs.length; x++){
			divs[x].style.display="";
		}
		
		if(limpiar) {
			var chk = document.getElementsByTagName("input");
			for(var i=0; i<chk.length; i++) {
				if(chk[i].checked) {
					alert("<%=rb.getString("admin.planAudit.alert.unchecked")%>");
					break;
				}
			}

			for(var i=0; i<chk.length; i++) {
				if(chk[i].checked) {
					chk[i].checked = false;
				}
			}
		}
		
		
	}
</script>
</head>

<body class="bodyInternas" <%=onLoad.toString()%>>
<table cellSpacing=0 cellPadding=2 align=center border=0 width="100%">
    <logic:present name="dataTablePlan" scope="session">
        <logic:equal name="cmd" value="<%=SuperActionForm.cmdLoad%>">
        <!-- Paginación -->
        <%
            String from = request.getParameter("from");
            String size = (String)session.getAttribute("sizeParamPlan");
            Users users = (Users)session.getAttribute("user");
            PaginPage Pagingbean = ToolsHTML.getBeanPaginPage(from,Integer.parseInt(size),String.valueOf(users.getNumRecord()));
        %>
        <html:form action='<%=(String)session.getAttribute("loadEditManagerPlan")%>'> <!-- FORMA-5 -->
            <html:hidden property="idPlanAudit" />
            <html:hidden property="idProgramAudit" />
            <html:hidden property="id" value="" />
            <html:hidden property="cmd" value="<%=SuperActionForm.cmdEdit%>"/>
            <% if (isInput) { %>
                <html:hidden property="input" value="<%=input%>"/>
                <html:hidden property="value" value="<%=value%>"/>
                <html:hidden property="nameForma" value="<%=nameForma%>"/>
            <% } %>
        </html:form>

        <form name="selection" id="selection" action=""> <!-- FORMA-1 -->
            <input type="hidden" name="id"/>
            <input type="hidden" name="desc"/>

            <% if (isInput) { %>
                <html:hidden property="input" value="<%=input%>"/>
                <html:hidden property="value" value="<%=value%>"/>
                <html:hidden property="nameForma" value="<%=nameForma%>"/>
            <%}%>
            <tr>
                <td>
                    <table cellSpacing="3" cellPadding="3" align=center border="0" width="100%">
                        <tr>
                            <td width="40%" class="td_titulo_C">
								<%=rb.getString("admin.planAudit.descript")%>
                            </td>
                            <td width="10%" class="td_titulo_C">
								<%=rb.getString("admin.planAudit.peridoDD")%>
                            </td>
                            <td width="10%" class="td_titulo_C">
								<%=rb.getString("admin.planAudit.periodoHH")%>
                            </td>
                            <td width="30%" class="td_titulo_C">
								<%=rb.getString("admin.planAudit.normAudit")%>
                            </td>
                            <td width="10%" class="td_titulo_C">
								<%=rb.getString("admin.planAudit.status")%>
                            </td>
                        </tr>
                        <logic:iterate id="bean" name="dataTablePlan" indexId="ind" offset="<%=Pagingbean.getDesde()%>" length="<%=Pagingbean.getCuantos()%>"
                        type="com.desige.webDocuments.utils.beans.Search" scope="session">
                            <%
                                int item = ind.intValue()+1;
                                int num = item%2;
                            %>
                            <tr>
                                <td class='td_<%=num%>' valign="top">
                                        <% if (isInput) { %>
                                            <input name="id" type="radio" value="<%=bean.getId()%>,<%=bean.getDescript()%>"/>
                                        <%}%>
                                        <a href="javascript:edit('<%=bean.getId()%>')" class="ahref_b">
                                          <%=ToolsHTML.cortarCadena(bean.getDescript()!=null?bean.getDescript():"")%>
                                        </a>
                                </td>
                                <td class='td_<%=num%>' valign="top">
		                                <%=bean.getField1()!=null?bean.getField1():""%>
                                </td>
                                <td class='td_<%=num%>' valign="top">
		                                <%=bean.getField1()!=null?bean.getField2():""%>
                                </td>
                                <td class='td_<%=num%>' valign="top">
		                                <%=bean.getField1()!=null?bean.getField3():""%>
                                </td>
                                <td class='td_<%=num%>' valign="top">
		                                <%=bean.getField1()!=null?rb.getString("admin.planAudit.status".concat(bean.getField4())):""%>
                                </td>
                            </tr>
                        </logic:iterate>
                   </table>
                </td>
            </tr>
        </form>
        <tr>
            <td>
                <center>
                    <html:form action='<%=(String)session.getAttribute("newManagerPlan")%>'> <!-- FORMA-2 -->
                        <html:hidden property="cmd" value="<%=SuperActionForm.cmdNew%>"/>

                        <html:hidden property="idPlanAudit" value=""/>
                        <html:hidden property="namePlan" value=""/>
                        <input type="button" class="boton" value="<%=rb.getString("btn.incluir")%>" onClick="javascript:incluir(this.form);" />
                  </html:form>
                </center>
            </td>
        </tr>
        <tr>
            <td>
                &nbsp;
            </td>
        </tr>
        <%
            if (size.compareTo("0") > 0){
        %>
                <tr>
                    <td align="center">
                        <!-- Inicio de Paginación -->
                        <table class="paginador">
                          <tr>
                            <td align="center" colspan="4">
                              <br>
                              <font size="1" color="#000000">
                                <%=rb.getString("pagin.title")+ " "%>
                                <%=Integer.parseInt(Pagingbean.getPages())+1%>
                                <%=rb.getString("pagin.of")%>
                                <%=Pagingbean.getNumPages()%>
                              </font>
                            </td>
                          </tr>
                          <tr>
                              <td align="center"> <img src="img/First.gif" width="24" height="24"
                                   class="GraphicButton" title="<%=rb.getString("pagin.First")%>"
                                   onclick="paging_OnClick(0)">
                              </td>
                              <td align="center"> <img src="img/left.gif" width="24" height="24"
                                   class="GraphicButton" title="<%=rb.getString("pagin.Previous")%>"
                                   onclick="paging_OnClick(<%=(Integer.parseInt(Pagingbean.getPages())-1)%>)">
                              </td>
                              <td align="center"> <img src="img/right.gif" width="24" height="24"
                                   class="GraphicButton" title="<%=rb.getString("pagin.next")%>"
                                   onclick="paging_OnClick(<%=(Integer.parseInt(Pagingbean.getPages())+1)%>)">
                              </td>
                              <td align="center"> <img src="img/End.gif" width="24" height="24"
                                   class="GraphicButton" title="<%=rb.getString("pagin.Last")%>"
                                   onclick="paging_OnClick(<%=Pagingbean.getNumPages()%>)">
                              </td>
                          </tr>
                        </table>

                        <form name="formPagingPage" method="post" action="planAudit.jsp">
                            <input type="hidden" name="from"  value="">
                            <input type="hidden" name="cmd"  value="<%=SuperActionForm.cmdLoad%>">
                            <% if (isInput) { %>
                                <html:hidden property="input" value="<%=input%>"/>
                                <html:hidden property="value" value="<%=value%>"/>
                                <html:hidden property="nameForma" value="<%=nameForma%>"/>
                            <%}%>
                        </form>
                        <!-- Fin de Paginación -->
                    </td>
                </tr>
            <% } %>

        </logic:equal>
    </logic:present>
            <logic:equal name="cmd" value="<%=SuperActionForm.cmdLoad%>">
            <tr>
                <td align="center">
                    <html:form action='<%=(String)session.getAttribute("newManagerPlan")%>'> <!-- FORMA-3 -->
                        <html:hidden property="cmd" value="<%=SuperActionForm.cmdNew%>"/>
                        <html:hidden property="idPlanAudit" value=""/>
                        <html:hidden property="namePlan" value=""/>
                        <% if (isInput) { %>
                            <html:hidden property="input" value="<%=input%>"/>
                            <html:hidden property="value" value="<%=value%>"/>
                            <html:hidden property="nameForma" value="<%=nameForma%>"/>
                        <%}%>
                        <logic:notPresent name="dataTablePlan" scope="session">
                            <logic:notEqual value="<%=SuperActionForm.cmdInsert%>" name="cmd" >
                                <table width="100%" cellSpacing=0 cellPadding=0 align=center border=0>
                                    <tr>
                                        <td>
                                            <input type="button" class="boton" value="<%=rb.getString("btn.incluir")%>" onClick="javascript:incluir(this.form);" />
                                        </td>
                                    </tr>
                                </table>
                            </logic:notEqual>
                        </logic:notPresent>
                    </html:form>
                </td>
            </tr>
	        </table>
            </logic:equal>


        <logic:notEqual name="cmd" value="<%=SuperActionForm.cmdLoad%>">
    <%

        String forma = (String)session.getAttribute("editManagerPlan");
        if (UrlsActionForm.cmdInsert.equalsIgnoreCase(cmd)){
            forma = (String)session.getAttribute("newManagerPlan");
        }
    %>

    <tr>
        <td>
        	<bean:define id="frmPlanAudit1" name="frmPlanAudit1" type="com.desige.webDocuments.accion.forms.PlanAudit" />
        	<bean:define id="frmProgramAudit1" name="frmProgramAudit1" type="com.desige.webDocuments.accion.forms.ProgramAudit" />
        	<% boolean isPlanificar = frmPlanAudit1==null || frmPlanAudit1.getStatusPlan()==null || frmPlanAudit1.getStatusPlan().equals("") || frmPlanAudit1.getStatusPlan().equals("B");%>
        	
        	<logic:equal name="cmd" value="<%=SuperActionForm.cmdInsert%>">
        		<%frmPlanAudit1.cleanForm();%>
        	</logic:equal>
            <html:form action="<%=forma%>" enctype='multipart/form-data' > <!-- FORMA-4 -->
                <html:hidden property="cmd" value='<%=cmd%>'/>
                <html:hidden name="frmPlanAudit1" property="id"/>
                <html:hidden name="frmProgramAudit1" property="idProgramAudit"/>
                <html:hidden name="frmProgramAudit1" property="dateFrom"/>
                <html:hidden name="frmProgramAudit1" property="dateUntil"/>
                <html:hidden name="frmPlanAudit1" property="idPersonPlan" value="<%=String.valueOf(frmPlanAudit1.getIdPersonPlan()>0?frmPlanAudit1.getIdPersonPlan():usuario.getIdPerson())%>" />
                
                <% if (isInput) { %>
                    <html:hidden property="input" value="<%=input%>"/>
                    <html:hidden property="value" value="<%=value%>"/>
                    <html:hidden property="nameForma" value="<%=nameForma%>"/>
                <%}%>
                <!--Se agrega una norma nueva -->
                <table align=center border="0" width="100%">
                    <tr>
                        <td width="20%" class="titleLeft" style="background: url(img/btn120.png); background-repeat: no-repeat" valign="middle">
                            <%=rb.getString("admin.planAudit.descriptShort")%>:
                        </td>
                        <td width="*" class="td_gris_l">
                        	<table width="100%" border="0">
                        		<tr>
                        			<td >
		                        		<html:text name="frmPlanAudit1" property="namePlan" size="40" maxlength="100" readonly="<%=!isPlanificar%>" />
                        			</td>
                        			<td width="10%">
										<table border="0">
						                    <tr>
						                        <td class="titleLeft" style="background: url(img/btn120.png); background-repeat: no-repeat" valign="middle">
						                            <%=rb.getString("admin.planAudit.status")%>:
						                        </td>
						                        <td width="*" class="td_gris_l" >
							                        <html:hidden name="frmPlanAudit1" property="statusPlan" />
							                        <%=rb.getString("admin.planAudit.status"+(frmPlanAudit1.getStatusPlan().equals("")?"B":frmPlanAudit1.getStatusPlan()))%>
						                        </td>
						                        <td  style="display:none">
						                        	<a href="#" onClick="javascript:abrirVentana('loadDocumentRequired.do?tipoConsulta=plan&idPlan=<%=frmPlanAudit1.getIdPlanAudit()%>',1024,768);"><img border="0" src="images/marcar.gif" alt="<%=rb.getString("btn.actualStatePlan")%>" title="<%=rb.getString("btn.actualStatePlan")%>"></a>
						                        </td>
						                        <td  style="display:none">
													<a href="#" onClick="javascript:abrirVentana('loadDocumentRequired.do?tipoConsulta=programa&idProgram=<%=frmProgramAudit1.getIdProgramAudit()%>&idPlan=<%=frmPlanAudit1.getIdPlanAudit()%>',1024,768);"><img border="0" src="images/calendario.gif" alt="<%=rb.getString("btn.actualStateProgram")%>" title="<%=rb.getString("btn.actualStateProgram")%>"></a>
						                        </td>
						                    </tr>
										</table>                        			
                        			</td>
                        		</tr>
                        	</table>
                        </td>
                    </tr>
                    <tr>
                        <td width="20%" class="titleLeft" style="background: url(img/btn120.png); background-repeat: no-repeat" valign="middle">
                            <%=rb.getString("admin.planAudit.dateFrom")%>:
                        </td>
                    	<td>
                    		<table >	
                    			<tr>
			                        <td width="*" class="td_gris_l" >
			                        	<table>
			                        		<tr>
			                        			<td>
			                        				<html:text styleId="dateFromPlan" name="frmPlanAudit1" property="dateFromPlan" size="10" readonly="true" />
			                        			</td>
			                        			<td  style="display:<%=isPlanificar?"":"none"%>">
			    	            					<a href="#" onclick='showCalendar(document.getElementById("dateFromPlan"),"calendarioDateFromPlan")' ><img src="images/calendario2.gif" border="0" ></a>
			    	            				</td>
			    	            			</tr>
			    	            		</table>
			                       		<span id="calendarioDateFromPlan"></span>
			                        </td>
			                        <td width="100px" class="titleLeft" style="background: url(img/btn120.png); background-repeat: no-repeat" valign="middle">
			                            <%=rb.getString("admin.planAudit.dateUntil")%>:
			                        </td>
			                        <td width="*" class="td_gris_l">
			                        	<table>
			                        		<tr>
			                        			<td>
			                        				<html:text styleId="dateUntilPlan" name="frmPlanAudit1" property="dateUntilPlan" size="10" readonly="true" />
			                        			</td>
			                        			<td  style="display:<%=isPlanificar?"":"none"%>">
			    	            					<a href="#" onclick='showCalendar(document.getElementById("dateUntilPlan"),"calendarioDateUntilPlan")' ><img src="images/calendario2.gif" border="0" ></a>
			    	            				</td>
			    	            			</tr>
			    	            		</table>
			                       		<span id="calendarioDateUntil"></span>
			                        </td>
			                    </tr>
			               </table>
			            </td>
                    </tr>
                    <tr>
                        <td width="20%" class="titleLeft" style="background: url(img/btn120.png); background-repeat: no-repeat" valign="middle">
                            <%=rb.getString("admin.planAudit.normAudit")%>:
                        </td>
                        <td width="*" class="td_gris_l" >
                        	<%frmPlanAudit1.setIdNorm(frmPlanAudit1.getIdNorm()==0?idNormAudit:frmPlanAudit1.getIdNorm());%>
	                         <html:select styleId="idNorm" name="frmPlanAudit1" property="idNorm" onchange="mostrarNorma(this,true)">
	                         <option value="">Seleccione...</option>
                         	<%for( Iterator<Search> i=listaNormas.iterator(); i.hasNext(); ){
                         		search =  i.next();
                         		%>
                         		<option value="<%=search.getId()%>" <%=String.valueOf(frmPlanAudit1.getIdNorm()).equals(search.getId())?"selected":""%>><%=search.getDescript()%> / <%=search.getField3().trim()%></option>
                         	<%	
                         	}%>
                         	  </html:select>
                         	  <input type="button" class="boton" value="<%=rb.getString("seguridad.toCheckTodos")%>" onClick="seleccionCompleta()" >
                        </td>
                    </tr>
                    <tr style="display:none;">
                        <td width="20%" class="titleLeft" style="background: url(img/btn120.png); background-repeat: no-repeat; vertical-align:top;padding-top:8px;" >
                            <%=rb.getString("admin.planAudit.ManagementSystemAudit")%>:
                        </td>
                        <td width="*" class="td_gris_l" style="border:1px solid #cdcdcd;">
	                         <html:hidden name="frmPlanAudit1" property="idNormPlanCheck" />
	                         <div id="listNormCheck" style="height:120px;overflow:auto;">
	                         	<%Iterator<Search> ite = ToolsHTML.sortedIteratorSearch(listaNormasDetalle.iterator(),new Comparator<Search>() {
	                         	    public int compare(Search s1, Search s2) {
	                         	        return s1.getId().compareTo(s2.getId());
	                         	    }
	                        	});
	                         	for (Iterator<Search> i = ite; i.hasNext(); ) {
	                         		search =  i.next();
	                         		if(isPlanificar && idNormAudit!=Integer.parseInt(search.getId())) {%>
	                         		<div class="<%=search.getField3().trim()%>" style="display:none;">
	                         			<input type="checkbox" value="<%=search.getId()%>" class="CHK-<%=search.getField3().trim()%>" <%=ToolsHTML.isFoundValueIntoString(frmPlanAudit1.getIdNormPlanCheck(),search.getId())?"checked":""%> />
	                         			<%=search.getField3()%> <%=search.getDescript()%> <%=search.getAditionalInfo().equals("1")?"(Requerido)":""%>
	                         		</div>
	                         		<%} else {
	                         			if(ToolsHTML.isFoundValueIntoString(frmPlanAudit1.getIdNormPlanCheck(),search.getId()) ) {%>
			                         		<div class="<%=search.getField3().trim()%>" >
			                         			<input type="checkbox" value="<%=search.getId()%>" class="CHK-<%=search.getField3().trim()%>" style="display:none;" <%=ToolsHTML.isFoundValueIntoString(frmPlanAudit1.getIdNormPlanCheck(),search.getId())?"checked":""%> /><%=search.getDescript()%>
			                         		</div>
			                         	<%}
	                         		}
	                         	}%>
	                         </div>
                        </td>
                    </tr>
                    <tr> 
                        <td colspan="2" align="center">
                            <logic:equal name="cmd" value="<%=SuperActionForm.cmdInsert%>">
                                <input type="button" class="boton" value="<%=rb.getString("btn.incluir")%>"  name="btnIncluir" onClick="javascript:guardar(this.form);" />
                                &nbsp;
                            </logic:equal>
                            <logic:equal name="cmd" value="<%=SuperActionForm.cmdEdit%>">
                            	<%if(!frmPlanAudit1.getStatusPlan().equals("C")) {%>
	                            <input type="button" class="boton" value="<%=rb.getString("btn.save")%>" name="btnSalvar"  onClick="javascript:salvar(this.form);"/>
                                &nbsp;
	                            <input type="button" class="boton" value="<%=rb.getString("btn.close")%>" name="btnClosed"  onClick="javascript:cerrar(this.form);"/>
                                &nbsp;
                                <span id="eliminar">
                                &nbsp;
                                <input type="button" class="boton" value="<%=rb.getString("btn.delete")%>" name="btnDelete" onClick="javascript:youAreSure(this.form);" />
                                </span>
                                &nbsp;
                                <%}%>
                             </logic:equal>
                                &nbsp;
                            <input type="button" class="boton" value="<%=rb.getString("btn.cancel")%>" name="btnCancelar" onClick="javascript:cancelar(this.form);" />
                        </td>
                    </tr>
                </table>
            </html:form>
        </td>
    </tr>
    </logic:notEqual>
</table>
</iframe>
</body>
</html>
<script language="javascript">
	//mostrarNorma(document.frmPlanAudit1.idNorm,false);
	
	if(document.getElementById('idNorm')) {
		setTimeout("mostrarNorma(document.getElementById('idNorm'),true)",500);
	}
</script>
