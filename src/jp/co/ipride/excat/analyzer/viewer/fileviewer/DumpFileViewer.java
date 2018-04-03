/*
 * Error Analyzer Tool for Java
 *
 * Created on 2007/10/9
 *
 * Copyright (c) 2006-2009 iPride Co.,Ltd.
 * All rights reserved.
 */
package jp.co.ipride.excat.analyzer.viewer.fileviewer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import jp.co.ipride.excat.MainViewer;
import jp.co.ipride.excat.common.ApplicationResource;
import jp.co.ipride.excat.common.setting.SettingManager;
import jp.co.ipride.excat.common.utility.ExcatMessageUtilty;
import jp.co.ipride.excat.configeditor.ExcatText;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.ibm.icu.util.StringTokenizer;

/**
 * ファイルビュー
 *
 * @author tatebayashiy
 *
 */
public class DumpFileViewer implements IDoubleClickListener {
	/** アプリケーションのトップレベルウィンドウ */
	private MainViewer appWindow;

	/** ツリービュー */
	private TreeViewer treeViewer;

	/** フィルター */
	private DumpFileViewerFilter filter;

	/** フィルター文字列 */
	private ExcatText filterText;

	/**
	 * コンストラクタ
	 *
	 * @param appWindow
	 *            アプリケーションウィンド
	 * @param parent
	 *            ツリービューを設定する対象となるコンポジット
	 * @param style
	 *            ツリービューのスタイル
	 */
	public DumpFileViewer(MainViewer appWindow, Composite parent, int style) {
		this.appWindow = appWindow;
		Composite viewerArea = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(1, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.marginLeft = 2;
		viewerArea.setLayout(layout);

		// フィルタリング入力領域のコンテンツ生成。
		createFilteringComposite(viewerArea);

		// ツリービューを生成。
		createTreeViewer(viewerArea, style);

	}

	public void createTreeViewer(Composite parent, int style) {
		treeViewer = new TreeViewer(parent, style|SWT.MULTI);
		treeViewer.getControl().addKeyListener(new KeyListener(){
			public void keyPressed(KeyEvent arg0) {
				if(arg0.keyCode == SWT.F5){
					updateTreeViewer();
				}
			}
			public void keyReleased(KeyEvent arg0) {
			}
		});
		treeViewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
		treeViewer.setContentProvider(new FileTreeContentProvider());
		treeViewer.setLabelProvider(new FileTreeLabelProvider());
		treeViewer.setComparer(new FileTreeItemComparer());
		filter = new DumpFileViewerFilter();
		treeViewer.addFilter(filter);
		treeViewer.addDoubleClickListener(this);
		treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(
					SelectionChangedEvent event) {
				StructuredSelection selection = (StructuredSelection) event.getSelection();
				List items = selection.toList();
				File[] files = new File[items.size()];
				for (int i = 0; i < items.size(); i++) {
					if (items.get(i) instanceof FileTreeRootItem) {
						appWindow.deleteFileAction.setFile(null);
						appWindow.deleteFileAction.setEnabled(false);
						return;
					}
					files[i] = ((BaseFileTreeItem)(items.get(i))).getFile();
				}
				appWindow.deleteFileAction.setFile(files);
				appWindow.deleteFileAction.setEnabled(true);
			}
		});

		// ルートを設定
		treeViewer.setInput(new FileTreeTopItem());

