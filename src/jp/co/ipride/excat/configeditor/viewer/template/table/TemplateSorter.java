package jp.co.ipride.excat.configeditor.viewer.template.table;

import jp.co.ipride.excat.configeditor.model.template.Template;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

public class TemplateSorter extends ViewerSorter{
	
	public final static int COL_1 	= 1;
	public final static int COL_2 	= 2;
	
	private static boolean switch_sort_col_1 = true;
	private static boolean switch_sort_col_2 = true;

	private int criteria;

	/**
	 * construct
	 * @param criteria
	 */
	public TemplateSorter(int criteria){
		super();
		this.criteria=criteria;
	}
	
	/* (non-Javadoc)
	 * Method declared on ViewerSorter.
	 */
	public int compare(Viewer viewer, Object o1, Object o2) {

		Template obj1 = (Template) o1;
		Template obj2 = (Template) o2;

		switch (criteria) {
			case COL_1 :
				return compareClassNames(obj1, obj2);
			case COL_2 :
				return compareUses(obj1, obj2);
			default:
				return 0;
		}
	}
	
	private int compareClassNames(Template obj1, Template obj2){
		int n= collator.compare(obj1.getClassName(), obj2.getClassName());
		if (!switch_sort_col_1){
			n = -1*n;
			switch_sort_col_1=true;
		}else{
			switch_sort_col_1=false;
		}
		return n;
	}
	
	private int compareUses(Template obj1, Template obj2){
		int n;
		if (obj1.isUse()){
			if (obj2.isUse()){
				n= 0;
			}else{
				n= 1;
			}
		}else{
			if (obj2.isUse()){
				n= -1;
			}else{
				n= 0;
			}
		}
		if (!switch_sort_col_2){
			n = -1*n;
			switch_sort_col_2 = true;
		}else{
			switch_sort_col_2 = false;
		}
		return n;
	}
}
