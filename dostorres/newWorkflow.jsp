<jsp:include page="richeditDocType.jsp" /> 
<!--/**
 * Title: newWorkflow.jsp <br/>  
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 *
 * @author Ing. Nelsón Crespo.(NC)
 * @author Ing. Simon Rodriguez.(SR)
 * @version WebDocuments v3.0
 * <br/>
 *          Changes:<br/>
 * <ul>
 *          <li> 13/05/2005 (NC) Creation </li>
 *          <li> 13/05/2005 (SR) Se modificaron las imagnes de up y down
 *            Se modifico el titulo con validacion logic para que salga aprobar o revisar</li>
 *          <li> 13/06/2005 (SR) Se valido los up y down para que en caso de no seleccionar ningun elemento, no borre toda la lista</li>
 *          <li> 11/07/2005 (SR)
 *                              Se agrego un checkbox eliminar y una funcion updateCheckEli
 *                              ALTER TABLE WORKFLOWS ADD eliminar bit default (0)
 *                              UPDATE WORKFLOWS SET ELIMINAR=0</li>
 *          <li> 12/078/2005 (SR) Se usa esta session statuelim para eliminar solo los aprobados</li>
 *          <li> 25/04/2006 (SR) Se cambio algunas rutas /WebDocuments/.. a ./..</li>

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
    String onLoad = "";//ToolsHTML.getMensajePages(request,rb,null);
    String nodeActive = (String)session.getAttribute("nodeActive");
    Long dateDeadDocumentTime = (Long) session.getAttribute("dateDeadDocTime");
%>
<html>
<head>
<title><%=rb.getString("principal.title")%></title>
<jsp:include page="meta.jsp" /> 
<link href="estilo/estilo.css" rel="stylesheet" type="text/css">
<script language=javascript src="./estilo/popcalendar2inner.js"></script>
<script language=javascript src="./estilo/funciones.js"></script>
<script language=javascript src="./estilo/fechas.js"></script>
<jsp:include page="richeditHead.jsp" /> 

<script language="JavaScript">
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

    function save() {

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
    
    function mostrarObj(layer){
		if(layer.style.display!='')
			layer.style.display = '';
	}
	
	function ocultarObj(layer){
		if(layer.style.display!='none')
			layer.style.display = 'none';
	}
   
    function toggleDiv(gn)  {
		//var g = document.all(gn);
		var g = document.getElementById(gn);
		
		if( g.style.display == "none")
			g.style.display = "";
		else
			g.style.display = "none";
    }	
    
    function cancelar() {
    	var form = document.getElementById("Selection");
    	form.action = "showDataDocument.do";//"loadAllStruct.do";
        form.cmd.value="<%=SuperActionForm.cmdLoad%>";
<%--    form.target = "_parent";--%>
        form.target = "_self";
        
        form.submit();
    }

    function validar(forma){
        if (forma.expireI.checked) {
            if (!validarFecha(forma.dateExpireWF.value)) {
                alert("<%=rb.getString("wf.badDate")%>");
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
            alert("<%=rb.getString("err.notUserInWF")%>");
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
        if (!validar(forma)) {

        } else {
            var lista = forma.groupsSelected;
            var items = lista.length;
            var row = 0;
            var groups = false;
            for (row = 0; row < items ; row++) {
                var valor = lista[row];
                //alert("lista[row].value="+lista[row].value+" lista[row].text="+lista[row].text);
                valor.selected = true;
                groups = true;
            }
            lista = forma.usersSelected;
            var items = lista.length;
            var users = false;
            for (row = 0; row < items ; row++){
                var valor = lista[row];
                valor.selected = true;
                users = true;
            }
<%--
            if (groups&&users) {
                alert('<%=rb.getString("E0018")%>');
                return false;
            }
--%>
			updateRTEs();
            forma.comments.value = forma.richedit.value;
	        if (isEmptyRicheditValue(forma.richedit.value)) {
                alert('<%=rb.getString("E0027")%>');
                return false;
            }
            botones(forma);
            forma.submit();
        }
    }

    function updateCheck(check,field) {
        if (check.checked) {
            field.value = 0;
        } else{
            field.value = 1;
        }
    }
     function updateCheckEli(check,field) {
        if (check.checked) {
            field.value = 1;
            //alert('<%=session.getAttribute("infoEliminar")%>');
	     	<%if(session.getAttribute("infoEliminar")!=null){%>
	     		alert('<%=session.getAttribute("infoEliminar")%>');
	     	<%}%>
        } else{
            field.value = 0;
        }
    }
    
    //ydavila Ticket 001-00-003023
         function updateCheckCamb(check,field) {
        if (check.checked) {
            field.value = 1;
            alert('<%=session.getAttribute("infoCambiar")%>');
	     	<%if(session.getAttribute("infoCambiar")!=null){%>
	     		alert('<%=session.getAttribute("infoCambiar")%>');
	     	<%}%>
        } else{
            field.value = 0;
        }
    }
    
    function getDateExpire(check,field,dateField,nameForm,dateValue) {
        valor = dateValue.value;
        if (check.checked) {
            field.value = "0";
            dateEndWF = "<%=session.getAttribute("dateEndWF")!=null?session.getAttribute("dateEndWF"):""%>";
            showCalendar(document.newWF.dateExpireWF,'calendario');
        } else{
            document.newWF.dateExpireWF.value = "";
            field.value = 1;
        }
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

	function createXMLHttp() {
		if(typeof XMLHttpRequest != "undefined") {
			return new XMLHttpRequest();
		} else {
			var aVersions = [ "MSXML2.XMLHttp.5.0", "MSXML2.XMLHttp.4.0", "MSXML2.XMLHttp.3.0", "MSXML2.XMLHttp", "Microsoft.XMLHttp"];
			for(var i=0; i<aVersions.length; i++) {
				try {
					var oXmlHttp = new ActiveXObject(aVersions[i]);
					return oXmlHttp;
				} catch(e) {
					// no hacer nada
				}
			}
		}
	}
	
	
	function mover(listDer,listIzq,addUsers){
		var items = listDer.length;
		var selecteds = contarItemsSelecteds(listDer);
		if (selecteds > 0){
			var	newItem;
			for (row = 0; row < items; row++){
				var valor = listDer[row];
				if (valor.selected) {
                    newItem = createElement(listDer[row].value,listDer[row].text);
					listIzq.options[listIzq.length]=newItem;
				}
			}
			removerDeLista(listDer,items);
		}
		if(typeof addUsers != 'undefined') {
			// con ajax buscaremos los usuarios del grupo
			
		}
	}

	function removerDeLista(lista,items){
		if (lista){
            var row = 0;
			for (row = items - 1; row >= 0; row--){
				var valor = lista[row];
				if (valor.selected){
					lista.remove(row);
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

<body class="bodyInternas" <%=onLoad%>>
<logic:present name="showDocument">
    <bean:define id ="doc" name="showDocument" type="com.desige.webDocuments.document.forms.BaseDocumentForm" scope="session"/>
    <bean:define id ="dataWF" name="newWF" type="com.desige.webDocuments.workFlows.forms.BaseWorkFlow" scope="session"/>

    <form name="Selection" id="Selection" action="loadStructMain.do">
        <input type="hidden" name="idNodeSelected" value="<%=nodeActive%>"/>
        <input type="hidden" name="cmd"/>
        <input type="hidden" name="nexPage" value="loadStructMain.do"/>
        <input type="hidden" name="idDocument" value="<%=doc.getIdDocument()!=null?doc.getIdDocument().trim():""%>"/>
	    <input type="hidden" name="isSendToFlexWF" value="<%=request.getParameter("isSendToFlexWF")!=null?request.getParameter("isSendToFlexWF"):"false"%>"/>
    </form>

    <html:form action="/saveWF.do">
        <html:hidden property="typeWF"/>
        <%--ydavila Ticket 001-00-003023 --%>
        <html:hidden property="subtypeWF"/>
        
        <html:hidden property="mayorVersionDocument"/>
        <html:hidden property="lastVersion"/>
            <table cellSpacing=0 cellPadding=0 align=center border=0 width="100%">
                <tr>
                    <td width="100%">
                        <table class="clsTableTitle" width="100%" cellSpacing=0 cellPadding=2 align=center border=0>
                            <tr>
                                <td class="td_title_bc" height="22 px" width="80%">

                                    <%=rb.getString("workFlow.titleOf")%>
                                     <logic:equal value="0" name="dataWF" property="typeWF">
                                        <%=rb.getString("btn.wfReview")%>
                                     </logic:equal>
                                     <logic:equal value="1" name="dataWF" property="typeWF">
                                       <%=rb.getString("btn.wf.aproved2")%>
                                     </logic:equal>
                                     
                                     <%--ydavila Ticket 001-00-003023 Flujo de Revisión para Solicitud de Cambio--%>
                                     <logic:equal value="3" name="dataWF" property="typeWF">
                                     <%--<%out.println("3");%>--%>
                                       <%=rb.getString("btn.wfSolCambio")%>
                                     </logic:equal> 
                                                                        
                                       <%--ydavila Ticket 001-00-003023 Flujo de Revisión para Solicitud de Eliminación--%>
                                     <logic:equal value="4" name="dataWF" property="typeWF">
                                     <%--<%out.println("4");%>--%>
                                       <%=rb.getString("btn.wfSolElimin")%>
                                     </logic:equal>                                     
                                </td>
                                <td class="td_title_bc" width="*">
                                    <%=rb.getString("cbs.options")%>
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
                <tr>
                    <td>
                        <table cellSpacing=0 cellPadding=0 align=center border=0 width="100%">
                            <tr>
                                <td width="80%" valign="top" >
                                    <table cellSpacing=0 cellPadding=0 align=center border=0 width="100%">
                                        <tr>
                                            <td colspan="4" valign="top">
                                                <table class="clsTableTitle" width="100%" cellSpacing=0 cellPadding=2 align=center border=0>
                                                    <tbody>
                                                        <tr>
                                                            <td class="td_title_bc" width="45%"align="center">
                                                                <%=rb.getString("wf.groups")%>
                                                            </td>
                                                            <td width="5%">
                                                                &nbsp;
                                                            </td>
                                                            <td width="*" align="center">
                                                                <%=rb.getString("wf.participation")%>
                                                            </td>
                                                        </tr>
                                                    </tbody>
                                                </table>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td width="45%">
                                                <center>
                                                    <select name="groups" size="5" multiple style="width:310px;" styleClass="classText">
                                                      
                                                      <%--ydavila Ticket 001-00-003023--%>
													  <logic:lessThan value="3" name="dataWF" property="typeWF">
                                                        <logic:present name="groups" scope="session">
                                                            <logic:iterate id="bean" name="groups" scope="session" type="com.desige.webDocuments.utils.beans.Search">
                                                                <option value="<%=bean.getId()%>"><%=bean.getDescript()%></option>
                                                            </logic:iterate>
                                                        </logic:present>
                                                      </logic:lessThan>  
                                                    </select>
                                                </center>
                                            </td>
                                            <td width="5%">
                                              <%--ydavila Ticket 001-00-003023--%>
											  <logic:lessThan value="3" name="dataWF" property="typeWF">
                                            	<input type="button" value=">" onClick="javascript:mover(document.newWF.groups,document.newWF.groupsSelected,true);" class="BUTTON" style="width:28px;">
                                                <br/>
                                            	<input type="button" value="<" onClick="javascript:mover(document.newWF.groupsSelected,document.newWF.groups);" class="BUTTON" style="width:28px;">
											  </logic:lessThan>
                                            </td>
                                            <td width="40%">
                                                <center>
                                                    <html:select property="groupsSelected" multiple="true" style="width:310px;" size="5" styleClass="classText">
                                                    </html:select>
                                                </center>
                                            </td>
                                            <select name="auxG" style="display:none" multiple>
                                            </select>
                                            <select name="auxSelecG" style="display:none" multiple>
                                            </select>
                                            <%--ydavila Ticket 001-00-003023--%>
											  <logic:lessThan value="3" name="dataWF" property="typeWF">
                                            <td width="*" align="left">
                                               	<input type="button" value="+" onClick="javascript:up(document.newWF.groupsSelected,document.newWF.auxG,document.newWF.auxSelecG);" class="BUTTON" style="width:28px;">
                                                   <br/>
                                               	<input type="button" value="-" onClick="javascript:down(document.newWF.groupsSelected,document.newWF.auxG,document.newWF.auxSelecG);" class="BUTTON" style="width:28px;">
                                            </td>
											</logic:lessThan>          
                                        </tr>
                                    </table>
                                </td>
                                <td width="*" valign="top" rowspan="2" class="td_title_bc">
                                    <p align="left" >
                                    <%
                                        boolean editable = false;
                                    %>
                                    <logic:present name="changeWF">
                                        <%
                                            editable = true;
                                        %>
                                    </logic:present>
                                    <!-- 11 DE JULIO 2005 SIMÓN INICIO-->	
                                    								 
								<%-- ydavila Ticket 001-00-003023 sólo se permite eliminar para flujo revisión(0) subtipo 4--%>
								<%-- <logic:equal value="0" name="dataWF" property="typeWF">--%>
                                     <logic:equal value="4" name="dataWF" property="typeWF">
                                          <logic:present name="statuelim" scope="session">
                                              <logic:equal value="5" name="statuelim" scope="session">
                                                   <%=ToolsHTML.showCheckBox("eliminarI","updateCheckEli(this.form.eliminarI,this.form.eliminar)",
                                                                          dataWF.getEliminar(),0,true)%><%=rb.getString("btn.wfSolElimin")%>
                                                   <html:hidden property="eliminar" value="<%=String.valueOf(dataWF.getEliminar())%>"/>
                                                  <br/>
                                             </logic:equal>     
                                         </logic:present>
                                     </logic:equal>
                                    <%-- ydavila Ticket 001-00-003023 sólo se permite solicitud de cambio para flujo revisión(0) subtipo 3--%> 
                                    <logic:equal value="3" name="dataWF" property="typeWF">
                                          <logic:present name="statucamb" scope="session">
                                              <logic:equal value="5" name="statucamb" scope="session">
                                                   <%=ToolsHTML.showCheckBox("cambiarI","updateCheckCamb(this.form.cambiarI,this.form.cambiar)",
                                                                          dataWF.getCambiar(),1,true)%><%=rb.getString("btn.wfSolCambio")%>
                                                   <html:hidden property="cambiar" value="<%=String.valueOf(dataWF.getCambiar())%>"/>
                                                  <br/>
                                             </logic:equal>     
                                         </logic:present>
                                     </logic:equal>
                                     
                                     <%if(ToolsHTML.copyContents()){%>
                                        <%=ToolsHTML.showCheckBox("copyI","updateCheck(this.form.copyI,this.form.copy)",
                                                                  dataWF.getCopy(),0,editable)%><%=rb.getString("cbs.copy")%>
                                        <html:hidden property="copy" value="<%=String.valueOf(dataWF.getCopy())%>"/>
                                        <br/>
                                    <%} else {%>
                                        <html:hidden property="copy" value="1"/>
                                    <%}%>
                                    <!-- 11 DE JULIO 2005 SIMÓN FIN-->
                                    
                                      <%-- ydavila Ticket 001-00-003023 --%>
                                      <logic:lessThan value="3" name="dataWF" property="typeWF">
                                        <%=ToolsHTML.showCheckBox("secuentialI","updateCheck(this.form.secuentialI,this.form.secuential)",
                                                                  dataWF.getSecuential(),0,editable)%><%=rb.getString("wf.secuential")%>
                                        <html:hidden property="secuential" value="<%=String.valueOf(dataWF.getSecuential())%>"/>
                                        <br/>
                                      </logic:lessThan>  
                                      
                                      <%-- ydavila Ticket 001-00-003023 --%>
                                      <logic:greaterThan value="2" name="dataWF" property="typeWF">
                                        <%=ToolsHTML.showCheckBox("secuentialI","updateCheck(this.form.secuentialI,this.form.secuential)",
                                                                  dataWF.getSecuential(),1,true)%><%=rb.getString("wf.secuential")%>
                                        <%-- <html:hidden property="secuential" value="<%=String.valueOf(dataWF.getSecuential())%>"/>--%>
                                        <html:hidden property="secuential" value="0"/>
                                        <br/>
                                      </logic:greaterThan> 
                                      
                                     <%-- ydavila Ticket 001-00-003023 --%>
									  <logic:lessThan value="3" name="dataWF" property="typeWF">
                                        <%=ToolsHTML.showCheckBox("conditionalI","updateCheck(this.form.conditionalI,this.form.conditional)",
                                                                  dataWF.getConditional(),0,editable)%><%=rb.getString("wf.conditional")%>
                                        <html:hidden property="conditional" value="<%=String.valueOf(dataWF.getConditional())%>"/>
                                        <br/>
                                      </logic:lessThan>                             
                                      
                                      <%-- ydavila Ticket 001-00-003023 --%>
                                      <logic:greaterThan value="2" name="dataWF" property="typeWF">
										<%=ToolsHTML.showCheckBox("conditionalI","updateCheck(this.form.conditionalI,this.form.conditional)",
                                                                  dataWF.getConditional(),1,true)%><%=rb.getString("wf.conditional")%>
                                        <html:hidden property="conditional" value="<%=String.valueOf(dataWF.getConditional())%>"/>
                                        <br/>
                                      </logic:greaterThan> 
                                                 
                                        <%=ToolsHTML.showCheckBox("notifiedI","updateCheck(this.form.notifiedI,this.form.notified)",
                                                                  dataWF.getNotified(),0,editable)%><%=rb.getString("wf.notify")%>
                                        <html:hidden property="notified" value="1"/>
                                        <br/>
                                        <%=ToolsHTML.showCheckBox("expireI","getDateExpire(this.form.expireI,this.form.expire,'dateExpireWF',this.form.name,this.form.dateExpireWF)",
                                                                  dataWF.getExpire(),0,editable)%><%=rb.getString("wf.expire")%> 
                                        <html:hidden property="expire" value="1"/>
                                        <br/>
                                        <html:text property="dateExpireWF" readonly="<%=editable%>"/>
                                        <div id="calendario" style="textDecoration:none;"></div>
                                    </p>
                                </td>
                            </tr>
                            <tr>
                            <%-- VENTANA USUARIOS --%>
                                <td width="80%" valign="top" >
                                    <table cellSpacing=0 cellPadding=0 align=center border=0 width="100%">
                                        <tr>
                                            <td colspan="4" valign="top">
                                                <table class="clsTableTitle" width="100%" cellSpacing=0 cellPadding=2 align=center border=0>
                                                    <tbody>
                                                        <tr>
                                                            <td class="td_title_bc" width="45%"align="center">
                                                              <logic:present name="showCharge" scope="session">
                                                                    <%=rb.getString("wf.cargos")%> ( <%=rb.getString("wf.users")%> )
                                                               </logic:present>
                                                               <logic:notPresent name="showCharge" scope="session">
                                                                    <%=rb.getString("wf.users")%>
                                                               </logic:notPresent>
                                                                <%-- ydavila Ticket 001-00-003023 --%>
                                      							<logic:lessThan value="3" name="dataWF" property="typeWF">
                                                              -<span style="color:blue;"><input type="checkbox" onclick="mostrarViewer(this)">Viewers</span>
                                      							</logic:lessThan>
                                                            </td>
                                                            <td width="5%">
                                                                &nbsp;
                                                            </td>
                                                            <td width="*" align="center">
                                                                <%=rb.getString("wf.participation")%>
                                                            </td>
                                                        </tr>
                                                    </tbody>
                                                </table>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td width="45%">
                                                 <center>
                                                     <select id="users" name="users" onDblClick="javascript:vertexto(this.form.users);" size="7" multiple style="width:310px;" styleClass="classText">
                                                         <logic:present name="usuarios" scope="session">
                                                             <logic:equal value="0" name="permitWF" scope="session">
                                                                 <%
                                                                     String userActive = ((Users)session.getAttribute("user")).getUser();
                                                                     if (userActive==null) {
                                                                         userActive = "";
                                                                     }
                                                                 %>
                                                                 <logic:iterate id="bean" name="usuarios" scope="session" type="com.desige.webDocuments.utils.beans.Search">
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
                                                             
                                                             <logic:notEqual value="0" name="permitWF" scope="session">
	                                                             <logic:lessThan value="3" name="dataWF" property="typeWF">
	                                                                 <logic:iterate id="bean" name="usuarios" scope="session" type="com.desige.webDocuments.utils.beans.Search">
	                                                                    <logic:present name="showCharge" scope="session">
	                                                                        <option value="<%=bean.getId()%>"><%=bean.getAditionalInfo()%><br>(<%=bean.getDescript()%>)</option>
	                                                                    </logic:present>
	                                                                    <logic:notPresent name="showCharge" scope="session">
	                                                                        <option value="<%=bean.getId()%>"><%=bean.getDescript()%></option>
	                                                                    </logic:notPresent>
	                                                                 </logic:iterate>
                                                                 </logic:lessThan>
                                                             </logic:notEqual>
                                                             
                                                         </logic:present>
                                                     </select>
                                                     
                                                     <select id="usersViewers" name="usersViewers" style="display:none">
                                                         <logic:present name="usuariosViewer" scope="session">
                                                             <logic:equal value="0" name="permitWF">
                                                                 <%
                                                                     String userActive = ((Users)session.getAttribute("user")).getUser();
                                                                     if (userActive==null) {
                                                                         userActive = "";
                                                                     }
                                                                 %>
                                                                 <logic:iterate id="bean" name="usuariosViewer" scope="session" type="com.desige.webDocuments.utils.beans.Search">
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
                                                                 <logic:iterate id="bean" name="usuariosViewer" scope="session" type="com.desige.webDocuments.utils.beans.Search">
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
                                                     
                                                 </center>
                                            </td>
                                            <td width="5%">
                                              <%--ydavila Ticket 001-00-003023--%>
											  <logic:lessThan value="3" name="dataWF" property="typeWF">
                                               	<input type="button" value=">" onClick="javascript:mover(document.newWF.users,document.newWF.usersSelected);" class="BUTTON" style="width:28px;">
                                                   <br/>
                                               	<input type="button" value="<" onClick="javascript:mover(document.newWF.usersSelected,document.newWF.users);" class="BUTTON" style="width:28px;">
											 </logic:lessThan>
                                            </td>
                                                <select name="aux" style="display:none" multiple>
                                                </select>
                                                <select name="auxSelec" style="display:none" multiple>
                                                </select>
                                            <td width="40%">
                                                <center>
                                                    <select name="usersSelected" onDblClick="javascript:vertexto(this.form.usersSelected);" size="7" multiple style="width:310px;" styleClass="classText">
                                                      <%--ydavila Ticket 001-00-003023--%>
													  <logic:lessThan value="3" name="dataWF" property="typeWF">
                                                        <logic:present name="previousWF">
                                                            <logic:present name="showCharge" scope="session">
                                                                <logic:iterate id="bean" name="previousWF" scope="session" type="com.desige.webDocuments.utils.beans.Search">
                                                                     <option value="<%=bean.getId()%>"><%=bean.getAditionalInfo()%><br>(<%=bean.getDescript()%>)</option>
                                                                 </logic:iterate>
                                                             </logic:present>

                                                             <logic:notPresent name="showCharge" scope="session">
                                                                <logic:iterate id="bean" name="previousWF" scope="session" type="com.desige.webDocuments.utils.beans.Search">
                                                                     <option value="<%=bean.getId()%>"><%=bean.getDescript()%></option>
                                                                 </logic:iterate>
                                                             </logic:notPresent>

                                                         </logic:present>
                                                       </logic:lessThan>
                                                   <logic:greaterThan value="2" name="dataWF" property="typeWF"> 
                                                        <logic:present name="RespCambElimDocEOC"> 
                                                            <logic:present name="showCharge" scope="session">
                                                                <logic:iterate id="bean" name="RespCambElimDocEOC" scope="session" type="com.desige.webDocuments.utils.beans.Search">
                                                                     <option value="<%=bean.getId()%>"><%=bean.getAditionalInfo()%><br>(<%=bean.getDescript()%>)</option>
                                                                 </logic:iterate>
                                                             </logic:present>
 
                                                             <logic:notPresent name="showCharge" scope="session">
                                                                <logic:iterate id="bean" name="RespCambElimDocEOC" scope="session" type="com.desige.webDocuments.utils.beans.Search">
                                                                     <option value="<%=bean.getId()%>"><%=bean.getDescript()%></option>
                                                                 </logic:iterate>
                                                             </logic:notPresent>
                                                         </logic:present>   
                                                         <logic:present name="RespCambElimDocAdm">
                                                            <logic:present name="showCharge" scope="session">
                                                                <logic:iterate id="bean" name="RespCambElimDocAdm" scope="session" type="com.desige.webDocuments.utils.beans.Search">
                                                                     <option value="<%=bean.getId()%>"><%=bean.getAditionalInfo()%><br>(<%=bean.getDescript()%>)</option>                                                                     
                                                                     
                                                                 </logic:iterate>
                                                             </logic:present>

                                                             <logic:notPresent name="showCharge" scope="session">
                                                                <logic:iterate id="bean" name="RespCambElimDocAdm" scope="session" type="com.desige.webDocuments.utils.beans.Search">
                                                                     <option value="<%=bean.getId()%>"><%=bean.getDescript()%></option>                                                                   
                                                                 </logic:iterate>
                                                             </logic:notPresent>
                                                         </logic:present>                                                    
                                                   </logic:greaterThan>    
                                                      </select>
                                                </center>
                                            </td>
                                            <td width="*" align="left">
                                            <%--ydavila Ticket 001-00-003023--%>
											  <logic:lessThan value="3" name="dataWF" property="typeWF">
                                              	<input type="button" value="+" onClick="javascript:up(document.newWF.usersSelected,document.newWF.aux,document.newWF.auxSelec);" class="BUTTON" style="width:28px;">
                                                  <br/>
                                              	<input type="button" value="-" onClick="javascript:down(document.newWF.usersSelected,document.newWF.aux,document.newWF.auxSelec);" class="BUTTON" style="width:28px;">
											  </logic:lessThan>
                                            </td>
                                        </tr>
                                    </table>
                                </td>
                            </tr>
                            <html:textarea property="comments" style="display:none" />
                            <tr>
                                 <td colspan="2" heigth="28" class="titleCenter" valign="middle">
                                 	&nbsp;
                                 </td>
                            </tr>
                            <tr>
                                 <td colspan="2" heigth="28" class="titleCenter" style="background: url(img/BgComments.gif); background-repeat: no-repeat" valign="middle">
                                     <%=rb.getString("wf.comments")%>
                                 </td>
                            </tr>
                            <tr>
                                <td class="fondoEditor" colspan="2" valign="top">
									  <jsp:include page="richedit.jsp">
										<jsp:param name="richedit" value="richedit"/>
									  </jsp:include>
                                </td>
                            </tr>
		                     <tr>
		                          <td  colspan="2" valign="top" >
		                          	&nbsp;
		                          </td>
		                     </tr>                            
		                     <tr>
                                <td colspan="2" align="center" >
									<input type="button" class="boton" name="btnOK" value="<%=rb.getString("btn.send")%>" onClick="javascript:salvar(this.form);" >
									&nbsp;
									<input type="button" class="boton" name="btnCancel" value="<%=rb.getString("btn.cancel")%>" onClick="javascript:cancelar();" >
                                </td>
                            </tr>
                        </table>
                    </td>
                </tr>
            </table>
    </html:form>
    <script language="JavaScript" event="onload" for="window">
        <logic:present name="info" scope="session" >
            alert('<%=session.getAttribute("info")%>');
            <%session.removeAttribute("info");%>
        </logic:present>
        <logic:present name="error" scope="session" >
            alert('<%=session.getAttribute("error")%>');
            <%session.removeAttribute("error");%>
        </logic:present>
    </script>
</logic:present>
</body>
</html>
