package service.core;

/**
 * Class to store a test case in a submission
 * 
 *
 */
public class TestCase {
	public TestCase(String input, String output) {
		this.input = input;
        this.output = output;
	}
    public TestCase() {}
	public String input;
    public String output;
}
