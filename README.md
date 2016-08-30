# couchbase-lite-java-sg-replicate

This is a very simple Java app built using Swing for the UI.

It is designed to illustrate replication between Sync Gateway instances.

The app allows saving a document to a Couchbase Lite database.  Once you start replication, the app then pushes updates
to the primary Sync Gateway instance.  That instance then pushes changes to the backup Sync Gateway instance.

The app monitors and displays changes in both Sync Gateway instances and displays the information.  With this, you can
see document saves traveling the path first to the primary gateway, then to the backup.

Configuration files are included for both Sync Gateway instances.  The configuration are meant to be used so that
everything runs on a single machine.

To run, install Sync Gateway.  Start two instances, first one using the backup_gateway_config.json file, then one using
the primary_gateway_config.json file.

Once the Sync Gateway instances are running, start the app itself.  You will see three buttons and three text panes.  
You can paste JSON into the leftmost text pane.  Clicking "Save" will then save the JSON as a document in the Couchbase
Lite database.

Clicking "Start" starts the replications.  The center pane monitors changes in the primary Sync Gateway instance.  The 
rightmost pane monitors changes in the backup instance.  After replication has started, every time you save a document 
in the leftmost pane, you should rapidly see change notices in the center and right panes.
 
This code is _not_ robust.  It is meant to illustrate the core concepts.  As such it is quite simple.  There's very
little error handling, and the Couchbase Lite data you store does not get cleaned up automatically.




