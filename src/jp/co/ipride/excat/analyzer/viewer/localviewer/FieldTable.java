/*
 * Error Anaylzer Tool for Java
 * 
 * Created on 2006/4/1
 * 
 * Copyright (c) 2006-2009 iPride Co.,Ltd.
 * All rights reserved.
 */
package jp.co.ipride.excat.analyzer.viewer.localviewer;


import jp.co.ipride.excat.common.ApplicationResource;
import jp.co.ipride.excat.common.utility.HelperFunc;

import org.apache.bcel.classfile.Field;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;

/**
 * バイトコード・ビューアのサブ・ビューア
 * オブジェクトのフィールド・ビューア
 * @author 屠偉新
 * @since 2006/9/17
 */
public class FieldTable extends SashForm implements ISelectionChangedListener{
	
	private  Table table = null;
	private  TableColumn col_id = null;
	private  TableColumn col_name = null;
	private  TableColumn col_signature = null;
	private  TableColumn col_signatureIndex = null;

	private static final String[] COLUMN_NAMES = new String[] {
		ApplicationResource.getResource("COL_ARRIBUTE_ID"),
		ApplicationResource.getResource("COL_ARRIBUTE_NAME"), 
		ApplicationResource.getResource("COL_ARRIBUTE_SIGNATURE"), 
		ApplicationResource.getResource("COL_ARRIBUTE_SIGNATURE_INDEX"), 
		};
	
	/**
	 * 構築
	 * @param parent
	 * @param style
	 */
	public FieldTable(Composite parent, int style) {
		super(parent, style);

		table = new Table(this, SWT.FULL_SELECTION | SWT.HIDE_SELECTION | SWT.V_SCROLL);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		col_id = new TableColumn(table, SWT.LEFT);
		col_id.setText(COLUMN_NAMES[0]);

		col_name = new TableColumn(table, SWT.LEFT);
		col_name.setText(COLUMN_NAMES[1]);

		col_signature = new TableColumn(table, SWT.LEFT);
		col_signature.setText(COLUMN_NAMES[2]);

		col_signatureIndex = new TableColumn(table, SWT.RIGHT);
		col_signatureIndex.setText(COLUMN_NAMES[3]);

		this.addControlListener(new ControlAdapter() {
			public void controlResized(ControlEvent e) {
				col_id.setWidth(40);
				col_name.setWidth(200);
				col_signature.setWidth(400);
				col_signatureIndex.setWidth(100);
			}
		});
	}
	
	/**
	 * データをセット
	 * @param fields
	 */
	public void setValue(Field[] fields){
		if (fields==null){
			return;
		}
		for (int i=0; i<fields.length; i++){
			Field field = fields[i];
			TableItem item = new TableItem(table, SWT.NULL);
			
			int index = field.getNameIndex();
			item.setText(0,Integer.toString(index));
			
			String name = field.getName();
			item.setText(1,name);

			String signature = field.getSignature();
			String realSig = HelperFunc.convertClassSig(signature);
			item.setText(2,realSig);
			
			int start = field.getSignatureIndex();
			item.setText(3,Integer.toString(start));
		}
	}

	/**
	 *選択された場合の処理 
	 */
	public void selectionChanged(SelectionChangedEvent arg0) {
	}

}
