#!/bin/bash

submit_problem() {
    curl -X POST http://localhost:8080/submission \
    -H "Content-Type: application/json" \
    -d '{
      "idProblem": "6595a58605b8fe57b697700e",
      "code": "print(\"Hello World\")",
      "progLanguage": 0
    }'
}

main() {
    # Call the submit_problem function within the main function
    submit_problem
}

# Call the main function
main
