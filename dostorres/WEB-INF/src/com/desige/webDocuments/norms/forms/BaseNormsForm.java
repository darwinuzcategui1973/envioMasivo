package com.desige.webDocuments.norms.forms;

import java.io.Serializable;

import org.apache.struts.upload.FormFile;

import com.desige.webDocuments.utils.beans.SuperActionForm;

/**
 * Title: ${name}.java<br>
 * Copyright: (c) 2003 Consis International<br>
 * Company: Consis International (CON)<br>
 * @author Nelson Crespo (NC)
 * @version Acsel-e v2.2
 * <br>
 * Changes:<br>
 * <ul>
 *      <li> 22/08/2004 (NC) Creation </li>
 * <ul>
 */         //private FormFile myFile
public class BaseNormsForm extends SuperActionForm implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 359762782753751188L;
	
	private String id;
    private String name;
    private String descript;
    private String contentType;
    private FormFile nameFile;
    private String fileNameFisico;
    private String path;
    private boolean loadDoc;
    private int documentRequired;
    private int auditProcess;
    
    private String indice;
    private String sistemaGestion;
    
    public boolean getLoadDoc() {
       return loadDoc;
   }

   public void setLoadDoc(boolean loadDoc) {
       this.loadDoc = loadDoc;
   }

    public String getPath() {
       return path;
   }

   public void setPath(String path) {
       this.path = path;
   }


    public String getFileNameFisico() {
       return fileNameFisico;
   }

   public void setFileNameFisico(String fileNameFisico) {
       this.fileNameFisico = fileNameFisico;
   }


  //aqui se carga el archivo como tal
    public FormFile getNameFile() {
        return nameFile;
    }

    public void setNameFile(FormFile nameFile) {
        this.nameFile = nameFile;
    }
//-----------------------------------------


     public String getContextType() {
        return contentType;
    }

    public void setContextType(String contentType) {
        this.contentType = contentType;
    }






    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public void cleanForm(){
        setId("");
        setName("");
        setDescript("");
        setNameFile(null);
        setContextType("");
        setFileNameFisico("");
        setPath("");
        setIndice("");
        setSistemaGestion("");
    }
    
    public String getDescript() {
        return descript;
    }

    public void setDescript(String descript) {
        this.descript = descript;
    }
    
    public String getKeySearch() {
		String keySearch = name + indice;
    	return keySearch.replaceAll("\\.", "").replaceAll(" ", "").replaceAll(":", "").replaceAll("-", "");
	}
    
    public String getIndice() {
		return indice;
	}
    
    public void setIndice(String indice) {
		this.indice = indice;
	}
    
    public String getSistemaGestion() {
		return sistemaGestion;
	}
    
    public void setSistemaGestion(String sistemaGestion) {
		this.sistemaGestion = sistemaGestion;
	}

	public int getDocumentRequired() {
		return documentRequired;
	}

	public void setDocumentRequired(int documentRequired) {
		this.documentRequired = documentRequired;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("BaseNormsForm [id=").append(id).append(", name=")
				.append(name).append(", descript=").append(descript)
				.append(", contentType=").append(contentType)
				.append(", nameFile=").append(nameFile)
				.append(", fileNameFisico=").append(fileNameFisico)
				.append(", path=").append(path).append(", loadDoc=")
				.append(loadDoc).append(", indice=").append(indice)
				.append(", sistemaGestion=").append(sistemaGestion).append("]");
		return builder.toString();
	}

	public int getAuditProcess() {
		return auditProcess;
	}

	public void setAuditProcess(int auditProcess) {
		this.auditProcess = auditProcess;
	}
    
    
}
