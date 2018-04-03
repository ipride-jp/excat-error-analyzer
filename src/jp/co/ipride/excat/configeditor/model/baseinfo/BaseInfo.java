package jp.co.ipride.excat.configeditor.model.baseinfo;

import jp.co.ipride.excat.configeditor.model.ConfigContant;
import jp.co.ipride.excat.configeditor.model.ConfigModel;
import jp.co.ipride.excat.configeditor.util.DocumetUtil;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * 基本情報を保持する。
 *
 * @author tu
 * @since 2007/11/19
 */
public class BaseInfo {

	public static final String[] LOG_LEVEL = {"info","warn","error","fatal"};

	private String company=null;
	private String system=null;
	private String comment=null;
	private String dumpFileDir=null;
	private String dumpFilePrefix=null;
	private String dumpFileMaxSize=null;
	private String logFileDir=null;
	private String logLevel=null;
	private String mailServer=null;
	private String mailPort=null;
	private String mailAccount=null;
	private String mailPassword=null;
	private String mailTemplate=null;
	private String mailSubject=null;
	private String mailFrom=null;
	private String mailFromName=null;
	private String mailTo=null;
	private boolean mailIsAuth=false;
	private String autoCheckTime=null;
	private String minDiskSpace="100";    //最大32767
	private boolean checkDuplication=false;
	private boolean duplicationWhenThreadDiff=false;
	private String timeLimit=null;

	//Config以外の項目
	private boolean hasMailInfo = false;
	private boolean checkMaxDumpData = false;
	private boolean checkMinDiskSpace = true;
	private boolean hasUserInfo = false;
	private boolean checkMailTemplate=false;

	/**
	 * コンストラクタ
	 *
	 */
	public BaseInfo(){
		init();
	}

	/**
	 * 初期化（1回のみ）
	 *
	 */
	public void init(){
		company="";
		system="";
		comment="";
		dumpFileDir="";
		dumpFilePrefix="";
		dumpFileMaxSize="";
		logFileDir="";
		logLevel=LOG_LEVEL[0];
		mailServer="";
		mailPort="";
		mailAccount="";
		mailPassword="";
		mailTemplate="";
		mailSubject="";
		mailFrom="";
		mailFromName="";
		mailTo="";
		mailIsAuth=false;
		autoCheckTime="300";
		minDiskSpace="100";    //最大32767
		checkDuplication=false;
		duplicationWhenThreadDiff=true;
		timeLimit="60";

		//Config以外の項目
		hasUserInfo = false;
		hasMailInfo = false;
		checkMaxDumpData = false;
		checkMinDiskSpace = true;
		checkMailTemplate = false;
	}

	/**
	 * 「Others」部分を読込む
	 * @param othersNode : [Others] node
	 */
	public void inputDocument(Node othersNode){
		NodeList nodeList=othersNode.getChildNodes();
		for (int i=0; i<nodeList.getLength(); i++){
			Node node = nodeList.item(i);
			String name = node.getNodeName();
			if (ConfigContant.Tag_DUMP_FILE.equals(name)){
				inputDumpFile(node);
			}else if (ConfigContant.Tag_CHECK_CONFIG.equals(name)){
				inputCheckConfig(node);
			}else if (ConfigContant.Tag_MAX_DUMP_DATA.equals(name)){
				inputMaxDumpData(node);
			}else if (ConfigContant.Tag_LOG.equals(name)){
				inputLog(node);
			}else if (ConfigContant.Tag_MAIL.equals(name)){
				inputMail(node);
			}else if (ConfigContant.Tag_USER_INFO.equals(name)){
				inputUserInfo(node);
			}else if (ConfigContant.Tag_CHECK_DUPLICATION.equals(name)){
				inputCheckDuplication(node);
			}
		}
	}

