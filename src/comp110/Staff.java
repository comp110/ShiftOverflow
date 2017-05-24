package comp110;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

public class Staff extends HashSet<Employee> implements Serializable {

	// serialization version
	private static final long serialVersionUID = 1L;

	// variables
	
	
	// functions
	public double getCapacity() {
		double capacity = 0.0;
		for (Employee e : this) {
			capacity += (double) e.getCapacity();
		}
		return capacity;
	}

	public double getRemainingCapacity() {
		double remainingCapacity = 0;
		for (Employee e : this) {
			remainingCapacity += (double) e.getCapacityRemaining();
		}
		return remainingCapacity;
	}

	public Staff copy() {
		Staff copy = new Staff();
		Iterator<Employee> itr = this.iterator();
		while (itr.hasNext()) {
			copy.add(itr.next().copy());
		}
		return copy;
	}

	public boolean equals(Staff other) {
		if (this.size() != other.size()){
			return false;
		}
		
		for (Employee e : this) {
			boolean contains = false;
			for (Employee o : other) {
				if (e.equals(o)) {
					contains = true;
					break;
				}
			}
			if (contains == false) {
				return false;
			}
		}
		return true;
	}

	public ArrayList<String> getWhoIsAvailable(int day, int hour) {
		ArrayList<String> whoIsAvailable = new ArrayList<String>();
		for (Employee e : this) {
			if (e.isAvailable(day, hour)) {
				whoIsAvailable.add(e.toString());
			}
		}
		return whoIsAvailable;
	}

	public Employee getEmployeeByName(String name) {
		for (Employee e : this) {
			if (e.getName().equals(name)) {
				return e;
			}
		}
		return null;
	}
}
