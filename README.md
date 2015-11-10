# restful-harmony
Tbis subsystem is an extension of the https://github.com/tuck182/harmony-java-client client and starts a rest server that implements the basic harmony hub calls. this uses the tuck182 client as a library with no changes.  
## Build
To customize and build it yourself, build a new jar with maven:  
```
mvn install
```
Otherwise go to http://www.bwssystems.com/apps.html to download the latest jar file.  
## Run
Then locate the jar and start the server with:  
```
java -jar restful-harmony-0.X.Y.jar <harmony hub ip> <harmony user name> <harmony password>
```
## Available Arguments
### -Dserver.port=`<port>`
Optional: The server defaults to running on port 8081. If you're already running a server (like openHAB) on 8081, -Dserver.port=`<port>` on the command line.
```
java -jar -Dserver.port=8081 restful-harmony-0.X.Y.jar <harmony hub ip> <harmony user name> <harmony password>
```
### `<harmony hub ip>`
Required: This is the IP address of your harmony hub.
### `<harmony user name>`
Required: This is your username that you registered at MyHarmony.com.
### `<harmony password>`
Required: This is your password that you registered at MyHarmony.com.
## Api usage
This application exposes a restful api using the constructs for GET/PUT/POST. The following are the commands in the api that are available. The api address is: http://<ip address>:<port>/harmony and the context we will use below for examples is http://host:8081/harmony.

The REST body examples are all formatted for easy reading, the actual body usage should be like this:
```
{"var1":"value1","var2":"value2","var3:"value3"}
```
The body should be all in one string and not separated by returns, tabs or spaces. FYI, GET items do not require a body element. If you would like to see example return of data for full hub configuration, which includes activities and devices, take a look at the resource file config.data.
### List activities
There are no arguments necessary.
```
GET http://host:8081/harmony/list/activities
```
### List devices
There are no arguments necessary.
```
GET http://host:8081/harmony/list/devices
```
### Show current activity
There are no arguments necessary.
```
GET http://host:8081/harmony/show/activity
```
### Show full hub configuration
There are no arguments necessary.
```
GET http://host:8081/harmony/config
```
### Start an Activity
This method requires passing an activity either as an activity numeric id or an activity name.
```
PUT http://host:8081/harmony/start
{
"activityid" : "Watch TV"
}
```
### Press a button on a given device
This method requires passing a device as a numeric id or a name and a button name.
```
PUT http://host:8081/harmony/press
{
"device" : "TV",
"button" : "source"
}
```
## Debugging
To turn on debugging for the rest server, use the following extra parm in the command line:
```
java -jar -Dorg.slf4j.simpleLogger.defaultLogLevel=DEBUG restful-harmony-0.X.Y.jar <harmony hub ip> <harmony user name> <harmony password>
```
## Development Mode
To turn on development mode so that it will not need an Harmony Hub for testing, use the following extra parm in the command line and the harmony ip and login info will not be needed:
```
java -jar -Ddev.mode=true restful-harmony-0.X.Y.jar
```