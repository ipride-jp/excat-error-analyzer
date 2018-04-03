package jp.co.ipride.excat.configeditor.viewer.instance.table;

import jp.co.ipride.excat.configeditor.model.instance.ObjectLine;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

public class ObjectLineSorter extends ViewerSorter{
	
	public final static int COL_1 	= 1;
	public final static int COL_2 	= 2;
	public final static int COL_3 	= 3;
	public final static int COL_4 	= 4;
	
	
	private static boolean switch_sort_col_1 = true;
	private static boolean switch_sort_col_2 = true;
	private static boolean switch_sort_col_3 = true;
	private static boolean switch_sort_col_4 = true;

	private int criteria;

	/**
	 * construct
	 * @param criteria
	 */
	public ObjectLineSorter(int criteria){
		super();
		this.criteria=criteria;
	}
	
	/* (non-Javadoc)
	 * Method declared on ViewerSorter.
	 */
	public int compare(Viewer viewer, Object o1, Object o2) {

		ObjectLine obj1 = (ObjectLine) o1;
		ObjectLine obj2 = (ObjectLine) o2;

		switch (criteria) {
			case COL_1 :
				return compareClassNames(obj1, obj2);
			case COL_2 :
				return compareClassLoad(obj1, obj2);
			case COL_3 :
				return compareMaxSizes(obj1, obj2);
			case COL_4 :
				return compareUses(obj1, obj2);
			default:
				return 0;
		}
	}
	
	private int compareClassNames(ObjectLine obj1, ObjectLine obj2){
		int n= collator.compare(obj1.getClassName(), obj2.getClassName());
		if (!switch_sort_col_1){
			n = -1*n;
			switch_sort_col_1 = true;
		}else{
			switch_sort_col_1 = false;
		}
		return n;
	}

	private int compareClassLoad(ObjectLine obj1, ObjectLine obj2){
		int n= collator.compare(obj1.getMaxSize(), obj2.getMaxSize());
		if (!switch_sort_col_2){
			n = -1*n;
			switch_sort_col_2 = true;
		}else{
			switch_sort_col_2 = false;
		}
		return n;
	}
	
	private int compareMaxSizes(ObjectLine obj1, ObjectLine obj2){
		int n= collator.compare(obj1.getMaxSize(), obj2.getMaxSize());
		if (!switch_sort_col_3){
			n = -1*n;
			switch_sort_col_3 = true;
		}else{
			switch_sort_col_3 = false;
		}
		return n;
	}
	
	private int compareUses(ObjectLine obj1, ObjectLine obj2){
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
		if (!switch_sort_col_4){
			n = -1*n;
			switch_sort_col_4 = true;
		}else{
			switch_sort_col_4 = false;
		}
		return n;
	}
}
