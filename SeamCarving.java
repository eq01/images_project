import java.util.Iterator;
import tester.*;
import javalib.impworld.*;
import java.awt.Color;
import java.util.ArrayList;
import javalib.impworld.*;
import javalib.worldimages.*; 

// runs seam carving on an image
class SeamCarving extends World {
  // get image from file
  FromFileImage fileImage;

  // the width of the image
  int imageWidth;
  // the height of the image
  int imageHeight;

  // the grid corresponding with the carved image
  Grid imageGrid;

  // the constructor
  SeamCarving(FromFileImage image) {
    this.fileImage = image;
    this.imageWidth = (int) fileImage.getWidth();
    this.imageHeight = (int) fileImage.getHeight();
    // turn image into a grid of pixels
    imageGrid = new Grid();

    // for every column in the image, set the color of file image pixel into
    // a new Pixel in the grid
    for (int row = 0; row < imageHeight; row += 1) {
      // for every pixel in this file image row, make a new pixel of that color
      // and add it to the grid
      for (int rowIndex = 0; rowIndex < imageWidth; rowIndex += 1) {
        // add pixel to grid row
        this.imageGrid.addPixel(row, fileImage.getColorAt(row, rowIndex));
      }
    }
    // fix links
    this.imageGrid.fixLinks();
  }

  // draws the image
  public WorldScene makeScene() {
    WorldScene s = new WorldScene(this.imageWidth, this.imageHeight);
    s.placeImageXY(this.imageGrid.draw(), imageWidth / 2, imageHeight / 2);
    return s;
  }

  // on every tick, computes minimum seam and removes it from the image, 
  // resulting in an empty image
  public void onTick() {
    this.imageGrid.removeMinimumSeam();
    this.makeScene();
    // save image as a file
    // saveImage(String filename)
  }
}

class ExamplesSeamCarving {
  // the width of the canvas
  static final int WIDTH = 1000;
  // the height of the canvas
  static final int HEIGHT = 800;

  FromFileImage fileImage = new FromFileImage("image/balloons.jpg");
  SeamCarving sc = new SeamCarving(fileImage);

  void testBigBang(Tester t) {
    double tickRate = 1;
    sc.bigBang(WIDTH, HEIGHT, tickRate);
  }
}

// represents a pixel in a grid
interface IPixel {
  // inserts a new node with the given value after this node
  public void insertRight(Color color);

  // inserts a new node with the given value after this node
  public void insertBelow(Color color);

  // removes this node from the list and returns this value
  public void remove();

  // given this node, advance one step right, returns the next node 
  // in the list, if there is none, return itself
  IPixel advanceRight();

  // given this node, advance one step down, returns the next node 
  // in the list, if there is none, return itself 
  IPixel advanceDown();

  // calculates the brightness of this pixel
  double brightness();
}

// the implementations of a pixel in a grid
abstract class APixel implements IPixel {
  // the color of this pixel
  Color color;
  // the pixel above this one
  APixel up;
  // the pixel below this one
  APixel down;
  // the pixel to the left of this one
  APixel left;
  // the pixel to the right of this one
  APixel right;
  // preserve structural invariant
  // the pixel to the topleft of this one
  APixel topLeft;
  // the pixel to the topright of this one
  APixel topRight;
  // the pixel downleft of this one
  APixel downLeft;
  // the pixel downright of this one
  APixel downRight;

  // inserts a new pixel to the right of this pixel
  public void insertRight(Color color) {
    // make new node
    Pixel newPixel = new Pixel(color, this.up, this.down, this, this.right);
    // update this node's links
    this.right = newPixel;
    // also the diagonals!!!!!!!!
  }

  // inserts a new pixel below this pixel
  public void insertBelow(Color color) {
    // make new node
    Pixel newPixel = new Pixel(color, this, this.down, this, this.right);
    // update this node's links
    this.right = newPixel;
    // also the diagonals!!!!!!!!
  }

  // given this node, advance one step right, returns the next node 
  // in the list, if there is none, return itself
  public APixel advanceRight() {
    return this.right;
  }

  // given this node, advance one step down, returns the next node 
  // in the list, if there is none, return itself 
  public APixel advanceDown() {
    return this.down;
  }

  // calculates the brightness of this pixel
  public double brightness() {
    int blue = this.color.getBlue();
    int red = this.color.getRed();
    int green = this.color.getGreen();
    
    double average = (blue + red + green) / 255.0;
    
    if (average > 1.0 || average < 0.0) {
      throw new IllegalArgumentException("average is not between 0.0 and 1.0!");
    }
    else {
      return average;
    }
  }

  // computes the brightness of the pixel on top of this one
  public double topPixelBrightness() {
    return this.up.brightness();
  }

  // computes the brightness of the pixel to the bottom of this one
  public double bottomPixelBrightness() {
    return this.down.brightness();
  }

