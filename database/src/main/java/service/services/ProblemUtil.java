package service.services;

import java.util.ArrayList;

import service.core.Problem;
import service.database_entities.ProblemEntity;

public class ProblemUtil {
        public static Problem toProblem(ProblemEntity entity) {
        return new Problem(entity.getTitle(), entity.getDescription(), new ArrayList<>(entity.getTestCases()));
    }

    public static ProblemEntity toProblemEntity(Problem problem) {
        return new ProblemEntity(problem.getId(), problem.getTitle(), problem.getDescription(), problem.getTestCases());
    }    
}
