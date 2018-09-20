import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

/* Michael Patel
 * mrpatel5
 * CSC 520
 * Homework 1
 * Fall 2018
 * 
 * Project description:
 * 		- Route finding between a start city A and destination city B
 * 		- uses 3 different algorithm implementations (A*, greedy, dynamic programming)
 * 		- uses U.S. telecom network to describe roads and cities
 * 		- heuristic is based on city latitude and longitude distances 
 * 
 * Notes:
 * 		using pseudocode from Dr. Bahler's lecture notes to implement algorithms
 */
public class SearchUSA {
	/*********************************************************************************************/
	// Constants
	public static final String ASTAR = "astar";
	public static final String GREEDY = "greedy";
	public static final String DYNAMIC = "dynamic";

	/*********************************************************************************************/
	// Globals, Class variables
	public static List<Road> Roads = new ArrayList<>(); // list of all roads
	public static List<City> Cities = new ArrayList<>(); // list of all cities
	public static String SEARCH_TYPE;
	public static String SRC;
	public static String DST;

	/*********************************************************************************************/
	// Constructor
	// used to initialize SearchUSA object
	public SearchUSA() {
		initializeRoads(); // builds Roads list
		initializeCities(); // builds Cities list
	}	

	/*********************************************************************************************/
	// Main
	public static void main(String[] args) {
		SearchUSA s = new SearchUSA();

		// check and parse arguments
		if(args.length == 3) {
			SEARCH_TYPE = args[0];
			SRC = args[1];
			DST = args[2];

			if(SEARCH_TYPE.equals(ASTAR)) { // run A* search
				s.runAstar();
			} 
			else if(SEARCH_TYPE.equals(GREEDY)) { // run Greedy search
				s.runGreedy();
			} 
			else if(SEARCH_TYPE.equals(DYNAMIC)) { // run Dynamic Programming search
				s.runDynamic();
			} 
			else { // can't find search type
				System.out.println("Error: incorrect searchtype");
			}
		}
		else { // number of arguments != 3
			System.out.println("Error: incorrect number of arguments");
		}
	} // end main()

	/*********************************************************************************************/
	// A*
	public void runAstar() {

		// Frontier
		PriorityQueue<Node> frontier = new PriorityQueue<>();

		// build a 'root' node
		Node n = new Node();
		n.name = SRC;
		n.path.add(SRC);
		n.costFromStart = 0;
		n.estimatedCost = 0;

		frontier.add(n); // initialize Frontier with node labeled by initial state

		// Explored
		List<String> explored = new ArrayList<String>(); // initialize Explored to empty, list of city names

		while(!frontier.isEmpty()) {// remove highest-priority-node from Frontier
			// highest-priority is lowest cost, so node with
			// lowest totalCost will be removed from Frontier
			Node currentNode = frontier.remove();
			String currentCity = currentNode.path.get(currentNode.path.size()-1); // last city on path
			//System.out.println("Explored: " + explored);

			// goal test
			if(currentCity.equals(DST)) {
				// reached goal state
				printOutput(currentNode, explored);
				return;
			}			
			else { // did not reach goal state -> node expansion			
				// insert current node into Explored
				if(!explored.contains(currentCity)) {
					explored.add(currentCity); // added to Explored
				}

				// use moveGenerator() to produce successors of current node
				List<Node> successors = moveGenerator(currentCity);

				// for each successor
				for(int i=0; i<successors.size(); i++) {
					Node s = successors.get(i);
					//String sCity = s.name;

					// create a duplicate successor node, but with updated info
					Node t = new Node();
					t.name = s.name;
					t.path.addAll(currentNode.path);
					t.path.add(s.name);
					t.costFromStart = currentNode.costFromStart + s.costFromStart;
					t.estimatedCost = getEstimate(s.name);
					//System.out.println(t.name + " : " + t.estimatedCost);

					// check if successor is in Explored
					if(!explored.contains(s.name)) {
						//System.out.println("successor: " + s.name);
						// if Frontier already has successor
						// then update successor info
						for(Node f : frontier) {
							if(f.name.equals(s.name)) {
								if((t.costFromStart + t.estimatedCost) < (f.costFromStart + f.estimatedCost)) {
									f.costFromStart = t.costFromStart;
									f.estimatedCost = t.estimatedCost;
									f.path = t.path;
								}
							}
						}

						// else not in Frontier, add it
						frontier.add(t);
					}
				}
			}
		} // end while-loop

		// failed to reach goal state
		System.out.println("A* failed");
	} // end A*()

