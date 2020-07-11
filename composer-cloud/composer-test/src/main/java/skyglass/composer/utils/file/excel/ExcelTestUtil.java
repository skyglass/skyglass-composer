package skyglass.composer.utils.file.excel;

import java.util.List;

import org.apache.poi.ss.examples.ExcelComparator;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * @author skyglass
 */
public class ExcelTestUtil {

	public static List<String> compareFiles(XSSFWorkbook xssfWorkbook1, XSSFWorkbook xssfWorkbook2) {
		return ExcelComparator.compare(xssfWorkbook1, xssfWorkbook2);
	}

}
