package jp.co.ipride.excat.configeditor.util;

import java.util.StringTokenizer;

import jp.co.ipride.excat.configeditor.ExcatTextViewer;

import org.apache.log4j.Logger;
import org.eclipse.jface.text.BadLocationException;

/**
 * “ü—Í•â•‹@”\‚ÉŠÖ˜A
 * @author tu-ipride
 * @since 2009/9/22
 * @version 3.0
 */
public class ProposalUtility {

	/**
	 * ’¼‹ß•¶Žš‚ðŽæ“¾
	 * @param textViewer
	 * @param startSearchOffset
	 * @return
	 */
	public static String findMostRecentWord(ExcatTextViewer textViewer, int startSearchOffset) {
	    int currOffset = startSearchOffset;
	    char currChar;
	    String word = "";
	    try {
	      while (currOffset > 0
	          && !Character.isWhitespace(
	        		  currChar = textViewer.getChar(currOffset))) {
	        word = currChar + word;
	        currOffset--;
	      }
	      return word;
	    } catch (BadLocationException e) {
			Logger.getLogger("viewerLogger").debug(e);
			return null;
	    }
	}

	/**
	 * call by findMostRecentWord
	 * call by textChanged event process
	 * @param string
	 * @return
	 */
	public static boolean isWhitespaceString(String string) {
		    StringTokenizer tokenizer = new StringTokenizer(string);
		    //if there is at least 1 token, this string is not whitespace
		    return !tokenizer.hasMoreTokens();
	}

}
