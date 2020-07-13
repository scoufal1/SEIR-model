//Read data from CSV file
import java.util.*;
import java.io.*;

public class Data {
    public static final int DATE = 0;
    private int numDays;
    private ArrayList<String> dates = new ArrayList<>();
    private ArrayList<Integer> cases = new ArrayList<>();
    private ArrayList<Integer> deaths = new ArrayList<>();
    
    public Data(String fileName, String columnName, boolean cumulative) throws FileNotFoundException {
	Scanner fileInput = new Scanner(new File(fileName));
	String header = fileInput.nextLine();
	String[] columns = header.split(",");
	int index = 0;
	for(int i = 0; i < columns.length; i++){
	    if(columns[i].equals(columnName)) {
		index = i;
	    }
	}
	String line;
	String[] tokens;
	int deathCount;
	int prevCount;
	ArrayList<Integer> cumulativeCases = new ArrayList<>();
	ArrayList<Integer> cumulativeDeaths = new ArrayList<>();
	while(fileInput.hasNextLine()) {
	    line = fileInput.nextLine();
	    tokens = line.split(",");
	    dates.add(tokens[DATE]);
	    deathCount = Integer.parseInt(tokens[index]);
	    if(cumulative) {
		cumulativeDeaths.add(deathCount);
		if(deaths.size() > 0) {
		    prevCount = cumulativeDeaths.get(numDays - 1);
		    deathCount = deathCount - prevCount;
		}
	    }
	    deaths.add(deathCount);
	    numDays++;
	}
    }
    
    //returns the number of days (lines) of data
    public int numberOfDays() {
	return numDays;
    }
    
    //returns the number of cases on day starting from zero
    public int cases(int day) {
	return 0;
    }
    
    //returns the number of deaths on day starting from zero
    public int deaths(int day) {
	return deaths.get(day);
    }
    
    //returns the total number of cases from days start (inclusive) to
    //end (exclusive).
    public int totalCases(int start, int end) {
	return 0;
    }
    
    //returns the total number of deaths from days start (inclusive) to
    //end (exclusive).
    public int totalDeaths(int start, int end) {
	int sum = 0;
	for(int i = start; i < end; i++) {
	    sum += deaths.get(i);
	}
	return sum;
    }
    
    //returns the date corresponding to day
    public String date(int day) {
	return dates.get(day);
    }
    
    //returns the day corresponding to date
    public int day(String date) {
	for(int i = 0; i < deaths.size(); i++) {
	    if(date.equals(dates.get(i))) {
		return i;
	    }
	}
	return -1;
    }
}
