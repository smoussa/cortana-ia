package se.sics.tac.aw;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.Timer;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import uk.ac.soton.ecs.ia.cortana.AuctionMaster;
import uk.ac.soton.ecs.ia.cortana.ClientPosition;
import uk.ac.soton.ecs.ia.cortana.ClientPositionVariableHotelPrice;
import uk.ac.soton.ecs.ia.cortana.HotelAuction;
import uk.ac.soton.ecs.ia.cortana.Strategy;

public class StrategyTable {

	private StrategyDisplayModel strategyDisplayModel;
	private ClientPreferenceDisplayModel clientPreferenceModel;
	private ClientDisplay display;
	
	public void showGUI(AuctionMaster auctionMaster) {
		if (display == null) {
			strategyDisplayModel = new StrategyDisplayModel(auctionMaster);
			clientPreferenceModel = new ClientPreferenceDisplayModel(auctionMaster);
			display = new ClientDisplay(strategyDisplayModel, clientPreferenceModel);
		}
		display.setVisible(true);
	}
	
	private static class ClientDisplay implements ActionListener, WindowListener {

		private JFrame window;
		private JTable preferenceTable, strategyTable;
		private Timer timer;
		private boolean isVisible = false;

		public ClientDisplay(TableModel clientPreferences, TableModel strategyModel) {
			window = new JFrame("Client Preferences");
			window.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			window.addWindowListener(this);
			window.setSize(800, 520);
			JPanel panel = new JPanel();
			panel.setLayout(new BorderLayout());
			
			preferenceTable = new JTable(clientPreferences);
			panel.add(new JScrollPane(preferenceTable), BorderLayout.NORTH);
			
			strategyTable = new JTable(strategyModel);
			panel.add(new JScrollPane(strategyTable), BorderLayout.CENTER);
			
			window.getContentPane().add(panel);
			timer = new Timer(1000, this);
		}

		public void setVisible(boolean visible) {
			if (visible != isVisible) {
				this.isVisible = visible;
				window.setVisible(visible);
				if (visible) {
					timer.start();
				} else {
					window.dispose();
					timer.stop();
				}
			}
		}

		public void windowOpened(WindowEvent e) {
		}

		public void windowClosing(WindowEvent e) {
		}

		public void windowClosed(WindowEvent e) {
		}

		public void windowIconified(WindowEvent e) {
		}

		public void windowDeiconified(WindowEvent e) {
		}

		public void windowActivated(WindowEvent e) {
		}

		public void windowDeactivated(WindowEvent e) {
		}

		@Override
		public void actionPerformed(ActionEvent ae) {
			Object source = ae.getSource();
			if (source == timer) {
				preferenceTable.repaint();
				strategyTable.repaint();
			}
		}
	}
	
	private static class StrategyDisplayModel extends AbstractTableModel {

		private static final long serialVersionUID = 507252324991226048L;
		
		private final String[] columnName = new String[] { "ID", "Inflight Day", "Outflight Day", "Price Per Hotel"};

		private AuctionMaster auctionMaster;

		public StrategyDisplayModel(AuctionMaster auctionMaster) {
			this.auctionMaster = auctionMaster;
		}
		
		public String getColumnName(int col) {
			return columnName[col];
		}

		public int getRowCount() {
			if (auctionMaster.getStrategy()!=null){
				return auctionMaster.getStrategy().getClientPositionCount();
			}
			return 0;
		}

		public int getColumnCount() {
			return columnName.length;
		}

		public Object getValueAt(int row, int col) {
			
			Strategy strategy = auctionMaster.getStrategy();

			ClientPosition clientPosition = strategy.getClientPosition(row);
			
			String pricePerHotel = "";
			
			if(clientPosition instanceof ClientPositionVariableHotelPrice) {
				ClientPositionVariableHotelPrice cp = (ClientPositionVariableHotelPrice) clientPosition;
				
				for(HotelAuction hotelAuction:cp.hotels) {
					pricePerHotel = pricePerHotel + " " + (Math.round(cp.getHotelPriceForAuction(hotelAuction) * 100) / 100.0);
				}
			}
			
			switch (col) {
			case 0:
				// Ids start at 0 for clients
				return Integer.toString(row);
			case 1:
				return clientPosition.inFlight.AUCTION_DAY;
			case 2:
				return clientPosition.outFlight.AUCTION_DAY;
			case 3:
				return pricePerHotel;
			}
			return null;
		}
	}
	
	private static class ClientPreferenceDisplayModel extends AbstractTableModel {

		private static final long serialVersionUID = 507252324991226048L;
		
		private final String[] columnName = new String[] { "ID", "Inflight Day", "Outflight Day", "Hotel Value", "Entertainment Bonus 1", "Entertainment Bonus 2", "Entertainment Bonus 3" };

		private AuctionMaster auctionMaster;

		public ClientPreferenceDisplayModel(AuctionMaster auctionMaster) {
			this.auctionMaster = auctionMaster;
		}
		
		public String getColumnName(int col) {
			return columnName[col];
		}

		public int getRowCount() {
			return 8;
		}

		public int getColumnCount() {
			return columnName.length;
		}

		public Object getValueAt(int row, int col) {
			switch (col) {
			case 0:
				// Ids start at 0 for clients
				return Integer.toString(row);
			case 1:
				return DayEnum.getDay(auctionMaster.getClientPreference(row, ClientPreferenceEnum.ARRIVAL));
			case 2:
				return DayEnum.getDay(auctionMaster.getClientPreference(row, ClientPreferenceEnum.DEPARTURE));
			case 3:
				return auctionMaster.getClientPreference(row, ClientPreferenceEnum.HOTEL_VALUE);
			case 4:
				return auctionMaster.getClientPreference(row, ClientPreferenceEnum.E1);
			case 5:
				return auctionMaster.getClientPreference(row, ClientPreferenceEnum.E2);
			case 6:
				return auctionMaster.getClientPreference(row, ClientPreferenceEnum.E3);
			}
			return null;
		}
	}
	
}
