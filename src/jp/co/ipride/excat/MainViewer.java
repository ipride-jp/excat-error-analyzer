/*
 * Error Analyzer Tool for Java
 *
 * Created on 2006/4/1
 *
 * Copyright (c) 2006-2009 iPride Co.,Ltd.
 * All rights reserved.
 */
package jp.co.ipride.excat;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

import jp.co.ipride.ExcatLicenseException;
import jp.co.ipride.excat.analyzer.action.AboutAction;
import jp.co.ipride.excat.analyzer.action.AddMonitorMethodTask;
import jp.co.ipride.excat.analyzer.action.CloseAllEdition;
import jp.co.ipride.excat.analyzer.action.CloseXmlAction;
import jp.co.ipride.excat.analyzer.action.DeleteFileAction;
import jp.co.ipride.excat.analyzer.action.ExitAction;
import jp.co.ipride.excat.analyzer.action.ForwordAction;
import jp.co.ipride.excat.analyzer.action.GoToDeclareAction;
import jp.co.ipride.excat.analyzer.action.JavaSrcSearchAction;
import jp.co.ipride.excat.analyzer.action.LicenseAction;
import jp.co.ipride.excat.analyzer.action.ObjectReferenceAction;
import jp.co.ipride.excat.analyzer.action.OpenXmlAction;
import jp.co.ipride.excat.analyzer.action.OutputPdfAction;
import jp.co.ipride.excat.analyzer.action.PreviousAction;
import jp.co.ipride.excat.analyzer.action.PrintAction;
import jp.co.ipride.excat.analyzer.action.SetFileViewerVisibleAction;
import jp.co.ipride.excat.analyzer.action.DumpDataSearchAction;
import jp.co.ipride.excat.analyzer.action.ThreadMapAction;
import jp.co.ipride.excat.analyzer.action.ReflashFileViewerAction;
import jp.co.ipride.excat.analyzer.action.finder.FindAction;
import jp.co.ipride.excat.analyzer.action.finder.FindDialog;
import jp.co.ipride.excat.analyzer.viewer.AnalyzerForm;
import jp.co.ipride.excat.analyzer.viewer.fileviewer.DumpFileViewer;
import jp.co.ipride.excat.analyzer.viewer.sourceviewer.SourceViewerPlatform;
import jp.co.ipride.excat.analyzer.viewer.sourceviewer.move.MoveMgr;
import jp.co.ipride.excat.analyzer.viewer.stackviewer.StackTree;
import jp.co.ipride.excat.common.ApplicationResource;
import jp.co.ipride.excat.common.Message;
import jp.co.ipride.excat.common.action.CopyAction;
import jp.co.ipride.excat.common.action.PasteAction;
import jp.co.ipride.excat.common.action.SettingAction;
import jp.co.ipride.excat.common.action.SourceUpdateAction;
import jp.co.ipride.excat.common.clipboard.ExcatClipboard;
import jp.co.ipride.excat.common.icons.IconFilePathConstant;
import jp.co.ipride.excat.common.setting.SettingManager;
import jp.co.ipride.excat.common.utility.ExcatMessageUtilty;
import jp.co.ipride.excat.common.utility.HelperFunc;
import jp.co.ipride.excat.configeditor.model.ConfigModel;
import jp.co.ipride.excat.configeditor.util.ViewerUtil;
import jp.co.ipride.excat.configeditor.viewer.ConfigMainForm;
import jp.co.ipride.excat.configeditor.viewer.action.AddTaskAction;
import jp.co.ipride.excat.configeditor.viewer.action.CreateNewConfigAction;
import jp.co.ipride.excat.configeditor.viewer.action.DeleteTaskAction;
import jp.co.ipride.excat.configeditor.viewer.action.OpenConfigFileAction;
import jp.co.ipride.excat.configeditor.viewer.action.SaveAsNewConfigFileAction;
import jp.co.ipride.excat.configeditor.viewer.action.UpdateConfigAction;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.eclipse.jface.action.CoolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarContributionItem;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

