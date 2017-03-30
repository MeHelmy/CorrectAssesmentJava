/**
 * 
 */
package pl.comgen.assess.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * @author medhat
 *
 */
public class SequenceRepeat {
	public Map<String, String> readRepeat(String repeatFile) {
		Map<String, String> seqRepeat = new HashMap<String, String>();

		if (repeatFile != null && !repeatFile.trim().isEmpty()) {
			try (BufferedReader br = Files.newBufferedReader(Paths.get(repeatFile))) {
				String sCuurentLine;
				String[] sCuurentLineSplited;
				for(int i=1;i<=3;i++)
				{
				    br.readLine();
				}
				String seqName, repeatType, repeat;
				int start, end;
				StringBuilder repeats = new StringBuilder();

				while ((sCuurentLine = br.readLine()) != null) {
					sCuurentLineSplited = sCuurentLine.replace("\\n|\\r", "").trim().split("\\s+");
					seqName = sCuurentLineSplited[4];
					start = Integer.valueOf(sCuurentLineSplited[5]);
					end = Integer.valueOf(sCuurentLineSplited[6]);
					repeatType = sCuurentLineSplited[10];

					if (seqRepeat.containsKey(seqName)) {
						repeats = new StringBuilder();
	
						if (seqRepeat.get(seqName).length() < start) {
							repeat = new String(new char[start - seqRepeat.get(seqName).length()]).replace("\0", "N");
							repeats.append(repeat);
						}
						if ("Simple_repeat".equals(repeatType)) {
							repeat = new String(new char[end - start]).replace("\0", "S");
							repeats.append(repeat);
						} else {
							repeat = new String(new char[end - start]).replace("\0", "H");
							repeats.append(repeat);
						}
						seqRepeat.put(seqName, seqRepeat.get(seqName).concat(repeats.toString()));
						;
					} else {
						if(!repeats.toString().isEmpty()){
							seqRepeat.put(seqName, repeats.toString());
						}
						repeats = new StringBuilder();

						if (start > 0) {
							repeat = new String(new char[start-1]).replace("\0", "N");
							repeats.append(repeat);
						}

						if ("Simple_repeat".equals(repeatType)) {
							repeat = new String(new char[end - start]).replace("\0", "S");
							repeats.append(repeat);
						} else {
							repeat = new String(new char[end - start]).replace("\0", "H");
							repeats.append(repeat);
						}
						seqRepeat.put(seqName, repeats.toString());
					}

				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		
		return seqRepeat;
	}


}
