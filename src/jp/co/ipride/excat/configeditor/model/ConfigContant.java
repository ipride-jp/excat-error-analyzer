package jp.co.ipride.excat.configeditor.model;
/**
 * config xml file
 *
 * @author tu
 * @since 2007/11/18
 */
public class ConfigContant {

	public static final String Tag_CONFIG ="Config";

	public static final String Field_XSI = "xmlns:xsi";

	public static final String Field_SCHEMA = "xsi:noNamespaceSchemaLocation";

	public static final String Tag_USER_INFO = "UserInfo";

	public static final String Field_COMPANY_NAME = "CompanyName";

	public static final String Field_SYSTEM_NAME = "SystemName";

	public static final String Field_COMMENT = "Comment";

	public static final String Tag_TASK = "Task";

	public static final String Tag_MONITOR_TARGETS = "MonitoringTargets";

	public static final String Tag_MONITOR_TARGET = "MonitoringTarget";

	public static final String Tag_THROWABLE = "Throwable";

	public static final String Tag_PLACE = "Place";

	public static final String Field_CLASS = "Class";

	public static final String Field_CLASS_NAME = "ClassName";

	public static final String FIield_METHOD = "MethodName";

	public static final String Tag_MONITOR_METHOD = "MonitoringMethod";

	public static final String Field_CLASSLOADER = "ClassLoaderString";

	public static final String Tag_METHOD = "Method";

	public static final String Field_NAME = "Name";

	public static final String Field_SIGNATURE = "Signature";

	public static final String Field_SUFFIX = "Suffix";

	public static final String Field_MAX_DUMP_COUNT = "MaxDumpCount";

	public static final String Field_CONDITION = "Condition";

	public static final String Tag_MONITOR_SIGNAL = "MonitoringSignal";

	public static final String Field_DUMP_KIND = "DumpKind";

	public static final String Tag_FILTERS = "Filters";

	public static final String Tag_FILTER = "Filter";

	public static final String Field_EXCLUDE_CLASSS = "ExcludeClass";

	public static final String Tag_EXCLUDE_PLACE = "ExcludePlace";

    public static final String Field_METHOD_SIGNATURE = "MethodSignature";

    public static final String Field_METHOD_NAME = "MethodName";

    //add by V3
    public static final String Filed_VALID = "Valid";
    public static final String Field_POSITION = "Position";
    //dump data
	public static final String Tag_DUMP_DATA = "DumpData";

	public static final String Field_STACK_DEPTH = "StackTraceDepth";

	public static final String Field_ATTRIBUTE_DEPTH = "AttributeNestDepth";

	public static final String Field_VARIABLE = "Variable";

	public static final String Field_ARGUMENT = "Argument";

	public static final String Field_ATTRIBUTE = "Attribute";

	public static final String Field_PUBLIC = "Public";

	public static final String Field_PACKAGE = "Package";

	public static final String Field_PROTECTED = "Protected";

	public static final String Field_PRIVATE = "Private";

	public static final String Field_MAX_ELEMENT_FOR_OBJECT = "MaxArrayElementForObject";

	public static final String Field_MAX_ELEMENT_FOR_PRIMITIVE = "MaxArrayElementForPrimitive";

	public static final String Field_MAIL = "Mail";

	public static final String Field_ATTACH_FILE = "AttachFile";

	public static final String Field_SAVE_DAYS = "SaveDays";

	public static final String Field_DUMP_INSTANCE = "DumpInstance";

	public static final String Field_DUMP_ALLTHREADS = "DumpAllThreads";

	public static final String Field_Dump_ThreadPriority = "ThreadPriority";

	//mail
	public static final String Tag_MAIL = "Mail";

	public static final String Field_FROM = "From";

	public static final String Field_FROM_NAME = "FromName";

	public static final String Field_TO = "To";

	public static final String Field_SUBJECT = "Subject";

	public static final String Field_BODY_TEMP_FOLDER_PATH = "BodyTemplateFolderPath";

	public static final String Field_IS_ATTACH = "IsAttach";

	public static final String Field_SMTP_SERVER_PORT = "SmtpServerPort";

	public static final String Field_SMTP_SERVER = "SmtpServer";

	public static final String Field_IS_AUTH = "IsAuth";

	public static final String Field_ACCOUNT = "Account";

	public static final String Field_PASSWORD = "Password";

	//DumpInstance

	public static final String Tag_DUMP_INSTANCE = "DumpInstance";

	public static final String Tag_INSTANCE = "Instance";

	public static final String Field_MAX_INSTANCE_COUNT = "MaxInstanceCount";

	//template
	public static final String Tag_OBJECT_ELEMENT = "ObjectElement";

	public static final String Tag_OBJECT = "Object";

	public static final String Field_VALID = "Valid";

	public static final String TAG_FIELD = "Field";

	//others
	public static final String Tag_OTHERS = "Others";

	public static final String Tag_DUMP_FILE = "DumpFile";

	public static final String Field_PATH = "Path";

	public static final String Field_PREFIX = "Prefix";

	public static final String Field_MIN_DISK_SPACE = "MinDiskSpace";

	public static final String Tag_CHECK_CONFIG = "CheckConfig";

	public static final String Field_SLEEP = "Sleep";

	public static final String Tag_MAX_DUMP_DATA = "MaxDumpData";

	public static final String Field_LIMIT = "Limit";

	//log
	public static final String Tag_LOG = "Log";

	public static final String Field_LEVEL = "Level";

	//重複ダンプ防止定義
	public static final String Tag_CHECK_DUPLICATION = "CheckDuplication";

	public static final String Field_DUPLICATION_THREAD_DIFF = "DumpDuplicationWhenThreadDiff";

	public static final String Field_TIME_LIMIT = "TimeLimit";

	//add by v3
	public static final String Field_THIS = "This";
	public static final String Field_TYPE = "Type";
	public static final String Field_DumpAllThreads = "DumpAllThreads";

	//ApplicationResourceに定義した「基本情報」のキー
	public static final String Tree_Item_BaseInfo = "Config.Tree.BaseInfor";
	//ApplicationResourceに定義した「タスク」のキー
	public static final String Tree_Item_TaskRegister_Root = "Config.Tree.MonitorTask";
	//ApplicationResourceに定義した「インスタンス」のキー
	public static final String Tree_Item_Object_Register = "Config.Tree.Instance";
	//ApplicationResourceに定義した「フィルター」のキー
	public static final String Tree_Item_Template_Register = "Config.Tree.Template";

}
