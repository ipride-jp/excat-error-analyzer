/*
 * Error Anaylzer Tool for Java
 *
 * Created on 2006/4/1
 *
 * Copyright (c) 2006-2009 iPride Co.,Ltd.
 * All rights reserved.
 */
package jp.co.ipride.excat.analyzer.viewer.localviewer;

import jp.co.ipride.excat.MainViewer;
import jp.co.ipride.excat.analyzer.viewer.AnalyzerForm;
import jp.co.ipride.excat.analyzer.viewer.sourceviewer.BytecodeViewer;
import jp.co.ipride.excat.common.ApplicationResource;
import jp.co.ipride.excat.common.utility.HelperFunc;

import org.apache.bcel.classfile.LocalVariable;
import org.apache.bcel.classfile.LocalVariableTable;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

/**
 * バイトコード・ビューアのサブ・ビューア
 * オブジェクトのローカル変数ビューア
 * @author 屠偉新
 * @since 2006/9/17
 */
public class VariableTable  extends SashForm implements
								ISelectionChangedListener,
								SelectionListener{

	private  Table viewTable = null;
	private  TableColumn col_id = null;
	private  TableColumn col_object_id = null;
	private  TableColumn col_name = null;
	private  TableColumn col_signature = null;
	private  TableColumn col_startLine = null;
	private  TableColumn col_endLine = null;

	private  LocalVariable[] localVars=null;

	private static final String[] COLUMN_NAMES = new String[] {
		ApplicationResource.getResource("COL_VARIABLE_INDEX"),
		ApplicationResource.getResource("COL_VARIABLE_OBJECT_INDEX"),
		ApplicationResource.getResource("COL_VARIABLE_NAME"),
		ApplicationResource.getResource("COL_VARIABLE_SIGNATURE"),
		ApplicationResource.getResource("COL_VARIABLE_START_PC"),
		ApplicationResource.getResource("COL_VARIABLE_END_PC")
		};

	//今選択されたライン
	private int currentLine = -1;

	private BytecodeViewer bytecodeViewer;


	/**
	 * 構築
	 * @param parent
	 * @param style
	 */
	public VariableTable(Composite parent, int style) {
		super(parent, style);
		viewTable = new Table(this, SWT.FULL_SELECTION | SWT.HIDE_SELECTION | SWT.V_SCROLL);
		viewTable.setHeaderVisible(true);
		viewTable.setLinesVisible(true);

		col_id = new TableColumn(viewTable, SWT.LEFT);
		col_id.setText(COLUMN_NAMES[0]);

		col_object_id = new TableColumn(viewTable, SWT.RIGHT);
		col_object_id.setText(COLUMN_NAMES[1]);

		col_name = new TableColumn(viewTable, SWT.LEFT);
		col_name.setText(COLUMN_NAMES[2]);

		col_signature = new TableColumn(viewTable, SWT.LEFT);
		col_signature.setText(COLUMN_NAMES[3]);

		col_startLine = new TableColumn(viewTable, SWT.RIGHT);
		col_startLine.setText(COLUMN_NAMES[4]);

		col_endLine = new TableColumn(viewTable, SWT.RIGHT);
		col_endLine.setText(COLUMN_NAMES[5]);

		//add by tu 2007.4.15
		col_id.setWidth(40);
		col_object_id.setWidth(70);
		col_name.setWidth(180);
		col_signature.setWidth(300);
		col_startLine.setWidth(100);
		col_endLine.setWidth(100);
	}

	/**
	 * fill date
	 * @param varTable
	 */
	public void setValue(LocalVariableTable varTable){

		viewTable.removeAll();

		if (varTable== null){
			return;
		}

		localVars = varTable.getLocalVariableTable();
		for (int i=0; i<localVars.length; i++){
			LocalVariable localVariable = localVars[i];
			TableItem item = new TableItem(viewTable, SWT.NULL);

			int index = localVariable.getIndex();
			item.setText(0,"%"+ Integer.toString(index));

			int objectindex = localVariable.getNameIndex();
			item.setText(1,Integer.toString(objectindex));

			String name = localVariable.getName();
			item.setText(2,name);

			String signature = localVariable.getSignature();
			item.setText(3,HelperFunc.convertClassSig(signature));

			int start = localVariable.getStartPC();
			item.setText(4,Integer.toString(start));

			int end = localVariable.getLength() + start;
			item.setText(5,Integer.toString(end));
		}
	}

	/**
	 *
	 *clear line.
	 */
	public void clearLine(){
		if (currentLine>=0){
			viewTable.getItem(currentLine).setBackground(Display.getCurrent().
					getSystemColor(SWT.COLOR_WHITE));
		}
		currentLine=-1;
	}

	public void removeAll(){
		if (viewTable != null){
			viewTable.removeAll();
		}
	}

	/**
	 * will called when people click a var on byte-code viewer.
	 * @param byteLine
	 * @param varIndex
	 */
	public void setSelectLine(int byteLine, int varIndex){
		for (int i=0; i<localVars.length; i++){
			LocalVariable localVariable = localVars[i];
			int index = localVariable.getIndex();
			int start = localVariable.getStartPC();
			int end = localVariable.getLength() + start;
			if (index == varIndex && byteLine>= start && byteLine <=end){
				currentLine = i;
				viewTable.getItem(currentLine).setBackground(Display.getCurrent().
						getSystemColor(SWT.COLOR_GREEN));
				viewTable.setTopIndex(i);
				break;
			}
		}
	}

	/**
	 *選択された場合の処理
	 */
	public void selectionChanged(SelectionChangedEvent e) {
		try{
		    int index = viewTable.getSelectionIndex();
		    bytecodeViewer.underlineVar(index);
		}catch(Exception ex){
			MainViewer.win.logException(ex);
		}
	}

	public void setBytecodeViewer(BytecodeViewer bytecodeViewer){
		this.bytecodeViewer = bytecodeViewer;
	}

	public void widgetDefaultSelected(SelectionEvent arg0) {
	}

	/**
	 * call this method when source viewer tab-item has changed.
	 */
	public void widgetSelected(SelectionEvent event) {
		//ソースビューアのアイテムは切り替えた。
		viewTable.removeAll();
		Object data = event.item.getData();
		if (data instanceof BytecodeViewer){
			BytecodeViewer viewer = (BytecodeViewer)data;
			viewer.setLocatVarTableControl(this);
			AnalyzerForm.showLocalVarTable();
		}
	}
}
