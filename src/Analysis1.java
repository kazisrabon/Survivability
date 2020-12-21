//        1. Is there any fault?
//        2. Is all have fault?
//        3. Is all Generators have fault?
//        4. Is all Load have fault?
//        5. If all G can be reached from any available Lo
//        6. If some of G can be reached from any available Lo
//        7. If no G can be reached from any available Lo
// S - Survived, R - Reconfiguration, F - Failure, N - Total cases, M - #of faults, COLUMNSIZE - #of column in 'Analysis'
// count - count total combination or size M from code
// comb - total combination of size M mathematically
// ng - #of generator, nv - #of load, nh - #of link, nm - #of total element
// ngs - generators' number
// adjMatrix - adjacency matrix
// analysis - the final output. contains   M S R F N
// combinations - all the combination of size M
// faultMatrix - matrix after fault occurs

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Map;
import java.util.TreeMap;

public class Analysis1 {
    private static final int S = 1, R = 2, F = 3, N = 4, M = 0,
            PS = 5, PR = 6, PF = 7, COLUMNSIZE = 8;
    private int count = 0, comb = 0, ng = 2, nv = 2, nh = 2, nm = ng + nh + nv, countNg, faultSize;
    private int[] ngs, nvs, nhs, nms;
    private int[][] adjMatrix, combinations, faultMatrix;
    private double[][] analysis;

    private int getFaultSize() {
        return faultSize;
    }

    private void setFaultSize(int faultSize) {
        this.faultSize = faultSize;
    }


    private void initAnalysis(int x){
        analysis = new double[x][COLUMNSIZE];
        for (int i = 0; i < x; i++) {
            analysis[i][M] = i;
            for (int j = 1; j < COLUMNSIZE; j++) {
                analysis[i][j] = 0;
            }
        }
    }

    public double[][] getAnalysis() {
        return analysis;
    }

    private void initFaultMatrix(){
        faultMatrix = new int[adjMatrix.length][adjMatrix[0].length];
        for (int i = 0; i < adjMatrix.length; i++)
            for (int j = 0; j < adjMatrix[0].length; j++)
                faultMatrix[i][j] = adjMatrix[i][j];
    }

    private int[][] getFaultMatrix() {
        return faultMatrix;
    }

    private void setFaultMatrix(int[][] faultMatrix) {
        this.faultMatrix = faultMatrix;
    }

    private void initAdjMatrix(){
        adjMatrix = new int[nm][nm];
//        set 0 to all value
        for (int i = 0; i < nm; i++) {
            for (int j = 0; j < nm; j++) {
                adjMatrix[i][j] = 0;
            }
        }
//       todo set 1, -1 and 0 for generator, load and link
//        for (int i = 0; i < ng; i++) {
//            adjMatrix[i][i] = 1;
//        }
//        for (int i = ng; i < ng+nv; i++) {
//            adjMatrix[i][i] = -1;
//        }
    }

    private int[][] getAdjMatrix() {
        return adjMatrix;
    }

    private void setAdjMatrix(int x, int y) {
        adjMatrix[x-1][y-1] = 1;
        adjMatrix[y-1][x-1] = 1;
    }

    private void setNgs(int ng) {
        ngs = new int[ng];
        for (int i = 0; i < ng; i++) {
            ngs[i] = i+1; // 1, 2
        }
        countNg = ngs.length;
    }

    private int getCountNg() {
        return countNg;
    }

    private int getCount() {
        return count;
    }

    private void setCount(int count) {
        this.count = count;
    }

    private int getComb() {
        return comb;
    }

    private void setComb(int comb) {
        this.comb = comb;
    }

    private void initCombinations(int length){
        combinations = new int[getComb()][length];
    }

    private void setCombinations(int position, int[] combinations) {
        this.combinations[position] = combinations;
    }

    private int[][] getCombinations() {
        return combinations;
    }

    private int factorial(int n) {
        int fact = 1;
        int i = 1;
        while(i <= n) {
            fact *= i;
            i++;
        }
        return fact;
    }

