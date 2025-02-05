package com.desige.webDocuments.structured.forms;

import com.desige.webDocuments.utils.Constants;

/**
 * Title: InsertStructForm.java <br/> 
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * @author Ing. Nelson Crespo (NC)
 * @version WebDocuments v3.0
 * <br/>
 * Changes:<br/> 
 * <ul>
 *      <li>18/04/2004 (NC) Creation </li>
 </ul>
 */
public class InsertStructForm extends BaseStructForm {
    public void initFields() {
        setDisableUserWF(Constants.permission);
        setSequential(Constants.permission);
        setConditional(Constants.permission);
        setExpireWF(Constants.permission);
        setNotify(Constants.permission);
    }
}
