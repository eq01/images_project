import java.util.ArrayList;
import java.util.function.*;


// finds the minimum seam of both horizontal and vertical seams
class MinSeamVertical implements BiFunction<ArrayList<ArrayList<Pixel>>, 
ArrayList<ArrayList<Double>>, SeamInfo> {

  public SeamInfo apply(ArrayList<ArrayList<Pixel>> pixelPaths,
      ArrayList<ArrayList<Double>> energyPaths) {

    // look at SeamInfo of three upper neighbors (initializes)
    double topLeftEnergy = 0;
    double topEnergy = 0;
    double topRightEnergy = 0;

    // construct a list of SeamInfos to compare at the end
    ArrayList<SeamInfo> seams = new ArrayList<SeamInfo>();
    
    // for every pixel in the first row, start a seam
    ArrayList<Pixel> firstRow = pixelPaths.get(0);
    for (int i = 0; i < firstRow.size(); i += 1) {
      SeamInfo seam = new SeamInfo(firstRow.get(i));
      seams.add(seam);
    }

    // for every row in the grid, check the 
    // upper neighbors of each pixel in that row
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
      for (int pixIndex = 0; pixIndex < currRow.size(); pixIndex += 1) {
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
    return new MinSeamHelper().apply(seams);
  }
}

//finds the minimum seam of both horizontal and vertical seams
class MinSeamHorizontal implements
    BiFunction<ArrayList<ArrayList<Pixel>>, ArrayList<ArrayList<Double>>, SeamInfo> {

  public SeamInfo apply(ArrayList<ArrayList<Pixel>> pixelPaths,
      ArrayList<ArrayList<Double>> energyPaths) {

    // look at SeamInfo of three neighbors (initializes)
    double leftUpEnergy = 0;
    double leftEnergy = 0;
    double leftDownEnergy = 0;

    // construct a list of SeamInfos to compare at the end
    ArrayList<SeamInfo> seams = new ArrayList<SeamInfo>();

    // for every pixel in the first column, start a seam
    for (int i = 0; i < pixelPaths.size(); i += 1) {
      // gets the first element in each row
      SeamInfo seam = new SeamInfo(pixelPaths.get(i).get(0));
      seams.add(seam);
    }


    // for every row in the grid, check the 
    // neighbors of each pixel in that column
    // and sum up to the minimum path
    for (int colIndex = 1; colIndex < pixelPaths.get(0).size(); colIndex += 1) {
      // list of pixels in current column
      ArrayList<Pixel> currCol = new ArrayList<Pixel>();
      // list of the energies in current column
      ArrayList<Double> currColEnergies = new ArrayList<Double>();
      // list of energies in previous column
      ArrayList<Double> colEnergiesBehind = new ArrayList<Double>();
      
      // accumulates the information about the column to add to the above arrayLists
      for (int row = 0; row < pixelPaths.size(); row += 1) {
        currCol.add(pixelPaths.get(row).get(colIndex));
        currColEnergies.add(energyPaths.get(row).get(colIndex));
        colEnergiesBehind.add(energyPaths.get(row).get(colIndex - 1));
      }

      // for every pixel in that column, calculate the minimum path energy and change
      // the energy path to the sum with the current path
      for (int pixIndex = 0; pixIndex < currCol.size(); pixIndex++) {
        // current pixel
        Pixel currPixel = currCol.get(pixIndex);
        leftEnergy = colEnergiesBehind.get(pixIndex);
        // check if top
        if (pixIndex == 0) {
          leftDownEnergy = colEnergiesBehind.get(pixIndex + 1);
          leftUpEnergy = 0;
        }
        // check if bottom
        else if (pixIndex == currCol.size() - 1) {
          leftDownEnergy = 0;
          leftUpEnergy = colEnergiesBehind.get(pixIndex - 1);
        }
        else {
          leftDownEnergy = colEnergiesBehind.get(pixIndex + 1);
          leftUpEnergy = colEnergiesBehind.get(pixIndex - 1);
        }
        // compare energies, change energy in list and make seam
        double sumEnergy = 0;
        SeamInfo lastSeam;

        if (leftUpEnergy <= leftEnergy && leftUpEnergy <= leftDownEnergy) {
          // top has least energy
          sumEnergy = currColEnergies.get(pixIndex) + leftUpEnergy;
          lastSeam = seams.get(pixIndex - 1);
        }
        else if (leftEnergy <= leftDownEnergy) {
          // left has least energy
          currColEnergies.set(pixIndex, currColEnergies.get(pixIndex) + leftEnergy);
          lastSeam = seams.get(pixIndex);
        }
        else {
          // bottom has least energy
          currColEnergies.set(pixIndex, currColEnergies.get(pixIndex) + leftDownEnergy);
          lastSeam = seams.get(pixIndex + 1);
        }
        currColEnergies.set(pixIndex, sumEnergy);
        // add on this pixel to that minimum seam
        // link this new SeamInfo to the previous one
        SeamInfo newSeam = new SeamInfo(currPixel, sumEnergy, lastSeam);
        // change list of seams
        seams.set(pixIndex, newSeam);
      }
    }
    return new MinSeamHelper().apply(seams);
  }
}

// creates the new seams from what was generated in MinSeam
class MinSeamHelper implements Function<ArrayList<SeamInfo>, SeamInfo> {
  
  // creates new seams
  public SeamInfo apply(ArrayList<SeamInfo> seams) {
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
    return minSeam;
  }
}
