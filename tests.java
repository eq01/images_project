import tester.*;
import java.awt.Color;

class ExamplesImages {
  APixel se = new SentinelEdge();
  
  APixel sc = new SentinelCorner();

  APixel p1 = new Pixel (Color.pink, sc, se, sc, se);
  APixel p2 = new Pixel (Color.pink, sc, p1, sc, se);


  APixel p3 = new Pixel(Color.blue);
  APixel p4 = new Pixel(Color.gray);
  APixel p5 = new Pixel(Color.red);


  // test brightness method
  void testBrightness(Tester t) {
    t.checkException(new IllegalArgumentException("average is not between 0.0 and 1.0!"), p4, "brightness");
 
    t.checkExpect(this.p3.brightness(), 1.0);
    t.checkExpect(this.p5.brightness(), 1.0);
  }

  void testVerticalEnergy(Tester t) {
    //return t.checkExpect(null, null);
  }
}