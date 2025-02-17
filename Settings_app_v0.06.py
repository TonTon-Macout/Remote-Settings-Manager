import sys
import json
import requests
import ipaddress
import os
from concurrent.futures import ThreadPoolExecutor, as_completed
from PyQt6.QtWidgets import (QDialog, QLabel, QProgressBar, QListWidget, QListWidgetItem)
from PyQt6.QtCore import QThreadPool, QRunnable, pyqtSlot, pyqtSignal, QObject

import ctypes
from PyQt6.QtWidgets import QApplication, QMainWindow, QVBoxLayout, QHBoxLayout, QWidget, QLineEdit, QPushButton, QSpacerItem, QSizePolicy
from PyQt6.QtWebEngineWidgets import QWebEngineView
from PyQt6.QtCore import QUrl, Qt, QPoint
from PyQt6.QtGui import QIcon, QMouseEvent, QColor, QPainter, QPen, QBrush
from PyQt6.QtWidgets import QCheckBox, QMenu
from PyQt6 import QtCore

from PyQt6.QtWebEngineCore import QWebEngineProfile, QWebEnginePage
from PyQt6.QtWebEngineCore import QWebEngineSettings

from PyQt6.QtWidgets import QMessageBox
from PyQt6.QtCore import QThread
from ctypes import wintypes


class CheckWorker(QObject):
    finished = pyqtSignal(bool)
    
    def __init__(self, ip):
        super().__init__()
        self.ip = ip
        
    def run(self):
        try:
            response = requests.get(
                f"http://{self.ip}/settings?action=discover",
                timeout=0.3,
                verify=False
            )
            self.finished.emit(response.status_code == 200)
        except:
            self.finished.emit(False)

#Сигналы для работы потоков
class WorkerSignals(QObject):
    """Определяет сигналы, доступные для воркера"""
    progress = pyqtSignal(int)
    result = pyqtSignal(str)
    value = 0  # Для флага остановки

class DiscoverWorker(QRunnable):
    """Воркер для поиска устройств"""
    def __init__(self, ip, signals, stop_flag):
        super().__init__()
        self.ip = ip
        self.signals = signals
        self.stop_flag = stop_flag
        self.timeout = 0.5
        
    @pyqtSlot()
    def run(self):
        try:
            if self.stop_flag.value == 1:
                return
                
            url = f"http://{self.ip}/settings?action=discover"
            response = requests.get(url, timeout=self.timeout)
            if response.status_code == 200:
                data = response.json()
                self.signals.result.emit(f"{data['name']} at http://{self.ip}/")
        except requests.RequestException:
            pass
        finally:
            self.signals.progress.emit(1)


