/*
 * ServicioDocumentoWS.java
 *
 * Created on April 30, 2007, 9:58 AM
 *
 */

package com.focus.qwds.ondemand.server.documentos.ws;

import com.focus.qwds.ondemand.server.documentos.entidades.Documento;
import com.focus.qwds.ondemand.server.documentos.servicios.ServicioDocumento;
import com.focus.qwds.ondemand.server.excepciones.ErrorDeAplicaqcion;
import com.focus.qwds.ondemand.server.usuario.entidades.Usuario;
import com.focus.qwds.ondemand.server.usuario.excepciones.LoginInvalidoException;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

/**
 *
 * @author lcisneros
 */
@WebService()
public class ServicioDocumentoWS {
    
    private ServicioDocumento servicio;
    
    public ServicioDocumentoWS(){
        servicio = new ServicioDocumento();
    }                 

    /**
     * Web service operation
     */
    @WebMethod
    public Documento[] obtenerDocumentosBloqueados(@WebParam(name = "u") Usuario u) throws ErrorDeAplicaqcion {
        // TODO implement operation 
        return servicio.obtenerDocumentosBloqueados(u); 
    }

    /**
     * Web service operation
     */
    @WebMethod
    public byte[] obtenerDocumento(@WebParam(name = "idCheckOut") int idCheckOut) throws ErrorDeAplicaqcion {
        // TODO implement operation 
        return servicio.obtenerDocumento(idCheckOut);
    }

    /**
     * Web service operation
     */
    @WebMethod
    public void guardarDocumento(@WebParam(name = "archivo") byte[] archivo, @WebParam(name = "idCheckOut") int idCheckOut, @WebParam(name = "isFileView") boolean  isFileView) throws ErrorDeAplicaqcion {
        servicio.guardarDocumento(archivo, idCheckOut, isFileView);
    }

    /**
     * Web service operation
     */
    @WebMethod
    public Usuario obtenerUsuario(@WebParam(name = "clave") String clave, @WebParam(name = "usuario") String usuario) throws LoginInvalidoException, ErrorDeAplicaqcion {
        // TODO implement operation 
        return servicio.obtenerUsuario(clave, usuario);
    }

    /**
     * Web service operation
     */
    @WebMethod
    public Documento obtenerUltimoDocumentoCreado(@WebParam(name = "u") Usuario u) throws ErrorDeAplicaqcion {
       
        return servicio.obtenerUltimoDocumentoCreado(u);
    }

    /**
     * Web service operation
     */
    @WebMethod
    public byte[] obtenerDocumentoDeVersion(@WebParam(name = "idVersion") int idVersion) throws ErrorDeAplicaqcion {
        return servicio.obtenerDocumentoDeVersion(idVersion);
    }

    /**
     * Web service operation
     */
    @WebMethod
    public void guardarVersionDocumento(@WebParam(name = "archivo") byte[] archivo, @WebParam(name = "idVersion") int idVersion) throws ErrorDeAplicaqcion {
       servicio.guardarVersionDocumento(archivo, idVersion);
    }

    
    /**
     * Web service operation
     */
    @WebMethod
	public void subirDocumentoDigitalizado(@WebParam(name = "archivo") byte[] archivo, @WebParam(name = "nameFile") String nameFile ) throws ErrorDeAplicaqcion {
    	servicio.subirDocumentoDigitalizado(archivo, nameFile);
    }
    
   
    
    
}
