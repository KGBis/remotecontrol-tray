package com.kikegg.remote.shutdown;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.List;

import static com.kikegg.remote.shutdown.Main.REMOTE_SHUTDOWN;

@Slf4j
public class ShowIpFrame implements FocusListener {

	public static final String INFO_TEXT = "<html><span style=\"text-align: center;\">"
			+ "By Enrique García (c) 2022  (kike.g.garcia at gmail.com)<br>"
			+ "Based on the 'RemoteShutdownPCServer' by Isah Rikovic (https://github.com/rikovicisah) (rikovicisah at gmail.com)"
			+ "</span><p/><p/><span><b>Detected non-loopback IPs:</b><p>{IP_TEXT}<br><i>(IPs copied to clipboard)</i></span></html>";

	private final JFrame frame;

	private final JLabel label = new JLabel();

	private PopupMenu parentPopup;

	ShowIpFrame() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException ignored) {
			// Ignored
		}

		frame = new JFrame(REMOTE_SHUTDOWN);
		frame.setIconImage(IconImage.getIcon());
		frame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		frame.setLayout(new BorderLayout(5, 5));
		frame.setLocationRelativeTo(null);
		frame.setVisible(false);
		frame.addFocusListener(this);
	}

	public void show(List<String> ipList, PopupMenu parent) {
		this.parentPopup = parent;
		String ips = formatIpListToShow(ipList);

		String text = StringUtils.replace(INFO_TEXT, "{IP_TEXT}", ips);

		EventQueue.invokeLater(() -> {
			// remove old label, IP should not change but just in case...
			frame.remove(label);

			// set new label text and border
			label.setText(text);
			label.setBorder(new EmptyBorder(10, 10, 10, 10));

			// add the label, pack and make visible
			frame.add(label);
			frame.pack();
			frame.setVisible(true);
		});

		copyToClipboard(ipList.toString());
	}

	private String formatIpListToShow(List<String> ipList) {
		return StringUtils.join(ipList, "<br>");
	}

	private void copyToClipboard(String ip) {
		StringSelection stringSelection = new StringSelection(ip);
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(stringSelection, null);
		log.info("IP(s) {} copied to the clipboard.", ip);
	}

	@Override
	public void focusGained(FocusEvent e) {
		// no action on focus
	}

	@Override
	public void focusLost(FocusEvent e) {
		this.parentPopup.setEnabled(true);
	}

}