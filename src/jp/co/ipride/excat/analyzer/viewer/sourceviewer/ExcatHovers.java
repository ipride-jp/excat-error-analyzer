package jp.co.ipride.excat.analyzer.viewer.sourceviewer;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.Region;

/**
 * Hovers to show info for variable and field when mouse is on it
 * @author jiang
 *
 */
public class ExcatHovers implements ITextHover {
	

	/**
	 * ソースビューア
	 */
	private JavaSourceViewer sourceViewer = null;
	
	/**
	 * constructor
	 * @param sourceViewer
	 */
	public ExcatHovers(JavaSourceViewer sourceViewer){
		this.sourceViewer = sourceViewer;
	}
	
	/**
	 * get region of the word under mouse
	 */
	public IRegion getHoverRegion(ITextViewer textViewer, int offset) {
	
		try {
			ITypedRegion partition = textViewer.getDocument().getPartition(offset);
			if(partition != null){
				//ignore string,char,javadoc and comment
				if(IDocument.DEFAULT_CONTENT_TYPE.equals(partition.getType())){
					IRegion region = JavaWordFinder.findWord(textViewer.getDocument(),offset);
					
					if (region != null){
						JavaSourceVisitor sourceVisitor = sourceViewer.getSourceVisitor();
						if(sourceVisitor != null){
							int reg_off = region.getOffset();
							int ret_len = region.getLength();
							String word = textViewer.getDocument().get(reg_off, ret_len);
							if(sourceViewer.isFieldOrVariable(reg_off,ret_len)){
								if(sourceVisitor.getObjectToShowInfo(reg_off,
										ret_len,word) != null){
									return region;
								}
								
							}
						}
						
					}else{
						return new Region(offset, 0);
					}
						
				}
			}
		} catch (BadLocationException e) {
		}
		
		return new Region(offset, 0);
	}

	/**
	 * get info of the word under mouse
	 */
	public String getHoverInfo(ITextViewer textViewer, IRegion hoverRegion) {
		if (hoverRegion != null) {
			try {
				if (hoverRegion.getLength() > -1){
					JavaSourceVisitor sourceVisitor = sourceViewer.getSourceVisitor();
					if(sourceVisitor != null){
						String word = textViewer.getDocument().get(
								hoverRegion.getOffset(), hoverRegion.getLength());
						String info = sourceVisitor.getObjectToShowInfo(hoverRegion.getOffset(),
								hoverRegion.getLength(),word);
						return info;
					}					
				}
				
			} catch (BadLocationException x) {
			}
		}
		return ""; 
	}

}
