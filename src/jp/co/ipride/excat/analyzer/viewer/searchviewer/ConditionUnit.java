package jp.co.ipride.excat.analyzer.viewer.searchviewer;

import java.util.List;
import java.util.regex.Pattern;
import jp.co.ipride.excat.common.utility.HelperFunc;

/**
 * 検索用のユニット
 * @author tu-ipride
 * @version 3.0
 * @date 2009/10/9
 */
public class ConditionUnit {

	static String skip = "";

	public static String[] DEPTH = new String[]{
		"1",
		"2",
		"3",
		"4",
		"5",
		"6"
	};

	public static final int SEARCH_STACK=1;

	public static final int SEARCH_FILE=2;

	public static final int SEARCH_OBJECT=3;

	public static final int SEARCH_REPOSITORY=4;

	private String typeName = null;

	private String varName = null;

	private String value = null;

	private String text = null;

	private int depth = 5;

	private String objectId=null;

	private List<String> fileList = null;

	private int searchType = SEARCH_STACK;

	private Pattern  valuePattern = null;

	private Pattern  varNamePattern = null;

	private Pattern  typeNamePattern = null;


	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String clssName) {
		this.typeName = clssName;
	}

	public String getVarName() {
		return varName;
	}

	public void setVarName(String varName) {
		this.varName = varName;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public List<String> getFileList() {
		return fileList;
	}

	public void setFileList(List<String> fileList) {
		this.fileList = fileList;
	}

	public int getSearchType(){
		return searchType;
	}

	public void setSearchType(int type){
		this.searchType = type;
	}

	public int getDepth(){
		return depth;
	}

	public void setDepth(String d){
		try{
			depth=Integer.parseInt(d);
		}catch(Exception e){
		}
	}

	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	public void parseRule(){
		if (value != null && !"".equals(value)){
			this.valuePattern = Pattern.compile(parseRule(value));
		}
		if (varName != null && !"".equals(varName)){
			this.varNamePattern = Pattern.compile(parseRule(varName));
		}
		if (typeName != null && !"".equals(typeName)){
			this.typeNamePattern = Pattern.compile(parseRule(typeName));
		}
	}

	/**
	 * patternの規則：
	 * 　　　１．空白は任意文字列とする。
	 * 　　　２．他の内容は実内容とする
	 * @param keyWord
	 * @return
	 */
	private String parseRule(String keyWord){
		return HelperFunc.escapeRegex(keyWord);
	}

	/**
	 * 外部から呼び出す。
	 * @param node
	 * @return
	 */
	public boolean checkNode(ObjectUnit node){
		switch(this.searchType){
		case SEARCH_STACK:
			return  matchValue(node.getValue())
					&& matchVarName(node.getVarName())
					&& (matchTypeName(node.getDefTagName()) || matchTypeName(node.getRealType()) || matchTypeName(node.getDefType()));
		case SEARCH_FILE:
			return  matchValue(node.getValue())
					&& matchVarName(node.getVarName())
					&& (matchTypeName(node.getDefTagName()) || matchTypeName(node.getRealType()) || matchTypeName(node.getDefType()));
		case SEARCH_OBJECT:
			return matchObjectId(node.getObjectId());
		}
		return false;
	}

	private boolean matchValue(String value){
		if (valuePattern == null){
			return true;
		}else{
			boolean ret= this.valuePattern.matcher(value).matches();
//			if (ret){
//				System.out.println(value);
//			}
			return ret;
		}
	}

	private boolean matchVarName(String name){
		if (varNamePattern == null){
			return true;
		}else{
			boolean ret= this.varNamePattern.matcher(name).matches();
			return ret;
		}
	}

	private boolean matchTypeName(String name){
		if (name == null || "".equals(name)){
			return false;
		}
		if (typeNamePattern == null){
			return true;
		}else{
			return this.typeNamePattern.matcher(name).matches();
		}
	}

	private boolean matchObjectId(String id){
		if (this.objectId == null){
			return false;
		}else{
			return this.objectId.equals(id);
		}
	}

//	private boolean match(String target, List<String> rule){
//
//		int fromIndex = 0;
//
//		for (int i=0; i<rule.size(); i++){
//			String word = rule.get(i);
//
//			if (word.equals(skip)){
//				//スキップできる場合、
//				if (i+1 < rule.size()){
//					//次の要素へ
//					String nextWord = rule.get(i+1);
//					int begin = target.indexOf(nextWord, fromIndex);
//
//					if (begin < 0){
//						return false;
//					}else{
//						fromIndex = begin + nextWord.length();
//						i++;
//						//最後の項目
//						if (i == rule.size()-1){
//							if (fromIndex < target.length()){
//								//余りがある場合NG
//								return false;
//							}
//						}
//					}
//				}else{
//					//該当スキップは最後の要素です。処理終了。
//				}
//			}else{
//				int begin = target.indexOf(word, fromIndex);
//
//				//開始時
//				if (i==0){
//					if (begin == 0){
//						fromIndex = begin + word.length();
//					}else{
//						return false;
//					}
//				}
//				//終了時
//				if (i==rule.size()-1){
//					if (begin + word.length()==target.length()){
//						return true;
//					}else{
//						return false;
//					}
//				}
//				//その以外
//				if (begin<0){
//					return false;
//				}else{
//					fromIndex = begin + word.length();
//				}
//			}
//		}
//		return true;
//	}

}
