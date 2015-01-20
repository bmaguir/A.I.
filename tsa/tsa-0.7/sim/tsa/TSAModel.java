/*
 * 'Travelling Sales Ant' world (sample solution outline based on
 * Armin Buch's code)
 */
package sim.tsa;

import java.util.ArrayList;
import java.util.Random;
import java.awt.Color;
import uchicago.src.sim.space.*;
import uchicago.src.sim.engine.*;
import uchicago.src.sim.gui.*;
import uchicago.src.sim.util.SimUtilities;
import uchicago.src.sim.analysis.*;
import cern.jet.random.Uniform;
import uchicago.src.reflector.BooleanPropertyDescriptor;

public class TSAModel extends SimModelImpl {
  private Schedule schedule;
  private DataRecorder recorder;
  private DisplaySurface dsurf;
  private OpenSequenceGraph graph;
  private ArrayList antList = new ArrayList();
  private TSAWorld space;

  // best results so far
  private double bestLength;
  private ArrayList bestPath;

  // initial starting parameters of the model
  private boolean DelayedPheromone = false;
  private int numAnts = 40; // Number of Ants
  private int numCities = 10; //  Number of Cities
  private int xSize = 150; // vertical size
  private int ySize = 120; // horizontal size
  private double pheromoneWeight = 1.0; 
  private double distanceWeight = 3.0;
  private static final double initPher = .000001; // initial pheromone
  private double pheromoneAmount = 1.0; // Q
  private double evaporation = .5;


  public TSAModel() {
    BooleanPropertyDescriptor bd = new BooleanPropertyDescriptor("Replacement", false);
    descriptors.put("Replacement", bd);
  }

  // all the getters and setters
  public boolean getDelayedPheromone() { return DelayedPheromone; }
  public void setDelayedPheromone (boolean DelayedPheromone_) { DelayedPheromone =  DelayedPheromone_; }
  public int getNumAnts() { return numAnts; }
  public void setNumAnts(int num) { numAnts = num; }
  public int getXSize() { return xSize; }
  public void setXSize(int num) { xSize = num; }
  public int getYSize() { return ySize; }
  public void setYSize(int num) { ySize = num; }
  public int getNumCities() { return numCities; }
  public void setNumCities(int num) { numCities = num; }
  public double getPheromoneWeight() { return pheromoneWeight; }
  public void setPheromoneWeight(double num) { pheromoneWeight = num; }
  public double getDistanceWeight() { return distanceWeight; }
  public void setDistanceWeight(double num) { distanceWeight = num; }
  public double getPheromoneAmount() { return pheromoneAmount; }
  public void setPheromoneAmount(double num) { pheromoneAmount = num; }
  public double getEvaporation() { return evaporation; }
  public void setEvaporation(double num) { evaporation = num; }
  public double getInitPher() { return initPher; }


  public String[] getInitParam() {
    String[] params = {"DelayedPheromone","numAnts", "xSize", "ySize", "numCities", 
                       "pheromoneWeight", "distanceWeight", 
                       "pheromoneAmount", "evaporation"};
    return params;
  }

