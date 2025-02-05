package com.focus.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseConverterUtil {
	private static final Logger log = LoggerFactory.getLogger(BaseConverterUtil.class);
	
    private static final String baseDigits = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public static String toBase62( int decimalNumber ) {
        return fromDecimalToOtherBase( 62, decimalNumber );
    }

    public static String toBase36( int decimalNumber ) {
        return fromDecimalToOtherBase( 36, decimalNumber );
    }

    public static String toBase16( int decimalNumber ) {
        return fromDecimalToOtherBase( 16, decimalNumber );
    }

    public static String toBase8( int decimalNumber ) {
        return fromDecimalToOtherBase( 8, decimalNumber );
    }

    public static String toBase2( int decimalNumber ) {
        return fromDecimalToOtherBase( 2, decimalNumber );
    }

    public static int fromBase62( String base62Number ) {
        return fromOtherBaseToDecimal( 62, base62Number );
    }

    public static int fromBase36( String base36Number ) {
        return fromOtherBaseToDecimal( 36, base36Number );
    }

    public static int fromBase16( String base16Number ) {
        return fromOtherBaseToDecimal( 16, base16Number );
    }

    public static int fromBase8( String base8Number ) {
        return fromOtherBaseToDecimal( 8, base8Number );
    }

    public static int fromBase2( String base2Number ) {
        return fromOtherBaseToDecimal( 2, base2Number );
    }

    private static String fromDecimalToOtherBase ( int base, int decimalNumber ) {
        String tempVal = decimalNumber == 0 ? "0" : "";
        int mod = 0;

        while( decimalNumber != 0 ) {
            mod = decimalNumber % base;
            tempVal = baseDigits.substring( mod, mod + 1 ) + tempVal;
            decimalNumber = decimalNumber / base;
        }

        return tempVal;
    }

    private static int fromOtherBaseToDecimal( int base, String number ) {
        int iterator = number.length();
        int returnValue = 0;
        int multiplier = 1;

        while( iterator > 0 ) {
            returnValue = returnValue + ( baseDigits.indexOf( number.substring( iterator - 1, iterator ) ) * multiplier );
            multiplier = multiplier * base;
            --iterator;
        }
        return returnValue;
    }

	public static String toDec(String mac) {
		String[] num = mac.replaceAll("[^a-zA-Z0-9]", "-").split("-");
		StringBuffer ret = new StringBuffer();
		String sep = "";
		
		
		for(int i=0; i<num.length; i++) {
			ret.append(sep).append(Integer.parseInt(num[i],16));
			sep = "-"; //(i%2!=0?"-":"");
		}
		return ret.toString();
	}

	public static String MacToBase36(String mac) {
		log.info("MAC='" + mac + "'");
		mac = toDec(mac);
		String[] num = mac.replaceAll("[^a-zA-Z0-9]", "-").split("-");
		StringBuffer ret = new StringBuffer();
		String sep = "";
		
		for(int i=0; i<num.length; i++) {
			ret.append(sep).append(BaseConverterUtil.toBase36(Integer.parseInt(num[i])));
			sep = "-";
		}
		return ret.toString();
	}
	
	public static String MacFromBase36(String mac) {
		String[] num = mac.replaceAll("[^a-zA-Z0-9]", "-").split("-");
		StringBuffer ret = new StringBuffer();
		String sep = "";
		
		for(int i=0; i<num.length; i++) {
			ret.append(sep).append(BaseConverterUtil.fromBase36(num[i]));
			sep = "-";
		}

		num = ret.toString().replaceAll("[^a-zA-Z0-9]", "-").split("-");
		ret.setLength(0);
		sep = "";
		String pos = "";
		for(int i=0; i<num.length; i++) {
			pos = Integer.toHexString(Integer.parseInt(num[i]));
			ret.append(sep).append(pos.length()==1?"0":"").append(pos);
			sep = "-";
		}

		return ret.toString().toUpperCase();
	}

}
