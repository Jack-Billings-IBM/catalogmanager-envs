node('master') {
   jdk = tool name: 'JDK8'
   env.JAVA_HOME = "${jdk}"
   env.PATH="${env.JAVA_HOME}/bin:${env.PATH}"
   
   def services = []
   server = "${artifactory_server}"
   stringNum = "${number_of_services}"
   int intNum = stringNum as int  
   
   stage('Checkout Git Code to Jenkins on OpenShift') { // for display purposes
      // Get some code from a GitHub repository
      // need to define git credentials
      git credentialsId: 'git', url: '${git_url}'
   }


   stage('Build zOS Connect Services') {
        println "Calling zconbt"
        def output = sh (returnStdout: true, script: 'pwd')
        println output
        
        dir("services") {
           sh "ls"
           sh "ls | grep -vx 'services' > services"
           sh "cat services"
           def data = readFile(file: 'services')
           def lines = data.readLines()
           for (line in lines) {
              services.add(line)
           }
           println "${services}"
      
           for (int i = 0; i < intNum; i++) {
              println "Building service "+services[i]
              sh "${WORKSPACE}/zconbt/bin/zconbt -pd=./"+services[i]+" -f=./"+services[i]+".sar" 
           }
           println "Exiting Stage 2, entering Stage 3!"
        }
   }

   
    stage("Publish Artifacts to Artifactory") {
       // Obtain an Artifactory server instance, defined in Jenkins --> Manage:
       // need to define artifactory server
       def server = Artifactory.server "artifactory"

       // Read the upload spec which was downloaded from github.
       for (int i = 0; i < intNum; i++) {
          def sarFileName = services[i] 
          def uploadSpecTest = """{
            "files": [
               {
                  "pattern": "${sarFileName}.sar",
                  "target": "${artifactory_repo_name}/services/"
               }
               ]
            }"""
                  // Upload to Artifactory.
            def buildInfo = server.upload spec: uploadSpecTest

            // Publish the build to Artifactory
            server.publishBuildInfo buildInfo
        }         
    }
   
}
