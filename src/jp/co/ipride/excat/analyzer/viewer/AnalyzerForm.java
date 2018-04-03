package jp.co.ipride.excat.analyzer.viewer;

import java.net.URL;
import java.util.List;

import jp.co.ipride.ExcatLicenseException;
import jp.co.ipride.excat.MainViewer;
import jp.co.ipride.excat.analyzer.common.DumpFileXmlConstant;
import jp.co.ipride.excat.analyzer.viewer.fileviewer.DumpFileViewer;
import jp.co.ipride.excat.analyzer.viewer.localviewer.VariableTable;
import jp.co.ipride.excat.analyzer.viewer.propertyviewer.PropertyTable;
import jp.co.ipride.excat.analyzer.viewer.searchviewer.ConditionUnit;
import jp.co.ipride.excat.analyzer.viewer.searchviewer.TextSearchViewer;
import jp.co.ipride.excat.analyzer.viewer.sourceviewer.SourceViewerPlatform;
import jp.co.ipride.excat.analyzer.viewer.stackviewer.StackTree;
import jp.co.ipride.excat.analyzer.viewer.threadmap.AbstractThreadMapTable;
import jp.co.ipride.excat.analyzer.viewer.threadmap.ThreadMapTableTypeA;
import jp.co.ipride.excat.analyzer.viewer.threadmap.ThreadMapTableTypeB;
import jp.co.ipride.excat.common.ApplicationResource;
import jp.co.ipride.excat.common.Message;
import jp.co.ipride.excat.common.icons.IconFilePathConstant;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabFolder2Adapter;
import org.eclipse.swt.custom.CTabFolderEvent;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TreeItem;
import org.w3c.dom.Element;

public class AnalyzerForm{
	//「設定」、「分析」のタブ
	private CTabFolder tabFolder = null;
	//「分析」のアイテム
	private CTabItem tabItem = null;
	private SashForm platForm;
	private int[] weights = new int[] { 2, 3, 5 };
	private int oldLeftFormWeight;
	private boolean normalState = true;

	// ダンプファイルのツリー・ビューア
	public StackTree stackTree = null;

	// ソースビューア＆バイトコードビューア
	public SourceViewerPlatform sourceViewerPlatform = null;

	// ダンプファイルViewer
	public DumpFileViewer fileViewer = null;

	// 属性ビューア
	public PropertyTable propertyTable = null;

	public MainViewer mainvw;

	//use to display search result and byte-code property  v3
	public static CTabFolder otherTabFolder;

	//use to display search result. v3
	public static CTabItem searchTabItem;

	//use to display local var of byte-code.
	public static CTabItem varTabItem;

	//use to display local var of byte-code.
	public static VariableTable variableTable;

	//use to display thread-map
	public static CTabItem threadMapTabItem;

	public static AbstractThreadMapTable threadMapTable;
	private SashForm rightForm;

	/**
	 * construct
	 * @param view
	 * @param tabFolder
	 */
	public AnalyzerForm(MainViewer view ,CTabFolder tabFolder){
		this.mainvw = view;
		this.tabFolder=tabFolder;
		this.tabItem = new CTabItem(tabFolder, SWT.NULL, 0);
		this.tabItem.setText(ApplicationResource
				.getResource("tab.analyzer.Title"));

		URL url = MainViewer.class
				.getResource(IconFilePathConstant.TAB_ANALYZER);
		tabItem.setImage(ImageDescriptor.createFromURL(url).createImage());
		createBaseForm();
		otherTabFolder.addListener(SWT.MouseDoubleClick, new Listener() {
			public void handleEvent(Event event) {
				if (!otherTabFolder.getMaximized()) {
					//MainViewer.win.maximizeSourceViewer();
					maximizeOtherViewer();
					otherTabFolder.setMaximized(true);
				} else {
					//MainViewer.win.restoreSourceViewer();
					restoreOtherViewer();
					otherTabFolder.setMaximized(false);
				}
			}
		});
		otherTabFolder.setMaximizeVisible(true);
		otherTabFolder.setMinimizeVisible(false);
		otherTabFolder.addCTabFolder2Listener(new CTabFolder2Adapter() {
			public void minimize(CTabFolderEvent arg0) {
				// Viewer.win.mininizeSourceViewer();
				// tabFolder.setMinimized(true);
			}

			public void maximize(CTabFolderEvent arg0) {
				maximizeOtherViewer();
				otherTabFolder.setMaximized(true);
			}

			public void restore(CTabFolderEvent arg0) {
				restoreOtherViewer();
				otherTabFolder.setMinimized(false);
				otherTabFolder.setMaximized(false);
			}

		});

	}

