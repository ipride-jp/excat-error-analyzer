package jp.co.ipride.excat.configeditor.model.template;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import jp.co.ipride.excat.configeditor.model.ConfigContant;
import jp.co.ipride.excat.configeditor.model.ConfigModel;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class TemplateList {

	private Vector<Template> templates = new Vector<Template>();
	private Set<ITemplateListViewer> changeListeners = new HashSet<ITemplateListViewer>();

	public TemplateList(){
		super();
	}

	public void init(){
		for (int i=templates.size()-1; i>=0; i--){
			Template template = (Template)templates.get(i);
			removeTemplate(template);
		}
	}

	/**
	 * [ObjectElement]É^ÉOÇì«çûÇﬁÅB
	 * @param objectElementNode
	 */
	public void inputDocument(Node objectElementNode) {
		NodeList nodeList = objectElementNode.getChildNodes();
		for (int i=0; i<nodeList.getLength(); i++){
			Node node = nodeList.item(i);
			String name = node.getNodeName();
			if (ConfigContant.Tag_OBJECT.equals(name)){
				Template template = new Template();
				template.inputDocument(node);
				templates.add(templates.size(),template);
				Iterator<ITemplateListViewer> iterator = changeListeners.iterator();
				while (iterator.hasNext())
					iterator.next().addTemplate(template);
			}
		}
	}

	/**
	 * Config
	 *     ObjectElement
	 *          Object   [Class][Valid]
	 *          	Field [Name][Valid]
	 * @param root: [Config]
	 */
	public void outputDocument(Node root){
		if (templates.size()>0){
			Node objectElementNode = ConfigModel.getDocument().createElement(ConfigContant.Tag_OBJECT_ELEMENT);
			root.appendChild(objectElementNode);
			for (int i=0; i<templates.size(); i++){
				Template template = templates.get(i);
				template.outputDocument(objectElementNode);
			}
		}

	}

	/**
	 * Return the collection of tasks
	 */
	public Vector<Template> getTemplates() {
		return templates;
	}

	public void addTemplate(){
		Template template = new Template();
		templates.add(templates.size(),template);
		Iterator<ITemplateListViewer> iterator = changeListeners.iterator();
		while (iterator.hasNext()) {
			iterator.next().addTemplate(template);
			ConfigModel.setChanged();
		}
	}

	/**
	 * @param template
	 */
	public void removeTemplate(Template template) {
		templates.remove(template);
		Iterator<ITemplateListViewer> iterator = changeListeners.iterator();
		while (iterator.hasNext())
			iterator.next().removeTemplate(template);
	}

	/**
	 * @param template
	 */
	public void templateChanged(Template template) {
		Iterator<ITemplateListViewer> iterator = changeListeners.iterator();
		while (iterator.hasNext())
			 iterator.next().updateTemplate(template);
	}

	/**
	 * @param viewer
	 */
	public void removeChangeListener(ITemplateListViewer viewer) {
		changeListeners.remove(viewer);
	}

	/**
	 * @param viewer
	 */
	public void addChangeListener(ITemplateListViewer viewer) {
		changeListeners.add(viewer);
	}

}
