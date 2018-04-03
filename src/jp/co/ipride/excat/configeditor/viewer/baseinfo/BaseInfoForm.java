package jp.co.ipride.excat.configeditor.viewer.baseinfo;


import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import jp.co.ipride.excat.common.ApplicationResource;
import jp.co.ipride.excat.common.Message;
import jp.co.ipride.excat.common.utility.ExcatMessageUtilty;
import jp.co.ipride.excat.configeditor.ExcatText;
import jp.co.ipride.excat.configeditor.model.ConfigContant;
import jp.co.ipride.excat.configeditor.model.ConfigModel;
import jp.co.ipride.excat.configeditor.model.baseinfo.BaseInfo;
import jp.co.ipride.excat.configeditor.util.ViewerUtil;

/**
 * 基本情報画面
 * @author tu
 * @since 2007/11/16
 */
public class BaseInfoForm {

	private SashForm appWindow;
	private CTabFolder tabFolder;
	private CTabItem tabItem;

	private ExcatText company;
	private Button companyCheck;
	private ExcatText system;
	private Button systemCheck;
	private ExcatText comment;
	private Button commentCheck;
	private ExcatText dumpFileDir;
	private ExcatText dumpFilePrefix;
	private ExcatText dumpFileMaxSize;
	private Button dumpFileSizeCheck;
	private ExcatText logFileDir;
	private Combo logLevel;
	private Button mailSetting;
	private ExcatText mailServer;
	private ExcatText mailPort;
	private ExcatText mailAccount;
	private ExcatText mailPassword;
	private Button mailTemplateCheck;
	private ExcatText mailTemplate;
	private ExcatText mailSubject;
	private ExcatText mailFrom;
	private ExcatText mailFromName;
	private ExcatText mailTo;
	private Button mailIsAuth;
	private ExcatText autoCheckTime;
	private ExcatText minDiskSpace;
	private Button duplicationCheck;
	private Button diffThread;
	private ExcatText timeLimit;

	private BaseInfo baseInfo;

	private Label mailServerLabel;
	private Label mailPortLabel;
	private Label isAuthLabel;
	private Label mailAccountLabel;
	private Label mailPasswordLabel;
	private Label mailFromLabel;
	private Label mailTemplateLabel;
	private Label mailFromNameLabel;
	private Label mailToLabel;
	private Label mailSubjectLabel;

	private Label dumpDirLabel;
	private Label logDirLabel;
	private Label autCheckTimeLabel;
	private Label dumpFileSizeLimitLabel;
	private Label minDiskSpaceLabel;
	private Label diffThreadLabel;
	private Label timeLimitLabel;
	private Label prefixLabel;

	private Color red;
	private Color background;
	private Color white;

	/**
	 *
	 * @param appWindow
	 * @param parent
	 */
	public BaseInfoForm(SashForm appWindow, CTabFolder tabFolder){

		this.appWindow = appWindow;
		this.tabFolder = tabFolder;
		this.red = appWindow.getShell().getDisplay().getSystemColor(SWT.COLOR_RED);
		this.background = appWindow.getShell().getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);
		this.white = appWindow.getShell().getDisplay().getSystemColor(SWT.COLOR_WHITE);

		tabItem = new CTabItem(this.tabFolder, SWT.NULL);
		tabItem.setData(ConfigContant.Tree_Item_BaseInfo);

