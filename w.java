import javalib.impworld.*;
import javalib.worldimages.*;
import tester.*;

// runs seam carving on an image
class SeamCarving extends World {
  // width of canvas
  static final int WIDTH = 1250;
  // height of canvas
  static final int HEIGHT = 800;

  // get image from file
  FromFileImage fileImage;

  // the width of the image
  int imageWidth;
  // the height of the image
  int imageHeight;

  // the grid corresponding with the carved image
  Grid imageGrid;

  // indicates whether carving is paused
  boolean paused;
  // indicates whether it's the first tick or not
  boolean firstTick;

  // the constructor
  SeamCarving(FromFileImage image) {
    this.fileImage = image;
    this.imageWidth = (int) fileImage.getWidth();
    this.imageHeight = (int) fileImage.getHeight();
    // turn image into a grid of pixels
    this.imageGrid = new Grid();
    this.paused = true;
    this.firstTick = true;

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
    WorldScene s = new WorldScene(WIDTH, HEIGHT);
    s.placeImageXY(this.imageGrid.render(), WIDTH / 2, HEIGHT / 2);
    return s;
  }

  // on every tick, computes minimum seam and removes it from the image,
  // resulting in an empty image
  public void onTick() {
    if (!paused) {
      SeamInfo seamToRemove = this.imageGrid.minimumSeam("v");
      // remove seams randomly
      if (firstTick) {
        seamToRemove.paintRed();
      } else {
        // remove seams while grid is empty
        while (!this.imageGrid.isEmpty()) {
          this.imageGrid.removeMinimumSeam(seamToRemove, "v");
        }
      }
      this.firstTick = !firstTick;
    }
    // save image as a file
    //this.saveImage("img");
  }

  // based on the key pressed, allows user to pause and unpause removal of seams,
  // and choose between removing vertical or horizontal seams
  public void onKeyEvent(String key) {
    // check if it's spacebar
    if (key.equals(" ")) {
      // pause the removing process
      this.paused = !paused;
    }
    else if (key.equals("v") || key.equals("h")) {
      SeamInfo seamToRemove = this.imageGrid.minimumSeam(key);
      this.imageGrid.removeMinimumSeam(seamToRemove, key);
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
  static final int WIDTH = 1250;
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