package com.focus.qweb.to;

import com.desige.webDocuments.area.forms.Area;
import com.desige.webDocuments.utils.ToolsHTML;
import com.focus.qweb.interfaces.ObjetoTO;

public class AreaTO implements ObjetoTO {

	private static final long serialVersionUID = 1L;

	private String idarea;
	private String area;
	private String activea;
	private String prefijo;

	public AreaTO() {
		
	}
	
	public AreaTO(Area a) {
		setIdarea(String.valueOf(a.getIdarea()));
		setArea(a.getArea());
		setActivea(String.valueOf(a.getActive()));
		setPrefijo(a.getPrefijo());
	}
	
	public String getIdarea() {
		return idarea;
	}

	public int getIdareaInt() {
		return ToolsHTML.parseInt(idarea);
	}

	public void setIdarea(String idarea) {
		this.idarea = idarea;
	}


	public String getArea() {
		return area;
	}


	public void setArea(String area) {
		this.area = area;
	}


	public String getActivea() {
		return activea;
	}

	public int getActiveaInt() {
		return ToolsHTML.parseInt(activea);
	}

	public void setActivea(String activea) {
		this.activea = activea;
	}


	public String getPrefijo() {
		return prefijo;
	}


	public void setPrefijo(String prefijo) {
		this.prefijo = prefijo;
	}


	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append("AreaTO [");
		builder.append("  id=").append(idarea);
		builder.append(", area=").append(area);
		builder.append(", activea=").append(activea);
		builder.append(", prefijo=").append(prefijo);
		builder.append("]");

		return builder.toString();
	}

}
