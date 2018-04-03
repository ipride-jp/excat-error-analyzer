package jp.co.ipride.excat.configeditor.model.instance;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import jp.co.ipride.excat.configeditor.model.ConfigContant;
import jp.co.ipride.excat.configeditor.model.ConfigModel;
import jp.co.ipride.excat.configeditor.viewer.instance.table.IObjectLineListViewer;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * 特定オブジェクト・リスト
 * @author tu
 * @since 2007/11/14
 */
public class ObjectLineList {

	private Vector<ObjectLine> objectLines = new Vector<ObjectLine>();
	private Set<IObjectLineListViewer> changeListeners = new HashSet<IObjectLineListViewer>();

	/**
	 * コンストラクタ
	 *
	 */
	public ObjectLineList(){
		super();
	}

	/**
	 * 初期化
	 *
	 */
	public void init(){
		for (int i=objectLines.size()-1; i>=0; i--){
			ObjectLine objectLine = (ObjectLine)objectLines.get(i);
			removeObjectLine(objectLine);
		}

	}

	/**
	 * [DumpInstance]タグを読込む
	 * @param dumpInstanceNode
	 */
	public void inputDocument(Node dumpInstanceNode) {
		NodeList nodeList = dumpInstanceNode.getChildNodes();
		for (int i=0; i<nodeList.getLength(); i++){
			Node node = nodeList.item(i);
			String name = node.getNodeName();
			if (ConfigContant.Tag_INSTANCE.equals(name)){
				ObjectLine objectLine = new ObjectLine();
				objectLine.inputDocument(node);
				objectLines.add(objectLines.size(),objectLine);
				Iterator<IObjectLineListViewer> iterator = changeListeners.iterator();
				while (iterator.hasNext())
					iterator.next().addObjectLine(objectLine);
			}
		}
	}

	/**
	 * ＤＯＭ作成
	 * Config
	 *    DumpInstance
	 *        Instance [Class][ClassLoaderString][MaxInstanceCount][Valid]
	 * @param root：　[Config]
	 */
	public void outputDocument(Node root){
		if (objectLines.size() >0){
			Node dumpInstanceNode = ConfigModel.getDocument().createElement(ConfigContant.Tag_DUMP_INSTANCE);
			root.appendChild(dumpInstanceNode);
			for (int i=0; i<objectLines.size(); i++){
				ObjectLine objectLine = (ObjectLine)objectLines.get(i);
				objectLine.outputDocument(dumpInstanceNode);
			}
		}
	}


	/**
	 * Return the collection of object-lines
	 */
	public Vector<ObjectLine> getObjectLines() {
		return objectLines;
	}

	/**
	 * Add a new object-line to the collection of object-lines
	 */
	public void addObjectLine() {
		ObjectLine objectLine = new ObjectLine();
		objectLines.add(objectLines.size(),objectLine);
		Iterator<IObjectLineListViewer> iterator = changeListeners.iterator();
		while (iterator.hasNext()) {
			iterator.next().addObjectLine(objectLine);
			ConfigModel.setChanged();
		}
	}

	/**
	 * remove the object-line from collection
	 * @param objectLine
	 */
	public void removeObjectLine(ObjectLine objectLine) {
		objectLines.remove(objectLine);
		Iterator<IObjectLineListViewer> iterator = changeListeners.iterator();
		while (iterator.hasNext())
			iterator.next().removeObjectLine(objectLine);
	}

	/**
	 * オブジェクト更新
	 * @param objectLine
	 */
	public void objectLineChanged(ObjectLine objectLine) {
		Iterator<IObjectLineListViewer> iterator = changeListeners.iterator();
		while (iterator.hasNext())
			iterator.next().updateObjectLine(objectLine);
	}

	/**
	 * Listenerを削除
	 * @param viewer
	 */
	public void removeChangeListener(IObjectLineListViewer viewer) {
		changeListeners.remove(viewer);
	}

	/**
	 * Listenerを追加
	 * @param viewer
	 */
	public void addChangeListener(IObjectLineListViewer viewer) {
		changeListeners.add(viewer);
	}
}
