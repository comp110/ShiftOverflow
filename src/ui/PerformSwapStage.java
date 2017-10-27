package ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import comp110.Controller;
import comp110.Employee;
import comp110.Schedule;
import comp110.Week;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;

public class PerformSwapStage extends KarenStage {

	private boolean _continueToSwap;
	private SwapBox _topBox;
	private SwapBox _bottomBox;
	private BorderPane _addOrDropPane;
	private Button _addOrDropButton;
	private ComboBox<String> _addOrDropComboBox;
	private String _addOrDrop;
	private Schedule _dropSchedule;
	private Schedule _addSchedule;
	private int _dropDay;
	private int _dropHour;
	private int _addDay;
	private int _addHour;
	private Employee _employeeToAddOrDrop;

	public PerformSwapStage(String title, Controller controller, UI ui) {
		super(title, controller, ui);
		_continueToSwap = false;
		Group root = new Group();
		TabPane rootTabPane = new TabPane();
		rootTabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
		BorderPane rootSwapPane = new BorderPane();
		Tab t1 = new Tab("Swap", rootSwapPane);
		Tab t2 = new Tab("Add/Drop", this.getAddDropPane());
		rootTabPane.getTabs().addAll(t1, t2);
		root.getChildren().add(rootTabPane);
		Scene scene = new Scene(root);
		this.setScene(scene);
		Button saveButton = new Button("Swap!");
		_topBox = new SwapBox(_ui);
		_bottomBox = new SwapBox(_ui);
		_topBox.registerSwapListener(saveButton, _bottomBox);
		_bottomBox.registerSwapListener(saveButton, _topBox);
		// disable save button by default
		saveButton.setDisable(true);
		// TODO figure out how to not hardcode this and just make it fill stage
		saveButton.setPrefWidth(744);
		saveButton.setOnAction(this::performSwap);
		rootSwapPane.setBottom(saveButton);
		rootSwapPane.setTop(_topBox);
		rootSwapPane.setCenter(_bottomBox);
		this.sizeToScene();
		this.setResizable(false);
		this.show();
	}

