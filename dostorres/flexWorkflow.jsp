<jsp:include page="richeditDocType.jsp" /> 
<!--/**
 * Title: flexWorkflow.jsp <br/>
 * Copyright: (c) 2004 Desige Servicios de Computación<br/>
 *
 * @author Ing. Nelsón Crespo.(NC)
 * @version WebDocuments v1.0
 * <br/>
 *          Changes:<br/>
 * <ul>
 *          <li> 13/12/2005 (NC) Creation </li>
 * </ul>
 */-->
<%@ page import="java.util.ResourceBundle,
                 com.desige.webDocuments.utils.ToolsHTML,
                 com.desige.webDocuments.utils.beans.SuperActionForm,
                 com.desige.webDocuments.utils.beans.Users"%>

<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<%@ page language="java" %>
<%
	
    ResourceBundle rb = ToolsHTML.getBundle(request);
    String nodeActive = (String)session.getAttribute("nodeActive");
    String idDocument = (String)request.getAttribute("idDocument");
    Long dateDeadDocumentTime = (Long) session.getAttribute("dateDeadDocTimeFlex");

    String sizeSub = "0";
    String nameViewer = "";
    if (session.getAttribute("sizeSub")!=null) {
        sizeSub = (String)session.getAttribute("sizeSub");
    }
    int indice = 0;
    try {
	    indice = Integer.parseInt((String)session.getAttribute("posAct"));
    } catch(Exception e) {
    	e.printStackTrace();
    }

%>
<html>
<head>
<title><%=rb.getString("principal.title")%></title>
<jsp:include page="meta.jsp" /> 
<link href="estilo/estilo.css" rel="stylesheet" type="text/css">
<script type="text/javascript" src="./estilo/popcalendar2inner.js"></script>
<script type="text/javascript" src="./estilo/funciones.js"></script>
<script type="text/javascript" src="./estilo/fechas.js"></script>
<%--<script language=javascript src="./estilo/fWorkFlows.js"></script>--%>
<jsp:include page="richeditHead.jsp" /> 

