import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

public class PSResults extends JFrame {
	
	private JTable table;
	private DefaultTableModel model;
	private PSDB psdb;
	
	public PSResults(DB_PREFERENCES db_pref){
		super("Past Searches");
		setMinimumSize(new Dimension(800, 500));
		addGUI();
		addListener();
		
		psdb = new PSDB(db_pref);
		updateTable(psdb.PSDG_GetQueries());
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		pack();
		setVisible(true);
	}
	
	private void updateTable(ArrayList<PSDB.PSDB_RESULT> queries){
		model.setRowCount(0);
		if (queries == null) return;
		for (PSDB.PSDB_RESULT r : queries){
			model.addRow(new Object[]{r.PSDB_RESULT_getId(),r.PSDB_RESULT_getQuery(),r.PSDB_RESULT_getDate().toString()});
		}
	}
	
	private void addGUI(){
		model = new DefaultTableModel(new String[] { "id", "query", "time"}, 0){
			private static final long serialVersionUID = 1L;
			@Override
			public boolean isCellEditable(int x, int y){ return false; }
		};
		table = new JTable(model);
		table.setToolTipText("Click to Show Results");
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		table.getColumnModel().getColumn(0).setMaxWidth(50);
		table.getColumnModel().getColumn(2).setMaxWidth(130);
		table.getColumnModel().getColumn(2).setMinWidth(130);
		JScrollPane sp = new JScrollPane(table);
		add(sp);
	}
	
	public void addListener(){
		table.addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent me) {
		        JTable table =(JTable)me.getSource();
		        int r = table.rowAtPoint(me.getPoint());
		        int id = (int)table.getModel().getValueAt(r, 0);
		        String query = (String)table.getModel().getValueAt(r, 1);
		        SwingUtilities.invokeLater(new Runnable() {
					public void run() {
				        JFrame fr = new JFrame();
						fr.add(new JScrollPane(formLabel(id,query)));
						fr.setMinimumSize(new Dimension(1000, 600));
						fr.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
						fr.pack();
						fr.setVisible(true);
					}
				});
		    }
		});
	}
	
	private JTextArea formLabel(int id,String query){
		JTextArea ta = new JTextArea();
		ArrayList<PSDB.PSDB_RESULT> res = psdb.PSDB_GetQueryResults(id);
		if (res == null) return ta;
		int i = 1;
		String text = "Search Results for \"" + query + "\"\n\n";
		for (PSDB.PSDB_RESULT r : res){
			text += i + ") [" + r.PSDB_RESULT_getType() + "] " + r.PSDB_RESULT_getQuery() + "\n";
			i++;
		}
		ta.setText(text);
		ta.setCaretPosition(0);
		return ta;
	}
	
}
