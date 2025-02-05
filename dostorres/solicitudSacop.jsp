<%@page import="java.util.Calendar"%>
<%@ page import="java.util.ResourceBundle,
                 com.desige.webDocuments.utils.ToolsHTML,
				 com.desige.webDocuments.utils.Constants,
                 com.desige.webDocuments.utils.beans.SuperActionForm,
                 com.desige.webDocuments.utils.beans.Users,
                 com.desige.webDocuments.utils.beans.PaginPage,
                 com.desige.webDocuments.document.forms.BaseDocumentForm,
                 com.desige.webDocuments.persistent.managers.HandlerStruct,
                 com.focus.qweb.to.TitulosPlanillasSacopTO,
                 java.util.ArrayList,
                 java.util.TreeMap,
                 java.util.Iterator,
                 com.desige.webDocuments.enlaces.forms.UrlsActionForm"%>

<!--/**
 * Title: solicitudSacop.jsp<br>
 * Copyright: (c) 2016 Focus Consulting<br/>
 *
 * @author Jairo Rivero (JR)
 * @version WebDocuments v5.0
 * <br>
 *     Changes:<br>
 * <ul>
 *      <li> 01-11-2016 (JR)  Creacion
 * <ul>
 */-->
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<%@ page language="java" %>
<%

    ResourceBundle rb = ToolsHTML.getBundle(request);
	Users user = (Users)session.getAttribute("user");
	
	BaseDocumentForm doc = (BaseDocumentForm)request.getAttribute("doc");
	ArrayList<TitulosPlanillasSacopTO> listaOrigen = (ArrayList<TitulosPlanillasSacopTO>) request.getAttribute("listaOrigen");
	TitulosPlanillasSacopTO titulo = null;
	TreeMap<String, Users> listaResponsable = (TreeMap<String, Users>) request.getAttribute("listaResponsable");  
// GRG	
	String dateSystem 	 = 	ToolsHTML.date.format(new java.util.Date()); 
	 
%>
<!DOCTYPE>
<html>
	<head>
		<title><%=rb.getString("principal.title")%></title>
		<jsp:include page="meta.jsp" /> 
		<link href="estilo/estilo.css" rel="stylesheet" type="text/css">
		<script type="text/javascript" src="./estilo/funciones.js"></script>
		<script language=javascript src="./estilo/fechas.js"></script>
		<script type="text/javascript" src="./estilo/popcalendar2inner.js"></script>
		<script type="text/javascript">

		
		
		
<%--  		function incluir(form) {
				if(form.fecha.value.replace(/ /gi,'')=='') {
					alert("<%=rb.getString("requestSacop.occurrenceDateNotValid")%>");
					return false;
				}
				if(form.descripcion.value.replace(/ /gi,'')=='') {
					alert("<%=rb.getString("requestSacop.descriptionSacopNotValid")%>");
					return false;
				}
				form.submit();
			}	GRG	 --%>
			
//	 	
		function incluir(form) {
				if(validar(form)) {
					form.submit();
				}
			}				
	function validar(form) {
				if(form.fecha.value.replace(/ /gi,'')=='') {
					alert("<%=rb.getString("requestSacop.occurrenceDateNotValid")%>");
					return false;
				}	

				// Falta por incluir validacion que la fecha no sea 60 dias anteriores a la fecha del dia
	 			if (form.fecha.value.replace(/ /gi,'') > '<%=dateSystem%>') {
					alert("<%=rb.getString("requestSacop.occurrenceDateNotValid")%>");
            		return false;
        		}			
				
	 			if(form.descripcion.value.replace(/ /gi,'')=='') {
					alert("<%=rb.getString("requestSacop.descriptionSacopNotValid")%>");
					return false;
				}
				return true;
			}	
		