class ScanDialog(QDialog):
    def __init__(self, web_browser, parent=None):
        super().__init__(parent)
        self.setWindowFlags(self.windowFlags() | Qt.WindowType.Dialog)  # Делаем окно диалоговым
        self.web_browser = web_browser  # Сохраняем экземпляр WebBrowser
        self.setWindowTitle("Scan")
        self.setGeometry(300, 300, 400, 400)
        self.layout = QVBoxLayout(self)

        self.subnet_label = QLabel("Mask:")
        self.layout.addWidget(self.subnet_label)

        self.subnet_input = QLineEdit(self)
        self.subnet_input.setText("192.168.1.0/24")
        self.layout.addWidget(self.subnet_input)

        # Добавляем поле для таймаута
        self.timeout_label = QLabel("Timeout (ms):")
        self.layout.addWidget(self.timeout_label)

        self.timeout_input = QLineEdit(self)
        self.timeout_input.setText("500")  # По умолчанию 500 мс
        self.layout.addWidget(self.timeout_input)

        self.button_layout = QHBoxLayout()

        self.scan_button = QPushButton("Scan")
        self.scan_button.clicked.connect(self.toggle_scan)
        self.button_layout.addWidget(self.scan_button)

        self.clear_button = QPushButton("Clear")
        self.clear_button.clicked.connect(self.clear_devices)
        self.button_layout.addWidget(self.clear_button)

        self.delete_button = QPushButton("Delete")
        self.delete_button.clicked.connect(self.delete_device)
        self.button_layout.addWidget(self.delete_button)

        self.layout.addLayout(self.button_layout)

        self.progress_bar = QProgressBar(self)
        self.layout.addWidget(self.progress_bar)

        self.device_list = QListWidget(self)
        self.device_list.itemClicked.connect(self.select_device)
        self.layout.addWidget(self.device_list)

        self.scanning = False
        self.threadpool = QThreadPool()
        self.stop_flag = WorkerSignals()
        self.stop_flag.value = 0
        self.total_ips = 0
        self.discovered_devices = []

        self.load_devices()
        self.highlight_last_device()

    def toggle_scan(self):
        if self.scanning:
            self.stop_scan()
        else:
            self.start_scan()

    def start_scan(self):
        self.scanning = True
        self.scan_button.setText("Stop")
        self.stop_flag.value = 0
        # Загружаем существующие устройства перед сканированием
        if os.path.exists("discovered_devices.json"):
            with open("discovered_devices.json", 'r') as f:
                self.discovered_devices = json.load(f)
        self.scan_network()

    def stop_scan(self):
        self.scanning = False
        self.scan_button.setText("Scan")
        self.stop_flag.value = 1

    def load_devices(self):
        if os.path.exists("discovered_devices.json"):
            with open("discovered_devices.json", 'r') as f:
                devices = json.load(f)
                for device in devices:
                    device_info = f"{device['name']} at http://{device['ip']}/"
                    self.device_list.addItem(device_info)
        self.highlight_last_device()

    def highlight_last_device(self):
        if os.path.exists("last_url.txt"):
            with open("last_url.txt", 'r') as f:
                last_url = f.read().strip()
                for index in range(self.device_list.count()):
                    item = self.device_list.item(index)
                    if last_url in item.text():
                        item.setBackground(QColor(0, 100, 0))  # фон
                        item.setForeground(QColor(Qt.GlobalColor.white))
                        break

    def scan_network(self):
        subnet_mask = self.subnet_input.text()
        # Получаем значение таймаута и преобразуем в секунды
        try:
            timeout = float(self.timeout_input.text()) / 1000  # конвертируем мс в секунды
        except ValueError:
            timeout = 0.5  # значение по умолчанию, если введено некорректное значение
        
        network = ipaddress.ip_network(subnet_mask)
        self.total_ips = len(list(network))
        self.progress_bar.setMaximum(self.total_ips)
        self.device_list.clear()
        self.load_devices()

        self.completed_ips = 0
        for ip in network:
            if self.stop_flag.value == 1:
                break
            signals = WorkerSignals()
            signals.progress.connect(self.update_progress)
            signals.result.connect(self.add_device)
            worker = DiscoverWorker(ip, signals, self.stop_flag)
            worker.timeout = timeout  # Передаем таймаут в воркер
            self.threadpool.start(worker)

    def update_progress(self, value):
        self.completed_ips += value
        self.progress_bar.setValue(self.completed_ips)
        if self.completed_ips >= self.total_ips or self.stop_flag.value == 1:
            self.scan_button.setText("Scan")
            self.scanning = False
            with open("discovered_devices.json", 'w') as f:
                json.dump(self.discovered_devices, f, indent=4)

    def add_device(self, device_info):
        name, ip = device_info.split(' at http://')
        ip = ip.strip('/')
        
        # Проверяем, есть ли уже такое устройство
        for existing_device in self.discovered_devices:
            if existing_device["ip"] == ip:
                return  # Если устройство уже существует, прекращаем выполнение
        
        # Если устройство новое, добавляем его
        item = QListWidgetItem(device_info)
        self.device_list.addItem(item)
        self.discovered_devices.append({"name": name, "ip": ip})
        self.highlight_last_device()
        print(f"New device found: {device_info}")

    def select_device(self, item):
        # Сброс стиля для всех элементов
        for index in range(self.device_list.count()):
            self.device_list.item(index).setBackground(QColor(Qt.GlobalColor.transparent))
            self.device_list.item(index).setForeground(QColor(Qt.GlobalColor.black))

        # Установка стиля для выбранного элемента
        item.setBackground(QColor(0, 100, 0))  # Темно-зеленый фон
        item.setForeground(QColor(Qt.GlobalColor.white))

        # Извлечение IP и сохранение в файл
        ip = item.text().split(' at ')[1].strip()  # Убираем лишние пробелы

        # Сначала проверяем доступность устройства
        if self.web_browser.check_device_availability(ip.replace('http://', '').replace('/', '')):
            # Если устройство доступно, загружаем его
            with open("last_url.txt", 'w') as f:
                f.write(ip)
            self.web_browser.load_page(ip)
        else:
            # Если устройство недоступно, запускаем проверку
            with open("last_url.txt", 'w') as f:
                f.write(ip)
            self.web_browser.load_page(ip)  # 

    def clear_devices(self):
        self.device_list.clear()
        self.discovered_devices = []
        if os.path.exists("discovered_devices.json"):
            os.remove("discovered_devices.json")
        with open("last_url.txt", 'w') as f:
            f.write("")

    def delete_device(self):
        current_item = self.device_list.currentItem()
        if current_item:
            device_info = current_item.text()
            self.device_list.takeItem(self.device_list.row(current_item))
            name, ip = device_info.split(' at http://')
            ip = ip.strip('/')
            self.discovered_devices = [device for device in self.discovered_devices if device['ip'] != ip]
            with open("discovered_devices.json", 'w') as f:
                json.dump(self.discovered_devices, f, indent=4)
            with open("last_url.txt", 'r') as f:
                last_url = f.read().strip()
            if last_url == f"http://{ip}/":
                with open("last_url.txt", 'w') as f:
                    f.write("")