  // setting up everything in the beginning
  public void begin() {
    // set bestLength to initial value guaranteed to be larger than any actual path length
    bestLength = (double) (xSize + ySize) * numCities; 
    // contruct world (random city distribution)
    space = new TSAWorld(numCities, xSize, ySize, this);

    // create the ants
    for (int i = 0; i < numAnts; i++) { 
      antList.add(new TravellingSalesAnt(space, this)); 
    }
    
    recorder = new DataRecorder("./tsa.txt", this);
    recorder.addObjectDataSource("Avg. Path Lengh", new AvgPathLength());
    
    // set up the display in three steps: create display, feed list, plug into the displayer
    Object2DDisplay cityDisplay = new Object2DDisplay(space.getCities());
    Object2DDisplay bestPathDisplay = new Object2DDisplay(space.getBestPath());
    Object2DDisplay pathDisplay = new Object2DDisplay(space.getPaths());
    
    cityDisplay.setObjectList(space.getCitiesList());
    bestPathDisplay.setObjectList(space.getBestPathList());
    pathDisplay.setObjectList(space.getPathsList());

    // order matters (overpainting)!
    dsurf.addDisplayable(pathDisplay, "Paths");
    dsurf.addDisplayable(bestPathDisplay, "Best Path");
    dsurf.addDisplayableProbeable(cityDisplay, "Cities");

    addSimEventListener(dsurf);
    
    // class for the graph
    class BestLengthClass implements DataSource, Sequence {
      public Object execute() { return new Double(getSValue()); }
      public double getSValue() { return bestLength; }
    };

    // undocumented 'feature': passing -1 as markStyle gets rid of point marks altogether
    graph.addSequence("Best Path Length", new BestLengthClass(), -1);
    graph.addSequence("Avg. Path Length", new AvgPathLength(), -1);
    graph.setAxisTitles("Time", "Lengths");
    graph.setXRange(0, 10);
    graph.setYRange(0, bestLength/10);
    graph.setSize(500, 300);

    /**
     *  This class implements a version of the ACO meta-heuristic with
     *  delayed pheromone laying (by each ant, after they complete
     *  their tour). 
     *  @see<a href="https://www.cs.tcd.ie/courses/msciet/cs7et03/1/ants.pdf">The lecture notes</a> 
     */
    class AntAction extends BasicAction {
      public void execute() {
        for (int i = 0; i < antList.size(); i++) {
          TravellingSalesAnt ant = (TravellingSalesAnt) antList.get(i);
          // place ant onto a randomly chosen city
          ant.step((TSACity) space.getCitiesList().get(Uniform.staticNextIntFromTo(0, numCities - 1)));
          if (ant.getLength() < bestLength) {
            bestLength = ant.getLength();
            System.out.println("New best path: "+bestLength);
            bestPath = ant.getPath();
            space.updateBestPath(bestPath);
          }
        }
        space.evaporate(); // evaporate pheromone 
        // now - after all ants went their way - lay down pheromone
        for (int i = 0; i < antList.size(); i++) {
          TravellingSalesAnt ant = (TravellingSalesAnt)antList.get(i);
          ant.step2(); // pheromone
        }
        dsurf.updateDisplay();  // displaying at the end
        graph.step();
        recorder.record();
      }
    };


    schedule.scheduleActionBeginning(0, new AntAction());
    schedule.scheduleActionAtPause(recorder, "writeToFile");
    schedule.scheduleActionAtPause(graph, "writeToFile");
    schedule.scheduleActionAtEnd(recorder, "writeToFile");
    schedule.scheduleActionAtEnd(graph, "writeToFile");
  
    dsurf.display();
    graph.display();
  } // end begin

  public void setup() {
    schedule = null;
    antList = new ArrayList();
    space = null;

    if (dsurf != null) dsurf.dispose();
    dsurf = null;
    if (graph != null) graph.dispose();
    graph = null;

    System.gc();
    schedule = new Schedule(1);
    dsurf = new DisplaySurface(this, "World");
    registerDisplaySurface("World", dsurf);
    graph = new OpenSequenceGraph("Lengths", this, "./graph_data.txt",OpenSeqStatistic.CSV);
    this.registerMediaProducer("Plot", graph);
  }

  // necessary getters
  public Schedule getSchedule() { return schedule; }
  public String getName() { return "Travelling Sales Ant"; }


  // For the data recorder and the graph:
  // Calculate average path length by calling getLength() on every ant
  class AvgPathLength implements DataSource, Sequence {
    public Object execute() { return new Double(getSValue()); }

    public double getSValue() {
      int size = antList.size();
      int val = 0;
      for (int i = 0; i < size; i++) {
        TravellingSalesAnt agent = (TravellingSalesAnt)antList.get(i);
        val += agent.getLength(); // add all path lengths
      }
      return val / size; // get average
    }
  };

  public static void main(String[] args) {
    SimInit init = new SimInit();
    TSAModel model = new TSAModel();
    init.loadModel(model, "", false);
  }
}
