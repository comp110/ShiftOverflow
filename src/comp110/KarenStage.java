package comp110;

import javafx.scene.image.Image;
import javafx.stage.Stage;

public class KarenStage extends Stage {
	
	protected Controller _controller;
	protected Employee _currentEmployee;
	protected UI _ui;
	
	public KarenStage(String title, Controller controller, Employee currentEmployee, UI ui) {
		this.setTitle(title);
		_controller = controller;
		_currentEmployee = currentEmployee;
		_ui = ui;
		this.getIcons().add(new Image(getClass().getResource("karen.png").toString()));
	}	

}
