package bloodTool;

import javafx.scene.image.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;

//With help from: https://www.geeksforgeeks.org/find-the-number-of-islands-set-2-using-disjoint-set/
//FindCells adapted from there (adapted to work with this project, better commented, input generified...)
//Additional help from: https://thejeshvenkata.github.io/HTMLPages/find-the-number-of-islands-set-2-using-disjoint-set.html
//Website used to help understand how the implementation should work

public class Utils {

    //Tri-Colour posterize the image using Hue ranges rather than RGB values
    public static Image TriBloodSample(Image img, double whiteClipping) {
        //set up image
        PixelReader pr = img.getPixelReader();
        int w = (int) img.getWidth();
        int h = (int) img.getHeight();
        WritableImage wImg = new WritableImage(w, h);
        PixelWriter pw = wImg.getPixelWriter();

        //increment over every pixel and posterize accordingly
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                Color c = pr.getColor(x, y);
                //in HSV the only difference between white and red is the Saturation value
                if (c.getSaturation() <= whiteClipping) {
                    pw.setColor(x, y, Color.WHITE);
                } else if (c.getHue() >= 240.00 && c.getHue() <= 300.00) {
                    pw.setColor(x, y, Color.PURPLE);
                } else if ((c.getHue() >= 0.00 && c.getHue() <= 20.00)
                        || (c.getHue() >= 300.00 && c.getHue() <= 360.00)) {
                    pw.setColor(x, y, Color.RED);
                }
            }
        }
        return wImg;
    }

    //Creates a 2D array of 0's 1's and 2's based on the colour
    //of each pixel in the provided image
    public static int[][] CategorizePixels(Image img) {
        //setup
        PixelReader pr = img.getPixelReader();
        int w = (int) img.getWidth();
        int h = (int) img.getHeight();
        int white = 0;
        int red = 0;
        int purple = 0;
        int[][] ar = new int[w][h];
        //increment over each pixel of the image
        //and replicate it with a 2D int array,
        //where each red pixel is 1, each purple
        //pixel is 2, and each white pixel is 0.
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                Color c = pr.getColor(x, y);
                if (c.getSaturation() == 0) {
                    //White
                    ar[x][y] = 0;
                    white++;
                } else if (c.getHue() == Color.RED.getHue()) {
                    //Red
                    ar[x][y] = 1;
                    red++;
                } else {
                    //Purple (or anything else)
                    ar[x][y] = 2;
                    purple++;
                }
                //System.out.print(ar[x][y]);
            }
            //System.out.println();
        }
        System.out.println("White: " + white + " Red: " + red + " Purple: " + purple);
        return (ar);
    }

    //red cell == 1, white cell == 2, empty == 0
    //with help from https://www.geeksforgeeks.org/find-the-number-of-islands-set-2-using-disjoint-set/
    public static int FindCells(int[][] ar, int cellType, int noiseMin) {
        //name array lengths as variables
        int xLength = ar.length;
        int yLength = ar[0].length;
        //create disjoint set data structure
        DisjointSet dJS = new DisjointSet(ar.length * ar[0].length);
        //Increment over the array...
        for (int x = 0; x < xLength; x++) {
            for (int y = 0; y < yLength; y++) {
                // If current cell is the desired number, we
                //union with it's surrounding neighbours
                if (ar[x][y] == cellType) {
                    NeighbourUnion(ar, dJS, xLength, yLength, cellType, x, y);
                }
            }
        }
        //Initialise unique set counters for cells
        int[] setOccurrenceCount = new int[xLength * yLength];
        int numOfCells = 0;
        //increment over the 2D array post-unionizing
        for (int x = 0; x < xLength; x++) {
            for (int y = 0; y < yLength; y++) {
                //if node is what we're looking for,
                //Find the tree it's part of
                if (ar[x][y] == cellType) {
                    int currentSet = dJS.Find(x * yLength + y);
                    int currentSetSize = dJS.GetSetSize(x * yLength + y);
                    //If this set hasn't shown up yet already
                    //it's considered unique and can contribute
                    //To the total number of sets
                    //Also if the set size is larger than the minimum
                    //passed in size
                    if (setOccurrenceCount[currentSet] == 0) {
                        if (currentSetSize >= noiseMin) {
                            numOfCells++;
                        }
                    }
                    //increment the set occurrence variable
                    setOccurrenceCount[currentSet]++;
                }
            }
        }
        /*
        int min = GetAverageOfArray(dJS.setSizes) / 2;
        int previousSet = 0;
        for (int x = 0; x < xLength; x++) {
            for (int y = 0; y < yLength; y++) {
                if (ar[x][y] == cellType) {
                    int currentSet = dJS.Find(x * yLength + y);
                    int currentSetSize = dJS.GetSetSize(currentSet);
                    //if it's a different set
                    if (currentSet != previousSet) {
                        //if the current unique set size is less than minimum
                        if (currentSetSize > min) {
                            numOfCells++;
                        }
                    }
                    previousSet = currentSet;
                }
            }
        }
         */
        //Assign static variables
        Controller.cellOccurences = setOccurrenceCount;
        Controller.mainDJS = dJS;
        Controller.joinedCells = ar;
        return numOfCells;
    }

    //Check all 8 neighbours for similarity, and union if
    //they are the same AND not outside the provided matrix
    //"is node within bounds?" && "Is node the type we want?"
    private static void NeighbourUnion(int[][] ar, DisjointSet dJS, int xLength, int yLength, int cellType, int x, int y) {
        //right
        if (x + 1 < xLength && ar[x + 1][y] == cellType) {
            dJS.Union(x * (yLength) + y, (x + 1) * (yLength) + y);
        }
        //left
        if (x - 1 >= 0 && ar[x - 1][y] == cellType) {
            dJS.Union(x * (yLength) + y, (x - 1) * (yLength) + y);
        }
        //below
        if (y + 1 < yLength && ar[x][y + 1] == cellType) {
            dJS.Union(x * (yLength) + y, (x) * (yLength) + y + 1);
        }
        //above
        if (y - 1 >= 0 && ar[x][y - 1] == cellType) {
            dJS.Union(x * (yLength) + y, (x) * (yLength) + y - 1);
        }
        //right and below
        if (x + 1 < xLength && y + 1 < yLength && ar[x + 1][y + 1] == cellType) {
            dJS.Union(x * (yLength) + y, (x + 1) * (yLength) + y + 1);
        }
        //right and above
        if (x + 1 < xLength && y - 1 >= 0 && ar[x + 1][y - 1] == cellType) {
            dJS.Union(x * yLength + y, (x + 1) * (yLength) + y - 1);
        }
        //left and below
        if (x - 1 >= 0 && y + 1 < yLength && ar[x - 1][y + 1] == cellType) {
            dJS.Union(x * yLength + y, (x - 1) * yLength + y + 1);
        }
        //left and above
        if (x - 1 >= 0 && y - 1 >= 0 && ar[x - 1][y - 1] == cellType) {
            dJS.Union(x * yLength + y, (x - 1) * yLength + y - 1);
        }
    }

    //Unfinished method for drawing rectangles over the image
    //Method does draw rectangles given the correct roots, but does not resize rectangles yet
    //Main code logic from peter by email, adapted to work with 2D arrays
    public static void MakeRectangles(DisjointSet dJS, ImageView imgV, int cellType) {
        //create local instance of joinedCells array
        int[][] cellMatrix = Controller.joinedCells;
        //Create array of rectangles
        Rectangle[][] r = new Rectangle[cellMatrix.length][cellMatrix[0].length];
        //increment over array
        for (int x = 0; x < cellMatrix.length; x++) {
            for (int y = 0; y < cellMatrix[0].length; y++) {
                //if current node matches the type we are looking for,
                //find the tree it's a part of and draw rectangle over the tree
                if (cellMatrix[x][y] == cellType) {
                    if (dJS.Find(x * cellMatrix[0].length + y) == 0) { //placeholder id
                        if (r[x][y] == null) {
                            r[x][y] = new Rectangle(x, y, 1, 1);
                        } else {
                            if (x > (r[x][y].getX() + r[x][y].getWidth())) {
                                r[x][y].setWidth(x - r[x][y].getX());
                            }
                            if (x < r[x][y].getX()) {
                                r[x][y].setWidth(r[x][y].getX() + r[x][y].getWidth() - x);
                                r[x][y].setX(x);
                            }
                            if (y > r[x][y].getY() + r[x][y].getHeight()) {
                                r[x][y].setHeight(y - r[x][y].getY());
                            }
                        }
                    }
                    if (r[x][y] != null) {
                        System.out.println("I made a rectangle at x: " + x + " y: " + y);
                        r[x][y].setFill(Color.TRANSPARENT);
                        r[x][y].setStroke(Color.BLUE);
                        r[x][y].setTranslateX(imgV.getLayoutX());
                        r[x][y].setTranslateY(imgV.getLayoutY());
                        ((Pane) imgV.getParent()).getChildren().add(r[x][y]);
                    }
                }
            }
        }
    }

    //Adds all numbers in an array and then divides by length
    public static int GetAverageOfArray(int[] ar, int noiseFilter) {
        int total = 0;
        int count = 0;
        for (int i = 0; i < ar.length; i++) {
            if (ar[i] > noiseFilter) {
                total += ar[i];
                count++;
            }
        }
        System.out.println("Total: " + total);
        System.out.println("Count: " + count);
        return total / count;
    }

    //Flattens a 2D array to 1D by writing everything in it to an arraylist,
    //and then writing the array list to a new 1D array
    public static int[] Convert2DArrayTo1D(int[][] ar2D) {
        int[] ar1D = new int[ar2D.length * ar2D[0].length];
        ArrayList<Integer> temp = new ArrayList();
        for (int x = 0; x < ar2D.length; x++) {
            for (int y = 0; y < ar2D[0].length; y++) {
                temp.add(ar2D[x][y]);
            }
        }
        for (int i = 0; i < ar1D.length; i++) {
            ar1D[i] = temp.get(i);
        }
        return ar1D;
    }
}