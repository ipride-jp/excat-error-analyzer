package jp.co.ipride.excat.analyzer.viewer.sourceviewer.declare;

import org.eclipse.jdt.core.compiler.InvalidInputException;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;
import org.eclipse.jdt.internal.compiler.parser.ScannerHelper;

/**
 * •¶Žš—ñ‚©‚ç”Žš‚Ìƒ^ƒCƒv‚ð•ªÍ‚·‚é
 * @author iPride_Demo
 *
 */
public class NumberScanner {
	public static int TokenNameNotNumber = 0;
	public static int TokenNameCharLiteral = 46;
	public static int TokenNameIntegerLiteral = 47;
	public static int TokenNameLongLiteral = 48;
	public static int TokenNameFloatingPointLiteral = 49;
	public static int TokenNameDoubleLiteral = 50;

	public static final String INVALID_HEXA = "Invalid_Hexa_Literal"; //$NON-NLS-1$
	public static final String INVALID_OCTAL = "Invalid_Octal_Literal"; //$NON-NLS-1$
	public static final String INVALID_CHARACTER_CONSTANT = "Invalid_Character_Constant";  //$NON-NLS-1$
	public static final String INVALID_ESCAPE = "Invalid_Escape"; //$NON-NLS-1$
	public static final String INVALID_INPUT = "Invalid_Input"; //$NON-NLS-1$
	public static final String INVALID_UNICODE_ESCAPE = "Invalid_Unicode_Escape"; //$NON-NLS-1$
	public static final String INVALID_FLOAT = "Invalid_Float_Literal"; //$NON-NLS-1$
	
	private char currentCharacter;
	private int startPosition = 0;
	private int currentPosition = 0;
	private long sourceLevel = 0L;
	
	//unicode support
	private char[] withoutUnicodeBuffer = null;	
	private boolean unicodeAsBackSlash = false;	
	private int withoutUnicodePtr = 0; //when == 0 ==> no unicode in the current token
	
	//source should be viewed as a window (aka a part)
	//of a entire very large stream
	private char source[] = null;
	
	public NumberScanner(long sourceLevel,String str){
		this.sourceLevel = sourceLevel;
		source = str.toCharArray();
	}
	
	private final int getNextChar() {
		try {
			if (((this.currentCharacter = this.source[this.currentPosition++]) == '\\')
				&& (this.source[this.currentPosition] == 'u')) {
					getNextUnicodeChar();
			} else {
				this.unicodeAsBackSlash = false;
				if (this.withoutUnicodePtr != 0) {
				    unicodeStore();
				}
			}
			return this.currentCharacter;
		} catch (IndexOutOfBoundsException e) {
			return -1;
		} catch(InvalidInputException e) {
			return -1;
		}
	}
	
	private final int getNextChar(char testedChar1, char testedChar2) {
		//INT 0 : testChar1 \\\\///\\\\ 1 : testedChar2 \\\\///\\\\ -1 : others
		//test can be done with (x==0) for the first and (x>0) for the second
		//handle the case of unicode.
		//when a unicode appears then we must use a buffer that holds char internal values
		//At the end of this method currentCharacter holds the new visited char
		//and currentPosition points right next after it
		//Both previous lines are true if the currentCharacter is == to the testedChar1/2
		//On false, no side effect has occured.

		//ALL getNextChar.... ARE OPTIMIZED COPIES 
		if (this.currentPosition >= this.source.length) // handle the obvious case upfront
			return -1;

		int temp = this.currentPosition;
		try {
			int result;
			if (((this.currentCharacter = this.source[this.currentPosition++]) == '\\')
				&& (this.source[this.currentPosition] == 'u')) {
				getNextUnicodeChar();
				if (this.currentCharacter == testedChar1) {
					result = 0;
				} else if (this.currentCharacter == testedChar2) {
					result = 1;
				} else {
					this.currentPosition = temp;
					this.withoutUnicodePtr--;
					result = -1;
				}
				return result;
			} else {
				if (this.currentCharacter == testedChar1) {
					result = 0;
				} else if (this.currentCharacter == testedChar2) {
					result = 1;
				} else {
					this.currentPosition = temp;
					return -1;
				}

				if (this.withoutUnicodePtr != 0)
					unicodeStore();
				return result;
			}
		} catch (IndexOutOfBoundsException e) {
			this.currentPosition = temp;
			return -1;
		} catch(InvalidInputException e) {
			this.currentPosition = temp;
			return -1;
		}
	}
	
