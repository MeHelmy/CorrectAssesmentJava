/**
 * 
 */
package pl.comgen.assess.util;

/**
 * @author medhat
 *
 */
public class AlignmentCompareResult {
	
	private int start = 0;
	private int end = 0;
    
    private int  corIndelSimple = 0;
    private int  corMismatchSimple = 0;

    private int  uncorIndelSimple = 0;
    private int  uncorMismatchSimple = 0;

    private int  introducedIndelSimple = 0;
    private int  introducedMismatchSimple = 0;

    // efficiency in other repeats

    private int  corIndelOther = 0;
    private int  corMismatchOther = 0;

    private int  uncorIndelOther = 0;
    private int  uncorMismatchOther = 0;

    private int  introducedIndelOther = 0;
    private int  introducedMismatchOther = 0;

    // efficiency in non repeats

    private int  corIndelNoRepeat = 0;
    private int  corMismatchNoRepeat = 0;

    private int  uncorIndelNoRepeat = 0;
    private int  uncorMismatchNoRepeat = 0;

    private int  introducedIndelNoRepeat = 0;
    private int  introducedMismatchNoRepeat = 0;

    // efficiency overall

    private int  corIndel = 0;  // ref = - and cor = - OR art = - and cor == ref
    private int  corMismatch = 0;  // ref ==  cor != art and ref == [ACGT]

    private int  uncorIndel = 0;  // ref = - and  cor = [ACGT] OR art = - and ref = [ACGT]
    private int  uncorMismatch = 0;  // ref != art ==  cor

