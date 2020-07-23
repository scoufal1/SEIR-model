//Read data from CSV file
import java.util.*;
import java.io.*;

public class Data {
    public static final int DATE = 0;
    private int numDays;
    private String fileName;
    private ArrayList<String> dates = new ArrayList<>();
    private ArrayList<Double> cases = new ArrayList<>();
    private ArrayList<Double> deaths = new ArrayList<>();

    //constructor specifically used for using expected values as data, not actual data from file
    public Data(double[] means) {
	for(int i = 0; i < means.length; i++) {
	    deaths.add(means[i]);
	    numDays++;
	}
    }

    public Data(String fileName, String casesColumnName, String deathsColumnName, boolean cumulative) throws FileNotFoundException {
	this.fileName = fileName;
	Scanner fileInput = new Scanner(new File(fileName));
	String header = fileInput.nextLine();
	String[] columns = header.split(",");
	int casesIndex = 0;
	int deathsIndex = 0;
	for(int i = 0; i < columns.length; i++){
	    if(columns[i].equals(casesColumnName)) {
		casesIndex = i;
	    }
	    if(columns[i].equals(deathsColumnName)) {
		deathsIndex = i;
	    }
	}
	String line;
	String[] tokens;
	double caseCount;
	double deathCount;
	double prevCaseCount;
	double prevDeathCount;
	ArrayList<Double> cumulativeCases = new ArrayList<>();
	ArrayList<Double> cumulativeDeaths = new ArrayList<>();
	while(fileInput.hasNextLine()) {
	    line = fileInput.nextLine();
	    tokens = line.split(",");
	    dates.add(tokens[DATE]);
	    //end loop if value is NaN
	    if(tokens[deathsIndex].equals("NaN")) {
		break;
	    }
	    caseCount = Double.parseDouble(tokens[casesIndex]);
	    deathCount = Double.parseDouble(tokens[deathsIndex]);
	    if(cumulative) {
		cumulativeCases.add(caseCount);
		cumulativeDeaths.add(deathCount);
		if(deaths.size() > 0) {
		    prevCaseCount = cumulativeCases.get(numDays - 1);
		    prevDeathCount = cumulativeDeaths.get(numDays - 1);
		    caseCount = caseCount - prevCaseCount;
		    deathCount = deathCount - prevDeathCount;
		}
	    }
	    cases.add(caseCount);
	    deaths.add(deathCount);
	    numDays++;
	}
    }

    //returns the number of days (lines) of data
    public int numberOfDays() {
	return numDays;
    }
    
    //returns the number of cases on day starting from zero
    public double cases(int day) {
	return cases.get(day);
    }
    
    //returns the number of deaths on day starting from zero
    public double deaths(int day) {
	return deaths.get(day);
    }
    
    //returns the total number of cases from days start (inclusive) to
    //end (exclusive).
    public double totalCases(int start, int end) {
	double sum = 0;
	for(int i = start; i < end; i++) {
	    sum += cases.get(i);
	}
	return sum;
    }
    
    //returns the total number of deaths from days start (inclusive) to
    //end (exclusive).
    public double totalDeaths(int start, int end) {
	double sum = 0;
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

    //only call if data is read in from file, not with other constructor
    //any other basic info needed?
    public void info() {
	System.out.println("File: " + fileName);
	System.out.println("Number of Days: " + numDays);
	System.out.println("Start Day: " + date(0));
	System.out.println("End Day: " + date(numDays-1));
    }
}
