package org.diplomacy.client;

import java.util.*;
import org.diplomacy.client.GameApi.*;


class Region {
	public final static int SEA = 0;
	public final static int LAND = 1;
	
	String name;
	String alias;
	int type;
	boolean isSourceCenter;
	Troop holder;
	List<Region> adjacentRegions;
	
	public boolean isSea() {
		return type == SEA;
	}
	
	public boolean isLand() {
		return type == LAND;
	}
	
	public Region(String name, String alias, int type, boolean isSourceCenter) {
		super();
		this.name = name;
		this.alias = alias;
		this.type = type;
		this.isSourceCenter = isSourceCenter;
	}

	public Region(String name, String alias, int type, boolean isSourceCenter, Troop holder) {
		super();
		this.name = name;
		this.alias = alias;
		this.type = type;
		this.isSourceCenter = isSourceCenter;
		this.holder = holder;
	}
	
	private void setAdjacentRegions(List<Region> adjacentRegions) {
		this.adjacentRegions = adjacentRegions;
	}
}

class Move {
	Region from;
	Region to;
}
class Hold {}

class Attack {}

class Support {}

class Convoy {}


class Troop {
	Nation nation;
	Region on;
}

class Army extends Troop {
	Army() {}
	void hold() {return;}
	void move() {return;}
	void support() {return;}
}

class Fleet extends Troop {
	Fleet() {}
	Hold hold() {return new Hold();}
	void move() {return;}
	void support() {return;}
	void convoy() {return;}
}

class Nation {
	
