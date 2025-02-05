<!--**
 * Title: contacts.jsp <br/>
 * Copyright: (c) 2004 Desige Servicios de Computación<br/>
 * @author Ing. Nelson Crespo
 * @author Ing. Simón Rodriguéz. (SR)
 * @version WebDocuments v1.0
 * <br/>
 * Changes:<br/>
 * <ul>
 *      <li> 27-08-2004 (NC) Creation </li>
 *      <li> 11-10-2006 (SR) Cambio de una etiqueta</li>
 *      <li> 24-05-2006 (NC) Cambios para Mostrar el Botón Cancelar
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
    StringBuffer onLoad = new StringBuffer("");
    String ok = (String)session.getAttribute("usuario");
    String info = (String)request.getAttribute("info");
    if (ToolsHTML.checkValue(ok)) {
        onLoad.append(" onLoad=\"self.status='").append(rb.getString("pagin.welcome")).append(" ");
        onLoad.append(ok).append("'");
    } else{// El Usuario No se ha Logeado.... => Lo reboto...
        response.sendRedirect(rb.getString("href.logout"));
    }
    if (ToolsHTML.checkValue(info)){
        onLoad.append(";alert('").append(info).append("')");
    }
    if (onLoad.length()>0){
        onLoad.append("\"");
    }
    String cmd = (String)request.getAttribute("cmd");
    if (cmd==null){
        cmd = SuperActionForm.cmdLoad;
    }
    pageContext.setAttribute("cmd",cmd);
    String inputReturn = (String)session.getAttribute("input");
    if (inputReturn==null){
        inputReturn = "";
    }
    String valueReturn = (String)session.getAttribute("value");
    if (valueReturn==null){
        valueReturn = "";
    }
    boolean showLink = false;
%>
<logic:notPresent name="notShow" scope="request">
    <%
        showLink = true;
    %>
</logic:notPresent>
<html>
<head>
<title><%=rb.getString("principal.title")%></title>
<jsp:include page="meta.jsp" /> 
<link href="estilo/estilo.css" rel="stylesheet" type="text/css">
<script type="text/javascript">

    function incluir() {
        location.href = "insertContacts.jsp";
     }

    function validateCheck() {
    	var formaSelection = document.getElementById("selection");
        var set = false;
        var valueSelect = false;
        for (var i=0; i < formaSelection.length;i++) {
            if (formaSelection.elements[i].checked) {
                msg = formaSelection.elements[i].value;
                p = msg.indexOf(",");
                if (formaSelection.ids.value.length > 0 ) {
                    ids = msg.substring(0,p);
                    msg = msg.substring(p+1,msg.length);
                    p = msg.indexOf(",");
                    formaSelection.ids.value = formaSelection.ids.value + "," + ids;
                    formaSelection.emails.value = formaSelection.emails.value + ";" + msg.substring(0,p);
                    formaSelection.names.value = formaSelection.names.value + ";" + msg.substring(p+1,msg.length);
                } else {
                    ids = msg.substring(0,p);
                    msg = msg.substring(p+1,msg.length);
                    p = msg.indexOf(",");
                    formaSelection.ids.value = ids;
                    formaSelection.emails.value = msg.substring(0,p);
                    formaSelection.names.value  = msg.substring(p+1,msg.length);
                }
                valueSelect = true;
            }
        }
        return valueSelect;
    }

    function check() {
    	var forma = document.getElementById("selection");
    	
        if (!validateCheck()){
<%--            alert('<%=rb.getString("error.selectValue")%>');--%>
<%--            return false;--%>
            setValue("","","",true);
        } else {
            setValue(forma.emails.value,forma.names.value,forma.ids.value,true);
        }
     }

     function checkSelect() {
        validateCheck();
        var forma = document.getElementById("selection");
<%--        alert("forma.selected.value: " + forma.selected.value);--%>
        if (forma.selected.value.length > 0) {
            forma.selected.value = forma.selected.value + "," + forma.ids.value;
        } else {
            forma.selected.value = forma.ids.value;
        }
<%--        alert("forma.selected.value: " + forma.selected.value);--%>
        forma.ids.value = "";
        setValue(forma.emails.value,forma.names.value,forma.selected.value,false);
     }

    function youAreSure(){
    	var forma = document.getElementById("selection");
        if (!validateCheck()) {
            alert('<%=rb.getString("error.selectValue")%>');
            //return false;
        } else {
            if (confirm("<%=rb.getString("areYouSure")%>")) {
                forma.cmd.value = "<%=SuperActionForm.cmdDelete%>";
                forma.action = "insertContacts.do";
                forma.submit();
            }
        }
    }

    function cancelar() {
        <logic:present name="notShow" scope="request">
            window.close();
        </logic:present>
        <logic:notPresent name="notShow" scope="request">
            location.href = "inbox.jsp";
        </logic:notPresent>
    }

    function setValue(id,desc,ids,close) {
        <%=inputReturn%>
        <%=valueReturn%>
        <%=session.getAttribute("ids")!=null?session.getAttribute("ids"):""%>
        if (close) {
            window.close();
        }
    }

    function showList(forma,contactSystems,showAddressBook,showLinkSystem) {
        forma.action = "contacts.do";
        checkSelect();
        forma.contactSystems.value = contactSystems;
        forma.showAddressBook.value = showAddressBook;
        forma.showLinkSystem.value = showLinkSystem;
        forma.submit();
    }

    function buscarContacto(objeto) {
	    valor=objeto.value;
	    objeto.value="";
    	showListPerson(document.getElementById("selection"),false,'',true,false,valor);
    }

    function showListPerson(forma,contactSystems,showAddressBook,showLinkSystem,mostrarLink,valor) {
        forma.action = "contacts.do";
        forma.contactSystems.value = contactSystems;
        if(mostrarLink){
        	forma.showAddressBook.value = showAddressBook;
        	forma.showLinkSystem.value = showLinkSystem;
        }
       	forma.search.value = valor;
        forma.submit();
    }
