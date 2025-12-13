package io.github.kgbis.remotecontrol.tray.ui;

import io.github.kgbis.remotecontrol.tray.misc.ResourcesHelper;
import io.github.kgbis.remotecontrol.tray.net.info.NetworkChangeListener;
import io.github.kgbis.remotecontrol.tray.ui.support.TraySupportDetector;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.stream.Collectors;

import static io.github.kgbis.remotecontrol.tray.RemoteControl.REMOTE_PC_CONTROL;
import static io.github.kgbis.remotecontrol.tray.ui.support.TraySupport.*;
import static io.github.kgbis.remotecontrol.tray.ui.support.TraySupportDetector.*;

@Singleton
@Slf4j
public class InformationScreen {

	private final JFrame frame;

	private final DefaultTableModel model;

	private final NetworkChangeListener networkChangeListener;

	@Inject
	public InformationScreen(NetworkChangeListener networkChangeListener) {
		this.networkChangeListener = networkChangeListener;

		frame = new JFrame(REMOTE_PC_CONTROL);
		frame.setIconImage(ResourcesHelper.getIcon());
		frame.setLayout(new BorderLayout(10, 10));
		frame.setAlwaysOnTop(false);
		frame.getRootPane().setBorder(new EmptyBorder(10, 10, 0, 10));
		frame.setExtendedState(/*isPartialTraySupport() ? Frame.ICONIFIED :*/ Frame.NORMAL);

		// ---------------------
		// Header panel (text)
		// ---------------------
		JPanel headerPanel = buildHeaderPanel();
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
		JPanel buttonBar = buildBottomBar(table);
		frame.add(buttonBar, BorderLayout.SOUTH);

		frame.setMinimumSize(new Dimension(380, 200));
		frame.pack();
		frame.setVisible(false);

		// Register ESC key to close
		frame.getRootPane()
			.registerKeyboardAction(e -> frame.setVisible(false), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
					JComponent.WHEN_IN_FOCUSED_WINDOW);

		// Register CLOSE (x) window to exit
		frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				log.debug("Window close clicked. Exiting");
				System.exit(0);
			}
		});

	}

	public void show() {
		EventQueue.invokeLater(() -> {
			loadData();
			frame.pack();
			frame.setResizable(false);
			placeNearTray(frame);
			frame.setVisible(true);
			frame.toFront();
		});
	}

	public boolean isVisible() {
		return frame.isVisible();
	}

	public void hide() {
		EventQueue.invokeLater(() -> frame.setVisible(false));
	}

	/* private methods */

	private JPanel buildHeaderPanel() {
		JPanel headerPanel = new JPanel();
		headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
		headerPanel.setBorder(new EmptyBorder(5, 5, 5, 5)); // margen abajo

		// Line 1: Title
		JPanel versionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JLabel versionStr = new JLabel(REMOTE_PC_CONTROL + " - Version:", SwingConstants.LEADING);
		JLabel versionVal = new JLabel(ResourcesHelper.getVersion(), SwingConstants.LEADING);
		versionVal.setFont(versionVal.getFont().deriveFont(Font.BOLD));
		versionPanel.add(versionStr);
		versionPanel.add(versionVal);

		// Line 2: Description
		JPanel descPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JLabel descLabel = new JLabel("Detected local IP and MAC addresses", SwingConstants.LEADING);
		descPanel.add(descLabel);

		// Add to panel
		headerPanel.add(versionPanel);
		headerPanel.add(descPanel);
		return headerPanel;
	}

	private JPanel buildBottomBar(JTable table) {
		JPanel buttonBar = new JPanel(new BorderLayout());

		// Left panel with "exit" button
		JPanel leftPanel = new JPanel();

		// do not show exit button with Partial Support
		// if (getTraySupport().equals(FULL) || getTraySupport().equals(NONE)) {
		// Windows OK, Cinnamon OK, Mate OK, XFCE OK, KDE OK, LXQt OK, Gnome OK
		if (!isPartialTraySupport()) {
			JButton exitBtn = new JButton("Exit Program");
			exitBtn.addActionListener(e -> System.exit(0));
			leftPanel.add(exitBtn);
		}

		// Right panel with "copy" and "close" buttons
		JPanel rightPanel = buildBottomRightPanel(table);

		// add left and right panel to button bar
		buttonBar.add(leftPanel, BorderLayout.WEST);
		buttonBar.add(rightPanel, BorderLayout.EAST);
		return buttonBar;
	}

	private @NonNull JPanel buildBottomRightPanel(JTable table) {
		JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		if (!isPartialTraySupport()) {
			JButton copyBtn = new JButton("Copy All");
			copyBtn.addActionListener(e -> {
				int row = table.getSelectedRow();
				if (row >= 0) {
					copyRow(row, table);
				} else {
					copyAll();
				}
			});
			rightPanel.add(copyBtn);
		}

		// Close button only available with full Tray support
		// Windows OK, Cinnamon OK, Mate OK, XFCE OK, KDE OK, LXQt OK, Gnome OK
		if (isFullTraySupport()) {
			JButton closeBtn = new JButton("Close this window");
			closeBtn.addActionListener(e -> frame.setVisible(false));
			rightPanel.add(closeBtn);
		}
		return rightPanel;
	}

	private void placeNearTray(JFrame frame) {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice gd = ge.getDefaultScreenDevice();
		Rectangle screen = gd.getDefaultConfiguration().getBounds();

		int margin = 30;
		int x = screen.x + screen.width - frame.getWidth() - margin;
		int y = screen.y + screen.height - frame.getHeight() - margin;

		frame.setLocation(x, y);
	}

	// Load IPs and MACs to table
	private void loadData() {
		model.setRowCount(0);
		networkChangeListener.getIpMacMap().forEach((ip, mac) -> model.addRow(new Object[] { ip, mac }));
	}

	// Copy all to clipboard
	private void copyAll() {
		String toCopy = networkChangeListener.getIpMacMap()
			.entrySet()
			.stream()
			.map(e -> e.getKey() + " -> " + e.getValue())
			.collect(Collectors.joining("\n"));

		StringSelection selection = new StringSelection(toCopy);
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
		log.debug("Copied all to clipboard:\n{}", toCopy);
	}

	private void copyRow(int row, JTable table) {
		StringBuilder sb = new StringBuilder();
		for (int col = 0; col < table.getColumnCount(); col++) {
			sb.append(table.getValueAt(row, col)).append('\t');
		}
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(sb.toString().trim()), null);
		log.debug("Copied selected row to clipboard:\n{}", sb);
	}

}
