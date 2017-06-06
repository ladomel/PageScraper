import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

public class PageScrapperFrame extends JFrame{
	
	private JButton scrape;
	private JTextField scrape_txt;
	private PageScrapper ps;
	private JLabel status,scraping;
	private ArrayList<String> list;
	private JTable table;
	private DefaultTableModel model;
	private static final String NO_RESULTS = "No results!";
	
	
	public PageScrapperFrame(){
		super("Page Scrapper");
		initGUI();
		addListeners();
		
		setMinimumSize(new Dimension(700, 400));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setVisible(true);
		
		ps = new PageScrapper();
	}

	private void initGUI(){
		status = new JLabel("Status: ");
		scraping = new JLabel("Results for: ");
		scrape = new JButton("Scrape");
		scrape.setPreferredSize(new Dimension(80, 25));
		scrape_txt = new JTextField();
		scrape_txt.setPreferredSize(new Dimension(500,25));
		JPanel topP = new JPanel(new FlowLayout());
		topP.add(scrape_txt);
		topP.add(scrape);
		topP.setMaximumSize(new Dimension(700, 50));
		
		model = new DefaultTableModel(new String[] { "url", "type"}, 0){
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int x, int y){ return false; }
		};
		
		table = new JTable(model);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		table.getColumnModel().getColumn(1).setMaxWidth(50);
		
		JScrollPane sp = new JScrollPane(table);
		sp.setPreferredSize(new Dimension(700,300));
		
		JPanel topP1 = new JPanel(new BorderLayout());
		topP1.add(scraping, BorderLayout.LINE_START);
		topP1.setMaximumSize(new Dimension(100000000, 50));
		
		JPanel botP = new JPanel(new BorderLayout());
		botP.add(status, BorderLayout.LINE_START);
		botP.setMaximumSize(new Dimension(100000000, 50));
		
		JPanel fullP = new JPanel();
		fullP.setLayout(new BoxLayout(fullP, BoxLayout.Y_AXIS));
		fullP.add(topP);
		fullP.add(topP1);
		fullP.add(sp);
		fullP.add(botP);
		add(fullP);
	}
	
	private void addListeners(){
		scrape.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				scrape(scrape_txt.getText());
			}
		});
		table.addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent me) {
		        JTable table =(JTable)me.getSource();
		        int r = table.rowAtPoint(me.getPoint());
		        if (r >=0 && me.getClickCount() > 1) {
		        	String s = (String)table.getModel().getValueAt(r, 0);
		        	if (!s.equals(NO_RESULTS)) scrape(s);
		        }
		    }
		});
	}
	
	private void scrape(String s){
		scrape.setEnabled(false);
		status.setText("Status: Scraping...");
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				fillTable(ps.PageDownload(s));
				status.setText("Status: " + ps.PageGetError());
				scrape_txt.setText("");
				scrape.setEnabled(true);
				scraping.setText("Results for: " + s);
			}
		});
	}

	private void fillTable(ArrayList<PageScrapper.Pair> a){
		model.setRowCount(0);
		if (a == null) {
			model.addRow(new Object[]{NO_RESULTS});
			return;
		}
		for (PageScrapper.Pair p : a){
			String type = "IMAGE";
			if (p.PairGetType() == PageScrapper.DATA_TYPE.A) type = "LINK";
			model.addRow(new Object[]{p.PairGetValue(), type});
		}
	}
	
	public static void main(String[] args) {
		PageScrapperFrame psf = new PageScrapperFrame();
	}
	
}
