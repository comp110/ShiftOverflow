package server;

import comp110.Credentials;
import comp110.Employee;
import comp110.Parser;
import comp110.Schedule;
import comp110.Storage;
import comp110.Storage.StorageListener;
import javafx.application.Platform;
import ui.UI;

public class SController implements StorageListener {
  
  private UI m_ui;
  private SController _sController;
  private Storage m_storage;
  private Parser m_parser;
  private Server _server;
  
  public SController(){
    try{
      _server = new Server(3600);  
    }catch(Exception e){
      System.out.println("Failed to start server.");
    }
    
    this.m_storage = new Storage(this, ".");
    this.m_parser = new Parser();
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
  
    public void storage_get_files_complete(boolean success, String message){
    // let the ui know of success or failure
    Platform.runLater(() -> this.m_ui.githubPullResult(success, message));
    
    // go ahead and give them the schedule immediately for use
    try{
      Schedule schedule = this.m_parser.parseSchedule(this.m_storage.get_schedule_json_filename(), this.m_storage.get_path_to_onyen_csv_directory());
      this.m_ui.setSchedule(schedule);
    } catch (Exception e){
      // tell the ui things suck
      this.m_ui.setSchedule(null);
      Platform.runLater(() -> this.m_ui.displayMessage("Controller::storage_get_files_complete(): " + e.toString()));
    }
    }
    
    public void storage_save_files_complete(boolean success, String message){
    // let the ui know
    Platform.runLater(() -> this.m_ui.githubPushResult(success, message));      
    }

  public void uiRequestSchedule() {
    // tell the ui to show the schedule...this can/will be null if there was an exception
    // ui needs to be ready to handle null schedule
    try{
      Schedule schedule = this.m_parser.parseSchedule(this.m_storage.get_schedule_json_filename(), this.m_storage.get_path_to_onyen_csv_directory());
      this.m_ui.setSchedule(schedule);
      Platform.runLater(() -> this.m_ui.displaySchedule(schedule));
    } catch (Exception e){
      Platform.runLater(() -> this.m_ui.displayMessage("Controller::uiRequestSchedule(): " + e.toString()));
    }
  }

  public void uiRequestEmployeeAvailability(String onyen) {
    // parse the employee
    try{
      Employee employee = this.m_parser.parseEmployee(this.m_storage.get_availability_csv_filename_from_onyen(onyen));
      //display available object on ui
      Platform.runLater(() -> this.m_ui.displayAvailable(employee));  
    } catch (Exception e){
      this.m_ui.createNewEmployeeCSV(onyen);
    }
  }

  public void uiRequestSwaps() {
    Platform.runLater(() -> this.m_ui.displayPossibleSwaps());
  }

  public void uiRequestSaveAvailability(Employee employee, String commit_message) {
    // need to tell parser to save this employee object and what filename to save it as
    if (employee == null){
      // unable to save
      this.m_ui.displayMessage("Controller::uiRequestSaveAvailability(): Employee is null");
      return;
    }
    
    String filename = this.m_storage.get_availability_csv_filename_from_onyen(employee.getOnyen());
    try{
      // have the parser write out the file
      this.m_parser.writeFile(employee, filename);
      // have storage push to the repo
      this.m_storage.save_files(commit_message);
    } catch (Exception e){
      this.m_ui.displayMessage("Controller::uiRequestSaveAvailability(): " + e.toString());
    }
  }
  
  public void uiRequestChangeSchedule(Schedule schedule, String commit_message){
    try{
    this.m_parser.writeScheduleToJson(schedule, this.m_storage.get_schedule_json_filename());
    this.m_storage.save_files(commit_message);
    } catch (Exception e){
      this.m_ui.displayMessage("Controller::uiRequestChangeSchedule(): " + e.toString());
    }
  }
  
  public void displayMessage(String message) {
    this.m_ui.displayMessage(message);
  }
}