	/*********************************************************************************************/
	// Greedy
	public void runGreedy() {
		// Frontier
				PriorityQueue<Node> frontier = new PriorityQueue<>();

				// build a 'root' node
				Node n = new Node();
				n.name = SRC;
				n.path.add(SRC);
				n.costFromStart = 0;
				n.estimatedCost = 0;

				frontier.add(n); // initialize Frontier with node labeled by initial state

				// Explored
				List<String> explored = new ArrayList<String>(); // initialize Explored to empty, list of city names

				while(!frontier.isEmpty()) {// remove highest-priority-node from Frontier
					// highest-priority is lowest cost, so node with
					// lowest totalCost will be removed from Frontier
					Node currentNode = frontier.remove();
					String currentCity = currentNode.path.get(currentNode.path.size()-1); // last city on path

					// goal test
					if(currentCity.equals(DST)) {
						// reached goal state
						//printOutput(currentNode, explored);
						// arguments
						System.out.println("-----------------------------------------------------------");
						System.out.println("Search type: " + SEARCH_TYPE);
						System.out.println("Source city: " + SRC);
						System.out.println("Destination city: " + DST);
						System.out.println("-----------------------------------------------------------");

						// output
						System.out.println("Expanded nodes: " + explored); // list of expanded nodes
						System.out.println("Number of expanded nodes: " + explored.size()); // number of expanded nodes
						System.out.println("Solution path: " + currentNode.path); // solution path = list of nodes
						System.out.println("Number of nodes in solution path: " + currentNode.path.size()); // number of nodes in solution path
						//System.out.println("Total distance from " + SRC + " to " + DST + ": " + node.costFromStart); // total distance from A to B in solution path		
						
						// trace solution path to find costFromStart
						double distance = greedyFinalDistance(currentNode.path);
						System.out.println("Total distance from " + SRC + " to " + DST + ": " + distance); // total distance from A to B in solution path		
						
						return;
					}			
					else { // did not reach goal state -> node expansion			
						// insert current node into Explored
						if(!explored.contains(currentCity)) {
							explored.add(currentCity); // added to Explored
						}

						// use moveGenerator() to produce successors of current node
						List<Node> successors = moveGenerator(currentCity);

						// for each successor
						for(int i=0; i<successors.size(); i++) {
							Node s = successors.get(i);
							//String sCity = s.name;

							// create a duplicate successor node, but with updated info
							Node t = new Node();
							t.name = s.name;
							t.path.addAll(currentNode.path);
							t.path.add(s.name);
							//t.costFromStart = currentNode.costFromStart + s.costFromStart;
							t.costFromStart = 0;
							t.estimatedCost = getEstimate(s.name);

							// check if successor is in Explored
							if(!explored.contains(s.name)) {
								// if Frontier already has successor
								// then update successor info
								for(Node f : frontier) {
									if(f.name.equals(s.name)) {
										if((t.costFromStart + t.estimatedCost) < (f.costFromStart + f.estimatedCost)) {
											f.costFromStart = t.costFromStart;
											f.estimatedCost = t.estimatedCost;
											f.path = t.path;
										}
									}
								}

								// else not in Frontier, add it
								frontier.add(t);
							}
						}
					}
				} // end while-loop

		// failed to reach goal state
		System.out.println("greedy failed");
	} // end Greedy()

