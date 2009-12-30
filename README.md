# Imageflow

An application proving an graphical node-based user interface for creating [ImageJ][imagej] workflows, for Macro creation and image editing in general.

## Imageflow Provides

It provides you with the means to create macros by connecting nodes to form a workflow, which represents the way macro-commands are called by ImageJ. These workflows can be saved and loaded. 

A workflow consists of Sources, which load or create images. They will be connected to other units to define the order of processing. A workflow requires at least one node to be defined as a display. The result of this unit will be opened after running the workflow.

ImageFlow is also available as a stand-alone program that uses ImageJ as a library. To run it, download and extract the archive Imageflow.zip and double click on the ImageFlow_.jar. 
Works on all platforms, Java 5.0 or later must be installed.

A number of unit-elements for building graphs are included, however the list is not very long yet. Documentation on how to create own units is included and can be used. I can incorporate more units in later releases. 
Feedback of any kind is well appreciated.

## Quick Start

Delete the previous version, download Imageflow-binaries and extract somewhere on your hard drive. Start the jar to run the program.

To start Imageflow from ImageJ you have to move "Imageflow"-folder into ImageJ's plugin directory. After restarting ImageJ you will find a menu-item in the Plugins-Menu to start the plugin.

## More Information
Please see the [wiki][wiki]

## Author
Imageflow is written by 
[Daniel Senff][dahie].<br>
Kai Uwe Barthel<br>
Friedrich Maiwald

## License
Released under a [Gnu General Public License v2][license].

[dahie]: http://github.com/Dahie
[imagej]: http://rsb.info.nih.gov/ij/
[wiki]: http://wiki.github.com/Dahie/imageflow
[license]: http://github.com/Dahie/imageflow/blob/master/LICENSE.md