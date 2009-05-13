=================================================================================

ImageFlow 1.0, May 2009, 
authors: 
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

A workflow consists of Sources, which load or create images. 
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
Tested with Windows XP, Linux Debian distro (KDE) and Mac OS X 10.4

----------------------------------------

Releases notes

Version 1.0: stable release
Version 0.9: is the first public beta. It works, but it is not yet stable.

----------------------------------------

Credits

ImageFlow is build on top of VisualAp, a visual-programming application by Livio. 
VisualAp is licensed under the GPL V2.
https://visualap.dev.java.net/



Thanks by Daniel to:
- family, who survived me working on this project for half a year now
- friends, who É same thing
- Weezer, background-music of the past few weeks
- my testers
- everyone, who had no idea what I was talking about, when I explained this concept
- David for giving me the idea to make this my final thesis

Special thanks to Kai-Uwe Barthel for allowing me to pursue this project for my bachelor-thesis.
Another thanks to Andreas Jahnen for allowing me to continue the development at CRP Tudor.

