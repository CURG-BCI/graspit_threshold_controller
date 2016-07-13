#!/bin/bash
#cd ~/ros/bci_project_ws/src/bci_controller/scripts
#java -jar statePublisher.jar
#!/bin/sh
exec java -jar `rospack find graspit_threshold_controller`/jars/statePublisherWithTimeout.jar
