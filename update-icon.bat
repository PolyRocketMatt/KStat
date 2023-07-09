@echo off
set "source=img\logo-icon.svg"
set "destination=docs\images\logo-icon.svg"

REM Check if source file exists
if not exist "%source%" (
    echo Source file does not exist.
    exit /b
)

REM Create the destination folder if it doesn't exist
if not exist "%destination%\.." (
    mkdir "%destination%\.."
)

REM Move the file
copy "%source%" "%destination%"

REM Check if the move operation was successful
if errorlevel 1 (
    echo An error occurred while moving the file.
) else (
    echo File copied successfully.
)