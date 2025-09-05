@echo off
echo Compiling and running Torus Rotation...

REM Создаем папку для скомпилированных файлов
if not exist "target" mkdir target

REM Компилируем Java файлы
javac -d target src/Main.java

REM Запускаем приложение
java -cp target Main

pause 