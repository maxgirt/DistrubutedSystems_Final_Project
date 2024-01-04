package service.core;
import java.util.ArrayList;
import java.util.List;
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
	public String id;
	public String title;
	public String description;
	public List<TestCase> testCases;

	@Override
	public String toString() {
		return "Problem [id=" + id + ", title=" + title + ", description=" + description + ", testCases=" + testCases
				+ "]";
	}

	public String getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	public List<TestCase> getTestCases() {
		return testCases;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setTestCases(List<TestCase> testCases) {
		this.testCases = testCases;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	

}

