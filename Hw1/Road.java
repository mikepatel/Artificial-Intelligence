/* Michael Patel
 * mrpatel5
 * CSC 520
 * Homework 1
 * Fall 2018
 * 
 * Road user-defined object
 * 		- source city name
 * 		- destination city name
 * 		- cost/weight/distance
 */
public class Road {
	String srccityname;
	String destcityname;
	double cost;
	
	/*********************************************************************************************/
	// Constructor
	public Road(String srccityname, String destcityname, double cost) {
		this.srccityname = srccityname;
		this.destcityname = destcityname;
		this.cost = cost;
	}
}