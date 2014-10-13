package com.fav24.shootr.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtils {
	
	public static boolean match(Pattern pattern, String str){
		Matcher matcher = pattern.matcher(str);
		return matcher.matches();
	}
}
