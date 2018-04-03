package jp.co.ipride.excat.analyzer.viewer.searchviewer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.co.ipride.ExcatLicenseException;
import jp.co.ipride.excat.analyzer.common.DOMParser;
import jp.co.ipride.excat.analyzer.common.DumpFileXmlConstant;
import jp.co.ipride.excat.common.setting.SettingManager;
import jp.co.ipride.excat.common.setting.SettingUtility;
import jp.co.ipride.excat.common.utility.HelperFunc;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * this is a engine for xml file.
 * it will be used to search dump files of excat.
 * @author tu-ipride
 * @version 3.0
 * @since 2009/10/13
 */
public class TextSearchEngine {

	private ConditionUnit conditionUnit;

	private Document document;

	private NodeList objectNodeList;

	private NodeList methodNodeList;

	//all of top object of object-pool.
	private List<ObjectUnit> poolObjectList= new ArrayList<ObjectUnit>();

	//refernce id -> reference object unit.
	private Map<String, List<ObjectUnit>> objectRefMap = new HashMap<String, List<ObjectUnit>>();

	//this is a collection which will be used to search.
	private List<ObjectUnit> targetObjectList = new ArrayList<ObjectUnit>();

	/**
	 * construct
	 */
	public TextSearchEngine(ConditionUnit conditionUnit){
		this.conditionUnit = conditionUnit;
		ObjectUnit.MAX_DEPTH = conditionUnit.getDepth();
	}

	/**
	 * search one dump data.
	 * @param textSearchUnit
	 */
	public List<MethodCell> searchOneDumpData(String path)throws ExcatLicenseException{
		document = DOMParser.getDocument(path);
		Element DumpNode = document.getDocumentElement();
		Element oPoolNode = (Element)DumpNode.getElementsByTagName(DumpFileXmlConstant.NODE_OBJECT_POOL).item(0);
		objectNodeList = oPoolNode.getElementsByTagName(DumpFileXmlConstant.NODE_OBJECT);
		methodNodeList = DumpNode.getElementsByTagName(DumpFileXmlConstant.NODE_METHOD);

		List<MethodCell> result = null;

		//at first, we build the object structure for object-pool.
		scanObjectPool();

		//second, we analyze the method variants and connect to the object structure of pool.
		List<MethodUnit> methodList = scanMethod();

		//then, we search above object alone the search condition.
		int matchCount = matchObject();

		//last, we have to clear
		if (matchCount >0 ){
			result = extracRoot(methodList);
		}

		clearAll();

		return result;
	}

	/**
	 * we do search using target collection.
	 * if object unit was checked. it will be marked.
	 */
	private int matchObject(){
		int matchCount = 0;
		for (ObjectUnit objectUnit: targetObjectList){
			boolean ret = conditionUnit.checkNode(objectUnit);
			if (ret){
				matchCount++;
			}
			objectUnit.setMatched(ret);
		}
		return matchCount;
	}

	/**
	 * search each method. if this method haven't any match,we will kill this method
	 * from the method list.
	 * @param methodList
	 */
	private List<MethodCell> extracRoot(List<MethodUnit> methodList){
		List<MethodCell> list = new ArrayList<MethodCell>();
		for (int i=0; i<methodList.size();i++){
			resetObjectUnit();
			MethodUnit methodUnit = methodList.get(i);
			MethodCell ret = methodUnit.extracRoot();
			if (ret.getLocalVarArray().size()>0){
				list.add(ret);
			}
		}

		return list;
	}

	/**
	 * To protect recurrence reference during a tree,
	 * before searching, we set all of object-unit 'searched' to false.
	 */
	private void resetObjectUnit(){
		for (ObjectUnit objectUnit: targetObjectList){
			objectUnit.setSearched(false);
		}
	}

