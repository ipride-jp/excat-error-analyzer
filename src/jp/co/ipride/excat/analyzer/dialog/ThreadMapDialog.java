package jp.co.ipride.excat.analyzer.dialog;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import jp.co.ipride.excat.MainViewer;
import jp.co.ipride.excat.analyzer.viewer.fileviewer.FileViewerImageUtil;
import jp.co.ipride.excat.common.ApplicationResource;
import jp.co.ipride.excat.common.icons.IconFilePathConstant;
import jp.co.ipride.excat.common.setting.SettingManager;
import jp.co.ipride.excat.common.utility.HelperFunc;
import jp.co.ipride.excat.common.utility.Utility;
import jp.co.ipride.excat.configeditor.util.ViewerUtil;
import jp.co.ipride.excat.common.setting.dialog.RootPathItem;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

/**
 * this dialog is to analyze threads.
 * @author tu-ipride
 * @version 3.0
 * @since 2009/10/4
 */
public class ThreadMapDialog  extends Dialog{

	private static int text_width    = 500;

	private Tree tree;

	private TreeItem treeRoot;

	private MainViewer appWindow;

	private Button saveBtn;

	private Button cancelBtn;

	private List<List<String>> data = new ArrayList<List<String>>();

	private File rootFile=null;
	private String filterWord = null;

	private static Image checkedBox;
	private static Image uncheckedBox;
	private static Image folder;

	static{
		URL url = MainViewer.class.getResource(IconFilePathConstant.CHECK_BOX);
		checkedBox = ImageDescriptor.createFromURL(url).createImage();
		url = MainViewer.class.getResource(IconFilePathConstant.UNCHECK_BOX);
		uncheckedBox = ImageDescriptor.createFromURL(url).createImage();
		url = MainViewer.class.getResource(IconFilePathConstant.FILE_TREE_FORDER);
		folder = ImageDescriptor.createFromURL(url).createImage();
	}
	/**
	 * construct
	 * @param parentShell  window shell
	 */
	public ThreadMapDialog(MainViewer appWindow, File rootFile) {
		super(appWindow.getShell());
		this.appWindow = appWindow;
		this.rootFile = rootFile;
		this.filterWord = appWindow.analyzerform.fileViewer.getFilter();
	}

	public List<List<String>> getData(){
		return data;
	}

	private void init(){
	}

	/**
	 * 表示処理
	 */
	private void displayTree(File topFile){
		tree.removeAll();
		treeRoot = new TreeItem(tree,SWT.NULL);
		treeRoot.setText(ApplicationResource.getResource("Dialog.ThreadMap.Root"));
		treeRoot.setImage(folder);
		if (topFile == null){
			ArrayList<RootPathItem> rootPathList = SettingManager.getSetting().getRootPathList();
			if (rootPathList != null){
				for (int i=0; i<rootPathList.size(); i++){
					RootPathItem rootPathItem = rootPathList.get(i);
					File file = new File(rootPathItem.getPath());
					if (file.isDirectory() && filterFolder(file)){
						TreeItem item = new TreeItem(treeRoot,SWT.NULL);
						item.setData(file);
						item.setText(file.getName());
						setImage(item);
						setChildrenTreeItem(item);
					}
				}
			}
		}else{
			TreeItem item = new TreeItem(treeRoot,SWT.NULL);
			item.setData(topFile);
			item.setText(topFile.getName());
			setImage(item);
			setChildrenTreeItem(item);
			item.setExpanded(true);
		}
		treeRoot.setExpanded(true);
	}

	private void setChildrenTreeItem(TreeItem root){
		File dir = (File)root.getData();
		File[] files = dir.listFiles();
		for (int i=0; i<files.length; i++){
			File file = files[i];
			if (file.isDirectory() && filterFolder(file)){
				TreeItem item = new TreeItem(root,SWT.NULL);
				item.setData(file);
				item.setText(file.getName());
				setImage(item);
				setChildrenTreeItem(item);
			}
		}
	}

	/**
	 * 終了後の処理
	 *　想定した構造：
	 *　　第1組
	 *　　　　スレッド１
	 *　　　　スレッド２
	 *　　第2組
	 *　　　　スレッド１
	 *　　　　スレッド２
	 */
	 private void updateToMode(){
		data.clear();
		 TreeItem item = tree.getItem(0);
		 List<TreeItem> list = extracCheckedItems(item);
		 for (int i=0; i<list.size(); i++){
			 TreeItem dirItem = list.get(i);
			 File dir = (File)dirItem.getData();
			 setTarget(dir);
		 }
	 }

	 /**
	  * チェックされたtreeitemを抽出
	  * @date 2009/10/9
	  */
	 private List<TreeItem> extracCheckedItems(TreeItem parentItem){
		 List<TreeItem> result = new ArrayList<TreeItem>();

		 if (isCheckedItem(parentItem)){
			 result.add(parentItem);
		 }

		 if (parentItem.getItemCount() > 0){
			 TreeItem[] items =parentItem.getItems();
			 for (int i=0; i<items.length; i++){
				 List<TreeItem> list = extracCheckedItems(items[i]);
				 result.addAll(list);
			 }
		 }
		 return result;
	 }