	/**
	 * create form of analyzer tab.
	 */
	private void createBaseForm(){
		platForm = new SashForm(this.tabFolder, SWT.NONE);
		this.tabItem.setControl(platForm);
		SashForm moreLeftForm = new SashForm(platForm, SWT.NONE);
		moreLeftForm.setOrientation(SWT.VERTICAL);
		SashForm leftForm = new SashForm(platForm, SWT.NONE);
		leftForm.setOrientation(SWT.VERTICAL);
		rightForm = new SashForm(platForm, SWT.NONE);
		rightForm.setOrientation(SWT.VERTICAL);
		platForm.setWeights(weights);

		// dump file viewer
		fileViewer = new DumpFileViewer(this.mainvw, moreLeftForm, SWT.BORDER);

		// source viewer
		sourceViewerPlatform = new SourceViewerPlatform(this.mainvw, rightForm);

		// tree
		stackTree = new StackTree(this.mainvw, leftForm, SWT.BORDER);

		// property table
		propertyTable = new PropertyTable(leftForm, SWT.BORDER
				| SWT.FULL_SELECTION | SWT.HIDE_SELECTION);

		leftForm.setWeights(new int[] { 5, 2 });

		// use to display search result and byte-code property
		otherTabFolder = new CTabFolder(rightForm,SWT.BORDER );

		// other folder.
		otherTabFolder.setTabHeight(25);
		otherTabFolder.setSimple(false);
		rightForm.setWeights(new int[] { 5, 2 });

		// search item
		searchTabItem = new CTabItem(otherTabFolder, SWT.NONE);
		searchTabItem.setText(
	    		ApplicationResource.getResource("SearchView.Tab.Text"));

		// byte-code property item
		variableTable = new VariableTable(otherTabFolder, SWT.BORDER | SWT.V_SCROLL);
		varTabItem = new CTabItem(otherTabFolder, SWT.NONE);
		varTabItem.setText(
				ApplicationResource.getResource("COL_VARIABLE_TAB_NAME"));
		varTabItem.setControl(variableTable);
		sourceViewerPlatform.tabFolder.addSelectionListener(variableTable);

		//thread-map item
		threadMapTabItem = new CTabItem(otherTabFolder, SWT.NONE);
		threadMapTabItem.setText(
				ApplicationResource.getResource("Tab.ThreadMap.Name"));

	}

	/**
	 * ファイルビューワの表示状態を設定します。
	 *
	 * @param visible 表示するかどうか
	 */
	public void setFileViewerVisible(boolean visible) {
		fileViewer.setVisible(visible);
		if (visible) {
			int[] currentWeights = platForm.getWeights();
			platForm.setWeights(new int[] {oldLeftFormWeight, currentWeights[1], currentWeights[2] - oldLeftFormWeight});
		} else {
			int[] currentWeights = platForm.getWeights();
			oldLeftFormWeight = currentWeights[0];
			platForm.setWeights(new int[] { 0, currentWeights[1], currentWeights[0] + currentWeights[2] });
		}
	}

	public void maximizeSourceViewer() {
		if (normalState){
			weights = platForm.getWeights();
		}
		normalState = false;
		platForm.setWeights(new int[] { 0, 0, 1 });
	}

	public void restoreSourceViewer() {
		normalState = true;
		platForm.setWeights(weights);
	}

