package jp.co.ipride.excat.configeditor.viewer.contentassist;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;

/**
 * “ü—Í•â•‚ÌŠî‘bƒNƒ‰ƒX
 * @author tu-ipride
 * @version 3.0
 * @since 2009/9/22
 */
public abstract class AbstractContentAssistProcessor implements IContentAssistProcessor{

	protected Exception error;

	public ICompletionProposal[] computeCompletionProposals(
			ITextViewer viewer, int documentOffset) {
		return null;
	}

	public IContextInformation[] computeContextInformation(
			ITextViewer viewer, int documentOffset) {
		return null;
	}

	public char[] getCompletionProposalAutoActivationCharacters() {
		return null;
	}

	public char[] getContextInformationAutoActivationCharacters() {
		return null;
	}

	public IContextInformationValidator getContextInformationValidator() {
		return null;
	}

	public String getErrorMessage() {
		return (error == null) ? null : error.getMessage();
	}

	protected String getCurrentWord(IDocument document, int documentOffset) {
		StringBuffer currentWord = new StringBuffer();
		char ch;

		try {
			for (int offset = documentOffset - 1 ;
				offset >=0 && !Character.isWhitespace(ch = document.getChar(offset)) ;
				offset--) {
				currentWord.insert(0, ch);
			}
			return currentWord.toString();
		} catch (BadLocationException e) {
			error = e;
			return null;
		}
	}
}
