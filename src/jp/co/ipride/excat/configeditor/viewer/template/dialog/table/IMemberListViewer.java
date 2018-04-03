package jp.co.ipride.excat.configeditor.viewer.template.dialog.table;

import jp.co.ipride.excat.configeditor.model.template.Member;

public interface IMemberListViewer {

	public void addMember(Member member);

	public void removeMember(Member member);
	
	public void updateMember(Member member);
}
