/**
 * 
 */
package pl.comgen.assess;

import java.io.IOException;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import pl.comgen.assess.lordec.Lordec;
import pl.comgen.assess.lsc.Lsc;
import pl.comgen.assess.pbcr.Pbcr;
import pl.comgen.assess.proovread.Proovread;
import pl.comgen.assess.util.SequenceRepeat;
import pl.comgen.maf.Maf;

/**
 * @author medhat
 *
 */
public class RefineArgs {

	// @Option(name = "-r", aliases = { "--useRepeat" }, usage = "Repeat maksk
	// supported")
	// private boolean isRepeatMask;

	@Option(name = "-o", aliases = { "--output" }, usage = "output assessment to this file")
	private String out;

	@Option(name = "-be", aliases = { "--outputBed" }, usage = "output bed to this file")
	private String bed;

	@Option(name = "-in", aliases = { "--input" }, required = true, usage = "input corrected file to this file")
	private String input;

	@Option(name = "-rf", aliases = { "--repeatFile" }, usage = "input repeat file to this file")
	private String repeatFile;

	@Option(name = "-l", aliases = { "--log" }, usage = "input log file for pbcr to this file")
	private String logFile;

	@Option(name = "-d", aliases = { "--workDir" }, usage = "directory where temp file will be created")
	private String tempDir;

	@Option(name = "-c", aliases = {
			"--correctionFile" }, required = true, usage = "type of correction file lo=lordec ls=lsc pr=proovread pb=pbcr")
	private String correctionFileType;

	@Option(name = "-m", aliases = { "--mafFile" }, required = true, usage = "input maf file to this file")
	private String maf;

	@Option(name = "-lo", aliases = { "--lostData" }, usage = "output lost data to this file")
	private String lostData;

	/**
	 * @return the lostData
	 */
	public String getLostData() {
		return lostData;
	}

	/**
	 * @return the maf
	 */
	public String getMaf() {
		return maf;
	}

	// /**
	// * @return the isRepeatMask
	// */
	// public boolean isRepeatMask() {
	// return isRepeatMask;
	// }

	/**
	 * @return the out
	 */
	public String getOut() {
		return out;
	}

	/**
	 * @return the bed
	 */
	public String getBed() {
		return bed;
	}

	/**
	 * @return the input
	 */
	public String getInput() {
		return input;
	}

	/**
	 * @return the repeatFile
	 */
	public String getRepeatFile() {
		if (repeatFile == null)
			repeatFile = "";
		return repeatFile;
	}

	/**
	 * @return the logFile
	 */
	public String getLogFile() {
		return logFile;
	}

	/**
	 * @return the tempDir
	 */
	public String getTempDir() {
		return tempDir;
	}

	/**
	 * @return the correctionFileType
	 */
	public String getCorrectionFileType() {
		return correctionFileType;
	}

	public void doMain(String[] args) throws IOException {
		CmdLineParser parser = new CmdLineParser(this);

		try {
			// parse the arguments.
			parser.parseArgument(args);

			// detect what kind of correction file
			if (getOut().isEmpty())
				out = "./assessment.txt";
			if (getBed().isEmpty())
				bed = "./assessment.txt";
			if (getTempDir().isEmpty())
				tempDir = ".";
			if (getLostData().isEmpty())
				lostData = "./lost_data.txt";

			switch (getCorrectionFileType()) {
			case "pr":
				Proovread proovread = new Proovread();

				if (getRepeatFile().isEmpty()) {
					proovread.assessProovread(new Maf().readFile(getMaf()), proovread.readProovread(getInput()),
							new SequenceRepeat().readRepeat(""), getOut(), getBed(), getLostData(), getTempDir());
				} else {
					proovread.assessProovread(new Maf().readFile(getMaf()), proovread.readProovread(getInput()),
							new SequenceRepeat().readRepeat(getRepeatFile()), getOut(), getBed(), getLostData(),
							getTempDir());
				}

				break;
			case "lo":
				Lordec lordec = new Lordec();

				if (getRepeatFile().isEmpty()) {
					lordec.assessLordec(new Maf().readFile(getMaf()), lordec.readLordec(getInput()),
							new SequenceRepeat().readRepeat(""), getOut(), getBed(), getLostData(), getTempDir());
				} else {
					lordec.assessLordec(new Maf().readFile(getMaf()), lordec.readLordec(getInput()),
							new SequenceRepeat().readRepeat(getRepeatFile()), getOut(), getBed(), getLostData(),
							getTempDir());
				}
				break;
			case "ls":
				Lsc lsc = new Lsc();
				System.out.println(getRepeatFile());
				if (getRepeatFile().isEmpty()) {
					lsc.assessLsc(new Maf().readFile(getMaf()), lsc.readLsc(getInput()),
							new SequenceRepeat().readRepeat(""), getOut(), getBed(), getLostData(), getTempDir());
				} else {
					lsc.assessLsc(new Maf().readFile(getMaf()), lsc.readLsc(getInput()),
							new SequenceRepeat().readRepeat(getRepeatFile()), getOut(), getBed(), getLostData(),
							getTempDir());
				}
				break;
			case "pb":
				if (getLogFile().isEmpty()) {
					System.err.println("there is no log filr please use --log to add log file in case of PBcR ");
				} else {
					Pbcr pbcr = new Pbcr();
					if (getRepeatFile().isEmpty()) {
						pbcr.assessPbcr(new Maf().readFile(getMaf()),
								pbcr.readPbcr(getInput(), pbcr.readLog(getLogFile())),
								new SequenceRepeat().readRepeat(""), getOut(), getBed(), getLostData(), getTempDir());
					} else {
						pbcr.assessPbcr(new Maf().readFile(getMaf()),
								pbcr.readPbcr(getInput(), pbcr.readLog(getLogFile())),
								new SequenceRepeat().readRepeat(getRepeatFile()), getOut(), getBed(), getLostData(), getTempDir());

					}
				}
				break;

			default:
				break;
			}

		} catch (CmdLineException e) {

			System.err.println(e.getMessage());
			System.err.println("java SampleMain [options...] arguments...");
			// print the list of available options
			parser.printUsage(System.err);
			System.err.println();

			return;
		}

		// this will redirect the output to the specified output
		System.out.println(out);

	}
}