</script>
</head>

<body class="bodyInternas" <%=onLoad.toString()%>>
<%if(false && !(request.getAttribute("showLinkSystem")!=null)){%>
   <table cellspacing="0" cellpadding="0" border="0">
   	<tr></td>
        <%=ToolsHTML.printMenuInnerToolBar(ToolsHTML.getMenuMailsII(rb),"",true,false, rb)%>
   	</td></tr>
   </table>
<%}%>
    <table align=center border=0 width="100%">
        <tr>
            <td colspan="3">
                <table class="clsTableTitle" width="100%" cellSpacing=0 cellPadding=2 align=center border=0>
                    <tbody>
                        <tr>
                            <td class="td_title_bc" height="23 px" >
                                <%=rb.getString("contacts.lista")%>
                            </td>
                        </tr>
                    </tbody>
                </table>
            </td>
        </tr>
        <!--<tr>
            <td colspan="3">
                &nbsp;
            </td>
        </tr>  -->
        <logic:present name="showLinkSystem" scope="request">
            <tr>
                <td colspan="3">
                    <a class="ahref_b" href="javascript:showList(document.getElementById('selection'),true,true,'')">
                        <%=rb.getString("contacts.showUsersSystem")%>
                    </a>
                </td>
            </tr>
        </logic:present>

        <logic:present name="showAddressBook" scope="request">
            <tr>
                <td colspan="3">
                    <a class="ahref_b" href="javascript:showList(document.getElementById('selection'),false,'',true)">
                        <%=rb.getString("contacts.showAddress")%>
                    </a>
                </td>
            </tr>
        </logic:present>

        <tr id="buscarTexto">
        	<td colspan="3" align="center">
                <input type="text" name="nombre" id="nombre" style="width: 250 px;height: 19px" value="<%=request.getAttribute("search")!=null?request.getAttribute("search"):""%>"/>
                &nbsp;&nbsp;
                <logic:present name="showAddressBook" scope="request">
                	<input type="button" class="boton" value="<%=rb.getString("btn.search")%>" onClick="javascript:showListPerson(document.getElementById('selection'),true,true,'', true,document.getElementById('nombre').value);"/>
                </logic:present>
                <logic:notPresent name="showAddressBook" scope="request">
                	<logic:present name="showLinkSystem" scope="request">
    	            	<input type="button" class="boton" value="<%=rb.getString("btn.search")%>" onClick="javascript:showListPerson(document.getElementById('selection'),false,'',true,true,document.getElementById('nombre').value);"/>
                	</logic:present>
                	<logic:notPresent name="showLinkSystem" scope="request">
	                	<input type="button" class="boton" value="<%=rb.getString("btn.search")%>" onClick="javascript:showListPerson(document.getElementById('selection'),false,'',true,false,document.getElementById('nombre').value);"/>
                	</logic:notPresent>
                </logic:notPresent>
            </td>
        </tr>            
            
        <tr>
            <td class="td_orange_l_b barraBlue" width="35%">
                <%=rb.getString("contacts.apellido")%>
            </td>
            <td class="td_orange_l_b barraBlue" width="35%">
                <%=rb.getString("contacts.nombre")%>
            </td>
            <td class="td_orange_l_b barraBlue" width="30%">
                <%=rb.getString("contacts.email")%>
            </td>
        </tr>
        <form name="selection" id="selection" action="">
            <input type="hidden" name="cmd" value=""/>
            <input type="hidden" name="emails" value="<%=request.getAttribute("emails")!=null?request.getAttribute("emails"):""%>"/>
            <input type="hidden" name="names" value="<%=request.getAttribute("names")!=null?request.getAttribute("names"):""%>"/>
            <input type="hidden" name="selected" value="<%=request.getAttribute("selected")!=null?request.getAttribute("selected"):""%>"/>
            <input type="hidden" name="ids" value=""/>

            <input type="hidden" name="input" value="<%=request.getAttribute("input")%>"/>
            <input type="hidden" name="value" value="<%=request.getAttribute("value")%>"/>
            <input type="hidden" name="nameForm" value="<%=request.getAttribute("nameForm")%>"/>
            <input type="hidden" name="contactSystems" value=""/>
            <input type="hidden" name="showAddressBook" value=""/>
            <input type="hidden" name="showLinkSystem" value=""/>
            <input type="hidden" name="search" value=""/>

            <logic:present name="Lista" scope="session">
                <logic:iterate id="contacts" name="Lista" indexId="ind" type="com.desige.webDocuments.mail.forms.AddressForm" scope="session">
                    <%
                        int item = ind.intValue()+1;
                        int num = item%2;
                    %>
                    <tr class='fondo_<%=num%>'>
                        <td class="td_gris_l">
                            <input name="contacts" type= "checkbox" value="<%=contacts.getIdAddress()%>,<%=contacts.getEmail()%>,<%=contacts.getApellido() + " " + contacts.getNombre()%>"
                            <%=contacts.isSelected()?"checked":""%>>
                            <%
                                if (showLink) {
                            %>
                                <a class="ahref_b" href="editContacts.do?idAddress=<%=contacts.getIdAddress()%>">
                            		<%=contacts.getApellido()%>
                                </a>
                            <% } else {%>
                            		<%=contacts.getApellido()%>
                            <% } %>
                        </td>
                        <td class="td_gris_l">
                            <%=contacts.getNombre()%>
                        </td>
                        <td class="td_gris_l">
                            <%=contacts.getEmail()%>
                        </td>
                    </tr>
                </logic:iterate>
            </logic:present>
        </form>
        <tr class="<%=!showLink?"":"none"%>">
            <td colspan="3">
