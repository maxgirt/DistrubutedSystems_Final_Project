package service.core;
import java.util.ArrayList;
/**
 * Data Class that contains Problem Information
 * 
 * 
 *
 */
public class Problem {
	public Problem(String title, String description, ArrayList<TestCase> testCase) {
		this.title = title;
		this.description = description;
		this.testCases = testCase;
	}
	
	public Problem() {}
	/**
	 * Public fields are used as modern best practice argues that use of set/get
	 * methods is unnecessary as (1) set/get makes the field mutable anyway, and
	 * (2) set/get introduces additional method calls, which reduces performance.
	 */
	public int id;
	public String title;
	public String description;
	public ArrayList<TestCase> testCases;
}