	/**
	 * 重複ダンプ防止定義部分を読込む
	 * @param checkDupliNode
	 */
	private void inputCheckDuplication(Node checkDupliNode){
		if (checkDupliNode == null){
			checkDuplication=false;
			duplicationWhenThreadDiff=false;
			timeLimit="";
		}else{
			checkDuplication = true;
			Node attrNode;
			NamedNodeMap map = checkDupliNode.getAttributes();
			attrNode = map.getNamedItem(ConfigContant.Field_DUPLICATION_THREAD_DIFF);
			duplicationWhenThreadDiff = DocumetUtil.parseBoolean(attrNode.getNodeValue());
			attrNode = map.getNamedItem(ConfigContant.Field_TIME_LIMIT);
			timeLimit=attrNode.getNodeValue();
		}
	}

	/**
	 * ユーザー情報部分を読込む
	 * @param userInfoNode
	 */
	private void inputUserInfo(Node userInfoNode){
		if (userInfoNode == null){
			hasUserInfo = false;
			company="";
			system="";
			comment="";
		}else{
			hasUserInfo = true;
			Node attrNode;
			NamedNodeMap map = userInfoNode.getAttributes();
			attrNode = map.getNamedItem(ConfigContant.Field_COMPANY_NAME);
			if (attrNode != null){
				company = attrNode.getNodeValue();
			}
			attrNode = map.getNamedItem(ConfigContant.Field_SYSTEM_NAME);
			if (attrNode != null){
				system = attrNode.getNodeValue();
			}
			attrNode = map.getNamedItem(ConfigContant.Field_COMMENT);
			if (attrNode != null){
				comment = attrNode.getNodeValue();
			}
		}
	}

	/**
	 * ダンプファイルの定義部分を読込む
	 * @param dumpFileNode
	 */
	private void inputDumpFile(Node dumpFileNode){
		Node attrNode;
		NamedNodeMap map = dumpFileNode.getAttributes();
		attrNode = map.getNamedItem(ConfigContant.Field_MIN_DISK_SPACE);
		if (attrNode != null){
			minDiskSpace = attrNode.getNodeValue();
		}
		attrNode = map.getNamedItem(ConfigContant.Field_PATH);
		dumpFileDir = attrNode.getNodeValue();
		attrNode = map.getNamedItem(ConfigContant.Field_PREFIX);
		dumpFilePrefix = attrNode.getNodeValue();
	}

	/**
	 * コンフィグ自動チェックの定義部分を読込む。
	 * @param checkConfigNode
	 */
	private void inputCheckConfig(Node checkConfigNode){
		Node attrNode;
		NamedNodeMap map = checkConfigNode.getAttributes();
		attrNode = map.getNamedItem(ConfigContant.Field_SLEEP);
		autoCheckTime = attrNode.getNodeValue();
	}

	/**
	 * ダンプデータ量の制限情報
	 * @param maxDumpdataNode
	 */
	private void inputMaxDumpData(Node maxDumpdataNode){
		if (maxDumpdataNode==null){
			checkMaxDumpData = false;
			dumpFileMaxSize="";
			checkMinDiskSpace=false;
		}else{
			checkMaxDumpData=true;
			Node attrNode;
			NamedNodeMap map = maxDumpdataNode.getAttributes();
			attrNode = map.getNamedItem(ConfigContant.Field_LIMIT);
			if (attrNode == null || "".equals(attrNode.getNodeValue().trim())){
				checkMinDiskSpace=false;
				dumpFileMaxSize="";
			}else{
				checkMinDiskSpace=true;
				dumpFileMaxSize = attrNode.getNodeValue();
			}
		}
	}

	/**
	 * ログ定義部分を読込む
	 * @param logNode
	 */
	private void inputLog(Node logNode){
		Node attrNode;
		NamedNodeMap map = logNode.getAttributes();
		attrNode = map.getNamedItem(ConfigContant.Field_PATH);
		logFileDir = attrNode.getNodeValue();
		attrNode = map.getNamedItem(ConfigContant.Field_LEVEL);
		if (attrNode !=null){
			logLevel = attrNode.getNodeValue();
		}
	}

