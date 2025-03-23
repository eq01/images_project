import java.util.ArrayList;
import tester.*;
import javalib.impworld.*;
import javalib.worldimages.*;
import java.awt.Color;

// runs seam carving on an image
class World {
  // placeholder code for understanding
  WorldImage image = new FromFileImage("");

  // turn image into a grid of pixels
  // using this method to help
  // getColorAt(int x, int y)
  // Grid imageGrid = new Grid();

  // the image size 
  // WorldImage carvedImage = new ComputedPixelImage(int width, int height);
  // set the pixel colors using this method
  // setPixel(int x, int y, Color c)

  // save image as a file
  // saveImage(String filename)
}

// represents a pixel in an image
class Pixel {
  // the color of this pixel
  Color color;
  // the pixel above this one
  Pixel up;
  // the pixel below this one
  Pixel down;
  // the pixel to the left of this one
  Pixel left;
  // the pixel to the right of this one
  Pixel right;

  // preserve structural invariant
  // the pixel topleft of this one
  Pixel topLeft;
  // the pixel topright of this one
  Pixel topRight;
  // the pixel downleft of this one
  Pixel downLeft;
  // the pixel downright of this one
  Pixel downRight;

  // the constructor
  Pixel(Color color) {
    //
    this.color = color;
  }

  // calculates the brightness of this pixel
  double brightness() {
    //
  }

  // calculates the horizontal energy of this pixel: how much the three left neighbors
  // of this pixel differ from the three right neighbors
  double horizontalEnergy() {
    //
  }

  // calculates the vertical energy of this pixel: how much the three top neighbors
  // of this pixel differ from the three bottom neighbors 
  double verticalEnergy() {
    //
  }

  // caluculates the total energy of this pixel
  double totalEnergy() {
    //
  }
}

// represents an image as a grid of pixels
class Grid {
  //
}

// represents a seam
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