	/*********************************************************************************************/
	// Dynamic Programming
	public void runDynamic() {
		// Frontier
		PriorityQueue<Node> frontier = new PriorityQueue<>();

		// build a 'root' node
		Node n = new Node();
		n.name = SRC;
		n.path.add(SRC);
		n.costFromStart = 0;
		n.estimatedCost = 0;

		frontier.add(n); // initialize Frontier with node labeled by initial state

		// Explored
		List<String> explored = new ArrayList<String>(); // initialize Explored to empty, list of city names

		while(!frontier.isEmpty()) {// remove highest-priority-node from Frontier
			// highest-priority is lowest cost, so node with
			// lowest totalCost will be removed from Frontier
			Node currentNode = frontier.remove();
			String currentCity = currentNode.path.get(currentNode.path.size()-1); // last city on path

			// goal test
			if(currentCity.equals(DST)) {
				// reached goal state
				printOutput(currentNode, explored);
				return;
			}			
			else { // did not reach goal state -> node expansion			
				// insert current node into Explored
				if(!explored.contains(currentCity)) {
					explored.add(currentCity); // added to Explored
				}

				// use moveGenerator() to produce successors of current node
				List<Node> successors = moveGenerator(currentCity);

				// for each successor
				for(int i=0; i<successors.size(); i++) {
					Node s = successors.get(i);
					//String sCity = s.name;

					// create a duplicate successor node, but with updated info
					Node t = new Node();
					t.name = s.name;
					t.path.addAll(currentNode.path);
					t.path.add(s.name);
					t.costFromStart = currentNode.costFromStart + s.costFromStart;
					//t.estimatedCost = getEstimate(s.name);
					t.estimatedCost = 0;

					// check if successor is in Explored
					if(!explored.contains(s.name)) {
						// if Frontier already has successor
						// then update successor info
						for(Node f : frontier) {
							if(f.name.equals(s.name)) {
								if((t.costFromStart + t.estimatedCost) < (f.costFromStart + f.estimatedCost)) {
									f.costFromStart = t.costFromStart;
									f.estimatedCost = t.estimatedCost;
									f.path = t.path;
								}
							}
						}

						// else not in Frontier, add it
						frontier.add(t);
					}
				}
			}
		} // end while-loop
	} // end Dynamic()

	/*********************************************************************************************/
	// generate successors from current
	public List<Node> moveGenerator(String city){
		List<Node> successors = new ArrayList<Node>();

		// use Roads list to find successors of current city
		// i.e. follow roads from current city to neighboring cities
		// roads in Roads are not always bi-directional, so checking src and dst sides of road
		for(int i=0; i<Roads.size(); i++) {
			Road r = Roads.get(i);

			if(r.srccityname.equals(city)) {
				// get city on other side of the road (i.e. destination city)
				// add that city (node) to list of successor nodes
				Node n = new Node();
				n.name = r.destcityname;
				n.path.add(r.destcityname);
				n.costFromStart = r.cost;
				n.estimatedCost = 0;
				successors.add(n);
			}
			if(r.destcityname.equals(city)) {
				// get city on other side of the road (i.e. source city)
				// add that city (node) to list of successor nodes
				Node n = new Node();
				n.name = r.srccityname;
				n.path.add(r.srccityname);
				n.costFromStart = r.cost;
				n.estimatedCost = 0;
				successors.add(n);	
			}
		}

		return successors;
	} // end moveGenerator()

	/*********************************************************************************************/
	// compute heuristic (estimated) cost between current and destination
	public double getEstimate(String city) {
		double lat1 = 0; // latitude for city
		double lat2 = 0; // latitude for destination city
		double long1 = 0; // longitude for city
		double long2 = 0; // longitude for destination city

		// use Cities list to find (lat, long) coordinates
		for(int i=0; i<Cities.size(); i++) {
			City c = Cities.get(i);

			if(c.name.equals(city)) {
				lat1 = c.latitude;
				long1 = c.longitude;
			}
			if(c.name.equals(DST)) {
				lat2 = c.latitude;
				long2 = c.longitude;
			}
		}

		double a = Math.pow((69.5*(lat1-lat2)), 2);
		double b = Math.pow((69.5*Math.cos((lat1+lat2)*Math.PI/360)*(long1-long2)), 2);
		double c = a + b;
		double estimate = Math.sqrt(c);

		return estimate;
	}

	/*********************************************************************************************/
	// compute total distance cost for Greedy by tracing solution path
	// since dist(SRC, current) is zero
	public double greedyFinalDistance(List<String> soln_path) {
		double distance = 0;
		
		// use Roads.cost info
		// solution path contains cities, so find road between solution-path-cities
		// get cost of that road and sum
		for(int i=0; i<soln_path.size()-1; i++) {
			String s = soln_path.get(i);
			String d = soln_path.get(i+1);		
			
			//System.out.println(s + " :: " + d);
			
			for(int j=0; j<Roads.size(); j++) {
				Road r = Roads.get(j);
				
				// roads in Roads are not always bi-directional, so checking src and dst sides of road
				if((r.srccityname.equals(s)) && (r.destcityname.equals(d))){					
					distance = distance + r.cost;
				}
				if((r.srccityname.equals(d)) && (r.destcityname.equals(s))) {
					distance = distance + r.cost;
				}
			}
		}
		
		return distance;
	}

