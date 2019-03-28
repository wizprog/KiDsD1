package exceptions;

public class CantOpenURL extends Exception{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public CantOpenURL(String errorMessage) {
        super(errorMessage);
    }

}
