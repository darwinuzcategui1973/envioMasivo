
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<!--/**
 * Title: showcdr.jsp<br>
 * Copyright: (c) 2003 Consis International<br>
 * Company: Consis International (CON)<br>
 * @author Nelson Crespo (NC)
 * @author Simón Rodriguez (SR)
 * @version Acsel-e v2.2
 * <br>
 * Changes:<br>
 * <ul>
 *      <li> 16/06/2005 (SR) Se invalida la tecla ctrl en java script para imprimir en la forma </li>
 *      <li> 30/06/2006 (NC) Cambios para correcto formato de los documentos a Mostrar </li>
 * <ul>
 */-->

<html>
<head>
<title></title>
<jsp:include page="meta.jsp" /> 
<script language="JavaScript">
    document.onkeydown = function() {
        if(window.event && (window.event.keyCode == 122 ||
                            window.event.keyCode == 116 || window.event.ctrlKey)) {
            window.event.keyCode = 505;
        }
        if(window.event.keyCode == 505) {
            return false;
        }
    }
    
     function sendForm(){
        window.location='<%=request.getParameter("url")%>'
    }
    function AbritVentana() 
    { 
    	//alert("sendForm()");
    	//sendForm();
        open('<%=request.getParameter("url")%>',"CorelDraw","toolbar=no"); 
    } 
    
</script>


</head>
<body onLoad="javascript:AbritVentana();">
<form name="coreldrow" method="Post">
 <!-- <a href='<%=request.getParameter("url")%>' target=_top class="td_title_bc"> <%=request.getParameter("name")%>=<%=request.getParameter("url")%></a>   --> 
</form>
</body>
</html>
