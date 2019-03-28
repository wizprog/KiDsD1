package exceptions;

public class CorpusNotComplete extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public CorpusNotComplete(String errorMessage) {
        super(errorMessage);
    }

}
