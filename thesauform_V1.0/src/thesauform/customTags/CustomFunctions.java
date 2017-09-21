package thesauform.customTags;

import java.io.UnsupportedEncodingException;

public class CustomFunctions {
	public static String encode(String parameter){
		try {
			//TODO detect encoding
			parameter = java.net.URLEncoder.encode(parameter,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return parameter;
	}

	public static String decode(String parameter){
		try {
			//TODO detect encoding
			parameter = java.net.URLDecoder.decode(parameter,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return parameter;
	}
}
