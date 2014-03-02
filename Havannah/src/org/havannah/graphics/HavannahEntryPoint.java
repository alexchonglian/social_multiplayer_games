package org.havannah.graphics;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.RootPanel;

public class HavannahEntryPoint implements EntryPoint {

	@Override
	public void onModuleLoad() {
		RootPanel.get("top").add(new Button("yes"));

	}

}
