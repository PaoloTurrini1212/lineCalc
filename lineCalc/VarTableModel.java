package lineCalc;

import java.util.HashMap;

import javax.swing.table.AbstractTableModel;

/**
 * (EN) Table model manager class
 * 
 * (IT) Classe per la gestione della tabella delle variabili
 */

public class VarTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 5515666544901706208L;
	private String[] columnNames = { "Name", "Value" };
	private HashMap<String, Cplx> data = new HashMap<String, Cplx>();

	public HashMap<String, Cplx> getData() {
		return data;
	}

	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public String getColumnName(int col) {
		return columnNames[col];
	}

	@Override
	public int getRowCount() {
		return data.size();
	}

	@Override
	public boolean isCellEditable(int row, int col) {
		return true;
	}

	@Override
	public Object getValueAt(int row, int col) {
		String s = (String) data.keySet().toArray()[row];
		if (col == 0) {
			return s;
		} else {
			return data.get(s);
		}
	}

	@Override
	public void setValueAt(Object value, int row, int col) {
		Cplx val = (Cplx) value;
		String s = (String) data.keySet().toArray()[row];
		if (col == 0) {
			String newName = val.toString();
			if (data.containsKey(newName)) {
				return;
			}
			Cplx old = data.get(s);
			data.remove(s);
			data.put(newName, old);
		} else {
			data.replace(s, val);
		}
		fireTableCellUpdated(row, col);
	}

	public void addRow(String key, Cplx value) {
		if (!data.containsKey(key)) {
			data.put(key, value);
		}
		this.fireTableDataChanged();
	}

	public void removeRow(int row) {
		String s = (String) data.keySet().toArray()[row];
		data.remove(s);
		this.fireTableDataChanged();
	}
}
