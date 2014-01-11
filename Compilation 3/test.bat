
cd pa-3-input
for %%i in (*) do java -cp ..\bin;..\gearley.jar Main %%i >  ..\output\%%i
cd ..\output
for %%i in (*) do move /y  %%i %%~ni.sym
cd ..
cd pa-3-output
for %%i in (*) do fc %%i ..\output\%%i 
cd ..
echo !!!ALL DONE!!!