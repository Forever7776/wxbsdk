/**
 * 
 */
package com.wxb.ext.kit;

import java.util.regex.Pattern;

/**
 * @author BruceZCQ
 *
 */
public class MobileValidateKit {

	public static Boolean isValidate(String mobile) {
		 return Pattern
			.compile(
					"^((13[0-9])|(147)|(15[^4,\\D])|(18[0-9])|(17[0-9]))\\d{8}$")
			.matcher(mobile).find();
	}
	
}
