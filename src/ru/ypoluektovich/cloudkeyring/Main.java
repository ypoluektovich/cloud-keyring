package ru.ypoluektovich.cloudkeyring;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * @author Yanus Poluektovich (ypoluektovich@gmail.com)
 */
public class Main implements Runnable {
	
	private final Cipher decryptCipher;
	private final Cipher encryptCipher;

	public static void main(final String[] args) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, UnsupportedEncodingException {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new TehFrame().setVisible(true);
			}
		});
		new Main().run();
	}

	private Main() throws UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
		final SecretKeySpec keySpec = new SecretKeySpec("cloud-keyring".getBytes("UTF-8"), "Blowfish");
		decryptCipher = Cipher.getInstance("Blowfish");
		decryptCipher.init(Cipher.DECRYPT_MODE, keySpec);
		encryptCipher = Cipher.getInstance("Blowfish");
		encryptCipher.init(Cipher.ENCRYPT_MODE, keySpec);
	}

	@Override
	public void run() {
		final Path workingDir = Paths.get(".").toAbsolutePath().normalize();
		try (final WatchService watcher = workingDir.getFileSystem().newWatchService()) {
			workingDir.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY);

			try {
				Files.write(workingDir.resolve("test.ck"), encryptCipher.doFinal("Nyaa!".getBytes("UTF-8")));
			} catch (IllegalBlockSizeException | BadPaddingException e) {
				e.printStackTrace();
				return;
			}

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

				for (final WatchEvent<?> event : key.pollEvents()) {
					final WatchEvent.Kind<?> kind = event.kind();
					if (kind == StandardWatchEventKinds.OVERFLOW) {
						continue;
					}
					final Path filename = workingDir.resolve(((WatchEvent<Path>) event).context());

					System.out.println("Modified: " + filename);

					final byte[] bytes = Files.readAllBytes(filename);

					try {
						System.out.println(new String(decryptCipher.doFinal(bytes), "UTF-8"));
					} catch (IllegalBlockSizeException | BadPaddingException e) {
						e.printStackTrace();
					}
				}

				if (!key.reset()) {
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
