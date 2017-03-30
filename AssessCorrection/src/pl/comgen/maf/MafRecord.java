/**
 * 
 */
package pl.comgen.maf;
/**
 * @author medhat
 *
 */

import java.util.ArrayList;
import java.util.List;

public class MafRecord {
	private List<MafRecordData> mafRecordDatas = new ArrayList<MafRecordData>();
	public void addRecord(MafRecordData mafRecordData) {
		this.mafRecordDatas.add(mafRecordData);
		
	}
	
	public List<MafRecordData> getMafRecord() {
		return mafRecordDatas;
	}
	}
