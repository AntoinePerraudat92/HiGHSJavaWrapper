setlocal

copy "%HIGHS_HOME%\build\Release\bin\highs.dll" . || exit /b 1
copy "%HIGHS_HOME%\build\Release\bin\highs.lib" . || exit /b 1

cd src\main\java\highs || exit /b 1

echo "COMPILING"
cl /O2 /EHsc /I"%JAVA_HOME%\include" /I"%JAVA_HOME%\include\win32" /I"%HIGHS_HOME%\highs" /I"%HIGHS_HOME%\build" /c swig_java_highs_wrap.cxx || exit /b 1

echo "LINKING"
link /DLL /OUT:..\..\..\..\highswrap.dll swig_java_highs_wrap.obj ..\..\..\..\highs.lib || exit /b 1

endlocal