<script type="text/javascript">
    var listaSeleccionados;
    setDimensionScreen();
    function botones(forma) {
        if (forma.btnOK) {
            forma.btnOK.disabled = true;
        }
        if (forma.btnCancel) {
            forma.btnCancel.disabled = true;
        }
    }
    // Función utilizada para limpiar la lista indicada
    function clear(lista){
		var nodos = lista.length;
        var row = 0;
		if (nodos && nodos > 0) {
			for (row = nodos - 1 ; row >= 0; row--) {
				lista.remove(row);
			}
		}
	}
    function cancelar() {
    	var form = document.getElementById("Selection");
        form.action = "showDataDocument.do";
        form.cmd.value="<%=SuperActionForm.cmdLoad%>";
        form.target = "_parent";
        form.submit();
    }

    function validar(forma) {
        if (forma.expireI&&forma.expireI.checked) {
            var mydate = new Date();
            var year = mydate.getYear();
            if (year < 1000)
                year += 1900;
            var day = mydate.getDay();
            var month = mydate.getMonth() + 1;
            if (month < 10) {
                month = "0" + month;
            }
            var daym = mydate.getDate();
            if (daym < 10) {
                daym = "0" + daym;
            }
            var fecSystem = year + "-" + month + "-" + daym;
            if (!validarFecha(forma.dateExpireWF.value)||(fecSystem >= forma.dateExpireWF.value)) {
                alert("<%=rb.getString("wf.badDate")%>" + ' <%=rb.getString("act.ActTitle")%> ' + forma.nameAct.value);
                forma.expire.value = "1";
                forma.expireI.checked = false;
                forma.dateExpireWF.value = "";
                return false;
            }
            
            // validamos que la fecha de vencimiento del flujo
            // sea mayor a la del documento
            var dateDocDead = new Date(<%=dateDeadDocumentTime%>);
            var dateWorkDead = getDate(forma.dateExpireWF.value);
            if(dateDocDead.getTime()<dateWorkDead.getTime()) {
            	alert("<%=rb.getString("wf.badDateEraser")%>");
            	return false;
            }
            
        }
        grupos = contSelectInList(forma.groupsSelected);
        if ((contSelectInList(forma.usersSelected)==0)&&(grupos==0)) {
            alert("<%=rb.getString("err.notUserInWF")%>" + ' <%=rb.getString("act.ActTitle")%> ' + forma.nameAct.value);
            return false;
        }
        return true;
    }

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

    function salvar(forma) {
        var row = 0;
        if (validar(forma)) {
            nForms = parseInt(forma.cuantos.value);
            for (formas = 0; formas < nForms; formas++) {
                var lista = forma.groupsSelected;
                var items = lista.length;
                row = 0;
                var groups = false;
                for (row = 0; row < items ; row++) {
                    var valor = lista[row];
                    valor.selected = true;
                    groups = true;
                }
                //Validando Usuarios
                lista = forma.usersSelected;
                lista2 = forma.usersSelectedExpire;
                items = lista.length;
                var users = false;
                for (row = 0; row < items ; row++) {
                    var valor = lista[row];
                    valor.selected = true;
		            try {
				        if(document.newWF.secuentialI.checked && document.newWF.expireI.checked) {
				            lista2[row].selected = true; // seleccionamos las fechas
				        }
		            } catch(e){}
                    users = true;
                }
                updateRTEs();
                forma.comments.value = forma.richedit.value;
		        var texto = forma.comments.value.replace(/&nbsp;/g,"").replace(/<br>/g,"").replace(/^\s*|\s*$/g,"");
    		    if (texto.length == 0) {
                    alert("<%=rb.getString("E0027")%>" + ' <%=rb.getString("act.ActTitle")%> ' + forma.nameAct.value);
                    return false;
                }
            }
            
            botones(forma);
<%--            forma.action = "saveFlexFlow";--%>

            forma.submit();
        }
    }
    function updateCheck(check,field) {
        if (check.checked) {
            field.value = 0;
        } else{
            field.value = 1;
        }
		showFecha();
    }
    
    function updateCheckEli(check,field) {
        if (check.checked) {
            field.value = 1;
        } else{
            field.value = 0;
        }
    }
    
    //ydavila Ticket 001-00-003023
        function updateCheckCamb(check,field) {
        if (check.checked) {
            field.value = 1;
        } else{
            field.value = 0;
        }
    }
    
    function getDateExpire(check,field,dateField,nameForm,dateValue) {
        if (check.checked) {
            field.value = "0";
			showCalendar(document.newWF.dateExpireWF,"calendario","calculateExpire()");
        } else{
            eval("document.newWF."+dateField+".value=''");
            field.value = 1;
        }
		showFecha();
    }
    
	function calculateExpire(row,add) {
			if(document.newWF.dateExpireWF.value!=''){
				var elSel = document.newWF.usersSelectedExpire;
				while (elSel.length > 0) {
				  elSel.remove(elSel.length - 1);
				}
				for(var i=0;i<document.newWF.usersSelected.length; i++) {
					x = document.newWF.usersSelectedExpire.options.length;
					document.newWF.usersSelectedExpire.options[x]=new Option(document.newWF.dateExpireWF.value,document.newWF.dateExpireWF.value);
				}
			} else {
				  var elSel = document.newWF.usersSelectedExpire;
				  while (elSel.length > 0) {
				    elSel.remove(elSel.length - 1);
				  }
			}
	}	
	
	
    
    
    function getDateExpireUser(item) {
		showCalendar(document.newWF.dateExpireUserWF,"calendario2","cambiarFecha()");
    }
    
    function cambiarFecha() {
    	var hoy = new Date();
    	actual = hoy.getYear()+"-"+cero(hoy.getMonth()+1,2)+"-"+cero(hoy.getDate(),2);
    	var fec=document.newWF.dateExpireUserWF.value;
    	var ind = document.newWF.usersSelectedExpire.selectedIndex;
    	if((ind-1)>=0){ // comparamos que sea mayor a la fecha anterior
    		var fecAnt=document.newWF.usersSelectedExpire.options[ind-1].value;
    		if(fec<fecAnt || fec<actual) {
    			alert("Fecha fuera de rango.");
    			return;
    		}
    	}
    	if((ind+1)<document.newWF.usersSelectedExpire.length){ // comparamos que sea mayor a la fecha anterior
    		var fecSig=document.newWF.usersSelectedExpire.options[ind+1].value;
    		if(fec>fecSig || fec<actual) {
    			alert("Fecha fuera de rango.");
    			return;
    		}
    	}
    	if(ind>=0) {
	    	document.newWF.usersSelectedExpire.options[ind].value=fec;
	    	document.newWF.usersSelectedExpire.options[ind].text=fec;
	    	if(ind==document.newWF.usersSelectedExpire.length-1) {
		    		document.newWF.dateExpireWF.value=fec;
	    	}
    	}
    }
    
    function contarItemsSelecteds(lista){
		if (lista){
			var items = lista.length;
			var valor;
			var cont = 0;
            var row = 0;
			for (row = 0; row < items; row++){
				valor = lista[row];
				if (valor.selected){
					cont++;
				}
			}
			return cont;
		}
		return 0;
	}
	function mover(listDer,listIzq,add) {
		var items = listDer.length;
		var selecteds = contarItemsSelecteds(listDer);
		if (selecteds > 0){
			var	newItem;
			for (row = 0; row < items; row++){
				var valor = listDer[row];
				if (valor.selected) {
                    newItem = createElement(listDer[row].value,listDer[row].text);
					listIzq.options[listIzq.length] = newItem;
					if(add==1) {
						x = document.newWF.usersSelectedExpire.options.length;
						document.newWF.usersSelectedExpire.options[x]=new Option(document.newWF.dateExpireWF.value,document.newWF.dateExpireWF.value);
					}
				}
			}
			removerDeLista(listDer,items,add);
		}
	}
	
	function removerDeLista(lista,items,add) {
		if (lista){
            var row = 0;
			for (row = items - 1; row >= 0; row--) {
				var valor = lista[row];
				if (valor.selected){
					lista.remove(row);
					if(add==2){
					    var elSel = document.newWF.usersSelectedExpire;
					    elSel.remove(row);
					}
				}
			}
		}
	}
	function verificar(lista) {
		var items = lista.length;
		var resp = false;
		if (lista && items > 0) {
            var row = 0;
			listaSeleccionados = new Array(items);
			for (row = items - 1 ; row >= 0; row--) {
				var valor = lista[row];
				if (valor.selected) {
					listaSeleccionados.push(row);
					resp = true;
				}
			}
		}
		return resp;
	}
    function down(lista,notSeleteds,itemsSelecteds) {
        //04 DE JULIO 2005 INICIO SIMON
	    var selecteds = contarItemsSelecteds(lista);
        if (selecteds > 0) {
            //04 DE JULIO 2005 FIN SIMON
            clear(notSeleteds);
            clear(itemsSelecteds);
            var items = lista.length;
            checked = false;
            for (row = 0; row < items; row++) {
                var valor = lista[row];
                if (!valor.selected) {
                    if (!checked) {
                        newItem = createElement(lista[row].value,lista[row].text);
                        notSeleteds.options[notSeleteds.length]=newItem;
                    } else {
                        newItem = createElement(lista[row].value,lista[row].text);
                        notSeleteds.options[notSeleteds.length]=newItem;
                        changeNodes(itemsSelecteds,notSeleteds);
                        clear(itemsSelecteds);
                        checked = false;
                    }
                } else {
                    newItem = createElement(lista[row].value,lista[row].text);
                    itemsSelecteds.options[itemsSelecteds.length]=newItem;
                    if (lista[row+1]) {
                        if (!lista[row+1].selected) {
                            checked = true;
                        }
                    }
                }
            }
            if (itemsSelecteds!=null&&itemsSelecteds.length > 0) {
                changeNodes(itemsSelecteds,notSeleteds);
            }
            clear(lista);
            changeNodes(notSeleteds,lista);
        } else {
             alert('<%=rb.getString("wf.selectedLista")%>');
        }
     }
     function up(lista,notSeleteds,itemsSelecteds) {
        //04 DE JULIO 2005 INICIO SIMON
		var selecteds = contarItemsSelecteds(lista);
		if (selecteds > 0) {
            //04 DE JULIO 2005 FIN SIMON
            clear(notSeleteds);
            clear(itemsSelecteds);
            var items = lista.length;
            checked = false;
            for (row = 0; row < items; row++) {
                var valor = lista[row];
                if (!valor.selected) {
                    if (!checked) {
                        newItem = createElement(lista[row].value,lista[row].text);
                        notSeleteds.options[notSeleteds.length]=newItem;
                    } else {
                        changeNodes(notSeleteds,itemsSelecteds);
                        newItem = createElement(lista[row].value,lista[row].text);
                        itemsSelecteds.options[itemsSelecteds.length]=newItem;
                        clear(notSeleteds);
                        checked = false;
                    }
                } else {
                    itemsSelecteds.options[itemsSelecteds.length]=createElement(lista[row].value,lista[row].text);
                    if (lista[row+1]) {
                        if (!lista[row+1].selected) {
                            checked = true;
                        }
                    }
                }
            }
            if (itemsSelecteds!=null&&itemsSelecteds.length > 0) {
                changeNodes(notSeleteds,itemsSelecteds);
            }
            clear(lista);
            changeNodes(itemsSelecteds,lista);
        } else {
             alert('<%=rb.getString("wf.selectedLista")%>');
        }
    }
    function changeNodes(origen,destino) {
        var items = origen.length;
        var row = 0;
        for (row = 0 ; row < items ; row++) {
            destino.options[destino.length]=createElement(origen[row].value,origen[row].text);
        }
    }
	function createElement(value,texto){
		var newItem = document.createElement("OPTION");
		newItem.value = value;
		newItem.text = texto;
		return newItem;
	}
    function clearDate() {
        newWF.dateExpireWF.value = "";
        newWF.expireI.checked = false;
    }
    function changeForm(item) {
        forma = document.newWF;
        forma.action = "changeActFlex.do";
        updateRTEs();
        forma.comments.value = forma.richedit.value;
//        forma.comments.value.trim();
        forma.posNew.value = item;
        //Validando Usuarios
        var lista = forma.usersSelected;
        var lista2 = forma.usersSelectedExpire;
        var items = lista.length;
        var users = false;
        for (row = 0; row < items ; row++) {
            var valor = lista[row];
            valor.selected = true;
            try {
		        if(document.newWF.secuentialI.checked && document.newWF.expireI.checked) {
		            lista2[row].selected = true; // seleccionamos las fechas
		        }
            } catch(e){}
            users = true;
        }
        if(validar(forma)){
        	forma.submit();
        }
    }

    function chkUser(usrs,chk) {
        if (chk) {
            forma = document.newWF;
            if (forma.usrQuit) {
                forma.usrQuit.disabled = false;
            }
            var valores = usrs.split(",");
            //Validando Usuarios
            var lista = forma.usersSelected;
            var items = lista.length;
            var users = false;
            for (row = 0; row < items ; row++) {
                var valor = lista[row];
                if (valor.selected) {
                    for (pos = 0; pos < valores.length; pos++) {
                        if (valor.value == valores[pos]) {
                            if (forma.usrQuit) {
                                forma.usrQuit.disabled = true;
                            }
                            break;
                        }
                    }
                }
            }
        }
        // seleccionamos el combo de fechas
        //alert(document.newWF.usersSelected.selectedIndex);
        document.newWF.usersSelectedExpire.selectedIndex=document.newWF.usersSelected.selectedIndex;
        document.newWF.dateExpireUserWF.value=document.newWF.usersSelectedExpire.value;
        
		showFecha();
    }
    
    function showFecha() {
        if(document.newWF.secuentialI.checked && document.newWF.expireI.checked) {
			document.getElementById("tablaFecha").style.visibility="";
        } else {
			document.getElementById("tablaFecha").style.visibility="hidden";
        }
		try {
	        document.newWF.usersSelectedExpire.selectedIndex=document.newWF.usersSelected.selectedIndex;
	        document.newWF.dateExpireUserWF.value=document.newWF.usersSelectedExpire.value;
		}catch(e){
			document.newWF.dateExpireUserWF.value="";
		}
        if(!document.newWF.secuentialI.checked && document.newWF.expireI.checked) {
			calculateExpire();        
		}
    }
	function mostrarViewer(obj) {
		var sel = document.getElementById("users");
		var selView = document.getElementById("usersViewers");
		var selSele = document.newWF.usersSelected;

		var n = selView.options.length;
		var s = selSele.options.length;		
		if(obj.checked){
			for(var k=0; k<n; k++) {
				agregar = true;
				for(var x=0; x<s; x++) {
					if(selSele.options[x].value==selView.options[k].value) {
						agregar = false;
						break;
					}
				}
				if(agregar) {
					var op = new Option(selView.options[k].text+" (Viewer)",selView.options[k].value);
					sel.options[sel.options.length]=op;
				}
			}
		} else {
			for(var k=0; k<n; k++) {
				sel.value=selView.options[k].value;
				var items = sel.length;
				removerDeLista(sel,items);
			}
		}
	}    
