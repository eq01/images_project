import javalib.impworld.*;
import javalib.worldimages.*;
import tester.*;

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

    // for every row in the image, set the color of file image pixel into
    // a new Pixel in the grid
    for (int row = 0; row < this.imageHeight; row++) {
      // for every pixel in this file image row, make a new pixel of that color
      // and add it to the grid
      for (int rowIndex = 0; rowIndex < this.imageWidth; rowIndex++) {
        // add pixel to grid row
        this.imageGrid.addPixel(row, fileImage.getColorAt(rowIndex, row));
      }
    }
  }

  // draws the image carving
  public WorldScene makeScene() {
    WorldScene s = new WorldScene(this.imageWidth, this.imageHeight);
    s.placeImageXY(this.imageGrid.draw(), imageWidth / 2, imageHeight / 2);
    return s;
  }

  // on every tick, computes minimum seam and removes it from the image,
  // resulting in an empty image
  public void onTick() {
    this.imageGrid.removeMinimumSeam();
    this.imageGrid.draw();
    // save image as a file
    this.saveImage("img");
  }

  // based on the key pressed, allows user to pause and unpause removal of seams,
  // and choose between removing vertical or horizontal seams
  public void onKeyEvent(String key) {
    // check if it's spacebar
    if (key.equals(" ")) {
      // pause the removing process
    }
    else if (key.equals("v")) {
      // remove vertical seams
    }
    else if (key.equals("h")) {
      // remove horizontal seams
    }
  }

  // saves the image as a file
  public void saveImage(String fileName) {
    new FromFileImage(fileName + ".png");
  }
}

// testing class: SeamCarving
class ExamplesSeamCarving {
  // the width of the canvas
  static final int WIDTH = 1000;
  // the height of the canvas
  static final int HEIGHT = 800;
  // the image to carve
  FromFileImage fileImage;
  // to carve
  SeamCarving sc;

  void initData() {
    fileImage = new FromFileImage("src/balloons.jpg");
    sc = new SeamCarving(fileImage);
  }

  void testBigBang(Tester t) {
    this.initData();
    double tickRate = 1.0;
    sc.bigBang(WIDTH, HEIGHT, tickRate);
  }
}