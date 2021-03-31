// Java program to use JFileChooser to restrict
// the type of files shown to the user
import java.awt.*;
import java.io.*;
import javax.swing.*;
import java.awt.event.*;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.filechooser.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.plaf.ColorUIResource;
import javax.swing.text.NumberFormatter;

class NetworkSurvivabilityAnalysis extends JFrame implements ActionListener {
    // Jlabel to show the files user selects
    static JLabel label;
    String openFileName, saveFileName;
    private final int OK = 0;
    private final int INFO = 1;
    private final int CANCEL = 2;
    private final int SAVE = 3;
    private final int REFRESH = 0;
    private final int height = 400;
    private final int width = 400;
    private JButton btnOpen, btnSave, btnCriteria;
    private JFrame frame;
    private JTextArea textArea, textArea2, textArea3;
    private JPanel panelTop, panelBottom;
    private final String AppName = "Network Survivability Analysis", sOPEN = "Open",
            sCLOSE = "Close", sSAVE = "Run the Analysis", sCriteria = "Criteria to Survive";
    private final String NoFileSelected = "no file selected", NoInputs = "No Inputs";
    private final String isThereAnyPartial = "There are two default criteria:\n" +
            "Full Survival: when demand of survived loads is fully satisfied\n" +
            "Failure: when demand of survived loads is not satisfied\n" +
            "Do you want to add the criterion for partial survival?\n" +
            "A system survives partially if generated power is above or equal to the threshold";
    private Analysis2 analysis2;
    private int[] powerInput, arr;
    private int inputSize = 0, n, lowerLoad = Integer.MAX_VALUE, higherLoad = Integer.MIN_VALUE;
    private List<List<Integer>> clusterList;
//    private ArrayList<Integer> powerInputsList;
    // a default constructor
private NetworkSurvivabilityAnalysis() {
        setOpenFileName("");
        setSaveFileName("");
        analysis2 = new Analysis2();
//        powerInputsList = new ArrayList<>();
    }

    private String getOpenFileName() {
        return openFileName;
    }

    private void setOpenFileName(String openFileName) {
        this.openFileName = openFileName;
    }

    private String getSaveFileName() {
        return saveFileName;
    }

    private void setSaveFileName(String saveFileName) {
        this.saveFileName = saveFileName;
    }

    public static void main(String args[]) {
        //initialize filenames
        NetworkSurvivabilityAnalysis NetworkSurvivabilityAnalysis = new NetworkSurvivabilityAnalysis();
        //initialize component
        NetworkSurvivabilityAnalysis.initComponent(NetworkSurvivabilityAnalysis);
    }

