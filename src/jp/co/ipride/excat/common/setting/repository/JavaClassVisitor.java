package jp.co.ipride.excat.common.setting.repository;

import java.util.ArrayList;
import java.util.List;

import jp.co.ipride.excat.analyzer.viewer.sourceviewer.ClassTypeInfo;
import jp.co.ipride.excat.analyzer.viewer.sourceviewer.MatchField;
import jp.co.ipride.excat.analyzer.viewer.sourceviewer.MatchMethodInfo;
import jp.co.ipride.excat.common.utility.HelperFunc;

import org.apache.bcel.classfile.Field;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.Type;

/**
 * analyze java class file to create a ClassTypeInfo
 * @author tu-ipride
 * @version 3.0
 * @date 2009/9/20
 */
public class JavaClassVisitor {

	public static ClassTypeInfo extraInfo(JavaClass clazz){
		String className = clazz.getClassName();
		ClassTypeInfo cu = new ClassTypeInfo();

		cu.setClassName(className);
		String superClassName = clazz.getSuperclassName();
		cu.setSuperClassName(superClassName);

		//when class is Object,bcel return Object for it's super class
		if("java.lang.Object".equals(className)){
			cu.setSuperClassName(null);
		}

		//register interfaces
		String[] interfaceNames = clazz.getInterfaceNames();
		List<String> interfaceList = new ArrayList<String>();
		for(int i = 0; i < interfaceNames.length;i++){
			interfaceList.add(interfaceNames[i]);
		}
		cu.setInterfaceTypeList(interfaceList);
		cu.setInterfaceType(clazz.isInterface());

		//register methods
		Method[] methods = clazz.getMethods();
		if(methods != null && methods.length > 0){
			for(int i= 0;i < methods.length;i++){
				MatchMethodInfo mu = new MatchMethodInfo();
				String methodName = methods[i].getName();
				String signature = methods[i].getSignature();
//				if("<init>".equals(methodName)){
//					methodName = HelperFunc.getPureClassName(
//							className);
//				}
				mu.setMethodName(methodName);
				String mConvertedSig = HelperFunc.convertMethodSig(signature);
				mu.setMethodSignature(mConvertedSig);
				//引数のタイプのリストを取得
				List<String> paramTypeList = HelperFunc.getParamsListFromSignature(signature);
				mu.setParamTypeList(paramTypeList);
				mu.setParamNumber(paramTypeList.size());
				Type returnType = methods[i].getReturnType();
				mu.setReturnType(HelperFunc.convertClassSig(
						returnType.getSignature()));
				cu.addMethod(mu);
				mu.setMyClassTypeInfo(cu);  //tu add for v3
			}
		}
		//register field
		Field[] fields = clazz.getFields();
		if(fields != null && fields.length > 0){
			for(int i = 0; i < fields.length; i++){
				MatchField fu = new MatchField();
				fu.setFieldName(fields[i].getName());
				fu.setFullFieldType(HelperFunc.convertClassSig(
						fields[i].getSignature()));
				cu.addField(fu);
			}
		}

		return cu;
	}
}
