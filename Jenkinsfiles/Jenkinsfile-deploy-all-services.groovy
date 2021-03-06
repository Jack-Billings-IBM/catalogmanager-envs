node('master') {
   jdk = tool name: 'JDK8'
   env.JAVA_HOME = "${jdk}"
   env.PATH="${env.JAVA_HOME}/bin:${env.PATH}"
   
   server = "${server}"
   def sars = []
   def services = []
   int intNum = 0
   

   stage('Checkout sar Files from Artifactory') {
      println "Downloading serivce artifacts (sar) from Artifactory" 
      // Obtain an Artifactory server instance, defined in Jenkins --> Manage:
       def artifactory_server = Artifactory.server "artifactory"
       
       // download service artifacts from artifactory repository/services/*.sar into artifacts workspace
       def downloadSpec = """{
         "files": [
            {
               "pattern": "${artifactory_repo_name}/services/*.sar",
               "target": "artifacts/"
            }
            ]
         }"""
         artifactory_server.download spec: downloadSpec   

         // cd into services/artifacts directory
         dir("artifacts/services/") {
              //read contents of artifacts folder into artifacts file
              sh "ls | grep -vx 'artifacts' > artifacts"
              
              //creates a file named data from artifacts file, reads each line of data and appends each line (service artifact) to list services
              def data = readFile(file: 'artifacts')
              def lines = data.readLines()
              for (line in lines) {
                 sars.add(line)
                 line = line[0..<line.lastIndexOf('.')]
                 services.add(line)
              }

              println "sar files that will be uploaded to server "+server
              println "${sars}"
              //determine how many services
              intNum = sars.size()
              println "The number of services being deployed is: " + intNum

         }
      
   }
   
  stage('Check for and Handle Existing Services') {
       println "Going to stop and remove existing service from zOS Connect Server if required"
       for (int i = 0; i < intNum; i++) {
           def service = services[i]
           def resp = stopAndDeleteRunningService(service)
           println "Cleared the field for service deploy: "+resp
        }
    }

    stage('Deploy to z/OS Connect Server'){
       //call code to deploy the service.  passing the name of the service as a param
       for (int i = 0; i < intNum; i++) {
           def sarFileName = sars[i]
           installSar(sarFileName)
        }
       sh "rm -r *"
    }
  
}


   //Will stop a running service if required and delete it
   def stopAndDeleteRunningService(service_name){

       println("Checking existence/status of Service name: "+service_name)

       //will be building curl commands, so saving the tail end for appending
       def urlval = server+"/zosConnect/services/"+service_name
       def stopurlval = server+"/zosConnect/services/"+service_name+"?status=stopped"

       //complete curl command will be saved in these values
       def command_val = ""
       def stop_command_val = ""
       def del_command_val = ""

       //call utility to get saved credentials and build curl command with it.  Commands were built to check, stop and delete service
       //curl command spits out response code into stdout.  that's then held in response field to evaluate
       command_val = "curl -o response.json -w %{response_code} --header 'Content-Type:application/json' --insecure "+urlval
       stop_command_val = "curl -X PUT -o responseStop.json --header \'Accept: application/json\' --header 'Content-Type:application/json' --insecure "+stopurlval
       del_command_val = "curl -X DELETE -o responseDel.json --header 'Content-Type:application/json' --insecure "+urlval
      
       // this checks the initial status of the service.  If it exists, HTTP Response Code will be 200
       def response = sh (script: command_val, returnStdout: true)
       println ""
       println "Response code is: "+response

       if(response != "200"){
          println "This Service does not exist on the server.  Deploying for first Time"
          return true
       }
       else{
           println "Service already exists, stopping and deleting it now"
           //reading status of existing service from response file.  file was created during curl command.
           def myObject = readJSON file: 'response.json'
           def status = myObject.zosConnect.serviceStatus
           println "Service status is "+status
           if(status == "Started"){
               //Stop API
               def responseStop = sh (script: stop_command_val, returnStdout: true)
               def myObjectStop = readJSON file: 'responseStop.json'
               def statusStop = myObjectStop.zosConnect.serviceStatus
              //ensure that status was actually stopped
               println "New status of service : "+statusStop

              //Delete API using curl command that was built
               def responseDel = sh (script: del_command_val, returnStdout: true)
               println "Service delete call complete"
               return true
           }
           else{
               println "Don't know why we hit this! Service is currently stopped......handle this if you like"
               return false
           }    
         return true
       }
   }

   def installSar(sarFileName){
       println "Starting sar deployment now"

       def urlval = server+"/zosConnect/services/"
       def respCode = ""

      //call utility to get saved credentials and build curl command with it and sar file name and then execute command
      //curl command spits out response code into stdout.  that's then held in respCode field to evaluate
       def command_val = "curl -X POST -o response.json -w %{response_code} --header 'Content-Type:application/zip' --data-binary @${WORKSPACE}/artifacts/services/"+sarFileName+" --insecure "+urlval
       respCode = sh (script: command_val, returnStdout: true)

       println "Service Installation Response code is: "+respCode
       if(respCode == "201"){
           println "Deployment completed successfully"
       }else if(respCode == "409"){
           error("Deployment failed due to it already existing")
       }
   }