	private void unicodeStore() {
		int pos = ++this.withoutUnicodePtr;
	    if (this.withoutUnicodeBuffer == null) 
	    	this.withoutUnicodeBuffer = new char[10];
	    int length = this.withoutUnicodeBuffer.length;
	    if (pos == length) {
	        System.arraycopy(this.withoutUnicodeBuffer, 0, this.withoutUnicodeBuffer = new char[length * 2], 0, length);
	    }
		this.withoutUnicodeBuffer[pos] = this.currentCharacter;
	}
	
	private void getNextUnicodeChar()
	    throws InvalidInputException {
		//VOID
		//handle the case of unicode.
		//when a unicode appears then we must use a buffer that holds char internal values
		//At the end of this method currentCharacter holds the new visited char
		//and currentPosition points right next after it
	
		//ALL getNextChar.... ARE OPTIMIZED COPIES 
	
		try {
			int c1 = 0, c2 = 0, c3 = 0, c4 = 0, unicodeSize = 6;
			this.currentPosition++;
			while (this.source[this.currentPosition] == 'u') {
				this.currentPosition++;
				unicodeSize++;
			}
	
			if ((c1 = ScannerHelper.getNumericValue(this.source[this.currentPosition++])) > 15
				|| c1 < 0
				|| (c2 = ScannerHelper.getNumericValue(this.source[this.currentPosition++])) > 15
				|| c2 < 0
				|| (c3 = ScannerHelper.getNumericValue(this.source[this.currentPosition++])) > 15
				|| c3 < 0
				|| (c4 = ScannerHelper.getNumericValue(this.source[this.currentPosition++])) > 15
				|| c4 < 0){
				throw new InvalidInputException(INVALID_UNICODE_ESCAPE);
			}
			this.currentCharacter = (char) (((c1 * 16 + c2) * 16 + c3) * 16 + c4);
			//need the unicode buffer
			if (this.withoutUnicodePtr == 0) {
				//buffer all the entries that have been left aside....
				unicodeInitializeBuffer(this.currentPosition - unicodeSize - this.startPosition);
			}
			//fill the buffer with the char
			unicodeStore();
			this.unicodeAsBackSlash = this.currentCharacter == '\\';
		} catch (ArrayIndexOutOfBoundsException e) {
			this.currentPosition--;
			throw new InvalidInputException(INVALID_UNICODE_ESCAPE);
		}
    }
	
	private void unicodeInitializeBuffer(int length) {
		this.withoutUnicodePtr = length;	
	    if (this.withoutUnicodeBuffer == null)
	    	this.withoutUnicodeBuffer = new char[length+(1+10)];
	    int bLength = this.withoutUnicodeBuffer.length;
	    if (1+length >= bLength) {
	        System.arraycopy(this.withoutUnicodeBuffer, 0, this.withoutUnicodeBuffer = new char[length + (1+10)], 0, bLength);
	    }
		System.arraycopy(this.source, this.startPosition, this.withoutUnicodeBuffer, 1, length);    
	}
	
