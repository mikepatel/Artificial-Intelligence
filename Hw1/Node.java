import java.util.ArrayList;
import java.util.List;

/* Michael Patel
 * mrpatel5
 * CSC 520
 * Homework 1
 * Fall 2018
 * 
 * Node user-defined object
 * 		- path to node (series of city names)
 * 		- cost from start city to current city
 * 		- cost from current city to destination city
 * 
 * Notes:
 * 		https://www.callicoder.com/java-priority-queue/
 */
public class Node implements Comparable<Node>{
	List<String> path = new ArrayList<String>(); // series of names (e.g. "a,b,c,d,e...")
	double costFromStart = 0;
	double estimatedCost = 0;
	
	/*********************************************************************************************/
	@Override
	// Needed because Priority Queue has user-defined objects (i.e. Node)
	public int compareTo(Node n) {
		double t1 = this.costFromStart + this.estimatedCost;
		double t2 = n.costFromStart + n.estimatedCost;		
		
		if(t1 > t2) {
			return 1;
		}
		else if(t1 < t2) {
			return -1;
		}
		else {
			return 0;
		}
	}
}