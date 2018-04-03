package jp.co.ipride.excat.configeditor.util;

import org.w3c.dom.Element;
import org.w3c.dom.Node;


/**
 * ユーティリティ
 * @author tu
 * @since 2007/12/5
 */
public class DocumetUtil {

	public static boolean parseBoolean(String str){
		String upper = str.toLowerCase();
		if ("true".equals(upper)){
			return true;
		}else{
			return false;
		}
	}

	/**
	 * 二つのURLの比較
	 *
	 * @param url1
	 * @param url2
	 * @return
	 */
	public static boolean checkClassLoaderURL(String url1, String url2){
		if (url1.indexOf(url2)>=0 || url2.indexOf(url1)>=0){
			return true;
		}else{
			return false;
		}

	}
	public static void setAttribute(Node node, String attributeName, boolean attributeValue){
		((Element)node).setAttribute(attributeName, Boolean.toString(attributeValue));
	}
	public static void setAttribute(Node node, String attributeName, String attributeValue){
		((Element)node).setAttribute(attributeName, attributeValue);
	}
	public static void setAttribute(Node node, String attributeName, int attributeValue){
		((Element)node).setAttribute(attributeName, Integer.toString(attributeValue));
	}
}
