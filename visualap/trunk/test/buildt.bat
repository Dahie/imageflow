@REM build for transform
del /s test\*.bak
set compiledir=test\transform
javac %compiledir%\*.java
jar cfm transform.jar %compiledir%\manifest11 %compiledir%\*.class %compiledir%\*.gif %compiledir%\*.html
set compiledir=test\sink
javac %compiledir%\*.java
jar cfm sink.jar %compiledir%\manifest10 %compiledir%\*.class %compiledir%\*.gif %compiledir%\*.html
set compiledir=test\source
javac %compiledir%\*.java
jar cfm source.jar %compiledir%\manifest01 %compiledir%\*.class %compiledir%\*.gif %compiledir%\*.html
