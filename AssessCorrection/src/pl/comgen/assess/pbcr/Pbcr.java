/**
 * 
 */
package pl.comgen.assess.pbcr;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import pl.comgen.assess.util.AlignmentCompareResult;
import pl.comgen.assess.util.SequenceTools;
import pl.comgen.maf.MafRecord;

/**
 * @author medhat
 *
 */
public class Pbcr {
	public PbcrResult readPbcr(String pbcrFile, Map<String, String> readLog) {
		PbcrResult pbcrResult = new PbcrResult();
		Map<String, List<PbcrData>> pbcrReads = new HashMap<String, List<PbcrData>>();
		List<PbcrData> pbcrdDatas = null;
		long missingValues = 0;
		if (pbcrFile != null && !pbcrFile.trim().isEmpty()) {
			String originalName = "", newName, baseName, seqValue;
			long start, end;

			PbcrData pbcrData;
			try (BufferedReader br = Files.newBufferedReader(Paths.get(pbcrFile))) {
				String sCurrentLine;
				String[] seqTitleSplit;
				List<String> readCoordinate;
				while ((sCurrentLine = br.readLine()) != null) {
					if (sCurrentLine.startsWith(">")) {
						// get name of the read: ecoli_assembly_2044040_1/40_143
						// -> 2044040
						seqTitleSplit = sCurrentLine.replaceAll("[^0-9]", " ").trim().split(" ");
						// reverse the array so if the corrected sequence contains numbers in its name will not affect the result
						readCoordinate = Arrays.asList(seqTitleSplit);
						Collections.reverse(readCoordinate);
						baseName = readCoordinate.get(3);
						newName = String.valueOf(readCoordinate.get(3)) + "_" + String.valueOf(readCoordinate.get(2));
						start = Integer.parseInt(readCoordinate.get(1));
						end = Integer.parseInt(readCoordinate.get(0));
						if (readLog.containsKey(baseName)) {
							originalName = readLog.get(baseName);
							if (pbcrReads.containsKey(originalName)) {
								pbcrData = new PbcrData();
								pbcrData.setOriginalName(originalName);
								pbcrData.setNewName(newName);
								pbcrData.setBaseName(baseName);
								pbcrData.setStart(start);
								pbcrData.setEnd(end);
								pbcrReads.get(originalName).add(pbcrData);
							} else {
								pbcrdDatas = new ArrayList<PbcrData>();
								pbcrData = new PbcrData();
								pbcrData.setOriginalName(originalName);
								pbcrData.setNewName(newName);
								pbcrData.setBaseName(baseName);
								pbcrData.setStart(start);
								pbcrData.setEnd(end);
								pbcrdDatas.add(pbcrData);
								pbcrReads.put(originalName, pbcrdDatas);
							}

						} else {
							missingValues++;
						}

					} else {
						// get value of the read:
						seqValue = sCurrentLine.replaceAll("\\r|\\n", "");
						int size = pbcrReads.get(originalName).size();
						List<PbcrData> listOfpbcrRead = pbcrReads.get(originalName);
						if (size > 0) {
							listOfpbcrRead.get(size - 1).setSeqValue(seqValue);
							listOfpbcrRead.get(size - 1).setSeqLength(seqValue.length());
						}
					}

				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		pbcrResult.setMissedData(missingValues);
		pbcrResult.setReadData(pbcrReads);
		return pbcrResult;
	}

	public Map<String, String> readLog(String logFile) {
		Map<String, String> mapLog = new HashMap<String, String>();
		if (logFile != null && !logFile.trim().isEmpty()) {
			try (BufferedReader br = Files.newBufferedReader(Paths.get(logFile))) {
				// skip header
				br.readLine();
				String sCurrentLine;
				String[] sCurrentLineSplit;
				String originalName = "", newName = "";
				while ((sCurrentLine = br.readLine()) != null) {
					sCurrentLine = sCurrentLine.trim();
					sCurrentLineSplit = sCurrentLine.split("\\s+");
					originalName = sCurrentLineSplit[0];
					newName = sCurrentLineSplit[1].replaceAll("[^0-9]+", " ").trim().split(" ")[0];
					mapLog.put(newName, originalName);

				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		return mapLog;
	}

	public void assessPbcr(List<MafRecord> mafRecords, PbcrResult pbcrResult, Map<String, String> seqRepeat,
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
					subSeqName, subSeqValue, lostDataFromCorrection = String.valueOf(pbcrResult.getMissedData());
			// #################################################
			// looping through the sequences that was corrected in each
			// sequence_id
			for (Entry<String, List<PbcrData>> read : pbcrResult.getReadData().entrySet()) {

				// key is sequence name ; reads are each corrected read for this
				// sequence.

				seqName = read.getKey();
				originalSeqValue = mafDictionary.get(seqName).get(1);
				artificialSeqValue = mafDictionary.get(seqName).get(0);
				seqSign = mafDictionary.get(seqName).get(2);
				alignmentLength = artificialSeqValue.length();
				sizeOfAllSubreads = String.valueOf(sequenceTools.getTotalLengthPbcr(read.getValue()));
				// get sequence repeat value
				if (!seqRepeat.isEmpty() && seqRepeat != null && seqRepeat.containsKey(seqName)) {
					repeatInfo = sequenceTools.getCorrectedLengthRepeat(originalSeqValue.replace("-", "").length(),
							seqRepeat.get(seqName));
				}else {
					repeatInfo = "";
				}

				// looping through the sequences that was corrected in each
				// sequence_id
				for (PbcrData readInfo : read.getValue()) {
					subSeqName = readInfo.getNewName();
					subSeqValue = readInfo.getSeqValue();
					subSeqLength = (int) readInfo.getSeqLength();
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
					alignmentResult = new SequenceTools().alignSeq(workingDirectory + "/original.fa",
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
						bufferAssessmentWriter.write(seqName + "\t" + subSeqName + "\t" + alignmenteval.getCorIndel()
								+ "\t" + alignmenteval.getCorMismatch() + "\t" + alignmenteval.getUncorIndel() + "\t"
								+ alignmenteval.getUncorMismatch() + "\t" + alignmenteval.getIntroducedIndel() + "\t"
								+ alignmenteval.getIntroducedMismatch() + "\t" + alignmenteval.getCorIndelSimple()
								+ "\t" + alignmenteval.getCorMismatchSimple() + "\t"
								+ alignmenteval.getUncorIndelSimple() + "\t" + alignmenteval.getUncorMismatchSimple()
								+ "\t" + alignmenteval.getIntroducedIndelSimple() + "\t"
								+ alignmenteval.getIntroducedMismatchSimple() + "\t" + alignmenteval.getCorIndelOther()
								+ "\t" + alignmenteval.getCorMismatchOther() + "\t" + alignmenteval.getUncorIndelOther()
								+ "\t" + alignmenteval.getUncorMismatchOther() + "\t"
								+ alignmenteval.getIntroducedIndelOther() + "\t"
								+ alignmenteval.getIntroducedMismatchOther() + "\t"
								+ alignmenteval.getCorIndelNoRepeat() + "\t" + alignmenteval.getCorMismatchNoRepeat()
								+ "\t" + alignmenteval.getUncorIndelNoRepeat() + "\t"
								+ alignmenteval.getUncorMismatchNoRepeat() + "\t"
								+ alignmenteval.getIntroducedIndelNoRepeat() + "\t"
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

			}
			bufferAssessmentWriter.close();
			bufferBedWriter.close();
			bufferMissedtWriter.close();
			try{
				File dir = new File(workingDirectory);
				File f = null;
				if (dir.isDirectory()) {
					f = new File(dir, "lostDataByPbcr.txt");
				} 
				FileWriter fw = new FileWriter(f.getAbsolutePath());
				BufferedWriter bw =  new BufferedWriter(fw);
				bw.write(lostDataFromCorrection);
				bw.close();
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}


}
