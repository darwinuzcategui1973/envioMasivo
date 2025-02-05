<html>
<body>
<form method="post" action="export">
<table>
<caption>Exportar documentos de Qwebdocuments</caption>
	<tr>
		<td>Driver</td>
		<td>:</td>
		<td>
			<select name="driver" value="">
				<option value="net.sourceforge.jtds.jdbc.Driver">MS Sql server</option>
				<option value="org.postgresql.Driver">PostgreSql</option>
			</select>
		</td>
	</tr>
	<tr>
		<td>Url de base de datos</td>
		<td>:</td>
		<td><input type="text" name="url"></td>
	</tr>
	<tr>
		<td>Usuario BD</td>
		<td>:</td>
		<td><input type="text" name="user"></td>
	</tr>
	<tr>
		<td>Clave BD</td>
		<td>:</td>
		<td><input type="password" name="passwd"></td>
	</tr>
	<tr>
		<td>Carpeta destino</td>
		<td>:</td>
		<td><input type="input" name="ruta"></td>
	</tr>
	<tr>
		<td>Clave de exportacion</td>
		<td>:</td>
		<td><input type="password" name="claveExportacion"></td>
	</tr>
	<tr>
		<td colspan="3">
			<input type="radio" value="0" checked name="prueba"> Validar&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			<input type="radio" value="1" name="prueba"> Exportar
		</td>
	</tr>
	<tr>
		<td colspan="3"><input type="submit" value="Send"></td>
	</tr>
</table>
</form>
</body>
</html>