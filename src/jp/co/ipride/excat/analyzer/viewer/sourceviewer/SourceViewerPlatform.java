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
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import jp.co.ipride.excat.MainViewer;
import jp.co.ipride.excat.analyzer.action.finder.FindCondition;
import jp.co.ipride.excat.analyzer.action.finder.IFindProvider;
import jp.co.ipride.excat.analyzer.action.finder.IFindStringProvider;
import jp.co.ipride.excat.analyzer.common.DumpFileXmlConstant;
import jp.co.ipride.excat.analyzer.viewer.searchviewer.Match;
import jp.co.ipride.excat.analyzer.viewer.sourceviewer.move.MoveRecord;
import jp.co.ipride.excat.common.ApplicationResource;
import jp.co.ipride.excat.common.Message;
import jp.co.ipride.excat.common.setting.Setting;
import jp.co.ipride.excat.common.setting.SettingManager;
import jp.co.ipride.excat.common.setting.SettingUtility;
import jp.co.ipride.excat.common.setting.repository.Repository;
import jp.co.ipride.excat.common.utility.ExcatMessageUtilty;
import jp.co.ipride.excat.common.utility.HelperFunc;
import jp.co.ipride.excat.common.utility.Utility;

import org.apache.bcel.classfile.JavaClass;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabFolder2Adapter;
import org.eclipse.swt.custom.CTabFolderEvent;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * ソースビューアを管理するクラス
 * @author tu-ipride
 * @version 2.0
 * @since 2009/10/23 add by tu
 */
