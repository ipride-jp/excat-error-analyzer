/*
 * Error Anaylzer Tool for Java
 *
 * Created on 2006/4/1
 *
 * Copyright (c) 2006-2009 iPride Co.,Ltd.
 * All rights reserved.
 */
package jp.co.ipride.excat.analyzer.viewer.sourceviewer;

import java.util.ArrayList;
import java.util.List;

import jp.co.ipride.excat.MainViewer;
import jp.co.ipride.excat.analyzer.viewer.AnalyzerForm;
import jp.co.ipride.excat.analyzer.viewer.localviewer.VariableTable;
import jp.co.ipride.excat.common.ApplicationResource;
import jp.co.ipride.excat.common.utility.HelperFunc;

import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.LocalVariable;
import org.apache.bcel.classfile.LocalVariableTable;
import org.apache.bcel.classfile.Method;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

/**
 * バイトコードのビューア
 * @author 屠偉新
 * @since 2006/9/17
 */
public class BytecodeViewer extends AbstractViewer{
	//表示のビューアTBL
	protected Table sourceViewTable;
	//local var data
	private LocalVariableTable varTable;
	//オフセット
	private  TableColumn col_offset = null;
	//操作コード
	private  TableColumn col_opcode = null;
	//パラメータ
	private  TableColumn col_parameters = null;
	//バイトコード・リスト
	private List<ByteCode> byteCodeList = new ArrayList<ByteCode>();
	//呼ばれているメソッド
	private int methodLine = -1;
	//選択されたライン
	private int selectLine = -1;

	/**
	 * construct
	 * @param appWindow
	 * @param item
	 */
	public BytecodeViewer(MainViewer appWindow, CTabItem item, MethodInfo methodInfo){
		this.appWindow = appWindow;
		this.item = item;
		this.item.setData(this);
		this.parent = (SashForm)item.getControl();
		this.methodInfo=methodInfo;
		this.variableTable=AnalyzerForm.variableTable;
		variableTable.removeAll();
		AnalyzerForm.showLocalVarTable();
		createSrcViewer();
	}

	/**
	 * バイトコード・ビューア作成
	 */
	protected void createSrcViewer(){
		//source viewer
		sourceViewTable = new Table(parent, SWT.VIRTUAL | SWT.FULL_SELECTION | SWT.HIDE_SELECTION);
		sourceViewTable.setHeaderVisible(true);
		sourceViewTable.setLinesVisible(true);
//		sourceViewTable.addSelectionListener(new SelectionListenerImp());

		col_offset = new TableColumn(sourceViewTable, SWT.RIGHT);
//		col_offset.setText("Offset");
		col_offset.setText(ApplicationResource.getResource("SrcViewer.Offset"));

		col_opcode = new TableColumn(sourceViewTable, SWT.LEFT);
//		col_opcode.setText("OpCode");
		col_opcode.setText(ApplicationResource.getResource("SrcViewer.OpCode"));

		col_parameters = new TableColumn(sourceViewTable, SWT.LEFT);
//		col_parameters.setText("Parameters");
		col_parameters.setText(ApplicationResource.getResource("SrcViewer.Parameters"));

		//add by tu 2007.4.15
		col_offset.setWidth(60);
		col_opcode.setWidth(150);
		col_parameters.setWidth(600);

	}

	/**
	 * fill date and display this view
	 * @param clazz
	 * @param location
	 * @param methodName
	 * @param methodSig
	 */
	public void diplay(JavaClass clazz, MethodInfo methodInfo){
		this.methodInfo=methodInfo;
		type = BYTE_CODE;
		Method[] methods = clazz.getMethods();
		for (int index = 0; index < methods.length; index++) {
			Method method = methods[index];
			String mSig = HelperFunc.convertMethodSig(method.getSignature());
			String mN= method.getName();
			if("<init>".equals(mN)){
				mN = HelperFunc.getPureClassName(
						clazz.getClassName());
			}
			boolean b =  method.isNative();
			String objectMSig = methodInfo.getMethodSig();
			String objectMName = methodInfo.getMethodName();
			if (mSig.equals(objectMSig)
					&& mN.equals(objectMName)
					&& !b) {
				String codeContents = method.getCode().toString(false);
				setContents(codeContents);
				highlight(methodInfo.getLocation());
				varTable = method.getLocalVariableTable();
				variableTable.setValue(varTable);
				break;
			}
		}

	}

	/**
	 * 該当ビューアのタイプを取得
	 */
	public int getType(){
		return type;
	}


	/**
	 * 実行される行を表示するようにViewerをScrollする
	 *
	 */
	public  void showCalledPlace(MethodInfo methodInfo){

		this.methodInfo = methodInfo;
		for (int i=0; i<byteCodeList.size(); i++){
			ByteCode code = byteCodeList.get(i);
			if (code.getOffset()==methodInfo.getLocation()){
				sourceViewTable.setTopIndex(i);
				break;
			}
		}
	}

