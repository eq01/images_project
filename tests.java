import tester.*;
import java.awt.Color;
import javalib.impworld.WorldScene;
import javalib.worldcanvas.WorldCanvas;
import javalib.worldimages.ComputedPixelImage;
import java.util.ArrayList;

class ExamplesImages {
  
  SentinelCorner sc;
  SentinelCorner s;

  SentinelEdge se;
  SentinelEdge se2;

  Pixel p;
  Pixel p2;
  Pixel p3;
  Pixel p4;
  Pixel p5;

  Grid g;
  
  GridIterator git;
  SentinelIt sit;
  SentinelIt sit2;
  SentinelColumnIt scIt;
  SentinelColumnIt scIt2;
  SentinelColumnIt scIt3;
  SentinelRowIt rowIt;
  SentinelRowIt rowIt2;
  SentinelRowIt rowIt3;
  RightIterator rit;
  RightIterator rit2;
  RightIterator rit3;
  DownIterator dit;
  DownIterator dit2;
  DownIterator dit3;
  
  Grid gridEmpty;
  Grid nonEmpty;

  Pixel blue;
  Pixel red;
  Pixel yellow;


  void initData() {
     this.sc = new SentinelCorner();
     this.s = new SentinelCorner();

     this.se = new SentinelEdge();
     this.se2 = new SentinelEdge();

     this.p = new Pixel(Color.blue);
     this.p2 = new Pixel(Color.magenta, sc, p, se, se2);
     this.p3 = new Pixel(Color.orange, p2, p, sc, se);
     this.p4 = new Pixel(Color.yellow, p3, p2, p, se);
     this.p5 = new Pixel(Color.red, p4, p, p3, p2);

     this.g = new Grid(sc);

     this.git = new RightIterator(p2);
     this.sit = new SentinelColumnIt(sc);
     this.sit2 = new SentinelColumnIt(se);
     this.scIt = new SentinelColumnIt(s);
     this.scIt2 = new SentinelColumnIt(se);
     this.rowIt = new SentinelRowIt(s);
     this.rowIt2 = new SentinelRowIt(se);
     this.rit = new RightIterator(s);
     this.rit2 = new RightIterator(p2);
     this.dit = new DownIterator(s);
     this.dit2 = new DownIterator(p4);
     
     this.gridEmpty = new Grid();
     this.blue = new Pixel(Color.BLUE);
     this.red = new Pixel(Color.RED);
     this.yellow = new Pixel(Color.YELLOW);

     this.scIt3 = new SentinelColumnIt(this.sc.down);
     this.rit3 = new RightIterator(this.sc);

     this.rowIt3 = new SentinelRowIt(this.gridEmpty.corner.right);
     this.dit3  = new DownIterator(this.gridEmpty.corner);

  }

  // tests for APixel
  void testBrightness(Tester t) {
    this.initData();

    t.checkExpect(this.sc.brightness(), 0.0);
    t.checkExpect(this.p2.brightness(), 0.6666666666666666);
    t.checkExpect(this.p.brightness(), 0.3333333333333333);
  }

  void testTopPixelBrightness(Tester t) {
    this.initData();

    t.checkExpect(this.p5.topPixelBrightness(), 0.6666666666666666);
    t.checkExpect(this.p4.topPixelBrightness(), 0.5947712418300654);
    t.checkExpect(this.p3.topPixelBrightness(), 0.6666666666666666);
  }

  void testBottomPixelBrightness(Tester t) {
    this.initData();

    t.checkExpect(this.p5.bottomPixelBrightness(), 0.3333333333333333);
    // p4's bottom updated to p5 when p5 was created
    t.checkExpect(this.p4.bottomPixelBrightness(), 0.3333333333333333);
    // p3's bottom updated to p4, when p4 was created
    t.checkExpect(this.p3.bottomPixelBrightness(), 0.6666666666666666);
  }

