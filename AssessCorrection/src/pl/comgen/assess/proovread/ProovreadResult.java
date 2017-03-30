/**
 * 
 */
package pl.comgen.assess.proovread;

import java.util.List;
import java.util.Map;

import pl.comgen.assess.util.Read;

/**
 * @author medhat
 *
 */
public class ProovreadResult {
	private Map<String, List<Read>> proovreadReads; 
	private Map<String, String> totalReadLength;
	/**
	 * @return the proovreadReads
	 */
	public Map<String, List<Read>> getProovreadReads() {
		return proovreadReads;
	}
	/**
	 * @param proovreadReads the proovreadReads to set
	 */
	public void setProovreadReads(Map<String, List<Read>> proovreadReads) {
		this.proovreadReads = proovreadReads;
	}
	/**
	 * @return the totalReadLength
	 */
	public Map<String, String> getTotalReadLength() {
		return totalReadLength;
	}
	/**
	 * @param totalReadLength the totalReadLength to set
	 */
	public void setTotalReadLength(Map<String, String> totalReadLength) {
		this.totalReadLength = totalReadLength;
	}
	

}
