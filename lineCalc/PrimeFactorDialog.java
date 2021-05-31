package lineCalc;

import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JTextField;

public class PrimeFactorDialog extends JDialog {
	private static final long serialVersionUID = -5112853614760992826L;
	private JTextField txt_number, txt_factors;
	private JButton btn_calc, btn_close;

	PrimeFactorDialog() {
		super(null, "Fattorizza", Dialog.DEFAULT_MODALITY_TYPE);
		setSize(new Dimension(400, 170));
		setResizable(true);
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		setLayout(new FlowLayout());
		getContentPane().setBackground(new Color(0, 50, 100));

		txt_number = new JTextField("");
		txt_number.setBackground(Color.black);
		txt_number.setForeground(Color.white);
		txt_number.setFont(LineCalc.font);
		txt_number.setPreferredSize(new Dimension(300, 40));
		txt_number.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					update();
				}
			}
		});

		txt_factors = new JTextField("");
		txt_factors.setBackground(Color.black);
		txt_factors.setForeground(new Color(150, 255, 200));
		txt_factors.setFont(LineCalc.font);
		txt_factors.setEditable(false);
		txt_factors.setMinimumSize(new Dimension(350, 40));
		txt_factors.setPreferredSize(new Dimension(350, 40));

		btn_calc = new JButton("CALCOLA");
		btn_calc.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				update();
			}
		});

		btn_close = new JButton("Chiudi");
		btn_close.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});

		getContentPane().add(txt_number);
		getContentPane().add(txt_factors);
		getContentPane().add(btn_calc);
		getContentPane().add(btn_close);

		setLocationRelativeTo(null);
		setVisible(true);
		pack();
		repaint();
	}

	private void update() {
		long n;
		try {
			n = Long.parseLong(txt_number.getText());
		} catch (Exception ex) {
			n = 0L;
		}
		ArrayList<Long[]> f = factors(n);
		txt_factors.setText(printFactors(f));
		revalidate();
		repaint();
	}

	private String printFactors(ArrayList<Long[]> f) {
		if (f.size() <= 0)
			return "";
		String s = String.valueOf(f.get(0)[0]);
		if (f.get(0)[1] > 1) {
			s += "^" + String.valueOf(f.get(0)[1]);
		}
		for (int i = 1; i < f.size(); i++) {
			s += ", " + String.valueOf(f.get(i)[0]);
			if (f.get(i)[1] > 1) {
				s += "^" + String.valueOf(f.get(i)[1]);
			}
		}
		return s;
	}

	private ArrayList<Long[]> factors(long n) {
		ArrayList<Long[]> f = new ArrayList<Long[]>();
		if (n <= 0) {
			f.add(new Long[] { 0L, 1L });
			return f;
		}
		if (n == 1) {
			f.add(new Long[] { 1L, 1L });
			return f;
		}

		if (n % 2 == 0) {
			long m = 1;
			n = n >> 1;
			while (n % 2 == 0) {
				m++;
				n = n >> 1;
			}
			f.add(new Long[] { 2L, m });
		}

		long kmax = (long) Math.floor(Math.sqrt(n));
		for (long k = 3; k <= kmax; k += 2) {
			if (n % k == 0) {
				long m = 1;
				n = Math.floorDiv(n, k);
				while (n % k == 0) {
					m++;
					n = Math.floorDiv(n, k);
				}
				f.add(new Long[] { k, m });
			}
		}

		if (n > 2) {
			f.add(new Long[] { n, 1L });
		}

		return f;
	}
}
