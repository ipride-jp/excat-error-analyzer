package jp.co.ipride.excat.analyzer.common;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import jp.co.ipride.ExcatLicenseException;
import jp.co.ipride.excat.common.utility.HelperFunc;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public class DOMParser {

	public static Document getDocument(String path)throws ExcatLicenseException{
		try{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			String result = DumpFile.getDumpFileContent(path);
			return builder.parse(new InputSource(new StringReader(result)));
		} catch(ExcatLicenseException e){
			e.setPath(path);
			throw e;
        } catch (Exception e) {
			HelperFunc.getLogger().error("DOMParser", e);
			return null;
        }
	}

}
