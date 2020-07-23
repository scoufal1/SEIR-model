public class Evaluate {
    
    static double[] dailyProbOfDeathGivenDeathFromCovid = {
	0.0000, 0.0000, 0.0000, 0.0000, 0.0006, 0.0013, 0.0025, 0.0051,
	0.0076, 0.0101, 0.0139, 0.0177, 0.0215, 0.0266, 0.0304, 0.0341,
	0.0379, 0.0405, 0.0430, 0.0436, 0.0443, 0.0445, 0.0440, 0.0430,
	0.0417, 0.0398, 0.0379, 0.0354, 0.0329, 0.0304, 0.0278, 0.0255,
	0.0233, 0.0215, 0.0196, 0.0177, 0.0158, 0.0142, 0.0129, 0.0116,
	0.0101, 0.0089, 0.0080, 0.0072, 0.0063, 0.0057, 0.0048, 0.0040,
	0.0035, 0.0028, 0.0025, 0.0023, 0.0021, 0.0020, 0.0019, 0.0016,
	0.0014, 0.0013, 0.0011, 0.0010, 0.0010
    };
    
    static double probOfDeathGivenDeathFromCovid(int patientDay) {
	return dailyProbOfDeathGivenDeathFromCovid[patientDay];
    }
    
    static int maximumDurationOfCovid() {
	return dailyProbOfDeathGivenDeathFromCovid.length;
    }
    // Calc mean expected deaths for each day
    // μ(d) = ∑ NI(j)δθ(d − j)
    static double[] means(SEIR seir, double ifr) {
	int numDays = seir.numberOfDays();
	double[] means = new double[numDays];
	double probDeath;
	for(int day = 0; day < numDays; day++) {
	    for(int patientDay = 0; patientDay < maximumDurationOfCovid(); patientDay++) {
		if(day+patientDay < numDays) {
		    probDeath = ifr * probOfDeathGivenDeathFromCovid(patientDay);
		    means[day+patientDay]+=seir.newInfections(day)*probDeath;
		}
	    }
	}
	return means;
    }

    // Calc variance expected deaths for each day
    // σ^2(d) = ∑ NI(j)δθ(d − j)(1-δθ(d − j))
    static double[] variances(SEIR seir, double ifr) {
	int numDays = seir.numberOfDays();
	double[] variances = new double[numDays];
	double probDeath;
	for(int day = 0; day < numDays; day++) {
	    for(int patientDay = 0; patientDay < maximumDurationOfCovid(); patientDay++) {
		if(day+patientDay < numDays) {
		    probDeath = ifr * probOfDeathGivenDeathFromCovid(patientDay);
		    variances[day+patientDay]+=seir.newInfections(day)*probDeath*(1-probDeath);
		}
	    }
	}
	return variances;
    }
    
    // This computes Gaussian(numberOfDeaths,mean,variance):
    static double probOfDeaths(double numberOfDeaths, double mean, double variance) {
	double sigma = Math.sqrt(variance);
	double logresult = logCDF(numberOfDeaths, mean, sigma);

	return logresult;
    }

    static double logCDF(double numberOfDeaths, double mean, double sigma) {
	double x = (numberOfDeaths-mean)/sigma;
	return Math.log(1/sigma)+logPhi(x);
    }

    //log of standard normal pdf
    static double logPhi(double x) {
	return Math.log(1/(Math.pow(2*Math.PI, 0.5)))-0.5*Math.pow(x,2);
    }
    
    // This computes the probability of death for each day in
    // [start,end) and returns the sum of their logarithms.
    // Computed in log form to avoid underflow
    static double logProbOfData(SEIR seir, Data data, double ifr, int start, int end) {
	double[] means = means(seir, ifr);
	double[] variances = variances(seir, ifr);
	double logProbData = 0;
	double prob;
	for(int day = start; day < end; day++) {
	    //variance must be greater than 0 bc sigma = 0 causes error
	    if(variances[day] > 0) {
		prob = probOfDeaths(data.deaths(day), means[day], variances[day]);
		//logProbData += Math.log(prob);
		logProbData += prob;
	    }
	}
	return logProbData;
    }
}
