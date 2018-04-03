package jp.co.ipride.excat.common.dialog;

import java.io.File;
import java.io.IOException;

import jp.co.ipride.License;
import jp.co.ipride.excat.MainViewer;
import jp.co.ipride.excat.common.ApplicationResource;
import jp.co.ipride.excat.common.utility.ExcatMessageUtilty;
import jp.co.ipride.excat.common.utility.Utility;
import jp.co.ipride.excat.configeditor.ExcatText;
import jp.co.ipride.excat.configeditor.util.ViewerUtil;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * ライセンス登録画面
 * @author 盛申
 * @since 2006/12/20
 *         2006/12/24 登録ボタン、閉じるボタン、アイコン、アクションの追加
 *         2009/02/10 2009年2月現時点でopensslが対応している最大な日付
 */
public class LicenseDialog extends Dialog {

	//add by qiusong on 2009.02.10 for 2009年2月現時点でopensslが対応している最大な日付（タスク番号：343）
	private static final String MaxDate = "491231";
    //end of add by qiusong on 2009.02.10 for 2009年2月現時点でopensslが対応している最大な日付（タスク番号：343）

//	public static final int BUTTON_WIDTH = 70;
	public static final int HORIZONTAL_SPACING = 3;
	public static final int MARGIN_WIDTH = 0;
	public static final int MARGIN_HEIGHT = 2;

	private String newLicenseFile = null;

	private ExcatText licenseInfoText;

	private Button selectButton;
	private Button deleteButton;
	private Button closeButton;
	private Button registButton;

//	private static Font fntNormal10 = new Font(Display.getDefault(),
//			"MS PGothic", 10, SWT.NORMAL);

	/**
	 * construct
	 * @param parentShell  window shell
	 */
	public LicenseDialog(Shell parentShell) {
		super(parentShell);
	}

	/**
	 * override
	 *
	 */
	protected Control createContents(Composite parent) {
		getShell().setText(
				ApplicationResource.getResource("LicenseDialog.ShellText"));
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(1, false);
		layout.marginTop = ViewerUtil.getMarginHeight(composite);
		layout.marginBottom = ViewerUtil.getMarginHeight(composite);
		layout.marginLeft = ViewerUtil.getMarginWidth(composite);
		layout.marginRight = ViewerUtil.getMarginWidth(composite);
		composite.setLayout(layout);

		createLicenseContext(composite);

		addSpaceLine(composite, 10);

		createHorizotalLine(composite);

		Composite btmComposite = createRightAlignmentComposite(composite);

		createSelectButton(btmComposite);

		createRegistButton(btmComposite);

		createDeleteButton(btmComposite);

		createCloseButton(btmComposite);

		String licenseInfo = getLicenseInfo(License.licenseFile);

		if ("".equals(licenseInfo)) {
			showUnregisterInfo();
			registButton.setEnabled(false);
			deleteButton.setEnabled(false);
		} else {
			licenseInfoText.setText(licenseInfo);
			registButton.setEnabled(false);
			deleteButton.setEnabled(true);
		}

		closeButton.setFocus();

		return composite;
	}

	/**
	 *
	 *licenseInfoTextにライセンスの未登録情報を表示
	 */
	private void showUnregisterInfo(){

		licenseInfoText.setText(ApplicationResource
		        .getResource("LicenseDialog.TrialVersion"));
	}

	/**
	 * ライセンス・コンテクストを作成
	 * @param composite
	 */
	private void createLicenseContext(Composite parent) {
		licenseInfoText = new ExcatText(parent,
				SWT.MULTI | SWT.READ_ONLY | SWT.H_SCROLL | SWT.BORDER);
		licenseInfoText.setBackground(this.getShell().getDisplay()
				.getSystemColor(SWT.COLOR_WHITE));
		GridData data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = true;
		data.horizontalSpan = 2;
		data.widthHint = 500;
		data.heightHint = 180;
		licenseInfoText.setLayoutData(data);
	}