	Nation(String name) {
		this.name = name;
		this.troops = new ArrayList<Troop>();
		initializeTroops();
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

class Austria extends Nation {

	Austria(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}
}

class Turkey extends Nation {

	Turkey(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}
	
}

class Italy extends Nation {

	Italy(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}
	
}

class France extends Nation {

	France(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}
	
}

class England extends Nation {

	England(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}
	
}

class Germany extends Nation {

	Germany(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}
	
}

class Russia extends Nation {

	Russia(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}
	
}

class Order {}
class Retreat {}
class Adjustment {}

class Judge {}



public class DiplomacyLogic {
	
	List<Region> regions;
	List<Region> seas;
	List<Region> lands;
	
	Map<String, Region> regionHash;
	Map<String, Object> connectivity;
	
	Nation[] nations;
	
	Region NAO, IRI, ENG, MAO, WES, 
	LYO, TYS, ION, ADR, AEG, 
	BLA, EAS, BAL, BOT, SKA, 
	NTH, HEL, NWG, BAR, Cly, 
	Edi, Yor, Lon, Lvp, Wal, 
	Bre, Gas, Mar, Pic, Par, 
	Bur, Por, Spa, Naf, Tun, 
	Nap, Rom, Tus, Apu, Ven, 
	Pie, Bel, Hol, Ruh, Mun, 
	Kie, Ber, Sil, Pru, Tyr, 
	Tri, Vie, Boh, Gal, Bud, 
	Ser, Alb, Gre, Bul, Rum, 
	Con, Ank, Smy, Syr, Arm, 
	Sev, Ukr, War, Lvn, Mos, 
	Stp, Fin, Nwy, Swe, Den;
	

	
	public DiplomacyLogic() {
		this.createRegions(); //create 75 Region as instance variables
		this.addRegions(); // add reference to regions[]
		this.addSeas(); //add reference to seas[]
		this.addLands(); //add reference to lands[]
		this.regionHash = this.createRegionHash();
	}



	private void createRegions() {
		// Regions as instance variable in DiplomacyLogic
		// create 19 seas
		NAO = new Region("North Atlantic", "NAO", 0, false);
		IRI = new Region("Irish Sea", "IRI", 0, false);
		ENG = new Region("English Channel","ENG", 0, false);
		MAO = new Region("Mid Atlantic","MAO", 0, false);
		WES = new Region("West Med.","WES", 0, false);
		LYO = new Region("Gulf of Lyon", "LYO",0, false);
		TYS = new Region("Tyrbennian Sea", "TYS", 0, false);
		ION = new Region("Ionian Sea", "ION", 0, false);
		ADR = new Region("Adriatic Sea", "ADR", 0, false);
		AEG = new Region("Aegean Sea", "AEG", 0, false);
		BLA = new Region("Black Sea", "BLA", 0, false);
		EAS = new Region("East Med.", "EAS", 0, false);
		BAL = new Region("Baltic Sea", "BAL", 0, false);
		BOT = new Region("Gulf of Bothnia", "BOT", 0, false);
		SKA = new Region("Skagerrak", "SKA", 0, false);
		NTH = new Region("North Sea", "NTH", 0, false);
		HEL = new Region("Helgoland Bight", "HEL", 0, false);
		NWG = new Region("Norwegian Sea", "NWG", 0, false);
		BAR = new Region("Barents Sea", "BAR", 0, false);
		
		//create 56 lands
		Cly = new Region("Clyde", "Cly", 1, false);
		Edi = new Region("Edinburge", "Edi", 1, true);
		Yor = new Region("York", "Yor", 1, false);
		Lon = new Region("London", "Lon", 1, true);
		Lvp = new Region("Liverpool", "Lvp", 1, true);
		Wal = new Region("Wales", "Wal", 1, false);
		Bre = new Region("Brest", "Bre", 1, true);
		Gas = new Region("Gascomy", "Gas", 1, false);
		Mar = new Region("Marseilles", "Mar", 1, true);
		Pic = new Region("Picardy", "Pic", 1, false);
		Par = new Region("Paris", "Par", 1, true);
		Bur = new Region("Burgundy", "Bur", 1, false);
		Por = new Region("Portugal", "Por", 1, true);
		Spa = new Region("Spain", "Spa", 1, true);
		Naf = new Region("North Africa", "Naf", 1, false);
		Tun = new Region("Tunis", "Tun", 1, true);
		Nap = new Region("Naples", "Nap", 1, true);
		Rom = new Region("Rome", "Rom", 1, true);
		Tus = new Region("Tuscany", "Tus", 1, false);
		Apu = new Region("Apulia", "Apu", 1, false);
		Ven = new Region("Venice", "Ven", 1, true);
		Pie = new Region("Piemonte", "Pie", 1, false);
		Bel = new Region("Belgium", "Bel", 1, true);
		Hol = new Region("Holland", "Hol", 1, true);
		Ruh = new Region("Ruhr", "Ruh", 1, false);
		Mun = new Region("Munich", "Mun", 1, false);
		Kie = new Region("Kiel", "Kie", 1, true);
		Ber = new Region("Berlin", "Ber", 1, true);
		Sil = new Region("Silesia", "Sil", 1, false);
		Pru = new Region("Prussia", "Pru", 1, false);
		Tyr = new Region("Tyrolia", "Tyr", 1, false);
		Tri = new Region("Trieste", "Tri", 1, true);
		Vie = new Region("Vienna", "Vie", 1, true);
		Boh = new Region("Bohemia", "Boh", 1, false);
		Gal = new Region("Galicia", "Gal", 1, false);
		Bud = new Region("Budapest", "Bud", 1, true);
		Ser = new Region("Serbia", "Ser", 1, true);
		Alb = new Region("Albania", "Alb", 1, false);
		Gre = new Region("Greece", "Gre", 1, true);
		Bul = new Region("Bulgaria", "Bul", 1, true);
		Rum = new Region("Rumania", "Rum", 1, true);
		Con = new Region("Constantinople", "Con", 1, true);
		Ank = new Region("Ankara", "Ank", 1, true);
		Smy = new Region("Smyrna", "Smy", 1, true);
		Syr = new Region("Syria", "Syr", 1, false);
		Arm = new Region("Armenia", "Arm", 1, false);
		Sev = new Region("Sevastopol", "Sev", 1, true);
		Ukr = new Region("Ukraine", "Ukr", 1, false);
		War = new Region("Warsaw", "War", 1, true);
		Lvn = new Region("Livonia", "Lvn", 1, false);
		Mos = new Region("Moscow", "Mos", 1, true);
		Stp = new Region("Saint Petersburg", "Stp", 1, true);
		Fin = new Region("Finland", "Fin", 1, false);
		Nwy = new Region("Norway", "Nwy", 1, true);
		Swe = new Region("Sweden", "Swe", 1, true);
		Den = new Region("Denmark", "Den", 1, true);
		
	}

	private void addRegions() {
		Region[] allRegions = {
			NAO, IRI, ENG, MAO, WES, 
			LYO, TYS, ION, ADR, AEG, 
			BLA, EAS, BAL, BOT, SKA, 
			NTH, HEL, NWG, BAR, Cly, 
			Edi, Yor, Lon, Lvp, Wal, 
			Bre, Gas, Mar, Pic, Par, 
			Bur, Por, Spa, Naf, Tun, 
			Nap, Rom, Tus, Apu, Ven, 
			Pie, Bel, Hol, Ruh, Mun, 
			Kie, Ber, Sil, Pru, Tyr, 
			Tri, Vie, Boh, Gal, Bud, 
			Ser, Alb, Gre, Bul, Rum, 
			Con, Ank, Smy, Syr, Arm, 
			Sev, Ukr, War, Lvn, Mos, 
			Stp, Fin, Nwy, Swe, Den
		};
		this.regions = new ArrayList<Region>(Arrays.asList(allRegions));
	}
	
	private void addSeas() {
		Region[] allSeas = {
			NAO, IRI, ENG, MAO, WES, 
			LYO, TYS, ION, ADR, AEG, 
			BLA, EAS, BAL, BOT, SKA, 
			NTH, HEL, NWG, BAR
		};
		this.seas = new ArrayList<Region>(Arrays.asList(allSeas));
	}
	
	private void addLands() {
		Region[] allLands = {Cly, 
			Edi, Yor, Lon, Lvp, Wal, 
			Bre, Gas, Mar, Pic, Par, 
			Bur, Por, Spa, Naf, Tun, 
			Nap, Rom, Tus, Apu, Ven, 
			Pie, Bel, Hol, Ruh, Mun, 
			Kie, Ber, Sil, Pru, Tyr, 
			Tri, Vie, Boh, Gal, Bud, 
			Ser, Alb, Gre, Bul, Rum, 
			Con, Ank, Smy, Syr, Arm, 
			Sev, Ukr, War, Lvn, Mos, 
			Stp, Fin, Nwy, Swe, Den
		};
		this.lands = new ArrayList<Region>(Arrays.asList(allLands));
	}
	
	private Map<String, Region> createRegionHash() {
		
		HashMap<String, Region> tempRegionHash = new HashMap<String, Region>();
		
		for (Region r: this.regions) {
			tempRegionHash.put(r.alias, r);
		}
		
		return tempRegionHash;
	}
	
	private void createAdjacencyMap() {
		Region[][][] connectivityPatternIn3dArray = {//75 x 2
			{
			{NAO}, {NWG, Cly, Lvp, IRI, MAO
			}},

			{ 
			{IRI}, {NAO, Wal, Lvp, ENG, MAO
			}},

			{ 
			{ENG}, {MAO, IRI, Wal, Lon, NTH, Bel, Pic, Bre
			}},

			{ 
			{MAO}, {NAO, IRI, ENG, Bre, Gas, Spa, Por, WES
			}},

			{ 
			{WES}, {Spa, LYO, TYS, Tun, Naf, MAO
			}},

			{ 
			{LYO}, {WES, Spa, Mar, Pie, Tus, TYS
			}},

			{ 
			{TYS}, {LYO, Tus, Rom, Nap, ION, Tun 
			}},

			{ 
			{ION}, {Tun, TYS, Nap, Apu, ADR, Alb, Gre, AEG, EAS
			}},

			{ 
			{ADR}, {Apu, Ven, Tri, Alb, ION
			}},

			{ 
			{AEG}, {BLA, Bul, Con, Smy, EAS, ION, Gre
			}},

			{ 
			{BLA}, {Sev, Arm, Ank, Con, Bul, Rum, AEG
			}},

			{ 
			{EAS}, {ION, AEG, Smy, Syr
			}},

			{ 
			{BAL}, {Kie, Den, SKA, Swe, BOT, Lvn, Pru, Ber
			}},

			{ 
			{BOT}, {BAL, Swe, Fin, Stp, Lvn
			}},

			{ 
			{SKA}, {Nwy, Swe, BAL, Den, NTH
			}},

			{ 
			{NTH}, {ENG, Lon, Yor, Edi, NWG, Nwy, SKA, Den, HEL, Hol, Bel
			}},

			{ 
			{HEL}, {NTH, Den, Kie, Hol
			}},

			{ 
			{NWG}, {NAO, BAR, Nwy, NTH, Edi, Cly
			}},

			{ 
			{BAR}, {NWG, Nwy, Stp
			}},

			{ 
			{Cly}, {NAO, NWG, Edi, Lvp, IRI
			}},

			{ 
			{Edi}, {Cly, NWG, NTH, Yor, Lvp
			}},

			{ 
			{Yor}, {Lon, Wal, Lvp, Edi, NTH
			}},

			{ 
			{Lon}, {ENG, Wal, Yor, NTH
			}},

			{ 
			{Lvp}, {IRI, NAO, Cly, Edi, Yor, Wal
			}},

			{ 
			{Wal}, {ENG, IRI, Lvp, Yor, Lon
			}},

			{ 
			{Bre}, {MAO, ENG, Pic, Par, Gas
			}},

			{ 
			{Gas}, {MAO, Bre, Par, Bur, Mar
			}},

			{ 
			{Mar}, {Spa, Gas, Bur, Pie, LYO
			}},

			{ 
			{Pic}, {ENG, Bel, Bur, Gas, Bre
			}},

			{ 
			{Par}, {Gas, Bre, Pic, Bur
			}},

			{ 
			{Bur}, {Par, Pic, Bel, Ruh, Mun, Mar
			}},

			{ 
			{Por}, {MAO, Spa
			}},

			{ 
			{Spa}, {MAO, Por, WES
			}},

			{ 
			{Naf}, {MAO, WES, Tun
			}},

			{ 
			{Tun}, {Naf, WES, TYS, ION
			}},

			{ 
			{Nap}, {Rom, Apu, ION, TYS
			}},

			{ 
			{Rom}, {Tus, Ven, Apu, Nap, TYS
			}},

			{ 
			{Tus}, {Pie, Ven, Rom, LYO, TYS
			}},

			{ 
			{Apu}, {Nap, Rom, Ven, ADR, ION
			}},

			{ 
			{Ven}, {Pie, Tyr, Tri, ADR, Apu, Rom, Tus
			}},

			{ 
			{Pie}, {Mar, Tyr, Ven, Tus, LYO
			}},

			{ 
			{Bel}, {ENG, NTH, Hol, Ruh, Bur, Pic
			}},

			{ 
			{Hol}, {NTH, HEL, Kie, Ruh, Bel
			}},

			{ 
			{Ruh}, {Bel, Hol, Kie, Mun, Bur
			}},

			{ 
			{Mun}, {Bur, Ruh, Kie, Sil, Boh, Tyr
			}},

			{ 
			{Kie}, {HEL, Den, BAL, Ber, Mun, Ruh, Hol
			}},

			{ 
			{Ber}, {BAL, Pru, Sil, Mun, Kie
			}},

			{ 
			{Sil}, {Ber, Pru, War, Gal, Boh, 
			}},

			{ 
			{Pru}, {BAL, Lvn, War, Sil, Ber
			}},

			{ 
			{Tyr}, {Mun, Boh, Vie, Tri, Ven, Pie
			}},

			{ 
			{Tri}, {Ven, Tyr, Vie, Bud, Ser, Alb, ADR
			}},

			{ 
			{Vie}, {Tyr, Boh, Gal, Bud, Tri
			}},

			{ 
			{Boh}, {Mun, Sil, Gal, Vie, Tyr
			}},

			{ 
			{Gal}, {Sil, War, Ukr
			}},

			{ 
			{Bud}, {Tri, Vie, Gal, Rum, Ser
			}},

			{ 
			{Ser}, {Tri, Bud, Rum, Bul, Gre, Alb
			}},

			{ 
			{Alb}, {ADR, Tri, Ser, Gre, ION
			}},

			{ 
			{Gre}, {ION, Alb, Ser, Bul, AEG
			}},

			{ 
			{Bul}, {Gre, Ser, Rum, BLA, Con, AEG
			}},

			{ 
			{Rum}, {Bul, Ser, Bud, Gal, Ukr, Sev, BLA
			}},

			{ 
			{Con}, {Bul, BLA, Ank, Smy, AEG
			}},

			{ 
			{Ank}, {BLA, Arm, Smy, Con
			}},

			{ 
			{Smy}, {AEG, Con, Ank, Arm, Syr, EAS
			}},

			{ 
			{Syr}, {Smy, Arm, EAS
			}},

			{ 
			{Arm}, {BLA, Sev, Syr, Smy, Ank
			}},

			{ 
			{Sev}, {Rum, Ukr, Mos, Arm, Ank, BLA
			}},

			{ 
			{Ukr}, {Gal, War, Mos, Ser, Rum
			}},

			{ 
			{War}, {Sil, Pru, Lvn, Mos, Ukr, Gal, 
			}},

			{ 
			{Lvn}, {BAL, BOT, Stp, Mos, War, Pru
			}},

			{ 
			{Mos}, {Lvn, Stp, Ser, Ukr, War
			}},

			{ 
			{Stp}, {BOT, Fin, BAR, Mos, Lvn
			}},

			{ 
			{Fin}, {BOT, Swe, Nwy, Stp
			}},

			{ 
			{Nwy}, {NTH, NWG, BAR, Stp, Fin, Swe, SKA
			}},

			{ 
			{Swe}, {SKA, Nwy, Fin, BOT, BAL
			}},

			{ 
			{Den}, {HEL, NTH, SKA, BAL, Kie
			}}
		};
		for(int i=0; i < this.regions.size(); i++) {
			//add adjacent Regions to their list
		}
		
	}
	
	private Nation[] createNations() {
		Nation[] nations = {
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
		return nations;
	}


	int countLand() {
		return 0;
	}
	
	int countSea() {
		return 0;
	}
	
	int countSourceCenter () {
		return 0;
	}
	
	

	public VerifyMoveDone verify(VerifyMove verifyMove) {
		return new VerifyMoveDone();
	}

	public void checkMoveIsLegal(VerifyMove verifyMove) {
		return;
	}
	
	public static void main(String args[]) {
		DiplomacyLogic d = new DiplomacyLogic();
		Region[] rs = d.regions;
		List<String> regionShort = new ArrayList<>();
		for (int i=0; i<rs.length; i++) {
			regionShort.add(rs[i].name.substring(0, 3));
		}
		
		HashSet<String> setRegions = new HashSet<String>(regionShort);
		
		if (setRegions.size() < regionShort.size()) {
			System.out.println("Duplication");
		}
		
		HashMap<String, Integer> counter = new HashMap<String, Integer>();
		for (int i=0; i<regionShort.size(); i++) {
			
		}
		
		for (int i=0; i<regionShort.size(); i++) {
			System.out.println("'"+regionShort.get(i)+"'," );
		}
		
 	}
}








