package jp.co.ipride.excat.analyzer.viewer.searchviewer;

import java.net.URL;
import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.TreeItem;

import jp.co.ipride.excat.MainViewer;
import jp.co.ipride.excat.common.ApplicationResource;
import jp.co.ipride.excat.common.icons.IconFilePathConstant;

/**
 * 検索条件を表示
 * @author tu-ipride
 *
 */
public class ConditionCell implements ICell{

	private TopCell parent=null;

	private ConditionUnit conditionUnit;

	private List<FileCell> fileList;

	private static Image image_search = null;

	static{
		URL url = MainViewer.class.getResource(IconFilePathConstant.SEARCH_ITEM);
		image_search = ImageDescriptor.createFromURL(url).createImage();
	}
	public ConditionCell(){}

	public ConditionUnit getConditionUnit() {
		return conditionUnit;
	}

	public void setConditionUnit(ConditionUnit conditionUnit) {
		this.conditionUnit = conditionUnit;
	}

	public List<FileCell> getFileList() {
		return fileList;
	}

	public void setFileList(List<FileCell> fileList) {
		this.fileList = fileList;
	}

	public void setParent(TopCell topCell){
		this.parent=topCell;
	}

	/**
	 * 検索した結果を検索テーブルに表示
	 */
	public String getText(){
		String text = ApplicationResource.getResource("SearchView.Tab.Item");
		if (conditionUnit.getValue() !=null && !"".equals(conditionUnit.getValue())){
			text += ApplicationResource.getResource("SearchView.Tab.Value") + conditionUnit.getValue()+ "  ";
		}
		if (conditionUnit.getVarName() != null && !"".equals(conditionUnit.getVarName())){
			text += ApplicationResource.getResource("SearchView.Tab.Var") + conditionUnit.getVarName()+ "  ";
		}
		if (conditionUnit.getTypeName() != null && !"".equals(conditionUnit.getTypeName())){
			text += ApplicationResource.getResource("SearchView.Tab.Type")+conditionUnit.getTypeName();
		}
		if (conditionUnit.getObjectId() != null && !"".equals(conditionUnit.getObjectId())){
			text += ApplicationResource.getResource("SearchView.Tab.ObjectId")+conditionUnit.getObjectId();
		}
		if (conditionUnit.getText() != null && !"".equals(conditionUnit.getText())){
			text += conditionUnit.getText();
		}
		return text;
	}

	public Object[] getChildren() {
		FileCell[] fileCellArray = new FileCell[fileList.size()];
		for (int i=0; i<fileList.size(); i++){
			fileCellArray[i] = fileList.get(i);
		}
		return fileCellArray;
	}

	public Image getImage() {
		return image_search;
	}

	public Object getParent() {
		return parent;
	}

	public boolean hasChildren() {
		if (fileList.size()>0){
			return true;
		}else{
			return false;
		}
	}

	public TreeItem selectItem() {
		return null;
	}

}
