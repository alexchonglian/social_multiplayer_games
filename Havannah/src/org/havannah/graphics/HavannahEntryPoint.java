package org.havannah.graphics;

import java.util.ArrayList;

import org.havannah.client.GameApi;
import org.havannah.client.GameApi.Game;
import org.havannah.client.GameApi.IteratingPlayerContainer;
import org.havannah.client.GameApi.UpdateUI;
import org.havannah.client.GameApi.VerifyMove;
import org.havannah.client.GameApi.VerifyMoveDone;

import org.havannah.client.HavannahPresenter;
import org.havannah.client.HavannahLogic;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;

public class HavannahEntryPoint implements EntryPoint {
	IteratingPlayerContainer container;
	HavannahPresenter havannahPresenter;
	int playerId = 0;
	boolean canMakeMove = true;

	@Override
	public void onModuleLoad() {
		
		Game game = new Game() {

			@Override
			public void sendVerifyMove(VerifyMove verifyMove) {
				//TODO
				container.sendVerifyMoveDone(null);
			}

			@Override
			public void sendUpdateUI(UpdateUI updateUI) {
				havannahPresenter.updateUI(updateUI);
			}
			
		};
		
		
		container = new IteratingPlayerContainer(game, 2);
	    HavannahGraphics havannahGraphics = new HavannahGraphics();
	    havannahPresenter = new HavannahPresenter(havannahGraphics, container);
	    
	    
	    
	    
	    final String currentRedPlayer = "Red player making move";
	    final String currentBluePlayer = "Blue player making move";
	    final String currentViewer = "Viewer can't make move";
	    
	    final Label currentRedLabel = new Label(currentRedPlayer);
	    final Label currentBlueLabel = new Label(currentBluePlayer);
	    final Label currentViewerLabel = new Label(currentViewer);
		
		final ListBox playerSelect = new ListBox();
	    playerSelect.addItem("RedPlayer");
	    playerSelect.addItem("BluePlayer");
	    playerSelect.addItem("Viewer");
	    playerSelect.addChangeHandler(new ChangeHandler() {
	      @Override
	      public void onChange(ChangeEvent event) {
	        int selectedIndex = playerSelect.getSelectedIndex();
	        if (playerId != selectedIndex) {
	        	canMakeMove= true;
	        }
	        playerId = selectedIndex;
//	        int playerId = selectedIndex == 2 ? GameApi.VIEWER_ID
//	            : container.getPlayerIds().get(selectedIndex);
//	        container.updateUi(playerId);
	      }
	    });
	    
	    //RootPanel.get("bottom").add(playerSelect);
	    
	    RootPanel.get("bottom").add(new Button("Red", new ClickHandler() {
			public void onClick(ClickEvent event) {
				if (playerId != 0) {
		        	canMakeMove= true;
		        }
				playerId = 0;
				RootPanel.get("prompt").clear(true);
				RootPanel.get("prompt").add(currentRedLabel);
			}
		}));
	    
	    RootPanel.get("bottom").add(new Button("Blue", new ClickHandler() {
			public void onClick(ClickEvent event) {
				if (playerId != 1) {
		        	canMakeMove= true;
		        }
				playerId = 1;
				RootPanel.get("prompt").clear(true);
				RootPanel.get("prompt").add(currentBlueLabel);
			}
		}));
	    
	    RootPanel.get("bottom").add(new Button("Viewer", new ClickHandler() {
			public void onClick(ClickEvent event) {
				canMakeMove = false;
				playerId = 2;
				RootPanel.get("prompt").clear(true);
				RootPanel.get("prompt").add(currentViewerLabel);
			}
		}));
	    

	    
	    RootPanel.get("prompt").add(currentRedLabel);
	    
	    
	    String[] pieces = {"a1", "a2", "a3", "a4", "a5",
	    		"b1", "b2", "b3", "b4", "b5", "b6",
	    		"c1", "c2", "c3", "c4", "c5", "c6", "c7",
	    		"d1", "d2", "d3", "d4", "d5", "d6", "d7", "d8",
	    		"e1", "e2", "e3", "e4", "e5", "e6", "e7", "e8", "e9",
	    		"f1", "f2", "f3", "f4", "f5", "f6", "f7", "f8",
	    		"g1", "g2", "g3", "g4", "g5", "g6", "g7",
	    		"h1", "h2", "h3", "h4", "h5", "h6",
	    		"i1", "i2", "i3", "i4", "i5"};
	    
	    for (String pieceStr: pieces) {
	    	final String piece = pieceStr;
			RootPanel.get(pieceStr).sinkEvents(Event.ONCLICK);
			RootPanel.get(pieceStr).addHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					
					if (playerId == 0 && canMakeMove) {// Red
						RootPanel.get(piece).clear(true);
						RootPanel.get(piece).add(new Image("assets/redx.gif"));
						canMakeMove = false;
					} else if (playerId == 1 && canMakeMove) {// Blue
						RootPanel.get(piece).clear(true);
						RootPanel.get(piece).add(new Image("assets/bluex.gif"));
						canMakeMove = false;
					}
				}}, ClickEvent.getType());
	    }
	    

		
		
		
		Grid g = new Grid(5, 5);

	    // Put some values in the grid cells.
	    for (int row = 0; row < 5; ++row) {
	      for (int col = 0; col < 5; ++col)
	        g.setText(row, col, "" + row + ", " + col);
	    }

	    // Just for good measure, let's put a button in the center.
	    g.setWidget(2, 2, new Button("Does nothing, but could"));

	    // You can use the CellFormatter to affect the layout of the grid's cells.
	    g.getCellFormatter().setWidth(0, 2, "256px");

	    //RootPanel.get("top").add(g);
	}

}
