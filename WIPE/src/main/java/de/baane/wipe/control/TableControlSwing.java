package de.baane.wipe.control;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import de.baane.wipe.model.Character;
import de.baane.wipe.model.CharacterClass;
import de.baane.wipe.model.Instance;
import de.baane.wipe.model.RaidStatus;
import de.baane.wipe.view.TableViewSwing;

public class TableControlSwing extends TableControlBase {
	private class ColorRenderer extends DefaultTableCellRenderer {
		private static final long serialVersionUID = -5548144955513457488L;
		
		@Override
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			
			Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			
			if (column == 0) {
				CharacterClass charClass = getData().getCharacters().get(row).getCharClass();
				Color color = charClass.getColor();
				
				setBackground(color);
				if (getBrightness(color) < 130) setForeground(Color.WHITE);
				else setForeground(Color.BLACK);
				
				setFont(getFont().deriveFont(Font.BOLD));
			}
			if (value instanceof RaidStatus) {
				RaidStatus v = (RaidStatus)value;
				Color color = v.getColor();
				c.setBackground(color);
				
				if (getBrightness(color) < 130) setForeground(Color.WHITE);
				else setForeground(Color.BLACK);
			}

			return c;
		}
		
		private int getBrightness(Color c) {
		    return (int) Math.sqrt(
		      c.getRed() * c.getRed() * .241 +
		      c.getGreen() * c.getGreen() * .691 +
		      c.getBlue() * c.getBlue() * .068);
		}
	}
	
	public static final boolean DEBUG = true;
	
	private TableViewSwing view;
	
	public TableControlSwing() {
		view = new TableViewSwing();
		load(FileControl.SAVE_FILE);
	}
	
	/**
	 * TODO: REFUCKTOR!
	 */
	@Override
	public void fillTable() {
		ArrayList<Character> characters = getData().getCharacters();
		ArrayList<Instance> instances = getData().getInstances();
		
		String[] columnNames = new String[instances.size()+1];
		int j = 0;
		columnNames[j] = localize("Character");
		for (Instance i : instances) {
			if(DEBUG) System.out.println("Filltable (INI) ::  "+i.getName());
			j++;
			columnNames[j] = i.getName();
		}
		
		Object[][] content = new Object[characters.size()][instances.size()];
		
		int charIdx = 0;
		for (Character c : characters) {
			if(DEBUG) System.out.println("Filltable (CHAR) :: "+c.getName());
			LinkedHashMap<Instance, RaidStatus> progress = c.getProgresses();
			
			// init
			Object[] charIniEntryRow = new Object[columnNames.length];
			for (int i = 0; i < charIniEntryRow.length; i++) charIniEntryRow[i] = RaidStatus.DEFAULT;
			charIniEntryRow[0] = c.getName();
			
			for (Entry<Instance, RaidStatus> e : progress.entrySet()) {
				int idx = findColumnNameIdx(columnNames, e.getKey().getName());
				if(idx == -1) continue;
				charIniEntryRow[idx] = e.getValue();
				if (DEBUG) {
					System.out.println("Filltable :: " + c.getName() + " | " 
										+ e.getKey().getName() + "("+idx+"): " 
										+ e.getValue());
				}
			}
			content[charIdx] = charIniEntryRow;
			charIdx++;
		}
		
		view.setModel(content, columnNames);
		
		view.getModel().addTableModelListener(getTableModelListener());
		addTableColorRenderer();
		initCellEditor();
	}

	private void addTableColorRenderer() {
		askView().getTable().setDefaultRenderer(Object.class, new ColorRenderer());
	}

	private void initCellEditor() {
		JTable table = askView().getTable();
		for (int i = 0; i < table.getColumnModel().getColumnCount(); i++) {
			if (!table.getColumnClass(i).equals(RaidStatus.class)) continue;
			TableColumn column = table.getColumnModel().getColumn(i);
			JComboBox<RaidStatus> box = new JComboBox<RaidStatus>();
			for (RaidStatus s : RaidStatus.values()) {
				box.addItem(s);
			}
			column.setCellEditor(new DefaultCellEditor(box));
		}
	}
	
	public TableViewSwing askView() {
		return view;
	}
	
	private TableModelListener getTableModelListener() {
		return new TableModelListener() {
			@Override
			public void tableChanged(TableModelEvent e) {
				int row = e.getFirstRow();
				int column = e.getColumn();
				TableModel model = (TableModel) e.getSource();
				Character character = getData().getCharacters().get(row);
				
				Object cellValue = model.getValueAt(row, column);
				updateCell(column, character, cellValue);
			}
		};
	}
	
}
