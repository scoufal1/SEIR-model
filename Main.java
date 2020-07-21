import java.io.*;
import java.util.*;

public class Main {
    	static double MIN_FRACTION_SUSCEPTIBLE = 0.9 ;
	static double MAX_FRACTION_SUSCEPTIBLE = 1.0 ;
	static int MIN_EXPOSED = 0 ;
	static int MAX_EXPOSED = 10 ;
	static int MIN_INFECTED = 0 ;
	static int MAX_INFECTED = 10 ;
	static int MIN_LATENT_PERIOD = 1 ;
	static int MAX_LATENT_PERIOD = 10 ;
	static int MIN_INFECTIOUS_PERIOD = 1 ;
	static int MAX_INFECTIOUS_PERIOD = 10 ;
	static double MIN_R0 = 1.0 ;
	static double MAX_R0 = 5.0 ; 
	static int MIN_BASELINE = 10 ;
	static int MAX_BASELINE = 100 ;
	static int MIN_START_DAY = 0 ;
	static int MAX_START_DAY = 60 ;

    public static void main(String[] args) throws FileNotFoundException {
	
	
	double frac_sus = (MIN_FRACTION_SUSCEPTIBLE + MAX_FRACTION_SUSCEPTIBLE)/2;
	int population = 250000;
	int sus = (int)(frac_sus * population);
	int exp = (MIN_EXPOSED + MAX_EXPOSED)/2;
	int inf = (MIN_INFECTED + MAX_INFECTED)/2;
	int rec = (int)((1-frac_sus)*population)-exp-inf;
	double latentPer =  (MIN_LATENT_PERIOD +  MAX_LATENT_PERIOD)/2;
	double infectPer = (MIN_INFECTIOUS_PERIOD + MAX_INFECTIOUS_PERIOD)/2;
	double R0 = (MIN_R0 + MAX_R0)/2;
	int baseline = (MIN_BASELINE + MAX_BASELINE)/2;
	int startDay = (MIN_START_DAY + MAX_START_DAY)/2;

	double deathRate = 0.01;
	SEIR seir = new SEIR(sus, exp, inf, rec, latentPer, infectPer, R0, baseline, 0, 200);
	double[] means = Evaluate.means(seir, deathRate);
	Data data = new Data(means);   
	int numDays = data.numberOfDays();
	int start = 0;
	int end = numDays;
	double logResult;
	double result;
	
	System.out.println("Data is expected deaths based on model:");
	seir.info();
	System.out.println();
	System.out.println("Probability of data based on model where R0 is incremented for each model:\n");
	System.out.println("R0\tlogResult\te^logResult");
	while(R0 < 6.0) {
	    //data is exp number of deaths
	    seir = new SEIR(sus, exp, inf, 0, latentPer, infectPer, R0, baseline, 0, 200);
	    logResult = Evaluate.logProbOfData(seir, data, deathRate, start, end);
	    result = Math.pow(Math.E, logResult);
	    System.out.printf("%.2f\t", R0);
	    System.out.printf("%.2g\t", logResult);
	    System.out.printf("%.2g\n", result);
	    R0 += 0.2;
	}
	//R0 set back to original value
	R0 = (MIN_R0 + MAX_R0)/2;
	System.out.println();
	System.out.println("Probability of data based on model where infectPer is incremented for each model:\n");
	System.out.println("infPer\tlogResult\te^logResult");
	while(infectPer < 20.0) {
	    //data is exp number of deaths
	    seir = new SEIR(sus, exp, inf, 0, latentPer, infectPer, R0, baseline, 0, 200);
	    logResult = Evaluate.logProbOfData(seir, data, deathRate, start, end);
	    result = Math.pow(Math.E, logResult);
	    System.out.printf("%.2f\t", infectPer);
	    System.out.printf("%.2g\t", logResult);
	    System.out.printf("%.2g\n", result);
	    infectPer += 1.5;
	}
    }
}
