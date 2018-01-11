package ui;

import javafx.geometry.Insets;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class ScheduleBox extends TextFlow {
	
	
	public ScheduleBox(Text text, boolean isBottom) {
		super(text);
		if (isBottom) {
			this.setBorder(new Border(new BorderStroke(Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK,
						            BorderStrokeStyle.NONE, BorderStrokeStyle.NONE, BorderStrokeStyle.SOLID, BorderStrokeStyle.NONE,
						            CornerRadii.EMPTY, new BorderWidths(0, 0, 5, 0), Insets.EMPTY)));
		}
	}

}
