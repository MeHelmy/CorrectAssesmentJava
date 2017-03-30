/**
 * 
 */
package pl.comgen.assess.lsc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import pl.comgen.assess.util.AlignmentCompareResult;
import pl.comgen.assess.util.Read;
import pl.comgen.assess.util.SequenceTools;
import pl.comgen.maf.MafRecord;

/**
 * @author medhat
 *
 */
public class Lsc {
	public Map<String, Read> readLsc(String lscFile) {
		String seqName = "", seqValue = "";
		Map<String, Read> lscReads = new HashMap<String, Read>();

		if (lscFile != null && !lscFile.trim().isEmpty()) {
			try (BufferedReader br = Files.newBufferedReader(Paths.get(lscFile))) {
				String sCurrentLine;
				while ((sCurrentLine = br.readLine()) != null) {
					if (sCurrentLine.startsWith(">")) {

						if (!seqValue.isEmpty()) {
							Read lscData = new Read();
							lscData.setSeqName(seqName);
							lscData.setSeqValue(seqValue);
							lscData.setSeqLength(seqValue.length());
							lscReads.put(seqName, lscData);
							seqValue = "";
						}
						seqName = sCurrentLine.trim().replace(">", "").split("\\|")[0];
					} else {
						seqValue += sCurrentLine.trim().replace("\\n", "");

					}
				}

			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		return lscReads;
	}

	public void assessLsc(List<MafRecord> mafRecords, Map<String, Read> readLsc, Map<String, String> seqRepeat,
			String outputFile, String outputBed, String missedData, String workingDirectory) {
		// initialize all variables
		SequenceTools sequenceTools = new SequenceTools();
		AlignmentCompareResult alignmenteval;
		try {
			File outputAssesment = new File(outputFile);
			File outputAssessmentBed = new File(outputBed);
			File missedAssesment = new File(missedData);

			// if file doesn't exists, then create it
			if (!outputAssesment.exists()) {
				outputAssesment.createNewFile();
			}

			// if file doesn't exists, then create it
			if (!outputAssessmentBed.exists()) {
				outputAssessmentBed.createNewFile();
			}

			// if file doesn't exists, then create it
			if (!missedAssesment.exists()) {
				missedAssesment.createNewFile();
			}

			FileWriter fileAssessmentWriter = new FileWriter(outputAssesment.getAbsoluteFile());
			BufferedWriter bufferAssessmentWriter = new BufferedWriter(fileAssessmentWriter);

			FileWriter fileBedtWriter = new FileWriter(outputAssessmentBed.getAbsoluteFile());
			BufferedWriter bufferBedWriter = new BufferedWriter(fileBedtWriter);

			FileWriter fileMissedWriter = new FileWriter(missedAssesment.getAbsoluteFile());
			BufferedWriter bufferMissedtWriter = new BufferedWriter(fileMissedWriter);

			Map<String, List<String>> mafDictionary = sequenceTools.getMafMap(mafRecords);
			Map<String, String> sequenceToComare = new LinkedHashMap<>();
			Map<String, String> alignmentResult = new LinkedHashMap<>();

			double GCContent = 0d, localCompositionComplexity = 0d, efficiency = 0d, allChanges = 0d;

			int alignmentLength, subSeqLength, correctedAlignmentLength;

			String seqName, originalSeqValue, artificialSeqValue, seqSign, sizeOfAllSubreads, repeatInfo = "",
					subSeqName, subSeqValue;
			// #################################################
			for (Entry<String, Read> read : readLsc.entrySet()) {
				seqName = read.getKey();
				originalSeqValue = mafDictionary.get(seqName).get(1);
				artificialSeqValue = mafDictionary.get(seqName).get(0);
				seqSign = mafDictionary.get(seqName).get(2);
				alignmentLength = artificialSeqValue.length();
				sizeOfAllSubreads = String.valueOf(read.getValue().getSeqLength());
				// get sequence repeat value
				if (!seqRepeat.isEmpty() && seqRepeat != null && seqRepeat.containsKey(seqName)) {
					repeatInfo = sequenceTools.getCorrectedLengthRepeat(originalSeqValue.replace("-", "").length(),
							seqRepeat.get(seqName));
				}else {
					repeatInfo = "";
				}
				subSeqName = read.getValue().getSeqName();
				subSeqValue = read.getValue().getSeqValue();
				subSeqLength = (int) read.getValue().getSeqLength();
				if (seqSign.equals("-")) {
					// reverse complement
					subSeqValue = sequenceTools.reverseComplement(subSeqValue);
				}

				// write the sequence that will be aligned.
				sequenceToComare.put(seqName, subSeqValue);
				sequenceTools.wrtieSequenceFile(workingDirectory, "corrected.fa", sequenceToComare);
				sequenceToComare.clear();
				sequenceToComare.put("ref", originalSeqValue);
				sequenceToComare.put("art", artificialSeqValue);
				sequenceTools.wrtieSequenceFile(workingDirectory, "original.fa", sequenceToComare);
				sequenceToComare.clear();
				// running alignment using mafft.
				alignmentResult = sequenceTools.alignSeq(workingDirectory + "/original.fa",
						workingDirectory + "/corrected.fa");
				// if alignment succeeded
				if (alignmentResult != null && !alignmentResult.isEmpty()) {
					alignmenteval = sequenceTools.getAlignmentEvaluation(alignmentResult, repeatInfo);
					GCContent = sequenceTools.getGCContent(subSeqValue);
					localCompositionComplexity = sequenceTools
							.localCompositionComplexity(originalSeqValue.replace("-", ""), 5);
					correctedAlignmentLength = alignmenteval.getEnd() - alignmenteval.getStart();
					allChanges = alignmenteval.getUncorMismatch() + alignmenteval.getUncorIndel()
							+ alignmenteval.getCorMismatch() + alignmenteval.getCorIndel();
					if (allChanges != 0) {
						efficiency = ((alignmenteval.getCorMismatch() + alignmenteval.getCorIndel())
								- (alignmenteval.getIntroducedMismatch() + alignmenteval.getIntroducedIndel()))
								/ allChanges;
						efficiency = efficiency * 100d;

					}
					// write the result to output file
					bufferAssessmentWriter.write(seqName + "\t" + subSeqName + "\t" + alignmenteval.getCorIndel() + "\t"
							+ alignmenteval.getCorMismatch() + "\t" + alignmenteval.getUncorIndel() + "\t"
							+ alignmenteval.getUncorMismatch() + "\t" + alignmenteval.getIntroducedIndel() + "\t"
							+ alignmenteval.getIntroducedMismatch() + "\t" + alignmenteval.getCorIndelSimple() + "\t"
							+ alignmenteval.getCorMismatchSimple() + "\t" + alignmenteval.getUncorIndelSimple() + "\t"
							+ alignmenteval.getUncorMismatchSimple() + "\t" + alignmenteval.getIntroducedIndelSimple()
							+ "\t" + alignmenteval.getIntroducedMismatchSimple() + "\t"
							+ alignmenteval.getCorIndelOther() + "\t" + alignmenteval.getCorMismatchOther() + "\t"
							+ alignmenteval.getUncorIndelOther() + "\t" + alignmenteval.getUncorMismatchOther() + "\t"
							+ alignmenteval.getIntroducedIndelOther() + "\t"
							+ alignmenteval.getIntroducedMismatchOther() + "\t" + alignmenteval.getCorIndelNoRepeat()
							+ "\t" + alignmenteval.getCorMismatchNoRepeat() + "\t"
							+ alignmenteval.getUncorIndelNoRepeat() + "\t" + alignmenteval.getUncorMismatchNoRepeat()
							+ "\t" + alignmenteval.getIntroducedIndelNoRepeat() + "\t"
							+ alignmenteval.getIntroducedMismatchNoRepeat() + "\t" + alignmentLength + "\t"
							+ correctedAlignmentLength + "\t" + efficiency + "\t"
							+ originalSeqValue.replace("-", "").length() + "\t" + GCContent + "\t"
							+ localCompositionComplexity + "\t" + sizeOfAllSubreads + "\t" + subSeqLength + "\n");
					// write bed file
					bufferBedWriter.write(seqName + "\t" + subSeqName + "\t" + alignmenteval.getStart() + "\t"
							+ alignmenteval.getEnd() + "\t" + efficiency);
				} else {
					// write that there is missed data
					bufferMissedtWriter.write(seqName + "\n");
				}

			}

			bufferAssessmentWriter.close();
			bufferBedWriter.close();
			bufferMissedtWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