	/*********************************************************************************************/
	// prints arguments and output
	public void printOutput(Node node, List<String> explored) {
		// arguments
		System.out.println("-----------------------------------------------------------");
		System.out.println("Search type: " + SEARCH_TYPE);
		System.out.println("Source city: " + SRC);
		System.out.println("Destination city: " + DST);
		System.out.println("-----------------------------------------------------------");

		// output
		System.out.println("Expanded nodes: " + explored); // list of expanded nodes
		System.out.println("Number of expanded nodes: " + explored.size()); // number of expanded nodes
		System.out.println("Solution path: " + node.path); // solution path = list of nodes
		System.out.println("Number of nodes in solution path: " + node.path.size()); // number of nodes in solution path
		System.out.println("Total distance from " + SRC + " to " + DST + ": " + node.costFromStart); // total distance from A to B in solution path		
	}

	/*********************************************************************************************/
	public void initializeRoads() {
		// from usroads.pl and roads_list.txt
		Roads.add(new Road("albanyNY", "montreal", 226));
		Roads.add(new Road("albanyNY", "boston", 166));
		Roads.add(new Road("albanyNY", "rochester", 148));		
		Roads.add(new Road("albanyGA", "tallahassee", 120));
		Roads.add(new Road("albanyGA", "macon", 106));		
		Roads.add(new Road("albuquerque", "elPaso", 267));
		Roads.add(new Road("albuquerque", "santaFe", 61));		
		Roads.add(new Road("atlanta", "macon", 82));
		Roads.add(new Road("atlanta", "chattanooga", 117));		
		Roads.add(new Road("augusta", "charlotte", 161));
		Roads.add(new Road("augusta", "savannah", 131));		
		Roads.add(new Road("austin", "houston", 186));
		Roads.add(new Road("austin", "sanAntonio", 79));		
		Roads.add(new Road("bakersfield", "losAngeles", 112));
		Roads.add(new Road("bakersfield", "fresno", 107));		
		Roads.add(new Road("baltimore", "philadelphia", 102));
		Roads.add(new Road("baltimore", "washington", 45));		
		Roads.add(new Road("batonRouge", "lafayette", 50));
		Roads.add(new Road("batonRouge", "newOrleans", 80));		
		Roads.add(new Road("beaumont", "houston", 69));
		Roads.add(new Road("beaumont", "lafayette", 122));		
		Roads.add(new Road("boise", "saltLakeCity", 349));
		Roads.add(new Road("boise", "portland", 428));		
		Roads.add(new Road("boston", "providence", 51));		
		Roads.add(new Road("buffalo", "toronto", 105));
		Roads.add(new Road("buffalo", "rochester", 64));
		Roads.add(new Road("buffalo", "cleveland", 191));		
		Roads.add(new Road("calgary", "vancouver", 605));
		Roads.add(new Road("calgary", "winnipeg", 829));		
		Roads.add(new Road("charlotte", "greensboro", 91));		
		Roads.add(new Road("chattanooga", "nashville", 129));		
		Roads.add(new Road("chicago", "milwaukee", 90));
		Roads.add(new Road("chicago", "midland", 279));		
		Roads.add(new Road("cincinnati", "indianapolis", 110));
		Roads.add(new Road("cincinnati", "dayton", 56));		
		Roads.add(new Road("cleveland", "pittsburgh", 157));
		Roads.add(new Road("cleveland", "columbus", 142));
		Roads.add(new Road("coloradoSprings", "denver", 70));
		Roads.add(new Road("coloradoSprings", "santaFe", 316));
		Roads.add(new Road("columbus", "dayton", 72));
		Roads.add(new Road("dallas", "denver", 792));
		Roads.add(new Road("dallas", "mexia", 83));
		Roads.add(new Road("daytonaBeach", "jacksonville", 92));
		Roads.add(new Road("daytonaBeach", "orlando", 54));
		Roads.add(new Road("denver", "wichita", 523));
		Roads.add(new Road("denver", "grandJunction", 246));
		Roads.add(new Road("desMoines", "omaha", 135));
		Roads.add(new Road("desMoines", "minneapolis", 246));
		Roads.add(new Road("elPaso", "sanAntonio", 580));
		Roads.add(new Road("elPaso", "tucson", 320));
		Roads.add(new Road("eugene", "salem", 63));
		Roads.add(new Road("eugune", "medford", 165));
		Roads.add(new Road("europe", "philadelphia", 3939));
		Roads.add(new Road("ftWorth", "oklahomaCity", 209));
		Roads.add(new Road("fresno", "modesto", 109));
		Roads.add(new Road("grandJunction", "provo", 220));
		Roads.add(new Road("greenBay", "minneapolis", 304));
		Roads.add(new Road("greenBay", "milwaukee", 117));		
		Roads.add(new Road("greensboro", "raleigh", 74));		
		Roads.add(new Road("houston", "mexia", 165));		
		Roads.add(new Road("indianapolis", "stLouis", 246));		
		Roads.add(new Road("jacksonville", "savannah", 140));
		Roads.add(new Road("jacksonville", "lakeCity", 113));		
		Roads.add(new Road("japan", "pointReyes", 5131));
		Roads.add(new Road("japan", "sanLuisObispo", 5451));		
		Roads.add(new Road("kansasCity", "tulsa", 249));
		Roads.add(new Road("kansasCity", "stLouis", 256));
		Roads.add(new Road("kansasCity", "wichita", 190));		
		Roads.add(new Road("keyWest", "tampa", 446));		
		Roads.add(new Road("lakeCity", "tampa", 169));
		Roads.add(new Road("lakeCity", "tallahassee", 104));		
		Roads.add(new Road("laredo", "sanAntonio", 154));
		Roads.add(new Road("laredo", "mexico", 741));		
		Roads.add(new Road("lasVegas", "losAngeles", 275));
		Roads.add(new Road("lasVegas", "saltLakeCity", 486));		
		Roads.add(new Road("lincoln", "wichita", 277));
		Roads.add(new Road("lincoln", "omaha", 58));		
		Roads.add(new Road("littleRock", "memphis", 137));
		Roads.add(new Road("littleRock", "tulsa", 276));		
		Roads.add(new Road("losAngeles", "sanDiego", 124));
		Roads.add(new Road("losAngeles", "sanLuisObispo", 182));		
		Roads.add(new Road("medford", "redding", 150));		
		Roads.add(new Road("memphis", "nashville", 210));		
		Roads.add(new Road("miami", "westPalmBeach", 67));		
		Roads.add(new Road("midland", "toledo", 82));		
		Roads.add(new Road("minneapolis", "winnipeg", 463));		
		Roads.add(new Road("modesto", "stockton", 29));		
		Roads.add(new Road("montreal", "ottawa", 132));		
		Roads.add(new Road("newHaven", "providence", 110));
		Roads.add(new Road("newHaven", "stamford", 92));		
		Roads.add(new Road("newOrleans", "pensacola", 268));		
		Roads.add(new Road("newYork", "philadelphia", 101));		
		Roads.add(new Road("norfolk", "richmond", 92));
		Roads.add(new Road("norfolk", "raleigh", 174));		
		Roads.add(new Road("oakland", "sanFrancisco", 8));
		Roads.add(new Road("oakland", "sanJose", 42));		
		Roads.add(new Road("oklahomaCity", "tulsa", 105));		
		Roads.add(new Road("orlando", "westPalmBeach", 168));
		Roads.add(new Road("orlando", "tampa", 84));		
		Roads.add(new Road("ottawa", "toronto", 269));		
		Roads.add(new Road("pensacola", "tallahassee", 120));		
		Roads.add(new Road("philadelphia", "pittsburgh", 319));
		Roads.add(new Road("philadelphia", "newYork", 101));
		Roads.add(new Road("philadelphia", "uk1", 3548));
		Roads.add(new Road("philadelphia", "uk2", 3548));		
		Roads.add(new Road("phoenix", "tucson", 117));
		Roads.add(new Road("phoenix", "yuma", 178));		
		Roads.add(new Road("pointReyes", "redding", 215));
		Roads.add(new Road("pointReyes", "sacramento", 115));		
		Roads.add(new Road("portland", "seattle", 174));
		Roads.add(new Road("portland", "salem", 47));		
		Roads.add(new Road("reno", "saltLakeCity", 520));
		Roads.add(new Road("reno", "sacramento", 133));		
		Roads.add(new Road("richmond", "washington", 105));		
		Roads.add(new Road("sacramento", "sanFrancisco", 95));
		Roads.add(new Road("sacramento", "stockton", 51));		
		Roads.add(new Road("salinas", "sanJose", 31));
		Roads.add(new Road("salinas", "sanLuisObispo", 137));		
		Roads.add(new Road("sanDiego", "yuma", 172));		
		Roads.add(new Road("saultSteMarie", "thunderBay", 442));
		Roads.add(new Road("saultSteMarie", "toronto", 436));		
		Roads.add(new Road("seattle", "vancouver", 115));		
		Roads.add(new Road("thunderBay", "winnipeg", 440));
	}

