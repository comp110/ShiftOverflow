package comp110;

import java.io.Serializable;

public class Week implements Serializable {

	// version for serialization
	private static final long serialVersionUID = 1L;
	
	// constants
	private static final int NUMBER_DAYS = 7;
	private static final int NUMBER_HOURS = 24;
	
	// variables
	private String m_title;
	private Shift[][] m_shifts;

	// functions
	public Week(String title) {
		this.m_title = title;
		this.m_shifts = new Shift[NUMBER_DAYS][NUMBER_HOURS];
		for (int day = 0; day < NUMBER_DAYS; ++day) {
			for (int hour = 0; hour < NUMBER_HOURS; ++hour) {
				this.m_shifts[day][hour] = new Shift(day, hour, 0);
			}
		}
	}

	public boolean equals(Week other) {
		if (this.m_title.equals(other.m_title)){
			for (int day = 0; day < NUMBER_DAYS; ++day) {
				for (int hour = 0; hour < NUMBER_HOURS; ++hour) {
					if (this.m_shifts[day][hour].equals(other.m_shifts[day][hour]) == false) {
						return false;
					}
				}
			}
			return true;
		}
		return false;
	}

	public String getTitle() {
		return this.m_title;
	}

	public void setTitle(String title) {
		this.m_title = title;
	}

	public Shift[][] getShifts() {
		return this.m_shifts;
	}

	public Shift getShift(int day, int hour) {
		return this.m_shifts[day][hour];
	}

	public double getScheduledHours() {
		double hours = 0.0;
		for (Shift[] day : this.m_shifts) {
			for (Shift shift : day) {
				hours += shift.size();
			}
		}
		return hours;
	}

	public int getNumberOfShifts() {
		int shifts = 0;
		for (Shift[] day : this.m_shifts) {
			for (Shift shift : day) {
				if (shift.getCapacity() > 0) {
					shifts++;
				}
			}
		}
		return shifts;
	}

	public static String dayString(int day) {
		switch (day) {
		case 0:
			return "Sunday";
		case 1:
			return "Monday";
		case 2:
			return "Tuesday";
		case 3:
			return "Wednesday";
		case 4:
			return "Thursday";
		case 5:
			return "Friday";
		case 6:
			return "Saturday";
		default:
			return "Unknown [day=" + day + "]";
		}
	}

	public static int dayInt(String day) {
		switch (day) {
		case "Sunday":
			return 0;
		case "Monday":
			return 1;
		case "Tuesday":
			return 2;
		case "Wednesday":
			return 3;
		case "Thursday":
			return 4;
		case "Friday":
			return 5;
		case "Saturday":
			return 6;
		default:
			return -1;
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append("** ");
		sb.append(this.m_title);
		sb.append(" **\n");

		for (int day = 0; day < NUMBER_DAYS; ++day) {
			sb.append(Week.dayString(day) + "\n");
			for (Shift shift : this.m_shifts[day]) {
				if (shift.getCapacity() > 0) {
					sb.append("\t" + shift + "\n");
				}
			}
		}

		return sb.toString();
	}

	public Week copy() {
		Week copy = new Week(this.m_title);
		copy.m_shifts = new Shift[NUMBER_DAYS][NUMBER_HOURS];
		for (int day = 0; day < NUMBER_DAYS; ++day) {
			for (int hour = 0; hour < NUMBER_HOURS; ++hour) {
				copy.m_shifts[day][hour] = this.m_shifts[day][hour].copy();
			}
		}
		return copy;
	}

	public String toCSV() {
		StringBuilder sb = new StringBuilder();
		for (int hour = 0; hour < NUMBER_HOURS; ++hour) {
			sb.append(hour + ",");
			for (int day = 0; day < NUMBER_DAYS; ++day) {
				if (this.m_shifts[day][hour].getCapacity() > 0) {
					sb.append("1,");
				} else {
					sb.append("0,");
				}
			}
			sb.append("\n");
		}
		return sb.toString();
	}

	public JsonWeek toJsonWeek() {
		return new JsonWeek(this.m_shifts);
	}
}