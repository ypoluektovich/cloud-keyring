package ru.ypoluektovich.cloudkeyring;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * @author Yanus Poluektovich (ypoluektovich@gmail.com)
 */
public class Main implements Runnable {

	public static void main(final String[] args) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, UnsupportedEncodingException {
		final Log log = new Log();
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					new TehFrame(log).setVisible(true);
				}
			});
		} catch (InterruptedException | InvocationTargetException e) {
			e.printStackTrace();
			return;
		}
		new Main(log).run();
	}

	private final Log myLog;
	
	private final Cipher myDecryptCipher;

	private Main(final Log log) throws UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
		myLog = log;

		final SecretKeySpec keySpec = new SecretKeySpec("cloud-keyring".getBytes("UTF-8"), "Blowfish");
		myDecryptCipher = Cipher.getInstance("Blowfish");
		myDecryptCipher.init(Cipher.DECRYPT_MODE, keySpec);

		log.info("Initialized cipher");
	}

	@Override
	public void run() {
		final Path workingDir = Paths.get(".").toAbsolutePath().normalize();
		myLog.info("Listening to directory: " + workingDir);
		try (final WatchService watcher = workingDir.getFileSystem().newWatchService()) {
			workingDir.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY);

			myLog.info("Starting watch loop");
			while (true) {
				final WatchKey key;
				try {
					key = watcher.take();
				} catch (InterruptedException e) {
					break;
				}

				if (!key.isValid()) {
					break;
				}

				boolean processedModify = false;
				for (final WatchEvent<?> event : key.pollEvents()) {
					final WatchEvent.Kind<?> kind = event.kind();
					if (kind == StandardWatchEventKinds.OVERFLOW) {
						continue;
					}

					if (processedModify) {
						break;
					}
					processedModify = true;

					final Object contextObject = event.context();
					if (!(contextObject instanceof Path)) {
						continue;
					}
					final Path filename = workingDir.resolve((Path) contextObject);

					myLog.info("Modified: %s", filename);

					final byte[] bytes = Files.readAllBytes(filename);

					try {
						myLog.info("New contents: %s", new String(myDecryptCipher.doFinal(bytes), "UTF-8"));
					} catch (IllegalBlockSizeException | BadPaddingException e) {
						e.printStackTrace();
					}
				}

				if (!key.reset()) {
					break;
				}
			}
		} catch (IOException e) {
			myLog.info("Error! %s" + e.getMessage());
			e.printStackTrace();
		}
	}
}
