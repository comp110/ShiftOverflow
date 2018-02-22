package comp110;

import java.util.List;

import javafx.application.Platform;
import ui.UI;

public class Controller implements Storage.StorageListener {

	// variables
	private UI m_ui;
	private Storage m_storage;
	private Parser m_parser;

	// functions
	public Controller(UI ui) {
		this.m_storage = new Storage(this, ".");
		this.m_parser = new Parser();
		this.m_ui = ui;
	}

	public void cleanup() {
		this.m_storage.delete_storage();
	}

	public void uiUsernamePasswordCallback(Credentials credentials) {
		// save username and password for github
		this.m_storage.set_username(credentials.getUsername());
		this.m_storage.set_password(credentials.getPassword());

		// pull files from github
		this.m_storage.get_files();
	}

	public void storage_get_files_complete(boolean success, String message) {
		// let the ui know of success or failure
		Platform.runLater(() -> this.m_ui.githubPullResult(success, message));
		// check version
		Platform.runLater(() -> {
			this.m_ui.checkVersion(
					this.m_parser.parseCurrentVersion(this.m_storage.get_shift_overflow_version_filename()));
		});
		// go ahead and give them the schedule immediately for use
		try {
			List<Schedule> schedules = this.m_parser.parseSchedules(this.m_storage.get_schedule_json_folder(),
					this.m_storage.get_path_to_onyen_csv_directory(), this.m_storage.get_schedule_leads_filename());
			this.m_ui.setSchedules(schedules);
		} catch (Exception e) {
			// tell the ui things suck
			this.m_ui.setSchedules(null);
			//Only want one error message
			/*Platform.runLater(
					() -> this.m_ui.displayMessage("Controller::storage_get_files_complete(): " + e.toString()));*/
		}
	}

	public void storage_save_files_complete(boolean success, String message) {
		// let the ui know
		Platform.runLater(() -> this.m_ui.githubPushResult(success, message));
	}

	public void uiRequestSchedule() {
		// tell the ui to show the schedule...this can/will be null if there was
		// an exception
		// ui needs to be ready to handle null schedule
		try {
			List<Schedule> schedules = this.m_parser.parseSchedules(this.m_storage.get_schedule_json_folder(),
					this.m_storage.get_path_to_onyen_csv_directory(), this.m_storage.get_schedule_leads_filename());
			this.m_ui.setSchedules(schedules);
			Platform.runLater(() -> this.m_ui.displaySchedule(schedules));
		} catch (Exception e) {
			Platform.runLater(() -> this.m_ui.displayMessage("Controller::uiRequestSchedule(): " + e.toString()));
		}
	}

	public void uiRequestEmployeeAvailability(String onyen) {
		// parse the employee
		try {
			Employee employee = this.m_parser
					.parseEmployee(this.m_storage.get_availability_csv_filename_from_onyen(onyen));
			// display available object on ui
			Platform.runLater(() -> this.m_ui.displayAvailable(employee));
		} catch (Exception e) {
			this.m_ui.createNewEmployeeCSV(onyen);
		}
	}

	public void uiRequestSwaps() {
		Platform.runLater(() -> this.m_ui.displayPossibleSwaps());
	}

	public void uiRequestSaveAvailability(Employee employee, String commit_message) {
		// need to tell parser to save this employee object and what filename to
		// save it as
		if (employee == null) {
			// unable to save
			this.m_ui.displayMessage("Controller::uiRequestSaveAvailability(): Employee is null");
			return;
		}

		String filename = this.m_storage.get_availability_csv_filename_from_onyen(employee.getOnyen());
		try {
			// have the parser write out the file
			this.m_parser.writeFile(employee, filename);
			// have storage push to the repo
			this.m_storage.save_files(commit_message);
		} catch (Exception e) {
			this.m_ui.displayMessage("Controller::uiRequestSaveAvailability(): " + e.toString());
		}
	}

	public void uiRequestChangeSchedule(List<Schedule> schedules, String commit_message) {
		try {
			this.m_parser.writeScheduleToJson(schedules, this.m_storage.get_schedule_json_folder());
			this.m_storage.save_files(commit_message);
		} catch (Exception e) {
			this.m_ui.displayMessage("Controller::uiRequestChangeSchedule(): " + e.toString());
		}
	}

	public void displayMessage(String message) {
		this.m_ui.displayMessage(message);
	}
}
