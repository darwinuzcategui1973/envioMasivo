<!--/**
 * Title: associateActUser.jsp <br>
 * Copyright: (c) 2003 Consis International<br>
 * Company: Consis International (CON)<br>
 * @author Nelson Crespo (NC)
 * @version Acsel-e v2.2
 * <br>
 * Changes:<br>
 * <ul>
 *      <li> 2005-11-10 (NC) Bug in check method Fixed </li>
 * <ul>
 */-->

<%@ page import="java.util.ResourceBundle,
                 com.desige.webDocuments.utils.beans.SuperActionForm,
                 com.desige.webDocuments.utils.ToolsHTML,
                 com.desige.webDocuments.utils.beans.PaginPage,
                 com.desige.webDocuments.utils.beans.Users,
                 com.desige.webDocuments.enlaces.forms.UrlsActionForm"%>

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
    } else {
        response.sendRedirect(rb.getString("href.logout"));
    }
    if (ToolsHTML.checkValue(info)) {
        onLoad.append(";alert('").append(info).append("')");
    }
    if (onLoad.length()>0){
        onLoad.append("\"");
    }
    String inputReturn = (String)session.getAttribute("input");
    if (inputReturn==null){
        inputReturn = "";
    }
    String valueReturn = (String)session.getAttribute("value");
    if (valueReturn==null){
        valueReturn = "";
    }
    String userAssociate = request.getParameter("userAssociate");
    if (ToolsHTML.isEmptyOrNull(userAssociate)) {
        userAssociate = "";
    }
    String type = (String)ToolsHTML.getAttribute(request,"type");
    if (ToolsHTML.isEmptyOrNull(type)) {
        type = "0";
    }

    String id = "";
    String valorSeleccionado = (String) session.getAttribute("idarea");
    if (ToolsHTML.isEmptyOrNull(valorSeleccionado)){
        valorSeleccionado=(String) request.getParameter("idarea");
    }

	String numero = (String) session.getAttribute("number");
	String inputNew = (String) session.getAttribute("inputNew");
    String valueNew = (String) session.getAttribute("valueNew");
    String nameFormaNew = (String) session.getAttribute("nameFormaNew");
%>
<html>
<head>
<title><%=rb.getString("principal.title")%></title>
<jsp:include page="meta.jsp" /> 
<link href="estilo/estilo.css" rel="stylesheet" type="text/css">
<script type="text/javascript">
    var selecteds = null;
    var selectedsArray = new Array();
    <%  if (ToolsHTML.isEmptyOrNull(userAssociate)) {
            userAssociate = "";
    %>
            selecteds = new Array();
    <%
        } else {
    %>
            selecteds = new Array(<%=userAssociate%>);
    <%
        }
    %>
    
    function llenarArray(){
    	var string = '<%=userAssociate%>';
    	var separator = ',';
		var stringArray = string.split(separator);

		for (var i=0; i < stringArray.length; i++){
    		selectedsArray[i] = stringArray[i];
		}
    }
    
    function isCheck(valor){
    	result = false;
    	if (selectedsArray!=null){
    		for (i=0;i<selectedsArray.length;i++) {
    			if(selectedsArray[i]==valor){
    				result = true;
    				break;
    			}
    		}
    	}
    	return result;
    }

    function selectChecks(){
    	var formaSelection = document.getElementById("selection");
    	llenarArray();
        for (var i=0; i < formaSelection.length;i++) {
            if (isCheck(formaSelection.elements[i].value)){
                formaSelection.elements[i].checked = true;
            }
        }
    }
    
    function setValue(id){
        <%=inputReturn%>
<%--        opener.document.subActivity.executant.value=id;--%>
<%--        <%=valueReturn%>--%>
<%--        window.close();--%>
    }

    <logic:present name="closeWindow" scope="request">
        <%=valueReturn%>
        cancelar();
    </logic:present>

    function cancelar(){
        window.close();
    }

    function check() {
    	var formaSelection = document.getElementById("selection");
        if (!validateCheck()) {
            if (formaSelection.userAssociate.value == '') {
                setValue('');
            }
            document.associate.submit();
            <%--window.close();--%>
        } else {
            setValue(formaSelection.userAssociate.value);
            document.associate.userAssociate.value = formaSelection.userAssociate.value;
            document.associate.submit();
        }
     }

     function validateCheck() {
    	var formaSelection = document.getElementById("selection");
        var set = false;
        var valueSelect = false;
<%--        items = new Array();--%>
<%--        alert("Hay: "+selecteds.join(','));--%>
        for (var i=0; i < formaSelection.length;i++) {
            if (formaSelection.elements[i].checked){
                msg = formaSelection.elements[i].value;
                if (formaSelection.userAssociate.value.length > 0) {
                    formaSelection.userAssociate.value += "," + msg;
                } else {
                    formaSelection.userAssociate.value = msg;
                }
                valueSelect = true;
            }
        }
        return valueSelect;
    }

     function validateCheck2(forma) {
    	var formaSelection = document.getElementById("selection");
        var set = false;
        var valueSelect = false;
<%--        items = new Array();--%>
<%--        alert("Hay: "+selecteds.join(','));--%>
		forma.userAssociate.value = "";
        for (var i=0; i < formaSelection.length;i++) {
            if (formaSelection.elements[i].checked){
                msg = formaSelection.elements[i].value;
                if (forma.userAssociate.value.length > 0) {
                    forma.userAssociate.value += "," + msg;
                } else {
                    forma.userAssociate.value = msg;
                }
                valueSelect = true;
            }
        }
        return valueSelect;
    }
    
    function paging_OnClick(pageFrom) {
    	var formaSelection = document.getElementById("selection");
        document.formPagingPage.from.value = pageFrom;
        validateCheck();
        document.formPagingPage.userAssociate.value = formaSelection.userAssociate.value;
        document.formPagingPage.submit();
    }

     function getDatos(){
        document.cargarArea.action = "loadActUsers.do"
        document.cargarArea.areaSel.value = document.cargarArea.idarea.options[document.cargarArea.idarea.selectedIndex].value;

     	if(document.cargarArea.idarea.value=="") {
	    	document.cargarArea.porArea.value=null
	        document.cargarArea.areaSel.value = "";
	    }

        //validateCheck2(document.cargarArea);
        document.cargarArea.submit();
    }
    
    </script>
