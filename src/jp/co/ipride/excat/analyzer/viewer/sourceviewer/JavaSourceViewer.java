/*
 * Error Anaylzer Tool for Java
 *
 * Created on 2006/4/1
 *
 * Copyright (c) 2006-2009 iPride Co.,Ltd.
 * All rights reserved.
 */
package jp.co.ipride.excat.analyzer.viewer.sourceviewer;

import jp.co.ipride.excat.MainViewer;
import jp.co.ipride.excat.analyzer.action.AddMonitorMethodTask;
import jp.co.ipride.excat.analyzer.action.GoToDeclareAction;
import jp.co.ipride.excat.analyzer.action.finder.FindCondition;
import jp.co.ipride.excat.analyzer.action.finder.IFindProvider;
import jp.co.ipride.excat.analyzer.action.finder.IFindStringProvider;
import jp.co.ipride.excat.analyzer.common.DumpFileXmlConstant;
import jp.co.ipride.excat.analyzer.viewer.sourceviewer.declare.IGoToDeclare;
import jp.co.ipride.excat.analyzer.viewer.sourceviewer.declare.MethodDeclarePlace;
import jp.co.ipride.excat.analyzer.viewer.sourceviewer.declare.NoClassInPathException;
import jp.co.ipride.excat.analyzer.viewer.sourceviewer.declare.SourceNotFoundException;
import jp.co.ipride.excat.analyzer.viewer.sourceviewer.declare.ThisFieldDeclarePlace;
import jp.co.ipride.excat.analyzer.viewer.sourceviewer.declare.VarFieldDeclarePlace;
import jp.co.ipride.excat.analyzer.viewer.sourceviewer.move.MoveMgr;
import jp.co.ipride.excat.analyzer.viewer.sourceviewer.move.MoveRecord;
import jp.co.ipride.excat.common.Message;
import jp.co.ipride.excat.common.action.CopyAction;
import jp.co.ipride.excat.common.clipboard.ExcatClipboard;
import jp.co.ipride.excat.common.utility.ExcatMessageUtilty;
import jp.co.ipride.excat.common.utility.HelperFunc;
import jp.co.ipride.excat.common.utility.Utility;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.rules.FastPartitioner;
import org.eclipse.jface.text.source.CompositeRuler;
import org.eclipse.jface.text.source.LineNumberRulerColumn;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TreeItem;
import org.w3c.dom.Node;
import org.w3c.dom.Element;


/**
 * javaソースビューア
 * @author 屠偉新
 * @since 2006/9/17
 */
public class JavaSourceViewer extends AbstractViewer implements IFindProvider, IFindStringProvider {

	protected SourceViewer sourceViewer;
	private CompositeRuler ruler = null;

	private int methodLine = -1;
	private boolean optionState = false;

	/**
	 * visitor for ast tree
	 */
	private JavaSourceVisitor sourceVisitor = null;

	private GoToDeclareAction gotoDeclareAction = null;

	private CopyAction copyAction = null;

	private AddMonitorMethodTask addMonitorMethodTask = null;

	private boolean needMoveFlag = true;

	/**
	 * AST Node Root of this source file
	 */
	private CompilationUnit compileUnit = null;

	private Thread resolveThread = null;

	public void setOptionState(boolean optionState){
		this.optionState = optionState;
	}
	public boolean getOptionState(){
		return optionState;
	}

	public synchronized JavaSourceVisitor getSourceVisitor() {
		return sourceVisitor;
	};

	public synchronized void setSourceVisitor(JavaSourceVisitor sv){
		sourceVisitor = sv;
	}

	/**
	 * コンストラクタ
	 * @param appWindow
	 * @param item
	 * @param style
	 */
	public JavaSourceViewer(MainViewer appWindow, CTabItem item, MethodInfo methodInfo,int style){
		this.appWindow = appWindow;
		this.item = item;
		this.item.setData(this);
		this.parent = (SashForm)item.getControl();
		this.methodInfo=methodInfo;
		this.gotoDeclareAction = appWindow.gotoDeclareAction;
		this.copyAction = appWindow.copyAction;
		this.addMonitorMethodTask = appWindow.addMonitorMethodTask;
		createSrcViewer();
	}

	/**
	 * reset ruler
	 *
	 */
	public void updateRuler(){
	    ruler.update();
	}

