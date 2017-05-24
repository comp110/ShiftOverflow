package comp110;


public class JsonShift {
	// variables
	private int day;
	private int hour;
	private String[] onyens;

	// functions
	public JsonShift(Shift s) {
		this.day = s.getDay();
		this.hour = s.getHour();
		this.onyens = new String[s.size()];
		int i = 0;
		for (Employee e : s) {
			this.onyens[i] = e.getOnyen();
			i++;
		}
	}

	public int getDay() {
		return this.day;
	}

	public int getHour() {
		return this.hour;
	}

	public String[] getScheduled() {
		return this.onyens;
	}

}
