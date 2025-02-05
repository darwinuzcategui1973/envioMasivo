package com.desige.webDocuments.document.comparators;

import java.util.Comparator;

import com.desige.webDocuments.document.forms.BaseDocumentForm;

/**
 * Title: VersionComparator.java <br/>
 * Copyright: (c) 2007 Focus Consulting C.A. <br/>
 * @author Ing. Nelson Crespo (NC)
 * @version QwebDocuments v4.0
 * <br/>
 *     Changes:<br/>
 * <ul>
 *      <li> 2007-01-24 (NC) Creation </li>
 * </ul>
 */
public class VersionComparator implements Comparator {

	private boolean isDesc=false;
	
	public VersionComparator() {
	}

	public VersionComparator(boolean isDesc) {
		this.isDesc=isDesc;
	}

	public int compare(Object o1, Object o2) {
        BaseDocumentForm doc1 = (BaseDocumentForm)o1;
        BaseDocumentForm doc2 = (BaseDocumentForm)o2;
        String verDoc1 = doc1.getMayorVer().trim() + "." + doc1.getMinorVer().trim();
        String verDoc2 = doc2.getMayorVer().trim() + "." + doc2.getMinorVer().trim();
        try {
        	double nVerDoc1 = Double.parseDouble(verDoc1);
        	double nVerDoc2 = Double.parseDouble(verDoc2);

            return (!isDesc?(nVerDoc1>nVerDoc2?1:-1):(nVerDoc2>nVerDoc1?1:-1));

        } catch(Exception e) {
        	// no hacemos nada
        }
        return (!isDesc?verDoc1.compareToIgnoreCase(verDoc2):verDoc2.compareToIgnoreCase(verDoc1));
    }
}
