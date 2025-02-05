package com.gestionEnvio.custon.dostorres.interfaces;

import java.util.ArrayList;


public interface ObjetoDosTorresDAO {
	
	public boolean cargar(ObjetoDosTorresTO objetoDosTorresTO) throws Exception ;
	public ArrayList listar() throws Exception ;
	
}


