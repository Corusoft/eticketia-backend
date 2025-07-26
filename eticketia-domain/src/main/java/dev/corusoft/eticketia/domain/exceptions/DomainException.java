package dev.corusoft.eticketia.domain.exceptions;


import lombok.Getter;

@Getter
/**
 * Customized exception that can be thrown during the execution of the application.
 */
public class DomainException extends Exception {

  /**
   * Key to translate the exception message.
   */
  protected final String i18nKey;
  /**
   * Arguments to replace in the exception message placeholder.
   */
  protected final transient Object[] i18nArgs;

  /**
   * Creates a new customized exception with the key to obtain the translation. <br>
   *
   * @param i18nKey Key to translate the exception message
   */
  public DomainException(String i18nKey) {
    this(i18nKey != null ? i18nKey : DomainException.class.getName(), null);
  }

  /**
   * Creates a new customized exception.<br> In case the template key {@code i18nKey} contains any
   * placeholder, they must be included in the {@code i18nArgs}.
   *
   * @param i18nKey  Key to translate the exception message
   * @param i18nArgs Arguments to replace in the exception message placeholder
   */
  public DomainException(String i18nKey, Object[] i18nArgs) {
    this(i18nKey, i18nArgs, null, null);
  }

  /**
   * Creates a new customized exception, including the exception {@code cause} that caused this
   * domain exception. Also includes a short message {@code message} explaining the reason.<br> In
   * case the template key {@code i18nKey} contains any placeholder, they must be included in the
   * {@code i18nArgs}.
   *
   * @param i18nKey  Key to translate the exception message
   * @param i18nArgs Arguments to replace in the exception message placeholder
   * @param message  Reason of the exception
   * @param cause    Original exception
   */
  public DomainException(String i18nKey, Object[] i18nArgs, String message, Throwable cause) {
    super(message, cause);
    this.i18nKey = i18nKey;
    this.i18nArgs = i18nArgs;
  }

  /**
   * Creates a new customized exception, including the exception {@code cause} that caused this
   * domain exception. Also includes a short message {@code message} explaining the reason.<br> In
   * this case, the template key {@code i18nKey} must not contain any placeholder.
   *
   * @param i18nKey Key to translate the exception message
   * @param message Reason of the exception
   * @param cause   Original exception
   */
  public DomainException(String i18nKey, String message, Throwable cause) {
    this(i18nKey, null, message, cause);
  }

  /**
   * Creates a new customized exception, including the original exception that caused this domain
   * exception.<br> In case the template key {@code i18nKey} contains any placeholder, they must be
   * included in the {@code i18nArgs}.
   *
   * @param i18nKey  Key to translate the exception message
   * @param i18nArgs Arguments to replace in the exception message placeholder
   * @param cause    Original exception
   */
  public DomainException(String i18nKey, Object[] i18nArgs, Throwable cause) {
    this(i18nKey, i18nArgs, null, cause);
  }

  /**
   * Creates a new customized exception, including the original exception that caused this domain
   * exception.<br> In case the template key {@code i18nKey} contains any placeholder, they must be
   * included in the {@code i18nArgs}.
   *
   * @param i18nKey  Key to translate the exception message
   * @param i18nArgs Arguments to replace in the exception message placeholder
   * @param message  Reason of the exception
   */
  public DomainException(String i18nKey, Object[] i18nArgs, String message) {
    this(i18nKey, i18nArgs, message, null);
  }


}
