package com.jasnapaka.dpnk;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelReader {

	private static Logger log = LogManager.getRootLogger();

	private byte[]			fileContent;

	public ExcelReader(byte[] fileContent) {
		this.fileContent = fileContent;
	}

	/**
	 * Vyhledá sloupec v tabulce dle názvu. Ignoruje se velikost písmen a předpokládá se, že nadpis sloupec je v prvním
	 * řádku.
	 * 
	 * @param nazev
	 *            název hled
	 * @return pozici, kde je sloupec uveden (číslování od nuly), nebo null, pokud nebyl nalezen
	 */
	public Integer findColumn(String nazev) throws IOException, InvalidFormatException {
		if (log.isDebugEnabled()) {
			log.debug("findColumn()");
		}

		try (Workbook workbook = WorkbookFactory.create(new ByteArrayInputStream(fileContent))) {
			Sheet sheet = workbook.getSheetAt(0);
			Row row = sheet.getRow(0);
			if (row == null) {
				return null;
			}

			int i = 0;
			Cell cell = null;
			while ((cell = row.getCell(i)) != null) {
				if (cell.getCellTypeEnum() == CellType.STRING
						&& StringUtils.equalsIgnoreCase(cell.getStringCellValue(), nazev)) {
					return i;
				}

				i++;
			}
		}

		return null;
	}

	/**
	 * Test, zda je soubor v původním či novém formátu Microsoft Excel (XLS či XLSX).
	 * 
	 * @return
	 */
	public boolean getIsExcel() {
		if (log.isTraceEnabled()) {
			log.trace("getIsExcel(" + ")");
		}

		return getIsXls() || getIsXlsx();
	}

	/**
	 * Test, zda je soubor v původním formátu Microsoft Excel (XLS).
	 * 
	 * @return
	 */
	public boolean getIsXls() {
		if (log.isTraceEnabled()) {
			log.trace("getIsXls(" + ")");
		}

		try {
			try (HSSFWorkbook workbook = new HSSFWorkbook(new ByteArrayInputStream(fileContent))) {
				workbook.getNumberOfSheets();
				return true;
			}
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Test, zda je soubor v aktuálním formátu Microsoft Excel (XLSX).
	 * 
	 * @return
	 */
	public boolean getIsXlsx() {
		if (log.isTraceEnabled()) {
			log.trace("getIsXlsx(" + ")");
		}

		try {
			try (Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(fileContent))) {
				workbook.getNumberOfSheets();
				return true;
			}
		} catch (Exception e) {
			return false;
		}
	}

}
