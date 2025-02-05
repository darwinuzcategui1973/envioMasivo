<!-- principalSacop1Old.jsp -->
<%@ page import="java.util.ResourceBundle,
                 com.desige.webDocuments.utils.ToolsHTML,
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
    String info = (String) ToolsHTML.getAttribute(request,"info",true);
    if (!ToolsHTML.isEmptyOrNull(info)) {
        onLoad.append(" onLoad=\"alert('").append(info).append("')\"");
    }
    String ok = (String)session.getAttribute("usuario");
    String idPerson=(String)ToolsHTML.getAttribute(request,"idPerson",true);
    //ToolsHTML.clearSession(session,"application.sacop");
%>
<html>
<head>
<title><%=rb.getString("principal.title")%></title>
<jsp:include page="meta.jsp" /> 
<link href="estilo/estilo.css" rel="stylesheet" type="text/css">
<script language="JavaScript">
	function showPlanilla(forma,id,edo){
        //alert('id='+id+' edo='+edo+' forma.action='+forma.action);
        forma.Idplanillasacop1.value=='';
         if ((edo=='<%=LoadSacop.edoBorrador%>')) {    //edoBorrador
            forma.goTo.value="planillauno";
            forma.edodelsacop.value=edo;
            forma.Idplanillasacop1.value=id;
            forma.submit();
        } else if ((edo=='<%=LoadSacop.edoEmitida%>')||(edo=='<%=LoadSacop.edoRechazado%>')) {    //edoEmitida o edoRechazado
            forma.edodelsacop.value=edo;
            forma.goTo.value="planillados";
            forma.Idplanillasacop1.value=id;
            forma.submit();
        } else if((edo=='<%=LoadSacop.edoAprobado%>')) {  //edoAprobado
            forma.goTo.value="planilla3_0";
            forma.Idplanillasacop1.value=id;
            forma.submit();

        } else if((edo=='<%=LoadSacop.edoPendienteVerifSeg%>')||(edo=='<%=LoadSacop.edoEnEjecucion%>')) {
            forma.goTo.value="planilla4";
            forma.Idplanillasacop1.value=id;
            forma.submit();

        } else if((edo=='<%=LoadSacop.edoVerificacion%>')) {
            forma.goTo.value="planilla5";
            forma.Idplanillasacop1.value=id;
            forma.submit();

        } else if((edo=='<%=LoadSacop.edoCerrado%>')) {
            forma.goTo.value="planilla6";
            forma.Idplanillasacop1.value=id;
            forma.submit();
        }

	}
	function validar(){
        if (document.login.docSearch.value.length>0){
            alert('<%=rb.getString("err.notDocumentSearch")%>');
        }else{
		    document.login.submit();
        }
	}

    function showWF(idWorkFlow,row,owner){
    	var forma = document.getElementById("selection");
        forma.idWorkFlow.value = idWorkFlow;
        forma.row.value = row;
        forma.owner.value = owner;
        forma.submit();
    }

     function crearSacop2(forma){
                if (forma.origensacop.value==""){
                   alert('<%=rb.getString("scp.notieneorigensacop")%>');
                }else{
                   forma.submit();
                }

     }
    function crearSacop(forma){
        forma.action="CrearSacop.do";
        forma.submit();
    }
    function visualizarTodos(){
         document.frmvisualizarTodos.submit();
    }
    function showCharge(userName,charge) {
	    charge = charge.replace(/\[/gi,"-").replace(/\]/gi,"-");
        window.open("showCharge.jsp?userName="+userName+"&charge="+charge, "WebDocuments", "resizable=no,scrollbars=yes,statusbar=yes,width=300,height=200,left=410,top=250");
    }
    function deletePlanilla(forma,id,idPerson,borrarAtodos){
         if (confirm("<%=rb.getString("scp.eliminarask")%>")) {
             forma.userdlt.value= idPerson;
             forma.idpdlt.value=id;
             forma.borrarAtodos.value= borrarAtodos;
             forma.action= "DeleteSacopAction.do";
             forma.submit();
        }
    }
	function reporteLista(forma) {
        forma.submit();
    }
    
    function cancelar() {
   		location.href = "loadSACOPMain.do?goTo=reload";
    }
