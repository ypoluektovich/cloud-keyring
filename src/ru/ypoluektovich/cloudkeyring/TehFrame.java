package ru.ypoluektovich.cloudkeyring;

import javax.swing.*;
import java.awt.*;

/**
 * @author Yanus Poluektovich (ypoluektovich@gmail.com)
 */
public class TehFrame extends JFrame {

	public TehFrame(final Log log) {
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		final Container contentPane = getContentPane();
		contentPane.setLayout(new GridBagLayout());

		addModifierButton(contentPane, log);
		addLogList(contentPane, log);

		pack();

		log.info("Created UI");
	}

	private void addModifierButton(final Container contentPane, final Log log) {
		final JButton button = new JButton("Modify file");
		button.addActionListener(new FileModifierButtonListener(log));

		final GridBagConstraints gbc = new GridBagConstraints(
				0, 0, // grid position
				1, 1, // grid sizes
				0.0, 0.0, // weights
				GridBagConstraints.CENTER, // anchor
				GridBagConstraints.NONE, // fill
				new Insets(12, 12, 3, 12),
				0, 0 // internal padding
		);
		contentPane.add(button, gbc);
	}

	private void addLogList(final Container contentPane, final Log log) {
		final DefaultListModel<String> model = new DefaultListModel<>();
		log.setLogListModel(model);

		final JList<String> list = new JList<>(model);

		final Dimension size = new Dimension(400, 100);
		list.setMinimumSize(size);
		list.setPreferredSize(size);

		final GridBagConstraints gbc = new GridBagConstraints(
				0, 1, // grid position
				1, 1, // grid sizes
				1.0, 1.0, // weights
				GridBagConstraints.CENTER, // anchor
				GridBagConstraints.BOTH, // fill
				new Insets(3, 12, 12, 12),
				0, 0 // internal padding
		);
		contentPane.add(list, gbc);
	}
}
