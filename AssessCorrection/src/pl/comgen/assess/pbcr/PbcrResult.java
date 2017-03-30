/**
 * 
 */
package pl.comgen.assess.pbcr;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author medhat
 *
 */
public final class PbcrResult {
	private long missedData;
	private Map<String, List<PbcrData>> readData = new HashMap<String, List<PbcrData>>();
	/**
	 * @return the missedData
	 */
	public long getMissedData() {
		return missedData;
	}
	/**
	 * @param missedData the missedData to set
	 */
	public void setMissedData(long missedData) {
		this.missedData = missedData;
	}
	/**
	 * @return the readData
	 */
	public Map<String, List<PbcrData>> getReadData() {
		return readData;
	}
	/**
	 * @param readData the readData to set
	 */
	public void setReadData(Map<String, List<PbcrData>> readData) {
		this.readData = readData;
	}


}
