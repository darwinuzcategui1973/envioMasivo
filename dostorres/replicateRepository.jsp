<html>
<body>
<form method="post" action="export">
<input type="hidden" name="replicateRepository" value="replicateRepository">
<table>
<%--><caption>Crear replica de repositorio de documentos (caso de Version 4.3.1)</caption>--%>
	<tr>
		<td colspan="1">
			 Verifique permisos en la carpeta destino para esta operación. 
			 El proceso termina cuando al final del log diga proceso finalizado. 
			 Si interrumpe la ejecución, realice el proceso completo nuevamente.
			 No coloque documentos ya encriptados en la carpera origen.
			 La carpeta destino debe estar vacía.
		</td>
	</tr>
	<tr>
		<td>Directorio ORIGEN (documentos desencriptados): </td>
		<td><input type="text" name="initialPath"></td>
	</tr>
	<tr>
		<td>Directorio DESTINO (documentos que quedarán encriptados):</td>
		<td><input type="text" name="finalPath"></td>
	</tr>
	<tr>
		<td colspan="2">
			<input type="radio" value="0" checked name="prueba"> Validar Directorios&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			<input type="radio" value="1" name="prueba"> Ejecutar encriptación
		</td>
	</tr>
	<tr>
		<td colspan="3"><input type="submit" value="Send"></td>
	</tr>
</table>
</form>
</body>
</html>
