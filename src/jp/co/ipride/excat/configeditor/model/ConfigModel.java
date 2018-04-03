package jp.co.ipride.excat.configeditor.model;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.Writer;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import jp.co.ipride.excat.common.Message;
import jp.co.ipride.excat.common.utility.ExcatMessageUtilty;
import jp.co.ipride.excat.configeditor.model.baseinfo.BaseInfo;
import jp.co.ipride.excat.configeditor.model.instance.ObjectLine;
import jp.co.ipride.excat.configeditor.model.instance.ObjectLineList;
import jp.co.ipride.excat.configeditor.model.task.ExceptionRegisterTask;
import jp.co.ipride.excat.configeditor.model.task.ITask;
import jp.co.ipride.excat.configeditor.model.task.MonitorMethodTask;
import jp.co.ipride.excat.configeditor.model.task.MonitoringException;
import jp.co.ipride.excat.configeditor.model.task.MonitoringMethod;
import jp.co.ipride.excat.configeditor.model.task.TaskList;
import jp.co.ipride.excat.configeditor.model.template.Template;
import jp.co.ipride.excat.configeditor.model.template.TemplateList;
import org.apache.log4j.Logger;
import org.apache.xerces.dom.DocumentImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * コンフィグ・ファイルのデータ・モデル
 * @author tu
 * @since 2007/11/18
 */
public class ConfigModel {

	private static String xmi = "http://www.w3.org/2001/XMLSchema-instance";

	private static String schema = "Configuration.xsd";

	private static String encoding = "UTF-8";

	private static TaskList taskList = new TaskList();

	private static BaseInfo baseInfo = new BaseInfo();

	private static ObjectLineList objectLineList = new ObjectLineList();

	private static TemplateList templateList = new TemplateList();

	private static Document document = null;

	private static String filePath = null;

	private static boolean change = false;

	private static boolean fileOpening = false;

	/**
	 * 初期化
	 */
	public static void removeAll(){
		taskList.removeAll();
		baseInfo = new BaseInfo();
		objectLineList = new ObjectLineList();
		templateList = new TemplateList();
		document = null;
		filePath = null;
		change = false;
	}

	/**
	 * 初期化済みの新規コンフィグを作成
	 *
	 */
	public static void createNewConfig(){
		taskList.init();
		//update by v3
		//taskList.addNewTask();
		baseInfo.init();
		objectLineList.init();
		templateList.init();
		document = null;
		filePath = null;
		change = false;
	}

