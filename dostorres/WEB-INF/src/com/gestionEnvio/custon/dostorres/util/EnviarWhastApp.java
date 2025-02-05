package com.gestionEnvio.custon.dostorres.util;

import java.io.IOException;
import java.sql.SQLException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.desige.webDocuments.utils.ToolsHTML;
import com.focus.qwds.ondemand.server.excepciones.ErrorDeAplicaqcion;
import com.gestionEnvio.custon.dostorres.forms.WhastAppForm;


public class EnviarWhastApp {
	
	private static Logger log = LoggerFactory.getLogger(EnviarWhastApp.class.getName());
	private static final String COMILLA ="\"" ;
	private static ClientConnectionManager simpleClientConnMng = null;
	private static HttpPost  httpPostReq = null;
	private static HttpResponse response = null;

	public static boolean  enviarMensajeWhatsapp(WhastAppForm forma)throws ErrorDeAplicaqcion, SQLException, IOException	{
		
		 String mensaje=forma.getMensaje(); 
		 String telefono=forma.getTelefono();

		if (telefono == null) {
			//throw new ErrorDeAplicaqcion("Es necesario El Telefono no se puede ejecutar el servicio");
			return false;
		}
		if (telefono.isEmpty()) {
			return false;
			//throw new ErrorDeAplicaqcion("Es necesario El Telefono no pueden estar vacio *** no se puede ejecutar el servicio");
			
			
		}
		
		if (mensaje == null) {
			return false;
			//throw new ErrorDeAplicaqcion("Es necesario El mensaje no se puede ejecutar el servicio");
		}
		
		if (mensaje.isEmpty()) {
			return false;
			//throw new ErrorDeAplicaqcion("Es necesario El mensaje no pueden estar vacio *** no se puede ejecutar el servicio");
			
			
		}
		//if (!ToolsHTML.isEmptyOrNull(value)) {
		if (!ToolsHTML.isEmptyOrNull(mensaje) && !ToolsHTML.isEmptyOrNull(telefono) ) {
			enviarPostWhastApp(telefono, mensaje);
		}

	return true;
		

	}
	
	
	@SuppressWarnings("unlikely-arg-type")
	static	void enviarPostWhastApp(String telefono,String mensaje) throws ErrorDeAplicaqcion,  IOException, ClientProtocolException {
		DefaultHttpClient client = null;
		 boolean respuesta = false;
		client = new DefaultHttpClient(simpleClientConnMng);
		//aqui url lo puedo mandar como un parametro
		httpPostReq = new HttpPost("http://localhost:3005/api/whatsapp/send"); 
		
	    /*body y encabezados*/
		String JSON_STRING="{"
				+COMILLA+"numeroDestino"+COMILLA+":"+COMILLA+telefono+COMILLA+","
				+ COMILLA+"mensaje"+COMILLA+":"+COMILLA+mensaje+COMILLA
				+ "}";
		  System.out.println(JSON_STRING);
	    HttpEntity stringEntity = new StringEntity(JSON_STRING,ContentType.APPLICATION_JSON);
	    httpPostReq.setEntity(stringEntity);
	    httpPostReq.addHeader("accept", "application/json");
		//httpPostReq.setDoAuthentication(true);
		httpPostReq.addHeader("content-type", "application/json");		
		//httpPostReq.addHeader("Accept", getAccept());		
		httpPostReq.addHeader("Keep-Alive", "300");		
		httpPostReq.addHeader("Connection", "Keep-Alive");
		
		try {
			response = client.execute(httpPostReq);
			respuesta = response.toString().contains(HttpStatus.SC_OK+"");
			if (!respuesta) { 
			throw new  ErrorDeAplicaqcion ("Error en servicio externo de whastsAPP "
					+ "\r\n"
					+ response);
			}
			System.out.println("-------------response------------");
			System.out.println(respuesta);
			System.out.println(response);
			
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			
			e.printStackTrace();
			throw new  ClientProtocolException ("Error en servicio externo de whastsAPP "+ "\r\n"
					+ e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new  IOException ("Error en servicio externo de whastsAPP "+ "\r\n"
					+ e);
		}
		
		System.out.println("-------------response------------");
		System.out.println(respuesta);
		System.out.println(response);
		

	}

	
	
}
		  




