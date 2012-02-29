package ru.ypoluektovich.cloudkeyring;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * @author Yanus Poluektovich (ypoluektovich@gmail.com)
 */
public class FileModifierButtonListener implements ActionListener {

	private final Log myLog;
	
	private final Cipher myEncryptCipher;

	public FileModifierButtonListener(final Log log) throws ImpossibleException {
		myLog = log;

		final SecretKeySpec keySpec;
		try {
			keySpec = new SecretKeySpec("cloud-keyring".getBytes("UTF-8"), "Blowfish");
		} catch (UnsupportedEncodingException e) {
			throw new ImpossibleException("UTF-8 encoding is not supported", e);
		}
		try {
			myEncryptCipher = Cipher.getInstance("Blowfish");
			myEncryptCipher.init(Cipher.ENCRYPT_MODE, keySpec);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
			throw new ImpossibleException("Cryptographic exception in stub code", e);
		}
	}

	@Override
	public void actionPerformed(final ActionEvent event) {
		myLog.info("Modifying file...");
		try {
			Files.write(Paths.get("test.ck"), myEncryptCipher.doFinal("Nyaa!".getBytes("UTF-8")));
		} catch (IOException e) {
			myLog.info("IO error on file modification");
			e.printStackTrace();
		} catch (IllegalBlockSizeException | BadPaddingException e) {
			myLog.info("Cryptographic error on file modification");
			e.printStackTrace();
		}
	}
}
