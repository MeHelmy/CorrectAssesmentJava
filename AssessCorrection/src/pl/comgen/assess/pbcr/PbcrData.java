/**
 * 
 */
package pl.comgen.assess.pbcr;

/**
 * @author medhat
 *
 */
public class PbcrData {
	private String originalName, newName, baseName, seqValue;
	private long start, end, seqLength;
	/**
	 * @return the seqLength
	 */
	public long getSeqLength() {
		return seqLength;
	}
	/**
	 * @param seqLength the seqLength to set
	 */
	public void setSeqLength(long seqLength) {
		this.seqLength = seqLength;
	}
	/**
	 * @return the seqValue
	 */
	public String getSeqValue() {
		return seqValue;
	}
	/**
	 * @param seqValue the seqValue to set
	 */
	public void setSeqValue(String seqValue) {
		this.seqValue = seqValue;
	}
	
	/**
	 * @return the originalName
	 */
	public String getOriginalName() {
		return originalName;
	}
	/**
	 * @param originalName the originalName to set
	 */
	public void setOriginalName(String originalName) {
		this.originalName = originalName;
	}
	/**
	 * @return the newName
	 */
	public String getNewName() {
		return newName;
	}
	/**
	 * @param newName the newName to set
	 */
	public void setNewName(String newName) {
		this.newName = newName;
	}
	/**
	 * @return the baseName
	 */
	public String getBaseName() {
		return baseName;
	}
	/**
	 * @param baseName the baseName to set
	 */
	public void setBaseName(String baseName) {
		this.baseName = baseName;
	}
	/**
	 * @return the start
	 */
	public long getStart() {
		return start;
	}
	/**
	 * @param start the start to set
	 */
	public void setStart(long start) {
		this.start = start;
	}
	/**
	 * @return the end
	 */
	public long getEnd() {
		return end;
	}
	/**
	 * @param end the end to set
	 */
	public void setEnd(long end) {
		this.end = end;
	}
}
