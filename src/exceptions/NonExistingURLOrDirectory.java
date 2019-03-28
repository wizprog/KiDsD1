package exceptions;

public class NonExistingURLOrDirectory extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public NonExistingURLOrDirectory(String errorMessage) {
        super(errorMessage);
    }
}
