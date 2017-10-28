package comp110;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import com.google.gson.Gson;
import java.io.PrintWriter;

/* Created by Keith Whitley */

public class Parser {

	// constants
	private static final int NUMBER_DAYS = 7;
	private static final int NUMBER_HOURS = 24;

	// variables

	// functions
	public Parser() {

	}

	public Employee parseEmployee(String file) throws Exception {
		File csv = new File(file);
		BufferedReader csvReader = null;
		String onyen = csv.getName().substring(0, csv.getName().length() - 4);
		String name = "";
		String gender = "";
		int capacity = 0;
		int level = 0;
		try {
			csvReader = new BufferedReader(new FileReader(csv));
			// parsing employee info
			String[] s = csvReader.readLine().split(",");
			name = (s.length >= 2) ? s[1] : ""; // not as ugly
			// name = csvReader.readLine().split(",")[1]; ugly
			gender = csvReader.readLine().split(",")[1];
			capacity = Integer.parseInt(csvReader.readLine().split(",")[1]);
			level = Integer.parseInt(csvReader.readLine().split(",")[1]);
		} catch (Exception e) {
			throw new Exception("Parser::parseEmployee(): Error parsing csv file=" + file);
		}

		try {
			csvReader.readLine();
		} catch (IOException e1) {
			csvReader.close();
			throw new Exception("Parser::parseEmployee(): Error parsing csv file=" + file);
		} // throw away header line with days

		// read in schedule
		int[][] availability = new int[7][24];
		for (int hour = 0; hour < 24; hour++) {
			String scheduleLine = "";
			try {
				scheduleLine = csvReader.readLine();
			} catch (IOException e1) {
				csvReader.close();
				throw new Exception("Parser::parseEmployee(): Error parsing csv file=" + file);
			}
			for (int day = 0; day < 7; day++) {
				// Offset by 1 accounts for label in CSV
				try {
					availability[day][hour] = Integer.parseInt(scheduleLine.split(",")[day + 1]);
				} catch (NumberFormatException e) {
					csvReader.close();
					throw new Exception("Parser::parseEmployee(): Error parsing csv file=" + file);
				}
			}
		}

		csvReader.close();
		return new Employee(name, onyen, capacity, gender.equals("M") ? false : true, level, availability);
	}

	public List<Schedule> parseSchedules(String scheduleFolderPath, String staff_dir, String leadsFile)
			throws Exception {

		List<Schedule> schedules = new ArrayList<Schedule>();

		// read in the json file
		Gson gson = new Gson();
		String json = "";

		File scheduleFilePath = null;
		try {
			scheduleFilePath = new File(scheduleFolderPath);

		} catch (Exception e) {
			System.err.println("error in parseSchedules");
			throw new Exception("Parser::parseSchedule(): Error reading schedule folder=" + scheduleFolderPath);
		}
		for (File scheduleJson : scheduleFilePath.listFiles()) {
			Scanner scanner = null;
			try {
				scanner = new Scanner(scheduleJson);
				scanner.useDelimiter("\\Z");
				json = scanner.nextLine();
			} catch (Exception e) {
				System.err.println(e.toString());
				throw new Exception(
						"Parser::parseSchedule(): Error reading schedule file= " + scheduleJson.getAbsolutePath());
			}
			scanner.close();

			// create the json week object
			JsonWeek jsonweek = gson.fromJson(json, JsonWeek.class);
			if (jsonweek == null) {
				throw new Exception(
						"Parser::parseSchedule(): Error parsing json file ito jsonweek. File=" + scheduleFolderPath);
			}
			if (jsonweek.getShifts() == null) {
				// some problem parsing json
				throw new Exception(
						"Parser::parseSchedule(): Error parsing json file ito jsonweek. File=" + scheduleFolderPath);
			}

			// now we have the json we need to reconstruct the schedule object
			// first we will build the staff object
			Staff staff = null;
			try {
				staff = parseStaff(staff_dir);
			} catch (Exception e) {
				throw new Exception("Parser::parseSchedule(): " + e.toString());
			}

			// now build the week object
			Week week = new Week("Current Schedule");

			// grab a reference to the shifts array
			Shift[][] shifts = week.getShifts();
			for (int day = 0; day < NUMBER_DAYS; day++) {
				for (int hour = 0; hour < NUMBER_HOURS; hour++) {
					for (int i = 0; i < jsonweek.getShifts()[day][hour].getScheduled().length; i++) {
						String onyen = jsonweek.getShifts()[day][hour].getScheduled()[i];
						Employee employee = this.getEmployeeByOnyen(onyen, staff);
						if (employee == null) {
							throw new Exception(
									"Parser::parseSchedule(): No Employee in Staff for Onyen in Schdeule. Onyen="
											+ onyen);
						}
						// add the employee to the shift
						shifts[day][hour].add(employee);
					}
				}
			}
			Leads leads = this.parseLeads(leadsFile, staff);
			schedules.add(new Schedule(staff, week, leads,
					scheduleJson.getName().substring(9, scheduleJson.getName().length()).split("\\.")[0])); // grab
																											// just
																											// the
																											// date
																											// portion
																											// of
																											// file
																											// name
		}

		if (schedules.size() == 0) {
			throw new Exception("Parser::parseSchedule(): No schedules found to load in: " + scheduleFolderPath);
		} else {
			return schedules;
		}
	}