/**
 * メイン・クラス
 * 設定・分析の共通プラットフォーム
 *
 * @version 1.0 2004/11/29
 * @author 管 暁華
 * @version  3.0 2009/9/5 屠
 */
public class MainViewer extends ApplicationWindow {

	/////////////////　分析画面 ///////////////////

	public String fileName = "";

	// ダンプファイルのツリー・ビューア
	private StackTree stackTree = null;

	// ソースビューア＆バイトコードビューア
	private SourceViewerPlatform sourceViewerPlatform = null;

	// ダンプファイルViewer
	private DumpFileViewer fileViewer = null;

	// 分離棒
	private Separator separator = new Separator();

	// ダンプファイルを開くアクション
	public OpenXmlAction openDumpFileAction = new OpenXmlAction(this);

	// ダンプファイルを閉じるアクション
	public CloseXmlAction closeDumpFileAction = new CloseXmlAction(this);

	//PDFファイル生成アクション
	public OutputPdfAction outputPdfAction = new OutputPdfAction(this);

	// AP終了アクション
	public ExitAction exitAction = new ExitAction(this);

	// ヘルプ・アクション
	public AboutAction aboutAction = new AboutAction(this);

	// ライセンスアクション
	public LicenseAction licenseAction = new LicenseAction(this);

	// 印刷・アクション
	public PrintAction printAction = new PrintAction(this);

	public CloseAllEdition closeAllAction = new CloseAllEdition(this);

	// ファイルビューワの表示切替アクション
	public SetFileViewerVisibleAction setFileViewerVisibleAction = new SetFileViewerVisibleAction(
			this);

	//v3 フィルターと検索の機能
	public DumpDataSearchAction supportSearchAction = new DumpDataSearchAction(this);
	public ObjectReferenceAction objectReferenceAction = new ObjectReferenceAction(this);
	public FindAction sourecViewTextSearchAction = new FindAction(this);
	public ReflashFileViewerAction updateDumpDataViewerAction = new ReflashFileViewerAction(this);
	public DeleteFileAction deleteFileAction = new DeleteFileAction(this);
	public ThreadMapAction threadMapAction = new ThreadMapAction(this);
	public PreviousAction previousAction = new PreviousAction(this);
	public ForwordAction forwordAction = new ForwordAction(this);
	public GoToDeclareAction gotoDeclareAction = new GoToDeclareAction(this);
	public AddMonitorMethodTask addMonitorMethodTask = new AddMonitorMethodTask(this);
	public JavaSrcSearchAction javaSrcSearchAction = new JavaSrcSearchAction(this);

	/////////////// 設定画面部分 //////////////////////
	public CreateNewConfigAction createNewConfigAction = new CreateNewConfigAction(this);
	public OpenConfigFileAction openConfigFileAction = new OpenConfigFileAction(this);
	public SaveAsNewConfigFileAction saveAsNewConfigFileAction = new SaveAsNewConfigFileAction(this);
	public UpdateConfigAction updateConfigAction = new UpdateConfigAction(this);
	public AddTaskAction addAutoMonitorExceptionTaskAction = new AddTaskAction(this,0);
	public AddTaskAction addMonitorExceptionTaskAction = new AddTaskAction(this,1);
	public AddTaskAction addMonitorMethodTaskAction = new AddTaskAction(this,2);
	public AddTaskAction addMonitorSignalTaskAction = new AddTaskAction(this,3);

	public DeleteTaskAction deleteTaskAction = new DeleteTaskAction(this);

	private CTabFolder tabFolder;
	private ToolBarContributionItem[] analyzer_toolBar;
	private ToolBarContributionItem[] config_toolBar;
	private ToolBarContributionItem   help;

	//////////////  共通アクション  ///////////////
	public CopyAction copyAction = new CopyAction(this);
	public PasteAction pasteAction = new PasteAction(this);

	// ソース＆Jarパスを設置するアクション
	private SettingAction settingAction = new SettingAction(this);
	private SourceUpdateAction sourceUpdateAction = new SourceUpdateAction(this);

