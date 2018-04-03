package jp.co.ipride.excat.analyzer.viewer.searchviewer;

/**
 * ƒŠƒ|ƒWƒgƒŠŒŸõŒ‹‰Ê
 * @author saisk
 *
 */
public class Match {

	private Object fElement;
	private int fOffset;
	private int fLength;

	public Match(Object element, int offset, int length)
	{
	    fElement = element;
	    fOffset = offset;
	    fLength = length;
	}

	public int getOffset()
	{
	    return fOffset;
	}

	public void setOffset(int offset)
	{
	    fOffset = offset;
	}

	public int getLength()
	{
	    return fLength;
	}

	public void setLength(int length)
	{
	    fLength = length;
	}

	public Object getElement()
	{
	    return fElement;
	}
}