	/**
	 * underline the selected method.
	 */
	public void highlight(int location){
		for (int i=0; i<byteCodeList.size(); i++){
			ByteCode code = byteCodeList.get(i);
			if (code.getOffset()==location){
				sourceViewTable.getItem(i).setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_YELLOW));
				sourceViewTable.setTopIndex(i);
				methodLine=i;
				break;
			}
		}

	}

	/**
	 * will be called by var-table when people click the local var table.
	 * @param varIndex
	 */
	public void underlineVar(int varIndex){
	}

	/**
	 * set bytecode into this table.
	 * @param contents
	 */
	protected void setContents(String contents){
		String[] lines = contents.split("\n");
		for (int i=0; i<lines.length; i++){
			if (checkContents(lines[i])){
				ByteCode code = new ByteCode(lines[i]);
				byteCodeList.add(code);
				TableItem item = new TableItem(sourceViewTable, SWT.NULL);
				item.setText(0,Integer.toString(code.getOffset()));
				item.setText(1,code.getOpCode());
				item.setText(2,code.getParams());
			}
		}
	}

	/**
	 * check this line whether is running line.
	 * @param line
	 * @return
	 */
	protected boolean checkContents(String line){
		int pos = line.indexOf(':');
		if (pos == -1) {
			return false;
		}
		String lineNum = line.substring(0, pos);
		try {
			Integer.parseInt(lineNum);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	public void clearSelectLine(){
		if (selectLine != methodLine && selectLine != -1 ){
			sourceViewTable.getItem(selectLine).setBackground(
					Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));

		}

		selectLine=-1;
	}

	protected void underlineSelect(int line){
		selectLine=line;
	}

	/**
	 * call by variableTable when this tab item has selected.
	 * @date 2009/9/22
	 * @param variableTable
	 */
	public void setLocatVarTableControl(VariableTable variableTable){
		this.variableTable=variableTable;
		this.variableTable.setValue(varTable);
	}

	/**
	 * call by stack viewer.
	 * we have to show a line on byte-code viewer.
	 * @param varName
	 */
	public void selectLocalVarNameFromStackViewer(String varName){
		clearSelectLine();
		String runningOffsetStr = sourceViewTable.getItem(methodLine).getText(0);
		int runningOffset = Integer.parseInt(runningOffsetStr);

		for (int i=0; i<varTable.getLength(); i++){
			LocalVariable localVariable = varTable.getLocalVariable(i);
			if (localVariable == null){
				continue;
			}
			String name = localVariable.getName();
			int start = localVariable.getStartPC();
			int end = start + localVariable.getLength();
			String  nameIndex = "%" + localVariable.getIndex();

			if (name.equals(varName) && runningOffset > start && runningOffset < end){

				for (int j = methodLine; j>=0; j--){
					ByteCode byteCode = byteCodeList.get(j);
					String param = byteCode.getParams();
					if (nameIndex.equals(param)){
						selectLine = j;
						sourceViewTable.setTopIndex(selectLine);
						sourceViewTable.getItem(selectLine).setBackground(
								Display.getCurrent().getSystemColor(SWT.COLOR_CYAN));
						break;
					}
				}
				break;
			}
		}
	}

	/**
	 * this is a inner-class for storing information of one line of bytecode.
	 * @author 屠偉新
	 *
	 */
	class ByteCode {
		static final String ALOAD="aload_";
		private int offset=-1;
		private String opCode="";
		private String params="";

		public ByteCode(String line){
			setOffset(line);
		}

		public void setOffset(String line) {
			String trimLine = line.trim();
			int n = trimLine.indexOf(" ");
			String offset = trimLine.substring(0, n-1);
			this.offset = Integer.parseInt(offset);
			String others = trimLine.substring(n, trimLine.length());
			setOpCode(others.trim());
		}

		public void setOpCode(String line) {
			String parameter=null;
			String trimLine = line.trim();
			int n = trimLine.indexOf("\t");
			if (n<0){
				this.opCode = trimLine;
				parameter = "";
			}else{
				this.opCode = trimLine.substring(0, n);
				parameter = trimLine.substring(n, trimLine.length());
			}
			setParams(parameter.trim());
		}

		public void setParams(String params) {
			this.params = params.trim();
			if (this.params.equals("")){
				if (opCode.length()>6 && opCode.indexOf(ALOAD)>=0){
					this.params = "%" + opCode.substring(6);
				}
			}
		}

		public int getOffset() {
			return offset;
		}
		public String getOpCode() {
			return opCode;
		}
		public String getParams() {
			return params;
		}

	}

	/**
	 * a listner for a selecting of byte-code line item.
	 * @author 屠偉新
	 *
	 */
	class SelectionListenerImp implements SelectionListener{

		public void widgetSelected(SelectionEvent e) {
			Table table = (Table)e.getSource();
			int index = table.getSelectionIndex();
			ByteCode byteCode = (ByteCode)byteCodeList.get(index);

			//clear old
			variableTable.clearLine();

			//underline new
			underlineSelect(index);

			if ("".equals(byteCode.params)){
				return;
			}

			String mark = byteCode.params.substring(0,1);
			if ("%".equals(mark)){
				String strIndex = byteCode.params.substring(1);
				try{
					int varIndex = Integer.parseInt(strIndex);
					variableTable.setSelectLine(byteCode.offset, varIndex);
				}catch(Exception e1){}
			}
		}

		public void widgetDefaultSelected(SelectionEvent arg0) {
		}
	}
}