	private MenuManager[] analyzer_menus =null;
	private MenuManager[] config_menus = null;

	public ConfigMainForm configMainForm = null;
	public AnalyzerForm analyzerform =null;

	public static MainViewer win;

	/**
	 * construct
	 */
	public MainViewer() {
		super(null);

		//初期化
		analyzer_toolBar = new ToolBarContributionItem[6];
		config_toolBar = new ToolBarContributionItem[4];
		analyzer_menus = new MenuManager[6];
		config_menus = new MenuManager[4];

//		registerFindActions();

		this.addMenuBar();
		this.addStatusLine();
		this.addCoolBar(SWT.FLAT);
		URL url = MainViewer.class
				.getResource(IconFilePathConstant.EXCAT_TM_SMALL_16);
		setDefaultImage(ImageDescriptor.createFromURL(url).createImage());
	}

	/**
	 * ライセンスの登録と削除でタイトルを変更
	 *
	 */
	public void resetTitle(){
		Shell shell = getShell();
		String toolName = ApplicationResource.getResource("Title");
		shell.setText(toolName);
	}

	/**
	 * override method.
	 */
	protected Control createContents(final Composite parent) {
		// caption
		Shell shell = getShell();
		resetTitle();

		tabFolder = new CTabFolder(shell,SWT.BORDER );
		tabFolder.setTabHeight(25);
		tabFolder.setSimple(false);

		analyzerform = new AnalyzerForm(this,tabFolder);		//分析時のメインフォーム
		configMainForm = new ConfigMainForm(this,tabFolder);	//設定時のメインフォーム

		//分析、設定の切り替え
		tabFolder.addSelectionListener(new SelectionListener(){
			public void widgetDefaultSelected(SelectionEvent arg0) {}

			public void widgetSelected(SelectionEvent arg0) {
				int index = tabFolder.getSelectionIndex();
				toolBarSelection(index);
			}
		});

		//ディフォルトは設定
		tabFolder.setSelection(0);
		toolBarSelection(0);

		iniState();

		// dump file viewer
		fileViewer = analyzerform.fileViewer;
		deleteFileAction.setEnabled(false);

		// source viewer
		sourceViewerPlatform = analyzerform.sourceViewerPlatform;
		sourceViewerPlatform.addSelectionChangedListener(analyzerform.propertyTable);

		// tree
		stackTree = analyzerform.stackTree;
		stackTree.addSelectionChangedListener(analyzerform.propertyTable);
		stackTree.addSelectionChangedListener(sourceViewerPlatform);
		stackTree.addDoubleClickListener(sourceViewerPlatform);

		//source rearch
		sourecViewTextSearchAction.setFindDialog(new FindDialog(getShell()));
		sourecViewTextSearchAction.setFindProvider(sourceViewerPlatform);
		sourecViewTextSearchAction.setFindStringProvider(sourceViewerPlatform);
		sourecViewTextSearchAction.setEnabled(false);

		//move action
		MoveMgr.setForwordAction(forwordAction);
		MoveMgr.setPreviousAction(previousAction);
		forwordAction.setSourceViewerPlatform(sourceViewerPlatform);
		previousAction.setSourceViewerPlatform(sourceViewerPlatform);

		shell.setMaximized(true);
		shell.layout();

		SettingManager.creatRepository(
				shell,
				ApplicationResource.getResource("SourceUpdateProgressDialog.Create.Text"));

		return parent;
	}

	/**
	 * メニュー作成
	 * override method
	 */
	protected MenuManager createMenuManager() {
		MenuManager menuBar = new MenuManager("");
		createAnalyzerMenuManager();
		createConfigMenuManager();
		return menuBar;
	}

	/**
	 * ツール・バー作成
	 */
	protected CoolBarManager createCoolBarManager(int style) {

		CoolBarManager coolBarManager = new CoolBarManager(style);

		//分析
		createAnalyzeCoolBarManager(style);

		//設定
		createConfgCoolBarManager(style);

		return coolBarManager;
	}

