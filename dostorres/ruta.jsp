

<%
java.io.File file = new java.io.File("");
file = new java.io.File(file.getAbsolutePath());
java.io.File file2 = null;
java.io.File file3 = null;
java.io.File[] lista = null;
java.io.File[] lista2 = null;
if(file.isDirectory()) {
	lista = file.listFiles();
	for(int i=0;i<lista.length;i++){
		//System.out.println(lista[i].getAbsolutePath());
		file2 = new java.io.File(lista[i].getAbsolutePath());
		if(file2.getName().endsWith("web.xml")) {
			out.println("------------------------------------");
		}
		if(file2.isDirectory()) {
			lista2 = file2.listFiles();
			for(int k=0;k<lista2.length;k++){
				file3 = new java.io.File(lista2[k].getAbsolutePath());
				if(file3.getName().endsWith("web.xml")) {
					out.println("------------------------------------");
				}
				out.print(file3.getAbsolutePath());
				out.print("<br/>");
			}
		}
	}
}
%>

