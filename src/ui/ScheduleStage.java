package ui;

import java.io.InputStream;
import java.util.ArrayList;

import comp110.Controller;
import comp110.Employee;
import comp110.Schedule;
import comp110.Week;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class ScheduleStage extends KarenStage {
	
	public ScheduleStage(Schedule schedule, String title, Controller controller, UI ui) {
		super(title, controller, ui);
		this.setOnCloseRequest((event) -> {
			_ui.setScheduleStageIsOpen(false);
			this.close();
		}
	);
		renderScheduleStage(schedule);
		this.show();
	}
	
	// called whenever we refresh an open schedule stage, we want to set same x and y as previously
	 public ScheduleStage(Schedule schedule, String title, Controller controller, UI ui, double x, double y) {
	   this(schedule, title, controller, ui);
	   this.setX(x);
	   this.setY(y);
	 }
	
	private void renderScheduleStage(Schedule schedule) {
		_ui.setScheduleStageIsOpen(true);
		if (_ui.getSchedule() == null) {
			// only want to do this first time we get the schedule, otherwise UI
			// has most up to date
			// version of schedule and controller version is out of date
			_ui.setSchedule(schedule);

		}

		GridPane schedulePane = this.writeSchedule(_ui.getSchedule());
		ScrollPane scroll = new ScrollPane();
		scroll.setPrefSize(700, 500);
		scroll.setContent(schedulePane);
		// this handles resize of nodes if user resizes stage
		scroll.prefHeightProperty()
				.addListener((obs, oldVal, newVal) -> this.setHeight(newVal.doubleValue()));
		scroll.prefWidthProperty().addListener((obs, oldVal, newVal) -> this.setWidth(newVal.doubleValue()));
		Scene scene = new Scene(scroll);
		this.setScene(scene);
		this.sizeToScene();
	}
	
	public GridPane writeSchedule(Schedule schedule) {
		GridPane schedulePane = new GridPane();
		schedulePane.setAlignment(Pos.CENTER);
		schedulePane.setGridLinesVisible(true);
		ArrayList<ArrayList<ArrayList<Employee>>> shifts = shiftsAsArray(schedule.getWeek());

		for (int day = 0; day < 7; day++) {
			// +1 for hour column
			schedulePane.add(new Label(Week.dayString(day)), day + 1, 0);
		}

		//Load the fonts
		InputStream is = ScheduleStage.class.getResourceAsStream("segoeui.ttf");
        Font font = Font.loadFont(is, 12.0);   
		InputStream is2 = ScheduleStage.class.getResourceAsStream("segoeuibold.ttf");
        Font boldFont = Font.loadFont(is2, 12.0);
		
		
		int hourRow = 0;
		for (int hour = getEarliestHour(schedule.getWeek()); hour < getLatestHour(schedule.getWeek()); hour++) {
			Label dayLabel = new Label(
					(hour % 12 == 0 ? 12 : hour % 12) + " -- " + ((hour + 1) % 12 == 0 ? 12 : (hour + 1) % 12));
			dayLabel.setMaxWidth(Double.MAX_VALUE);
			dayLabel.setAlignment(Pos.CENTER);
			// +1 to account for day row
			schedulePane.add(dayLabel, 0, hourRow + 1);

			int max = getMaxSize(hour, schedule.getWeek());

			for (int i = 0; i < max; i++) {
				for (int day = 0; day < 7; day++) {
					if (i < shifts.get(day).get(hour).size()) {
						Text scheduledEmployee = new Text(shifts.get(day).get(hour).get(i).toString());
						scheduledEmployee.setFont(font);

						//bold shift lead
						if (_ui.getCurrentEmployee() != null && schedule.getWeek().getShift(day, hour).getLead() != null && shifts.get(day).get(hour).get(i).equals(schedule.getWeek().getShift(day, hour).getLead())) {
							scheduledEmployee.setFont(boldFont);
							//scheduledEmployee.setFont(Font.getDefault());
						}
						// highlight your name on the schedule
						if ((_ui.getCurrentEmployee() != null)
								&& (shifts.get(day).get(hour).get(i).toString().equals(_ui.getCurrentEmployee().getName()))) {
							scheduledEmployee.setFill(Color.RED);
						}
						// +1 to account for day row
						schedulePane.add(new TextFlow(scheduledEmployee), day + 1, hourRow + i + 1);
					}
				}
			}
			hourRow += max;
		}
		return schedulePane;
	}
	
	// gets earliest scheduled hour in the week
	private static int getEarliestHour(Week week) {
		int min = 10000;
		for (int day = 0; day < 7; day++) {
			for (int hour = 0; hour < 24; hour++) {
				if (week.getShift(day, hour).size() > 0) {
					if (hour < min) {
						min = hour;
					}
				}
			}
		}
		return min;
	}

	// gets latest scheduled hour in the week
	private static int getLatestHour(Week week) {
		int max = 0;
		for (int day = 0; day < 7; day++) {
			for (int hour = 0; hour < 24; hour++) {
				if (week.getShift(day, hour).size() > 0) {
					if (hour > max) {
						max = hour;
					}
				}
			}
		}
		return max + 1;
	}

	// turn the week object into 3D array to make it easier to write out
	private static ArrayList<ArrayList<ArrayList<Employee>>> shiftsAsArray(Week week) {
		ArrayList<ArrayList<ArrayList<Employee>>> shifts = new ArrayList<ArrayList<ArrayList<Employee>>>();
		for (int day = 0; day < 7; day++) {
			shifts.add(new ArrayList<ArrayList<Employee>>());
			for (int hour = 0; hour < 24; hour++) {
				shifts.get(day).add(new ArrayList<Employee>());
				for (Employee e : week.getShift(day, hour)) {
					shifts.get(day).get(hour).add(e);
				}
			}
		}

		return shifts;
	}	

	// gets the size of the longest shift for any given hour across all days
	private static int getMaxSize(int hour, Week week) {
		int max = 0;
		for (int day = 0; day < 7; day++) {
			if (week.getShift(day, hour).size() > max) {
				max = week.getShift(day, hour).size();
			}
		}
		return max;
	}

}
