
cd inputs
for %%i in (*) do java -cp ..\bin;..\gearley.jar Main  %%i -Llibic.sig >  ..\myOutputs\%%i
cd ..\myOutputs
for %%i in (*) do move /y  %%i %%~ni.3ac
for %%i in (*) do java -jar ..\3ac-emu.jar %%i > ..\myFinalResult\%%i
cd ..\myFinalResult
for %%i in (*) do move /y  %%i %%~ni.out
cd ..
cd outputs
for %%i in (*) do fc %%i ..\myFinalResult\%%i 
cd ..
echo !!!ALL DONE!!!