  // computes the brightness of the pixel to the left of this one
  public double leftPixelBrightness() {
    return this.left.brightness();
  }

  // computes the brightness of the pixel to the right of this one
  public double rightPixelBrightness() {
    return this.right.brightness();
  }
  
  // removes this node from the list and returns this value
  public abstract void remove() {
    // change links
  }
}

// represents a pixel in an image
class Pixel extends APixel {

  // the constructor
  Pixel(Color color) {
    super();
    this.color = color;
  }

  // convenience constructor
  Pixel(Color color, APixel up, APixel down, APixel left, APixel right) {
    super();
    if (up == null || down == null || left == null || right == null) {
      throw new IllegalArgumentException("given nodes are null.");
    }
    
    this.color = color;
    this.up = up;
    this.down = down;
    this.left = left;
    this.right = right;
    this.topLeft = up.left;
    this.topRight = up.right;
    this.downLeft = down.left;
    this.downRight = down.right;
    // update other nodes to refer to this node
    this.right.left = this;
    this.left.right = this;
    this.down.up = this;
    this.up.down = this;
  }

  // calculates the horizontal energy of this pixel: how much the three left neighbors
  // of this pixel differ from the three right neighbors
  double horizontalEnergy() {
    double topLeft = this.left.topPixelBrightness();
    double left = this.left.brightness();
    double bottomLeft = this.left.bottomPixelBrightness();
    double topRight = this.right.topPixelBrightness();
    double right = this.right.brightness();
    double bottomRight = this.right.bottomPixelBrightness();
     
    return (topLeft + 2 * left + bottomLeft) - (topRight + 2 * right + bottomRight);
  }

  // calculates the vertical energy of this pixel: how much the three top neighbors
  // of this pixel differ from the three bottom neighbors 
  double verticalEnergy() {
    double topLeft = this.left.topPixelBrightness();
    double top = this.up.brightness();
    double topRight = this.right.topPixelBrightness();
    double bottomLeft = this.down.leftPixelBrightness();
    double down = this.down.brightness();
    double bottomRight = this.down.rightPixelBrightness();
    
    return (topLeft + 2 * top + topRight) - (bottomLeft + 2 * down + bottomRight);
  }

  // calculates the total energy of this pixel
  double totalEnergy() {
    return Math.sqrt(Math.pow(this.horizontalEnergy(), 2) + Math.pow(this.verticalEnergy(), 2));
  }

  // removes this pixel from the grid
  public void remove() {
    // change links
    this.left.right = this.right;
    this.up.down = this.right;
    this.down.up = this.right;
    // change diagonal links?
  }
}

// represents a pixel at the edge of the image (black pixel)
class SentinelEdge extends APixel {
  // constructor that takes in zero arguments and initializes
  // next and previous to itself
  SentinelEdge() {
    super();
    this.color = Color.BLACK;
    this.up = this;
    this.down = this;
    this.left = this;
    this.right = this;

    // plus others
  }

  // throws exception when removal of this node from this empty list is tried
  public void remove() {
    throw new RuntimeException("Can't remove from an empty list.");
  }
}

// represents a pixel at the corner of the image (black pixel)
class SentinelCorner extends APixel {
  // constructor that takes in zero arguments and initializes
  // next and previous to itself
  SentinelCorner() {
    super();
    this.color = Color.BLACK;
    this.up = this;
    this.down = this;
    this.left = this;
    this.right = this;

    // plus others
  }

  // throws exception when removal of this node from this empty list is tried
  public void remove() {
    throw new RuntimeException("Can't remove from an empty list.");
  }
}

// represents an image as a grid of pixels
class Grid implements Iterable<APixel> {
  // the start and ends of the grid
  // a list of rows and columns (the corner)
  SentinelCorner corner;

  // the width of this grid
  public int width;
  // the height of this grid
  public int height;

  // the constructor
  Grid() {
    this.corner = new SentinelCorner();
  }

  // convenience constructor
  Grid(SentinelCorner corner) {
    this.corner = corner;
  }

  // returns an iterator that iterates right to access columns
  public Iterator<APixel> iterator() {
    // forward as default
    return new RightIterator(this.corner);
  }

  // returns an iterator that iterates down to access rows
  public Iterator<APixel> verticalIterator() {
    // forward as default
    return new DownIterator(this.corner);
  }

  // EFFECT: adds a pixel of the given color to the end of the specified row
  void addPixel(int row, Color color) {
    // insert new node after sentinel
    SentinelColumnIt rowNum = new SentinelColumnIt(this.corner.down);
    int currRow = 0;
    APixel rowToAddTo = rowNum.next();
    while (rowNum.hasNext() && currRow < row) {
      rowToAddTo = rowNum.next();
      currRow++;
    }
    // check if row exists
    if (currRow < row) {
      // add new sentinels
    }
    rowToAddTo.left.insertRight(color);
  }

