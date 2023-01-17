# FTP Notificator

## Short description
An android application, created to track changes in a directory on a remote FTP server, and pop a notification in the event that the content of said directory changes in any way.

## !! WARNING !!
**_Please note that the application (as it is) checks for changes every 30 seconds, and this is considered as suspicious behaviour on most public FTP servers, therefore I recommend you to only use this in scenarios where you have the authority to configure the FTP server to not consider this behaviour as suspicious. If you do use it on public FTP servers, it is possible that after a time you will get blacklisted._**

(Either by account, or by IP adress). 

**Consider yourself warned.**

## Requirements
**To install and run this application, you need to have a device that can run the "v31" version of the android API. In my understanding, as long as devices go, this means that if your device runs the latest version of Android 12 (or newer), the application should run. Also, the UI is designed for mobile devices only, because I intended to run this on mobile phones.**

## Long description
This is my first android application, and my first and primary goal was to get a better understanding of the differences between developing a mobile and a desktop application. Because of this, the application got a very simple user interface, that hides itself in the background once a successful connection has been made.

After compiling and installing the program, it should welcome you on the welcome screen, and ask you to make a new connection. After you tap the "+" button, the program will ask you for information about the host name, the log-in credentials, and the location of the folder you wish to monitor after the connection has been made (referred to as "path").

Once you are done, you can press the connect button to connect. If the connection is successful, the app will hide itself in the background, and a notification should appear that the app is listening to changes in the background.

Technically, the app connects to the FTP server with the specified information every 30 seconds, and it will create you an alarming notification if the folder's contents change in any way. (Either a new file gets uploaded, or an existing one gets deleted, does not notify you when the content of an already existing file has been modified.)

The periodic task will run in the background as long as the main app window is not closed.

## Outer dependencies
You might need to import the apache commons library (version 3.9) into your project to compile and build it. You can find it in the 'libs' subfolder of the 'app' folder. Additionally, you can also download it at:

https://commons.apache.org/proper/commons-net/download_net.cgi

(Only "commons-net-3.9.0.jar" is needed.)
