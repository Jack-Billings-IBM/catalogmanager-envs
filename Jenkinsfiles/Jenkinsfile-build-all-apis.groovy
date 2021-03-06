node('master') {
   jdk = tool name: 'JDK8'
   env.JAVA_HOME = "${jdk}"
   env.PATH="${env.JAVA_HOME}/bin:${env.PATH}"
   
   //array of all the apis
   def apis = []
   
   stage('Checkout Git Code to Jenkins on OpenShift') { // for display purposes
      // Get some code from a GitHub repository
      // need to define git credentials
      git credentialsId: 'git', url: '${git_url}'
   }


   stage('Build zOS Connect APIs') {
        //cd into the apis folder
        dir("apis") {
           //read contents of apis folder into apis file
           sh "ls | grep -vx 'apis' > apis"
           
           //creates a file named data from apis file, reads each line of data and appends each line (api) to list apis
           def data = readFile(file: 'apis')
           def lines = data.readLines()
           for (line in lines) {
              apis.add(line)
           }

           //determine how many apis
           int intNum = apis.size()
           println "The number of APIs to be built: " + intNum
           println "Building these APIs: "+apis
           
           //create aar file for each api
           for (int i = 0; i < intNum; i++) {
              println "Building API "+apis[i]
              sh "${WORKSPACE}/zconbt/bin/zconbt -pd=./apis/"+apis[i]+" -f=./"+apis[i]+".aar" 
           }
           println "aar files that have been built: "
           println "${apis}"
           sh "rm *.aar"
           println "Exiting Stage 2, entering Stage 3!"
        }
   }

   
    stage("Publish Artifacts to Artifactory") {
       // Obtain an Artifactory server instance, defined in Jenkins --> Manage:
       // need to define artifactory server in Jenkins
       def artifactory_server = Artifactory.server "artifactory"
       println "Publishing to server: "+artifactory_server
       
       int intNum = apis.size()
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
