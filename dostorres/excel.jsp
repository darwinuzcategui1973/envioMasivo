<!-- /**
 * Title: vacio.jsp<br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * @author Ing. Nelson Crespo (NC)
 * @author Ing. Simon Rodríguez (SR)
 * @version WebDocuments v3.0
 * <br/>
 * Changes:<br/>
 * <ul>
 *      <li>Nothing</li>
 </ul>
 */ -->
<html>
    <head>
    	<style>
#itsthetable	{
		background: #F0F0F0 url(back01.gif) no-repeat;
		}

table		{
		margin: 0;
		padding: 0;
		font: normal 0.9em tahoma, arial, sans-serif;
		line-height: 1.4em;
		border-collapse: collapse;
		border: 4px solid #ADBBCA;
		color: #4F6480;
		background: #F0F0F0;
		}
		


table caption	{
		margin: 0;
		height: 32px;
		padding: 0;
		color: #4F6480;
		line-height: 2em;
		text-align: left;
		font: bold 150% georgia, serif;
		text-transform: uppercase;
		letter-spacing: 0.14em;
		}
		
thead		{
		color: #fff;
		background: #5E7796;
		}
		
thead tr th	{
		padding: 4px 8px 4px 8px;
		}

		
thead th	{
		border: 4px solid #ADBBCA;
		}

tfoot		{
		background: #fff;
		}
		
tfoot tr td, tfoot tr th, tbody tr td	{
		padding: 4px;
		}
		
tbody tr:hover	{
		background: #fff;
		}
		
tbody tr	{
		border: 4px solid #ADBBCA;
		}
		
tbody th	{
		padding: 8px;
		border: 4px solid #ADBBCA;
		}
		
tbody td	{
		background: #E9ECEE;
		}

tbody tr.odd		{
		background: #C4CFDB;
		}
		
tbody tr.odd td		{
		background: #F0F0F0;
		color: #4F6480;
		}
		

		
tbody tr.odd:hover		{
		background: #fff;
		}
		

		
tbody tr th a:link		{
		font: bold 0.9em tahoma, arial, sans-serif;
		color: #5E7796;
		text-decoration: underline;
		
		}
		
tbody tr th a:visited		{
		font: bold 0.9em tahoma, arial, sans-serif;
		color: #5E7796;
		text-decoration: none;
		
		}
		
tbody tr th a:hover		{
		font: bold 0.9em tahoma, arial, sans-serif;
		color: #5E7796;
		text-decoration: none;
		
		}
		
tbody tr th a:active		{
		font: bold 0.9em tahoma, arial, sans-serif;
		color: #5E7796;
		text-decoration: line-through;
		
		}
		
tbody tr th a:visited:after {
		content: "\00A0\221A";
		}
		
tbody td a:link		{
		font: normal 0.9em tahoma, arial, sans-serif;
		color: #808000;
		text-decoration: underline;
		}
		
tbody td a:visited		{
		font: normal 0.9em tahoma, arial, sans-serif;
		color: #808000;
		text-decoration: none;
		}
		
tbody td a:hover		{
		font: normal 0.9em tahoma, arial, sans-serif;
		color: #808000;
		text-decoration: none;
		}
		
tbody td a:active		{
		font: normal 0.9em tahoma, arial, sans-serif;
		color: #808000;
		text-decoration: underline;
		}
			
tbody td a:visited:after {
		content: "\00A0\221A";
		color: #808000;
		text-decoration: none;
		}
		
tbody td + td + td + td a { background: transparent url(downloadcss244.gif) no-repeat scroll 0 50%; 
			display: block;
			height: 24px;
			width: 24px;
			overflow: hidden;
			text-decoration: none;
			text-indent: -5000px;
			border: none;
			}
			