	/**
	 * ダンプファイルを読み込み、処理
	 *
	 * @param path
	 * @throws Exception
	 */
	public boolean openXmlFile(String path) throws Exception {
		try {
			Logger.getLogger("viewerLogger").info(Message.get("OpenXmlAction.Start")+ path);
			SettingManager.getSetting().setCurrentDumpFilePath(path);
			this.getShell().setCursor(new Cursor(null, SWT.CURSOR_WAIT));
			stackTree.closeXmlFile();
			sourceViewerPlatform.closeAll();
			boolean ret = stackTree.setXmlFile(path);
			if(ret){
				stackTree.expandTopNode();
				this.fileName = path;
				return true;
			}else{
				this.fileName = null;
				return false;
			}
		}catch(ExcatLicenseException e){
			String msg=ApplicationResource.getResource("LicenseDialog.Check.ErrMessgae");
			ExcatMessageUtilty.showMessage(this.getShell(),msg);
			return false;
		}catch(Exception e){
			Logger.getLogger("viewerLogger").debug("MainViewer.openXmlFile.error------>>" + e.toString());
			throw e;
		} finally {
			this.getShell().setCursor(null);
		}

	}

	/**
	 * ダンプファイルを閉じた後の処理
	 *
	 * @return
	 */
	public boolean closeDumpFile() {
		analyzerform.closeLocalVarView();
		sourceViewerPlatform.closeAll();
		return stackTree.closeXmlFile();
	}

	/**
	 * PDF出力
	 * @param appWindow　アプリケションウィンドウ
	 * @return なし
	 * @throws Exception
	 */
	public void outputToPdf(MainViewer appWindow) throws Exception {
		try {
			this.getShell().setCursor(new Cursor(null, SWT.CURSOR_WAIT));
			stackTree.outputToPdf(appWindow);
		} finally {
			this.getShell().setCursor(null);
		}
	}

	public void closeAllEditors() {
		sourceViewerPlatform.closeAll();
	}

	/**
	 * Windowを閉じた時点の処理
	 *
	 * @see org.eclipse.jface.window.Window#close()
	 */
	public boolean close() {
		//sourceViewerPlatform.closeAll();
		//closeDumpFile();
		if (ConfigModel.isChanged()) {
			int n = ViewerUtil.reloadConfig(this);
			switch(n){
			case -2:
				//変化なし
				return super.close();
			case 0:
				//保存する
				if (!((MainViewer)this).checkItems()){
					return false;//modified by Qiu Song on 20091207
				}
				if (ConfigModel.isNewConfig()){
					FileDialog saveDialog = new FileDialog(this.getShell(),SWT.SAVE);
					saveDialog.setFilterExtensions(new String[]{"*.config"});
					String saveFile = saveDialog.open();
					if (saveFile != null){
						ConfigModel.saveAsNewConfig(saveFile);
						ExcatMessageUtilty.showMessage(
								this.getShell(),
								Message.get("Tool.Save.Text"));
						return super.close();
					}
				}else{
					if (ConfigModel.update()) {
						ExcatMessageUtilty.showMessage(
								this.getShell(),
								Message.get("Tool.Update.Text"));
						return super.close();
					}
				}
				break;
			case 2:
				//保存せず
				return super.close();
			case 1:
				//キャンセル
				return false;
			}
			return false;
		} else {
			return super.close();
		}
	}

	/**
	 * Windowを開いた時点の処理
	 *
	 * @see org.eclipse.jface.window.Window#open()
	 */
	public int open() {

		Shell shell = getShell();

		if (shell == null) {
			// create the window
			create();
		}

		// limit the shell size to the display size
		constrainShellSize();

		shell = getShell();

		// open the window
		shell.open();

		// run the event loop if specified
		runEventLoop(shell);

		return 0;

	}

