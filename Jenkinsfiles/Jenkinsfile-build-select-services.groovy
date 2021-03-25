node('master') {
   jdk = tool name: 'JDK8'
   env.JAVA_HOME = "${jdk}"
   env.PATH="${env.JAVA_HOME}/bin:${env.PATH}"
   
   def services = [ "${service1}", "${service2}"]
   stringNum = "${number_of_services}"
   int intNum = stringNum as int  
   
   stage('Checkout Git Code to Jenkins on OpenShift') { // for display purposes
      // Get some code from a GitHub repository
      git credentialsId: 'git', url: '${git_url}'
   }

   stage('Build zOS Connect Services') {

        for (int i = 0; i < intNum; i++) {
           println "Building service "+services[i]
           sh "${WORKSPACE}/zconbt/bin/zconbt -pd=./services/"+services[i]+" -f=./"+services[i]+".sar" 
        }
        println "Exiting Stage 2, entering Stage 3!"
   }

   
    stage("Publish Artifacts to Artifactory") {
       // Obtain an Artifactory server instance, defined in Jenkins --> Manage:
       def server = Artifactory.server "artifactory"

       // Read the upload spec which was downloaded from github.
       for (int i = 0; i < intNum; i++) {
          def sarFileName = services[i] 
          println "Uploading "+sarFileName+" to Artifactory server "+server
          def uploadSpec = """{
            "files": [
               {
                  "pattern": "${sarFileName}.sar",
                  "target": "${artifactory_repo_name}/services/"
               }
               ]
            }"""
          // Upload to Artifactory.
          def buildInfo = server.upload spec: uploadSpec

          // Publish the build to Artifactory
          server.publishBuildInfo buildInfo
          println "Uploaded "+sarFileName+" to Artifactory server "+server
        }    
       
       
       //sh "rm response.json"
       //sh "rm responseStop.json"
       for (int i = 0; i < intNum; i++) {
           def sarFileName = services[i]
           sh "rm "+sarFileName+".sar"
        }      
    }
}
