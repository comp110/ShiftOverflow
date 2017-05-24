package comp110;

import java.io.Serializable;

public class Schedule implements Serializable {

	// serialization version 
	private static final long serialVersionUID = 1L;
	
	
	// variables
	private Staff m_staff;
	private Week m_week;
	
	// functions
	public Schedule(Staff staff, Week week) {
		this.m_staff = staff;
		this.m_week = week;
	}

	public Staff getStaff() {
		return this.m_staff;
	}

	public Week getWeek() {
		return this.m_week;
	}

	public boolean equals(Schedule other) {
		return this.m_staff.equals(other.m_staff) && this.m_week.equals(other.m_week);
	}

	public Schedule copy() {
		return new Schedule(this.m_staff.copy(), this.m_week.copy());
	}

}