	/**
	 * Runs the event loop for the given shell.
	 * @param shell  the shell
	 */
	private void runEventLoop(Shell shell) {

		// Use the display provided by the shell if possible
		Display display;
		if (shell == null)
			display = Display.getCurrent();
		else
			display = shell.getDisplay();

		while (shell != null && !shell.isDisposed()) {
			try {
				if (!display.readAndDispatch())
					display.sleep();
			} catch (Throwable e) {
				HelperFunc.logException(e);
			}
		}
		display.update();
	}

	public void displayValidLocalVar() {
		stackTree.displayValidLocalVar();
	}

	public void displayAllLocalVar() {
		stackTree.displayAllLocalVar();
	}


	/**
	 * ツール起動のメイン関数
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			URL url = MainViewer.class.getClassLoader().getResource("log4j.xml");
			DOMConfigurator.configure(url);

			SettingManager.load();

			win = new MainViewer();
			win.setBlockOnOpen(true);

			win.open();

			ExcatClipboard.dispose();

			Display.getCurrent().dispose();

			SettingManager.save();

		} catch (Throwable e) {
			try {
				HelperFunc.getLogger().error("MainViewer", e);
				ExcatMessageUtilty.showErrorMessage(null,e);
			} catch (Exception e1) {
				ExcatMessageUtilty.showErrorMessage(null,e1);
			}
		}
	}

	public void logException(Throwable e) {

		StringBuffer sb = new StringBuffer();
		// 現時点の参照パス情報
		List<String> paths = sourceViewerPlatform.getViewerPaths();
		sb.append(ApplicationResource.getResource("Log.Path"));
		for (Iterator<String> it = paths.iterator(); it.hasNext();) {
			String path =  it.next();
			if (path != null) {
				sb.append(path);
				sb.append(";");
			}
		}
		sb.append(System.getProperty("line.separator"));

		// 現時点、参照しているダンプ・ファイル名
		sb.append(ApplicationResource.getResource("Log.DumpFileName"));
		sb.append(this.fileName);
		sb.append(System.getProperty("line.separator"));

		// スタック・ダンプ情報
		StringWriter writer = new StringWriter();
		PrintWriter pWriter = new PrintWriter(writer);
		e.printStackTrace(pWriter);
		sb.append(writer.toString());

		Logger.getLogger("viewerLogger").error(sb);
	}

	public void maximizeSourceViewer() {
		setFileViewerVisibleAction.setEnabled(false);
		analyzerform.maximizeSourceViewer();
	}

	public void restoreSourceViewer() {
		setFileViewerVisibleAction.setEnabled(true);
		analyzerform.restoreSourceViewer();
	}

	public void setFileViewerVisible(boolean flag){
		analyzerform.setFileViewerVisible(flag);
	}

	public void setPdfPrintState(boolean bool){
		outputPdfAction.setEnabled(bool);
		printAction.setEnabled(bool);
	}

	/**
	 * ファイルビューを更新します。
	 */
	public void updateFileViewer() {
		fileViewer.updateTreeViewer();
	}

	/**
	 * スタックトレース内容を印刷する。
	 */
	public void print() {
		stackTree.print();
	}

