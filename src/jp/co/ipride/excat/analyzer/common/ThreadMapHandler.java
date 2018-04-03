package jp.co.ipride.excat.analyzer.common;

import jp.co.ipride.excat.analyzer.viewer.threadmap.ThreadMapItemTypeA;
import jp.co.ipride.excat.common.utility.HelperFunc;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * for analyzing thread map.
 * @author tu-ipride
 * @date 2009/10/4
 */
public class ThreadMapHandler extends DefaultHandler{

	private ThreadMapItemTypeA threadMapItem = null;

	/**
	 * get ThreadMapItem by user.
	 * @return
	 */
	public ThreadMapItemTypeA getThreadMapItem(){
		return threadMapItem;
	}

	/**
	 *
	 */
	public ThreadMapHandler(){
		this.threadMapItem = new ThreadMapItemTypeA();
	}

    // ContentHandler‚ÌŽÀ‘•
    public void startDocument() throws SAXException {
    }

    //
    public void endDocument() throws SAXException {
    }

    //
    public void startElement(java.lang.String uri,
                       java.lang.String localName,
                       java.lang.String qName,
                       Attributes atts)
                		throws SAXException {
    	if (DumpFileXmlConstant.NODE_STACKTRACE.equals(qName)){
    		threadMapItem.setCPUTime(
    				atts.getValue(uri, DumpFileXmlConstant.ATTR_CPUTIME));
    		threadMapItem.setStatus(
    				atts.getValue(uri, DumpFileXmlConstant.ATTR_STATUS));
    		threadMapItem.setPriority(
    				atts.getValue(uri, DumpFileXmlConstant.ATTR_PRIORITY));
    		threadMapItem.setThreadName(
    				atts.getValue(uri, DumpFileXmlConstant.ATTR_THREADNAME));
    		threadMapItem.setDumpTime(
    				atts.getValue(uri, DumpFileXmlConstant.ATTR_DUMPTIME));
    		threadMapItem.setWaitReason(
    				atts.getValue(uri, DumpFileXmlConstant.ATTR_WAIT_REASON));
//    		threadMapItem.setMonitorObject(
//    				atts.getValue(uri, DumpFileXmlConstant.ATTR_MONITOR_NAME));
//    		threadMapItem.setMonitorObject(
//    				atts.getValue(uri, DumpFileXmlConstant.ATTR_WAIT_THREAD));
    	}
    	if (DumpFileXmlConstant.NODE_CONTEND_MONITOR_OBJECT.equals(qName)) {
    		threadMapItem.setMonitorObject(
    				atts.getValue(uri, DumpFileXmlConstant.ATTR_DEF_TYPE));
    		threadMapItem.setWaitThread(
    				atts.getValue(uri, DumpFileXmlConstant.ATTR_USETHREADNAME));
    	}
    }

    /**
     *
     */
    public void endElement(java.lang.String uri,
                       java.lang.String localName,
                       java.lang.String qName)
                throws SAXException {
    }

    /**
     *
     */
    public void characters(char[] ch,
                       int start,
                       int length)
                throws SAXException {
    }
    /**
     *  ErrorHandler‚ÌŽÀ‘•
     */
    public void warning(SAXParseException e) {
    	HelperFunc.logException(e);
    }
    /**
     *
     */
    public void error(SAXParseException e) {
    	HelperFunc.logException(e);
    }
    /**
     *
     */
    public void fatalError(SAXParseException e) {
    	HelperFunc.logException(e);
    }
}
