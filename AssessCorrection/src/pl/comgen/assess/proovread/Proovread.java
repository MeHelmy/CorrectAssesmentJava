/**
 * 
 */
package pl.comgen.assess.proovread;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
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
public class Proovread {

	public ProovreadResult readProovread(String proovreadFile) {

		// method that return a map where key is the sequence id and value is
		// list of object contain info about each sequence.
		String seqName = "", subSeqName = "", seqValue = "";
		long allCorrectedSeqLegth = 0;

		Map<String, List<Read>> proovreadReads = new HashMap<String, List<Read>>();
		Map<String, String> seqLength = new HashMap<String, String>();
		ProovreadResult proovreadResult = new ProovreadResult();

		Read proovreadData = null;
		List<Read> proovreadDatas = null;

		try (BufferedReader br = new BufferedReader(new FileReader(proovreadFile))) {

			String sCurrentLine;
			while ((sCurrentLine = br.readLine()) != null) {
				if (!sCurrentLine.trim().isEmpty()) {
					if (sCurrentLine.startsWith(">")) {
						if (!seqName.trim().isEmpty())
							seqLength.put(seqName, String.valueOf(allCorrectedSeqLegth));
						// sequence name
						subSeqName = sCurrentLine.split("\\s+")[0].replace(">", "");

						if (subSeqName.contains("."))
							seqName = subSeqName.split("\\.")[0];
						else
							seqName = subSeqName;

						// check if the key already exists
						if (proovreadReads.containsKey(seqName)) {
							proovreadData = new Read();
							proovreadData.setSeqName(subSeqName);
							// get list and add new value to it
							proovreadReads.get(seqName).add(proovreadData);
							seqLength.put(seqName, String.valueOf(allCorrectedSeqLegth));
						} else {
							proovreadDatas = new ArrayList<Read>();
							proovreadData = new Read();
							allCorrectedSeqLegth = 0;
							proovreadData.setSeqName(subSeqName);
							proovreadDatas.add(proovreadData);
							proovreadReads.put(seqName, proovreadDatas);
							seqLength.put(seqName, String.valueOf(allCorrectedSeqLegth));
						}

					} else {
						// sequence data
						seqValue = sCurrentLine.replaceAll("\\r|\\n", "");
						int size = proovreadReads.get(seqName).size();
						allCorrectedSeqLegth += seqValue.length();
						List<Read> listOfproovread = proovreadReads.get(seqName);
						if (size > 0) {
							listOfproovread.get(size - 1).setSeqValue(seqValue);
							listOfproovread.get(size - 1).setSeqLength(seqValue.length());
						}

					}

				}

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		proovreadResult.setProovreadReads(proovreadReads);
		proovreadResult.setTotalReadLength(seqLength);
		return proovreadResult;
	}

	public void assessProovread(List<MafRecord> mafRecords, ProovreadResult proovreadResult,
			Map<String, String> seqRepeat, String outputFile, String outputBed,String missedData, String workingDirectory) {
		
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
			Map<String, List<Read>> reads = proovreadResult.getProovreadReads();
			Map<String, String> totalSubReadLength = proovreadResult.getTotalReadLength();
			Map<String, String> sequenceToComare = new LinkedHashMap<>();
			Map<String, String> alignmentResult = new LinkedHashMap<>();

			double GCContent = 0d, localCompositionComplexity = 0d, efficiency = 0d, allChanges = 0d;

			int  alignmentLength, subSeqLength, correctedAlignmentLength;

			String seqName, originalSeqValue, artificialSeqValue, seqSign, sizeOfAllSubreads, repeatInfo = "",
					subSeqName, subSeqValue;
			// #################################################
		
		// loop through the main sequence and sub sequence that was corrected
		for (Entry<String, List<Read>> read : reads.entrySet()) {
			// key is sequence name ; reads are each corrected read for this sequence.

			seqName = read.getKey();
			originalSeqValue = mafDictionary.get(seqName).get(1);
			artificialSeqValue = mafDictionary.get(seqName).get(0);
			seqSign = mafDictionary.get(seqName).get(2);
			alignmentLength = artificialSeqValue.length();
			sizeOfAllSubreads = totalSubReadLength.get(seqName);
			
			
			// get sequence repeat value
			if (!seqRepeat.isEmpty() && seqRepeat != null && seqRepeat.containsKey(seqName)) {
				repeatInfo = sequenceTools.getCorrectedLengthRepeat(originalSeqValue.replace("-", "").length(),
						seqRepeat.get(seqName));
			}else {
				repeatInfo = "";
			}

			// looping through the sequences that was corrected in each sequence_id
			for (Read readInfo : read.getValue()) {
				subSeqName = readInfo.getSeqName();
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
//					System.out.println("subseq name "+ subSeqName);
					 alignmenteval = sequenceTools.getAlignmentEvaluation(alignmentResult, repeatInfo);
					 GCContent = sequenceTools.getGCContent(subSeqValue);
					 localCompositionComplexity = sequenceTools.localCompositionComplexity(originalSeqValue.replace("-", ""), 5);
					 correctedAlignmentLength = alignmenteval.getEnd() - alignmenteval.getStart();
					 allChanges = alignmenteval.getUncorMismatch() + alignmenteval.getUncorIndel()
					 + alignmenteval.getCorMismatch() + alignmenteval.getCorIndel(); 
					 if(allChanges != 0){
						 efficiency = ((alignmenteval.getCorMismatch() + alignmenteval.getCorIndel()) - 
								 (alignmenteval.getIntroducedMismatch() + alignmenteval.getIntroducedIndel())) / allChanges ;
						 efficiency = efficiency * 100d;
						 
					 }
					 // write the result to output file
					 bufferAssessmentWriter.write(seqName+"\t"
					 +subSeqName+"\t"
							 +alignmenteval.getCorIndel()+"\t"
							 +alignmenteval.getCorMismatch()+"\t"
							 +alignmenteval.getUncorIndel()+"\t"
							 +alignmenteval.getUncorMismatch()+"\t"
							 +alignmenteval.getIntroducedIndel()+"\t"
							 +alignmenteval.getIntroducedMismatch()+"\t"
							 +alignmenteval.getCorIndelSimple()+"\t"
							 +alignmenteval.getCorMismatchSimple()+"\t"
							 +alignmenteval.getUncorIndelSimple()+"\t"
							 +alignmenteval.getUncorMismatchSimple()+"\t"
							 +alignmenteval.getIntroducedIndelSimple()+"\t"
							 +alignmenteval.getIntroducedMismatchSimple()+"\t"
							 +alignmenteval.getCorIndelOther()+"\t"
							 +alignmenteval.getCorMismatchOther()+"\t"
							 +alignmenteval.getUncorIndelOther()+"\t"
							 +alignmenteval.getUncorMismatchOther()+"\t"
							 +alignmenteval.getIntroducedIndelOther()+"\t"
							 +alignmenteval.getIntroducedMismatchOther()+"\t"
							 +alignmenteval.getCorIndelNoRepeat()+"\t"
							 +alignmenteval.getCorMismatchNoRepeat()+"\t"
							 +alignmenteval.getUncorIndelNoRepeat()+"\t"
							 +alignmenteval.getUncorMismatchNoRepeat()+"\t"
							 +alignmenteval.getIntroducedIndelNoRepeat()+"\t"
							 +alignmenteval.getIntroducedMismatchNoRepeat()+"\t"
							 +alignmentLength+"\t"
							 +correctedAlignmentLength+"\t"
							 +efficiency+"\t"
							 +originalSeqValue.replace("-", "").length()+"\t"
							 +GCContent+"\t"
							 +localCompositionComplexity+"\t"
							 +sizeOfAllSubreads+"\t"
							 +subSeqLength+"\n");
					 //write bed file
					 bufferBedWriter.write(seqName+"\t"+subSeqName+"\t"+alignmenteval.getStart()+"\t"+alignmenteval.getEnd()+"\t"+efficiency);
				}else {
					// write that there is missed data
					bufferMissedtWriter.write(seqName+"\n");
				}

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