	 /**
	  * 指定のフォルダ配下のダンプ・ファイルを抽出
	  * @param folder
	  */
	 private void setTarget(File folder){
		 File[] files = folder.listFiles();
		 List<String> dumpFileList = new ArrayList<String>();
		 for (int i=0; i<files.length; i++){
			 File file = files[i];
			 if (HelperFunc.isDumpFile(file)){
				 dumpFileList.add(file.getPath());
			 }
		 }
		 data.add(dumpFileList);
	 }

	/**
	 * override
	 *
	 */
	protected Control createContents(Composite parent) {
		this.getShell().setText(
				ApplicationResource.getResource("Dialog.ThreadMap.Title"));
		URL url = MainViewer.class.getResource(IconFilePathConstant.EXCAT_TM_SMALL_16);
		this.getShell().setImage(ImageDescriptor.createFromURL(url).createImage());

		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(1, false);
		layout.marginTop = ViewerUtil.getMarginHeight(composite);
		layout.marginBottom = ViewerUtil.getMarginHeight(composite);
		layout.marginLeft = ViewerUtil.getMarginWidth(composite);
		layout.marginRight = ViewerUtil.getMarginWidth(composite);
		composite.setLayout(layout);

		createTable(composite);

		createSelectButton(composite);

		init();

		displayTree(rootFile);

		cancelBtn.setFocus();

		return composite;
	}

	/**
	 * テーブルを作成
	 * @param composite
	 */
	private void createTable(Composite composite) {

        Group tableGroup = new Group(composite, SWT.NONE);
        tableGroup.setText(
        		ApplicationResource.getResource("Dialog.ThreadMap.Select"));
        GridLayout layout = new GridLayout();
		layout.marginTop = ViewerUtil.getMarginHeight(composite);
		layout.marginBottom = ViewerUtil.getMarginHeight(composite);
		layout.marginLeft = ViewerUtil.getMarginWidth(composite);
		layout.marginRight = ViewerUtil.getMarginWidth(composite);
		tableGroup.setLayout(layout);
		tableGroup.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));

		tree = new Tree(tableGroup,  SWT.SINGLE | SWT.BORDER);
		GridData gridData = new GridData();
		gridData.widthHint = text_width;
		gridData.heightHint = 210;
		tree.setLayoutData(gridData);
		tree.addSelectionListener(new SelectionListener(){
			public void widgetDefaultSelected(SelectionEvent event) {
			}

			public void widgetSelected(SelectionEvent event) {
				TreeItem item = (TreeItem)event.item;
				if (item.getImage() != null){
					if (item.getImage() == checkedBox){
						item.setImage(uncheckedBox);
					}else if(item.getImage() == uncheckedBox){
						item.setImage(checkedBox);
					}
				}
				checkSaveState();
			}
		});
	}

	/**
	 * 実行ボタンを作成する
	 * @param composite
	 */
	private void createSelectButton(Composite composite) {

		Composite buttonForm = new Composite(composite,SWT.NONE);
		GridLayout buttonFormlayout = new GridLayout();
		buttonFormlayout.numColumns=3;
		buttonForm.setLayout(buttonFormlayout);
		GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_END);
		buttonForm.setLayoutData(gd);

		saveBtn = Utility.createButton(buttonForm, SWT.PUSH,
				ApplicationResource.getResource("Dialog.ThreadMap.Run"),
				Utility.BUTTON_WIDTH,1);

		cancelBtn = Utility.createButton(buttonForm, SWT.PUSH,
				ApplicationResource.getResource("Dialog.ThreadMap.Stop"),
				Utility.BUTTON_WIDTH,1);

		cancelBtn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				cancelPressed();
			}
		});

		saveBtn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				updateToMode();
				okPressed();
			}
		});
		saveBtn.setEnabled(false);
	}

	/**
	 * 初回時に候補のフォルダに「uncheckedBox」を追加
	 * @param item
	 */
	private void setImage(TreeItem item){
		File file = (File)item.getData();
		File[] children = file.listFiles();
		if (children.length==0){
			return;
		}
		for (File child: children){
			if (child.isDirectory()){
				item.setImage(folder);
				return;
			}
			if (!HelperFunc.isDumpFile(child)){
				return;
			}
		}
		item.setImage(uncheckedBox);
	}

	/**
	 * 選択されたItemを識別
	 * @param item
	 * @return
	 */
	private boolean isCheckedItem(TreeItem item){
		Image image = item.getImage();
//		if (image== null){
//			return false;
//		}else if (image == uncheckedBox){
//			return false;
//		}else{
//			return true;
//		}
		if (image == checkedBox) {
			return true;
		} else {
			return false;
		}
	}

	private void checkSaveState(){
		 TreeItem item = tree.getItem(0);
		 List<TreeItem> list = extracCheckedItems(item);
		 if (list.size()>0){
			 saveBtn.setEnabled(true);
		 }else{
			 saveBtn.setEnabled(false);
		 }
	}

	/**
	 * ファイル・ビューアと同一のアクション
	 * @param path
	 * @return
	 */
	private boolean filterFolder(File folder){
		if (filterWord == null || "".equals(filterWord)){
			return true;
		}else{
			if (folder.getPath().indexOf(this.filterWord) >= 0){
				return true;
			}else{
				File[] subFileList = folder.listFiles();
				if (subFileList == null){
					return false;
				}
				for (File file : subFileList){
					if(filterFolder(file)){
						return true;
					}
				}
			}
			return false;
		}
	}

}
