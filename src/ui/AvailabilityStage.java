package ui;

import comp110.Controller;
import comp110.Week;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;

public class AvailabilityStage extends KarenStage {

  private TextField        _onyenField;
  private Button           _showSwapAvailabilityButton;
  private Button           _performSwapButton;
  private GridPane         _grid;
  private Button           _saveAvailabilityButton;
  private PerformSwapStage _performSwapStage;

  public AvailabilityStage(String title, Controller controller, UI ui) {
    super(title, controller, ui);
    this.setOnCloseRequest(event -> {
      // call the controller cleanup
      controller.cleanup();

      try {
        // give time for cleanup to complete
        Thread.sleep(2000);
      } catch (Exception e) {
        /* dont care about an exception here */}
      // since we cleanup we need to terminate because program can no longer
      // do anything useful without the data
      System.exit(0);
    });
    this.renderAvailabilityStage();
  }

  public Button getSaveAvailabilityButton() {
    return _saveAvailabilityButton;
  }

  // called whenever we change the current employee
  public void renderAvailabilityStage() {
    Group availabilityRoot = new Group();
    Scene availabilityScene = new Scene(availabilityRoot);
    BorderPane rootPane = new BorderPane();
    VBox topBox = new VBox();
    topBox.setAlignment(Pos.CENTER);
    rootPane.setTop(topBox);

    HBox topBar = new HBox();
    // populate fields with employee
    if (_ui.getCurrentEmployee() != null) {
      _onyenField = new TextField(_ui.getCurrentEmployee().getOnyen());
    } else {
      _onyenField = new TextField("Enter onyen here");
      _onyenField.setOnKeyPressed((event) -> {
        if (event.getCode() == KeyCode.ENTER) {
          getAvailability(null);
        }
      });

      _onyenField.focusedProperty().addListener((observable, oldValue, newValue) -> {
        if (newValue) {
          // just gained focus...text select entire text
          // so when we start typing it will overwrite it
          Platform.runLater(() -> AvailabilityStage.this._onyenField.selectAll());
        }
      });
    }
    topBar.getChildren().add(_onyenField);
    topBox.getChildren().add(topBar);

    // create button to get availability
    Button getAvailabilityButton = new Button("Get Availability");
    topBar.getChildren().add(getAvailabilityButton);
    getAvailabilityButton.setOnAction(this::getAvailability);

    // create button to show current schedule
    Button showScheduleButton = new Button("Show Current Schedule");
    showScheduleButton.setOnAction(this::requestScheduleButtonPressed);
    topBar.getChildren().add(showScheduleButton);

    // create button to show the swap stage stuff
    _showSwapAvailabilityButton = new Button("Show Swaps");
    // _showSwapAvailabilityButton.setDisable(true);

    // request controller to give it the information for the swaps
    _showSwapAvailabilityButton.setOnAction((event) -> _controller.uiRequestSwaps());
    topBar.getChildren().add(_showSwapAvailabilityButton);

    // create button to do the swap stage stuff
    _performSwapButton = new Button("Swap");
    _performSwapButton.setPrefWidth(54);

    // _performSwapButton.setDisable(true);

    // start the perform swap stage
    _performSwapButton.setOnAction((event) -> {
      // make sure valid schedule
      if (_ui.getSchedule() == null) {
        _controller.displayMessage(
            "There is no schedule loaded yet. A schedule must be loaded before swapping.");
        return;
      } else {
        // check to close any existing open swap stage
        if (_performSwapStage != null) {
          _performSwapStage.close();
        }
        // let them swap
        _performSwapStage = new PerformSwapStage("Perform Swap", _controller, _ui);
      }

    });

    // middle bar with employee demographic info and swap button
    HBox middleBar = new HBox();
    topBox.getChildren().add(middleBar);

    final TextField nameField = new TextField();
    nameField.focusedProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue) {
        // just gained focus...text select entire text
        // so when we start typing it will overwrite it
        Platform.runLater(new Runnable() {
          @Override
          public void run() {
            nameField.selectAll();
          }
        });
      }
    });
    if (_ui.getCurrentEmployee() != null) {
      nameField.setText(_ui.getCurrentEmployee().getName());
    } else {
      nameField.setText("Name");
    }
    nameField.textProperty().addListener((observable, oldValue, newValue) -> {
      if (_ui.getCurrentEmployee() != null) {
        _ui.getCurrentEmployee().setName(newValue);
      }
    });
    ComboBox<String> genderDropdown = new ComboBox<String>();
    if (_ui.getCurrentEmployee() != null) {
      genderDropdown.getSelectionModel()
          .select(_ui.getCurrentEmployee().getIsFemale() ? "Female" : "Male");
    }
    genderDropdown.getItems().addAll("Male", "Female");
    genderDropdown.getSelectionModel().selectedItemProperty()
        .addListener((observable, oldValue, newValue) -> {
          if (_ui.getCurrentEmployee() != null) {
            _ui.getCurrentEmployee()
                .setIsFemale(newValue.equals("Female") ? true : false);
            _saveAvailabilityButton.setDisable(false);
          }

        });

    ComboBox<Integer> capacityDropdown = new ComboBox<Integer>();
    for (int i = 1; i <= 10; i++) {
      capacityDropdown.getItems().add(i);
    }
    if (_ui.getCurrentEmployee() != null) {
      // have to -1 because it is pulling by index and list is zero
      // indexed
      capacityDropdown.getSelectionModel()
          .select(_ui.getCurrentEmployee().getCapacity() - 1);
    }
    capacityDropdown.getSelectionModel().selectedItemProperty()
        .addListener((observable, oldValue, newValue) -> {
          if (_ui.getCurrentEmployee() != null) {
            _ui.getCurrentEmployee().setCapacity(newValue);
            _saveAvailabilityButton.setDisable(false);
          }

        });

    ComboBox<String> levelDropdown = new ComboBox<String>();
    levelDropdown.getItems().addAll("1 - In 401", "2 - In 410/411", "3 - In Major");
    if (_ui.getCurrentEmployee() != null) {
      // have to -1 because it is pulling by index and list is zero
      // indexed
      levelDropdown.getSelectionModel().select(_ui.getCurrentEmployee().getLevel() - 1);
    }
    levelDropdown.getSelectionModel().selectedItemProperty()
        .addListener((observable, oldValue, newValue) -> {
          // grab the level # and use it to update employee
          if (_ui.getCurrentEmployee() != null) {
            _ui.getCurrentEmployee().setLevel(Integer.parseInt(newValue.split(" ")[0]));
            _saveAvailabilityButton.setDisable(false);
          }

        });

    middleBar.getChildren().addAll(nameField, genderDropdown, capacityDropdown,
        levelDropdown, _performSwapButton);

    // this grid contains the checkboxes to mark availability
    _grid = new GridPane();
    _grid.setGridLinesVisible(true);

    for (int day = 0; day < 8; day++) {
      // 8 to account for first column with hours
      if (day != 0) {
        // all labels are -1 offset
        Label dayLabel = new Label(Week.dayString(day - 1));
        dayLabel.setMaxWidth(Double.MAX_VALUE);
        dayLabel.setAlignment(Pos.CENTER);
        _grid.add(dayLabel, day, 0);
      }
      for (int hour = 0; hour < 12; hour++) {
        HBox box = new HBox();
        if (day == 0) {
          // if we are at hour column write out hour labels
          int time = (hour + 9) % 12;
          TextFlow timeLabel = new TextFlow(new Text((time % 12 == 0 ? 12 : time) + " -- "
              + ((time + 1) % 12 == 0 ? 12 : time + 1)));
          timeLabel.setTextAlignment(TextAlignment.CENTER);
          _grid.add(timeLabel, day, hour + 1);

        } else {
          TimedCheckBox check = new TimedCheckBox(day - 1, hour + 9);
          if (_ui.getCurrentEmployee() != null) {
            if (_ui.getCurrentEmployee().isAvailable(day - 1, hour + 9)) {
              // map day and hour onto our space
              check.setSelected(true);
              box.setBackground(
                  new Background(new BackgroundFill(Color.GREEN, null, null)));
            }
          }
          // when checked handoff to handleCheck
          check.setOnAction(this::handleCheck);
          box.getChildren().add(check);
          box.setAlignment(Pos.CENTER);
          box.setMinHeight(30);
          box.setMinWidth(60);
          _grid.add(box, day, hour + 1); // +1 to account for header
          // row
        }
      }
    }
    rootPane.setCenter(_grid);

    // create the save button
    HBox bottomBar = new HBox();
    _saveAvailabilityButton = new Button("Save");
    _saveAvailabilityButton.setDisable(true);
    _saveAvailabilityButton.setPrefWidth(465);
    _saveAvailabilityButton.setOnAction(this::saveButtonPressed);
    bottomBar.getChildren().add(_saveAvailabilityButton);
    rootPane.setBottom(bottomBar);

    availabilityRoot.getChildren().add(rootPane);
    this.setScene(availabilityScene);
    this.sizeToScene();
    this.setResizable(false);
  }

  // called whenever the user presses enter to load an employee object
  private void getAvailability(ActionEvent event) {
    // ask the controller to load an employee availability file based on the
    // onyen
    String onyen = _onyenField.getText();
    if ((onyen.equals("") == true) || (onyen.equals("Enter onyen here")) == true) {
      // need to put an onyen in first
      _controller.displayMessage("Please first enter an onyen");
    } else {
      // ask controller to load it
      _controller.uiRequestEmployeeAvailability(_onyenField.getText());
    }
  }

  private void requestScheduleButtonPressed(ActionEvent e) {
    // ask the controller for the schedule
    _controller.uiRequestSchedule();
  }

  private void handleCheck(ActionEvent event) {
    TimedCheckBox check = (TimedCheckBox) event.getSource();
    if (_ui.getCurrentEmployee() == null) {
      _controller.displayMessage("You must first load an Employee");
      // unselect the one just selected
      check.setSelected(false);
      return;
    }
    // now that the schedule has changed we can let the person save
    _saveAvailabilityButton.setDisable(false);
    int[][] updatedAvailability = _ui.getCurrentEmployee().getAvailability();
    // System.out.println("Day: " + check.getDay() + " Hour: " +
    // check.getHour());
    HBox parent = (HBox) check.getParent();
    if (check.isSelected()) {
      parent.setBackground(new Background(new BackgroundFill(Color.GREEN, null, null)));
      updatedAvailability[check.getDay()][check.getHour()] = 1;
    } else {
      parent.setBackground(new Background(new BackgroundFill(Color.RED, null, null)));
      updatedAvailability[check.getDay()][check.getHour()] = 0;

    }
    _ui.getCurrentEmployee().setAvailability(updatedAvailability);
  }

  private void saveButtonPressed(ActionEvent event) {
    // need to send to controller to save the current modified Employee
    // object
    // disable the save button so they can't save again until they make
    // another change
    _saveAvailabilityButton.setDisable(true);
    if (_ui.getCurrentEmployee() != null) {
      _controller.uiRequestSaveAvailability(_ui.getCurrentEmployee(),
          "AVAILABILITY CHANGE: " + _ui.getCurrentEmployee().getName());
    }
  }

  public Button getShowSwapAvailabilityButton() {
    return _showSwapAvailabilityButton;
  }

  public Button getPerformSwapButton() {
    return _performSwapButton;
  }

  public TextField getOnyenField() {
    return _onyenField;
  }

}