	/**
	 * ソースビューア作成
	 **/
	protected void createSrcViewer(){
		//source viewer
		ruler = new CompositeRuler();
		LineNumberRulerColumn lineCol =  new LineNumberRulerColumn();
        lineCol.setForeground(appWindow.getShell().getDisplay().getSystemColor(SWT.COLOR_BLUE));
		ruler.addDecorator(0,lineCol);
		sourceViewer = new SourceViewer(parent, ruler,
				SWT.VIRTUAL | SWT.READ_ONLY|SWT.BORDER|SWT.V_SCROLL|SWT.H_SCROLL);

		CColorManager colorManager = new CColorManager();
		JavaSourceViewerConfiguration srcCfg = new JavaSourceViewerConfiguration(
				colorManager,this);
		sourceViewer.configure(srcCfg);
		IDocument document = new Document();

		WordPartitionScanner scanner = new WordPartitionScanner();

		IDocumentPartitioner partitioner =
		      new FastPartitioner(scanner,
		        new String[]{WordPartitionScanner.JAVA_SINGLE_LINE_COMMENT,
		    		  WordPartitionScanner.JAVA_MULTI_LINE_COMMENT,
		    		  WordPartitionScanner.JAVA_DOC,
		    		  WordPartitionScanner.JAVA_STRING,
		    		  WordPartitionScanner.JAVA_CHARACTER}
		        );
		partitioner.connect(document);
		document.setDocumentPartitioner(partitioner);

		sourceViewer.setDocument(document);

		//add function key
		addKeyActions();

		//create context menu
		createContextMenu();

		//v3
		addSelectListeners();

		addMouseListener();

	}


	/**
	 * キーボートイベント
	 *
	 */
	private void addKeyActions() {

		sourceViewer.getTextWidget().addKeyListener(new KeyAdapter() {
              public void keyPressed(KeyEvent event) {
                  if (event.keyCode == SWT.F3 ){
                	  actionOfDelcarement();
                  }
              }
          });
    }

	/**
	 * Context Menuの作成
	 *
	 */
	private void createContextMenu(){

		// Create menu manager.
        MenuManager menuMgr = new MenuManager("#SourceViewMenu");
        menuMgr.setRemoveAllWhenShown(true);
        menuMgr.addMenuListener(new IMenuListener() {
            public void menuAboutToShow(IMenuManager mgr) {
                fillContextMenu(mgr);
            }
        });

        // Create menu.
        Menu menu = menuMgr.createContextMenu(sourceViewer.getTextWidget());
        sourceViewer.getTextWidget().setMenu(menu);

	}

	/**
	 * ContextMenuに表示されるMenuItemの生成
	 * @param mgr
	 */
	private void fillContextMenu(IMenuManager mgr) {

		//get selected string
		Point sel = sourceViewer.getSelectedRange();
        if(sel.y >  0){
        	mgr.add(copyAction);		//選択あり
        	mgr.add(addMonitorMethodTask);
        }
        mgr.add(appWindow.gotoDeclareAction);
	}

	/**
	 * get type of this viewr
	 */
	public int getType(){
		return type;
	}

	public void setEnable(boolean flag){
		this.parent.setEnabled(flag);
	}

	/**
	 * 指定した文字が変数名／フィールド名であるかどうかを判断
	 * @param offset
	 * @param length
	 * @return
	 */
	public boolean isFieldOrVariable(int offset,int length){

		Point sel = new Point(offset,length);
		//対応するASTノートを取得
		SimpleVisitor simpleVisitor = new SimpleVisitor(sel);
		compileUnit.accept(simpleVisitor);

		ASTNode node = simpleVisitor.getMatchedNode();
		if(node == null ){
			return false;
		}
		if(sourceVisitor == null){
			return false;
		}

		return sourceVisitor.isFieldOrVariable(node);
	}