    private int  introducedIndel = 0;  //  cor = [ACGT] and ref = - OR  cor = - and ref = [ACGT]
    private int  introducedMismatch = 0;  // ref !=  cor != art
	/**
	 * @return the start
	 */
	public int getStart() {
		return start;
	}
	/**
	 * @param start the start to set
	 */
	public void setStart(int start) {
		this.start = start;
	}
	/**
	 * @return the end
	 */
	public int getEnd() {
		return end;
	}
	/**
	 * @param end the end to set
	 */
	public void setEnd(int end) {
		this.end = end;
	}
	/**
	 * @return the corIndelSimple
	 */
	public int getCorIndelSimple() {
		return corIndelSimple;
	}
	/**
	 * @param corIndelSimple the corIndelSimple to set
	 */
	public void setCorIndelSimple(int corIndelSimple) {
		this.corIndelSimple = corIndelSimple;
	}
	/**
	 * @return the corMismatchSimple
	 */
	public int getCorMismatchSimple() {
		return corMismatchSimple;
	}
	/**
	 * @param corMismatchSimple the corMismatchSimple to set
	 */
	public void setCorMismatchSimple(int corMismatchSimple) {
		this.corMismatchSimple = corMismatchSimple;
	}
	/**
	 * @return the uncorIndelSimple
	 */
	public int getUncorIndelSimple() {
		return uncorIndelSimple;
	}
	/**
	 * @param uncorIndelSimple the uncorIndelSimple to set
	 */
	public void setUncorIndelSimple(int uncorIndelSimple) {
		this.uncorIndelSimple = uncorIndelSimple;
	}
	/**
	 * @return the uncorMismatchSimple
	 */
	public int getUncorMismatchSimple() {
		return uncorMismatchSimple;
	}
	/**
	 * @param uncorMismatchSimple the uncorMismatchSimple to set
	 */
	public void setUncorMismatchSimple(int uncorMismatchSimple) {
		this.uncorMismatchSimple = uncorMismatchSimple;
	}
	/**
	 * @return the introducedIndelSimple
	 */
	public int getIntroducedIndelSimple() {
		return introducedIndelSimple;
	}
	/**
	 * @param introducedIndelSimple the introducedIndelSimple to set
	 */
	public void setIntroducedIndelSimple(int introducedIndelSimple) {
		this.introducedIndelSimple = introducedIndelSimple;
	}
	/**
	 * @return the introducedMismatchSimple
	 */
	public int getIntroducedMismatchSimple() {
		return introducedMismatchSimple;
	}
	/**
	 * @param introducedMismatchSimple the introducedMismatchSimple to set
	 */
	public void setIntroducedMismatchSimple(int introducedMismatchSimple) {
		this.introducedMismatchSimple = introducedMismatchSimple;
	}
	/**
	 * @return the corIndelOther
	 */
	public int getCorIndelOther() {
		return corIndelOther;
	}
	/**
	 * @param corIndelOther the corIndelOther to set
	 */
	public void setCorIndelOther(int corIndelOther) {
		this.corIndelOther = corIndelOther;
	}
	/**
	 * @return the corMismatchOther
	 */
	public int getCorMismatchOther() {
		return corMismatchOther;
	}
	/**
	 * @param corMismatchOther the corMismatchOther to set
	 */
	public void setCorMismatchOther(int corMismatchOther) {
		this.corMismatchOther = corMismatchOther;
	}
	/**
	 * @return the uncorIndelOther
	 */
	public int getUncorIndelOther() {
		return uncorIndelOther;
	}
	/**
	 * @param uncorIndelOther the uncorIndelOther to set
	 */
	public void setUncorIndelOther(int uncorIndelOther) {
		this.uncorIndelOther = uncorIndelOther;
	}
	/**
	 * @return the uncorMismatchOther
	 */
	public int getUncorMismatchOther() {
		return uncorMismatchOther;
	}
	/**
	 * @param uncorMismatchOther the uncorMismatchOther to set
	 */
	public void setUncorMismatchOther(int uncorMismatchOther) {
		this.uncorMismatchOther = uncorMismatchOther;
	}
	/**
	 * @return the introducedIndelOther
	 */
	public int getIntroducedIndelOther() {
		return introducedIndelOther;
	}
	/**
	 * @param introducedIndelOther the introducedIndelOther to set
	 */
	public void setIntroducedIndelOther(int introducedIndelOther) {
		this.introducedIndelOther = introducedIndelOther;
	}
	/**
	 * @return the introducedMismatchOther
	 */
	public int getIntroducedMismatchOther() {
		return introducedMismatchOther;
	}
	/**
	 * @param introducedMismatchOther the introducedMismatchOther to set
	 */
	public void setIntroducedMismatchOther(int introducedMismatchOther) {
		this.introducedMismatchOther = introducedMismatchOther;
	}
	/**
	 * @return the corIndelNoRepeat
	 */
	public int getCorIndelNoRepeat() {
		return corIndelNoRepeat;
	}
	/**
	 * @param corIndelNoRepeat the corIndelNoRepeat to set
	 */
	public void setCorIndelNoRepeat(int corIndelNoRepeat) {
		this.corIndelNoRepeat = corIndelNoRepeat;
	}
	/**
	 * @return the corMismatchNoRepeat
	 */
	public int getCorMismatchNoRepeat() {
		return corMismatchNoRepeat;
	}
	/**
	 * @param corMismatchNoRepeat the corMismatchNoRepeat to set
	 */
	public void setCorMismatchNoRepeat(int corMismatchNoRepeat) {
		this.corMismatchNoRepeat = corMismatchNoRepeat;
	}
	/**
	 * @return the uncorIndelNoRepeat
	 */
	public int getUncorIndelNoRepeat() {
		return uncorIndelNoRepeat;
	}
	/**
	 * @param uncorIndelNoRepeat the uncorIndelNoRepeat to set
	 */
	public void setUncorIndelNoRepeat(int uncorIndelNoRepeat) {
		this.uncorIndelNoRepeat = uncorIndelNoRepeat;
	}
	/**
	 * @return the uncorMismatchNoRepeat
	 */
	public int getUncorMismatchNoRepeat() {
		return uncorMismatchNoRepeat;
	}
	/**
	 * @param uncorMismatchNoRepeat the uncorMismatchNoRepeat to set
	 */
	public void setUncorMismatchNoRepeat(int uncorMismatchNoRepeat) {
		this.uncorMismatchNoRepeat = uncorMismatchNoRepeat;
	}
	/**
	 * @return the introducedIndelNoRepeat
	 */
	public int getIntroducedIndelNoRepeat() {
		return introducedIndelNoRepeat;
	}
	/**
	 * @param introducedIndelNoRepeat the introducedIndelNoRepeat to set
	 */
	public void setIntroducedIndelNoRepeat(int introducedIndelNoRepeat) {
		this.introducedIndelNoRepeat = introducedIndelNoRepeat;
	}
	/**
	 * @return the introducedMismatchNoRepeat
	 */
	public int getIntroducedMismatchNoRepeat() {
		return introducedMismatchNoRepeat;
	}
	/**
	 * @param introducedMismatchNoRepeat the introducedMismatchNoRepeat to set
	 */
	public void setIntroducedMismatchNoRepeat(int introducedMismatchNoRepeat) {
		this.introducedMismatchNoRepeat = introducedMismatchNoRepeat;
	}
	/**
	 * @return the corIndel
	 */
	public int getCorIndel() {
		return corIndel;
	}
	/**
	 * @param corIndel the corIndel to set
	 */
	public void setCorIndel(int corIndel) {
		this.corIndel = corIndel;
	}
	/**
	 * @return the corMismatch
	 */
	public int getCorMismatch() {
		return corMismatch;
	}
	/**
	 * @param corMismatch the corMismatch to set
	 */
	public void setCorMismatch(int corMismatch) {
		this.corMismatch = corMismatch;
	}
	/**
	 * @return the uncorIndel
	 */
	public int getUncorIndel() {
		return uncorIndel;
	}
	/**
	 * @param uncorIndel the uncorIndel to set
	 */
	public void setUncorIndel(int uncorIndel) {
		this.uncorIndel = uncorIndel;
	}
	/**
	 * @return the uncorMismatch
	 */
	public int getUncorMismatch() {
		return uncorMismatch;
	}
	/**
	 * @param uncorMismatch the uncorMismatch to set
	 */
	public void setUncorMismatch(int uncorMismatch) {
		this.uncorMismatch = uncorMismatch;
	}
	/**
	 * @return the introducedIndel
	 */
	public int getIntroducedIndel() {
		return introducedIndel;
	}
	/**
	 * @param introducedIndel the introducedIndel to set
	 */
	public void setIntroducedIndel(int introducedIndel) {
		this.introducedIndel = introducedIndel;
	}
	/**
	 * @return the introducedMismatch
	 */
	public int getIntroducedMismatch() {
		return introducedMismatch;
	}
	/**
	 * @param introducedMismatch the introducedMismatch to set
	 */
	public void setIntroducedMismatch(int introducedMismatch) {
		this.introducedMismatch = introducedMismatch;
	}
}
