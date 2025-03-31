import tester.*;
import java.util.Iterator;
import java.awt.Color;
import java.util.ArrayList;
import javalib.worldimages.*; 
import javalib.funworld.*;
import javalib.worldcanvas.WorldCanvas;

// represents a pixel in a grid
interface IPixel {
  // inserts a new node with the given color after this node
  public void insertRight(Color color);

  // inserts a new node with the given color after this node
  public void insertBelow(Color color);

  // inserts a new edge sentinel to the right of this pixel node
  public SentinelEdge insertSentinelRight();

  // inserts a new edge sentinel below this pixel node
  public SentinelEdge insertSentinelBelow();

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
    Pixel newPixel = new Pixel(color, this, this.down, this.left.down, this.right.down);
    // update this node's links
    this.down = newPixel;
    // also the diagonals!!!!!!!!
  }

  // inserts a new edge sentinel to the right of this pixel node
  public SentinelEdge insertSentinelRight() {
    SentinelEdge sentinel = new SentinelEdge();
    this.right = sentinel;
    sentinel.left = this;
    sentinel.right = this.right;
    return sentinel;
  }

  // inserts a new edge sentinel below this pixel node
  public SentinelEdge insertSentinelBelow() {
    SentinelEdge sentinel = new SentinelEdge();
    this.down = sentinel;
    sentinel.up = this;
    sentinel.down = this.down;
    return sentinel;
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
    
    double average = (blue + red + green) / 3;
    average = average / 255.0;
    
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
}

// represents a pixel in an image
class Pixel extends APixel {

  // the constructor
  Pixel(Color color) {
    super();
    this.color = color;
  }

  // convenience constructor that takes in left and right pixel
  Pixel(Color color, APixel left, APixel right) {
    super();
    if (left == null || right == null) {
      throw new IllegalArgumentException("given pixels are null.");
    }
    
    this.color = color;
    this.left = left;
    this.right = right;

    // update other nodes to refer to this node
    this.right.left = this;
    this.left.right = this;
  }

  // convenience constructor
  Pixel(Color color, APixel up, APixel down, APixel left, APixel right) {
    super();
    if (up == null || down == null || left == null || right == null) {
      throw new IllegalArgumentException("given pixels are null.");
    }
    
    this.color = color;
    this.up = up;
    this.down = down;
    this.left = left;
    this.right = right;

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
    this.up.down = this.down;
    this.down.up = this.up;
    this.right.left = this.left;
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
  }

  // removes this edge pixel from the grid
  public void remove() {
    // change links
    this.left.right = this.right;
    this.up.down = this.down;
    this.down.up = this.up;
    this.right.left = this.left;
  }
}

// represents a pixel at the corner of the image (black pixel)
class SentinelCorner extends APixel {
  // constructor that takes in zero arguments and initializes
  // all directional links to itself
  SentinelCorner() {
    super();
    this.color = Color.BLACK;
    this.up = this;
    this.down = this;
    this.left = this;
    this.right = this;
  }

  // throws an exception when attempting to link a regular pixel to this since
  // the corner should only be able to add sentinel edges to its direct links
  public void insertRight(Color color) {
    throw new RuntimeException("Can only add edges to corner.");
  }

  // throws an exception when attempting to link a regular pixel to this since
  // the corner should only be able to add sentinel edges to its direct links
  public void insertBelow(Color color) {
    throw new RuntimeException("Can only add edges to corner.");
  }

  // throws exception when removal of this node from this empty list is tried
  public void remove() {
    throw new RuntimeException("Can't remove from an empty grid.");
  }

  // checks whether this sentinel links to other pixels
  public boolean hasPixels() {
    if (this.right == this && this.down == this) {
      return false;
    }
    return true;
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
    APixel rowToAddTo = this.corner.down;
    // check if establishing first pixel
    if (!this.corner.hasPixels()) {
      this.corner.left.insertSentinelRight();
      this.width++;
      this.corner.up.insertSentinelBelow();
      this.height++;
    }
    else if (row == 0) { // first row establishes width of grid
      // make more edge sentinels for the row
      this.corner.left.insertSentinelRight();
      rowToAddTo.left.insertRight(color);
      this.width++;
    }
    else {
      // insert new node after sentinel
      SentinelColumnIt it = new SentinelColumnIt(this.corner.down);
      int currRow = 0;
      while (it.hasNext() && currRow < row) {
        rowToAddTo = it.next();
        currRow++;
      }
      // check if row exists
      if (currRow < row) {
        // add new sentinel below
        rowToAddTo = rowToAddTo.insertSentinelBelow();
        this.height++;
      }
      // need to check that all row lengths are the same
      rowToAddTo.left.insertRight(color);
    }
    // fix links here or once it's all done for more efficient?
    this.fixLinks(rowToAddTo.right);
  }

