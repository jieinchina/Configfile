package org.assist.load;

import java.io.IOException;

import javax.management.JMRuntimeException;

public class ConfigException extends JMRuntimeException {

	private static final long serialVersionUID = 2831317610367464148L;

	private java.lang.Error error ;

    /**
     * Default constructor.
     *
     * @param e the wrapped error.
     */
    public ConfigException(java.lang.Error e) {
      super();
      error = e ;
    }

    /**
     * Constructor that allows a specific error message to be specified.
     *
     * @param e the wrapped error.
     * @param message the detail message.
     */
    public ConfigException(java.lang.Error e, String message) {
       super(message);
       error = e ;
    }

    /**
     * Returns the actual {@link Error} thrown.
     *
     * @return the wrapped {@link Error}.
     */
    public java.lang.Error getTargetError()  {
        return error ;
    }

    /**
     * Returns the actual {@link Error} thrown.
     *
     * @return the wrapped {@link Error}.
     */
    public Throwable getCause() {
        return error;
    }
    

	public ConfigException(String message) {
		super(message);
		this.error = new Error(message);
	}

	public ConfigException(IOException e) {
		super(e.getMessage());
		this.error = new Error(e.getCause());
	}

}