	/**
	 * 既存コンフィグを読み込み、データオブジェクトを作成
	 * @param config_path
	 */
	public static boolean openOldConfig(String config_path){
		filePath = config_path;
		byte[] total;
		InputStream stream = null;
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setIgnoringComments(true);
			DocumentBuilder builder = factory.newDocumentBuilder();
			stream = new FileInputStream(config_path);
			total = new byte[stream.available()];
			stream.read(total);
			String result = new String(total, "UTF-8");
			document = builder.parse(new InputSource(new StringReader(result)));
			inputConfig();
			change = false;
			return true;
		}catch (Exception e){
			Logger.getLogger("ConfigLogger").error("openOldConfig",e);
			ExcatMessageUtilty.showMessage(
					null,
					Message.get("Dialog.Error.OpenXML.Text"));
			return false;
		} finally {
			if (stream != null){
				try {
					stream.close();
				} catch (IOException e) {
				}
			}
		}
	}

	/**
	 * 指定ファイルで保存する。
	 * @param new_path
	 */
	public static boolean saveAsNewConfig(String new_path){
		filePath = new_path;
		outputDocument();
		if (saveToFile()) {
			change = false;
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 既存コンフィグ・ファイルを更新
	 *
	 */
	public static boolean update(){
		outputDocument();
		if (saveToFile()) {
			change = false;
			return true;
		} else {
			return false;
		}

	}

	public static boolean checkConfigModel(){
		return taskList.checkTasks();
	}

	public static BaseInfo getBaseInfo() {
		return baseInfo;
	}

	public static ObjectLineList getObjectLineList() {
		return objectLineList;
	}

	public static TaskList getTaskList() {
		return taskList;
	}

	public static TemplateList getTemplateList() {
		return templateList;
	}

	public static Document getDocument(){
		return document;
	}

	public static ITask createNewTask(int taskType){
		return taskList.createNewTask(taskType);
	}

	/**
	 * [Config]タグを読込み、ＤＯＭを作成する。
	 */
	private static void inputConfig(){
		NodeList nodeList = document.getChildNodes().item(0).getChildNodes();
		templateList.init();
		baseInfo.init();
		taskList.init();
		objectLineList.init();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			String name = node.getNodeName();
			if (ConfigContant.Tag_OTHERS.equals(name)){
				baseInfo.inputDocument(node);
			}else if (ConfigContant.Tag_OBJECT_ELEMENT.equals(name)){
				templateList.inputDocument(node);
			}else if (ConfigContant.Tag_TASK.equals(name)){
				taskList.inputDocument(node);
			}else if (ConfigContant.Tag_DUMP_INSTANCE.equals(name)){
				objectLineList.inputDocument(node);
			}
		}
	}


	/**
	 * 生成されたDOMをファイルに保存する。.
	 * @param filePath
	 */
	private static boolean saveToFile(){
		Writer writer = null;
		FileOutputStream fileOutStream = null;
		try{
			fileOutStream = new FileOutputStream(filePath);
			XmlWriter xmlWriter = new XmlWriter();
			xmlWriter.setOutput(fileOutStream,encoding);
			xmlWriter.write(document);
			return true;
		}catch (Exception e){
			Logger.getLogger("ConfigLogger").error("saveToFile",e);
			ExcatMessageUtilty.showMessage(
					null,
					Message.get("Dialog.Error.Text"));
			return false;
		}finally{
			if (fileOutStream != null){
				try {
					fileOutStream.close();
				} catch (IOException e) {
				}
			}
			if (writer != null){
				try {
					writer.close();
				} catch (IOException e) {
				}
			}
		}
	}

	/**
	 * オブジェクトからＤＯＭを作成する。
	 *
	 */
	private static void outputDocument(){
		document = new DocumentImpl();
		Node root = document.createElement(ConfigContant.Tag_CONFIG);
		((Element)root).setAttribute(ConfigContant.Field_XSI, xmi);
		((Element)root).setAttribute(ConfigContant.Field_SCHEMA, schema);
		taskList.outputDocument(root);
		objectLineList.outputDocument(root);
		templateList.outputDocument(root);
		baseInfo.outputDocument(root);
		document.appendChild(root);
	}

	/** ------------ 以下は関連チェック関数-------------------**/


	/**
	 * シングナル監視タスクは一つのみであること。
	 * チェックのタイミング：「シングナル監視」に選択した時
	 * @return
	 */
	public static boolean hasOtherMonitoringSignalTask(ITask myTask){
		for(int i=0; i<taskList.getTasks().size(); i++){
			ITask task = taskList.getTasks().get(i);
			if (task.getTaskType() == ITask.MONITOR_SIGNAL){
				if (!task.equals(myTask)){
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 1．既存の登録情報では、クラス・ロードを指定していない場合、
	 *    同名クラスの新規登録では、クラスロードを指定することはできない。
	 * 2．既存の登録情報では、クラス・ロードを指定している場合、
	 * 　 同名クラスの新規登録では、クラスロードを指定すること。
	 * 3. クラス名とロードクラス名が同じである場合、拡張子名を統一すること。
	 * 3.チェックタイミング：
	 * 　　・該当画面の保存ボタン。
	 *
	 * @param myMethod　編集中の情報
	 * @param orgMethod 編集元の情報
	 * @return
	 */
//	public static int checkClassDefForMonitiringMethod(MonitoringMethod myMethod, MonitoringMethod orgMethod){
//		for (int i=0; i<taskList.getTasks().size(); i++){
//			ITask task = taskList.getTasks().get(i);
//			if (task instanceof MonitorMethodTask){
//				MonitorMethodTask monitorMethodTask = (MonitorMethodTask)task;
//				for (int j=0; j<monitorMethodTask.getMonitoringMethodList().size(); j++){
//					MonitoringMethod method = (MonitoringMethod)monitorMethodTask.getMonitoringMethodList().get(j);
//					//同一ものなら
//					if (method.equals(orgMethod)){
//					  continue;
//					}
//					if (method.getClassName().equals(myMethod.getClassName())){
//						//既存定義のクラスロードがない場合
//						if ("".equals(method.getClassLoaderURL())){
//							if (!"".equals(myMethod.getClassLoaderURL())){
//								return 1;
//							}
//						}else{
//							//既存定義のクラスロードがある場合
//							//当方がない場合。
//							if ("".equals(myMethod.getClassLoaderURL())){
//								return 2;
//							}
//							//クラス・ロード名が一致している
//							if (DocumetUtil.checkClassLoaderURL(method.getClassLoaderURL(),myMethod.getClassLoaderURL())){
//								//拡張子が一致していない
//								if (!method.getClassLoader_suffix().equals(myMethod.getClassLoader_suffix())){
//									return 3;
//								}
//							}
//						}
//						return 0;
//					}
//				}
//			}
//		}
//		return 0;
//	}

	/**
	 * １．クラス、クラス・ロード、メソッドが同じ定義が既にある場合
	 * 　　１．１．既存メソッドのシグネチャが無ければ、当定義では無効。
	 * 　　１．２．既存メソッドのシグネチャがあれば、当定義のシグネチャは既存定義と異なるべき
	 * ３．チェックのタイミング：該当画面「登録」ボタンを押す時
	 *
	 * @param myMethod　編集中の情報
	 * @param orgMethod 編集元の情報
	 * @return
	 */
	public static int checkMethodDefForMonitiringMethod(MonitoringMethod myMethod, MonitoringMethod orgMethod){
		for (int i=0; i<taskList.getTasks().size(); i++){
			ITask task = taskList.getTasks().get(i);
			if (task instanceof MonitorMethodTask){
				MonitorMethodTask monitorMethodTask = (MonitorMethodTask)task;
				for (int j=0; j<monitorMethodTask.getMonitoringMethodList().size(); j++){
					MonitoringMethod method = (MonitoringMethod)monitorMethodTask.getMonitoringMethodList().get(j);
					//同一ものなら
					if (method.equals(orgMethod)){
					  continue;
					}
					//クラス、クラス・ロード、メソッドが同じ定義が既にある場合
					if (method.getClassName().equals(myMethod.getClassName()) &&
							method.getClassLoaderURL().equals(myMethod.getClassLoaderURL()) &&
							method.getMethodName().equals(myMethod.getMethodName())){
						//既存メソッドのシグネチャが無ければ、当定義は無効であること。
						if ("".equals(method.getMethodSignature())){
							return 1;
						}else{
						//既存メソッドのシグネチャがあれば、当定義のシグネチャは既存定義と異なるべき
							if (method.getMethodSignature().equals(myMethod.getMethodSignature())){
								return 2;
							}
						}
					}
				}
			}
		}
		return 0;
	}

	/**
	 * ２．メソッドを監視するクラスとの重複がある場合、
	 * 　　①．メソッドが監視するクラスのクラス・ロードが無い場合、
	 * 　　　　「特定」クラスのロードがあれば、ＮＧ
	 * 　　②．メソッドが監視するクラスのクラス・ロードがある場合、
	 * 　　　　「特定」クラスのロードが無ければ、ＮＧ
	 *
	 * ３．チェックのタイミング：保存時
	 */
	public static int checkClassOfInstanceDef(ObjectLine objectLine){
		for (int i=0; i<taskList.getTasks().size(); i++){
			ITask task = taskList.getTasks().get(i);
			if (task instanceof MonitorMethodTask){
				MonitorMethodTask monitorMethodTask = (MonitorMethodTask)task;
				for (int j=0; j<monitorMethodTask.getMonitoringMethodList().size(); j++){
					MonitoringMethod method = (MonitoringMethod)monitorMethodTask.getMonitoringMethodList().get(j);
					if (method.getClassName().equals(objectLine.getClassName())){
						if ("".equals(method.getClassLoaderURL()) &&
							!"".equals(objectLine.getClassLoadName())){
							return 1;
						}
						if (!"".equals(method.getClassLoaderURL()) &&
							"".equals(objectLine.getClassLoadName())){
							return 2;
						}
					}
				}
			}
		}
		return 0;
	}

	/**
	 * 監視クラスが既にある場合、true
	 * ない場合、false
	 * @param monitoringException  編集中の情報
	 * @param orgMonitoringException　元の情報
	 */
	public static boolean hasSameMonitoringClassName(MonitoringException monitoringException,
													  MonitoringException orgMonitoringException){
		for (int i=0; i<taskList.getTasks().size(); i++){
			ITask task = taskList.getTasks().get(i);
			if (task instanceof ExceptionRegisterTask){
				ExceptionRegisterTask emTask = (ExceptionRegisterTask)task;
				for (int j=0; j<emTask.getMonitoringExceptionList().size(); j++){
					MonitoringException monitor = (MonitoringException)emTask.getMonitoringExceptionList().get(j);
					if (monitor.equals(orgMonitoringException)){
						continue;
					}
					String className = monitor.getTargetClassName();
					if (className.equals(monitoringException.getTargetClassName())){
						return true;
					}
				}
			}

		}
		return false;
	}

	/**
	 * 同じクラス名を持つテンプレートが有無するかをチェックする。
	 * @param template　編集中のテンプレート
	 * @param orgTemplate　該当編集の元テンプレート
	 * @return
	 */
	public static boolean hasSameClassForTemplateDef(Template template, Template orgTemplate){
		Vector<Template> templates = templateList.getTemplates();
		for (int i=0; i<templates.size(); i++){
			Template item = (Template)templates.get(i);
			if (item.equals(orgTemplate)){
				continue;
			}
			if (item.getClassName().equals(template.getClassName())){
				return false;
			}
		}
		return true;
	}

	/**
	 * チェック：もし、基本情報にメール情報が登録されていない場合、タスクのメール送信と添付を選択してはいけない。
	 * チェックのタイミング：「メール送信」を選択した時
	 *
	 */
	public static boolean checkMail(){
		if (ConfigModel.baseInfo.isHasMailInfo()){
			return true;
		}else{
			return false;
		}
	}

	/**
	 * 新規編集かをチェックする。
	 * @return
	 */
	public static boolean isNewConfig(){
		if (filePath == null){
			return true;
		}else{
			return false;
		}
	}

	/**
	 * ステータス表示などに使用する。
	 * @return
	 */
	public static String getFilePath(){
		return filePath;
	}

	public static boolean isChanged(){
		return change;
	}

	public static void setChanged(){
		if (!fileOpening) {
			change = true;
		}
	}

	public static void noChanged(){
		change = false;
	}

	public static boolean hasDocument(){
		if (document == null){
			return false;
		}else{
			return true;
		}
	}

	public static void setFileOpening(boolean fileOpening) {
		ConfigModel.fileOpening = fileOpening;
	}
}