<%--            <td colspan="3" class="td_orange_l_b">--%>
                <center>
                    <logic:present name="notShow" scope="request">
                        <input type="button" class="boton" value="<%=rb.getString("btn.ok")%>" onClick="javascript:check();" />
                        &nbsp;
                    </logic:present>

                    <logic:notPresent name="notShow" scope="request">
                        <input type="button" class="boton" value="<%=rb.getString("btn.incluir")%>" onClick="javascript:incluir();" />
                        &nbsp;
                        <input type="button" class="boton" value="<%=rb.getString("mail.delete")%>" onclick="javascript:youAreSure();" />
                        &nbsp;
                    </logic:notPresent>

			        <logic:present name="notShow" scope="request">
                        <input type="button" class="boton" value="<%=rb.getString("btn.cancel")%>" onClick="javascript:cancelar();" />
        			</logic:present>
        			<logic:notPresent name="notShow" scope="request">
                        <!--<input type="button" class="boton" value="<%=rb.getString("btn.back")%>" onClick="javascript:cancelar();" />-->
        			</logic:notPresent>
                    
                </center>
            </td>
        </tr>
    </table>
<%--</table>--%>
</body>
</html>
<script language="javascript" event="onload" for="window">
	try{
		window.parent.agregarNuevoUrl();
		document.getElementById("buscarTexto").className="none";
	}catch(e){}
</script>
