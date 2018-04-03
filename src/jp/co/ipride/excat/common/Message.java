/*
 * Error Anaylzer Tool for Java
 * 
 * Created on 2006/4/1
 * 
 * Copyright (c) 2006-2009 iPride Co.,Ltd.
 * All rights reserved.
 */
package jp.co.ipride.excat.common;

import java.text.MessageFormat;
import java.util.ResourceBundle;



/**
 * メッセージ画面
 * 
 * @author GuanXH
 * @since 2006/9/17
 *
 */
public class Message {
	private static final ResourceBundle messages;
	static {
		messages = ResourceBundle.getBundle("Message");
	}
	
	private Message() {
	}
	
	public static String get(String name) {
		return messages.getString(name);
	}
	
	public static String getMsgWithParam(String msgID,String param){
		
		String msgTemplate = messages.getString(msgID);
		if(msgTemplate != null){
			String msg = MessageFormat.format(msgTemplate, new String[]{param});
			return msg;
		}
		return null;
	}
}
