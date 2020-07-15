import java.io.*;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {
	
	double deathRate = 0.02;
	SEIR seir = new SEIR(900000, 0, 1000, 99990, 4, 6, 2.5, 0, 0, 50);
	double[] means = Evaluate.means(seir, deathRate);
	Data data = new Data(means);
	int numDays = data.numberOfDays();
	int start = 0;
	int end = numDays;
	double result = Evaluate.logProbOfData(seir, data, deathRate, start, end);
	System.out.println("data is expected number of deaths based on seir model and probability of death each day");
	System.out.println("log probability of data given IFR = " + deathRate + " and model");
	System.out.println("result: " + result);
    }
}
