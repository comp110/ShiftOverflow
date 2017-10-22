package comp110;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Leads extends ArrayList<Employee> {
	private Staff _staff;
	
	public Leads(Staff staff) {
		_staff = staff;
	}
	
	public void add(String onyen) {
		System.out.print(onyen);
		Employee e = _staff.getEmployeeByOnyen(onyen);
		if (e != null) {
			super.add(e);
		}
	}
	
	public int getRank(Employee toRank) {
		for (int i = 0; i < this.size(); i++) {
			if (this.get(i).equals(toRank)) {
				return i;
			}
		}
		return Integer.MAX_VALUE; //since we are trying to find min
	}

	public Leads copy() {
		Leads copy = new Leads(this._staff);
		Iterator<Employee> itr = this.iterator();
		while (itr.hasNext()) {
			copy.add(itr.next().copy());
		}
		return copy;
	}
}
