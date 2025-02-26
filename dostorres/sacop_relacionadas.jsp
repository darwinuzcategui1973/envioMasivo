<!--/**
 * Title: sacop_relacionadas.jsp <br>
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
                 com.desige.webDocuments.utils.beans.Users"%>
<%@ page import="com.desige.webDocuments.sacop.actions.LoadSacop"%>

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

    String userAssociateLimpia = (String)request.getAttribute("userAssociateLimpia");
    if (ToolsHTML.isEmptyOrNull(userAssociateLimpia)) {
        userAssociate = "";
    }else{
       userAssociate= userAssociateLimpia;
    }


    String type = (String)ToolsHTML.getAttribute(request,"type");
    if (ToolsHTML.isEmptyOrNull(type)) {
        type = "0";
    }
    int paginaActual =0;


     String numero=request.getParameter("numero")!=null?request.getParameter("numero"):"";
    if (ToolsHTML.isEmptyOrNull(numero)) {
        numero = "";
    }

     String number=request.getParameter("number")!=null?request.getParameter("number"):"";
    if (ToolsHTML.isEmptyOrNull(number)) {
        number = "";
    }

    //-----------------------------------------------------
    //variables para las busquedas

    String inputb = (String)ToolsHTML.getAttribute(request,"inputb");
    if (ToolsHTML.isEmptyOrNull(inputb)) {
        inputb = inputReturn;
    }
    String valueb = (String)ToolsHTML.getAttribute(request,"valueb");
    if (ToolsHTML.isEmptyOrNull(valueb)) {
        valueb =valueReturn;
    }
    String nameFormab = (String)ToolsHTML.getAttribute(request,"nameFormab");
    if (ToolsHTML.isEmptyOrNull(nameFormab)) {
        nameFormab = "0";
    }
    String typeb = (String)ToolsHTML.getAttribute(request,"typeb");
    if (ToolsHTML.isEmptyOrNull(typeb)) {
        typeb =type;
    }
     String userAssociateb = (String)ToolsHTML.getAttribute(request,"userAssociateb");
    if (ToolsHTML.isEmptyOrNull(userAssociateb)) {
        userAssociateb =userAssociate;
    }
    //-----------------------------------------------------
%>
<html>
<head>
<title><%=rb.getString("principal.title")%></title>
<jsp:include page="meta.jsp" /> 
<link href="estilo/estilo.css" rel="stylesheet" type="text/css">
<script type="text/javascript" src="./estilo/funciones.js"></script>
<script type="text/javascript">
    var selecteds = null;

         <logic:present name="closeWindow" scope="request">
                       <%=valueReturn%>
                       <%="cancelar();"%>

       </logic:present>

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

    function quitarValor(valor){
    	var formaSelection = document.getElementById("selection");
        var selectedsNew = new Array();
        //quitamosel valor qued deselleccinemos
        for (var i=0;i<selecteds.length;i++){
             if (selecteds[i] !=valor){
                      selectedsNew[i]=selecteds[i];
             }else{
                       selectedsNew[i]='-1';
             }
        }
        selecteds=selectedsNew;
        formaSelection.userAssociate.value=selecteds;
    }
    function setValue(id){
        <%=inputReturn%>

    }


    function desmarcarCheck(valor,checkBox1) {
        if (!checkBox1.checked){

            quitarValor(valor);

        }

    }

    function cancelar(){
        window.close();
    }

    function check() {
    	var formaSelection = document.getElementById("selection");
        if (!validateCheck()) {
            if (formaSelection.userAssociate.value == '') {
                setValue('');
            }
            alert('<%=rb.getString("scp.nosacoprelacionados")%>');
            setValue(formaSelection.userAssociate.value);
            document.associate.userAssociate.value = formaSelection.userAssociate.value;
            document.associate.submit();

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

    function paging_OnClick(pageFrom) {
    	var formaSelection = document.getElementById("selection");
        document.formPagingPage.from.value = pageFrom;
        validateCheck();
        document.formPagingPage.userAssociate.value = formaSelection.userAssociate.value;
        document.formPagingPage.submit();
    }

function abrirVentana(pagina,width,height) {

    var hWnd = null;
    var nameWin="qweb";
    var left = getPosition(winWidth,width);
    var top = getPosition(winHeight,height);
    hWnd = window.open(pagina, nameWin, "resizable=yes,scrollbars=yes,statusbar=yes,width="+width+",height="+height+",left="+left+",top="+top);
      window.close();
}

   function checkKey(evt,forma) {

        var charCode = (evt.which) ? ect.which : event.keyCode;
        if (charCode == 13) {
            toSearch(forma);
        }
    }

   function toSearch(forma) {
        forma.action = "Sacop_Relacinadas.do";
        forma.submit();
    }

    function editField(pages,input,value,forma) {
        var hWnd = null;
        hWnd = window.open(pages+"?input="+input+"&value="+value+"&nameForma="+forma.name,"Catalogo","width=600,height=450,resizable=yes,statubar=yes,scrollbars=yes,left=200,top=150");
        if ((document.window != null) && (!hWnd.opener)){
            hWnd.opener = document.window;
        }
    }

   </script>
</head>
<body class="bodyInternas" <%=onLoad%>>
    <%
        String from = request.getParameter("from");
        String size = (String)session.getAttribute("size")!=null&&!session.getAttribute("size").toString().equals("")?(String)session.getAttribute("size"):"0";
        Users users = (Users)session.getAttribute("user");
        PaginPage Pagingbean = ToolsHTML.getBeanPaginPage(from,Integer.parseInt(size),"5"/*String.valueOf(users.getNumRecord())*/);
    %>
    
    <form name="associate" method="post" action="saveSacop_Relacionadas.do">
        <input type="hidden" name="userAssociate" value="<%=userAssociate%>"/>
        <input type="hidden" name="type" value="<%=type%>"/>
        <!--Estas variable se usan solo para las busquedas, pero hay que llevarlas y traerlas en las actions-->
            <input type="hidden" name="input" value="<%=inputb%>">
            <input type="hidden" name="value" value="<%=valueb%>">
            <input type="hidden" name="nameForma" value="<%=nameFormab%>">
            <input type="hidden" name="number" value="<%=number%>">
        </form>

        <table cellSpacing=0 cellPadding=0 align=center border=0 width="100%">
            <tr>
                <td colspan="5">
                    <table class="clsTableTitle" width="100%" cellSpacing=0 cellPadding=3 align=center border=0>
                        <tbody>
                            <tr>
                                <td class="td_title_bc">
                                    <%=rb.getString("scp.sacop")%>'S
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </td>
            </tr>
            <tr>
                <td class="td_orange_l_b">
                    <%=rb.getString("scp.num")%>
                </td>
                <td class="td_orange_l_b">
                    <%=rb.getString("scp.prcafectados")%>
                </td>
                 <td class="td_orange_l_b">
                   <%=rb.getString("scp.responsable")%>
                </td>
                <td class="td_orange_l_b">
                    <%=rb.getString("scp.receivingArea")%>
                </td>
                <td class="td_orange_l_b">
                    <%=rb.getString("scp.fueefectiva")%>   
                </td>
            </tr>
        <logic:present name="relacionarBusqueda" scope="request">
        <form name="search" action="">
        <logic:notEqual name="relacionarBusqueda" scope="request" value="-1">
            <tr>
                <td>
                    <input type="text" name="buscar1" value="" onkeypress="javascript:checkKey(event,document.search);" style="width:100px"/>
                    <input type="hidden" name="input" value="<%=inputb%>">
                    <input type="hidden" name="value" value="<%=valueb%>">
                    <input type="hidden" name="nameForma" value="<%=nameFormab%>">
                    <input type="hidden" name="type" value="<%=typeb%>">
                    <input type="hidden" name="userAssociate" value="<%=userAssociateb%>">
                    <input type="hidden" name="number" value="<%=number%>">
                </td>
                <td>
                    &nbsp;
                    <input type="text" name="nameRout"  style="width:80px" value="<%=request.getAttribute("nameRout")!=null?request.getAttribute("nameRout"):""%>"/>
                    <input type="hidden" name="idRout" value="<%=request.getAttribute("idRout")!=null?request.getAttribute("idRout"):""%>"/>
                    <input type="button" class="boton" value=" ... " onClick="javascript:editField('loadAllStruct.do?toSelectValue=true','estado','nameState',this.form);" style="width:20px;" />
                </td>
                <td>
                    <select name="responsablesfiltro" size="1" style="width: 140px; height: 19px" styleClass="classText">
                                <option value="0">-----------------------------------------------------------</option>
                                <logic:present name="slcresponsables">
                                    <logic:iterate id="bean" name="slcresponsables" type="com.desige.webDocuments.utils.beans.Search">
                                            <option value="<%=bean.getId()%>"><%=bean.getDescript()%></option>
                                    </logic:iterate>
                                </logic:present>
                    </select>
                </td>
                <td>
                    <select name="areasfiltro" size="1" style="width: 140px; height: 19px" styleClass="classText">
                                <option value="0">-----------------------------------------------------------</option>
                                <logic:present name="slcareas">
                                    <logic:iterate id="bean" name="slcareas" type="com.desige.webDocuments.utils.beans.Search">
                                            <option value="<%=bean.getId()%>"><%=bean.getDescript()%></option>
                                    </logic:iterate>
                                </logic:present>
                    </select>
                </td>
                <td>
                    <input type="radio" id="fueEfectiva" name="fueEfectiva" value="0"/> <%= rb.getString("scp.si") %>
                    <input type="radio" id="fueEfectiva" name="fueEfectiva" value="1"/> <%= rb.getString("scp.no") %>
                </td>
            </tr>
            <tr>
                <td align="center" colspan="5">
                    <input type="button" class="boton" value="<%=rb.getString("btn.search")%>" onClick="javascript:toSearch(document.search);" />
                </td>
            </tr>
            </logic:notEqual>
                   <logic:equal name="relacionarBusqueda" scope="request" value="0">
                       <!--<tr><td><%=rb.getString("doc.busqfallida")%></td></tr>-->
                   </logic:equal>
            </form>
            </logic:present>
            <tr>
                <td cass="td_gris_l" colspan="3">
                    &nbsp;
                </td>
            </tr>

            <form name="selection" id="selection">
                <input type="hidden" name="userAssociate" value="<%=userAssociate%>"/>
                <%int i=0;%>
                 <logic:present name="sacops_relacionar" scope="session">
                    <logic:iterate id="associate" name="sacops_relacionar" indexId="ind" offset="<%=Pagingbean.getDesde()%>" length="<%=Pagingbean.getCuantos()%>"
                                 type="com.desige.webDocuments.sacop.forms.Plantilla1BD" scope="session">
                        <tr>
                            <td class="td_gris_l">
                                <logic:notPresent name="onlyRead" scope="session">
                                    <% String docLinks="docLinks"+i;
                                        i++;
                                    %>
                                  <logic:present name="relacionarBusqueda" scope="request">
                                     <logic:notEqual name="relacionarBusqueda" scope="request" value="-1">
                                         <input name="<%=docLinks%>" type="checkbox" onClick='javascript:desmarcarCheck("<%=associate.getIdplanillasacop1()%>",<%=docLinks%>);' value="<%=associate.getIdplanillasacop1()%>" <%=associate.isSelected()?"checked":""%>>

                                     </logic:notEqual>
                                 </logic:present>
                                </logic:notPresent>

                                  <a href="javascript:abrirVentana('Sacop_RelacionadasIndividual.jsp?edo=<%=associate.getEstado()%>&id=<%=associate.getIdplanillasacop1()%>',800, 600);" class="ahreMenu" >
                                           <%=associate.getSacopnum()%>
                                   </a>

                            </td>
                            <td class="td_gris_l">
                               <%=associate.getProcesosafectados()%>
                            </td>
                            <td class="td_gris_l">
                               <%=associate.getRespbleareatxt()%>
                            </td>
                            <td class="td_gris_l">
                              <%=associate.getUsernotificadotxt()%>
                            </td>
                        </tr>
                    </logic:iterate>


                <%
                    if (size.compareTo("0") > 0) {
                %>
                        <tr>
                            <td align="center" colspan="5">
                                <!-- Inicio de Paginación -->
                                <table class="paginador">
                                  <tr>
                                    <td align="center" colspan="4">
                                      <br>
                                      <font size="1" color="#000000">
                                        <%=rb.getString("pagin.title")+ " "%>
                                        <%=Integer.parseInt(Pagingbean.getPages())+1%>
                                         <%paginaActual=Integer.parseInt(Pagingbean.getPages());%>
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
                    <%
                        }
                    %>
                  </logic:present>
                  <logic:notPresent name="sacops_relacionar" scope="session">
                        <tr>
                            <td class="td_gris_l">

                                 <logic:present name="noesproceso" scope="request">
                                    <%=(String)request.getAttribute("noesproceso")%>
                                 </logic:present>
                                 <logic:notPresent name="noesproceso" scope="request">
                                      <%=rb.getString("scp.nosacoprelacionados")%>
                                 </logic:notPresent>
                            </td>
                             <td class="td_gris_l">

                            </td>
                             <td class="td_gris_l">

                            </td>
                       </tr>
                </logic:notPresent>
            </form>
            <form name="formPagingPage" method="post" action="Sacop_Relacionadas_Puente.do">
                <input type="hidden" name="quitarValor"  value="">
                <input type="hidden" name="from"  value="">
                <input type="hidden" name="cmd"  value="<%=SuperActionForm.cmdLoad%>">
                <input type="hidden" name="userAssociate" value="<%=userAssociate%>">
                <input type="hidden" name="userAssociateLimpia" value="">
                <!--Estas variable se usan solo para las busquedas, pero hay que llevarlas y traerlas en las actions-->
                <input type="hidden" name="input" value="<%=inputb%>">
                <input type="hidden" name="value" value="<%=valueb%>">
                <input type="hidden" name="nameForma" value="<%=nameFormab%>">
                <input type="hidden" name="type" value="<%=typeb%>">
                <input type="hidden" name="number" value="<%=number%>">


                <input type="hidden" name="type" value="<%=type%>"/>
            <tr>
<%--                <td colspan="3" class="td_orange_l_b">--%>
                <td colspan="5">
                    <center>


                         <%boolean sw=false;%>
                        <logic:notPresent name="onlyRead" scope="session">

                             <logic:notPresent name="relacionarBusqueda" scope="request">
                                <logic:equal name="relacionarBusqueda" scope="request" value="-1">
                                    <input type="button" class="boton" value="<%=rb.getString("btn.ok")%>"  onClick="javascript:cancelar();"  />
                                        &nbsp;
                                </logic:equal>
                             </logic:notPresent>

                               <logic:present name="relacionarBusqueda" scope="request">
                                     <logic:notEqual name="relacionarBusqueda" scope="request" value="-1">
                                          <input type="button" class="boton" value="<%=rb.getString("btn.ok")%>" onClick="javascript:check();" />
                                         &nbsp;
                                     </logic:notEqual>
                               </logic:present>

                        </logic:notPresent>

                        <input type="button" class="boton" value="<%=rb.getString("btn.cancel")%>" onClick="javascript:cancelar();" />
                    </center>
                </td>
            </tr>
        </table>
</body>
</html>
