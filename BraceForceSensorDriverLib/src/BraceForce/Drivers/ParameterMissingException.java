package BraceForce.Drivers;

/**
 * Ported from ODK from University of Washington
 * @author wbrunette@gmail.com
 * @author rohitchaudhri@gmail.com
 * 
 */
public class ParameterMissingException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2420170487471243813L;

	  /**
	   * Default constructor
	   */
	  public ParameterMissingException() {
	    super();
	  }
	  
	  /**
	   * Construct exception with the error message
	   * 
	   * @param message
	   *    exception message
	   */
	  public ParameterMissingException(String message) {
	    super(message);
	  }

	  /**
	   * Construction exception with error message and throwable cause
	   * 
	   * @param message
	   *    exception message
	   * @param cause
	   *    throwable cause
	   */
	  public ParameterMissingException(String message, Throwable cause) {
	    super(message, cause);
	  }

	  /**
	   * Construction exception with throwable cause
	   * 
	   * @param cause
	   *    throwable cause
	   */
	  public ParameterMissingException(Throwable cause) {
	    super(cause);
	  }

	
}