//
		</script>
	</head>
	
	<body class="bodyInternas" >
		<%if(request.getAttribute("master")==null){%>
		<form name="forma" action="saveSacopRequest.do">
		<table width="100%" cellspacing="5" >
			<tr>
				<td colspan="2">
                    <table class="clsTableTitle" width="100%" cellSpacing="0" cellPadding="2" align=center border=0>
                        <tbody>
                            <tr>
                                <td class="td_title_bc">
                                    <%=rb.getString("requestSacop.title")%>
                                </td>
                            </tr>
                        </tbody>
                    </table>
				</td>
			</tr>
			<tr>
				<td class="titleLeft" height="22" style="background: url(img/btn160.png); background-repeat: no-repeat" width="29%" valign="middle">
					<%=rb.getString("requestSacop.applicant")%>
				</td>
				<td class="td_gris_l">
					<%=user.getUser()%> - <%=user.getCargo().getCargo()%> [ <%=user.getArea().getArea()%> ]
				</td>
			</tr>
			<tr>
				<td class="titleLeft" height="22" style="background: url(img/btn160.png); background-repeat: no-repeat" width="29%" valign="middle">
					<%=rb.getString("requestSacop.nameRegister")%>
				</td>
				<td>
					<input type="text" size="70" value="<%=doc.getNameDocument()%>" readonly style="background-color:transparent;border:1px solid white;"/>
				</td>
			</tr>
			<tr>
				<td class="titleLeft" height="22" style="background: url(img/btn160.png); background-repeat: no-repeat" width="29%" valign="middle">
					<%=rb.getString("requestSacop.folderProcess")%>
				</td>
				<td>
					<input type="text" size="70" value="<%=HandlerStruct.getIdNodeName(ToolsHTML.parseInt(doc.getIdNode()))%>" readonly style="background-color:transparent;border:1px solid white;"/>
				</td>
			</tr>
			<tr>
				<td class="titleLeft" height="22" style="background: url(img/btn160.png); background-repeat: no-repeat" width="29%" valign="middle">
					<%=rb.getString("requestSacop.class")%>
				</td>
				<td>
               		<input type="text" size="70" value="<%=Constants.registerclassTable.get(doc.getIdRegisterClass())%>" readonly style="background-color:transparent;border:1px solid white;"/>
				</td>
			</tr>
			<tr style="display:none;">
				<td class="titleLeft" height="22" style="background: url(img/btn160.png); background-repeat: no-repeat;" width="29%" valign="middle">
					<%=rb.getString("requestSacop.origen")%>
				</td>
				<td>
					<select name="origen" style="width:447px;">
						<option value="0">Seleccione un valor...</option>
						<%for(int i=0;i<listaOrigen.size();i++){
							titulo = listaOrigen.get(i);%>
							<option value="<%=titulo.getNumtitulosplanillas()%>"><%=titulo.getTitulosplanillas()%></option>
						<%}%>
					</select>
				</td>
			</tr>
			<tr>
				<td class="titleLeft" height="22" style="background: url(img/btn160.png); background-repeat: no-repeat" width="29%" valign="middle">
					<%=rb.getString("requestSacop.occurrenceDate")%>
				</td>
				<td>
					<table class="td_title_bc" cellspacing="0" cellpadding="0" border="0">
						<tr>
							<td>
       							<input type="text" name="fecha" readonly maxlength="10" size="10" value="" />
                        		<div id="calendario1"></div>
							</td>
							<td>&nbsp;
        						<input type="button" name="btnDateFrom" onclick="showCalendar(this.form.fecha,'calendario1');" value='>>' class="boton" style="width:20px;"></input>
							</td>
						</tr>
					</table>
				</td>
			</tr>
			<tr>
				<td class="titleLeft"  style="background: url(img/btn160.png); background-repeat: no-repeat;vertical-align: top;padding-top:5px;" width="29%" >
					<%=rb.getString("requestSacop.descriptionSacop")%>
				</td>
				<td>
					<textarea name="descripcion" rows="10" style="width:447px;"></textarea>
				</td>
			</tr>
			<tr>
				<td class="titleLeft" height="22" style="background: url(img/btn160.png); background-repeat: no-repeat" width="29%" valign="middle">
					<%=rb.getString("requestSacop.manager")%>
				</td>
				<td>
					<select name="responsable" style="width:447px;">
						<%for (Iterator it = listaResponsable.values().iterator(); it.hasNext();) {
							Users usu = (Users) it.next();%>
							<option value="<%=usu.getIdPerson()%>"><%=usu.getNameUser()%></option>
						<%}%>
					</select>
				</td>
			</tr>
			<tr>
				<td colspan="2" align="center">
					<input type="button" class="boton" value="<%=rb.getString("requestSacop.register")%>" onClick="javascript:incluir(this.form);" />
				</td>
			</tr>
		</table>
		</form>
		<%}%>
		
		<table width="100%" cellspacing="5" >
			<tr>
				<td colspan="2">
                    <table class="clsTableTitle" width="100%" cellSpacing="0" cellPadding="2" align=center border=0>
                        <tbody>
                            <tr>
                                <td class="td_title_bc">
                                    <%=rb.getString("requestSacop.historyAll")%>
                                </td>
                            </tr>
                        </tbody>
                    </table>
				</td>
			</tr>
			<tr>
				<td class="td_gris_l" colspan="2">
					<iframe src="loadSacopHistoryAll.do?idDocumentRelatedFilter=<%=request.getParameter("idDocument")%>" width="100%" height="800px" style="margin:0px;padding:0px;" frameborder="0"></iframe>
				</td>
			</tr>
		</table>
		
	</body>
</html>
