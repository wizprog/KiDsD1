package exceptions;

public class NonReadableDirectory extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public NonReadableDirectory(String errorMessage) {
        super(errorMessage);
    }

}