	/*********************************************************************************************/
	public void initializeCities() {
		// from usroads.pl and cities_list.txt
		Cities.add(new City("albanyGA", 31.58, 84.17));
		Cities.add(new City("albanyNY", 42.66, 73.78));
		Cities.add(new City("albuquerque", 35.11, 106.61));
		Cities.add(new City("atlanta", 33.76, 84.40));
		Cities.add(new City("augusta", 33.43, 82.02));
		Cities.add(new City("austin", 30.30, 97.75));
		Cities.add(new City("bakersfield", 35.36, 119.03));
		Cities.add(new City("baltimore", 39.31, 76.62));
		Cities.add(new City("batonRouge", 30.46, 91.14));
		Cities.add(new City("beaumont", 30.08, 94.13));
		Cities.add(new City("boise", 43.61, 116.24));
		Cities.add(new City("boston", 42.32, 71.09));
		Cities.add(new City("buffalo", 42.90, 78.85));
		Cities.add(new City("calgary", 51.00, 114.00));
		Cities.add(new City("charlotte", 35.21, 80.83));
		Cities.add(new City("chattanooga", 35.05, 85.27));
		Cities.add(new City("chicago", 41.84, 87.68));
		Cities.add(new City("cincinnati", 39.14, 84.50));
		Cities.add(new City("cleveland", 41.48, 81.67));
		Cities.add(new City("coloradoSprings", 38.86, 104.79));
		Cities.add(new City("columbus", 39.99, 82.99));
		Cities.add(new City("dallas", 32.80, 96.79));
		Cities.add(new City("dayton", 39.76, 84.20));
		Cities.add(new City("daytonaBeach", 29.21, 81.04));
		Cities.add(new City("denver", 39.73, 104.97));
		Cities.add(new City("desMoines", 41.59, 93.62));
		Cities.add(new City("elPaso", 31.79, 106.42));
		Cities.add(new City("eugene", 44.06, 123.11));
		Cities.add(new City("europe", 48.87, -2.33));
		Cities.add(new City("ftWorth", 32.74, 97.33));
		Cities.add(new City("fresno", 36.78, 119.79));
		Cities.add(new City("grandJunction", 39.08, 108.56));
		Cities.add(new City("greenBay", 44.51, 88.02));
		Cities.add(new City("greensboro", 36.08, 79.82));
		Cities.add(new City("houston", 29.76, 95.38));
		Cities.add(new City("indianapolis", 39.79, 86.15));
		Cities.add(new City("jacksonville", 30.32, 81.66));
		Cities.add(new City("japan", 35.68, 220.23));
		Cities.add(new City("kansasCity", 39.08, 94.56));
		Cities.add(new City("keyWest", 24.56, 81.78));
		Cities.add(new City("lafayette", 30.21, 92.03));
		Cities.add(new City("lakeCity", 30.19, 82.64));
		Cities.add(new City("laredo", 27.52, 99.49));
		Cities.add(new City("lasVegas", 36.19, 115.22));
		Cities.add(new City("lincoln", 40.81, 96.68));
		Cities.add(new City("littleRock", 34.74, 92.33));
		Cities.add(new City("losAngeles", 34.03, 118.17));
		Cities.add(new City("macon", 32.83, 83.65));
		Cities.add(new City("medford", 42.33, 122.86));
		Cities.add(new City("memphis", 35.12, 89.97));
		Cities.add(new City("mexia", 31.68, 96.48));
		Cities.add(new City("mexico", 19.40, 99.12));
		Cities.add(new City("miami", 25.79, 80.22));
		Cities.add(new City("midland", 43.62, 84.23));
		Cities.add(new City("milwaukee", 43.05, 87.96));
		Cities.add(new City("minneapolis", 44.96, 93.27));
		Cities.add(new City("modesto", 37.66, 120.99));
		Cities.add(new City("montreal", 45.50, 73.67));
		Cities.add(new City("nashville", 36.15, 86.76));
		Cities.add(new City("newHaven", 41.31, 72.92));
		Cities.add(new City("newOrleans", 29.97, 90.06));
		Cities.add(new City("newYork", 40.70, 73.92));
		Cities.add(new City("norfolk", 36.89, 76.26));
		Cities.add(new City("oakland", 37.80, 122.23));
		Cities.add(new City("oklahomaCity", 35.48, 97.53));
		Cities.add(new City("omaha", 41.26, 96.01));
		Cities.add(new City("orlando", 28.53, 81.38));
		Cities.add(new City("ottawa", 45.42, 75.69));
		Cities.add(new City("pensacola", 30.44, 87.21));
		Cities.add(new City("philadelphia", 40.72, 76.12));
		Cities.add(new City("phoenix", 33.53, 112.08));
		Cities.add(new City("pittsburgh", 40.40, 79.84));
		Cities.add(new City("pointReyes", 38.07, 122.81));
		Cities.add(new City("portland", 45.52, 122.64));
		Cities.add(new City("providence", 41.80, 71.36));
		Cities.add(new City("provo", 40.24, 111.66));
		Cities.add(new City("raleigh", 35.82, 78.64));
		Cities.add(new City("redding", 40.58, 122.37));
		Cities.add(new City("reno", 39.53, 119.82));
		Cities.add(new City("richmond", 37.54, 77.46));
		Cities.add(new City("rochester", 43.17, 77.61));
		Cities.add(new City("sacramento", 38.56, 121.47));
		Cities.add(new City("salem", 44.93, 123.03));
		Cities.add(new City("salinas", 36.68, 121.64));
		Cities.add(new City("saltLakeCity", 40.75, 111.89));
		Cities.add(new City("sanAntonio", 29.45, 98.51));
		Cities.add(new City("sanDiego", 32.78, 117.15));
		Cities.add(new City("sanFrancisco", 37.76, 122.44));
		Cities.add(new City("sanJose", 37.30, 121.87));
		Cities.add(new City("sanLuisObispo", 35.27, 120.66));
		Cities.add(new City("santaFe", 35.67, 105.96));
		Cities.add(new City("saultSteMarie", 46.49, 84.35));
		Cities.add(new City("savannah", 32.05, 81.10));
		Cities.add(new City("seattle", 47.63, 122.33));
		Cities.add(new City("stLouis", 38.63, 90.24));
		Cities.add(new City("stamford", 41.07, 73.54));
		Cities.add(new City("stockton", 37.98, 121.30));
		Cities.add(new City("tallahassee", 30.45, 84.27));
		Cities.add(new City("tampa", 27.97, 82.46));
		Cities.add(new City("thunderBay", 48.38, 89.25));
		Cities.add(new City("toledo", 41.67, 83.58));
		Cities.add(new City("toronto", 43.65, 79.38));
		Cities.add(new City("tucson", 32.21, 110.92));
		Cities.add(new City("tulsa", 36.13, 95.94));
		Cities.add(new City("uk1", 51.30, 0.00));
		Cities.add(new City("uk2", 51.30, 0.00));
		Cities.add(new City("vancouver", 49.25, 123.10));
		Cities.add(new City("washington", 38.91, 77.01));
		Cities.add(new City("westPalmBeach", 26.71, 80.05));
		Cities.add(new City("wichita", 37.69, 97.34));
		Cities.add(new City("winnipeg", 49.90, 97.13));
		Cities.add(new City("yuma", 32.69, 114.62));
	}

} // end SearchUSA