  void testLeftPixelBrightness(Tester t) {
    this.initData();

    t.checkExpect(this.p5.leftPixelBrightness(), 0.5947712418300654);
    t.checkExpect(this.p4.leftPixelBrightness(), 0.3333333333333333);
    t.checkExpect(this.p3.leftPixelBrightness(), 0.0);
  }

  void testRightPixelBrightness(Tester t) {
    this.initData();

    t.checkExpect(this.p5.rightPixelBrightness(), 0.6666666666666666);
    t.checkExpect(this.p4.rightPixelBrightness(), 0.0);
    // right updated to p5 when p5 was created
    t.checkExpect(this.p3.rightPixelBrightness(), 0.3333333333333333);
  } 


  // tests for Pixel
  void testPixelConstructors(Tester t) {
    this.initData();

    t.checkConstructorException(new IllegalArgumentException("given pixels are null."), "Pixel", Color.blue, null, p , p3, p2);
    t.checkConstructorException(new IllegalArgumentException("given pixels are null."), "Pixel", Color.blue, null, p);
  } 

  void testHorizontalEnergy(Tester t) {
    this.initData();

    t.checkExpect(this.p2.horizontalEnergy(), 1.6666666666666665);
    t.checkExpect(this.p3.horizontalEnergy(), -0.9999999999999999);
  }

  void testVerticalEnergy(Tester t) {
    this.initData();

    t.checkExpect(this.p2.verticalEnergy(), 0.47712418300653603);
    t.checkExpect(this.p3.verticalEnergy(), 0.3333333333333335);
  }

  void testTotalEnergy(Tester t) {
    this.initData();

    t.checkExpect(this.p2.totalEnergy(), 1.7336162389027832);
    t.checkExpect(this.p3.totalEnergy(), 1.0540925533894596);

  }

  void testPaintRed(Tester t) {
    this.initData();

    this.p2.paintRed();
    t.checkExpect(this.p2.brightness(), 0.3333333333333333);
    this.p3.paintRed();
    t.checkExpect(this.p3.brightness(), 0.3333333333333333);
    this.p4.paintRed();
    t.checkExpect(this.p4.brightness(), 0.3333333333333333);

  }

  // testing SentinelCorner 
  void testInsertRight(Tester t) {
    this.initData();

    t.checkException(new RuntimeException("Can only add edges to corner."), this.sc, "insertRight", Color.blue);
  }

  void testInsertBelow(Tester t) {
    this.initData();
    
    t.checkException(new RuntimeException("Can only add edges to corner."), this.sc, "insertBelow", Color.blue);
  }

  void testRemove(Tester t) {
    this.initData();
    
    t.checkException(new RuntimeException("Can't remove from an empty grid."), this.sc, "remove");
  }

  void testHasPixels(Tester t) {
    this.initData();
    t.checkExpect(this.s.hasPixels(), false);
    
    Pixel p1 = new Pixel(Color.blue, this.s, this.se);

    t.checkExpect(this.s.hasPixels(), true);
  }

  // testing Grid
  void testIterator(Tester t) {
    this.initData();
    
    t.checkExpect(this.g.iterator(), new SentinelRowIt(sc));
  }  

  void testVerticalIterator(Tester t) {
    this.initData();
    
    t.checkExpect(this.g.verticalIterator(), new SentinelColumnIt(sc));
  }
  
  void testAddPixel(Tester t) {
    this.initData();
    t.checkExpect(gridEmpty, new Grid());

    // add one pixel
    gridEmpty.addPixel(0, Color.BLUE);
    SentinelCorner corner = new SentinelCorner();
    SentinelEdge rightSent = corner.insertSentinelRight();
    SentinelEdge downSent = corner.insertSentinelBelow();
    corner.right = rightSent;
    corner.down = downSent;
    rightSent.down = blue;
    downSent.right = blue;
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
    t.checkExpect(gridEmpty.width, 2);

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
    t.checkExpect(gridEmpty.height, 2);
  }
  