    private void initComponent(NetworkSurvivabilityAnalysis NetworkSurvivabilityAnalysis) {
//        UIManager.put("InternalFrame.activeTitleBackground", new javax.swing.plaf.ColorUIResource(Color.BLUE));
//        UIManager.put("InternalFrame.activeTitleForeground", new javax.swing.plaf.ColorUIResource(Color.BLUE));
        UIDefaults uiDefaults = UIManager.getDefaults();
        uiDefaults.put("activeCaption", new javax.swing.plaf.ColorUIResource(Color.WHITE));
        UIManager.put("InternalFrame.titleFont", new Font("Dialog", Font.PLAIN, 17));
        UIManager.put("Button.font", new Font("Dialog", Font.PLAIN, 15));
        UIManager.put("TextArea.font", new Font("Dialog", Font.BOLD, 15));
        UIManager.put("Label.font", new Font("Dialog", Font.BOLD, 15));
        UIManager.put("OptionPane.background", new javax.swing.plaf.ColorUIResource(Color.WHITE));
        UIManager.put("OptionPane.font", new Font("Dialog", Font.PLAIN, 17));
//        uiDefaults.put("activeCaptionText", new javax.swing.plaf.ColorUIResource(Color.white));
        JFrame.setDefaultLookAndFeelDecorated(true);
    // frame to contains GUI elements
        frame = new JFrame(AppName);
        frame.setBackground(Color.RED);

        // set the size of the frame
        frame.setSize(width, height);

        // set the frame's visibility
        frame.setVisible(true);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // button to open save dialog
        btnSave = new JButton(sSAVE);
        // button to open open dialog
        btnOpen = new JButton(sOPEN);
        // button to open criteria dialog
        btnCriteria = new JButton(sCriteria);
        //text area
        textArea = new JTextArea(NoInputs);
        textArea2 = new JTextArea(NoInputs);
        textArea3 = new JTextArea(NoInputs);
        // label
        label = new JLabel(NoFileSelected);

        // add action listener to the button to capture user
        // response on buttons
        btnSave.addActionListener(NetworkSurvivabilityAnalysis);
        btnOpen.addActionListener(NetworkSurvivabilityAnalysis);
        btnCriteria.addActionListener(NetworkSurvivabilityAnalysis);

        // make a panelTop to add the buttons and labels
        panelTop = new JPanel();
        panelBottom = new JPanel();

        // add buttons to the frame
        panelTop.add(btnSave);
        panelTop.add(btnOpen);
        panelTop.add(btnCriteria);
        panelTop.add(label, BorderLayout.SOUTH);
        panelBottom.add(textArea);
        panelBottom.add(textArea2);
        panelBottom.add(textArea3);

        frame.add(panelTop);
        frame.add(panelBottom, BorderLayout.PAGE_END);
        frame.setVisible(true);
//        frame.show();

//        initially hide the criteria and save button
//        btnCriteria.setEnabled(false);
//        btnSave.setEnabled(false);
        btnCriteria.setVisible(false);
        btnSave.setVisible(false);

//        initiate cluster list
    }