</head>
<body class="bodyInternas" <%=onLoad%>>
    <logic:present name="usuarios" scope="session">
        <%
            String from = request.getParameter("from");
            if(from==null || from.equals(""))
            	from = "0";
            String size = (String)session.getAttribute("size");
            Users users = (Users)session.getAttribute("user");
            int record = (users==null?10:users.getNumRecord());
            PaginPage Pagingbean = ToolsHTML.getBeanPaginPage(from,Integer.parseInt(size),String.valueOf(record));
            
        %>
        <form name="associate" method="post" action="saveActUsers.do">
            <input type="hidden" name="userAssociate" value="<%=userAssociate%>"/>
            <input type="hidden" name="type" value="<%=type%>"/>
        </form>

         <table cellSpacing=1 cellPadding=1 align=center border="0" width="100%">
		<tr>
             <td class="td_title_bc" colspan="2">
                  <%=rb.getString("admin.areas")%>
             </td>
        </tr>
        <tr>
            <td class="td_title_bc" colspan="2">
         	<form action="/loadActUsers.do" name="cargarArea">
            <input type="hidden" name="userAssociate" value="<%=userAssociate%>"/>
            <input type="hidden" name="number" value="<%=numero%>"/>
            <input type="hidden" name="type" value="<%=type%>"/>
            <input type="hidden" name="porArea" value="1"/>
            <input type="hidden" name="areaSel" value="<%=valorSeleccionado%>"/>
            <input type="hidden" name="input" value="<%=inputNew%>"/>
            <input type="hidden" name="value" value="<%=valueNew%>"/>
            <input type="hidden" name="nameForma" value="<%=nameFormaNew%>"/>
            <select onchange="getDatos();" name="idarea" >
                    <option value="">
                       ------------ <%=rb.getString("lista.todas")%> ------------
                    </option>
                <logic:iterate id="idarea" name="area" scope="session" type="com.desige.webDocuments.utils.beans.Search">
                    <%
                   if (idarea.getId().equalsIgnoreCase(valorSeleccionado)) {
                    %>
                    <option value="<%=idarea.getId()%>" selected>
                        <%=idarea.getDescript()%>
                    </option>
                    <%

                        id = idarea.getId();
                    } else {
                        if (valorSeleccionado==null) {
                            valorSeleccionado = idarea.getId();

                            id = idarea.getId();
                        }
                    %>
                    <option value="<%=idarea.getId()%>">
                        <%=idarea.getDescript()%>
                    </option>
                    <% } %>
                </logic:iterate>
            </select>
        	</form>
        	</td>
        </tr>


            <tr>
                <td colspan="2">
                    <table class="clsTableTitle" width="100%" cellSpacing=0 cellPadding=2 align=center border=0>
                        <tbody>
                            <tr>
                                <td class="td_title_bc">
                                    <%=rb.getString("cbs.associate")%>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </td>
            </tr>
            <tr>
                <td class="td_orange_l_b barraBlue" width="30%">
                    <%=rb.getString("user.ape")%>
                </td>
                <td class="td_orange_l_b barraBlue" width="*">
                    <%=rb.getString("user.name")%>
                </td>
            </tr>
            <form name="selection" id="selection">
                <input type="hidden" name="userAssociate" value="<%=userAssociate%>"/>
                <logic:iterate id="associate" name="usuarios" indexId="ind" offset="<%=Pagingbean.getDesde()%>" length="<%=Pagingbean.getCuantos()%>"
                             type="com.desige.webDocuments.perfil.forms.PerfilActionForm" scope="session">
                    <tr>
                        <td class="td_gris_l">
                            <logic:notPresent name="onlyRead" scope="session">
                                <input name="docLinks" type="checkbox" value="<%=associate.getId()%>" <%=associate.isSelected()?"checked":""%>>
                            </logic:notPresent>
                            <%=associate.getApellidos()%>
                        </td>
                        <td class="td_gris_l">
                            <%=associate.getNombres()%>
                        </td>
                    </tr>
                </logic:iterate>

                <%
                    if (size.compareTo("0") > 0) {
                %>
                        <tr>
                            <td align="center" colspan="2">
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
                                <!-- Fin de Paginación -->
                            </td>
                        </tr>
                        <tr>
                            <td colspan="2" class="td_gris_l">
                                &nbsp;
                            </td>
                        </tr>
                    <%
                        }
                    %>
            </form>
            <form name="formPagingPage" method="post" action="associateActUser.jsp">
                <input type="hidden" name="from"  value="">
                <input type="hidden" name="cmd"  value="<%=SuperActionForm.cmdLoad%>">
                <input type="hidden" name="userAssociate" value="<%=userAssociate%>">
                <input type="hidden" name="type" value="<%=type%>"/>
            <tr>
