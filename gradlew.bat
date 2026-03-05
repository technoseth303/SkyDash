@echo off
set JAVA_EXE=%JAVA_HOME%\bin\java.exe

if exist "%JAVA_EXE%" goto runGradle

echo ERROR: JAVA_HOME is not set and no java.exe found in PATH
exit /b 1

:runGradle
"%JAVA_EXE%" org.gradle.wrapper.GradleWrapperMain %*
