node('master') {
   jdk = tool name: 'JDK8'
   env.JAVA_HOME = "${jdk}"
   env.PATH="${env.JAVA_HOME}/bin:${env.PATH}"
   
   def apis = [ "${api1}"]
   stringNum = "${number_of_apis}"
   int intNum = stringNum as int  
   
   stage('Checkout Git Code to Jenkins on OpenShift') { // for display purposes
      // Get some code from a GitHub repository
      // need to define git credentials
      git credentialsId: 'git', url: '${git_url}'
   }


   stage('Build zOS Connect APIs') {
           println "The number of APIs to be built: " + intNum
           println "Building these APIs: "+apis
           
           //create sar file for each api
           for (int i = 0; i < intNum; i++) {
              println "Building API "+apis[i]
              sh "${WORKSPACE}/zconbt/bin/zconbt -pd=./apis/"+apis[i]+" -f=./"+apis[i]+".aar" 
           }
           println "aar files that have been built: "
           println "${apis}"
           sh "rm *.aar"
           println "Built all the APIs, publishing to artifact repository next"
   }

   
    stage("Publish Artifacts to Artifactory") {
       // Obtain an Artifactory server instance, defined in Jenkins --> Manage:
       // need to define artifactory server in Jenkins
       def artifactory_server = Artifactory.server "artifactory"
       println "Publishing to server: "+artifactory_server
       
       println "The number of APIs to publish: " + intNum
       
       // loops thorugh each aar created and publishes it to your artifactory server
       for (int i = 0; i < intNum; i++) {
          def aarFileName = apis[i] 
          println "Publishing aar file: "+aarFileName
          def uploadSpec = """{
            "files": [
               {
                  "pattern": "${aarFileName}.aar",
                  "target": "${artifactory_repo_name}/apis/"
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