tbody td + td + td + td a:hover { background: transparent url(downloadcss2441.gif) no-repeat scroll 0 50%; 
			display: block;
			height: 24px;
			width: 24px;
			overflow: hidden;
			text-decoration: none;
			text-indent: -5000px;
			border: none;
			}
			    	</style>
        <meta content="MSHTML 6.00.2800.1106" name=generator/>
        <link href="estilo/estilo.css" rel="stylesheet" type="text/css"/>
<script language="JavaScript">

    String.prototype.endsWith = function(str){return (this.match(str+"$")==str)}

    function validar(forma){
   		forma.btnAccept.disabled = true;
   		forma.submit();
    }
</script>
	</head>
<body class="bodyInternas">
<center>
<form method="POST"  enctype='multipart/form-data' action="excel.do">
        <table cellSpacing=0 cellPadding=0 align=center border=0 >
        	<caption style="text-align:center;">lectura de evaluaciones en excel</caption>
            <tr>
	            <td class="pagesTitle" align="center">
		        <table cellSpacing=3 cellPadding=5 align=center border="0" align="center">
		            <tr>
		               <td valign="top" align="right" class="td_title_bc" style="text-align:left;" nowrap>
		               		Archivo 1 :
		               </td>
		               <td valign="middle" width="300" align="left">
		               		<input type="file" value="" maxlength="250" size="70" name="nombre0">
		               </td>
		            </tr>
		            <tr>
		               <td valign="top" align="right" class="td_title_bc" style="text-align:left;" nowrap>
		               		Archivo 2 :
		               </td>
		               <td valign="middle" width="300" align="left">
		               		<input type="file" value="" maxlength="250" size="70" name="nombre1">
		               </td>
		            </tr>
		            <tr>
		               <td valign="top" align="right" class="td_title_bc" style="text-align:left;" nowrap>
		               		Archivo 3 :
		               </td>
		               <td valign="middle" width="300" align="left">
		               		<input type="file" value="" maxlength="250" size="70" name="nombre2">
		               </td>
		            </tr>
		            <tr>
		               <td valign="top" align="right" class="td_title_bc" style="text-align:left;" nowrap>
		               		Archivo 4 :
		               </td>
		               <td valign="middle" width="300" align="left">
		               		<input type="file" value="" maxlength="250" size="70" name="nombre3">
		               </td>
		            </tr>
		            <tr>
		               <td valign="top" align="right" class="td_title_bc" style="text-align:left;" nowrap>
		               		Archivo 5 :
		               </td>
		               <td valign="middle" width="300" align="left">
		               		<input type="file" value="" maxlength="250" size="70" name="nombre4">
		               </td>
		            </tr>
		            <tr>
		            	<td colspan="2" align="center">
			            	<br>
		            		<input type="button" value="Ejecutar" name="btnAccept" onclick="javascript:validar(this.form);">
		            	</td>
		            </tr>
		        </table>
                </td>
            </tr>
        </table><br/><br/>
        <% String[][] data = (String[][]) request.getAttribute("data");
        if(data!=null){%>
		<table border="1" width="100%">
			<tr>
				<th abbr="CODIGO DEL USUARIO">CODIGO</th>
				<th>FECHA</th>
				<th>NOMBRE</th>
				<th>APELLIDO</th>
				<th>CEDULA</th>
				<th>FECHA</th>
				<th>PREGUNTAS</th>
				<th>PUNTAJE</th>
				<th>RESULTADO</th>
				<th>BUENAS</th>
				<th>MALAS</th>
			</tr>
        <%for(int i=0;i<data.length;i++){%>
			<tr>
				<td><%=data[i][0]%></td>
				<td><%=data[i][1]%></td>
				<td><%=data[i][2]%></td>
				<td><%=data[i][3]%></td>
				<td><%=data[i][4]%></td>
				<td><%=data[i][5]%></td>
				<td><%=data[i][6]%></td>
				<td><%=data[i][7]%></td>
				<td><%=data[i][8]%></td>
				<td><%=data[i][9]%></td>
				<td><%=data[i][10]%></td>
			</tr>
		<%}%>
		</table>
		<%}%>
<form>
</center>
</body>
</html>
