<!--**
 * Title: recordFiltersOld.jsp <br/>
 * Copyright: (c) 2007 Focus Consulting<br/>
 * @version WebDocuments v4.3
 * <br/>
 * Changes:<br/>
 * <ul>
 *      <li> 05-12-2007 (YSA) Creation </li>
 </ul>

 */-->


<%@ page import="java.util.ResourceBundle,
                 com.desige.webDocuments.utils.ToolsHTML,
                 com.desige.webDocuments.utils.beans.SuperActionForm"%>

<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<%@ page language="java" %>

<%
    ResourceBundle rb = ToolsHTML.getBundle(request);
    String onLoad = ToolsHTML.getMensajePages(request,rb,"initPage();");
    if (onLoad==null) {
        response.sendRedirect(rb.getString("href.logout"));
    }
%>
<html>
<head>
<title><%=rb.getString("principal.title")%></title>
<jsp:include page="meta.jsp" /> 
<link href="estilo/estilo.css" rel="stylesheet" type="text/css">
<script type="text/javascript" src="./estilo/funciones.js"></script>
<script type="text/javascript" src="./estilo/fechas.js"></script><%-- Luis Cisneros 23-02-07 --%>
<script type="text/javascript">

    function initPage() {
        //forma = document.forms[0];
        //if ((forma)&&(forma.nameDocument)) {
            //forma.nameDocument.focus();
        //}
    }
    
	function popUpIndicators(){
		setDimensionScreen();
		var width = "600";
		var height = "600"
	    var left = getPosition(winWidth,width);
	    var top = getPosition(winHeight,height);
        window.open("recordIndicators.jsp", "Indicadores", "resizable=no,scrollbars=yes,statusbar=yes,width="+width+",height="+height+",left="+left+",top="+top);
	}
	
    function viewIndicators(form) {
		if (validar(form)) {
			//var iframe = document.usersRecord;
			//var lista = iframe.selection.listUsers;
			document.location = "#tope";
			includeUsersIframe();
    		popUpIndicators();
        }
    }
	
	function includeUsers(){
		var list = "";
		iframe = document.usersRecord;
		lista = iframe.selection.listUsers;
		list = llenarValoresChecked(lista);
		//alert(list);
		if(document.recordForm.usersSelected.value.length>1 && list.length>0)
			list = "," + list;
		document.recordForm.usersSelected.value += list;
	}
	
	function includeUsersIframe(){
		var list = "";
		iframe = document.usersRecord;
		lista = iframe.selection.listUsers;
		//alert(lista.length);
		list = llenarValoresChecked(lista);
		//alert(list);
		//Si ya esta en la lista no se agrega
		//Si esta en los checked pero no seleccionado y esta en la lista se elimina
		//alert("hidden: " + document.recordForm.usersSelected.value);
		//alert("valores checked: " + list);
		if(document.recordForm.usersSelected.value.length>1 && list.length>0)
			list = "," + list;
		document.recordForm.usersSelected.value += list;
		//Depurar lista con los que aparecen en listUsers y no estan seleccionados
		depurarLista(lista);
	}

    function generateReport() {
    	document.getElementById("loading").style.visibility = "visible";
		var areas = llenarValores(document.recordForm.areasSelected);
		var cargos = llenarValores(document.recordForm.chargesSelected);
        document.recordForm.areasSel.value = areas;
        document.recordForm.chargesSel.value = cargos;

        document.recordForm.target = "_self";
        document.recordForm.action = "generateReportRecord.do";
        document.location = "#tope";
        document.recordForm.submit();
    }

    function updateCheck(check,field)  {
        if (check.checked) {
            field.value = "0";
        } else{
            field.value = "1";
        }
    }
	
	function selectInterval(forma,valor){
		forma.intervalo.value = valor;
		if(valor=="4"){
			forma.btnDateFrom.disabled = "";
			forma.btnDateTo.disabled = "";
			forma.btnCleanFrom.disabled = "";
			forma.btnCleanTo.disabled = "";
		}else{
			forma.btnDateFrom.disabled = "disabled";
			forma.btnDateTo.disabled = "disabled";
			forma.btnCleanFrom.disabled = "disabled";
			forma.btnCleanTo.disabled = "disabled";
		}
	}
	
    function validar(forma) {
	    if(!forma.recordPromedioI.checked && !forma.recordPorUsuarioI.checked && !forma.medianaGeneralI.checked){
	    	alert('<%=rb.getString("rcd.selectOption")%>');
	    	return false;
	    }
	    
        if (forma.desde.value.length != 0 && forma.hasta.value.length != 0 ){
		    //alert(forma.desde.value+ "  " + getDate(forma.desde.value));
		    //alert(forma.hasta.value+ "  " + getDate(forma.hasta.value));
            //if (getDate(forma.desde.value) > getDate(forma.hasta.value) ) {
            if (forma.desde.value > forma.hasta.value) {
                alert('<%=rb.getString("sch.dateFromGreaterThanTo")%>');   
                return false;
            }
        }
        
        //La fecha hasta debe ser menor a hoy
        if (forma.hasta.value.length != 0 ){
            var today = new Date();
		    var	dateNow	 = today.getDate();
			var	monthNow = today.getMonth()+1;
			var	yearNow	 = today.getYear();
				
			var hoy = yearNow + '-' + monthNow + '-' + dateNow;
            if (forma.hasta.value > hoy) {
                alert('<%=rb.getString("sch.dateToGreaterThanToday")%>');                
                return false;
            }
        }

        //es necesario copiar en el hidden el valor, ya que como el campo visible es readonly y disabled no se envía al form.             
        forma.desdeTime.value = forma.desde.value;
        forma.hastaTime.value = forma.hasta.value;    

    	if(forma.recordPromedioI.checked) forma.recordPromedio.value = "1";
    	if(forma.recordPorUsuarioI.checked) forma.recordPorUsuario.value = "1";
    	if(forma.medianaGeneralI.checked) forma.medianaGeneral.value = "1";
    
        /*if (form.nameDocument.value.length==0) {
            alert('<%=rb.getString("err.notNameFile")%>');
            return false;
        }*/

        return true;
    }

    function getDate(dateField,nameForm,dateValue,text){
        window.open("getDate.jsp?field=" + dateField + "&nameForm="+nameForm+"&valueForm="+dateValue+"&text="+text,"","width=275,height=170,resizable=no,scrollbars=yes,left=460,top=250");
    }
    
	function createElement(value,texto){
		var newItem = document.createElement("OPTION");
		newItem.value = value;
		newItem.text = texto;
		return newItem;
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
	
	function createListSelecteds(list){
		listaNew = document.createElement("SELECT");
		if (list){
			var items = list.length;
			var valor;
			for (row = 0; row < items; row++){
				var valor = list[row];
				if (valor.selected && valor.value!=""){
	            	newItem = createElement(list[row].value,list[row].text);
					listaNew.options[listaNew.length]=newItem;
				}
				valor.selected = false;
			}
		}
		return listaNew;	
	}
	
	function existe(campo,listIzq){
		var items = listIzq.length;
		var existe = false;
		if (items > 0){
			for (row = 0; row < items; row++){
				var valor = listIzq[row];
				if (campo==valor.value) {
                    existe = true;
                    break;
				}
			}
		}
		return existe;
	}
	
	function moverDerIzq(listDer,listIzq){
		var listIzqIni = listIzq;
		var selecteds = contarItemsSelecteds(listDer);
		var listSelecteds = createListSelecteds(listDer);
		if (selecteds > 0){
			var	newItem;
			for (row = 0; row < listSelecteds.length; row++){
				var valor = listSelecteds[row];
				if (valor.value!="") {
					if(!existe(valor.value,listIzqIni)){
	                    newItem = createElement(valor.value,valor.text);
						listIzq.options[listIzq.length]=newItem;
					}
				}
				valor.selected = false;
			}
			//removerDeLista(listDer,items);
		}
	}
	
	function mover(listDer,listIzq){
		var items = listDer.length;
		var selecteds = contarItemsSelecteds(listDer);
		if (selecteds > 0){
			removerDeLista(listDer,items);
		}
	}

	function removerDeLista(lista,items){
		if (lista){
            var row = 0;
			for (row = items - 1; row >= 0; row--){
				var valor = lista[row];
				if (valor.selected && valor.value!=""){
					lista.remove(row);
				}
			}
		}
	}    
	
	function limpiarLista(lista){
		if (lista){
            var row = 0;
			for (row = lista.length; row >= 0; row--){
				lista.remove(row);
			}
		}
	}  	
	
	function disabledOption(){
		//alert(document.recordForm.charges.options[0].value);
		document.recordForm.charges.disabled=true;
		document.recordForm.charges.options[0].selected = false;
	}
	
	function llenarValores(lista){
		var resultado = "";
		if (lista){
            for (row = 0; row < lista.length; row++){
				var valor = lista[row];
				resultado += valor.value + ","; 
			}
		}		
		if(resultado.length>0)
			resultado = resultado.substring(0,resultado.length-1);
		return resultado;
	}

	function llenarValoresChecked(lista){
		var resultado = "";
		
		//iframe = document.usersRecord;
		//lista = iframe.selection.listUsers;
		
		//alert("llenarValoresChecked lista:" + lista.length);
		if (lista){
            for (row = 0; row < lista.length; row++){
				var valor = lista[row];
				if(valor.checked && !existeLista(valor.value)){
					//alert("llenarValoresChecked revisa si valor esta checked: " + valor.value);
					resultado += valor.value + ","; 
				}
			}
		}		
		if(resultado.length>0)
			resultado = resultado.substring(0,resultado.length-1);
		return resultado;
	}
		
	function existeLista(valor){
		var existe = false;
		var listaParent = document.recordForm.usersSelected.value;
		//alert(listaParent);
		if(listaParent!=""){
			var tokens = listaParent.tokenize(",", " ", true);
	
			for(var i=0; i<tokens.length; i++){
				if(valor==tokens[i]){
					existe = true;
				}
		  	}
	    }
	  	return existe;		
	}
	
	function depurarLista(lista){
		var newValorHidden = "";
		var valorHidden = document.recordForm.usersSelected.value;
		//alert("estoy en depurarLista");
		//alert("valorHidden: " + document.recordForm.usersSelected.value);
		//alert("lista: " + lista);
		
		if(valorHidden!=""){
			var tokens = valorHidden.tokenize(",", " ", true);
	
			for(var i=0; i<tokens.length; i++){
				if(existeEnListaChecked(tokens[i])){
					newValorHidden += tokens[i] + ",";
				}
		  	}
		  	
		if(newValorHidden.length>0)
			newValorHidden = newValorHidden.substring(0,newValorHidden.length-1);
	    }
		document.recordForm.usersSelected.value = newValorHidden;
	}
	
	function existeEnListaChecked(valor){
		var existeEnListaChecked = true;
		var iframe = document.usersRecord;
		var lista = iframe.selection.listUsers;
		//alert("reviso: " + valor);
		if(lista){
			for(var row=0; row < lista.length; row ++){
				//Existe y no esta deleccionado
				//alert("contra: " + lista[row].value);
				//alert("lista[row].checked: " + lista[row].checked);
				if(valor==lista[row].value && !lista[row].checked){
					//alert("existe en lista pero no seleccionado ");
					existeEnListaChecked = false;
					break;
				}
			}
		}
		return existeEnListaChecked;
	}
	
	var http = false;
	
    function verCargos() {
    	var areas = llenarValores(document.recordForm.areasSelected);
		if(navigator.appName == "Microsoft Internet Explorer") {
		  http = new ActiveXObject("Microsoft.XMLHTTP");
		} else {
		  http = new XMLHttpRequest();
		}
		http.open("POST", "markFiltersRecordAjax.do?schCharges=true&areas="+areas);
		http.onreadystatechange=function() {
		  if(http.readyState == 4) { // Complete
		  	if (http.status == 200) { // OK response
			    //obtenemos el XML
	  			parseCargos(http.responseXML.documentElement);
		  	}
		  }
		}
		http.send(null);
    }	
    
	function parseCargos(response){
		limpiarLista(document.recordForm.chargesSelected);
	    limpiarLista(document.recordForm.charges);
	    //obtenemos de HTML la zona donde se escribe el resultado
		var selectCharges=document.getElementById("cargos");
		var cargos=response.getElementsByTagName("cargo");
		selectCharges.options.length=cargos.length;
		for(i=0;i<cargos.length;i++){
			var car=cargos[i];
			selectCharges.options[i].value=car.getAttribute("id");
			selectCharges.options[i].innerHTML=car.firstChild.data;
		}
    }

	function verUsuarios(from) {
		includeUsersIframe();
		var areas = llenarValores(document.recordForm.areasSelected);
		var cargos = llenarValores(document.recordForm.chargesSelected);
		document.usersRecord.location.href = "usersRecord.do?cargos="+cargos+"&areas="+areas+"&from="+from;
	}

	// resizes Iframe according to content
	function resizeMe(obj){ 
		docHeight = usersRecord.document.body.scrollHeight;
 		obj.style.height = docHeight + 'px';
 	} 
 	
 	// :JR:
    function permission(type,nodeType) {
    	  var forma = document.getElementById("Selection");
          forma.action = "loadAllUserRecord.do";
          if (forma.nodeType) {
              forma.nodeType.value = nodeType;
          }
          if (type==1) {
              forma.action = "loadAllGroupsRecord.do";
          }
          if ((parent.frames['text'])||(parent.frames['opciones'])) {
              forma.target = "_self";
          }
          forma.submit();
      }
 	
</script>
</head>
<body class="bodyInternas" <%=onLoad%>>
        <form name="Selection" id="Selection" action="loadStructMain.do" method="post">
            <input type="hidden" name="idNode" value='4'/>
            <input type="hidden" name="idNodeSelected" value='4'/>
            <input type="hidden" name="idNodeChange" value=''/>
            <input type="hidden" name="isCopy" value=''/>
            <input type="hidden" name="movDocument" value='0'/>
            <input type="hidden" name="cmd"/>
            <input type="hidden" name="nexPage" value="loadStructMain.do"/>
            <input type="hidden" name="idDocument" value=''/>
            <input type="hidden" name="idVersion" value=""/>
            <input type="hidden" name="toStruct" value="0"/>
            <input type="hidden" name="expandir" value=""/>
            <input type="hidden" name="nodeType" value=""/>
            <input type="hidden" name="securityPorStructura" value="true"/>
        </form>
<html:form action="generateReportRecord.do">
<html:hidden property="indicatorsSelected" value=""/>
<html:hidden property="usersSelected" value=""/>
<html:hidden property="areasSel" value=""/>
<html:hidden property="chargesSel" value=""/>

<bean:define id ="dataRecord" name="recordForm" type="com.desige.webDocuments.record.forms.RecordFiltersForm" scope="session"/>

<logic:present name="securityRecord">
	<logic:equal name="securityRecord" property="toUpdate"  value="1">
	<table class="clsTableTitle" width="100%" cellSpacing=0 cellPadding=2 align=center border=0>
	   <tbody>
	       <tr>
	           <td class="td_title_bc" height="21 px">
	               <%=rb.getString("seguridad.permision")%>
	           </td>
	       </tr>
	   </tbody>
	</table>
	<table width="100%" align=center border=0 cellSpacing=0 cellPadding=0>
	   <tr>
	       <td style="background: url(img/BgComments.gif); background-repeat: no-repeat" height="26">
	           <table width="100%" border="0" cellspacing="0" cellpadding="0">
	               <tr>
				       <td width="49.5%" align='center'>
				       		<a class="ahreMenuInner" href='javascript:permission(0,3);' target='_self'><%=rb.getString("btn.permission")%></a>
				       </td>
			           <td width='1%'>
			               <img src='img/separadorInternas.gif' width='1' height='10'/>
			           </td>
				       <td width="49.5%" align='center'> 
				       		<a class="ahreMenuInner" href='javascript:permission(1,3);' target='_self'><%=rb.getString("btn.permission1")%></a>
				       </td>
	              </tr>
	           </table>
	       </td>
	   </tr>
	</table>
	</logic:equal>
</logic:present>

<table align=center border=0 width="100%">
	<tr>
		<td valign="top" class="pagesTitle">
			<%=rb.getString("rcd.title")%>
		</td>
	</tr>
	<tr>
		<td>
            <table cellSpacing=0 cellPadding=0 align=center border=0 width="100%">
                <tr>
                    <td width="100%">
                        <table class="clsTableTitle" width="100%" cellSpacing=0 cellPadding=2 align=center border=0>
                            <tr>
                                <td class="td_title_bc" height="22 px" width="*">
                                    <%=rb.getString("rcd.filters")%>
                                </td>
                                <td class="td_title_bc" width="250">
                                    <%=rb.getString("rcd.options")%>
                                </td>
                            </tr>
                        </table>
                    </td>
                </tr>
                <tr>
                    <td height="4"></td>
                </tr>
                <tr>
                    <td>
                    	<!-- FILTROS -->
                        <table cellSpacing=0 cellPadding=0 align=center border=0 width="100%">
                            <tr>
                                <td width="*" valign="top">
                                <!--Filtros areas / cargos-->
                                   	<table width="100%" cellSpacing=0 cellPadding=2 align=center border=0>
                                   	<tbody>
                                      	<tr>
	                                     	<td class="clsTableTitle" align="center" colspan="4">
                                            	<%=rb.getString("rcd.filter1")%>
                                            </td>
                                        </tr>
                                      	<tr>
                                            <td width="40%" align="center">
                                          		<select name="areas" size="5" multiple style="width:240px;" styleClass="classText">
                                           			<logic:present name="areas" scope="session">
                                                   		<logic:iterate id="bean" name="areas" scope="session" type="com.desige.webDocuments.utils.beans.Search">
                                                     		<option value="<%=bean.getId()%>"><%=bean.getDescript()%></option>
                                                   		</logic:iterate>
                                             		</logic:present>
                                             	</select>
                                            </td>
                                            <td width="5%" align="center">
                                            	<input type="button" value=">" onClick="javascript:moverDerIzq(document.recordForm.areas,document.recordForm.areasSelected);" class="BUTTON" style="width:28px;">
                                                <br/>
                                            	<input type="button" value="<" onClick="javascript:mover(document.recordForm.areasSelected,document.recordForm.areas);" class="BUTTON" style="width:28px;">
                                            </td>
                                            <td width="40%" align="center">
                                            	<html:select property="areasSelected" multiple="true" style="width:240px;" size="5" styleClass="classText">
                                             	</html:select>
                                            </td>
                                            <td width="15%" align="center">
                                            	<input type="button" name="btnViewCharges" onclick="javascript:verCargos();" value='<%=rb.getString("rcd.viewCharges")%>' class="boton" style="width:80px;">              
                                            </td>
                                        </tr>
                                      	<tr>
	                                     	<td class="clsTableTitle" align="center" colspan="4">
                                            	<%=rb.getString("rcd.filter2")%>
                                            </td>
                                        </tr>
                                      	<tr>
                                            <td width="40%" align="center">
                                           		<%String ultArea="";%>
                                             	<select name="charges" size="5" multiple style="width:240px;" styleClass="classText" id="cargos">
                                                	<logic:present name="cargos" scope="session">
                                                    	<logic:iterate id="bean" name="cargos" scope="session" type="com.desige.webDocuments.utils.beans.Search">
                                                    		<%
                                                    		String ultArea2=bean.getAditionalInfo();
                                                    		if (!ultArea2.equals(ultArea)){
	                                                       		ultArea = ultArea2;
                                                        	%>
                                                        		<option value="" onclick="javascript:disabledOption();" style="ahreMenu">--- <%=ultArea%> ---</option>
                                                        	<%}%>
                                                        		<option value="<%=bean.getId()%>"><%=bean.getDescript()%></option>
                                                        </logic:iterate>
                                                    </logic:present>
                                                </select>
                                            </td>
                                            <td width="5%" align="center">
                                            	<input type="button" value=">" onClick="javascript:moverDerIzq(document.recordForm.charges,document.recordForm.chargesSelected);" class="BUTTON" style="width:28px;">
                                                <br/>
                                            	<input type="button" value="<" onClick="javascript:mover(document.recordForm.chargesSelected,document.recordForm.charges);" class="BUTTON" style="width:28px;">
                                            </td>
                                            <td width="40%" align="center">
                                               	<html:select property="chargesSelected" multiple="true" style="width:240px;" size="5" styleClass="classText">
                                            	</html:select>
                                            </td>
                                            <td width="15%" align="center">
                                            	<input type="button" name="btnViewUsers" onclick="javascript:verUsuarios(0);" value='<%=rb.getString("rcd.viewUsers")%>' class="boton" style="width:80px;">              
                                            </td>
                                        </tr>
                                    </tbody>     
                                   	</table>
                                <!--FIN Filtros areas / cargos-->
		                        </td>

                                <td width="250" valign="top" class="td_title_bc" align="left" valign="top">
                            	<!-- OPCIONES -->
                                    <div align="left">
                                    <%
                                        boolean editable = false;
                                    %>
                                        <%=ToolsHTML.showCheckBox("recordPromedioI","updateCheck(this.form.recordPromedioI,this.form.recordPromedio)",
                                                                  dataRecord.getRecordPorUsuario(),1,editable)%><%=rb.getString("rcd.option1")%>
                                        <html:hidden property="recordPromedio" value="<%=String.valueOf(dataRecord.getRecordPromedio())%>"/>
                                        <br/>
                                        <%=ToolsHTML.showCheckBox("recordPorUsuarioI","updateCheck(this.form.recordPorUsuarioI,this.form.recordPorUsuario)",
                                                                  dataRecord.getRecordPorUsuario(),1,editable)%><%=rb.getString("rcd.option2")%>
                                        <html:hidden property="recordPorUsuario" value="<%=String.valueOf(dataRecord.getRecordPromedio())%>"/>
                                        <br/>
                                        <%=ToolsHTML.showCheckBox("medianaGeneralI","updateCheck(this.form.medianaGeneralI,this.form.medianaGeneral)",
                                                                  dataRecord.getMedianaGeneral(),1,editable)%><%=rb.getString("rcd.option3")%>
                                        <html:hidden property="medianaGeneral" value="<%=String.valueOf(dataRecord.getRecordPromedio())%>"/>
                                 	</div>
									<div align="center"><br/><%=rb.getString("rcd.time")%><br/></div>
									<div align="left">	
										<html:hidden property="intervalo" value="<%=String.valueOf(dataRecord.getIntervalo())%>"/>						
							            <%=ToolsHTML.getRadioButton("intervaloI",dataRecord.getIntervalo(),1,"selectInterval(this.form,'1')")%><%=rb.getString("rcd.week")%>
                                        <br/>
                                        <%=ToolsHTML.getRadioButton("intervaloI",dataRecord.getIntervalo(),2,"selectInterval(this.form,'2')")%><%=rb.getString("rcd.month")%>
                                        <br/>
                                        <%=ToolsHTML.getRadioButton("intervaloI",dataRecord.getIntervalo(),3,"selectInterval(this.form,'3')")%><%=rb.getString("rcd.year")%>
                                        <br/>
                                        <%=ToolsHTML.getRadioButton("intervaloI",dataRecord.getIntervalo(),4,"selectInterval(this.form,'4')")%><%=rb.getString("rcd.other")%>
                                    </div>	
									<table width="100%" cellSpacing=0 cellPadding=2 align=center border=0>
	    	                        	<tr>
			                         		<td class="td_title_bc" align="left" valign="top">
												<%=rb.getString("rcd.from")%>
			                					<input type="hidden" name="desdeTime" value=""></input>
			           							<input type="text" name="desde" readonly disabled maxlength="10" size="10" value="<%=String.valueOf(dataRecord.getDesde()) == null ?"":String.valueOf(dataRecord.getDesde())%>" />
			            						<input type="button" name="btnDateFrom" onclick='getDate("desde",this.form.name,this.form.desde.value,"sch.from")' value='>>' class="boton" style="width:20px;"></input>
												<input type="button" name="btnCleanFrom" onclick='this.form.desdeTime.value=""; this.form.desde.value="";' value='<%=rb.getString("btn.clear")%>' class="boton" style="width:60px;">              
			                         		</td>
		                         		</tr>
	    	                        	<tr>
			                              	<td class="td_title_bc" align="left" valign="top">
												<%=rb.getString("rcd.to")%>
			                            		<input type="hidden" name="hastaTime" value=""></input>
			                            		<input type="text" name="hasta" readonly disabled maxlength="10" size="10"  value="<%=String.valueOf(dataRecord.getHasta()) == null ?"":String.valueOf(dataRecord.getHasta())%>" />
			                            		<input type="button" name="btnDateTo" onclick='getDate("hasta",this.form.name,this.form.hasta.value,"sch.to")' value='>>' class="boton" style="width:20px;"></input>
												<input type="button" name="btnCleanTo" onclick='this.form.hastaTime.value=""; this.form.hasta.value="";' value='<%=rb.getString("btn.clear")%>' class="boton" style="width:60px;">
			                             	</td>
		                            	</tr>
									</table>		                                     	
                            	<!-- FIN OPCIONES -->
		                        </td>
        		            </tr>
           				</table>
                    	<!-- FIN FILTROS -->
	                </td>
    		    </tr>   
                <tr>
                    <td width="100%">
	                    <!-- USUARIOS -->
                        <table class="clsTableTitle" width="100%" cellSpacing=0 cellPadding=2 align=center border=0>
                            <tr>
                                <td class="td_title_bc" height="22 px" width="*">
                                    <%=rb.getString("rcd.users")%>
                                </td>
                            </tr>
                        </table>
	                    <!-- FIN USUARIOS -->
                    </td>
                </tr>   
                <tr>
                	<td width="100%">
	                	<a name="tope"></a>
						<span id="listUsers">
						<iframe name="usersRecord" id="usersRecord" width="100%" frameborder="0" align="top" scrolling="auto" height="10" src="usersRecord.do" onLoad="resizeMe(this)"></iframe>
						</span>
						<br>
                	</td>
                </tr> 		          
                <tr>
                	<td width="100%" align="center">
	                	<br>
	                	<input type="button" name="btnIndicators" onclick="javascript:viewIndicators(this.form);" value='<%=rb.getString("rcd.viewIndicators")%>' class="boton">              
                	</td>
                </tr> 		          
        	</table>
		</td>
	</tr>
</table>
<div id="loading" align="center" style="visibility: hidden; position: absolute; top: 400px; left: 0px; width: 98%; z-index: 1;">
<table border="0" align="center" width="430" bgcolor="#FFFFFF">
	<tr>
		<td width="60" align="center">
			<img src="img/loader1.gif" border="0">
		</td>
		<td valign="middle" class="td_title_bc">
			<font size="2">
			 <%=rb.getString("showMSG.loading")%>
			</font>
		</td>
	</tr>
</table>
</div>  
<script>
	document.getElementById("loading").style.visibility = "hidden";
	selectInterval(document.recordForm,"<%=dataRecord.getIntervalo()%>");
</script>
</html:form>
</body>
</html>