<%--                <td colspan="3" class="td_orange_l_b">--%>
                <td colspan="3">
                    <center>
                        <logic:notPresent name="onlyRead" scope="session">
                            <input type="button" class="boton" value="<%=rb.getString("btn.ok")%>" onClick="javascript:check();" />
                            &nbsp;
                        </logic:notPresent>
                        <input type="button" class="boton" value="<%=rb.getString("btn.cancel")%>"  onClick="javascript:cancelar();" />
                    </center>
                </td>
            </tr>
        </table>
        <script language="Javascript">
			selectChecks();
        </script>
    </logic:present>
    <logic:notPresent name="usuarios" scope="session">
         <table cellSpacing=0 cellPadding=0 align=center border=0 width="100%">
		<tr>
             <td class="td_title_bc" colspan="2">
                  <%=rb.getString("admin.areas")%>
             </td>
        </tr>
        <tr>
            <td class="td_title_bc" colspan="2">
         	<form action="/loadActUsers.do" name="cargarArea">
            <input type="hidden" name="userAssociate" value="<%=userAssociate%>"/>
            <input type="hidden" name="number" value="<%=numero%>"/>
            <input type="hidden" name="type" value="<%=type%>"/>
            <input type="hidden" name="porArea" value="1"/>
            <input type="hidden" name="areaSel" value="<%=valorSeleccionado%>"/>
            <input type="hidden" name="input" value="<%=inputNew%>"/>
            <input type="hidden" name="value" value="<%=valueNew%>"/>
            <input type="hidden" name="nameForma" value="<%=nameFormaNew%>"/>
            <select onchange="getDatos();" name="idarea" >
                    <option value="">
                       ---------------------------
                    </option>
                <logic:iterate id="idarea" name="area" scope="session" type="com.desige.webDocuments.utils.beans.Search">
                    <%
                   if (idarea.getId().equalsIgnoreCase(valorSeleccionado)) {
                    %>
                    <option value="<%=idarea.getId()%>" selected>
                        <%=idarea.getDescript()%>
                    </option>
                    <%

                        id = idarea.getId();
                    } else {
                        if (valorSeleccionado==null) {
                            valorSeleccionado = idarea.getId();

                            id = idarea.getId();
                        }
                    %>
                    <option value="<%=idarea.getId()%>">
                        <%=idarea.getDescript()%>
                    </option>
                    <% } %>
                </logic:iterate>
            </select>
        	</form>
        	</td>
        </tr>


       <!--<table cellSpacing=0 cellPadding=0 align=center border=0 width="100%">-->
            <tr>
                <td colspan="2">
                    &nbsp;
                </td>
            </tr>
            <tr>
                <td colspan="2">
                    <table class="clsTableTitle" width="100%" cellSpacing=0 cellPadding=2 align=center border=0>
                        <tbody>
                            <tr>
                                <td class="td_title_bc">
                                    <%=rb.getString("E0116")%>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </td>
            </tr>
            <tr>
                <td>
                    <p align="center"><br><br>
                        <input type="button" class="boton" value="<%=rb.getString("btn.close")%>" onclick="javascript:cancelar()" />
                    </p>
                </td>
            </tr>                  
        </table>
    </logic:notPresent>
</body>
</html>