class WebBrowser(QMainWindow):
    def __init__(self):
        super().__init__()
        self.setWindowTitle("Settings")
        
        self.setGeometry(100, 100, 525, 980)
        self.setWindowIcon(QIcon("icon.ico"))
        
        # Устанавливаем флаг для скрытия заголовка окна
        self.setWindowFlags(Qt.WindowType.FramelessWindowHint)

        # Устанавливаем прозрачный фон для окна
        self.setAttribute(Qt.WidgetAttribute.WA_TranslucentBackground)

        
        try:
            storage_path = os.path.abspath("./browser_data")
            cache_path = os.path.abspath("./browser_cache")
            
            # Проверяем и создаем директории
            for path in [storage_path, cache_path]:
                if not os.path.exists(path):
                    os.makedirs(path, mode=0o777, exist_ok=True)
                else:
                    os.chmod(path, 0o777)
            
           
            self.profile = QWebEngineProfile("myprofile", self)
            self.profile.setPersistentStoragePath(storage_path)
            self.profile.setCachePath(cache_path)
            self.profile.setPersistentCookiesPolicy(QWebEngineProfile.PersistentCookiesPolicy.ForcePersistentCookies)
            
            # Устанавливаем мобильный User-Agent
            mobile_user_agent = "Mozilla/5.0 (Linux; Android 10; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36"
            self.profile.setHttpUserAgent(mobile_user_agent)
            
            # Настройки профиля
            self.profile.settings().setAttribute(QWebEngineSettings.WebAttribute.LocalStorageEnabled, True)
            self.profile.settings().setAttribute(QWebEngineSettings.WebAttribute.LocalContentCanAccessRemoteUrls, True)
            self.profile.settings().setAttribute(QWebEngineSettings.WebAttribute.LocalContentCanAccessFileUrls, True)
            
        except Exception as e:
            print(f"Ошибка при настройке профиля браузера: {e}")
            # Используем временный профиль если не удалось создать постоянный
            self.profile = QWebEngineProfile("myprofile", self)
        
        # Создаем браузер с настроенным профилем
        self.browser = QWebEngineView()
        self.browser.setPage(QWebEnginePage(self.profile, self.browser))
        self.browser.loadFinished.connect(self.get_page_color)
        self.initUI()

        self.browser.setContextMenuPolicy(Qt.ContextMenuPolicy.CustomContextMenu)
        self.browser.customContextMenuRequested.connect(self.show_context_menu)

        # Переменные для перемещения окна
        self.dragging = False
        self.offset = QPoint()

        # Флаг для отслеживания скрытия окна
        self.hidden_flag = False

        # Загружаем последний сохранённый URL
        self.load_last_url()

        # Подключаем сигнал изменения URL
        self.browser.urlChanged.connect(self.update_url)

        # Добавляем отступы для содержимого окна
        container = QWidget()
        container.setStyleSheet("background: transparent;")

        # Добавляем таймер для проверки доступности
        self.check_timer = QtCore.QTimer(self)
        self.check_timer.timeout.connect(self.check_current_device)
        self.current_checking_device = None

        # Загружаем последний сохранённый URL и проверяем доступность
        QtCore.QTimer.singleShot(0, self.check_initial_device)


    def initUI(self):
        """ строка ввода  """
        self.address_input = QLineEdit()
        self.address_input.setPlaceholderText("Enter IP Address")
        self.address_input.returnPressed.connect(self.load_page)
        self.address_input.setFixedWidth(150)  # ширина
        self.address_input.setStyleSheet("background-color: rgba(200, 0, 0, 0); border-radius: 9px; ")

        #  кнопка ообновить
        self.go_button = QPushButton()
        self.go_button.setFixedSize(20, 20)  #  размер кнопки
        self.go_button.setIcon(QIcon("refresh.png"))
        self.go_button.setStyleSheet("background-color: transparent; border: none;")  # Прозрачный фон
        self.go_button.clicked.connect(self.refresh_page)

        #  кнопка поиск
        self.scan_button = QPushButton()
        self.scan_button.setFixedSize(20, 20)  #  размер кнопки
        self.scan_button.setIcon(QIcon("scan.png"))
        self.scan_button.setStyleSheet("background-color: transparent; border: none;")  # Прозрачный фон
        self.scan_button.clicked.connect(self.open_menu)

        # чекбокс
        self.checkbox = QCheckBox()
        self.checkbox.stateChanged.connect(self.toggle_on_top)

        # Кнопка "Свернуть"
        self.minimize_button = QPushButton()
        self.minimize_button.setIcon(QIcon.fromTheme("window-minimize"))  # Иконка "Свернуть"
        self.minimize_button.setFixedSize(20, 20)  #  размер кнопки
        self.minimize_button.setIcon(QIcon("minimize.png"))
        self.minimize_button.setStyleSheet("background-color: transparent; border: none;")  # Прозрачный фон
        self.minimize_button.clicked.connect(self.showMinimized)  # Действие при нажатии

        # Кнопка "Закрыть"
        self.close_button = QPushButton()
        self.close_button.setFixedSize(20, 20)  #  размер кнопки
        self.close_button.setIcon(QIcon("close.png"))
        self.close_button.setStyleSheet("background-color: transparent; border: none;")  # Прозрачный фон
        self.close_button.clicked.connect(self.close)  # Действие при нажатии

        # Пустой виджет для отступа
        spacer_widget = QWidget()
        spacer_widget.setFixedSize(5, 0)  # Ширина отступа пикселей

        # Макет для кнопок
        buttons_layout = QHBoxLayout()
        buttons_layout.addWidget(spacer_widget)  # Добавляем отступ
        buttons_layout.addWidget(self.scan_button)
        buttons_layout.addWidget(self.go_button)
        buttons_layout.addWidget(self.checkbox)
        buttons_layout.addWidget(self.address_input)
        buttons_layout.addStretch()  # Добавляем растягивающееся пространство
        buttons_layout.addWidget(self.minimize_button)
        buttons_layout.addWidget(self.close_button)
        buttons_layout.addWidget(spacer_widget)  # Добавляем отступ

        # Макет для строки ввода 
        address_layout = QHBoxLayout()
        address_layout.addWidget(spacer_widget)  # Добавляем отступ слева
        address_layout.addWidget(spacer_widget)

        # Центральный виджет -- фон всего окна 
        central_widget = QWidget()
        central_widget.setStyleSheet("""
            background-color: rgba(255, 0, 0, 0); 
            border-radius: 25px;
        """)

        # Пустой виджет для отступа
        spacer_layoutt = QWidget()
        spacer_layoutt.setFixedSize(20, 0)  # Ширина отступа пикселей

        spacer_bottom_layoutt = QWidget()
        spacer_bottom_layoutt.setFixedSize(30, 14)  # Ширина отступа пикселей


        layout = QVBoxLayout()
        layout.addWidget(spacer_layoutt)  # Добавляем отступ сверху
        layout.addLayout(address_layout)  # поле адреса 
        layout.addLayout(buttons_layout)  # кнопки
        layout.addWidget(self.browser)    # браузер
        central_widget.setLayout(layout)  # 
        self.setCentralWidget(central_widget)
        layout.addWidget(spacer_bottom_layoutt)  #  отступ снизу

        # Применяем стили к виджету браузера
        self.browser.setStyleSheet("""
            QWebEngineView {
                background-color: rgba(255, 0, 0, 255);
                border-radius: 100px;
            }
        """)


    
    def open_menu(self):
        self.scan_dialog = ScanDialog(self)
        
        # Получаем размеры экрана
        screen = QApplication.primaryScreen().geometry()
        
        # Получаем текущее положение и размеры главного окна
        main_window_pos = self.geometry()
        
        # Вычисляем координаты для окна сканирования
        scan_x = 0
        
        # Проверяем, есть ли место слева от главного окна
        if (main_window_pos.left() >= self.scan_dialog.width() + 10):
            # Размещаем слева
            scan_x = main_window_pos.left() - self.scan_dialog.width() - 10
        # Проверяем, есть ли место справа от главного окна
        elif (main_window_pos.right() + self.scan_dialog.width() + 10 <= screen.width()):
            # Размещаем справа
            scan_x = main_window_pos.right() + 10
        else:
            # Если нет места ни слева, ни справа, размещаем по центру главного окна
            scan_x = main_window_pos.left() + (main_window_pos.width() - self.scan_dialog.width()) // 2
        
        # Вычисляем Y координату (выравнивание по вертикали с главным окном)
        scan_y = main_window_pos.top()
        
        # Устанавливаем позицию окна сканирования
        self.scan_dialog.move(scan_x, scan_y)
        self.scan_dialog.show()



    def show_about_dialog(self):
        QMessageBox.about(self, "О программе",
                          "Settings APP\n"
                          "Версия 0.06\n"
                          "Автор: VaniAI\n"
                          "Описание:\nпрограмма для поиска и отображения истройств в локальной сети\n"
                          "с установленной библиотекой Settinggs AlexGyver \n"
                          "тестированно на версии библиотеки v1.2.5"
                          )

    def get_page_color(self):
        #  JavaScript для получения значения --accent
        script = """
        (function() {
            try {
                // Сначала проверяем root элемент
                var root = document.documentElement;
                if (!root) return null;

                var style = window.getComputedStyle(root);
                if (!style) return null;

                var accentColor = style.getPropertyValue('--accent');
                if (accentColor) {
                    return accentColor.trim();
                }

                // Если не нашли в root, ищем в body
                var body = document.body;
                if (!body) return null;

                style = window.getComputedStyle(body);
                accentColor = style.getPropertyValue('--accent');

                return accentColor ? accentColor.trim() : null;
            } catch (e) {
                console.error('Error getting accent color:', e);
                return null;
            }
        })();
        """
        # Выполняем JavaScript и получаем результат
        self.browser.page().runJavaScript(script, self.update_border_color)

    def update_border_color(self, color):
        # Проверяем, получили ли мы цвет
        if color and color.startswith('#'):
            try:
                # Конвертируем HEX в RGB
                color = color.strip()
                r = int(color[1:3], 16)
                g = int(color[3:5], 16)
                b = int(color[5:7], 16)
                self.border_color = QColor(r, g, b, 150)  # Alpha = 150 для прозрачности
            except ValueError:
                # Если не удалось преобразовать цвет, используем цвет по умолчанию
                self.border_color = QColor(49, 113, 49, 150)
        else:
            # Если цвет не получен, используем цвет по умолчанию
            self.border_color = QColor(49, 113, 49, 150)

        self.update()  # Перерисовываем окно


    def paintEvent(self, event):
        # окно 
        painter = QPainter(self)
        painter.setRenderHint(QPainter.RenderHint.Antialiasing)
    
        # Получаем цвет рамки
        border_color = getattr(self, 'border_color', QColor(49, 113, 49, 150))
        
        # Рисуем фон с скруглёнными углами -- меню и заголовок
        brush = QBrush(QColor(28, 29, 34, 255))  # Цвет фона
        painter.setBrush(brush)
        painter.setPen(Qt.PenStyle.NoPen)  # Убираем границу
        painter.drawRoundedRect(self.rect().adjusted(9, 9, -9, -9), 15, 15)  # + Скругление углов 
    
        # рамка
        pen2 = QPen(border_color, 10)
        painter.setPen(pen2)
        painter.setBrush(Qt.BrushStyle.NoBrush)
        painter.drawRoundedRect(self.rect().adjusted(4, 4, -4, -4), 20, 20)

    
    def load_page(self, ip=None):
        url = ip if ip else self.address_input.text().strip()
        if url:
            # Сохраняем оригинальный URL для строки адреса
            original_url = url
            
            if not url.startswith("http://") and not url.startswith("https://"):
                url = "http://" + url
                original_url = url
                    
            # Проверяем доступность устройства
            device_ip = url.split('//')[1].split('/')[0]
            if not self.check_device_availability(device_ip):
                # Запускаем периодическую проверку
                self.current_checking_device = device_ip
                self.check_timer.start(5000)  # Проверка каждые 5 секунд
                
                # HTML заглушка для недоступного устройства
                error_html = f"""
                <!DOCTYPE html>
                <html lang="en" style="--accent: #ff0000;">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Device Offline</title>
                    <style>
                        body {{
                            background-color: #1c1d22;
                            color: #ffffff;
                            font-family: Arial, sans-serif;
                            display: flex;
                            flex-direction: column;
                            align-items: center;
                            justify-content: center;
                            height: 100vh;
                            margin: 0;
                        }}
                        .error-icon {{
                            font-size: 48px;
                            margin-bottom: 20px;
                        }}
                        .error-message {{
                            font-size: 24px;
                            text-align: center;
                        }}
                        .device-info {{
                            margin-top: 20px;
                            color: #888;
                        }}
                    </style>
                </head>
                <body>
                    <div class="error-icon">⚠️</div>
                    <div class="error-message">Устройство недоступно</div>
                    <div class="device-info">IP: {device_ip}</div>
                </body>
                </html>
                """
                self.browser.setHtml(error_html, QUrl(original_url))
                self.address_input.setText(original_url)
                
                # Запускаем анимацию
                self.start_border_animation()
            else:
                # Останавливаем проверку если устройство доступно
                self.check_timer.stop()
                self.current_checking_device = None
                self.stop_border_animation()
                self.browser.setUrl(QUrl(url))
                self.save_url(url)
                QtCore.QTimer.singleShot(1000, self.get_page_color)
                    
            self.hide()
            self.show()
            self.activateWindow()

    def closeEvent(self, event):
        # Закрываем окно сканирования, если оно открыто
        if hasattr(self, 'scan_dialog') and self.scan_dialog.isVisible():
            self.scan_dialog.close()
        
        # Создаем пустую страницу
        empty_page = QWebEnginePage(self)
        # Устанавливаем пустую страницу
        self.browser.setPage(empty_page)
        # Очищаем кэш
        self.profile.clearHttpCache()
        # Удаляем браузер
        self.browser.deleteLater()
        # Удаляем профиль
        self.profile.deleteLater()
        # Принимаем событие закрытия
        event.accept()
        # Вызываем родительский метод
        super().closeEvent(event)



    def update_url(self, url):
        """ Обновляет строку ввода при изменении URL """
        self.address_input.setText(url.toString())

    def save_url(self, url):
        """ Сохраняет URL в файл """
        with open("last_url.txt", "w") as file:
            file.write(url)

    def load_last_url(self):
        """ Загружает последний сохранённый URL из файла """
        try:
            with open("last_url.txt", 'r') as file:
                url = file.read().strip()
                if url:
                    self.browser.setUrl(QUrl(url))
                    self.address_input.setText(url)
        except FileNotFoundError:
            # Если файла нет, используем URL по умолчанию
            self.browser.setUrl(QUrl("http://192.168.1.132/"))
            self.address_input.setText("http://192.168.1.132/")

    # Переопределение событий мыши для перемещения окна
    def mousePressEvent(self, event: QMouseEvent):
        if event.button() == Qt.MouseButton.LeftButton:
            self.dragging = True
            self.offset = event.position().toPoint()

    def mouseMoveEvent(self, event: QMouseEvent):
        if self.dragging:
            self.move(self.pos() + event.position().toPoint() - self.offset)

    def mouseReleaseEvent(self, event: QMouseEvent):
        if event.button() == Qt.MouseButton.LeftButton:
            self.dragging = False

    def toggle_on_top(self):
        if self.checkbox.isChecked():
            self.setWindowFlags(self.windowFlags() | Qt.WindowType.WindowStaysOnTopHint)
        else:
            self.setWindowFlags(self.windowFlags() & ~Qt.WindowType.WindowStaysOnTopHint)
        self.show()

    def switch_theme(self):
        self.update()  # Перерисовываем виджет

    def resizeEvent(self, event):
        print("resizeEvent")
        super(WebBrowser, self).resizeEvent(event)

    def focusInEvent(self, event):
        print("focusInEvent")
        super(WebBrowser, self).focusInEvent(event)

    def focusOutEvent(self, event):
        print("focusOutEvent")
        super(WebBrowser, self).focusOutEvent(event)

    def kostyle(self):
        if self.checkbox.isChecked():
            self.setWindowFlags(self.windowFlags() & ~Qt.WindowType.WindowStaysOnTopHint)
            self.setWindowFlags(self.windowFlags() | Qt.WindowType.WindowStaysOnTopHint)
            self.show()
        else:
            self.setWindowFlags(self.windowFlags() | Qt.WindowType.WindowStaysOnTopHint)
            self.setWindowFlags(self.windowFlags() & ~Qt.WindowType.WindowStaysOnTopHint)
            self.show()

    def showEvent(self, event):
        print("showEvent")
        self.setWindowFlag(QtCore.Qt.WindowType.FramelessWindowHint)
        super(WebBrowser, self).showEvent(event)

    def event(self, event):
