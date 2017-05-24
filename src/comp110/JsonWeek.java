package comp110;


public class JsonWeek {

	// constants
	private static final int NUMBER_DAYS = 7;
	private static final int NUMBER_HOURS = 24;
	
	// variables
	private JsonShift shifts[][];

	// functions
	public JsonWeek(Shift shifts[][]) {
		// initialize array to same size
		this.shifts = new JsonShift[NUMBER_DAYS][NUMBER_HOURS];
		
		for (int day = 0; day < NUMBER_DAYS; ++day) {
			for (int hour = 0; hour < NUMBER_HOURS; ++hour) {
				this.shifts[day][hour] = new JsonShift(shifts[day][hour]);
			}
		}
	}

	public JsonShift[][] getShifts() {
		return this.shifts;
	}

}
