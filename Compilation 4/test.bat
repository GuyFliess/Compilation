
cd inputs
for %%i in (*) do java -cp ..\bin;..\gearley.jar Main  %%i -Llibic.sig >  ..\myOutputs\%%i~ni.3ac
cd ..\myOutputs

for %%i in (*) do java -jar ..\3ac-emu.jar -q %%i > ..\myFinalResults\%%i~ni.3ac 
cd ..\myFinalResult

cd ..
echo !!!ALL DONE!!!