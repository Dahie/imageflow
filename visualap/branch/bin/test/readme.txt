The test directory contains the files useful to build test beans for VisualAp
In the main directory use the buildtest.bat batch file to build the test beans

List of useful demo beans:

Imagefilter: performs basic filtering of the image
Viewer: shows the incoming data in a floating window
ReadFile: read a file, contains a property "file" -> a custom editor is used for file property
WriteFile: write a file, supported type: text, audio, image
Mux: generate stereo audio from two mono audio inputs
DeMux: split stereo audio in two mono audio
Inspect: shows the type of the incoming data
Speaker: plays an audio stream
ToneGenerator: generate a simple audio tone, contains properties that are checked against max values, contains a property "type" -> a custom editor is used to select a specific value


The following beans are only old experiments, please ignore:

Sink: contains a property "value" that cannot be greater than 9
Transform: does not contain the 32x32 icon -> generic box is used to show the bean on the board
Source: generate a single string
