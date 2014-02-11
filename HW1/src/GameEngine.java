import java.util.ArrayList;

public class GameEngine {
	
	enum GameStage {SPRING_ORDER, SPRING_MOVE, SPRING_RESOLVE, SPRING_RETREAT, 
					FALL_ORDER, FALL_MOVE, FALL_RESOLVE, FALL_RETREAT, WINTER_ADJUST}
	
	
	GameEngine() {
		
	}
	
	private ArrayList gameMap = new ArrayList();
	
	private boolean initializeMap() {
		return false;
	};
	
	private boolean initializeNations() {
		return false;
	}
	
	public static void main(String[] args) {
		GameEngine gameEngine = new GameEngine();
		
		gameEngine.initializeMap();
		
		gameEngine.initializeNations();
		
		

	}

}
