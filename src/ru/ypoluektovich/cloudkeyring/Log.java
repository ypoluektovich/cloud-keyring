package ru.ypoluektovich.cloudkeyring;

import javax.swing.*;

/**
 * @author Yanus Poluektovich (ypoluektovich@gmail.com)
 */
class Log {

	private DefaultListModel<String> myLogListModel;

	synchronized void setLogListModel(final DefaultListModel<String> model) {
		myLogListModel = model;
	}
	
	synchronized void info(final String format, final Object... args) {
		final String message = String.format(format, args);

		if (myLogListModel != null) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					myLogListModel.addElement(message);
				}
			});
		}
	}
}
