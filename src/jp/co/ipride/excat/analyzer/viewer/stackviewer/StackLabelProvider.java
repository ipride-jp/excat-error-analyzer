/*
 * Error Anaylzer Tool for Java
 *
 * Created on 2006/4/1
 *
 * Copyright (c) 2006-2009 iPride Co.,Ltd.
 * All rights reserved.
 */
package jp.co.ipride.excat.analyzer.viewer.stackviewer;

import java.net.URL;

import jp.co.ipride.excat.MainViewer;
import jp.co.ipride.excat.analyzer.common.DumpFileXmlConstant;
import jp.co.ipride.excat.common.icons.IconFilePathConstant;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.w3c.dom.Node;

/**
 * this is a label provider of dump data tree viewer.
 * @version 	1.0
 * @author 	管 暁華
 */
public class StackLabelProvider implements ILabelProvider {
	private ImageRegistry images;

	private static Image stacktraceImage = null;
	private static Image methodImage = null;
	private static Image variableImage = null;
	private static Image thisImage = null;
	private static Image superclassImage = null;
	private static Image attributeImage = null;
	private static Image argumentImage = null;
	private static Image itemImage= null;
	private static Image monitorObjectImage = null;
	private static Image instanceImage = null;
	private static Image returnImage = null;
	private static Image exceptionImage = null;
	private static Image MonitorImage = null;
	/**
	 * construct
	 * @param appWindow
	 */
	public StackLabelProvider(MainViewer appWindow) {

		//load images
		images = new ImageRegistry();
		URL url;
		ImageDescriptor nid;

		url = MainViewer.class.getResource(IconFilePathConstant.TREE_DUMP);
		nid = ImageDescriptor.createFromURL(url);
		images.put(DumpFileXmlConstant.NODE_DUMP, nid);

		url = MainViewer.class.getResource(IconFilePathConstant.TREE_STACKTRACE);
		nid = ImageDescriptor.createFromURL(url);
		images.put(DumpFileXmlConstant.NODE_STACKTRACE, nid);

		url = MainViewer.class.getResource(IconFilePathConstant.TREE_METHOD);
		nid = ImageDescriptor.createFromURL(url);
		images.put(DumpFileXmlConstant.NODE_METHOD, nid);

		url = MainViewer.class.getResource(IconFilePathConstant.TREE_VARIABLE);
		nid = ImageDescriptor.createFromURL(url);
		images.put(DumpFileXmlConstant.NODE_VARIABLE, nid);

		url = MainViewer.class.getResource(IconFilePathConstant.TREE_THIS);
		nid = ImageDescriptor.createFromURL(url);
		images.put(DumpFileXmlConstant.NODE_THIS, nid);

		url = MainViewer.class.getResource(IconFilePathConstant.TREE_SUPERCLASS);
		nid = ImageDescriptor.createFromURL(url);
		images.put(DumpFileXmlConstant.NODE_SUPERCLASS, nid);

		url = MainViewer.class.getResource(IconFilePathConstant.TREE_ATTRIBUTE);
		nid = ImageDescriptor.createFromURL(url);
		images.put(DumpFileXmlConstant.NODE_ATTRIBUTE, nid);

		url = MainViewer.class.getResource(IconFilePathConstant.TREE_ARGUMENT);
		nid = ImageDescriptor.createFromURL(url);
		images.put(DumpFileXmlConstant.NODE_ARGUMENT, nid);

		url = MainViewer.class.getResource(IconFilePathConstant.TREE_ITEM);
		nid = ImageDescriptor.createFromURL(url);
		images.put(DumpFileXmlConstant.NODE_ITEM, nid);

		url = MainViewer.class.getResource(IconFilePathConstant.MONITOROBJECT_ITEM);
		nid = ImageDescriptor.createFromURL(url);
		images.put(DumpFileXmlConstant.NODE_MONITOROBJECT, nid);

		url = MainViewer.class.getResource(IconFilePathConstant.INSTANCE_ITEM);
		nid = ImageDescriptor.createFromURL(url);
		images.put(DumpFileXmlConstant.NODE_INSTANCE, nid);

		url = MainViewer.class.getResource(IconFilePathConstant.TREE_RETURN);
		nid = ImageDescriptor.createFromURL(url);
		images.put(DumpFileXmlConstant.NODE_RETURN, nid);

		url = MainViewer.class.getResource(IconFilePathConstant.TREE_EXCEPTION);
		nid = ImageDescriptor.createFromURL(url);
		images.put(DumpFileXmlConstant.NODE_EXCEPTION_OBJECT, nid);

		url = MainViewer.class.getResource(IconFilePathConstant.TREE_CONTEND_MONITOR_OBJECT);
		nid = ImageDescriptor.createFromURL(url);
		images.put(DumpFileXmlConstant.NODE_CONTEND_MONITOR_OBJECT, nid);
	}
	/**
	 * implement interface to supply image of element.
	 *
	 * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
	 */
	public Image getImage(Object element) {
		Node node = (Node)element;
		short nodeType = node.getNodeType();
		if (nodeType == Node.ELEMENT_NODE) {
			if (DumpFileXmlConstant.NODE_DUMP.equalsIgnoreCase(node.getNodeName())){
				ImageDescriptor id = images.getDescriptor(DumpFileXmlConstant.NODE_DUMP);
				return id.createImage();
			}

			if (DumpFileXmlConstant.NODE_STACKTRACE.equalsIgnoreCase(node.getNodeName())){
				if (stacktraceImage==null){
					ImageDescriptor id = images.getDescriptor(DumpFileXmlConstant.NODE_STACKTRACE);
					stacktraceImage=id.createImage();
				}
				return stacktraceImage;
			}

			if (DumpFileXmlConstant.NODE_OBJECT.equalsIgnoreCase(node.getNodeName())){
				if (attributeImage==null){
					ImageDescriptor id = images.getDescriptor(DumpFileXmlConstant.NODE_ATTRIBUTE);
					attributeImage=id.createImage();
				}
				return attributeImage;
			}

			if (DumpFileXmlConstant.NODE_METHOD.equalsIgnoreCase(node.getNodeName())){
				if (methodImage==null){
					ImageDescriptor id = images.getDescriptor(DumpFileXmlConstant.NODE_METHOD);
					methodImage=id.createImage();
				}
				return methodImage;
			}

			if (DumpFileXmlConstant.NODE_VARIABLE.equalsIgnoreCase(node.getNodeName())){
				if (variableImage==null){
					ImageDescriptor id = images.getDescriptor(DumpFileXmlConstant.NODE_VARIABLE);
					variableImage= id.createImage();
				}
				return variableImage;
			}

			if (DumpFileXmlConstant.NODE_THIS.equalsIgnoreCase(node.getNodeName())){
				if (thisImage==null){
					ImageDescriptor id = images.getDescriptor(DumpFileXmlConstant.NODE_THIS);
					thisImage = id.createImage();
				}
				return thisImage;
			}

			if (DumpFileXmlConstant.NODE_MONITOROBJECT.equalsIgnoreCase(node.getNodeName())){
				if (monitorObjectImage==null){
					ImageDescriptor id = images.getDescriptor(DumpFileXmlConstant.NODE_MONITOROBJECT);
					monitorObjectImage = id.createImage();
				}
				return monitorObjectImage;
			}

			if (DumpFileXmlConstant.NODE_INSTANCE.equalsIgnoreCase(node.getNodeName())){
				if (instanceImage==null){
					ImageDescriptor id = images.getDescriptor(DumpFileXmlConstant.NODE_INSTANCE);
					instanceImage = id.createImage();
				}
				return instanceImage;
			}

			if (DumpFileXmlConstant.NODE_SUPERCLASS.equalsIgnoreCase(node.getNodeName())){
				if (superclassImage==null){
					ImageDescriptor id = images.getDescriptor(DumpFileXmlConstant.NODE_SUPERCLASS);
					superclassImage = id.createImage();
				}
				return superclassImage;
			}

			if (DumpFileXmlConstant.NODE_ATTRIBUTE.equalsIgnoreCase(node.getNodeName())){
				if (attributeImage==null){
					ImageDescriptor id = images.getDescriptor(DumpFileXmlConstant.NODE_ATTRIBUTE);
					attributeImage = id.createImage();
				}
				return attributeImage;
			}

			if (DumpFileXmlConstant.NODE_ARGUMENT.equalsIgnoreCase(node.getNodeName())){
				if (argumentImage==null){
					ImageDescriptor id = images.getDescriptor(DumpFileXmlConstant.NODE_ARGUMENT);
					argumentImage=id.createImage();
				}
				return argumentImage;
			}

			if (DumpFileXmlConstant.NODE_ITEM.equalsIgnoreCase(node.getNodeName())){
				if (itemImage==null){
					ImageDescriptor id = images.getDescriptor(DumpFileXmlConstant.NODE_ITEM);
					itemImage = id.createImage();
				}
				return itemImage;
			}
			//v3
			if (DumpFileXmlConstant.NODE_RETURN.equalsIgnoreCase(node.getNodeName())){
				if (returnImage==null){
					ImageDescriptor id = images.getDescriptor(DumpFileXmlConstant.NODE_RETURN);
					returnImage = id.createImage();
				}
				return returnImage;
			}

			//障害 #441
			if (DumpFileXmlConstant.NODE_EXCEPTION_OBJECT.equalsIgnoreCase(node.getNodeName())){
				if (exceptionImage == null){
					ImageDescriptor id = images.getDescriptor(DumpFileXmlConstant.NODE_EXCEPTION_OBJECT);
					exceptionImage = id.createImage();
				}
				return exceptionImage;
			}

			//v3
			if (DumpFileXmlConstant.NODE_CONTEND_MONITOR_OBJECT.equalsIgnoreCase(node.getNodeName())){
				if (MonitorImage==null){
					ImageDescriptor id = images.getDescriptor(DumpFileXmlConstant.NODE_CONTEND_MONITOR_OBJECT);
					//modified by Qiu Song on 2009.10.27 for 障害#455：モニターアイコン見えない
					//returnImage = id.createImage();
					MonitorImage = id.createImage();
				}
				return MonitorImage;
			}

			return null;
		}

		return null;
	}

	/**
	 * implement interface to supply text of element.
	 *
	 * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
	 */
	public String getText(Object element) {
	    Node node = (Node)element;

		return CommonNodeLabel.getText(node);
	}

	/**
	 *
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
	 */
	public void addListener(ILabelProviderListener listener) {
	}

	/**
	 *
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
	 */
	public void dispose() {
		images.dispose();
	}

	/**
	 *
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object, java.lang.String)
	 */
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	/**
	 *
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
	 */
	public void removeListener(ILabelProviderListener listener) {
	}

}
