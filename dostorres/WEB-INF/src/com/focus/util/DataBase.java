package com.focus.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.swing.JOptionPane;

import com.desige.webDocuments.persistent.utils.JDBCUtil;
import com.desige.webDocuments.utils.ToolsHTML;

public class DataBase {

	private HttpServletRequest request;
	
	public static final int DATABASE_DERBY = 1;
	public static final int DATABASE_POSTGRES = 2;
	public static final int DATABASE_SQLSERVER = 3;
	
	//public static final String CONTRASENA_DB = "08smmb8a9N";

	private Conector conector = null;

	private final ArrayList<String> create = new ArrayList<String>();
	private final ArrayList<String> estructura = new ArrayList<String>();
	private final ArrayList<String> dataInicial = new ArrayList<String>();
	private final ArrayList<String> cambiosEstructura = new ArrayList<String>();
	private final ArrayList<String> cambiosData = new ArrayList<String>();

	public static String default_driver = "org.apache.derby.jdbc.EmbeddedDriver";
	public static String default_servidor = "localhost";
	public static String default_basedataos = "qnetfiles";
	public static String default_usuario = "qnetfiles";
	public static String default_password = "qwebdocuments";
	public static String default_puerto = "";
	public static String default_url = "jdbc:derby:DATABASE;create=true;user=USER;password=PASSWORD;";

	public DataBase() {
		conector = new Conector();
		conector.iniciarConector();
	}

	public DataBase(Conector conector) {
		this.conector = conector;
	}

	public boolean testConection() {
		return testConection(conector);
	}

	public boolean isDerby() {
		return conector.getDriver().equals(default_driver);
	}
	public boolean isPostgres() {
		return conector.getDriver().equals("org.postgresql.Driver");
	}

	public boolean isSqlServer() {
		return conector.getDriver().equals("net.sourceforge.jtds.jdbc.Driver");
	}
	
    public String getNow() {
    	if(isPostgres()) {
    		return "now()";
    	} else if(isSqlServer()) {
    		return "current_timestamp";
    	} else if(isDerby()) {
    		return "current_timestamp";
    	} else {
    		return "now()";
    	}
    }	

    public String getDateField() {
    	if(isPostgres()) {
    		return "TIMESTAMP";
    	} else if(isSqlServer()) {
    		return "DATETIME";
    	} else if(isDerby()) {
    		return "TIMESTAMP";
    	} else {
    		return "TIMESTAMP";
    	}
    }	

    /*
	 * Probamos la conexion a la base de datos
	 */
	public boolean testConection(String driver, String server, String database, String port, String user, String passwd, String url) {
		conector = new Conector();
		conector.setDriver(driver);
		conector.setServidor(server);
		conector.setBasededatos(database);
		conector.setPuerto(port);
		conector.setUsuario(user);
		conector.setPassword(passwd);
		conector.setUrl(url);
		return testConection(conector);
	}

