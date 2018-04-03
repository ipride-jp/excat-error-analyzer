package jp.co.ipride.excat.configeditor.model;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * 
 * @author tu
 * @since 2007/12/8
 */
public class XmlWriter {

    /** Print writer. */
    protected PrintWriter fOut;

    /**
     *  Default constructor. 
     *
     **/
    public XmlWriter() {
    }

    /**
     *  Sets the output stream for printing. 
     **/
    public void setOutput(OutputStream stream, String encoding) throws UnsupportedEncodingException {

        if (encoding == null) {
            encoding = "UTF8";
        }
        Writer writer = new OutputStreamWriter(stream, encoding);
        fOut = new PrintWriter(writer);
    }

    /**
     *  Writes the specified node, recursively. 
     **/
    public void write(Node node) {

        if (node == null) {
            return;
        }
        fOut.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        fOut.flush();
        XmlOutputFormat line = new XmlOutputFormat();
        Document document = (Document)node;
        line.input(document.getDocumentElement(),0);
        line.write(fOut);
    }    
}