	private final boolean getNextCharAsDigit() throws InvalidInputException {
		//BOOLEAN
		//handle the case of unicode.
		//when a unicode appears then we must use a buffer that holds char internal values
		//At the end of this method currentCharacter holds the new visited char
		//and currentPosition points right next after it
		//Both previous lines are true if the currentCharacter is a digit
		//On false, no side effect has occured.

		//ALL getNextChar.... ARE OPTIMIZED COPIES 
		if (this.currentPosition >= this.source.length) // handle the obvious case upfront
			return false;

		int temp = this.currentPosition;
		try {
			if (((this.currentCharacter = this.source[this.currentPosition++]) == '\\')
				&& (this.source[this.currentPosition] == 'u')) {
				getNextUnicodeChar();
				if (!ScannerHelper.isDigit(this.currentCharacter)) {
					this.currentPosition = temp;
					this.withoutUnicodePtr--;
					return false;
				}
				return true;
			} else {
				if (!ScannerHelper.isDigit(this.currentCharacter)) {
					this.currentPosition = temp;
					return false;
				}
				if (this.withoutUnicodePtr != 0)
					unicodeStore();
				return true;
			}
		} catch (IndexOutOfBoundsException e) {
			this.currentPosition = temp;
			return false;
		} catch(InvalidInputException e) {
			this.currentPosition = temp;
			return false;
		}
	}
	private final boolean getNextCharAsDigit(int radix) {
		//BOOLEAN
		//handle the case of unicode.
		//when a unicode appears then we must use a buffer that holds char internal values
		//At the end of this method currentCharacter holds the new visited char
		//and currentPosition points right next after it
		//Both previous lines are true if the currentCharacter is a digit base on radix
		//On false, no side effect has occured.

		//ALL getNextChar.... ARE OPTIMIZED COPIES 
		if (this.currentPosition >= this.source.length) // handle the obvious case upfront
			return false;

		int temp = this.currentPosition;
		try {
			if (((this.currentCharacter = this.source[this.currentPosition++]) == '\\')
				&& (this.source[this.currentPosition] == 'u')) {
				getNextUnicodeChar();
				if (ScannerHelper.digit(this.currentCharacter, radix) == -1) {
					this.currentPosition = temp;
					this.withoutUnicodePtr--;
					return false;
				}
				return true;
			} else {
				if (ScannerHelper.digit(this.currentCharacter, radix) == -1) {
					this.currentPosition = temp;
					return false;
				}
				if (this.withoutUnicodePtr != 0)
					unicodeStore();
				return true;
			}
		} catch (IndexOutOfBoundsException e) {
			this.currentPosition = temp;
			return false;
		} catch(InvalidInputException e) {
			this.currentPosition = temp;
			return false;
		}
	}
	
	private final boolean getNextChar(char testedChar) {
		//BOOLEAN
		//handle the case of unicode.
		//when a unicode appears then we must use a buffer that holds char internal values
		//At the end of this method currentCharacter holds the new visited char
		//and currentPosition points right next after it
		//Both previous lines are true if the currentCharacter is == to the testedChar
		//On false, no side effect has occured.

		//ALL getNextChar.... ARE OPTIMIZED COPIES 

		if (this.currentPosition >= this.source.length) { // handle the obvious case upfront
			this.unicodeAsBackSlash = false;
			return false;
		}

		int temp = this.currentPosition;
		try {
			if (((this.currentCharacter = this.source[this.currentPosition++]) == '\\')
				&& (this.source[this.currentPosition] == 'u')) {
				getNextUnicodeChar();
				if (this.currentCharacter != testedChar) {
					this.currentPosition = temp;
					this.withoutUnicodePtr--;
					return false;
				}
				return true;
			} //-------------end unicode traitement--------------
			else {
				if (this.currentCharacter != testedChar) {
					this.currentPosition = temp;
					return false;
				}
				this.unicodeAsBackSlash = false;
				if (this.withoutUnicodePtr != 0)
					unicodeStore();
				return true;
			}
		} catch (IndexOutOfBoundsException e) {
			this.unicodeAsBackSlash = false;
			this.currentPosition = temp;
			return false;
		} catch(InvalidInputException e) {
			this.unicodeAsBackSlash = false;
			this.currentPosition = temp;
			return false;
		}
	}
	
