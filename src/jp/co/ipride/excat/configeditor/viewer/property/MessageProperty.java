package jp.co.ipride.excat.configeditor.viewer.property;

import java.util.Locale;
import java.util.ResourceBundle;

public class MessageProperty {

	private static final ResourceBundle messageProperty; 

	static {
		messageProperty = ResourceBundle.getBundle("Message", Locale.getDefault());
	}
	
	private MessageProperty() {
	}
	
	public static String getResource(String name) {
			String str = messageProperty.getString(name);
			return str;
	}

}