	/**
	 * 選択されている要素の宣言を開く
	 *
	 */
	private void actionOfDelcarement(){

		//get selected string
		try{
			Point sel = sourceViewer.getSelectedRange();
			ITypedRegion partition = sourceViewer.getDocument().getPartition(
					sel.x);
			String objectStr = null;
			if(IDocument.DEFAULT_CONTENT_TYPE.equals(partition.getType())){

			    if(sel.y >  0){
			       	//選択あり
			    	objectStr = sourceViewer.getDocument().get(
			    			sel.x, sel.y);

			    }else{
			    	//選択なし、現在位置より、JavaWordを取得
					IRegion region = JavaWordFinder.findWord(sourceViewer.getDocument(),
							sel.x);
					if (region != null){
						objectStr = sourceViewer.getDocument().get(
								region.getOffset(), region.getLength());
						sel.x = region.getOffset();
						sel.y = region.getLength();
					}
			    }
			}//of if(IDocument.DEFAULT_CONTENT_TYPE.equals(partition.getType()))
		    String errMsg = Message.get("SourceViewer.NotJavaElements");
			if(objectStr == null){
		    	ExcatMessageUtilty.showMessage(this.appWindow.getShell(),errMsg);
		    }else{
		    	if(this.sourceVisitor != null){
		    		//対応するASTノートを取得
		    		SimpleVisitor simpleVisitor = new SimpleVisitor(sel);
		    		compileUnit.accept(simpleVisitor);

		    		ASTNode node = simpleVisitor.getMatchedNode();
		    		if(node == null){
				    	ExcatMessageUtilty.showMessage(this.appWindow.getShell(),errMsg);
		    		}else{
		    			//要素の宣言を開く機能を実装するクラスのインスタンスを取得
		    			DeclareVisitor declareVisitor = new DeclareVisitor(
		    					node,this.sourceVisitor);
		    			compileUnit.accept(declareVisitor);

		    			IGoToDeclare delclareImpl = declareVisitor.getDelclareImpl();
		    			if(delclareImpl != null){
		    				delclareImpl.setJavaSourceViewer(this);
		    				delclareImpl.gotoDeclarePlace();
		    			}
		    		}
		    	}
		    }
		}catch (BadLocationException e) {
			HelperFunc.getLogger().error("JavaSourceViewer", e);
			ExcatMessageUtilty.showErrorMessage(this.appWindow.getShell(), e);
		}catch(SourceNotFoundException ex){
			ExcatMessageUtilty.showMessage(
					this.appWindow.getShell(),
					Message.get("SourceViewer.NoSourceFile"));
		}catch(NoClassInPathException ex){
			HelperFunc.getLogger().error("JavaSourceViewer", ex);
			ExcatMessageUtilty.showMessage(
					this.appWindow.getShell(),
					Message.get("SourceViewer.NoClassFound"));
		}

	}

	/**
	 * generate ast tree from source file
	 * @param fileContents
	 */
	private void compileJavaSrc(String fileContents){

		//build ast tree
		char[] source = fileContents.toCharArray();
		ASTParser parser = ASTParser.newParser(AST.JLS3);  // handles JDK 1.0, 1.1, 1.2, 1.3, 1.4, 1.5
		parser.setSource(source);
		compileUnit = (CompilationUnit) parser.createAST(null);

		//初めてソースファイルをオープンする場合、タイプ情報を解決しない
		JavaSourceVisitor sv = new JavaSourceVisitor();
		compileUnit.accept(sv);
		setSourceVisitor(sv);

		//resolve type
		resolveThread = new Thread(){
			public void run(){
				sourceVisitor.resolveType();
			}
		};
		resolveThread.start();
	}

	/**
	 * データを表示<br>
	 * 初めてこのビューが作成された後に、よばれる
	 * @param lineNum
	 * @param fileContents
	 */
	public void display(String fileContents, MethodInfo methodInfo){
		sourceViewer.getDocument().set(fileContents);

		//compile source
		compileJavaSrc(fileContents);

		sourceViewer.getControl().pack();
		this.methodInfo = methodInfo;
		if ("".equals(fileContents)){
			type = DUMMY;
		}else{
			type = JAVA_SOURCE;
		}
	}

