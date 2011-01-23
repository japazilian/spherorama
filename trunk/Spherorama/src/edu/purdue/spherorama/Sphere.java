package edu.purdue.spherorama;

public class Sphere {
	
	private String [][] tiles;
	private int level;
	private int location;
	
	public Sphere() {
		tiles = new String[5][];
		tiles[0] = new String[1];
		tiles[1] = new String[6];
		tiles[2] = new String[6];
		tiles[3] = new String[6];
		tiles[4] = new String[1];
		level = 2;
		location = 0;
	}
	
	/**
	 * Used to keep track of which location in the sphere we are in
	 * @param direction 0 for up, 1 for right, 2 for down, 3 for left
	 * @return true if that direction is allowed, false otherwise (i.e. can't 
	 * 		   move up if we're already at level 0)
	 */
	public boolean changeLocation(int direction) {
		switch(direction) {
		case 0:
			if (level == 0)
				return false;
			level++;
			if (level == 0)
				location = 0;
			break;
		case 1:
			if (level == 0 || level == 4)
				location = 0;
			else 
				location = (location+1)%6;
			break;
		case 2:
			if (level == 4)
				return false;
			level--;
			if (level == 4)
				location = 0;
			break;
		case 3:
			if (level == 0 || level == 4)
				location = 0;
			else 
				location = (location-1)%6;
			break;
		}
		return true;		
	}
}