	/**
	 * メソッドのローカル変数を検索していく
	 * @return　マッチしたメソッド
	 */
	private List<MethodUnit> scanMethod(){
		List<MethodUnit> result = new ArrayList<MethodUnit>();

		for (int i=0; i<methodNodeList.getLength(); i++){
			MethodUnit methodUnit = new MethodUnit();
			Element method = (Element)methodNodeList.item(i);
			methodUnit.setSequence(i);
			methodUnit.setMethodNode(method);
			methodUnit.addParaList(scanLocalVar(method,DumpFileXmlConstant.NODE_ARGUMENT));
			methodUnit.addParaList(scanLocalVar(method,DumpFileXmlConstant.NODE_VARIABLE));
			methodUnit.addParaList(scanLocalVar(method,DumpFileXmlConstant.NODE_THIS));
			methodUnit.addParaList(scanLocalVar(method,DumpFileXmlConstant.NODE_MONITOROBJECT));
			methodUnit.addParaList(scanLocalVar(method,DumpFileXmlConstant.NODE_RETURN));
			methodUnit.addParaList(scanLocalVar(method,DumpFileXmlConstant.NODE_EXCEPTION_OBJECT));
			methodUnit.addParaList(scanLocalVar(method,DumpFileXmlConstant.NODE_CONTEND_MONITOR_OBJECT));

			if ( methodUnit.getParaSize() > 0 ){
				result.add(methodUnit);
			}else{
				methodUnit.clear();
			}
		}
		return result;
	}

	/**
	 * １．ローカル変数の自身のチェック
	 * ２．参照する、継承するオブジェクトのチェック
	 * @param method チェックするメソッド
	 * @param type　　ローカル変数のタイプ
	 * @return
	 */
	private List<ObjectUnit> scanLocalVar(Element method, String type){
		List<ObjectUnit> result = new ArrayList<ObjectUnit>();
		ObjectUnit objectUnit;
		NodeList localVarList=method.getElementsByTagName(type);
		for (int i=0; i<localVarList.getLength(); i++){
			Node localVarNode = localVarList.item(i);
			objectUnit = new ObjectUnit();
			objectUnit.setSequence(i);
			objectUnit.setDefiniteNode(localVarNode);
			String ref_id = objectUnit.getObjectReferenceId();
			if (ref_id != null  && !"".equals(ref_id)){
				ObjectUnit realObjectUnit = getRealObjectNode(ref_id);
				if (realObjectUnit != null){
					objectUnit.setRealObject(realObjectUnit.getRealNode());
					objectUnit.addRelationObjectList(realObjectUnit.getRelationObjectList());
				}
			}
			result.add(objectUnit);
			targetObjectList.add(objectUnit);
		}
		return result;
	}

	private ObjectUnit getRealObjectNode(String objectId){
		for (int i=0; i<poolObjectList.size(); i++){
			ObjectUnit objectUnit = poolObjectList.get(i);
			String object_id = SearchUtility.getObjectId((Element)objectUnit.getRealNode());
			if (objectId.equals(object_id)){
				return objectUnit;
			}
		}
		return null;
	}


	/**
	 * POOLのオブジェクトの構造を構築し、チェックする。
	 */
	private void scanObjectPool(){
		//ツリー構造を作成
		for (int i=0; i<objectNodeList.getLength(); i++){
			Element element = (Element)objectNodeList.item(i);
			ObjectUnit objectUnit = new ObjectUnit();
			//set as a real object
			objectUnit.setRealObject(element);
			//scan others under this object.
			scanSubObject(element,objectUnit);
			//this is a list of top object.
			poolObjectList.add(objectUnit);
		}
		//参照関係を構築
		for (ObjectUnit objectUnit: poolObjectList){
			String objectId = objectUnit.getObjectId();
			if (objectId != null  && !"".equals(objectId)){
				List<ObjectUnit> objUnitList = objectRefMap.get(objectId);
				//if i can get the reference object
				if (objUnitList != null){
					for (int i=0; i<objUnitList.size(); i++){
						ObjectUnit refObjectUnit = objUnitList.get(i);
						//set real object's node into the reference object.
						refObjectUnit.setRealObject(objectUnit.getRealNode());
						//set real object's sub object into reference object.
						refObjectUnit.addRelationObjectList(objectUnit.getRelationObjectList());
					}
				}
			}
		}
	}