</script>
</head>

<body class="bodyInternas" <%=onLoad.toString()%>>
<!--<a href="javascript:reporteLista(document.reporte);" class="ahreMenu" >-->
        <%--<%=rb.getString("lst.reporte")%>--%>
<!--</a>-->
 <form name=frmvisualizarTodos action='VisualizarTodos.do'>
 </form>
<logic:notPresent name="crearSacop" scope="request">
<form name="reporte" action="CrearReporteSacop.do">
<table cellSpacing=0 cellPadding=2 align=center border=0 width="100%">
        <tr>
            <td>
                <input type=text name="conjuntosacop">
                &nbsp;
                &nbsp;
                <input type="button" class="boton" value="<%=rb.getString("scp.crear")%> <%=rb.getString("scp.sacop")%>" onClick="javascript:crearSacop(document.frmcrearSacop);" name="btnscrearr"/>
                &nbsp;
                &nbsp;
                <input type="button" class="boton" value="<%=rb.getString("lst.reporte")%>" onClick="javascript:reporteLista(document.reporte);" name="btnreporte"/>
                &nbsp;
                &nbsp;
                <input type="button" class="boton" value="<%=rb.getString("scp.visualizartodos")%>" onClick="javascript:visualizarTodos();" name="btnvisualizartodos"/>
            </td>
            <td>
            </td>
        </tr>

    </table>
