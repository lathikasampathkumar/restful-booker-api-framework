// Declarative Jenkins pipeline for the API test suite.
// Build parameters let you pick the suite and the target environment without editing code.
pipeline {
    agent any

    tools {
        maven 'Maven3'      // names must match Manage Jenkins -> Tools
        jdk   'JDK17'
    }

    parameters {
        choice(name: 'SUITE', choices: ['testng.xml', 'testng-smoke.xml', 'testng-full.xml'], description: 'Which TestNG suite to run')
        string(name: 'BASE_URI', defaultValue: 'https://restful-booker.herokuapp.com', description: 'API under test')
    }

    stages {
        stage('Checkout') {
            steps { checkout scm }
        }
        stage('Run API tests') {
            steps {
                bat "mvn clean test -Dsuite=${params.SUITE} -Dbase.uri=${params.BASE_URI}"
                // on a Linux agent use: sh "mvn clean test -Dsuite=${params.SUITE} -Dbase.uri=${params.BASE_URI}"
            }
        }
    }

    post {
        always {
            junit 'target/surefire-reports/*.xml'
            archiveArtifacts artifacts: 'reports/**, logs/**', allowEmptyArchive: true
        }
    }
}
