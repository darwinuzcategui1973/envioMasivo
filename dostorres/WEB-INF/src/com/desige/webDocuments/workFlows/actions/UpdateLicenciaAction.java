package com.desige.webDocuments.workFlows.actions;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.utils.ToolsHTML;
import com.focus.util.Archivo;
import com.focus.util.NetworkInfo;

public class UpdateLicenciaAction extends Action {

	public synchronized ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {

		try {
			PrintWriter out = response.getWriter();

			String emp = request.getParameter("emp");
			String rif = request.getParameter("rif");
			String mac = NetworkInfo.getMacAddress();
			String lic = request.getParameter("lic");

			if (emp == null || emp.trim().equals(""))
				return null;
			if (rif == null || rif.trim().equals(""))
				return null;
			if (mac == null || mac.trim().equals(""))
				return null;
			if (lic == null || lic.trim().equals(""))
				return null;
			
			emp = emp.replaceAll(".ampersand.", "&");
			
			
			// validamos la codificacion de la licencia
			
			// crearemos el archivo que almacenara la licencia
			StringBuffer cad = new StringBuffer();
			cad.append(emp).append("|");
			cad.append(rif).append("|");
			cad.append(mac).append("|");
			cad.append(lic);
			
			
			ToolsHTML tool = new ToolsHTML();
			
			String key = tool.decifrar(lic,emp);
			
			Archivo arc = new Archivo();
			arc.escribir(ToolsHTML.getPathKey(), cad.toString());
			
			out.print("true");
			

		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			//e.printStackTrace();
		}

		return null;
	}
	
	
	public final static int suma(String mac) throws IOException {
		BigInteger val = new BigInteger(mac.replaceAll("-", "").replaceAll(":", ""), 16);
		char[] numero = val.toString(10).toCharArray();
		int suma = 0;
		for(int i=0;i<numero.length;i++) {
			suma += Integer.parseInt(String.valueOf(numero[i]));
		}
		return suma;
	}
	
	
	
	private String zero(String cad, int largo) {
		if (cad.length() < largo) {
			cad = "0".concat(cad);
			cad = zero(cad, largo);
		}
		return cad;
	}
	
	
	public String xor(String cad) {
		int nCad = cad.length();
		byte[] valor = cad.getBytes();
		StringBuffer ret = new StringBuffer();
		
		for(int i=0; i<nCad;i++) {
			ret.append(valor[i]^33);
		}
		
		return ret.toString();
	}
}
