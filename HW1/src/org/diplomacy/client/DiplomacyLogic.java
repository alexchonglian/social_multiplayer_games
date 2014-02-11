package org.diplomacy.client;

import java.util.*;
import org.diplomacy.client.GameApi.*;



class Region {
	public final static int SEA = 0;
	public final static int LAND = 1;
	
	String name;
	int type;
	boolean isSourceCenter;
	Troop holder;
	Region[] adjacentRegions;
	
	public boolean isSea() {
		return type == 0;
	}
	
	public boolean isLand() {
		return type == 1;
	}
	
	public Region(String name, int type, boolean isSourceCenter) {
		super();
		this.name = name;
		this.type = type;
		this.isSourceCenter = isSourceCenter;
	}

	public Region(String name, int type, boolean isSourceCenter, Troop holder) {
		super();
		this.name = name;
		this.type = type;
		this.isSourceCenter = isSourceCenter;
		this.holder = holder;
	}
	
}

class Troop {
	Nation nation;
}

class Army extends Troop {
	Army() {}
	void hold() {return;}
	void attack() {return;}
	void support() {return;}
}

class Fleet extends Troop {
	Fleet() {}
	void hold() {return;}
	void attack() {return;}
	void support() {return;}
	void convoy() {return;}
}

class Nation {
	
	Nation(String name) {
		this.name = name;
		this.troops = new ArrayList<Troop>();
	}
	
	String name = null;
	
	ArrayList<Troop> troops = null;
	
	void initializeTroops() {
		switch(name){
		case "Austria": //create Troops based on number of Army and Fleet
			break;
		case "England": // similar stuff
			break;
		case "Germany": 
			break;
		case "Turkey":
			break;
		case "Italy":
			break;
		case "Russia":
			break;
		case "France":
			break;
		default:
			break;
		}
	}
}

class Order {}
class Retreat {}
class Adjustment {}

class Judge {}



public class DiplomacyLogic {
	// 1. Initialize Nations
	// 2. Initialize Map and Regions
	// 3. Define adjacency relation between Regions
	// 4. Each Nation create and assign troops on EUmap
	
	public Nation[] nations = {
			// I will change data structure if needed
			// I will abstract it out to a new method if needed
			new Nation("Austria"),
			new Nation("Turkey"),
			new Nation("Italy"),
			new Nation("France"),
			new Nation("England"),
			new Nation("Germany"),
			new Nation("Russia")
	};
	
	public Region[] EuropeMap = {
			// will change data structure if needed
			// will abstract it out to a new method if needed
			// I think Map is more suitable
			// because it need to support query from name to object
			// will fix it later
			
			//create 19 seas
			new Region("North Atlantic", 0, false),
			new Region("Irish Sea", 0, false),
			new Region("English Channel", 0, false),
			new Region("Mid Atlantic", 0, false),
			new Region("West Med.", 0, false),
			new Region("Gulf of Lyon", 0, false),
			new Region("Tyrbennian Sea", 0, false),
			new Region("Ionian Sea", 0, false),
			new Region("Adriatic Sea", 0, false),
			new Region("Aegean Sea", 0, false),
			new Region("Black Sea", 0, false),
			new Region("East Med.", 0, false),
			new Region("Baltic Sea", 0, false),
			new Region("Gulf of Bothnia", 0, false),
			new Region("Skagerrak", 0, false),
			new Region("North Sea", 0, false),
			new Region("Helgoland Bight", 0, false),
			new Region("Norwegian Sea", 0, false),
			new Region("Barents Sea", 0, false),
			
			//create 56 lands
			new Region("Clyde", 1, false),
			new Region("Edinburge", 1, true),
			new Region("York", 1, false),
			new Region("London", 1, true),
			new Region("Liverpool", 1, true),
			new Region("Wales", 1, false),
			new Region("Brest", 1, true),
			new Region("Gascomy", 1, false),
			new Region("Marseilles", 1, true),
			new Region("Picardy", 1, false),
			new Region("Paris", 1, true),
			new Region("Burgundy", 1, false),
			new Region("Portugal", 1, true),
			new Region("Spain", 1, true),
			new Region("North Africa", 1, false),
			new Region("Tunisia", 1, true),
			new Region("Napoli", 1, true),
			new Region("Roma", 1, true),
			new Region("Tuscany", 1, false),
			new Region("Apulia", 1, false),
			new Region("Venezia", 1, true),
			new Region("Piemonte", 1, false),
			new Region("Belgium", 1, true),
			new Region("Holland", 1, true),
			new Region("Ruhr", 1, false),
			new Region("Munich", 1, false),
			new Region("Kiel", 1, true),
			new Region("Berlin", 1, true),
			new Region("Silesia", 1, false),
			new Region("Prussia", 1, false),
			new Region("Tyrolia", 1, false),
			new Region("Trieste", 1, true),
			new Region("Vienna", 1, true),
			new Region("Bohemia", 1, false),
			new Region("Galicia", 1, false),
			new Region("Budapest", 1, true),
			new Region("Serbia", 1, true),
			new Region("Albania", 1, false),
			new Region("Greece", 1, true),
			new Region("Bulgaria", 1, true),
			new Region("Rumania", 1, true),
			new Region("Constantinople", 1, true),
			new Region("Ankara", 1, true),
			new Region("Smyrna", 1, true),
			new Region("Syria", 1, false),
			new Region("Armenia", 1, false),
			new Region("Stevastopol", 1, true),
			new Region("Ukraine", 1, false),
			new Region("Warsaw", 1, true),
			new Region("Livonia", 1, false),
			new Region("Moscow", 1, true),
			new Region("Saint Petersburg", 1, true),
			new Region("Finland", 1, false),
			new Region("Norway", 1, true),
			new Region("Sweden", 1, true),
			new Region("Denmark", 1, true),
			
	};// will change data structure if needed
	
	int countLand() {
		return 0;
	}
	
	int countSea() {
		return 0;
	}
	
	int countSourceCenter () {
		return 0;
	}
	
	
	public void initializeAdjacencyList() {
		for(int i=0; i < EuropeMap.length; i++) {
			//add adjacent Regions to their list
		}
	}
	public VerifyMoveDone verify(VerifyMove verifyMove) {
		return new VerifyMoveDone();
	}

	public void checkMoveIsLegal(VerifyMove verifyMove) {
		return;
	}
	
	public static void main(String args[]) {
		System.out.println("ko");
	}
}