	/**
	 * メール定義を読込み
	 * @param mailNode
	 */
	private void inputMail(Node mailNode){

		if (mailNode==null){
			hasMailInfo=false;
			mailServer="";
			mailPort="";
			mailAccount="";
			mailPassword="";
			mailTemplate="";
			mailSubject="";
			mailFrom="";
			mailFromName="";
			mailTo="";
			mailIsAuth=false;
			checkMailTemplate=false;
		}else{
			hasMailInfo=true;
			Node attrNode;
			NamedNodeMap map = mailNode.getAttributes();
			attrNode = map.getNamedItem(ConfigContant.Field_FROM);
			mailFrom = attrNode.getNodeValue();
			attrNode = map.getNamedItem(ConfigContant.Field_FROM_NAME);
			mailFromName = attrNode.getNodeValue();
			attrNode = map.getNamedItem(ConfigContant.Field_TO);
			mailTo = attrNode.getNodeValue();
			attrNode = map.getNamedItem(ConfigContant.Field_SUBJECT);
			mailSubject = attrNode.getNodeValue();
			attrNode = map.getNamedItem(ConfigContant.Field_BODY_TEMP_FOLDER_PATH);
			if (attrNode==null){
				checkMailTemplate=false;
				mailTemplate = "";
			}else{
				checkMailTemplate=true;
				mailTemplate = attrNode.getNodeValue();
			}
			attrNode = map.getNamedItem(ConfigContant.Field_SMTP_SERVER_PORT);
			mailPort = attrNode.getNodeValue();
			attrNode = map.getNamedItem(ConfigContant.Field_SMTP_SERVER);
			mailServer = attrNode.getNodeValue();
			attrNode = map.getNamedItem(ConfigContant.Field_IS_AUTH);
			if (attrNode==null){
				mailIsAuth=false;
				mailAccount="";
				mailPassword="";
			}else{
				mailIsAuth = DocumetUtil.parseBoolean(attrNode.getNodeValue());
				attrNode = map.getNamedItem(ConfigContant.Field_ACCOUNT);
				mailAccount = attrNode.getNodeValue();
				attrNode = map.getNamedItem(ConfigContant.Field_PASSWORD);
				mailPassword = attrNode.getNodeValue();
			}
		}
	}

