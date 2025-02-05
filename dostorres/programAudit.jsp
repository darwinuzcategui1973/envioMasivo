<%@ page import="java.util.ResourceBundle,
                 com.desige.webDocuments.utils.ToolsHTML,
                 com.desige.webDocuments.utils.beans.SuperActionForm,
                 com.desige.webDocuments.utils.beans.Users,
                 com.desige.webDocuments.utils.beans.PaginPage,
                 com.desige.webDocuments.enlaces.forms.UrlsActionForm,
                 com.desige.webDocuments.utils.beans.Search,
                 java.util.Collection,
                 java.util.Date"%>
                 <%@ page import="java.util.Calendar"%>

<!--/**
 * Title: programAudit.jsp<br>
 * Copyright: (c) 2005 Desige Servicios de Computación<br/>
 *
  * @author Ing. Simón Rodriguéz (SR)
 * @version WebDocuments v1.0
 * <br>
 *     Changes:<br>
 * <ul>
 *      <li> 06-12-2005 (SR)  Creaciòn
 * <ul>
 */-->
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<%@ page language="java" %>
<%

    ResourceBundle rb = ToolsHTML.getBundle(request);
    StringBuffer onLoad = new StringBuffer("");
    String info = (String)request.getAttribute("info");
    //System.out.println("info = " + info);
    String loadForm = (String)session.getAttribute("loadManager");
	
	Calendar dateobj = Calendar.getInstance();
	int dia=dateobj.get(Calendar.DAY_OF_MONTH);
	int mes=dateobj.get(Calendar.MONTH);
	int anio=dateobj.get(Calendar.YEAR);
	String mesStr=String.valueOf((mes+1));
	mesStr=mesStr.length()==1?"0"+mesStr:mesStr;
	String diaStr=String.valueOf((dia));
	diaStr=diaStr.length()==1?"0"+diaStr:diaStr;
    String fechaHoy=String.valueOf(anio)+"-"+mesStr.toString()+"-" +diaStr.toString();


    
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
    Collection listaNormas = (Collection)session.getAttribute("normasPadre");
    Collection listaUsuarios = (Collection)session.getAttribute("usuarios");
    Search search = null;
    
    if(session.getAttribute("newManager")==null) {
    	session.setAttribute("newManager","no asignado");
    }
