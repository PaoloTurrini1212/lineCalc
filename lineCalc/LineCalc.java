package lineCalc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.function.Function;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.WindowConstants;
//import javax.swing.table.AbstractTableModel;

//import com.sun.org.apache.xml.internal.utils.URI;

public class LineCalc {
	@SuppressWarnings("unused")
	private static final long serialVersionUID = 2841541638162570861L;
	public static String versionNumber = "0.2.1 (beta)";
	public static String path = System.getProperty("user.dir") + "/lineCalc";
	public static String helpTxt = "LineCalc " + versionNumber + "\nCompiled with / Compilato con: Java 14\n\n"
			+ "(EN) LineCalc is a simple, command-line calculator. It features a basic\n"
			+ "	    variable management system and recognizes several mathematical functions. It\n"
			+ "	    is intended for quick, back-of-the-envelope calculations and for aid in\n"
			+ "	    classroom problems.\nAdded in v. 0.2.1: prime factorization tool\n\n"
			+ "(IT) LineCalc è una semplice calcolatrice a riga di comando. Include un\n"
			+ "	    sistema basico di gestione delle variabili e riconosce numerose funzioni\n"
			+ "	    matematiche. Il programma è pensato per calcoli veloci e come ausilio nei\n"
			+ "	    problemi scolastici.\nAggiunto nella v. 0.2.1: strumento per la fattorizzazione";
	private static HashMap<String, Function<ArrayList<Cplx>, Cplx>> funcMap = new HashMap<>();
	private static HashMap<String, Cplx> constMap = new HashMap<>();
	private static Cplx value;
	private static String expr, res;
	private static JFrame frame;
	private static SpringLayout layout;
	private static JTextField txt_expr;
	private static JTextArea txt_res;
	private static JTable tbl_var;
	private static VarTableModel tm;
	protected static Font font = new Font("Monospaced", Font.PLAIN, 15);

