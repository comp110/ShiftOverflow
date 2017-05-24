package comp110;

import java.util.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class UI extends Application {

	private Stage _passwordStage;
	private AvailabilityStage _availabilityStage;
	private Controller _controller;
	private TextField _usernameField;
	private PasswordField _passwordField;
	private Button _passwordSubmitButton;
	private Employee _currentEmployee;
	private Schedule _schedule;
	private boolean _scheduleStageIsOpen;
	private ScheduleStage _scheduleStage;


	@Override
	public void start(Stage primaryStage) throws Exception {
		// create controller
		_controller = new Controller(this);

		// create the dialog to collect github username/password
		// and show it as the first thing
		_passwordStage = new KarenStage("Password", _controller, null, null);
		Group passwordGroup = new Group();
		Scene passwordScene = new Scene(passwordGroup);
		_passwordStage.setScene(passwordScene);
		//if the user closes without entering password we want to kill program
		_passwordStage.setOnCloseRequest((event) -> System.exit(0));
		VBox vbox = new VBox();
		vbox.setPadding(new Insets(10, 0, 0, 10));
		vbox.setSpacing(10);
		HBox hbox1 = new HBox();
		HBox hbox2 = new HBox();
		HBox hbox3 = new HBox();
		hbox1.setSpacing(10);
		hbox1.setAlignment(Pos.CENTER_LEFT);
		hbox2.setSpacing(10);
		hbox2.setAlignment(Pos.CENTER_LEFT);
		hbox3.setSpacing(10);
		hbox3.setAlignment(Pos.CENTER);

		_usernameField = new TextField();
		_passwordField = new PasswordField();
		// bind enter key to button press
		_passwordField.setOnKeyPressed((event) -> {
			if (event.getCode() == KeyCode.ENTER) {
				this.loginToGithub(null);
			}
		});
		Label usernameLabel = new Label("Username");
		Label passwordLabel = new Label("Password ");
		_passwordSubmitButton = new Button("Login");
		// bind enter key to button press
		_passwordSubmitButton.defaultButtonProperty().bind(_passwordSubmitButton.focusedProperty());
		_passwordSubmitButton.setOnAction(this::loginToGithub);
		hbox3.getChildren().add(_passwordSubmitButton);
		hbox1.getChildren().addAll(usernameLabel, _usernameField);
		hbox2.getChildren().addAll(passwordLabel, _passwordField);
		vbox.getChildren().addAll(hbox1, hbox2, hbox3);
		passwordGroup.getChildren().add(vbox);
		_passwordStage.sizeToScene();
		_passwordStage.setResizable(false);
		// load a blank stage behind password box so it looks pretty
		this.displayAvailable(null);
		// password stage should be modal
		_passwordStage.initModality(Modality.APPLICATION_MODAL);
		_passwordStage.showAndWait();

		_scheduleStageIsOpen = false;
	}

	private void loginToGithub(ActionEvent event) {
		// user has entered the username and password for github
		// send that info to the controller so it can pull the files
		_controller.uiUsernamePasswordCallback(new Credentials(_usernameField.getText(), _passwordField.getText()));

		// disable the password submit button until pull is done
		_passwordSubmitButton.setDisable(true);
	}
	
	public void refreshSchedule(Schedule schedule) {
		//close the current stage
		_scheduleStage.close();
		//update the schedule
		_schedule = schedule;
		//open a new one with the refreshed schedule
		_scheduleStage = new ScheduleStage(schedule, "Current Schedule", _currentEmployee, _controller, this);
	}

	public void displayAvailable(Employee employee) {
		// called from the controller when an Employee object is ready for
		// display
		_availabilityStage = new AvailabilityStage("COMP110 TA Availability", employee, _controller, this);
		_availabilityStage.show();
	}

	public void displaySchedule(Schedule schedule) {
		// make sure valid schedule
		if (this._schedule == null){
			this.displayMessage("There is no schedule loaded yet.  A schedule must be loaded before swapping.");
			return;
		}
		
		_scheduleStage = new ScheduleStage(schedule, "Current Schedule", _currentEmployee, _controller, this);
		// once we have the schedule we can enable the other buttons
		// TODO perhaps changes this so that schedule is available from the
		// start
		_availabilityStage.getShowSwapAvailabilityButton().setDisable(false);
		_availabilityStage.getPerformSwapButton().setDisable(false);
	}

	public void displayPossibleSwaps() {
		// make sure valid schedule
		if (this._schedule == null){
			this.displayMessage("There is no schedule loaded yet. A schedule must be loaded before swapping.");
			return;
		}
		
		// show the potential swaps
		new ViewSwapsStage("Available for Swaps", _currentEmployee, _controller, this);
	}

	public Credentials getUsernamePassword() {
		return new Credentials(_usernameField.getText(), _passwordField.getText());
	}

	public void displayMessage(String message) {
		this.displayMessage(message, true);
	}
	
	public void displayMessage(String message, boolean error){
		// make sure it is always on main thread
		Platform.runLater(() -> {
			if (error){
				Alert alert = new Alert(Alert.AlertType.ERROR);
				alert.setHeaderText("Error");
				alert.setContentText(message);
				alert.setResizable(true);
				alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
				alert.showAndWait();
			}
			else{
				Alert alert = new Alert(Alert.AlertType.INFORMATION);
				alert.setHeaderText("Info");
				alert.setContentText(message);
				alert.setResizable(true);
				alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
				alert.showAndWait();				
			}
		});
	}
	

	// called whenever someone inputs an invalid onyen
	public void createNewEmployeeCSV(String onyen) {
		Stage dialogueBox = new Stage();
		dialogueBox.getIcons().add(new Image(getClass().getResource("karen.png").toString()));
		Group root = new Group();
		Scene scene = new Scene(root);

		VBox vbox = new VBox();
		vbox.setPadding(new Insets(10, 0, 0, 10));
		vbox.setSpacing(10);

		HBox hbox1 = new HBox();
		hbox1.setSpacing(10);
		hbox1.setAlignment(Pos.CENTER_LEFT);

		Label text = new Label(onyen + " does not have an availability object yet or the CSV is corrupt. Would you like to create a new one?");
		hbox1.getChildren().add(text);

		HBox hbox2 = new HBox();
		hbox2.setSpacing(10);
		hbox2.setAlignment(Pos.CENTER);

		Button yes = new Button("Yes");
		yes.defaultButtonProperty().bind(yes.focusedProperty());
		yes.setOnAction((event) -> {
			_currentEmployee = new Employee("", _availabilityStage.getOnyenField().getText(), 0, false, 0, new int[7][24]);
			_availabilityStage.renderAvailabilityStage(_currentEmployee);
			dialogueBox.close();
		});
		Button no = new Button("No");
		no.setOnAction((event) -> {
			dialogueBox.close();
			_availabilityStage.getOnyenField().setText("Enter onyen here");
			this.displayMessage("Try again, enter a valid onyen");
		});
		hbox2.getChildren().addAll(yes, no);

		vbox.getChildren().addAll(hbox1, hbox2);
		root.getChildren().add(vbox);
		dialogueBox.initModality(Modality.APPLICATION_MODAL);
		dialogueBox.setResizable(false);
		dialogueBox.setTitle("Invalid onyen");
		dialogueBox.setScene(scene);
		dialogueBox.sizeToScene();
		dialogueBox.showAndWait();
	}
	
	// only return a list of days that have scheduled shifts in them
	public List<String> getDaysList() {
		List<String> daysList = new ArrayList<String>();
		for (int day = 0; day < 7; day++) {
			for (int hour = 0; hour < this.getSchedule().getWeek().getShifts()[day].length; hour++) {
				// if at least one shift is populated
				if (this.getSchedule().getWeek().getShifts()[day][hour].size() > 0
						&& !daysList.contains(Week.dayString(day))) {
					daysList.add(Week.dayString(day));
				}
			}

		}
		return daysList;
	}

	public List<String> getHoursList(String day) {
		List<String> hoursList = new ArrayList<String>();
		int minHour = -1;
		// find min
		for (int i = 0; i < this.getSchedule().getWeek().getShifts()[Week.dayInt(day)].length; i++) {
			if (this.getSchedule().getWeek().getShift(Week.dayInt(day), i).size() > 0) {
				minHour = i;
				break;
			}
		}
		// find max
		int maxHour = -1;
		for (int i = this.getSchedule().getWeek().getShifts()[Week.dayInt(day)][minHour]
				.getHour(); i < this.getSchedule().getWeek().getShifts()[Week.dayInt(day)].length; i++) {
			if (this.getSchedule().getWeek().getShift(Week.dayInt(day), i).size() == 0) {
				break;
			}
			maxHour = i;
		}
		for (int i = this.getSchedule().getWeek().getShifts()[Week.dayInt(day)][minHour]
				.getHour(); i <= this.getSchedule().getWeek().getShifts()[Week.dayInt(day)][maxHour].getHour(); i++) {
			hoursList.add(((i % 12) == 0 ? 12 : (i % 12)) + " -- " + (((i + 1) % 12) == 0 ? 12 : ((i + 1) % 12)));
		}
		return hoursList;
	}

	public void githubPullResult(boolean success, String message) {
		if (success == true) {
			// login was successful
			// move to next stage
			// display a null employee because we dont have the onyen yet
			this.displayAvailable(null);
			// can close the password stage
			_passwordStage.close();
		} else {
			// pull failed...highly likely wrong username and password
			this.displayMessage("Unable to pull files from github. " + message + "\nVery likey wrong github username/password or no network connectivity.");
			// renable the submit button
			_passwordSubmitButton.setDisable(false);
		}
	}

	public void githubPushResult(boolean success, String message) {
		if (success == true) {
			// save was successful
			this.displayMessage("Save complete", false);
		} else {
			// push failed
			this.displayMessage("Unable to push files to github. " + message);
			// re-enable save button after push finishes
			_availabilityStage.getSaveAvailabilityButton().setDisable(false);	
		}
	}
	
	public void setSchedule(Schedule s){
		_schedule = s;
	}
	
	public Schedule getSchedule() {
		return _schedule;
	}
	
	public Employee getCurrentEmployee() {
		return _currentEmployee;
	}
	
	public Stage getScheduleStage() {
		return _scheduleStage;
	}
	
	public boolean getScheduleStageIsOpen() {
		return _scheduleStageIsOpen;
	}
	
	public void setScheduleStageIsOpen(boolean value) {
		_scheduleStageIsOpen = value;
	}
}
