package jp.co.ipride.excat.analyzer.viewer.threadmap;

import java.util.ArrayList;
import java.util.Arrays;
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
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;


public class ThreadMapTableTypeA extends AbstractThreadMapTable {

	private List<TableColumn> columnList = new ArrayList<TableColumn>();
	/**
	 *
	 * @param parent
	 * @param style
	 */
	public ThreadMapTableTypeA(Composite parent, int style) {
		super(parent, style);

		this.tableViewer = new TableViewer(this, style);
		Table table = tableViewer.getTable();
		table.setLinesVisible(true);
		table.setHeaderVisible(true);

		for (int i =0;i<COLUMN_NAMES.length; i++){
			TableColumn column = new TableColumn(table, SWT.NONE);
			column.setText(
					ApplicationResource.getResource("Tab.ThreadMap.Column."+COLUMN_NAMES[i]));
			columnList.add(i, column);
			column.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					TableColumn column = (TableColumn)event.getSource();
					for (int i=0; i<columnList.size(); i++){
						if (columnList.get(i).equals(column)){
							tableViewer.setSorter(new TypeASorter(i));
							break;
						}
					}
				}
			});
		}

		GridData gridData = new GridData();
		gridData.verticalAlignment = GridData.FILL;
		gridData.verticalSpan = 2;
		gridData.widthHint = 500;
		gridData.heightHint = 300;
		table.setLayoutData(gridData);

		tableViewer.setContentProvider(new TableContentProviderA());
		tableViewer.setLabelProvider(new TableLabelProviderA());

		tableViewer.addDoubleClickListener(new IDoubleClickListener(){
             public void doubleClick(DoubleClickEvent event) {
                IStructuredSelection sel = (IStructuredSelection)event.getSelection();
                ThreadMapItemTypeA item = (ThreadMapItemTypeA)sel.getFirstElement();
                String filePath = item.getFilePath();
				try {
					mainvw.openXmlFile(filePath);
				} catch (Exception e) {
					HelperFunc.logException(e);
				}
             }
          });

	}

	/**
	 * call by dialog throw analyzeForm.
	 * @param data
	 */
	public void setThreadDumpData(List<List<String>> data){
		List<ThreadMapItemTypeA> itemList = new ArrayList<ThreadMapItemTypeA>();
		List<String> filePathList = data.get(0);
		for (int i=0; i<filePathList.size();i++){
			String path = filePathList.get(i);
			ThreadMapItemTypeA item = SAXParser.getThreadMapItemTypeA(path);
			itemList.add(item);
		}
		tableViewer.setInput(itemList);
		packColumns();
	}



	public List<String> getColumnNames() {
		return Arrays.asList(COLUMN_NAMES);
	}

	/**
	 * type A content provider.
	 * @author tu-ipride
	 * @version 3.0
	 * @date 2009/10/6
	 */
	class TableContentProviderA implements IStructuredContentProvider{
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
	 * type A
	 * @author tu-ipride
	 * @version 3.0
	 * @since 2009/10/6
	 */
	class TableLabelProviderA implements ITableLabelProvider{

		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		public String getColumnText(Object element, int columnIndex) {
			ThreadMapItemTypeA item = (ThreadMapItemTypeA)element;
			switch(columnIndex){
			case 0:
				return item.getThreadName();
			case 1:
				return item.getStatus();
			case 2:
				return item.getPriority();
			case 3:
				return item.getCPUTime();
			case 4:
				return item.getWaitReason();
			case 5:
				return item.getMonitorObject();
			case 6:
				return item.getWaitThread();
			}
			return null;
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
        	ThreadMapItemTypeA p1 = (ThreadMapItemTypeA) e1;
        	ThreadMapItemTypeA p2 = (ThreadMapItemTypeA) e2;
        	switch(column){
        	case 0:
        		return p1.getThreadName().compareTo(p2.getThreadName());
        	case 1:
        		return p1.getStatus().compareTo(p2.getStatus());
        	case 2:
        		return p1.getPriority().compareTo(p2.getPriority());
        	case 3:
        		return p1.getCPUTime().compareTo(p2.getCPUTime());
        	case 4:
        		return p1.getWaitReason().compareTo(p2.getWaitReason());
        	case 5:
        		return p1.getMonitorObject().compareTo(p2.getMonitorObject());
        	case 6:
        		return p1.getWaitThread().compareTo(p2.getWaitThread());
        	}
        	return 0;
        }

}

}