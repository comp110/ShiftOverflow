package comp110;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import javafx.application.Platform;
import server.SController;
import ui.UI;

public class Controller {

  // variables
  private UI m_ui;
  private SController _sController;
  private Storage m_storage;
  private Parser m_parser;

  // functions
  public Controller(UI ui) {
    this.m_parser = new Parser();
    this.m_ui = ui;

    //test
    NameValuePair[] NV = {new NameValuePair("cmd", "swap")};
    this.makeRequest(NV);
  }

  public void makeRequest(NameValuePair[] postItems) {
    try {
      URL url = new URL("http://localhost:3650/server");
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("POST");
      connection.setDoOutput(true);
      PrintWriter out = new PrintWriter(connection.getOutputStream());
      for (int i = 0; i < postItems.length; i++){
        NameValuePair p = postItems[i];
        String NV = p.name + "=" + URLEncoder.encode(p.value, "UTF-8");
        if (i == postItems.length - 1){
          out.print(NV);
        }else{
          out.print(NV + "&");          
        }
      }
      out.close();
      BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
      String line;
      while ((line = in.readLine()) != null) {
        System.out.println(line);
      }
      in.close();
    } catch (Exception e) {

    }
  }

  public void cleanup() {
    this.m_storage.delete_storage();
  }

  public void uiUsernamePasswordCallback(String user, String pass) {
    // save username and password for github
    NameValuePair[] NV = {new NameValuePair("cmd", "login"), new NameValuePair("user", user), new NameValuePair("pass", pass)};
    this.makeRequest(NV); 
  }

  public void storage_save_files_complete(boolean success, String message) {
    // let the ui know
    Platform.runLater(() -> this.m_ui.githubPushResult(success, message));
  }

  public void uiRequestSchedule() {
    // tell the ui to show the schedule...this can/will be null if there was an exception
    // ui needs to be ready to handle null schedule
    try {
      Schedule schedule = this.m_parser.parseSchedule(this.m_storage.get_schedule_json_filename(), this.m_storage.get_path_to_onyen_csv_directory());
      this.m_ui.setSchedule(schedule);
      Platform.runLater(() -> this.m_ui.displaySchedule(schedule));
    } catch (Exception e) {
      Platform.runLater(() -> this.m_ui.displayMessage("Controller::uiRequestSchedule(): " + e.toString()));
    }
  }

  public void uiRequestEmployeeAvailability(String onyen) {
    // parse the employee
    try {
      Employee employee = this.m_parser.parseEmployee(this.m_storage.get_availability_csv_filename_from_onyen(onyen));
      //display available object on ui
      Platform.runLater(() -> this.m_ui.displayAvailable(employee));
    } catch (Exception e) {
      this.m_ui.createNewEmployeeCSV(onyen);
    }
  }

  public void uiRequestSwaps() {
    Platform.runLater(() -> this.m_ui.displayPossibleSwaps());
  }

  public void uiRequestSaveAvailability(Employee employee, String commit_message) {
    // need to tell parser to save this employee object and what filename to save it as
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

  public void uiRequestChangeSchedule(Schedule schedule, String commit_message) {
    try {
      this.m_parser.writeScheduleToJson(schedule, this.m_storage.get_schedule_json_filename());
      this.m_storage.save_files(commit_message);
    } catch (Exception e) {
      this.m_ui.displayMessage("Controller::uiRequestChangeSchedule(): " + e.toString());
    }
  }

  public void displayMessage(String message) {
    this.m_ui.displayMessage(message);
  }
}