	public void maximizeOtherViewer() {
		if (normalState){
			weights = platForm.getWeights();
		}
		platForm.setWeights(new int[] { 0, 0, 1 });
		rightForm.setWeights(new int[] { 0, 1 });
	}

	public void restoreOtherViewer() {
		if (!normalState) {

		} else {
			normalState = true;
			platForm.setWeights(weights);
		}
		rightForm.setWeights(new int[] { 5, 2 });
	}

	/**
	 * @since 2009/9/22
	 */
	public static void showLocalVarTable(){
		otherTabFolder.setSelection(varTabItem);
	}

	// 選択したスレッドパスマップ
	public void setThreadPathMap(List<List<String>> threadPathList) {
		otherTabFolder.setSelection(threadMapTabItem);
		mainvw.getShell().setCursor(new Cursor(null, SWT.CURSOR_WAIT));
		if (threadPathList.size()==1){
			threadMapTable = new ThreadMapTableTypeA(otherTabFolder, SWT.BORDER | SWT.V_SCROLL);
		}else if (threadPathList.size()>1){
			threadMapTable = new ThreadMapTableTypeB(otherTabFolder, SWT.BORDER | SWT.V_SCROLL);
		}
		threadMapTable.setMainViewer(mainvw);
		threadMapTabItem.setControl(threadMapTable);
		threadMapTable.setThreadDumpData(threadPathList);
		mainvw.getShell().setCursor(new Cursor(null, SWT.CURSOR_ARROW));

	}

	/**
	 * オブジェクト参照の箇所の検索
	 * @param item
	 */
	public void searchReferenceObject(TreeItem item){
		try {
			mainvw.getShell().setCursor(new Cursor(null, SWT.CURSOR_WAIT));
			otherTabFolder.setSelection(searchTabItem);
			TextSearchViewer viewer = new TextSearchViewer(mainvw,otherTabFolder, SWT.BORDER);
			searchTabItem.setControl(viewer);
			ConditionUnit conditionUnit = new ConditionUnit();
			conditionUnit.setSearchType(ConditionUnit.SEARCH_OBJECT);
			Element node = (Element)item.getData();
			String objectId = node.getAttribute(DumpFileXmlConstant.ATTR_OBJECT_ID);
			conditionUnit.setObjectId(objectId);
			viewer.searchText(conditionUnit);
		} catch (ExcatLicenseException e) {
			TextViewer msgTextViewer = new TextViewer(otherTabFolder, SWT.NONE);
			msgTextViewer.setDocument(new Document());
			searchTabItem.setControl(msgTextViewer.getControl());
			String msg = e.getPath() + Message.get("SearchView.Tab.NoLicense");
			msgTextViewer.getDocument().set(msg);
		}finally{
			mainvw.getShell().setCursor(new Cursor(null, SWT.CURSOR_ARROW));
		}
	}

	/**
	 * テキスト検索
	 * @param conditionUnit
	 */
	public void searchText(ConditionUnit conditionUnit){
		try {
			mainvw.getShell().setCursor(new Cursor(null, SWT.CURSOR_WAIT));
			otherTabFolder.setSelection(searchTabItem);
			TextSearchViewer viewer = new TextSearchViewer(mainvw,otherTabFolder, SWT.BORDER);
			searchTabItem.setControl(viewer);
			viewer.searchText(conditionUnit);
		} catch (ExcatLicenseException e) {
			TextViewer msgTextViewer = new TextViewer(otherTabFolder, SWT.NONE);
			msgTextViewer.setDocument(new Document());
			searchTabItem.setControl(msgTextViewer.getControl());
			String msg = Message.get("SearchView.Tab.NoLicense");
			msgTextViewer.getDocument().set(msg);
		}finally{
			mainvw.getShell().setCursor(new Cursor(null, SWT.CURSOR_ARROW));
		}
	}

	public void closeLocalVarView(){
		variableTable.removeAll();
	}

}
