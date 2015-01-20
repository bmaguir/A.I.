package sim.tsa;

import java.awt.Dimension;
import java.util.ArrayList;
import uchicago.src.sim.space.*;
import uchicago.src.sim.gui.*;
import cern.jet.random.Uniform;

public class TSAWorld {
  private TSAModel model; // the model this world belongs to
  private Object2DTorus paths; // store all paths for display
  private Object2DTorus bestPath; // store the best path for display
  private Object2DTorus cities; // store all cities for display
  private double[][] distance; // & the distances between the cities
  private double[][] pheromone; // the amount of pheromone on the path between two cities
  private Dimension size; // the size of the world
  private ArrayList list = new ArrayList(); // list of cities (for efficient iterative handling)
  private ArrayList pathList = new ArrayList(); // the same for paths
  private ArrayList bestPathList = new ArrayList(); // the same for the best path
  
  // the constructor takes the number of cities, the size, and the model calling it
  public TSAWorld(int num, int x, int y, TSAModel model) {
    // instantiation
    this.model = model;
    distance = new double[x][y];
    pheromone = new double[x][y];
    cities = new Object2DTorus(x,y);
    paths = new Object2DTorus(x,y);
	int [] city_locations_x = {88,61,143,123,30,139,47,35,0,49,32,54,100,105,95,75,65,7,110,53,105,60,104,39,87,24,3,100,32,109,10,98,36,148,143,145,53,139,2,53,11,52,3,25,80,13,109,127,120,70};
	int [] city_locations_y = {119,84,10,52,67,69,84,21,0,75,112,40,9,11,3,3,53,7,54,28,55,77,68,119,61,99,1,87,74,68,53,23,19,15,27,53,22,8,0,82,27,88,0,50,109,44,81,11,96,28};
	num = city_locations_y.length;
    // placing cities
    for (int i = 0; i < num; i++) {
		int a,b;
		///*
		a = city_locations_x[i];
		b = city_locations_y[i];
		TSACity city = new TSACity(i,a,b); // contruct new city (in a day, unlike Rome)
		cities.putObjectAt(a, b, city); // store it in the grid
		list.add(city); // ... and in the list
		//*/
		/*
      do {
        a = Uniform.staticNextIntFromTo(0, x - 1);
        b = Uniform.staticNextIntFromTo(0, y - 1);

      } while (cities.getObjectAt(a, b) != null); // don't place two cities onto the same place
      TSACity city = new TSACity(i,a,b); // contruct new city (in a day, unlike Rome)
      cities.putObjectAt(a, b, city); // store it in the grid
      list.add(city); // ... and in the list
	  System.out.println(a+" "+b);
	 */
    }

    // calculate distances, place initial pheromone and instantiate Path objects
    size = cities.getSize();
    for (int i = 0; i < num; i++) {
      for (int j = 0; j < num; j++) { // for all pairs of cities
        TSACity from = (TSACity) list.get(i);
        TSACity to = (TSACity) list.get(j);
        distance[i][j] = calculateDistance(from, to); // calculate their distance
        pheromone[i][j] = model.getInitPher(); // place initial pheromone on the way connecting them
        Path path = new Path(from, to, this); // create this way
        pathList.add(path); // ... store it in the list
        paths.putObjectAt(from.getX(), from.getY(), path); // ... and in the grid
      }
    }
    // initialize best path as empty
    bestPath = new Object2DTorus(x,y);
    bestPathList = new ArrayList();    
  }
  
  // Evaporation takes place every step
  public void evaporate() {
    for (int i = 0; i < model.getNumCities(); i++) {
      for (int j = 0; j < model.getNumCities(); j++) { // for all pairs of cities
        pheromone[i][j] *= (1 - model.getEvaporation()); // reduce the pheromone amount between them
      }
    } 
  }

  // called by every ant every step for every partial path
  // the symmetric storage is inefficient (all twice), but easier to program
  public void deposit(TSACity city1, TSACity city2, double amount) {
    pheromone[city1.getNum()][city2.getNum()] += amount;
    pheromone[city2.getNum()][city1.getNum()] += amount;
  }

