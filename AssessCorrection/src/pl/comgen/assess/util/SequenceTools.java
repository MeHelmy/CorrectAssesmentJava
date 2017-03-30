/**
 * 
 */
package pl.comgen.assess.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import pl.comgen.assess.pbcr.PbcrData;
import pl.comgen.maf.MafRecord;

/**
 * @author medhat
 *
 */
public class SequenceTools {

	public String reverseComplement(String sequence) {

		String seq = new StringBuilder(sequence).reverse().toString();
		StringBuilder reverseseq = new StringBuilder();
		for (char s : seq.toLowerCase().toCharArray()) {
			switch (s) {
			case 'a':
				reverseseq.append("t");
				break;
			case 't':
				reverseseq.append("a");
				break;
			case 'c':
				reverseseq.append("g");
				break;
			case 'g':
				reverseseq.append("c");
				break;
			case '-':
				reverseseq.append("-");
			default:
				break;
			}

		}
		return reverseseq.toString();

	}

	public void wrtieSequenceFile(String locationDir, String fileName, Map<String, String> sequences) {
		try {
			File dir = new File(locationDir);
			File actualFile = null;
			if (dir.isDirectory()) {
				actualFile = new File(dir, fileName);
			} else {
				dir.mkdirs();
				actualFile = new File(dir, fileName);
			}

			FileWriter fw = new FileWriter(actualFile.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			Boolean first = true;
			for (Entry<String, String> sequence : sequences.entrySet()) {
				if (first) {
					bw.write(">" + sequence.getKey() + "\n" + sequence.getValue());
					first = false;
				} else {
					bw.write("\n>" + sequence.getKey() + "\n" + sequence.getValue());
				}
			}

			bw.close();
		} catch (IOException e) {

			e.printStackTrace();
		}

	}

	public Map<String, String> alignSeq(String subject, String query) {
		Map<String, String> alignmentResult = new LinkedHashMap<>();
		try {
			Process p;

			String[] cmd = { "/bin/sh", "-c", " mafft --clustalout --thread -1 --quiet --op 0.2 --ep 0.2 --seed "
					+ subject + " " + query + "  | correctMafft  -w 20 -f tab  " };
			p = Runtime.getRuntime().exec(cmd);
			p.waitFor();

			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

			String line = "";
			while ((line = reader.readLine()) != null) {
				if (!line.trim().isEmpty()) {
					String[] linesplitted = line.trim().split("\\s+");
					if (linesplitted[0].startsWith("_")) {
						linesplitted[0] = linesplitted[0].split("_")[linesplitted.length];
					}
					alignmentResult.put(linesplitted[0], linesplitted[1].trim().replace("\\r|\\n", ""));
				}
			}
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		return alignmentResult;
	}

	public int getNumberOfHypthens(String seq, boolean reverseFirst) {

		int numberOfHyphens = 0;
		if (reverseFirst) {
			seq = new StringBuilder(seq).reverse().toString();
		}
		if (seq.startsWith("-")) {
			for (Character c : seq.toCharArray()) {

				if (c.equals('-')) {
					numberOfHyphens++;
				} else {
					break;
				}
			}
		} else {
			return numberOfHyphens;
		}
		return numberOfHyphens;

	}

	public AlignmentCompareResult getAlignmentEvaluation(Map<String, String> alignmentResult, String repeatInfo) {
		// data to be returned
		AlignmentCompareResult alignmentCompareResult = new AlignmentCompareResult();
		char correctedChar, artificialChar, originalChar, repeatChar;
		// getting each key
		String originalKey = "", artificialKey = "", correctedKey = "";

		for (String seqKey : alignmentResult.keySet()) {
			if (seqKey.toLowerCase().startsWith("r")) {
				originalKey = seqKey;
			} else if (seqKey.toLowerCase().startsWith("a")) {
				artificialKey = seqKey;
			} else {
				correctedKey = seqKey;
			}

		}
		int start = getNumberOfHypthens(alignmentResult.get(correctedKey), false);
		int rightHyphens = getNumberOfHypthens(alignmentResult.get(correctedKey), true);
		// length is 1 based but extracting is 0 based
		int end = alignmentResult.get(correctedKey).length() - rightHyphens;

		// variable to calculate efficiency
		int corIndelSimple = 0;
		int corMismatchSimple = 0;

		int uncorIndelSimple = 0;
		int uncorMismatchSimple = 0;

		int introducedIndelSimple = 0;
		int introducedMismatchSimple = 0;

		// efficiency in other repeats

		int corIndelOther = 0;
		int corMismatchOther = 0;

		int uncorIndelOther = 0;
		int uncorMismatchOther = 0;

		int introducedIndelOther = 0;
		int introducedMismatchOther = 0;

		// efficiency in non repeats

		int corIndelNoRepeat = 0;
		int corMismatchNoRepeat = 0;

		int uncorIndelNoRepeat = 0;
		int uncorMismatchNoRepeat = 0;

		int introducedIndelNoRepeat = 0;
		int introducedMismatchNoRepeat = 0;

		// efficiency overall

		int corIndel = 0; // ref = - and cor = - OR art = - and cor
							// == ref
		int corMismatch = 0; // ref == cor != art and ref == [ACGT]

		int uncorIndel = 0; // ref = - and cor = [ACGT] OR art = -
							// and ref = [ACGT]
		int uncorMismatch = 0; // ref != art == cor

		int introducedIndel = 0; // cor = [ACGT] and ref = - OR cor
									// = - and ref = [ACGT]
		int introducedMismatch = 0; // ref != cor != art

		if (!repeatInfo.isEmpty()) {
			int repearIterator = getBeginningOfRepeatseq(start, alignmentResult.get(originalKey));
			if(repearIterator > 0){
				repearIterator = repearIterator - 1;
			}
			// length is 1 based but extracting is 0 based
//			System.out.println("===============================\nsequence name is "+ correctedKey+
//					" length of repeat seq "+ repeatInfo.length()+
//					" length of original seq with hyphens "+ alignmentResult.get(originalKey).length()+
//					" length of original seq after removing hyphens "+ alignmentResult.get(originalKey).replaceAll("-", "").length()+
//					" length of corrected "+ alignmentResult.get(correctedKey).length()+
//					" length of artificial "+ alignmentResult.get(artificialKey).length()+
//					" repeat iterator beginning "+repearIterator+
//					" start is "+start+
//					" end is "+end);
			for (int i = start; i < end; i++) {
				
				originalChar = Character.toLowerCase(alignmentResult.get(originalKey).charAt(i));
				artificialChar = Character.toLowerCase(alignmentResult.get(artificialKey).charAt(i));
				correctedChar = Character.toLowerCase(alignmentResult.get(correctedKey).charAt(i));
				if (originalChar != '-')
					repearIterator++;
//				if(repearIterator >= repeatInfo.length()){
//					System.out.println("last index of repeat iterator before crash is "+ repearIterator+" last index for sequence "+ i);
//					System.out.println(alignmentResult.get(originalKey));
//				}
				
				if ((end -1) == i) {
					repearIterator--;
				}
				try {
					repeatChar = Character.toLowerCase(repeatInfo.charAt(repearIterator));
				} catch (IndexOutOfBoundsException e) {
					e.printStackTrace();
					repeatChar = repeatInfo.charAt(repearIterator-1);
					System.out.println("an error happened in "+ correctedKey +" where all repeat info is "
					+repeatInfo.length()+ " at position "+repearIterator+" before the end of sequence at "+i+" from total"+ end);
					repearIterator--;
				}
				


				
//				if (originalChar == '-'){
//				repearIterator = repearIterator;
//			}else {
//				repearIterator++;
//			}
				switch (repeatChar) {
				// Simple repeat
				case 's':
					if (correctedChar == artificialChar && correctedChar == originalChar) {
						// do nothing
					} else if (correctedChar == originalChar) {
						if ((artificialChar == '-') || (correctedChar == '-'))
							corIndelSimple++;
						else
							corMismatchSimple++;
					} else {
						if (correctedChar == artificialChar) {
							if (correctedChar == '-')
								uncorMismatchSimple++;
							else
								uncorIndelSimple++;
						} else {
							if ((correctedChar == '-') || (originalChar == '-'))
								introducedIndelSimple++;
							else
								introducedMismatchSimple++;

						}
					}
					break;
				// otHer repeat
				case 'h':
					if (correctedChar == artificialChar && correctedChar == originalChar) {
						// do nothing
					} else if (correctedChar == originalChar) {
						if ((artificialChar == '-') || (correctedChar == '-'))
							corIndelOther++;
						else
							corMismatchOther++;
					} else {
						if (correctedChar == artificialChar) {
							if (correctedChar == '-')
								uncorMismatchOther++;
							else
								uncorIndelOther++;
						} else {
							if ((correctedChar == '-') || (originalChar == '-'))
								introducedIndelOther++;
							else
								introducedMismatchOther++;

						}
					}
					break;
				// no repeat
				default:
					if (correctedChar == artificialChar && correctedChar == originalChar) {
						// do nothing
					} else if (correctedChar == originalChar) {
						if ((artificialChar == '-') || (correctedChar == '-'))
							corIndelNoRepeat++;
						else
							corMismatchNoRepeat++;
					} else {
						if (correctedChar == artificialChar) {
							if (correctedChar == '-')
								uncorMismatchNoRepeat++;
							else
								uncorIndelNoRepeat++;
						} else {
							if ((correctedChar == '-') || (originalChar == '-'))
								introducedIndelNoRepeat++;
							else
								introducedMismatchNoRepeat++;

						}
					}
					break;
				}

			}
//			System.out.println("repeat iterator after loop "+repearIterator);
		} else {

			for (int i = start; i <= (end - 1); i++) {
				originalChar = Character.toLowerCase(alignmentResult.get(originalKey).charAt(i));
				artificialChar = Character.toLowerCase(alignmentResult.get(artificialKey).charAt(i));
				correctedChar = Character.toLowerCase(alignmentResult.get(correctedKey).charAt(i));

				if (correctedChar == artificialChar && correctedChar == originalChar) {
					// do nothing
				} else if (correctedChar == originalChar) {
					if ((artificialChar == '-') || (correctedChar == '-'))
						corIndel++;
					else
						corMismatch++;
				} else {
					if (correctedChar == artificialChar) {
						if (correctedChar == '-')
							uncorMismatch++;
						else
							uncorIndel++;
					} else {
						if ((correctedChar == '-') || (originalChar == '-'))
							introducedIndel++;
						else
							introducedMismatch++;

					}
				}
			}
		}
		alignmentCompareResult.setStart(start);
		alignmentCompareResult.setEnd(end);

		alignmentCompareResult.setCorIndel(corIndel);
		alignmentCompareResult.setCorMismatch(corMismatch);
		alignmentCompareResult.setUncorIndel(uncorIndel);
		alignmentCompareResult.setUncorMismatch(uncorMismatch);
		alignmentCompareResult.setIntroducedIndel(introducedIndel);
		alignmentCompareResult.setIntroducedMismatch(introducedMismatch);

		alignmentCompareResult.setCorIndelNoRepeat(corIndelNoRepeat);
		alignmentCompareResult.setCorMismatchNoRepeat(corMismatchNoRepeat);
		alignmentCompareResult.setUncorMismatchNoRepeat(uncorMismatchNoRepeat);
		alignmentCompareResult.setUncorIndelNoRepeat(uncorIndelNoRepeat);
		alignmentCompareResult.setIntroducedIndelNoRepeat(introducedIndelNoRepeat);
		alignmentCompareResult.setIntroducedMismatchNoRepeat(introducedMismatchNoRepeat);

		alignmentCompareResult.setCorIndelSimple(corIndelSimple);
		alignmentCompareResult.setCorMismatchSimple(corMismatchSimple);
		alignmentCompareResult.setUncorIndelSimple(uncorIndelSimple);
		alignmentCompareResult.setUncorMismatchSimple(uncorMismatchSimple);
		alignmentCompareResult.setIntroducedIndelSimple(introducedIndelSimple);
		alignmentCompareResult.setIntroducedMismatchSimple(introducedMismatchSimple);

		alignmentCompareResult.setCorIndelOther(corIndelOther);
		alignmentCompareResult.setCorMismatchOther(corMismatchOther);
		alignmentCompareResult.setUncorIndelOther(uncorIndelOther);
		alignmentCompareResult.setUncorMismatchOther(uncorMismatchOther);
		alignmentCompareResult.setIntroducedIndelOther(introducedIndelOther);
		alignmentCompareResult.setIntroducedMismatchOther(introducedMismatchOther);

		return alignmentCompareResult;

	}

	public Map<String, List<String>> getMafMap(List<MafRecord> mafRecords) {
		// loop through the maf to make dictionary
		Map<String, List<String>> mafDictionary = new HashMap<String, List<String>>();
		for (MafRecord mafRecord : mafRecords) {
			String originalRead = mafRecord.getMafRecord().get(0).getSeqValue();
			String artificialRead = mafRecord.getMafRecord().get(1).getSeqValue();
			String mafSeqName = mafRecord.getMafRecord().get(1).getSeqName();
			String sign = String.valueOf(mafRecord.getMafRecord().get(1).getSign());
			List<String> seqInf = Arrays.asList(artificialRead, originalRead, sign);
			mafDictionary.put(mafSeqName, seqInf);
		}
		return mafDictionary;
	}

	public String getCorrectedLengthRepeat(int originalLength, String seqRepeat) {
		int repeatSeqLength = seqRepeat.length();
		if (originalLength > repeatSeqLength) {
			String repeat = new String(new char[originalLength - repeatSeqLength]).replace("\0", "N");
			seqRepeat = seqRepeat.concat(repeat);

		}
		return seqRepeat;
	}

	public int getBeginningOfRepeatseq(int numberOfHyphens, String referenceSeq) {
		int repeatStart = 0;
		if(numberOfHyphens < 1)
			return repeatStart;
		for (int i = 0; i < numberOfHyphens; i++) {
			if (referenceSeq.charAt(i) != '-')
				repeatStart++;
		}
		return repeatStart;
	}

	public double getGCContent(String sequence) {
		double GCContent = 0;
		int GCValue = sequence.length() - sequence.replaceAll("(G|g|C|c)", "").length();
		try {
			GCContent = GCValue * 100d / sequence.length();
		} catch (ArithmeticException e) {
			e.printStackTrace();
		}
		return new BigDecimal(GCContent).setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	public double localCompositionComplexity(String sequence, Integer round) {
		double countA = 0d, countG = 0d, countC = 0d, countT = 0d;
		sequence = sequence.toUpperCase();

		if (sequence.contains("A")) {
			countA = complexityCount(sequence, "A");
		}
		if (sequence.contains("C")) {
			countC = complexityCount(sequence, "C");
		}
		if (sequence.contains("G")) {
			countG = complexityCount(sequence, "G");
		}
		if (sequence.contains("T")) {
			countT = complexityCount(sequence, "T");
		}
		if(round != null){
			return new BigDecimal(-(countA + countC + countG + countT)).setScale(round, BigDecimal.ROUND_HALF_UP).doubleValue();
		}else {
			return -(countA + countC + countG + countT);
		}
	}

	public double localCompositionComplexity(String sequence){
		return localCompositionComplexity(sequence, null);
	}
	
	public double complexityCount(String seq, String letter) {
		double countLetter = 0d;
		double seqLength = seq.length();
		double l2 = Math.log(2);
		double seqCount = (seqLength - seq.replaceAll(letter, "").length());

		countLetter = (seqCount / seqLength) * ((Math.log(seqCount / seqLength)) / l2);
		return countLetter;

	}

	public int getTotalLength(List<Read> reads) {
		int totalLength = 0;
		for (Read read : reads) {
			totalLength += read.getSeqLength();
		}
		return totalLength;
	}
	
	public int getTotalLengthPbcr(List<PbcrData> reads) {
		int totalLength = 0;
		for (PbcrData read : reads) {
			totalLength += read.getSeqLength();
		}
		return totalLength;
	}
	public static void main(String[] args) {
//		String s = "---agc--gc--caa----";
//		System.out.println(s.length());
//		int n = new SequenceTools().getNumberOfHypthens(s, false);
//		System.out.println(n);
//		System.out.println(s.charAt(n));
//		
//		int f = new SequenceTools().getNumberOfHypthens(s, true);
//		System.out.println(f);
//		System.out.println(s.charAt(s.length() - f-1));
		
//		System.err.println(new SequenceTools().getBeginningOfRepeatseq(3069,"cttttccgcctccgcc--a-ccaactg-ttccgggggtttagacatgtacagaaaagga--tgacttc-tcc-tgattgcagcttcagcagtatcag-atattatg-tcgaacccttgtgt-tctttatccctgaaaggtttctcttatcagccccctcaca-ct-tccc-ttaatatacttatg--ggtatat-a-tatatatatatatatatat-gtacaagctcaataaat-caacttatgataggagaaacg-ctatt-atccaatcaccttgctta-tatactta-tttcc-c-tctccctacttttgg-tactactcga-g--ga-ttagttcctc-ctctcgccgcagagcaa-t-gccttcgaaagtg--gcccgcagagac-tctttc-agcacgttacaactaaaagaaagggtga-gca-aac-aaaaaaaa-agagtaacggg-gaaa-aa-gagaaaacaaaaa-aatta-ttaagaggac-aaagcg-atgagtacccgaataataagaatatcttagggaaa-ctttgtggtccgcaaaagactcgca-act-tacggaacgcacgacaaactgctcc-ttttccccat-ggg-ggagct-cacaaccacacgcaacccccga-gaacata-atacacat--g--tga-cccga--gaaggg-aacagcagca-taaatcgtgtg--ctt-cat-ac-gtaagcgttatgcatacaagagcttttgaac-a-tccagactcaata-t-acatag-caggtaacaagtgataataaa-ggcaga-gaa-agaaatgaagaaggga--c-cgtaatggtt-tgcagac-agcccctccct-c-cttcacgcacccttttccgcagcagccatga-cattaaaaaaaaaaaaaact-ttcc-ca-cacacacacacacaaacacacacagagag-agagaga-gaga-gacactgacacaacagaaccacgcc-gactcatggacactgt-tg-c-aggcagtgaaggcg-cacacacaaaaaaaaatg--tacgtgggacg-cgactt-tttcctcct-c-tgtgctc-tgttg-tt-g-ttg-ttgccttttcact-cc-actct-gt-aatcgtctaca-tttcgttcatcctccccc---ccccgct-cccttttt-ttccccgccaggac---agttcgcgaag-aatt-gga-agaagagagcaat---agggacctcagc-tcacagctgcagatttgtgcaatgaagcaaacaa--a-c-aaaatagagcccc-a-t-gtacaaccgcaggagg-tggg-gagtcaatgatcaag-aaga--aaaagtgag-gcagcggaggta-acga-cacactt-cagtgagcgcagagaaaat-aatgaaagactgag-atgaa-gtacaggagaa-a-aaag-aaaagttcggacgca-ctacctgaaaa-gggcatattccacctggcgtcaccat-ata-agaagcagt--ccacatacctgctactggtc-actcttctccttc-cccct-ca-att-attattactctcccctcgatact-tttcttatc-ttatccttctcactc-tttttctt-ttt---ttt-ttaattttctccc-ttgcattttctt-t-gattgct-gcctcaatttcct-ttacattctc-t-tccttata-aagcggat-aaa-atcattt-tgct-gtgggt-gga-ttcgtagtccgct-taaacgcagcgtgatt-t-ca-a-agtttctttctctcttct-tcctctcccagggg-a-taaaaaga-aata-tgagtacgaagcgc-cc-aacaggaaaaagacacata--aacgcatac-acat--aacgca-caaa-ac-acagcggctga-gg-cagcgaatc--acacaccttgacggttattcgtgcccgcagcaaacagctg-gacggataaaggggaagagaacgtaaacaga-gtacagaaaa-gt-aaaaggtcaacatcacggaggcacaaa-agggtaggat-c--accgag-acctgtcgta-caggtgggtacgtttt-ttttt-ttg-a-aaaactg-a-cat-ggct--tta-ctttctgcggagtataggcggagcat-cggcaaa-ctgat-ccttcaggtc-acgctttcggcgctgtccct-tttcttcacgc-agccgc-tttgaaagtagacgctgctgcatctgctg-tcgcaactcctca-tg-c-gagacgacgggatcac-c-gtcatggtcatccttcccctcaa-c-a-ctttcgtcccc-gtacagttgagcgctt-ggaaggactgaacaa-cggacaaacttcgctgc--agtgcc-gtccttt-ctt-aca-ggcaagttcagtacg-ggactaaagtctgctgttccctc--aaccaatgcc-tgtaataga-cggacgtccact-tta-ct-accagaa-ctaccttcac-cgcc-ctcctccttaaccgcactca-caatgcccctagtgtttt-ccgccccaccggccgccgccgc-gtttccgcctctgtccccatc-gg-tt-tcctt-c-tccttaccag-ggagataa-gtctgat-catcgcccgcgccaatcggca-aagtccgcttg--g-aaccatccgccccttcttctgcggcctttt-gt-tcttgtagcatcgcctcc--gt-tcgtttatatagctg-gtaagtgcacaatat-cctcgtagc-tcgttc-ctctat-aaggccaag-aaacttcaactga-gtggattccgta-cagtggtccgatccg--accaggcgtc-ggg-cttc-ctccgct-gagcatccaatccgcttg-aaaacaccctctgtggtctcgcg-aatcctggcgagaacc-tcttt-gtgaacttcag-cagag--c-gattaagacg-atctagcatgagctct-gtctcgg---ccagacgatcttcgtattc-cttgatgaggcggcgctgctg-aacgtca-tgctcg-tgttcttccgcgattaccttcttcagcgtgtgt-at-atccc--gttccat-ct-cctctcgactggtagttagttcgttgat-acg-ctt---g--taaagggagaagt-tactct-caccaacaacctgg-taagtctgagccagc-ttttgcaaat-ca-cca---tctggagcgaacatggacagt-tgcgcgggaa-tgt-ccataatctctgatccc-acctca-gc-ggaagacataccat-cgga-agcatcctctccatcttccgt-aaat-ggggtaggggcatgag-tgttt-gc-aacagggag-gttttt-gcccacgccgctgtgatctacc-g-tagggtttggc--atgtagg-aacgcgcctcaaattcaatttgccgccggt-ggagttcatctcgcatt-ctgttgatg-tcg-atcatgcatacatcc--aagtt-acgc-acag-cggtgtcatattcctccttctg-ctcgct-aagcgctgttctaagctgctccacctgctgcaggt--agccgtctcgttcatcca---g-gtcgt-ggttt-gactgttc-aatccgttccg-acatgatttttttctta-gatctcagatcg-tcctc-c-atcttcttgtgtacac-gttgaaaaacgcgccgttccccgcgaattat-atcgatgtg-atcgcgc-agctc-tttgtt-atagg-a-aacagcg-tc--"));
//		String space10 = new String(new char[3732]).replace('\0', 'm');
//		System.out.println(space10.length());
//		System.out.println(space10.charAt(space10.length()));
		
		for (int i = 0; i < 10; i++) {
			System.out.println(i);
			
		}
	}

}
