<!--
 * Title: checkOut.jsp <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * @author Ing. Simón Rodriguez.
 * @author Ing. Nelson Crespo.
 * @version WebDocuments v3.0
 * <br/>
 *       Changes:<br/>
 *           se le agrego logic:equal value="0" name="showDataWF" property="typeWF" para validar si esta De Revision
 *           o si esta Aprobado
 *           08/05/2006 (SR) Se valido los botones para cuando se pulse aceptar, se invaliden
 * </ul>
-->
<%@ page import="java.util.Locale,
                 java.util.ResourceBundle,
                 java.text.SimpleDateFormat,
                 com.desige.webDocuments.utils.ToolsHTML"%>

<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<%@ page language="java" %>
<%
    ResourceBundle rb = ToolsHTML.getBundle(request);
    StringBuffer onLoad = new StringBuffer(100);
    String ok = (String)session.getAttribute("usuario");
    String info = (String)session.getAttribute("info");
    onLoad.append(" onLoad=\"self.status='").append(rb.getString("pagin.welcome")).append(" ");
    onLoad.append(ok).append("'");
    if (ToolsHTML.checkValue(info)){
        onLoad.append(";alert('").append(info).append("')");
        session.removeAttribute("info");
    }
    if (onLoad.length()>0){
        onLoad.append("\"");
    }
//    ToolsHTML.clearSession(session,"application.profile");
%>
<html>
<head>
<title><%=rb.getString("principal.title")%></title>
<jsp:include page="meta.jsp" /> 
<link href="estilo/estilo.css" rel="stylesheet" type="text/css">
<script type="text/javascript">
	function checkOut(frm){
        var forma = document.getElementById("Selection");;
        if (frm.btnchkout) {
            frm.btnchkout.disabled = true;
            frm.btncancelar.disabled = true;
        }
        forma.submit();
	}

	function cancelar() {
		history.back();
	}

    function showWindow(pages,title,idDoc,value,width,height){
        var hWnd = null;
        hWnd = window.open(pages+"?read=true&value="+value+"&idDoc="+idDoc+"&title="+title,"","width="+width+",height="+height+",resizable=no,scrollbars=yes,statusbar=yes,left=250,top=250");
        if ((document.window != null) && (!hWnd.opener)){
            hWnd.opener = document.window;
        }
    }
</script>
</head>

<body class="bodyInternas" <%=onLoad%>>
<logic:present name="checkOutDoc" scope="session">
	<table align=center border=0 width="100%">
		<bean:define id ="doc" name="checkOutDoc" type="com.desige.webDocuments.document.forms.CheckOutDocForm" scope="session"/>
		<form name="Selection" id="Selection" action="showDataDocument.do">
			<input type="hidden" name="nexPage" value="loadStructMain.do"/>
			<input type="hidden" name="idDocument" value="<%=doc.getIdDocument()%>"/>
			<input type="hidden" name="nameDocument" value="<%=doc.getNameDocument()%>"/>
			<input type="hidden" name="downFile" value="true"/>
		</form>
        <tr>
            <td colspan="2" class="pagesTitle">
                <%=rb.getString("back.outTitleLock")%>
            </td>
        </tr>

        <tr>
            <td colspan="2">
                <table class="clsTableTitle" width="100%" cellSpacing="0" cellPadding="0" align=center border=0>
                    <tbody>
                        <tr>
                            <td class="td_title_bc" height="21">
                                <%=rb.getString("btn.checkOut")%>
                            </td>
                        </tr>
                    </tbody>
                </table>
            </td>
        </tr>

		<tr>
            <td height="22" class="titleLeft" style="background: url(img/btn120.png); background-repeat: no-repeat" width="12%" valign="middle">
				<%=rb.getString("chk.name")%>:
			</td>
			<td class="td_gris_l">
				<%=doc.getNameDocument()%>
			</td>
		</tr>
		<tr>
			<td height="22" class="titleLeft" style="background: url(img/btn120.png); background-repeat: no-repeat" valign="middle">
				<%=rb.getString("chk.version")%>:
			</td>
			<td class="td_gris_l">
				<%=doc.getVersion()%>
			</td>
		</tr>
		<tr>
			<td height="22" class="titleLeft" style="background: url(img/btn120.png); background-repeat: no-repeat" valign="middle">
				<%=rb.getString("chk.type")%>:
			</td>
			<td class="td_gris_l">
				<%=doc.getTypeDocument()%>
			</td>
		</tr>
		<tr>
			<td height="22" class="titleLeft" style="background: url(img/btn120.png); background-repeat: no-repeat" valign="middle">
				<%=rb.getString("chk.number")%>:
			</td>
			<td class="td_gris_l">
				<%=doc.getNumber()%>
			</td>
		</tr>
        <tr>
            <td height="44" class="titleLeft" style="background: url(img/btn120x2.png); background-repeat: no-repeat" valign="middle">
                <%=rb.getString("doc.links")%>:
             </td>
            <td>
                <a href="javascript:showWindow('loadAllDocuments.do','<%=rb.getString("doc.links")%>','<%=doc.getIdDocument()%>','6',500,400);" class="ahref_b" target="_self">
                    <%=rb.getString("doc.links")%>
                </a>
            </td>
        </tr>
		<tr>
			<td colspan="2">
				&nbsp;
			</td>
		</tr>
        <form name="botones" action="">
            <tr>
                <td colspan="2" align="center">
                    <input type="button" class="boton" value="<%=rb.getString("btn.checkOut")%>" onClick="javascript:checkOut(this.form);" name="btnchkout" />
                    &nbsp;
                    <input type="button" class="boton" value="<%=rb.getString("btn.cancel")%>" onClick="javascript:cancelar();" name="btncancelar" />
                </td>
            </tr>
        </form>
    </table>
</logic:present>
</body>
</html>
