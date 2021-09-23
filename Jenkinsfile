pipeline {

 environment {
     dockerImage = ""
     image_name=""
     name="ta4j"
     portno="8080"
     targetport="10100"
       MY_CREDS = credentials('googlepass')
       MY_NORMAL_CREDS = credentials('normal')

  }
    agent any

    tools {
        maven 'maven'
    }


    stages {
        stage('Compile Stage') {
            steps {
                sh 'echo hello'
                sh 'pwd'
                 sh 'mkdir -p project2'
             dir('project2'){
                git (branch: 'history',url: 'https://github.com/rowanfoo/basej.git')
sh 'pwd'
sh 'ls'
              sh 'mvn -version'
              sh 'mvn compile'
              sh 'mvn install -DskipTests'

             }



            }
        }

        stage('Compile BASE') {
            steps {
             dir('project3'){
                git (branch: 'history',url: 'https://github.com/rowanfoo/base.git')
              sh 'mvn -version'
              sh 'mvn compile'
              sh 'mvn install -DskipTests'

             }
            }
        }



        stage('BUILD  TAG APP') {
            steps {
              sh 'mvn -version'
               sh 'mvn clean'
              sh 'mvn compile'
              sh 'mvn package -DskipTests'
            }
        }

        stage('DOCKER TIME'){
            steps{
                script {
                    image_name = "localhost:5000/rowanf/taj"
                    dockerImage =  docker.build image_name
                    sh 'pwd'
                }
            }
         }

           stage('Deploy') {
                steps {
                   echo "ALL IS DONE"
                     script {
                        sh 'docker rm -f ta4j'
                        sh """docker run -d  --restart=unless-stopped --name ta4j  -p 10100:8080 -e SPRING_DATASOURCE_URL=${env.dburl}   -e SPRING_DATASOURCE_USERNAME=postgres   -e SPRING_DATASOURCE_PASSWORD=${MY_CREDS_PSW} -e SPRING_MAIL_USERNAME=${env.gmail}  -e SPRING_MAIL_PASSWORD=${MY_CREDS_PSW}  localhost:5000/rowanf/taj"""

                    }
                }
           }
        }



}
