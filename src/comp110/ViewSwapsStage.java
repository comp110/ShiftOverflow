package comp110;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javafx.collections.FXCollections;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

public class ViewSwapsStage extends KarenStage {

	public ViewSwapsStage(String title, Employee currentEmployee, Controller controller, UI ui) {
		super(title, controller, currentEmployee, ui);
		this.renderSwapStage();
		this.show();
	}

	private void renderSwapStage() {
		// make sure valid schedule
		if (_ui.getSchedule() == null) {
			_ui.displayMessage("There is no schedule loaded yet.  A schedule must be loaded before swapping.");
			return;
		}

		Group root = new Group();
		Scene scene = new Scene(root);
		BorderPane rootPane = new BorderPane();
		root.getChildren().add(rootPane);
		this.setScene(scene);
		javafx.collections.ObservableList<String> scheduledShifts = FXCollections
				.observableArrayList(this.getScheduledShifts(_ui.getSchedule()));
		ListView<String> scheduledShiftsListView = new ListView<String>(scheduledShifts);
		HBox listBox = new HBox();
		listBox.getChildren().add(scheduledShiftsListView);

		ListView<String> availableSwapsListView = new ListView<String>();
		HBox swapBox = new HBox();
		swapBox.getChildren().add(availableSwapsListView);
		scheduledShiftsListView.getSelectionModel().selectedItemProperty()
				.addListener((observable, oldValue, newValue) -> {
					// we sort the list of potential swaps by likelihood it will
					// be compatible
					javafx.collections.ObservableList<String> availableToSwap = FXCollections
							.observableArrayList(getOrderedPotentialSwaps(_ui.getSchedule(),
									Week.dayInt(newValue.split(" ")[0]), Integer.parseInt(newValue.split(" ")[1])));
					availableSwapsListView.setItems(availableToSwap);
				});
		rootPane.setLeft(listBox);
		rootPane.setRight(swapBox);

		this.sizeToScene();
		this.setResizable(false);
	}

	// the hour gets passed in as regular time and needs to be converted to
	// military time
	private ArrayList<String> getOrderedPotentialSwaps(Schedule schedule, int day, int hour) {
		if (hour < 9) { // only hours from 9am to pm are valid so this works
			hour += 12;
		}
		ArrayList<String> swapCandidates = new ArrayList<String>();
		swapCandidates.addAll(schedule.getStaff().getWhoIsAvailable(day, hour));
		// remove yourself from the list
		swapCandidates.remove(_currentEmployee.getName());
		// now score each one
		Map<Employee, Double> scoredEmployees = new HashMap<Employee, Double>();
		for (String otherEmployeeName : swapCandidates) {
			Employee otherEmployee = schedule.getStaff().getEmployeeByName(otherEmployeeName);
			// if the other person is already scheduled for the shift we are
			// trying to swap then we can't swap with them so continue
			if (schedule.getWeek().getShift(day, hour).contains(otherEmployee)) {
				continue;
			}
			scoredEmployees.put(otherEmployee, 0.0);
			ArrayList<Shift> scheduledShifts = new ArrayList<Shift>();
			// get the shifts this employee is scheduled for
			for (int i = 0; i < schedule.getWeek().getShifts().length; i++) {
				for (int j = 0; j < schedule.getWeek().getShifts()[i].length; j++) {
					for (Employee e : schedule.getWeek().getShift(i, j)) {
						if (e.getName().equals(otherEmployee.getName())) {
							scheduledShifts.add(schedule.getWeek().getShift(i, j));
						}
					}
				}
			}
			// now see which shifts of other employee currentEmployee is
			// available for
			for (Shift shift : scheduledShifts) {
				if (_currentEmployee.isAvailable(shift.getDay(), shift.getHour())) {
					scoredEmployees.put(otherEmployee, scoredEmployees.get(otherEmployee) + 1);
				}
			}
			// divide by total number of shifts otherEmployee has
			scoredEmployees.put(otherEmployee, scoredEmployees.get(otherEmployee) / scheduledShifts.size());
		}
		// now that we have populated the map, sort by score write out the
		// candidates in order
		scoredEmployees = sortByValue(scoredEmployees);
		ArrayList<String> orderedSwapCandidates = new ArrayList<String>();
		for (Employee e : scoredEmployees.keySet()) {
			// only write out employees we have the potential to swap with
			if (scoredEmployees.get(e) > 0) {
				orderedSwapCandidates.add(e.getName() + " " + scoredEmployees.get(e));
			}
		}

		return orderedSwapCandidates;
	}

	// returns strings in the proper label format of all the shifts
	// _currentEmployee is scheduled for
	private ArrayList<String> getScheduledShifts(Schedule schedule) {
		ArrayList<String> scheduledShifts = new ArrayList<String>();
		for (int day = 0; day < schedule.getWeek().getShifts().length; day++) {
			for (int hour = 0; hour < schedule.getWeek().getShifts()[day].length; hour++) {
				for (Employee e : schedule.getWeek().getShift(day, hour)) {
					if (_currentEmployee != null && e.getName().equals(_currentEmployee.getName())) {
						scheduledShifts.add(Week.dayString(day) + " " + (hour % 12 == 0 ? 12 : hour % 12) + " -- "
								+ ((hour + 1) % 12 == 0 ? 12 : (hour + 1) % 12));
					}
				}
			}
		}
		return scheduledShifts;
	}

	// http://stackoverflow.com/questions/109383/sort-a-mapkey-value-by-values-java
	public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
		List<Map.Entry<K, V>> list = new LinkedList<>(map.entrySet());
		Collections.sort(list, (o1, o2) -> {
			return (o1.getValue()).compareTo(o2.getValue()) * -1;
		});
		Map<K, V> result = new LinkedHashMap<>();
		for (Map.Entry<K, V> entry : list) {
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}

}