</script>
</head>


<body class="bodyInternas">
<%--<logic:present name="showDocument">--%>
<%--    <bean:define id ="doc" name="showDocument" type="com.desige.webDocuments.document.forms.BaseDocumentForm" scope="session"/>--%>
    <bean:define id ="dataWF" name="newWF" type="com.desige.webDocuments.workFlows.forms.BaseWorkFlow" scope="session"/>

    <form name="Selection" id="Selection" action="loadStructMain.do">
        <input type="hidden" name="idNodeSelected" value="<%=nodeActive%>"/>
        <input type="hidden" name="cmd"/>
        <input type="hidden" name="nexPage" value="loadStructMain.do"/>
        <input type="hidden" name="idDocument" value="<%=idDocument%>"/>
        <input type="hidden" name="showStruct" value="true"/>        
    </form>

<%--    <html:form action="/saveWF.do">--%>
    <html:form action="/saveFlexFlow.do">
        <html:hidden property="typeWF"/>
        <html:hidden property="mayorVersionDocument"/>
        <html:hidden property="lastVersion"/>
        <!-- Datos de la Actividad -->
        <html:hidden property="nAct"/>
        <html:hidden property="subNumber"/>
        <html:hidden property="nameAct"/>
        <!-- Datos del Documento -->
        <input type="hidden" name="idDocument" value="<%=idDocument%>"/>
        <input type="hidden" name="posItem" value="<%=indice%>"/>
        <input type="hidden" name="posNew" value=""/>
        <input type="hidden" name="cuantos" value="<%=sizeSub%>">
        <input type="hidden" name="isSendToFlexWF" value="<%=( request.getAttribute("isSendToFlexWF")!=null ? request.getAttribute("isSendToFlexWF") : "false" )%>"/>        
                <table cellSpacing=0 cellPadding=0 align=center border=0 width="100%">
                    <tr>
                        <td width="100%">
                            <table class="clsTableTitle" width="100%" cellSpacing=0 cellPadding=2 align=center border=0>
                                <tr>
                                    <td class="td_title_bc" height="21 px" width="100%">
                                        <%=rb.getString("workFlow.titleII")%>&nbsp;<%=dataWF.getNameAct()%>
                                    </td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            &nbsp;
                        </td>
                    </tr>
                    <%
                        boolean editable = false;
                    %>
                    <logic:present name="changeWF">
                        <%
                            editable = true;
                        %>
                    </logic:present>
                    <tr>
                        <td width="100%">
                            <table cellSpacing=0 cellPadding=0 align=center border=0 width="100%">
                                <tr>
                                    <td width="100%" valign="top" >
                                        <table cellSpacing=0 cellPadding=0 align=center border=0 width="75%">
                                            <tr>
                                                <td width="60%" valign="top" class="td_title_bc" align="left">
                                                    <p align="left">

                                                    <!-- 11 DE JULIO 2005 SIMÓN INICIO-->
                                                     <logic:equal value="0" name="newWF" property="typeWF">
                                                          <logic:present name="statuelim" scope="session">
                                                              <logic:equal value="5" name="statuelim" scope="session">
                                                                   <%=ToolsHTML.showCheckBox("eliminarI","updateCheckEli(this.form.eliminarI,this.form.eliminar)",
                                                                                          dataWF.getEliminar(),1,false)%><%=rb.getString("btn.delete")%>
                                                                   <html:hidden property="eliminar" value="<%=String.valueOf(dataWF.getEliminar())%>"/>
                                                                  <br/>
                                                             </logic:equal>
                                                         </logic:present>
                                                     </logic:equal>
                                                        <%if(ToolsHTML.copyContents()){%>
            	                                            <%=ToolsHTML.showCheckBox("copyI","updateCheck(this.form.copyI,this.form.copy)",
                                                                                  dataWF.getCopy(),0,editable)%><%=rb.getString("cbs.copy")%>
	                                                        <input type="hidden" name="copy" value="<%=String.valueOf(dataWF.getCopy())%>">
        	                                                <br/>
                                                        <%} else {%>
    	                                                    <input type="hidden" name="copy" value="1">
                                                        <%}%>
                                                    <!-- 11 DE JULIO 2005 SIMÓN FIN-->
                                                        <table cellspacing="0"  border="0">
                                                        	<tr>
                                                        		<td colspan="2" align="left">
			                                                        <%=ToolsHTML.showCheckBox("secuentialI","updateCheck(this.form.secuentialI,this.form.secuential)",
			                                                                                  dataWF.getSecuential(),0,editable)%><%=rb.getString("wf.secuential")%>
			                                                        <input type="hidden" name="secuential" value="<%=String.valueOf(dataWF.getSecuential())%>">
			                                                        <!--<br/>-->
			                                                        <%--<%=ToolsHTML.showCheckBox("conditionalI","updateCheck(this.form.conditionalI,this.form.conditional)",--%>
			                                                                                  <%--dataWF.getConditional(),0,editable)%><%=rb.getString("wf.conditional")%>--%>
			                                                        <input type="hidden" name="conditional" value="0"/>
                                                        		</td>
                                                        	</tr>
                                                        	<tr>
                                                        		<td colspan="2" align="left">
			                                                        <%=ToolsHTML.showCheckBox("notifiedI","updateCheck(this.form.notifiedI,this.form.notified)",
			                                                                                  dataWF.getNotified(),0,editable)%><%=rb.getString("wf.notify")%>
			                                                        <input type="hidden" name="notified" value="<%=String.valueOf(dataWF.getNotified())%>"/>
                                                        		</td>
                                                        	</tr>
                                                        	<tr>
                                                        		<td align="left">
			                                                        <%=ToolsHTML.showCheckBox("expireI","getDateExpire(this.form.expireI,this.form.expire,'dateExpireWF',this.form.name,this.form.dateExpireWF)",
			                                                                                  dataWF.getExpire(),0,editable)%><%=rb.getString("wf.expFlexFlow")%>
			    	                                            </td>
			    	                                            <td>
			                                                        <input type="hidden" name="expire" value="<%=dataWF.getExpire()%>">
				                                                        <input type="text" style="width:90px;" name="dateExpireWF" value="<%=dataWF.getDateExpireWF()%>" <%=editable?"readOnly":""%>/>
			    	                                                    <div id="calendario"></div>
			    	                                            </td>
			    	                                        </tr>
    	                                                </table>
                                                    </p>
                                                </td>
                                                <td valign="top" width="40%" class="td_title_bc" align="center">
                                                    <logic:present name="actividadesFlex" scope="session" >
                                                        <table cellSpacing=0 cellPadding=0 align=center border=0 width="70%" class="clsTableTitle">
                                                            <tr>
                                                                <td>
                                                                    <table bgColor="#003366" width="100%" border="0">
                                                                        <tr>
                                                                           <td class="td_title_bwc">
                                                                                Actividades del Flujo
                                                                           </td>
                                                                        </tr>
                                                                        <logic:iterate id="actFlex" name="actividadesFlex" indexId="posAct" type="com.desige.webDocuments.workFlows.forms.BaseWorkFlow" scope="session">
                                                                            <tr onMouseover="this.bgColor='#E8E8D0'" onMouseout="this.bgColor='#003366'">
                                                                                <td align="center" class="td_btn">
                                                                                    <a href="javascript:changeForm(<%=posAct%>)" class="ahref_g">
                                                                                        <%=actFlex.getNameAct()%>
                                                                                    </a>
                                                                                </td>
                                                                            </tr>
                                                                        </logic:iterate>
                                                                    </table>
                                                                </td>
                                                            </tr>
                                                        </table>
                                                    </logic:present>
                                                </td>
                                            </tr>
                                        </table>
                                    </td>
                                </tr>
                                <tr>
                                    <td width="100%" valign="top" >
                                        <table cellSpacing=0 cellPadding=0 align=center border=0 width="100%">
                                            <tr>
                                                <td colspan="2" valign="top" height="21 px">
                                                    <table class="clsTableTitle" width="100%" cellSpacing=0 cellPadding=2 align=center border=0>
                                                        <tbody>
                                                            <tr>
                                                                <td class="td_title_bc" width="50%"align="center">
                                                                    <%=rb.getString("wf.groups")%>
                                                                </td>
                                                                <!--<td width="5%">-->
                                                                    <!--&nbsp;-->
                                                                <!--</td>-->
                                                                <td width="*" align="center">
                                                                    <%=rb.getString("wf.participation")%>
                                                                </td>
                                                            </tr>
                                                        </tbody>
                                                    </table>
                                                </td>
                                            </tr>
                                            <tr>
                                                <td colspan="2">
                                                    <table cellSpacing=0 cellPadding=0 align=center border="0" width="90%">
                                                        <tr>
                                                            <td width="40%">
                                                                <center>
                                                                    <!-- Cada SubActividad debe Cargar sus Grupos Previos -->
                                                                    <select name="groups" size="5" multiple style="width:300px;" styleClass="classText">
                                                                        <%
                                                                            String name = "groups" + dataWF.getSubNumber();
                                                                        %>
                                                                        <logic:present name="<%=name%>" scope="session">
                                                                            <logic:iterate id="bean" name="<%=name%>" scope="session" type="com.desige.webDocuments.utils.beans.Search">
                                                                                <option value="<%=bean.getId()%>"><%=bean.getDescript()%></option>
                                                                            </logic:iterate>
                                                                        </logic:present>
                                                                    </select>
                                                                </center>
                                                            </td>
                                                            <td width="5%">
                                                            	<input type="button" value=">" onClick="javascript:mover(document.newWF.groups,document.newWF.groupsSelected);" class="BUTTON" style="width:28px;">
                                                                <br/>
                                                            	<input type="button" value="<" onClick="javascript:mover(document.newWF.groupsSelected,document.newWF.groups);" class="BUTTON" style="width:28px;">
                                                            </td>
                                                            <td width="40%">
                                                                <center>
                                                                    <select name="groupsSelected" size="5" multiple style="width:300px;" styleClass="classText"></select>
                                                                </center>
                                                            </td>
                                                            <select name="auxG" style="display:none" multiple>
                                                            </select>
                                                            <select name="auxSelecG" style="display:none" multiple>
                                                            </select>
                                                            <td width="5%" align="left">
                                                            	<input type="button" value="+" onClick="javascript:up(document.newWF.groupsSelected,document.newWF.auxG,document.newWF.auxSelecG);" class="BUTTON" style="width:28px;">
                                                                <br/>
                                                            	<input type="button" value="-" onClick="javascript:down(document.newWF.groupsSelected,document.newWF.auxG,document.newWF.auxSelecG);" class="BUTTON" style="width:28px;">
                                                            </td>
                                                        </tr>
                                                    </table>
                                                </td>
                                            </tr>
                                        </table>
                                    </td>
                                </tr>
                                <tr>
                                    <td width="100%" valign="top" >
                                        <table cellSpacing=0 cellPadding=0 align=center border="0" width="100%">
                                            <tr>
                                                <td colspan="2" valign="top">
                                                    <table class="clsTableTitle" width="100%" cellSpacing=0 cellPadding=2 align=center border=0>
                                                        <tbody>
                                                            <tr>
                                                                <td class="td_title_bc" width="45%"align="center">
                                                                    <logic:present name="showCharge" scope="session">
                                                                        <%=rb.getString("wf.cargos")%>
                                                                    </logic:present>
                                                                    <logic:notPresent name="showCharge" scope="session">
                                                                        <%=rb.getString("wf.users")%>
                                                                    </logic:notPresent>
                                                                    -<span style="color:blue;"><input type="checkbox" onclick="mostrarViewer(this)">Viewers</span>
                                                                </td>
                                                                <!--<td width="5%">-->
                                                                    <!--&nbsp;-->
                                                                <!--</td>-->
                                                                <td width="*" align="center">
                                                                    <%=rb.getString("wf.participation")%>
                                                                </td>
                                                            </tr>
                                                        </tbody>
                                                    </table>
                                                </td>
                                            </tr>
                                            <!-- usuarios -->
                                            <tr>
                                                <td colspan="2">
                                                    <table cellSpacing=0 cellPadding=0 align=center border="0" width="90%">
                                                        <tr>
                                                            <td width="40%" align="center"><!-- <%=name%> -->
                                                                <%
                                                                    name = "usuarios"+dataWF.getSubNumber();
                                                                    nameViewer = "usuariosViewer"+dataWF.getSubNumber();
                                                                %>
                                                                 <select id="users" name="users" size="5" multiple style="width:300px;" styleClass="classText">
                                                                     <logic:present name="<%=name%>" scope="session">
                                                                         <logic:equal value="0" name="permitWF">
                                                                             <%
                                                                                 String userActive = ((Users)session.getAttribute("user")).getUser();
                                                                                 if (userActive==null) {
                                                                                     userActive = "";
                                                                                 }
                                                                             %>
                                                                             <logic:iterate id="bean" name="<%=name%>" scope="session" type="com.desige.webDocuments.utils.beans.Search">
                                                                                 <% if (!userActive.equalsIgnoreCase(bean.getId())) { %>
                                                                                    <logic:present name="showCharge" scope="session">
                                                                                        <option value="<%=bean.getId()%>"><%=bean.getAditionalInfo()%> ( <%=bean.getDescript()%> ) </option>
                                                                                    </logic:present>
                                                                                    <logic:notPresent name="showCharge" scope="session">
                                                                                        <option value="<%=bean.getId()%>"><%=bean.getDescript()%></option>
                                                                                    </logic:notPresent>
                                                                                 <% } %>
                                                                             </logic:iterate>
                                                                         </logic:equal>
                                                                         <logic:notEqual value="0" name="permitWF">
                                                                             <logic:iterate id="bean" name="<%=name%>" scope="session" type="com.desige.webDocuments.utils.beans.Search">
                                                                                <logic:present name="showCharge" scope="session">
                                                                                    <option value="<%=bean.getId()%>"><%=bean.getAditionalInfo()%> ( <%=bean.getDescript()%> ) </option>
                                                                                </logic:present>
                                                                                <logic:notPresent name="showCharge" scope="session">
                                                                                    <option value="<%=bean.getId()%>"><%=bean.getDescript()%></option>
                                                                                </logic:notPresent>
                                                                             </logic:iterate>
                                                                         </logic:notEqual>
                                                                     </logic:present>
                                                                 </select>
                                                                 
			                                                     <select id="usersViewers" name="usersViewers" style="display:none"><!-- <%=nameViewer%> -->
			                                                         <logic:present name='<%=nameViewer%>' scope="session">
			                                                             <logic:equal value="0" name="permitWF">
			                                                                 <%
			                                                                     String userActive = ((Users)session.getAttribute("user")).getUser();
			                                                                     if (userActive==null) {
			                                                                         userActive = "";
			                                                                     }
			                                                                 %>
			                                                                 <logic:iterate id="bean" name="<%=nameViewer%>" scope="session" type="com.desige.webDocuments.utils.beans.Search">
			                                                                     <% if (!userActive.equalsIgnoreCase(bean.getId())) { %>
			                                                                        <logic:present name="showCharge" scope="session">
			                                                                            <option value="<%=bean.getId()%>"><%=bean.getAditionalInfo()%><br>(<%=bean.getDescript()%>)</option>
			                                                                        </logic:present>
			                                                                        <logic:notPresent name="showCharge" scope="session">
			                                                                            <option value="<%=bean.getId()%>"><%=bean.getDescript()%> </option>
			                                                                        </logic:notPresent>
			                                                                     <% } %>
			                                                                 </logic:iterate>
			                                                             </logic:equal>
			                                                             <logic:notEqual value="0" name="permitWF">
			                                                                 <logic:iterate id="bean" name="<%=nameViewer%>" scope="session" type="com.desige.webDocuments.utils.beans.Search">
			                                                                    <logic:present name="showCharge" scope="session">
			                                                                        <option value="<%=bean.getId()%>"><%=bean.getAditionalInfo()%><br>(<%=bean.getDescript()%>)</option>
			                                                                    </logic:present>
			                                                                    <logic:notPresent name="showCharge" scope="session">
			                                                                        <option value="<%=bean.getId()%>"><%=bean.getDescript()%></option>
			                                                                    </logic:notPresent>
			                                                                 </logic:iterate>
			                                                             </logic:notEqual>
			                                                         </logic:present>
			                                                     </select>
                                                                 
                                                            </td>
                                                            <td width="5%">
                                                            	<input type="button" value=">" onClick="javascript:mover(document.newWF.users,document.newWF.usersSelected,1);" class="BUTTON" style="width:28px;">
                                                                <br/>
                                                            	<input type="button" value="<" onClick="javascript:mover(document.newWF.usersSelected,document.newWF.users,2);" class="BUTTON" style="width:28px;">
                                                            </td>
                                                            <select name="aux<%=indice%>" style="display:none" multiple>
                                                            </select>
                                                            <select name="auxSelec<%=indice%>" style="display:none" multiple>
                                                            </select>
                                                            <td width="40%" align="center">
                                                                <%
                                                                    boolean changeUsr = false;
                                                                %>
                                                                <logic:present name="permission" scope="session">
                                                                    <logic:equal name="permission" property="toChangeUsr" value="0">
                                                                        <%
                                                                            changeUsr = true;
                                                                        %>
                                                                    </logic:equal>
                                                                </logic:present>
                                                                <select name="usersSelected" size="5" multiple style="width:300px;" styleClass="classText" onchange="chkUser('<%=dataWF.getUsrSug()%>',<%=changeUsr%>);">
                                                                        <logic:present name="showCharge" scope="session">
                                                                            <logic:iterate id="bean" name="newWF" scope="session" type="com.desige.webDocuments.utils.beans.Search" property="userSelecteds" >
                                                                                 <option value="<%=bean.getId()%>"><%=bean.getAditionalInfo()%> ( <%=bean.getDescript()%> ) </option>
                                                                             </logic:iterate>
                                                                         </logic:present>
                                                                         <logic:notPresent name="showCharge" scope="session">
                                                                            <logic:iterate id="bean" name="newWF" scope="session" type="com.desige.webDocuments.utils.beans.Search" property="userSelecteds" >
                                                                                 <option value="<%=bean.getId()%>"><%=bean.getDescript()%></option>
                                                                             </logic:iterate>
                                                                         </logic:notPresent>
                                                                 </select>
                                                                <select name="usersSelectedExpire" size="5" multiple style="display:none;" >
                                                                    <logic:iterate id="bean" name="newWF" scope="session" type="com.desige.webDocuments.utils.beans.Search" property="userSelectedsExpire" >
                                                                         <option value="<%=bean.getId()%>"><%=bean.getDescript()%></option>
                                                                    </logic:iterate>
                                                                </select>
                                                            </td>
                                                            <td width="5%" align="left">
                                                            	<input type="button" value="+" onClick="javascript:up(document.newWF.usersSelected,document.newWF.aux<%=indice%>,document.newWF.auxSelec<%=indice%>);" class="BUTTON" style="width:28px;">
                                                                <br/>
                                                            	<input type="button" value="-" onClick="javascript:down(document.newWF.usersSelected,document.newWF.aux<%=indice%>,document.newWF.auxSelec<%=indice%>);" class="BUTTON" style="width:28px;">
                                                            </td>
                                                        </tr>
                                                        <tr>
                                                        	<td colspan="1" align="center">&nbsp;
                                                        	</td>
                                                        	<td colspan="3" align="center">
                                                            	<table id="tablaFecha" cellspacing="0" cellpadding="0" border="0" style="visibility:hidden;">
	                                                            	<tr>
	                                                            		<td>
	                                                            			<span class="td_title_bc"><%= rb.getString("wf.expbyuser")%> : </span>
	    		                                                        	<input type="text" name="dateExpireUserWF" onClick="getDateExpireUser()" style="width:90px;" readOnly="true">
	    		                                                        	<div id="calendario2"></div>
    		                                                        	</td>
    		                                                        </tr>
    		                                                    </table>
                                                        	</td>
                                                        </tr>
                                                    </table>
                                                </td>
                                            </tr>
                                            <html:textarea property="comments" style="display:none" />
