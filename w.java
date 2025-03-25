import javalib.impworld.*;
import javalib.worldimages.*;//runs seam carving on an image

class World {
  // placeholder code for understanding
  FromFileImage fileImage = new FromFileImage("image/balloons.jpg");

  // turn image into a grid of pixels
  // using this method to help
  // getColorAt(column, row)
  Grid imageGrid = new Grid();
  

  // for every column in the image, set the color of file image into
  // a new Pixel in the grid
  for (int column = 0; column < fileImage.getHeight(); column += 1) {
    // for every pixel in this file image row, make a new pixel of that color
    // and add it to the grid
    for (int row = 0; row < fileImage.getWidth(); row += 1) {
      //Pixel columnPixel = new Pixel(fileImage.getColorAt(column, row));
      // add pixel to grid row
      Grid.addToDown(fileImage.getColorAt(column, row));
    }
  }

  // the image size 
  // WorldImage carvedImage = new ComputedPixelImage(int width, int height);
  // set the pixel colors using this method
  // setPixel(int x, int y, Color c)
  
  // save image as a file
  // saveImage(String filename)
}