package service.core;




/**
 * Class to store the Submission return by graderService
 * 
 *
 */
public class TestCase {
	public static int counterSub = 0;
	public TestCase() {}
	public TestCase(String input, String output) {
		this.input = input;
        this.output = output;
	}
	public String input;
    public String output;
}