	/**
	 * クラス／メソッドを宣言する箇所を表示する。
	 */
	public void showDeclaredPlace(MethodInfo methodInfo){
		this.methodInfo = methodInfo;
		if(methodInfo.getMethodName() == null){
			//go to the place of the declaration of the class
			String className = methodInfo.getClassName();
			ClassTypeInfo classInfo = sourceVisitor.getClassTypeInfoByInnerName(className);

			if(classInfo != null){
				setSelectionWithTrace(classInfo.getClassNameStart(),
						classInfo.getClassNameStart() +
						classInfo.getClassNameLength());
			}
		}else{
			//go to the place of the declaration of the method
			int startPos = methodInfo.getStartPosition();
			if(startPos <= 0){
				//このメソッドを宣言するクラスの情報は、クラスファイル
				//から取得するため、メソッド名の位置情報がありません。
				//スレッドresolveThreadが該当ソースファイルを分析します。
				if(resolveThread != null){
					while(resolveThread.isAlive()){
						try {
							Thread.sleep(10);
						} catch (InterruptedException e) {
                           //do nothing
						}
					}
				}
				this.sourceVisitor.setMethodDeclarePos(methodInfo);
			}
			setSelectionWithTrace(methodInfo.getStartPosition(),
					methodInfo.getStartPosition()+ methodInfo.getOffset());
		}
	}

	/**
	 * フィールドを宣言する箇所を表示する。
	 * もし、該当ｸﾗｽになければ、親クラスを探す。
	 * @param matchField
	 * @version 3.0
	 * @date 2009/10/28
	 * @author tu-ipride
	 */
	public void showDeclaredPlace(MatchField matchField){
		setSelectionWithTrace(matchField.getStartPosition(),
				matchField.getStartPosition()+ matchField.getLength());
	}

	/**
	 * 実行される行を表示するようにViewerをScrollする。<br>
	 * StackTreeをダブルクリックした後に、よばれる
	 *
	 */
	public void showCalledPlace(MethodInfo methodInfo){

		this.methodInfo = methodInfo;

		if(methodInfo.getLineNum() >= 0){
			highlight(methodInfo.getLineNum());
		}
	}

	/**
	 * show the selection from start to end
	 * @param start
	 * @param end
	 */
	public void setSelectionWithTrace(int start,int end){
		String classPath = this.methodInfo.getClassSig();
		MoveRecord moveRecord = new MoveRecord(classPath,start,end);
		MoveMgr.register(moveRecord);
		sourceViewer.getTextWidget().setSelection(start, end);
		updateRuler();
	}

	/**
	 * this method call by move action.
	 * so, don't have traces.
	 * @param start
	 * @param end
	 */
	public void setSelection(int start,int end){
		sourceViewer.getTextWidget().setSelection(start, end);
		updateRuler();
	}

