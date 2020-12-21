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

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import com.opencsv.exceptions.CsvValidationException;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Map;
import java.util.TreeMap;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

/*
todo analyze different topology:
1. change the file path
todo change file name or sheet name
1. search file name or sheet name
 */

public class Analysis2 {
    private final int GENERATOR = 1, LOAD = -1, LINK = 0, CONNECTED = 1, NOTCONNECTED = 0;
    private static final int S = 1, R = 2, F = 3, N = 4, M = 0,
            PS = 5, PR = 6, PF = 7, COLUMNSIZE = 8;
    private int count = 0, comb = 0, ng, nv, nh, nm, countNg, faultSize;
    private int[] ngs;
    private int[] nvs;
    private int[] nhs;
    private int[] nms;

    public int[] getPowerInput() {
        return powerInput;
    }

    public void setPowerInput(int[] powerInput) {
        int[] onlyPowerInputs = new int[ng+nv];
        for (int i = 0; i < ng + nv; i++) {
            onlyPowerInputs[i] = powerInput[i];
        }
        this.powerInput = onlyPowerInputs;
//        if (ng + nv >= 0) System.arraycopy(powerInput, 0, this.powerInput, 0, ng + nv);
    }

    private int[] powerInput;
    private int[][] adjMatrix, combinations, faultMatrix;
    private double[][] analysis;

    public Analysis2() {
    }

    public void setAnalysis(int[] arr, int n, int[] criteriaValue){
        //        loop started
//        n is the number of components has error
        for (int r = 0; r <= n; r++) {
            int comb = factorial(n) /
                    (factorial(r) * factorial(n-r));
            // set all values
            setComb(comb);
            initCombinations(r);

            // calculate the combinations for 'arr' and size 'r'
            findCombinations(arr, n, r);
            int[][] combinations = getCombinations();

            setFaultSize(r);
            //put the number of combination in first column of r row
            analysis[r][N] = combinations.length;

//        check different survivability of combinations
//            if only one combination is found that is either survived or failure
            if (combinations.length == 1){
                if (combinations[0].length == 0) analysis[r][S] += 1;
                else analysis[r][F] += 1;
            }
//            otherwise pick each combination and analyze the scenario
            else {
                int[][] faultMatrix;
                for (int i = 0; i < combinations.length; i++) {
//                    combination is the array of failed components
                    int[] combination = combinations[i];
                    initFaultMatrix();
                    for (int j = 0; j < combinations[0].length; j++) {
//                    set 0
                        setZero(combination[j]);
                    }
                    faultMatrix = getFaultMatrix();
//                analyze
                    analyzeSurvivability(faultMatrix, combination, criteriaValue);
                }
            }
//            calculate probabilities
            analysis[r][PS] = analysis[r][S]/ analysis[r][N];
            analysis[r][PR] = analysis[r][R]/ analysis[r][N];
            analysis[r][PF] = analysis[r][F]/ analysis[r][N];
        }
//        loop ended
    }

    public Analysis2(String openFileName, String saveFileName) {
//get input from excel file
        int[] arr = setupInputsfromExcel(openFileName);
        int n = arr.length;

        initAnalysis(n+1);
//        set number of generators
        setNgs(getNg());
//        set analysis
        int[] criteriaValue = {1,1,1,1,0};
        setAnalysis(arr, n, criteriaValue);

        printMatrix(getAnalysis(), "Survivability Analysis");
//        change file name or sheet name
        saveFile(getAnalysis(), "Analysis", saveFileName);
    }

    /*Main function to check for above function*/
    public static void main (String[] args) {

//        numbering of the components
        //int[] arr = {1, 2, 3, 4, 5, 6, 7, 8};
        // use / for linux and \\ for windows
        String openFileName = System.getProperty("user.dir")+ "\\ADM.csv";
        String saveFileName = "Survivability Analysis of ADM";
        Analysis2 analysis2 = new Analysis2(openFileName, saveFileName);

    }

    public void setGVHM(int ng, int nv, int nh) {
        this.ng = ng;
        this.nv = nv;
        this.nh = nh;
        this.nm = ng+nv+nh;
    }

    public int getNg() {
        return ng;
    }

    public int getNv() {
        return nv;
    }

    public int getNh() {
        return nh;
    }

    public int getNm() {
        return nm;
    }

    private int getFaultSize() {
        return faultSize;
    }

    private void setFaultSize(int faultSize) {
        this.faultSize = faultSize;
    }

