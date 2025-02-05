package com.focus.qweb.interfaces;

import java.util.ArrayList;

public interface ObjetoDAO {
	
	public int insertar(ObjetoTO objetoTO) throws Exception ;
	public int actualizar(ObjetoTO objetoTO) throws Exception ;
	public void eliminar(ObjetoTO objetoTO) throws Exception ;
	public boolean cargar(ObjetoTO objetoTO) throws Exception ;
	public ArrayList listar() throws Exception ;
	
}