		//create context menu
		createContextMenu();
	}

	/**
	 * Context Menuの作成
	 *
	 */
	private void createContextMenu(){

		MenuManager menuMgr = new MenuManager("#DumpFileViewMenu");
		menuMgr.setRemoveAllWhenShown(true);

		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager mgr) {
				Tree tree = treeViewer.getTree();
				if(tree.getSelectionCount() == 1) {
					mgr.add(appWindow.updateDumpDataViewerAction);
					mgr.add(appWindow.deleteFileAction);
					mgr.add(appWindow.threadMapAction);
				} else {
					mgr.add(appWindow.updateDumpDataViewerAction);
					mgr.add(appWindow.deleteFileAction);
				}
			}
		});

		// Create menu.
		Menu menu = menuMgr.createContextMenu(treeViewer.getControl());
		treeViewer.getControl().setMenu(menu);

	}

	/** フィルタリング文字列入力領域のコンテンツを生成します。 */
	public void createFilteringComposite(Composite parent) {
		Composite area = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		area.setLayout(layout);

		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		area.setLayoutData(gd);

		gd = new GridData(GridData.FILL_HORIZONTAL);
		filterText = new ExcatText(area, SWT.SINGLE | SWT.BORDER);
		filterText.setLayoutData(gd);
		filterText.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent event) {
				if (event.character == SWT.CR) {
					doFiltering();
				}
			}
			public void keyReleased(KeyEvent event) {
			}
		});
		Button refactorButuon = new Button(area, SWT.PUSH);
		refactorButuon.setSize(35, 15);
		refactorButuon.setText(ApplicationResource.getResource("DumpFileViewer.Filter.Text"));

		refactorButuon.addSelectionListener(new SelectionListener(){
			public void widgetDefaultSelected(SelectionEvent arg0) {}

			public void widgetSelected(SelectionEvent arg0) {
				doFiltering();
			}
		});
	}

	/**
	 * ファイルビューの表示、非表示の切り替え
	 *
	 * @param visible
	 */
	public void setVisible(boolean visible) {
		treeViewer.getControl().getParent().setVisible(visible);
	}

	/**
	 * ダブルクリック時の処理。
	 * ディレクトリの開閉および、 ダンプファイルオープン処理。
	 * implement IDoubleClickListener
	 * @version 2.0
	 */
	public void doubleClick(DoubleClickEvent event) {
		StructuredSelection selection = (StructuredSelection) event.getSelection();
		BaseFileTreeItem item = (BaseFileTreeItem) selection.getFirstElement();
		File file = item.getFile();
		if (file != null) {
			if (file.isFile()) {
				String path = file.getPath();
				if (!isOpened(path)) {
					try {
						this.appWindow.openXmlFile(path);
					} catch (Exception e) {
						ExcatMessageUtilty.showErrorMessage(appWindow.getShell(),e);
					}
				}
			} else if (file.isDirectory()) {
				if (treeViewer.isExpandable(item)) {
					if (treeViewer.getExpandedState(item)) {
						treeViewer.setExpandedState(item, false);
					} else {
						treeViewer.setExpandedState(item, true);
					}
				}
			}
		}
	}


	/**
	 * ファイルビューワの更新
	 */
	public void updateTreeViewer() {
		appWindow.getShell().setCursor(new Cursor(null, SWT.CURSOR_WAIT));
		filter.setFilteringExtentions(SettingManager.getSetting().getFilterExtentions());
		treeViewer.refresh();
		appWindow.getShell().setCursor(null);
	}

	/**
	 * オープン対象となるダンプファイルが現在開かれているか取得します。
	 *
	 * @param path
	 * @return
	 */
	protected boolean isOpened(String path) {
		String openedFileName = ((MainViewer) appWindow).fileName;

		if (openedFileName != null && !"".equals(openedFileName)) {
			return openedFileName.equals(path);
		}
		return false;
	}

	/**
	 * フィルタリング処理を実行します。
	 */
	protected void doFiltering() {
		ArrayList<String> list = new ArrayList<String>();
		if (!"".equals(filterText.getText())) {
			list.add(filterText.getText());
		}
		filter.setFilteringStringList(list);
		treeViewer.refresh();
		FileTreeTopItem root = (FileTreeTopItem)treeViewer.getInput();
		if (!filterText.getText().equals("")) {
			treeViewer.collapseAll();
			expand(root);
		}
	}

	private boolean expand(BaseFileTreeItem item) {
		if (item.getChildren().length > 0) {
			boolean rtn = false;
			for (BaseFileTreeItem child : item.getChildren()) {
				if (expand(child)) {
					treeViewer.setExpandedState(item, true);
					if (!rtn) {
						rtn = true;
					}
				} else {
					File file = item.getFile();
					StringTokenizer tokenizer = new StringTokenizer(file.getPath(), "\\");
					String name = "";
					while (tokenizer.hasMoreElements()) {
						name = tokenizer.nextToken();
					}
					if (name.indexOf(filterText.getText()) >= 0) {
						if (!rtn) {
							rtn = true;
						}
					}
				}
			}
			return rtn;
		} else {
			File file = item.getFile();
			StringTokenizer tokenizer = new StringTokenizer(file.getPath(), "\\");
			String name = "";
			while (tokenizer.hasMoreElements()) {
				name = tokenizer.nextToken();
			}
			if (name.indexOf(filterText.getText()) >= 0) {
				return true;
			} else {
				return false;
			}
		}
	}

	public File getSelectPath(){
		TreeItem[] items = treeViewer.getTree().getSelection();
		if (items.length==0){
			return null;
		}
		Object item = items[0].getData();
		if (item instanceof FileTreeFileItem){
			return ((FileTreeFileItem)item).getFile();
		}else{
			return null;
		}
	}

	public String getFilter(){
		return filterText.getText();
	}

}
