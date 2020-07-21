//SEIR Model

public class SEIR {

    private int S; //susceptible
    private int E; //exposed
    private int I; //infected
    private int R; //recovered
    private int N; //total population
    private double latentPeriod; //average amount of time an individual is pre-infectious
    private double infectiousPeriod; //average amount of time an individual is infectious
    private double R0; //basic reproduction number
    private int baseline; //cases not accounted for by model
    private int startDay; //when community transmission begins
    private int numberOfDays;
    //arrays to hold values for each day
    private double[] sus;
    private double[] exp;
    private double[] inf;
    private double[] rec;
    private double[] forceInf;
    private double[] newInf;
    private double f; //rate at which pre-infectious individuals become infectious
    private double r; //rate at which individuals recover 

    //constructor
    public SEIR(int S, int E, int I, int R, double latentPeriod, double infectiousPeriod,
		double R0, int baseline, int startDay, int numberOfDays) {
	this.S = S;
	this.E = E;
	this.I = I;
	this.R = R;
	this.N = S + E + I + R;
	this.latentPeriod = latentPeriod;
	this.infectiousPeriod = infectiousPeriod;
	this.R0 = R0;
	this.baseline = baseline;
	this.startDay = startDay;
	this.numberOfDays = numberOfDays;
	this.sus = new double[numberOfDays];
	this.exp = new double[numberOfDays];
	this.inf = new double[numberOfDays];
	this.rec = new double[numberOfDays];
	this.forceInf = new double[numberOfDays];
	this.newInf = new double[numberOfDays];
	this.f = 1.0 / latentPeriod;
	this.r = 1.0 / infectiousPeriod;
	setStartDay();
	updateArrays();
	boolean okay = okay();
	if(!okay) {
	    System.out.println("population check not okay");                         
	}
    }
   
    //checks that S,E,I,R are in [0,population] and approximately add up to N
    private boolean okay() {
	double population=0;
	boolean check = true;
	for(int i = 0; i < numberOfDays; i++) {
	    population = sus[i] + exp[i] + inf[i] + rec[i];
	    if(sus[i] < 0 || sus[i] > N) {
		check = false;
	    }
	    if(exp[i] < 0 || exp[i] > N) {
		check = false;
	    }
	    if(inf[i] < 0 || inf[i] > N) {
		check = false;
	    }
	    if(rec[i] < 0 || rec[i] > N) {
		check = false;
	    }
	    //what is condidered "approximately" equal to N?
	    if(population < N-0.01 || population > N+0.01) {
		check = false;
	    }
	   
	}
	return check;
    }

    //risk that a susceptible person becomes infected between day d and d+1
    private double forceOfInfection(double I) {
	return (R0 * I)/(N * infectiousPeriod);
    }
    
    //sets initial array values
    private void setStartDay() {
	sus[0] = S;
	exp[0] = E;
	inf[0] = I;
	rec[0] = R;
	forceInf[0] = forceOfInfection(I);
	newInf[0] = baseline;
    }

    //updates the rest of ararys
    private void updateArrays() {
	double S;
	double E;
	double I;
	double R;
	double force;
	double newlyExposed;
	double newlyInfectious;
	double newlyRecovered;
	
	for(int d = 1; d < numberOfDays; d++) {
	    S = sus[d-1];
	    E = exp[d-1];
	    I = inf[d-1];
	    R = rec[d-1];
	    force = forceInf[d-1];
	    newlyExposed = force * S;
	    newlyInfectious = f * E;
	    newlyRecovered = r * I;

	    sus[d] = S - newlyExposed;
	    exp[d] = E + newlyExposed - newlyInfectious;
	    inf[d] = I + newlyInfectious - newlyRecovered;
	    rec[d] = R + newlyRecovered;
	    forceInf[d] = forceOfInfection(inf[d]);
	    newInf[d] = newlyExposed + baseline;
	}
    }
    public int numberOfDays() {
	return numberOfDays;
    }
    public double newInfections(int day){
	//int i = day - startDay;
	//return newInf[i];
	return newInf[day];
    }
    
    public double totalInfections(int start, int end) {
	double sum = 0;
	//should this be inclusive or exclusive?
	for(int i = (start - startDay); i <= (end - startDay); i++) {
	    sum += newInf[i];
	}
	return sum;
    }

    public void info() {
	System.out.println("Susceptible: " + S);
	System.out.println("Exposed: " + E);
	System.out.println("Infectious: " + I);
	System.out.println("Recovered: " + R);
	System.out.println("Population: " + N);
	System.out.println("Latent Period: " + latentPeriod);
	System.out.println("Infectious Period: " + infectiousPeriod);
	System.out.println("R0: " + R0);
	System.out.println("Baseline: " + baseline);
	System.out.println("Start Day: " + startDay);
	System.out.println("Number of Days: " + numberOfDays);
    }
	
}

