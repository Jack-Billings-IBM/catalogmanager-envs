node('master') {
   jdk = tool name: 'JDK8'
   env.JAVA_HOME = "${jdk}"
   env.PATH="${env.JAVA_HOME}/bin:${env.PATH}"
   def zceeHome
   stage('Checkout Git Code') { // for display purposes
      // Get some code from a GitHub repository
      println "project name is catalogTest"
      git credentialsId: 'git', url: 'https://github.com/Jack-Billings-IBM/catalogmanager-envs.git'
   }

   stage('Rebuild zOS Connect API') {
        println "Calling zconbt"
        def output = sh (returnStdout: true, script: 'pwd')
        println output
        sh "${WORKSPACE}/zconbt/bin/zconbt -pd=${WORKSPACE}/catalog -f=${WORKSPACE}/catalog.aar "
        println "Called zconbt for catalog"
        println "Exiting Stage 2, entering Stage 3!"
   }
   stage('Check for and Handle Existing API') {
       println "Going to stop and remove existing API from zOS Connect Server if required"
       def resp = stopAndDeleteRunningAPI("catalog")
       println "Cleared the field for API deploy: "+resp
   }

    stage('Deploy API to z/OS Connect Server'){
       //call code to deploy the API.  passing the name of the API as a param
       def apiFileName ="catalog.aar"
       installAPI(apiFileName)
    }
   
    stage('Test API') {
       def serviceName = "inquireSingle"
       testAPI(serviceName)
       def serviceName2 = "inquireCatalog"
       testAPI(serviceName2)
    }

    stage("Publish Artifacts to Artifactory") {
       // Obtain an Artifactory server instance, defined in Jenkins --> Manage:
       def server = Artifactory.server "artifactory"

       // Read the upload spec which was downloaded from github.
       def uploadSpec = readFile 'Artifactory/apis-upload.json'
       // Upload to Artifactory.
       def buildInfo = server.upload spec: uploadSpec

       // Publish the build to Artifactory
       server.publishBuildInfo buildInfo

       sh "rm response.json"
       sh "rm responseDel.json"
       sh "rm responseStop.json"
       sh "rm catalog.aar"
      
    }   
   
    // stage("Push to GitHub") {
    //    sh "rm response.json"
    //    sh "rm responseDel.json"
    //    sh "rm responseStop.json"
    //    sh "git config --global user.email 'jack.billings@ibm.com'"
    //    sh "git config --global user.name 'Jack-Billings-IBM'"
    //    sh "git add -A"
    //    sh "git commit -m 'new aar file'"
    //    //need to add git credentials to jenkins
    //    withCredentials([usernamePassword(credentialsId: 'git', usernameVariable: 'GIT_USERNAME', passwordVariable: 'GIT_PASSWORD')]){    
    //        sh('''
    //            git config --local credential.helper "!f() { echo username=\\$GIT_USERNAME; echo password=\\$GIT_PASSWORD; }; f"
    //            git push origin HEAD:master
    //        ''')
    //    }
    // }
}
// node('nodejs') {
//    def templateName = 'egui'
//    stage('Build egui') {
//            script {
//                openshift.withCluster() {
//                    openshift.withProject() {
//                      def builds = openshift.selector("bc", templateName).related('builds')
//                      timeout(5) { 
//                        builds.untilEach(1) {
//                          return (it.object().status.phase == "Complete")
//                        }
//                      }
//                    }
//                }
//          }
//        }
//     stage('Deploy egui') {
//        script {
//             openshift.withCluster() {
//                 openshift.withProject() {
//                   def rm = openshift.selector("dc", templateName).rollout().latest()
//                   timeout(5) { 
//                     openshift.selector("dc", templateName).related('pods').untilEach(1) {
//                       return (it.object().status.phase == "Running")
//                     }
//                   }
//                 }
//         }
//       }
//     }
   
// }
 



   //Will stop a running API if required and delete it
   def stopAndDeleteRunningAPI(apiName){

       println("Checking existence/status of API: "+apiName)

       //will be building curl commands, so saving the tail end for appending
       def urlval = "150.238.240.73:30820/zosConnect/apis/"+apiName
       def stopurlval = "150.238.240.73:30820/zosConnect/apis/"+apiName+"?status=stopped"

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
               println "Don't know why we hit this! Service is currently stopped......handle this if you like"
               return false
           }    
         return true
       }
   }

   def installAPI(apiFileName){
       println "Starting API deployment now"

       def urlval = "150.238.240.73:30820/zosConnect/apis/"
       def respCode = ""

      //call utility to get saved credentials and build curl command with it and sar file name and then execute command
      //curl command spits out response code into stdout.  that's then held in respCode field to evaluate
       def command_val = "curl -X POST -o response.json -w %{response_code} --header 'Content-Type:application/zip' --data-binary @${WORKSPACE}/catalog/"+apiFileName+" --insecure "+urlval
       respCode = sh (script: command_val, returnStdout: true)

       println "Service Installation Response code is: "+respCode
       if(respCode == "201"){
           println "Deployment completed successfully"
       }else if(respCode == "409"){
           error("Deployment failed due to it already existing")
       }
   }
   
   def testAPI(serviceName) {
      println "Starting testing now"

      def urlval = "150.238.240.73:30820/catalogManager/items"
      def respCode = ""
      
      //def single = readJSON file: 'tests/inquireSingle_service_request.json'
      if(serviceName == "inquireSingle") {
         def command_val = "curl -X GET --fail -w %{response_code} --header 'Accept: application/json' '"+urlval+"/30' -o ${WORKSPACE}/tests/"+serviceName+"_api.json"
         respCode = sh (script: command_val, returnStdout: true)
         println serviceName+" API Test Response code is: "+respCode
      }
      else {
         def command_val = "curl -X GET --fail -w %{response_code} --header 'Accept: application/json' '"+urlval+"?startItemID=40' -o ${WORKSPACE}/tests/"+serviceName+"_api.json"
         respCode = sh (script: command_val, returnStdout: true)
         println serviceName+" API Test Response code is: "+respCode
      }
   }

