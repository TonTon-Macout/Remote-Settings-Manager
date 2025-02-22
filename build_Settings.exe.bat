@echo off
chcp 65001
cd /d "%~dp0"

echo.
echo ================ Запуск скрипта сборки ===================
echo ------ Должен быть установлен python версии 3 и выше -----
echo --- а так же библиотеки PyQt6 PyQt6-WebEngine requests ---
echo.

REM Извлекаем имя и номер версии из скрипта
echo Извлекаем имя и номер версии из скрипта Settings...
for /f "delims=" %%i in ('python -c "import Settings; print(Settings.NAME, Settings.VERSION)" 2^>NUL') do set "line=%%i"
if "%line%"=="" (
    echo Ошибка: Не удалось извлечь имя и версию из Settings.py.
    echo Убедитесь, что файл Settings.py существует и содержит переменные NAME и VERSION.
    pause
    exit /b 1
)
REM echo Извлеченная строка: %line%
for /f "tokens=1,2" %%a in ("%line%") do (
    set "name=%%a"
    set "version=%%b"
)
echo Имя приложения: %name%
echo Версия: %version%
echo.

REM Определяем иконку в зависимости от имени приложения
REM Если имя отличается от GLUONiCA то используем стандартную
if /i "%name%"=="GLUONiCA" (
    set "icon_file=g_icon.ico"
) else ( 
    set "icon_file=icon.ico"
)
echo Используемая иконка: %icon_file%
echo.

REM Проверка наличия необходимых файлов
:CHECK_FILES
set "missing_files="
REM echo Проверяем наличие иконок...

REM Проверяем главную иконку в зависимости от имени
if not exist "%icon_file%" (
    set "missing_files=%missing_files% %icon_file%"
)

REM Проверяем иконки для кнопок
if not exist "minimize.png" (
    set "missing_files=%missing_files% minimize.png"
)
if not exist "refresh.png" (
    set "missing_files=%missing_files% refresh.png"
)
if not exist "scan.png" (
    set "missing_files=%missing_files% scan.png"
)
if not exist "close.png" (
    set "missing_files=%missing_files% close.png"
)

REM Если есть иконки, выводим ошибку и спрашиваем пользователя
if not "%missing_files%"=="" (
    echo.
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

REM Создаем имя исполняемого файла и папки
set "exe_name=%name%_app_v%version%"
set "dir_name=%exe_name%"
echo Будет создан исполняемый файл: %exe_name%.exe
echo Имя папки для сборки: %dir_name%
echo.

timeout /t 5

REM Запускаем PyInstaller с использованием извлеченного имени, версии и выбранной иконки
echo Запускаем PyInstaller...
start /wait cmd /c python -m PyInstaller --onefile --windowed --icon="%icon_file%" --name "%exe_name%" Settings.py
if %errorlevel% neq 0 (
    echo Ошибка при сборке exe файла.
    pause
    exit /b 1
) else (
    echo PyInstaller завершил работу.
)
echo.

REM Удаление ненужных файлов и папок после сборки
echo Удаляем временные папки и файлы...
rmdir /s /q build
del /q Settings.spec
del /q "%exe_name%.spec"

REM Создание папки с именем итогового исполняемого файла
echo Создаем папку %dir_name%...
mkdir "%dir_name%"

REM Перемещаем исполняемый файл в созданную папку
echo Перемещаем %exe_name%.exe в папку %dir_name%...
move "dist\%exe_name%.exe" "%dir_name%"
if %errorlevel% neq 0 (
    echo Не удалось переместить exe файл.
    pause
    exit /b 1
)

REM Копирование файлов в созданную папку
echo Копируем файлы...
REM Копируем только нужную иконку в зависимости от имени
if /i "%name%"=="GLUONiCA" (
    copy "g_icon.ico" "%dir_name%"
) else (
    copy "icon.ico" "%dir_name%"
)
REM Копируем остальные файлы всегда
copy "minimize.png" "%dir_name%"
copy "refresh.png" "%dir_name%"
copy "scan.png" "%dir_name%"
copy "close.png" "%dir_name%"

REM Удаляем временную папку dist
rmdir /s /q dist

REM Удаляем папку __pycache__
rmdir /s /q __pycache__

echo.
echo Сборка завершена. Итоговый исполняемый файл и дополнительные файлы находятся в папке: %dir_name%
pause