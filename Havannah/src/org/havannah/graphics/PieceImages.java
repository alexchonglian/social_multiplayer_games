package org.havannah.graphics;

//https://github.com/yoav-zibin/cheat-game/tree/master/eclipse/src/org/cheat
//gif are provided by jon.kleiser@usit.uio.no

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface PieceImages extends ClientBundle {
	@Source("assets/top0.gif")
	ImageResource top0();

	@Source("assets/top1.gif")
	ImageResource top1();
	
	@Source("assets/top2.gif")
	ImageResource top2();
	
	@Source("assets/righte.gif")
	ImageResource righte();
	
	@Source("assets/right0.gif")
	ImageResource right0();
	
	@Source("assets/lefte.gif")
	ImageResource lefte();
	
	@Source("assets/bot.gif")
	ImageResource bot();
	
	@Source("assets/nox.gif")
	ImageResource nox();
	
	@Source("assets/bluex.gif")
	ImageResource bluex();
	
	@Source("assets/redx.gif")
	ImageResource redx();
}