	private BorderPane getAddDropPane() {
		_addOrDropPane = new BorderPane();

		_addOrDropComboBox = new ComboBox<String>();
		_addOrDropComboBox.getSelectionModel().selectFirst();
		_addOrDropComboBox.setPrefWidth(744);
		_addOrDropComboBox.setItems(FXCollections.observableArrayList("Drop", "Add"));
		_addOrDropComboBox.getSelectionModel().selectFirst(); // set Drop as
																// default
		_addOrDrop = "Drop";

		// because default is drop set initial text to be that
		_addOrDropButton = new Button("Drop");
		_addOrDropButton.setDisable(true);
		_addOrDropButton.setPrefWidth(744);
		_addOrDropButton.setOnAction(this::addDropButtonPress);

		_addOrDropComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			_addOrDrop = newValue;
			if (_addOrDrop.equals("Add")) {
				_addOrDropPane.getChildren().clear();
				_addOrDropPane.setTop(_addOrDropComboBox);
				_addOrDropPane.setBottom(_addOrDropButton);
				_addOrDropButton.setText("Add");
				setupForAdd();
			} else {
				_addOrDropPane.getChildren().clear();
				_addOrDropPane.setTop(_addOrDropComboBox);
				_addOrDropPane.setBottom(_addOrDropButton);
				_addOrDropButton.setText("Drop");
				setupForDrop();
			}
		});
		_addOrDropPane.setTop(_addOrDropComboBox);

		this.setupForDrop(); // first time through we want to setup for drop

		_addOrDropPane.setBottom(_addOrDropButton);
		return _addOrDropPane;
	}

	private void performSwap(ActionEvent event) {
		String unavailableEmployee = "";
		Schedule swapSchedule1 = _topBox.getSwapSchedule();
		Employee swapEmployee1 = _topBox.getSwapEmployee();
		int swapDay1 = _topBox.getSwapDay();
		int swapHour1 = _topBox.getSwapHour();
		Schedule swapSchedule2 = _topBox.getSwapSchedule();
		Employee swapEmployee2 = _bottomBox.getSwapEmployee();
		int swapDay2 = _bottomBox.getSwapDay();
		int swapHour2 = _bottomBox.getSwapHour();

		if (!swapEmployee1.isAvailable(swapDay2, swapHour2)) {
			unavailableEmployee = swapEmployee1.getName();
		} else if (!swapEmployee2.isAvailable(swapDay1, swapHour1)) {
			if (unavailableEmployee.equals("")) {
				unavailableEmployee = swapEmployee2.getName();
			} else {
				unavailableEmployee += " and " + swapEmployee2.getName();
			}
		}
		// show a warning that one of the employees is unavailable for the shift
		// they are swapping into
		_continueToSwap = true;
		if (unavailableEmployee != "") {
			KarenStage dialogueBox = new KarenStage("Unavailable Employee", null, _ui);
			Group root = new Group();
			Scene scene = new Scene(root);

			VBox vbox = new VBox();
			vbox.setPadding(new Insets(10, 0, 0, 10));
			vbox.setSpacing(10);

			HBox hbox1 = new HBox();
			hbox1.setSpacing(10);
			hbox1.setAlignment(Pos.CENTER);

			BorderPane pane = new BorderPane();

			Label text = new Label(unavailableEmployee
					+ " is listed as unavailable on their csv for the time you are trying to swap.\n");
			Label text2 = new Label("Are you sure you want to continue?");
			text.setTextAlignment(TextAlignment.CENTER);
			text2.setTextAlignment(TextAlignment.CENTER);
			pane.setTop(text);
			pane.setBottom(text2);
			BorderPane.setAlignment(text, Pos.CENTER);
			BorderPane.setAlignment(text2, Pos.CENTER);
			hbox1.getChildren().add(pane);

			HBox hbox2 = new HBox();
			hbox2.setSpacing(10);
			hbox2.setAlignment(Pos.CENTER);

			Button yes = new Button("Yes");
			yes.defaultButtonProperty().bind(yes.focusedProperty());
			yes.setOnAction((event1) -> {
				// if they are ok with this just close the box and continue
				dialogueBox.close();
			});
			Button no = new Button("No");
			dialogueBox.setOnCloseRequest((event1) -> {
				// assume hitting the close window button is like clicking no
				// if not set the flag so we don't swap
				_continueToSwap = false;
				dialogueBox.close();
			});
			no.setOnAction((event1) -> {
				// if not set the flag so we don't swap
				_continueToSwap = false;
				dialogueBox.close();
			});
			hbox2.getChildren().addAll(yes, no);

			vbox.getChildren().addAll(hbox1, hbox2);
			root.getChildren().add(vbox);
			dialogueBox.initModality(Modality.APPLICATION_MODAL);
			dialogueBox.setResizable(false);
			dialogueBox.setScene(scene);
			dialogueBox.sizeToScene();
			dialogueBox.showAndWait();
			dialogueBox.setOnCloseRequest((event1) -> {
				event1.consume();
				_continueToSwap = false;
			});
		}
		// now check and see if we should proceed
		if (!_continueToSwap) {
			return;
		}
		// remove employees
		swapSchedule1.getWeek().getShift(swapDay1, swapHour1).remove(swapEmployee1);
		swapSchedule2.getWeek().getShift(swapDay2, swapHour2).remove(swapEmployee2);
		// add employees
		swapSchedule1.getWeek().getShift(swapDay1, swapHour1).add(swapEmployee2);
		swapSchedule2.getWeek().getShift(swapDay2, swapHour2).add(swapEmployee1);

		// tell controller to push changes
		_controller.uiRequestChangeSchedule(_ui.getSchedules(), "SWAPPED: " + swapSchedule1.getDatesValid() + " "+ swapEmployee1.getName() + " "
				+ Week.dayString(swapDay1) + " " + ((swapHour1 % 12) == 0 ? 12 : (swapHour1 % 12)) + " -- "
				+ (((swapHour1 + 1) % 12) == 0 ? 12 : ((swapHour1 + 1) % 12)) + " with " + swapSchedule2.getDatesValid() + " " + swapEmployee2.getName() + " "
				+ Week.dayString(swapDay2) + " " + ((swapHour2 % 12) == 0 ? 12 : (swapHour2 % 12)) + " -- "
				+ (((swapHour2 + 1) % 12) == 0 ? 12 : ((swapHour2 + 1) % 12)));
	}

	private void setupForDrop() {
		_addOrDropButton.setDisable(true);

		javafx.collections.ObservableList<String> dayList = FXCollections.observableArrayList(_ui.getDaysList());
		ListView<String> dayListView = new ListView<String>(dayList);
		_addOrDropPane.setLeft(dayListView);
		ListView<String> hourListView = new ListView<String>();
		_addOrDropPane.setCenter(hourListView);
		dayListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			_dropDay = Week.dayInt(newValue);
			javafx.collections.ObservableList<String> hours = FXCollections
					.observableArrayList(_ui.getHoursList(newValue));
			hourListView.setItems(hours);
			_employeeToAddOrDrop = null;
			_addOrDropButton.setDisable(true);
		});

		ListView<Label> personListView = new ListView<Label>();
		_addOrDropPane.setRight(personListView);
		hourListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			_dropHour = Integer.parseInt(newValue.split(" ")[0]);
			if (_dropHour < 9) {
				_dropHour += 12;
			}
			List<Label> scheduledEmployees = new ArrayList<Label>();
			for (Employee e : _ui.getSchedules().getWeek().getShift(_dropDay, _dropHour)) {
				Label toAdd = new Label(e.getName());
				if (_ui.getCurrentEmployee() != null && toAdd.getText().equals(_ui.getCurrentEmployee().getName())) {
					toAdd.setTextFill(Color.RED);
				}
				scheduledEmployees.add(toAdd);
			}
			javafx.collections.ObservableList<Label> people = FXCollections.observableArrayList(scheduledEmployees);
			personListView.setItems(people);
			_employeeToAddOrDrop = null;
			_addOrDropButton.setDisable(true);
		});
		personListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			_employeeToAddOrDrop = _ui.getSchedules().getStaff().getEmployeeByName(newValue.getText());
			if (_employeeToAddOrDrop == null) {
				_addOrDropButton.setDisable(true);
			} else {
				_addOrDropButton.setDisable(false);
			}
		});
	}

	private void setupForAdd() {
		_addOrDropButton.setDisable(true);

		javafx.collections.ObservableList<String> dayList = FXCollections.observableArrayList(_ui.getDaysList());
		ListView<String> dayListView = new ListView<String>(dayList);
		_addOrDropPane.setLeft(dayListView);
		ListView<String> hourListView = new ListView<String>();
		_addOrDropPane.setCenter(hourListView);
		dayListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			_addDay = Week.dayInt(newValue);
			javafx.collections.ObservableList<String> hours = FXCollections
					.observableArrayList(_ui.getHoursList(newValue));
			hourListView.setItems(hours);
			_employeeToAddOrDrop = null;
			_addOrDropButton.setDisable(true);
		});

		ListView<Label> personListView = new ListView<Label>();
		_addOrDropPane.setRight(personListView);
		hourListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			_addHour = Integer.parseInt(newValue.split(" ")[0]);
			if (_addHour < 9) {
				_addHour += 12;
			}
			List<Label> allEmployees = new ArrayList<Label>();
			for (Employee e : _ui.getSchedules().getStaff()) {
				Label toAdd = new Label(e.getName());
				if (_ui.getCurrentEmployee() != null && toAdd.getText().equals(_ui.getCurrentEmployee().getName())) {
					toAdd.setTextFill(Color.RED);
				}
				allEmployees.add(toAdd);
			}
			Collections.sort(allEmployees, (a, b) -> {
				// need to do this because I don't want to add my last name lol
				String aName = "";
				String bName = "";
				if (((Label) a).getText().equals("Jeffrey")) {
					aName = "Jeffrey Young";
				} else {
					aName = ((Label) a).getText();
				}
				if (((Label) b).getText().equals("Jeffrey")) {
					bName = "Jeffrey Young";
				} else {
					bName = ((Label) b).getText();
				}
				if (aName.split(" ")[1].compareTo(bName.split(" ")[1]) >= 1) {
					return 1;
				} else if (aName.split(" ")[1].compareTo(bName.split(" ")[1]) <= -1) {
					return -1;
				} else {
					return 0;
				}
			});
			javafx.collections.ObservableList<Label> people = FXCollections.observableArrayList(allEmployees);
			personListView.setItems(people);
			_employeeToAddOrDrop = null;
			_addOrDropButton.setDisable(true);
		});
		personListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			_employeeToAddOrDrop = _ui.getSchedules().getStaff().getEmployeeByName(newValue.getText());
			if (_employeeToAddOrDrop == null) {
				_addOrDropButton.setDisable(true);
			} else {
				_addOrDropButton.setDisable(false);
			}
		}

		);

	}

	private void addDropButtonPress(ActionEvent event) {

		if (_addOrDrop.equals("Add")) {
			_ui.getSchedules().getWeek().getShift(_addDay, _addHour).add(_employeeToAddOrDrop);
		} else { // must be a drop
			_ui.getSchedules().getWeek().getShift(_dropDay, _dropHour).remove(_employeeToAddOrDrop);

		}
		// tell controller to push changes
		if (_addOrDrop.equals("Add")) {
			_controller.uiRequestChangeSchedule(_ui.getSchedules(),
					_addOrDrop.toUpperCase() + ": " + _employeeToAddOrDrop + " " + Week.dayString(_addDay) + " "
							+ ((_addHour % 12) == 0 ? 12 : (_addHour % 12)) + " -- "
							+ (((_addHour + 1) % 12) == 0 ? 12 : ((_addHour + 1) % 12)));
		} else {
			_controller.uiRequestChangeSchedule(_ui.getSchedules(),
					_addOrDrop.toUpperCase() + ": " + _employeeToAddOrDrop + " " + Week.dayString(_dropDay) + " "
							+ ((_dropHour % 12) == 0 ? 12 : (_dropHour % 12)) + " -- "
							+ (((_dropHour + 1) % 12) == 0 ? 12 : ((_dropHour + 1) % 12)));
		}
	}
}
