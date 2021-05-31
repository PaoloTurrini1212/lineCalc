package lineCalc;

public class Cplx {
	public static final Cplx NAN = new Cplx(Double.NaN, Double.NaN);
	public static final Cplx ZERO = new Cplx(0.0, 0.0);
	public static final Cplx ONE = new Cplx(1.0, 0.0);
	public static final Cplx I = new Cplx(0.0, 1.0);

	private double real, imag;

	public Cplx(double a, double b, String form) throws IllegalArgumentException {
		if (form.equals("XY")) {
			// c = a + b i
			real = a;
			imag = b;
		} else if (form.equals("POLAR")) {
			// c = a e^(b i) = (a cos(b)) + (a sin(b)) i
			real = a * Math.cos(b);
			imag = a * Math.sin(b);
		} else {
			throw new IllegalArgumentException("Invalid form: expected XY or POLAR.");
		}
	}

	public Cplx(double re, double im) {
		// default: XY
		real = re;
		imag = im;
	}

	public Cplx(double re) {
		// Default Im=0 (numero reale)
		real = re;
		imag = 0.;
	}

	public String toString() {
		if (imag == 0) {
			return String.valueOf(real);
		}
		return String.valueOf(real) + (imag < 0 ? " - " : " + ") + Math.abs(imag) + " i";
	}

	public double real() {
		return real;
	}

	public double imag() {
		return imag;
	}

	public double mag() {
		if (isReal())
			return Math.abs(real);
		return Math.sqrt(real * real + imag * imag);
	}

	public double arg() {
		return Math.atan2(imag, real);
	}

	public boolean equals(Cplx other) {
		return this.real == other.real && this.imag == other.imag;
	}

	public Cplx copy() {
		return new Cplx(real, imag);
	}

	public boolean isReal() {
		return imag == 0.0;
	}

	public boolean isZero() {
		return real == 0.0 && imag == 0.0;
	}

	public Cplx conjugate() {
		return new Cplx(real, -imag);
	}

	public Cplx neg() {
		return new Cplx(-real, -imag);
	}

	public static Cplx negative(Cplx z) {
		return new Cplx(-z.real, -z.imag);
	}

	public Cplx signum() {
		if (isZero())
			return ZERO;
		if (isReal())
			return new Cplx(Math.signum(real));
		return Cplx.multiply(this, 1.0 / this.mag());
	}

	public Cplx floor() {
		return new Cplx(Math.floor(real), Math.floor(imag));
	}

	public Cplx ceil() {
		return new Cplx(Math.ceil(real), Math.ceil(imag));
	}

	public Cplx round() {
		return new Cplx(Math.round(real), Math.round(imag));
	}

	public Cplx add(Cplx z) {
		return add(this, z);
	}

	public static Cplx add(Cplx z1, Cplx z2) {
		return new Cplx(z1.real + z2.real, z1.imag + z2.imag);
	}

	public Cplx sub(Cplx z) {
		return subtract(this, z);
	}

	public static Cplx subtract(Cplx z1, Cplx z2) {
		return new Cplx(z1.real - z2.real, z1.imag - z2.imag);
	}

	public Cplx mult(double a) {
		return multiply(this, a);
	}

	public Cplx mult(Cplx z) {
		return multiply(this, z);
	}

	public static Cplx multiply(Cplx z, double a) {
		return new Cplx(z.real * a, z.imag * a);
	}

	public static Cplx multiply(double a, Cplx z) {
		return new Cplx(z.real * a, z.imag * a);
	}

	public static Cplx multiply(Cplx z1, Cplx z2) {
		double re = z1.real * z2.real - z1.imag * z2.imag;
		double im = z1.real * z2.imag + z1.imag * z2.real;
		return new Cplx(re, im);
	}

	public Cplx div(double a) {
		return divide(this, a);
	}

	public Cplx div(Cplx z) {
		return divide(this, z);
	}

	public static Cplx divide(Cplx z, double a) {
		if (a == 0.0) {
			return NAN;
		}
		return Cplx.multiply(z, 1.0 / a);
	}

	public static Cplx divide(Cplx z1, Cplx z2) {
		if (z2.isZero()) {
			return NAN;
		}
		Cplx z = Cplx.multiply(z1, z2.conjugate());
		double m = z2.mag();
		return z.div(m * m);
	}

	public Cplx invert() {
		return Cplx.inverse(this);
	}

	public static Cplx inverse(Cplx z) {
		if (z.isZero())
			return NAN;
		return ONE.div(z);
	}

	public static Cplx intDivide(Cplx z1, Cplx z2) {
		// quoziente di divisione intera
		if (z2.isZero())
			return NAN;
		if (z1.isReal() && z2.isReal())
			return new Cplx(Math.floor(z1.real / z2.real));
		return z1.div(z2).round();
	}

	public static Cplx remainder(Cplx z1, Cplx z2) {
		// resto di divisione intera
		if (z2.isZero())
			return NAN;
		Cplx q = Cplx.intDivide(z1, z2);
		return z1.sub(q.mult(z2));
	}

	public static Cplx exp(Cplx z) {
		double x = Math.exp(z.real);
		return new Cplx(Math.cos(z.imag), Math.sin(z.imag)).mult(x);
	}

	public static Cplx exp(double b, Cplx z) {
		if (b <= 0) {
			return NAN;
		}
		return Cplx.exp(z.mult(Math.log(b)));
	}

	public static Cplx pow(Cplx z, double a) {
		return new Cplx(Math.pow(z.mag(), a), z.arg() * a, "POLAR");
	}