	/**
	 * Config
	 *    Others
	 *        DumpFile     [Path][Prefix][MinDiskSpace]
	 *        CheckConfig  [Sleep]
	 *        MaxDumpData  [Limit]
	 *        Log          [Path][Level]
	 *        Mail         [From][FromName][To][Subject][BodyTemplateFolderPath]
	 *                     [SmtpServer][SmtpServerPort][IsAuth][Account][Password]
	 *        CheckDuplication [DumpDuplicationWhenThreadDiff][TimeLimit]
	 *        UserInfo     [CompanyName][SystemName][Comment]
	 *
	 * @param root [Config]
	 */
	public void outputDocument(Node root){
		Node othersNode = ConfigModel.getDocument().createElement(ConfigContant.Tag_OTHERS);
		root.appendChild(othersNode);

		Node dumpFileNode = ConfigModel.getDocument().createElement(ConfigContant.Tag_DUMP_FILE);
		othersNode.appendChild(dumpFileNode);
		((Element)dumpFileNode).setAttribute(ConfigContant.Field_PATH, dumpFileDir);
		((Element)dumpFileNode).setAttribute(ConfigContant.Field_PREFIX, dumpFilePrefix);
		if (checkMinDiskSpace){
			((Element)dumpFileNode).setAttribute(ConfigContant.Field_MIN_DISK_SPACE, minDiskSpace);
		}

		Node checkConfigNode = ConfigModel.getDocument().createElement(ConfigContant.Tag_CHECK_CONFIG);
		othersNode.appendChild(checkConfigNode);
		((Element)checkConfigNode).setAttribute(ConfigContant.Field_SLEEP, autoCheckTime);

		if (checkMaxDumpData){
			Node maxDumpDatNode = ConfigModel.getDocument().createElement(ConfigContant.Tag_MAX_DUMP_DATA);
			othersNode.appendChild(maxDumpDatNode);
			((Element)maxDumpDatNode).setAttribute(ConfigContant.Field_LIMIT, dumpFileMaxSize);
		}

		Node logNode = ConfigModel.getDocument().createElement(ConfigContant.Tag_LOG);
		othersNode.appendChild(logNode);
		((Element)logNode).setAttribute(ConfigContant.Field_PATH, logFileDir);
		((Element)logNode).setAttribute(ConfigContant.Field_LEVEL, logLevel);

		if (hasMailInfo){
			Node mailNode = ConfigModel.getDocument().createElement(ConfigContant.Tag_MAIL);
			othersNode.appendChild(mailNode);
			((Element)mailNode).setAttribute(ConfigContant.Field_FROM, mailFrom);
			((Element)mailNode).setAttribute(ConfigContant.Field_FROM_NAME, mailFromName);
			((Element)mailNode).setAttribute(ConfigContant.Field_TO, mailTo);
			((Element)mailNode).setAttribute(ConfigContant.Field_SUBJECT, mailSubject);
			if (checkMailTemplate){
				((Element)mailNode).setAttribute(ConfigContant.Field_BODY_TEMP_FOLDER_PATH, mailTemplate);
			}
			((Element)mailNode).setAttribute(ConfigContant.Field_SMTP_SERVER, mailServer);
			((Element)mailNode).setAttribute(ConfigContant.Field_SMTP_SERVER_PORT, mailPort);
			((Element)mailNode).setAttribute(ConfigContant.Field_IS_AUTH, Boolean.toString(mailIsAuth));
			((Element)mailNode).setAttribute(ConfigContant.Field_ACCOUNT, mailAccount);
			((Element)mailNode).setAttribute(ConfigContant.Field_PASSWORD, mailPassword);
		}
		if (checkDuplication){
			Node duplicationNode = ConfigModel.getDocument().createElement(ConfigContant.Tag_CHECK_DUPLICATION);
			othersNode.appendChild(duplicationNode);
			((Element)duplicationNode).setAttribute(ConfigContant.Field_DUPLICATION_THREAD_DIFF, Boolean.toString(duplicationWhenThreadDiff));
			((Element)duplicationNode).setAttribute(ConfigContant.Field_TIME_LIMIT, timeLimit);
		}
		Node userlNode = ConfigModel.getDocument().createElement(ConfigContant.Tag_USER_INFO);
		othersNode.appendChild(userlNode);
		((Element)userlNode).setAttribute(ConfigContant.Field_COMPANY_NAME, company);
		((Element)userlNode).setAttribute(ConfigContant.Field_SYSTEM_NAME, system);
		((Element)userlNode).setAttribute(ConfigContant.Field_COMMENT, comment);
	}


	//----------- getter and setter ----------------------

	public String getAutoCheckTime() {
		return autoCheckTime;
	}

	public void setAutoCheckTime(String autoCheckTime) {
		this.autoCheckTime = autoCheckTime;
		ConfigModel.setChanged();
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
		ConfigModel.setChanged();
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
		ConfigModel.setChanged();
	}

	public String getMinDiskSpace() {
		return minDiskSpace;
	}

	public void setMinDiskSpace(String diskLimit) {
		this.minDiskSpace = diskLimit;
		ConfigModel.setChanged();
	}

	public String getDumpFileDir() {
		return dumpFileDir;
	}

	public void setDumpFileDir(String dumpFileDir) {
		this.dumpFileDir = dumpFileDir;
		ConfigModel.setChanged();
	}

	public String getDumpFileMaxSize() {
		return dumpFileMaxSize;
	}

	public void setDumpFileMaxSize(String dumpFileMaxSize) {
		this.dumpFileMaxSize = dumpFileMaxSize;
		ConfigModel.setChanged();
	}

	public String getDumpFilePrefix() {
		return dumpFilePrefix;
	}

	public void setDumpFilePrefix(String dumpFilePrefix) {
		this.dumpFilePrefix = dumpFilePrefix;
		ConfigModel.setChanged();
	}

	public String getLogFileDir() {
		return logFileDir;
	}

	public void setLogFileDir(String logFileDir) {
		this.logFileDir = logFileDir;
		ConfigModel.setChanged();
	}

	public String getLogLevel() {
		return logLevel;
	}

	public void setLogLevel(String logLevel) {
		this.logLevel = logLevel;
		ConfigModel.setChanged();
	}

