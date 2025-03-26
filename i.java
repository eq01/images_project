import java.util.Iterator;
import tester.*;
import javalib.impworld.*;
import java.awt.Color;

// represents a pixel in a grid
interface IPixel {
  // inserts a new node with the given value after this node
  public void insertToRight(Color color);

  // inserts a new node with the given value after this node
  public void insertBelow(Color color);

  // removes this node from the list and returns this value
  public void remove();

  // given this node, advance one step, returns the next node in the list,
  // if there is none, return itself
  IPixel advanceRight();

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

  // inserts a new pixel to the right of this pixel
  public void insertBelow(Color color) {
    // make new node
    Pixel newPixel = new Pixel(color, this, this.down, this, this.right);
    // update this node's links
    this.right = newPixel;
    // also the diagonals!!!!!!!!
  }

  // given this node, advance one step, returns the next node in the list,
  // if there is none, return itself
  public APixel advanceRight() {
    return this.right;
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
  public abstract void remove();
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

//represents an image as a grid of pixels
class Grid implements Iterable<APixel> {
  // the start and ends of the grid
  // a list of rows and columns (the corner)
  SentinelCorner corner;

  // the constructor
  Grid() {
    this.corner = new SentinelCorner();
  }

  // convenience constructor
  Grid(SentinelCorner corner) {
    this.corner = corner;
  }

  // returns an iterator that iterates forward (right)
  public Iterator<APixel> iterator() {
    // forward as default
    return new RightIterator(this.corner.right);
  }

  // returns an iterator that iterates down
  public Iterator<APixel> verticalIterator() {
    // forward as default
    return new DownIterator(this.corner.down);
  }

  // EFFECT: adds the given node to the front of the list
  void addColumn(Color color) {
    // insert new node after sentinel
    this.corner.insertAfter(color);
  }

  // EFFECT: inserts the given node to the tail of the list
  void addRow(Color color) {
    // insert new node after sentinel's previous
    this.corner.insertAfter(color);
  }

  // removes the first node from this list and returns that node value
  APixel removeColumn() {
    return this.corner.next.remove();
  }

  // removes the last node from this list and returns that node value
  APixel removeRow() {
    return this.corner.prev.remove();
  }
}

// represents an iterator that goes forward in the list
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

  // retrieves this pixel's energy and goes on to the next
  public Double next() {
    // checks if there are elements
    if (!this.hasNext()) { // next one is sentinel
      throw new RuntimeException("No elements to iterate through.");
    }
    Pixel abstractNodeAsNode = (Pixel) source;
    double energy = abstractNodeAsNode.totalEnergy();
    this.source = abstractNodeAsNode.advanceRight();
    return energy;
  }

  // removes this node from the list
  public void remove() {
    this.source.remove();
  }
}

//represents a seam
class SeamInfo {
  // the pixel that this information corresponds to
  Pixel pixel;
  // the total weight of this seam
  double totalWeight;
  // the seam up to this seam (null if first pixel of the seam)
  SeamInfo cameFrom;

  SeamInfo(Pixel pixel, double totalWeight, SeamInfo cameFrom) {
    this.pixel = pixel;
    this.totalWeight = pixel.totalEnergy();
    this.cameFrom = ;
  }
  // computes the minimum
  SeamInfo minimumSeam() {
    // 
  }

  // removes this seam?
  SeamInfo removeSeam() {
    //
  }
}
