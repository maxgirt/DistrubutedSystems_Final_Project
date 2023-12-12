package service.core;

/**
 * Class to store the Submission return by graderService
 * 
 *
 */
public class SubmissionRequest {
	
	public SubmissionRequest(int idProblem, String code,ProgLanguage progLanguage,Result result) {
		this.idProblem = idProblem;
		this.code = code;
		this.progLanguage = progLanguage;
	}
	public SubmissionRequest() {}
	
	public int idProblem;
	//public int idUser;
	public String code;
	public ProgLanguage progLanguage;
}
