package de.baane.wipe.view.table;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import de.baane.wipe.model.RaidStatus;

@Deprecated
public class TableViewSwing extends JPanel {
	private static final long serialVersionUID = -1011547732583338893L;
	
	private JTable table;
	private DefaultTableModel model;
	
	public TableViewSwing() {
		initGUI();
	}
	
	private void initGUI() {
		BorderLayout l = new BorderLayout();
		this.setLayout(l);
		
		this.add(new JScrollPane(getTable()), BorderLayout.CENTER);
	}
	
	public JTable getTable() {
		if (table == null) {
			table = new JTable() {
				private static final long serialVersionUID = -4668251572423106143L;

				@SuppressWarnings({ "unchecked", "rawtypes" })
				@Override
				public Class getColumnClass(int column) {
					switch (column) {
						case 0:
							return String.class;
						default:
							return RaidStatus.class;
					}
				}
			};
			table.setModel(getModel());
			table.getTableHeader().setReorderingAllowed(false);
		}
		return table;
	}
	
	public DefaultTableModel getModel() {
		if (model == null) {
			String[] columnNames = { "Instance" };
			model = new DefaultTableModel(null, columnNames);
		}
		return model;
	}
	
	public void setModel(Object[][] content, String[] columnNames) {
		model = new DefaultTableModel(content, columnNames);
		table.setModel(model);
	}
	
}
