package com.desige.webDocuments.document.comparators;

import java.util.Comparator;

import com.desige.webDocuments.document.forms.BaseDocumentForm;
import com.desige.webDocuments.utils.ToolsHTML;

/**
 * Created by IntelliJ IDEA.
 * User: ydavila
 * Date: 26/12/2006
 * Time: 11:28:22 AM
 * To change this template use File | Settings | File Templates.
 */
public class CreationDateComparator  implements Comparator {

	private boolean isDesc=false;
	
	public CreationDateComparator() {
	}

	public CreationDateComparator(boolean isDesc) {
		this.isDesc=isDesc;
	}

	public int compare(Object o1, Object o2) {
        BaseDocumentForm doc1 = (BaseDocumentForm)o1;
        BaseDocumentForm doc2 = (BaseDocumentForm)o2;
        String fechaI = null;
        String fechaII = null;
        try {
            fechaI = ToolsHTML.sdf.format(ToolsHTML.sdfShow.parse(doc1.getDateCreation()));
            fechaII = ToolsHTML.sdf.format(ToolsHTML.sdfShow.parse(doc2.getDateCreation()));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return (!isDesc?fechaI.compareTo(fechaII):fechaII.compareTo(fechaI));
//        return doc1.getDateCreation().compareTo(doc2.getDateCreation());
    }
}
