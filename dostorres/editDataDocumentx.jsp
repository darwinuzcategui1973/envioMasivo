<!--
 * Title: editDataDocument.jsp
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * @author Ing. Nelson Crespo (NC)
 * @author Ing. Simón Rodriguéz (SR)
 * @version WebDocuments v3.0
 * <br/>
 * Changes:<br/>
 * <ul>
 *      <li> 01/07/2005 (SR) Se valido, en caso que la session tree este nula, se sustituye por la session arbol creada en editDocumentAction </li>
 *      <li> 04/07/2005 (SR) Se valido la fecha de aprobacion con la fecha de publicación. </li>
 *      <li> 06/07/2005 (SR) En cancelar(form), se cambio form.target=_self en vez de 'text'. </li>
 *      <li> 06/07/2005 (SR) Se comento la session arbol, ya que la creo en EditDocumentAction.java . </li>
 *      <li> 25/04/2006 (SR) Se sustituyo /WebDocuments/.. por ./..
 </ul>
-->
<%@ page import="java.util.ResourceBundle,
                 com.desige.webDocuments.utils.ToolsHTML,
                 java.util.Hashtable,
                 com.desige.webDocuments.utils.beans.Users,
                 com.desige.webDocuments.utils.beans.SuperActionForm,
                 com.desige.webDocuments.structured.forms.BaseStructForm,
                 com.desige.webDocuments.seguridad.forms.PermissionUserForm"%>
<%@ page import="com.desige.webDocuments.utils.DesigeConf" %>
<%@ page import="com.desige.webDocuments.persistent.managers.HandlerStruct,
				com.desige.webDocuments.persistent.managers.HandlerDocuments"%>

<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<%@ page language="java" %>
<%
    ResourceBundle rb = ToolsHTML.getBundle(request);
    String onLoad = ToolsHTML.getMensajePages(request,rb);
    if (onLoad==null){
        response.sendRedirect(rb.getString("href.logout"));
    }
    String nodeActive = (String)session.getAttribute("nodeActive");
    ToolsHTML.clearSession(session,"application.editDocuments");
    String dateSystem = ToolsHTML.date.format(new java.util.Date());
    ////System.out.println("dateSystem = " + dateSystem);
    
    ToolsHTML.setQualitySystem(session); 
%> 
<html>
<head>
<title><%=rb.getString("principal.title")%></title>
<jsp:include page="meta.jsp" /> 
<link href="estilo/estilo.css" rel="stylesheet" type="text/css">
<script type="text/javascript" src="./estilo/fechas.js"></script>
<script type="text/javascript" src="./estilo/popcalendar2.js"></script>
<script type="text/javascript" src="estilo/funciones.js"></script>

<logic:present name="showDocument">
    <bean:define id ="doc" name="showDocument" type="com.desige.webDocuments.document.forms.BaseDocumentForm" scope="session"/>
    <script type="text/javascript">
    //SIMON 04 DE JULIO 2005 INICIO
    <% if(doc.getDateApproved()!=null && doc.getDateApproved().length()>9){%>
	    var Fecha_aprobacion = "<%=doc.getDateApproved()%>";
	    Fecha_aprobacion=Fecha_aprobacion.substr(6,4)+"-"+Fecha_aprobacion.substr(3,2)+"-"+Fecha_aprobacion.substr(0,2);
    <%}else{%>
	    var Fecha_aprobacion="01-01-2005";
    <%}%>
   //SIMON 04 DE JULIO 2005 FIN
   </script>
</logic:present>
<logic:notPresent name="showDocument">
	<script type="text/javascript">
	   var Fecha_aprobacion="01-01-2005";
	</script>
