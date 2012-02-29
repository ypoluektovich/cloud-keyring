package ru.ypoluektovich.cloudkeyring;

import javax.swing.*;
import java.awt.*;

/**
 * @author Yanus Poluektovich (ypoluektovich@gmail.com)
 */
public class TehFrame extends JFrame {

	private final DefaultListModel<String> logListModel = new DefaultListModel<>();
	private JList<String> logList = new JList<>(logListModel);

	{
		final Dimension size = new Dimension(400, 100);
		logList.setMinimumSize(size);
		logList.setPreferredSize(size);
	}

	public TehFrame() {
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		final Container contentPane = getContentPane();
		contentPane.setLayout(new GridBagLayout());

		addLogList(contentPane);

		pack();

		logListModel.addElement("Created UI");
	}

	private void addLogList(final Container contentPane) {
		final GridBagConstraints gbc = new GridBagConstraints(
				0, 0, // grid position
				1, 1, // grid sizes
				1.0, 1.0, // weights
				GridBagConstraints.CENTER, // anchor
				GridBagConstraints.BOTH, // fill
				new Insets(12, 12, 12, 12),
				0, 0 // internal padding
		);
		contentPane.add(logList, gbc);
	}
}
