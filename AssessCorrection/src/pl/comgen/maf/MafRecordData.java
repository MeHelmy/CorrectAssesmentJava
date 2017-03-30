/**
 * 
 */
package pl.comgen.maf;

/**
 * @author medhat
 *
 */
public class MafRecordData {
	
	private String seqName, seqValue;
	private long start, end, length;
	private char sign;
	/**
	 * @return the seqName
	 */
	public String getSeqName() {
		return seqName;
	}
	/**
	 * @param seqName the seqName to set
	 */
	public void setSeqName(String seqName) {
		this.seqName = seqName;
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
	/**
	 * @return the length
	 */
	public long getLength() {
		return length;
	}
	/**
	 * @param length the length to set
	 */
	public void setLength(long length) {
		this.length = length;
	}
	/**
	 * @return the sign
	 */
	public char getSign() {
		return sign;
	}
	/**
	 * @param sign the sign to set
	 */
	public void setSign(char sign) {
		this.sign = sign;
	}

}
