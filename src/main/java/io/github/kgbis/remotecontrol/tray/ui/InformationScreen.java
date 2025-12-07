package io.github.kgbis.remotecontrol.tray.ui;

import io.github.kgbis.remotecontrol.tray.net.info.NetworkChangeListener;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.util.stream.Collectors;

import static io.github.kgbis.remotecontrol.tray.RemoteControl.REMOTE_PC_CONTROL;

@Singleton
@Slf4j
public class InformationScreen {

	private final JFrame frame;

	private final DefaultTableModel model;

	private final NetworkChangeListener networkChangeListener;

	@Inject
	public InformationScreen(IconImage iconImage, NetworkChangeListener networkChangeListener) {
		this.networkChangeListener = networkChangeListener;

		frame = new JFrame(REMOTE_PC_CONTROL);
		frame.setIconImage(iconImage.getIcon());
		frame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		frame.setLayout(new BorderLayout(10, 10));
		frame.setAlwaysOnTop(true); // <--- configurable
		frame.getRootPane().setBorder(new EmptyBorder(10, 10, 10, 10));

		// ---------------------
		// Header panel (text)
		// ---------------------
		String versionNumber = getClass().getPackage().getImplementationVersion();
		String version = versionNumber == null ? "-DEBUG MODE-" : versionNumber;

		JPanel headerPanel = new JPanel();
		headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
		headerPanel.setBorder(new EmptyBorder(5, 5, 10, 5)); // margen abajo

		// Line 1: Title
		JLabel titleLabel = new JLabel("Remote PC Control " + version);
		titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 12f));

		// Line 2: Description
		JLabel descLabel = new JLabel("Detected local IP and MAC addresses");

		// Line 3: Extra info
		JLabel copyLabel = new JLabel("(copied to clipboard automatically)");
		copyLabel.setFont(copyLabel.getFont().deriveFont(Font.ITALIC, 10f));

		// Añadir en orden (alineados a la izquierda automáticamente con BoxLayout)
		headerPanel.add(titleLabel);
		headerPanel.add(descLabel);
		headerPanel.add(copyLabel);

		frame.add(headerPanel, BorderLayout.NORTH);

		// ---------------
		// IP + MAC Table
		// ---------------
		model = new DefaultTableModel(new Object[] { "IP Address", "MAC" }, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		JTable table = new JTable(model);
		table.setFillsViewportHeight(true);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setShowHorizontalLines(true);
		table.setShowVerticalLines(false);

		JScrollPane scroll = new JScrollPane(table);
		frame.add(scroll, BorderLayout.CENTER);

		// -----------
		// Bottom bar
		// -----------
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));

		JButton copyBtn = new JButton("Copy All");
		copyBtn.addActionListener(e -> copyToClipboard());

		JButton closeBtn = new JButton("Close");
		closeBtn.addActionListener(e -> frame.setVisible(false));

		buttonPanel.add(copyBtn);
		buttonPanel.add(closeBtn);

		frame.add(buttonPanel, BorderLayout.SOUTH);

		frame.setMinimumSize(new Dimension(380, 200));
		frame.pack();
		frame.setVisible(false);
	}

	public void show() {
		loadData();
		frame.pack();
		frame.setResizable(false);
		placeNearTray(frame);
		frame.setVisible(true);
		frame.toFront();
	}

	public boolean isVisible() {
		return frame.isVisible();
	}

	public void hide() {
		frame.setVisible(false);
	}

	private void placeNearTray(JFrame frame) {
		PointerInfo pi = MouseInfo.getPointerInfo();
		Rectangle screen = pi.getDevice().getDefaultConfiguration().getBounds();

		int margin = 20;
		int x = screen.x + screen.width - frame.getWidth() - margin;
		int y = screen.y + screen.height - frame.getHeight() - margin;

		frame.setLocation(x, y);
	}

	// Load IPs and MACs to table
	private void loadData() {
		model.setRowCount(0);
		networkChangeListener.getIpMacMap().forEach((ip, mac) -> model.addRow(new Object[] { ip, mac }));
		// copyToClipboard(); // comportamiento igual que tu clase original
	}

	// Copy to clipboard
	private void copyToClipboard() {
		String toCopy = networkChangeListener.getIpMacMap()
			.entrySet()
			.stream()
			.map(e -> e.getKey() + " -> " + e.getValue())
			.collect(Collectors.joining("\n"));

		StringSelection selection = new StringSelection(toCopy);
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
		log.info("Copied to clipboard:\n{}", toCopy);
	}

}
