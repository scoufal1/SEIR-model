import java.io.*;

public class IFR {
    static double MIN_FRACTION_SUSCEPTIBLE = 0.9 ;
    static double MAX_FRACTION_SUSCEPTIBLE = 1.0 ;
    static int MIN_EXPOSED = 0 ;
    static int MAX_EXPOSED = 10 ;
    static int MIN_INFECTED = 0 ;
    static int MAX_INFECTED = 10 ;
    static double MIN_LATENT_PERIOD = 1.0 ;
    static double MAX_LATENT_PERIOD = 10.0 ;
    static double MIN_INFECTIOUS_PERIOD = 1.0 ;
    static double MAX_INFECTIOUS_PERIOD = 10.0 ;
    static double MIN_R0 = 1.0 ;
    static double MAX_R0 = 5.0 ; 
    static int MIN_BASELINE = 10 ;
    static int MAX_BASELINE = 100 ;
    static int MIN_START_DAY = 0 ;
    static int MAX_START_DAY = 60 ;

    public static void main(String[] args) throws FileNotFoundException {
        String fileName;
	String casesColumnName;
	String deathsColumnName;
	boolean cumulative;
	//update to get from command line
	//Data data = new Data(fileName, casesColumnName, deathsColumnName, cumulative);
	Data data = new Data("nyc-date-cases-hospitalizations-deaths.csv", "CASES", "DEATHS", false);
	int numDays = data.numberOfDays();
	double MIN_IFR = 0.01;
	double MAX_IFR = 0.05;
	int numIFR = 10;
	//min inclusive, max exclusive
	double increment = (MAX_IFR - MIN_IFR)/numIFR;
	double[] ifrValue = new double[numIFR];

	double value = MIN_IFR;
	for(int i = 0; i < numIFR; i++) {
	    ifrValue[i] = value;
	    value += increment;
	}

	int numModels = 10;
	int start = 0;
	int end = numDays;
	
	SEIR seir;
	double frac_sus;
	int population =1000;
	int sus;
	int exp;
	int inf;
	int rec = 0;
	double latentPer;
	double infectPer;
	double R0;
	int baseline;
	int startDay;
	
	LNumber[] ifr = new LNumber[numIFR];
	//initilize values to 0
	for(int j = 0; j < numIFR; j++) {
	    ifr[j] = new LNumber(0.0);
	}
	double result;
	LNumber prob;
	
	for(int i = 0; i < numIFR; i++) {
	    for(int n = 0; n < numModels; n++) {
		frac_sus = genRandomDouble(MIN_FRACTION_SUSCEPTIBLE, MAX_FRACTION_SUSCEPTIBLE);
		sus = (int)(frac_sus * population);
		exp = genRandomInt(MIN_EXPOSED, MAX_EXPOSED);
		inf = genRandomInt(MIN_INFECTED, MAX_INFECTED);
		latentPer = genRandomDouble(MIN_LATENT_PERIOD, MAX_LATENT_PERIOD);
		infectPer = genRandomDouble(MIN_INFECTIOUS_PERIOD, MAX_INFECTIOUS_PERIOD);
		R0 = genRandomDouble(MIN_R0, MAX_R0);
		baseline = genRandomInt(MIN_BASELINE, MAX_BASELINE);
		startDay = genRandomInt(MIN_START_DAY, MAX_START_DAY);

		//update startDay once figured out
		seir = new SEIR(sus, exp, inf, rec, latentPer, infectPer, R0, baseline, 0, numDays);
		result = Evaluate.logProbOfData(seir, data, ifrValue[i], start, end);
		prob = new LNumber(result, true);
		ifr[i] = ifr[i].add(prob);
	    }
	    //divide sum of probabilities for each IFR by number of models
	    ifr[i] = ifr[i].divide(new LNumber(numModels));
	}
	//totalProbability = sum of LNumbers in ifr[]
	LNumber totalProbability = new LNumber(0.0);
	for(int i = 0; i < ifr.length; i++) {
	    totalProbability = totalProbability.add(ifr[i]);
	}
	LNumber lnum;
	for(int i = 0; i < numIFR; i++) {
	    lnum = ifr[i].divide(totalProbability);
	    System.out.print("IFR: ");
	    System.out.printf("%.3f\n",ifrValue[i]);
	    System.out.println("Probability: " + lnum);
	    System.out.print("Log of Probability: ");
	    System.out.printf("%.3g\n",lnum.logOfValue);
	    System.out.println();
	}
    }
    
    public static double genRandomDouble(double min, double max) {
	return min + (Math.random() * (max-min));
    }
    
    //should include max or not?
    public static int genRandomInt(int min, int max) {
	return min + (int)(Math.random() * (max-min));
    }
    
}