	/**
	 * 表示・描線
	 */
	public void highlight(int lineNum){
		if (lineNum == 0) {
			return;
		}
		//復元
		if (methodLine >=0){
			sourceViewer.getTextWidget().setLineBackground(methodLine, 1,
					Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		}

		int maxLine = sourceViewer.getTextWidget().getLineCount();

		if (lineNum > maxLine){
			return;
		}

		methodLine = lineNum - 1;

		//change the color of the line
		sourceViewer.getTextWidget().setLineBackground(methodLine, 1,
				Display.getCurrent().getSystemColor(SWT.COLOR_YELLOW));

		//move to the position
		if (methodLine>20){
			sourceViewer.getTextWidget().setTopIndex(methodLine-20);
		}else{
			sourceViewer.getTextWidget().setTopIndex(0);
		}
		updateRuler();
	}

	public void clearSelectLine(){}

	/**
	 * 検索対象文字列として、現在選択中の文字列を対象として取得します。
	 */
	public String getFindString() {
		return sourceViewer.getFindReplaceTarget().getSelectionText();
	}

	/**
	 * ソースビューワのドキュメントから指定された条件で検索を行います。
	 * 検索にヒットした最初の文字列を選択状態となります。
	 * 見つからなかった場合は、メッセージダイアログを表示します。
	 */
	public boolean find(FindCondition condition) {
		sourceViewer.getFindReplaceTarget();
		IDocument doc = sourceViewer.getDocument();
		int findOffset;

		// 現在のフォーカスから開始位置を取得
		Point curPos = sourceViewer.getSelectedRange();
		if(condition.isForwardSearch()) {
			findOffset = curPos.x + curPos.y;
		} else
		{
			if(curPos.x > 0) {
				findOffset = curPos.x - 1;
			} else
			{
				if(condition.isCircularSearch()){
					findOffset = -1;
				}else{
					if(!condition.isForwardSearch()){
						return false;
					}
					findOffset = 0;
				}
			}
		}

		int foundPos = sourceViewer.getFindReplaceTarget().findAndSelect(
				findOffset,
				condition.getTargetString(),
				condition.isForwardSearch(),
				condition.isCaseSensitive(),
				condition.isWordSearch());
		if(foundPos >= 0){
			return true;
		}
		else
		{
			if(condition.isCircularSearch())
			{
				// 循環検索
				if(condition.isForwardSearch())
				{
					findOffset = 0;
				} else
				{
					findOffset = doc.getLength() - 1;
				}
				int foundPos2 = sourceViewer.getFindReplaceTarget().findAndSelect(
						findOffset,
						condition.getTargetString(),
						condition.isForwardSearch(),
						condition.isCaseSensitive(),
						condition.isWordSearch());
				if(foundPos2 >= 0)
				{
					return true;
				}else
				{
					return false;
				}
			}else
			{
				return false;
			}
		}
	}

	/**
	 * a listener for clip board copy and paste.
	 * @version 3.0
	 * @date 2009/10/18
	 */
	private void addSelectListeners(){
		sourceViewer.addPostSelectionChangedListener(new ISelectionChangedListener(){
			public void selectionChanged(
					SelectionChangedEvent selectionchangedevent) {
				String data = sourceViewer.getTextWidget().getSelectionText();
				if (data != null && !"".equals(data)){
					ExcatClipboard.setCopyCandidate(data);
				}
			}

		});
	}

	public int getLineNum(int pos) throws BadLocationException{
		return sourceViewer.getDocument().getLineOfOffset(pos);
	}

	/**
	 * double click on java source viewer.
	 * @version 3.0
	 * @date 2009/10/24
	 * @author tu-ipride
	 */
	private void addMouseListener(){
		sourceViewer.getTextWidget().addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent selectionevent) {
			}

			public void widgetSelected(SelectionEvent selectionevent) {
				try {
					int startPos = selectionevent.x;
					int length = selectionevent.y - selectionevent.x;
					IGoToDeclare declare = getDelclare(startPos, length);
					if (declare != null) {
						showStackViewer(declare);
			        	gotoDeclareAction.SetGoToDeclare(declare);
			        	gotoDeclareAction.setEnabled(true);
					} else {
						showStackViewer(startPos, length);
						gotoDeclareAction.SetGoToDeclare(null);
			        	gotoDeclareAction.setEnabled(false);
					}
			        if (declare instanceof MethodDeclarePlace){
			        	addMonitorMethodTask.setMethodDeclarePlace((MethodDeclarePlace)declare);
			        	addMonitorMethodTask.setEnabled(true);
			        }else{
			        	addMonitorMethodTask.setEnabled(false);
			        }
				} catch (Exception e) {
					HelperFunc.getLogger().error("JavaSourceViewer", e);
				}
			}

		});
		// 障害478
