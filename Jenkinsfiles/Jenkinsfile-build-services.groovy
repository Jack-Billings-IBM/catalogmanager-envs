import static groovy.io.FileType.FILES

node('master') {
   jdk = tool name: 'JDK8'
   env.JAVA_HOME = "${jdk}"
   env.PATH="${env.JAVA_HOME}/bin:${env.PATH}"
   
   server = "${artifactory_server}"
   def services = [ "${service1}", "${service2}", "${service3}" ]
   stringNum = "${number_of_services}"
   int intNum = stringNum as int  
   
   stage('Checkout Git Code to Jenkins on OpenShift') { // for display purposes
      // Get some code from a GitHub repository
      println "project name is catalog"
      git credentialsId: 'git', url: '${git_url}'
   }

   stage('Build zOS Connect Services') {
        println "Calling zconbt"
        def output = sh (returnStdout: true, script: 'pwd')
        println output
        
        for (int i = 0; i < intNum; i++) {
           println "Building service "+services[i]
           sh "${WORKSPACE}/zconbt/bin/zconbt -pd=./"+services[i]+" -f=./"+services[i]+".sar" 
        }
        println "Exiting Stage 2, entering Stage 3!"
   }

   
    stage("Publish Artifacts to Artifactory") {
       // Obtain an Artifactory server instance, defined in Jenkins --> Manage:
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
   
   stage("test reading file names") {
      FILES_DIR = './foo'
      cleanWs()

       sh """
           mkdir foo
           touch foo/bar1
           touch foo/bar2
           touch foo/bar3
       """

       def filenames = [];
       def dir = new File("${env.WORKSPACE}/${FILES_DIR}");
       dir.traverse(type: FILES, maxDepth: 0) {
           filenames.add(it.getName())
       }

       for (int i = 0; i < filenames.size(); i++) {
           def filename = filenames[i]
           echo "${filename}"
       }
   }
   
}