  // returns the distance between two cities  
  public double getDistance(TSACity city1, TSACity city2) {
    return distance[list.indexOf(city1)][list.indexOf(city2)];
  }
  
  // calculates the distance. calling this once allows to access the value via getDistance()
  private double calculateDistance(TSACity city1, TSACity city2) {
    if (city1 == city2) { return 0.0; }
    int x = city1.getX() - city2.getX();
    int y = city1.getY() - city2.getY();
    // for now: treats Torus as 2d plane
    return Math.sqrt(x*x + y*y);
  }


  // inner class for paths 
  class Path implements Drawable {
    static final int stretch = 5; // RePast Parameters not read, just given.
    static final int discount = 500; // display paths not all too bright
    int x1, x2, y1, y2; // for identifying the line spatially...
    TSACity from, to; // ... and logically
    TSAWorld space; // the TSAWorld this path belongs to
    
    // contructor; only sets fields
    public Path(TSACity from, TSACity to, TSAWorld space) {
      this.from = from;
      this.to = to;
      this.space = space;
      x1 = from.getX();
      x2 = to.getX();
      y1 = from.getY();
      y2 = to.getY();
    }
    
    // necessary getters for implementing Drawable        
    public int getX() { return from.getX(); }
    public int getY() { return from.getY(); }
    
    // for drawing. The more pheromone, the bluer
    public void draw(SimGraphics g) {
      double pher = space.getPheromone(from, to);
      // the formula works, leave it like this, or use some graph plotter to see, how it works. 
      // it goes thru (0,0) and approaches 255 as pher approaches infinity
      int rgb = 255 - (int) (1.0/ ((1.0/255) + pher/discount)); 
      g.drawLink(new java.awt.Color(rgb,rgb,rgb), x1*stretch, x2*stretch, y1*stretch, y2*stretch);
    }
  }
  
  // red paths... again inefficient to copy the whole stuff, but it works
  // masks blue paths :(
  class BestPath implements Drawable {
    static final int stretch = 5; // RePast Parameters not read, just given.
    int x1, x2, y1, y2; // for identifying the line spatially...
    TSACity from, to; // ... and logically
    TSAWorld space; // the TSAWorld this path belongs to
    
    // contructor; only sets fields
    public BestPath(TSACity from, TSACity to, TSAWorld space) {
      this.from = from;
      this.to = to;
      this.space = space;
      x1 = from.getX();
      x2 = to.getX();
      y1 = from.getY();
      y2 = to.getY();
    }

    // necessary getters for implementing Drawable        
    public int getX() { return from.getX(); }
    public int getY() { return from.getY(); }

    public void draw(SimGraphics g) {
      g.drawLink(new java.awt.Color(255,0,0), x1*stretch, x2*stretch, y1*stretch, y2*stretch); // in plain red
    }
  }

  
  public void updateBestPath(ArrayList path) {

    // the following I thought to be necessary, but actually it unlinks the data from the displayer
    //    bestPath = new Object2DTorus(paths.getSizeX(), paths.getSizeY());
    bestPathList.clear();

    for (int j = 0; j < path.size() - 1; j++) { // go thru the path
      TSACity from = (TSACity) path.get(j);
      TSACity to = (TSACity) path.get(j+1);
      BestPath p = new BestPath(from, to, this); // create a red path between them
      bestPathList.add(p); // ... and store it both in list
      bestPath.putObjectAt(from.getX(), from.getY(), p); // ... and grid
    }
  }
    
  // accesses the 2D pheromone array
  public double getPheromone(TSACity city1, TSACity city2) {
    return pheromone[list.indexOf(city1)][list.indexOf(city2)];
  }

  // a bunch of getters  
  public Dimension getSize() { return size; }
  public Object2DGrid getCities() { return cities; }
  public Object2DGrid getPaths() { return paths; }
  public Object2DGrid getBestPath() { return bestPath; }
  public ArrayList getCitiesList() { return list; }
  public ArrayList getPathsList() { return pathList; }
  public ArrayList getBestPathList() { return bestPathList; }
  public int getXSize() { return size.width; }
  public int getYSize() { return size.height; }  
}