		createBaseForm();
		init();
		addListeners();
	}

	private void createBaseForm(){

		ScrolledComposite scroller = new ScrolledComposite(tabFolder, SWT.H_SCROLL | SWT.V_SCROLL);
		scroller.setMinSize(
				ViewerUtil.getScrollMinRectangle(appWindow).width,
				ViewerUtil.getScrollMinRectangle(appWindow).height);
		scroller.setExpandHorizontal(true);
		scroller.setExpandVertical(true);
		tabItem.setControl(scroller);

		Composite form = new Composite(scroller, SWT.NONE);
		scroller.setContent(form);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginLeft=ViewerUtil.getMarginWidth(appWindow);
		layout.marginRight=ViewerUtil.getMarginWidth(appWindow);
		layout.marginTop=ViewerUtil.getMarginHeight(appWindow);
		layout.marginBottom=ViewerUtil.getMarginHeight(appWindow);
		form.setLayout(layout);
        GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
        form.setLayoutData(gridData);

		createLeftLayout(form);
		createRightLayout(form);
	}

	/**
	 * 初期化
	 */
	private void init(){

		this.baseInfo = ConfigModel.getBaseInfo();
		setDataToDisplay();
		if (mailSetting.getSelection()){
			setMail();
		}else{
			setNoMail();
		}
		if (duplicationCheck.getSelection()){
			limitDuplication();
		}else{
			notLimitDuplication();
		}
		if (mailTemplateCheck.getSelection()){
			mailTemplate.setEnabled(true);
		}else{
			mailTemplate.setEnabled(false);
		}
		if (dumpFileSizeCheck.getSelection()){
			dumpFileMaxSize.setEnabled(true);
		}else{
			dumpFileMaxSize.setEnabled(false);
		}
		if ("".equals(company.getText())){
			companyCheck.setSelection(false);
			company.setEnabled(false);
		}else{
			companyCheck.setSelection(true);
			company.setEnabled(true);
		}
		if ("".equals(system.getText())){
			systemCheck.setSelection(false);
			system.setEnabled(false);
		}else{
			systemCheck.setSelection(true);
			system.setEnabled(true);
		}
		if ("".equals(comment.getText())){
			commentCheck.setSelection(false);
			comment.setEnabled(false);
		}else{
			commentCheck.setSelection(true);
			comment.setEnabled(true);
		}

	}

	private void createLeftLayout(Composite form){
		int spaceHeight = ViewerUtil.getMarginHeight(appWindow);

		//create left plate
		Composite leftForm = new Composite(form,SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginLeft = ViewerUtil.getMarginWidth(appWindow);
		layout.marginRight = ViewerUtil.getMarginWidth(appWindow);
		layout.marginTop = ViewerUtil.getMarginHeight(appWindow);
		layout.marginBottom = ViewerUtil.getMarginHeight(appWindow);
		layout.numColumns=1;
		leftForm.setLayout(layout);
        GridData gridData = new GridData();
        gridData.widthHint =ViewerUtil.getConfigPlateWidth(appWindow);
        leftForm.setLayoutData(gridData);

        //基本情報定義のエリア
        addSystemInfoArea(leftForm);
        addSpaceLine(leftForm,spaceHeight);

        //ダンプファイルのエリア
        addDumpFileArea(leftForm);
        addSpaceLine(leftForm,spaceHeight);

        //ログファイルのエリア
        addLogFileArea(leftForm);
        addSpaceLine(leftForm,spaceHeight);

        //ハードウェアの容量チェック
        addLimitHardDiskArea(leftForm);
        addSpaceLine(leftForm,spaceHeight);

        //コンフィグ・ファイルの自動チェック
        addAutoCheckArea(leftForm);

	}

	private void addSystemInfoArea(Composite leftForm){
		Group group = new Group(leftForm, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns=3;
		layout.marginLeft=ViewerUtil.getMarginWidth(appWindow);
		layout.marginRight=ViewerUtil.getMarginWidth(appWindow);
		layout.marginTop=ViewerUtil.getMarginHeight(appWindow);
		layout.marginBottom=ViewerUtil.getMarginHeight(appWindow);
		group.setLayout(layout);
		GridData gridData= new GridData(GridData.FILL_BOTH );
		group.setLayoutData(gridData);
		group.setText(
				ApplicationResource.getResource("Tab.BasicInfo.BasicInfo.Text"));

		//会社名
		Label companyLabel = new Label(group, SWT.NONE);
		ViewerUtil.setControlWidth(companyLabel,75);
		companyCheck = new Button(group, SWT.CHECK);
		companyLabel.setText(
				ApplicationResource.getResource("Tab.BasicInfo.Company.Text"));
		company = new ExcatText(group,SWT.LEFT | SWT.SINGLE | SWT.BORDER);
		company.setLayoutData(new GridData(GridData.FILL_HORIZONTAL ));

		//システム名
		Label systemLabel = new Label(group, SWT.NONE);
		systemLabel.setText(
				ApplicationResource.getResource("Tab.BasicInfo.System.Text"));
		systemCheck = new Button(group, SWT.CHECK);
		system = new ExcatText(group,SWT.LEFT | SWT.SINGLE | SWT.BORDER);
		system.setLayoutData(new GridData(GridData.FILL_HORIZONTAL ));

		//補足説明
		Label commentLabel = new Label(group, SWT.NONE);
		commentLabel.setText(
				ApplicationResource.getResource("Tab.BasicInfo.Comment.Text"));
		commentCheck = new Button(group, SWT.CHECK);
		comment = new ExcatText(group,SWT.LEFT | SWT.SINGLE | SWT.BORDER);
		comment.setLayoutData(new GridData(GridData.FILL_HORIZONTAL ));

	}

	private void addDumpFileArea(Composite leftForm){
		Group group = new Group(leftForm, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns=4;
		layout.marginTop=ViewerUtil.getMarginHeight(appWindow);
		layout.marginBottom=ViewerUtil.getMarginHeight(appWindow);
		layout.marginLeft=ViewerUtil.getMarginWidth(appWindow);
		layout.marginRight=ViewerUtil.getMarginWidth(appWindow);
		group.setLayout(layout);
		group.setLayoutData(new GridData(GridData.FILL_BOTH ));
		group.setText(
				ApplicationResource.getResource("Tab.BasicInfo.DumpFile.Text"));

		dumpDirLabel = new Label(group, SWT.NONE);
		dumpDirLabel.setText(
				ApplicationResource.getResource("Tab.BasicInfo.DumpFile.Dir.Text"));
		dumpFileDir = new ExcatText(group,SWT.LEFT | SWT.SINGLE | SWT.BORDER);
		dumpFileDir.jointHorizontalSpan(3);

		prefixLabel = new Label(group, SWT.NONE);
		prefixLabel.setText(
				ApplicationResource.getResource("Tab.BasicInfo.DumpFile.PreFix.Text"));
		dumpFilePrefix = new ExcatText(group,SWT.LEFT | SWT.SINGLE | SWT.BORDER);
		dumpFilePrefix.jointHorizontalSpan(3);

		dumpFileSizeLimitLabel = new Label(group, SWT.NONE);
		dumpFileSizeLimitLabel.setText(
				ApplicationResource.getResource("Tab.BasicInfo.DumpFile.SizeLimit.Text"));
		dumpFileSizeCheck = new Button(group,SWT.CHECK);
		dumpFileMaxSize = new ExcatText(group,SWT.RIGHT | SWT.SINGLE | SWT.BORDER);
		dumpFileMaxSize.setControlWidth(50);

		Label sizeUnitLabel = new Label(group,SWT.NONE);
		sizeUnitLabel.setText(
				ApplicationResource.getResource("Tab.BasicInfo.DumpFile.SizeLimit.Unit.Text"));

	}

	private void addLogFileArea(Composite leftForm){
		Group group = new Group(leftForm, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns=2;
		layout.marginTop=ViewerUtil.getMarginHeight(appWindow);
		layout.marginBottom=ViewerUtil.getMarginHeight(appWindow);
		layout.marginLeft=ViewerUtil.getMarginWidth(appWindow);
		layout.marginRight=ViewerUtil.getMarginWidth(appWindow);
		group.setLayout(layout);
		group.setLayoutData(new GridData(GridData.FILL_BOTH ));
		group.setText(
				ApplicationResource.getResource("Tab.BasicInfo.Log.Text"));

		logDirLabel = new Label(group, SWT.NONE);
		logDirLabel.setText(
				ApplicationResource.getResource("Tab.BasicInfo.Log.Dir.Text"));
		logFileDir = new ExcatText(group,SWT.LEFT | SWT.SINGLE | SWT.BORDER);
		logFileDir.setLayoutData(new GridData(GridData.FILL_HORIZONTAL ));

		Label logLevelLabel = new Label(group, SWT.NONE);
		logLevelLabel.setText(
				ApplicationResource.getResource("Tab.BasicInfo.Log.Level.Text"));
		logLevel = new Combo(group,SWT.DROP_DOWN | SWT.READ_ONLY);
		for (int i=0; i<BaseInfo.LOG_LEVEL.length; i++){
			logLevel.add(BaseInfo.LOG_LEVEL[i]);
		}
//		group.setTabList(new Control[]{
//				logFileDir.getTextBox(),
//				logLevel
//		});
	}

	private void addLimitHardDiskArea(Composite leftForm){
		Group group = new Group(leftForm, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginTop=ViewerUtil.getMarginHeight(appWindow);
		layout.marginBottom=ViewerUtil.getMarginHeight(appWindow);
		layout.marginLeft=ViewerUtil.getMarginWidth(appWindow);
		layout.marginRight=ViewerUtil.getMarginWidth(appWindow);
		layout.numColumns=3;
		group.setLayout(layout);
		group.setLayoutData(new GridData(GridData.FILL_BOTH ));
		group.setText(
				ApplicationResource.getResource("Tab.BasicInfo.Hardware.Limit.Text"));

		minDiskSpaceLabel = new Label(group, SWT.NONE);
		minDiskSpaceLabel.setText(
				ApplicationResource.getResource("Tab.BasicInfo.Hardware.Limit.Check.Text"));
		minDiskSpace = new ExcatText(group,SWT.RIGHT | SWT.SINGLE | SWT.BORDER);
		minDiskSpace.setControlWidth(50);

        Label unitLabel = new Label(group, SWT.NONE);
		unitLabel.setText(
				ApplicationResource.getResource("Tab.BasicInfo.Hardware.Limit.Check.Unit.Text"));
	}

	private void addAutoCheckArea(Composite leftForm){
		Group group = new Group(leftForm, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns=3;
		layout.marginTop=ViewerUtil.getMarginHeight(appWindow);
		layout.marginBottom=ViewerUtil.getMarginHeight(appWindow);
		layout.marginLeft=ViewerUtil.getMarginWidth(appWindow);
		layout.marginRight=ViewerUtil.getMarginWidth(appWindow);
		group.setLayout(layout);
		group.setLayoutData(new GridData(GridData.FILL_BOTH ));
		group.setText(
				ApplicationResource.getResource("Tab.BasicInfo.Check.Text"));

		autCheckTimeLabel = new Label(group, SWT.NONE);
		ViewerUtil.setControlWidth(autCheckTimeLabel,73);
		autCheckTimeLabel.setText(
				ApplicationResource.getResource("Tab.BasicInfo.Check.Time.Text"));
		autoCheckTime = new ExcatText(group,SWT.RIGHT | SWT.SINGLE | SWT.BORDER);
		autoCheckTime.setControlWidth(50);
		Label label = new Label(group, SWT.NONE);
		label.setText(
				ApplicationResource.getResource("Tab.BasicInfo.Check.Unit.Text"));
	}

	private void createRightLayout(Composite form){
		int spaceHeight = ViewerUtil.getMarginHeight(appWindow);
		Composite rightForm = new Composite(form,SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginLeft=ViewerUtil.getMarginWidth(appWindow);
		layout.marginRight=ViewerUtil.getMarginWidth(appWindow);
		layout.marginTop=ViewerUtil.getMarginHeight(appWindow);
		layout.marginBottom=ViewerUtil.getMarginHeight(appWindow);
		layout.numColumns=1;
		rightForm.setLayout(layout);
        GridData rightFormGridData = new GridData();
        rightFormGridData.widthHint = ViewerUtil.getConfigPlateWidth(appWindow);
        rightForm.setLayoutData(rightFormGridData);

		//メール設定のエリア
		addMailArea(rightForm);
        addSpaceLine(rightForm,spaceHeight);

        //重複制限
        addDuplicatCheckArea(rightForm);
	}

	private void addMailArea(Composite rightForm){
		//メール設定有無選択
		mailSetting = new Button(rightForm,SWT.CHECK);
		mailSetting.setText(
				ApplicationResource.getResource("Tab.BasicInfo.Mail.Text"));

		Group group = new Group(rightForm, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns=3;
		layout.marginTop=ViewerUtil.getMarginHeight(appWindow);
		layout.marginBottom=ViewerUtil.getMarginHeight(appWindow);
		layout.marginLeft=ViewerUtil.getMarginWidth(appWindow);
		layout.marginRight=ViewerUtil.getMarginWidth(appWindow);
		group.setLayout(layout);
		GridData mailGroupGridData= new GridData(GridData.FILL_BOTH );
		mailGroupGridData.widthHint = ViewerUtil.getConfigPlateWidth(appWindow);
		group.setLayoutData(mailGroupGridData);

        //送信者名
        mailFromNameLabel = new Label(group, SWT.NONE);
        mailFromNameLabel.setText(
				ApplicationResource.getResource("Tab.BasicInfo.Mail.FromName.Text"));
		mailFromName = new ExcatText(group,SWT.LEFT | SWT.SINGLE | SWT.BORDER);
		mailFromName.jointHorizontalSpan(2);

        //送信者アドレス
        mailFromLabel = new Label(group, SWT.NONE);
        mailFromLabel.setText(
				ApplicationResource.getResource("Tab.BasicInfo.Mail.From.Text"));
		mailFrom = new ExcatText(group,SWT.LEFT | SWT.SINGLE | SWT.BORDER);
		mailFrom.jointHorizontalSpan(2);

        //送信先アドレス
        mailToLabel = new Label(group, SWT.NONE);
        mailToLabel.setText(
				ApplicationResource.getResource("Tab.BasicInfo.Mail.To.Text"));
		mailTo = new ExcatText(group,SWT.LEFT | SWT.SINGLE | SWT.BORDER);
		mailTo.jointHorizontalSpan(2);

        //送信タイトル
        mailSubjectLabel = new Label(group, SWT.NONE);
        mailSubjectLabel.setText(
				ApplicationResource.getResource("Tab.BasicInfo.Mail.Subject.Text"));
		mailSubject = new ExcatText(group,SWT.LEFT | SWT.SINGLE | SWT.BORDER);
		mailSubject.jointHorizontalSpan(2);

		//送信文のテンプレート
		mailTemplateLabel = new Label(group, SWT.NONE);
		mailTemplateLabel.setText(
				ApplicationResource.getResource("Tab.BasicInfo.Mail.Template.Text"));
		mailTemplateCheck = new Button(group, SWT.CHECK);
		mailTemplate = new ExcatText(group,SWT.LEFT | SWT.SINGLE | SWT.BORDER);
		mailTemplate.setLayoutData(new GridData(GridData.FILL_HORIZONTAL ));

		//送信サーバー
        mailServerLabel = new Label(group, SWT.NONE);
		mailServerLabel.setText(
				ApplicationResource.getResource("Tab.BasicInfo.Mail.Server.Text"));
		mailServer = new ExcatText(group,SWT.LEFT | SWT.SINGLE | SWT.BORDER);
		mailServer.jointHorizontalSpan(2);

        //送信サーバーのポート番号
		mailPortLabel = new Label(group, SWT.NONE);
		mailPortLabel.setText(
				ApplicationResource.getResource("Tab.BasicInfo.Mail.Port.Text"));
		mailPort = new ExcatText(group,SWT.RIGHT | SWT.SINGLE | SWT.BORDER);
		mailPort.jointHorizontalSpan(2);

		//認証有無
		isAuthLabel = new Label(group, SWT.NONE);
		isAuthLabel.setText(
				ApplicationResource.getResource("Tab.BasicInfo.Mail.IsAuth.Text"));
		mailIsAuth = new Button(group, SWT.CHECK);
		ViewerUtil.jointHorizontalSpan(mailIsAuth,2);

		//認証のアカウント
		mailAccountLabel = new Label(group, SWT.NONE);
		mailAccountLabel.setText(
				ApplicationResource.getResource("Tab.BasicInfo.Mail.Account.Text"));
		mailAccount = new ExcatText(group,SWT.LEFT | SWT.SINGLE | SWT.BORDER);
		mailAccount.jointHorizontalSpan(2);

		//認証のパスワード
		mailPasswordLabel = new Label(group, SWT.NONE);
		mailPasswordLabel.setText(
				ApplicationResource.getResource("Tab.BasicInfo.Mail.Password.Text"));
		//modified by Qiu Song on 20091124 for バグ:532
		mailPassword = new ExcatText(group,SWT.LEFT | SWT.SINGLE | SWT.BORDER | SWT.PASSWORD);
		//end of modified by Qiu Song on 20091124 for バグ:532
		mailPassword.jointHorizontalSpan(2);
	}

	private void addDuplicatCheckArea(Composite rightForm){
        duplicationCheck=new Button(rightForm, SWT.CHECK);
        duplicationCheck.setText(
				ApplicationResource.getResource("Tab.BasicInfo.Duplication.Limit.Text"));
        Group group = new Group(rightForm, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns=3;
		layout.marginTop = ViewerUtil.getMarginHeight(appWindow);
		layout.marginBottom = ViewerUtil.getMarginHeight(appWindow);
		layout.marginLeft = ViewerUtil.getMarginWidth(appWindow);
		layout.marginRight = ViewerUtil.getMarginWidth(appWindow);
		group.setLayout(layout);
		group.setLayoutData(new GridData(GridData.FILL_BOTH ));

		diffThreadLabel =  new Label(group,SWT.NONE);
		diffThreadLabel.setText(
				ApplicationResource.getResource("Tab.BasicInfo.Duplication.Limit.DiffThread.Text"));
		diffThread = new Button(group, SWT.CHECK);
		ViewerUtil.jointHorizontalSpan(diffThread,2);

		//制限間隔設定
		timeLimitLabel =  new Label(group,SWT.NONE);
		timeLimitLabel.setText(
				ApplicationResource.getResource("Tab.BasicInfo.Duplication.Limit.Time.Text"));

		timeLimit = new ExcatText(group,SWT.RIGHT | SWT.SINGLE | SWT.BORDER);
		timeLimit.setControlWidth( 50);
		Label label =  new Label(group,SWT.NONE);
		label.setText(
				ApplicationResource.getResource("Tab.BasicInfo.Duplication.Limit.Time.Unit.Text"));
	}

	private void addSpaceLine(Composite composite,int spaceHeight){
		Composite spaceComposite = new Composite(composite, SWT.NONE);
        GridData spaceGridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        spaceGridData.heightHint = spaceHeight;
        spaceComposite.setLayoutData(spaceGridData);
	}

	/**
	 * add listener
	 *
	 */
	private void addListeners(){
		company.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent arg0) {
				baseInfo.setCompany(company.getText().trim());
				company.setBackground(white);
			}
		});
		system.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent arg0) {
				baseInfo.setSystem(system.getText().trim());
				system.setBackground(white);
			}
		});
		comment.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent arg0) {
				baseInfo.setComment(comment.getText().trim());
				comment.setBackground(white);
			}
		});
		dumpFileDir.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent arg0) {
				baseInfo.setDumpFileDir(dumpFileDir.getText().trim());
				dumpFileDir.setBackground(white);
			}
		});
		dumpFilePrefix.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent arg0) {
				baseInfo.setDumpFilePrefix(dumpFilePrefix.getText().trim());
				dumpFilePrefix.setBackground(white);
			}
		});
		dumpFileMaxSize.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent arg0) {
				baseInfo.setDumpFileMaxSize(dumpFileMaxSize.getText().trim());
				dumpFileMaxSize.setBackground(white);
			}
		});
		logFileDir.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent arg0) {
				baseInfo.setLogFileDir(logFileDir.getText().trim());
				logFileDir.setBackground(white);
			}
		});
		logLevel.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent arg0) {
				baseInfo.setLogLevel(logLevel.getText().trim());
			}
		});
		mailServer.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent arg0) {
				baseInfo.setMailServer(mailServer.getText().trim());
				mailServer.setBackground(white);
			}
		});
		mailPort.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent arg0) {
				baseInfo.setMailPort(mailPort.getText().trim());
				mailPort.setBackground(white);
			}
		});
		mailAccount.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent arg0) {
				baseInfo.setMailAccount(mailAccount.getText().trim());
				mailAccount.setBackground(white);
			}
		});
		mailPassword.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent arg0) {
				baseInfo.setMailPassword(mailPassword.getText().trim());
				mailPassword.setBackground(white);
			}
		});
		mailTemplate.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent arg0) {
				baseInfo.setMailTemplate(mailTemplate.getText().trim());
				mailTemplate.setBackground(white);
			}
		});
		mailSubject.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent arg0) {
				baseInfo.setMailSubject(mailSubject.getText().trim());
				mailSubject.setBackground(white);
			}
		});
		mailFrom.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent arg0) {
				baseInfo.setMailFrom(mailFrom.getText().trim());
				mailFrom.setBackground(white);
			}
		});
		mailFromName.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent arg0) {
				baseInfo.setMailFromName(mailFromName.getText().trim());
				//障害 #429 modify by tu
				mailFromName.setBackground(white);
				//mailFrom.setBackground(white);
			}
		});
		mailTo.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent arg0) {
				baseInfo.setMailTo(mailTo.getText().trim());
				mailTo.setBackground(white);
			}
		});
		autoCheckTime.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent arg0) {
				baseInfo.setAutoCheckTime(autoCheckTime.getText().trim());
				autoCheckTime.setBackground(white);
			}
		});
		minDiskSpace.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent arg0) {
				if (minDiskSpaceLessMaxLimit()){
					baseInfo.setMinDiskSpace(minDiskSpace.getText().trim());
					minDiskSpace.setBackground(white);
				}
			}
		});
		timeLimit.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent arg0) {
				baseInfo.setTimeLimit(timeLimit.getText().trim());
				timeLimit.setBackground(white);
			}
		});

		dumpFileMaxSize.addVerifyListener(
				new VerifyListener() {
					public void verifyText(VerifyEvent e) {
						e.doit=ViewerUtil.verifyNumbers(e.text);
						//e.doit = "0123456789".indexOf(e.text) >= 0 ;
					}
				});
		mailPort.addVerifyListener(
				new VerifyListener() {
					public void verifyText(VerifyEvent e) {
						e.doit=ViewerUtil.verifyNumbers(e.text);
					}
				});
		minDiskSpace.addVerifyListener(
				new VerifyListener() {
					public void verifyText(VerifyEvent e) {
						e.doit=ViewerUtil.verifyNumbers(e.text);
					}
				});
		timeLimit.addVerifyListener(
				new VerifyListener() {
					public void verifyText(VerifyEvent e) {
						e.doit=ViewerUtil.verifyNumbers(e.text);
					}
				});

		//メール設定有無
		mailSetting.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				baseInfo.setHasMailInfo(mailSetting.getSelection());
				if (mailSetting.getSelection()){
					setMail();
				}else{
					setNoMail();
				}
			}
		});
		mailTemplateCheck.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				baseInfo.setCheckMailTemplate(mailTemplateCheck.getSelection());
				if (mailTemplateCheck.getSelection()){
					mailTemplate.setBackground(white);
					mailTemplate.setEnabled(true);
				}else{
					mailTemplate.setBackground(background);
					mailTemplate.setEnabled(false);
				}
			}
		});
		dumpFileSizeCheck.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				baseInfo.setCheckMaxDumpData(dumpFileSizeCheck.getSelection());
				if (dumpFileSizeCheck.getSelection()){
					dumpFileMaxSize.setBackground(white);
					dumpFileMaxSize.setEnabled(true);
				}else{
					dumpFileMaxSize.setText("");
					dumpFileMaxSize.setBackground(background);
					dumpFileMaxSize.setEnabled(false);
				}
			}
		});
		duplicationCheck.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				baseInfo.setCheckDuplication(duplicationCheck.getSelection());
				if (duplicationCheck.getSelection()){
					timeLimit.setBackground(white);
					limitDuplication();
				}else{
					timeLimit.setBackground(background);
					notLimitDuplication();
				}
			}
		});
		mailIsAuth.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				baseInfo.setMailIsAuth(mailIsAuth.getSelection());
				if (mailIsAuth.getSelection()){
					mailAccount.setBackground(white);
					mailPassword.setBackground(white);
					mailAccount.setEnabled(true);
					mailPassword.setEnabled(true);
				}else{
					mailAccount.setBackground(background);
					mailPassword.setBackground(background);
					mailAccount.setEnabled(false);
					mailPassword.setEnabled(false);
				}
			}
		});
		companyCheck.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (companyCheck.getSelection()){
					company.setBackground(white);
					company.setEnabled(true);
				}else{
					company.setText("");
					company.setBackground(background);
					company.setEnabled(false);
				}
				ConfigModel.setChanged();
			}
		});
		systemCheck.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (systemCheck.getSelection()){
					system.setBackground(white);
					system.setEnabled(true);
				}else{
					system.setText("");
					system.setBackground(background);
					system.setEnabled(false);
				}
				ConfigModel.setChanged();
			}
		});
		commentCheck.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (commentCheck.getSelection()){
					comment.setBackground(white);
					comment.setEnabled(true);
				}else{
					comment.setText("");
					comment.setBackground(background);
					comment.setEnabled(false);
				}
				ConfigModel.setChanged();
			}
		});
		diffThread.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				ConfigModel.setChanged();
			}
		});
	}

	private void setNoMail(){
		mailSubject.setEnabled(false);
		mailFrom.setEnabled(false);
		mailFromName.setEnabled(false);
		mailTo.setEnabled(false);
		mailServer.setEnabled(false);
		mailPort.setEnabled(false);
		mailAccount.setEnabled(false);
		mailPassword.setEnabled(false);
		mailTemplateCheck.setEnabled(false);
		mailTemplate.setEnabled(false);
		mailIsAuth.setEnabled(false);

		mailServer.setBackground(background);
		mailPort.setBackground(background);
		mailTemplateCheck.setBackground(background);
		mailTemplate.setBackground(background);
		mailSubject.setBackground(background);
		mailFrom.setBackground(background);
		mailFromName.setBackground(background);
		mailTo.setBackground(background);
		mailIsAuth.setBackground(background);
		mailAccount.setBackground(background);
		mailPassword.setBackground(background);
		//通知
		//appWindow.closeMailNotice();
	}

	private void setMail(){
		mailServer.setEnabled(true);
		mailPort.setEnabled(true);
		mailTemplateCheck.setEnabled(true);
		mailTemplate.setEnabled(true);
		mailSubject.setEnabled(true);
		mailFrom.setEnabled(true);
		mailFromName.setEnabled(true);
		mailTo.setEnabled(true);
		mailIsAuth.setEnabled(true);

		mailServer.setBackground(white);
		mailPort.setBackground(white);
		mailTemplateCheck.setBackground(white);
		mailSubject.setBackground(white);
		mailFrom.setBackground(white);
		mailFromName.setBackground(white);
		mailTo.setBackground(white);
		if (mailTemplateCheck.getSelection()){
			mailTemplate.setBackground(white);
		}else{
			mailTemplate.setBackground(background);
		}
		if (mailIsAuth.getSelection()){
			mailAccount.setBackground(white);
			mailPassword.setBackground(white);
			mailAccount.setEnabled(true);
			mailPassword.setEnabled(true);
		}else{
			mailAccount.setBackground(background);
			mailPassword.setBackground(background);
			mailAccount.setEnabled(false);
			mailPassword.setEnabled(false);
		}
	}

	private void limitDuplication(){
		diffThread.setEnabled(true);
		timeLimit.setEnabled(true);
	}

	private void notLimitDuplication(){
		diffThread.setEnabled(false);
		timeLimit.setEnabled(false);
	}

	/**
	 *ConfigModelよりデータを読込み
	 *
	 */
	private void setDataToDisplay(){

		company.setText(baseInfo.getCompany());
		system.setText(baseInfo.getSystem());
		comment.setText(baseInfo.getComment());
		dumpFileDir.setText(baseInfo.getDumpFileDir());
		dumpFilePrefix.setText(baseInfo.getDumpFilePrefix());
		dumpFileSizeCheck.setSelection(baseInfo.isCheckMaxDumpData());
		dumpFileMaxSize.setText(baseInfo.getDumpFileMaxSize());
		logFileDir.setText(baseInfo.getLogFileDir());
		for (int i=0; i<BaseInfo.LOG_LEVEL.length; i++){
			if (BaseInfo.LOG_LEVEL[i].equals(baseInfo.getLogLevel().toLowerCase())){
				logLevel.select(i);
			}
		}

		mailSetting.setSelection(baseInfo.isHasMailInfo());
		mailServer.setText(baseInfo.getMailServer());
		mailPort.setText(baseInfo.getMailPort());
		mailAccount.setText(baseInfo.getMailAccount());
		mailPassword.setText(baseInfo.getMailPassword());
		mailTemplateCheck.setSelection(baseInfo.isCheckMailTemplate());
		mailTemplate.setText(baseInfo.getMailTemplate());
		mailSubject.setText(baseInfo.getMailSubject());
		mailFrom.setText(baseInfo.getMailFrom());
		mailFromName.setText(baseInfo.getMailFromName());
		mailTo.setText(baseInfo.getMailTo());
		mailIsAuth.setSelection(baseInfo.isMailIsAuth());
		autoCheckTime.setText(baseInfo.getAutoCheckTime());
		minDiskSpace.setText(baseInfo.getMinDiskSpace());
		duplicationCheck.setSelection(baseInfo.isCheckDuplication());
		diffThread.setSelection(baseInfo.isDuplicationWhenThreadDiff());
		timeLimit.setText(baseInfo.getTimeLimit());
	}

	/**
	 * 最大値チェック
	 *
	 * @return
	 */
	private boolean minDiskSpaceLessMaxLimit(){
		String s = minDiskSpace.getText().trim();
		if ("".equals(s)){
			return true;
		}

		int num = Integer.parseInt(s);
		if (num > 32767){
			minDiskSpace.setBackground(red);
			ExcatMessageUtilty.showMessage(
					this.appWindow.getShell(),
					Message.get("Dialog.Error.MinDiskSpace.Text"));
			return false;
		}else{
			return true;
		}
	}


	/**------------ 関連チェック----------------**/

	public boolean checkItems(){
		boolean result=true;
		if (companyCheck.getSelection() &&
			!ViewerUtil.checkStringItem(company.getText())){
			company.setBackground(red);
			result=false;
		}
		if (systemCheck.getSelection() &&
			!ViewerUtil.checkStringItem(system.getText())){
			system.setBackground(red);
			result=false;
		}
		if (commentCheck.getSelection() &&
				!ViewerUtil.checkStringItem(comment.getText())){
			comment.setBackground(red);
			result=false;
		}

		//folder
		if (!ViewerUtil.checkStringItem(dumpFileDir.getText())){
			dumpFileDir.setBackground(red);
			result = false;
		}
		if (!ViewerUtil.checkStringItem(dumpFilePrefix.getText())){
			dumpFilePrefix.setBackground(red);
			result=false;
		}
		if (!ViewerUtil.checkStringItem(dumpFileDir.getText())){
			dumpFileDir.setBackground(red);
			result = false;
		}
		if (dumpFileSizeCheck.getSelection() &&
			!ViewerUtil.isLargerThanMinValue(dumpFileMaxSize.getText(),0)){
			dumpFileMaxSize.setBackground(red);
			result=false;
		}

		if (!ViewerUtil.checkStringItem(logFileDir.getText())){
			logFileDir.setBackground(red);
			result=false;
		}
		if (!ViewerUtil.isLargerThanMinValue(minDiskSpace.getText(),0)){
			minDiskSpace.setBackground(red);
			result=false;
		}
		if (!minDiskSpaceLessMaxLimit()){
			return false;
		}
		if (!ViewerUtil.isLargerThanMinValue(autoCheckTime.getText(),0)){
			autoCheckTime.setBackground(red);
			result=false;
		}



		//mail
		if (mailSetting.getSelection()){
			if (!ViewerUtil.checkStringItem(mailFromName.getText())){
				mailFromName.setBackground(red);
				result=false;
			}
			if (!ViewerUtil.checkStringItem(mailFrom.getText())){
				mailFrom.setBackground(red);
				result = false;
			}
			if (!ViewerUtil.checkStringItem(mailTo.getText())){
				mailTo.setBackground(red);
				result= false;
			}
			if (!ViewerUtil.checkStringItem(mailServer.getText())){
				mailServer.setBackground(red);
				result= false;
			}
			if (!ViewerUtil.isLargerThanMinValue(mailPort.getText(),0)){
				mailPort.setBackground(red);
				result= false;
			}
			if (!ViewerUtil.checkStringItem(mailSubject.getText())){
				mailSubject.setBackground(red);
				result= false;
			}
			if (mailIsAuth.getSelection()){
				if (!ViewerUtil.checkStringItem(mailAccount.getText())){
					mailAccount.setBackground(red);
					result= false;
				}
				if (!ViewerUtil.checkStringItem(mailPassword.getText())){
					mailPassword.setBackground(red);
					result= false;
				}
			}
			if (mailTemplateCheck.getSelection()){
				if (!ViewerUtil.checkStringItem(mailTemplate.getText())){
					mailTemplate.setBackground(red);
					result=false;
				}
			}
		}
		//duplication
		if (duplicationCheck.getSelection()){
			if (!ViewerUtil.isLargerThanMinValue(timeLimit.getText(),0)){
				timeLimit.setBackground(red);
				result=false;
			}
		}

		return result;
	}

}

