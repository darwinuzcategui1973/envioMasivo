import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.regex.Pattern;

import com.focus.filesystem.Main;

public class ReplaceInFiles {

	private String ext = "";
	private String old = "";
	private String nueva = "";

	public ReplaceInFiles() {

	}

	public ReplaceInFiles(String ext, String old, String nueva) {
		this.ext = ext;
		this.old = old;
		this.nueva = nueva;
	}

	public void start(String dir)  throws IOException {
		File[] lista = new File[] { new File(dir) };

		leerDirectorio(lista);
	}

	private void leerDirectorio(File[] lista) throws IOException  {
		String[] ex = ext.split(",");
		boolean hacer = false;
		for (int i = 0; i < lista.length; i++) {
			// System.out.println(lista[i].getAbsolutePath());
			if (lista[i].isDirectory()) {
				leerDirectorio(lista[i].listFiles());
			} else {
				hacer = false;
				String name = lista[i].getAbsolutePath();
				for(int x=0;x<ex.length;x++) {
					if(name.endsWith(ex[x])) {
						hacer=true;
						break;
					}
				}
				if(!hacer) 
					continue;

				if(name.endsWith("taglibs.jsp")) {
					int xxx=0;
				}
				
				if (!name.endsWith("ReplaceInFiles.java") && (hacer || ext.length()==0)) {
					String sb = readFile(new File(name)).toString();
					//System.out.println("--->" + name); 
					if(sb.indexOf(old)!=-1) {
						System.out.println("archivo=" + name); 
						sb = sb.replaceAll(Pattern.quote(old), nueva);
						write(name,sb);
					}

				}
			}
		}
	}

	private StringBuffer readFile(File file) {
		StringBuffer sb = new StringBuffer();
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		DataInputStream dis = null;

		try {
			fis = new FileInputStream(file);

			// Here BufferedInputStream is added for fast reading.
			bis = new BufferedInputStream(fis);
			dis = new DataInputStream(bis);

			// dis.available() returns 0 if the file does not have more lines.
			while (dis.available() != 0) {

				// this statement reads the line from the file and print it to
				// the console.
				sb.append(dis.readLine()).append("\n");
				//System.out.println(dis.readLine());
			}

			// dispose all the resources after using them.
			fis.close();
			bis.close();
			dis.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb;
	}

	/** Write fixed content to the given file. */
	public void write(String nameFile, String cadena) throws IOException {
		Writer out = new OutputStreamWriter(new FileOutputStream(nameFile));
		try {
			out.write(cadena);
		} finally {
			out.close();
		}
	}

	public static void mainx(String[] args)  throws IOException { 
		//ReplaceInFiles c = new ReplaceInFiles(".jsp", "891900", "B78009");
		//ReplaceInFiles c = new ReplaceInFiles(".jsp", "8d6307", "815a06");
		ReplaceInFiles c;
		
		//c = new ReplaceInFiles(".java", "nameMethodCurrent", "Thread.currentThread().getStackTrace()[1].getMethodName()");
		//c.start("D:/appproyectos/qweb5/workspace/qweb/WEB-INF/src/");

		//c = new ReplaceInFiles("CrearReporte.java", "JDBCUtil.getConnection\\)", "JDBCUtil.getConnection");
		//c.start("D:/appproyectos/qweb5/workspace/qweb/WEB-INF/src/");

		//ReplaceInFiles c = new ReplaceInFiles("CrearReporte.java", "JDBCUtil.getConnection\\(\\)", "JDBCUtil.getConnection(nameMethodCurrent)");
		//ReplaceInFiles c = new ReplaceInFiles(".java", "from PermissionStructGroups", "from permissionstructgroups");
		//ReplaceInFiles c = new ReplaceInFiles(".java", " Person ", " person ");
		//ReplaceInFiles c = new ReplaceInFiles(".java", ",Person ", ",person ");
		
		//c.start("c:/temp");
		//c.start("D:/appproyectos/qweb5/workspace/qweb/WEB-INF/src");
		System.out.println("Finalizado");
		System.exit(0);
	}
	
	public static void main(String[] args) throws IOException {
		
		String[] lista = "PermissionStructUser,PermissionStructGroups,Struct,HistoryStruct,Person,confDocumento_typeDocument,Activities,FlexWorkFlow,user_FlexWorkFlows,versionDoc,VersionDoc,Documents,Pais,Idiomas,PermissionRecordUser,PermissionFilesUser,PermisionDocGroup,PermissionRecordGroup,DocumentsLinks,TypeDocuments,DocCheckOut,PermisionDocUser,PermisionDocGroup,User_FlexWorkFlows,GroupUsers,PermissionFilesGroup,Address,Contacts,documentsLinks,User_WorkFlows,WorkFlows,BackUpFiles,typeDocuments,listDistDocument,docOfRejection,Messages,MessageXUser,Norms,Parameters,tbl_deleteSacop,TBL_AREA,seguridadUser,Security,Location,Module,listDist,HistoryDocs,HistoryFiles,Enlaces,UrlsUsers,flowWorker,askingPerson,subActivities,WFHistory,FlexFlowHistory,Activity,StatusUserMov,DUAL_SEQGEN,Dual_Seqgen,LOCATION,seguridadUser,tbl_deleteSacop,versiondocView,VersionDoc,ChangesWF,SubActivities,workflowsPendings,seguridadGrupo,DocCheckOut,HistoryWF".split(",");
		//String[] lista = "HistoryWF".split(",");
		ReplaceInFiles c = null; 
		String ruta = "D:/appproyectos/qweb5/workspace/qweb/WEB-INF/src";
				
		//c = new ReplaceInFiles(".java", "import org.apache.log4j.Logger;", "import org.slf4j.Logger;\r\nimport org.slf4j.LoggerFactory;");
		//c.start(ruta);
		
		//c = new ReplaceInFiles(".java", "Logger.getLogger", "LoggerFactory.getLogger");
		//c.start(ruta);
		
		c = new ReplaceInFiles(".java", "log.error(sqle);", "log.error(sqle.getMessage());");
		c.start(ruta);
		

		System.out.println("Finalizado");
		System.exit(0);
		
	}
}
