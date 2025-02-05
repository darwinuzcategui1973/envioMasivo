package com.desige.webDocuments.util;

import java.security.Key;

import javax.xml.bind.DatatypeConverter;

import com.desige.webDocuments.utils.StringUtil;

public class Test {

	/**
	 * @param args
	 */
	public static void main2(String[] args) { 
		// TODO Auto-generated method stub
		
		Key k = (Key)Encryptor.getKey("16-171-151-56-196-97-64-7");
		//System.out.println(k.serialVersionUID);
		//System.out.println(k.getAlgorithm());
		//System.out.println(k.getFormat());

	}
	
	public static void main(String[] args) {
		
		 String c = "<span [] á-Ñ style=\"<span style=\"color: rgb(41, 41, 41); font-family: charter, Georgia, Cambria, &quot;Times New Roman&quot;, Times, serif; font-size: 20px; letter-spacing: -0.06px; background-color: rgb(255, 255, 255);\">Sin embargo, con la especificación ES2015 llegó la posibilidad de crear lo que se conoce como </span><em class=\"kz\" style=\"box-sizing: inherit; color: rgb(41, 41, 41); font-family: charter, Georgia, Cambria, &quot;Times New Roman&quot;, Times, serif; font-size: 20px; letter-spacing: -0.06px; background-color: rgb(255, 255, 255);\">template literals&nbsp;</em><span style=\"color: rgb(41, 41, 41); font-family: charter, Georgia, Cambria, &quot;Times New Roman&quot;, Times, serif; font-size: 20px; letter-spacing: -0.06px; background-color: rgb(255, 255, 255);\">o </span><em class=\"kz\" style=\"box-sizing: inherit; color: rgb(41, 41, 41); font-family: charter, Georgia, Cambria, &quot;Times New Roman&quot;, Times, serif; font-size: 20px; letter-spacing: -0.06px; background-color: rgb(255, 255, 255);\">template strings</em><span style=\"color: rgb(41, 41, 41); font-family: charter, Georgia, Cambria, &quot;Times New Roman&quot;, Times, serif; font-size: 20px; letter-spacing: -0.06px; background-color: rgb(255, 255, 255);\">&nbsp;para lo cual basta con encapsular texto entre comillas invertidas:</span> ";
			//System.out.println(c.replaceAll("^[a-zA-Z0-9!@#$%^*(),.<>~`{}\\/+=-]*","***"));
			//System.out.println(c.replaceAll("[!-~]*$","*"));
			
	        String str = c;
	        // encode data using BASE64
	        String encoded = StringUtil.serialize(str);
	        System.out.println("encoded value is \t" + encoded);

	        // Decode data 
	        String decoded = StringUtil.deserialize(encoded);
	        System.out.println("decoded value is \t" + decoded);

	        System.out.println("original value is \t" + str);
		
	}

}
