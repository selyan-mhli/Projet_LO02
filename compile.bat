@echo off
REM Script de compilation pour Windows

echo Compilation du projet Jest...

REM Creer le dossier classes s il n existe pas
if not exist classes mkdir classes

REM Compiler tous les fichiers Java
dir /s /B src\*.java > sources.txt
javac -d classes @sources.txt

if %ERRORLEVEL% EQU 0 (
    echo Compilation reussie !
    del sources.txt
) else (
    echo Erreur de compilation.
    del sources.txt
    exit /b 1
)