	private void scanSubObject(Element parent, ObjectUnit parentUnit){
		NodeList attrArray = parent.getElementsByTagName(DumpFileXmlConstant.NODE_ATTRIBUTE);
		//1. process attribute tag for this object's field.
		for (int i=0; i<attrArray.getLength(); i++){
			Element attrElement = (Element)attrArray.item(i);
			ObjectUnit objectUnit = new ObjectUnit();
			objectUnit.setSequence(i);
			//save as def object.
			objectUnit.setDefiniteNode(attrElement);
			//add the field object to parent object.
			parentUnit.addRelationObject(objectUnit);
			//extrac the reference id of top object
			String obj_Ref_id = SearchUtility.getObjectRefId(attrElement);
			if (obj_Ref_id != null && !"".equals(obj_Ref_id)){
				List<ObjectUnit> objUnitList;
				objUnitList = objectRefMap.get(obj_Ref_id);
				if (objUnitList == null){
					objUnitList = new ArrayList<ObjectUnit>();
					objUnitList.add(objectUnit);
					objectRefMap.put(obj_Ref_id, objUnitList);
				}else{
					objUnitList.add(objectUnit);
				}
			}
			//as a search traget.
			targetObjectList.add(objectUnit);
			//scan the field object's field.
			scanSubObject(attrElement,objectUnit);
		}
		// if the parent is a array type.
		NodeList itemArray = parent.getElementsByTagName(DumpFileXmlConstant.NODE_ITEM);
		for (int i=0; i<itemArray.getLength(); i++){
			Element itemElement = (Element)itemArray.item(i);
			ObjectUnit objectUnit = new ObjectUnit();
			objectUnit.setSequence(i);
			objectUnit.setDefiniteNode(itemElement);
			parentUnit.addRelationObject(objectUnit);

			String obj_Ref_id = SearchUtility.getObjectRefId(itemElement);
			if (obj_Ref_id != null && !"".equals(obj_Ref_id)){
				List<ObjectUnit> objUnitList;
				objUnitList = objectRefMap.get(obj_Ref_id);
				if (objUnitList == null){
					objUnitList = new ArrayList<ObjectUnit>();
					objUnitList.add(objectUnit);
					objectRefMap.put(obj_Ref_id, objUnitList);
				}else{
					objUnitList.add(objectUnit);
				}
			}
			targetObjectList.add(objectUnit);
			scanSubObject(itemElement,objectUnit);
		}
		//if the parent has a super-object.
		NodeList superArray = parent.getElementsByTagName(DumpFileXmlConstant.NODE_SUPERCLASS);
		for (int i=0; i<superArray.getLength(); i++){
			Element superElement = (Element)superArray.item(i);
			ObjectUnit objectUnit = new ObjectUnit();
			objectUnit.setSequence(i);
			objectUnit.setDefiniteNode(superElement);
			parentUnit.addRelationObject(objectUnit);

			String obj_Ref_id = SearchUtility.getObjectRefId(superElement);
			if (obj_Ref_id != null && !"".equals(obj_Ref_id)){
				List<ObjectUnit> objUnitList;
				objUnitList = objectRefMap.get(obj_Ref_id);
				if (objUnitList == null){
					objUnitList = new ArrayList<ObjectUnit>();
					objUnitList.add(objectUnit);
					objectRefMap.put(obj_Ref_id, objUnitList);
				}else{
					objUnitList.add(objectUnit);
				}
			}
			targetObjectList.add(objectUnit);
			scanSubObject(superElement,objectUnit);
		}
	}

	public void clearAll(){
		methodNodeList=null;
		objectNodeList = null;
		document = null;
		targetObjectList.clear();
		objectRefMap.clear();
		poolObjectList.clear();
	}

	/**
	 * リポジトリのjavaソースを検索するメソッド
	 *
	 * @param keyword キーワード
	 * @return 検索結果Beanクラスのリスト
	 */
	public List<MatchedFileUnit> searchRepository(String keyword) {
		List<MatchedFileUnit> fileList = new ArrayList<MatchedFileUnit>();

		List<String> javaFileList = SettingManager.getRepository().getJavaFileList();
		for (String fileName : javaFileList) {
			List<Match> list = matchJavaFileContents(fileName, keyword);
			if (list.size() > 0) {
				MatchedFileUnit unit = new MatchedFileUnit(fileName);
				unit.setMatchList(list);
				fileList.add(unit);
			}
		}
		return fileList;
	}

	/**
	 * Javaソース内容を検索するメソッド
	 *
	 * @param path ファイル名
	 * @param keyword キーワード
	 * @return 検索結果リスト
	 */
	private List<Match> matchJavaFileContents(String fileName, String keyword) {
		List<Match> list = new ArrayList<Match>();
		String fileContents = SettingUtility.getContents(fileName);
		if (fileContents != null){
			Pattern p = Pattern.compile(HelperFunc.escapeRegex(keyword));
			Matcher m = p.matcher(fileContents);
			while (m.find()) {
				int start= m.start();
				int end= m.end();
				list.add(new Match(fileName, start, end - start));
			}
		}
		return list;
	}
}