	/**
	 * 分析時のツールバー
	 * @param style
	 */
	private void createAnalyzeCoolBarManager(int style){
		ToolBarManager fileToolBar = new ToolBarManager(style);
		fileToolBar.add(openDumpFileAction);
		fileToolBar.add(closeDumpFileAction);
		fileToolBar.add(closeAllAction);
		fileToolBar.add(outputPdfAction);
		fileToolBar.add(printAction);
		ToolBarContributionItem analyzer_file =
				new ToolBarContributionItem(fileToolBar,"analyzer_file");

		ToolBarManager editBar = new ToolBarManager(style);
		editBar.add(settingAction);
		editBar.add(sourceUpdateAction);
		editBar.add(updateDumpDataViewerAction);
		editBar.add(deleteFileAction);
		editBar.add(copyAction);
		editBar.add(pasteAction);
		editBar.add(setFileViewerVisibleAction);
		ToolBarContributionItem analyzer_edit =
				new ToolBarContributionItem(editBar,"analyzer_edit");

		ToolBarManager supportBar = new ToolBarManager(style);
		supportBar.add(addMonitorMethodTask);
		supportBar.add(threadMapAction);
		ToolBarContributionItem analyzer_support =
				new ToolBarContributionItem(supportBar,"analyzer_support");

		ToolBarManager searchBar = new ToolBarManager(style);
		searchBar.add(objectReferenceAction);
		searchBar.add(supportSearchAction);
		searchBar.add(sourecViewTextSearchAction);
		searchBar.add(javaSrcSearchAction);
		ToolBarContributionItem analyzer_search =
			new ToolBarContributionItem(searchBar,"analyzer_search");

		ToolBarManager navigationBar = new ToolBarManager(style);
		navigationBar.add(gotoDeclareAction);
		navigationBar.add(previousAction);
		navigationBar.add(forwordAction);
		ToolBarContributionItem analyzer_navigation =
				new ToolBarContributionItem(navigationBar,"navigation");

		ToolBarManager helpToolBar = new ToolBarManager(style);
		helpToolBar.add(licenseAction);
		helpToolBar.add(aboutAction);
		help = new ToolBarContributionItem(helpToolBar,"help");

		analyzer_toolBar[0] = analyzer_file;
		analyzer_toolBar[1] = analyzer_edit;
		analyzer_toolBar[2] = analyzer_support;
		analyzer_toolBar[3] = analyzer_search;
		analyzer_toolBar[4] = analyzer_navigation;
		analyzer_toolBar[5] = help;
	}
	/**
	 * 設定時のツールバー
	 * @param style
	 */
	private void createConfgCoolBarManager(int style) {

		ToolBarManager fileToolBar = new ToolBarManager(style);
		fileToolBar.add(createNewConfigAction);
		fileToolBar.add(openConfigFileAction);
		fileToolBar.add(saveAsNewConfigFileAction);
		fileToolBar.add(updateConfigAction);
		ToolBarContributionItem config_set = new ToolBarContributionItem(fileToolBar,"config_set");
		config_toolBar[0] = config_set;

		ToolBarManager editBar = new ToolBarManager(style);
		editBar.add(settingAction);
		editBar.add(sourceUpdateAction);
		editBar.add(copyAction);
		editBar.add(pasteAction);
		ToolBarContributionItem config_edit =
				new ToolBarContributionItem(editBar,"config_edit");
		config_toolBar[1] = config_edit;

		ToolBarManager taskBar = new ToolBarManager(style);
		editBar.add(addAutoMonitorExceptionTaskAction);
		editBar.add(addMonitorExceptionTaskAction);
		editBar.add(addMonitorMethodTaskAction);
		editBar.add(addMonitorSignalTaskAction);
		editBar.add(deleteTaskAction);
		ToolBarContributionItem config_task =
			new ToolBarContributionItem(taskBar,"config_task");
		config_toolBar[2] = config_task;
		config_toolBar[3] = analyzer_toolBar[5];

	}

	/**
	 * 「設定」、「分析」のタブを切りかえる場合、呼ばれる。
	 * @param tab 選択タブのID
	 */
	public void toolBarSelection(int tab){

		if (tab == 0){
			addCoolBarContributionItem(config_toolBar);
			addMenus(config_menus);
		}else{
			addCoolBarContributionItem(analyzer_toolBar);
			addMenus(analyzer_menus);
		}
	}

	private void addCoolBarContributionItem (ToolBarContributionItem[] items){
		getCoolBarManager().removeAll();
		getMenuBarManager().update(true);
		for(int i = 0; i < items.length; i++){
			getCoolBarManager().add(items[i]);
			getCoolBarManager().update(true);
		}
	}

	private void addMenus (MenuManager[] menus){
		getMenuBarManager().removeAll();
		getMenuBarManager().updateAll(true);
		for(int i = 0; i < menus.length; i++){
			getMenuBarManager().add(menus[i]);
			getMenuBarManager().updateAll(true);
		}
	}

