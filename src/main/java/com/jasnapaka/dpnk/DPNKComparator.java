package com.jasnapaka.dpnk;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class DPNKComparator {

	private static Logger log = LogManager.getRootLogger();

	public static final String	COLUMN_USERPROFILE	= "userprofile";
	public static final String	COLUMN_FIRSTNAME	= "userprofile__user__first_name";
	public static final String	COLUMN_LASTNAME		= "userprofile__user__last_name";
	public static final String	COLUMN_EMAIL		= "userprofile__user__email";

	
	public DPNKComparator(String filepath1, String filepath2) throws Exception {
		byte[] fileContent1 = Files.readAllBytes(new File(filepath1).toPath());
		byte[] fileContent2 = Files.readAllBytes(new File(filepath2).toPath());
		
		process(fileContent1, fileContent2);
	}

	private void process(byte[] fileContent1, byte[] fileContent2) throws Exception {
		if (log.isDebugEnabled()) {
			log.debug("process()");
		}

		List<DPNKUser> dpnkUsers1 = new ArrayList<DPNKUser>();
		List<DPNKUser> dpnkUsers2 = new ArrayList<DPNKUser>();

		loadFile(fileContent1, dpnkUsers1);
		loadFile(fileContent2, dpnkUsers2);

		int pocet = 0;
		Map<String, Integer> emails = new HashMap<String, Integer>();

		for (DPNKUser user : dpnkUsers1) {
			if (!findUser(user, dpnkUsers2)) {
				System.out.println(user.getFullname() + "|" + user.getEmail());
				pocet++;

				String emailDomain = user.getEmail().split("@")[1];
				if (!emails.containsKey(emailDomain)) {
					emails.put(emailDomain, 0);
				}
				Integer value = emails.get(emailDomain);
				value++;
				emails.put(emailDomain, value);
			}
		}

		System.out.println("Počet: " + pocet);
		System.out.println("---------------");
		System.out.println("Dle domény u e-mailu:");

		Set<Entry<String, Integer>> set = emails.entrySet();
		List<Entry<String, Integer>> list = new ArrayList<Entry<String, Integer>>(set);
		Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
			public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
				int result = (o2.getValue()).compareTo(o1.getValue());
				if (result != 0) {
					return result;
				} else {
					return o1.getKey().compareTo(o2.getKey());
				}
			}
		});

		for (Entry<String, Integer> value : list) {
			System.out.println(value.getKey() + "|" + value.getValue());
		}

	}

	private boolean findUser(DPNKUser user, List<DPNKUser> dpnkUsers2) {
		if (log.isTraceEnabled()) {
			log.trace("findUser(" + user + ")");
		}

		// nejprve zkusíme dle id profilu
		for (DPNKUser user2 : dpnkUsers2) {
			if (user.getId() == user2.getId()) {
				return true;
			}
		}

		// dále dle e-mailu
		for (DPNKUser user2 : dpnkUsers2) {
			if (user.getEmail().equals(user2.getEmail())) {
				return true;
			}
		}

		// dále dle jména
		for (DPNKUser user2 : dpnkUsers2) {
			if (user.getFullname().replaceAll(" ", "").equals(user2.getFullname().replaceAll(" ", ""))) {
				return true;
			}
		}

		return false;
	}

	private void loadFile(byte[] fileContent, List<DPNKUser> dpnkUsers) throws Exception {
		if (log.isTraceEnabled()) {
			log.trace("loadFile()");
		}

		ExcelReader reader = new ExcelReader(fileContent);

		Integer idPos = reader.findColumn(COLUMN_USERPROFILE);
		Integer firstnamePos = reader.findColumn(COLUMN_FIRSTNAME);
		Integer lastnamePos = reader.findColumn(COLUMN_LASTNAME);
		Integer emailPos = reader.findColumn(COLUMN_EMAIL);

		try (Workbook workbook = WorkbookFactory.create(new ByteArrayInputStream(fileContent))) {
			Sheet sheet = workbook.getSheetAt(0);

			int i = 1;
			Row row;
			while ((row = sheet.getRow(i)) != null) {

				int id = Integer.valueOf(row.getCell(idPos).getStringCellValue());
				String firstname = row.getCell(firstnamePos).getStringCellValue();
				String lastname = row.getCell(lastnamePos).getStringCellValue();
				String email = row.getCell(emailPos).getStringCellValue();

				dpnkUsers.add(new DPNKUser(id, firstname, lastname, email));

				i++;
			}
		}
	}

}
