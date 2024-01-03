package service.core;

/**
 * Class to store a test case in a submission
 * 
 *
 */
public class TestCase {
	public TestCase(String input, String output, Integer timeout, Integer problemId) {
		this.problemId = problemId;
		this.input = input;
        this.output = output;
		this.timeout = timeout;
		this.hidden = false;

	}
    public TestCase() {}
	public String input;
    public String output;
	public Integer timeout;
	public Integer problemId;
	public Boolean hidden;
}
