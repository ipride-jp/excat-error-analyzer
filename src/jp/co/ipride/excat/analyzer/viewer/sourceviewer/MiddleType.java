package jp.co.ipride.excat.analyzer.viewer.sourceviewer;

import org.eclipse.jdt.core.dom.ArrayType;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.Type;

/**
 * ASTのタイプノートに対応するタイプ
 * @author iPride_Demo
 *
 */
public class MiddleType {

	/**
	 * タイプ文字列
	 */
	private String typeString = null;
	
	private boolean simpleType = false;
	
	private boolean qualifiedType = false;
	
	private boolean arrayType = false;
	
	private int dimensions = 0;
	private Type elementType = null;
	
	private boolean primitiveType = false;
	
	private int startPosition = 0;
	
	private int length = 0;
	

	/**
	 * コンストラクタ
	 * @param type
	 */
	public MiddleType(Type type){
		initialByType(type);
	}
	
	public void initialByType(Type type){
		typeString = type.toString();
		simpleType = type.isSimpleType();
		qualifiedType = type.isQualifiedType();
		arrayType = type.isArrayType();
		if(arrayType){
			dimensions =( (ArrayType)type).getDimensions();
			elementType = ((ArrayType)type).getElementType();
		}
		primitiveType = type.isPrimitiveType();
		startPosition = type.getStartPosition();
		length = type.getLength();
	}
	/**
	 * コンストラクタ
	 * @param type
	 */
	public MiddleType(Type type,int extraDimention){
		initialByType(type);
		if(extraDimention > 0){
			arrayType = true;
			for(int i = 0; i < extraDimention;i++){
				typeString += "[]";
			}
			simpleType = false;
			qualifiedType = false;	
			primitiveType = false;
			dimensions += extraDimention;
			if(elementType == null){
				elementType = type;
			}
		}
	}
	
	public MiddleType(SimpleName sn){
		
		typeString = sn.getFullyQualifiedName();
		simpleType = true;
		qualifiedType = false;
		arrayType = false;
		primitiveType = false;
		startPosition = sn.getStartPosition();
		length = sn.getLength();
	}
	
	public MiddleType(QualifiedName qname){
		
		typeString = qname.getFullyQualifiedName();
		simpleType = false;
		qualifiedType = true; //unknown
		arrayType = false;
		primitiveType = false;
		startPosition = qname.getStartPosition();
		length = qname.getLength();
	}
	
	public MiddleType(String str,int startPosition,int length){
		
		typeString = str;
		if(str.indexOf('.') > 0){
			simpleType = false;
			qualifiedType = true;
		}else{
			simpleType = true;
			qualifiedType = false;
		}

		arrayType = false;
		primitiveType = false;
		this.startPosition = startPosition;
		this.length = length;
	}
	
	public boolean isArrayType() {
		return arrayType;
	}

	public void setArrayType(boolean arrayType) {
		this.arrayType = arrayType;
	}

	public boolean isQualifiedType() {
		return qualifiedType;
	}

	public void setQualifiedType(boolean qualifiedType) {
		this.qualifiedType = qualifiedType;
	}

	public boolean isSimpleType() {
		return simpleType;
	}

	public void setSimpleType(boolean simpleType) {
		this.simpleType = simpleType;
	}

	public String getTypeString() {
		return typeString;
	}

	public void setTypeString(String typeString) {
		this.typeString = typeString;
	}

	public boolean isPrimitiveType() {
		return primitiveType;
	}

	public boolean isVoid(){
		return "void".equals(typeString);
	}
	
	public void setPrimitiveType(boolean primitiveType) {
		this.primitiveType = primitiveType;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public int getStartPosition() {
		return startPosition;
	}

	public void setStartPosition(int startPosition) {
		this.startPosition = startPosition;
	}

	public int getDimensions() {
		return dimensions;
	}

	public void setDimensions(int dimensions) {
		this.dimensions = dimensions;
	}

	public Type getElementType() {
		return elementType;
	}

	public void setElementType(Type elementType) {
		this.elementType = elementType;
	}
	
	
}
