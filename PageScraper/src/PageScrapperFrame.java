import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

public class PageScrapperFrame extends JFrame{
	private static final long serialVersionUID = 1L;
	
	
	private JButton scrape, savebutton;
	private JTextField scrape_txt;
	private PageScrapper ps;
	private JLabel status,scraping;
	private JTable table;
	private DefaultTableModel model;
	private static final String NO_RESULTS = "No results!";
	private JFrame img_frame;
	private PSFile psfile;
	private JFileChooser filechooser;
	private ArrayList<PageScrapper.Pair> links;
	private boolean permitScraping;
	private String last_scraped;
	
	
	public PageScrapperFrame(){
		super("Page Scrapper");
		initGUI();
		addListeners();
		
		setMinimumSize(new Dimension(700, 400));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setVisible(true);
		
		ps = new PageScrapper();
		psfile = new PSFile();
		links = null;
		permitScraping = true;
		last_scraped = "";
	}

	private void initGUI(){
		filechooser = new JFileChooser();
		status = new JLabel("Status: ");
		savebutton = new JButton("Save Results");
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
		table.setToolTipText("Double Click to Scrape/Display Image");
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
		botP.add(savebutton, BorderLayout.LINE_END);
		
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
				last_scraped = scrape_txt.getText();
				scrape(last_scraped);
			}
		});
		table.addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent me) {
		        JTable table =(JTable)me.getSource();
		        int r = table.rowAtPoint(me.getPoint());
		        if (r >=0 && me.getClickCount() > 1) {
		        	String url = (String)table.getModel().getValueAt(r, 0);
		        	String type = (String)table.getModel().getValueAt(r, 1);
		        	if (url.equals(NO_RESULTS)) return;
		        	if (type.equals("LINK")) scrape(url);
		        	else if (type.equals("IMAGE")) showImage(url);
		        }
		    }
		});
		savebutton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				permitScraping = false;
				scrape.setEnabled(false);
				status.setText("Status: Saving...");
				
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						filechooser.showSaveDialog(PageScrapperFrame.this);
						psfile.PSFileSave(links,filechooser.getSelectedFile(),last_scraped);
						status.setText("Status: " + psfile.PSFileGetError());
						scrape.setEnabled(true);
					}
				});
				permitScraping = true;
			}
		});
	}
	
	private void scrape(String s){
		if (!permitScraping) return;
		scrape.setEnabled(false);
		status.setText("Status: Scraping...");
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				links = ps.PageDownload(s);
				fillTable();
				status.setText("Status: " + ps.PageGetError());
				scrape_txt.setText("");
				scrape.setEnabled(true);
				scraping.setText("Results for: " + s);
			}
		});
	}

	private void fillTable(){
		model.setRowCount(0);
		if (links == null) {
			model.addRow(new Object[]{NO_RESULTS});
			return;
		}
		for (PageScrapper.Pair p : links){
			String type = "IMAGE";
			if (p.PairGetType() == PageScrapper.DATA_TYPE.LINK) type = "LINK";
			model.addRow(new Object[]{p.PairGetValue(), type});
		}
		
	}
	
	private void showImage(String url){
		img_frame = new JFrame();
		Image im = ps.PageGetImage(url);
		if (im != null) img_frame.add(new JScrollPane(new JLabel(new ImageIcon(im))));
			else img_frame.add(new JScrollPane(new JLabel("Could not get this image")));
		img_frame.setMinimumSize(new Dimension(1000, 600));
		img_frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		img_frame.pack();
		img_frame.setVisible(true);
	}
	
	public static void main(String[] args) {
		PageScrapperFrame psf = new PageScrapperFrame();
	}
	
}
