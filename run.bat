@echo off
echo Lancement de l'application Fnart...

REM Vérifier si Java est installé
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo Java n'est pas installé ou n'est pas dans le PATH.
    echo Veuillez installer Java 17 ou supérieur.
    pause
    exit /b 1
)

REM Set Maven path
set MAVEN_HOME=%~dp0apache-maven-3.9.9
set PATH=%MAVEN_HOME%\bin;%PATH%

REM Compiler le projet avec Maven
echo Compilation du projet...
call mvn clean package

REM Vérifier si la compilation a réussi
if %errorlevel% neq 0 (
    echo La compilation a échoué.
    pause
    exit /b 1
)

REM Exécuter l'application
echo Lancement de l'application...
java --module-path "target/modules" --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.base,javafx.media -jar target/fnartPI-1.0-SNAPSHOT.jar

REM Si l'application s'est terminée avec une erreur
if %errorlevel% neq 0 (
    echo L'application s'est terminée avec une erreur.
    pause
    exit /b 1
)

echo Application terminée.
pause 