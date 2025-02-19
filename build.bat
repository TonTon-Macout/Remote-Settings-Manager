@echo off
cd /d "%~dp0" 
start cmd /k python -m PyInstaller --onefile --windowed --icon=icon.ico Settings_app_v0.07.py
