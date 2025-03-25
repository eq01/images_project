import java.util.Iterator;
import tester.*;
import javalib.impworld.*;
import java.awt.Color;

// represents a pixel in a grid
interface IPixel {
  // inserts a new node with the given value after this node
  void insertToRight(Color color);

  // inserts a new node with the given value after this node
  void insertBelow(Color color);

  // removes this node from the list and returns this value
  Pixel remove();

  // given this node, advance one step, returns the next node in the list,
  // if there is none, return itself
  IPixel advanceRight();
}

// the implementations of a pixel in a grid
abstract class APixel implements IPixel {
  // the pixel above this one
  APixel up;
  // the pixel below this one
  APixel down;
  // the pixel to the left of this one
  APixel left;
  // the pixel to the right of this one
  APixel right;

  // preserve structural invariant

  // inserts a new pixel to the right of this pixel
  public void insertRight(Color color) {
    // make new node
    Pixel newPixel = new Pixel(color, this.up, this.down, this, this.right);
    // update this node's links
    this.right = newPixel;
    // also the diagonals!!!!!!!!
  }

  // given this node, advance one step, returns the next node in the list,
  // if there is none, return itself
  public APixel advanceRight() {
    return this.right;
  }
}

// represents a pixel in an image
class Pixel extends APixel {
  // the color of this pixel
  Color color;

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
    // update other nodes to refer to this node
    this.right.left = this;
    this.left.right = this;
    this.down.?
  }

  // calculates the brightness of this pixel
  double brightness() {
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

  // calculates the horizontal energy of this pixel: how much the three left neighbors
  // of this pixel differ from the three right neighbors
  double horizontalEnergy() {
    double b
    
    return
  }

  // calculates the vertical energy of this pixel: how much the three top neighbors
  // of this pixel differ from the three bottom neighbors 
  double verticalEnergy() {
    //
  }

  // calculates the total energy of this pixel
  double totalEnergy() {
    return Math.sqrt(Math.pow(this.horizontalEnergy(), 2) + Math.pow(this.verticalEnergy(), 2));
  }

  // removes this pixel from the grid
  void remove() {
    //
  }
}

// represents a pixel at the edge of the image (black pixel)
class SentinelPixel extends APixel {
  Color BLACK = Color.black;
  // constructor that takes in zero arguments and initializes
  // next and previous to itself
  SentinelPixel() {
    super();
    this.up = this;
    this.down = this;
    this.left = this;
    this.right = this;

    // plus others
  }

  // throws exception when removal of this node from this empty list is tried
  public Pixel remove() {
    throw new RuntimeException("Can't remove from an empty list.");
  }
}

//represents an image as a grid of pixels
class Grid implements Iterable<APixel> {
  // the start and ends of the grid
  // a list of rows 
  SentinelPixel rows;
  // list of columns
  SentinelPixel columns;

  // the constructor
  Grid() {
    //
  }

  // returns an iterator that iterates forward
  public Iterator<APixel> iterator() {
    // forward as default
    return new RightIterator(this.header.right);
  }

  // returns an iterator that iterates down
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

  // computes the minimum
  SeamInfo minimumSeam() {
    // 
  }

  // removes this seam?
  SeamInfo removeSeam() {
    //
  }
}
