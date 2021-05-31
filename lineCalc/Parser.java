package lineCalc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Function;

/**
 * (EN) This parser for math expressions is based on:
 * https://stackoverflow.com/questions/3422673/how-to-evaluate-a-math-expression-given-in-string-form
 * 
 * (IT) Il parser per espressioni matematiche è basato su:
 * https://stackoverflow.com/questions/3422673/how-to-evaluate-a-math-expression-given-in-string-form
 */

public class Parser {
	private int pos = -1, ch;
	private String str;
	private static HashMap<String, Function<ArrayList<Cplx>, Cplx>> funcMap;
	private static HashMap<String, Cplx> constMap;
	private static VarTableModel tm;

	public Parser(HashMap<String, Function<ArrayList<Cplx>, Cplx>> funcMap_, HashMap<String, Cplx> constMap_,
			VarTableModel tm_) {
		funcMap = funcMap_;
		constMap = constMap_;
		tm = tm_;
	}

	void nextChar() {
		ch = (++pos < str.length()) ? str.charAt(pos) : -1;
	}

	// "eat" spaces and checks next char
	// advances only if char matches
	boolean eat(int charToEat) {
		while (ch == ' ')
			nextChar();
		if (ch == charToEat) {
			nextChar();
			return true;
		}
		return false;
	}

	Cplx parse(String stringToParse) {
		str = stringToParse;
		nextChar();
		Cplx x = parseExpression();
		if (pos < str.length())
			throw new RuntimeException("At " + pos + " (parse) Unexpected: " + (char) ch);
		return x;
	}

	// Grammar:
	// expression = term | expression `+` term | expression `-` term
	// term = factor | term `*` factor | term `/` factor
	// factor = `+` factor | `-` factor | `(` expression `)`
	// | number | functionName factor | factor `^` factor

	Cplx parseExpression() {
		Cplx x = parseTerm();
		for (;;) {
			if (eat('+'))
				x = x.add(parseTerm()); // addition
			else if (eat('-'))
				x = x.sub(parseTerm()); // subtraction
			else
				return x;
		}
	}

	Cplx parseTerm() {
		Cplx x = parseFactor();
		for (;;) {
			if (eat('*'))
				x = x.mult(parseFactor()); // multiplication
			else if (eat('/'))
				x = x.div(parseFactor()); // division
			else if (eat('\\'))
				x = Cplx.intDivide(x, parseFactor()); // (Gaussian) integer division
			else if (eat('%')) {
				Cplx y = parseFactor();
				x = Cplx.remainder(x, y); // modulo (remainder)
			} else
				return x;
		}
	}

	ArrayList<Cplx> parseArgs() {
		ArrayList<Cplx> args = new ArrayList<Cplx>();
		eat('(');
		while (!eat(')')) {
			args.add(parseExpression());
			eat(',');
		}
		return args;
	}

	Cplx parseFactor() {
		if (eat('+'))
			return parseFactor(); // unary plus
		if (eat('-'))
			return Cplx.negative(parseFactor()); // unary minus

		Cplx x;
		int startPos = this.pos;
		if (eat('(')) { // parentheses
			x = parseExpression();
			eat(')');
		} else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
			int chprev = ' ';
			while ((ch >= '0' && ch <= '9') || ch == '.' || ch == 'e' || (ch == '-' && chprev == 'e')) {
				chprev = ch;
				nextChar();
			}
			x = new Cplx(Double.parseDouble(str.substring(startPos, this.pos)));
		} else if (ch >= 'a' && ch <= 'z') { // functions
			while ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || (ch >= '0' && ch <= '9') || ch == '_')
				nextChar();
			String func = str.substring(startPos, this.pos);
			if (!funcMap.containsKey(func))
				throw new RuntimeException("At " + pos + " (func) Unknown function: " + func);
			ArrayList<Cplx> args = new ArrayList<Cplx>();
			if (ch == '(')
				args = parseArgs();
			else
				args.add(parseFactor());
			Function<ArrayList<Cplx>, Cplx> f = funcMap.get(func);
			try {
				x = (Cplx) f.apply(args);
			} catch (RuntimeException ex) {
				throw new RuntimeException("At " + pos + " (func) " + ex.getLocalizedMessage());
			}
		} else if (ch >= 'A' && ch <= 'Z') {
			while ((ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z') || (ch >= '0' && ch <= '9') || ch == '_')
				nextChar();
			String constant = str.substring(startPos, this.pos);
			if (constMap.containsKey(constant))
				x = constMap.get(constant);
			else if (tm.getData().containsKey(constant))
				x = tm.getData().get(constant);
			else
				throw new RuntimeException("At " + pos + " (const) Unknown constant: " + constant);
		} else {
			throw new RuntimeException("At " + pos + " (factor) Unexpected: " + (char) ch);
		}

		if (eat('^'))
			x = Cplx.pow(x, parseFactor()); // exponentiation

		return x;
	}
}