  // EFFECT: removes the first node from this list and returns that node value
  // if there's only one seam to remove, remove it and leave an empty image
  void removeMinimumSeam() {
    SeamInfo seamToRemove = this.minimumSeam();
    // remove it through delegation and mutate this grid
    seamToRemove.removeSeam();
  }

  // computes the minimum seam of this grid
  SeamInfo minimumSeam() {
    // construct array list of the pixels of this grid
    ArrayList<ArrayList<Pixel>> pixelPaths = new ArrayList<ArrayList<Pixel>>();
    // two arraylists whose indexes corresponding with one another?
    ArrayList<ArrayList<Double>> energyPaths = new ArrayList<ArrayList<Double>>();
    
    
    ArrayList<Pixel> inner1 = new ArrayList<Pixel>();
    ArrayList<Double> inner2 = new ArrayList<Double>();
        
    // accumulates the pixel and their energies into the lists
    /*while (this.iterator().hasNext()) {
      inner1.add(this.iterator().next());
      inner2.add(this.iterator().next().totalEnergy());
    }*/
    
    energyPaths.add(inner2);
    pixelPaths.add(inner1);
    

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
      if (currSeam.hasLessEnergy(minSeam)) {
        minSeam = currSeam;
      }
    }
    // potentially can be null if no grid
    return minSeam;
  }
  
  // draws this grid as a pixel image
  WorldImage render() {
    // check this image grid's size
    if (this.width <= 0 && this.height <= 0) {
      return new EmptyImage();
    }
    // convert grid to image
    ComputedPixelImage carvedImage = new ComputedPixelImage(this.width, this.height);
    // setPixel(int column, int row, Color c)

    int columnIndex = 0;
    int rowIndex = 0;
    SentinelColumnIt rowsIt = new SentinelColumnIt(this.corner.down);
    APixel rowPixel = this.corner.down;

    // for every row of this grid, iterate through each pixel in the row and set 
    // pixel image color to color of grid pixel
    while (rowsIt.hasNext()) {
      rowPixel = rowPixel.right;
      RightIterator rowIt = new RightIterator(rowPixel);
      // set pixels in the current row
      while (rowIt.hasNext()) {
        carvedImage.setPixel(rowIndex, columnIndex, rowPixel.color);
        rowPixel = rowIt.next();
        rowIndex++;
      }
      rowIndex = 0;
      columnIndex++;
      rowPixel = rowsIt.next();
    }
    return carvedImage;
  }

  // fixes the links of the new pixel
  public void fixLinks(APixel pixel) {
    
    APixel left = pixel.left;
    APixel top = left.up.right;

    top.down = pixel;
    pixel.up = top;
  }

}

// represents a generic iterator that goes through in the list of pixels
abstract class GridIterator implements Iterator<APixel> {
  // the list to iterate through
  APixel source;

  // the constructor
  GridIterator(APixel source) {
    this.source = source;
  }

  // checks whether there exists a next node
  public boolean hasNext() {
    // check if next one is a node or goes back to sentinel:
    return this.source instanceof Pixel;
  }

  // removes this node from the list
  public void remove() {
    this.source.remove();
  }
}

// represents a generic iterator that iterates through sentinels 
abstract class SentinelIt implements Iterator<APixel> {
  // the list to iterate through
  APixel source;

  // the constructor
  SentinelIt(APixel source) {
    this.source = source;
  }

  // checks whether next node is still an edge sentinel or not
  public boolean hasNext() {
    return this.source instanceof SentinelEdge;
  }

  // removes this node from the list
  public void remove() {
    this.source.remove();
  }
}

// represents an iterator that iterates through a column of sentinels (keeps track of row)
class SentinelColumnIt extends SentinelIt {
  // the constructor
  SentinelColumnIt(APixel source) {
    super(source);
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
}

// represents an iterator that iterates through a row of sentinels (keeps track of column)
class SentinelRowIt extends SentinelIt {
  // the list to iterate through
  APixel source;

  // the constructor
  SentinelRowIt(APixel source) {
    super(source);
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
}

// represents an iterator that goes forward in the list of pixels
class RightIterator extends GridIterator {
  // the list to iterate through
  APixel source;

  // the constructor
  RightIterator(APixel source) {
    super(source);
  }

  // retrieves this pixel's color and goes on to the next
  public Pixel next() {
    // checks if there are elements
    if (!this.hasNext()) { // next one is sentinel
      throw new RuntimeException("No elements to iterate through.");
    }
    Pixel abstractPixelAsPixel = (Pixel) source;
    //Color color = abstractPixelAsPixel.color;
    this.source = abstractPixelAsPixel.advanceRight();
    return abstractPixelAsPixel;
  }
}

// represents an iterator that goes down in a column of pixels
class DownIterator extends GridIterator {
  // the list to iterate through
  APixel source;

