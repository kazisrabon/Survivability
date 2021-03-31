import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.Map;
import java.util.TreeMap;

class Combination {
    private static XSSFWorkbook workbook;
    private static XSSFSheet spreadsheet;
    private static XSSFRow row;
    private static Map< Integer, Object[] > empinfo;
    private static int rowid = 0, cellid = 0;
    private static XSSFCellStyle cellStyleBody;
    /* arr[]  ---> Input Array 
    data[] ---> Temporary array to store current combination 
    start & end ---> Staring and Ending indexes in arr[] 
    index  ---> Current index in data[] 
    r ---> Size of a combination to be printed */

    static void combinationUtil(int arr[], int data[], int start,
                                int end, int index, int r)
    {
        // Current combination is ready to be printed, print it 
        if (index == r)
        {
            for (int j=0; j<r; j++) {
                row = spreadsheet.createRow(rowid++);
                Cell cell = row.createCell(cellid);
                cell.setCellStyle(cellStyleBody);
                cell.setCellValue(data[j]);
                System.out.print(data[j] + " ");
            }
            System.out.println("");
            return;
        }

        // replace index with all possible elements. The condition 
        // "end-i+1 >= r-index" makes sure that including one element 
        // at index will make a combination with remaining elements 
        // at remaining positions 
        for (int i=start; i<=end && end-i+1 >= r-index; i++)
        {
            data[index] = arr[i];
            combinationUtil(arr, data, i+1, end, index+1, r);
        }
    }

    // The main function that prints all combinations of size r 
    // in arr[] of size n. This function mainly uses combinationUtil() 
    static void printCombination(int arr[], int n, int r)
    {
        // A temporary array to store all combination one by one 
        int data[]=new int[r];

        // Print all combination using temprary array 'data[]' 
        combinationUtil(arr, data, 0, n-1, 0, r);
    }

    /*Driver function to check for above function*/
    public static void main (String[] args) {
        int arr[] = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};
        int r = 2;
        int n = arr.length;

        //Create blank workbook
        workbook = new XSSFWorkbook();
        //Create a blank sheet
        spreadsheet = workbook.createSheet("Combination");
        empinfo = new TreeMap< Integer, Object[]>();
        cellStyleBody = (XSSFCellStyle) workbook.createCellStyle();
        cellStyleBody.setAlignment(HorizontalAlignment.CENTER);

        printCombination(arr, n, r);
//        for (int i = 13; i <= 16; i++) {
//            printCombination(arr, n, i);
//        }
//        try {
//            // this Writes the workbook 'fileName'
//            FileOutputStream out = new FileOutputStream(new File("Combination"+".xlsx"));
//            workbook.write(out);
//            out.close();
//            System.out.println("Combination"+".xlsx written successfully on disk.");
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//        }
    }
} 