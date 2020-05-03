package bloodTool;

import javafx.fxml.FXML;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class Controller {

    //main disjoint set data structure
    public static DisjointSet mainDJS;

    //main image view
    @FXML
    public ImageView iv;
    //slider which allows the user to specify
    //the threshold with which the program will
    //filter white pixels
    @FXML
    public Slider whiteThresh;

    //The image that will be acted upon
    public Image img;

    //text that displays number of red cells in image
    @FXML
    public Text redCellText;

    @FXML
    public Text AvgText;

    static public int redCells = 0;

    //text that displays number of white cells in image
    @FXML
    public Text whiteCellText;

    static public int whiteCells = 0;

    //Used in rectangle creation method
    static public int[][] joinedCells;

    //Array that keeps track of amount of times
    //A node's root appears during a scan
    static public int[] cellOccurences;

    //Stage with which the file explorer opens
    Stage viewerStage;

    //Text box for the noise filter
    @FXML
    public TextField filterBox;


    @FXML
    public void OpenImage() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Open an image...");
        //need to prefix a file with "file:///" as the protocol or else java thinks
        //"c" is the protocol if using an absolute path.
        try {
            String imgPath = "file:///" + fc.showOpenDialog(viewerStage).getAbsolutePath();
            //ensuring an image path was obtained
            AddImageToDisplay(imgPath);
        } catch (Exception e) {
            System.out.println("Uh oh! " + e);
            System.out.println("User did not select an image!");
        }
    }

    //run a series of different methods to process the image
    //and find blood cells
    @FXML
    public void ProcessImage() {
        int noiseFilter = 0;
        try {
            if (filterBox.getText() != null) {
                noiseFilter = Integer.parseInt(filterBox.getText());
            }
            iv.setImage(Utils.TriBloodSample(img, whiteThresh.getValue()));
            //red cell == 1, white cell == 2, empty == 0
            redCells = Utils.FindCells(Utils.CategorizePixels(iv.getImage()), 1, noiseFilter);
            whiteCells = Utils.FindCells(Utils.CategorizePixels(iv.getImage()), 2, noiseFilter);
            redCellText.setText("Red Cells: " + redCells);
            whiteCellText.setText("White Cells: " + whiteCells);
            try {
                AvgText.setText("Average Cell Size: " + Utils.GetAverageOfArray(cellOccurences, noiseFilter));
            } catch (ArithmeticException aE) {
                System.out.println(aE);
            }
            //Utils.MakeRectangles(mainDJS, iv, 1);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    //puts the selected image into the imageview
    public void AddImageToDisplay(String path) {
        img = new Image(path);
        iv.setPreserveRatio(true);
        iv.setImage(img);
    }
}