  // the constructor
  DownIterator(APixel source) {
    super(source);
  }

  // retrieves this pixel's color and goes on to the next
  public Pixel next() {
    // checks if there are elements
    if (!this.hasNext()) { // next one is sentinel
      throw new RuntimeException("No elements to iterate through.");
    }
    Pixel abstractPixelAsPixel = (Pixel) source;
    //Color color = abstractPixelAsPixel.color;
    this.source = abstractPixelAsPixel.advanceDown();
    return abstractPixelAsPixel;
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
    this.totalWeight = totalWeight;
    this.cameFrom = null;
  }

  // removes this seam
  SeamInfo removeSeam() {
    // iterate through grid and relink pixels while there are still pixels
    // in this seam
    SeamInfo currSeam = this;
    // while there is still a pixel in the seam, remove each pixel in this seam
    while (currSeam.cameFrom != null) {
      currSeam.pixel.remove();
      currSeam = currSeam.cameFrom;
    }
    currSeam.pixel.remove();
    return this;
  }

  // compares energies with the given seam and returns true if this seam
  // has less energy
  boolean hasLessEnergy(SeamInfo that) {
    return totalWeight < that.totalWeight;
  }
}

class ExamplesImages {
  APixel se = new SentinelEdge();
  
  APixel sc = new SentinelCorner();

  APixel p1 = new Pixel (Color.pink, sc, se, sc, se);
  APixel p2 = new Pixel (Color.pink, sc, p1, sc, se);


  APixel p3 = new Pixel(Color.blue);
  APixel p4 = new Pixel(Color.gray);
  APixel p5 = new Pixel(Color.red);

  
  Grid gridEmpty;
  Grid nonEmpty;

  Pixel blue;
  Pixel red;
  Pixel yellow;

  void initData() {
    gridEmpty = new Grid();
    blue = new Pixel(Color.BLUE);
    red = new Pixel(Color.RED);
    yellow = new Pixel(Color.YELLOW);
  }

  // tests adding pixels to a Grid
  void testAddPixel(Tester t) {
    this.initData();
    t.checkExpect(gridEmpty, new Grid());

    // add one pixel
    gridEmpty.addPixel(0, Color.BLUE);
    SentinelCorner corner = new SentinelCorner();
    SentinelEdge rightSent = corner.insertSentinelRight();
    SentinelEdge downSent = corner.insertSentinelBelow();
    corner.right.down = blue;
    corner.down.right = blue;
    blue.up = rightSent;
    blue.down = rightSent;
    blue.left = downSent;
    blue.right = downSent;
    Grid expected = new Grid(corner);
    t.checkExpect(gridEmpty, expected);

    // add another pixel to same row
    gridEmpty.addPixel(0, Color.RED);
    SentinelEdge rightSent2 = rightSent.insertSentinelRight();
    rightSent2.down = red;
    blue.right = red;
    red.left = blue;
    red.up = rightSent2;
    red.right = downSent;
    red.down = rightSent2;
    t.checkExpect(gridEmpty, expected);

    // add pixel to new row
    gridEmpty.addPixel(1, Color.YELLOW);
    SentinelEdge downSent2 = downSent.insertSentinelBelow();
    downSent2.right = yellow;
    blue.down = yellow;
    yellow.left = downSent2;
    yellow.up = blue;
    yellow.right = downSent2;
    yellow.down = rightSent;
    t.checkExpect(gridEmpty, expected);
  }

  void testDraw(Tester t) {
    WorldCanvas c = new WorldCanvas(1250, 750);
    WorldScene s = new WorldScene(1250, 750);
    
    this.initData();
    ComputedPixelImage expected = new ComputedPixelImage(2, 2);
    expected.setColorAt(0, 0, Color.BLUE);
    expected.setColorAt(1, 0, Color.RED);
    expected.setColorAt(0, 1, Color.YELLOW);

    gridEmpty.addPixel(0, Color.BLUE);
    gridEmpty.addPixel(0, Color.RED);
    gridEmpty.addPixel(1, Color.YELLOW);
    t.checkExpect(gridEmpty.render(), expected);
    boolean hi = c.drawScene(s.placeImageXY(new ScaleImage(expected, 100), 625, 375)) && c.show();
  }

  // test brightness method
  /*void testBrightness(Tester t) {
    t.checkException(new IllegalArgumentException("average is not between 0.0 and 1.0!"), p4, "brightness");
 
    t.checkExpect(this.p3.brightness(), 1.0);
    t.checkExpect(this.p5.brightness(), 1.0);
  }

  void testVerticalEnergy(Tester t) {
    //return t.checkExpect(null, null);
  }*/
}