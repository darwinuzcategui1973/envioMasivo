<html>
<head>
    <script>
       function setValues(idRout,nameRout) {
    	   setValues(idRout,nameRout,0,0);
       }
       function setValues(idRout,nameRout,majorId,minorId) {
            forma = document.select;
            forma.idRout.value = idRout;
            forma.nameRout.value = nameRout;
            forma.majorId.value = majorId;
            forma.minorId.value = minorId;
        }

        function closeWin(forma) {
            if (top.opener.document.<%=request.getParameter("nameForma")%>){
            	 if(top.opener.document.<%=request.getParameter("nameForma")%>.idRout){
                 	top.opener.document.<%=request.getParameter("nameForma")%>.idRout.value = forma.idRout.value;
                 }
                 if(top.opener.document.<%=request.getParameter("nameForma")%>.nameRout){
                 	top.opener.document.<%=request.getParameter("nameForma")%>.nameRout.value = forma.nameRout.value;
                 }
                 if(top.opener.document.getElementById('idRout')) {
                 	top.opener.document.getElementById('idRout').value = forma.idRout.value;
                 }
                 if(top.opener.document.<%=request.getParameter("nameForma")%>.hidMayorVer){
                     top.opener.document.<%=request.getParameter("nameForma")%>.hidMayorVer.value = forma.majorId.value;
                     if(forma.majorId.value == 0){
                    	 top.opener.document.<%=request.getParameter("nameForma")%>.mayorVer.value = "0";
                     } else {
                    	 top.opener.document.<%=request.getParameter("nameForma")%>.mayorVer.value = "A";
                     }
                 }
                 if(top.opener.document.<%=request.getParameter("nameForma")%>.hidMinorVer){
                     top.opener.document.<%=request.getParameter("nameForma")%>.hidMinorVer.value = forma.minorId.value;
                     if(forma.minorId.value == 0){
                         top.opener.document.<%=request.getParameter("nameForma")%>.minorVer.value = "0";
                     } else {
                         top.opener.document.<%=request.getParameter("nameForma")%>.minorVer.value = "A";
                     }
                 }
            }
            
           if (forma.majorId.value!="menu_folder_closed.gif"){
        		forma.idRout.value = 0;
                forma.nameRout.value = "";
        		alert("Seleccion Permitida solo tipo carpeta");
        	}else {
        		
        		top.window.close();
        	}
        	
        }
    </script>
</head>
<body>
    <form name="select" action="">
        <input type="hidden" name="idRout" value=""/>
        <input type="hidden" name="majorId" value=""/>
        <input type="hidden" name="minorId" value=""/>
        <input type="text" name="nameRout" value="" readonly="readonly" size="22"/>
        <table>
            <tr>
                <td>
                    <input type="button" class="boton" value="Aceptar" onclick="javascript:closeWin(this.form);" />
                </td>
            </tr>
        </table>
    </form>
</body>
</html>