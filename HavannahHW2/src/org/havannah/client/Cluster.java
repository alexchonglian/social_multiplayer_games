package org.havannah.client;

public class Cluster {
	private static int idAccumulator = 0;//make sure unique id
	private int id; 
	private int numCorner;
	private int numSide;
	
	public boolean equals(Cluster c) {
		return this.id == c.id && this.numCorner == c.numCorner && this.numSide == c.numSide;
	}
	
	// Getters and Setters
	public int getId() {
		return id;
	}

	public int getNumCorner() {
		return numCorner;
	}

	public int getNumSide() {
		return numSide;
	}

	public void addCornerAndSide(int extraNumCorner, int extraNumSide) {
		this.numCorner += extraNumCorner;
		this.numSide += extraNumSide;
	}
	
	
	// Contructors
	public Cluster() {
		this(0, 0);
	}
	
	public Cluster (int numCorner, int numSide) {
		this.id = Cluster.idAccumulator++;
		this.numCorner = numCorner;
		this.numSide = numSide;
	}
	
	/*---------------------------------------------------------------------
	 * Illustration of cluster merge
	 *---------------------------------------------------------------------
	 * X is new added point;
	 * A B are cluster labels of 2 neighbors that are held by player;
	 * C is newly created cluster label
	 * 
	 *  A 0      C 0
	 * 0 X B => 0 X C
	 *  0 0      0 0
	 *  
	 *---------------------------------------------------------------------
	 * X is new added point;
	 * A B C are cluster labels of 3 neighbors that are held by player;
	 * C is newly created cluster label
	 * 
	 *  A 0      D 0
	 * 0 X B => 0 X D
	 *  C 0      D 0
	 *  
	 *---------------------------------------------------------------------
	 */
	public Cluster merge(Cluster c) {// merge 2 cluster labels
		int totalNumCorner = this.numCorner + c.numCorner;
		int totalNumSide = this.numSide + c.numSide;
		return new Cluster(totalNumCorner, totalNumSide);
	}
	
	public Cluster merge(Cluster c1, Cluster c2) {// merge 3 cluster labels
		int totalNumCorner = this.numCorner + c1.numCorner + c2.numCorner;
		int totalNumSide = this.numSide + c1.numSide + c2.numSide;
		return new Cluster(totalNumCorner, totalNumSide);
	}
	
}