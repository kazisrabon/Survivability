//import java.io.File;
//import java.io.FileInputStream;
//import java.util.Iterator;
//import org.apache.poi.ss.usermodel.Cell;
//import org.apache.poi.ss.usermodel.Row;
//import org.apache.poi.xssf.usermodel.XSSFSheet;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import com.opencsv.exceptions.CsvValidationException;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class OpenandRead {
    private static final int GENERATOR = 1, LOAD = -1, LINK = 0;

    public static void main(String[] args)
    {
        String csvFile = "C:/Users/kazii/OneDrive/Desktop/Poroseva/Srabon/ADM.csv";

        CSVReader reader = null;
        try {
            reader = new CSVReader(new FileReader(csvFile));
            List<String[]> myEntries = reader.readAll();
            String[] line;
            int[][] adm = new int[myEntries.size()][myEntries.size()];;
            int i = 0;
            while ((line = myEntries.get(i)) != null) {

                for (int j = i; j < line.length; j++) {
                    int value = Integer.parseInt(line[j]);
                    if (j == i) switch (value) {
                        case GENERATOR:
                            //increase generator
                            System.out.println("increase generator");
                            break;
                        case LOAD:
                            //increase generator
                            System.out.println("increase load");
                            break;
                        case LINK:
                            //increase generator
                            System.out.println("increase link");
                            break;
                    }
                    else{
                        System.out.println("cell[" + i + ", " + j + "] = " + line[j]);
                    }

                }
                System.out.println("\n");
                i++;
                if (i == myEntries.size()) break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CsvValidationException e) {
            e.printStackTrace();
        } catch (CsvException e) {
            e.printStackTrace();
        }

//        try
//        {
//            File file = new File("C:\\demo\\employee.xlsx");   //creating a new file instance
//            FileInputStream fis = new FileInputStream(file);   //obtaining bytes from the file
////creating Workbook instance that refers to .xlsx file
//            XSSFWorkbook wb = new XSSFWorkbook(fis);
//            XSSFSheet sheet = wb.getSheetAt(0);     //creating a Sheet object to retrieve object
//            Iterator<Row> itr = sheet.iterator();    //iterating over excel file
//            while (itr.hasNext())
//            {
//                Row row = itr.next();
//                Iterator<Cell> cellIterator = row.cellIterator();   //iterating over each column
//                while (cellIterator.hasNext())
//                {
//                    Cell cell = cellIterator.next();
//                    System.out.println(cell.getCellType().toString());
////                    switch (cell.getCellType())
////                    {
////                        case Cell.CELL_TYPE_STRING:    //field that represents string cell type
////                            System.out.print(cell.getStringCellValue() + "\t\t\t");
////                            break;
////                        case Cell.CELL_TYPE_NUMERIC:    //field that represents number cell type
////                            System.out.print(cell.getNumericCellValue() + "\t\t\t");
////                            break;
////                        default:
////                    }
//                }
//                System.out.println("");
//            }
//        }
//        catch(Exception e)
//        {
//            e.printStackTrace();
//        }
    }
}
