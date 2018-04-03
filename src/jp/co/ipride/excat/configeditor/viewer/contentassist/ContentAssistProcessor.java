package jp.co.ipride.excat.configeditor.viewer.contentassist;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;

/**
 * —áŠO“ü—ÍŽx‰‡
 * @author tu-ipride
 * @version 3.0
 * @since 2009/9/22
 */
public class ContentAssistProcessor extends AbstractContentAssistProcessor{

	private String[] candidates;

	/**
	 *
	 * @param candidates
	 */
	public ContentAssistProcessor(String[] candidates) {
		if (candidates == null){
			this.candidates = new String[]{};
		}else{
			this.candidates = candidates;
		}
	}

	public void setCandidates(String[] candidates){
		if (candidates == null){
			this.candidates = new String[]{};
		}else{
			this.candidates = candidates;
		}
	}

	/**
	 *
	 */
	public ICompletionProposal[] computeCompletionProposals(
			ITextViewer viewer, int documentOffset) {
		List<ICompletionProposal> results = new LinkedList<ICompletionProposal>();
		Iterator<String> itCandidates = Arrays.asList(candidates).iterator();
		String currentWord = getCurrentWord(viewer.getDocument(), documentOffset);
		int currentWordLen = currentWord.length();

		while (itCandidates.hasNext()) {
			String currentCandidate = itCandidates.next();
			if (currentCandidate == null){
				continue;
			}
			if (currentCandidate.startsWith(currentWord)) {
				results.add(
					new CompletionProposal(
						currentCandidate,
						documentOffset - currentWordLen,
						currentWordLen,
						currentCandidate.length()
					)
				);
			}
		}

		return (results.size() == 0) ? null :
			results.toArray(new ICompletionProposal[results.size()]);
	}

}
