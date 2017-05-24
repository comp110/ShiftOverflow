package comp110;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class Shift extends HashSet<Employee> implements Serializable {

	// serialization version
	private static final long serialVersionUID = 1L;

	// variables
	private int m_day;
	private int m_hour;
	private int m_capacity;

	
	// functions
	public Shift(int day, int hour, int capacity) {
		this.m_day = day;
		this.m_hour = hour;
		this.m_capacity = capacity;
	}

	public boolean add(Employee e) {
		// make sure employee is valid
		if (e == null){
			return false;
		}
		
		if (super.add(e)) {
			e.setCapacityUsed(e.getCapacityUsed() + 1);
			return true;
		}
		return false;
	}

	public boolean remove(Employee e) {
		// make sure employee is valid
		if (e == null){
			return false;
		}
		
		if (super.remove(e)) {
			e.setCapacityUsed(e.getCapacityUsed() - 1);
			return true;
		}
		return false;
	}

	@Override
	public String toString() {
		List<String> names = this.stream().map(e -> e.getName()).collect(Collectors.toList());
		return String.format("%02d", this.m_hour) + ": (" + String.format("%02d", names.size()) + ") "
				+ String.join(", ", names);
	}

	public boolean equals(Shift other) {
		if (this.m_day != other.m_day){
			return false;
		}
		if (this.m_hour != other.m_hour){
			return false;
		}
		if (this.m_capacity != other.m_capacity){
			return false;
		}
		if (this.size() != other.size()){
			return false;
		}

		// This is literal vomit. Someone please figure out and teach me why
		// Set's
		// containsAll method fails here. This should be O(n) not O(n^2).
		// Ugh.
		// Daniel J. Steffey's Answer to your above question:
		// you *really* shouldn't modify Objects after their insertion into a HashSet
		// *especially* if that modification changes their hash code
		// after you modify it then you can't be guaranteed that the hashset.contains()
		// method will properly function
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

	public int getDay() {
		return this.m_day;
	}

	public void setDay(int day) {
		this.m_day = day;
	}

	public int getHour() {
		return this.m_hour;
	}

	public void setHour(int hour) {
		this.m_hour = hour;
	}

	public int getCapacity() {
		return this.m_capacity;
	}

	public int getCapacityRemaining() {
		return this.m_capacity - size();
	}

	public Shift copy() {
		Shift copy = new Shift(this.m_day, this.m_hour, this.m_capacity);
		Iterator<Employee> itr = this.iterator();
		while (itr.hasNext()) {
			copy.add(itr.next().copy());
		}
		return copy;
	}
}
