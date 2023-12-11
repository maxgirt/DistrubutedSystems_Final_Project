package service.core;




/**
 * Class to store the Submission return by graderService
 * 
 *
 */
public class Submission {
	
	
	public static int counterSub = 0;
	public Submission() {}
	public Submission(int idProblem, String code,ProgLanguage progLanguage,Result result) {
		this.idSubmission = counterSub++;
		this.code = code;
		this.progLanguage = progLanguage;
		this.result = Result.wait;
	}
	
	public int idSubmission;
	public int idProblem;
	//public int idUser;
	public String code;
	public ProgLanguage progLanguage;
	public Result result;
}