    private void combinationUtil(int[] arr, int[] data, int start,
                                 int end, int index, int r)
    {
        if (index == r) {
            int[] combination = new int[r];
            for (int j=0; j<r; j++) {
//                to print all the combination
//                System.out.print(data[j] + " ");
                combination[j] = data[j];
            }
//            System.out.println();

            int [][] combinations = new int[getComb()][combination.length];
            combinations[count] = combination;
            setCombinations(count, combination);
            ++count;
            setCount(count);
            return;
        }
        for (int i=start; i<=end && end-i+1 >= r-index; i++) {
            data[index] = arr[i];
            combinationUtil(arr, data, i+1, end, index+1, r);
        }
    }

    private void findCombinations(int[] arr, int n, int r) {
        int[] data = new int[r];
        setCount(0);
        combinationUtil(arr, data, 0, n-1, 0, r);
    }

    /*Main function to check for above function*/
    public static void main (String[] args) {
//        todo make the analysis for all combinations of each size
        int[] arr = {1, 2, 3, 4, 5, 6};
        int n = arr.length;
        Analysis1 analysis1 = new Analysis1();
//        initiate the adjacency matrix
        analysis1.initAdjMatrix();
        analysis1.initAnalysis(n+1);
//        connections
        analysis1.setAdjMatrix(1,3);
        analysis1.setAdjMatrix(1,5);
        analysis1.setAdjMatrix(1,6);
        analysis1.setAdjMatrix(2,4);
        analysis1.setAdjMatrix(2,5);
        analysis1.setAdjMatrix(2,6);
        analysis1.setAdjMatrix(3,5);
        analysis1.setAdjMatrix(3,6);
        analysis1.setAdjMatrix(4,5);
        analysis1.setAdjMatrix(4,6);
        analysis1.setAdjMatrix(5,6);
//        set number of generators
        analysis1.setNgs(analysis1.ng);
//        int r = 2;
//        loop started
        for (int r = 0; r <= n; r++) {
            int comb = analysis1.factorial(n) /
                    (analysis1.factorial(r) * analysis1.factorial(n-r));
//        print adjacency matrix
//        analysis1.printMatrix(analysis1.getAdjMatrix(), "Adjacency matrix");

            // set all values
            analysis1.setComb(comb);
            analysis1.initCombinations(r);

            // calculate the combinations for 'arr' and size 'r'
            analysis1.findCombinations(arr, n, r);
            int[][] combinations = analysis1.getCombinations();

            analysis1.setFaultSize(r);
            analysis1.analysis[r][N] = combinations.length;

//        check different survivability of combinations
            if (combinations.length == 1){
                if (combinations[0].length == 0) analysis1.analysis[r][S] += 1;
                else analysis1.analysis[r][F] += 1;
            }
            else {
                int[][] faultMatrix;
                for (int i = 0; i < combinations.length; i++) {
                    int[] combination = combinations[i];
                    analysis1.initFaultMatrix();
                    for (int j = 0; j < combinations[0].length; j++) {
//                    set 0
                        analysis1.setZero(combination[j]);
                    }
                    faultMatrix = analysis1.getFaultMatrix();
//                analyze
                    analysis1.analyzeSurvivability(faultMatrix);
                }
            }
//            calculate probabilities
            analysis1.analysis[r][PS] = analysis1.analysis[r][S]/analysis1.analysis[r][N];
            analysis1.analysis[r][PR] = analysis1.analysis[r][R]/analysis1.analysis[r][N];
            analysis1.analysis[r][PF] = analysis1.analysis[r][F]/analysis1.analysis[r][N];
        }
//        loop ended
        analysis1.printMatrix(analysis1.getAnalysis(), "Survivability Analysis");
        analysis1.saveFile(analysis1.getAnalysis(), " Topology 2", "Survivability Analysis");
    }

