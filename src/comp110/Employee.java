package comp110;

import java.io.Serializable;

public class Employee implements Serializable {

	// serialization version 
	private static final long serialVersionUID = 1L;

	// constants
	private static final int NUMBER_DAYS = 7;
	private static final int NUMBER_HOURS = 24;
	
	// variables
	private String m_name;
	private String m_onyen;
	private int m_capacity;
	private int m_capacityUsed;
	private boolean m_isFemale;
	private int m_level; // 1: in 401, 2: in 410/411, 3: in major
	private int[][] m_availability;

	// functions
	public Employee(String name, String onyen, int capacity, boolean isFemale, int level, int[][] availability) {
		this.m_onyen = onyen;
		this.m_availability = availability;
		this.m_name = name;
		this.m_capacity = capacity;
		this.m_isFemale = isFemale;
		this.m_level = level;
		this.m_capacityUsed = 0;
	}

	@Override
	public int hashCode() {
		return this.m_name.hashCode();
	}

	public boolean equals(Employee other) {
		if (this.m_name.equals(other.m_name) == false){
			return false;
		}
		if (this.m_capacity != other.m_capacity){
			return false;
		}
		if (this.m_isFemale != other.m_isFemale){
			return false;
		}
		if (this.m_level != other.m_level){
			return false;
		}
		for (int day = 0; day < NUMBER_DAYS; ++day) {
			for (int hour = 0; hour < NUMBER_HOURS; ++hour) {
				if (this.m_availability[day][hour] != other.m_availability[day][hour]) {
					return false;
				}
			}
		}
		return true;
	}

	public String getName() {
		return this.m_name;
	}

	public void setName(String name) {
		this.m_name = name;
	}

	public String getOnyen() {
		return this.m_onyen;
	}

	public int getCapacity() {
		return this.m_capacity;
	}

	public void setCapacity(int capacity) {
		this.m_capacity = capacity;
	}

	public int getCapacityUsed() {
		return this.m_capacityUsed;
	}

	void setCapacityUsed(int capacityUsed) {
		this.m_capacityUsed = capacityUsed;
	}

	public int getCapacityRemaining() {
		return this.m_capacity - this.m_capacityUsed;
	}

	public boolean getIsFemale() {
		return this.m_isFemale;
	}

	public void setIsFemale(boolean isFemale) {
		this.m_isFemale = isFemale;
	}

	public int getLevel() {
		return this.m_level;
	}

	public void setLevel(int level) {
		this.m_level = level;
	}

	public int[][] getAvailability() {
		return this.m_availability;
	}

	public void setAvailability(int[][] availability) {
		this.m_availability = availability;
	}

	public boolean isAvailable(int day, int hour) {
		return this.m_availability[day][hour] == 1 ? true : false;
	}

	public boolean isAvailable(int day, int startHour, int endHour) {
		for (int i = startHour; i <= endHour; i++) {
			if (this.m_availability[day][i] == 0) {
				return false;
			}
		}
		return true;
	}

	public Employee copy() {
		int[][] availability = new int[NUMBER_DAYS][NUMBER_HOURS];
		for (int day = 0; day < NUMBER_DAYS; ++day) {
			for (int hour = 0; hour < NUMBER_HOURS; ++hour) {
				availability[day][hour] = this.m_availability[day][hour];
			}
		}
		return new Employee(this.m_name, this.m_onyen, this.m_capacity, this.m_isFemale, this.m_level, availability);
	}

	@Override
	public String toString() {
		return this.m_name;
	}

}