	public static Cplx pow(Cplx z, Cplx w) {
		if (w.isReal())
			return Cplx.pow(z, w.real);
		if (z.isZero())
			return NAN;
		if (z.isReal())
			return Cplx.exp(z.real, w);
		double L = Math.log(z.mag());
		double r = L * w.real - z.arg() * w.imag;
		double i = L * w.imag + z.arg() * w.real;
		return Cplx.exp(new Cplx(r, i));
	}

	public static Cplx log(Cplx z) {
		// Natural Log (principal value)
		return new Cplx(Math.log(z.mag()), z.arg());
	}

	public static Cplx log(Cplx z, double base) {
		// Log (principal value) in a real base
		return Cplx.log(z, new Cplx(base));
	}

	public static Cplx log(Cplx z, Cplx base) {
		// Log (principal value) in a complex base
		return Cplx.log(z).div(Cplx.log(base));
	}

	// Funzioni trigonometriche

	public static Cplx sin(Cplx z) {
		if (z.isReal())
			return new Cplx(Math.sin(z.real));
		double r = Math.sin(z.real) * Math.cosh(z.imag);
		double i = Math.cos(z.real) * Math.sinh(z.imag);
		return new Cplx(r, i);
	}

	public static Cplx cos(Cplx z) {
		if (z.isReal())
			return new Cplx(Math.cos(z.real));
		double r = Math.cos(z.real) * Math.cosh(z.imag);
		double i = Math.sin(z.real) * Math.sinh(z.imag);
		return new Cplx(r, i);
	}

	public static Cplx tan(Cplx z) {
		return Cplx.sin(z).div(Cplx.cos(z));
	}

	public static Cplx cot(Cplx z) {
		return Cplx.cos(z).div(Cplx.sin(z));
	}

	public static Cplx sec(Cplx z) {
		return Cplx.cos(z).invert();
	}

	public static Cplx csc(Cplx z) {
		return Cplx.sin(z).invert();
	}

	// Funzioni trigonometriche inverse (ramo principale)

	public static Cplx arcsin(Cplx z) {
		if (z.isReal())
			return new Cplx(Math.asin(z.real));
		Cplx rq = Cplx.pow(ONE.sub(Cplx.pow(z, 2)), 0.5);
		return I.mult(Cplx.log(rq.sub(I.mult(z))));
	}

	public static Cplx arccos(Cplx z) {
		return new Cplx(Math.PI / 2.0).sub(Cplx.arcsin(z));
	}

	public static Cplx arctan(Cplx z) {
		if (z.isReal())
			return new Cplx(Math.atan(z.real));
		Cplx x = Cplx.divide(I.sub(z), I.add(z));
		return I.mult(-0.5).mult(Cplx.log(x));
	}

	public static Cplx arccot(Cplx z) {
		if (z.isReal())
			return new Cplx(Math.atan(1.0 / z.real));
		Cplx x = Cplx.divide(z.add(I), z.sub(I));
		return I.mult(-0.5).mult(Cplx.log(x));
	}

	public static Cplx arcsec(Cplx z) {
		return Cplx.arccos(z.invert());
	}

	public static Cplx arccsc(Cplx z) {
		return Cplx.arcsin(z.invert());
	}

	// Funzioni iperboliche

	public static Cplx sinh(Cplx z) {
		if (z.isReal())
			return new Cplx(Math.sinh(z.real));
		return I.neg().mult(Cplx.sin(I.mult(z)));
	}

	public static Cplx cosh(Cplx z) {
		if (z.isReal())
			return new Cplx(Math.cosh(z.real));
		return Cplx.cos(I.mult(z));
	}

	public static Cplx tanh(Cplx z) {
		if (z.isReal())
			return new Cplx(Math.tanh(z.real));
		return I.neg().mult(Cplx.tan(I.mult(z)));
	}

	public static Cplx coth(Cplx z) {
		return I.mult(Cplx.cot(I.mult(z)));
	}

	public static Cplx sech(Cplx z) {
		return Cplx.sec(I.mult(z));
	}

	public static Cplx csch(Cplx z) {
		return I.mult(Cplx.csc(I.mult(z)));
	}

	// Funzioni trigonometriche inverse

	public static Cplx arsinh(Cplx z) {
		Cplx rq = Cplx.pow(Cplx.pow(z, 2).add(z), 0.5);
		return Cplx.log(z.add(rq));
	}

	public static Cplx arcosh(Cplx z) {
		Cplx rq1 = Cplx.pow(z.add(ONE), 0.5);
		Cplx rq2 = Cplx.pow(z.sub(ONE), 0.5);
		return Cplx.log(z.add(rq1.mult(rq2)));
	}

	public static Cplx artanh(Cplx z) {
		Cplx f = Cplx.divide(ONE.add(z), ONE.sub(z));
		return Cplx.log(f).mult(0.5);
	}

	public static Cplx arcoth(Cplx z) {
		Cplx f = Cplx.divide(z.add(ONE), z.sub(ONE));
		return Cplx.log(f).mult(0.5);
	}

	public static Cplx arsech(Cplx z) {
		Cplx w = Cplx.inverse(z);
		Cplx r1 = w.add(ONE);
		Cplx r2 = w.sub(ONE);
		Cplx rq = Cplx.pow(r1.mult(r2), 0.5);
		return Cplx.log(w.add(rq));
	}

	public static Cplx arcsch(Cplx z) {
		Cplx z1 = z.invert();
		Cplx z2 = Cplx.pow(Cplx.pow(z, -2).add(ONE), 0.5);
		return Cplx.log(z1.add(z2));
	}
}