    public void initAnalysis(int x){
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

    private void initAdjMatrix(int nm){
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

    public void setNgs(int ng) {
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

    public void saveFile(double[][] matrix, String sheetName, String fileName) {

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

    //open excel file to read the matrix
    public int[] setupInputsfromExcel(String fileName){
        int[] arr = new int[0];
        int[] powerInputs = new int[0];
        int ng =0, nv = 0, nh = 0;
        CSVReader reader = null;
        try {
            reader = new CSVReader(new FileReader(fileName));
            List<String[]> myEntries = reader.readAll();
//        initiate the adjacency matrix
            initAdjMatrix(myEntries.size());
            arr = new int[myEntries.size()];
            powerInputs = new int[myEntries.size()];
            for (int i = 1; i <= myEntries.size(); i++) arr[i-1] = i;
            String[] line;
            int i = 0;
            while ((line = myEntries.get(i)) != null) {

                for (int j = i; j < line.length; j++) {
                    int value = Integer.parseInt(line[j]);

                    if (j==i){
                        if (value > 0) ng++;
                        else if (value < 0) nv++;
                        else nh++;

                        powerInputs[i] = value;
                    }

                    else{
                        int row = i+1;
                        int col = j+1;
                        if (value == CONNECTED) {
                            setAdjMatrix(row, col);
                            System.out.println("cell[" + row + ", " + col + "] = " + CONNECTED);
                        } else {
                            System.out.println("cell[" + row + ", " + col + "] = " + NOTCONNECTED);
                        }
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
        setGVHM(ng, nv, nh);
        setPowerInput(powerInputs);
        return arr;
    }

    //    Warshall algorithm
    private void analyzeSurvivability(int[][] faultMatrix, int[] combination, int[] criteriaValue) {
        int nm = getNm();
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
        // size of the array: ng+nv+2
        // second last value is for survived criteria
        // last value is for reconfiguration critetia
//        int[] criteriaValue = {1,1,1,1,0}; // size of the array: ng+nv+2
        findSurvivability(reach, criteriaValue, combination);
    }

    private void findSurvivability(int[][] reach, int[] criteriaValues, int[] combination) {
        int r = getFaultSize();
        int totalGenerator = getCountNg();
        int totalGeneratorAfterFailure = 0;
//        int totalLoadAfterFailure = 0;
        int ng = getNg();
        int nv = getNv();
        int[] countG = new int[ng];
        int[] countL = new int[nv];
//        int survivedPower = criteriaValues[criteriaValues.length - 2];
        int reconfiguredPower = criteriaValues[criteriaValues.length - 1];
        int generatedPower = 0;
        int absorbedPower = 0;
        for (int i = 0; i < criteriaValues.length - 1; i++) {
            if (criteriaValues[i] > 0)
                generatedPower+=criteriaValues[i];
            else
                absorbedPower+= criteriaValues[i];

            absorbedPower = absorbedPower*-1;
        }
//        populate counts
        for (int i = 0; i < ng; i++) {
            countG[i] = 0;
        }
        for (int i = 0; i < nv; i++) {
            countL[i] = 0;
        }
//        calculate active generator and load
        for (int i = ng; i < ng + nv; i++) {
            for (int j = 0; j < ng; j++) {
                if (reach[i][j] == 1) {
                    if (countG[j] == 0) {
                        countG[j] = 1;
                        totalGeneratorAfterFailure++;
                    }
                    if (countL[i-ng] == 0){
                        countL[i-ng] = 1;
//                        totalLoadAfterFailure++;
                    }
                }
            }
        }

//        for (int i = 0; i < ng; i++) totalGeneratorAfterFailure += countG[i];

        //todo check the criteria
        int totalPowerGenerated = 0;
        int totalPowerAbsorbed = 0;
        for (int i = 0; i < combination.length; i++) {
            if (combination[i] <= getNg()) {
                totalPowerGenerated += criteriaValues[combination[i] - 1];
            }
            else if (combination[i] <= getNg()+getNv()){
                totalPowerAbsorbed += criteriaValues[combination[i] - 1];
            }
        }
        int survivedGeneratorPower = generatedPower - totalPowerGenerated;
        int survivedLoadPower = absorbedPower + totalPowerAbsorbed;

        int[] survivedLoad = new int[getNv()];
//        when survived and reconfiguration are defined
//        check if there is limitation of power generated
//        if (survivedPower != 0) {
//            for (int i = 0; i < getNg(); i++) {
//                for (int j = 0; j < combination.length; j++) {
//                    if (i + 1 != combination[j]) {
//                        totalPowerGenerated += criteriaValues[i];
//                    }
//                }
//            }
////            check if there is limitation of power absorbed
//            if (reconfiguredPower != 0) {
//                int counterLoad = 0;
//                for (int i = getNg(); i < getNg() + getNv(); i++) {
//                    for (int j = 0; j < combination.length; j++) {
//                        if (i + 1 != combination[j]) {
//                            survivedLoad[counterLoad] = criteriaValues[i];
//                            totalPowerAbsorbed += criteriaValues[i];
//                            counterLoad++;
//                        }
//                    }
//                }
//
////                when all the generators are intact
//                if (totalGeneratorAfterFailure == totalGenerator)
//                    analysis[r][S] += 1;
//
////              when there is failure in some generator
//                else if (totalGeneratorAfterFailure < totalGenerator){
////                  when total generated is greater than power absorbed
//                    if (totalPowerGenerated >= totalPowerAbsorbed)
//                        analysis[r][S] += 1;
////                  when there is no generate power
//                    else if (totalPowerGenerated == 0)
//                        analysis[r][F] += 1;
////                  when total power generated is less then total power absorbed
//                    else {
////                        check if total generated power
//
//
//                        boolean isReconfigurable = false;
//                        for (int i = 0; i < survivedLoad.length; i++) {
////                          check if there is any load which can be satisfied by the survived generator
//                            if (survivedLoad[i] <= totalPowerGenerated)
//                                isReconfigurable = true;
//
//                            if (isReconfigurable)
//                                analysis[r][R] += 1;
//
//                            else analysis[r][F] += 1;
//                        }
//                    }
//                }
////                when there is no generator after fault
//                else if (totalGeneratorAfterFailure == 0)
//                    analysis[r][F] += 1;
//
//            }
//
////          todo when only survived scenario is defined
//            else {
////              when all the generators are intact
//                if (totalGeneratorAfterFailure == totalGenerator)
//                    analysis[r][S] += 1;
////              when there is no generator after fault
//                else if (totalGeneratorAfterFailure == 0)
//                    analysis[r][F] += 1;
////              todo when there is failure in some generator
//                else if (totalGeneratorAfterFailure < totalGenerator){
//
//                }
//            }
//        }

//        todo when reconfiguration scenario is defined
        if (reconfiguredPower != 0) {
//            no demand is failure

            if (survivedLoadPower == 0
                    || survivedGeneratorPower == 0
                    || totalGeneratorAfterFailure == 0) {
                analysis[r][F] += 1;
//                System.out.print("[");
//                for (int i = 0; i < combination.length; i++) {
//                    System.out.print(combination[i]+", ");
//                }
//                System.out.print("] = ");
                System.out.println("F");
            }
//            over or equal production is survived or
//            production is greater that reconfiguration
            else if (survivedGeneratorPower >= survivedLoadPower
                    || survivedGeneratorPower >= reconfiguredPower) {
                analysis[r][S] += 1;
//                System.out.print("[");
//                for (int i = 0; i < combination.length; i++) {
//                    System.out.print(combination[i]+", ");
//                }
//                System.out.print("] = ");
                System.out.println("S");
            }
            else {
//                THIS WILL HAPPEN WHEN
//                TOTAL GENERATED POWER AFTER FAILURE IS LESS THAN THE RECONFIGURATION POWER
//                AND TOTAL GENERATED POWER IS GREATER THAN ZERO (0)
                analysis[r][R] += 1;
//                System.out.print("[");
//                for (int i = 0; i < combination.length; i++) {
//                    System.out.print(combination[i]+", ");
//                }
//                System.out.print("] = ");
                System.out.println("R");
            }
        }
        //when no criteria
        else {
//            if (totalGeneratorAfterFailure == totalGenerator) {
//                analysis[r][S] += 1;
//                System.out.print("[");
//                for (int i = 0; i < combination.length; i++) {
//                    System.out.print(combination[i]+", ");
//                }
//                System.out.println("] is survived");
//            }

                //failure scenario
            if (totalGeneratorAfterFailure == 0) {
                analysis[r][F] += 1;

//                System.out.print("[");
//                for (int i = 0; i < combination.length; i++) {
//                    System.out.print(combination[i]+", ");
//                }
//                System.out.println("] is failure");
                System.out.println("F");
            }

            else {
                analysis[r][S] += 1;

//                System.out.print("[");
//                for (int i = 0; i < combination.length; i++) {
//                    System.out.print(combination[i]+", ");
//                }
//                System.out.println("] is survived");
                System.out.println("S");
            }
//            else {
//                analysis[r][R] += 1;
//                System.out.print("[");
//                for (int i = 0; i < combination.length; i++) {
//                    System.out.print(combination[i]+",");
//                }
//                System.out.println("] is reconfig");
//            }
        }
    }

    /* A utility function to print solution */
    public void printMatrix(double[][] matrix, String caption) {
        System.out.println(caption);
        for (int i = 0; i < matrix.length; i++)
        {
            for (int j = 0; j < matrix[0].length; j++)
                System.out.print(matrix[i][j]+" ");
            System.out.println();
        }
    }

    private void setZero(int value) {
        int nm = getNm();
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