	private int scanNumber(boolean dotPrefix) throws InvalidInputException {

		//when entering this method the currentCharacter is the first
		//digit of the number. It may be preceeded by a '.' when
		//dotPrefix is true

		boolean floating = dotPrefix;
		if ((!dotPrefix) && (this.currentCharacter == '0')) {
			if (getNextChar('x', 'X') >= 0) { //----------hexa-----------------
				int start = this.currentPosition;
				while (getNextCharAsDigit(16)){/*empty*/}
				int end = this.currentPosition;
				if (getNextChar('l', 'L') >= 0) {
					if (end == start) {
						throw new InvalidInputException(INVALID_HEXA);
					}
					return TokenNameLongLiteral;
				} else if (getNextChar('.')) {
					if (this.sourceLevel < ClassFileConstants.JDK1_5) {
						if (end == start) {
							throw new InvalidInputException(INVALID_HEXA);
						}
						this.currentPosition = end;
						return TokenNameIntegerLiteral;
					}
					// hexadecimal floating point literal
					// read decimal part
					boolean hasNoDigitsBeforeDot = end == start;
					start = this.currentPosition;
					while (getNextCharAsDigit(16)){/*empty*/}
					end = this.currentPosition;
					if (hasNoDigitsBeforeDot && end == start) {
						throw new InvalidInputException(INVALID_HEXA);
					}
					
					if (getNextChar('p', 'P') >= 0) { // consume next character
						this.unicodeAsBackSlash = false;
						if (((this.currentCharacter = this.source[this.currentPosition++]) == '\\')
							&& (this.source[this.currentPosition] == 'u')) {
							getNextUnicodeChar();
						} else {
							if (this.withoutUnicodePtr != 0) {
								unicodeStore();
							}
						}

						if ((this.currentCharacter == '-')
							|| (this.currentCharacter == '+')) { // consume next character
							this.unicodeAsBackSlash = false;
							if (((this.currentCharacter = this.source[this.currentPosition++]) == '\\')
								&& (this.source[this.currentPosition] == 'u')) {
								getNextUnicodeChar();
							} else {
								if (this.withoutUnicodePtr != 0) {
									unicodeStore();
								}
							}
						}
						if (!ScannerHelper.isDigit(this.currentCharacter)) {
							throw new InvalidInputException(INVALID_HEXA);
						}
						while (getNextCharAsDigit()){/*empty*/}
						if (getNextChar('f', 'F') >= 0) {
							return TokenNameFloatingPointLiteral;
						}
						if (getNextChar('d', 'D') >= 0) {
							return TokenNameDoubleLiteral;
						}
						if (getNextChar('l', 'L') >= 0) {
							throw new InvalidInputException(INVALID_HEXA);
						}					
						return TokenNameDoubleLiteral;
					} else {
						throw new InvalidInputException(INVALID_HEXA);
					}
				} else if (getNextChar('p', 'P') >= 0) { // consume next character
					if (this.sourceLevel < ClassFileConstants.JDK1_5) {
						// if we are in source level < 1.5 we report an integer literal
						this.currentPosition = end;
						return TokenNameIntegerLiteral;
					}
					this.unicodeAsBackSlash = false;
					if (((this.currentCharacter = this.source[this.currentPosition++]) == '\\')
						&& (this.source[this.currentPosition] == 'u')) {
						getNextUnicodeChar();
					} else {
						if (this.withoutUnicodePtr != 0) {
							unicodeStore();
						}
					}

					if ((this.currentCharacter == '-')
						|| (this.currentCharacter == '+')) { // consume next character
						this.unicodeAsBackSlash = false;
						if (((this.currentCharacter = this.source[this.currentPosition++]) == '\\')
							&& (this.source[this.currentPosition] == 'u')) {
							getNextUnicodeChar();
						} else {
							if (this.withoutUnicodePtr != 0) {
								unicodeStore();
							}
						}
					}
					if (!ScannerHelper.isDigit(this.currentCharacter))
						throw new InvalidInputException(INVALID_FLOAT);
					while (getNextCharAsDigit()){/*empty*/}
					if (getNextChar('f', 'F') >= 0)
						return TokenNameFloatingPointLiteral;
					if (getNextChar('d', 'D') >= 0)
						return TokenNameDoubleLiteral;
					if (getNextChar('l', 'L') >= 0) {
						throw new InvalidInputException(INVALID_HEXA);
					}
					return TokenNameDoubleLiteral;
				} else {
					if (end == start)
						throw new InvalidInputException(INVALID_HEXA);
					return TokenNameIntegerLiteral;
				}
			}

			//there is x or X in the number
			//potential octal ! ... some one may write 000099.0 ! thus 00100 < 00078.0 is true !!!!! crazy language
			if (getNextCharAsDigit()) { //-------------potential octal-----------------
				while (getNextCharAsDigit()){/*empty*/}

				if (getNextChar('l', 'L') >= 0) {
					return TokenNameLongLiteral;
				}

				if (getNextChar('f', 'F') >= 0) {
					return TokenNameFloatingPointLiteral;
				}

				if (getNextChar('d', 'D') >= 0) {
					return TokenNameDoubleLiteral;
				} else { //make the distinction between octal and float ....
					boolean isInteger = true;
					if (getNextChar('.')) { 
						isInteger = false;
						while (getNextCharAsDigit()){/*empty*/}
					}
					if (getNextChar('e', 'E') >= 0) { // consume next character
						isInteger = false;
						this.unicodeAsBackSlash = false;
						if (((this.currentCharacter = this.source[this.currentPosition++]) == '\\')
							&& (this.source[this.currentPosition] == 'u')) {
							getNextUnicodeChar();
						} else {
							if (this.withoutUnicodePtr != 0) {
								unicodeStore();
							}
						}

						if ((this.currentCharacter == '-')
							|| (this.currentCharacter == '+')) { // consume next character
							this.unicodeAsBackSlash = false;
							if (((this.currentCharacter = this.source[this.currentPosition++]) == '\\')
								&& (this.source[this.currentPosition] == 'u')) {
								getNextUnicodeChar();
							} else {
								if (this.withoutUnicodePtr != 0) {
									unicodeStore();
								}
							}
						}
						if (!ScannerHelper.isDigit(this.currentCharacter))
							throw new InvalidInputException(INVALID_FLOAT);
						while (getNextCharAsDigit()){/*empty*/}
					}
					if (getNextChar('f', 'F') >= 0)
						return TokenNameFloatingPointLiteral;
					if (getNextChar('d', 'D') >= 0 || !isInteger)
						return TokenNameDoubleLiteral;
					return TokenNameIntegerLiteral;
				}
			} else {
				/* carry on */
			}
		}

		while (getNextCharAsDigit()){/*empty*/}

		if ((!dotPrefix) && (getNextChar('l', 'L') >= 0))
			return TokenNameLongLiteral;

		if ((!dotPrefix) && (getNextChar('.'))) { //decimal part that can be empty
			while (getNextCharAsDigit()){/*empty*/}
			floating = true;
		}

		//if floating is true both exponant and suffix may be optional

		if (getNextChar('e', 'E') >= 0) {
			floating = true;
			// consume next character
			this.unicodeAsBackSlash = false;
			if (((this.currentCharacter = this.source[this.currentPosition++]) == '\\')
				&& (this.source[this.currentPosition] == 'u')) {
				getNextUnicodeChar();
			} else {
				if (this.withoutUnicodePtr != 0) {
					unicodeStore();
				}
			}

			if ((this.currentCharacter == '-')
				|| (this.currentCharacter == '+')) { // consume next character
				this.unicodeAsBackSlash = false;
				if (((this.currentCharacter = this.source[this.currentPosition++]) == '\\')
					&& (this.source[this.currentPosition] == 'u')) {
					getNextUnicodeChar();
				} else {
					if (this.withoutUnicodePtr != 0) {
						unicodeStore();
					}
				}
			}
			if (!ScannerHelper.isDigit(this.currentCharacter))
				throw new InvalidInputException(INVALID_FLOAT);
			while (getNextCharAsDigit()){/*empty*/}
		}

		if (getNextChar('d', 'D') >= 0)
			return TokenNameDoubleLiteral;
		if (getNextChar('f', 'F') >= 0)
			return TokenNameFloatingPointLiteral;

		//the long flag has been tested before

		return floating ? TokenNameDoubleLiteral : TokenNameIntegerLiteral;
	}
	
	public int parseNumber() throws InvalidInputException {
		
	    //read next char
		getNextChar();
		switch(this.currentCharacter){
			case '.' :
				if (getNextCharAsDigit()) {
					return scanNumber(true);
				}
			default:
				return scanNumber(false);
		}
	}
}
