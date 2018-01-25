package ui;

import java.util.ArrayList;
import java.util.List;

import comp110.Employee;
import comp110.Schedule;
import comp110.Week;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

public class SwapBox extends HBox {
	
	private Schedule _swapSchedule;
	private int _swapDay;
	private int _swapHour;
	private Employee _swapEmployee;
	private ListView<Label> _personListView;
	private UI _ui;
	private Schedule _ohSchedule;

	public SwapBox(UI ui) {
		_ui = ui;
		
		List<String> scheduleListAsString = new ArrayList<String>();
		for (Schedule s : _ui.getSchedules()) {
			scheduleListAsString.add(s.getDatesValid());
		}
		javafx.collections.ObservableList<String> scheduleList = FXCollections.observableArrayList(scheduleListAsString);
		ListView<String> scheduleListView = new ListView<String>(scheduleList);
		
		ListView<String> dayListView = new ListView<String>();
		
		
		scheduleListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			String ohSchedName = newValue.replaceAll("Tutoring", "OH");
			ohSchedName = ohSchedName .replaceAll("Grading", "OH");
			_ohSchedule = _ui.getScheduleByName(ohSchedName);
			_swapSchedule = _ui.getScheduleByName(newValue); // newValue will be the dateValid property of the schedule
			javafx.collections.ObservableList<String> days = FXCollections
					.observableArrayList(_ui.getDaysList(_ohSchedule));
			// newValue is the new day
			dayListView.setItems(days);
			dayListView.prefHeightProperty().bind(Bindings.size(days).multiply(24));

		});



		ListView<String> hourListView = new ListView<String>();
		_swapDay = 0;

		dayListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			_swapDay = Week.dayInt(newValue);
			javafx.collections.ObservableList<String> hours = FXCollections
					.observableArrayList(_ui.getHoursList(_ohSchedule, newValue));
			// newValue is the new day
			hourListView.setItems(hours);
			hourListView.prefHeightProperty().bind(Bindings.size(hours).multiply(24));

		});

		_personListView = new ListView<Label>();
		hourListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			int newHour = Integer.parseInt(newValue.split(" ")[0]);
			if (newHour < 9) {
				// convert hour back into military time
				newHour += 12;
			}
			_swapHour = newHour;
			List<Label> scheduledEmployees = new ArrayList<Label>();
			for (Employee e : _swapSchedule.getWeek().getShift(_swapDay, newHour)) {
				Label toAdd = new Label(e.getName());
				if (toAdd.getText().equals(_ui.getCurrentEmployee().getName())) {
					toAdd.setTextFill(Color.RED);
				}
				scheduledEmployees.add(toAdd);
			}
			javafx.collections.ObservableList<Label> people = FXCollections.observableArrayList(scheduledEmployees);
			_personListView.setItems(people);
			_personListView.prefHeightProperty().bind(Bindings.size(people).multiply(24));

		});
		
		// https://stackoverflow.com/questions/17429508/how-do-you-get-javafx-listview-to-be-the-height-of-its-items
		scheduleListView.prefHeightProperty().bind(Bindings.size(scheduleList).multiply(24));
		dayListView.prefHeightProperty().bind(Bindings.size(scheduleList).multiply(24));
		hourListView.prefHeightProperty().bind(Bindings.size(scheduleList).multiply(24));
		_personListView.prefHeightProperty().bind(Bindings.size(scheduleList).multiply(24));
		
		this.getChildren().add(scheduleListView);
		this.getChildren().add(dayListView);
		this.getChildren().add(hourListView);
		this.getChildren().add(_personListView);

		
		
	}

	// needs to be called on each swapBox so that they can talk to each other
	// and coordinate swapping
	public void registerSwapListener(Button saveButton, SwapBox otherBox) {
		_personListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			_swapEmployee = _swapSchedule.getStaff().getEmployeeByName(newValue.getText());

			if (_swapEmployee != null && otherBox.getSwapEmployee() != null) {
				saveButton.setDisable(false);
			}
		});
	}
	
	public Schedule getSwapSchedule() {
		return _swapSchedule;
	}
	
	public Employee getSwapEmployee() {
		return _swapEmployee;
	}
	
	public int getSwapDay() {
		return _swapDay;
	}
	
	public int getSwapHour() {
		return _swapHour;
	}

}
