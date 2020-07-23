/// Contents: Methods for log arithmetic.
/// Author:   John Aronis
/// Date:     July 2020

public class LNumber {

    public static void main(String[] args) {
	LNumber x, y ;
	x = new LNumber(0.5) ;
	y = new LNumber(7.0) ;
	System.out.println( x.add(y) + " " + x.multiply(y) + " " + x.divide(y) + " " + x.isLessThan(y) ) ;
    }

    public boolean isZero ;
    public double logOfValue ;
    private static final double MAX_EXP = -50000.0;

    //alternate constructor if value is log
    public LNumber(double x, boolean log) {
	//change base from e to 10
	if(log) { logOfValue = x/Math.log(10); isZero = false; }
	else { System.out.println( x + " " + "ERROR creating LNumber number"); }
    }
    
    public LNumber(double x) {
	if (x==0.0)     { isZero = true ; }
	else if (x>0.0) { logOfValue = Math.log10(x) ; isZero = false ; }
	else            { System.out.println( x + " " + "ERROR creating LNumber number") ; }
    }

    private static double logOfSum(double logX, double logY) {
	double logYMinusLogX, temp;
	if (logY>logX) { temp=logX; logX=logY; logY=temp; }
	logYMinusLogX=logY-logX;
	if (logYMinusLogX<MAX_EXP) { return logX; }
	else { return Math.log10(1+Math.pow(10,logYMinusLogX))+logX; }
    }

    public LNumber add(LNumber x) {
	if (this.isZero && x.isZero) return this ;
	if (x.isZero)                return this ;
	if (this.isZero)             return x ;
	LNumber result = new LNumber(0.0) ;
	result.isZero = false ;
	result.logOfValue = logOfSum(this.logOfValue,x.logOfValue) ;
	return result ;
    }

    public LNumber multiply(LNumber x) {
	if (this.isZero || x.isZero) return new LNumber(0.0) ;
	LNumber result = new LNumber(0.0) ;
	result.isZero = false ;
	result.logOfValue = this.logOfValue + x.logOfValue ;
	return result ;
    }

    public LNumber divide(LNumber x) {
	if (this.isZero ) return new LNumber(0.0) ;
	LNumber result = new LNumber(0.0) ;
	result.isZero = false ;
	result.logOfValue = this.logOfValue - x.logOfValue ;
	return result ;
    }

    public boolean isLessThan(LNumber p) {
	if ( this.isZero && p.isZero ) return false ;
	else if ( this.isZero && !p.isZero ) return true ;
	else if ( !this.isZero && p.isZero ) return false ;
	else return ( this.logOfValue < p.logOfValue ) ;
    }

    public String toString() {
	return "" + ( isZero ? 0.0 : Math.pow(10,this.logOfValue) ) ;
    }

}

/// End-of-File