public class SourceViewerPlatform implements
		IDoubleClickListener,
		ISelectionChangedListener,
		IFindProvider, IFindStringProvider {

	private MainViewer appWindow;

	// 親フォーム
	private Composite parent;

	// ソースビューア管理のタブ
	public CTabFolder tabFolder;

	// ソースビューア・リスト
	//the key is a full class name for java source.
	//the key is a full class name + method name + method sig.
	private HashMap<String, AbstractViewer> viewers = new HashMap<String, AbstractViewer>();
	// 属性ビューア・リスンナ
	private ISelectionChangedListener listener;

	/**
	 * このクラスのインスタンス
	 */
	private static SourceViewerPlatform mySelf = null;

	/**
	 * construct
	 *
	 * @param appWindow
	 * @param parent
	 */
	public  SourceViewerPlatform(MainViewer appWindow, Composite parent) {
		if(mySelf  == null){
			this.appWindow = appWindow;
			this.parent = parent;
			init();
			//
			mySelf = this;
		}

	}

	public static SourceViewerPlatform getInstance(){
		return mySelf;
	}

	/**
	 * 初期化
	 *
	 */
	protected void init() {
		tabFolder = new CTabFolder(parent, SWT.BORDER);
		tabFolder.setMaximizeVisible(true);
		tabFolder.setMinimizeVisible(false);
		// tabFolder.setSimple(false);
		tabFolder.addCTabFolder2Listener(new CTabFolder2Adapter() {
			public void minimize(CTabFolderEvent arg0) {
				// Viewer.win.mininizeSourceViewer();
				// tabFolder.setMinimized(true);
			}

			public void maximize(CTabFolderEvent arg0) {
				MainViewer.win.maximizeSourceViewer();
				tabFolder.setMaximized(true);
			}

			public void restore(CTabFolderEvent arg0) {
				MainViewer.win.restoreSourceViewer();
				tabFolder.setMinimized(false);
				tabFolder.setMaximized(false);
			}

			public void close(CTabFolderEvent arg0){
				if (viewers.size()==1){
					appWindow.analyzerform.closeLocalVarView();
				}
			}
		});
		tabFolder.setTabHeight(24);
		tabFolder.setSelectionBackground(new Color[] {
				parent.getDisplay().getSystemColor(SWT.COLOR_TITLE_BACKGROUND),
				parent.getDisplay().getSystemColor(
						SWT.COLOR_TITLE_BACKGROUND_GRADIENT) },
				new int[] { 90 }, true);
		tabFolder.setSelectionForeground(parent.getDisplay().getSystemColor(
				SWT.COLOR_WHITE));
		tabFolder.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				CTabItem item = tabFolder.getSelection();
				if(item != null){
					changeStatusBar(getViewerOfTabItem(item));
				}
				chageFindMenuEnableState();
			}
		});
		tabFolder.addListener(SWT.MouseDoubleClick, new Listener() {
			public void handleEvent(Event event) {
				if (!tabFolder.getMaximized()) {
					MainViewer.win.maximizeSourceViewer();
					tabFolder.setMaximized(true);
				} else {
					MainViewer.win.restoreSourceViewer();
					tabFolder.setMaximized(false);
				}
			}
		});
		
		//add by Qiu Song on 20100222 for StatusBarにエラーメッセージの表示
		tabFolder.addListener(SWT.MouseDown, new Listener() {
			public void handleEvent(Event event) {
				appWindow.resetStatusErr();	
			}
		});
		//end of add by Qiu Song on 20100222 for StatusBarにエラーメッセージの表示
		//add by Qiu Song for チケット:535
		// Create menu manager.
        MenuManager menuMgr = new MenuManager("#tabFolderMenu");
        menuMgr.setRemoveAllWhenShown(true);
        menuMgr.addMenuListener(new IMenuListener() {
            public void menuAboutToShow(IMenuManager mgr) {
                fillContextMenu(mgr);
            }
        });

        // Create menu.
        Menu menu = menuMgr.createContextMenu(tabFolder);
        tabFolder.setMenu(menu);
		//end of add by Qiu Song for チケット:535
	}
	
	//add by Qiu Song for チケット:535
	/**
	 * ContextMenuに表示されるMenuItemの生成
	 * @param mgr
	 */
	private void fillContextMenu(IMenuManager mgr) {

		//get selected string
        	mgr.add(appWindow.closeAllAction);		//すべてのソースを閉じる
	}
	//end of add by Qiu Song for チケット:535
	
	public List<String> getViewerPaths() {
		List<String> result = new ArrayList<String>();
		Collection<AbstractViewer> values = viewers.values();
		for (Iterator<AbstractViewer> it = values.iterator(); it != null && it.hasNext();) {
			AbstractViewer viewer = (AbstractViewer) it.next();
			result.add(viewer.getSourcePath());
		}
		return result;
	}

	/**
	 * a process when select a method.
	 * call by stack view.
	 * @param node
	 */
	private void selectMethod(Node node) {
		MethodInfo methodInfo = new MethodInfo(node);
		AbstractViewer viewer = (AbstractViewer) getViewer(methodInfo);
		if (viewer == null) {
            //modified by Qiu Song on 20100216 for StatusBarにエラーメッセージの表示
//			ExcatMessageUtilty.showMessage(
//					appWindow.getShell(),
//					Message.get("SourceViewer.NoFileExists"));
			setStatusMsg(Message.get("SourceViewer.NoFileExists"));
			//end of modified by Qiu Song on 20100216 for StatusBarにエラーメッセージの表示
		} else {
			viewer.clearSelectLine();
			//scroll to the place pointed by methodinfo
			viewer.showCalledPlace(methodInfo);

			//change status bar and others
			displayViewer(viewer);
		}
	}

	/**
	 * 指定したクラス／メソッドの該当ソースビューを表示する。
	 * call by BaseDeclarePlace class.
	 * @param methodInfo
	 */
	public void showDelcareViewer(MethodInfo methodInfo){

		AbstractViewer viewer = (AbstractViewer) getViewer(methodInfo);
		if (viewer == null) {
            //modified by Qiu Song on 20100216 for StatusBarにエラーメッセージの表示
//			ExcatMessageUtilty.showMessage(
//					appWindow.getShell(),
//					Message.get("SourceViewer.NoFileExists"));
			setStatusMsg(Message.get("SourceViewer.NoFileExists"));
			//end of modified by Qiu Song on 20100216 for StatusBarにエラーメッセージの表示
		} else {
			//show definition place of the class or method
			viewer.showDeclaredPlace(methodInfo);
			//change status bar and others
			displayViewer(viewer);
		}
	}

	public void showDelcareViewer(MatchField matchField){
		MethodInfo methodInfo = new MethodInfo();
		methodInfo.setClassSig(matchField.getFullClassName());
		AbstractViewer viewer = (AbstractViewer) getViewer(methodInfo);
		if (viewer != null){
			//show definition place of the field
			viewer.showDeclaredPlace(matchField);
			//change status bar and others
			displayViewer(viewer);
		}else{
            //modified by Qiu Song on 20100216 for StatusBarにエラーメッセージの表示
//			ExcatMessageUtilty.showMessage(
//					appWindow.getShell(),
//					Message.get("SourceViewer.NoFileExists"));
			setStatusMsg(Message.get("SourceViewer.NoFileExists"));
			//end of modified by Qiu Song on 20100216 for StatusBarにエラーメッセージの表示
		}
	}

	/**
	 * ジャバクラスの取得
	 * call by getViewerByFileChoice!!
	 * @param methodInfo
	 * @return clazz
	 */
	private JavaClass getByteCode(MethodInfo methodInfo) {

		//メソッド名が設定されていない場合、検索しない
		if(methodInfo.getMethodName() == null){
			return null;
		}
		String classSig = methodInfo.getClassSig();
		return SettingManager.getSetting().getByteCodeContents(classSig);
	}

	/**
	 * ファイル内容の取得
	 * call by getViewerByFileChoice!!
	 * @param methodInfo
	 * @return fileContents
	 */
	private String getJavaFileContents(MethodInfo methodInfo) {
		String nameForJavaSrc = HelperFunc.getJavaFileFullName(methodInfo);
		String fileContents = SettingManager.getSetting().getJavaSourceContent(nameForJavaSrc);
		if (fileContents != null){
			String fileName = SettingManager.getSetting().getJavaFileName(nameForJavaSrc);
			methodInfo.setSourceName(fileName);
		}
		return fileContents;
	}


	/**
	 * Ｖｉｅｗｅｒを表示したあと、関連状態の変更
	 *
	 * @param viewer
	 */
	protected void displayViewer(AbstractViewer viewer) {
		changeStatusBar(viewer);
		tabFolder.setSelection(viewer.getTabItem());
		chageFindMenuEnableState();
	}

	/**
	 * Tabの選択が変わった時に、StatusBarを変更
	 * @param viewer
	 */
	private void changeStatusBar(AbstractViewer viewer){

		//add by Qiu Song on 20100216 for StatusBarにエラーメッセージの表示
		appWindow.resetStatusErr();
		//end of add by Qiu Song on 20100216 for StatusBarにエラーメッセージの表示
		if(viewer == null){
			return;
		}

		String jarPath = ApplicationResource.getResource("Status.JarPath");
		String javaPath = ApplicationResource.getResource("Status.JavaPath");
		String sourceRepository = ApplicationResource
				.getResource("Status.SourceRepositoryPath");
		if (viewer.getType() == AbstractViewer.JAVA_SOURCE) {
			appWindow.setStatus(javaPath + viewer.getSourcePath());
		} else if (viewer.getType() == AbstractViewer.BYTE_CODE) {
			appWindow.setStatus(jarPath + viewer.getSourcePath());
		} else if (viewer.getType() == AbstractViewer.SOURCE_REPOSITORY) {
			appWindow.setStatus(sourceRepository + viewer.getSourcePath());
		} else {
			appWindow.setStatus(ApplicationResource
					.getResource("Status.NoJavaJar"));
		}
	}

	/**
	 * StatusBarのクリア
	 *
	 */
	private void clearStatusBar(){
		//add by Qiu Song on 20100216 for StatusBarにエラーメッセージの表示
		appWindow.resetStatusErr();
		//end of add by Qiu Song on 20100216 for StatusBarにエラーメッセージの表示
		CTabItem item = tabFolder.getSelection();
		if(item == null){
			appWindow.setStatus("");
		}
	}

	/**
	 * 以下の要素で新しいビューアを作成
	 *
	 * @param methodInfo
	 * @return
	 */
	protected AbstractViewer getViewer(MethodInfo methodInfo) {
		AbstractViewer currentViewer = null;
		String currentPriority = null;

		//try to get a java file viewer.
		if (methodInfo.getSourceName() == null) {
			String fileName = SettingManager.getSetting().getJavaFileName(HelperFunc.getJavaFileFullName(methodInfo));
			methodInfo.setSourceName(fileName);
		}
		String sourceName = HelperFunc.getJavaFileFullName(methodInfo);
		currentViewer = (AbstractViewer) viewers.get(sourceName);

		//try to get a byte-code viewer.
		if(currentViewer == null){
			currentViewer = (AbstractViewer) viewers.get(methodInfo.getMethodIdentity());
		}

		//get priority of current viewer.
		if (currentViewer != null && currentViewer.type < Setting.FILE_PRIORITY.length) {
			currentPriority = Setting.FILE_PRIORITY[currentViewer.type];
		}

		String firstPriority = SettingManager.getSetting().getFirstPriority();

		if (currentViewer == null){
			return getViewerBasedPriority(firstPriority,methodInfo);
		}

		if (firstPriority.equals(currentPriority)){
			return currentViewer;
		}else{
			viewers.remove(currentViewer.getTabKey());
			currentViewer.item.dispose();
			return getViewerBasedPriority(firstPriority,methodInfo);
		}
	}

	private AbstractViewer getViewerBasedPriority(String firstPriority, MethodInfo methodInfo) {
		AbstractViewer viewer = null;

		//first selection is byte-code
		if (Setting.FILE_PRIORITY[1].equals(firstPriority)){
			JavaClass javaClass = this.getByteCode(methodInfo);
			if (javaClass != null){
				viewer = this.createBytecodeViewer(javaClass, methodInfo);
			}else{
				String fileContent = this.getJavaFileContents(methodInfo);
				if (fileContent != null){
					viewer = this.createJavaSourceViewer(fileContent, methodInfo);
				}
			}
		}else{
			//first selection is java file.
			String fileContent = this.getJavaFileContents(methodInfo);
			if (fileContent != null){
				viewer = this.createJavaSourceViewer(fileContent, methodInfo);
			}else{
				JavaClass javaClass = this.getByteCode(methodInfo);
				if (javaClass != null){
					viewer = this.createBytecodeViewer(javaClass, methodInfo);
				}
			}
		}
		return viewer;
	}

	/**
	 * ジャバソースビューアの取得
	 *call by getViewerByFileChoice!!
	 * @param fileContent
	 * @param methodInfo
	 * @return
	 */
	private JavaSourceViewer createJavaSourceViewer(String fileContent,
			MethodInfo methodInfo) {
		if (fileContent == null) {
			return null;
		}
		JavaSourceViewer javaViewer = createJavaSourceViewer(methodInfo);
		//tu modify 2009/10/24
//		javaViewer.setSourcePath(SettingManager.getSetting().getCurrentFilePath());
		javaViewer.setSourcePath(
				SettingManager.getSetting().
						getJavaFileFullPath(methodInfo.getClassSig()));
		javaViewer.addSelectionChangedListener(listener);
		String sourceName = HelperFunc.getJavaFileFullName(methodInfo);
		viewers.put(sourceName, javaViewer);
		javaViewer.setTabKey(sourceName);
		javaViewer.display((String) fileContent, methodInfo);
		return javaViewer;
	}

	/**
	 * バイトコードビューアの取得
	 * call by getViewerByFileChoice!!
	 * @param clazz
	 * @param methodInfo
	 * @return
	 */
	private BytecodeViewer createBytecodeViewer(Object clazz, MethodInfo methodInfo) {
		if (clazz == null) {
			return null;
		}
		BytecodeViewer bytecodeViewer = createBytecodeViewer(methodInfo);
		//tu modify 2009/10/24
//		bytecodeViewer.setSourcePath(SettingManager.getSetting().getCurrentFilePath());
		bytecodeViewer.setSourcePath(
				SettingManager.getSetting().getClassFileFullPath(
						methodInfo.getClassSig()));

		bytecodeViewer.addSelectionChangedListener(listener);
		viewers.put(methodInfo.getMethodIdentity(), bytecodeViewer);
		bytecodeViewer.setTabKey(methodInfo.getMethodIdentity());
		bytecodeViewer.diplay((JavaClass) clazz, methodInfo);
		return bytecodeViewer;
	}

	/**
	 * 新規ソースビューア
	 *
	 * @param methodInfo
	 * @return
	 */
	private JavaSourceViewer createJavaSourceViewer(MethodInfo methodInfo) {
		CTabItem viewer = new CTabItem(tabFolder, SWT.CLOSE);
		viewer.setText(methodInfo.getTabTitle());
		SashForm form = new SashForm(tabFolder, SWT.VERTICAL | SWT.NONE);
		viewer.setControl(form);

		viewer.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				CTabItem item = (CTabItem) (e.getSource());
				AbstractViewer viewer = getViewerOfTabItem(item);
				if(viewer != null){
					String tabKey = viewer.getTabKey();
					viewers.remove(tabKey);
				}
				clearStatusBar();
				chageFindMenuEnableState();
			}
		});
		return new JavaSourceViewer(appWindow, viewer, methodInfo,
				SWT.READ_ONLY | SWT.BORDER | SWT.V_SCROLL);
	}

	/**
	 * tabItemに対応するViewerを取得する。
	 * @param tabItem
	 * @return
	 */
	private AbstractViewer getViewerOfTabItem(CTabItem tabItem){

		Collection<AbstractViewer> values = viewers.values();
		for (Iterator<AbstractViewer> iterator = values.iterator(); iterator.hasNext();) {
			AbstractViewer v =  iterator.next();
			if (v.item.equals(tabItem)) {
	            return v;
			}
		}
		return null;
	}

	/**
	 * 新規バイトコード・ビューア
	 *
	 * @param methodInfo
	 * @return
	 */
	private BytecodeViewer createBytecodeViewer(MethodInfo methodInfo) {
		CTabItem viewer = new CTabItem(tabFolder, SWT.CLOSE);
		viewer.setText(methodInfo.getClassName() + "."
				+ methodInfo.getMethodName());
		SashForm form = new SashForm(tabFolder, SWT.VERTICAL | SWT.NONE);
		form.setOrientation(SWT.VERTICAL);
		viewer.setControl(form);
		viewer.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {

				CTabItem item = (CTabItem) (e.getSource());
				AbstractViewer viewer = getViewerOfTabItem(item);
				if(viewer != null){
					String tabKey = viewer.getTabKey();
					viewers.remove(tabKey);
				}
				clearStatusBar();
			}
		});
		return new BytecodeViewer(appWindow, viewer, methodInfo);
	}

	/**
	 * close all tab-item.
	 *
	 */
	public void closeAll() {
		CTabItem[] items = tabFolder.getItems();
		for (int i = 0; i < items.length; i++) {
			CTabItem item = items[i];
			item.dispose();
		}
		viewers.clear();
	}

	/**
	 * add a listner to tree.
	 *
	 * @param listener
	 */
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		this.listener = listener;
	}


	/**
	 * 検索対象となるビューワを取得します。 現在表示中のビューワを対象とする。
	 *
	 * @return
	 */
	public IFindProvider getCurrentFindableViewer() {
		// 現在表示しているViewの取得
		IFindProvider targetViewer = null;
		AbstractViewer viewer = null;
		CTabItem targetTabItem = tabFolder.getSelection();
		if (targetTabItem != null) {
			viewer = getViewerOfTabItem(targetTabItem);
			if(viewer != null){
				if (viewer.getType() == AbstractViewer.JAVA_SOURCE ||
						viewer.getType() == AbstractViewer.SOURCE_REPOSITORY) {
					// Javaソースコードのみを検索対象とする
					targetViewer = (IFindProvider) viewer;
				}
			}
		}
		if (targetViewer != null) {
			return targetViewer;
		}
		// 現在表示中のJavaソースコードViewerが見つからない場合はnullを返却
		return null;
	}

	/**
	 * 検索処理を行います。
	 */
	public boolean find(FindCondition condition) {
		IFindProvider targetViewer = getCurrentFindableViewer();
		if (targetViewer != null) {
			return targetViewer.find(condition);
		}
		return false;
	}

	/**
	 * 検索対象文字列を取得します。
	 */
	public String getFindString() {
		IFindProvider targetViewer = getCurrentFindableViewer();
		if (targetViewer instanceof IFindStringProvider) {
			return ((IFindStringProvider) targetViewer).getFindString();
		}
		return null;
	}

	/**
	 * 検索可能なビューの表示/非表示状態を基に、 検索メニューのEnable状態を変更します。
	 */
	protected void chageFindMenuEnableState() {
		if (getCurrentFindableViewer() != null) {
			appWindow.setSourecViewTextSearchActionState(true);
		} else {
			appWindow.setSourecViewTextSearchActionState(false);
		}
	}

	/**
	 * show source when user double click a element on stack viewer.
	 * @version 2.0
	 * @date 2009/10/? modify by tu.
	 */
	public void doubleClick(DoubleClickEvent event) {
		StructuredSelection selection = (StructuredSelection) event.getSelection();

		Node nodeSelected = (Node) selection.getFirstElement();
		if (nodeSelected == null) {
			return;
		}

		if (DumpFileXmlConstant.NODE_METHOD.equals(nodeSelected.getNodeName())) {
			selectMethod(nodeSelected);
		}

		//add by tu 2009/10/24
		if (DumpFileXmlConstant.NODE_THIS.equals(nodeSelected.getNodeName())
			|| DumpFileXmlConstant.NODE_SUPERCLASS.equals(nodeSelected.getNodeName())
			|| DumpFileXmlConstant.NODE_RETURN.equals(nodeSelected.getNodeName())
			|| DumpFileXmlConstant.NODE_EXCEPTION_OBJECT.equals(nodeSelected.getNodeName())
			|| DumpFileXmlConstant.NODE_ARGUMENT.equals(nodeSelected.getNodeName())
			|| DumpFileXmlConstant.NODE_ATTRIBUTE.equals(nodeSelected.getNodeName())
			|| DumpFileXmlConstant.NODE_CONTEND_MONITOR_OBJECT.equals(nodeSelected.getNodeName())
			|| DumpFileXmlConstant.NODE_INSTANCE.equals(nodeSelected.getNodeName())
			|| DumpFileXmlConstant.NODE_ITEM.equals(nodeSelected.getNodeName())){

			String realType = ((Element)nodeSelected).getAttribute(DumpFileXmlConstant.ATTR_REAL_TYPE);
			if (realType == null || "".equals(realType) || Utility.isArray(realType)){
				return;
			}
			MethodInfo methodInfo = new MethodInfo();
			methodInfo.setClassSig(realType);
			//add comment by Qiu Song on 20100219 for TODO:May be modify here for bug
			showDelcareViewer(methodInfo);
		}
	}

	/**
	 * show def position when user click the elemnt on stack viewer.
	 * @version 3.0
	 * @since 2009/10/20
	 * @author tu-ipride
	 */
	public void selectionChanged(SelectionChangedEvent event) {
		String varName=null;
		try{
			StructuredSelection selection = (StructuredSelection) event.getSelection();
			Node nodeSelected = (Node) selection.getFirstElement();
			if (nodeSelected == null) {
				return;
			}
			//select element's name
			varName = ((Element)nodeSelected).getAttribute(DumpFileXmlConstant.ATTR_NAME);

			//process for local variants.
			if (DumpFileXmlConstant.NODE_ARGUMENT.equals(nodeSelected.getNodeName())
				|| DumpFileXmlConstant.NODE_VARIABLE.equals(nodeSelected.getNodeName())	) {

				Node methodNode = nodeSelected.getParentNode();
				//if the parent is a method.
				if (DumpFileXmlConstant.NODE_METHOD.equals(methodNode.getNodeName())){
					MethodInfo methodInfo = new MethodInfo(methodNode);
					JavaSourceViewer srcViewer = getActiveJavaSrcViewer(HelperFunc.getJavaFileFullName(methodInfo));
					if (srcViewer != null){
						selectLocalVarOfJavaSrcViewer(srcViewer,methodInfo,varName);
					}else{
						BytecodeViewer bViewer = getActiveBytecodeViewer(methodInfo);
						if (bViewer != null){
							selectLocalVarOfByteCodeViewer(bViewer, varName);
						}
					}
				}
			}else if (DumpFileXmlConstant.NODE_ATTRIBUTE.equals(nodeSelected.getNodeName())){
				Node parentNode = nodeSelected.getParentNode();
				String realType = ((Element)parentNode).getAttribute(DumpFileXmlConstant.ATTR_REAL_TYPE);
				if (realType == null){
					return;
				}
				String sourceName = SettingManager.getSetting().getJavaFileName(realType);
				String sourceFullName = HelperFunc.getJavaFileFullName(realType, sourceName);
				AbstractViewer viewer = getActiveJavaSrcViewer(sourceFullName);
				if (viewer == null || viewer.getType() == AbstractViewer.BYTE_CODE){
					return;
				}
				JavaSourceViewer srcViewer = (JavaSourceViewer)viewer;
				List<MatchField> fieldList = srcViewer.getSourceVisitor().getFieldList();
				for (MatchField field : fieldList){
					if (field.getFieldName().equals(varName)){
						int start = field.getStartPosition();
						int end = start + field.getFieldLength();
						srcViewer.setSelection(start, end);
						break;
					}
				}
			}
		}catch(Exception e){
			HelperFunc.getLogger().error("sourceViewerPlatform", e);
		}
	}

	/**
	 * call by forword action and privous action.
	 * @param moveRecord
	 */
	public void moveTo(MoveRecord moveRecord){
		JavaSourceViewer srcViewer = null;
		srcViewer = (JavaSourceViewer) viewers.get(moveRecord.getClassPath());
		if (srcViewer == null){
			MethodInfo methodInfo = new MethodInfo();
			methodInfo.setClassSig(moveRecord.getClassPath());
			AbstractViewer viewer = getViewer(methodInfo);
			if (viewer != null && viewer.getType() == AbstractViewer.JAVA_SOURCE){
				srcViewer = (JavaSourceViewer)viewer;
				displayViewer(srcViewer);
				srcViewer.setSelection(moveRecord.getStartPos(), moveRecord.getEndPos());
			}
		}else {
			displayViewer(srcViewer);
			srcViewer.setSelection(moveRecord.getStartPos(), moveRecord.getEndPos());
		}
	}

	/**
	 * process a selection of local variant on stack tree when the viewer is a java source viewer.
	 * @param srcViewer  java source viewer
	 * @param methodInfo  method of selected variant
	 * @param varName   name of selected variant
	 * @throws BadLocationException
	 */
	private void selectLocalVarOfJavaSrcViewer(
			JavaSourceViewer srcViewer,
			MethodInfo methodInfo,
			String varName) throws BadLocationException{
		MatchMethodInfo mInfo = srcViewer.getSourceVisitor().getMatchedMethod(methodInfo);
		if (mInfo == null){
			return;
		}
		//if not is a same class, will return.
		List<MatchVariableInfo> varInfoList = mInfo.getVariableList();
		for (MatchVariableInfo varInfo : varInfoList){
			if ( varInfo.getName().equals(varName)
				&& 	methodInfo.getLineNum()>= srcViewer.getLineNum(varInfo.getValidFrom())
				&&  methodInfo.getLineNum()<= srcViewer.getLineNum(varInfo.getValidTo())){

				int start = varInfo.getStartPosition();
				int end = start + varInfo.getLength();
				srcViewer.setSelection(start, end);
				break;
			}
		}
	}

	private void selectLocalVarOfByteCodeViewer(
			BytecodeViewer bViewer,
			String varName) throws BadLocationException{
		bViewer.selectLocalVarNameFromStackViewer(varName);
	}

	/**
	 * 現在に表示しているビューアは、指定のパスが一致する場合返却する
	 * それ以外の場合、NULL
	 * @param classPath
	 * @return
	 */
	private JavaSourceViewer getActiveJavaSrcViewer(String classPath){
		AbstractViewer viewer = (AbstractViewer) viewers.get(classPath);
		if (viewer == null){
			return null;
		}
		CTabItem curItem = tabFolder.getSelection();
		if (curItem == null){
			return null;
		}
		AbstractViewer curViewer = getViewerOfTabItem(curItem);
		if (curViewer != viewer){
			return null;
		}else{
			return (JavaSourceViewer)viewer;
		}
	}

	private BytecodeViewer getActiveBytecodeViewer(MethodInfo methodInfo){
		BytecodeViewer viewer = (BytecodeViewer) viewers.get(methodInfo.getMethodIdentity());
		if (viewer == null){
			return null;
		}
		CTabItem curItem = tabFolder.getSelection();
		if (curItem == null){
			return null;
		}
		AbstractViewer curViewer = getViewerOfTabItem(curItem);
		if (curViewer != viewer){
			return null;
		}else{
			return viewer;
		}
	}


	/**
	 * リポジトリ検索結果を表示するメソッド
	 *
	 * @param sourceName ソースファイル名
	 * @param matchList 検索結果リスト
	 */
	public void showDelcareViewer(String sourceName, List<Match> matchList) {
		String fileName = HelperFunc.getFileNameWithoutPath(sourceName);
		MethodInfo methodInfo = new MethodInfo();
		methodInfo.setSourceName(fileName);
		String classSig = Repository.getInstance().getClassNameByFileName(sourceName);
		methodInfo.setClassSig(classSig);
		String fullName = HelperFunc.getJavaFileFullName(methodInfo);
		JavaSourceViewer viewer = (JavaSourceViewer) viewers.get(fullName);
		if (viewer != null) {
			String contents = SettingUtility.getContents(sourceName);
			viewer = createJavaSourceViewer(viewer.getTabItem(), methodInfo);
			viewer.setSourcePath(sourceName);
			viewers.put(fullName, viewer);
			viewer.setTabKey(fullName);
			viewer.display((String) contents, methodInfo);
			Point size = viewer.getTabItem().getControl().getSize();
			viewer.sourceViewer.getControl().setSize(size.x, size.y);
		} else {
			String contents = SettingUtility.getContents(sourceName);
			viewer = createJavaSourceViewer(methodInfo);
			viewer.setSourcePath(sourceName);
			viewer.addSelectionChangedListener(listener);
			viewers.put(fullName, viewer);
			viewer.setTabKey(fullName);
			viewer.display((String) contents, methodInfo);
		}

		displayViewer(viewer);


		for (Match match : matchList) {
			viewer.setMatchColor(match.getOffset(), match.getLength());
		}
	}

	private JavaSourceViewer createJavaSourceViewer(CTabItem viewer, MethodInfo methodInfo) {
		SashForm form = new SashForm(tabFolder, SWT.VERTICAL | SWT.NONE);
		viewer.setControl(form);
		return new JavaSourceViewer(appWindow, viewer, methodInfo,
				SWT.READ_ONLY | SWT.BORDER | SWT.V_SCROLL);
	}

	public void refreshAll() {
		// 障害 #531
		Collection<AbstractViewer> values = viewers.values();
		for (Iterator<AbstractViewer> it = values.iterator(); it != null && it.hasNext();) {
			AbstractViewer viewer = (AbstractViewer) it.next();
			if (viewer instanceof JavaSourceViewer) {
				String contents = SettingUtility.getContents(viewer.getSourcePath());
				if (contents != null) {
					((JavaSourceViewer)viewer).refresh(contents);
				} else {
					viewer.getTabItem().dispose();
				}
			}
		}
	}
	
	//add by Qiu Song on 20100216 for StatusBarにエラーメッセージの表示
	/**
	 * ソースファイルを見つからない時に、StatusBarにエラーメッセージを表示する
	 * @param viewer
	 */
	private void setStatusMsg(String errMsg){
        appWindow.setStatusErr(true);
		appWindow.setStatus(errMsg);
	}
	//end of add by Qiu Song on 20100216 for StatusBarにエラーメッセージの表示
}