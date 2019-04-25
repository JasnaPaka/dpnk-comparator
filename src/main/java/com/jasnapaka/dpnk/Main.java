package com.jasnapaka.dpnk;

import java.io.File;
import java.nio.file.Files;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {
	
	private static Logger log = LogManager.getRootLogger();

	private static void printHelp() {
		// TODO
	}
	
	private static boolean checkFile(String filepath) {
		return new File(filepath).exists();
	}
	
	private static boolean excelCheck(String filepath) throws Exception {

		byte[] bytes = Files.readAllBytes(new File(filepath).toPath());
		ExcelReader reader = new ExcelReader(bytes);

		if (!reader.getIsExcel()) {
			return false;
		}
		if (reader.findColumn(DPNKComparator.COLUMN_USERPROFILE) == null) {
			return false;
		}
		if (reader.findColumn(DPNKComparator.COLUMN_FIRSTNAME) == null) {
			return false;
		}
		if (reader.findColumn(DPNKComparator.COLUMN_LASTNAME) == null) {
			return false;
		}
		if (reader.findColumn(DPNKComparator.COLUMN_EMAIL) == null) {
			return false;
		}

		return true;
	}
	
	public static void main(String[] args) throws Exception {
		if(log.isDebugEnabled()) {
			log.debug("main(" + args + ")");
		}
		
		if (args.length != 2) {
			log.error("Chybný počet parametrů.");
			printHelp();
			return;
		}
		
		if (!checkFile(args[0])) {
			log.error("První soubor nebyl nalezen: " + args[0]);
			printHelp();
			return;
		}

		if (!checkFile(args[1])) {
			log.error("Druhý soubor nebyl nalezen: " + args[1]);
			printHelp();
			return;
		}

		if (!excelCheck(args[0])) {
			log.error("První soubor není exportem z DPNK.");
			printHelp();
			return;
		}

		if (!excelCheck(args[1])) {
			log.error("Druhý soubor není exportem z DPNK.");
			printHelp();
			return;
		}

		new DPNKComparator(args[0], args[1]);
	}

}