  void testRemoveMinimumSeam(Tester t) {
    this.initData();

   // t.checkExpect(this.g.removeMinimumSeam(), null);
  }

  void testMinimumSeam(Tester t) {
    this.initData();

   // t.checkExpect(this.g.minimumSeam("v"), null);
  }

  void testGridToArrayListPixel(Tester t) {
    this.initData();
    gridEmpty.addPixel(0, Color.BLUE);
    gridEmpty.addPixel(0, Color.RED);
    gridEmpty.addPixel(1, Color.YELLOW);
    gridEmpty.addPixel(1, Color.GREEN);

    t.checkExpect(this.g.gridToArrayListPixel(scIt3, rit3), new ArrayList<ArrayList<Pixel>>());
    //t.checkExpect(this.gridEmpty.gridToArrayListPixel(scIt3, rit3), null);
    t.checkExpect(this.gridEmpty.gridToArrayListPixel(rowIt3, dit3), null);
  }

  // testing GridIterator
  void testHasNext(Tester t) {
    this.initData();
   
    t.checkExpect(this.git.hasNext(), true);

    this.git.createIterator(s);
    t.checkExpect(this.git.hasNext(), false);

    this.git.createIterator(p);
    t.checkExpect(this.git.hasNext(), true);

  }

  void testCreateIterator(Tester t) {
    this.initData();
   
    t.checkExpect(this.git.createIterator(p2), new RightIterator(p2));
    t.checkExpect(this.git.createIterator(p3), new RightIterator(p3));
    t.checkExpect(this.git.createIterator(sc), new RightIterator(sc));

  }

  // testing SentinelIt
  void testHasNextSentinel(Tester t) {
    this.initData();
   
    t.checkExpect(this.sit2.hasNext(), true);
    t.checkExpect(this.sit.hasNext(), false);
    this.sit.advancePixel(p);
    t.checkExpect(this.sit.hasNext(), false);

  }

  void testAdvancePixel(Tester t) {
    this.initData();
   
    t.checkExpect(this.sit2.advancePixel(p2), se2);
    t.checkExpect(this.sit.advancePixel(p), p4);
    t.checkExpect(this.sit.advancePixel(p3), p5);

  }

  // testing SentinelColumnIt
  void testNext(Tester t) {
    this.initData();
   
    t.checkException(new RuntimeException("No elements to iterate through."), this.scIt, "next");
 
    t.checkExpect(this.scIt2.next(), se);
  }

  void testAdvancePixelSC(Tester t) {
    this.initData();
   
    t.checkExpect(this.scIt.advancePixel(p2), se2);
    t.checkExpect(this.scIt2.advancePixel(p), p4);
    t.checkExpect(this.scIt2.advancePixel(p3), p5);

  }

  // testing SentinelRowIt
  void testNextSR(Tester t) {
    this.initData();

    t.checkException(new RuntimeException("No elements to iterate through."), this.rowIt, "next");
 
    t.checkExpect(this.rowIt2.next(), se);
  }


  void testAdvancePixelSR(Tester t) {
    this.initData();
   
    t.checkExpect(this.rowIt.advancePixel(p2), p3);
    t.checkExpect(this.rowIt2.advancePixel(p4), p5);
    t.checkExpect(this.rowIt2.advancePixel(p3), p4);

  }

  // testing RightIterator
  void testNextR(Tester t) {
    this.initData();

    t.checkException(new RuntimeException("No elements to iterate through."), this.rit, "next");
 
    t.checkExpect(this.rit2.next(), p2);
  }

  // testing DownIterator
  void testNextD(Tester t) {
    this.initData();

    t.checkException(new RuntimeException("No elements to iterate through."), this.dit, "next");
 
    t.checkExpect(this.dit2.next(), p4);
  }

  // testing SeamInfo
}