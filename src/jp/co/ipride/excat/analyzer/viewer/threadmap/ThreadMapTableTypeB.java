package jp.co.ipride.excat.analyzer.viewer.threadmap;

import java.util.ArrayList;
import java.util.List;

import jp.co.ipride.excat.analyzer.common.SAXParser;
import jp.co.ipride.excat.common.ApplicationResource;
import jp.co.ipride.excat.common.utility.HelperFunc;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

/**
 * スレッドダンプは2組以上の場合に適用する。
 * @author tu-ipride
 * @version 3.0
 * @date 2009/10/6
 */
public class ThreadMapTableTypeB extends AbstractThreadMapTable{

	private List<TableColumn> columnList = new ArrayList<TableColumn>();

	private List<ThreadMapItemTypeB> itemList = new ArrayList<ThreadMapItemTypeB>();

	public ThreadMapTableTypeB(Composite composite, int style) {
		super(composite, style);
		this.tableViewer = new TableViewer(this, style|SWT.FULL_SELECTION);
		final Table table = tableViewer.getTable();
		table.setLinesVisible(true);
		table.setHeaderVisible(true);

		GridData gridData = new GridData();
		gridData.verticalAlignment = GridData.FILL;
		gridData.verticalSpan = 2;
		gridData.widthHint = 500;
		gridData.heightHint = 300;
		table.setLayoutData(gridData);

		tableViewer.setContentProvider(new TableContentProviderB());
		tableViewer.setLabelProvider(new TableLabelProviderB());

		// add action to display file.
		table.addListener(SWT.MouseDoubleClick, new Listener() {
			public void handleEvent(Event event) {
				Point pt = new Point(event.x, event.y);
				TableItem item = table.getItem(pt);
				if (item == null)
					return;
				for (int i = 0; i < table.getColumnCount(); i++) {
					Rectangle rect = item.getBounds(i);
					if (rect.contains(pt)) {
						int index = table.indexOf(item);
						ThreadMapItemTypeB itemTypeB = (ThreadMapItemTypeB)tableViewer.getElementAt(index);
						if (i - 1 <=columnList.size()) {
	                        String filePath = itemTypeB.getFilePath(i-1);
	                        if (filePath != null && filePath.length() != 0) {
		        				try {
		        					mainvw.openXmlFile(filePath);
		        				} catch (Exception e) {
		        					HelperFunc.logException(e);
		        				}
	                        }
						}
					}
				}
			}
		});

		packColumns();
	}

	@Override
	public void setThreadDumpData(List<List<String>> data) {
		int size = data.size();
		ThreadMapItemTypeB.listSize = size;
		Table table = tableViewer.getTable();
		TableColumn column = new TableColumn(table, SWT.NONE);
		column.setText(
				ApplicationResource.getResource("Tab.ThreadMap.Column."+COLUMN_NAMES[0]));
		column.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				TableColumn column = (TableColumn)event.getSource();
				for (int j=0; j<columnList.size(); j++){
					if (columnList.get(j).equals(column)){
						tableViewer.setSorter(new TypeASorter(j));
						break;
					}
				}
			}
		});
		columnList.add(column);
		for (int i =0; i<size; i++){
			column = new TableColumn(table, SWT.NONE);
			columnList.add(column);
			column.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					TableColumn column = (TableColumn)event.getSource();
					for (int j=0; j<columnList.size(); j++){
						if (columnList.get(j).equals(column)){
							tableViewer.setSorter(new TypeASorter(j));
							break;
						}
					}
				}
			});
		}
		//各フォルダの解析結果をList<TypeB>に格納
		List<List<ThreadMapItemTypeA>> typeAListList = new ArrayList<List<ThreadMapItemTypeA>>();
		for (int i=0; i<data.size(); i++){
			List<String> time = data.get(i);
			List<ThreadMapItemTypeA> typeAList = new ArrayList<ThreadMapItemTypeA>();
			for (int j=0; j<time.size(); j++){
				String filePath = time.get(j);
				ThreadMapItemTypeA item = SAXParser.getThreadMapItemTypeA(filePath);
				//最初のスレッドの時刻をヘッドに設定
				if (j==0){
					columnList.get(i+1).setText(item.getDumpTime());
				}
				updateThreadMapItemTypeB(i, item);
			}
			typeAListList.add(typeAList);
		}

		tableViewer.setInput(itemList);
		packColumns();
	}

	/**
	 * typeBの追加
	 * @param list
	 */
	private void updateThreadMapItemTypeB(int index, ThreadMapItemTypeA typeA){
		ThreadMapItemTypeB typeB;
		String threadName = typeA.getThreadName();
		boolean hasSameThread = false;
		for (int i=0; i<itemList.size(); i++){
			typeB = itemList.get(i);
			if (typeB.getThreadName() != null && typeB.getThreadName().equals(threadName)){
				typeB.setStatus(index, typeA.getStatus());
				typeB.setFilePathList(index, typeA.getFilePath());
				hasSameThread = true;
				break;
			}
		}
		if (!hasSameThread){
			typeB = new ThreadMapItemTypeB();
			typeB.setThreadName(typeA.getThreadName());
			typeB.setFilePathList(index, typeA.getFilePath());
			typeB.setStatus(index, typeA.getStatus());
			itemList.add(typeB);
		}
	}

	/**
	 * type B content provider.
	 * @author tu-ipride
	 * @version 3.0
	 * @date 2009/10/6
	 */
	class TableContentProviderB implements IStructuredContentProvider{
		public Object[] getElements(Object inputElement) {
			List array = (List)inputElement;
            return array.toArray();
		}

		public void dispose() {
		}

		public void inputChanged(Viewer arg0, Object arg1, Object arg2) {
		}
	}

	/**
	 * type B
	 * @author tu-ipride
	 * @version 3.0
	 * @since 2009/10/6
	 */
	class TableLabelProviderB implements ITableLabelProvider{

		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		public String getColumnText(Object element, int columnIndex) {
			ThreadMapItemTypeB item = (ThreadMapItemTypeB)element;
			if (columnIndex ==0){
				return item.getThreadName();
			}else{
				return item.getStatus(columnIndex-1);
			}
		}

		public void addListener(ILabelProviderListener listener) {
		}

		public void dispose() {
		}

		public boolean isLabelProperty(Object arg0, String arg1) {
			return false;
		}

		public void removeListener(ILabelProviderListener arg0) {
		}
	}
	class TypeASorter extends ViewerSorter {
		int column;
		public TypeASorter(int column){
			this.column=column;
		}
        public int compare(Viewer viewer, Object e1, Object e2) {
        	ThreadMapItemTypeB p1 = (ThreadMapItemTypeB) e1;
        	ThreadMapItemTypeB p2 = (ThreadMapItemTypeB) e2;
        	if (column ==0){
           		return p1.getThreadName().compareTo(p2.getThreadName());
        	}else{
           		return p1.getStatus(column-1).compareTo(p2.getStatus(column-1));
        	}
        }
	}
}