	/**
	 * (EN) LineCalc is a simple, command-line calculator. It features a basic
	 * variable management system and recognizes several mathematical functions. It
	 * is intended for quick, back-of-the-envelope calculations and for aid in
	 * classroom problems.
	 * 
	 * (IT) LineCalc è una semplice calcolatrice a riga di comando. Include un
	 * sistema basico di gestione delle variabili e riconosce numerose funzioni
	 * matematiche. Il programma è pensato per calcoli veloci e come ausilio nei
	 * problemi scolastici.
	 */
	public static void main(String[] args) {
		funcMap = createFuncMap();
		constMap = createConstMap();

		frame = new JFrame("LineCalc " + versionNumber);
		frame.setPreferredSize(new Dimension(640, 480));
		frame.setResizable(true);
		frame.getContentPane().setBackground(new Color(0, 50, 100));
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setVisible(true);
		layout = new SpringLayout();
		frame.setLayout(layout);

		try {
			frame.setIconImage(new ImageIcon(LineCalc.class.getResource("/lineCalcIcon.png")).getImage());
		} catch (Exception e1) {
			frame.setIconImage(new ImageIcon(path + "/lineCalcIcon.png").getImage());
		}

		txt_expr = new JTextField();
		txt_expr.setBackground(Color.black);
		txt_expr.setForeground(Color.white);
		txt_expr.setCaretColor(Color.white);
		txt_expr.getMargin().set(5, 5, 5, 5);
		txt_expr.setFont(font);
		txt_expr.setPreferredSize(new Dimension(600, 40));
		txt_expr.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					expr = txt_expr.getText();
					try {
						value = eval(expr);
						res = value.toString();
						txt_res.setForeground(new Color(150, 255, 200));
						txt_res.setText(res);
					} catch (Exception e1) {
						txt_res.setForeground(new Color(255, 0, 100));
						txt_res.setText(e1.getLocalizedMessage());
					} finally {
						frame.repaint();
					}
				}
			}
		});

		JLabel lbl_res = new JLabel("Risultato = ");
		lbl_res.setFont(font);
		lbl_res.setForeground(new Color(230, 255, 200));
		txt_res = new JTextArea();
		txt_res.setLineWrap(true);
		txt_res.setBackground(Color.black);
		txt_res.setForeground(new Color(200, 255, 230));
		txt_res.setFont(font);
		txt_res.setPreferredSize(new Dimension(480, 30));
		txt_res.setEditable(false);

		tm = new VarTableModel();
		tbl_var = new JTable(tm);
		tbl_var.setFont(font);
		tbl_var.setRowHeight(20);
		tbl_var.getColumnModel().getColumn(1).setPreferredWidth(400);
		JScrollPane pane_var = new JScrollPane(tbl_var);
		pane_var.setPreferredSize(new Dimension(500, 230));

		// Comandi per modificare le variabili
		// Se l'area del risultato è vuota, il risultato corrente di default è 0.0

		JButton btn_addVar = new JButton();
		try {
			btn_addVar.setIcon(new ImageIcon(LineCalc.class.getResource("/addVarBtn.png")));
		} catch (Exception e1) {
			btn_addVar.setIcon(new ImageIcon(path + "/addVarBtn.png"));
		}
		btn_addVar.setToolTipText("Aggiungi nuova variabile col valore attuale del risultato.");
		btn_addVar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// aggiunge una variabile col valore corrente
				String x = txt_res.getText();
				if (value != null && !x.isEmpty()) {
					String newName = JOptionPane.showInputDialog("Nome della variabile:");
					while (!isValidName(newName)) {
						newName = JOptionPane
								.showInputDialog("Nome non valido o già presente. Inserire un altro nome:");
					}
					if (newName != null) {
						tm.addRow(newName, value);
					}
				}
				frame.repaint();
			}
		});

		JButton btn_removeVar = new JButton();
		try {
			btn_removeVar.setIcon(new ImageIcon(LineCalc.class.getResource("/removeVarBtn.png")));
		} catch (Exception e1) {
			btn_removeVar.setIcon(new ImageIcon(path + "/removeVarBtn.png"));
		}
		btn_removeVar.setToolTipText("Elimina la variabile selezionata.");
		btn_removeVar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// rimuove la variabile corrispondente alla riga selezionata
				int r = tbl_var.getSelectedRow();
				if (r >= 0) {
					tm.removeRow(r);
				}
				frame.repaint();
			}
		});

		JButton btn_assignVar = new JButton();
		try {
			btn_assignVar.setIcon(new ImageIcon(LineCalc.class.getResource("/assignVarBtn.png")));
		} catch (Exception e1) {
			btn_assignVar.setIcon(new ImageIcon(path + "/assignVarBtn.png"));
		}
		btn_assignVar.setToolTipText("Assegna il valore attuale del risultato alla variabile selezionata");
		btn_assignVar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// sostituisce il valore della variabile selezionata col risultato corrente
				int r = tbl_var.getSelectedRow();
				String x = txt_res.getText();
				if (r >= 0 && value != null && !x.isEmpty()) {
					tm.setValueAt(value, r, 1);
				}
				frame.repaint();
			}
		});

		JButton btn_help = new JButton();
		try {
			btn_help.setIcon(new ImageIcon(LineCalc.class.getResource("/helpBtn.png")));
		} catch (Exception e1) {
			btn_help.setIcon(new ImageIcon(path + "/helpBtn.png"));
		}
		btn_help.setToolTipText("Aiuto");
		btn_help.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					// Desktop.getDesktop().browse(LineCalc.class.getResource("/lineCalcHelp_IT.html").toURI());
					URL h = getClass().getResource("/lineCalcHelp_IT.html");
					JFrame hw = new JFrame("LineCalc - Help");
					hw.setSize(520, 540);
					hw.setLayout(new FlowLayout());
					hw.setLocationRelativeTo(null);
					JEditorPane hp = new JEditorPane();
					hp.setEditable(false);
					hp.setPage(h);
					JScrollPane hs = new JScrollPane(hp);
					hs.setPreferredSize(new Dimension(500, 500));
					hw.getContentPane().add(hs, BorderLayout.CENTER);
					hw.setVisible(true);
				} catch (Exception e1) {
					e1.printStackTrace();
					JOptionPane.showMessageDialog(frame, e1.getLocalizedMessage(), "LineCalc - Error",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		JButton btn_launchPrimeFactorDialog = new JButton("Fattorizza...");
		btn_launchPrimeFactorDialog.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PrimeFactorDialog d = new PrimeFactorDialog();
			}
		});

		frame.add(txt_expr);
		frame.add(lbl_res);
		frame.add(txt_res);
		frame.add(pane_var);
		frame.add(btn_addVar);
		frame.add(btn_removeVar);
		frame.add(btn_assignVar);
		frame.add(btn_help);
		frame.add(btn_launchPrimeFactorDialog);
		layout.putConstraint(SpringLayout.NORTH, txt_expr, 10, SpringLayout.NORTH, frame);
		layout.putConstraint(SpringLayout.WEST, txt_expr, 10, SpringLayout.WEST, frame);
		layout.putConstraint(SpringLayout.NORTH, lbl_res, 20, SpringLayout.SOUTH, txt_expr);
		layout.putConstraint(SpringLayout.WEST, lbl_res, 0, SpringLayout.WEST, txt_expr);
		layout.putConstraint(SpringLayout.BASELINE, txt_res, 0, SpringLayout.BASELINE, lbl_res);
		layout.putConstraint(SpringLayout.WEST, txt_res, 10, SpringLayout.EAST, lbl_res);

		layout.putConstraint(SpringLayout.NORTH, btn_addVar, 20, SpringLayout.SOUTH, lbl_res);
		layout.putConstraint(SpringLayout.WEST, btn_addVar, 10, SpringLayout.WEST, lbl_res);
		layout.putConstraint(SpringLayout.NORTH, btn_removeVar, 0, SpringLayout.NORTH, btn_addVar);
		layout.putConstraint(SpringLayout.WEST, btn_removeVar, 20, SpringLayout.EAST, btn_addVar);
		layout.putConstraint(SpringLayout.NORTH, btn_assignVar, 0, SpringLayout.NORTH, btn_removeVar);
		layout.putConstraint(SpringLayout.WEST, btn_assignVar, 20, SpringLayout.EAST, btn_removeVar);
		layout.putConstraint(SpringLayout.NORTH, pane_var, 20, SpringLayout.SOUTH, btn_addVar);
		layout.putConstraint(SpringLayout.WEST, pane_var, 0, SpringLayout.WEST, txt_expr);

		layout.putConstraint(SpringLayout.NORTH, btn_help, 10, SpringLayout.SOUTH, txt_res);
		layout.putConstraint(SpringLayout.EAST, btn_help, 0, SpringLayout.EAST, txt_expr);
		layout.putConstraint(SpringLayout.NORTH, btn_launchPrimeFactorDialog, 50, SpringLayout.SOUTH, btn_help);
		layout.putConstraint(SpringLayout.EAST, btn_launchPrimeFactorDialog, 0, SpringLayout.EAST, btn_help);

		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.repaint();
	}

	public static Cplx eval(final String str) {
		Parser parser = new Parser(funcMap, constMap, tm);
		return parser.parse(str);
	}

	// -------------------
	/**
	 * (EN) Constants map
	 * 
	 * (IT) Mappa delle costanti
	 */
	private static HashMap<String, Cplx> createConstMap() {
		HashMap<String, Cplx> map = new HashMap<String, Cplx>();
		map.put("I", Cplx.I);
		map.put("E", new Cplx(Math.E));
		map.put("PI", new Cplx(Math.PI));
		map.put("TAU", new Cplx(2 * Math.PI));
		map.put("DEG", new Cplx(Math.PI / 180.0));
		map.put("GRAD", new Cplx(Math.PI / 200.0));
		map.put("PHI", new Cplx(0.5 * (Math.sqrt(5) + 1))); // rapporto aureo
		map.put("GAMMA_EM", new Cplx(0.57721566490153286)); // costante di Eulero-Mascheroni
		return map;
	}

	// -------------------
	/**
	 * (EN) Functions map
	 * 
	 * (IT) Mappa delle funzioni
	 */
	private static HashMap<String, Function<ArrayList<Cplx>, Cplx>> createFuncMap() {
		HashMap<String, Function<ArrayList<Cplx>, Cplx>> map = new HashMap<>();

		// *** Funzioni a una variabile ***

		/** (EN) Square root (IT) Radice quadrata */
		map.put("sqrt", (ArrayList<Cplx> x) -> Cplx.pow(x.get(0), 0.5));
		/** (EN) Exponential (IT) Esponenziale */
		map.put("exp", (ArrayList<Cplx> x) -> Cplx.exp(x.get(0)));
		/**
		 * (EN) Base-10 logarithm (principal value) (IT) Logaritmo decimale (valore
		 * principale)
		 */
		map.put("log", (ArrayList<Cplx> x) -> Cplx.log(x.get(0), 10.));
		/**
		 * (EN) Natural logarithm (principal value) (IT) Logaritmo naturale (valore
		 * principale)
		 */
		map.put("ln", (ArrayList<Cplx> x) -> Cplx.log(x.get(0)));
		/** (EN) Factorial (IT) Fattoriale */
		map.put("fact", (ArrayList<Cplx> x) -> {
			if (!x.get(0).isReal())
				throw new RuntimeException("Illegal argument: must be an integer >=0");
			return new Cplx(fact(x.get(0).real()));
		});
		/** (EN) Double factorial (IT) Fattoriale doppio */
		map.put("dfact", (ArrayList<Cplx> x) -> {
			if (!x.get(0).isReal())
				throw new RuntimeException("Illegal argument: must be an integer >=0");
			return new Cplx(dfact(x.get(0).real()));
		});

		/** (EN) Absolute value (IT) Valore assoluto */
		map.put("abs", (ArrayList<Cplx> x) -> new Cplx(x.get(0).mag()));
		/**
		 * (EN) Argument (angle with real axis) (IT) Argomento (angolo con l'asse reale)
		 */
		map.put("arg", (ArrayList<Cplx> x) -> new Cplx(x.get(0).arg()));
		/** (EN) Complex conjugate (IT) Complesso coniugato */
		map.put("conj", (ArrayList<Cplx> x) -> x.get(0).conjugate());
		/** (EN) Signum (IT) Segno */
		map.put("sign", (ArrayList<Cplx> x) -> x.get(0).signum());
		/** (EN) Floor (toward -inf) (IT) Intero inferiore (verso -inf) */
		map.put("floor", (ArrayList<Cplx> x) -> x.get(0).floor());
		/** (EN) Ceiling (toward +inf) (IT) Intero superiore (verso +inf) */
		map.put("ceil", (ArrayList<Cplx> x) -> x.get(0).ceil());
		/** (EN) Round to nearest integer (IT) Arrotonda all'intero più vicino */
		map.put("round", (ArrayList<Cplx> x) -> x.get(0).round());

		// (EN) Trigonometric/hyperbolic functions (principal values)
		// (IT) Funzioni trigonometriche/iperboliche (valori principali)

		/** (EN) Sine (IT) Seno */
		map.put("sin", (ArrayList<Cplx> x) -> Cplx.sin(x.get(0)));
		/** (EN) Cosine (IT) Coseno */
		map.put("cos", (ArrayList<Cplx> x) -> Cplx.cos(x.get(0)));
		/** (EN) Tangent (IT) Tangente */
		map.put("tan", (ArrayList<Cplx> x) -> Cplx.tan(x.get(0)));
		/** (EN) Cotangent (IT) Cotangente */
		map.put("cot", (ArrayList<Cplx> x) -> Cplx.cot(x.get(0)));
		/** (EN) Secant (IT) Secante */
		map.put("sec", (ArrayList<Cplx> x) -> Cplx.sec(x.get(0)));
		/** (EN) Cosecant (IT) Cosecante */
		map.put("csc", (ArrayList<Cplx> x) -> Cplx.csc(x.get(0)));

		/** (EN) Inverse sine (IT) Arcoseno */
		map.put("arcsin", (ArrayList<Cplx> x) -> Cplx.arcsin(x.get(0)));
		/** (EN) Inverse cosine (IT) Arcocoseno */
		map.put("arccos", (ArrayList<Cplx> x) -> Cplx.arccos(x.get(0)));
		/** (EN) Inverse tangent (IT) Arcotangente */
		map.put("arctan", (ArrayList<Cplx> x) -> Cplx.arctan(x.get(0)));
		/** (EN) Inverse cotangent (IT) Arcocotangente */
		map.put("arccot", (ArrayList<Cplx> x) -> Cplx.arccot(x.get(0)));
		/** (EN) Inverse secant (IT) Arcosecante */
		map.put("arcsec", (ArrayList<Cplx> x) -> Cplx.arcsec(x.get(0)));
		/** (EN) Inverse cosecant (IT) Arcocosecante */
		map.put("arccsc", (ArrayList<Cplx> x) -> Cplx.arccsc(x.get(0)));

		/** (EN) Hyperbolic sine (IT) Seno iperbolico */
		map.put("sinh", (ArrayList<Cplx> x) -> Cplx.sinh(x.get(0)));
		/** (EN) Hyperbolic Cosine (IT) Coseno iperbolico */
		map.put("cosh", (ArrayList<Cplx> x) -> Cplx.cosh(x.get(0)));
		/** (EN) Hyperbolic Tangent (IT) Tangente iperbolica */
		map.put("tanh", (ArrayList<Cplx> x) -> Cplx.tanh(x.get(0)));
		/** (EN) Hyperbolic Cotangent (IT) Cotangente iperbolica */
		map.put("coth", (ArrayList<Cplx> x) -> Cplx.coth(x.get(0)));
		/** (EN) Hyperbolic Secant (IT) Secante iperbolica */
		map.put("sech", (ArrayList<Cplx> x) -> Cplx.sech(x.get(0)));
		/** (EN) Hyperbolic Cosecant (IT) Cosecante iperbolica */
		map.put("csch", (ArrayList<Cplx> x) -> Cplx.csch(x.get(0)));

		/** (EN) Inverse hyperbolic sine (IT) Arcoseno iperbolico */
		map.put("arsinh", (ArrayList<Cplx> x) -> Cplx.arsinh(x.get(0)));
		/** (EN) Inverse hyperbolic cosine (IT) Arcocoseno iperbolico */
		map.put("arcosh", (ArrayList<Cplx> x) -> Cplx.arcosh(x.get(0)));
		/** (EN) Inverse hyperbolic tangent (IT) Arcotangente iperbolica */
		map.put("artanh", (ArrayList<Cplx> x) -> Cplx.artanh(x.get(0)));
		/** (EN) Inverse hyperbolic cotangent (IT) Arcocotangente iperbolica */
		map.put("arcoth", (ArrayList<Cplx> x) -> Cplx.arcoth(x.get(0)));
		/** (EN) Inverse hyperbolic secant (IT) Arcosecante iperbolica */
		map.put("arsech", (ArrayList<Cplx> x) -> Cplx.arsech(x.get(0)));
		/** (EN) Inverse hyperbolic cosecant (IT) Arcocosecante iperbolica */
		map.put("arcsch", (ArrayList<Cplx> x) -> Cplx.arcsch(x.get(0)));

		// (EN) Misc functions (IT) Funzioni varie

		/** (EN) Gamma function (IT) Funzione gamma */
		map.put("gamma", (ArrayList<Cplx> x) -> gamma(x.get(0)));
		/** (EN) Lambert W (main branch) (IT) W di Lambert (ramo principale) */
		map.put("lambertW0", (ArrayList<Cplx> x) -> new Cplx(lambertW0(x.get(0).real())));
		/** (EN) Lambert W (-1 branch) (IT) W di Lambert (ramo -1) */
		map.put("lambertWm1", (ArrayList<Cplx> x) -> new Cplx(lambertWm1(x.get(0).real())));
		/** (EN) Error function (IT) Funzione degli errori */
		map.put("erf", (ArrayList<Cplx> x) -> new Cplx(erf(x.get(0).real())));
		/** (EN) Logistic function (IT) Funzione logistica */
		map.put("logistic", (ArrayList<Cplx> x) -> new Cplx(1. / (1 - Math.exp(-x.get(1).real() * x.get(0).real()))));
		/** (EN) Elliptic integral 1st kind (IT) Integrale ellittico 1° tipo */
		map.put("ell_int1", (ArrayList<Cplx> x) -> new Cplx(ell_int1(x.get(0).real(), x.get(1).real())));
		/** (EN) Elliptic integral 2nd kind (IT) Integrale ellittico 2° tipo */
		map.put("ell_int2", (ArrayList<Cplx> x) -> new Cplx(ell_int2(x.get(0).real(), x.get(1).real())));
		/** (EN) Elliptic integral 3rd kind (IT) Integrale ellittico 3° tipo */
		map.put("ell_int3",
				(ArrayList<Cplx> x) -> new Cplx(ell_int3(x.get(0).real(), x.get(1).real(), x.get(2).real())));

		// *** Funzioni a più variabili ***

		map.put("rnd", (ArrayList<Cplx> x) -> {
			/**
			 * (EN) Random number in open interval [a,b[ (default [0,1[) With complex input,
			 * result has: Re in [Re(a),Re(b)[, Im in [Im(a),Im(b)[
			 * 
			 * (IT) Numero casuale nell'intervallo aperto [a,b[ (default [0,1[) Con
			 * parametri complessi, il risultato ha: Re in [Re(a),Re(b)[, Im in
			 * [Im(a),Im(b)[
			 */
			double rb = x.size() > 0 ? x.get(0).real() : 1.0;
			double ra = x.size() > 1 ? x.get(1).real() : 0.0;
			double r = ra + Math.random() * (rb - ra);
			double ib = x.size() > 0 ? x.get(0).imag() : 0.0;
			double ia = x.size() > 1 ? x.get(1).imag() : 0.0;
			double i = ia + Math.random() * (ib - ia);
			return new Cplx(r, i);
		});

		map.put("rndint", (ArrayList<Cplx> x) -> {
			/**
			 * (EN) Random INTEGER in open interval [a,b[ (default [0,1[) Imaginary parts
			 * ignored
			 * 
			 * (IT) Numero INTERO casuale nell'intervallo aperto [a,b[ (default [0,1[) Si
			 * trascurano le parti immaginarie
			 */
			double b = x.size() > 0 ? x.get(0).real() : 1.0;
			double a = x.size() > 1 ? x.get(1).real() : 0.0;
			if (a <= b) {
				a = Math.ceil(a);
				b = Math.floor(b);
			} else {
				double temp = a;
				a = Math.floor(b);
				b = Math.floor(temp);
			}
			return new Cplx(a + Math.floor(Math.random() * (b - a)));
		});

		/** (EN) Binomial coefficient (IT) Coefficiente binomiale */
		map.put("binom", (ArrayList<Cplx> x) -> {
			if (!x.get(0).isReal() || !x.get(1).isReal()) {
				throw new RuntimeException("Illegal argument(s): must be integer >=0");
			}
			double n = x.get(0).real(), k = x.get(1).real();
			return new Cplx(fact(n) / (fact(k) * fact(n - k)));
		});

		// *** Funzioni di array ***
		/** (EN) Sum (IT) Somma */
		map.put("sum", (ArrayList<Cplx> x) -> sum(x));
		/** (EN) Sum of squares (IT) Somma dei quadrati */
		map.put("sum_sq", (ArrayList<Cplx> x) -> sum_sq(x));
		/** (EN) Product (IT) Prodotto */
		map.put("prod", (ArrayList<Cplx> x) -> prod(x));
		/** (EN) Mean (IT) Media */
		map.put("mean", (ArrayList<Cplx> x) -> mean(x));
		/** (EN) Geometric mean (IT) Media geometrica */
		map.put("geom_mean", (ArrayList<Cplx> x) -> geom_mean(x));
		/** (EN) Maximum (IT) Massimo */
		map.put("max", (ArrayList<Cplx> x) -> max(x));
		/** (EN) Minimum (IT) Minimo */
		map.put("min", (ArrayList<Cplx> x) -> min(x));
		/** (EN) Median (IT) Mediana */
		map.put("median", (ArrayList<Cplx> x) -> median(x));
		/** (EN) GCD (IT) MCD */
		map.put("gcd", (ArrayList<Cplx> x) -> gcd(x));
		/** (EN) lcm (IT) mcm */
		map.put("lcm", (ArrayList<Cplx> x) -> lcm(x));

		return map;
	}

	// -------------------
	/**
	 * (EN) Utility functions
	 * 
	 * (IT) Funzioni accessorie
	 */

	public static boolean isValidName(String name) {
		/**
		 * (EN) Check if the given string is a valid variable name: 1) must contain only
		 * letters, digits or underscore; 2) must start with an uppercase letter; 3)
		 * cannot be already assigned
		 * 
		 * (IT) Controlla se la stringa data è un nome valido per le variabili: 1) deve
		 * contenere solo lettere, cifre o trattini bassi 2) deve iniziare con una
		 * lettera maiuscola 3) non deve essere già assegnato
		 */
		if (name == null || name.isBlank()) {
			return false;
		}
		if (name.charAt(0) < 'A' || name.charAt(0) > 'Z') {
			return false;
		}
		for (int i = 1; i < name.length(); i++) {
			int ch = name.charAt(i);
			if (!((ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z') || (ch >= '0' && ch <= '9') || ch == '_')) {
				return false;
			}
		}
		return !(constMap.containsKey(name) || tm.getData().containsKey(name));
	}

	private static double integral(Function<Double, Double> f, double a, double b) {
		/**
		 * (EN) Computes the definite integral of the given function on an interval
		 * Integration algorithm: Simpson 3/8
		 * 
		 * (IT) Calcola l'integrale definito della funzione data fra due estremi
		 * Algoritmo di integrazione: Simpson 3/8
		 */
		double I = 0.;
		double L = b - a;
		double dx = L / 100.;
		for (int i = 0; i < 100; i++) {
			double x0 = a + L * i / 100., x1 = x0 + dx / 3, x3 = a + L * (i + 1) / 100., x2 = x3 - dx / 3;
			double y0 = f.apply(x0), y1 = f.apply(x1), y2 = f.apply(x2), y3 = f.apply(x3);
			I += (y0 + 3 * y1 + 3 * y2 + y3) * dx;
		}
		return I / 8.;
	}

	// --------------------------------
	// *** Funzioni matematiche: ***
	// --------------------------------

	public static Cplx sum(ArrayList<Cplx> x) {
		/**
		 * (EN) Sum (IT) Somma
		 */
		Cplx s = Cplx.ZERO;
		for (Cplx xi : x) {
			s = s.add(xi);
		}
		return s;
	}

	public static Cplx sum_sq(ArrayList<Cplx> x) {
		/**
		 * (EN) Sum of squares (IT) Somma dei quadrati
		 */
		Cplx s = Cplx.ZERO;
		for (Cplx xi : x) {
			s = s.add(Cplx.pow(xi, 2));
		}
		return s;
	}

	public static Cplx prod(ArrayList<Cplx> x) {
		/**
		 * (EN) Product (IT) Prodotto
		 */
		Cplx p = Cplx.ONE;
		for (Cplx xi : x) {
			p = p.mult(xi);
		}
		return p;
	}

	public static Cplx mean(ArrayList<Cplx> x) {
		/**
		 * (EN) Arithmetic mean (IT) Media aritmetica
		 */
		return Cplx.divide(sum(x), new Cplx(x.size()));
	}

	public static Cplx geom_mean(ArrayList<Cplx> x) {
		/**
		 * (EN) Geometric mean (IT) Media geometrica
		 */
		return Cplx.pow(prod(x), 1. / x.size());
	}

	public static Cplx harm_mean(ArrayList<Cplx> x) {
		/**
		 * (EN) Harmonic mean (IT) Media armonica
		 */
		Cplx h = Cplx.ZERO;
		for (Cplx xi : x) {
			if (xi.isZero())
				return Cplx.NAN;
			h = h.add(Cplx.inverse(xi));
		}
		return new Cplx(x.size()).div(h);
	}

	// (EN) The functions max, min, median, gcd, lcm
	// only consider the real part
	// (IT) Le funzioni max, min, median, gcd, lcm
	// considerano solo la parte reale

	public static Cplx max(ArrayList<Cplx> x) {
		/**
		 * (EN) Maximum element (IT) Elemento massimo
		 */
		double m = x.get(0).real();
		for (int i = 1; i < x.size(); i++) {
			if (x.get(i).real() > m)
				m = x.get(i).real();
		}
		return new Cplx(m);
	}

	public static Cplx min(ArrayList<Cplx> x) {
		/**
		 * (EN) Minimum element (IT) Elemento minimo
		 */
		double m = x.get(0).real();
		for (int i = 1; i < x.size(); i++) {
			if (x.get(i).real() < m)
				m = x.get(i).real();
		}
		return new Cplx(m);
	}

	public static Cplx median(ArrayList<Cplx> x) {
		/**
		 * (EN) Median (IT) Mediana
		 */
		Collections.sort(x, new Comparator<Cplx>() {
			public int compare(Cplx a, Cplx b) {
				double aa = a.real(), bb = b.real();
				return (int) Math.signum(aa - bb);
			}
		});
		int n2 = Math.floorDiv(x.size() - 1, 2);
		if (Math.floorMod(x.size(), 2) == 0) {
			return new Cplx(0.5 * (x.get(n2).real() + x.get(n2 + 1).real()));
		} else {
			return x.get(n2);
		}
	}

	private static long gcd2(long a, long b) {
		/**
		 * (EN) Auxiliary function for computing GCD (greatest common divisor) of two
		 * numbers (Euclid's method) (IT) Funzione ausiliaria per il calcolo del MCD
		 * (massimo comune divisore) di due numeri (metodo di Euclide)
		 */
		long aa = Math.max(a, b);
		long bb = Math.min(a, b);
		long r = aa % bb;
		while (r > 0) {
			aa = bb;
			bb = r;
			r = aa % bb;
		}
		return bb;
	}

	public static Cplx gcd(ArrayList<Cplx> x) {
		/**
		 * (EN) GCD (greatest common divisor) (Euclid's method) (IT) MCD (massimo comune
		 * divisore) (metodo di Euclide)
		 */
		int n = x.size();
		if (n < 1)
			return Cplx.ZERO;
		ArrayList<Long> nn = new ArrayList<Long>();
		for (Cplx xi : x) {
			if (xi.real() % 1 != 0)
				return Cplx.NAN;
			nn.add((long) xi.real());
		}
		for (long ni : nn) {
			if (ni == 0)
				return Cplx.ZERO;
		}
		if (n == 1)
			return x.get(0);

		long a = nn.get(0);
		long b = a;
		for (int i = 1; i < n; i++) {
			b = gcd2(a, nn.get(i));
			if (b == 1)
				return new Cplx(b);
			a = b;
		}
		return new Cplx(b);
	}

	public static Cplx lcm(ArrayList<Cplx> x) {
		/**
		 * (EN) lcm (least common multiple) (IT) mcm (minimo comune multiplo)
		 */
		int n = x.size();
		if (n < 1)
			return Cplx.ZERO;
		ArrayList<Long> nn = new ArrayList<Long>();
		for (Cplx xi : x) {
			if (xi.real() % 1 != 0)
				return Cplx.NAN;
			nn.add((long) xi.real());
		}
		for (long ni : nn) {
			if (ni == 0)
				return Cplx.ZERO;
		}
		if (n == 1)
			return x.get(0);

		long a = nn.get(0);
		for (int i = 1; i < n; i++) {
			a = a * nn.get(i) / gcd2(a, nn.get(i));
		}
		return new Cplx(a);
	}

	// fattoriale (naif per n<100, poi Stirling 2° ordine)
	public static double fact(double n) {
		/**
		 * (EN) Factorial of an integer (throws exception for non-integer or negative
		 * argument) Naif method for argument <100, else 2nd order Stirling approx.
		 * 
		 * (IT) Fattoriale di un intero (dà errore se l'argomento è non-intero o
		 * negativo) Metodo naif per argomento <100, altrimenti approssimazione Stirling
		 * 2° ordine
		 */
		if (n < 0 || n % 1 != 0) {
			throw new RuntimeException("Illegal argument: must be an integer >=0");
		}
		if (n <= 1) {
			return 1.0;
		} else if (n >= 100) {
			return Math.sqrt(2 * Math.PI * n) * Math.pow(n / Math.E, n) * (1 + 1 / (12 * n));
		} else {
			double f = 1;
			for (int i = 2; i <= n; i++) {
				f *= i;
			}
			return f;
		}
	}

	public static double dfact(double n) {
		/**
		 * (EN) Double Factorial of an integer (product of previous integers of the same
		 * parity) Throws exception for non-integer or negative argument
		 * 
		 * (IT) Fattoriale doppio di un intero (prodotto degli interi minori con stessa
		 * parità) Dà errore se l'argomento è non-intero o negativo
		 */
		if (n < 0 || n % 1 != 0) {
			throw new RuntimeException("Illegal argument: must be an integer >=0");
		}
		if (n <= 1) {
			return 1.0;
		} else if (n % 2 == 0) {
			double k = n / 2;
			return Math.pow(2, k) * fact(k);
		} else {
			double k = (n - 1) / 2;
			return fact(n) / (Math.pow(2, k) * fact(k));
		}
	}

	public static double lambertW0(double x) {
		/**
		 * (EN) Principal branch (0) of Lambert W function (inverse function of x*e^x)
		 * Approximation via Newton's method
		 * 
		 * (IT) Ramo principale (0) della funzione W di Lambert (funzione inversa di
		 * x*e^x) Approssimazione col metodo di Newton
		 */
		if (x < -1.0 / Math.E) {
			return Double.NaN;
		}
		double w = Math.log1p(x);
		double wp = w;
		int it = 0;
		do {
			it++;
			wp = w;
			w = wp - (wp * Math.exp(wp) - x) / (Math.exp(wp) + wp * Math.exp(wp));
		} while (it < 50 && Math.abs(w - wp) > 1e-15);
		return w;
	}

	public static double lambertWm1(double x) {
		/**
		 * (EN) Secondary branch (-1) of Lambert W function (inverse function of x*e^x)
		 * Approximation with truncated logarithmic series
		 * 
		 * (IT) Ramo secondario (-1) della funzione W di Lambert (funzione inversa di
		 * x*e^x) Approssimazione con serie logaritmica troncata
		 */
		if (x < -1. / Math.E || x >= 0)
			return Double.NaN;
		double L1 = Math.log(-x), L2 = Math.log(-L1);
		return L1 - L2 + L2 / L1 + L2 * (L2 - 2) / (2 * L1 * L1) + L2 * (2 * L2 * L2 - 9 * L2 + 6) / (6 * L1 * L1 * L1)
				+ L2 * (3 * L2 * L2 * L2 - 22 * L2 * L2 + 36 * L2 - 12) / (12 * Math.pow(L1, 4))
				+ L2 * (12 * L2 * L2 * L2 * L2 - 125 * L2 * L2 * L2 + 350 * L2 * L2 - 300 * L2 + 60)
						/ (60 * Math.pow(L1, 5));
	}

	public static double erf(double x) {
		/**
		 * (EN) Error function (primitive of normalized Gaussian function e^-x^2) (IT)
		 * Funzione degli errori (primitiva della gaussiana normalizzata e^-x^2)
		 */
		if (Math.abs(x) >= 5.91)
			return Math.signum(x);
		return integral(t -> Math.exp(-(t * t)), 0., x) / (4. * Math.sqrt(Math.PI));
	}

	public static double ell_int1(double x, double k) {
		/**
		 * (EN) Elliptic integral of 1st kind (IT) Integrale ellittico del 1° tipo
		 */
		return integral(t -> 1. / Math.sqrt(1 - k * k * Math.pow(Math.sin(t), 2)), 0, x);
	}

	public static double ell_int2(double x, double k) {
		/**
		 * (EN) Elliptic integral of 2nd kind (IT) Integrale ellittico del 2° tipo
		 */
		return integral(t -> Math.sqrt(1 - k * k * Math.pow(Math.sin(t), 2)), 0, x);
	}

	public static double ell_int3(double x, double n, double k) {
		/**
		 * (EN) Elliptic integral of 3rd kind (IT) Integrale ellittico del 3° tipo
		 */
		return integral(
				t -> 1. / ((1 - n * Math.pow(Math.sin(t), 2)) * Math.sqrt(1 - k * k * Math.pow(Math.sin(t), 2))), 0, x);
	}

	public static final double[] GAMMA_COEFFS = { 676.5203681218851, -1259.1392167224028, 771.32342877765313,
			-176.61502916214059, 12.507343278686905, -0.13857109526572012, 9.9843695780195716e-6,
			1.5056327351493116e-7 };

	public static Cplx gamma(Cplx z) {
		if (z.real() < 0.5) {
			Cplx s = Cplx.sin(z.mult(Math.PI));
			Cplx g = gamma(Cplx.ONE.sub(z));
			return new Cplx(Math.PI).div(Cplx.multiply(s, g));
		}
		Cplx y = z.sub(Cplx.ONE);
		Cplx x = new Cplx(0.99999999999980993);
		for (int i = 0; i < GAMMA_COEFFS.length; i++) {
			x = x.add(new Cplx(GAMMA_COEFFS[i]).div(y.add(new Cplx(1, 1))));
		}
		Cplx t = Cplx.add(y, new Cplx(GAMMA_COEFFS.length - 0.5));
		return new Cplx(Math.sqrt(2 * Math.PI)).mult(Cplx.pow(t, y.add(new Cplx(0.5)))).mult(Cplx.exp(t.neg())).mult(x);
	}
}