    private void saveFile(double[][] matrix, String sheetName, String fileName) {

        //Create blank workbook
        XSSFWorkbook workbook = new XSSFWorkbook();

        //Create a blank sheet
        XSSFSheet spreadsheet = workbook.createSheet(sheetName);

        //Create row object
        XSSFRow row;

        Map< Integer, Object[] > empinfo = new TreeMap< Integer, Object[]>();
        empinfo.put( 1, new Object[] {
                "m", "S", "R", "F", "N", "P(S)", "P(R)", "P(F)" });
        int rowid = 0;
//        styling
        final XSSFCellStyle cellStyleHeader = (XSSFCellStyle) workbook.createCellStyle();
        cellStyleHeader.setFillBackgroundColor(IndexedColors.LIGHT_GREEN.getIndex());
//        cellStyleHeader.setFillPattern( FillPatternType.SOLID_FOREGROUND );
        cellStyleHeader.setAlignment(HorizontalAlignment.CENTER);
        final XSSFCellStyle cellStyleBody = (XSSFCellStyle) workbook.createCellStyle();
        cellStyleBody.setAlignment(HorizontalAlignment.CENTER);

        for (int i = 0; i < matrix.length ; i++) {
            row = spreadsheet.createRow(rowid++);
            int cellid = 0;
//            for (int j = 0; j <matrix[0].length ; j++) {
//                Cell cell = row.createCell(cellid++);
//                cell.setCellValue(matrix[i][j]);
//            }
            if (i == 0){
                Object [] objectArr = empinfo.get(i+1);
                for (int j = 0; j < matrix[0].length; j++) {
                    Cell cell = row.createCell(cellid++);
                    cell.setCellStyle(cellStyleHeader);
                    cell.setCellValue((String) objectArr[j]);
                }
            }
            else{
                for (int j = 0; j < matrix[0].length; j++) {
                    Cell cell = row.createCell(cellid++);
                    cell.setCellStyle(cellStyleBody);
                    cell.setCellValue(matrix[i-1][j]);
                }
            }
        }
        try {
            // this Writes the workbook 'fileName'
            FileOutputStream out = new FileOutputStream(new File(fileName+".xlsx"));
            workbook.write(out);
            out.close();
            System.out.println(fileName+".xlsx written successfully on disk.");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    //    Warshall algorithm
    private void analyzeSurvivability(int[][] faultMatrix) {
        int[][] reach = new int[nm][nm];
        int i, j, k;
        for (i = 0; i < nm; i++)
            for (j = 0; j < nm; j++)
                reach[i][j] = faultMatrix[i][j];

        for (k = 0; k < nm; k++)
        {
            // Pick all vertices as source one by one
            for (i = 0; i < nm; i++)
            {
                // Pick all vertices as destination for the
                // above picked source
                for (j = 0; j < nm; j++)
                {
                    // If vertex k is on a path from i to j,
                    // then make sure that the value of reach[i][j] is 1
                    reach[i][j] = (reach[i][j]!=0) ||
                            ((reach[i][k]!=0) && (reach[k][j]!=0))?1:0;
                }
            }
        }


        // Print the shortest distance matrix
//        printMatrix(reach, "Following matrix is transitive closure"+
//                " of the given graph");
//        calculate survivability
        findSurvivability(reach);
    }

    private void findSurvivability(int[][] reach) {
        int r = getFaultSize();
        int totalGenerator = getCountNg();
        int totalGeneratorAfterFailure = 0;
        int[] countG = new int[ng];
        for (int i = 0; i < ng; i++) {
            countG[i] = 0;
        }
        for (int i = ng; i < ng + nv; i++)
            for (int j = 0; j < ng; j++)
                if (reach[i][j] == 1 && countG[j] == 0) countG[j] = 1;
        for (int i = 0; i < ng; i++) totalGeneratorAfterFailure += countG[i];

        if (totalGeneratorAfterFailure == totalGenerator)
            analysis[r][S] += 1;
        else if (totalGeneratorAfterFailure == 0)
            analysis[r][F] += 1;
        else analysis[r][R] += 1;;
    }

    /* A utility function to print solution */
    private void printMatrix(double[][] matrix, String caption) {
        System.out.println(caption);
        for (int i = 0; i < matrix.length; i++)
        {
            for (int j = 0; j < matrix[0].length; j++)
                System.out.print(matrix[i][j]+" ");
            System.out.println();
        }
    }

    private void setZero(int value) {
        int position = value-1;
        int[][] newMatrix;
        newMatrix = getFaultMatrix();
        for (int i = 0; i < nm; i++) {
            for (int j = 0; j < nm; j++) {
                if (i == position || j ==position) newMatrix[i][j] = 0;
            }
        }
        setFaultMatrix(newMatrix);
    }
}
