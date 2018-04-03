package jp.co.ipride.excat.analyzer.viewer.sourceviewer.move;

import java.util.ArrayList;
import java.util.List;

import jp.co.ipride.excat.analyzer.action.ForwordAction;
import jp.co.ipride.excat.analyzer.action.PreviousAction;

/**
 * ÉÜÅ[ÉUÅ[ÇÃëJà⁄Çä«óùÇ∑ÇÈ
 * @author tu-ipride
 * @version 3.0
 * @date 2009/10/26
 */
public class MoveMgr {

	private static int MAX_RECORD = 1000;

	private static MoveRecord currentRecord=null;

	private static List<MoveRecord> recordList = new ArrayList<MoveRecord>();

	private static PreviousAction previousAction = null;

	private static ForwordAction forwordAction = null;

	public static void register(MoveRecord record){
		if (record != null){
			currentRecord = record;
			recordList.add(currentRecord);
			checkLenght();
			setActionStatus();
		}
	}


	public static MoveRecord forword(){
		if (currentRecord != null){
			int index = recordList.indexOf(currentRecord);
			if (index <recordList.size()-1){
				currentRecord = recordList.get(index+1);
				setActionStatus();
				return currentRecord;
			}else{
				setActionStatus();
				return null;
			}
		}else{
			return null;
		}
	}

	public static MoveRecord back(){
		if (currentRecord != null){
			int index = recordList.indexOf(currentRecord);
			if (index > 0){
				currentRecord = recordList.get(index-1);
				setActionStatus();
				return currentRecord;
			}else{
				setActionStatus();
				return null;
			}
		}else{
			return null;
		}
	}

	private static void checkLenght(){
		if (recordList.size()>MAX_RECORD){
			recordList.remove(0);
		}
	}

	private static void setActionStatus(){
		int index = recordList.indexOf(currentRecord);
		int size = recordList.size();
		if (size <= 1){
			previousAction.setEnabled(false);
			forwordAction.setEnabled(false);
		}else if (size > 1 && index==0){
			previousAction.setEnabled(false);
			forwordAction.setEnabled(true);
		}else if (size > 1 && index == size-1){
			previousAction.setEnabled(true);
			forwordAction.setEnabled(false);
		}else{
			previousAction.setEnabled(true);
			forwordAction.setEnabled(true);
		}
	}

	public static void setPreviousAction(PreviousAction p){
		previousAction = p;
	}

	public static void setForwordAction(ForwordAction f){
		forwordAction = f;
	}

}
