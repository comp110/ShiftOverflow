package comp110;

import static org.junit.Assert.*;
import org.junit.*;

/* Created By Keith Whitley */

public class ParserTest {
	@Test
	public void parseEmployee_test() {
		Parser p = new Parser();

		try {
			Employee e = p.parseEmployee("testData/test.csv");

			assertEquals(e.getName(), "Test");
			assertEquals(e.getIsFemale(), false);
			assertEquals(e.getCapacity(), 4);
			assertEquals(e.getLevel(), 3);

			int[][] capacity = { { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0 },
					{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0 },
					{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0 },
					{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0 },
					{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0 },
					{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
					{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 } };
			assertArrayEquals(e.getAvailability(), capacity);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	@Test
	public void parseSchedule_test() {
		Parser p = new Parser();
		try {
			Schedule s = p.parseSchedule("testData/schedule.json", "data/spring-17/staff");
			Shift test1 = new Shift(0, 1, 0);
			Shift test2 = new Shift(0, 2, 0);
			Shift test3 = new Shift(1, 5, 0); 
			Shift test4 = new Shift(1, 19, 0);
			
			Employee melissa = p.parseEmployee("data/spring-17/staff/melissa.csv");
			Employee aatieh = p.parseEmployee("data/spring-17/staff/aatieh.csv");
			Employee slichten = p.parseEmployee("data/spring-17/staff/slichten.csv");
			Employee lexihart = p.parseEmployee("data/spring-17/staff/lexihart.csv");
			Employee sunmiche = p.parseEmployee("data/spring-17/staff/sunmiche.csv");
			test4.add(melissa);
			test4.add(aatieh);
			test4.add(slichten);
			test4.add(lexihart);
			test4.add(sunmiche);
			
		
			Shift test5 = new Shift(1, 20, 0);
			
			
			Shift test6 = new Shift(5, 16, 0);
			
			Employee alking96 = p.parseEmployee("data/spring-17/staff/alking96.csv");
			Employee codycody = p.parseEmployee("data/spring-17/staff/codycody.csv");
			Employee jamhenry = p.parseEmployee("data/spring-17/staff/jamhenry.csv");
			Employee tabathav = p.parseEmployee("data/spring-17/staff/tabathav.csv");
			Employee masonmc = p.parseEmployee("data/spring-17/staff/masonmc.csv");
			Employee sfirrin = p.parseEmployee("data/spring-17/staff/sfirrin.csv");
			Employee shanesp = p.parseEmployee("data/spring-17/staff/shanesp.csv");
			Employee heather1 = p.parseEmployee("data/spring-17/staff/heather1.csv");
			
			test6.add(alking96);
			test6.add(codycody);
			test6.add(jamhenry);
			test6.add(tabathav);
			test6.add(masonmc);
			test6.add(sfirrin);
			test6.add(shanesp);
			test6.add(heather1);
			
			Shift test7 = new Shift (3, 12, 0);
			Employee haydenl = p.parseEmployee("data/spring-17/staff/haydenl.csv");
			Employee cphamlet = p.parseEmployee("data/spring-17/staff/cphamlet.csv");
			test7.add(haydenl);
			test7.add(codycody);
			test7.add(cphamlet);
			test7.add(sunmiche);
			
			Shift[][] shift = s.getWeek().getShifts();
			
			assertEquals(shift[0][1].equals(test1), true);
			assertEquals(shift[0][2].equals(test2), true);
			assertEquals(shift[1][5].equals(test3), true);
			assertEquals(shift[1][19].equals(test4), true);
			assertEquals(shift[1][20].equals(test5), true);
			assertEquals(shift[5][16].equals(test6), true);
			assertEquals(shift[3][12].equals(test7), true);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void writeFile_test() {
		Parser p = new Parser();
		int[][] availability = { { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 } };
		Employee e = new Employee("writeTest", "writeOnyen", 4, true, 3, availability);
		try {
			p.writeFile(e, "testData/writeOnyen.csv");
			Employee result = p.parseEmployee("testData/writeOnyen.csv");
			assertEquals(e.equals(result), true);
		}catch (Exception e1) {
				e1.printStackTrace();
		}
		
		
		
	}

	@Test
	public void writeScheduleToJson_Test() {
		Parser p = new Parser();
		try {
			Schedule s = p.parseSchedule("testData/schedule.json", "data/spring-17/staff");
			p.writeScheduleToJson(s, "testData/testSchedule.json");
			Schedule result = p.parseSchedule("testData/testSchedule.json", "data/spring-17/staff");
			assertEquals(s.equals(result), true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Test
	public void writeRepeatedFiles(){
		Parser p = new Parser();
		int[][] availability = { { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 } };
		Employee e = new Employee("writeTest", "writeOnyen", 4, true, 3, availability);
		try {
			p.writeFile(e, "testData/writeOnyen.csv");
			Employee result = p.parseEmployee("testData/writeOnyen.csv");
			for(int i = 0; i < 20; i++){
				p.writeFile(result, "testData/writeOnyen.csv");
				result = p.parseEmployee("testData/writeOnyen.csv");
			}
			assertEquals(e.equals(result), true);
		}catch (Exception e1) {
				e1.printStackTrace();
		}
	
	}
	@Test
	public void writeRepeatedSchedules(){
		Parser p = new Parser();
		try {
			Schedule s = p.parseSchedule("testData/testSchedule.json", "data/spring-17/staff");
			p.writeScheduleToJson(s, "testData/testSchedule.json");
			Schedule result = p.parseSchedule("testData/testSchedule.json", "data/spring-17/staff");
			for(int i = 0; i < 20; i++){
				p.writeScheduleToJson(result, "testData/testSchedule.json");
				result = p.parseSchedule("testData/testSchedule.json", "data/spring-17/staff");
			}
			assertEquals(s.equals(result), true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
