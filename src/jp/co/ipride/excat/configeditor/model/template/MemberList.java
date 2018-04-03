package jp.co.ipride.excat.configeditor.model.template;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import jp.co.ipride.excat.configeditor.model.ConfigContant;
import jp.co.ipride.excat.configeditor.viewer.template.dialog.table.IMemberListViewer;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * テンプレートのメンバーの配列
 * @author tu
 * @since 2007/12/5
 */
public class MemberList {

	private Vector<Member> members = new Vector<Member>();
	private Set<IMemberListViewer> changeListeners = new HashSet<IMemberListViewer>();

	public MemberList(){
		super();
	}

	public void init(){
		for (int i=members.size()-1; i>=0; i--){
			Member member = (Member)members.get(i);
			removeMember(member);
		}
	}

	/**
	 * [Object]タグを読込む。
	 * @param objectNode
	 */
	public void inputDocument(Node objectNode) {
		NodeList nodeList = objectNode.getChildNodes();
		for (int i=0; i<nodeList.getLength(); i++){
			Node node = nodeList.item(i);
			String name = node.getNodeName();
			if (ConfigContant.TAG_FIELD.equals(name)){
				Member member = new Member();
				member.inputDocument(node);
				members.add(members.size(),member);
				Iterator<IMemberListViewer> iterator = changeListeners.iterator();
				while (iterator.hasNext())
					((IMemberListViewer) iterator.next()).addMember(member);
			}
		}
	}

	/**
	 * Config
	 *     ObjectElement
	 *          Object   [Class][Valid]
	 *          	Field [Name][Valid]
	 * @param objectNode: [Object]
	 */
	public void outputDocument(Node objectNode){
		for (int i=0; i<members.size(); i++){
			Member member = (Member)members.get(i);
			member.outputDocument(objectNode);
		}
	}

	/**
	 * Return the collection of tasks
	 */
	public Vector<Member> getMembers() {
		return members;
	}

	public void addMember(){
		Member member = new Member();
		createMember(member);
	}

	public void addMember(String name){
		Member member = new Member();
		member.setName(name);
		createMember(member);
	}

	private void createMember(Member member){
		members.add(members.size(),member);
		Iterator<IMemberListViewer> iterator = changeListeners.iterator();
		while (iterator.hasNext())
			((IMemberListViewer) iterator.next()).addMember(member);
	}

	/**
	 * @param template
	 */
	public void removeMember(Member member) {
		members.remove(member);
		Iterator<IMemberListViewer> iterator = changeListeners.iterator();
		while (iterator.hasNext())
			((IMemberListViewer) iterator.next()).removeMember(member);
	}

	public void removeAll(){
		for (int i=members.size()-1; i>=0; i-- ){
			Member member =members.get(i);
			removeMember(member);
		}
	}

	/**
	 * @param template
	 */
	public void memberChanged(Member member) {
		Iterator<IMemberListViewer> iterator = changeListeners.iterator();
		while (iterator.hasNext())
			((IMemberListViewer) iterator.next()).updateMember(member);
	}

	/**
	 * @param viewer
	 */
	public void removeChangeListener(IMemberListViewer viewer) {
		changeListeners.remove(viewer);
	}

	/**
	 * @param viewer
	 */
	public void addChangeListener(IMemberListViewer viewer) {
		changeListeners.add(viewer);
	}

}
