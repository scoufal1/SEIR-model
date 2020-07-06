//SEIR Model

public class SEIR {

    private int S; //susceptible
    private int E; //exposed
    private int I; //infected
    private int R; //recovered
    private int N; //total population
    private int latentPeriod; //average amount of time an individual is pre-infectious
    private int infectiousPeriod; //average amount of time an individual is infectious
    //expected number of individuals that an infectious person would infect if 
    //introduced into an entirely susceptible population.
    private double R0;
    private int baseline; //number of cases that are always present in the population
    private int startDay; //when community transmission begins
    private int numberOfDays;
    //arrays to hold values for each day
    private double[] sus;
    private double[] exp;
    private double[] inf;
    private double[] rec;
    private double[] forceInf;
    private double[] newInf;

    //constructor
    public SEIR(int S, int E, int I, int R, int latentPeriod, int infectiousPeriod,
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
	setStartDay();
	updateArrays();
    }
    //risk that a susceptible person becomes infected between day d and d+1
    private double forceOfInfection(double I) {
	return (R0 * I)/(N * infectiousPeriod) ;
    }
    //rate at which pre-infectious individuals become infectious
    private double f() {
	return 1/(double)latentPeriod;
    }
    //rate at which individuals recover 
    private double r() {
	return 1/(double)infectiousPeriod;
    }
    //checks that S,E,I,R are in [0,population] and approximately add up to N
    private boolean okay() {
	double population;
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

    //sets initial array values
    private void setStartDay() {
	sus[0] = S;
	exp[0] = E;
	inf[0] = I;
	rec[0] = R;
	forceInf[0] = forceOfInfection(I);
	newInf[0] = 0;
    }

    //calculate S, E, I, R based on formulas
    private double calcS(double S, double I) {
	double force = forceOfInfection(I);
	return S - (force * S);
    }
    private double calcE(double S, double E, double I) {
	double force = forceOfInfection(I);
	return E + (force * S) - (f() * E);
    }
    private double calcI(double E, double I) {
	return I + (f() * E) - (r() * I);
    }
    private double calcR(double I, double R) {
	return R + (r() * I);
    }
    //updates the rest of ararys
    private void updateArrays() {
	double S;
	double E;
	double I;
	double R;
	
	for(int d = 1; d < numberOfDays; d++) {
	    S = sus[d-1];
	    E = exp[d-1];
	    I = inf[d-1];
	    R = rec[d-1];
	    
	    sus[d] = calcS(S, I);
	    exp[d] = calcE(S, E, I);
	    inf[d] = calcI(E, I);
	    rec[d] = calcR(I, R);
	    //inf[d] or I?
	    forceInf[d] = forceOfInfection(inf[d]);
	    //should this be forceInf[d] * sus[d]?
	    newInf[d] = (forceInf[d-1] * S);
	}
    }

    public double newInfections(int day){
	int i = day - startDay;
	return newInf[i];
    }
    public double infectious(int day){
	int i = day - startDay;
	return inf[i];
    }
}

