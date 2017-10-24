package ui;

import java.util.ArrayList;
import java.util.List;

import comp110.Employee;
import comp110.Week;
import javafx.collections.FXCollections;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

public class SwapBox extends HBox {
	
	private static final int LIST_VIEW_HEIGHT = 250;

	private int _swapDay;
	private int _swapHour;
	private Employee _swapEmployee;
	private ListView<Label> _personListView;
	private UI _ui;

	public SwapBox(UI ui) {
		_ui = ui;

		javafx.collections.ObservableList<String> dayList = FXCollections.observableArrayList(_ui.getDaysList());
		ListView<String> dayListView = new ListView<String>(dayList);
		this.getChildren().add(dayListView);
		ListView<String> hourListView = new ListView<String>();
		this.getChildren().add(hourListView);
		_swapDay = 0;

		dayListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			_swapDay = Week.dayInt(newValue);
			javafx.collections.ObservableList<String> hours = FXCollections
					.observableArrayList(_ui.getHoursList(newValue));
			// newValue is the new day
			hourListView.setItems(hours);
		});

		_personListView = new ListView<Label>();
		this.getChildren().add(_personListView);
		hourListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			int newHour = Integer.parseInt(newValue.split(" ")[0]);
			if (newHour < 9) {
				// convert hour back into military time
				newHour += 12;
			}
			_swapHour = newHour;
			List<Label> scheduledEmployees = new ArrayList<Label>();
			for (Employee e : _ui.getSchedule().getWeek().getShift(_swapDay, newHour)) {
				Label toAdd = new Label(e.getName());
				if (toAdd.getText().equals(_ui.getCurrentEmployee().getName())) {
					toAdd.setTextFill(Color.RED);
				}
				scheduledEmployees.add(toAdd);
			}
			javafx.collections.ObservableList<Label> people = FXCollections.observableArrayList(scheduledEmployees);
			_personListView.setItems(people);
		});

		dayListView.setPrefHeight(LIST_VIEW_HEIGHT);
		hourListView.setPrefHeight(LIST_VIEW_HEIGHT);
		_personListView.setPrefHeight(LIST_VIEW_HEIGHT);
	}

	// needs to be called on each swapBox so that they can talk to each other
	// and coordinate swapping
	public void registerSwapListener(Button saveButton, SwapBox otherBox) {
		_personListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			_swapEmployee = _ui.getSchedule().getStaff().getEmployeeByName(newValue.getText());

			if (_swapEmployee != null && otherBox.getSwapEmployee() != null) {
				saveButton.setDisable(false);
			}
		});
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
