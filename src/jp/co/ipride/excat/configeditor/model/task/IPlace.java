package jp.co.ipride.excat.configeditor.model.task;

public interface IPlace {

	public String getClassName();

	public void setClassName(String className);

	public String getMethodName();

	public void setMethodName(String methodName);

	public String getMethodSignature();

	public void setMethodSignature(String methodSignature);

	public boolean isUse();

	public void setUse(boolean use);
}
