=================================================================================

ImageFlow 1.2pre, 27 October 2010, 
Contributors: 
Daniel Senff (mail@danielsenff.de)
Kai-Uwe Barthel (k.barthel@fhtw-berlin.de)
Friedrich Maiwald (friedrich.maiwald@gmx.de)

Based on VisualAp (https://visualap.dev.java.net/) by Livio.

Licensed under the "GNU GENERAL PUBLIC LICENSE" as described in license.txt file.

=================================================================================

Introduction

This plugin offers a node-based graphical user interface for Macro-creation and image editing. 
It allows to create macros by connecting nodes to form a workflow, which represents the 
way macro-commandos are called by ImageJ. These workflows can be saved and loaded. 

A workflow consists of Sources, which loads or create images and stacks. 
They will be connected to other units to define the order of processing. 
The workflow requires at least one node to be defined as a display. 
The result of this unit will be displayed after executing the workflow.

ImageFlow is also available as a stand-alone program that uses ImageJ as a library. 
To run it, download and extract the archive Imageflow.zip and double click on the ImageFlow_.jar. 
Works on all platforms, Java 5.0 or later must be installed.

A number of unit-elements for building graphs are included, however the list is far from being complete. 
Documentation on how to create own units is included and can be used. 
I can incorporate more units in later releases. 
Feedback of any kind is well appreciated.

----------------------------------------

Please read the imageflow.html for further information. 

Imageflow requires either JRE or JDK, version 1.5 or later
Tested with Windows XP, Linux Debian distro (KDE and Gnome) and MacOS X 10.6

----------------------------------------

Releases notes

Version 1.2: stable release
Version 1.1: stable release
Version 1.0: stable release
Version 0.9: is the first public beta. It works, but it is not yet stable.

----------------------------------------

Known issues

http://github.com/dahie/imageflow/issues

- parameters window has at its borders a repaint problem in WinXP
- CombineStacks leaves a temporary window opened
- imageJ window sometimes doesn't close properly on Mac OSX and Linux

----------------------------------------

Related Links:

Source Code: http://github.com/dahie/imageflow
Wiki: http://github.com/dahie/imageflow/wiki
XML-Specification: http://github.com/Dahie/imageflow/wiki/Unit-XML-Specification

----------------------------------------

Credits

Author: Daniel Senff
Contributions:
- Kai-Uwe Barthel
- Friedrich Maiwald 

----------------------------------------

License

ImageFlow is build on top of VisualAp, a visual-programming application by Livio. 
VisualAp is licensed under the GPL V2.
https://visualap.dev.java.net/

