package com.desige.webDocuments.document.servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.desige.webDocuments.util.ReplicateRepository;
import com.focus.filesystem.FileSystem;

/**
 * Se esta reutilizando este servlet para agregar el proceso de replicacion del repositorio (cliente en 4.3.1)
 * Ademas de manejar el proceso de extraccion de documentos de la base de datos.
 * 
 * Todo esto dependiendo de lo indicado en el request.
 * @author FJR
 *
 */
public class ExportServlet extends HttpServlet {
	private static final String REPLICATE_REPOSITORY = "replicateRepository";

	/**
	 * 
	 */
	private static final long serialVersionUID = 1560763440223384379L;

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();

		if(request.getParameter(REPLICATE_REPOSITORY) != null){
			doReplicateLogic(request, response, out);
		} else {
			doExportLogic(request, response, out);
		}

		out.close();
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @param out
	 * @throws ServletException
	 * @throws IOException
	 */
	public void doReplicateLogic(HttpServletRequest request, HttpServletResponse response,
			PrintWriter out) throws ServletException, IOException {
		String initialPath = request.getParameter("initialPath");
		String finalPath = request.getParameter("finalPath");
		String prueba = request.getParameter("prueba");

		boolean procesar = true;
		boolean justValidateParameters = false;

		if (initialPath == null || initialPath.trim().equals("")){
			procesar = false;
		} else {
			File initialPathFile = new File(initialPath);
			if(!initialPathFile.exists()){
				procesar = false;

				out.print("<h2>");
				out.print("Directorio: '" + initialPath + "', no existe en el servidor");
				out.print("</h2> </ br>");
			}
		}

		if (finalPath == null || finalPath.trim().equals("")){
			procesar = false;
		} else {
			File finalPathFile = new File(finalPath);
			if(!finalPathFile.exists() && !finalPathFile.mkdirs()){
				procesar = false;

				out.print("<h2>");
				out.print("Directorio: '" + finalPath + "', no existe en el servidor");
				out.print("</h2> </ br>");
			}
		}
		out.print("prueba: '" + prueba + initialPath + finalPath );
		if (prueba == null || prueba.trim().equals("") || prueba.equals("0")){
			procesar = false;
		} else if(prueba.equals("0")){
			justValidateParameters = true;
		}

		if (procesar) {
			out.println("<title>Resultados</title>" + "<body bgcolor=FFFFFF>");
			try {
				ReplicateRepository.executeReplication(initialPath, finalPath, out);
			} catch (Exception ex) {
				out.println("Error: " + ex.getMessage());
			}
			out.print("<h2> Proceso Finalizado</h2>");
		} else if(justValidateParameters){
			out.print("<h2>Parametros fueron revisados, verifique el resultado obtenido</h2>");
		} else {
			out.print("<h2>Error, datos incompletos o errados</h2>");
			out.print("<h2>procesar, initialPath, finalPath, out</h2>");
		}

		out.println("<P>Return to <a href='replicateRepository.jsp'>Replicate Repository form</a>");
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @param out
	 * @throws ServletException
	 * @throws IOException
	 */
	public void doExportLogic(HttpServletRequest request, HttpServletResponse response,
			PrintWriter out) throws ServletException, IOException {
		String driver = request.getParameter("driver");
		String url = request.getParameter("url");
		String user = request.getParameter("user");
		String passwd = request.getParameter("passwd");
		String ruta = request.getParameter("ruta");
		String claveExportacion = request.getParameter("claveExportacion");
		String prueba = request.getParameter("prueba");

		boolean procesar = true;
		String resultado = "";

		if (driver == null || driver.trim().equals(""))
			procesar = false;
		if (url == null || url.trim().equals(""))
			procesar = false;
		if (user == null || user.trim().equals(""))
			procesar = false;
		if (passwd == null || passwd.trim().equals(""))
			procesar = false;
		if (ruta == null || ruta.trim().equals(""))
			procesar = false;
		if (prueba == null || prueba.trim().equals(""))
			procesar = false;
		if (claveExportacion == null || claveExportacion.trim().equals(""))
			procesar = false;

		if (procesar && claveExportacion.equals("*F0cusvision91*")) {
			out.println("<title>Resultados</title>" + "<body bgcolor=FFFFFF>");
			try {
				FileSystem file = new FileSystem();
				file.setDriver(driver);
				file.setUrl(url);
				file.setUser(user);
				file.setPasswd(passwd);
				file.setRuta(ruta);

				resultado = file.start(prueba.equals("0"));

			} catch (Exception ex) {
				System.exit(1);
			}
			out.print("<h2>");
			out.print(resultado);
			out.print("</h2>");
		} else {
			out.print("<h2>Error, datos incompletos o errados</h2>");
		}

		out.println("<P>Return to <a href='export.jsp'>form</a>");
	}
}