</logic:notPresent>
<script type="text/javascript">

    function openCalendar(dateField,nameForm,dateValue,text){
        window.open("getDate.jsp?field=" + dateField + "&nameForm="+nameForm+"&valueForm="+dateValue+"&text="+text,"","width=275,height=170,resizable=no,scrollbars=yes,left=460,top=250");
    }

    function validDateInput(valor) {
        if (!validarFecha(valor)){
            alert('<%=rb.getString("err.badDatePublicWF")%>');
            return false;
        }
        if (valor>=Fecha_aprobacion) {
           return true;
        }else{
           alert('<%=rb.getString("application.fechapublicada1")%>'+valor+" "+'<%=rb.getString("application.fechapublicada2")%>'+Fecha_aprobacion);
           return false;
        }
    }

    function cancelar(form){
    	var form = document.getElementById("Selection");
        //ydavila Ticket 001-00-003023
    	form.action = "showDataDocument.do";
    	//form.action="showPublished.do";
        form.target = "_self";
        form.cmd.value="<%=SuperActionForm.cmdLoad%>";
        form.submit();
    }
        
	String.prototype.trim = function() { return this.replace(/^\s*|\s*$/g,""); };

    function salvar(forma) {
    
        if (forma.nameDocument && forma.nameDocument.value.trim()=="") {
            alert('<%=rb.getString("err.notName")%>');
            return false;
        }
       //alert(forma.action);
     <logic:notEqual name="showDocument" property="lastVersionApproved" value="0">
        /*if (forma.checkactiveFactory.value=="0"){
             if (forma.docPublic.value != "0") {
                alert('<%=rb.getString("wonderware.activefactorypropiedadeserror")%>');

                 return false;
             }
        }*/
     </logic:notEqual>
        if (forma.toForFiles) {
        	forma.razonFiles.value="";
            if (forma.toForFiles.value != forma.toForFilesBefore.value) {
            	forma.razonFiles.value="El documento puede pertenercer a un expediente";
        	}
        }
        if (forma.docPublic) {
            if (forma.docPublic.value == "0") {
                if (!validDateInput(forma.datePublic.value)) {
                    return false;
                    //forma.submit();
                }
//            } else {
//                forma.submit();
            }
//        } else {
            forma.submit();
        }
        forma.submit();
    }

    function updateCheck(check,field,texto){
        if (check.checked){
            field.value = "0";
        } else{
            field.value = 1;
        }
    }

    function showWindow(pages,title,idDoc,value,width,height){
        var hWnd = null;
        hWnd = window.open(pages+"?read=true&value="+value+"&idDoc="+idDoc+"&title="+title,"","width="+width+",height="+height+",resizable=no,scrollbars=yes,statusbar=yes,left=250,top=250");
        if ((document.window != null) && (!hWnd.opener)){
            hWnd.opener = document.window;
        }
    }

    function showLinks(pages,input,nameForm,value,width,height){
        var hWnd = null;
        hWnd = window.open(pages+"?input="+input+"&nameForma="+nameForm+"&value="+value,"","width="+width+",height="+height+",resizable=no,scrollbars=yes,statusbar=yes,left=250,top=250");
        if ((document.window != null) && (!hWnd.opener)){
            hWnd.opener = document.window;
        }
    }
    
	function wnd(pages,input,nameForm,value,title,width,height,read){
	    var hWnd = null;
	    //alert(pages+"&input="+input+"&nameForma="+nameForm+"&value="+value+"&title="+title+"&read="+read);
	    hWnd = window.open(pages+"&input="+input+"&nameForma="+nameForm+"&value="+value+"&title="+title+"&read="+read,"","width="+width+",height="+height+",resizable=no,scrollbars=yes,statusbar=yes,left=100,top=150");
	    if ((document.window != null) && (!hWnd.opener)){
	        hWnd.opener = document.window;
	    }
	}
    
	function evaluaChequeo(forma,parametro,titleForm){
            if (parametro=='n'){
                wnd('changeProperties.jsp?tipo=n','razonNameDocument','showDocument',forma.nameDocument.value,titleForm ,800,470,'0');
            }else if (parametro=='p'){
                wnd('changeProperties.jsp?tipo=p','razonOwner','showDocument',forma.owner.value,titleForm ,800,470,'0');    
            }

    }

	function verCamposDefinidos(tipo) {
		var id="";
		valor = ","+tipo+",";
		for(var i=1;i<100;i++){
			id='d'+i;
			if(document.getElementById(id)){
				if(document.getElementById(id).className.indexOf(valor)!=-1){
					document.getElementById(id).style.display="";
				} else {
					document.getElementById(id).style.display="none";
				}
			} else {
				break;
			}
		}
	}
    
</script>
</head>

<body class="bodyInternas" <%=onLoad%>>

