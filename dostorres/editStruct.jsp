<!-- /**
 * Title: editStruct.jsp<br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * @author Ing. Nelson Crespo (NC)
 * @author Ing. Simon Rodríguez (SR)
 * @version WebDocuments v3.0
 * <br/>
 * Changes:<br/>
 * <ul>
 *      <li> 01/07/2005 (SR) Se cambio en el select de html, el comparar el bean id por descript por el usuario dueño.(SR)</li>
 *      <li> 30/06/2006 (NC) Cambios para editar el Nombre de una Carpeta independientemente de Su estado </li> 
 *     <li>  13/07/2006 (NC) Cambios para heredar o no los Prefijos de las Carpetas </li>
  </ul>
 */ -->
 <style>
.textareaSin {
	overflow: hidden;
	overflow: hidden;
	}
 </style>
<%@ page import="java.util.ResourceBundle,
                 com.desige.webDocuments.utils.ToolsHTML,
                 com.desige.webDocuments.utils.beans.SuperActionForm,
                 java.util.Hashtable,
                 java.util.ArrayList,
                 com.desige.webDocuments.utils.DesigeConf,
                 com.desige.webDocuments.structured.forms.BaseStructForm"%>
<%@ page import="com.desige.webDocuments.utils.Constants"%>

<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<%@ page language="java" %>
<%
    ResourceBundle rb = ToolsHTML.getBundle(request);
    String color = "";
    String cadenaVacia = "";
    String onLoad = ToolsHTML.getMensajePages(request,rb);
    if (onLoad==null){
        response.sendRedirect(rb.getString("href.logout"));
    }
    String cmd = (String)session.getAttribute("cmd");
    if (cmd==null){
        cmd = SuperActionForm.cmdLoad;
    }
    String nodeActive = (String)session.getAttribute("nodeActive");
    pageContext.setAttribute("cmd",cmd);
    Hashtable tree = (Hashtable)session.getAttribute("tree");
    BaseStructForm editForm = null;
    
    ArrayList listaTypeDocs = new ArrayList(); //(ArrayList)request.getAttribute("listaTypeDocs");
    
