package jp.co.ipride.excat.analyzer.viewer.sourceviewer;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.IUndoManager;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.swt.SWT;


public class JavaSourceViewerConfiguration extends SourceViewerConfiguration {

	private CColorManager colorManager = null;
	private JavaSourceViewer mySourceViewer = null;
	
	public JavaSourceViewerConfiguration(CColorManager colorManager,
			JavaSourceViewer mySourceViewer){
		super();
		this.colorManager = colorManager;
		this.mySourceViewer = mySourceViewer;
	}
	
	public ITextHover getTextHover(ISourceViewer sourceViewer, String contentType) {
		//return new ExcatHovers();
		return new ExcatHovers(mySourceViewer);
	}
	
    public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
        return new String[] {
            IDocument.DEFAULT_CONTENT_TYPE,
            WordPartitionScanner.JAVA_SINGLE_LINE_COMMENT,
  		    WordPartitionScanner.JAVA_MULTI_LINE_COMMENT,
  		    WordPartitionScanner.JAVA_DOC,
  		    WordPartitionScanner.JAVA_STRING,
  		    WordPartitionScanner.JAVA_CHARACTER        
        };
    }	
    
	/**
	 * set color
	 */
	public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
	    PresentationReconciler reconciler = new PresentationReconciler();
	    
	    //generate scanner
	    JavaCodeScanner javaCodeScanner = new JavaCodeScanner();
	    
	    //set keyword token
	    javaCodeScanner.setTokenData(IStyleConstantsJSPJava.JAVA_KEYWORD,
	    		new TextAttribute(colorManager.getColor(IStyleConstantsJSPJava.JAVA_KEYWORD),
	                null, SWT.BOLD));
	    
	    //set comment token
	    javaCodeScanner.setTokenData(IStyleConstantsJSPJava.JAVA_SINGLE_LINE_COMMENT,
	    		new TextAttribute(colorManager.getColor(IStyleConstantsJSPJava.JAVA_SINGLE_LINE_COMMENT)));
	   
	    //set java doc token
	    javaCodeScanner.setTokenData(IStyleConstantsJSPJava.JAVA_DOC,
	    		new TextAttribute(colorManager.getColor(IStyleConstantsJSPJava.JAVA_DOC)));
	    
	    //set String token
	    javaCodeScanner.setTokenData(IStyleConstantsJSPJava.JAVA_STRING,
	    		new TextAttribute(colorManager.getColor(IStyleConstantsJSPJava.JAVA_STRING)));
  
	    //set default token
	    javaCodeScanner.setTokenData(IStyleConstantsJSPJava.JAVA_DEFAULT,
	    		new TextAttribute(colorManager.getColor(IStyleConstantsJSPJava.JAVA_DEFAULT)));
          
	    
	    javaCodeScanner.initializeRules();
	    DefaultDamagerRepairer dr = new DefaultDamagerRepairer(javaCodeScanner);
	    reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
	    reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);
	    	    
	    dr = new DefaultDamagerRepairer(javaCodeScanner);
	    reconciler.setDamager(dr, WordPartitionScanner.JAVA_SINGLE_LINE_COMMENT);
	    reconciler.setRepairer(dr, WordPartitionScanner.JAVA_SINGLE_LINE_COMMENT);	    
	  
	    dr = new DefaultDamagerRepairer(javaCodeScanner);
	    reconciler.setDamager(dr, WordPartitionScanner.JAVA_DOC);
	    reconciler.setRepairer(dr, WordPartitionScanner.JAVA_DOC);   
	    
	    dr = new DefaultDamagerRepairer(javaCodeScanner);
	    reconciler.setDamager(dr, WordPartitionScanner.JAVA_MULTI_LINE_COMMENT);
	    reconciler.setRepairer(dr, WordPartitionScanner.JAVA_MULTI_LINE_COMMENT);
	    
	    dr = new DefaultDamagerRepairer(javaCodeScanner);
	    reconciler.setDamager(dr, WordPartitionScanner.JAVA_STRING);
	    reconciler.setRepairer(dr, WordPartitionScanner.JAVA_STRING);   
	    
	    dr = new DefaultDamagerRepairer(javaCodeScanner);
	    reconciler.setDamager(dr, WordPartitionScanner.JAVA_CHARACTER);
	    reconciler.setRepairer(dr, WordPartitionScanner.JAVA_CHARACTER);
	    
	    return reconciler;
	}
	
   public IUndoManager getUndoManager(ISourceViewer arg0) {
	return null;
   }
}
