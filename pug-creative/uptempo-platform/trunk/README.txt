MedSelect Application Development README

Setting up the development environment
1. Install ANT.  Instructions at: http://ant.apache.org/manual/install.html
2. The development environment requires that the Google App Engine SDK is
installed in a directory that can be accessed from a relative path to the path
of this file.  Download the latest App Engine SDK to
./appengine-java-sdk-{version} (for example ./appengine-java-sdk-1.7.2.1).
Then update the build-gae.xml file for the new path.

Build commands
Since this application uses a custom build file, use the command line:

(Compile application)
ant -f build-gae.xml compile

(Development server)
ant -f build-gae.xml runserver