package comp110;

import java.io.Serializable;

public class Schedule implements Serializable {

	// serialization version 
	private static final long serialVersionUID = 1L;
	
	
	// variables
	private Staff m_staff;
	private Week m_week;
	private Leads m_leads;
	
	// functions
	public Schedule(Staff staff, Week week, Leads leads) {
		this.m_staff = staff;
		this.m_week = week;
		this.m_leads = leads;
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

	public boolean equals(Schedule other) {
		return this.m_staff.equals(other.m_staff) && this.m_week.equals(other.m_week);
	}

	public Schedule copy() {
		return new Schedule(this.m_staff.copy(), this.m_week.copy(), this.m_leads.copy());
	}
	
	private void computeShiftLeads() {
		for (int day = 0; day < m_week.getShifts().length; day++) {
			for (int hour = 0; hour < m_week.getShifts()[day].length; hour++) {
				Employee lead = null;
				int leadRank = Integer.MAX_VALUE; //we want to find min
				System.out.println("Day: " + day + " Hour: " + hour);
				for (Employee e : m_week.getShift(day, hour)) {
					int rank = m_leads.getRank(e);
					if (e.getOnyen().equals("youngjt")) {
						System.out.println("Jeffrey rank: " + rank);
					}
					if (e.getOnyen().equals("dunnac11")) {
						System.out.println("Duncan rank: " + rank);
					}
					if (rank < leadRank) { //if this employee is a potential lead
						// found a new min
						lead = e;
						leadRank = rank;
					}
				}
				if (lead != null) {
					m_week.getShift(day, hour).setLead(lead);
					System.out.println("Lead: " + lead.getOnyen());
				}
			}
		}
	}

}
