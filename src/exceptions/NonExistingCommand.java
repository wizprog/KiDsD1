package exceptions;

public class NonExistingCommand extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NonExistingCommand(String errorMessage) {
        super(errorMessage);
    }
	
}
