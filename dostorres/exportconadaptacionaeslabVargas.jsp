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
		<td><input type="text" name="url" value="jdbc:jtds:sqlserver://192.168.10.202:1433/vargas"></td>
	</tr>
	<tr>
		<td>Usuario BD</td>
		<td>:</td>
		<td><input type="text" name="user" value="qwebdocuments"></td>
	</tr>
	<tr>
		<td>Clave BD</td>
		<td>:</td>
		<td><input type="password" name="passwd" value="qwebdocuments"></td>
	</tr>
	<tr>
		<td>Carpeta destino </td>
		<td>:</td>
		<td><input type="input" name="ruta" value="C:\qweb\repositorios\vargas"></td>
	</tr>
	<tr>
		<td>Clave de exportacion</td>
		<td>:</td>
		<td><input type="password" name="claveExportacion" value="*F0cusvision91*" ></td>
	</tr>
	<tr>
		<td colspan="3">
		<input type="radio" value="0" name="prueba"> Validar&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		<input type="radio" value="1" checked name="prueba"> Exportar
		</td>
	</tr>
	<tr>
		<td colspan="3"><input type="submit" value="Send"></td>
	</tr>
</table>
</form>
</body>
</html>