	public void writeFile(Employee employee, String filename) throws Exception {

		// check if employee is null first
		if (employee == null) {
			throw new Exception("Parser::writeFile(): Employee is null");
		}

		// write to file
		PrintWriter fw = null;
		try {
			// System.out.println("Writing Schedule to" + filename);
			fw = new PrintWriter(filename);
			StringBuilder sb = new StringBuilder();

			// Name
			sb.append("Name:,");
			sb.append(employee.getName());
			sb.append(",,,,,,\n");

			// Gender
			sb.append("Gender (enter M or F):,");

			if (employee.getIsFemale()) {
				sb.append("F");
			} else {
				sb.append("M");
			}
			sb.append(",,,,,,\n");

			// Capacity
			sb.append("Capacity:,");
			sb.append(employee.getCapacity());
			sb.append(",,,,,,\n");

			// Level
			sb.append("Level (1 - in 401; 2 - in 410/411; 3 - in major),");
			sb.append(employee.getLevel());
			sb.append(",,,,,,\n");

			// Week
			sb.append(",0. Sun,1. Mon,2. Tue,3. Weds,4. Thu,5. Fri,6. Sat\n");

			// Schedule
			int[][] avail = employee.getAvailability();

			// for loop is backwards to write line by line
			for (int hour = 0; hour < 24; hour++) {
				sb.append(hour);
				for (int day = 0; day < avail.length; day++) {
					sb.append(',');
					sb.append(avail[day][hour]);
				}
				sb.append('\n');
			}
			fw.write(sb.toString());
			fw.close();
		} catch (Exception e) {
			// unable to open file
			if (fw != null) {
				fw.close();
			}
			throw new Exception("Parser::writeFile(): Unable to open file for writing. File=" + filename);
		}
	}

	private Staff parseStaff(String dir) throws Exception {
		// create staff object
		Staff staff = new Staff();

		// get a file object to the directory containing all the csv's
		File csvDirectory = new File(dir);

		// create an employee from each csv
		for (File csv : csvDirectory.listFiles()) {
			Employee employee = null;
			try {
				employee = parseEmployee(csv.getAbsolutePath());
			} catch (Exception e) {
				// unable to parse employee
				throw new Exception("Parser::parseStaff(): " + e.toString());
			}
			// add the employee to the staff
			staff.add(employee);
		}
		// return staff
		return staff;
	}

	private Employee getEmployeeByOnyen(String onyen, Staff staff) {
		// search for the onyen
		for (Employee employee : staff) {
			if (employee.getOnyen().equals(onyen)) {
				return employee;
			}
		}
		// not able to find employee
		return null;
	}

	private Leads parseLeads(String leadsFile, Staff staff) throws Exception {
		BufferedReader reader = null;
		Leads leads = new Leads(staff);
		try {
			File file = new File(leadsFile);
			reader = new BufferedReader(new FileReader((file)));
			while (reader.ready()) {
				leads.add(reader.readLine().split(",")[0]);
			}
			reader.close();
		} catch (FileNotFoundException e) {
			throw new Exception("Parser::parseSchedule(): Error reading leads file=" + leadsFile);
		}

		if (leads.size() == 0) {
			throw new Exception("Parser::parseSchedule(): Error parsing leads file to Leads object. File=" + leadsFile);
		}

		return leads;
	}

	public void writeScheduleToJson(List<Schedule> schedules, String path) throws Exception {
		if (schedules.size() == 0) {
			throw new Exception("Parser::writeScheduleToJson(): Schedule list is empty");
		}
		if (path.equals("") || path == null) {
			throw new Exception("Parser::writeScheduleToJson(): Path is invalid");
		}

		// create gson object
		for (Schedule schedule : schedules) {
			try {
				Gson gson = new Gson();
				JsonWeek jsonWeek = schedule.getWeek().toJsonWeek();
				File f = new File(path + "schedule_" + schedule.getDatesValid() + ".json");
				BufferedOutputStream writer = new BufferedOutputStream(new FileOutputStream(f), 65536);
				String json = gson.toJson(jsonWeek);
				writer.write(json.getBytes());
				writer.close();
			} catch (Exception e) {
				throw new Exception("Parser::writeScheduleToJson(): Unable to open file=" + path);
			}
		}

	}

	public String parseCurrentVersion(String versionPath) {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(versionPath));
		} catch (FileNotFoundException e) {
			return ""; // if we can't find the file just carry on
		}
		String currentVersion = null;
		try {
			currentVersion = reader.readLine();
			reader.close();
		} catch (IOException e) { // This should never happen
			System.err.println("lol this should never happen");
			e.printStackTrace();
		}
		return currentVersion;
	}
}