  // removes the first node from this list and returns that node value
  // if there's only one seam to remove, remove it and leave an empty image
  void removeMinimumSeam() {
    SeamInfo seamToRemove = this.minimumSeam();
    // remove it through delegation
    seamToRemove.removeSeam();
  }

  // computes the minimum
  SeamInfo minimumSeam() {
    // construct array list of the pixels of this grid
    ArrayList<ArrayList<Pixel>> pixelPaths = new ArrayList<ArrayList<Pixel>>();
    // two arraylists whose indexes corresponding with one another?
    ArrayList<ArrayList<Double>> energyPaths = new ArrayList<ArrayList<Double>>();

    // check if arraylists are empty first?

    // look at SeamInfo of three upper neighbors
    double topLeftEnergy = 0;
    double topEnergy = 0;
    double topRightEnergy = 0;

    // construct a list of SeamInfos to compare at the end
    ArrayList<SeamInfo> seams = new ArrayList<SeamInfo>();
    // for every pixel in the first row, start a seam
    ArrayList<Pixel> firstRow = pixelPaths.get(0);
    for (int i = 0; i < firstRow.size(); i++) {
      SeamInfo seam = new SeamInfo(firstRow.get(i));
      seams.add(seam);
    }

    // for every row in the grid, check the upper neighbors of each pixel in that row 
    // and sum up to the minimum path
    for (int rowIndex = 1; rowIndex < pixelPaths.size(); rowIndex++) {
      // get that row to iterate through
      ArrayList<Pixel> currRow = pixelPaths.get(rowIndex);
      // get the row of energies that correspond to this row
      ArrayList<Double> currRowEnergies = energyPaths.get(rowIndex);
      // the energies of the row above
      ArrayList<Double> rowEnergiesAbove = energyPaths.get(rowIndex - 1);

      // for every pixel in that row, calculate the minimum path energy and change 
      // the energy path to the sum with the current path
      for (int pixIndex = 0; pixIndex < currRow.size(); pixIndex++) {
        // current pixel
        Pixel currPixel = currRow.get(pixIndex);
        topEnergy = rowEnergiesAbove.get(pixIndex);
        // check if left edge
        if (pixIndex == 0) {
          topRightEnergy = rowEnergiesAbove.get(pixIndex + 1);
          topLeftEnergy = 0;
        }
        // check if right edge
        else if (pixIndex == currRow.size() - 1) {
          topRightEnergy = 0;
          topLeftEnergy = rowEnergiesAbove.get(pixIndex - 1);
        }
        else {
          topRightEnergy = rowEnergiesAbove.get(pixIndex + 1);
          topLeftEnergy = rowEnergiesAbove.get(pixIndex - 1);
        }
        // compare energies, change energy in list and make seam
        double sumEnergy = 0;
        SeamInfo lastSeam;

        if (topLeftEnergy <= topEnergy && topLeftEnergy <= topRightEnergy) {
          // top left has least energy
          sumEnergy = currRowEnergies.get(pixIndex) + topLeftEnergy;
          lastSeam = seams.get(pixIndex - 1);
        }
        else if (topEnergy <= topRightEnergy) {
          // top has least energy
          currRowEnergies.set(pixIndex, currRowEnergies.get(pixIndex) + topEnergy);
          lastSeam = seams.get(pixIndex);
        }
        else {
          // top right has least energy
          currRowEnergies.set(pixIndex, currRowEnergies.get(pixIndex) + topRightEnergy);
          lastSeam = seams.get(pixIndex + 1);
        }
        currRowEnergies.set(pixIndex, sumEnergy);
        // add on this pixel to that minimum seam
        // link this new SeamInfo to the previous one
        SeamInfo newSeam = new SeamInfo(currPixel, sumEnergy, lastSeam);
        // change list of seams
        seams.set(pixIndex, newSeam);
      }
    }

    SeamInfo minSeam = seams.get(0);
    SeamInfo currSeam = minSeam;
    // compare the weights of the seams at end
    // parse through seam list and find minimum energy seam
    for (int i = 0; i < seams.size(); i++) {
      currSeam = seams.get(i);
      if(currSeam.hasLessEnergy(minSeam)) {
        minSeam = currSeam;
      }
    }
    // potentially can be null if no grid
    return minSeam;
  }
  
  // draws this grid as a pixel image
  WorldImage draw() {
    ComputedPixelImage carvedImage = new ComputedPixelImage(this.width, this.height);
    // setPixel(int column, int row, Color c)

    int columnIndex = 0;
    int rowIndex = 0;
    SentinelRowIt numOfColumnsIt = new SentinelRowIt(this.corner.right);

    // for every column of this grid, iterate vertically and set pixel image color
    // to color of grid pixel
    while (numOfColumnsIt.hasNext()) {
      APixel columnStart = numOfColumnsIt.next().down;
      DownIterator columnIt = new DownIterator(columnStart);
      // set pixels 
      while (columnIt.hasNext()) {
        carvedImage.setPixel(columnIndex, rowIndex, columnIt.next());
        rowIndex++;
      }
      columnIndex++;
    }
    return carvedImage;
  }
}

