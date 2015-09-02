package de.baane.wipe.control;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;

import de.baane.wipe.model.Character;
import de.baane.wipe.model.CharacterClass;
import de.baane.wipe.model.Instance;
import de.baane.wipe.model.RaidStatus;
import de.baane.wipe.util.ColorUtil;
import de.baane.wipe.view.table.TableViewFX;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class TableControl extends TableControlBase {
	@SuppressWarnings("unused")
	private class ColorRenderer extends DefaultTableCellRenderer {
		private static final long serialVersionUID = -5548144955513457488L;
		
		@Override
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			
			Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			
			if (column == 0) {
				CharacterClass charClass = getData().getCharacters().get(row).getCharClass();
				Color color = ColorUtil.getAwtColor(charClass.getColor());
				
				setBackground(color);
				if (getBrightness(color) < 130) setForeground(Color.WHITE);
				else setForeground(Color.BLACK);
				
				setFont(getFont().deriveFont(Font.BOLD));
			}
			if (value instanceof RaidStatus) {
				RaidStatus v = (RaidStatus)value;
				Color color = ColorUtil.getAwtColor(v.getColor());
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
	
	private TableViewFX view;
	
	public TableControl() {
		view = new TableViewFX();
		load(FileControl.SAVE_FILE);
	}
	
	@Override
	public void fillTable() {
		ArrayList<Character> characters = getData().getCharacters();
		ArrayList<Instance> instances = getData().getInstances();
		
		String[] columnNames = readColumnNames(instances);
		updateProgresses(characters, instances);
		
		view.setModel(characters, columnNames);
		
//		view.getModel().addTableModelListener(getTableModelListener());
		addTableColorRenderer();
//		initCellEditor();
	}

	/**
	 * Reading column names by saved instances.
	 * 
	 * @param instances
	 * @return Array with instance names.
	 */
	private String[] readColumnNames(ArrayList<Instance> instances) {
		String[] columnNames = new String[instances.size()+1];
		int j = 0;
		columnNames[j] = localize("Character");
		for (Instance i : instances) {
			if(DEBUG) System.out.println("Filltable (INI) ::  "+i.getName());
			j++;
			columnNames[j] = i.getName();
		}
		return columnNames;
	}

	private void updateProgresses(ArrayList<Character> characters, ArrayList<Instance> instances) {
		for (Character c : characters) {
			if(DEBUG) System.out.println("Filltable (CHAR) :: "+c.getName());
			LinkedHashMap<Instance, RaidStatus> progress = c.getProgresses();
			
			A: for (Instance ini : instances) {
				for (Entry<Instance, RaidStatus> e : progress.entrySet()) {
					if (ini.equals(e.getKey())) continue A;
				}
				progress.put(ini, RaidStatus.DEFAULT);
			}
		}
	}
	
	//TODO
	private void addTableColorRenderer() {
//		askView().getTable().setDefaultRenderer(Object.class, new ColorRenderer());
	}

	//TODO
	@SuppressWarnings({ "unused", "rawtypes" })
	private void initCellEditor() {
		TableView<Character> table = askView().getTable();
		for (int i = 0; i < table.getColumns().size(); i++) {
			if (!table.getColumns().get(i).equals(RaidStatus.class)) continue;
			TableColumn column = table.getColumns().get(i);
			ComboBox<RaidStatus> box = new ComboBox<RaidStatus>();
			for (RaidStatus s : RaidStatus.values()) {
				box.getItems().add(s);
			}
			//TODO
//			column.setCellEditor(new DefaultCellEditor(box));
		}
	}
	
	public TableViewFX askView() {
		return view;
	}
	
	@SuppressWarnings("unused")
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
