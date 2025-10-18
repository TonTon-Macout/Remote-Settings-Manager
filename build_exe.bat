@echo off
chcp 65001
cd /d "%~dp0"
echo.
echo ==================== Запуск скрипта сборки ======================
echo -------- Должен быть установлен Python версии 3.8 и новее -------
echo -- PyInstaller а также библиотеки PyQt6 PyQt6-WebEngine psutil requests beautifulsoup4 urllib3 lxml --
echo.
REM Проверка прав администратора
net session >nul 2>&1
if %errorlevel% neq 0 (
    echo Запустите скрипт от имени администратора.
    pause
    exit /b 1
)
REM Извлекаем имя и номер версии из скрипта
echo Извлекаем имя и версию из Settings...
for /f "delims=" %%i in ('python -c "import Settings; print(Settings.NAME, Settings.VERSION)" 2^>NUL') do set "line=%%i"
if "%line%"=="" (
    echo Ошибка: Не удалось извлечь данные из Settings.py.
    echo Убедитесь, что файл Settings.py существует и содержит переменные NAME и VERSION.
    pause
    exit /b 1
)
for /f "tokens=1,2" %%a in ("%line%") do (
    set "name=%%a"
    set "version=%%b"
)
echo Имя приложения: %name%
echo Версия: %version%
echo.

REM Устанавливаем иконку
set "icon_file=icon.ico"
echo Используемая иконка программы: %icon_file%
echo.

REM Проверка наличия необходимых файлов
:CHECK_FILES
set "missing_files="
echo Проверка наличия иконок...
if exist "%icon_file%" (
    echo      %icon_file%: найден
) else (
    echo      %icon_file%: не найден
    set "missing_files=%missing_files% %icon_file%"
)
if exist "icon_sm.ico" (
    echo      icon_sm.ico: найден
) else (
    echo      icon_sm.ico: не найден
    set "missing_files=%missing_files% icon_sm.ico"
)
if exist "minimize.png" (
    echo      minimize.png: найден
) else (
    echo      minimize.png: не найден
    set "missing_files=%missing_files% minimize.png"
)
if exist "refresh.png" (
    echo      refresh.png: найден
) else (
    echo      refresh.png: не найден
    set "missing_files=%missing_files% refresh.png"
)
if exist "scan.png" (
    echo      scan.png: найден
) else (
    echo      scan.png: не найден
    set "missing_files=%missing_files% scan.png"
)
if exist "close.png" (
    echo      close.png: найден
) else (
    echo      close.png: не найден
    set "missing_files=%missing_files% close.png"
)
if exist "copy.png" (
    echo      copy.png: найден
) else (
    echo      copy.png: не найден
    set "missing_files=%missing_files% copy.png"
)
if exist "open.png" (
    echo      open.png: найден
) else (
    echo      open.png: не найден
    set "missing_files=%missing_files% open.png"
)
if exist "info.png" (
    echo      info.png: найден
) else (
    echo      info.png: не найден
    set "missing_files=%missing_files% info.png"
)
if exist "settings.png" (
    echo      settings.png: найден
) else (
    echo      settings.png: не найден
    set "missing_files=%missing_files% settings.png"
)
if exist "swap.png" (
    echo      swap.png: найден
) else (
    echo      swap.png: не найден
    set "missing_files=%missing_files% swap.png"
)
if exist "wled.png" (
    echo      wled.png: найден
) else (
    echo      wled.png: не найден
    set "missing_files=%missing_files% wled.png"
)
if exist "unknown.png" (
    echo      unknown.png: найден
) else (
    echo      unknown.png: не найден
    set "missing_files=%missing_files% unknown.png"
)
if exist "gear.png" (
    echo      gear.png: найден
) else (
    echo      gear.png: не найден
    set "missing_files=%missing_files% gear.png"
)
if exist "splash_sm.png" (
    echo      splash_sm.png: найден
) else (
    echo      splash_sm.png: не найден
    set "missing_files=%missing_files% splash_sm.png"
)
if exist "splash.png" (
    echo      splash.png: найден
) else (
    echo      splash.png: не найден
    set "missing_files=%missing_files% splash.png"
)
if exist "trash.png" (
    echo      trash.png: найден
) else (
    echo      trash.png: не найден
    set "missing_files=%missing_files% trash.png"
)
if exist "ha.png" (
    echo      ha.png: найден
) else (
    echo      ha.png: не найден
    set "missing_files=%missing_files% ha.png"
)
if exist "new.png" (
    echo      new.png: найден
) else (
    echo      new.png: не найден
    set "missing_files=%missing_files% new.png"
)
if exist "back.png" (
    echo      back.png: найден
) else (
    echo      back.png: не найден
    set "missing_files=%missing_files% back.png"
)
echo.
if not "%missing_files%"=="" (
    echo Ошибка: Отсутствуют следующие иконки:%missing_files%
    echo Поместите недостающие иконки в текущую директорию и нажмите любую клавишу.
    echo Для отмены нажмите Ctrl+C.
    pause
    goto CHECK_FILES
) else (
    echo Отлично! Все необходимые иконки найдены, продолжаем...
    echo.
)

REM Замена запятых на точки в номере версии (если есть)
set "version=%version:,=.%"
echo.
REM Создаем имя исполняемого файла
set "exe_name=%name%_v%version%"
echo Будет создан исполняемый файл: %exe_name%.exe
echo.
timeout /t 5

REM Запускаем PyInstaller с использованием извлеченного имени, версии и иконки
echo Запускаем PyInstaller...
start /wait cmd /c python -m PyInstaller --onefile --windowed --icon="%icon_file%" ^
--add-data "%icon_file%;." ^
--add-data "icon_sm.ico;." ^
--add-data "minimize.png;." ^
--add-data "refresh.png;." ^
--add-data "scan.png;." ^
--add-data "close.png;." ^
--add-data "copy.png;." ^
--add-data "open.png;." ^
--add-data "info.png;." ^
--add-data "settings.png;." ^
--add-data "swap.png;." ^
--add-data "wled.png;." ^
--add-data "unknown.png;." ^
--add-data "gear.png;." ^
--add-data "splash_sm.png;." ^
--add-data "splash.png;." ^
--add-data "trash.png;." ^
--add-data "ha.png;." ^
--add-data "new.png;." ^
--add-data "back.png;." ^
--name "%exe_name%" Settings.py --log-level DEBUG
if %errorlevel% neq 0 (
    echo Ошибка при сборке exe файла. Проверьте лог PyInstaller.
    pause
    exit /b 1
) else (
    echo PyInstaller завершил работу.
)
echo.

REM Перемещаем исполняемый файл в текущую директорию
echo Перемещаем %exe_name%.exe в текущую директорию...
move "dist\%exe_name%.exe" "." 2>nul
if %errorlevel% neq 0 (
    echo Не удалось переместить exe файл. Проверьте папку dist.
    pause
    exit /b 1
)

REM Удаление ненужных файлов и папок после сборки
echo Удаляем временные папки и файлы...
rmdir /s /q build 2>nul
del /q /f Settings.spec 2>nul
del /q /f "%exe_name%.spec" 2>nul
rmdir /s /q dist 2>nul
rmdir /s /q __pycache__ 2>nul

echo.
echo Сборка завершена. Итоговый исполняемый файл: %exe_name%.exe
pause