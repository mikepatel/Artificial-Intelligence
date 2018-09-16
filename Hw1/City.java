/* Michael Patel
 * mrpatel5
 * CSC 520
 * Homework 1
 * Fall 2018
 * 
 * City user-defined object:
 * 		- city name
 * 		- latitude North coordinate of city
 * 		- longitude West coordinate of city 
 */
public class City {
	String name;
	double latitude;
	double longitude;
	
	/*********************************************************************************************/
	// Constructor
	public City(String name, double latitude, double longitude) {
		this.name = name;
		this.latitude = latitude;
		this.longitude = longitude;
	}
}