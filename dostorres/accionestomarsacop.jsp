<%@page import="com.itextpdf.text.log.SysoCounter"%>
<%@ page import="java.util.ResourceBundle,
                 com.desige.webDocuments.utils.ToolsHTML,
                 com.desige.webDocuments.utils.beans.SuperActionForm,
                 com.desige.webDocuments.utils.beans.Users,
                 com.desige.webDocuments.utils.beans.PaginPage,
                 com.focus.qweb.to.PossibleCauseTO,
                 java.util.ArrayList,
                 com.desige.webDocuments.enlaces.forms.UrlsActionForm"%>
<%@ page import="java.util.Calendar"%>

<!--/**
 * Title: accionestomarsacop.jsp<br>
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
    if (ToolsHTML.isEmptyOrNull(loadForm)) {
        loadForm = "";
    }
    if (!ToolsHTML.isEmptyOrNull(info)) {
        onLoad.append(" onLoad=\"alert('").append(info).append("');try{window.opener.refrescarPaginaPadre();}catch(e){}\"");
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
    Calendar dateobj = Calendar.getInstance();
    int dia=dateobj.get(Calendar.DAY_OF_MONTH);
    int mes=dateobj.get(Calendar.MONTH);
    int anio=dateobj.get(Calendar.YEAR);

    //se usan stas variables para tener la fecha estimada como variables globales en sta forma
    int diast=dateobj.get(Calendar.DAY_OF_MONTH);
    int messt=dateobj.get(Calendar.MONTH);
    int aniost=dateobj.get(Calendar.YEAR);
  
    boolean SwMuestraFormGnrl=false;
    String fechaEstimada=request.getAttribute("fechaEstimada")!=null?(String)request.getAttribute("fechaEstimada"):"";
    String fechaEmision=request.getAttribute("fechaemision")!=null?(String)request.getAttribute("fechaemision"):"nada";

    String fechaSacops1="";
    if(request.getAttribute("fechaSacops1")!=null) {
    	fechaSacops1 = (String)request.getAttribute("fechaSacops1");
    	if(fechaSacops1.indexOf("-")!=-1) {
    		String[] arr = fechaSacops1.split("-");
    		anio= Integer.parseInt(arr[0]);
    		mes= Integer.parseInt(arr[1]);
    		mes=mes-1;
    		dia= Integer.parseInt(arr[2]);
    	}
    }
    
    String comentario = request.getAttribute("comentario")!=null?(String)request.getAttribute("comentario"):"";
    String evidencia = request.getAttribute("evidencia")!=null?(String)request.getAttribute("evidencia"):"";
    String archivoTecnica = request.getAttribute("archivoTecnica")!=null?(String)request.getAttribute("archivoTecnica"):"";
    
    if (!ToolsHTML.isEmptyOrNull((String)request.getAttribute("fechaEstimada"))){
        aniost =Integer.parseInt(fechaEstimada!=null?fechaEstimada.substring(0,4):String.valueOf(aniost));
        if (fechaEstimada!=null){
            messt= Integer.parseInt(fechaEstimada.substring(5,7))-1;
        }
        
        diast= Integer.parseInt(fechaEstimada!=null?fechaEstimada.substring(8,10):String.valueOf(diast));
    }
    
    ArrayList listPossibleCause = (ArrayList)request.getAttribute("listPossibleCause");
    PossibleCauseTO possibleTO = null;

%>
<html>

<head>
<title><%=rb.getString("principal.title")%></title>
<jsp:include page="meta.jsp" /> 
<link href="estilo/estilo.css" rel="stylesheet" type="text/css">
<script type="text/javascript" src="./estilo/funciones.js"></script>
<script type="text/javascript">
//seleccionar de lista
function contSelectInList(lista) {
        var items = lista.length;
        if (items==0) {
            return 0;
        }
        var row = 0;
        for (row = 0; row < items ; row++) {
            var valor = lista[row];
            valor.selected = true;
        }
        return items;
    }
 function contarItemsSelecteds(lista){
           if (lista){
               var items = lista.length;
               var valor;
               var cont = 0;
               var row = 0;
               for (row = 0; row < items; row++){
                   var valor = lista[row];
                   if (valor.selected){
                       cont++;
                   }
               }
               return cont;
           }
           return 0;
       }
	function createElement(value,texto){
		var newItem = document.createElement("OPTION");
		newItem.value = value;
		newItem.text = texto;
		return newItem;
	}
	function removerDeLista(lista,items) {
		if (lista){
            var row = 0;
			for (row = items - 1; row >= 0; row--) {
				var valor = lista[row];
				if (valor.selected){
					lista.remove(row);
				}
			}
		}
	}
	function mover(listDer,listIzq) {
		var items = listDer.length;
		var selecteds = contarItemsSelecteds(listDer);
		if (selecteds > 0){
			var	newItem;
			for (row = 0; row < items; row++){
				var valor = listDer[row];
				if (valor.selected) {
                    newItem = createElement(listDer[row].value,listDer[row].text);
					listIzq.options[listIzq.length] = newItem;
				}
			}
			removerDeLista(listDer,items);
		}
	}

//end seleccionar de lista
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
        form.idplanillasacop.value="<%=(String)request.getAttribute("idplanillasacop")%>";
        form.cmd.value="<%=SuperActionForm.cmdLoad%>";
        //alert(form.action);
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
        forma = eval(document.<%=(String)session.getAttribute("formEdit")%>);
        forma.cmd.value = '<%=SuperActionForm.cmdEdit%>';
        forma.idplanillasacopaccion.value = num;
        forma.id.value = num;
        forma.submit();

    }

    function DeleteArchivoTecnica(form){
    	var result = confirm('<%= rb.getString("areYouSure") %>');
    	if(result){
    		form.deleteArchivoTecnica.value = 1;
            SaveGeneral(form);
    	}
    }
    
    function SaveGeneral(form){
    	//alert(form.action);
    	var comeFromDeleteArchivoTecnica = (form.deleteArchivoTecnica.value == 1);
    	var result = true;
    	if(!comeFromDeleteArchivoTecnica){
    	
    		var isAssociate = false;
    		if(document.getElementById("idDocumentAssociate").value!=null && parseInt(document.getElementById("idDocumentAssociate").value) ) {
    			isAssociate = true;
    		}
    		//verificamos si el formulario se desea guardar sin archivo de tecnica
    		if(form.archivoTecnica.value == "" && <%= ToolsHTML.isEmptyOrNull(archivoTecnica)%> && isAssociate==false){
    			result = confirm('<%= rb.getString("scp.continuar.sin.archivo.tecnica") %>');
    		}
    	}
    	
    	if(result){
    		form.submit();
    	}
    }
    
    function salvar(form){
        if (validar(form)){
          form.submit();
        }
    }
    
    function salvarUserBorraEvidencia(form){
    	var result = confirm('<%= rb.getString("areYouSure") %>');
        if(result){
        	form.delPreviousEvidence.value = "1";
            salvar_userponecomentario(form, 0);
        }
    }
    
   function salvar_userponecomentario(form,firma){
       //alert(form.action+"=editaccionesporpersona.do");
       //alert(form.action);
        form.action="editAccionesporpersona.do?firma="+firma
        form.btnSalvarf.disabled =true;
        form.btnSalvars.disabled =true;
        form.submit();
    }
    function validar(forma){

        if ((contSelectInList(<%out.print("forma.usersSelected");%>)==0)) {
             alert('<%=rb.getString("scp.val9")%>');
             return false;
        }
      //  alert(Trim(forma.numerodeaccion.value) + ' ' + forma.numerodeaccion.value.length);
        var accion1=Trim(forma.numerodeaccion.value);
        if (stringVacio(forma.numerodeaccion.value)) {
            alert('<%=rb.getString("scp.val10")%>');
            return false;
        }
        if (hayComillas(forma.numerodeaccion.value)){
            alert('<%=rb.getString("scp.vald2")%>');
            return false;
        }

        if (fechavalbisiesto(forma.dia.value,forma.mes.value,forma.anio.value)=='1'){
            alert('<%=rb.getString("scp.diafebrero")%>');
            return false;
        }else if (fechavalbisiesto(forma.dia.value,forma.mes.value,forma.anio.value)=='2'){
            alert('<%=rb.getString("scp.diainvalido")%>');
            return false;
        }else if (fechavalbisiesto(forma.dia.value,forma.mes.value,forma.anio.value)=='3'){
            alert('<%=rb.getString("scp.mestreintadias")%>');
            return false;
        }

        //la fecha de la accion siempre debe ser mayor o igual a la fecha de hoy
        if (('<%=anio%>' - forma.anio.value) >0){
            alert('<%=rb.getString("scp.fechaaccionmayorhoy")%>'+ " " + "<%=dia%>/<%=(mes + 1)%>/<%=anio%>");
            return false;
        }else if  ( (('<%=anio%>'-forma.anio.value)>=0) && (('<%=(mes + 1)%>' - forma.mes.value )>0) ) {
            alert('<%=rb.getString("scp.fechaaccionmayorhoy")%>'+ " " + "<%=dia%>/<%=(mes + 1)%>/<%=anio%>");
            return false;
        }else if ((('<%=anio%>'-forma.anio.value)>=0) && (('<%=(mes + 1)%>' - forma.mes.value) >=0) &&  ( ('<%=dia%>'-forma.dia.value) >0)){
            alert('<%=rb.getString("scp.fechaaccionmayorhoy")%>'+ " " + "<%=dia%>/<%=(mes + 1)%>/<%=anio%>");
            return false;
        }
        return true;
    }

    function incluir(form) {

        form.cmd.value = "<%=SuperActionForm.cmdNew%>";
        form.submit();
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
 
 function showFileSacop(idplanillasacop1) {
     var idplanillasacop1 = escape(idplanillasacop1);
     abrirVentana("viewDocumentSacop.jsp?idplanillasacop1=" + idplanillasacop1 ,800,600);
 }
 
 function agregar() {
	//abrirVentana("searchDocument.do?expand=false&popup=true",1000,600);
	abrirVentana("showPublished.do?expand=false&popup=true",1024,600);
 }
 
 
 function setIdDocumentAssociate(idDoc,numVer,nameDoc) {
 	document.getElementById('idDocumentAssociate').value=idDoc;
 	document.getElementById('numVerDocumentAssociate').value=numVer;
 	document.getElementById('nameDocumentAssociate').value=nameDoc;
 	document.getElementById('lnkNameDocumentAssociate').innerHTML=nameDoc;
 }
 
 function abrirDoc() {
 	var id = document.getElementById('idDocumentAssociate').value;
 	var ver = document.getElementById('numVerDocumentAssociate').value;
 	showDocumentPublishImp(id,ver,id);
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
</script>
</head>

<body class="bodyInternas" <%=onLoad.toString()%>>
<table cellSpacing=0 cellPadding=2 align=center border=0 width="100%">
  <tr>
    <td>
       <table class="clsTableTitle" width="100%" cellSpacing="0" cellPadding="2" align=center border=0>
            <tbody>
                  <tr>
                     <td class="td_title_bc"> 
                         <%=rb.getString("scp.acctomar")%>
                     </td>
                  </tr>
             </tbody>
        </table>
     </td>
   </tr>
   <logic:present name="dataTable" scope="session">
        <logic:equal name="cmd" value="<%=SuperActionForm.cmdLoad%>">
   <tr>
       <td>
        <!-- Paginación -->
        <%
            String from = request.getParameter("from");
            String size = (String)session.getAttribute("sizeParam");
            Users users = (Users)session.getAttribute("user");
            PaginPage Pagingbean = ToolsHTML.getBeanPaginPage(from,Integer.parseInt(size),String.valueOf(users.getNumRecord()));
            //System.out.println("Aqui Vamos" + session.getAttribute("loadEditManager"));
        %>
        <html:form action='<%=(String)session.getAttribute("loadEditManager")%>'>
            <html:hidden property="id" value=""/>
            <html:hidden property="idplanillasacopaccion" value=""/>
            <html:hidden property="idplanillasacop1" value=""/>
            <input type="hidden" name="idplanillasacop" value='<%=(String)request.getAttribute("idplanillasacop")%>'>
            <html:hidden property="cmd" value="<%=SuperActionForm.cmdEdit%>"/>
            <% if (isInput) { %>
                <html:hidden property="input" value="<%=input%>"/>
                <html:hidden property="value" value="<%=value%>"/>
                <html:hidden property="nameForma" value="<%=nameForma%>"/>
            <% } %>
        </html:form>
        
        <form name="selection" id="selection" action="">
            <input type="hidden" name="id"/>
            <input type="hidden" name="desc"/>

            <% if (isInput) { %>
                <html:hidden property="input" value="<%=input%>"/>
                <html:hidden property="value" value="<%=value%>"/>
                <html:hidden property="nameForma" value="<%=nameForma%>"/>
            <%}%>
                    <table cellSpacing=0 cellPadding=0 align=center border=0 width="100%">
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
                                            <%=bean.getDescript()%>
                                        </a>
                                       <% if (!ToolsHTML.isEmptyOrNull(bean.getAditionalInfo())){%>
                                              <a href="javascript:showNorms('<%=bean.getId()%>')" class="ahref_b">
                                              &nbsp;&nbsp;&nbsp;<img src="img/pendings.gif" width="16" height="16">
                                              </a>
                                        <%}%>
                                </td>
                            </tr>
                        </logic:iterate>
                   </table>
        </form>
                </td>
            </tr> 
   <tr>
            <td>
                <center>
                    <html:form action='<%=(String)session.getAttribute("newManager")%>'>
                        <html:hidden property="cmd" value="<%=SuperActionForm.cmdNew%>"/>
                        <html:hidden property="idplanillasacopaccion" value=""/>
                        <html:hidden property="idplanillasacop1" value=""/>
                        <input type="hidden" name=idplanillasacop value=<%=(String)request.getAttribute("idplanillasacop")%>>
                        <logic:notPresent name="userponecomentario" scope="session">
                            <input type="button" class="boton" value="<%=rb.getString("btn.incluir")%>" onClick="javascript:incluir(this.form);"/>
                        </logic:notPresent>

                    </html:form>
                    &nbsp;
                </center>
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
                        <form name="formPagingPage" method="post" action="editField.jsp">
                            <input type="hidden" name="from"  value="">
                            <input type="hidden" name="cmd"  value="<%=SuperActionForm.cmdLoad%>">
                            <input type="hidden" name=idplanillasacop value=<%=(String)request.getAttribute("idplanillasacop")%>>
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
                <td> 
                    <html:form action='<%=(String)session.getAttribute("newManager")%>'>
                         <html:hidden property="cmd" value="<%=SuperActionForm.cmdNew%>"/>
                         <input type="hidden" name=idplanillasacop value=<%=(String)request.getAttribute("idplanillasacop")%>>
                         <html:hidden property="idplanillasacopaccion" value=""/>
                         <html:hidden property="idplanillasacop1" value=""/>
                        <% if (isInput) { %>
                            <html:hidden property="input" value="<%=input%>"/>
                            <html:hidden property="value" value="<%=value%>"/>
                            <html:hidden property="nameForma" value="<%=nameForma%>"/>
                        <%}%>
                        <logic:notPresent name="dataTable" scope="session">
                            <logic:notEqual value="<%=SuperActionForm.cmdInsert%>" name="cmd" >
                                <logic:notPresent name="userponecomentario" scope="session">
                                    <table width="100%" cellSpacing=0 cellPadding=0 align=center border=0>
                                        <tr>
                                            <td>
                                                <input type="button" class="boton" value="<%=rb.getString("btn.incluir")%>" onClick="javascript:incluir(this.form);"/>
                                            </td>
                                        </tr>
                                    </table>
                               </logic:notPresent>
                            </logic:notEqual>
                        </logic:notPresent>
                    </html:form>
                </td>
            </tr>  
    <logic:notEqual name="cmd" value="<%=SuperActionForm.cmdLoad%>">
        <%

        String forma = (String)session.getAttribute("editManager");
        //System.out.println("cmd = " + cmd);

        if (UrlsActionForm.cmdInsert.equalsIgnoreCase(cmd)){
            forma = (String)session.getAttribute("newManager");
        }
        %>
    <tr>
        <td>
            <html:form action="<%=forma%>" enctype='multipart/form-data' method="post">
                <html:hidden property="cmd" value='<%=cmd%>'/>
                <input type="hidden" name=idplanillasacop value=<%=(String)request.getAttribute("idplanillasacop")%>>
                <% if (isInput) { %>
                    <html:hidden property="input" value="<%=input%>"/>
                    <html:hidden property="value" value="<%=value%>"/>
                    <html:hidden property="nameForma" value="<%=nameForma%>"/>
                <%}%>
                <!--Se agrega una norma nueva -->
                 <table align=center border=0 width="100%">
                    
                    <tr>
                        <td colspan="4">
                            <table>
                                <tbody>
                                    <%String accion1="";%>
                                    <logic:present name="editTypeDoc" scope="session">
                                              <bean:define id ="acc" name="editTypeDoc" type="com.desige.webDocuments.sacop.forms.PlantillaAccion" scope="session"/>

                                               <% accion1=acc.getAccion();%> <br>
                                               <%anio =Integer.parseInt(acc.getFecha()!=null?acc.getFecha().substring(0,4):"");
                                                 mes= Integer.parseInt(acc.getFecha()!=null?acc.getFecha().substring(5,7):"")-1;
                                                 dia= Integer.parseInt(acc.getFecha()!=null?acc.getFecha().substring(8,10):"");
                                               %>

                                    </logic:present>
                                     <tr>
                                        <td  class="titleLeft" height="44" style="background: url(img/btn260x60.gif); background-repeat: no-repeat" width="26%" valign="middle">
                                         <%=rb.getString("scp.accion")%>
                                        </td>
                                        <td width="*" class="td_gris_l">
                                        <logic:notPresent name="userponecomentario" scope="session">
                                            <html:textarea property="numerodeaccion" cols='60' rows='3' value="<%=accion1%>" />
                                        </logic:notPresent>

                                         <logic:present name="userponecomentario" scope="session">
                                            <html:textarea property="numerodeaccion" disabled='true' cols='60' rows='3' value="<%=accion1%>" />
                                        </logic:present>
                                        </td>
                                    </tr>
                                    <logic:present name="userponecomentario" scope="session">
                                    <tr>
                                        <td  class="titleLeft" height="44" style="background: url(img/btn260x60.gif); background-repeat: no-repeat" width="26%" valign="middle">
                                            <%=rb.getString("scp.comm")%>
                                        </td>
                                        <td width="*" class="td_gris_l">
                                            <html:textarea property="comentario"  cols='60' rows='3' value="<%=comentario%>" />
                                        </td>
                                    </tr>
                                    <tr>
                                        <td  class="titleLeft" height="44" style="background: url(img/btn260x60.gif); background-repeat: no-repeat" width="26%" valign="middle">
                                            <%=rb.getString("scp.evidencia")%>
                                        </td>
                                        <td width="*" class="td_gris_l">
                                            <html:hidden property="delPreviousEvidence" value="0"/>
                                            <html:file property="evidencia" />
                                            <%
                                            if(! "".equals(evidencia)){
                                            %>
                                            	<br />
                                            	<a  href="javascript:showFileSacop('<%
                                            	   String prefix = (String) request.getAttribute("evidenciaPrefix");
                                            	   int pos = prefix.lastIndexOf("/");
                                            	   if (pos != -1) {
                                            		   prefix = prefix.substring(0, pos+1);
                                            	   }
                                            	   out.print(prefix + evidencia);
                                            		%>');">
                                            	   <%= evidencia %>
                                            	   &nbsp;&nbsp;
                                            	   <img src="img/pendings.gif" width="16" height="16" border="0">
                                            	</a>
                                            <%
                                            }
                                            %>
                                        </td>
                                    </tr>
                                  </logic:present>
                                   <tr>
                                   <logic:notPresent name="userponecomentario" scope="session">
                                        <td align="center" class="titleLeft" height="44" style="background: url(img/btn260x80.gif); background-repeat: no-repeat" width="26%" valign="middle">
                                            <%=rb.getString("scp.fecha")%>
                                        </td>
                                        <td colspan="2">
                                            <%=rb.getString("scp.dia")%>:<select name=dia >
                                                 <%
                                                     String less9;
                                                     for (int i=1;i<=31;i++){
                                                      if (i<=9){
                                                         less9="0"+i;
                                                      }else{
                                                        less9=String.valueOf(i);
                                                      }
                                                       if (dia==i){
                                                          out.print("<option value="+less9+" selected>"+less9+"</option>");
                                                       }else{
                                                          out.print("<option value="+less9+">"+less9+"</option>");
                                                       }

                                                     }
                                                 %>
                                             </select>
                                            &nbsp;<%=rb.getString("scp.mes")%>:<select name=mes>
                                                 <%
                                                   // String less9;
                                                     for (int i=1;i<=12;i++){
                                                      if (i<=9){
                                                         less9="0"+i;
                                                      }else{
                                                        less9=String.valueOf(i);
                                                      }

                                                      if ((mes+1)==i){
                                                          out.print("<option value="+less9+" selected>"+less9+"</option>");
                                                       }else{
                                                          out.print("<option value="+less9+">"+less9+"</option>");
                                                       }
                                                     }
                                                 %>
                                             </select>
                                              &nbsp;<%=rb.getString("scp.anio")%>:<select name=anio>
                                                 <%
                                                     for (int i=1930;i<=2099;i++){
                                                       if (i<=9){
                                                         less9="0"+i;
                                                       }else{
                                                         less9=String.valueOf(i);
                                                       }
                                                        if ((anio)==i){
                                                          out.print("<option value="+less9+" selected>"+less9+"</option>");
                                                       }else{
                                                          out.print("<option value="+less9+">"+less9+"</option>");
                                                       }
                                                     }
                                                 %>
                                             </select>
                                           </logic:notPresent>
                                        </td>
                                    </tr>
                                </tbody>
                            </table>
                        </td>
                    </tr>
                   <logic:notPresent name="userponecomentario" scope="session">
                    <tr>
                         <td class="titleLeft" style="background: url(img/btn260x100.gif); background-repeat: no-repeat; text-align:left" width="15%" valign="middle">
                             <%=rb.getString("scp.responsables")%>:
                        </td>
                        <td colspan="2">&nbsp;</td>
                    </tr>
                    <tr>
                        <td>
                         <select name="users" size="8" multiple style="width:400px;" styleClass="classText" onDblClick="javascript:vertexto(this.form.users);">
                            <logic:present name="usuarios" scope="request">
                                <logic:iterate id="bean" name="usuarios" scope="request" type="com.desige.webDocuments.utils.beans.Search">
                                    <option value="<%=bean.getId()%>"><%=bean.getDescript()%>(<%=bean.getAditionalInfo()%>)</option>
                                </logic:iterate>
                            </logic:present>
                        </select>
                        </td>
                        <td align="center" width="10%">
                         <input type="button" class="boton" value=">" style="width:28px;" onClick="javascript:mover(this.form.users,this.form.usersSelected);" />
                            <br/>
                         <input type="button" class="boton" value="<" style="width:28px;" onClick="javascript:mover(this.form.usersSelected,this.form.users);" />
                        </td>
                        <select name="aux" style="display:none" multiple>
                        </select>
                        <select name="auxSelec" style="display:none" multiple>
                        </select>
                        <td>
                        <select name="usersSelected" size="8" multiple style="width:210px;" styleClass="classText" onDblClick="javascript:vertexto(this.form.usersSelected);">
                           <logic:present name="usuariosEscojidos" scope="request">
                                <logic:iterate id="bean" name="usuariosEscojidos" scope="request" type="com.desige.webDocuments.utils.beans.Search">
                                    <option value="<%=bean.getId()%>"><%=bean.getDescript()%>(<%=bean.getAditionalInfo()%>)</option>
                                </logic:iterate>
                            </logic:present>
                        </select>
                        </td>
                    </tr>
                     </logic:notPresent>
                    <tr>
                        <td>

                        </td>
                        <td>

                        </td>
                    </tr>
                    <logic:present name="userponecomentario" scope="session">
                        <tr>
                            <td colspan="4">
                                <input type="button" class="boton" value="<%=rb.getString("scp.firmar")%>" name="btnSalvarf"  onClick="javascript:salvar_userponecomentario(this.form,'1');"/>
                                &nbsp;
                                <input type="button" class="boton" value="<%=rb.getString("btn.save")%>" name="btnSalvars"  onClick="javascript:salvar_userponecomentario(this.form,'0');"/>
                                &nbsp;
                                <input type="button" class="boton" value="<%=rb.getString("btn.cancel")%>" name="btnCancelar" onClick="javascript:cancelar(this.form);"/>
                                <%
                                if(! "".equals(evidencia)){
                                %>
	                                &nbsp;
	                                <input type="button" class="boton" value="<%=rb.getString("scp.del.prev.evidencia")%>" name="btnDelEvidence"  onClick="javascript:salvarUserBorraEvidencia(this.form);"/>
	                            <%
	                            }
	                            %>
                            </td>
                        </tr>
                    </logic:present>
                            <logic:notPresent name="userponecomentario" scope="session">
                             <tr>
                               <td align="center" colspan="8">
                                <input type="button" class="boton" value="<%=rb.getString("btn.save")%>" name="btnSalvar"  onClick="javascript:salvar(this.form);"/>
                                <logic:notEqual name="cmd" value="<%=SuperActionForm.cmdInsert%>">
                                    &nbsp;
                                    <input type="button" class="boton" value="<%=rb.getString("btn.incluir")%>"  name="btnIncluir" onClick="javascript:incluir(this.form);"/>
                                    &nbsp;
                                    <logic:notPresent name="valor" scope="session" >
                                    &nbsp;
                                    <input type="button" class="boton" value="<%=rb.getString("btn.delete")%>" name="btnDelete" onClick="javascript:youAreSure(this.form);"/>
                                    </logic:notPresent>
                                 </logic:notEqual>
                                   &nbsp;
                                    <input type="button" class="boton" value="<%=rb.getString("btn.cancel")%>" name="btnCancelar" onClick="javascript:cancelar(this.form);"/>
                               </td>
                             </tr>
                            </logic:notPresent>
                    </table>
                    </td>
                   </tr>
            </html:form>
        </td>
    </tr>
    </logic:notEqual>
    <logic:notPresent name="dataTable" scope="session">
    <logic:notEqual value="<%=SuperActionForm.cmdInsert%>" name="cmd" >
        <%
            SwMuestraFormGnrl=true;
        %>
    </logic:notEqual>
  </logic:notPresent>
  <logic:present name="dataTable" scope="session">
    <logic:equal name="cmd" value="<%=SuperActionForm.cmdLoad%>">
        <%
            SwMuestraFormGnrl=true;
        %>
    </logic:equal>
  </logic:present>
<% if (SwMuestraFormGnrl){%>
    <tr>
        <td align="center">
            <html:form action="editGnrlAccionesTomarSacop.do" enctype="multipart/form-data" method="post">
            <html:hidden property="idplanillasacop1" value='<%=(String)request.getAttribute("idplanillasacop")%>'/>
            <html:hidden property="cmd" value="<%=SuperActionForm.cmdLoad%>"/>
            <table width="100%" border="0">
                <tr>
                    <td >
                        <table width="100%" border="0" cellspacing="0" cellpadding="0" bordercolor="#EEEEEE">
                                <tbody>
                                    <% String obs=request.getAttribute("Accionobservacion")!=null?(String)request.getAttribute("Accionobservacion"):""; %>
                                    <% String causasnoconformidad=request.getAttribute("causasnoconformidad")!=null?(String)request.getAttribute("causasnoconformidad"):""; %>
                                    <% String actionMainDescription=request.getAttribute("descripcionAccionPrincipal")!=null?(String)request.getAttribute("descripcionAccionPrincipal"):""; %>
                                <logic:notPresent name="userponecomentario" scope="session">
                                
                                     <tr>
                                         <td colspan="6" width="*" class="td_gris_l">
                                         	<table width="100%" border="0" cellspacing="3" cellpadding="0">
                                         		<tr>
                                         			<td width="50%" align="center" heigth="26" class="titleCenter" style="background: url(img/BgComments.gif); background-repeat: no-repeat;padding:2px;">
                                            			<%=rb.getString("scp.obs")%>
                                         			</td>
                                         			<td width="50%"  align="center" heigth="26" class="titleCenter" style="background: url(img/BgComments.gif); background-repeat: no-repeat;padding:2px;" nowrap>
                                         				<%=rb.getString("scp.selectCause")%>
                                         			</td> 
                                         		</tr>
                                         		<tr>
                                         			<td>
                                         				<html:textarea property="accionobservacion" rows='6' value="<%=obs%>" style="height:120px;width:100%" />
			                                             <br />
			                                             <br />
                                         			</td>
                                         			<td rowspan="3" style="vertical-align:top">
								 	                    <html:hidden property="causasnoconformidad" value='<%=causasnoconformidad%>'  />
								 	                    <select name="cbo_causasnoconformidad" size="12" style="width:100%;height:283px" multiple="multiple" onchange="selectOptions(this,this.form.causasnoconformidad)">
								 	                    	<%if(listPossibleCause!=null) {
								 	                    	for(int i=0; i<listPossibleCause.size();i++) {
								 	                    		possibleTO = (PossibleCauseTO)listPossibleCause.get(i);%>
								 	                    		<option value="<%=possibleTO.getIdPossibleCause()%>" <%=causasnoconformidad.indexOf(",".concat(possibleTO.getIdPossibleCause()).concat(","))!=-1?"selected":""%> >
								 	                    		<%=possibleTO.getDescPossibleCause()%>
								 	                    		</option>
								 	                    	<%}
								 	                    	}%>
								 	                    </select>
								 	                    <br/>
                                         			</td>
                                         		</tr>
                                         		<tr>
                                         			<td align="center" heigth="26" class="titleCenter" style="background: url(img/BgComments.gif); background-repeat: no-repeat;padding:2px;">
                                         				<%=rb.getString("scp.descriptionActionMainTitle")%>
                                         			</td>
                                         		</tr>
                                         		<tr>
                                         			<td>
                                         				<html:textarea property="descripcionAccionPrincipal"  rows='6' value="<%=actionMainDescription%>" style="height:120px;width:100%" />
			                                             <br />
			                                             <br />
                                         			</td>
                                         		</tr>                                         		
                                         	</table>
                                         </td>
                                      </tr>


                                     <tr>
                                        <td align="center" colspan="6" heigth="26" class="titleCenter" style="background: url(img/BgComments.gif); background-repeat: no-repeat;padding:2px;">
                                            <%=rb.getString("scp.tecnica")%>
                                        </td>
                                     </tr>
                        
                                     <tr>
                                         <td colspan="3" align="center" style="padding-top:3px;">
                                             <html:file property="archivoTecnica" />
                                             <br />
                                             <%
                                             if(! ToolsHTML.isEmptyOrNull(archivoTecnica)){
                                             %>
                                               <a href="javascript:showFileSacop('<%=(String)request.getAttribute("idplanillasacop") + "/" + archivoTecnica %>');">
                                                   <%= archivoTecnica %>
                                                   &nbsp;&nbsp;
                                                   <img src="img/pendings.gif" width="16" height="16" border="0">
                                                </a>
                                             <%
                                             }
                                             %>
                                             
                                         </td>
                                         <td colspan="2" style="padding-top:3px;">
                                         	<input type="button" class="boton" value="<%=rb.getString("scp.associateDocument")%>" onClick="agregar();"/>
                                         </td>
                                         <td >
                                         	
                                         </td>
                                      </tr>
                                 <tr>
                                 	<td colspan="7">
                                 		<input type="hidden" id="idDocumentAssociate" name="idDocumentAssociate" value="<%=request.getAttribute("idDocumentAssociate")%>" /><br>
                                 		<input type="hidden" id="numVerDocumentAssociate" name="numVerDocumentAssociate" value="<%=request.getAttribute("numVerDocumentAssociate")%>" />
                                 		<input type="hidden" id="nameDocumentAssociate" name="nameDocumentAssociate" value="<%=request.getAttribute("nameDocumentAssociate")%>" style="width:600px" readonly />
                                 		<a id="lnkNameDocumentAssociate" href="#" onclick="abrirDoc()">
                                 			<%=!String.valueOf(request.getAttribute("nameDocumentAssociate")).equals("null")?request.getAttribute("nameDocumentAssociate"):""%>
                                 		</a>
                                 	</td>
                                 </tr>
                                 <tr>
                                    <td colspan="6">
                                        <center>
                                           <!--Salva las causas de la no conformidad que el comentario general de las acciones -->
                                            &nbsp;
                                            <input type="button" class="boton" value="<%=rb.getString("btn.actualiza")%>" onClick="javascript:SaveGeneral(this.form);"/>
                                            &nbsp;
                                            <input type="hidden" name="deleteArchivoTecnica" value="0" />
                                            <%
                                             if(! ToolsHTML.isEmptyOrNull(archivoTecnica)){
                                             %>
                                                <input type="button" class="boton" style="width: 200px;" value="<%=rb.getString("scp.del.prev.archivo.tecnica")%>" onClick="javascript:DeleteArchivoTecnica(this.form);"/>
                                             <%
                                             }
                                             %>
                                   </logic:notPresent>
                                             &nbsp;
                                            <input type="button" class="boton" value="<%=rb.getString("scp.cerrar")%>" onClick="window.close();"/>
                                        </center>
                                    </td>
                                </tr>
                                
                                 
                                </tbody>
                            </table>
                    </td>
                </tr>
            </table>
            </html:form>
        </td>
    </tr>
<% }%>
</table>
</body>
</html>