<%--                                            <textarea name="comments" style="display:none"></textarea>--%>
                                            <tr>
                                                 <td colspan="3"  class="titleCenter" style="background: url(img/BgComments.gif); background-repeat: no-repeat" valign="middle">
                                                     <%=rb.getString("wf.comments")%>
                                                 </td>
                                            </tr>
                                            <tr>
                                                <td class="fondoEditor" colspan="3" valign="top">
													  <jsp:include page="richedit.jsp">
														<jsp:param name="richedit" value="richedit"/>
														<jsp:param name="defaultValue" value="<%= String.valueOf(dataWF.getComments()) %>"/>
														<jsp:param name="height" value="150"/>
													  </jsp:include>
                                                </td>
                                            </tr>
                            </table>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            &nbsp;
                        </td>
                    </tr>
                </table>
<%--            </logic:iterate>--%>
                <table cellSpacing=0 cellPadding=0 align=center border=0 width="100%">
                    <tr>
                        <td width="80%" valign="top" align="center">
                            <input type="button" class="boton" name="btnOK" value="<%=rb.getString("btn.send")%>" onClick="javascript:salvar(this.form);" >
                            &nbsp;
                            <input type="button" class="boton" name="btnCancel" value="<%=rb.getString("btn.cancel")%>" onClick="javascript:cancelar();" >
                        </td>
                    </tr>
                </table>
