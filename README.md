# TelecomCloudAnalytics
Predictive Analytics, Up-time Management, Rule based self learning for a telecom organisation

This is a java application using Esper and camel to address -
a) How predictive Analysis can help a telecom company to proactively anticipate a problem.
b) to monitor their network in real time to identify potential problems and fix them before they occur.
c) Predict the reasons for a problem, Validate the prediction and Take Action.

Installation Instructions: 
1.	Please create an installation directory in a Linux machine like below – 
/test/TelecomCloudAnalytics Hence forth, we will refer this location as ‘Installation_directory’.
2.	Please put swisscomcloudanalytics.jar file the installation_directory.
3.	Please create below two folders in installation_directory – 
config/
configcopy/
4.	Please place ‘AllRules.xml’ file in config directory. 
5.	Please place ‘Input.xml’ file in installation_directory.
6.  Please use below command to start the application - 
java -jar swisscomcloudanalytics.jar TELCO_CLOUD_ANALYTICS /test/TelecomCloudAnalytics/Input.xml true >> {log_file} 2>&1
7.	Please use below commands to stop the application - 
ps axf | grep swisscomcloudanalytics.jar | grep -v grep | awk '{print "kill -9 " $1}' | sh
ps axf | grep swisscomcloudanalytics.jar | grep -v grep | awk '{print "kill -15 " $1}' | sh

Rule Change Steps:
1.	Go to Linux machine and installation_directory location.
2.	Assuming that installation_directory location is /test/TelecomCloudAnalytics, please go to config directory.
3.	One will find “AllRules.xml” file in config directory, absolute location of the xml file is /test/TelecomCloudAnalytics/config
4.	Please make sure below two directories are present in /test/TelecomCloudAnalytics location – 
config/
configcopy/

5.	Now, use the following command to change “AllRules.xml” file
vi AllRules.xml 
6.	Please carefully follow the instruction as mentioned in the xml file. Mentioning below few important points – 
a)	All the rule statements should be under “AllRules” root xml tag
b)	All the rule statements should be enclosed with a UNIQUE tab name for each rule
c)	One can make changes at any point of in xml file, the new change (addition/modification) will get reflected within data polling interval as mentioned in “Input.xml” file.
d)	Individual rule syntax has been mentioned in AllRules.xml file itself
e)	Please contact admin team if any further queries or clarification