%>
<html>
<head>
<title><%=rb.getString("principal.title")%></title>
<jsp:include page="meta.jsp" /> 
<link href="estilo/estilo.css" rel="stylesheet" type="text/css">
<script type="text/javascript" src="./estilo/popcalendar2.js?sdf"></script>
<script type="text/javascript" src="./estilo/funciones.js"></script>
<style>
form {
	margin:0px;
	padding:0px;
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
        var formEdit='<%=(String)session.getAttribute("formEdit")%>';
        forma = document.<%=(String)session.getAttribute("formEdit")%>;
        forma.idProgramAudit.value=num;
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
    	forma.nameProgram.value = forma.nameProgram.value.trim();
    
    	
    	
        if (forma.nameProgram.value.length==0) {
        	alert('<%=rb.getString("admin.programAudit.alert.nameProgramEdit")%>');
            return false;
        }
        
      
        if (forma.dateFrom.value.length==0) {
        	alert('<%=rb.getString("admin.programAudit.alert.dateFrom")%>');
            return false;
        }
        
        if (forma.dateUntil.value.length==0) {
        	alert('<%=rb.getString("admin.programAudit.alert.dateUntil")%>');
            return false;
        }
    	 
        if (forma.dateFrom.value<'<%=fechaHoy%>'){
        	alert('<%=rb.getString("admin.programAudit.alert.fechaHoy")%>');
            return false;
        }
        
          
        if (forma.dateFrom.value.replace(/-/gi,'')>forma.dateUntil.value.replace(/-/gi,'') ) {
        	alert('<%=rb.getString("admin.programAudit.alert.dateNotLess")%>');
            return false;
        }
        
        
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
    
    function cerrar(form){
        if (confirm("<%=rb.getString("areYouSure")%>")) {
           form.cmd.value = "<%=SuperActionForm.cmdClosed%>";
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
</script>
</head>

<body class="bodyInternas" <%=onLoad.toString()%>>
<table cellSpacing=0 cellPadding=2 align=center border=0 width="100%">
    <logic:present name="dataTable" scope="session">
        <logic:equal name="cmd" value="<%=SuperActionForm.cmdLoad%>">
        <!-- Paginación -->
        <%
            String from = request.getParameter("from");
            String size = (String)session.getAttribute("sizeParam");
            Users users = (Users)session.getAttribute("user");
            PaginPage Pagingbean = ToolsHTML.getBeanPaginPage(from,Integer.parseInt(size),String.valueOf(users.getNumRecord()));
        %>
        <html:form action='<%=(String)session.getAttribute("loadEditManager")%>'>
            <html:hidden property="idProgramAudit" /> <!-- FORM1 -->
            <html:hidden property="id" value="" />
            <html:hidden property="cmd" value="<%=SuperActionForm.cmdEdit%>"/>
            <% if (isInput) { %>
                <html:hidden property="input" value="<%=input%>"/>
                <html:hidden property="value" value="<%=value%>"/>
                <html:hidden property="nameForma" value="<%=nameForma%>"/>
            <% } %>
        </html:form>

        <form name="selection" id="selection" action="">
            <input type="hidden" name="id"/>  <!-- FORM2 -->
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
                            <td colspan="5">
                                <table class="clsTableTitle" width="100%" cellSpacing="0" cellPadding="2" align=center border=0>
                                    <tbody>
                                        <tr>
                                            <td class="td_title_bc">
                                                <%=rb.getString("admin.programAudit")%>
                                            </td>
                                        </tr>
                                    </tbody>
                                </table>
                            </td>
                        </tr>
                        <tr>
                            <td width="40%" class="td_titulo_C">
								<%=rb.getString("admin.programAudit.descript")%>
                            </td>
                            <td width="10%" class="td_titulo_C"  style="display:noneS;">
								<%=rb.getString("admin.programAudit.peridoDD")%>
                            </td>
                            <td width="10%" class="td_titulo_C"  style="display:noneS;">
								<%=rb.getString("admin.programAudit.periodoHH")%>
                            </td>
                            <td width="30%" class="td_titulo_C">
								<%=rb.getString("admin.programAudit.ManagementSystemAudit")%>
                            </td>
                            <td width="10%" class="td_titulo_C">
								<%=rb.getString("admin.programAudit.status")%>
                            </td>
                        </tr>
                        <logic:iterate id="bean" name="dataTable" indexId="ind" offset="<%=Pagingbean.getDesde()%>" length="<%=Pagingbean.getCuantos()%>"
                        type="com.desige.webDocuments.utils.beans.Search" scope="session">
                            <%
                                int item = ind.intValue()+1;
                                int num = item%2;
                            %>
                            <tr>
                                <td class='td_<%=num%>'>
                                        <% if (isInput) { %>
                                            <input name="id" type="radio" value="<%=bean.getId()%>,<%=bean.getDescript()%>"/>
                                        <%}%>
                                        <a href="javascript:edit('<%=bean.getId()%>')" class="ahref_b">
                                          <%=ToolsHTML.cortarCadena(bean.getDescript()!=null?bean.getDescript():"")%>
                                        </a>
                                </td>
                                <td class='td_<%=num%>' style="display:noneS;">
		                                <%=bean.getField1()!=null?bean.getField1():""%>
                                </td>
                                <td class='td_<%=num%>' style="display:noneS;">
		                                <%=bean.getField1()!=null?bean.getField2():""%>
                                </td>
                                <td class='td_<%=num%>'>
		                                <%=bean.getField1()!=null?bean.getField3():""%>
                                </td>
                                <td class='td_<%=num%>'>
		                                <%=bean.getField1()!=null?rb.getString("admin.programAudit.status".concat(bean.getField4())):""%>
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
                    <html:form action='<%=(String)session.getAttribute("newManager")%>'>
                        <html:hidden property="cmd" value="<%=SuperActionForm.cmdNew%>"/>  <!-- FORM3 -->

                        <html:hidden property="idProgramAudit" value=""/>
                        <html:hidden property="nameProgram" value=""/>
                        <input type="button" class="boton" value="<%=rb.getString("btn.incluir")%>" onClick="javascript:incluir(this.form);" />
	                    &nbsp;
    	                <input type="button" class="boton" value="<%=rb.getString("btn.cancel")%>" onClick="window.close();" />
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

                        <form name="formPagingPage" method="post" action="programAudit.jsp">
                            <input type="hidden" name="from"  value="">  <!-- FORM4 -->
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
            <tr>
                <td align="center">
                    <html:form action='<%=(String)session.getAttribute("newManager")%>'>  <!-- FORM5 -->
                        <html:hidden property="cmd" value="<%=SuperActionForm.cmdNew%>"/>
                        <html:hidden property="idProgramAudit" value=""/>
                        <html:hidden property="nameProgram" value=""/>
                        <% if (isInput) { %>
                            <html:hidden property="input" value="<%=input%>"/>
                            <html:hidden property="value" value="<%=value%>"/>
                            <html:hidden property="nameForma" value="<%=nameForma%>"/>
                        <%}%>
                        <logic:notPresent name="dataTable" scope="session">
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


        <logic:notEqual name="cmd" value="<%=SuperActionForm.cmdLoad%>">
    <%

        String forma = (String)session.getAttribute("editManager");
        if (UrlsActionForm.cmdInsert.equalsIgnoreCase(cmd)){
            forma = (String)session.getAttribute("newManager");
        }
    %>

    <tr>
        <td>
        	<bean:define id="frmProgramAudit1" name="frmProgramAudit1" type="com.desige.webDocuments.accion.forms.ProgramAudit" />
        	<% boolean isPlanificar = frmProgramAudit1==null || frmProgramAudit1.getStatus()==null || frmProgramAudit1.getStatus().equals("") || frmProgramAudit1.getStatus().equals("P");%>
        	
        	<logic:equal name="cmd" value="<%=SuperActionForm.cmdInsert%>">
        		<%frmProgramAudit1.cleanForm();%>
        	</logic:equal>
            <html:form action="<%=forma%>" enctype='multipart/form-data' >  <!-- FORM6 -->
                <html:hidden property="cmd" value='<%=cmd%>'/>
                <html:hidden name="frmProgramAudit1" property="id"/>
                <% if (isInput) { %>
                    <html:hidden property="input" value="<%=input%>"/>
                    <html:hidden property="value" value="<%=value%>"/>
                    <html:hidden property="nameForma" value="<%=nameForma%>"/>
                <%}%>
                <!--Se agrega una norma nueva -->
                <table align=center border=0 width="100%">
                    <tr>
                        <td colspan="2">
                            <table class="clsTableTitle" width="100%" cellSpacing="0" cellPadding="2" align=center border=0>
                                <tbody>
                                    <tr>
                                        <td class="td_title_bc">
                                           <%=rb.getString("admin.programAudit")%>
                                        </td>
                                    </tr>
                                </tbody>
                            </table>
                        </td>
                    </tr>
                    <tr>
                        <td width="20%" class="titleLeft" style="background: url(img/btn120.png); background-repeat: no-repeat" valign="middle">
                            <%=rb.getString("admin.programAudit.descriptShort")%>:
                        </td>
                        <td width="*" class="td_gris_l">
                        	<table width="100%" border="0">
                        		<tr>
                        			<td >
		                        		<html:text name="frmProgramAudit1" property="nameProgram" size="40" maxlength="100" readonly="<%=!isPlanificar%>" />
                        			</td>
                        			<td width="10%">
										<table border="0" style="display:nones;">
						                    <tr>
						                        <td width="20%" class="titleLeft" style="background: url(img/btn120.png); background-repeat: no-repeat" valign="middle">
						                            <%=rb.getString("admin.programAudit.status")%>:
						                        </td>
						                        <td width="*" class="td_gris_l">
							                         <html:hidden name="frmProgramAudit1" property="status" />
							                         	<%if(frmProgramAudit1.getStatus().equals("P") || frmProgramAudit1.getStatus().equals("B")){%>
							                         		<%=rb.getString("admin.programAudit.statusB").toUpperCase()%>
							                         	<%} else if (frmProgramAudit1.getStatus().equals("A")) {%>
							                         		<%=rb.getString("admin.programAudit.statusA").toUpperCase()%>
							                         	<%} else if (frmProgramAudit1.getStatus().equals("C")) {%>
							                         		<%=rb.getString("admin.programAudit.statusC").toUpperCase()%>
							                         	<%}%>
							                         	
							                         
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
                            <%=rb.getString("admin.programAudit.normGuide")%>:
                        </td>
                        <td width="*" class="td_gris_l">
                        	<%frmProgramAudit1.setIdNorm(frmProgramAudit1.getIdNorm()==0?idNormAudit:frmProgramAudit1.getIdNorm());%>
	                         <html:hidden name="frmProgramAudit1" property="idNorm" />
                         	<%for( java.util.Iterator<Search> i=listaNormas.iterator(); i.hasNext(); ){
                         		search =  i.next();
                         		if(frmProgramAudit1.getIdNorm()==Integer.parseInt(search.getId())) {%>
                         		<input type="text" value="<%=search.getDescript()%>" size="70" readonly >
                         	<%	}
                         	}%>
                        </td>
                    </tr>
                    <tr style="display:none;">
                        <td width="20%" class="titleLeft" style="background: url(img/btn120.png); background-repeat: no-repeat" valign="middle">
                            <%=rb.getString("admin.programAudit.responsible")%>:
                        </td>
                        <td width="*" class="td_gris_l">
	                         <html:select name="frmProgramAudit1" property="idPersonResponsible" >
	                         	<%for( java.util.Iterator<Search> i=listaUsuarios.iterator(); i.hasNext(); ){
	                         		search =  i.next();%>
	                         		<option value="<%=search.getId()%>" <%=frmProgramAudit1.getIdPersonResponsible()==Integer.parseInt(search.getId())?"selected":""%> ><%=search.getDescript()%></option>
	                         	<%}%>
	                         </html:select>
                        </td>
                    </tr>
                    <tr style="display:noneS">
                        <td width="20%" class="titleLeft" style="background: url(img/btn120.png); background-repeat: no-repeat" valign="middle">
                            <%=rb.getString("admin.programAudit.dateFrom")%>:
                        </td>
                    	<td>
                    		<table>	
                    			<tr>
			                        <td width="*" class="td_gris_l" >
			                        	<table>
			                        		<tr>
			                        			<td>
			                        				<html:text styleId="dateFrom" name="frmProgramAudit1" property="dateFrom" size="10" readonly="true" />
			                        			</td>
			                        			<td  style="display:<%=isPlanificar?"":"none"%>">
			    	            					<a href="#" onclick='showCalendar(document.getElementById("dateFrom"),"calendarioDateFrom")' ><img src="images/calendario2.gif" border="0" ></a>
			    	            				</td>
			    	            			</tr>
			    	            		</table>
			                       		<span id="calendarioDateFrom"></span>
			                        </td>
			                        <td width="100px" class="titleLeft" style="background: url(img/btn120.png); background-repeat: no-repeat" valign="middle">
			                            <%=rb.getString("admin.programAudit.dateUntil")%>:
			                        </td>
			                        <td width="*" class="td_gris_l">
			                        	<table>
			                        		<tr>
			                        			<td>
			                        				<html:text styleId="dateUntil" name="frmProgramAudit1" property="dateUntil" size="10" readonly="true" />
			                        			</td>
			                        			<td  style="display:<%=isPlanificar?"":"none"%>">
			    	            					<a href="#" onclick='showCalendar(document.getElementById("dateUntil"),"calendarioDateUntil")' ><img src="images/calendario2.gif" border="0" ></a>
			    	            				</td>
			    	            			</tr>
			    	            		</table>
			                       		<span id="calendarioDateUntil"></span>
			                        </td>
			                    </tr>
			               </table>
			            </td>
                    </tr>
                    <tr style="display:none">
                        <td width="20%" class="titleLeft" style="background: url(img/btn120.png); background-repeat: no-repeat; vertical-align:top;padding-top:8px;" >
                            <%=rb.getString("admin.programAudit.ManagementSystemAudit")%>:
                        </td>
                        <td width="*" class="td_gris_l" style="border:1px solid #cdcdcd;">
	                         <html:hidden name="frmProgramAudit1" property="idNormCheck" />
	                         <div id="listNormCheck">
	                         	<%for( java.util.Iterator<Search> i=listaNormas.iterator(); i.hasNext(); ){
	                         		search =  i.next();
	                         		if(isPlanificar && idNormAudit!=Integer.parseInt(search.getId())) {%>
	                         		<div>
	                         			<input type="checkbox" value="<%=search.getId()%>" <%=ToolsHTML.isFoundValueIntoString(frmProgramAudit1.getIdNormCheck(),search.getId())?"checked":""%> /><%=search.getDescript()%>
	                         		</div>
	                         		<%} else {
	                         			if(ToolsHTML.isFoundValueIntoString(frmProgramAudit1.getIdNormCheck(),search.getId()) ) {%>
			                         		<div>
			                         			<input type="checkbox" value="<%=search.getId()%>" style="display:none;" <%=ToolsHTML.isFoundValueIntoString(frmProgramAudit1.getIdNormCheck(),search.getId())?"checked":""%> /><%=search.getDescript()%>
			                         		</div>
			                         	<%}
	                         		}
	                         	}%>
	                         </div>
                        </td>
                    </tr>
                    <logic:equal name="cmd" value="<%=SuperActionForm.cmdEdit%>">
                    <tr>
                    	<td colspan="2">
                            <div style="background-color:#cdcdcd;padding:2px;" class="td_title_bc">
                            	<%=rb.getString("admin.planAudit")%>
                            </div>
							<iframe name="frmPlan" style="width:99.7%;height:320px;background-color:#efefef;border:2px solid #cdcdcd" src="loadPlanAudit.do?idProgramAudit=<%=frmProgramAudit1.getIdProgramAudit()%>"></iframe>
                    	</td>
                    </tr>
                    </logic:equal>
                    <tr> 
                        <td colspan="2" align="center">
                            <logic:equal name="cmd" value="<%=SuperActionForm.cmdInsert%>">
                                <input type="button" class="boton" value="<%=rb.getString("btn.incluir")%>"  name="btnIncluir" onClick="javascript:guardar(this.form);" />
                                &nbsp;
                            </logic:equal>
                            <logic:equal name="cmd" value="<%=SuperActionForm.cmdEdit%>">
                            	<%if(!frmProgramAudit1.getStatus().equals("C")) {%>
	                            <input type="button" class="boton" value="<%=rb.getString("btn.save")%>" name="btnSalvar"  onClick="javascript:salvar(this.form);"/>
                                &nbsp;
	                            <input type="button" class="boton" value="<%=rb.getString("btn.close")%>" name="btnCerrar"  onClick="javascript:cerrar(this.form);"/>
                                &nbsp;
                                <logic:notPresent name="valor" scope="session" >
                                <span id="eliminar">
                                &nbsp;
                                <input type="button" class="boton" value="<%=rb.getString("btn.delete")%>" name="btnDelete" onClick="javascript:youAreSure(this.form);" />
                                </span>
                                </logic:notPresent>
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
	window.onunload=cerrar;
	
	if(document.frmProgramAudit1!=null && document.frmProgramAudit1.id!=null && document.frmProgramAudit1.id.value=='1' && document.getElementById("eliminar")!=null) {
		document.getElementById("eliminar").style.display="none";
	}
</script>
