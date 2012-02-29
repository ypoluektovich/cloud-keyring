package ru.ypoluektovich.cloudkeyring;

/**
 * @author Yanus Poluektovich (ypoluektovich@gmail.com)
 */
public class ImpossibleException extends RuntimeException {
	public ImpossibleException(final String message, final Throwable cause) {
		super(message, cause);
	}
}
