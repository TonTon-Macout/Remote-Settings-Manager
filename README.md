
# APP-for-AlexGyver-Settings
Виндовс приложение для поиска и отображения вебморды устройств с установленной библиотекой Settings AlexGyver

+ для запуска скрипта или сборки exe файла необходим python 3 
+ после установки python
+ в папке где расположен скрипт 
+ правкой кнопкой -> открыть в терминале -> скопировать команду -> энтер
+ кириллицы в пути лучше избегать


установить библиотеки
```pip install PyQt6 requests ipaddress```

запуск скрипта
```python Settings_app_v0.06.py```

сборка exe --- иконка должна лежать в той же папке где и скрипт
```pyinstaller --onefile --windowed --icon=icon.ico Settings_app_v0.06.py```

собирется экзешник в папке dist 
рядоом нужно положить все фойлы иконок 

![Снимок экрана 2025-02-16 172425](https://github.com/user-attachments/assets/20cf2ee4-79ae-41fb-9882-3e3b45f95cdd)

![Снимок экрана 2025-02-16 172516](https://github.com/user-attachments/assets/fc6bfe1a-0f53-4b14-afa5-2e16a265367c)
![Снимок экрана 2025-02-16 172501](https://github.com/user-attachments/assets/7ffa2837-f7b2-49a1-9e6a-eac7ec83827a)
![Снимок экрана 2025-02-16 172442](https://github.com/user-attachments/assets/53e3930d-a9bb-4b06-b86a-c11e99b0361c)

