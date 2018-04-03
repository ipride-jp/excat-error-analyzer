package jp.co.ipride.excat.configeditor.viewer.template.dialog.table;

import jp.co.ipride.excat.configeditor.model.template.Member;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

public class MemberSorter extends ViewerSorter{

	public final static int COL_1 	= 1;
	public final static int COL_2 	= 2;
	
	private static boolean switch_sort_col_1 = true;
	private static boolean switch_sort_col_2 = true;

	private int criteria;

	/**
	 * construct
	 * @param criteria
	 */
	public MemberSorter(int criteria){
		super();
		this.criteria=criteria;
	}
	
	/* (non-Javadoc)
	 * Method declared on ViewerSorter.
	 */
	public int compare(Viewer viewer, Object o1, Object o2) {

		Member obj1 = (Member) o1;
		Member obj2 = (Member) o2;

		switch (criteria) {
			case COL_1 :
				return compareNames(obj1, obj2);
			case COL_2 :
				return compareUses(obj1, obj2);
			default:
				return 0;
		}
	}
	
	private int compareNames(Member obj1, Member obj2){
		int n= collator.compare(obj1.getName(), obj2.getName());
		if (!switch_sort_col_1){
			n = -1*n;
			switch_sort_col_1=true;
		}else{
			switch_sort_col_1=false;
		}
		return n;
	}
	
	private int compareUses(Member obj1, Member obj2){
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
			switch_sort_col_2=true;
		}else{
			switch_sort_col_2 = false;
		}
		return n;
	}

}
