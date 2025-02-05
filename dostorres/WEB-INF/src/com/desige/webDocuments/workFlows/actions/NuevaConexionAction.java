package com.desige.webDocuments.workFlows.actions;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.JOptionPane;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.focus.util.Conector;
import com.focus.util.ContextBean;
import com.focus.util.DataBase;

public class NuevaConexionAction extends Action {

	private boolean isConexionEfectiva = false;
	private boolean isPostgres = false;

	private String servidor = "";
	private String puerto = "";
	private String database = "";
	private String usuario = "";
	private String password = "";
	private String driver = "";
	private String usuarioAdmin = "";
	private String passwordAdmin = "";
	
	private String message = null;
	
	private HttpServletRequest request;
	private HttpServletResponse response;

	public synchronized ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		this.response = response;
		this.request = request;
		
		usuarioAdmin = request.getParameter("usuarioAdmin");
		passwordAdmin = request.getParameter("passwordAdmin");
		
		servidor = request.getParameter("servidor");
		puerto = request.getParameter("puerto");
		database = request.getParameter("database");
		usuario = request.getParameter("usuario");
		password = request.getParameter("password");
		driver = request.getParameter("driver");

		try {
			probarConexion();
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		try {
			if(message==null){
				message="Ocurrio un error en el proceso. comuniquese con sistemas";
			}
			
			StringBuffer sb = new StringBuffer();
			sb.append("{success:'true',message:'").append(message).append("'}");
			
			response.getWriter().print(sb.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	public void escribir() {
			// escribimos los datos al contex.xml
			ContextBean context = new ContextBean(request);

			// que driver es?
			StringBuffer dir = new StringBuffer(driver.equals("org.postgresql.Driver")?"jdbc:postgresql://":"jdbc:jtds:sqlserver://");
			dir.append(servidor).append(":").append(puerto).append("/").append(database);

			context.setUsername(usuario);
			context.setPassword(password);
			context.setDriverClassName(driver);
			context.setUrl(dir.toString());
			
			context.write();

			context.writeEclipse();
			
	}
	
	public void probarConexion() throws Exception {
			isConexionEfectiva = false;
			// realizaremos una prueba de conexion a la base de datos
			Connection connection = null;
			PreparedStatement ps = null;
			ResultSet rs = null;
			StringBuffer url = new StringBuffer();
			try {
				Class.forName(driver);
				System.out.println(driver);

				isPostgres = (driver.equals("org.postgresql.Driver")); 

				if (isPostgres) { /************************ POSTGRESQL *******************************/
					url.append("jdbc:postgresql://").append(servidor).append(":").append(puerto).append("/").append(database);
					System.out.println(url.toString());
					try {
						isServerActive(servidor, Integer.parseInt(puerto));
					} catch (Exception ex) {
						ex.printStackTrace();
						message = "No hay conexion con el servidor. asegurese que este instalado el manejador de base de datos PostgresSql y este escuchando en el puerto indicado";
						throw new Exception(message);
					}
					try {
						//System.out.println(usuario);
						//System.out.println(password);
						connection = DriverManager.getConnection(url.toString(), usuario, String.valueOf(password));
					} catch (Exception ex) {
						System.out.println(ex.getMessage());
						if (ex.getMessage().indexOf("password authentication") != -1 || ex.getMessage().indexOf("does not exist") != -1 || ex.getMessage().indexOf("autenti") != -1 || ex.getMessage().indexOf("no existe") != -1 ) {
							if(passwordAdmin==null || passwordAdmin.equals("") || usuarioAdmin==null || usuarioAdmin.equals("")) {
								message = "La base de datos no pudo ser autenticada Ingrese la clave de usuario postgres para verificar y crear la conexion";
								throw new Exception(message);
							} else {
								String urlAdmin = "jdbc:postgresql://SERVIDOR:PORT/postgres".replaceAll("SERVIDOR", servidor).replaceAll("PORT", puerto);
								String userAdmin = "postgres";
								System.out.println("urlAdmin=" + urlAdmin);
								System.out.println("useAdmin=" + userAdmin);
								System.out.println("pasAdmin=" + passwordAdmin);
								try {
									connection = DriverManager.getConnection(urlAdmin, userAdmin, passwordAdmin);

									// si nos conectamos creamos el usuario y la
									// base de datos
									DataBase db = new DataBase();
									db.loadCreate(DataBase.DATABASE_POSTGRES, database, usuario, password);
									db.createUserAndDataBase(connection);
									connection.close();

									// hay que crear la estructura y la info
									// basica de la base de datos
									Conector c = new Conector();
									c.setDriver(driver);
									c.setServidor(servidor);
									c.setPuerto(puerto);
									c.setUsuario(usuario);
									c.setPassword(String.valueOf(password));
									c.setBasededatos(database);
									c.setUrl("jdbc:postgresql://SERVIDOR:PORT/DATABASE");

									connection = DriverManager.getConnection(url.toString(), usuario, String.valueOf(password));
									db = new DataBase(c);
									db.setRequest(request);
									db.load();
									db.createDataBase(connection);

								} catch (Exception e1) {
									message = "No se pudo establecer una conexion con la base de datos con el usuario administrador";
									throw new Exception(message);
								}
							}
						} else {
							message = "No se pudo establecer una conexion con la base de datos";
							throw new Exception(message);
						}
					}
					isConexionEfectiva = true;
					message = "La conexion se realizo exitosamente";

					// escribimos el archivo en el disco
					escribir();
					
					
				} else { /************************ SQLSERVER *******************************/
					
					url.append("jdbc:jtds:sqlserver://").append(servidor).append(":").append(puerto).append("/").append(database);
					System.out.println(url.toString());
					try {
						isServerActive(servidor, Integer.parseInt(puerto));
					} catch (Exception ex) {
						ex.printStackTrace();
						message = "No hay conexion con el servidor, asegurese que este instalado el manejador de base de datos MS SqlServer y este escuchando en el puerto indicado"; 
						throw new Exception(message);
					}
					try {
						connection = DriverManager.getConnection(url.toString(), usuario, String.valueOf(password));
					} catch (Exception ex) {
						System.out.println(ex.getMessage());
						if (ex.getMessage().indexOf("password authentication") != -1 || ex.getMessage().indexOf("Error de inicio de sesión")!=-1 || ex.getMessage().indexOf("autenti") != -1 || ex.getMessage().indexOf("Login failed") != -1) {
							if(passwordAdmin==null || passwordAdmin.equals("") || usuarioAdmin==null || usuarioAdmin.equals("")) {
								message = "La base de datos no pudo ser autenticada Ingrese la clave de usuario sa para verificar y crear la conexion";
								throw new Exception(message);
							} else {
								String urlAdmin = "jdbc:jtds:sqlserver://SERVIDOR:PORT/master".replaceAll("SERVIDOR", servidor).replaceAll("PORT", puerto);
								String userAdmin = "sa";
								System.out.println("urlAdmin=" + urlAdmin);
								System.out.println("useAdmin=" + userAdmin);
								System.out.println("pasAdmin=" + passwordAdmin);
								try {
									connection = DriverManager.getConnection(urlAdmin, userAdmin, passwordAdmin);

									// si nos conectamos creamos el usuario y la
									// base de datos
									DataBase db = new DataBase();
									db.loadCreate(DataBase.DATABASE_SQLSERVER, database, usuario, password);
									db.createUserAndDataBase(connection);
									connection.close();

									// hay que crear la estructura y la info
									// basica de la base de datos
									Conector c = new Conector();
									c.setDriver(driver);
									c.setServidor(servidor);
									c.setPuerto(puerto);
									c.setUsuario(usuario);
									c.setPassword(String.valueOf(password));
									c.setBasededatos(database);
									c.setUrl("jdbc:jtds:sqlserver://SERVIDOR:PORT/DATABASE");

									
									// creamos el usuario en esta base de datos conectados on el usuario administrador
									connection = DriverManager.getConnection(url.toString(), userAdmin, passwordAdmin);
									db.createUserMsSqlServer(connection, database, usuario, password);
									connection.close();
									
									// nos conectamos a la nueva base de datos con el nuevo usuario
									connection = DriverManager.getConnection(url.toString(), usuario, String.valueOf(password));

									db = new DataBase(c);
									db.setRequest(request);
									db.load();
									db.createDataBase(connection);

								} catch (Exception e1) {
									e1.printStackTrace();
									message = "No se pudo establecer una conexion con la base de datos con el usuario administrador";
									throw new Exception(message);
								}
							}
						} else {
							message = "No se pudo establecer una conexion con la base de datos";
							throw new Exception(message);
						}
					}
					isConexionEfectiva = true;
					message = "La conexion se realizo exitosamente";

					// escribimos el archivo en el disco
					escribir();
				}

			} catch (ClassNotFoundException ex) {
				ex.printStackTrace();
				message = "No se encuentra el driver para la base de datos";
				throw new Exception(message);
			} catch (SQLException ex) {
				ex.printStackTrace();
				message = "No se puede establecer la conexion con la fuente de datos seleccionada";
				throw new Exception(message);
			} catch (Exception ex) {
				ex.printStackTrace();
				if(message==null) {
					message = "Ocurrio un error en la configuracion, comuniquese con sistemas.";
				}
				throw new Exception(message);
			} finally {
				if (connection == null) {
					try {
						connection.close();
					} catch (SQLException ex) {
						ex.printStackTrace();
					}
				}
			}
	}

	public boolean isServerActive(String server, int port) throws IOException {
		try {
			Socket canalComunicacion = null;
			OutputStream bufferSalida;
			DataOutputStream datos;
			canalComunicacion = new Socket(server, port);
			bufferSalida = canalComunicacion.getOutputStream();
			datos = new DataOutputStream(bufferSalida);
			String mensaje = "Hola Mundo!\n";
			// for (int i = 0; i < 2; i++) {
			datos.writeUTF(mensaje);
			// }
			//datos.writeUTF("");
			datos.close();
			bufferSalida.close();
			canalComunicacion.close();
		} catch (IOException ex) {
			ex.printStackTrace();
			throw ex;
		}
		return true;
	}
	
}
