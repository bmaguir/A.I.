package sim.tsa;

import java.util.ArrayList;
import cern.jet.random.Uniform;
import java.util.Random;

/* TravellingSalesAnt. Practical 3: Implement an agent capable of
 * finding a path through the space (step()) and updating the
 * pheromone levels proportionally to the quality of the solution
 * (step2()), according to an ant colony optimisation strategy.
 *
 */

public class TravellingSalesAnt {

  private TSAWorld space;
  private TSAModel model;
  private double length;
  private ArrayList visited;
  private ArrayList unvisited;
  boolean updateImediately;

  public TravellingSalesAnt(TSAWorld ss, TSAModel model) {
    space = ss;
    this.model = model;
	updateImediately = false;
  }
  public ArrayList getPath() { return visited; }
  public double getLength() { return length; }

  public void step(TSACity start) {
    // some placeholder code. Add your code implementing a tour here 
    length = 0;
    unvisited = (ArrayList) space.getCitiesList().clone();
	ArrayList cityList = (ArrayList) space.getCitiesList().clone();
	visited = new ArrayList();
	double [] routingTable = new double [space.getCitiesList().size()];
	TSACity currentCity = start;
	
	double a = model.getPheromoneWeight();
	double b = model.getDistanceWeight();
	
	while(unvisited.size() != 0){
	
		if(unvisited.contains(currentCity))
			unvisited.remove(unvisited.indexOf(currentCity));
		visited.add(currentCity);
		
		for(int i = 0; i<space.getCitiesList().size(); i++){
			if(unvisited.contains(cityList.get(i))){
				double tij = space.getPheromone(currentCity, (TSACity) cityList.get(i));
				double dist = space.getDistance(currentCity, (TSACity) cityList.get(i));
				routingTable[i] = Math.pow(tij, a)*(1/Math.pow(dist, b));
			}
			else{
				routingTable[i] = 0;
			}
		}
		/*
		double max = 0;
		int max_index =0;

		for(int i=0; i<cityList.size(); i++)
		{
			if(routingTable[i] > max)
			{
				max = routingTable[i];
				max_index = i;
			}
		}
		*/

		double routingTable_sum = 0;
		
		for(int j = 0; j<cityList.size(); j++){
			routingTable_sum += routingTable[j];
		}
		for(int i = 0; i<cityList.size(); i++){
			routingTable[i] = routingTable[i]/routingTable_sum;
		}

		int chosen_city = chooseCity(cityList, routingTable);
		length += space.getDistance(currentCity, (TSACity) cityList.get(chosen_city));
		currentCity = (TSACity) cityList.get(chosen_city);
	}
	
	if(!model.getDelayedPheromone())
	{
		for(int i=0; i<(visited.size()-1); i++)
		{
			space.deposit((TSACity) visited.get(i), (TSACity) visited.get(i+1), 1/length);
		}
	}
	
  }

  private int chooseCity(ArrayList cityList, double[] routingTable)
  {
		Random random_generator = new Random();
		double random = random_generator.nextDouble();
		double sum =0;
		for(int i = 0; i<cityList.size(); i++){
			sum += routingTable[i];
			if(sum > random)
			{
				return i;
			}
		}
		return (cityList.size()-1);
  }
  
  public void step2() {
    // add your code for pheromone distribution here
	if(model.getDelayedPheromone())
	{
		for(int i=0; i<(visited.size()-1); i++)
		{
			space.deposit((TSACity) visited.get(i), (TSACity) visited.get(i+1), 1/length);
		}
	}
  }
}