#         if event.type() == QtCore.QEvent.Type.WindowActivate:
#                 print("WindowActivate")
#                 if self.hidden_flag:
#         #                self.kostyle()
#                         self.hidden_flag = False  # Опускаем флаг после выполнения kostyle
#         elif event.type() == QtCore.QEvent.Type.WindowDeactivate:
#                 print("WindowDeactivate")
#                 self.hidden_flag = True  # Поднимаем флаг при сворачивании или уходе на второй план
        return super(WebBrowser, self).event(event)

    def show_context_menu(self, position):
        menu = QMenu(self)

        # Создаем подменю для устройств
        devices_menu = menu.addMenu("Переключить устройство")
        if os.path.exists("discovered_devices.json"):
            with open("discovered_devices.json", 'r') as f:
                devices = json.load(f)
                for device in devices:
                    device_action = devices_menu.addAction(f"{device['name']} ({device['ip']})")
                    # 
                    def make_handler(ip=device['ip']):
                        return lambda: self.load_page(f"http://{ip}/")
                    device_action.triggered.connect(make_handler())
        
        menu.addSeparator()
        
        # Поиск устройств
        scan_action = menu.addAction(QIcon("scan.png"), "Поиск устройств")
        scan_action.triggered.connect(self.open_menu)
        
        # Обновить страницу с проверкой доступности
        refresh_action = menu.addAction(QIcon("refresh.png"), "Обновить")
        refresh_action.triggered.connect(self.refresh_page)
        
        menu.addSeparator()
        

        on_top_action = menu.addAction("Поверх окон")
        on_top_action.setCheckable(True)
        on_top_action.setChecked(self.checkbox.isChecked())
        on_top_action.triggered.connect(self.checkbox.click)
        
        menu.addSeparator()
        
        copy_url_action = menu.addAction("Копировать URL")
        copy_url_action.triggered.connect(self.copy_current_url)
  
        about_action = menu.addAction("О программе")
        about_action.triggered.connect(self.show_about_dialog)        
        
        menu.addSeparator()
        
        minimize_action = menu.addAction(QIcon("minimize.png"), "Свернуть")
        minimize_action.triggered.connect(self.showMinimized)
        
        close_action = menu.addAction(QIcon("close.png"), "Закрыть")
        close_action.triggered.connect(self.close)
        
        menu.exec(self.browser.mapToGlobal(position))

    def copy_current_url(self):
        url = self.browser.url().toString()
        QApplication.clipboard().setText(url)

    def clear_browser_cache(self):
        self.profile.clearHttpCache()

    def check_device_availability(self, ip):
        """Синхронная проверка доступности для initial check"""
        try:
            response = requests.get(
                f"http://{ip}/settings?action=discover",
                timeout=0.3,
                verify=False
            )
            return response.status_code == 200
        except:
            return False
            
    def start_border_animation(self):
        if not hasattr(self, 'border_animation_timer'):
            self.border_animation_timer = QtCore.QTimer(self)
            self.border_animation_timer.timeout.connect(self.animate_border)
            self.border_animation_value = 0
        self.border_animation_timer.start(50)

    def stop_border_animation(self):
        if hasattr(self, 'border_animation_timer'):
            self.border_animation_timer.stop()
            self.border_color = getattr(self, 'saved_border_color', QColor(49, 113, 49, 150))
            self.update()

    def animate_border(self):
        import math
        self.border_animation_value = (self.border_animation_value + 5) % 360
        if not hasattr(self, 'saved_border_color'):
            self.saved_border_color = getattr(self, 'border_color', QColor(49, 113, 49, 150))
        
        factor = (math.sin(math.radians(self.border_animation_value)) + 1) / 2
        red_color = QColor(255, 0, 0, 150)
        
        r = int(red_color.red() * factor + self.saved_border_color.red() * (1 - factor))
        g = int(red_color.green() * factor + self.saved_border_color.green() * (1 - factor))
        b = int(red_color.blue() * factor + self.saved_border_color.blue() * (1 - factor))
        
        self.border_color = QColor(r, g, b, 150)
        self.update()

    def refresh_page(self):
        """Метод для обновления страницы"""
        current_url = self.address_input.text()
        if current_url:
            
            self.load_page(current_url)

    def check_current_device(self):
        """Периодическая проверка текущего устройства"""
        if not self.current_checking_device:
            return
            
        # Создаем воркер
        self.check_worker = CheckDeviceWorker(self.current_checking_device)
        
        def on_finished(is_available):
            if is_available:
                device_ip = self.current_checking_device
                self.check_timer.stop()
                self.current_checking_device = None
                self.load_page(f"http://{device_ip}/")
                self.check_worker.deleteLater()
        
        # Подключаем сигнал напрямую
        self.check_worker.finished.connect(on_finished)
        
        # Запускаем проверку
        self.check_worker.run()

    def check_initial_device(self):
        """Проверка доступности устройства при запуске"""
        try:
            with open("last_url.txt", 'r') as file:
                url = file.read().strip()
                if url:
                    self.load_page(url)
        except FileNotFoundError:
            self.load_page("http://192.168.1.132/")

class CheckDeviceWorker(QObject):
    finished = pyqtSignal(bool)
    
    def __init__(self, ip):
        super().__init__()
        self.ip = ip
        
    def run(self):
        try:
            response = requests.get(
                f"http://{self.ip}/settings?action=discover",
                timeout=0.3,
                verify=False
            )
            self.finished.emit(response.status_code == 200)
        except:
            self.finished.emit(False)

if __name__ == "__main__":
    app = QApplication(sys.argv)
    #app.setAttribute(Qt.ApplicationAttribute.AA_DontCreateNativeWidgetSiblings)  # Убираем баг с прозрачностью
    window = WebBrowser()
    window.show()
    sys.exit(app.exec())
