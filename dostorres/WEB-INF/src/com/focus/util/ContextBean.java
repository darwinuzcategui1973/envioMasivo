package com.focus.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.desige.webDocuments.utils.ToolsHTML;

public class ContextBean {

	private HttpServletRequest request;

	private String auth = "Container";
	private String description = "DataSource";
	private String name = "jdbc/WebDocs";
	private String type = "javax.sql.DataSource";
	private String maxActive = "1000";
	private String maxIdle = "2";
	private String username = "qwebdocuments";
	private String maxWait = "5000";
	private String validationQuery = "select * from parameters";
	private String driverClassName = "org.postgresql.Driver";
	private String password = "qwebdocuments";
	private String url = "jdbc:postgresql://localhost:5432/qweb452";

	public ContextBean() {

	}

	public ContextBean(HttpServletRequest request) {
		this.request = request;
	}

	public String getServidor() {
		String[] aUrl = url.split("/");
		return aUrl[2].split(":")[0];
	}

	public String getPort() {
		String[] aUrl = url.split("/");
		return aUrl[2].split(":")[1];
	}

	public String getDatabase() {
		String[] aUrl = url.split("/");
		return aUrl[3];
	}

	/* Getter y setter */

	public void load() {
		try {
			String nameFile = ToolsHTML.getPath().concat("/META-INF/context.xml");

			File fXmlFile = new File(nameFile);

			System.out.println("Existe el xml? " + fXmlFile.getAbsolutePath());
			System.out.println(fXmlFile.exists());

			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);

			// optional, but recommended
			// read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
			doc.getDocumentElement().normalize();

			// System.out.println("Root element :" + doc.getDocumentElement().getNodeName());

			NodeList nList = doc.getElementsByTagName("Resource");

			// System.out.println("----------------------------");

			for (int temp = 0; temp < nList.getLength(); temp++) {

				Node nNode = nList.item(temp);

				// System.out.println("\nCurrent Element :" + nNode.getNodeName());

				if (nNode.getNodeType() == Node.ELEMENT_NODE) {

					Element eElement = (Element) nNode;

					System.out.println("Resource url : " + eElement.getAttribute("url"));

					setAuth(eElement.getAttribute("auth"));
					setDescription(eElement.getAttribute("description"));
					setName(eElement.getAttribute("name"));
					setType(eElement.getAttribute("type"));
					setMaxActive(eElement.getAttribute("maxActive"));
					setMaxIdle(eElement.getAttribute("maxIdle"));
					setUsername(eElement.getAttribute("username"));
					setMaxWait(eElement.getAttribute("maxWait"));
					setValidationQuery(eElement.getAttribute("validationQuery"));
					setDriverClassName(eElement.getAttribute("driverClassName"));
					setPassword(eElement.getAttribute("password"));
					setUrl(eElement.getAttribute("url"));

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void write() {
		try {
			StringBuffer content = new StringBuffer("");
			
			content.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
			content.append("<Context>\n");
			content.append("  <Resource\n");
			content.append("    auth=\"Container\"\n");
			content.append("    description=\"DataSource\"\n");
			content.append("    name=\"jdbc/WebDocs\"\n");
			content.append("    type=\"javax.sql.DataSource\"\n");
			content.append("    maxActive=\"1000\"\n");
			content.append("    maxIdle=\"2\"\n");
			content.append("    username=\"").append(getUsername()).append("\"\n");
			content.append("    maxWait=\"5000\"\n");
			content.append("    validationQuery=\"select * from parameters\"\n");
			content.append("    driverClassName=\"").append(getDriverClassName()).append("\"\n");
			content.append("    password=\"").append(getPassword()).append("\"\n");
			content.append("    url=\"").append(getUrl()).append("\"/>\n");
			content.append("</Context>\n");

			
			String nameFile = ToolsHTML.getPath().concat("/META-INF/context.xml");
			File file = new File(nameFile);

			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(content.toString());
			bw.close();

			System.out.println("Done");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	public void writeEclipse() {
		try {
			StringBuffer content = new StringBuffer("");
			
			// solo para probar en eclipse
			
			content.setLength(0);
			content.append(" <Context path=\"/qwebds4\" reloadable=\"true\" docBase=\"E:\\appProyectos\\qweb\\qweb452\\appworkspace452\\qwebds4\" workDir=\"E:\\appProyectos\\qweb\\qweb452\\appworkspace452\\qwebds4\\work\" > \n");
			content.append("<Logger className=\"org.apache.catalina.logger.SystemOutLogger\" verbosity=\"4\" timestamp=\"true\"/>\n");
			content.append("\n");
			content.append("<Resource\n");
			content.append("auth=\"Container\"\n");
			content.append("description=\"DataSource\"\n");
			content.append("name=\"jdbc/WebDocs\"\n");
			content.append("type=\"javax.sql.DataSource\"\n");
			content.append("maxActive=\"1000\"\n");
			content.append("maxIdle=\"2\"\n");
			content.append("username=\"").append(getUsername()).append("\"\n");
			content.append("maxWait=\"5000\"\n");
			content.append("validationQuery=\"select * from parameters\"\n");
			content.append("driverClassName=\"").append(getDriverClassName()).append("\"\n");
			content.append("password=\"").append(getPassword()).append("\"\n");
			content.append("url=\"").append(getUrl()).append("\"/>\n");
			content.append("\n");
			content.append("</Context>\n");
			
			
			String nameFile = "E:\\appProyectos\\qweb\\qweb452\\tomcat7\\conf\\Catalina\\localhost\\qwebds4.xml";
			File file = new File(nameFile);

			// if file doesnt exists, then create it
			if (!file.exists()) {
				// no lo creamos porque no existe
				return;
				//file.createNewFile();
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(content.toString());
			bw.close();

			System.out.println("Done");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String getAuth() {
		return auth;
	}

	public void setAuth(String auth) {
		this.auth = auth;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDriverClassName() {
		return driverClassName;
	}

	public void setDriverClassName(String driverClassName) {
		this.driverClassName = driverClassName;
	}

	public String getMaxActive() {
		return maxActive;
	}

	public void setMaxActive(String maxActive) {
		this.maxActive = maxActive;
	}

	public String getMaxIdle() {
		return maxIdle;
	}

	public void setMaxIdle(String maxIdle) {
		this.maxIdle = maxIdle;
	}

	public String getMaxWait() {
		return maxWait;
	}

	public void setMaxWait(String maxWait) {
		this.maxWait = maxWait;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getValidationQuery() {
		return validationQuery;
	}

	public void setValidationQuery(String validationQuery) {
		this.validationQuery = validationQuery;
	}

}
