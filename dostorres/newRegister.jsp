
<!--
 * Title: newRegister.jsp <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * @author Ing. Simón Rodriguez.
 * @author Ing. Nelson Crespo.
 * @version WebDocuments v3.0
 * <br/>
 *       Changes:<br/>

 *      <li> 25/04/2006 (SR) Se sustituyo /WebDocuments/.. por ./..
 * </ul>
-->
<%@ page import="java.util.ResourceBundle,
                 com.desige.webDocuments.utils.ToolsHTML,
                 com.desige.webDocuments.utils.Constants,
                 com.desige.webDocuments.typeDocuments.forms.TypeDocumentsForm,
                 com.desige.webDocuments.utils.beans.SuperActionForm"%>

<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<%@ page language="java" %>
<%
    ResourceBundle rb = ToolsHTML.getBundle(request);
    String width = "12%";
    if (request.getAttribute("width")!=null) {
        width = "25%";
    }
    
    TypeDocumentsForm tipo = (TypeDocumentsForm) request.getAttribute("tipoDocumento");
%>
<html>
<head>
<title><%=rb.getString("principal.title")%></title>
<jsp:include page="meta.jsp" /> 
<link href="estilo/estilo.css" rel="stylesheet" type="text/css">
<script language=javascript src="./estilo/funciones.js"></script>
<script language="JavaScript">

    setDimensionScreen();

    function send(forma){
        if (validar(forma)) {
           forma.submit();
        }
    }

    <logic:present name="info" scope="session">
        showInfo('<%=session.getAttribute("info")%>');
        <%session.removeAttribute("info");%>
    </logic:present>

    <logic:present name="error" scope="session">
        showInfo('<%=session.getAttribute("error")%>');
        <%session.removeAttribute("error");%>
    </logic:present>

    function editField(pages,input,value,forma){
        var hWnd = null;
        hWnd = window.open(pages+"?input="+input+"&value="+value+"&nameForma="+forma.name,"Catalogo","width=600,height=450,resizable=yes,statubar=yes,scrollbars=yes,left=200,top=150");
        if ((document.window != null) && (!hWnd.opener)){
            hWnd.opener = document.window;
        }
    }

    function cancelar() {
        history.back();
    }

    function validar(forma){
        if (forma.nameDocument.value.length==0) {
            alert('<%=rb.getString("err.notNameFile")%>');
            return false;
        }
        if (forma.idRout.value.length==0){
            alert ("<%=rb.getString("err.notIDRout")%>");
            return false;
        }
        return true;
    }

    function save(){
    }
</script>
</head>

<body class="bodyInternas">

    <html:form action="/saveRegister.do">
        <html:hidden property="idDocument"/>
        <input type="hidden" name="flujoParametrico" value="<%=request.getParameter("flujoParametrico")%>" />
        <table align=center border=0 width="100%">
            <tr>
                <td class="pagesTitle" colspan="2">
                    <%=rb.getString("reg.new.title")%>
                </td>
            </tr>
            <tr>
                <td colspan="2">
                    <table class="clsTableTitle" width="100%" cellSpacing=0 cellPadding=2 align=center border=0>
                        <tbody>
                            <tr>
                                <td class="td_title_bc">
                                    <%=rb.getString("reg.new.title")%>
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
                <td class="titleLeft" height="26" style="background: url(img/btn160.png); background-repeat: no-repeat" width="20%" valign="middle">
                    <%=rb.getString("cbs.name")%>:
                </td>
                <td class="td_gris_l" width="*">
                    <html:text property="nameDocument" size="60" maxlength="1000"  styleClass="classText"/>
                </td>
            </tr>
            <tr>
                <td class="titleLeft" height="26" style="background: url(img/btn160.png); background-repeat: no-repeat" valign="middle">
                    <%=rb.getString("reg.new.folder")%>:
                </td>
                <td class="td_gris_l">
                    <html:hidden property="idRout"/>
                    <html:text property="nameRout" size="60" maxlength="1000"  styleClass="classText" readonly="true"/>
                    <input type="button" class="boton" value=" ... " onClick="javascript:editField('loadAllStruct.do?toSelectValue=true','estado','nameState',this.form);" style="width:20px;" />
                </td>
            </tr>
			<%if(tipo.getGenerateRequestSacop()==1){%>
	            <tr>
	                <td class="titleLeft" height="26" style="background: url(img/btn160.png); background-repeat: no-repeat" valign="middle">
	                    <%=rb.getString("reg.new.registerClass")%>:
	                </td>
	                <td class="td_gris_l">
	                	<html:select property="idRegisterClass" styleClass="width:330px;" >
	                		<%for(int i=1; i<Constants.registerclassTable.size(); i++) {%>
	                			<option value="<%=i%>"><%=Constants.registerclassTable.get(i)%></option>
	                		<%}%>
	                	</html:select>
	                </td>
	            </tr>
        	<%} else {%>
				<html:hidden property="idRegisterClass" value="0" />                	
        	<%}%>
            <tr>
                <td colspan="2" align="center">
                    <input type="button" class="boton" value="<%=rb.getString("btn.create")%>" onClick="javascript:send(this.form);" />
                    &nbsp;
                    <input type="button" class="boton" value="<%=rb.getString("btn.cancel")%>" onClick="javascript:cancelar();" />
                </td>
            </tr>
        </table>
    </html:form>
</body>
</html>
