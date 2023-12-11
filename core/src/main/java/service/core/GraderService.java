package service.core;

import java.util.ArrayList;

public interface GraderService {
	
	public Result generateResult(String code,ArrayList<TestCase> testCases);
}
