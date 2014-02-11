import java.util.ArrayList;

public abstract class Region {
	
	private enum Type {SEA, LAND}
	
	private String name;
	
	private Type type;// SEA or LAND
	
	private boolean isSourceCenter;
	
	private ArrayList adjacentRegions;
	
	private void setAdjacentRegions(ArrayList adjacentRegions) {
		this.adjacentRegions = adjacentRegions;
	}
	
	Region(String name, Type type, boolean isSourceCenter) {
		this.name = name;
		this.type = type;
		this.isSourceCenter = isSourceCenter;
	}
	
	Region(String name, Type type, boolean isSourceCenter, ArrayList adjacent) {
		this(name, type, isSourceCenter);
		this.adjacentRegions = adjacent;
	}
}
