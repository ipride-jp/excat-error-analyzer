package jp.co.ipride.excat.configeditor.viewer.instance;

import jp.co.ipride.excat.common.ApplicationResource;
import jp.co.ipride.excat.common.utility.Utility;
import jp.co.ipride.excat.configeditor.model.ConfigContant;
import jp.co.ipride.excat.configeditor.util.ViewerUtil;
import jp.co.ipride.excat.configeditor.viewer.instance.table.ObjectTableViewer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class ObjectRegisterForm {

	private SashForm appWindow;
	private CTabFolder tabFolder;
	private CTabItem tabItem;
	private Composite form;
	private ObjectTableViewer objectTable;
	private Button deleteButton;
	private Button addButton;

	public ObjectRegisterForm(SashForm appWindow, CTabFolder tabFolder){
		this.appWindow=appWindow;
		this.tabFolder=tabFolder;
		tabItem = new CTabItem(tabFolder, SWT.NULL);

		//add by v3
		tabItem.setData(ConfigContant.Tree_Item_Object_Register);

		form = new Composite(tabFolder, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginTop = ViewerUtil.getMarginHeight(appWindow);
		layout.marginBottom = ViewerUtil.getMarginHeight(appWindow);
		layout.marginLeft = ViewerUtil.getMarginWidth(appWindow);
		layout.marginRight = ViewerUtil.getMarginWidth(appWindow);
		layout.numColumns=1;
		form.setLayout(layout);
		tabItem.setControl(form);
		createLayout();
		addListeners();
	}

	private void createLayout(){
		Label objectLabel = new Label(form, SWT.NONE);
		objectLabel.setText(
				ApplicationResource.getResource("Tab.Object.Title.Text"));
		objectTable = new ObjectTableViewer(form);

		Composite buttonForm = new Composite(form,SWT.NONE);
		GridLayout buttonFormlayout = new GridLayout();
		buttonFormlayout.numColumns=2;
		buttonForm.setLayout(buttonFormlayout);
		GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_END);
		buttonForm.setLayoutData(gd);

		addButton = Utility.createButton(buttonForm, SWT.PUSH,
				ApplicationResource.getResource("Tab.Button.Add.Text"),
				Utility.BUTTON_WIDTH,1);

		deleteButton = Utility.createButton(buttonForm, SWT.PUSH,
				ApplicationResource.getResource("Tab.Button.Delete.Text"),
				Utility.BUTTON_WIDTH,1);

	}

	private void addListeners(){
		addButton.addSelectionListener(new SelectionAdapter() {
 			public void widgetSelected(SelectionEvent e) {
 				objectTable.addNewObjectLine();
			}
		});
		deleteButton.addSelectionListener(new SelectionAdapter() {
 			public void widgetSelected(SelectionEvent e) {
 				objectTable.deleteObjectLine();
			}
		});
	}

	public boolean checkItems(){
		return objectTable.checkItems();
	}

}

