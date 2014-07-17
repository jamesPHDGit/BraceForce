package BraceForce.SensorLink;

public class SensorNotStartException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2420170487471243814L;

	  /**
	   * Default constructor
	   */
	  public SensorNotStartException() {
	    super();
	  }
	  
	  /**
	   * Construct exception with the error message
	   * 
	   * @param message
	   *    exception message
	   */
	  public SensorNotStartException(String message) {
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
	  public SensorNotStartException(String message, Throwable cause) {
	    super(message, cause);
	  }

	  /**
	   * Construction exception with throwable cause
	   * 
	   * @param cause
	   *    throwable cause
	   */
	  public SensorNotStartException(Throwable cause) {
	    super(cause);
	  }

	
}