%>
<html>
<head>
<title><%=rb.getString("principal.title")%></title>
<jsp:include page="meta.jsp" /> 
<link href="estilo/estilo.css" rel="stylesheet" type="text/css">
<script language="JavaScript" src="estilo/funciones.js"></script>
<script language="JavaScript">
    function edit(num) {
        document.urls.cmd.value = "<%=SuperActionForm.cmdEdit%>";
        document.urls.id.value = num;
        document.urls.submit();
    }

    function cancelar(form) {
        form.action="loadStructMain.do";
        form.cmd.value="<%=SuperActionForm.cmdLoad%>";
        form.submit();
    }

    function incluir(form){
        form.cmd.value = "<%=SuperActionForm.cmdNew%>";
        form.submit();
    }
    
    function salvar(form){
        if (validar(form)){
            var ant = form.name;
            //quitamos los enters
        	form.name.value=form.name.value.replace(/\r\n/g,";");
        	if(ant==form.name) {
	        	form.name.value=form.name.value.replace(/\r/g,"");
	        	form.name.value=form.name.value.replace(/\n/g,";");
        	}
        	
        	// validamos la lista de distribucion
        	if(toListDist=='0' && form.toListDistNameUser.value==toListDistNameUser) {
        		form.toListDistNameUser.value="";
        	}
        	
            form.target = "_self";
            form.submit();
        }
    }

    function updateCheck(check,field){
        if (check.checked){
            field.value = "0";
        } else{
            field.value = 1;
        }
    }

    function updateCheckAssociate(check,field,campo) {
        if (check.checked) {
            field.value = "1";
        } else{
            field.value = "0";
            if (campo) {
                campo.enabled = true;
            }
        }
    }

    function updateCheckCargo(check,field){
        if (check.checked){
            field.value = "1";
        } else{
            field.value = 0;
        }
    }

    String.prototype.trim = function() { return this.replace(/^\s*|\s*$/g,""); };

    function validar(form){
        if (form.name.value.trim().length == 0){
            alert("<%=rb.getString("err.invalidName")%>");
            return false;
        }
        if (form.cmd.value!="<%=SuperActionForm.cmdEdit%>"){
            if (!validateRadio(form)){
                alert("<%=rb.getString("err.invalidNameIcon")%>");
                return false;
            }
        }
        if (form.cmd.value=="<%=SuperActionForm.cmdEdit%>"){
            if (form.nodeType.value==1){
                var valor = form.daysWF.value;
                if (form.expireWF.value==0){
                    if (valor==null || valor.length==0){
                        alert('<%=rb.getString("err.notDays")%>');
                        return false;
                    }
                }
            }
        }
        if (form.expireWFI) {
            if (form.expireWFI.checked&&form.daysWF.value.length==0) {
                alert('<%=rb.getString("err.notDays")%>');
                return false;
            }
        }
       if (form.timeDocVencWFI) {
            if (form.timeDocVencWFI.checked&&form.txttimeDocVenc.value.length==0) {
                alert('<%=rb.getString("err.notDaysVenc")%>');
                return false;
            }
        }

        if (form.checkvijenToprintI ) {
            if (form.checkvijenToprintI.checked && (form.vijenToprint.value.length==0 || form.vijenToprint.value == "0")) {
                alert('<%=rb.getString("err.notNumberExpireDay")%>');
                return false;
            }
        }



         if (form.majorKeep) {
            if (form.majorKeep.value.length==0) {
                alert('<%=rb.getString("err.notHistAprobaciones")%>');
                return false;
            }
        }
        if (form.minorKeep) {
            if (form.minorKeep.value.length==0) {
                alert('<%=rb.getString("err.notHistRevisiones")%>');
                return false;
            }
        }


        return true;
    }

    function setNodeType(form,msg){
        if (msg=='<%=DesigeConf.getProperty("imgFolder")%>'){
            form.nodeType.value = "3";//Folder
        } else
            if (msg=='<%=DesigeConf.getProperty("imgLocation")%>'){
                form.nodeType.value = "1";//Location Devolver
            } else{
                form.nodeType.value = "2";//Anywhere
            }
    }

    function validateRadio(form) {
        var msg;
        msg = form.nameIcon.value;
        if (msg!=null){
            setNodeType(form,msg);
            return true;
        } else {
            for (var i=0; i < form.nameIcon.length; i++) {
                if (form.nameIcon[i].checked){
                    msg = form.nameIcon[i].value;
                    setNodeType(form,msg);
                    return true;
                }
            }
        }
        return false;
    }

    function youAreSure(form){
      if (confirm("<%=rb.getString("areYouSure")%>")) {
         form.next.value = "loadAllStruct.do";
         form.target = "_parent";
         form.cmd.value = "<%=SuperActionForm.cmdDelete%>";
         form.submit();
      }
    }

    function paging_OnClick(pageFrom){
        document.formPagingPage.from.value = pageFrom;
        document.formPagingPage.submit();
    }
    
    function openUser() {
    	abrirVentana('listUser.do',600,500);
    }
    
    function getListUser() {
    	if(document.editarStruct != null){
    		return document.editarStruct.toListDistNameUser.value;
    	} else if(document.insertStruct != null){
    		return document.insertStruct.toListDistNameUser.value;
    	}
    }
    
    function openUserReturn(list) {
    	document.forms[0].toListDistNameUser.value=list;
    	var lista = list.split(",");
    	var caja = document.getElementById("listaUser");
		while (caja.firstChild) {
		  caja.removeChild(caja.firstChild);
		}

    	var color="";
    	for(var i=0; i<lista.length; i++) {
    		color=(color=="#efefef"?"#d4d4d4":"#efefef");
    		var div = document.createElement("div");
    		div.style.backgroundColor=color;
    		div.appendChild(document.createTextNode ("\u00a0"+lista[i]));
    		caja.appendChild(div);
    	}
    }
</script>
</head>

<body class="bodyInternas" <%=onLoad%>>

