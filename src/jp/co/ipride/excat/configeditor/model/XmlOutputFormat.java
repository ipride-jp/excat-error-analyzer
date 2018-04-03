package jp.co.ipride.excat.configeditor.model;

import java.io.PrintWriter;
import java.util.Vector;

import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author tu
 * @since 2007/12/8
 */
public class XmlOutputFormat{

	static String INDENT_SPACE = "    ";   //4 space

	static int MAX_WORDES = 80;            //行幅

	private int indent;                    //インデトのインデックス

	private String name;                    //ノード名

	private Vector<XmlOutputFormat> childElementLine = new Vector<XmlOutputFormat>();

	private Attr[] attrs;

	private StringBuffer buffer;            //作業用

	/**
	 * construct
	 *
	 */
	public XmlOutputFormat(){
	}

	/**
	 * 解析
	 */
	public void input(Node node, int indent) {
		this.indent=indent;
		name= node.getNodeName();
		NodeList nodes = node.getChildNodes();
		for (int i=0; i<nodes.getLength(); i++){
			Node childNode = nodes.item(i);
			if (Node.ELEMENT_NODE == childNode.getNodeType()){
				XmlOutputFormat line = new XmlOutputFormat();
				line.input(childNode, indent+1);
				childElementLine.add(childElementLine.size(),line);
			}
		}
		attrs = sortAttributes(node.getAttributes());
	}

	/**
	 * output contents
	 */
	public void write(PrintWriter writer) {
		buffer = new StringBuffer();
		addIndentSpace(indent);
		buffer.append('<');
		buffer.append(name);
		for (int i=0; i<attrs.length; i++){
			buffer.append(' ');
			Attr attr = attrs[i];
			buffer.append(attr.getNodeName());
			buffer.append("=\"");
            normalizeAndPrint(attr.getNodeValue(), true);
            buffer.append('"');
            if (buffer.length() > MAX_WORDES && i < attrs.length-1 ){
            	writer.println(buffer.toString());
            	buffer = new StringBuffer();
        		addIndentSpace(indent + 1);
            }
		}
		if (childElementLine.size() == 0){
			buffer.append(" />");
			writer.println(buffer.toString());
			writer.flush();
		}else{
			buffer.append('>');
			writer.println(buffer.toString());
			writer.flush();
			for (int i=0; i<childElementLine.size(); i++){
				XmlOutputFormat element = (XmlOutputFormat)childElementLine.get(i);
				element.write(writer);
			}
			buffer = new StringBuffer();
			addIndentSpace(indent);
			buffer.append("</");
			buffer.append(name);
			buffer.append('>');
			writer.println(buffer.toString());
			writer.flush();
		}

	}

    /**
     *  Returns a sorted list of attributes.
     **/
    protected Attr[] sortAttributes(NamedNodeMap attrs) {

        int len = (attrs != null) ? attrs.getLength() : 0;
        Attr array[] = new Attr[len];
        for (int i = 0; i < len; i++) {
            array[i] = (Attr)attrs.item(i);
        }
        for (int i = 0; i < len - 1; i++) {
            String name = array[i].getNodeName();
            int index = i;
            for (int j = i + 1; j < len; j++) {
                String curName = array[j].getNodeName();
                if (curName.compareTo(name) < 0) {
                    name = curName;
                    index = j;
                }
            }
            if (index != i) {
                Attr temp = array[i];
                array[i] = array[index];
                array[index] = temp;
            }
        }

        return array;

    }

    /**
     *  Normalizes and prints the given string.
     **/
    protected void normalizeAndPrint(String s, boolean isAttValue) {
        int len = (s != null) ? s.length() : 0;
        for (int i = 0; i < len; i++) {
            char c = s.charAt(i);
            normalizeAndPrint(c, isAttValue);
        }
    }

    /**
     *  Normalizes and print the given character.
     **/
    protected void normalizeAndPrint(char c, boolean isAttValue) {

        switch (c) {
            case '<': {
            	buffer.append("&lt;");
                break;
            }
            case '>': {
            	buffer.append("&gt;");
                break;
            }
            case '&': {
            	buffer.append("&amp;");
                break;
            }
            case '"': {
                if (isAttValue) {
                	buffer.append("&quot;");
                }
                else {
                	buffer.append("\"");
                }
                break;
            }
            case '\r': {
            	// If CR is part of the document's content, it
            	// must not be printed as a literal otherwise
            	// it would be normalized to LF when the document
            	// is reparsed.
            	buffer.append("&#xD;");
            	break;
            }
            case '\n': {
                	buffer.append("&#xA;");
                    break;
            }
            default:{
            	buffer.append(c);
            }
        }
    }

    protected void addIndentSpace(int number){
		for (int i=0; i<number; i++){
			buffer.append(INDENT_SPACE);
		}
    }

}