<%--        </logic:present>--%>
    </html:form>
    <script language="JavaScript" event="onload" for="window">
        <logic:present name="info" scope="session">
            alert("<%=session.getAttribute("info")%>");
            <%
                session.removeAttribute("info");
            %>
        </logic:present>

        <logic:present name="error" scope="session">
            alert("<%=session.getAttribute("error")%>");
            <%
                session.removeAttribute("error");
            %>
        </logic:present>
        // jairo
        if (document.newWF&&document.newWF.richedit) {
            document.newWF.richedit.value = document.newWF.comments.innerText;
            updateRTEs();
        }
        <logic:present name="info" scope="session" >
            alert('<%=session.getAttribute("info")%>');
            <%session.removeAttribute("info");%>
        </logic:present>
        <logic:present name="error" scope="session" >
            alert('<%=session.getAttribute("error")%>');
            <%session.removeAttribute("error");%>
        </logic:present>
/*
        largo = document.newWF.usersSelected.length;
        cad = ",";
		for(var i = 0 ; i < largo; i++ ) {
			document.newWF.usersSelected.options[i].selected=true;
			cad += document.newWF.usersSelected.options[i].value+",";
		}
		alert(cad);
        largo = document.newWF.users.length;
		for(var k = 0 ; k < largo; k++ ) {
		alert(cad+" --- "+document.newWF.users.options[k].value);
			if( cad.indexOf(","+document.newWF.users.options[k].value+",")!=-1) {
				document.newWF.users.options[k].selected=true;
				alert("entro");
			}
		}
*/	
        mover(document.newWF.users,document.newWF.usersSelected)
        
        <%if(request.getAttribute("isSendToFlexWF")!=null && request.getAttribute("isSendToFlexWF").equals("true")) {
        	out.println("salvar(document.newWF);");
        }%>
        
    </script>
<%--</logic:present>--%>
</body>
</html>