<br/>
<table align=center border=0 width="100%">
    <logic:notEqual name="cmd" value="<%=SuperActionForm.cmdLoad%>">
        <%
            String forma = "/editStruct.do";
            String nameIcon = "";
            String next = "loadStructMain.do";
            if (SuperActionForm.cmdInsert.equalsIgnoreCase(cmd)){
                forma = "/newStruct.do";
            } else {
                nameIcon = ((BaseStructForm)session.getAttribute("editarStruct")).getNameIcon();
            }
        %>
        <tr>
            <td>
                <html:form action="<%=forma%>">
                    <html:hidden property="cmd"/>
                    <html:hidden property="idNode"/>
                    <html:hidden property="toListDistNameUser" />
                    <input type="hidden" name="nexPage" value="<%=next%>"/>
                    
                    <%
                        BaseStructForm dataForm = (BaseStructForm)tree.get(nodeActive);
                        String rout = dataForm.getRout();
                        String nodeSelected = nodeActive;
                        boolean edit = true;
                        String nodeTypeNew = (String)request.getAttribute("nodeTypeNew");
                        if (!ToolsHTML.checkValue(nodeTypeNew)) {
                            nodeTypeNew = "0";
                        }
                        boolean editFields = "T".equals(request.getSession().getAttribute("emptyNodes"));
                        //request.getSession().getAttribute("emptyNodes")!=null?request.getSession().getAttribute("emptyNodes").equals("T"):true;
                    %>
                    <logic:equal name="cmd" value="<%=SuperActionForm.cmdInsert%>">
                        <html:hidden property="idNodeParent" value="<%=nodeActive%>"/>
                        <%
                            editFields = true;
                            dataForm = (BaseStructForm)session.getAttribute("insertStruct");
                        %>
                    </logic:equal>

                    <logic:notEqual name="cmd" value="<%=SuperActionForm.cmdInsert%>">
                        <%
                            nodeTypeNew = dataForm.getNodeType();
                            edit = false;
                        %>
                    </logic:notEqual>
                    <%
                        int num = Integer.parseInt(nodeTypeNew);
                    %>
                      <input type="hidden" name="idNodeSelected" value="<%=nodeSelected%>"/>
                      <table width="100%" border="0">
                        <tr>
                            <td colspan="2">
                                <%
                                    String style = "clsTableTitle";
                                    String height = "21";
                                    if (rout!=null&&rout.length() > 75) {
                                        style = "clsTableTitle2";
                                        height = "42";
                                    }
                                %>
                                <table class="<%=style%>" width="100%" cellSpacing=0 cellPadding=2 align=center border=0>
                                    <tbody>
                                        <tr>
                                            <td class="td_title_bc" height="<%=height%>">
                                                <%=rb.getString("cbs.location") + ": "+rout%>
                                            </td>
                                        </tr>
                                    </tbody>
                                </table>
                            </td>
                        </tr>
                        <tr>
                            <td class="td_gris_l" colspan="2">
                                &nbsp;
                            </td>
                        </tr>
                        <tr>
                            <td colspan="2">
                                <table class="clsTableTitle" width="100%" cellSpacing=0 cellPadding=2 align=center border=0>
                                    <tbody>
                                        <tr>
                                            <td class="td_title_bc" height="21">
                                                <%=rb.getString("cbs.update")%>
                                            </td>
                                        </tr>
                                    </tbody>
                                </table>
                            </td>
                        </tr>

                        <tr>
                            <td class="titleLeft" style="background: url(img/btn140.png); background-repeat: no-repeat" width="19%" height="26" valign="middle">
                                <%=rb.getString("cbs.name")%>:
                            </td>
                            <td class="td_gris_l">
                                <!--<html:text property="name" size="70" styleClass="classText"/>-->
                                <%
                                    String s = "'" + rb.getString("user.character.allowed") + "'";
                                %>
                                <textarea rows="1" cols="50" name="name" class="classText" style="overflow:hidden;overflow:hidden;height:20px;" onchange="allowCharacter(this, <%= s %>);"><%=ToolsHTML.isEmptyOrNull(dataForm.getName(), "") %></textarea>
                            </td>
                        </tr>
                        <input type="hidden" name="idNodeSelected" value="<%=dataForm.getIdNode()%>"/>
                        <html:hidden property="nodeType" value="<%=nodeTypeNew%>"/>
                        <html:hidden property="rout" value="<%=rout%>"/>
                        <tr>
                            <td class="titleLeft" style="background: url(img/btn140.png); background-repeat: no-repeat" height="26" valign="middle">
                                <%=rb.getString("cbs.icon")%>:
                            </td>
                            <td>
                                <%=ToolsHTML.getImgs(nameIcon,nodeTypeNew,edit)%>
                            </td>
                        </tr>
                            <% if ((num==3)||(num==2)) { %>
                                <tr>
                                    <td class="titleLeft" style="background: url(img/btn140.png); background-repeat: no-repeat" height="26" valign="middle">
                                        <%=rb.getString("cbs.parent.prefix")%>:
                                    </td>
                                    <td class="td_gris_l">
                                    <logic:equal name="cmd" value="<%=SuperActionForm.cmdInsert%>">
                                        <%
                                            String parentPrefix = "";
                                            BaseStructForm newFolder = (BaseStructForm)session.getAttribute("insertStruct");
                                            if (newFolder.getHeredarPrefijo() == Constants.permission) {
                                                if (!ToolsHTML.isEmptyOrNull(dataForm.getParentPrefix())) {
                                                    parentPrefix = dataForm.getParentPrefix();
                                                } else {
                                                    if (!ToolsHTML.isEmptyOrNull(dataForm.getPrefix())) {
                                                        parentPrefix = dataForm.getPrefix();
                                                    }
                                                }
                                            } else {
                                                if (dataForm.getHeredarPrefijo() == Constants.permission) {
                                                    parentPrefix = dataForm.getParentPrefix();
                                                } else {
                                                    parentPrefix = dataForm.getPrefix();
                                                }
                                            }
                                        %>
                                        <html:text property="parentPrefix" value="<%=parentPrefix%>" size="48" readonly="true" styleClass="classText"/>

                                        <%=ToolsHTML.showCheckBox("heredarPrefijoI","updateCheckAssociate(this.form.heredarPrefijoI,this.form.heredarPrefijo,this.form.typePrefix)",
                                           dataForm.getHeredarPrefijo(),1,false)%><%=rb.getString("cbs.prefixHer")%>

                                           <html:hidden property="heredarPrefijo" value="0"/>
                                    </logic:equal>
                                   <logic:notEqual name="cmd" value="<%=SuperActionForm.cmdInsert%>">
                                        <html:text property="parentPrefix" readonly="true" styleClass="classText" size="48"/>
                                       <logic:present name="hayDocs" scope="session">
                                           <!--si hay documentos me los inhabilita-->
                                         <logic:equal name="hayDocs" scope="session" value="true">
                                            <%=ToolsHTML.showCheckBox("heredarPrefijoI","updateCheckAssociate(this.form.heredarPrefijoI,this.form.heredarPrefijo,this.form.typePrefix)",
                                               dataForm.getHeredarPrefijo(),1,true)%><%=rb.getString("cbs.prefixHer")%>
                                          </logic:equal>
                                           <logic:notEqual name="hayDocs" scope="session" value="true">
                                              <%=ToolsHTML.showCheckBox("heredarPrefijoI","updateCheckAssociate(this.form.heredarPrefijoI,this.form.heredarPrefijo,this.form.typePrefix)",
                                               dataForm.getHeredarPrefijo(),1,false)%><%=rb.getString("cbs.prefixHer")%>
                                           </logic:notEqual>
                                        </logic:present>
                                           <html:hidden property="heredarPrefijo"/>
                                    </logic:notEqual>
                                    </td>
                                </tr>
                                <tr>
                                    <td class="titleLeft" style="background: url(img/btn140.png); background-repeat: no-repeat" height="26" valign="middle">
                                        <%=rb.getString("cbs.prefix")%>:
                                    </td>
                                    <td>
                                        <html:text property="prefix" readonly="<%=!editFields%>" styleClass="classText" size="70"/>
                                    </td>
                                </tr>
                            <% } else { %>
                                <html:hidden property="prefix" value=""/>
                            <% } %>
                            <!-- Begin data Location -->
                        <tr>
                            <td class="titleLeft" style="background: url(img/btn140.png); background-repeat: no-repeat" width="18%" height="26" valign="middle">
                                <%=rb.getString("cbs.owner")%>:
                            </td>
                            <td>
                            	<table>
                            		<tr>
			                            <td>
				                            <%
				                                String userOwner = "";
				                            %>
				                           <logic:present name="editarStruct">
				                            <%
				                                editForm = (BaseStructForm)session.getAttribute("editarStruct");
				                                if (editForm!=null&&editForm.getOwner()!=null) {
				                                    userOwner = editForm.getOwner();
				                                }
				                                
				                            %>
					                        <logic:equal name="cmd" value="<%=SuperActionForm.cmdInsert%>">
					                        	<% editForm.setOwnerResponsible(editForm.getOwner()); %>
					                        </logic:equal>

					                        </logic:present>
				                            <select name="owner" size="1" style="width:210px;" styleClass="classText">
				                                <logic:present name="allUsers" scope="session">
				                                    <logic:iterate id="bean" name="allUsers" scope="session" type="com.desige.webDocuments.utils.beans.Search">
				                                        <logic:equal value="<%=userOwner%>" property="descript" name="bean" >
				                                            <option value="<%=bean.getId()%>" selected="true"><%=bean.getDescript()%></option>
				                                        </logic:equal>
				                                        <logic:notEqual value="<%=userOwner%>" property="descript" name="bean" >
				                                            <logic:notEqual value="<%=userOwner%>" property="id" name="bean" >
				                                                <option value="<%=bean.getId()%>"><%=bean.getDescript()%></option>
				                                            </logic:notEqual>
				                                        </logic:notEqual>
				                                    </logic:iterate>
				                                </logic:present>
				                            </select>
			                            </td>

			                            <td class="titleLeft" style="background: url(img/btn140.png); background-repeat: no-repeat;display:<%=nodeTypeNew.equals("2")?"":"none"%>" width="18%" height="26" valign="middle" >
			                                <%=rb.getString("cbs.ownerResponsible")%>:
			                            </td>
			                            <td style="display:<%=nodeTypeNew.equals("2")?"":"none"%>">
				                            <%
				                                String userOwnerResponsible = "";
				                            %>
				                           <logic:present name="editarStruct">
					                        </logic:present>
				                            <%
				                                editForm = (BaseStructForm)session.getAttribute("editarStruct");
				                                if (editForm!=null&&editForm.getOwnerResponsible()!=null) {
				                                    userOwnerResponsible = editForm.getOwnerResponsible();
				                                }
				                            %>
					                        
					                        <%if(request.getAttribute("tieneSacopsAbiertas")!=null && request.getAttribute("tieneSacopsAbiertas").toString().equals("true")) {%>
			                                    <logic:iterate id="bean" name="allUsers" scope="session" type="com.desige.webDocuments.utils.beans.Search">
			                                        <logic:equal value="<%=userOwnerResponsible%>" property="descript" name="bean" >
			                                            <input type="hidden" name="ownerResponsible" value="<%=bean.getId()%>" /> 
			                                        </logic:equal>
			                                    </logic:iterate>
				                            	<input type="text" readonly="true" value="<%=userOwnerResponsible%>"/>
				                            <%} else {%>
					                            <select name="ownerResponsible" size="1" style="width:210px;" class="classTextx" >
					                                <logic:present name="allUsers" scope="session">
					                                    <logic:iterate id="bean" name="allUsers" scope="session" type="com.desige.webDocuments.utils.beans.Search">
					                                        <logic:equal value="<%=userOwnerResponsible%>" property="descript" name="bean" >
					                                            <option value="<%=bean.getId()%>" selected="true"><%=bean.getDescript()%></option>
					                                        </logic:equal>
					                                        <logic:notEqual value="<%=userOwnerResponsible%>" property="descript" name="bean" >
					                                            <logic:notEqual value="<%=userOwnerResponsible%>" property="id" name="bean" >
					                                                <option value="<%=bean.getId()%>"><%=bean.getDescript()%></option>
					                                            </logic:notEqual>
					                                        </logic:notEqual>
					                                    </logic:iterate>
					                                </logic:present>
					                            </select>
				                            <%}%>
			                            </td>

                            		</tr>
                            	</table>
                            </td>
                        </tr>
                            <% if (num==1) {%>
                                <!--<tr>-->
                                    <!--<td class="titleLeft" style="background: url(img/btn140.png); background-repeat: no-repeat" height="26" valign="middle">-->
                                        <%--<%=rb.getString("cbs.number")%>:--%>
                                    <!--</td>-->
                                    <!--<td>-->
                                        <%--<html:text property="number"/> --%>
                                    <!--</td>-->
                                <!--</tr>-->
                              <logic:present name="hayDocs" scope="session">
                                  <logic:equal name="hayDocs" scope="session" value="true">
                                     <tr>
                                        <td class="titleLeft" style="background: url(img/btn140x4.png); background-repeat: no-repeat" height="44" valign="middle">
                                            <%=rb.getString("cbs.majorId")%>:
                                        </td>
                                        <td class="td_gris_l">

                                            <%=ToolsHTML.getRadioButton(rb,"cbs.majorIdNumber","majorIdVisible",dataForm.getMajorId(),null,true)%>
                                             <%-- Luis Cisneros 02/03/07
                                            Si el campo esta deshabilitado no se envia al form y se pierde el valor
                                            Hay que colocar el valor en un input hidden
                                            --%>
                                            <input type="hidden" name="majorId" value="<%=dataForm.getMajorId()%>" />
                                        </td>
                                    </tr>
                                  </logic:equal>
                                  <logic:notEqual name="hayDocs" scope="session" value="true">
                                   <tr>
                                        <td class="titleLeft" style="background: url(img/btn140x4.png); background-repeat: no-repeat" height="44" valign="middle">
                                            <%=rb.getString("cbs.majorId")%>:
                                        </td>
                                        <td class="td_gris_l">
                                            <%=ToolsHTML.getRadioButton(rb,"cbs.majorIdNumber","majorId",dataForm.getMajorId(),null)%>
                                        </td>
                                    </tr>
                                  </logic:notEqual>
                              </logic:present>
                                <tr>
                                    <td class="titleLeft" style="background: url(img/btn140x4.png); background-repeat: no-repeat" height="44" valign="middle">
                                        <%=rb.getString("cbs.majorKeep")%>:
                                    </td>
                                    <td class="td_gris_l">
                                        <%=rb.getString("cbs.maintain")%>
                                        <html:text property="majorKeep" size="3" styleClass="classText"/>
                                        <%=rb.getString("cbs.aprobed")%>
                                    </td>
                                </tr>
                             <logic:present name="hayDocs" scope="session">
                               <logic:equal name="hayDocs" scope="session" value="true">
                                 <tr>
                                    <td class="titleLeft" style="background: url(img/btn140x4.png); background-repeat: no-repeat" height="44" valign="middle">
                                        <%=rb.getString("cbs.minorId")%>:
                                    </td>
                                    <td class="td_gris_l">
                                        <%=ToolsHTML.getRadioButton(rb,"cbs.majorIdNumber","minorIdVisible",dataForm.getMinorId(),null,true)%>
                                       <%-- Luis Cisneros 02/03/07
                                        Si el campo esta deshabilitado no se envia al form y se pierde el valor
                                        Hay que colocar el valor en un input hidden
                                        --%>
                                        <input type="hidden" name="minorId" value="<%=dataForm.getMinorId()%>" />
                                    </td>
                                 </tr>
                               </logic:equal>
                                <logic:notEqual name="hayDocs" scope="session" value="true">
                                 <tr>
                                    <td class="titleLeft" style="background: url(img/btn140x4.png); background-repeat: no-repeat" height="44" valign="middle">
                                        <%=rb.getString("cbs.minorId")%>:
                                    </td>
                                    <td class="td_gris_l">
                                        <%=ToolsHTML.getRadioButton(rb,"cbs.majorIdNumber","minorId",dataForm.getMinorId(),null)%>
                                    </td>
                                 </tr>
                               </logic:notEqual>

                               <logic:equal name="hayDocs" scope="session" value="true">
                                <tr>
                                    <td class="titleLeft" height="44" style="background: url(img/btn140x4.png); background-repeat: no-repeat" valign="middle">
                                        <%=rb.getString("param.typePrefix")%>
                                     </td>
                                    <td class="td_gris_l">

                                        <%=ToolsHTML.getRadioButton(rb,"param.numTypePrefix","typePrefixVisible",(int) dataForm.getTypePrefix(),null,true)%>
                                        <%-- Luis Cisneros 02/03/07
                                        Si el campo esta deshabilitado no se envia al form y se pierde el valor
                                        Hay que colocar el valor en un input hidden
                                        --%>
                                        <input type="hidden" name="typePrefix" value="<%=dataForm.getTypePrefix()%>" />
                                    </td>
                                </tr>
                               </logic:equal>
                                <logic:notEqual name="hayDocs" scope="session" value="true">
                                <tr>
                                    <td class="titleLeft" height="44" style="background: url(img/btn140x4.png); background-repeat: no-repeat" valign="middle">
                                        <%=rb.getString("param.typePrefix")%>
                                     </td>
                                     
                                     <logic:equal name="cmd" value="<%=SuperActionForm.cmdInsert%>">
                                        <td class="td_gris_l">
                                            <!-- descendente por defecto -->
	                                        <%=ToolsHTML.getRadioButton(rb,"param.numTypePrefix","typePrefix",1,null)%>
	                                    </td>
                                     </logic:equal>
                                     <logic:notEqual name="cmd" value="<%=SuperActionForm.cmdInsert%>">
                                        <td class="td_gris_l">
                                            <%=ToolsHTML.getRadioButton(rb,"param.numTypePrefix","typePrefix",(int) dataForm.getTypePrefix(),null)%>
                                        </td>
                                     </logic:notEqual>
                                </tr>
                               </logic:notEqual>
                            </logic:present>
                            <%}%>
                        <!-- End data Location -->
                            <tr>
                                <td class="titleLeft" style="background: url(img/btn140x4.png); background-repeat: no-repeat" height="44" valign="middle">
                                    <%=rb.getString("cbs.description")%>:
                                </td>
                                <td>
                                    <html:textarea property="description" rows="5" cols="70" styleClass="classText"/>
                                </td>
                            </tr>
							<tr>
                                <td class="titleLeft" style="background: url(img/btn140x4.png); background-repeat: repeat-y" height="44" valign="middle">
									Lista de divulgaci&oacute;n
									<div style="font-size:10;;">
										Marque los usuarios que seran notificados
										de la publicacion de una nueva versi&oacute;n
										del los documentos contenidos en esta localidad
									</div>
								</td>
								<td>
									<table>
										<tr>
											<td>
												<div id="listaUser" style="overflow: auto;height:150px;width:300px;border:1px solid #afafaf;">
													<%if(editForm!=null && editForm.getToListDistNameUser()!=null) {
														String[] list = editForm.getToListDistNameUser().split(",");
														for(int i=0; i<list.length; i++){%>
													        <%if(editForm.getToListDist()==1){
													        	color=color.equals("#EFFBFB")?"#CEECF5":"#EFFBFB";
													          } else {
													          	color=color.equals("#efefef")?"#d4d4d4":"#efefef";
													        }%>
							                            	<div style="background-color:<%=color%>">
							                            		&nbsp;<%=list[i]%>
							                            	</div>
						                            	<%}
						                            }%>
												</div>
											</td>
											<td valign="top">
												<input type="button" class="boton" value="<%=rb.getString("wf.users")%>" onClick="openUser(this.form);"/>
												<br>
												<%if(editForm.getToListDist()==0){%><br>
													<div style="font-size:12;font-family:Tahoma;width:110;">
													La lista de usuarios 
													es heredada
													de un nodo superior.
													</div>
												<%}%>
											</td>
											<td>
												&nbsp;
											</td>
										</tr>
									</table>
								</td>
							</tr>
                            
                            <% if (num==1) {%>
                                <tr>
                                    <td class="titleLeft" style="background: url(img/btn140x4.png); background-repeat: repeat-y"  height="44"  valign="middle">
                                        <%=rb.getString("cbs.setWF")%>:
                                    </td>
                                    <td class="td_gris_l">
                                        <%=ToolsHTML.showCheckBox("disableUserWFI","updateCheck(this.form.disableUserWFI,this.form.disableUserWF)",
                                           dataForm.getDisableUserWF(),0,false)%><%=rb.getString("cbs.disableUserWF")%>
                                           <html:hidden property="disableUserWF"/>
                                        <br/>
                                        <%if(ToolsHTML.copyContents()){%>
                                        <%=ToolsHTML.showCheckBox("copyI","updateCheck(this.form.copyI,this.form.copy)",
                                           dataForm.getCopy(),0,false)%><%=rb.getString("cbs.copy")%>
                                           <html:hidden property="copy"/>
                                        <br/>
                                        <%}%>
                                        <%=ToolsHTML.showCheckBox("sequentialI","updateCheck(this.form.sequentialI,this.form.sequential)",
                                           dataForm.getSequential(),0,false)%><%=rb.getString("cbs.sequential")%>
                                           <html:hidden property="sequential"/>
                                        <br/>
                                        <%=ToolsHTML.showCheckBox("conditionalI","updateCheck(this.form.conditionalI,this.form.conditional)",
                                           dataForm.getConditional(),0,false)%><%=rb.getString("cbs.conditional")%>
                                           <html:hidden property="conditional"/>
                                        <br/>
                                        <%=ToolsHTML.showCheckBox("notifyI","updateCheck(this.form.notifyI,this.form.notify)",
                                           dataForm.getNotify(),0,false)%><%=rb.getString("cbs.notify")%>
                                           <html:hidden property="notify"/>
                                        <br/>
                                        <%=ToolsHTML.showCheckBox("expireWFI","updateCheck(this.form.expireWFI,this.form.expireWF)",
                                           dataForm.getExpireWF(),0,false)%><%=rb.getString("cbs.expiresWF")%>&nbsp;
                                           <html:text property="daysWF" size="2" styleClass="classText"/>
                                           <html:hidden property="expireWF"/>
                                        <br/>
                                           <%=ToolsHTML.showCheckBox("cargoWFI","updateCheckCargo(this.form.cargoWFI,this.form.showCharge)",
                                            dataForm.getShowCharge(),1,false)%>&nbsp;<%=rb.getString("cbs.showNameUser")%>&nbsp;
                                           <html:hidden property="showCharge"/>
<%--                                        <br/>--%>
<%--                                           <%=ToolsHTML.showCheckBox("timeDocVencWFI","updateCheckCargo(this.form.timeDocVencWFI,this.form.timeDocVenc)",--%>
<%--                                            dataForm.getTimeDocVenc(),1,false)%>&nbsp;<%=rb.getString("cbs.timeDocVenc")%>&nbsp;--%>
<%--                                              <html:text property="txttimeDocVenc" size="2"/>--%>
<%--                                           <html:hidden property="timeDocVenc"/>--%>
<%--                                         <br>--%>
                                    </td>
                                </tr>
                                <tr>
                                    <td class="titleLeft" style="background: url(img/btn140x4.png); background-repeat: no-repeat" height="44" valign="middle">
                                        <%=rb.getString("cbs.setDoc")%>:
                                    </td>
                                    <td class="td_gris_l">
                                         <%=ToolsHTML.showCheckBox("timeDocVencWFI","updateCheckCargo(this.form.timeDocVencWFI,this.form.timeDocVenc)",
                                            dataForm.getTimeDocVenc(),1,false)%>&nbsp;<%=rb.getString("cbs.timeDocVenc")%>&nbsp;
                                         <html:text property="txttimeDocVenc" size="2" styleClass="classText"/>
                                         <html:hidden property="timeDocVenc"/>
                                         <br>
                                         <%=rb.getString("cbs.setDocPreI")%>
                                         <%=ToolsHTML.getSelectList(rb,"param.monthExpireDoc","cantExpDoc",dataForm.getCantExpDoc())%>
                                         <%=ToolsHTML.getSelectList(rb,"cbs.notItems","cbs.notItemsValue","unitExpDoc",dataForm.getUnitExpDoc())%>
                                         <%=rb.getString("cbs.setDocPreII")%>
                                    </td>
                                </tr>
                                <tr>
                                    <td class="titleLeft" style="background: url(img/btn140x4.png); background-repeat: no-repeat" height="44" valign="middle">
                                        <%=rb.getString("imp.vigimpr")%>
                                    </td>
                                    <td class="td_gris_l">
                                     <%=ToolsHTML.showCheckBox("checkvijenToprintI","updateCheckCargo(this.form.checkvijenToprintI,this.form.checkvijenToprint)",
                                         dataForm.getCheckvijenToprint(),1,false)%>&nbsp;<%=rb.getString("imp.vigimprdias")%>&nbsp;
                                         <html:hidden property="checkvijenToprint"/>
                                         <html:text property="vijenToprint" size="2" styleClass="classText"/>
                                    </td>
                                </tr>
                                <tr>
                                    <td class="titleLeft" style="background: url(img/btn140x4.png); background-repeat: no-repeat" height="44" valign="middle">
                                        <%=rb.getString("corr.borradores")%>
                                    </td>
                                    <td class="td_gris_l">
                                    <%=ToolsHTML.showCheckBox("checkborradorCorrelativoI","updateCheckCargo(this.form.checkborradorCorrelativoI,this.form.checkborradorCorrelativo)",
                                         dataForm.getCheckborradorCorrelativo(),1,false)%>&nbsp;<%=rb.getString("corr.borradoresdocs")%>&nbsp;
                                         <html:hidden property="checkborradorCorrelativo"/>
                                    </td>
                                </tr>
                            <% } %>
                        <tr>
                            <td colspan="2" align="center">
                                <input type="button" class="boton" value="<%=rb.getString("btn.save")%>" onClick="javascript:salvar(this.form);"/>
                                <logic:notEqual name="cmd" value="<%=SuperActionForm.cmdInsert%>">
                                    <html:hidden property="idNodeParent" value="<%=dataForm.getIdNodeParent()%>"/>
                                    &nbsp;
                                </logic:notEqual>
                                    &nbsp;
                                <input type="button" class="boton" value="<%=rb.getString("btn.cancel")%>" onClick="history.back();" />
                            </td>
                        </tr>
                     </table>
                </html:form>
            </td>
        </tr>
    </logic:notEqual>
</table>
</body>
</html>
<script>
	var toListDistNameUser = '<%=editForm.getToListDistNameUser()%>';
	var toListDist = '<%=editForm.getToListDist()%>';
	
	if(<%= SuperActionForm.cmdInsert.equalsIgnoreCase(cmd) %>){
		document.insertStruct.toListDistNameUser.value = '<%=editForm.getToListDistNameUser()%>';
	}
</script>