	/**
	 * 分割線を作成
	 * @param c
	 * @return
	 */
	private Label createHorizotalLine(Composite composite) {
		Label line = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);
		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		line.setLayoutData(data);
		return line;
	}

	/**
	 * ボタン・パーネルを作成
	 * @param composite　画面composite
	 * @return
	 */
	private Composite createRightAlignmentComposite(Composite composite) {
		Composite c = new Composite(composite, SWT.NONE);
		GridLayout layout = new GridLayout(4, false);
		layout.horizontalSpacing = HORIZONTAL_SPACING;
		layout.marginWidth = MARGIN_WIDTH;
		layout.marginHeight = MARGIN_HEIGHT;
		c.setLayout(layout);
		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_END);
		c.setLayoutData(data);
		return c;
	}

	/**
	 * 選択ボタンを作成する
	 * @param composite
	 */
	private void createSelectButton(Composite composite) {
		selectButton = Utility.createButton(composite, SWT.PUSH,
				ApplicationResource.getResource("LicenseDialog.Select.Button"),
				Utility.BUTTON_WIDTH,1);
		selectButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				LicenseDialog.this.selectLicenseFile();
			}
		});
	}

	/**
	 * 削除ボタンを構築
	 * @param composite
	 */
	private void createDeleteButton(Composite composite) {
		deleteButton = Utility.createButton(composite, SWT.PUSH,
				ApplicationResource.getResource("LicenseDialog.Delete.Button"),
				Utility.BUTTON_WIDTH,1);
		deleteButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {

				boolean confirm = ExcatMessageUtilty.showConfirmDialogBox(
						LicenseDialog.this.getShell(),
						ApplicationResource.getResource("LicenseDialog.Delete.Message.Text"));

				if (confirm) {
					License.delLicenseFile();
					showUnregisterInfo();
					MainViewer.win.resetTitle();
					registButton.setEnabled(false);
					deleteButton.setEnabled(false);
				}
			}
		});

	}

	/**
	 * 閉じるボタンを作成
	 * @param composite
	 */
	private void createCloseButton(Composite composite) {
		closeButton = Utility.createButton(composite, SWT.PUSH,
				ApplicationResource.getResource("LicenseDialog.Close.Button"),
				Utility.BUTTON_WIDTH,1);
		closeButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				close();
			}
		});
	}

	/**
	 * 登録ボタンを作成する
	 * @param composite
	 */
	private void createRegistButton(Composite composite) {
		registButton = Utility.createButton(composite, SWT.PUSH,
				ApplicationResource.getResource("LicenseDialog.Regist.Button"),
				Utility.BUTTON_WIDTH,1);
		registButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				copyLicenseFile();
				close();
			}
		});
	}

	/**
	 * ライセンス情報を取得
	 * @return
	 */
	private String getLicenseInfo(String filePath) {
		StringBuffer sb = new StringBuffer();

		File licenseFile = new File(filePath);
		if (!licenseFile.exists()) {
			return "";
		}
		String subject = License.getSubject(filePath);
		if (subject == null) {
			return "";
		}
		String[] str = subject.split("/");
		for (int i = 0; i < str.length; i++) {
			if (str[i] == null || "".equals(str[i])) {
				continue;
			}
			int idx = str[i].indexOf("=");
			if (idx != -1) {
				String key = str[i].substring(0, idx);
				String value = str[i].substring(idx + 1, str[i].length());
				sb.append(ApplicationResource.getResource("License.Subject."
						+ key));
				sb.append(":");
				sb.append(value);
				sb.append("\n");
			}
		}
		sb.append(ApplicationResource
				.getResource("License.Subject.effectiveStartDay"));
		sb.append(License.getStartDate(filePath));
		sb.append("\n");

		//add by qiusong on 2009.02.10 for OPENSSL日付の件
		String strEndData = License.getEndDate(filePath);
		int nDataIndex = strEndData.indexOf(LicenseDialog.MaxDate);
		if(nDataIndex <0){
			sb.append(ApplicationResource
						.getResource("License.Subject.ineffectiveStartDay"));
			sb.append(strEndData);
			sb.append("\n");
		}
		//end of add by qiusong on 2009.02.10 for OPENSSL日付の件
		sb.append(ApplicationResource.getResource("License.Subject.version"));

		String versionInfo = License.getAppVersion(filePath);
		String versionToShow = versionInfo;
		int index = versionInfo.indexOf("Ccat");
		if(index >=0){
			//4 is the length of Ccat
			versionToShow = versionInfo.substring(index + 4);
		}
		sb.append(versionToShow);

		return sb.toString();
	}

	/**
	 * 選択ボタンの選択
	 *
	 */
	private void selectLicenseFile() {
		FileDialog dialog = new FileDialog(this.getShell(), SWT.OPEN);
		String[] exts = { "*.pem" };
		String[] names = { "*.pem" };
		dialog.setFilterExtensions(exts);
		dialog.setFilterNames(names);

		String path = dialog.open();
		if (path == null) {
			return;
		}
		if (License.isValidFile(path, License.verStr)) {
			newLicenseFile = path;
			registButton.setEnabled(true);
			deleteButton.setEnabled(false);
			licenseInfoText.setText(getLicenseInfo(newLicenseFile));
		} else {
			ExcatMessageUtilty.showMessage(
					this.getShell(),
					ApplicationResource.getResource("LicenseDialog.Select.Message.Text"));
		}
	}

	/**
	 * ライセンス・ファイルを配下にコピーする。
	 *
	 */
	private void copyLicenseFile() {
		try {
			License.copyLicenseFile(newLicenseFile);
			License.setHasValidLicense(true);
			MainViewer.win.resetTitle();
		} catch (IOException e) {
			ExcatMessageUtilty.showMessage(
					this.getShell(),
					e.getMessage());
		}
	}

	private void addSpaceLine(Composite composite,int spaceHeight){
		Composite spaceComposite = new Composite(composite, SWT.NONE);
        GridData spaceGridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        spaceGridData.heightHint = spaceHeight;
        spaceComposite.setLayoutData(spaceGridData);
	}
}
