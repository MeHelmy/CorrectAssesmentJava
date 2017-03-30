package pl.comgen.maf;

/**
 * @author Medhat
 *
 */

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.FileReader;

public class Maf {
	private MafRecord mafRecord = null;
	private MafRecordData mafRecordData = null;

	public List<MafRecord> readFile(String fileName) {

		List<MafRecord> mafRecords = new ArrayList<MafRecord>();

		if (fileName != null && !fileName.trim().isEmpty()) {
			try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
				String sCurrentLine;
				while ((sCurrentLine = br.readLine()) != null) {
					if (!sCurrentLine.trim().isEmpty()) {
						if (sCurrentLine.startsWith("a")) {
							// new record
							mafRecord = new MafRecord();
							mafRecords.add(mafRecord);
						} else {
							// data added to the previous record
							mafRecordData = new MafRecordData();
							String[] sCurrentLineSplitted = sCurrentLine.split("\\s+");
							mafRecordData.setSeqName(sCurrentLineSplitted[1]);
							mafRecordData.setStart(Long.parseLong(sCurrentLineSplitted[2]));
							mafRecordData.setEnd(Long.parseLong(sCurrentLineSplitted[3]));
							mafRecordData.setSign(sCurrentLineSplitted[4].charAt(0));
							mafRecordData.setLength(Long.parseLong(sCurrentLineSplitted[5]));
							mafRecordData.setSeqValue(sCurrentLineSplitted[6]);
							// get last MafRecord
							if (mafRecords.size() > 0)
								mafRecords.get(mafRecords.size() - 1).addRecord(mafRecordData);
							else
								mafRecords.add(mafRecord);
						}
					}
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return mafRecords;
	}


}