// represents an iterator that iterates through a column of sentinels (keeps track of row)
class SentinelColumnIt implements Iterator<APixel> {
  // the list to iterate through
  APixel source;

  // the constructor
  SentinelColumnIt(APixel source) {
    this.source = source;
  }

  // checks whether there exists a next node
  public boolean hasNext() {
    return this.source instanceof SentinelEdge;
  }

  // retrieves this pixel's color and goes on to the next
  public APixel next() {
    // checks if there are elements
    if (!this.hasNext()) { // next one is SentinelCorner
      throw new RuntimeException("No elements to iterate through.");
    }
    SentinelEdge pixel = (SentinelEdge) source;
    this.source = pixel.advanceDown();
    return pixel;
  }

  // removes this node from the list
  public void remove() {
    this.source.remove();
  }
}

// represents an iterator that iterates through a row of sentinels (keeps track of column)
class SentinelRowIt implements Iterator<APixel> {
  // the list to iterate through
  APixel source;

  // the constructor
  SentinelRowIt(APixel source) {
    this.source = source;
  }

  // checks whether there exists a next node
  public boolean hasNext() {
    return this.source instanceof SentinelEdge;
  }

  // retrieves this pixel's color and goes on to the next
  public APixel next() {
    // checks if there are elements
    if (!this.hasNext()) { // next one is SentinelCorner
      throw new RuntimeException("No elements to iterate through.");
    }
    SentinelEdge pixel = (SentinelEdge) source;
    this.source = pixel.advanceRight();
    return pixel;
  }

  // removes this node from the list
  public void remove() {
    this.source.remove();
  }
}

// represents an iterator that goes forward in the list of pixels
class RightIterator implements Iterator {
  // the list to iterate through
  APixel source;

  // the constructor
  RightIterator(APixel source) {
    this.source = source;
  }

  // checks whether there exists a next node
  public boolean hasNext() {
    // check if next one is a node or goes back to sentinel:
    return this.source instanceof Pixel;
  }

  // retrieves this pixel's color and goes on to the next
  public Color next() {
    // checks if there are elements
    if (!this.hasNext()) { // next one is sentinel
      throw new RuntimeException("No elements to iterate through.");
    }
    Pixel abstractPixelAsPixel = (Pixel) source;
    Color color = abstractPixelAsPixel.color;
    this.source = abstractPixelAsPixel.advanceRight();
    return color;
  }

  // removes this node from the list
  public void remove() {
    this.source.remove();
  }
}

// represents an iterator that goes down in a column of pixels
class DownIterator implements Iterator {
  // the list to iterate through
  APixel source;

  // the constructor
  DownIterator(APixel source) {
    this.source = source;
  }

  // checks whether there exists a next node
  public boolean hasNext() {
    // check if next one is a node or goes back to sentinel:
    return this.source instanceof Pixel;
  }

  // retrieves this pixel's energy and goes on to the next
  public Color next() {
    // checks if there are elements
    if (!this.hasNext()) { // next one is sentinel
      throw new RuntimeException("No elements to iterate through.");
    }
    Pixel abstractPixelAsPixel = (Pixel) source;
    Color color = abstractPixelAsPixel.color;
    this.source = abstractPixelAsPixel.advanceRight();
    return color;
  }

  // removes this node from the list
  public void remove() {
    this.source.remove();
  }
}

// represents a seam
class SeamInfo {
  // the pixel that this information corresponds to
  Pixel pixel;
  // the total weight of this seam
  double totalWeight;
  // the seam up to this seam (null if first pixel of the seam)
  SeamInfo cameFrom;

  // constructor for initial seam where cameFrom is null
  SeamInfo(Pixel pixel) {
    this.pixel = pixel;
    this.totalWeight = pixel.totalEnergy();
  }

  // the constructor
  SeamInfo(Pixel pixel, double totalWeight, SeamInfo cameFrom) {
    this.pixel = pixel;
    this.totalWeight = pixel.totalEnergy();
    this.cameFrom = cameFrom;
  }

  // removes this seam
  SeamInfo removeSeam() {
    // iterate through grid and relink pixels while there are still pixels
    // in this seam
    SeamInfo currSeam = this;
    while (currSeam.cameFrom != null) {
      currSeam.pixel.remove();
    }
    return this;
  }

  // compares energies with the given seam and returns true if this seam
  // has less energy
  boolean hasLessEnergy(SeamInfo that) {
    return totalWeight < that.totalWeight;
  }
}