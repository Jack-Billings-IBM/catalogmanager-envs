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
        //cd into the services folder
        dir("services") {
           //read contents of services folder into services file
           sh "ls | grep -vx 'services' > services"
           
           //creates a file named data from services file, reads each line of data and appends each line (service) to list services
           def data = readFile(file: 'services')
           def lines = data.readLines()
           for (line in lines) {
              services.add(line)
           }

           //determine how many services
           int intNum = services.size()
           println "The number of services to be built: " + intNum
           println "Building these services: "+services
           
           //create sar file for each service
           for (int i = 0; i < intNum; i++) {
              println "Building service "+services[i]
              sh "${WORKSPACE}/zconbt/bin/zconbt -pd=./"+services[i]+" -f=./"+services[i]+".sar" 
           }
           println "sar files that have been built: "
           println "${services}"
           sh "rm *.sar"
           println "Exiting Stage 2, entering Stage 3!"
        }
   }

   
    stage("Publish Artifacts to Artifactory") {
       // Obtain an Artifactory server instance, defined in Jenkins --> Manage:
       // need to define artifactory server
       def artifactory_server = Artifactory.server "artifactory"
       println "Publishing to server: "+artifactory_server
       
       int intNum = services.size()
       println "The number of services to publish: " + intNum
       
       // loops thorugh each sar created and publishes it to your artifactory server
       for (int i = 0; i < intNum; i++) {
          def sarFileName = services[i] 
          println "Publishing sar file: "+sarFileName
          def uploadSpec = """{
            "files": [
               {
                  "pattern": "${sarFileName}.sar",
                  "target": "${artifactory_repo_name}/services/"
               }
               ]
            }"""
            println "Uploading"
            // Upload to Artifactory.
            def buildInfo = artifactory_server.upload spec: uploadSpec

            // Publish the build to Artifactory
            artifactory_server.publishBuildInfo buildInfo
          
            println "Upload successful"
        }         
    }
   
}
