package widgets.contextMenu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JTable;

import QueryHelper.QueryHelper.QueryHelper;
import Routenplaner.AddressVector;
import Routenplaner.Constants;
import Routenplaner.Utils;
import client.Routeplaner;
import database.DatabaseLogic;
import gps_coordinates.GpsCoordinate;
import overview.OverView;
import overview.OverViewLogic;
import tablemodel.CommonModel;
import widgets.IconMenuItem;

@SuppressWarnings("serial")
public class OverViewContextMenu extends widgets.contextMenu.CommonContextMenu implements ActionListener {

	private IconMenuItem removeFlight;
	private IconMenuItem renameFlight;
	private IconMenuItem selectFlight;
	private OverView overView;
	private Routeplaner myRouteplaner;

	public OverViewContextMenu(OverView overView, MouseEvent event) {
		super(event);
		initComponent();
		this.overView = overView;
		showMenu();
	}

	private void initComponent() {
		myRouteplaner = Routeplaner.getInstance();

		removeFlight = new IconMenuItem("Images/deleteIcon.png", Constants.REMOVEFLIGHT);
		removeFlight.addActionListener(this);
		renameFlight = new IconMenuItem("Images/rename.jpg", Constants.RENAME);
		renameFlight.addActionListener(this);
		selectFlight = new IconMenuItem("Images/showIcon.png", Constants.SELECTFLIGHT);
		selectFlight.addActionListener(this);

		super.add(removeFlight, renameFlight, selectFlight);
		super.activate();
	}

	@Override
	public void actionPerformed(ActionEvent event) {

		Object o = event.getSource();

		if (o.equals(selectFlight)) {
			JTable table = overView.getTable();
			int row = table.getSelectedRow();
			if (row != -1) {
				String flightNumber = (String) table.getValueAt(row, 1);
				ArrayList<GpsCoordinate> flight = null;
				try {
					flight = myRouteplaner.getDatabase().getFlightAsList(flightNumber);
					/** setting start */
					myRouteplaner.setStartGps(
							OverViewLogic.getStartGps(flightNumber, myRouteplaner.getDatabase().getConnection()));
					myRouteplaner.setMaster(flight);
				} catch (SQLException e) {
					e.printStackTrace();
				}
				if (flight != null) {
					CommonModel modelTargets = myRouteplaner.getModelTargets();
					if (!modelTargets.isEmpty()) {
						modelTargets.clear();
					}
					flight.forEach(gps -> modelTargets.addDataRow(new AddressVector(String.valueOf(gps.getId()),
							gps.getStreet(), gps.getCity(), gps.getCountry(), String.valueOf(gps.getLongitude()),
							String.valueOf(gps.getLatitude()))));
					modelTargets.revalidate();
					JLabel statusBar = myRouteplaner.getStatusBar();
					statusBar.setText(DatabaseLogic.getDbName() + File.separator + flightNumber);
					myRouteplaner.setStatusBar(statusBar);
					Routeplaner.flightNumber = flightNumber;
					overView.dispose();
				}
			}
			myRouteplaner.getTabbedPane().setSelectedIndex(1);
			this.dispose();
		}

		else if (o.equals(removeFlight)) {
			JTable table = overView.getTable();
			CommonModel model = overView.getModel();
			DatabaseLogic databaseLogic = myRouteplaner.getDatabase();
			String nameOfFlight = null;

			int[] arrayOfSelectedRows = table.getSelectedRows();

			if (!Utils.isEmpty(arrayOfSelectedRows)) {
				for (int row = 0; row < arrayOfSelectedRows.length; row++) {
					nameOfFlight = (String) table.getValueAt(arrayOfSelectedRows[row], 1);
					try {
						OverViewLogic.removeFlight(nameOfFlight, databaseLogic);
						QueryHelper.dropTable(nameOfFlight,
								myRouteplaner.getDatabase().getConnection().getConnection());
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
				model.deleteRow(arrayOfSelectedRows);
				model.revalidate();
				if (nameOfFlight.equals(myRouteplaner.getFlightNumber())) {
					myRouteplaner.getModelTargets().clear();
				}
			}
			this.dispose();
		}
	}
}