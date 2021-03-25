node('master') {
   jdk = tool name: 'JDK8'
   env.JAVA_HOME = "${jdk}"
   env.PATH="${env.JAVA_HOME}/bin:${env.PATH}"
   
   server = "${server}"
   def apis = [ "${api1}"]
   def aars = []
   stringNum = "${number_of_apis}"
   int intNum = stringNum as int

   stage('Checkout aar Files from Artifactory') {
      println "Downloading API artifacts (aar) from Artifactory" 
      // Obtain an Artifactory server instance, defined in Jenkins --> Manage:
      def artifactory_server = Artifactory.server "artifactory"
       
      // download API artifacts from artifactory repository/apis/*.aar into artifacts workspace
      for (int i = 0; i < intNum; i++) {
          def aarFileName = apis[i] 
          println "Downloading "+aarFileName+" from Artifactory server "+server
          def downloadSpec = """{
            "files": [
               {
                  "pattern": "${artifactory_repo_name}/apis/${aarFileName}.aar",
                  "target": "artifacts/"
               }
               ]
            }"""
          // Download from Artifactory.
          artifactory_server.download spec: downloadSpec 
       }
        dir("artifacts/apis/") {
              //read contents of artifacts folder into artifacts file
              sh "ls | grep -vx 'artifacts' > artifacts"
              
              //creates a file named data from artifacts file, reads each line of data and appends each line (API artifact) to list APIs
              def data = readFile(file: 'artifacts')
              def lines = data.readLines()
              for (line in lines) {
                 aars.add(line)
              }

              println "aar files that will be uploaded to server "+server
              println "${aars}"
              //determine how many APIs
              intNum = aars.size()
              println "The number of APIs being deployed is: " + intNum
           
        }
      
   }
   
  stage('Check for and Handle Existing APIs') {
       println "Going to stop and remove existing API from zOS Connect Server if required"
       for (int i = 0; i < intNum; i++) {
           def api = apis[i]
           def resp = stopAndDeleteRunningAPI(api)
           println "Cleared the field for API deploy: "+resp
        }
    }

    stage('Deploy to z/OS Connect Server'){
       //call code to deploy the API.  passing the name of the API as a param
       for (int i = 0; i < intNum; i++) {
           def aarFileName = aars[i]
           installAar(aarFileName)
        }
       sh "rm -r *"
    }
   

}


   //Will stop a running API if required and delete it
   def stopAndDeleteRunningAPI(api_name){

       println("Checking existence/status of API name: "+api_name)

       //will be building curl commands, so saving the tail end for appending
       def urlval = server+"/zosConnect/apis/"+api_name
       def stopurlval = server+"/zosConnect/apis/"+api_name+"?status=stopped"

       //complete curl command will be saved in these values
       def command_val = ""
       def stop_command_val = ""
       def del_command_val = ""

       //call utility to get saved credentials and build curl command with it.  Commands were built to check, stop and delete API
       //curl command spits out response code into stdout.  that's then held in response field to evaluate
       command_val = "curl -o response.json -w %{response_code} --header 'Content-Type:application/json' --insecure "+urlval
       stop_command_val = "curl -X PUT -o responseStop.json --header \'Accept: application/json\' --header 'Content-Type:application/json' --insecure "+stopurlval
       del_command_val = "curl -X DELETE -o responseDel.json --header 'Content-Type:application/json' --insecure "+urlval
      
       // this checks the initial status of the API.  If it exists, HTTP Response Code will be 200
       def response = sh (script: command_val, returnStdout: true)
       println ""
       println "Response code is: "+response

       if(response != "200"){
          println "This API does not exist on the server.  Deploying for first Time"
          return true
       }
       else{
           println "API already exists, stopping and deleting it now"
           //reading status of existing API from response file.  file was created during curl command.
           def myObject = readJSON file: 'response.json'
           println myObject
           def status = myObject.status
           println "API status is "+status
           if(status == "Started"){
               //Stop API
               def responseStop = sh (script: stop_command_val, returnStdout: true)
               def myObjectStop = readJSON file: 'responseStop.json'
               def statusStop = myObjectStop.status
              //ensure that status was actually stopped
               println "New status of API : "+statusStop

              //Delete API using curl command that was built
               def responseDel = sh (script: del_command_val, returnStdout: true)
               println "API delete call complete"
               return true
           }
           else{
               println "Don't know why we hit this! API is currently stopped......handle this if you like"
               return false
           }    
         return true
       }
   }

   def installAar(aarFileName){
       println "Starting aar deployment now"

       def urlval = server+"/zosConnect/apis/"
       def respCode = ""

      //call utility to get saved credentials and build curl command with it and aar file name and then execute command
      //curl command spits out response code into stdout.  that's then held in respCode field to evaluate
       def command_val = "curl -X POST -o response.json -w %{response_code} --header 'Content-Type:application/zip' --data-binary @${WORKSPACE}/artifacts/apis/"+aarFileName+" --insecure "+urlval
       respCode = sh (script: command_val, returnStdout: true)

       println "API Installation Response code is: "+respCode
       if(respCode == "201"){
           println "Deployment completed successfully"
       }else if(respCode == "409"){
           error("Deployment failed due to it already existing")
       }
   }


