package org.havannah.graphics;

import java.util.List;

import org.havannah.client.HavannahPresenter;

import com.google.common.collect.ImmutableList;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class HavannahGraphics extends Composite implements HavannahPresenter.View{

	private static HavannahGraphicsUiBinder uiBinder = GWT
			.create(HavannahGraphicsUiBinder.class);

	interface HavannahGraphicsUiBinder extends
			UiBinder<Widget, HavannahGraphics> {
	}

	public HavannahGraphics() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	public void setPresenter(HavannahPresenter havannahPresenter) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setViewerState(List<ImmutableList<Integer>> white,
			List<ImmutableList<Integer>> black) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setPlayerState(List<ImmutableList<Integer>> white,
			List<ImmutableList<Integer>> black) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void choosePoint(List<Integer> selectedPoint) {
		// TODO Auto-generated method stub
		
	}

}