	protected void showMenuBar(boolean show) {
		if (show) {
			this.getMenuBarManager().setVisible(true);
			if (this.getMenuBarManager() == null) {
				this.addMenuBar();
			}
		} else {
			this.getMenuBarManager().setVisible(false);
			if (this.getMenuBarManager() != null) {
				this.getMenuBarManager().removeAll();
			}
		}
		this.getMenuBarManager().update(true);
	}

	/**
	 * ダンプ結果を分析する時のメニュー
	 * @version 3.0
	 */
	private void createAnalyzerMenuManager(){
		MenuManager fileMenu = new MenuManager(ApplicationResource
				.getResource("Menu.File"));
		MenuManager editMenu = new MenuManager(ApplicationResource
				.getResource("Menu.edit"));

		MenuManager supportMenu = new MenuManager(ApplicationResource
				.getResource("Menu.support"));

		MenuManager searchMenu = new MenuManager(ApplicationResource
				.getResource("Menu.search"));

		MenuManager navigationMenu = new MenuManager(ApplicationResource
				.getResource("Menu.Navigation"));

		MenuManager helpMenu = new MenuManager(ApplicationResource
				.getResource("Menu.Help"));


		analyzer_menus[0] = fileMenu;
		analyzer_menus[1] = editMenu;
		analyzer_menus[2] = supportMenu;
		analyzer_menus[3] = searchMenu;
		analyzer_menus[4] = navigationMenu;
		analyzer_menus[5] = helpMenu;

		// file menu
		fileMenu.add(openDumpFileAction);
		fileMenu.add(separator);
		fileMenu.add(closeDumpFileAction);
		fileMenu.add(closeAllAction);
		fileMenu.add(separator);
		fileMenu.add(outputPdfAction);
		fileMenu.add(printAction);
		fileMenu.add(separator);
		fileMenu.add(exitAction);

		// edit menu
		editMenu.add(settingAction);
		editMenu.add(sourceUpdateAction);
		editMenu.add(updateDumpDataViewerAction);
		editMenu.add(deleteFileAction);
		editMenu.add(copyAction);
		editMenu.add(pasteAction);
		editMenu.add(setFileViewerVisibleAction);

		// support menu
		supportMenu.add(addMonitorMethodTask);
		supportMenu.add(threadMapAction);

		//search menu
		searchMenu.add(objectReferenceAction);
		searchMenu.add(supportSearchAction);
		searchMenu.add(sourecViewTextSearchAction);
		searchMenu.add(javaSrcSearchAction);

		//navigation
		navigationMenu.add(previousAction);
		navigationMenu.add(forwordAction);
		navigationMenu.add(gotoDeclareAction);


		// help menu
		helpMenu.add(licenseAction);
		helpMenu.add(aboutAction);
	}

	/**
	 * 設定編集時のメニュー
	 * @version 3.0
	 */
	private void createConfigMenuManager() {
		MenuManager fileMenu = new MenuManager(
				ApplicationResource.getResource("Menu.File.Text"));
		MenuManager editMenu = new MenuManager(
				ApplicationResource.getResource("Menu.Edit.Text"));
		MenuManager taskMenu = new MenuManager(
				ApplicationResource.getResource("Menu.Task"));

		fileMenu.add(createNewConfigAction);
		fileMenu.add(openConfigFileAction);
		fileMenu.add(saveAsNewConfigFileAction);
		fileMenu.add(updateConfigAction);
		fileMenu.add(exitAction);

		editMenu.add(settingAction);
		editMenu.add(sourceUpdateAction);
		editMenu.add(copyAction);
		editMenu.add(pasteAction);

		taskMenu.add(addAutoMonitorExceptionTaskAction);
		taskMenu.add(addMonitorExceptionTaskAction);
		taskMenu.add(addMonitorMethodTaskAction);
		taskMenu.add(addMonitorSignalTaskAction);
		taskMenu.add(deleteTaskAction);

		config_menus[0] = fileMenu;
		config_menus[1] = editMenu;
		config_menus[2] = taskMenu;
		config_menus[3] = analyzer_menus[5];
	}