	public boolean testConection(Conector conector) {
		try {
			this.conector = conector;
			// realizaremos una prueba de conexion a la base de datos
			Connection connection = null;
			PreparedStatement ps = null;
			StringBuffer url = new StringBuffer();
			try {
				Class.forName(conector.getDriver());
				System.out.println(conector.getUrl() + "- " + conector.getDriver());
				if (conector.getDriver().equals("org.postgresql.Driver") || conector.getDriver().equals("net.sourceforge.jtds.jdbc.Driver")) {
					System.out.println(conector.getUrl());
					connection = DriverManager.getConnection(conector.getUrl(), conector.getUsuario(), conector.getPassword());

					// comprobamos que existe la base de datos
					boolean isExiste = true;
					try {
						ps = connection.prepareStatement(JDBCUtil.replaceCastMysql("select * from parameters"));
						ResultSet rs = ps.executeQuery();
						if (rs.next()) {
							isExiste = true;
						}
					} catch (Exception e) {
						isExiste = false;
					} 
				} else {
					System.out.println(conector.getUrl());
					connection = DriverManager.getConnection(conector.getUrl());

					// comprobamos que existe la base de datos
					boolean isExiste = true;
					try {
						ps = connection.prepareStatement(JDBCUtil.replaceCastMysql("SELECT version FROM config"));
						ResultSet rs = ps.executeQuery();
						if (rs.next()) {
							isExiste = true;
						}
					} catch (Exception e) {
						isExiste = false;
					}
					if (!isExiste) {
						System.out.println("No existe la base de datos local, la creamos");
						// creamos la base de datos
						DataBase db = new DataBase();
						db.load();
						db.createDataBase(connection);
					}
				}

				// JOptionPane.showMessageDialog(null, "La conexion se realizo
				// exitosamente");

			} catch (ClassNotFoundException ex) {
				JOptionPane.showMessageDialog(null, "No se encuentra el driver para la base de datos");
				ex.printStackTrace();
				return false;
			} catch (SQLException ex) {
				//JOptionPane.showMessageDialog(null, "No se pudo establecer la conexion con la fuente de datos\n\nPor favor, verifique que la base de datos no esta siendo utilizada por otro proceso");
				ex.printStackTrace();
				return false;
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(null, ex.getMessage());
				ex.printStackTrace();
				return false;
			} finally {
				if (connection == null) {
					try {
						connection.close();
					} catch (SQLException ex) {
						ex.printStackTrace();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean createUserAndDataBase(Connection cone) {
		try {
			executeUpdate(create, cone);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return true;
	}

	public boolean createDataBase(Connection cone) {
		try {
			executeUpdate(estructura, cone);
			executeUpdate(dataInicial, cone);
			//executeUpdate(cambiosEstructura, cone);
			//executeUpdate(cambiosData, cone);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return true;
	}

	public boolean updateDataBase(Connection cone) {
		try {
			loadUpdate();
			executeUpdate(cambiosEstructura, cone);
			executeUpdate(cambiosData, cone);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return true;
	}

	public boolean executeUpdate(ArrayList<String> querys, Connection cone) throws SQLException {
		PreparedStatement pst = null;
		for (int i = 0; i < querys.size(); i++) {
			try {
				System.out.println(querys.get(i));
				pst = cone.prepareStatement(JDBCUtil.replaceCastMysql(querys.get(i)));
				pst.executeUpdate();
			} catch(SQLException e) {
				e.printStackTrace();
				if(e.getMessage().indexOf(" exist")!=-1) {
					System.out.println("El campo ya existe");
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		return true;
	}

	public boolean executeUpdate(String query, Connection cone) throws SQLException {
		try {
			PreparedStatement pst = null;
			System.out.println(query);
			pst = cone.prepareStatement(JDBCUtil.replaceCastMysql(query));
			pst.executeUpdate();
		} catch(Exception e) {
			e.printStackTrace();
		}
		return true;
	}
	
	/**
	 * 
	 * @param cone
	 * @param database
	 * @param usuario
	 * @param password
	 * @return
	 * @throws SQLException
	 */
	public boolean createUserMsSqlServer(Connection cone, String database, String usuario, String password) throws SQLException {
		StringBuffer query = new StringBuffer();
		
		query.append("CREATE USER ").append(usuario).append(" FOR LOGIN ").append(usuario);
		
		if(executeUpdate(query.toString(), cone)){
			query.setLength(0);
			query.append("EXEC sp_addrolemember N'db_owner', N'").append(usuario).append("'");
			return executeUpdate(query.toString(), cone);
		}
		
		return false;
	}
	
	public boolean updateTables() {

		return true;
	}

	public boolean updateData() {

		return true;
	}

	public void load() {
		loadEstructura();
		loadDataInicial();
		//loadCambioEstructura();
		//loadCambioData();
	}

	public void loadUpdate() {
		loadCambioEstructura();
		loadCambioData();
	}

	public void loadCreate(int tipo, String database, String user, String password) {
		StringBuffer linea = new StringBuffer();

		switch (tipo) {
		case DATABASE_POSTGRES:
			linea.setLength(0);
			linea.append("create user ").append(user).append(" with password '").append(password).append("'");
			create.add(linea.toString());

			linea.setLength(0);
			//linea.append("create database ").append(database).append(" owner ").append(user).append(" encoding 'LATIN9'");
			linea.append("CREATE DATABASE ").append(database).append(" TEMPLATE template0 ENCODING='WIN1252' OWNER=").append(user).append(" CONNECTION LIMIT=-1");
			create.add(linea.toString());

			linea.setLength(0);
			linea.append("alter user ").append(user).append(" SUPERUSER");
			create.add(linea.toString());
			break;

		case DATABASE_SQLSERVER:
			linea.setLength(0);
			linea.append("CREATE LOGIN ").append(user).append(" WITH PASSWORD = '").append(password).append("', CHECK_POLICY = OFF");
			create.add(linea.toString());

			linea.setLength(0);
			linea.append("CREATE DATABASE ").append(database).append("");
			create.add(linea.toString());

			break;
		} 

	}

	public void loadEstructura() {
		StringBuffer linea = new StringBuffer();
		
		Archivo archivo = new Archivo();
		ArrayList<String> contenido = new ArrayList<String>();
		String nameFile = null;
		String lin = null;
		
		// cargamos la estructura desde el archivo 
		if(isSqlServer()) {
			// EmptyDB_MSSQL.sql
			nameFile = ToolsHTML.getPath().concat("/db/EmptyDB_MSSQL.sql");
		} else { // es postgres
			// postgres_db.sql
			nameFile = ToolsHTML.getPath().concat("/db/postgres_db.sql");
		}
		contenido = archivo.leerPorLinea(nameFile);
		
		int size = contenido.size();
		boolean isLinea = false;
		for(int i=0; i<size; i++) {
			lin =  contenido.get(i);
			if(lin.toUpperCase().startsWith("CREATE TABLE") || lin.toUpperCase().startsWith("ALTER TABLE")) {
				linea.setLength(0);
				isLinea=true;
			}
			
			if(isLinea) {
				linea.append(lin);
				if(lin.trim().endsWith(";")) {
					estructura.add(linea.toString());
					isLinea=false;
				}
			}
		}

	}

	public void loadDataInicial() {
		StringBuffer linea = new StringBuffer();
		
		Archivo archivo = new Archivo();
		ArrayList<String> contenido = new ArrayList<String>();
		String nameFile = null;
		String lin = null;
		
		// cargamos la estructura desde el archivo 
		if(isSqlServer()) {
			// EmptyDB_MSSQL.sql
			nameFile = ToolsHTML.getPath().concat("/db/insert_mssql.txt");
		} else { // es postgres
			// postgres_db.sql
			nameFile = ToolsHTML.getPath().concat("/db/insert_postgres.txt");
		}
		contenido = archivo.leerPorLinea(nameFile);
		
		int size = contenido.size();
		boolean isLinea = false;
		for(int i=0; i<size; i++) {
			lin =  contenido.get(i);
			if(lin.toUpperCase().startsWith("INSERT") || lin.toUpperCase().startsWith("UPDATE")) {
				linea.setLength(0);
				isLinea=true;
			}
			
			if(isLinea) {
				linea.append(lin);
				if(lin.trim().endsWith(";")) {
					dataInicial.add(linea.toString());
					isLinea=false;
				}
			}
		}

	}

	public void loadCambioEstructura() {
		StringBuffer linea = new StringBuffer();
		
		linea.setLength(0);
		linea.append("ALTER TABLE node ADD ownerNewDoc VARCHAR(1000) NOT NULL DEFAULT '' ");
		cambiosEstructura.add(linea.toString());
		
		linea.setLength(0);
		linea.append("ALTER TABLE node ADD ownerRead VARCHAR(1000) NOT NULL DEFAULT '' ");
		cambiosEstructura.add(linea.toString());

		linea.setLength(0);
		linea.append("ALTER TABLE node ADD ownerNewFolder VARCHAR(1000) NOT NULL DEFAULT '' ");
		cambiosEstructura.add(linea.toString());

		linea.setLength(0);
		linea.append("ALTER TABLE document ADD codigo VARCHAR(100)");
		cambiosEstructura.add(linea.toString());

		linea.setLength(0);
		linea.append("ALTER TABLE document ADD lote VARCHAR(100)");
		cambiosEstructura.add(linea.toString());
		
		linea.setLength(0);
		linea.append("ALTER TABLE document ADD datePublic ").append(getDateField());
		cambiosEstructura.add(linea.toString());

		linea.setLength(0);
		linea.append("ALTER TABLE document ADD dateRetention ").append(getDateField());
		cambiosEstructura.add(linea.toString());
		
		linea.setLength(0);
		linea.append("ALTER TABLE version ADD createVersion ").append(getDateField()).append(" NOT NULL default ").append(getNow());
		cambiosEstructura.add(linea.toString());
		
		linea.setLength(0);
		linea.append("ALTER TABLE version ADD modifyVersion  ").append(getDateField()).append(" NOT NULL default ").append(getNow());
		cambiosEstructura.add(linea.toString());
		
		linea.setLength(0);
		linea.append("ALTER TABLE document ADD blockTask INT");
		cambiosEstructura.add(linea.toString());
	
		linea.setLength(0);
		linea.append("ALTER TABLE document ADD idVersion INT");
		cambiosEstructura.add(linea.toString());

		linea.setLength(0);
		linea.append("ALTER TABLE document ADD idVersionApproved INT");
		cambiosEstructura.add(linea.toString());
		
		linea.setLength(0);
		linea.append("CREATE TABLE parametro (");
		linea.append("	idParametro varchar(50) NOT NULL ");
		linea.append("	, valor varchar(250) NOT NULL ");
		linea.append("	, utilidad varchar(250) NOT NULL ");
		linea.append("	, editable int NOT NULL ");
		linea.append("	, PRIMARY KEY (idParametro)");
		linea.append(") ");
		cambiosEstructura.add(linea.toString());
		
		linea.setLength(0);
		linea.append("INSERT INTO parametro VALUES('EXT_NO_EDITABLES','pdf','Extensiones de archivo no editables',1)");
		cambiosEstructura.add(linea.toString());
		
		linea.setLength(0);
		linea.append("ALTER TABLE config ADD serial varchar(500)");
		cambiosEstructura.add(linea.toString());
	}

	public void loadCambioData() {
		StringBuffer linea = new StringBuffer();

		//linea.setLength(0);
		//linea.append("update config set version='").append(Constants.VERSION_SISTEMA).append("' ");
		cambiosData.add(linea.toString());
		
		
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}
	
	
}