    public void actionPerformed(ActionEvent evt)
    {
        // if the user presses the save button show the save dialog
        String com = evt.getActionCommand();
        String openingLocation = System.getProperty("user.dir");
        Component component = (Component) evt.getSource();

        if (com.equals(sSAVE)) {
            textArea3.setText("");
//            initiate cluster load list
            clusterList = new ArrayList<List<Integer>>();
//            ask user for multi-load links
            int check = 1;
            int result = JOptionPane.showConfirmDialog(frame,
                    "Are there Cluster Loads?",
                    "Cluster Load",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
            if (result == JOptionPane.YES_OPTION) {
                while (true) {
                    if (check == 2) {
                        result = JOptionPane.showConfirmDialog(frame,
                                "More Cluster Loads?",
                                "Cluster Load",
                                JOptionPane.YES_NO_OPTION,
                                JOptionPane.QUESTION_MESSAGE);
                    }

                    if (result == JOptionPane.YES_OPTION) {
                        String input = JOptionPane.showInputDialog(frame,
                                "List Links in a cluster (Example: 1,2,...)");
                        if (input != null) {
                            check = 2;
                            int i = setCluster(input);
                            label.setForeground(Color.BLACK);
                            if (i == 1)
                                label.setText("Cluster Loads selected");

                            else if (i == 0) {
                                label.setText("Cluster inputs mismatch.\n " +
                                        "Please provide right Load number.");
                                label.setForeground(Color.RED);
                            } else if (i == -1) {
                                label.setText("Cluster inputs mismatch.\n " +
                                        "Same link cannot be included into different clusters");
                                label.setForeground(Color.RED);
                            } else if (i == -2) {
                                label.setText("Inputs only contains numbers (1,2,..0) " +
                                        "and separated by comma \",\"");
                                label.setForeground(Color.RED);
                            }

                        } else
                            break;

                    } else if (result == JOptionPane.NO_OPTION) {
                        label.setText("You selected: No");
                        label.setForeground(Color.BLACK);
                        break;
                    } else {
                        label.setText("None selected");
                        label.setForeground(Color.BLACK);
                        break;
                    }
                }
            }
//            check the cluster loads
            if (!clusterList.isEmpty()){
                for (int i = 0; i < clusterList.size(); i++){
                    for (int j = 0; j < clusterList.get(i).size(); j++){
                        int num = i+1;
                        textArea3.append("Cluster : "+num+" Load-VL : "+clusterList.get(i).get(j)+"\n");
                    }
                }
                run_and_save(openingLocation);
            }
            else {
                label.setText("No Cluster Load");
                label.setForeground(Color.BLACK);
                run_and_save(openingLocation);
            }
        }

        // open option
        // if the user presses the open dialog show the open dialog
        else if (com.equals(sOPEN)){
            // create an object of JFileChooser class
//            JFileChooser j = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
            JFileChooser j = new JFileChooser(new File(openingLocation));
            // restrict the user to select files of all types
            j.setAcceptAllFileFilterUsed(false);

            // set a title for the dialog
            j.setDialogTitle("Select a .csv file");

            // only allow files of .txt extension
            FileFilter restrict = new FileNameExtensionFilter("Only .csv files", "csv");

            j.addChoosableFileFilter(restrict);

            // invoke the showsOpenDialog function to show the save dialog
            int r = j.showOpenDialog(null);

            // if the user selects a file
            if (r == JFileChooser.APPROVE_OPTION) {
                setOpenFileName(j.getSelectedFile().getAbsolutePath());

                //  call analysis2
                //  setupInputsfromExcel()
                //  getGVHM();
                //  getPowerInput()
                //  show the result in Text Area
                //  result: # of generator with power
                //          # of load with power
                //  if mismatch/ discrepancy then ask user to open a correct file

                arr = analysis2.setupInputsfromExcel(getOpenFileName());
                n = arr.length;
                int sum = 0;
                int[] pi = analysis2.getPowerInput();
                inputSize = pi.length+1;
                powerInput = new int[inputSize];
                for (int i = 0; i < pi.length; i++){
                    powerInput[i] = pi[i];
                    sum += pi[i];
                }
                textArea.setText("");
                textArea2.setText("");
                for (int i = 0; i < powerInput.length-1; i++) {
                    int i1 = i+1;
                    if (powerInput[i] > 0)
                        textArea.append("Generator "+i1+": "+powerInput[i]+"kW\n");
                    else {
                        if (lowerLoad > i1)
                            lowerLoad = i1;
                        if (higherLoad < i1)
                            higherLoad = i1;
                        int loadPower = powerInput[i]*(-1);
                        textArea2.append("Load-VL "+i1+": "+loadPower+"kW\n");
                    }
                }
                if (sum >= 0){
                    // set the label to the path of the selected file
//                    label.setText(j.getSelectedFile().getAbsolutePath());
                    //make visible the criteria, and save button
                    btnCriteria.setVisible(true);
                    btnSave.setVisible(true);
                    label.setText("Power inputs are OK.");
                    label.setForeground(Color.BLACK);
                }

                else {
                    label.setText("Power input mismatch. Total generated power " +
                            "is not equal to total absorbed power");
                    label.setForeground(Color.RED);
                    btnCriteria.setVisible(true);
                    btnSave.setVisible(true);
                }
            }
            // if the user cancelled the operation
            else {
                label.setText("the user cancelled the operation");
                label.setForeground(Color.BLACK);
            }
        }

        else if (com.equals(sCriteria)){
            int result = JOptionPane.showConfirmDialog(frame,
                    isThereAnyPartial,
                    "Partial Survivability",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
            if(result == JOptionPane.YES_OPTION){
                String input = JOptionPane.showInputDialog(frame,
                        "Enter Partial Survived Threshold in kW:");
                powerInput[inputSize-1] = Integer.parseInt(input);
                System.out.println("input is: "+input);
                label.setText("You selected: Yes and input is: "+input+"kW");
                label.setForeground(Color.BLACK);
            }else if (result == JOptionPane.NO_OPTION){
                label.setText("You selected: No");
                label.setForeground(Color.BLACK);
            }else {
                label.setText("None selected");
                label.setForeground(Color.BLACK);
            }
        }
    }

    private void run_and_save(String openingLocation) {
        int result = JOptionPane.showConfirmDialog(frame,
                "Choose the directory and give the file" +
                        "name for the Analysis result",
                "Save Analysis",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.INFORMATION_MESSAGE);
//            ask user for saving
        if (result == JOptionPane.OK_OPTION) {
            // create an object of JFileChooser class
            JFileChooser j = new JFileChooser(new File(openingLocation));

            // restrict the user to select files of all types
            j.setAcceptAllFileFilterUsed(false);

            // set a title for the dialog
            j.setDialogTitle("Give a name for output .xlsx file");

            // only allow files of .xlsx extension
            FileNameExtensionFilter restrict = new FileNameExtensionFilter("Only .xlsx files", "xlsx");
            j.addChoosableFileFilter(restrict);

            // invoke the showsSaveDialog function to show the save dialog
            int r = j.showSaveDialog(null);

            // if the user selects a file
            if (r == JFileChooser.APPROVE_OPTION) {
                setSaveFileName(j.getSelectedFile().getAbsolutePath());
                // set the label to the path of the selected file
                label.setText(saveFileName);
                label.setForeground(Color.BLACK);
                if (!getOpenFileName().equals("") && !getSaveFileName().equals("")){
                    //todo
                    // initAnalysis
                    // setNgs
                    // setAnalysis
                    // print analysis
                    // save Analysis
                    analysis2.initAnalysis(n+1);
                    analysis2.setNgs(analysis2.getNg());
                    analysis2.setAnalysis(arr, n, powerInput, clusterList);
                    analysis2.printMatrix(analysis2.getAnalysis(),
                            "Survivability Analysis");
                    analysis2.saveFile(analysis2.getAnalysis(),
                            "Analysis",
                            saveFileName);


//                    Analysis2 analysis2 = new Analysis2(openFileName, saveFileName);
                }
                else if(getOpenFileName().equals("")) {
                    label.setText("Choose a file to analyze");
                    label.setForeground(Color.RED);
                }
                else {
                    label.setText("Choose a file to save the analysis");
                    label.setForeground(Color.RED);
                }
            }
            // if the user cancelled the operation
            else {
                label.setText("the user cancelled the operation");
                label.setForeground(Color.BLACK);
            }
        }
//            ask user to run the another analysis or not
        result = JOptionPane.showConfirmDialog(frame,
                "Do you want to run another analysis?\n" +
                        "Click \"Open\" for new Topology or " +
                        "Click \"Run the Analysis\" to run analysis on same topology",
                "Run Another Analysis",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.INFORMATION_MESSAGE);

        if (result == JOptionPane.CANCEL_OPTION)
            frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
        else {
//            textArea.setText(NoInputs);
//            textArea2.setText(NoInputs);
            textArea3.setText(NoInputs);
//            btnCriteria.setVisible(false);
//            btnSave.setVisible(false);
        }
    }

    private int setCluster(String input) {
        System.out.println("cluster called");
        String[] convertedLoadArray = input.split(",");
        List<Integer> convertedLoadList= new ArrayList<Integer>();
        for (String number : convertedLoadArray) {
            try {
                int i = Integer.parseInt(number.trim());
                for (List<Integer> list : clusterList)
                    if (list.contains(i))
                        return -1;

                if (i >= lowerLoad && i <= higherLoad)
                    convertedLoadList.add(i);
                else
                    return 0;
            }
            catch (NumberFormatException e){
                return -2;
            }

        }
        clusterList.add(convertedLoadList);
        return 1;
    }

    private void AddPowerInputs(ArrayList<Integer> list) {
        int[] counts = new int[3];
        counts[0] = 2;
        counts[1] = 2;
        int countGen = counts[0];
        int countLd = counts[1];
        int totalCounts = counts[0] + counts[1];

        NumberFormat format = NumberFormat.getInstance();
        NumberFormatter formatter = new NumberFormatter(format);
        formatter.setValueClass(Integer.class);
        formatter.setMinimum(1);
        formatter.setMaximum(Integer.MAX_VALUE);
        formatter.setAllowsInvalid(false);
        // If you want the value to be committed on each keystroke instead of focus lost
        formatter.setCommitsOnValidEdit(true);

        ArrayList<JFormattedTextField> textFields = new ArrayList<JFormattedTextField>();
        for (int i = 0; i < totalCounts; i++) {
            if (list != null && list.size() == totalCounts) {
                textFields.add(new JFormattedTextField(formatter));
                textFields.get(i).setValue(list.get(i));
            } else {
                if (i < countGen) {
//                    generator
                    textFields.add(new JFormattedTextField(formatter));
                    textFields.get(i).setValue(1);
                } else if (i < countGen + countLd) {
//                    load
                    textFields.add(new JFormattedTextField(formatter));
                    textFields.get(i).setValue(1);
                }
            }
        }

        Object[] messages = new Object[totalCounts];
        for (int i = 0; i < totalCounts; i++) {
            int j = i + 1;
            if (i < countGen) {
                messages[i] = new Object[]{"Power through Generator Link " + j + " (kW):", textFields.get(i)};
                textFields.get(i).setToolTipText("Please put number greater than 1(Default is 1kW)");
            } else if (i < countGen + countLd) {
                messages[i] = new Object[]{"Power through Load Link " + j + " (kW):", textFields.get(i)};
                textFields.get(i).setToolTipText("Please put number greater than 1(Default is 1kW)"
                        + " and make sure the total of generator's capacity is Load's capacity");
            }
        }
        String[] options = new String[]{"OK",
                "INFO",
                "Cancel"};

        JOptionPane jOptionPane = new JOptionPane();
        jOptionPane.setIcon(null);
        jOptionPane.setOptions(new String[]{"REFRESH"});
        jOptionPane.setOptionType(JOptionPane.DEFAULT_OPTION);
        jOptionPane.setMessage(messages);
        jOptionPane.setMessageType(JOptionPane.PLAIN_MESSAGE);

//        int option = JOptionPane.showOptionDialog(null, messages, "Enter Input for system elements",
//                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

        JScrollPane scrollPane = new JScrollPane(jOptionPane);
        scrollPane.setPreferredSize(new Dimension(500, 500));

        int option = JOptionPane.showOptionDialog(null, scrollPane,
                "Enter Inputs for Power elements",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

        ArrayList<Integer> powers = new ArrayList<Integer>();
        Object obj = jOptionPane.getValue();
//        if (Objects.equals((String) obj, "REFRESH")){
//            AddPowerInputs(graph, powers);
//        }

        if (option == OK) {
//            get the values from JTextField
            int totalGeneratorCapacity = 0;
            int totalLoadCapacity = 0;
            int totalLinkCapacity = 0;
            for (int i = 0; i < totalCounts; i++) {
                String s = textFields.get(i).getText().trim();
                if (s.equals(""))
                    s = "1";
                int value = Integer.parseInt(s);
                if (i < countGen) {
                    totalGeneratorCapacity += value;
                } else if (i < countGen + countLd) {
                    totalLoadCapacity += value;
                }
                powers.add(value);
            }
//            errors
//            if (totalGeneratorCapacity != totalLoadCapacity) {
//                JOptionPane.showMessageDialog(null,
//                        "Generators' power input has to be equal to Loads' power input.",
//                        "Power Input error",
//                        JOptionPane.ERROR_MESSAGE);
//                AddPowerInputs(graph, powers);
//            }
            System.out.println(powers);
        }
//        info option
        else if (option == INFO) {
            for (int i = 0; i < totalCounts; i++) {
                String s = textFields.get(i).getText().trim();
                if (s.equals(""))
                    s = "1";
                int value = Integer.parseInt(s);
                powers.add(value);
            }
            int n = JOptionPane.showConfirmDialog(null,
                    "Initially all Power Inputs are 1kw. " +
                            "If necessary a user can change the values to integer values greater than 1\n",
                    "Power Input's Information", JOptionPane.OK_CANCEL_OPTION);
            if (n == JOptionPane.OK_OPTION);
//                AddPowerInputs(graph, powers);
        }
//        save option : save matrix with powers
//        else if (option == SAVE) {
//            ExportAdjacencyMatrix(graph, "Adjacency_Matrix_with_weight.csv",
//                    true, powers, counts[2]);
//
//            new EditorActions.ExportAction(false, "matrix", true, powers);
//        }

    }
}
