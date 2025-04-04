import java.util.Iterator;

//represents a generic iterator that goes through in the list of pixels
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

  // returns this iterator with the given pixel as the source
  public GridIterator createIterator(APixel source) {
    this.source = source;
    return this;
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

  // advances the given pixel in the direction suitable for this iterator
  public APixel advancePixel(APixel pixel) {
    return pixel;
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

  @Override
  // advances the given pixel to the right to access the row of pixels
  public APixel advancePixel(APixel pixel) {
    return pixel.advanceRight();
  }
}

// represents an iterator that iterates through a row of sentinels (keeps track of column)
class SentinelRowIt extends SentinelIt {
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

  @Override
  // advances the given pixel down to access the column of pixels
  public APixel advancePixel(APixel pixel) {
    return pixel.advanceDown();
  }
}

// represents an iterator that goes forward in the list of pixels
class RightIterator extends GridIterator {
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
    // Color color = abstractPixelAsPixel.color;
    this.source = abstractPixelAsPixel.advanceRight();
    return abstractPixelAsPixel;
  }
}

// represents an iterator that goes down in a column of pixels
class DownIterator extends GridIterator {
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
    // Color color = abstractPixelAsPixel.color;
    this.source = abstractPixelAsPixel.advanceDown();
    return abstractPixelAsPixel;
  }
}