<logic:present name="showDocument">
    <bean:define id ="doc" name="showDocument" type="com.desige.webDocuments.document.forms.BaseDocumentForm" scope="session"/>
    <%
        BaseStructForm dataForm = null;
        boolean readOnly = false;
        boolean readOnlyName = false;
        
        //Si el documento no se encuentra Aprobado
        if ((doc.getApproved() != null && "0".equalsIgnoreCase(doc.getApproved())) || (!"0".equalsIgnoreCase(doc.getLastVersionApproved()))) {
            readOnly = true;
            readOnlyName = true;
        }
        
        String number1 = doc.getNumber() != null ? doc.getNumber() : "";
        String numDoc = doc.getNumberGen();
        Users usuario = (Users)session.getAttribute("user");
        
        boolean isLastVersionIsPublic = HandlerDocuments.isLastVersionIsPublic(numDoc);
        
        
        /*
        boolean isTypeRegister = false;
        if(doc.getTypeDocument()!=null && ((doc.getTypeDocument().compareTo(HandlerDocuments.TypeDocumentsRegistro)==0) || (doc.getTypeDocument().compareTo(DesigeConf.getProperty("typeDocs.docRegister"))==0))){
        	isTypeRegister = true;
        }

		Hashtable securityDocs = null;
		securityDocs = ToolsHTML.checkDocsSecurity(securityDocs, usuario, numDoc);
		PermissionUserForm securityDocsfrm = null;

		if (!ToolsHTML.isEmptyOrNull(number1)) {
			securityDocsfrm = (PermissionUserForm) securityDocs.get(numDoc);
		}

        //Si no está en Modo Sólo Lectura (no está aprobado), es un Registro 
        // y el usuario tiene permiso para cambiar propiedades se permite cambiar nombre
		if (securityDocsfrm != null) {
			//if (isTypeRegister && "1".equalsIgnoreCase(String.valueOf(securityDocsfrm.getToEdit())) && !readOnly) {
			if ("1".equalsIgnoreCase(String.valueOf(securityDocsfrm.getToEditDocs())) && !readOnly) {
				readOnlyName = false;
			}
		}*/

        Hashtable tree= (Hashtable) session.getAttribute("tree");
        if(tree!=null){
        	dataForm = (BaseStructForm)((Hashtable)session.getAttribute("tree")).get(nodeActive);
        }
        
    %>

    <html:form action="/editDataDocument.do">
        <html:hidden property="cmd" value="<%=SuperActionForm.cmdEdit%>" />
        <html:hidden property="numVer" />
        <input type="hidden" name="editando" value="true"/>
        <table align=center border=0 width="100%">
            <tr>
                <td valign="top" colspan="2" >
                    <table class="clsTableTitle" width="100%" cellSpacing=0 cellPadding=2 align=center border=0>
                        <tbody>
                            <tr>
                                <td class="td_title_bc">
                                    <%if (dataForm!=null){%>
                                    <%=rb.getString("cbs.location") + ": "%> + <%=ToolsHTML.cortarCadena(dataForm.getRout())%>
                                    <%}%>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </td>
            </tr>
            <tr>
                <td class="titleLeft" height="26" style="background: url(img/btn140.png); background-repeat: no-repeat" width="20%" valign="middle">
                    <%=rb.getString("cbs.name")%>: 
                </td>
                <td width="*" class="td_gris_l">
                	<%if(readOnlyName){%>
    	                <html:hidden property="nameDocument"/>
	                    <span id="lblNameDocument"><bean:write name="doc" property="nameDocument"/></span>
                    <%}else{%>
	                    <html:text property="nameDocument" size="110" styleClass="classText" readonly="<%=readOnlyName%>" />
                    <%}%>
                    <%if(isLastVersionIsPublic) {%>
					<logic:present name="changeProperties">
						<logic:equal name="changeProperties" value="true">
		                    &nbsp;&nbsp;&nbsp;
							<a  class="boton botonLinkBack" id="lnkEditar" name="lnkEditar" onClick="javascript:evaluaChequeo(document.showDocument,'n','<%=rb.getString("title.changePropertiesName")%>');" alt="<%=rb.getString("btn.change")%>" >
		                        <%=rb.getString("btn.change")%>
							</a>
						</logic:equal>
					</logic:present>
					<%}%>
					<textarea name="razonNameDocument" style="display:none"></textarea>
                </td>
            </tr>
            <tr>
                <td class="titleLeft" height="26" style="background: url(img/btn140.png); background-repeat: no-repeat" valign="middle">
                    <%=rb.getString("cbs.owner")%>:
                </td>
                <td class="td_gris_l">
                    <%
                        String selected = doc.getOwner();
                        pageContext.setAttribute("ownerDoc",selected);
                    %>
                        <span id="lblOwner0" style="<%=(!readOnly?"display:none":"")%>"><bean:write name="showDocument" property="nameOwner" /></span>
                        <span id="lblOwner1" style="<%=(readOnly?"display:none":"")%>">
                        <html:select property="owner" styleClass="classText" style="width: 250px">
                            <logic:present name="userSystem" scope="session" >
                                <logic:iterate id="bean" name="userSystem" scope="session" type="com.desige.webDocuments.utils.beans.Search">
                                    <html:option value="<%=bean.getId()%>" ><%=bean.getDescript()%></html:option>
                                </logic:iterate>
                            </logic:present>
                        </html:select>
                        </span>
                        &nbsp;&nbsp;&nbsp;
	                    <%if(isLastVersionIsPublic) {%>
						<logic:present name="changeProperties">
							<logic:equal name="changeProperties" value="true">
		        				<a  class="boton botonLinkBack" id="lnkEditar" name="lnkEditar" onClick="javascript:evaluaChequeo(document.showDocument,'p','<%=rb.getString("title.changePropertiesOwner")%>');" alt="<%=rb.getString("btn.change")%>" >
		        					&nbsp;&nbsp;&nbsp;
		    	                    <%=rb.getString("btn.change")%>
								</a>
							</logic:equal>
						</logic:present>
                        <%}%>
					<textarea name="razonOwner" style="display:none"></textarea>
                </td>
            </tr>
            <tr>
                <td class="titleLeft" height="26" style="background: url(img/btn140.png); background-repeat: no-repeat" valign="middle">
                    <%=rb.getString("doc.number")%>:
                </td>
                <td class="td_gris_l">
                    <!-- Si el Documento no se encuentra Aprobado -->
                    <logic:notEqual name="doc" value="5" property="statuDoc">
                        <!-- Si el Documento se encuentra en Borrador -->
                        <logic:equal name="doc" value="1" property="statuDoc">
                            <bean:write name="showDocument" property="prefix" /><html:text name="showDocument" property="number" value="<%=number1%>" />
                        </logic:equal>
                        <!-- Si el Documento se encuentra en Otro Statu -->
                        <logic:notEqual name="doc" property="statuDoc" value="1">
                            <bean:write name="showDocument" property="prefix" /><bean:write name="showDocument" property="number"/>
                        </logic:notEqual>
                    </logic:notEqual>
                    <!-- Si el Documento se encuentra Aprobado -->
                    <logic:equal name="doc" value="5" property="statuDoc">
                        <bean:write name="showDocument" property="prefix" /><bean:write name="showDocument" property="number"/>
                    </logic:equal>
                     <!--<!-- Si el Documento se encuentra Borrador -->
                    <%--<logic:notEqual name="doc" property="statu" value="1">--%>
                        <%--<bean:write name="showDocument" property="prefix" /><bean:write name="showDocument" property="number"/>--%>
                    <%--</logic:notEqual>--%>
                </td>
            </tr>

            <tr>
                <td class="titleLeft" height="26" style="background: url(img/btn140.png); background-repeat: no-repeat" valign="middle">
                    <%=rb.getString("doc.type")%>:
                </td>
                <%
                    selected = doc.getTypeDocument();
                %>
                <td class="td_gris_l">
                    <logic:present name="typesDocuments" scope="session">
                        <html:select property="typeDocument" styleClass="classText" disabled="<%=readOnly%>" style="width: 250px">
                            <logic:iterate id="bean" name="typesDocuments" scope="session" type="com.desige.webDocuments.utils.beans.Search">
                                <%--<html:option value="<%=bean.getId()%>">--%>
                                    <%--<%=bean.getDescript()%>--%>
                                <%--</html:option>--%>
                                <% if (selected.compareTo(bean.getId())==0) { %>
                                    <option value="<%=bean.getId()%>" selected><%=bean.getDescript()%></option>
                                <% } else {%>
                                    <option value="<%=bean.getId()%>"><%=bean.getDescript()%></option>
                                <% } %>
                            </logic:iterate>
                        </html:select>
                    </logic:present>
                    <logic:notPresent name="typesDocuments" scope="session">
                        <%=rb.getString("E0006")%>
                    </logic:notPresent>
                </td>
            </tr>
            <%
                selected = doc.getNormISO();
                //System.out.println("selected = " + selected);
            %>
            <logic:equal value="true" name="showNorms">
                <tr>
                    <td class="titleLeft" height="26" style="background: url(img/btn140.png); background-repeat: no-repeat" valign="middle">
                        <%=rb.getString("doc.normISO")%>:
                    </td>
                    <td>
                        <input type="hidden" name="normISO" id="normISO" value="<%=selected%>" />
                        <input type="button" name="showNorms" value="<%= rb.getString("doc.relate.normISO") %>" onclick="showNormsPopUp('normISO');" />
                    </td>
                </tr>
            </logic:equal>
            <logic:notEqual name="showDocument" property="lastVersionApproved" value="0">
                <tr>
                    <td class="titleLeft" height="26" style="background: url(img/btn140.png); background-repeat: no-repeat" valign="middle">
                        <%=rb.getString("doc.public")%>
                    </td>
                    <td class="td_gris_l">
                        <%=ToolsHTML.showCheckBox("docPublicI","updateCheck(this.form.docPublicI,this.form.docPublic)",doc.getDocPublic(),"0")%>
                        <html:hidden property="docPublic"/>
                        <html:text property="datePublic" readonly="true" maxlength="10" size="10" />
                        
						<a href="#" onclick='showCalendar(document.showDocument.datePublic,"calendario")' ><img src="images/calendario2.gif" border="1" ></a>
						<a href='#' onclick='showDocument.datePublic.value=""; showDocument.initialDay.value="";  showDocument.initialMonth.value="";  showDocument.initialYear.value=""; ' value='<%=rb.getString("btn.clear")%>' ><img src="images/eraser.gif" border="0" alt='<%=rb.getString("btn.clear")%>' text='<%=rb.getString("btn.clear")%>'></a>
                        
                        <input type=hidden name="initialYear" value='<%= ((request.getParameter("initialYear") != null) ? request.getParameter("initialYear") : "")%>'>
                        <input type=hidden name="initialMonth" value='<%= ((request.getParameter("initialMonth") != null) ? request.getParameter("initialMonth") : "")%>'>
                        <input type=hidden name="initialDay" value='<%= ((request.getParameter("initialDay") != null) ? request.getParameter("initialDay") : "")%>'>
		                <span id="calendario"></span>
                        
                    </td>
                </tr>
            </logic:notEqual>
            <tr>
                <td class="titleLeft" height="26" style="background: url(img/btn120x4.png); background-repeat: no-repeat" valign="middle">
                    <%=rb.getString("sch.lote")%>:
                </td>
                <td class="td_gris_l">
                    <input type="text" name="loteDoc" value="<bean:write name="doc" property="loteDoc"/>">
                </td>
            </tr>
            <tr style="display:<%=doc.getOwner().equals(usuario.getUser())?"":"none"%>">
                <td class="titleLeft" height="26" style="background: url(img/btn120x4.png); background-repeat: no-repeat" valign="middle">
                    <%=rb.getString("doc.toForFiles")%>:
                </td>
                <td class="td_gris_l" style="display:<%=doc.getOwner().equals(usuario.getUser())?"":"none"%>" >
                    <%=ToolsHTML.showCheckBox("toForFilesI","updateCheck(this.form.toForFilesI,this.form.toForFiles)",doc.getToForFiles(),"0")%>
                    <html:hidden property="toForFiles"/>
                    <input type="hidden" name="toForFilesBefore" value="<bean:write name="doc" property="toForFiles"/>">
   					<textarea name="razonFiles" style="display:none"></textarea>
                </td>
            </tr>
            <tr>
                <td class="titleLeft" height="26" style="background: url(img/btn120x4.png); background-repeat: no-repeat" valign="middle">
                    <%=rb.getString("doc.url")%>:
                </td>
                <td class="td_gris_l">
                    <html:textarea property="URL" cols="110" rows="2" styleClass="classText" />
                </td>
            </tr>

            <tr>
                <td class="titleLeft" height="26" style="background: url(img/btn120x4.png); background-repeat: no-repeat" valign="middle">
                    <%=rb.getString("doc.keys")%>:
                </td>
                <td class="td_gris_l">
                    <html:textarea property="keys" cols="110" rows="3" styleClass="classText"/>
                </td>
            </tr>

            <tr>
                <td class="titleLeft" height="26" style="background: url(img/btn120x4.png); background-repeat: no-repeat" valign="middle">
                    <%=rb.getString("doc.descript")%>:
                </td>
                <td class="td_gris_l">
                      <!--El status 1 es borrador-->
                    <logic:equal name="doc" value="1" property="statu">
                        <html:textarea property="descript" cols="110" rows="3" styleClass="classText" readonly="false"/>
                    </logic:equal>
                    <logic:notEqual name="doc" property="statu" value="1">
                        <!--< html:textarea property="descript" cols="110" rows="5" styleClass="classText" readonly="true"/>-->
                        <html:textarea property="descript" cols="110" rows="3" styleClass="classText" readonly="false"/>
                    </logic:notEqual>

                </td>
            </tr>
            <%
                if (!readOnly && !(1==doc.getStatu())) {
            %>
                    <tr>
                        <td class
