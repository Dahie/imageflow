@rem build 
del /s *.class
del /s *.bak
javac graph\*.java
javac visualap\*.java
jar cmf build.mf visualap.jar visualap\*.* graph\*.* property\*.* parser\*.* beans\*.jar test\*.* common\*.* doc\*.pdf readme.txt license.txt ding.wav *.jpg *.vas build.mf build.bat buildtest.bat visualap.bat