	/**
	 * 新規タスク追加操作の処理
	 *
	 */
	public void addNewTask(int taskType){
		configMainForm.addNewTask(taskType);
		deleteTaskAction.setEnabled(true);
	}

	public void closeCurrentConfig(){
		ConfigModel.createNewConfig();
		configMainForm.moveAll();
		displayFilePath();
		updateConfigAction.setEnabled(false);
		saveAsNewConfigFileAction.setEnabled(false);
		addAutoMonitorExceptionTaskAction.setEnabled(false);
		addMonitorExceptionTaskAction.setEnabled(false);
		addMonitorMethodTaskAction.setEnabled(false);
		addMonitorSignalTaskAction.setEnabled(false);

	}

	public static void displayFilePath(){
		String word = ApplicationResource.getResource("Status.File.Text");
		String file = ConfigModel.getFilePath();
		if (file == null){
			win.setStatus(	word );
		}else{
			win.setStatus(	word + file);
		}
	}

	/**
	 * 新規設定ファイルの処理
	 *
	 */
	public void createNewConfig(){
		ConfigModel.removeAll();
		ConfigModel.createNewConfig();
		displayFilePath();
		configMainForm.createNewConfig();
		updateConfigAction.setEnabled(false);
		saveAsNewConfigFileAction.setEnabled(true);
		addAutoMonitorExceptionTaskAction.setEnabled(true);
		addMonitorExceptionTaskAction.setEnabled(true);
		addMonitorMethodTaskAction.setEnabled(true);
		addMonitorSignalTaskAction.setEnabled(true);
		configMainForm.configTree.getTree().setEnabled(true);
	}

	/**
	 * 既存設定ファイルの編集
	 * @param config_path
	 */
	public void openOldConfig(String config_path){
		ConfigModel.removeAll();
		ConfigModel.openOldConfig(config_path);
		displayFilePath();
		configMainForm.createOldConfig();
		updateConfigAction.setEnabled(true);
		saveAsNewConfigFileAction.setEnabled(true);
		addAutoMonitorExceptionTaskAction.setEnabled(true);
		addMonitorExceptionTaskAction.setEnabled(true);
		addMonitorMethodTaskAction.setEnabled(true);
		addMonitorSignalTaskAction.setEnabled(true);
		configMainForm.configTree.getTree().setEnabled(true);
	}

	public boolean checkItems(){
		return configMainForm.checkItems();
	}

	public boolean canDeleteThisTask(){
		return configMainForm.canDeleteThisTask();
	}

	public void deleteCurrentTask(){
		configMainForm.deleteCurrentTask();
		if (!configMainForm.hasTask()){
			deleteTaskAction.setEnabled(false);
		}
	}

	public ObjectReferenceAction getObjectReferenceAction(){
		return objectReferenceAction;
	}

	private void iniState(){
		deleteTaskAction.setEnabled(false);
		updateConfigAction.setEnabled(false);
		saveAsNewConfigFileAction.setEnabled(false);
		addAutoMonitorExceptionTaskAction.setEnabled(false);
		addMonitorExceptionTaskAction.setEnabled(false);
		addMonitorMethodTaskAction.setEnabled(false);
		addMonitorSignalTaskAction.setEnabled(false);
		configMainForm.configTree.getTree().setEnabled(false);
		displayFilePath();
	}

	public void setSourecViewTextSearchActionState(boolean state){
		sourecViewTextSearchAction.setEnabled(state);
	}

	public void selectConfig(){
		configMainForm.selectConfig();
		int index = tabFolder.getSelectionIndex();
		toolBarSelection(index);
	}
	//add by Qiu Song on 20100216 for StatusBarにエラーメッセージの表示
	private boolean isStatusErr = false;

	public void setStatusErr(boolean bErr){
		isStatusErr = bErr;
	}

	public void resetStatusErr(){
		if(win != null && win.isStatusErr == true){
			win.setStatusErr(false);
			win.setStatus("");
		}
	}
	//end of add by Qiu Song on 20100216 for StatusBarにエラーメッセージの表示

}