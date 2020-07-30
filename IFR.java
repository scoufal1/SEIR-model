import java.io.*;

public class IFR {
    //constants to indicate the various command line flags
    public static final String FILE = "-f"; //input file name
    public static final String CUMULATIVE = "-c"; //boolean indicating that file has cumulative counts
    public static final String CASES_COLUMN = "-case"; //column name of file for case counts
    public static final String DEATHS_COLUMN = "-death"; //column name of file for death counts
    public static final String MIN_IFR = "-min"; //minimum IFR value
    public static final String MAX_IFR = "-max"; //maximum IFR value
    public static final String NUM_MODELS = "-m"; //number of models
    public static final String NUM_IFR = "-i"; //number of IFRs
    public static final String START = "-s"; //start day
    public static final String END = "-e"; //end day
    
    //ranges of possible SEIR parameters
    static double MIN_FRACTION_SUSCEPTIBLE = 0.9 ;
    static double MAX_FRACTION_SUSCEPTIBLE = 1.0 ;
    static int POPULATION = 250000;
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
	
	String fileName = "";
	boolean cumulative = false;
	String deathsColumn = "";
	String casesColumn = "";
	double minIFR = 0;
	double maxIFR = 0;
	int numModels = 0;
	int numIFR = 0;
	int start = 0;
	int end = 0;

	//get inputs from command line
	for (int i = 0; i < args.length; i++) {
	    if (args[i].equals(FILE)) {
		fileName = args[i+1];
		i++;
	    } else if (args[i].equals(CUMULATIVE)) {
		cumulative = true;
	    } else if (args[i].equals(CASES_COLUMN)) {
		casesColumn = args[i+1];
		i++;
	    } else if (args[i].equals(DEATHS_COLUMN)) {
		deathsColumn = args[i+1];
		i++;
	    } else if (args[i].equals(MIN_IFR)) {
		minIFR = Double.parseDouble(args[i+1]);
		i++;
	    } else if (args[i].equals(MAX_IFR)) {
		maxIFR = Double.parseDouble(args[i+1]);
		i++;
	    } else if (args[i].equals(NUM_MODELS)) {
		numModels = Integer.parseInt(args[i+1]);
		i++;
	    } else if (args[i].equals(NUM_IFR)) {
		numIFR = Integer.parseInt(args[i+1]);
		i++;
	    } else if (args[i].equals(START)) {
		start = Integer.parseInt(args[i+1]);
		i++;
	    } else if (args[i].equals(END)) {
		end = Integer.parseInt(args[i+1]);
		i++;
	    }
	}

	//read file and make data object
	Data data = new Data(fileName, casesColumn, deathsColumn, cumulative);
	int numDays = data.numberOfDays();
	
	//prevent index out of bound error in evaluate
	if(end > numDays) {
	    end = numDays;
	}
      
	//ifrValue contains discrete range of IFR values within given range
	double increment = (maxIFR - minIFR)/numIFR; //min inclusive, max exclusive
	double[] ifrValue = new double[numIFR];

	double value = minIFR;
	for(int i = 0; i < numIFR; i++) {
	    ifrValue[i] = value;
	    value += increment;
	}
	
	SEIR seir;
	double frac_sus;
	int population = POPULATION;
	int sus;
	int exp;
	int inf;
	int rec = 0;
	double latentPer;
	double infectPer;
	double R0;
	int baseline;
	int startDay;

	//ifr contains probabilities of IFR values, indices corresponding with ifrValue array
	LNumber[] ifr = new LNumber[numIFR];
	//initilize values to 0
	for(int j = 0; j < numIFR; j++) {
	    ifr[j] = new LNumber(0.0);
	}
	double result;
	LNumber prob;
	
	for(int i = 0; i < numIFR; i++) {
	    for(int n = 0; n < numModels; n++) {
		//generate random parameters within given ranges
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
		//sum probability of IFR over all models
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
    
    public static int genRandomInt(int min, int max) {
	return min + (int)(Math.random() * (max-min));
    }
}
