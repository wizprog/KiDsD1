package exceptions;

public class CorpusDoesNotExist extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public CorpusDoesNotExist(String errorMessage) {
        super(errorMessage);
    }

}