//		sourceViewer.getTextWidget().addMouseListener(new MouseListener(){
//			@Override
//			public void mouseDoubleClick(MouseEvent arg0) {
//				try {
//					Point sel = sourceViewer.getSelectedRange();
//					char w = sourceViewer.getDocument().getChar(sel.x);
//					if (Utility.isSeparate(w)){
//						return;
//					}
//					int startPos = getStartPos(sel.x);
//					int endPos = getEndPos(sel.x);
//					setSelectionWithTrace(startPos,endPos);
//			        IGoToDeclare declare = getDelclare(sel.x, sel.y);
//			        if (declare != null){
////				        showStackViewer(declare);
////			        	gotoDeclareAction.SetGoToDeclare(declare);
////			        	gotoDeclareAction.setEnabled(true);
//			        }else{
//			        	showStackViewer(sel.x, sel.y);
////			        	gotoDeclareAction.SetGoToDeclare(null);
////			        	gotoDeclareAction.setEnabled(false);
//			        }
////			        if (declare instanceof MethodDeclarePlace){
////			        	addMonitorMethodTask.setMethodDeclarePlace((MethodDeclarePlace)declare);
////			        	addMonitorMethodTask.setEnabled(true);
////			        }else{
////			        	addMonitorMethodTask.setEnabled(false);
////			        }
//				} catch (Exception e) {
//					HelperFunc.getLogger().error("JavaSourceViewer", e);
//				}
//			}
//
//			@Override
//			public void mouseDown(MouseEvent arg0) {
////				gotoDeclareAction.setEnabled(false);
//			}
//
//			@Override
//			public void mouseUp(MouseEvent arg0) {
//			}
//		});
	}

	/**
	 * get a start position of a word.
	 * @version 3.0
	 * @param pos
	 * @return
	 * @throws BadLocationException
	 */
	private int getStartPos(int pos) throws BadLocationException{
		int startPos = pos;
		while(true){
			if (startPos > 0){
				startPos--;
				char w = sourceViewer.getDocument().getChar(startPos);
				if (Utility.isSeparate(w)){
					startPos++;
					break;
				}
			}else{
				break;
			}
		}
		return startPos;
	}

	/**
	 * get end position of a word.
	 * @version 3.0
	 * @param pos
	 * @return
	 * @throws BadLocationException
	 */
	private int getEndPos(int pos) throws BadLocationException{
		int startPos = pos;
		int max = sourceViewer.getDocument().getLength();
		while(true){
			if (startPos < max-1){
				startPos++;
				char w = sourceViewer.getDocument().getChar(startPos);
				if (Utility.isSeparate(w)){
					break;
				}
			}else{
				break;
			}
		}
		return startPos;
	}

	/**
	 * this is used to show variants on the stack tree viewer.
	 * @param startPos
	 * @param length
	 */
	private IGoToDeclare getDelclare(int startPos, int length){

		if(this.sourceVisitor == null){
			return null;
    	}

		Point sel = new Point(startPos,length);
		SimpleVisitor simpleVisitor = new SimpleVisitor(sel);
		compileUnit.accept(simpleVisitor);
		ASTNode node = simpleVisitor.getMatchedNode();
		if (node != null){
			DeclareVisitor declareVisitor = new DeclareVisitor(node,this.sourceVisitor);
			compileUnit.accept(declareVisitor);
			IGoToDeclare delclareImpl = declareVisitor.getDelclareImpl();
			if (delclareImpl != null){
				delclareImpl.setJavaSourceViewer(this);
			}
			return delclareImpl;
		}
		return null;
	}

	/**
	 * when a variant or field of this object is selected,
	 * we will show the node on stack viewer.
	 * @param goToDeclare
	 * @version 3.0
	 * @date 2009/10/27
	 */
	private void showStackViewer(IGoToDeclare goToDeclare){
		Node methodNode = this.methodInfo.getNode();
		if (methodNode == null){
			return;
		}
		if (goToDeclare instanceof VarFieldDeclarePlace){
			VarFieldDeclarePlace varFieldDeclarePlace = (VarFieldDeclarePlace)goToDeclare;
			int start = varFieldDeclarePlace.getStartPosition();
			int length = varFieldDeclarePlace.getLength();
			String varName = sourceViewer.getTextWidget().getText(start, start+length-1);
			showStackViewerVariant(methodNode,varName);
		}else if (goToDeclare instanceof ThisFieldDeclarePlace){
			ThisFieldDeclarePlace thisFieldDeclarePlace = (ThisFieldDeclarePlace)goToDeclare;
			int start = thisFieldDeclarePlace.getStartPosition();
			int length = thisFieldDeclarePlace.getLength();
			String fieldName = sourceViewer.getTextWidget().getText(start, start+length-1);
			showStackViewerThisObject(methodNode,fieldName);
		}
	}

	/**
	 * this is a simple type of local variant.
	 * @param startPos
	 * @param endPos
	 * @throws BadLocationException
	 */
	private void showStackViewer(int startPos, int length) throws BadLocationException{
		Node methodNode = this.methodInfo.getNode();
		if (methodNode == null){
			return;
		}
		if(this.sourceVisitor == null){
			return;
    	}

		Point sel = new Point(startPos,length);
		SimpleVisitor simpleVisitor = new SimpleVisitor(sel);
		compileUnit.accept(simpleVisitor);
		ASTNode node = simpleVisitor.getMatchedNode();
		if (node != null){
			String varName = sourceViewer.getTextWidget().getText(startPos, startPos + length-1 );
			MatchVariableInfo var = sourceVisitor.getMatchedVar(startPos,length,varName);
			if (var != null){
				int varStartLine = sourceViewer.getDocument().getLineOfOffset(var.getValidFrom());
				int varEndLine=sourceViewer.getDocument().getLineOfOffset(var.getValidTo());
				if (methodLine >=varStartLine && methodLine <= varEndLine){
					showStackViewerVariant(methodNode, varName);
				}
			}
		}
	}

	private void showStackViewerVariant(Node methodNode, String varName){
		TreeViewer treeViewer = this.appWindow.analyzerform.stackTree.getTreeViewer();
		treeViewer.expandToLevel(3);
		//[dump]tag
		TreeItem dumpItem = treeViewer.getTree().getItem(0);
		//[Thread]tag
		TreeItem threadItem = dumpItem.getItem(0);
		TreeItem[] methodItemList = threadItem.getItems();
		//[Method]tag
		for (TreeItem methodItem : methodItemList){
			Node node = (Node)methodItem.getData();
			if (node == methodNode){
				treeViewer.expandToLevel(methodNode,1);
				//[this],[Argument],[Variable]tag
				TreeItem[] varItemList = methodItem.getItems();
				for (TreeItem varItem : varItemList){
					Element vNode = (Element)varItem.getData();
					String name = vNode.getAttribute(DumpFileXmlConstant.ATTR_NAME);
					if ( varName.equals(name)){
						treeViewer.getTree().setSelection(varItem);
						return;
					}
				}
			}
		}
	}

	private void showStackViewerThisObject(Node methodNode,String fieldName){
		TreeViewer treeViewer = this.appWindow.analyzerform.stackTree.getTreeViewer();
		treeViewer.expandToLevel(3);
		//[dump]tag
		TreeItem dumpItem = treeViewer.getTree().getItem(0);
		//[Thread]tag
		TreeItem threadItem = dumpItem.getItem(0);
		TreeItem[] methodItemList = threadItem.getItems();
		//[Method]tag
		for (TreeItem methodItem : methodItemList){
			Node node = (Node)methodItem.getData();
			if (node == methodNode){
				treeViewer.expandToLevel(methodNode,1);
				//[this],[Argument],[Variable]tag
				TreeItem[] varItemList = methodItem.getItems();
				for (TreeItem item : varItemList){
					Node thisNode = (Node)item.getData();
					if (thisNode == null) continue;
					String name = ((Element)thisNode).getNodeName();
					if (DumpFileXmlConstant.NODE_THIS.equals(name)){
						treeViewer.expandToLevel(thisNode, 1);
						showStackViewerFieldObject(item,fieldName);
						return;
					}
				}
			}
		}
	}

	private void showStackViewerFieldObject(TreeItem thisItem,String fieldName){
		TreeViewer treeViewer = this.appWindow.analyzerform.stackTree.getTreeViewer();
		TreeItem[] fieldItemList = thisItem.getItems();

		for (TreeItem item : fieldItemList){
			Node node = (Node)item.getData();
			if (node == null) continue;

			String name = ((Element)node).getAttribute(DumpFileXmlConstant.ATTR_NAME);
			if (fieldName.equals(name)){
				treeViewer.getTree().setSelection(item);
				return;
			}
			//this field is during the super class
			String nodeName = node.getNodeName();
			if (DumpFileXmlConstant.NODE_SUPERCLASS.equals(nodeName)){
				treeViewer.expandToLevel(node, 1);
				showStackViewerFieldObject(item,fieldName);
				return;
			}
		}
	}

	public void setMatchColor(int offset, int i) {
		StyleRange styleRange = new StyleRange();
		styleRange.start = offset;
		styleRange.length = i;
		styleRange.background = Display.getCurrent().getSystemColor(SWT.COLOR_CYAN);
		sourceViewer.getTextWidget().setStyleRange(styleRange);
		if (needMoveFlag) {
			int lineNum = sourceViewer.getTextWidget().getLineAtOffset(offset);
			//move to the position
			if (lineNum>20){
				sourceViewer.getTextWidget().setTopIndex(lineNum-20);
			}
			needMoveFlag = false;
		}
	}
	public void refresh(String contents) {
		// 障害 #531
		sourceViewer.getDocument().set(contents);
		compileJavaSrc(contents);
		sourceViewer.refresh();
	}
}
