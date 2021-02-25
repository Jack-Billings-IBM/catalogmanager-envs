z/OS Connect Enterprise Edition Build Toolkit Version ${zconbt.ver}.${zconbt.rel} (20200817-1534)
===============================================================================

IBM Knowledge Center
--------------------
More information about the Build Toolkit is provided in the IBM Knowledge Center at:
https://www.ibm.com/support/knowledgecenter/SS4SVW_3.0.0//overview/build_toolkit.html 

Running the Build Toolkit
-------------------------

The zconbt command is supplied in the bin directory, with three variations:
   zconbt      for use on a UNIX or Linux or MAC operating system.
   zconbt.bat  for use on a Windows operating system.
   zconbt.zos  for use on a z/OS operating system.

Syntax:  
   To generate a service archive (.sar) or an API requester archive (.ara) file from a properties file:
   
      zconbt --properties=<properties file> --file=<archive file>

        --properties | -p - The file containing the properties for creating the archive
        --file | -f - The name of the service archive (.sar) file or API requester archive (.ara) file to create
        --help | -h - Display this message
        --verbose | -v - Output diagnostic information
        --reportError | -r - Set return code from utility to 4 if a warning occurs or 8 if an error occurs

   To generate a service archive (.sar) or an API archive (.aar) file from a z/OS Connect EE API toolkit project directory:
   
      zconbt --projectDirectory=<APItoolkit project directory> --outputDirectory=<output directory>
      zconbt --projectDirectory=<APItoolkit project directory> --file=<archive file>

        --projectDirectory | -pd - The path and the API toolkit project directory name for creating the archive
        --outputDirectory | -od - The output directory for storing the generated archive. The service or API archive 
                                  file inherits the same name as the service or API with a .sar or .aar file extension
        --file | -f - The name of the service archive (.sar) file or API archive (.aar) file to create
        --help | -h - Display this message
        --verbose | -v - Output diagnostic information
        --reportError | -r - Set return code from utility to 4 if a warning occurs or 8 if an error occurs


Building a Service Archive (SAR) file
------------------------------------
To create a service archive file, define the service configuration in a properties file and run the zconbt command.
The archive file name must end with .sar

   zconbt --properties=service1.properties --file=service1.sar 

To create a service archive file from an API toolkit project directory, specify an output directory to store the 
generated service archive. The service archive file inherits the same name as the service with a .sar file extension.

   zconbt -pd=./u/serviceProjects/HospitalService -od=./u/serviceSARs

Or specify a specific service archive file name. The file extension must be .sar
   
   zconbt -pd=./u/serviceProjects/HospitalService -f=./u/serviceSARs/hospital1.sar


Building an API Archive (AAR) file
----------------------------------
To create an API archive file from an API toolkit project directory, specify an output directory to store the generated
API archive. The API archive file inherits the same name as the API with a .aar file extension.

   zconbt -pd=./u/apiProjects/purchaseAPI -od=./u/AARs

Or specify a specific API archive file name. The file extension must be .aar
   
   zconbt -pd=./u/apiProjects/purchaseAPI -f=./u/AARs/purchase.aar


Building an API Requester Archive (ARA) file
-------------------------------------------
To create an API requester archive file, define the API requester configuration in a properties file and run the zconbt 
command. The archive file name must end with .ara

e.g. zconbt --properties=watson_translator2.properties --file=watson_translator2.ara 


Return codes
------------
By default, the Build Toolkit returns the following values:
  0 - the Build Toolkit has run successfully. 
  1 - the Build Toolkit has encountered an error that prevents it from running correctly or
      there are incorrect or invalid input files
If the Build Toolkit cannot generate a specific item for inclusion in an artifact or the generation
completes with a warning, an error or warning message will be written to the log locatation; as the Build Toolkit run
successfully with the input it was given, the Build Toolkit will return 0.

By specifying the 'reportError' option, the Build Toolkit returns the following values:
  0 - the command completes successfully
  1 - the Build Toolkit has encountered an error during startup that prevents it from running 
      correctly or there is an incorrect or invalid parameter specified
  4 - warnings are produced when generating the requested artifact
  8 - errors are produced when generating the requested artifact or a severe error occurs


Sample Files
------------
samples/service/sample_restClient_sar.properties is the sample properties file for REST client service provider.
samples/apiRequester/sample_ara.properties is the sample properties file for API requester.

*******************************************************************************
(C) Copyright IBM Corporation 2017, 2019 All rights reserved.
    For legal information, see http://www.ibm.com/legal/copytrade.shtml
*******************************************************************************