</form>
</logic:notPresent>
<table cellSpacing=0 cellPadding=2 align=center border=0 width="100%">
    <logic:notPresent name="crearSacop" scope="request">
        <form name=frmcrearSacop action='CrearSacop.do'>
            <tr>
                <td>

                </td>
            </tr>
       </form>
    </logic:notPresent>
    <logic:present name="crearSacop" scope="request">
        <tr>
            <td>
                <form name=solicitudsacop action='loadSACOPMain.do'>
                    <table width="100%" border="0">
                        <tr>
                            <td class="td_titulo_C barraBlue" width="35%">
                                <input type="radio" checked onClick='' name=correcpreven value='0'> <%=rb.getString("scp.ac")%>
                            </td>
                            <td class="td_titulo_C barraBlue" width="35%">
                                <input type="radio" onClick='' name=correcpreven value='1'><%=rb.getString("scp.ap")%>
                            </td>
                        </tr>
                        <tr>
                            <td  class="td_gris_l" colspan="2"> <%=rb.getString("admin.scpplanillas")%>:&nbsp;
                                <select name='origensacop'>
                                    <logic:present name="titulosplanillassacop" scope="session" >
                                        <logic:iterate id="bean" name="titulosplanillassacop" type="com.desige.webDocuments.utils.beans.Search" scope="session" >
                                            <option value="<%=bean.getId()%>"><%=bean.getDescript()%> </option>
                                        </logic:iterate>
                                    </logic:present>
                                </select>
                                &nbsp;
                                <input type="button" class="boton" value="<%=rb.getString("btn.ok")%>" onClick="javascript:crearSacop2(this.form);" name="btnscrearr"/>
                        		&nbsp;&nbsp;
                        		<input type="button" class="boton" value="<%=rb.getString("btn.cancel")%>" onClick="javascript:cancelar();" />
                            </td>
                            <input type='hidden' name=goTo value=planillauno>
                            <td>
                                <%--<input type="submit" value="<%=rb.getString("btn.ok")%>">--%>
                            </td>
                        </tr>
                    </table>
                       </form>
        </td>
     </tr>
   </logic:present>
    <!------------------------------------------------------------------------------>
   <logic:notPresent name="crearSacop" scope="request">
    <tr>
    	<td valign="top" width="50%">
			<table width="100%" border="0">
                <tr>
			        <td>
                     <form name=solicitudsacopplanilla action='loadSACOPMain.do'>
                          <!--Variables para usar en eliminar planillas-->
                            <input type='hidden' name=userdlt>
                            <input type='hidden' name=idpdlt>
                            <input type='hidden' name=goTo>
                            <input type='hidden' name="Idplanillasacop1">
                            <input type='hidden' name="edodelsacop">
                            <input type='hidden' name="borrarAtodos">
                        <table class="clsTableTitle" width="100%" cellSpacing=0 cellPadding=2 align=center border=0>
                            <tbody>
                                <tr>
                                    <td class="td_title_bc" height="21">
                                      <%=rb.getString("scp.sacop")%>&nbsp;<%=rb.getString("scp.borrador")%>
                                    </td>
                                </tr>
                            </tbody>
                        </table>
		  		        <table width="100%" border="0" >
              		        <tr>
                                <td class="td_titulo_C" width="18%">
                                    <%=rb.getString("scp.num")%>
                                </td>
                                <td class="td_titulo_C" width="35%">
                                    <%=rb.getString("scp.emisor")%>
                                </td>
                                <td class="td_titulo_C" width="12%">
                                    <%=rb.getString("scp.responsable")%>
                                </td>
                                <td class="td_titulo_C" width="13%">
                                    <%=rb.getString("scp.estado")%>
                                </td>
                                <td class="td_titulo_C" width="12%">

                                </td>
                              </tr>
                              <logic:present name="borrador" scope="session" >
                                <logic:iterate id="borrador1" name="borrador" type="com.desige.webDocuments.sacop.forms.Plantilla1BD" scope="session" >
                                    <tr>
                                        <td>
                                            <img src="img/chequeado.gif" width="17" height="17">
                                             <a href="javascript:showPlanilla(document.solicitudsacopplanilla,<%=borrador1.getIdplanillasacop1()%>,<%=borrador1.getEstado()%>);" class="ahreMenu" >
                                               <%=borrador1.getSacopnum()%>
                                            </a>
                                        </td>
                                        <td class="txt_b">
                                             <a href="javascript:showCharge('<%=borrador1.getSolicitantetxt()%>','<%=borrador1.getCargoSolicitante()%>')" class="ahref_b">
                                                <logic:equal name="borrador1" property="mostrarCargo" value="true">
                                                    <%=borrador1.getCargoSolicitante()%>
                                                </logic:equal>
                                                <logic:equal name="borrador1" property="mostrarCargo" value="false">
                                                    <%=borrador1.getSolicitantetxt()%>
                                                </logic:equal>
                                            </a>
                                        </td>
                                        <td class="txt_b">
                                         <a href="javascript:showCharge('<%=borrador1.getRespbleareatxt()%>','<%=borrador1.getCargo()%>')" class="ahref_b">
                                            <logic:equal name="borrador1" property="mostrarCargo" value="true">
                                                <%=borrador1.getCargo()%>
                                            </logic:equal>
                                            <logic:equal name="borrador1" property="mostrarCargo" value="false">
                                                <%=borrador1.getRespbleareatxt()%>
                                            </logic:equal>
                                          </a>
                                        </td>
                                        <td class="txt_b">
                                             <%=borrador1.getEstadotxt()%>
                                          
                                               <logic:notEqual name="borrador1" property="idplanillasacop1esqueleto" value="-1">
                                                   &nbsp;<%=rb.getString("scpintouch.intouch")%>
                                               </logic:notEqual>
                                        </td>
                                        <td class="txt_b">


                                            <logic:present name="verHistoricosTambien" scope="request">
                                               <logic:equal  name="verHistoricosTambien" value="true">
                                                 <%=rb.getString("scp.eliminar1")%>
                                               </logic:equal>
                                               <logic:equal  name="verHistoricosTambien" value="false">
                                                  <a href="javascript:deletePlanilla(document.solicitudsacopplanilla,<%=borrador1.getIdplanillasacop1()%>,<%=idPerson%>,'1');" class="ahreMenu" >
                                                            <%=rb.getString("scp.eliminar1")%>
                                                  </a>
                                               </logic:equal>
                                           </logic:present>
                                        </td>
                                    </tr>
                                </logic:iterate>
                            </logic:present>
            	        </table>
			        </td>
                </tr>
                <tr>
                    <td>&nbsp;</td>
                </tr>
                <tr>
                    <td>
                        <table class="clsTableTitle" width="100%" cellSpacing=0 cellPadding=2 align=center border=0>
                            <tbody>
                                <tr>
                                    <td class="td_title_bc" height="21">
                                        <%=rb.getString("scp.sacop")%>&nbsp;<%=rb.getString("scp.emitidos")%>
                                    </td>
                                </tr>
                            </tbody>
                        </table>
			            <table width="100%" border="0">
                            <tr>
                                 <td class="td_titulo_C" width="18%">
                                    <%=rb.getString("scp.num")%>
                                </td>
                                <td class="td_titulo_C" width="35%">
                                    <%=rb.getString("scp.emisor")%>
                                </td>
                                <td class="td_titulo_C" width="12%">
                                    <%=rb.getString("scp.responsable")%>
                                </td>
                                <td class="td_titulo_C" width="13%">
                                    <%=rb.getString("scp.estado")%>
                                </td>
                                <td class="td_titulo_C" width="12%">

                                </td>
                            </tr>
                            <logic:present name="emitido" scope="session" >
                                <logic:iterate id="emitido1" name="emitido" type="com.desige.webDocuments.sacop.forms.Plantilla1BD" scope="session" >
                                    <tr>
                                        <td>
                                            <img src="img/chequeado.gif" width="17" height="17">
                                             <a href="javascript:showPlanilla(document.solicitudsacopplanilla,<%=emitido1.getIdplanillasacop1()%>,<%=emitido1.getEstado()%>);" class="ahreMenu" >
                                               <%=emitido1.getSacopnum()%>
                                            </a>
                                        </td>
                                         <td class="txt_b">
                                         <a href="javascript:showCharge('<%=emitido1.getSolicitantetxt()%>','<%=emitido1.getCargoSolicitante()%>')" class="ahref_b">
                                            <logic:equal name="emitido1" property="mostrarCargo" value="true">
                                                <%=emitido1.getCargoSolicitante()%>
                                            </logic:equal>
                                            <logic:equal name="emitido1" property="mostrarCargo" value="false">
                                                <%=emitido1.getSolicitantetxt()%>
                                            </logic:equal>
                                         </a>
                                        </td>
                                        <td class="txt_b">
                                          <a href="javascript:showCharge('<%=emitido1.getRespbleareatxt()%>','<%=emitido1.getCargo()%>')" class="ahref_b">
                                            <!--El cargo del responsable-->
                                            <logic:equal name="emitido1" property="mostrarCargo" value="true">
                                                <%=emitido1.getCargo()%>
                                            </logic:equal>
                                            <logic:equal name="emitido1" property="mostrarCargo" value="false">
                                                <%=emitido1.getRespbleareatxt()%>
                                            </logic:equal>
                                           </a>

                                        </td>

                                        <td class="txt_b">

                                            <%=emitido1.getEstadotxt()%>
                                            <logic:notEqual name="emitido1" property="idplanillasacop1esqueleto" value="-1">
                                                 &nbsp;<%=rb.getString("scpintouch.intouch")%>
                                            </logic:notEqual>
                                            
                                        </td>
                                         <td class="txt_b">
                                           <%if ((emitido1.getEstado()==Integer.parseInt(LoadSacop.edoRechazado))){%>

                                             <logic:present name="verHistoricosTambien" scope="request">
                                               <logic:equal  name="verHistoricosTambien" value="true">
                                                    <%=rb.getString("scp.eliminar")%>
                                               </logic:equal>
                                               <logic:equal  name="verHistoricosTambien" value="false">
                                                    <a href="javascript:deletePlanilla(document.solicitudsacopplanilla,<%=emitido1.getIdplanillasacop1()%>,<%=idPerson%>,'0');" class="ahreMenu" >
                                                       <%=rb.getString("scp.eliminar")%>
                                                    </a>
                                               </logic:equal>
                                           </logic:present>

                                           <%}%>
                                                      <!--es responsable y no puede eliminar-->
                                        </td>
                                    </tr>
                                </logic:iterate>
                            </logic:present>
                        </table>
		            </td>
                </tr>
            </table>
	</td>
	<td width="*" valign="top">
		<table width="100%" border="0">
                <tr>
                    <td>
                        <table class="clsTableTitle" width="100%" cellSpacing=0 cellPadding=2 align=center border=0>
                            <tbody>
                                <tr>
                                    <td class="td_title_bc" height="21">
                                         <%=rb.getString("scp.sacop")%>&nbsp;<%=rb.getString("scp.pendientes")%>
                                    </td>
                                </tr>
                            </tbody>
                        </table>
                        <table width="100%" border="0">
                            <tr>
                                 <td class="td_titulo_C" width="18%">
                                    <%=rb.getString("scp.num")%>
                                </td>
                                <td class="td_titulo_C" width="35%">
                                    <%=rb.getString("scp.emisor")%>
                                </td>
                                <td class="td_titulo_C" width="12%">
                                    <%=rb.getString("scp.responsable")%>
                                </td>
                                <td class="td_titulo_C" width="13%">
                                    <%=rb.getString("scp.estado")%>
                                </td>
                                <td class="td_titulo_C" width="12%">

                                </td>
                            </tr>
                            <logic:present name="pendientes" scope="session">
                                <logic:iterate id="pendientes1" name="pendientes" type="com.desige.webDocuments.sacop.forms.Plantilla1BD" scope="session" >
                                   <tr>
                                        <td>
                                            <img src="img/chequeado.gif" width="17" height="17">
                                             <a href="javascript:showPlanilla(document.solicitudsacopplanilla,<%=pendientes1.getIdplanillasacop1()%>,<%=pendientes1.getEstado()%>);" class="ahreMenu" >
                                               <%=pendientes1.getSacopnum()%>
                                            </a>
                                        </td>
                                       <td class="txt_b">
                                       <a href="javascript:showCharge('<%=pendientes1.getSolicitantetxt()%>','<%=pendientes1.getCargoSolicitante()%>')" class="ahref_b">
                                          <logic:equal name="pendientes1" property="mostrarCargo" value="true">
                                                <%=pendientes1.getCargoSolicitante()%>
                                          </logic:equal>
                                          <logic:equal name="pendientes1" property="mostrarCargo" value="false">
                                                <%=pendientes1.getSolicitantetxt()%>
                                          </logic:equal>
                                       </a>
                                      </td>
                                      <td class="txt_b">
                                        <a href="javascript:showCharge('<%=pendientes1.getRespbleareatxt()%>','<%=pendientes1.getCargo()%>')" class="ahref_b">
                                          <!--El cargo del responsable-->
                                          <logic:equal name="pendientes1" property="mostrarCargo" value="true">
                                                <%=pendientes1.getCargo()%>
                                          </logic:equal>
                                          <logic:equal name="pendientes1" property="mostrarCargo" value="false">
                                                <%=pendientes1.getRespbleareatxt()%>
                                          </logic:equal>
                                        </a>
                                      </td>

                                        <td class="txt_b">
                                             <%=pendientes1.getEstadotxt()%>
                                        </td>
                                         <td class="txt_b">
                                               <logic:notEqual name="pendientes1" property="idplanillasacop1esqueleto" value="-1">
                                                   &nbsp;<%=rb.getString("scpintouch.intouch")%>
                                               </logic:notEqual>
                                                 
                                        </td>
                                    </tr>
                                </logic:iterate>
                            </logic:present>
                        </table>
                    </td>
                </tr>
                <tr>
                    <td>&nbsp;</td>
                </tr>
                <tr>
                    <td>
                        <table class="clsTableTitle" width="100%" cellSpacing=0 cellPadding=2 align=center border=0>
                            <tbody>
                                <tr>
                                    <td class="td_title_bc" height="21">
                                        <%=rb.getString("scp.sacop")%>&nbsp;<%=rb.getString("scp.cerrado")%>
                                    </td>
                                </tr>
                            </tbody>
                        </table>
                        <table width="100%" border="0">
                            <tr>
                                  <td class="td_titulo_C" width="18%">
                                    <%=rb.getString("scp.num")%>
                                </td>
                                <td class="td_titulo_C" width="35%">
                                    <%=rb.getString("scp.emisor")%>
                                </td>
                                <td class="td_titulo_C" width="12%">
                                    <%=rb.getString("scp.responsable")%>
                                </td>
                                <td class="td_titulo_C" width="13%">
                                    <%=rb.getString("scp.estado")%>
                                </td>
                                <td class="td_titulo_C" width="12%">

                                </td>
                            </tr>
                            <logic:present name="cerrado" scope="session" >
                                <logic:iterate id="cerrado1" name="cerrado" type="com.desige.webDocuments.sacop.forms.Plantilla1BD" scope="session" >
                                     <tr>
                                        <td>
                                            <img src="img/chequeado.gif" width="17" height="17">
                                             <a href="javascript:showPlanilla(document.solicitudsacopplanilla,<%=cerrado1.getIdplanillasacop1()%>,<%=cerrado1.getEstado()%>);" class="ahreMenu" >
                                               <%=cerrado1.getSacopnum()%>
                                            </a>
                                        </td>
                                      
                                        <td class="txt_b">
                                        <a href="javascript:showCharge('<%=cerrado1.getSolicitantetxt()%>','<%=cerrado1.getCargoSolicitante()%>')" class="ahref_b">
                                          <logic:equal name="cerrado1" property="mostrarCargo" value="true">
                                                <%=cerrado1.getCargoSolicitante()%>
                                          </logic:equal>
                                          <logic:equal name="cerrado1" property="mostrarCargo" value="false">
                                                <%=cerrado1.getSolicitantetxt()%>
                                          </logic:equal>
                                        </a>
                                      </td>
                                      <td class="txt_b">
                                        <a href="javascript:showCharge('<%=cerrado1.getRespbleareatxt()%>','<%=cerrado1.getCargo()%>')" class="ahref_b">
                                          <!--El cargo del responsable-->
                                          <logic:equal name="cerrado1" property="mostrarCargo" value="true">
                                                <%=cerrado1.getCargo()%>
                                          </logic:equal>
                                          <logic:equal name="cerrado1" property="mostrarCargo" value="false">
                                                <%=cerrado1.getRespbleareatxt()%>
                                          </logic:equal>
                                        </a>
                                      </td>
                                        <td class="txt_b">
                                             <%=cerrado1.getEstadotxt()%>
                                             <logic:notEqual name="cerrado1" property="idplanillasacop1esqueleto" value="-1">
                                                   &nbsp;<%=rb.getString("scpintouch.intouch")%>
                                             </logic:notEqual>
                                        </td>
                                         <td class="txt_b">
                                           <logic:present name="verHistoricosTambien" scope="request">
                                               <logic:equal  name="verHistoricosTambien" value="true">
                                                   <%=rb.getString("scp.eliminar")%>
                                               </logic:equal>
                                               <logic:equal  name="verHistoricosTambien" value="false">
                                                    <a href="javascript:deletePlanilla(document.solicitudsacopplanilla,<%=cerrado1.getIdplanillasacop1()%>,<%=idPerson%>,'0');" class="ahreMenu" >
                                                         <%=rb.getString("scp.eliminar")%>
                                                    </a>

                                               </logic:equal>
                                           </logic:present>
                                        </td>
                                    </tr>
                                </logic:iterate>
                            </logic:present>
                        </table>


                     </form>

                    </td>
                </tr>
            </table>
	</td>
  </tr>
 </table>
</logic:notPresent>
</body>
</html>
