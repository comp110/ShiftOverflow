package ui;

import comp110.Controller;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class KarenStage extends Stage {
	
	protected Controller _controller;
	protected UI _ui;
	
	public KarenStage(String title, Controller controller, UI ui) {
		this.setTitle(title);
		_controller = controller;
		_ui = ui;
		this.getIcons().add(new Image(getClass().getResource("karen.png").toString()));
	}	

}