	public String getMailAccount() {
		return mailAccount;
	}

	public void setMailAccount(String mailAccount) {
		this.mailAccount = mailAccount;
		ConfigModel.setChanged();
	}

	public String getMailFrom() {
		return mailFrom;
	}

	public void setMailFrom(String mailFrom) {
		this.mailFrom = mailFrom;
		ConfigModel.setChanged();
	}

	public String getMailFromName() {
		return mailFromName;
	}

	public void setMailFromName(String mailFromName) {
		this.mailFromName = mailFromName;
		ConfigModel.setChanged();
	}

	public boolean isMailIsAuth() {
		return mailIsAuth;
	}

	public void setMailIsAuth(boolean mailIsAuth) {
		this.mailIsAuth = mailIsAuth;
		ConfigModel.setChanged();
	}

	public String getMailPassword() {
		return mailPassword;
	}

	public void setMailPassword(String mailPassword) {
		this.mailPassword = mailPassword;
		ConfigModel.setChanged();
	}

	public String getMailPort() {
		return mailPort;
	}

	public void setMailPort(String mailPort) {
		this.mailPort = mailPort;
		ConfigModel.setChanged();
	}

	public String getMailServer() {
		return mailServer;
	}

	public void setMailServer(String mailServer) {
		this.mailServer = mailServer;
		ConfigModel.setChanged();
	}

	public String getMailSubject() {
		return mailSubject;
	}

	public void setMailSubject(String mailSubject) {
		this.mailSubject = mailSubject;
		ConfigModel.setChanged();
	}

	public String getMailTemplate() {
		return mailTemplate;
	}

	public void setMailTemplate(String mailTemplate) {
		this.mailTemplate = mailTemplate;
		ConfigModel.setChanged();
	}

	public String getMailTo() {
		return mailTo;
	}

	public void setMailTo(String mailTo) {
		this.mailTo = mailTo;
		ConfigModel.setChanged();
	}

	public String getSystem() {
		return system;
	}

	public void setSystem(String system) {
		this.system = system;
		ConfigModel.setChanged();
	}

	public boolean isCheckDuplication() {
		return checkDuplication;
	}

	public void setCheckDuplication(boolean checkDuplication) {
		this.checkDuplication = checkDuplication;
		ConfigModel.setChanged();
	}

	public boolean isDuplicationWhenThreadDiff() {
		return duplicationWhenThreadDiff;
	}

	public void setDuplicationWhenThreadDiff(boolean duplicationWhenThreadDiff) {
		this.duplicationWhenThreadDiff = duplicationWhenThreadDiff;
		ConfigModel.setChanged();
	}

	public String getTimeLimit() {
		return timeLimit;
	}

	public void setTimeLimit(String timeLimit) {
		this.timeLimit = timeLimit;
		ConfigModel.setChanged();
	}


	public boolean isCheckMaxDumpData() {
		return checkMaxDumpData;
	}


	public void setCheckMaxDumpData(boolean checkMaxDumpData) {
		this.checkMaxDumpData = checkMaxDumpData;
		ConfigModel.setChanged();
	}


	public boolean isCheckMinDiskSpace() {
		return checkMinDiskSpace;
	}


	public void setCheckMinDiskSpace(boolean checkMinDiskSpace) {
		this.checkMinDiskSpace = checkMinDiskSpace;
		ConfigModel.setChanged();
	}


	public boolean isHasMailInfo() {
		return hasMailInfo;
	}


	public void setHasMailInfo(boolean hasMailInfo) {
		this.hasMailInfo = hasMailInfo;
		ConfigModel.setChanged();
	}

	public boolean isHasUserInfo() {
		return hasUserInfo;
	}

	public void setHasUserInfo(boolean hasUserInfo) {
		this.hasUserInfo = hasUserInfo;
		ConfigModel.setChanged();
	}

	public boolean isCheckMailTemplate() {
		return checkMailTemplate;
	}

	public void setCheckMailTemplate(boolean checkMailTemplate) {
		this.checkMailTemplate = checkMailTemplate;
		ConfigModel.setChanged();
	}

}
