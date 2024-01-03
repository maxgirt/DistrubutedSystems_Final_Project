Read me for our project! 




# Setting up MongoDB #

## Installing on Mac ##

- brew tap mongodb/brew 
- brew install mongodb-community
  - this installed mongodb-community 7.0.2

## Create a data directory ##
  - this is crucial for MongoDB to persist data across restarts
  - when you start mongodb it will look for a /data/db directory
- sudo mkdir -p /System/Volumes/Data/data/db
- sudo chown -R $(whoami) /System/Volumes/Data/data/db

## Start MongoDB ##
- brew services start mongodb-community

## Verify that MongoDB has started successfully ##
- brew services list 
  - should see mongodb-community listed with status started

## Connect and use MongoDB ##
- mongosh
  - this will connect to the local MongoDB instance
  - if you cannot enter command, please try updating your path variable in your bash profile
    - echo 'export PATH="/usr/local/opt/mongodb-community/bin:$PATH"' >> ~/.zshrc
    - source ~/.zshrc

## Stop MongoDB ##
- brew services stop mongodb-community


## Database Setup ##
- mvn clean install -pl core
- it's important to run this command before compiling any other service to ensure that the core module is built first
- mvn clean install -pl database 
- mvn -pl database spring-boot:run


## Additional Info ##
The controllers have been set up to handle all the requests to interact with the database that are needed for the project
The implementation of actual code that interacts with the database is in the service implementations
The service implementations interact with the correct repository which acts as the data access layer and provides us with default CRUD operations as well as any custom queries we may need
The repositories are interfaces that extend the MongoRepository interface which provides us with the default CRUD operations
The entities are the schema of the database 

## example curl commands ##
We can use these to test the endpoints and make sure they are working properly in the terminal 
check the controllers in the database for the correct endpoints
the entities will have the correct json format for the body of the request

curl -X POST http://localhost:8083/problems \
-H "Content-Type: application/json" \
-d '{
    "title": "Sample Problem",
    "description": "This is a sample problem description.",
    "testCases": [{"input": "test input", "output": "test output"}]
}'

curl -X GET http://localhost:8083/problems