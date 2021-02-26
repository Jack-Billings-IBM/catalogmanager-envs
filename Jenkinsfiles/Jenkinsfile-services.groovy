

node('master') {
   jdk = tool name: 'JDK8'
   env.JAVA_HOME = "${jdk}"
   env.PATH="${env.JAVA_HOME}/bin:${env.PATH}"
   
   stage('Checkout Git Code to Jenkins on OpenShift') { // for display purposes
      // Get some code from a GitHub repository
      println "project name is catalog"
      git credentialsId: 'git', url: 'https://github.com/Jack-Billings-IBM/catalogmanager-envs.git'
   }

   stage('Rebuild zOS Connect Services') {
        int expectedInt = 2
      stringNum = "${number_of_services}"
        int intNum = stringNum.toInteger()    
        
        println "Calling zconbt"
        def output = sh (returnStdout: true, script: 'pwd')
        println output
      
        def obj = [ "1": "${service1}", "2": "${service2}", "3": "${service3}" ]
        def fieldNames = ["1", "2", "3"]
        fieldNames.each { println "Gimme the value ${obj[it]}" }
      
      println stringNum.getClass()   // class java.util.ArrayList
 
      println (stringNum instanceof Int)   
        
        for (i = 1; i < stringNum; i++) {
           println = "Building service ${obj[it]}"
           sh "${WORKSPACE}/zconbt/bin/zconbt -pd=./inquireSingle -f=./inquireSingle.sar " 
        }
        sh "${WORKSPACE}/zconbt/bin/zconbt -pd=./inquireSingle -f=./inquireSingle.sar "
        println "Called zconbt for inquireCatalog"
        sh "${WORKSPACE}/zconbt/bin/zconbt -pd=./inquireCatalog -f=./inquireCatalog.sar "
        println "Called zconbt for inquireSingle"
        println "Exiting Stage 2, entering Stage 3!"
   }
   stage('Check for and Handle Existing Services') {
       println "Going to stop and remove existing service from zOS Connect Server if required"
       def resp = stopAndDeleteRunningService("inquireSingle")
       def resp2 = stopAndDeleteRunningService("inquireCatalog")
       println "Cleared the field for service deploy: "+resp
       println "Cleared the field for service deploy: "+resp2
       }

    stage('Deploy to z/OS Connect Server'){
       //call code to deploy the service.  passing the name of the service as a param
       def sarFileName ="inquireSingle.sar"
       installSar(sarFileName)
       def sarFileName2 ="inquireCatalog.sar"
       installSar(sarFileName2)
    }
   
    stage('Test Services') {
       def serviceName = "inquireCatalog"
       testServices(serviceName)
       def serviceName2 = "inquireSingle"
       testServices(serviceName2)
    }
   
    stage("Publish Artifacts to Artifactory") {
       // Obtain an Artifactory server instance, defined in Jenkins --> Manage:
       def server = Artifactory.server "artifactory"

       // Read the upload spec which was downloaded from github.
       def uploadSpec = readFile 'Artifactory/services-upload.json'
       // Upload to Artifactory.
       def buildInfo = server.upload spec: uploadSpec

       // Publish the build to Artifactory
       server.publishBuildInfo buildInfo
       
       sh "rm response.json"
       sh "rm responseDel.json"
       sh "rm responseStop.json"
       sh "rm inquireSingle.sar"
       sh "rm inquireCatalog.sar"
      
    }
   
    // stage("Push to GitHub") {
    //    sh "rm response.json"
    //    sh "rm responseDel.json"
    //    sh "rm responseStop.json"
    //    sh "git config --global user.email 'jack.billings@ibm.com'"
    //    sh "git config --global user.name 'Jack-Billings-IBM'"
    //    sh "git add -A"
    //    sh "git commit -m 'new sar file'"
    //    //need to add git credentials to jenkins
    //    withCredentials([usernamePassword(credentialsId: 'git', usernameVariable: 'GIT_USERNAME', passwordVariable: 'GIT_PASSWORD')]){    
    //        sh('''
    //            git config --local credential.helper "!f() { echo username=\\$GIT_USERNAME; echo password=\\$GIT_PASSWORD; }; f"
    //            git push origin HEAD:master
    //        ''')
    //    }
    // }
}


   //Will stop a running service if required and delete it
   def stopAndDeleteRunningService(service_name){

       println("Checking existence/status of Service name: "+service_name)

       //will be building curl commands, so saving the tail end for appending
       def urlval = "150.238.240.74:30820/zosConnect/services/"+service_name
       def stopurlval = "150.238.240.74:30820/zosConnect/services/"+service_name+"?status=stopped"

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

       def urlval = "150.238.240.74:30820/zosConnect/services/"
       def respCode = ""

      //call utility to get saved credentials and build curl command with it and sar file name and then execute command
      //curl command spits out response code into stdout.  that's then held in respCode field to evaluate
       def command_val = "curl -X POST -o response.json -w %{response_code} --header 'Content-Type:application/zip' --data-binary @${WORKSPACE}/"+sarFileName+" --insecure "+urlval
       respCode = sh (script: command_val, returnStdout: true)

       println "Service Installation Response code is: "+respCode
       if(respCode == "201"){
           println "Deployment completed successfully"
       }else if(respCode == "409"){
           error("Deployment failed due to it already existing")
       }
   }
   
   def testServices(serviceName) {
      println "Starting testing now"

      def urlval = "150.238.240.74:30820/zosConnect/services/"+serviceName+"?action=invoke"
      def respCode = ""
      
      //def single = readJSON file: 'tests/inquireSingle_service_request.json'
      if(serviceName == "inquireSingle") {
         def single = '''{"DFH0XCMNOperation":{"ca_single_request_id":"01INQS","ca_inquire_single":{"ca_item_ref_req":40}}}'''
         def command_val = "curl -g -X POST -o ${WORKSPACE}/tests/"+serviceName+"_service.json -w %{response_code} --header 'Content-Type: application/json' --data "+single+" --insecure "+urlval
         respCode = sh (script: command_val, returnStdout: true)
         println serviceName+" Service Test Response code is: "+respCode
      }
      else {
         def catalog = '{"DFH0XCMNOperation":{"ca_request_id":"01INQC","ca_inquire_request":{"ca_list_start_ref":20}}}'
         def command_val = "curl -g -X POST -o ${WORKSPACE}/tests/"+serviceName+"_service.json -w %{response_code} --header 'Content-Type: application/json' --header 'Content-Type: text/plain' --data "+catalog+" --insecure "+urlval
         respCode = sh (script: command_val, returnStdout: true)
         println serviceName+" Service Test Response code is: "+respCode
      }
   }
