/*
 * Error Analyzer Tool for Java
 *
 * Created on 2006/4/1
 *
 * Copyright (c) 2006-2009 iPride Co.,Ltd.
 * All rights reserved.
 */
package jp.co.ipride.excat.analyzer.viewer.propertyviewer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jp.co.ipride.excat.MainViewer;
import jp.co.ipride.excat.common.ApplicationResource;
import jp.co.ipride.excat.analyzer.common.DumpFileXmlConstant;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * @author GuanXH
 */
public class PropertyTable implements ISelectionChangedListener {
	private static final String XML_COMPLETION_FALSE = "False";
	private static final String XML_COMPLETION_TRUE = "True";
	private static final String XML_COMPLETION = ApplicationResource.getResource("Property.Xml.Completion");
	private static final String XML_CONTENT = ApplicationResource.getResource("Property.Xml.Content");

	private static final String[] COLUMN_NAMES = new String[] {
			ApplicationResource.getResource("Property.NameTitle"),
			ApplicationResource.getResource("Property.ContentTitle") };

	private TableViewer tableViewer;
	private DomBuilder builder;

	private ArrayList<PropertyItem> propertyItems = new ArrayList<PropertyItem>();

	// the selected node in xmltree
	private Node nodeSelected;

	private ArrayList<IContentChangedListener> contentChangedListeners = new ArrayList<IContentChangedListener>();

	public PropertyTable(Composite parent, int style) {
		this.nodeSelected = null;

		this.tableViewer = new TableViewer(parent, style);
		Table table = tableViewer.getTable();
		table.setLinesVisible(true);
		table.setHeaderVisible(true);

		TableColumn column = new TableColumn(table, SWT.NONE);
		column.setText(COLUMN_NAMES[0]);

		column = new TableColumn(table, SWT.NONE);
		column.setText(COLUMN_NAMES[1]);

		tableViewer.setContentProvider(new ArrayContentProvider());
		tableViewer.setLabelProvider(new PropertyLabelProvider());

		tableViewer.setColumnProperties(COLUMN_NAMES);

		// create cell editors
		CellEditor[] editors = new CellEditor[COLUMN_NAMES.length];
		editors[0] = new TextCellEditor(table);
		editors[1] = new TextCellEditor(table);
		tableViewer.setCellEditors(editors);

		tableViewer.setCellModifier(new PropertyCellModifier(this));

		packColumns();

		// added by sai 2009/10/21
		// Disable native tool tip
	    table.setToolTipText("");
	    // add a customized tool tip
		new ItemToolTipper(tableViewer);
	}

	/**
	 * 内容が変わっている場合
	 *
	 * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
	 */
	public void selectionChanged(SelectionChangedEvent event) {
		try {
			StructuredSelection selection = (StructuredSelection) event
					.getSelection();
			nodeSelected = (Node) selection.getFirstElement();

			if (nodeSelected == null) {
				tableViewer.setInput(null);
				packColumns();
				return;
			}

			// clear property items
			propertyItems.clear();

			NamedNodeMap attrMap = nodeSelected.getAttributes();
			propertyItems.addAll(PropertyItemFactory.createPropertyItems(
					nodeSelected.getNodeName(), attrMap, nodeSelected));

			// added by sai 2009/10/21
			// 選択要素のタイプ属性を取得する。
			String type = "";
			Node node = attrMap
					.getNamedItem(DumpFileXmlConstant.ATTR_REAL_TYPE);
			if (node != null) {
				type = node.getNodeValue();
			}
			if (type.equals("") || type == null) {
				node = attrMap.getNamedItem(DumpFileXmlConstant.ATTR_DEF_TYPE);
				if (node != null) {
					type = node.getNodeValue();
				}
			}

			if (DomBuildHelper.checkEquals(type, DomBuildHelper.DOCUMENT_NODE_TYPE)
					|| DomBuildHelper.checkEquals(type, DomBuildHelper.ELEMENT_NODE_TYPE)) {
				if (builder == null) {
					builder = DomBuilderFactory.getInstance().newDomBuilder();
				}

				this.tableViewer.getTable().getShell().setCursor(new Cursor(null, SWT.CURSOR_WAIT));
				String strXml = null;
				if (DomBuildHelper.checkEquals(type, DomBuildHelper.DOCUMENT_NODE_TYPE)) {
					// タイプは「Document」の場合、Document対象に格納されたXML内容を表示する。
					strXml = builder.getDocumentXml(nodeSelected);
				} else if (DomBuildHelper.checkEquals(type, DomBuildHelper.ELEMENT_NODE_TYPE)){
					// タイプは「Element」の場合、Element対象に格納されたXML内容を表示する。
					strXml = builder.getElementXml(nodeSelected);
				}
				if (strXml != null) {
					addPropertyItem(XML_CONTENT, strXml);
					if (builder.isCompletely()) {
						// XMLの内容が完全に復元された場合、
						// プロパティビューに完全性フラグをTrueに設定して表示する。
						addPropertyItem(XML_COMPLETION, XML_COMPLETION_TRUE);
					} else {
						addPropertyItem(XML_COMPLETION, XML_COMPLETION_FALSE);
					}
				}
			}

			tableViewer.setInput(propertyItems);
			packColumns();
		} catch (Exception e) {
			MainViewer.win.logException(e);
		} finally {
			this.tableViewer.getTable().getShell().setCursor(null);
		}
	}

	// added by sai 2009/10/21
	/**
	 * ビルダーを初期化するメソッド
	 */
	public void initDomBuilder() {
		this.builder = DomBuilderFactory.getInstance().newDomBuilder();
	}

	// added by sai 2009/10/21
	/**
	 * アイテムを追加するメソッド
	 *
	 * @param strName 属性名
	 * @param strValue 属性値
	 */
	public void addPropertyItem(String strName, String strValue) {
		propertyItems.add(new PropertyItem(strName, strValue));
	}

	private void packColumns() {
		Table table = tableViewer.getTable();

		// 各カラムの幅を計算する
		int count = table.getColumnCount();
		TableColumn[] columns = table.getColumns();
		for (int index = 0; index < count; index++) {
			columns[index].pack();
		}
	}

	public List<String> getColumnNames() {
		return Arrays.asList(COLUMN_NAMES);
	}

	public void update(PropertyItem propertyItem) {
		tableViewer.update(propertyItem, null);
		NamedNodeMap attrMap = nodeSelected.getAttributes();
		Node node = attrMap.getNamedItem(propertyItem.getName());
		if (node != null) {
			node.setNodeValue(propertyItem.getValue());
		}

		notifyContentChangedListener();
	}

	public void addContentChangedListener(
			IContentChangedListener contentChangedListener) {

		contentChangedListeners.add(contentChangedListener);
	}

	private void notifyContentChangedListener() {
		int size = contentChangedListeners.size();
		for (int index = 0; index < size; index++) {
			IContentChangedListener listener = (IContentChangedListener) contentChangedListeners
					.get(index);
			listener.contentChanged();
		}
	}
}