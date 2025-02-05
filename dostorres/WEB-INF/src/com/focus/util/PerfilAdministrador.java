package com.focus.util;

import com.desige.webDocuments.utils.DesigeConf;
import com.desige.webDocuments.utils.beans.Users;

/**
 * 
 * Title: PerfilAdministrador.java <br>
 * Copyright: (c) 2012 Focus Consulting C.A.<br/>
 * 
 * @author Ing. Felipe Rojas (FJR)
 * @version WebDocuments v4.5.2
 * 
 * <br>
 *     Changes:<br>
 * <ul>
 *     <li> 21/03/2012 (FJR) Creation </li>
 * <ul>
 */
public final class PerfilAdministrador {
	
	/**
	 * Default empty constructor
	 */
	private PerfilAdministrador(){
		/**/
	}
	
	/**
	 * Funcion que nos permite saber si un determinado usuario pertenece al grupo de administradores del sistema.
	 * 
	 * @param usuario
	 * @return true si el usuario pertenece al grupo de administracion, false para cualquier otro caso.
	 * 
	 */
	public static boolean userIsInAdminGroup(Users usuario){
		boolean isInAdminGroup = false;
		
		if(usuario != null){
			isInAdminGroup = DesigeConf.getProperty("application.admon").equalsIgnoreCase(usuario.getIdGroup());
		}
		
		return isInAdminGroup;
	}
}
