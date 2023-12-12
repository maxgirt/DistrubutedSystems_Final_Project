package service.core;

/**
 * Class to store the Submission return by graderService
 * 
 *
 */
public class Submission {	
	public Submission(int idProblem, String code,ProgLanguage progLanguage,Result result) {
		this.idProblem = idProblem;
		this.code = code;
		this.progLanguage = progLanguage;
	}
	public Submission(SubmissionRequest submissionRequest,int idSubmission, Result result) {
		this.idProblem = submissionRequest.idProblem;
		this.code = submissionRequest.code;
		this.progLanguage = submissionRequest.progLanguage;
	}
	public Submission() {}
	
	public int id;
	public int idProblem;
	//public int idUser;
	public String code;
	public ProgLanguage progLanguage;
	public Result result;
}
