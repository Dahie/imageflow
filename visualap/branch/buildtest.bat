@REM build all javabeans
del /s test\*.bak
set compiledir=test\demux
javac %compiledir%\*.java
jar cfm demux.jar %compiledir%\manifest %compiledir%\*.class %compiledir%\*.png %compiledir%\*.html common\SampledAudio.class
move /Y demux.jar beans
set compiledir=test\imagefilter
javac %compiledir%\*.java
jar cfm imagefilter.jar %compiledir%\manifest %compiledir%\*.class %compiledir%\*.png %compiledir%\*.html
move /Y imagefilter.jar beans
set compiledir=test\inspect
javac %compiledir%\*.java
jar cfm inspect.jar %compiledir%\manifest %compiledir%\*.class %compiledir%\*.png %compiledir%\*.html common\SampledAudio.class
move /Y inspect.jar beans
set compiledir=test\readfile
javac %compiledir%\*.java
jar cfm readfile.jar %compiledir%\manifest01 %compiledir%\*.class %compiledir%\*.gif %compiledir%\*.html common\SampledAudio.class
move /Y readfile.jar beans
set compiledir=test\speaker
javac %compiledir%\*.java
jar cfm speaker.jar %compiledir%\manifest %compiledir%\*.class %compiledir%\*.gif %compiledir%\*.html
move /Y speaker.jar beans
set compiledir=test\mux
javac %compiledir%\*.java
jar cfm mux.jar %compiledir%\manifest %compiledir%\*.class %compiledir%\*.png %compiledir%\*.html
move /Y mux.jar beans
set compiledir=test\tonegenerator
javac %compiledir%\*.java
jar cfm tonegenerator.jar %compiledir%\manifest %compiledir%\*.class %compiledir%\*.png %compiledir%\*.html common\SampledAudio.class
move /Y tonegenerator.jar beans
set compiledir=test\viewer
javac %compiledir%\*.java
jar cfm viewer.jar %compiledir%\manifest %compiledir%\*.class %compiledir%\*.gif %compiledir%\*.html
move /Y viewer.jar beans
set compiledir=test\writefile
javac %compiledir%\*.java
jar cfm writefile.jar %compiledir%\manifest %compiledir%\*.class %compiledir%\*.gif %compiledir%\*.html
move /Y writefile.jar beans
set compiledir=test\microphone
javac %compiledir%\*.java
jar cfm microphone.jar %compiledir%\manifest %compiledir%\*.class %compiledir%\*.png %compiledir%\*.html common\SampledAudio.class
move /Y microphone.jar beans