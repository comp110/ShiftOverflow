package comp110;

import java.io.Serializable;

public class Schedule implements Serializable {

	// serialization version 
	private static final long serialVersionUID = 1L;
	
	
	// variables
	private Staff m_staff;
	private Week m_week;
	private Leads m_leads;
	private String m_datesValid;
	
	// functions
	public Schedule(Staff staff, Week week, Leads leads, String datesValid) {
		this.m_staff = staff;
		this.m_week = week;
		this.m_leads = leads;
		this.m_datesValid = datesValid;
		this.computeShiftLeads();
	}

	public Staff getStaff() {
		return this.m_staff;
	}

	public Week getWeek() {
		return this.m_week;
	}
	
	public Leads getLeads() {
		return this.m_leads;
	}
	
	public String getDatesValid() {
		return this.m_datesValid;
	}

	public boolean equals(Schedule other) {
		return this.m_staff.equals(other.m_staff) && this.m_week.equals(other.m_week);
	}

	public Schedule copy() {
		return new Schedule(this.m_staff.copy(), this.m_week.copy(), this.m_leads.copy(), this.m_datesValid);
	}
	
	public void computeShiftLeads() {
		// before computing we should invalidate any existing
		invalidateShiftLeads();
		for (int day = 0; day < m_week.getShifts().length; day++) {
			for (int hour = 0; hour < m_week.getShifts()[day].length; hour++) {
				Employee lead = null;
				int leadRank = Integer.MAX_VALUE; //we want to find min
				for (Employee e : m_week.getShift(day, hour)) {
					int rank = m_leads.getRank(e);
					if (rank < leadRank) { //if this employee is a potential lead
						// found a new min
						lead = e;
						leadRank = rank;
					}
				}
				if (lead != null) {
					m_week.getShift(day, hour).setLead(lead);
				}
			}
		}
	}
	
	private void invalidateShiftLeads() {
		for (int day = 0; day < m_week.getShifts().length; day++) {
			for (int hour = 0; hour < m_week.getShifts()[day].length; hour++) {
				m_week.getShift(day, hour).setLead(null);
			}
		}
	}

}
