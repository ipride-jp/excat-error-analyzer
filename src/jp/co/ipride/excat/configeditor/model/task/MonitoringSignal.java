package jp.co.ipride.excat.configeditor.model.task;

import jp.co.ipride.excat.configeditor.model.ConfigContant;
import jp.co.ipride.excat.configeditor.model.ConfigModel;
import jp.co.ipride.excat.configeditor.util.DocumetUtil;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * シグナル監視型のタスク
 * @author tu
 * @since 2007/11/19
 */
public class MonitoringSignal extends Monitoring{

	public static final int STACK = 1;		//全てのタスクをダンプする

	public static final int INSTANCE = 2;	//特定オブジェクトをダンプする。

	private int dumpKind = STACK;  //ダンプタイプ

	/**
	 * [MonitoringSignal]タグを読み込む
	 */
	public void inputDocument(Node signalNode) {
		NamedNodeMap map = signalNode.getAttributes();
		Node attrNode = map.getNamedItem(ConfigContant.Field_DUMP_KIND);
		if (attrNode != null){
			String kind = attrNode.getNodeValue();
			dumpKind = Integer.parseInt(kind);
		}
		inputValidAttribute(signalNode);
	}

	/**
	 * output a [MonitorTargets] tag under [Config] tag.
	 * the [MonitorTargets] must have a [MonitoringSignal] monitoring signal.
	 * @param monitoringTargetsNode: [MonitoringTargets]
	 */
	public void outputDocument(Node monitoringTargetsNode) {
		Node monitoringSignalNode = ConfigModel.getDocument().createElement(ConfigContant.Tag_MONITOR_SIGNAL);
		monitoringTargetsNode.appendChild(monitoringSignalNode);
		((Element)monitoringSignalNode).setAttribute(ConfigContant.Field_DUMP_KIND, Integer.toString(dumpKind));
		DocumetUtil.setAttribute(monitoringSignalNode, ConfigContant.Field_VALID, this.use);
	}


	public void setDumpKind(int dumpKind){
		this.dumpKind = dumpKind;
		ConfigModel.setChanged();
	}

	public int getDumpKind(){
		return dumpKind;
	}

	public void copyTo(Monitoring clone){
		((MonitoringSignal)clone).dumpKind = this.dumpKind;
		((MonitoringSignal)clone).use = this.use;
	}
}
