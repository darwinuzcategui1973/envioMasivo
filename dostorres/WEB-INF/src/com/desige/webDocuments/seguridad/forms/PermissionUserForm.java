package com.desige.webDocuments.seguridad.forms;

import java.io.Serializable;

import com.desige.webDocuments.utils.beans.SuperActionForm;

/**
 * Title: PermissionUserForm.java <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * @author Ing. Nelson Crespo
 * @author Ing. Simon Rodriguez (SR)
 * @version WebDocuments v3.0
 * <br/>
 *      Changes:<br/>
 * <ul>
 *      <li> 15/12/2004 (NC) Creation </li>
 *      <li> 18/05/2005 se agrego toEditRegister </li>
 *      <li> 02/06/2005 se agrego toImpresion </li>
 *      <li> 10/06/2005 se agrego toCheckTodos </li>
 *      <li> 30/06/2006 (NC) New constructor add </li>
 * </ul>
 */
public class PermissionUserForm extends SuperActionForm implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 8156300010721050620L;
	
	private String idStruct;
    private String idUser;
    private String nameUser;
    private long idGroup;
    private byte toView;
    private byte toRead;
    private byte toAddFolder;
    private byte toAddProcess;
    private byte toDelete;
    private byte toEdit;
    private byte toMove;
    private byte toAdmon;
    private String command;
    private String idDocument;
    private byte toPrint;
    private byte toReview;
    private byte toAprove;
    private byte toAddDocument;
    private byte checkOut;
    private String rout;
    private long idPerson;
    private String nodeType;

    private byte toViewDocs;
    private byte toEditDocs;
    private byte toAdmonDocs;
    private byte toDelDocs;
    private byte toMoveDocs;
    private byte toEditRegister;
    private byte toImpresion;
    private byte toCheckTodos;
    private byte toDoFlows;
    private byte toDocinLine;
    private byte toFlexFlow;
    private byte toChangeUsr;
    private byte toCompleteFlow;
    private byte toPublicEraser;
    private byte toDownload;

    private int permisoModificado;

    
    private String name;
    private String cargo;
    
    // Para permisos del modulo de estadisticas
	private byte toGenerate;
	private byte toUpdate;
	private byte toSend;
	private byte toExport;

	// Para permiso el modulo de expedientes
	private byte toFilesSecurity;
	private byte toFilesCreate;
	private byte toFilesExport;
	private byte toFilesEdit;
	private byte toFilesDelete;
	private byte toFilesRelated;
	private byte toFilesVersion;
	private byte toFilesView;
	private byte toFilesPrint;
	private byte toFilesHistory;
	private byte toFilesDownload;
	private byte toFilesSave;
	
    public byte getToDocinLine() {
                return toDocinLine;
            }

            public void setToDocinLine(byte toDocinLine) {
                this.toDocinLine = toDocinLine;
            }



    public byte getToDoFlows() {
            return toDoFlows;
        }

        public void setToDoFlows(byte toDoFlows) {
            this.toDoFlows = toDoFlows;
        }






    public byte getToCheckTodos() {
        return toCheckTodos;
    }

    public void setToCheckTodos(byte toCheckTodos) {
        this.toCheckTodos = toCheckTodos;
    }


    public byte getToImpresion() {
        return toImpresion;
    }

    public void setToImpresion(byte toImpresion) {
        this.toImpresion = toImpresion;
    }
    public byte getToEditRegister() {
        return toEditRegister;
    }

    public void setToEditRegister(byte toEditRegister) {
        this.toEditRegister = toEditRegister;
    }

    public PermissionUserForm() {

    }

    public PermissionUserForm(byte permission) {
        setCheckOut(permission);
        setToAddDocument(permission);
        setToAddFolder(permission);
        setToAddProcess(permission);
        setToAdmon(permission);
        setToAdmonDocs(permission);
        setToAprove(permission);
        setToCheckTodos(permission);
        setToDelDocs(permission);
        setToDelete(permission);
        setToDocinLine(permission);
        setToDoFlows(permission);
        setToEdit(permission);
        setToEditDocs(permission);
        setToEditRegister(permission);
        setToImpresion(permission);
        setToMove(permission);
        setToMoveDocs(permission);
        setToPrint(permission);
        setToRead(permission);
        setToReview(permission);
        setToView(permission);
        setToViewDocs(permission);
        setToChangeUsr(permission);
        setToFlexFlow(permission);
        setToCompleteFlow(permission);
        setToPublicEraser(permission);
        setToGenerate(permission);
		setToUpdate(permission);
		setToSend(permission);
		setToExport(permission);
		setToPrint(permission);
		setToPublicEraser(permission);
		setToDownload(permission);
		
        // para seguridad de expedientes
        setToFilesSecurity(permission);
        setToFilesCreate(permission);
        setToFilesExport(permission);
        setToFilesEdit(permission);
        setToFilesDelete(permission);
        setToFilesRelated(permission);
        setToFilesVersion(permission);
        setToFilesView(permission);
        setToFilesPrint(permission);
        setToFilesHistory(permission);
        setToFilesDownload(permission);
        setToFilesSave(permission);
		
    }

    public PermissionUserForm(String idStruct,String idUser) {
        setIdStruct(idStruct);
        setIdUser(idUser);
    }

    public PermissionUserForm(String idStruct) {
        setIdStruct(idStruct);
    }

    public String getIdStruct() {
        return idStruct;
    }

    public void setIdStruct(String idStruct) {
        this.idStruct = idStruct;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public byte getToRead() {
        return toRead;
    }

    public void setToRead(byte toRead) {
        this.toRead = toRead;
    }

    public byte getToAdmon() {
        return toAdmon;
    }

    public void setToAdmon(byte toAdmon) {
        this.toAdmon = toAdmon;
    }

    public String getNameUser() {
        return nameUser;
    }

    public void setNameUser(String nameUser) {
        this.nameUser = nameUser;
    }

    public byte getToAddFolder() {
        return toAddFolder;
    }

    public void setToAddFolder(byte toAddFolder) {
        this.toAddFolder = toAddFolder;
    }

    public byte getToAddProcess() {
        return toAddProcess;
    }

    public void setToAddProcess(byte toAddProcess) {
        this.toAddProcess = toAddProcess;
    }

    public byte getToDelete() {
        return toDelete;
    }

    public void setToDelete(byte toDelete) {
        this.toDelete = toDelete;
    }

    public byte getToEdit() {
        return toEdit;
    }

    public void setToEdit(byte toEdit) {
        this.toEdit = toEdit;
    }

    public byte getToMove() {
        return toMove;
    }

    public void setToMove(byte toMove) {
        this.toMove = toMove;
    }

    public String toString() {
        StringBuffer data = new StringBuffer(60);
        data.append("[idStruct: ").append(getIdStruct()).append(" idUser: ").append(getIdUser()).append("\n");
        data.append(" nameUser: ").append(getNameUser()).append(" toRead: ").append(getToRead()).append("\n");
        data.append(" toAddFolder: ").append(getToAddFolder()).append(" toAddProcess: ").append(getToAddProcess()).append("\n");
        data.append(" toViewDocs: ").append(getToViewDocs()).append("\n");
        data.append(" permisoModificado: ").append(getPermisoModificado()).append("\n");
        data.append("]\n");
        return data.toString();
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getIdDocument() {
        return idDocument;
    }

    public void setIdDocument(String idDocument) {
        this.idDocument = idDocument;
    }

    public byte getToPrint() {
        return toPrint;
    }

    public void setToPrint(byte toPrint) {
        this.toPrint = toPrint;
    }

    public byte getToReview() {
        return toReview;
    }

    public void setToReview(byte toReview) {
        this.toReview = toReview;
    }

    public byte getToAprove() {
        return toAprove;
    }

    public void setToAprove(byte toAprove) {
        this.toAprove = toAprove;
    }

    public byte getToAddDocument() {
        return toAddDocument;
    }

    public void setToAddDocument(byte toAddDocument) {
        this.toAddDocument = toAddDocument;
    }

    public String getRout() {
        return rout;
    }

    public void setRout(String rout) {
        this.rout = rout;
    }

    public byte getCheckOut() {
        return checkOut;
    }

    public void setCheckOut(byte checkOut) {
        this.checkOut = checkOut;
    }

    public long getIdPerson() {
        return idPerson;
    }

    public void setIdPerson(long idPerson) {
        this.idPerson = idPerson;
    }

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    public byte getToViewDocs() {
        return toViewDocs;
    }

    public void setToViewDocs(byte toViewDocs) {
        this.toViewDocs = toViewDocs;
    }

    public byte getToEditDocs() {
        return toEditDocs;
    }

    public void setToEditDocs(byte toEditDocs) {
        this.toEditDocs = toEditDocs;
    }

    public byte getToAdmonDocs() {
        return toAdmonDocs;
    }

    public void setToAdmonDocs(byte toAdmonDocs) {
        this.toAdmonDocs = toAdmonDocs;
    }

    public byte getToDelDocs() {
        return toDelDocs;
    }

    public void setToDelDocs(byte toDelDocs) {
        this.toDelDocs = toDelDocs;
    }

    public byte getToMoveDocs() {
        return toMoveDocs;
    }

    public void setToMoveDocs(byte toMoveDocs) {
        this.toMoveDocs = toMoveDocs;
    }

    public byte getToView() {
        return toView;
    }

    public void setToView(byte toView) {
        this.toView = toView;
    }

    public long getIdGroup() {
        return idGroup;
    }

    public void setIdGroup(long idGroup) {
        this.idGroup = idGroup;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }
    public byte getToFlexFlow() {
        return toFlexFlow;
    }

    public void setToFlexFlow(byte toFlexFlow) {
        this.toFlexFlow = toFlexFlow;
    }

    public byte getToChangeUsr() {
        return toChangeUsr;
    }

    public void setToChangeUsr(byte toChangeUsr) {
        this.toChangeUsr = toChangeUsr;
    }

	public byte getToCompleteFlow() {
		return toCompleteFlow;
	}

	public void setToCompleteFlow(byte toCompleteFlow) {
		this.toCompleteFlow = toCompleteFlow;
	}

	public int getPermisoModificado() {
		return permisoModificado;
	}

	public void setPermisoModificado(int permisoModificado) {
		this.permisoModificado = permisoModificado;
	}

	public byte getToPublicEraser() {
		return toPublicEraser;
	}

	public void setToPublicEraser(byte toPublicEraser) {
		this.toPublicEraser = toPublicEraser;
	}

	public byte getToExport() {
		return toExport;
	}

	public void setToExport(byte toExport) {
		this.toExport = toExport;
	}

	public byte getToGenerate() {
		return toGenerate;
	}

	public void setToGenerate(byte toGenerate) {
		this.toGenerate = toGenerate;
	}

	public byte getToSend() {
		return toSend;
	}

	public void setToSend(byte toSend) {
		this.toSend = toSend;
	}

	public byte getToUpdate() {
		return toUpdate;
	}

	public void setToUpdate(byte toUpdate) {
		this.toUpdate = toUpdate;
	}

	public byte getToFilesCreate() {
		return toFilesCreate;
	}

	public void setToFilesCreate(byte toFilesCreate) {
		this.toFilesCreate = toFilesCreate;
	}

	public byte getToFilesDelete() {
		return toFilesDelete;
	}

	public void setToFilesDelete(byte toFilesDelete) {
		this.toFilesDelete = toFilesDelete;
	}

	public byte getToFilesEdit() {
		return toFilesEdit;
	}

	public void setToFilesEdit(byte toFilesEdit) {
		this.toFilesEdit = toFilesEdit;
	}

	public byte getToFilesExport() {
		return toFilesExport;
	}

	public void setToFilesExport(byte toFilesExport) {
		this.toFilesExport = toFilesExport;
	}

	public byte getToFilesHistory() {
		return toFilesHistory;
	}

	public void setToFilesHistory(byte toFilesHistory) {
		this.toFilesHistory = toFilesHistory;
	}

	public byte getToFilesPrint() {
		return toFilesPrint;
	}

	public void setToFilesPrint(byte toFilesPrint) {
		this.toFilesPrint = toFilesPrint;
	}

	public byte getToFilesRelated() {
		return toFilesRelated;
	}

	public void setToFilesRelated(byte toFilesRelated) {
		this.toFilesRelated = toFilesRelated;
	}

	public byte getToFilesVersion() {
		return toFilesVersion;
	}

	public void setToFilesVersion(byte toFilesVersion) {
		this.toFilesVersion = toFilesVersion;
	}

	public byte getToFilesView() {
		return toFilesView;
	}

	public void setToFilesView(byte toFilesView) {
		this.toFilesView = toFilesView;
	}

	public byte getToFilesSecurity() {
		return toFilesSecurity;
	}

	public void setToFilesSecurity(byte toFilesSecurity) {
		this.toFilesSecurity = toFilesSecurity;
	}

	public byte getToFilesDownload() {
		return toFilesDownload;
	}

	public void setToFilesDownload(byte toFilesDownload) {
		this.toFilesDownload = toFilesDownload;
	}

	public byte getToFilesSave() {
		return toFilesSave;
	}

	public void setToFilesSave(byte toFilesSave) {
		this.toFilesSave = toFilesSave;
	}

	public byte getToDownload() {
		return toDownload;
	}

	public void setToDownload(byte toDownload) {
		this.toDownload = toDownload;
	}

}
