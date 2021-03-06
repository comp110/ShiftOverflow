package ui;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import comp110.Controller;
import comp110.Employee;
import comp110.Schedule;
import comp110.Week;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class ScheduleStage extends KarenStage {
	
	private String _currentlyOpenedTabTitle;
	
	public ScheduleStage(List<Schedule> schedules, String title, Controller controller, UI ui, String tabToReopen) {
		super(title, controller, ui);
		_currentlyOpenedTabTitle = tabToReopen;
		this.setOnCloseRequest((event) -> {
			_ui.setScheduleStageIsOpen(false);
			this.close();
		}
	);
		renderScheduleStage(schedules);
		this.show();
	}
	
	// called whenever we refresh an open schedule stage, we want to set same x and y as previously
	 public ScheduleStage(List<Schedule> schedules, String title, Controller controller, UI ui, double x, double y, String tabToReopen) {
	   this(schedules, title, controller, ui, tabToReopen);
	   this.setX(x);
	   this.setY(y);
	 }
	
	private void renderScheduleStage(List<Schedule> schedules) {
		_ui.setScheduleStageIsOpen(true);
		if (_ui.getSchedules() == null) {
			// only want to do this first time we get the schedule, otherwise UI
			// has most up to date
			// version of schedule and controller version is out of date
			_ui.setSchedules(schedules);

		}
		
		TabPane tabs = new TabPane();
		tabs.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
		
		for (Schedule schedule : _ui.getSchedules()) {
			Tab tab = new Tab();
			GridPane schedulePane = this.writeSchedule(schedule);
			ScrollPane scroll = new ScrollPane();
			scroll.setPrefSize(700, 500);
			scroll.setContent(schedulePane);
			// this handles resize of nodes if user resizes stage
			scroll.prefHeightProperty()
					.addListener((obs, oldVal, newVal) -> this.setHeight(newVal.doubleValue()));
			scroll.prefWidthProperty().addListener((obs, oldVal, newVal) -> this.setWidth(newVal.doubleValue()));
			tab.setContent(scroll);
			
			tab.setText(schedule.getDatesValid());
			tabs.getTabs().add(tab);
		}
		
        tabs.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
        	_currentlyOpenedTabTitle = newValue.getText();
        });
		
		for (Tab t : tabs.getTabs()) {
			if (t.getText().contains("OH")) {
				tabs.getSelectionModel().select(t);
				break;
			}
		}
        
        // reopen last open tab if there was one
		if (_currentlyOpenedTabTitle != null) {
			for (Tab t : tabs.getTabs()) {
				if (t.getText().equals(_currentlyOpenedTabTitle)) {
					tabs.getSelectionModel().select(t);
				}
			}
		}

		Scene scene = new Scene(tabs);
		scene.getStylesheets().add("Borders.css");
		this.setScene(scene);
		this.sizeToScene();
	}
	
	public GridPane writeSchedule(Schedule schedule) {
		GridPane schedulePane = new GridPane();
		
		//Load the fonts
		InputStream is = getClass().getResourceAsStream("segoeui.ttf");
        Font font = Font.loadFont(is, 12.0);   
		InputStream is2 = getClass().getResourceAsStream("segoeuibold.ttf");
        Font boldFont = Font.loadFont(is2, 12.0);
		
		schedulePane.setAlignment(Pos.CENTER);
		//schedulePane.setGridLinesVisible(true);
		ArrayList<ArrayList<ArrayList<Employee>>> shifts = shiftsAsArray(schedule.getWeek());
		TextFlow firstBlock = new TextFlow(new Text());
		firstBlock.getStyleClass().add("newHour");
		schedulePane.add(firstBlock, 0, 0);
		for (int day = 0; day < 7; day++) {
			// +1 for hour column
			Text test = new Text(Week.dayString(day));
			test.setFont(font);
			TextFlow h = new TextFlow(test);
			String style = "newHour";
			h.getStyleClass().add(style);
			schedulePane.add(h, day + 1, 0);
		}
		
		int hourRow = 0;
		int prevHourRow = hourRow;
		for (int hour = getEarliestHour(schedule.getWeek()); hour < getLatestHour(schedule.getWeek()); hour++) {
			
			Text dayLabel = new Text(
					(hour % 12 == 0 ? 12 : hour % 12) + " -- " + ((hour + 1) % 12 == 0 ? 12 : (hour + 1) % 12));
			//dayLabel.setMaxWidth(Double.MAX_VALUE);
			//dayLabel.setAlignment(Pos.CENTER);
			dayLabel.setFont(font);
			// +1 to account for day row
			TextFlow dayLabelWrapper = new TextFlow(dayLabel);
			dayLabelWrapper.getStyleClass().add("newHour");
			schedulePane.add(dayLabelWrapper, 0, hourRow + 1);
			
			for (int i = prevHourRow + 2; i < hourRow + 1; i++){
				Text test = new Text();
				TextFlow h = new TextFlow(test);
				h.getStyleClass().add("notNewHour");
				schedulePane.add(h, 0, i);
			}
			prevHourRow = hourRow;
			
			int max = getMaxSize(hour, schedule.getWeek());

			for (int i = 0; i < max; i++) {
				for (int day = 0; day < 7; day++) {
					if (i < shifts.get(day).get(hour).size()) {
						Text scheduledEmployee = new Text(shifts.get(day).get(hour).get(i).toString());
						scheduledEmployee.setFont(font);

						//bold shift lead
						if (schedule.getWeek().getShift(day, hour).getLead() != null && shifts.get(day).get(hour).get(i).equals(schedule.getWeek().getShift(day, hour).getLead())) {
							scheduledEmployee.setFont(boldFont);
							//scheduledEmployee.setFont(Font.getDefault());
						}
						// highlight your name on the schedule
						if ((_ui.getCurrentEmployee() != null)
								&& (shifts.get(day).get(hour).get(i).toString().equals(_ui.getCurrentEmployee().getName()))) {
							scheduledEmployee.setFill(Color.RED);
						}
						// +1 to account for day row
						TextFlow x = new TextFlow(scheduledEmployee);
						if (i == 0) x.getStyleClass().add("newHour");
						
						else x.getStyleClass().add("notNewHour");
						schedulePane.add(x, day + 1, hourRow + i + 1);
					}
					else {
						Text y = new Text();
						TextFlow x = new TextFlow(y);
						String style = i == 0 ? "newHour" : "notNewHour";
						x.getStyleClass().add(style);
						schedulePane.add(x, day + 1, hourRow + i + 1);
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

	public String getCurrentlyOpenedTabTitle() {
		return _currentlyOpenedTabTitle;
	}
}