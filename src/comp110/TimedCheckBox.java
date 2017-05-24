package comp110;

import javafx.scene.control.CheckBox;


public class TimedCheckBox extends CheckBox {
	
	// variables
	private int m_day;
	int m_hour;

	// functions
	public TimedCheckBox(int day, int hour) {
		super();
		this.m_day = day;
		this.m_hour = hour;
	}
	
	public int getDay(){
		return this.m_day;
	}

	public int getHour(){
		return this.m_hour;
	}
}
