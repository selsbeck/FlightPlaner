package listeners;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import client.Routeplaner;
import overview.OverView;
import widgets.contextMenu.OverViewContextMenu;
import widgets.contextMenu.TargetsContextMenu;

public class TableOverViewMouseListener implements MouseListener {

	private OverViewContextMenu overViewContextMenu;
	private OverView overView;

	public TableOverViewMouseListener(OverView overView, OverViewContextMenu overViewContextMenu) {
		this.overView = overView;
		this.overViewContextMenu = overViewContextMenu;
	}

	@Override
	public void mouseClicked(MouseEvent event) {
		// if (overViewContextMenu != null) {
		// overViewContextMenu.dispose();
		// }
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent event) {

		TargetsContextMenu targetsContextMenu = Routeplaner.getInstance().getTargetContextMenu();
		if (targetsContextMenu != null) {
			targetsContextMenu.dispose();
			targetsContextMenu.dispose();
		}

		if ((event.getSource() == overView.getTable())
				&& (event.getSource() != Routeplaner.getInstance().getTableTargets())
				&& (event.getButton() == MouseEvent.BUTTON3)) {
			if (overViewContextMenu != null) {
				overViewContextMenu.dispose();
			}
			overViewContextMenu = new OverViewContextMenu(overView, event);
			overView.setOverViewContextMenu(overViewContextMenu);
		}

	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

}
