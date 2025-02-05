<html>
<head>
<script>
function main() {
	var img = window.parent.document.getElementById('logoEmpresa');
	img.src = img.src + "?" + <%= System.currentTimeMillis()%>;
}
</script>
</head>
<body style="margin:0;" onload="main()">
<form action="./uploadImage.do" method="post" enctype="multipart/form-data" style="margin:0;">
<input type="file" name="nameImage">
<input type="submit" value="Almacenar" style="height:20px;">
</form>
</body>
</html>