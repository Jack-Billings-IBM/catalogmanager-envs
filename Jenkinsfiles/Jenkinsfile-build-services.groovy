node('master') {
   jdk = tool name: 'JDK8'
   env.JAVA_HOME = "${jdk}"
   env.PATH="${env.JAVA_HOME}/bin:${env.PATH}"
   
   //array of all the services
   def services = []
   
   stage('Checkout Git Code to Jenkins on OpenShift') { // for display purposes
      // Get some code from a GitHub repository
      // need to define git credentials
      git credentialsId: 'git', url: '${git_url}'
   }


   stage('Build zOS Connect Services') {
        println "Calling zconbt"
        def output = sh (returnStdout: true, script: 'pwd')
        println output
        
        //cd into the services folder
        dir("services") {
           sh "ls"
           //read contents of services folder into services file
           sh "ls | grep -vx 'services' > services"
           //show contents of services file
           sh "cat services"
           
           //creates a file named data from services file, reads each line of data and appends each line (service) to list services
           def data = readFile(file: 'services')
           def lines = data.readLines()
           for (line in lines) {
              services.add(line)
           }
           //display all the services
           println "${services}"
           //determine how many services
           int intNum = services.size()
           println "The length of the array is: " + intNum
           
           //create sar file for each service
           for (int i = 0; i < intNum; i++) {
              println "Building service "+services[i]
              sh "${WORKSPACE}/zconbt/bin/zconbt -pd=./"+services[i]+" -f=./"+services[i]+".sar" 
           }
           sh "rm *.sar"
           println "Exiting Stage 2, entering Stage 3!"
        }
   }

   
    stage("Publish Artifacts to Artifactory") {
       // Obtain an Artifactory server instance, defined in Jenkins --> Manage:
       // need to define artifactory server
       def server = Artifactory.server "artifactory"
       
       int intNum = services.size()
       println "The length of the array is: " + intNum
       
       // loops thorugh each sar created and publishes it to your artifactory server
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
