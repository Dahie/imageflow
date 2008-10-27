=================================================================================
VisualAp 1.1, February 2008, author: javalc6@gmail.com

Licensed under the "GNU GENERAL PUBLIC LICENSE" as described in license.txt file.
=================================================================================
Introduction
VisualAp is a visual framework for building application and systems based on visual components. 
Users can add their own visual components in order to extend the capability of VisualAp.
It will be possible to generate Java code, run process simulation. 
VisualAp can be used in order to perform audio processing, image processing, text and other process-driven emulation.
VisualAp will provide a visual framework based on lightweight components, called proclet. 
The user can create an application by selecting the components from a toolbox, configuring the parameters (via the Javabeans framework), and connecting the components together in order to set-up communication channels between the components.
In the first release the user will run code execution through the engine included in VisualAp, in interpreted mode. 
In later releases it will be possible to generate Java code that can be compiled and run in standalone mode.
VisualAp can be used in order to perform audio processing, image processing, text and other process-driven emulation.
----------------------------------------
Basic concepts
VisualAp is based on two main concepts: system and components.
A component is the basic element providing some services. A system is build using a number of components that are configured
and connected together in order to achieve a complex functionality.
In VisualAp a system is a graph whose nodes are the components.
In VisualAp a component is also called proclet (processing element). 

There are three type of components:
- source: any component that generates some sort of data
- sink: any component that consumes data
- processor: any component that process input data into output data
----------------------------------------
The simple way to launch the program is to run visualap.bat in a DOS window
----------------------------------------

Usage
       java visualap.VisualAp  [-fast] [-run] [-report] [-uniqueID] [-help] <filename>
       java -jar visualap.jar  [-fast] [-run] [-report] [-uniqueID] [-help] <filename>

Command line options:
-fast       fast startup
-run        automatic run
-report     print a report about available beans
-uniqueID   print uniqueID
-help       this help

----------------------------------------

Please read userguide.pdf for further information

VisualAp requires either JRE or JDK, version 1.5 or later
Tested with Windows 2000, Windows XP and Linux Debian distro
----------------------------------------

Releases notes

Version 1.1: added Check Latest Version feature (from the main menu: Help -> Check Version), added optional <version> field in components

