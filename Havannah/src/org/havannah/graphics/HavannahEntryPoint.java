package org.havannah.graphics;

import java.util.ArrayList;

import org.havannah.client.GameApi;
import org.havannah.client.GameApi.IteratingPlayerContainer;
import org.havannah.client.HavannahPresenter;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;

public class HavannahEntryPoint implements EntryPoint {
	IteratingPlayerContainer container;
	HavannahPresenter cheatPresenter;
	

	@Override
	public void onModuleLoad() {
		
		final ListBox playerSelect = new ListBox();
	    playerSelect.addItem("WhitePlayer");
	    playerSelect.addItem("BlackPlayer");
	    playerSelect.addItem("Viewer");
	    playerSelect.addChangeHandler(new ChangeHandler() {
	      @Override
	      public void onChange(ChangeEvent event) {
	        int selectedIndex = playerSelect.getSelectedIndex();
	        int playerId = selectedIndex == 2 ? GameApi.VIEWER_ID
	            : container.getPlayerIds().get(selectedIndex);
	        container.updateUi(playerId);
	      }
	    });
	    
	    RootPanel.get("bottom").add(playerSelect);
	    
		RootPanel.get("a1").sinkEvents(Event.ONCLICK);
		RootPanel.get("a1").addHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				// TODO Auto-generated method stub
				Window.alert("ok?");
				RootPanel.get("a1").clear(true);
				RootPanel.get("a1").add(new Image("assets/redx.gif"));
				
			}}, ClickEvent.getType());
		
		
		
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
