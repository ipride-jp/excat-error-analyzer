package jp.co.ipride.excat.configeditor.viewer.contentassist;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author tu-ipride
 * @since 2009/9/22
 * @version 3.0
 */
public class WordTracker {

	  private int maxQueueSize;

	  private List<String> wordBuffer;

	  private Map<String,String> knownWords = new HashMap<String,String>();

	  public WordTracker(int queueSize) {
	    maxQueueSize = queueSize;
	    wordBuffer = new LinkedList<String>();
	  }

	  public int getWordCount() {
	    return wordBuffer.size();
	  }

	  public void add(String word) {
	    if (wordIsNotKnown(word)) {
	      flushOldestWord();
	      insertNewWord(word);
	    }
	  }

	  private void insertNewWord(String word) {
	    wordBuffer.add(0, word);
	    knownWords.put(word, word);
	  }

	  private void flushOldestWord() {
	    if (wordBuffer.size() == maxQueueSize) {
	      String removedWord = (String) wordBuffer.remove(maxQueueSize - 1);
	      knownWords.remove(removedWord);
	    }
	  }

	  private boolean wordIsNotKnown(String word) {
	    return knownWords.get(word) == null;
	  }

	  public List<String> suggest(String word) {
	    List<String> suggestions = new LinkedList<String>();
	    for (Iterator<String> i = wordBuffer.iterator(); i.hasNext();) {
	      String currWord = i.next();
	      if (currWord.startsWith(word)) {
	        suggestions.add(currWord);
	      }
	    }
	    return suggestions;
	  }

}
