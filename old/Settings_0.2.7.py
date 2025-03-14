VERSION = "0.2.7"
NAME = "Settings" # Имя программы, можно переименовать, поменяется имя окна и имя программы в о программе
#NAME = "GLUONiCA" 
CUSTOM_NAME = "GLUONiCA" 
NAME_VARIABLES = ["GLUON", "gluon", "Gluon"]
# Если хотите использовать не просто свое имя, но и чтобы по умолчанию искало только ваши устройства то 
# NAME должно полностью совпадать с CUSTOM_NAME
# а в NAME_VARIABLES должны быть варианты с котрых может начинаться имя
# и по умолчанию будет искать только устройства с вашим именем, игнорируя все другие устройства
# поиск WLED по умолчанию включен 
# плюс свое окно о программе, в классе AboutDialog стоит обновить его текст
# а также в настройках появится чек бокс Искать только {NAME}
# при снятии которого будет искать все устройства с библиотекой Settings в сети 
# + иконка для приложения должна называться не icon.ico а g_icon.ico
 



import sys,json, requests, ipaddress, os, socket, ctypes, psutil, re,base64


from PyQt6.QtWidgets import ( QDialog, QLabel, QProgressBar, QListWidget, QMessageBox,
                             QListWidgetItem, QApplication, QMainWindow, QVBoxLayout, 
                             QHBoxLayout, QWidget, QLineEdit, QPushButton, QCheckBox, QMenu, 
                             QTextBrowser, QCompleter, QComboBox, QColorDialog, QFrame, QFormLayout
                             )
from PyQt6.QtCore import QThreadPool, QRunnable, pyqtSlot, pyqtSignal, QObject, QUrl, Qt, QPoint, QSize, QStringListModel, QRect, QTimer

from PyQt6.QtWebEngineWidgets import QWebEngineView

from PyQt6.QtGui import QIcon, QMouseEvent, QColor, QPainter, QPen, QBrush, QFont

from PyQt6 import QtCore

from PyQt6.QtWebEngineCore import QWebEngineProfile, QWebEnginePage, QWebEngineSettings


from PyQt6.QtWebChannel import QWebChannel



DARK_THEME = """
    /* Базовые стили для всех виджетов */
    QWidget {
        background-color: #202020;  /* Основной фон */
        color: #FFFFFF;  /* Основной цвет текста */
        font-family: "Segoe UI Variable";  /* Шрифт  */
        font-size: 14px;  /* Размер шрифта */
    }

    QAbstractItemView {
                border-radius: 10px;  
                background-color: transparent;
            } 
    QListView {
        background-color: #1c1d22;
        color: #FFFFFF;
        border: 1px solid #4A4A4A;
        border-radius: 10px;

        padding: 0px 5px;

        max-height: 550px;  /* Устанавливаем максимальную высоту списка */
        min-width: 160px;        

    }
    QListView::item {
        padding: 4px 10px;
        color: #FFFFFF;
    }
    QListView::item:selected {
        background-color: 27272f;  /* Начальный цвет по умолчанию */
        color: #FFFFFF;
        }


    /* Стили для кнопок (QPushButton) */
    QPushButton {
        background-color: #27272f;  /* Фон кнопки */
        color: #FFFFFF;  /* Цвет текста */
        border: 1px solid #4A4A4A;  /* Граница кнопки */
        border-radius: 4px;  /* Закругление углов */
         padding: 6px 6px;  Внутренние отступы 
    }
    QPushButton:hover {
        background-color: #4A4A4A;  /* Фон кнопки при наведении */
    }
    QPushButton:pressed {
        background-color: #27272f;  /* Фон кнопки при нажатии */
    }

    /* Стили для строки ввода (QLineEdit) */
    QLineEdit {
        background-color: #27272f;  /* Фон строки ввода */
        color: #FFFFFF;  /* Цвет текста */
        border: 1px solid #4A4A4A;  /* Граница */
        border-radius: 6px;  /* Закругление углов */
        /* padding: 6px;   Внутренние отступы */
    }
    QLineEdit:focus {
        border: 1px solid #29932e;  /* Граница при фокусе (акцентный цвет ) */
    }

    /* Стили для списка (QListWidget) */
    QListWidget {
        background-color: #27272f;  /* Фон списка */
        color: #FFFFFF;  /* Цвет текста */
        border: 1px solid #4A4A4A;  /* Граница */
        border-radius: 6px;  /* Закругление углов */
    }
    QListWidget::item {
        padding: 6px;  /* Внутренние отступы для элементов списка */
    }
    QListWidget::item:selected {
        background-color: #067100;  /* Фон выбранного элемента (акцентный цвет ) */
        color: #FFFFFF;  /* Цвет текста выбранного элемента */
    }

    /* Стили для прогресс-бара (QProgressBar) */
    QProgressBar {
        background-color: #2B2B2B;  /* Фон прогресс-бара */
        border: 1px solid #4A4A4A;  /* Граница */
        border-radius: 6px;  /* Закругление углов */
        text-align: center;  /* Выравнивание текста по центру */
    }
    QProgressBar::chunk {
        background-color: #0078D4;  /* Цвет заполненной части (акцентный цвет ) */
        border-radius: 6px;  /* Закругление углов */
    }

    /* Стили для меню (QMenu) */
    QMenu {
        background-color: #27272f;  /* Фон меню */
        color: #FFFFFF;  /* Цвет текста */
        border: 1px solid #4A4A4A;  /* Граница */
        border-radius: 6px;  /* Закругление углов */
        
       
    }
    QMenu::item {
        padding: 4px 20px 4px 10px;  /* Увеличиваем правый отступ (20px) для стрелочки */
        margin-right: 10px;  /* Отступ справа от пунктов меню */

    }
    QMenu::item:selected {
        background-color: #00612a;  /* Фон выбранного пункта (акцентный цвет ) */
        color: #000000;  /* Цвет текста выбранного пункта */

    }
    QMenu::icon {
    margin-left: 6px;  /* Отступ слева от значка */
    margin-right: 6px; /* Отступ справа от значка */
    }

    /* Стили для чекбокса (QCheckBox) */
    QCheckBox {
        color: #FFFFFF;  /* Цвет текста */
    }
    QCheckBox::indicator {
        width: 16px;  /* Ширина индикатора */
        height: 16px;  /* Высота индикатора */
        background-color: #2B2B2B;  /* Фон индикатора */
        border: 1px solid #4A4A4A;  /* Граница индикатора */
        border-radius: 4px;  /* Закругление углов */
    }
    QCheckBox::indicator:checked {
        background-color: #00612a;  /* Фон индикатора при выборе (акцентный цвет ) */
        border: 1px solid #009c44;  /* Граница индикатора при выборе */
    }

    /* Стили для полосы прокрутки (QScrollBar) */
    QScrollBar:vertical {
        background-color: #2B2B2B;  /* Фон полосы прокрутки */
        width: 12px;  /* Ширина полосы прокрутки */
    }
    QScrollBar::handle:vertical {
        background-color: #4A4A4A;  /* Фон ручки полосы прокрутки */
        border-radius: 6px;  /* Закругление углов */
    }
    QScrollBar::add-line:vertical,
    QScrollBar::sub-line:vertical {
        background: none;  /* Убираем стрелки */
    }

    /* Стили для вкладок (QTabWidget) */
    QTabWidget::pane {
        border: 1px solid #4A4A4A;  /* Граница вкладок */
        border-radius: 6px;  /* Закругление углов */
    }
    QTabBar::tab {
        background-color: #2B2B2B;  /* Фон вкладки */
        color: #FFFFFF;  /* Цвет текста */
        padding: 8px 16px;  /* Внутренние отступы */
        border: 1px solid #4A4A4A;  /* Граница вкладки */
        border-bottom: none;  /* Убираем нижнюю границу */
        border-top-left-radius: 6px;  /* Закругление верхних углов */
        border-top-right-radius: 6px;  /* Закругление верхних углов */
    }
    QTabBar::tab:selected {
        background-color: #00612a;  /* Фон выбранной вкладки (акцентный цвет ) */
        color: #FFFFFF;  /* Цвет текста выбранной вкладки */
    }
"""

def is_windows_dark_theme():
    try:
        # 0x0000 - светлая тема, 0x0001 - темная тема
        return ctypes.windll.dwmapi.DwmGetWindowAttribute(0, 20) == 1
    except Exception:
        # Если что-то пошло не так, считаем, что тема светлая
        return False



def get_all_local_subnets(prefix=24):
    local_subnets = []
    try:
        for interface, addrs in psutil.net_if_addrs().items():
            # Пропускаем только loopback
            if interface.startswith("lo"):
                continue
            for addr in addrs:
                if addr.family == socket.AF_INET:
                    ip = addr.address
                    # Исключаем localhost и самоназначаемые адреса
                    if ip != "127.0.0.1" and not ip.startswith("169.254"):
                        subnet = str(ipaddress.ip_network(f"{ip}/{prefix}", strict=False))
                        local_subnets.append(subnet)
        return local_subnets if local_subnets else ["127.0.0.1/24"]
    except Exception:
        return ["127.0.0.1/24"]

def get_subnet(ip, prefix=24):
    return ipaddress.ip_network(f"{ip}/{prefix}", strict=False)

def resizeEvent(self, event):
    super().resizeEvent(event)  # Вызываем родительский метод

    # Получаем текущий размер окна
    new_size = event.size()
    new_width = new_size.width()
    new_height = new_size.height()

    # Базовые размеры окна
    base_width = 525
    base_height = 980

    # Рассчитываем масштаб на основе ширины или высоты
    width_scale = new_width / base_width
    height_scale = new_height / base_height

    # Используем минимальный масштаб, чтобы сохранить пропорции
    new_scale = min(width_scale, height_scale)

    # Применяем новый масштаб к браузеру
    self.browser.setZoomFactor(new_scale)    

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
    result = pyqtSignal(object)
    value = 0  # Для флага остановки
#Сигналы для работы потоков
class DiscoverWorker(QRunnable):
    def __init__(self, ip, signals, stop_flag, gluon_only=True, wled_search=True):
        super().__init__()
        self.ip = ip
        self.signals = signals
        self.stop_flag = stop_flag
        self.timeout = 0.5
        self.gluon_only = gluon_only
        self.wled_search = wled_search
    
    def run(self):
        try:
            if self.stop_flag.value == 1:
                return
                
            url = f"http://{self.ip}/settings?action=discover"
            try:
                response = requests.get(url, timeout=self.timeout)
                if response.status_code == 200:
                    data = response.json()
                    name = data['name']
                    if NAME == CUSTOM_NAME and self.gluon_only:
                        if any(name.startswith(prefix) for prefix in NAME_VARIABLES):
                            self.signals.result.emit(f"{name} at http://{self.ip}/")
                    else:
                        self.signals.result.emit(f"{name} at http://{self.ip}/")
                    return 
            except requests.RequestException:
                pass

            if self.wled_search:
                url = f"http://{self.ip}/json/info"
                try:
                    response = requests.get(url, timeout=self.timeout)
                    if response.status_code == 200:
                        data = response.json()
                        name = data.get('name', f"WLED_{self.ip}")
                        self.signals.result.emit(f"{name} at http://{self.ip}/")
                except requests.RequestException:
                    pass
                    
        except Exception as e:
            print(f"Error in DiscoverWorker: {e}")
        finally:
            self.signals.progress.emit(1)



class CheckAvailabilityWorker(QRunnable):
    def __init__(self, url, signals, timeout):
        super().__init__()
        if not (url.startswith("http://") or url.startswith("https://")):
            url = f"http://{url}"
        self.url = url
        self.signals = signals
        self.timeout = timeout

    def run(self):
        try:
            response = requests.get(self.url, timeout=self.timeout, verify=False)
            is_available = response.status_code == 200
            print(f"Проверка {self.url}: статус {response.status_code}, доступно: {is_available}")
            self.signals.result.emit(is_available)
        except requests.RequestException as e:
            print(f"Устройство недоступно {self.url}: {e}")
            self.signals.result.emit(False)

class ScanDialog(QDialog):
    def __init__(self, web_browser, parent=None):
        super().__init__(parent)
        self.wled_search = web_browser.wled_search
        self.setWindowFlags(self.windowFlags() | Qt.WindowType.Dialog)
        self.web_browser = web_browser
        self.setWindowTitle("Поиск и редактирование")
        self.setGeometry(300, 300, 400, 500)
        
        accent_color = self.web_browser.accent_color if hasattr(self.web_browser, 'accent_color') else "#37a93c"

        if NAME == CUSTOM_NAME:
            self.setWindowIcon(QIcon("g_icon.ico"))
        else:
            self.setWindowIcon(QIcon("icon.ico"))

        self.layout = QVBoxLayout(self)

        self.subnet_label = QLabel("  Маска:")
        self.layout.addWidget(self.subnet_label)

        all_subnets = get_all_local_subnets()
        for subnet in all_subnets:
            SUBNET_HISTORY_MANAGER.add(subnet)

        self.subnet_input = QComboBox(self)
        self.subnet_input.setEditable(True)
        self.subnet_input.addItems(SUBNET_HISTORY_MANAGER.get_history())
        default_subnet = all_subnets[0] if all_subnets else "127.0.0.1/24"
        self.subnet_input.setCurrentText(default_subnet)

        if not SUBNET_HISTORY_MANAGER.get_history():
            SUBNET_HISTORY_MANAGER.add(default_subnet)
            SUBNET_HISTORY_MANAGER.add("192.168.1.0/24")


        self.completer = QCompleter(SUBNET_HISTORY_MANAGER.get_history(), self)
        self.completer.setCaseSensitivity(Qt.CaseSensitivity.CaseInsensitive)
        self.completer.setCompletionMode(QCompleter.CompletionMode.PopupCompletion)
        self.completer.setFilterMode(Qt.MatchFlag.MatchContains)
        self.subnet_input.lineEdit().setCompleter(self.completer)

        self.subnet_input.lineEdit().returnPressed.connect(self.add_new_input)
        self.subnet_input.lineEdit().mousePressEvent = self.show_completer
        self.layout.addWidget(self.subnet_input)

        self.timeout_label = QLabel("  Таймаут (мс):")
        self.layout.addWidget(self.timeout_label)

        

        self.timeout_input = QLineEdit(self)
        self.timeout_input.setText(str(int(self.web_browser.check_timeout * 1000)))
        self.timeout_input.setStyleSheet(f"""
                                            QLineEdit:focus {{
                                                border: 1px solid {accent_color};  
                                            }}
                                        """)
        self.timeout_input.textChanged.connect(self.update_timeout)
        self.layout.addWidget(self.timeout_input)

        self.button_layout = QHBoxLayout()

        self.scan_button = QPushButton("Сканировать")
        self.scan_button.clicked.connect(self.toggle_scan)
        self.button_layout.addWidget(self.scan_button)

        self.clear_button = QPushButton("Очистить")
        self.clear_button.clicked.connect(self.clear_devices)
        self.button_layout.addWidget(self.clear_button)

        self.delete_button = QPushButton("Удалить")
        self.delete_button.clicked.connect(self.delete_device)
        self.button_layout.addWidget(self.delete_button)

        self.layout.addLayout(self.button_layout)

        self.progress_bar = QProgressBar(self)
        self.layout.addWidget(self.progress_bar)

        self.device_list = QListWidget(self)
        
        self.device_list.setStyleSheet(f"""
            QListWidget {{
                background-color: #27272f;
                color: #FFFFFF;
                border: 1px solid #4A4A4A;
                border-radius: 6px;
            }}
            QListWidget::item {{
                padding: 6px;
            }}
            QListWidget::item:selected {{
                background-color: {accent_color};
                color: #000;
            }}
        """)
        self.device_list.itemClicked.connect(self.select_device)
        self.layout.addWidget(self.device_list)


        self.label_edit_layout = QHBoxLayout()
        self.edit_lable = QLabel("  Редактирование:")
        self.label_edit_layout.addWidget(self.edit_lable)
        self.layout.addLayout(self.label_edit_layout)


        self.edit_layout = QHBoxLayout()
        self.name_input = QLineEdit(self)
        self.name_input.setPlaceholderText(" Имя устройства")
        self.name_input.setStyleSheet(f"""
                                            QLineEdit:focus {{
                                                border: 1px solid {accent_color};  
                                            }}
                                        """)
        self.edit_layout.addWidget(self.name_input)
        
        self.ip_input = QLineEdit(self)
        self.ip_input.setPlaceholderText("  IP-адрес или URL")
        self.ip_input.setStyleSheet(f"""
                                            QLineEdit:focus {{
                                                border: 1px solid {accent_color};  
                                            }}
                                        """)
        self.edit_layout.addWidget(self.ip_input)
        
        self.layout.addLayout(self.edit_layout)
       
        self.button_edit_layout = QHBoxLayout()

        self.add_button = QPushButton("Добавить")
        self.add_button.clicked.connect(self.add_new_device)
        self.add_button.setStyleSheet(f"""
            QPushButton {{
                background-color: #27272f;
                color: #FFFFFF;
                border: 1px solid #4A4A4A;
                border-radius: 4px;
                padding: 6px;
            }}
            QPushButton:hover {{
                background-color: #4A4A4A;
            }}
        """)
        self.button_edit_layout.addWidget(self.add_button)
        
        self.apply_button = QPushButton("Применить")
        self.apply_button.clicked.connect(self.apply_changes)
        self.apply_button.setEnabled(False)
        self.apply_button.setStyleSheet(f"""
            QPushButton {{
                background-color: #27272f;
                color: #FFFFFF;
                border: 1px solid #4A4A4A;
                border-radius: 4px;
                padding: 6px;
            }}
            QPushButton:hover {{
                background-color: #4A4A4A;
            }}
            QPushButton:disabled {{
                background-color: #1c1d22;
                color: #888888;
            }}
        """)
        self.button_edit_layout.addWidget(self.apply_button)
        
        self.layout.addLayout(self.button_edit_layout)


        self.scanning = False
        self.threadpool = QThreadPool()
        self.stop_flag = WorkerSignals()
        self.stop_flag.value = 0
        self.total_ips = 0
        self.discovered_devices = []

        self.load_devices()
        self.highlight_last_device()
        self.original_devices = self.discovered_devices.copy()

        self.name_input.textChanged.connect(self.update_buttons_state)
        self.ip_input.textChanged.connect(self.update_buttons_state)




    def add_new_device(self):
        new_name = self.name_input.text().strip()
        new_url = self.ip_input.text().strip()
        if new_name and new_url:
            if not (new_url.startswith("http://") or new_url.startswith("https://")):
                new_url = "http://" + new_url
            for dev in self.discovered_devices:
                if dev["url"] == new_url:
                    QMessageBox.warning(self, "Ошибка", "Устройство с таким URL уже существует!")
                    return
            for dev in self.discovered_devices:
                if dev["name"] == new_name:
                    QMessageBox.warning(self, "Ошибка", "Устройство с таким именем уже существует!")
                    return
            self.discovered_devices.append({"name": new_name, "url": new_url})
            self.device_list.addItem(f"{new_name} at {new_url}")
            with open("discovered_devices.json", 'w') as f:
                json.dump(self.discovered_devices, f, indent=4)
            self.original_devices = self.discovered_devices.copy()
            self.name_input.clear()
            self.ip_input.clear()
            self.update_buttons_state()
        else:
            QMessageBox.warning(self, "Ошибка", "Введите имя и URL!")


    def update_list_style(self):
        accent_color = self.web_browser.accent_color if hasattr(self.web_browser, 'accent_color') else "#37a93c"
        brightness = self.web_browser.calculate_brightness(accent_color)
        text_color = "#FFFFFF" if brightness < 128 else "#000000"
        self.device_list.setStyleSheet(f"""
            QListWidget {{
                background-color: #27272f;
                color: #FFFFFF;
                border: 1px solid #4A4A4A;
                border-radius: 6px;
                font-weight: 400;                       
            }}
            QListWidget::item {{
                padding: 6px;
                font-weight: 400;                       
            }}
            QListWidget::item:selected {{
                background-color: {accent_color};
                color: {text_color};
                
            }}
        """)
        self.timeout_input.setStyleSheet(f"""
                                            QLineEdit:focus {{
                                                border: 1px solid {accent_color};  
                                            }}
                                        """)
        self.name_input.setStyleSheet(f"""
                                            QLineEdit:focus {{
                                                border: 1px solid {accent_color};  
                                            }}
                                        """)
        self.ip_input.setStyleSheet(f"""
                                            QLineEdit:focus {{
                                                border: 1px solid {accent_color};  
                                            }}
                                        """)



    def add_new_input(self):
        new_value = self.subnet_input.currentText().strip()
        SUBNET_HISTORY_MANAGER.add(new_value)
        if new_value not in [self.subnet_input.itemText(i) for i in range(self.subnet_input.count())]:
            self.subnet_input.insertItem(0, new_value)
        self.subnet_input.setCurrentText(new_value)
        model = self.completer.model()
        model.setStringList(SUBNET_HISTORY_MANAGER.get_history())

    def show_completer(self, event: QMouseEvent):
        self.completer.setCompletionPrefix("")
        self.subnet_input.lineEdit().completer().complete()
        QComboBox.mousePressEvent(self.subnet_input, event)

    def toggle_scan(self):
        if self.scanning:
            self.stop_scan()
        else:
            self.start_scan()

    def update_timeout(self):
        """Обновляет значение таймаута в настройках при изменении поля."""
        try:
            timeout_ms = float(self.timeout_input.text())
            if timeout_ms < 50:  # Минимальный таймаут
                timeout_ms = 50
                self.timeout_input.setText("50")
            self.web_browser.check_timeout = timeout_ms / 1000  # Переводим в секунды
            self.web_browser.save_settings()  # Сохраняем настройки
        except ValueError:
            # Если введено некорректное значение, оставляем текущее
            self.timeout_input.setText(str(int(self.web_browser.check_timeout * 1000)))
    def start_scan(self):
        self.scanning = True
        self.scan_button.setText("Стоп")
        self.stop_flag.value = 0
        if os.path.exists("discovered_devices.json"):
            with open("discovered_devices.json", 'r') as f:
                self.discovered_devices = json.load(f)
        self.scan_network()

    def scan_network(self):
        subnet_mask = self.subnet_input.currentText()
        try:
            timeout = float(self.timeout_input.text()) / 1000
        except ValueError:
            timeout = 0.5
        try:
            network = ipaddress.ip_network(subnet_mask)
        except ValueError as e:
            QMessageBox.warning(self, "Warning", f"Неверная маска подсети: {e}")
            print(f"Неверная маска подсети: {e}")
            return

        self.total_ips = network.num_addresses
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
            worker = DiscoverWorker(
                ip, 
                signals, 
                self.stop_flag, 
                gluon_only=self.web_browser.gluon_only,
                wled_search=self.wled_search
            )
            worker.timeout = timeout
            self.threadpool.start(worker)

    def stop_scan(self):
        self.scanning = False
        self.scan_button.setText("Сканировать")
        self.stop_flag.value = 1

    def update_progress(self, value):
        self.completed_ips += value
        self.progress_bar.setValue(self.completed_ips)
        if self.completed_ips >= self.total_ips or self.stop_flag.value == 1:
            self.scan_button.setText("Сканировать")
            self.scanning = False



    def add_device(self, device_info):
        name, url = device_info.split(' at ')
        for existing_device in self.discovered_devices:
            if existing_device["url"] == url:
                self.highlight_last_device()
                return

        name_count = sum(1 for d in self.discovered_devices if d["name"].startswith(name + "(") or d["name"] == name)
        unique_name = f"{name}({name_count})" if name_count > 0 else name

        item = QListWidgetItem(f"{unique_name} at {url}")
        self.device_list.addItem(item)
        self.discovered_devices.append({"name": unique_name, "url": url})
        self.highlight_last_device()
        print(f"Нашли новое устройство: {device_info}")

        self.web_browser.device_list = self.web_browser.load_devices_for_autocomplete()
        self.web_browser.completer.setModel(QStringListModel(self.web_browser.device_list))

        with open("discovered_devices.json", 'w') as f:
            json.dump(self.discovered_devices, f, indent=4)

    def load_devices_from_file(self):
        if os.path.exists("discovered_devices.json"):
            with open("discovered_devices.json", 'r') as f:
                return json.load(f)
        return []

    def load_devices(self):
        self.discovered_devices = self.load_devices_from_file()
        for device in self.discovered_devices:
            self.device_list.addItem(f"{device['name']} at {device['url']}")
        self.highlight_last_device()


    def select_device(self, item):
        device_info = item.text()
        name_part, url = device_info.split(' at ')
        base_name = name_part.split('(')[0].strip()

        devices = self.load_devices_from_file()
        devices = [d for d in devices if d['url'] != url]
        devices.insert(0, {"name": base_name, "url": url})
        with open("discovered_devices.json", 'w') as f:
            json.dump(devices, f, indent=4)

        self.discovered_devices = devices

        def handle_availability(is_available):
            if is_available:
                self.web_browser.load_page(url)
            else:
                self.web_browser.load_page(url)
            self.highlight_last_device()

        device_ip = url.split('//')[1].split('/')[0]
        full_url = url if url.startswith("http://") or url.startswith("https://") else f"http://{device_ip}"
        self.web_browser.check_device_availability(full_url, handle_availability)
        #self.web_browser.check_device_availability(device_ip, handle_availability)

        self.name_input.setText(name_part)
        self.ip_input.setText(url)
        font = QFont()
        font.setWeight(QFont.Weight.Bold)
        item.setFont(font)
        QTimer.singleShot(400, self.update_list_style)
        self.update_buttons_state()

    def apply_changes(self):
        current_item = self.device_list.currentItem()
        if current_item:
            new_name = self.name_input.text().strip()
            new_url = self.ip_input.text().strip()
            if not (new_url.startswith("http://") or new_url.startswith("https://")):
                new_url = "http://" + new_url
            if new_name and new_url:
                device_info = current_item.text()
                orig_name, orig_url = device_info.split(' at ', 1)
                orig_url = orig_url.strip()

                current_device = next((dev for dev in self.discovered_devices if dev["url"] == orig_url), None)
                if not current_device:
                    QMessageBox.warning(self, "Ошибка", "Не удалось найти устройство в списке!")
                    return
                for dev in self.discovered_devices:
                    if dev["url"] == new_url and dev != current_device:
                        QMessageBox.warning(self, "Ошибка", "Устройство с таким URL уже существует!")
                        return
                for dev in self.discovered_devices:
                    if dev["name"] == new_name and dev != current_device:
                        QMessageBox.warning(self, "Ошибка", "Устройство с таким именем уже существует!")
                        return
                current_device["name"] = new_name
                current_device["url"] = new_url
                current_item.setText(f"{new_name} at {new_url}")
                with open("discovered_devices.json", 'w') as f:
                    json.dump(self.discovered_devices, f, indent=4)
                self.original_devices = self.discovered_devices.copy()
                self.device_list.clear()
                self.load_devices()
                self.update_list_style()
                self.web_browser.device_list = self.web_browser.load_devices_for_autocomplete()
                self.web_browser.completer.setModel(QStringListModel(self.web_browser.device_list))
                self.web_browser.load_page(new_url)
                self.update_buttons_state()
            else:
                QMessageBox.warning(self, "Ошибка", "Введите имя и URL!")
        else:
            QMessageBox.warning(self, "Ошибка", "Выберите устройство для редактирования!")




    def get_darker_color(self, color, factor):
        """Возвращает более тёмный оттенок цвета"""
        if isinstance(color, str) and color.startswith('#'):
            from PyQt6.QtGui import QColor
            qcolor = QColor(color)
            return qcolor.darker(factor).name()
        return color

    def highlight_last_device(self):

        pass

    def clear_devices(self):
        self.device_list.clear()
        self.discovered_devices = []
        if os.path.exists("discovered_devices.json"):
            os.remove("discovered_devices.json")
        self.update_buttons_state()

        # Показываем заглушку в главном окне, если устройств больше нет
        self.web_browser.show_no_devices_placeholder()

    def delete_device(self):
        current_item = self.device_list.currentItem()
        if current_item:
            device_info = current_item.text()
            self.device_list.takeItem(self.device_list.row(current_item))
            name, url = device_info.split(' at ')
            self.discovered_devices = [device for device in self.discovered_devices if device['url'] != url]
            with open("discovered_devices.json", 'w') as f:
                json.dump(self.discovered_devices, f, indent=4)
            self.highlight_last_device()
            self.name_input.clear()
            self.ip_input.clear()
            self.web_browser.load_last_url()
            self.update_buttons_state()


    def update_buttons_state(self):
        accent_color = self.web_browser.accent_color if hasattr(self.web_browser, 'accent_color') else "#37a93c"
        current_item = self.device_list.currentItem()
        name = self.name_input.text().strip()
        url = self.ip_input.text().strip()

        if url and not (url.startswith("http://") or url.startswith("https://")):
            url = "http://" + url

        # Состояние кнопки "Добавить"
        add_enabled = False
        if name and url:
            url_exists = any(dev["url"] == url for dev in self.discovered_devices)
            add_enabled = not url_exists

        self.add_button.setEnabled(add_enabled)
        if add_enabled:
            self.add_button.setStyleSheet(f"""
                QPushButton {{
                    background-color: {accent_color};
                    color: #FFFFFF;
                    border: 1px solid #4A4A4A;
                    border-radius: 4px;
                    padding: 6px;
                }}
                QPushButton:hover {{
                    background-color: {self.get_darker_color(accent_color, 120)};
                }}
            """)
        else:
            self.add_button.setStyleSheet("""
                QPushButton {
                    background-color: #27272f;
                    color: #FFFFFF;
                    border: 1px solid #4A4A4A;
                    border-radius: 4px;
                    padding: 6px;
                }
                QPushButton:hover {
                    background-color: #4A4A4A;
                }
                QPushButton:disabled {
                    background-color: #1c1d22;
                    color: #888888;
                }
            """)

        # кнопка применить
        apply_enabled = False
        if current_item and name and url:
            device_info = current_item.text()
            orig_name, orig_url = device_info.split(' at ', 1)
            orig_url = orig_url.strip()
            apply_enabled = (orig_name != name or orig_url != url)  

        self.apply_button.setEnabled(apply_enabled)
        if apply_enabled:
            self.apply_button.setStyleSheet(f"""
                QPushButton {{
                    background-color: {accent_color};
                    color: #FFFFFF;
                    border: 1px solid #4A4A4A;
                    border-radius: 4px;
                    padding: 6px;
                }}
                QPushButton:hover {{
                    background-color: {self.get_darker_color(accent_color, 120)};
                }}
            """)
        else:
            self.apply_button.setStyleSheet("""
                QPushButton {
                    background-color: #27272f;
                    color: #FFFFFF;
                    border: 1px solid #4A4A4A;
                    border-radius: 4px;
                    padding: 6px;
                }
                QPushButton:hover {
                    background-color: #4A4A4A;
                }
                QPushButton:disabled {
                    background-color: #1c1d22;
                    color: #888888;
                }
            """)

        self.update_list_style()

# Класс для управления историей масок сетей
class SubnetHistoryManager:
    def __init__(self):
        self.history = []
        self.load()

    def add(self, value):
        if value and value not in self.history:
            self.history.insert(0, value)
            self.save()

    def load(self):
        try:
            with open("subnet_history.json", "r") as f:
                self.history = json.load(f)
        except FileNotFoundError:
            self.history = []

    def save(self):
        with open("subnet_history.json", "w") as f:
            json.dump(self.history, f)

    def get_history(self):
        return self.history


SUBNET_HISTORY_MANAGER = SubnetHistoryManager()


class AuthDialog(QDialog):
    def __init__(self, url, realm, saved_login="", saved_password="", parent=None):
        super().__init__(parent)
        self.setWindowTitle("Требуется авторизация")
        self.setModal(False)
        
        layout = QFormLayout()
        
        self.url_label = QLabel(f"{url}")
        layout.addRow(self.url_label)
        
        self.realm_label = QLabel(f"{realm}")
        layout.addRow(self.realm_label)
        
        self.login_input = QLineEdit()
        self.login_input.setPlaceholderText("Логин")
        self.login_input.setText(saved_login)  # Предзаполняем логин
        layout.addRow("Логин:", self.login_input)
        
        self.password_input = QLineEdit()
        self.password_input.setEchoMode(QLineEdit.EchoMode.Password)
        self.password_input.setPlaceholderText("Пароль")
        self.password_input.setText(saved_password)  # Предзаполняем пароль
        layout.addRow("Пароль:", self.password_input)
        
        self.remember_checkbox = QCheckBox("Запомнить меня")
        self.remember_checkbox.setChecked(bool(saved_login and saved_password))  # Включаем, если данные есть
        layout.addRow(self.remember_checkbox)
        
        self.ok_button = QPushButton("ОК")
        self.ok_button.clicked.connect(self.accept)
        layout.addRow(self.ok_button)
        
        self.cancel_button = QPushButton("Отмена")
        self.cancel_button.clicked.connect(self.reject)
        layout.addRow(self.cancel_button)
        
        self.setLayout(layout)
        
    def get_credentials(self):
        return self.login_input.text(), self.password_input.text(), self.remember_checkbox.isChecked()


class WebBrowser(QMainWindow):
    def __init__(self):
        super().__init__()
        self.setWindowTitle(NAME)

        self.check_timeout = 0.3
        self.update_available = False
        self.latest_version = None
        self.github_repo = "TonTon-Macout/APP-for-AlexGyver-Settings"  # Репо

        # Версия
        self.check_latest_version()


        # Получаем размеры 
        screen = QApplication.primaryScreen().geometry()
        # Ограничения размеров окна
        self.MIN_WINDOW_WIDTH = 200
        self.MIN_WINDOW_HEIGHT = 200
        self.MAX_WINDOW_WIDTH = screen.width()
        self.MAX_WINDOW_HEIGHT = screen.height() 
        
        screen_width = screen.width()
        screen_height = screen.height()

        # изначально размер настроен для 2K экрана 
        base_width = 525
        base_height = 980
        scale_factor = min(screen_width / 2560, screen_height / 1440)
        default_width = int(base_width * scale_factor)
        default_height = int(base_height * scale_factor)
        default_zoom_factor = scale_factor

        # Устанавливаем значения по умолчанию
        self.default_width = default_width
        self.default_height = default_height
        self.default_zoom_factor = default_zoom_factor
        
        # Область растягивания
        self.resizing = False
        self.resize_offset = QPoint()
        self.resize_area_size = 20

       
        self.show_names = True
        self.gluon_only = True
        self.window_width = default_width
        self.window_height = default_height
        self.zoom_factor = default_zoom_factor
        self.wled_search = True  # По умолчанию включен поиск WLED
        self.default_border_color = QColor(49, 113, 49, 150)  # Цвет рамки
        self.default_back_color = QColor(28, 29, 34, 255)

        self.custom_colors_enabled = False
        self.custom_border_color = self.default_border_color
        self.custom_back_color = self.default_back_color
         # Загружаем настройки из settings.json
        if os.path.exists("settings.json"):
            with open("settings.json", 'r') as f:
                settings = json.load(f)
                self.show_names = settings.get("show_names", self.show_names)
                self.gluon_only = settings.get("gluon_only", self.gluon_only)
                self.wled_search = settings.get("wled_search", self.wled_search)
                self.window_width = max(self.MIN_WINDOW_WIDTH, min(settings.get("window_width", self.window_width), self.MAX_WINDOW_WIDTH))
                self.window_height = max(self.MIN_WINDOW_HEIGHT, min(settings.get("window_height", self.window_height), self.MAX_WINDOW_HEIGHT))
                self.zoom_factor = settings.get("zoom_factor", self.zoom_factor)
                self.custom_colors_enabled = settings.get("custom_colors_enabled", False)
                self.check_timeout = settings.get("check_timeout", 0.3)

                border_color = settings.get("custom_border_color", 
                                         [self.default_border_color.red(),
                                          self.default_border_color.green(),
                                          self.default_border_color.blue(),
                                          self.default_border_color.alpha()])
                back_color = settings.get("custom_back_color",
                                       [self.default_back_color.red(),
                                        self.default_back_color.green(),
                                        self.default_back_color.blue(),
                                        self.default_back_color.alpha()])
                self.custom_border_color = QColor(*border_color)
                self.custom_back_color = QColor(*back_color)
       
        self.credentials = {} # учетные данные для веб 
       # Применяем загруженные размеры окна
        self.resize(int(self.window_width), int(self.window_height))
        self.browser = QWebEngineView()  
        self.browser.setZoomFactor(self.zoom_factor)  # Применяем масштаб
       
       # Устанавливаем начальные цвета
        self.border_color = self.default_border_color
        self.back_color = self.default_back_color

       
        self.load_settings()
        self.migrate_discovered_devices() # Преобразуем в новый формат хранения

        # Устанавливаем начальные размеры окна и масштаб



        if NAME == CUSTOM_NAME:
            self.setWindowIcon(QIcon("g_icon.ico"))
        else:
            self.setWindowIcon(QIcon("icon.ico"))
        
        # Устанавливаем флаг для скрытия заголовка окна
        self.setWindowFlags(Qt.WindowType.FramelessWindowHint)

        # Устанавливаем прозрачный фон для окна
        self.setAttribute(Qt.WidgetAttribute.WA_TranslucentBackground)

        
        try:
            storage_path = os.path.abspath("./browser_data")
            cache_path   = os.path.abspath("./browser_cache")
            
            # Проверяем и создаем
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
        #self.browser = QWebEngineView() переехал выше чтобы применить масштаб
        self.browser.setPage(QWebEnginePage(self.profile, self.browser))
        self.browser.loadFinished.connect(self.get_colors)
        # Сигнал авторизации
        self.browser.page().authenticationRequired.connect(self.handle_authentication)

        self.accent_color = "#37a93c"
        self.initUI()
        

        


        self.browser.setContextMenuPolicy(Qt.ContextMenuPolicy.CustomContextMenu)
        self.browser.customContextMenuRequested.connect(self.show_context_menu)

        # Устанавливаем начальный масштаб браузера
        self.browser.setZoomFactor(self.zoom_factor)
        # Переменные для перемещения окна
        self.dragging = False
        self.offset = QPoint()

        # Флаг для отслеживания скрытия окна
        self.hidden_flag = False


        self.load_last_url()
        # сигнал изменения URL
        self.browser.urlChanged.connect(self.update_url)

        # 
        container = QWidget()
        container.setStyleSheet("background: transparent;")

        # Настройка QWebChannel
       # self.channel = QWebChannel(self)
       # self.channel.registerObject("pybridge", self)  # Регистрируем сам объект WebBrowser
       # self.browser.page().setWebChannel(self.channel)

        self.bridge = Bridge(self)  # Передаём WebBrowser как родителя
        self.channel = QWebChannel(self)
        self.channel.registerObject("pybridge", self.bridge)  # Регистрируем только Bridge
        self.browser.page().setWebChannel(self.channel)

        # Таймер для проверки доступности
        self.check_timer = QtCore.QTimer(self)
        self.check_timer.timeout.connect(self.check_current_device)
        self.current_checking_device = None
        

        # Проверяем доступность
        QtCore.QTimer.singleShot(0, self.check_initial_device)




    def check_initial_device(self):
        # Проверка доступности устройства при запуске
        if os.path.exists("discovered_devices.json"):
            with open("discovered_devices.json", 'r') as f:
                devices = json.load(f)
                if devices:
                    url = devices[0]['url']
                    self.load_page(url)
                else:
                    self.show_no_devices_placeholder()
        else:
            self.show_no_devices_placeholder()

    def migrate_discovered_devices(self):
        #Преобразование старого формата в новый
        if os.path.exists("discovered_devices.json"):
            with open("discovered_devices.json", 'r') as f:
                try:
                    devices = json.load(f)
                except json.JSONDecodeError:
                    devices = []  # Если файл повреждён, начнём с пустого списка

            # Проверяем, есть ли старый формат
            updated = False
            for device in devices:
                if "ip" in device and "url" not in device:
                    # Преобразуем старый формат в новый
                    device["url"] = f"http://{device['ip']}/"
                    del device["ip"]  # Удаляем старый ключ
                    updated = True
                elif "url" not in device and "name" in device:
                    # Если нет ни ip, ни url, но есть name, пропускаем 
                    continue

            # Сохраняем обновлённый файл, если были изменения
            if updated:
                with open("discovered_devices.json", 'w') as f:
                    json.dump(devices, f, indent=4)
                print("Поменян формат файла discovered_devices на новую версию")

    def load_settings(self):
        if os.path.exists("settings.json"):
            with open("settings.json", 'r') as f:
                settings = json.load(f)
                self.show_names = settings.get("show_names", True)
                self.gluon_only = settings.get("gluon_only", False)
                self.wled_search = settings.get("wled_search", True)
                self.window_width = settings.get("window_width", 800)
                self.window_height = settings.get("window_height", 600)
                self.zoom_factor = settings.get("zoom_factor", 1.0)
                self.custom_colors_enabled = settings.get("custom_colors_enabled", False)
                self.custom_border_color = QColor(*settings.get("custom_border_color", [255, 255, 255, 255]))
                self.custom_back_color = QColor(*settings.get("custom_back_color", [0, 0, 0, 255]))
                self.check_timeout = settings.get("check_timeout", 0.3)
                # Учетные данные
                if "credentials" in settings:
                    for url, cred in settings["credentials"].items():
                        try:
                            login = base64.b64decode(cred["login"]).decode('utf-8')
                            password = base64.b64decode(cred["password"]).decode('utf-8')
                            self.credentials[url] = {"login": login, "password": password}
                        except Exception as e:
                            print(f"Ошибка расшифровки учетных данных для {url}: {e}")
        else:
            self.credentials = {}

    def save_settings(self):
        settings = {
            "show_names": self.show_names,
            "gluon_only": self.gluon_only,
            "wled_search": self.wled_search,
            "window_width": self.window_width,
            "window_height": self.window_height,
            "zoom_factor": self.zoom_factor,
            "custom_colors_enabled": self.custom_colors_enabled,
            "check_timeout": self.check_timeout,
            "custom_border_color": [self.custom_border_color.red(), 
                                   self.custom_border_color.green(), 
                                   self.custom_border_color.blue(), 
                                   self.custom_border_color.alpha()],
            "custom_back_color": [self.custom_back_color.red(), 
                                 self.custom_back_color.green(), 
                                 self.custom_back_color.blue(), 
                                 self.custom_back_color.alpha()],
            "credentials": {}
        }
        for url, cred in self.credentials.items():
            try:
                settings["credentials"][url] = {
                    "login": base64.b64encode(cred["login"].encode('utf-8')).decode('utf-8'),
                    "password": base64.b64encode(cred["password"].encode('utf-8')).decode('utf-8')
                }
            except Exception as e:
                print(f"Ошибка кодирования учетныйх данных для {url}: {e}")
        try:
            with open("settings.json", 'w') as f:
                json.dump(settings, f, indent=4)
            print("Настройки сохранены")
        except Exception as e:
            print(f"Ошибка при сохранении настроек: {e}")



    def handle_authentication(self, url, authenticator):
        url_str = url.toString()
        saved_login = self.credentials.get(url_str, {}).get("login", "")
        saved_password = self.credentials.get(url_str, {}).get("password", "")
        
        print(f"Запрос авторизации: {url_str}")
        dialog = AuthDialog(url_str, authenticator.realm(), saved_login, saved_password, self)
        
      
        if dialog.exec() == QDialog.DialogCode.Accepted:
            login, password, remember = dialog.get_credentials()
            #print(f"Введено: login={login}, password={password}, remember={remember}")
            
            if login and password:  # Проверяем что введено
                authenticator.setUser(login)
                authenticator.setPassword(password)
           
            
                if remember and login and password:
                    print(f"Сохранили учетные данные {url_str}")
                    self.credentials[url_str] = {"login": login, "password": password}
                    self.save_settings()  # Сохраняем все настройки
            else:
             # Если пользователь нажал ок но не ввел данные
                self.show_device_unavailable_placeholder(url_str)
                authenticator.setUser("")  # Отменяем
      
        else:
            authenticator.setUser("")
            print("Авторизация отменена")
            self.show_device_unavailable_placeholder(url_str)
            authenticator.setUser("")  





    def show_device_unavailable_placeholder(self, url):
       
        error_html = f"""
        <!DOCTYPE html>
        <html lang="en" style="--accent: #150000;">
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>No Devices Found</title>
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
                    transition: background-color 2s ease;
                }}
                .snowman-container {{
                    font-size: 64px;
                    margin-bottom: 20px;
                    color: #140000;
                    width: 10px;
                    height: 10px;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    position: relative;
                    transition: color 1s ease;
                }}
                .snowman-container .eyes {{
                    display: none;
                    position: absolute;
                    top: -7px;
                    width: 15px;
                    height: 10px;
                    background-color: #ff0000;
                    border-radius: 50%;
                    animation: look 5s infinite;
                    z-index: -1;
                }}
                .snowman-container .moon {{
                    display: none;
                    position: absolute;
                    top: -30px;
                    left: 20px;
                    width: 20px;
                    height: 20px;
                    background-color: #ffcc008f;
                    box-shadow: 0px 0px 7px 0px #ff0000de;
                    border-radius: 50%;
                    z-index: -2;
                    filter: blur(1px);
                }}
                @keyframes look {{
                    0% {{ opacity: 1; filter: brightness(70%); }}
                    20% {{ opacity: 0.3; filter: brightness(10%); }}
                    30% {{ opacity: 0.3; filter: brightness(10%); }}
                    40% {{ opacity: 1; filter: brightness(60%); }}
                    45% {{ opacity: 0.8; filter: brightness(50%); }}
                    60% {{ opacity: 0.2; filter: brightness(20%); }}
                    80% {{ opacity: 1; filter: brightness(70%); }}
                    85% {{ opacity: 0.4; filter: brightness(30%); }}
                    100% {{ opacity: 1; filter: brightness(70%); }}
                }}
                .message {{
                    margin-top: 20px;
                    margin-bottom: 20px;
                    font-size: 24px;
                    text-align: center;
                    color: #5e5e5e;
                }}
                .error-icon {{
                    font-size: 48px;
                    
                    cursor: pointer;
                    transition: opacity 0.5s ease;
                }}
                .snowman {{
                    display: none;
                    opacity: 0;
                    transition: opacity 0.5s ease;
                }}
                .device-info {{
                                margin-bottom: 7px;
                                color: #888;
                            }}
                .device-info2 {{
                    margin-bottom: 7px;
                    color: #888;
                }}
            </style>
        </head>
        <body>
            <div class="snowman-container">
                <div class="moon"></div>
                <div class="eyes"></div>
                <span class="snowman">⛇</span>
            </div>
            <div class="error-icon">🔒</div>
            <div class="message">Устройство недоступно</div>
            <div class="device-info">это устройство требует авторизации</div>
            <div class="device-info2">обновите страницу и попробуйте снова</div>

            <script>
                const errorIcon = document.querySelector('.error-icon');
                let clickCount = 0;

                errorIcon.addEventListener('click', () => {{
                    clickCount++;
                    if (clickCount === 2){{
                                   const message = document.querySelector('.message')
                                   message.style.display = 'none';

                                   const device_info = document.querySelector('.device-info')
                                   device_info.style.display = 'none';

                                   const device_info2 = document.querySelector('.device-info2')
                                   device_info2.style.display = 'none';
                                }}
                    if (clickCount === 10) {{
                        errorIcon.style.opacity = '0';
                        document.body.style.backgroundColor = '#000000';

                         setTimeout(() => {{
                              const eyes = document.querySelector('.eyes');
                              eyes.style.display = 'block';
                         
                         }}, 3000);

                        setTimeout(() => {{
                            const snowman = document.querySelector('.snowman');
                            snowman.style.display = 'inline';
                            setTimeout(() => {{
                                snowman.style.opacity = '1';
                            }}, 10);

                            setTimeout(() => {{
                                const snowmanContainer = document.querySelector('.snowman-container');
                                snowmanContainer.style.color = '#200000';


                            }}, 5000);
                        }}, 2000);
                    }}
                }});
            </script>
        </body>
        </html>
        """

        self.browser.setHtml(error_html, QUrl(url))
        self.address_input.setText(url if not self.show_names else self.get_name_by_url(url))





    def initUI(self):
        # строка ввода  
        self.address_input = QLineEdit()
        self.address_input.setPlaceholderText("Введите URL или IP")
        self.address_input.returnPressed.connect(self.load_page)
        #self.address_input.setMinimumWidth(170)  # ширина
        self.address_input.setStyleSheet("background-color: rgba(200, 0, 0, 0); border-radius: 9px; ")
        self.address_input.returnPressed.connect(self.load_page)
        self.address_input.textChanged.connect(self.adjust_address_input_width)  # сигнал
        self.adjust_address_input_width()  # начальная ширина
        # Aвтодополнение с устройствами
        self.device_list = self.load_devices_for_autocomplete()
        self.completer = QCompleter(self.device_list, self)
        self.completer.setCaseSensitivity(Qt.CaseSensitivity.CaseInsensitive)
        self.completer.setCompletionMode(QCompleter.CompletionMode.PopupCompletion)
        self.completer.setFilterMode(Qt.MatchFlag.MatchContains)
        self.address_input.setCompleter(self.completer)




        # Cигнал для загрузки выбранного устройства из автодополнения
        self.completer.activated.connect(self.load_selected_device)

        # Переопределяем mousePressEvent для показа полного списка при клике
        self.address_input.mousePressEvent = self.show_completer
      
        if os.path.exists("discovered_devices.json"):
                with open("discovered_devices.json", 'r') as f:
                    devices = json.load(f)
                    if devices:
                        if self.show_names:
                            self.address_input.setText(devices[0]['name'])
                        else:
                            self.address_input.setText(devices[0]['url'])
        


        #  кнопка ообновить
        self.go_button = QPushButton()
        self.go_button.setFixedSize(20, 20)  #  размер кнопки
        self.go_button.setIcon(QIcon("refresh.png"))
        self.go_button.setStyleSheet("background-color: transparent; border: none; margin-right:4px;")  # Прозрачный фон
        self.go_button.clicked.connect(self.refresh_page)

        #  кнопка поиск
        self.scan_button = QPushButton()
        self.scan_button.setFixedSize(20, 20)  #  размер кнопки
        self.scan_button.setIcon(QIcon("scan.png"))
        self.scan_button.setStyleSheet("background-color: transparent; border: none;")  # Прозрачный фон
        self.scan_button.clicked.connect(self.open_menu)

        # чекбокс поверх окон
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
        buttons_layout.addWidget(spacer_widget)  #  отступ
        buttons_layout.addWidget(self.scan_button)
        buttons_layout.addWidget(self.go_button)
        buttons_layout.addWidget(self.checkbox)
        buttons_layout.addWidget(self.address_input)
        buttons_layout.addStretch(0)  # Растягивающееся пространство
        buttons_layout.addWidget(self.minimize_button)
        buttons_layout.addWidget(self.close_button)
        buttons_layout.addWidget(spacer_widget)  # Отступ

        # Макет для строки ввода 
        address_layout = QHBoxLayout()
        address_layout.addWidget(spacer_widget)  #  отступ слева
        address_layout.addWidget(spacer_widget)

        # Центральный виджет -- фон всего окна 
        central_widget = QWidget()
        central_widget.setStyleSheet("""
            background-color: rgba(0, 0, 0, 0); 
            border-radius: 25px;
        """)

        # Пустой виджет для отступа
        spacer_layoutt = QWidget()
        spacer_layoutt.setFixedSize(20, 0)  # Ширина отступа пикселей

        spacer_bottom_layoutt = QWidget()
        spacer_bottom_layoutt.setFixedSize(30, 14)  # Ширина отступа пикселей


        layout = QVBoxLayout()
        layout.addWidget(spacer_layoutt)  #  отступ сверху
        layout.addLayout(address_layout)  # поле адреса 
        layout.addLayout(buttons_layout)  # кнопки
        layout.addWidget(self.browser)    # браузер
        central_widget.setLayout(layout)  # 
        self.setCentralWidget(central_widget)
        layout.addWidget(spacer_bottom_layoutt)  #  отступ снизу

        # стили виджета браузера неработают
        self.browser.setStyleSheet("""
            QWebEngineView {
                background-color: rgba(0, 0, 0, 255);
                border-radius: 100px;
            }
        """)
     
    def adjust_address_input_width(self):
        # устанавливаем ширину адресной строки, 
        self.address_input.setMinimumWidth(190)  # ширина фиксированно-минимальная
       
        # ширина зависит от длины текста 
        #text = self.address_input.text() or self.address_input.placeholderText()  # плейсхолдер
        #font_metrics = self.address_input.fontMetrics()  # Метрики шрифта
        #width = font_metrics.horizontalAdvance(text) + 20  # +20 для отступов
        #min_width = 100  # Минимальная ширина для удобства
        #max_width = self.width() - 100  
        #self.address_input.setMinimumWidth(max(min_width, min(width, max_width)))

    def check_latest_version(self):
        try:
            # Запрос к GitHub API для получения последнего релиза
            url = f"https://api.github.com/repos/{self.github_repo}/releases/latest"
            response = requests.get(url, timeout=5)
            if response.status_code == 200:
                data = response.json()
                self.latest_version = data["tag_name"].lstrip("v")  # Убираем 'v' из версии
                if self.latest_version != VERSION:
                    self.update_available = True
                    print(f"Доступна новая версия: {self.latest_version} (текущая: {VERSION})")
                else:
                    print(f"Уже последняя версия: {VERSION}")
            else:
                print(f"Ошибка проверки версии: {response.status_code}")
        except Exception as e:
            print(f"Не удалось проверить версию на GitHub: {e}")

    def open_latest_release_page(self):
        if self.latest_version:
            url = f"https://github.com/{self.github_repo}/releases/tag/v{self.latest_version}"
            import webbrowser
            webbrowser.open(url)



#    def toggle_show_names(self):
#        #Переключаем показ имён и сохранить
#        self.show_names = not self.show_names
#        with open("settings.json", 'w') as f:
#            json.dump({"show_names": self.show_names}, f, indent=4)
#        print(f"показывать имена в строке: {self.show_names}")



    def load_devices_for_autocomplete(self):
        device_list = []
        self.device_map = {}
        name_count = {}
        if os.path.exists("discovered_devices.json"):
            with open("discovered_devices.json", 'r') as f:
                devices = json.load(f)
                for device in devices:
                    base_name = device['name']
                    url = device['url']
                    if base_name in name_count:
                        name_count[base_name] += 1
                        display_name = f"{base_name}({name_count[base_name]})"
                    else:
                        name_count[base_name] = 0
                        display_name = base_name
                    self.device_map[display_name] = url
                    if self.show_names:
                        device_list.append(display_name)
                    else:
                        device_list.append(url)
        return device_list


    def load_selected_device(self, text):
        if text:
            if self.show_names and text in self.device_map:
                url = self.device_map[text]
                self.load_page(url)
            else:
                self.load_page(text)
  
    def show_completer(self, event: QMouseEvent):
        self.completer.setCompletionPrefix("")  # Префикс для показа всех устройств
        self.address_input.completer().complete()  #  Список
        QLineEdit.mousePressEvent(self.address_input, event)

    def open_menu(self):
        self.scan_dialog = ScanDialog(self)
        self.scan_dialog.wled_search = self.wled_search

        screen = QApplication.primaryScreen().geometry()
        main_window_pos = self.geometry()
        scan_x = 0
        if main_window_pos.left() >= self.scan_dialog.width() + 10:
            scan_x = main_window_pos.left() - self.scan_dialog.width() - 10
        elif main_window_pos.right() + self.scan_dialog.width() + 10 <= screen.width():
            scan_x = main_window_pos.right() + 10
        else:
            scan_x = main_window_pos.left() + (main_window_pos.width() - self.scan_dialog.width()) // 2
        scan_y = main_window_pos.top()
        self.scan_dialog.move(scan_x, scan_y)
        self.scan_dialog.setWindowFlags(self.scan_dialog.windowFlags() | Qt.WindowType.WindowStaysOnTopHint)  # флаг "поверх всех"
        self.scan_dialog.show()
        # Обновить список устройств после закрытия ScanDialog
        self.scan_dialog.exec()
        self.device_list = self.load_devices_for_autocomplete()
        self.completer.setModel(QStringListModel(self.device_list))


    def update_line_edit_style(self, color):
        if color and color.startswith('#'):
            try:
                # Конвертируем HEX в RGB
                color = color.strip()
                r = int(color[1:3], 16)
                g = int(color[3:5], 16)
                b = int(color[5:7], 16)
                accent_color = QColor(r, g, b)
                self.address_input.setStyleSheet(f"""
                    QLineEdit {{
                        background-color: transparent;
                        color: #FFFFFF;
                        border: none;
                        border-radius: 6px;
                       /* padding: 6px; */
                    }}
                    QLineEdit:focus {{
                        border: 1px solid {accent_color.name()};
                    }}
                """)
            except ValueError:
                # Если не удалось преобразовать цвет, используем цвет по умолчанию
                self.address_input.setStyleSheet(f"""
                    QLineEdit {{
                        background-color: transparent;
                        color: #FFFFFF;
                        border: none;
                        border-radius: 6px;
                       /* padding: 6px; */
                    }}
                    QLineEdit:focus {{
                        border: 1px solid {accent_color.name()};
                    }}
                """)
        else:
            # Если цвет не получен, используем цвет по умолчанию
            self.address_input.setStyleSheet("""
                QLineEdit {
                    background-color: #2B2B2B;
                    color: #FFFFFF;
                    border: 1px solid #4A4A4A;
                    border-radius: 6px;
                   /* padding: 6px; */
                }
                QLineEdit:focus {
                    border: 1px solid #0078D4;
                }
            """)

    def update_menu_style(self, color):
        if color and color.startswith('#'):  # Проверяем, что цвет в формате HEX
            try:
                # Устанавливаем стили для контекстного меню
                menu_style = f"""

                    QMenu::item:selected {{
                        background-color: {color};
                        color: #000000;
                        border-radius: 6px;
                    }}
                """
                self.setStyleSheet(menu_style)  # Применяем стили к главному окну
            except Exception as e:
                print(f"Ошибка при обновлении стилей меню: {e}")
                # Используем стили по умолчанию в случае ошибки
                self.setStyleSheet("""

                    QMenu::item:selected {
                        background-color: #0078D4;
                        color: #000000;
                        border-radius: 6px;
                    }
                """)
        else:
            # Если цвет не получен, используем стили по умолчанию
            self.setStyleSheet("""

                QMenu::item:selected {
                    background-color: #0078D4;
                    color: #000000;
                    border-radius: 6px;
                             
                }
            """)


    def update_checkbox_style(self, color):
        if color and color.startswith('#'):  # Проверяем, что цвет в формате HEX
            try:
                checkbox_style = f"""
                    QCheckBox {{
                        color: #FFFFFF;  /* Цвет текста */
                    }}
                    QCheckBox::indicator {{
                        width: 16px;  /* Ширина индикатора */
                        height: 16px;  /* Высота индикатора */
                        background-color: #2B2B2B;  /* Фон индикатора */
                        border: 1px solid #4A4A4A;  /* Граница индикатора */
                        border-radius: 4px;  /* Закругление углов */
                    }}
                    QCheckBox::indicator:checked {{
                        background-color: {color};  /* Фон индикатора при выборе (акцентный цвет) */
                        border: 1px solid {color};  /* Граница индикатора при выборе */
                        color: #FFFFFF;  /* Цвет текста (галочки) */
                    }}
                """
                self.setStyleSheet(checkbox_style)  # Применяем стили к главному окну
            except Exception as e:
                
                # Используем стили по умолчанию в случае ошибки
                self.setStyleSheet("""
                    QCheckBox {
                        color: #FFFFFF;
                    }
                    QCheckBox::indicator {
                        width: 16px;
                        height: 16px;
                        background-color: #2B2B2B;
                        border: 1px solid #4A4A4A;
                        border-radius: 4px;
                    }
                    QCheckBox::indicator:checked {
                        background-color: #00612a;
                        border: 1px solid #009c44;
                        color: #FFFFFF;  /* Цвет текста (галочки) */
                    }
                """)
        else:
            # Если цвет не получен, используем стили по умолчанию
            self.setStyleSheet("""
                QCheckBox {
                    color: #FFFFFF;
                }
                QCheckBox::indicator {
                    width: 16px;
                    height: 16px;
                    background-color: #2B2B2B;
                    border: 1px solid #4A4A4A;
                    border-radius: 4px;
                }
                QCheckBox::indicator:checked {
                    background-color: #00612a;
                    border: 1px solid #009c44;
                    color: #FFFFFF;  /* Цвет текста (галочки) */
                }
            """)



    def show_about_dialog(self):
        about_dialog = AboutDialog()
        about_dialog.setWindowFlags(about_dialog.windowFlags() | Qt.WindowType.WindowStaysOnTopHint)  #  флаг "поверх всех"
        about_dialog.exec()

    def get_colors(self):
        self.get_accent_color()
        self.get_back_color()
        if not self.custom_colors_enabled:
            # Если свои цвета не включены и цвета не найдены, используем по умолчанию
            if not hasattr(self, 'accent_color') or not self.accent_color:
                self.handle_accent_color(None)
            if not hasattr(self, 'back_color') or not self.back_color:
                self.update_back_color(None)

    def get_back_color(self):
        # JavaScript для получения значения --back
        script = """
        (function() {
            try {
                // Сначала проверяем root элемент
                var root = document.documentElement;
                if (!root) return null;

                var style = window.getComputedStyle(root);
                if (!style) return null;

                var backColor = style.getPropertyValue('--back');
                if (backColor) {
                    return backColor.trim();
                }

                // Если не нашли в root, ищем в body
                var body = document.body;
                if (!body) return null;

                style = window.getComputedStyle(body);
                backColor = style.getPropertyValue('--back');

                return backColor ? backColor.trim() : null;
            } catch (e) {
                console.error('Error getting back color:', e);
                return null;
            }
        })();
        """
        # Выполняем JavaScript и получаем результат
        self.browser.page().runJavaScript(script, self.update_back_color)
  

    def get_accent_color(self):
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
        self.browser.page().runJavaScript(script, self.handle_accent_color)
  
    def update_border_color(self, color):
        if self.custom_colors_enabled:
            self.border_color = self.custom_border_color
        elif color and color.startswith('#'):
            try:
                color = color.strip()
                r = int(color[1:3], 16)
                g = int(color[3:5], 16)
                b = int(color[5:7], 16)
                self.border_color = QColor(r, g, b, self.custom_border_color.alpha())
            except ValueError:
                self.border_color = self.custom_border_color  # Используем цвет из настроек
        else:
            self.border_color = self.custom_border_color  # Используем цвет из настроек
        self.update()  # Перерисовываем окно

    def handle_accent_color(self, color):
        # Обрабатываем полученный цвет
        if self.custom_colors_enabled:
            self.accent_color = self.custom_border_color  # Используем QColor
            self.border_color = self.custom_border_color
            self.back_color = self.custom_back_color
        else:
            if color and isinstance(color, str) and color.startswith('#'):
                self.accent_color = color
                self.update_border_color(color)  # Обновить цвет рамки из полученного цвета
            else:
                self.accent_color = self.custom_border_color.name(QColor.NameFormat.HexRgb)
                self.border_color = self.custom_border_color
                self.back_color = self.custom_back_color

        # Определяем яркость акцентного цвета и выбираем цвет текста
        brightness = self.calculate_brightness(self.accent_color)
        text_color = "#FFFFFF" if brightness < 128 else "#000000"  # Порог 128

        # Обновляем DARK_THEME с учетом цвета текста
        updated_dark_theme = DARK_THEME.replace(
            "QListView::item:selected { background-color: 27272f; color: #FFFFFF; }",
            f"QListView::item:selected {{ background-color: {self.accent_color}; color: {text_color}; border-radius: 6px; }}"
        ).replace(
            "QListWidget::item:selected { background-color: #067100; color: #FFFFFF; }",
            f"QListWidget::item:selected {{ background-color: {self.accent_color}; color: {text_color}; }}"
        ).replace(
            "QMenu::item:selected { background-color: #00612a; color: #000000; }",
            f"QMenu::item:selected {{ background-color: {self.accent_color}; color: {text_color}; border-radius: 6px; }}"
        )

        QApplication.instance().setStyleSheet(updated_dark_theme)

        # Обновляем динамические стили
        combined_style = self.get_combined_styles(self.accent_color, text_color)
        self.setStyleSheet(combined_style)
        self.update()  # Перерисовываем окно

    def calculate_brightness(self, color):
        """Вычисляет яркость цвета в формате HEX (#RRGGBB). Возвращает значение от 0 до 255."""
        if isinstance(color, QColor):
            r, g, b = color.red(), color.green(), color.blue()
        elif isinstance(color, str) and color.startswith('#'):
            r = int(color[1:3], 16)
            g = int(color[3:5], 16)
            b = int(color[5:7], 16)
        else:
            return 128  # Значение по умолчанию, если цвет некорректен
        # Формула luma
        return 0.299 * r + 0.587 * g + 0.114 * b

        
    def get_combined_styles(self, color, text_color="#000000"):
        # объединяем стили для всех элементов котрые меняют цвет динамически 
        # если по отдельности применять стили то они перезаписывают друг друга

        if isinstance(color, QColor):
            color = color.name(QColor.NameFormat.HexRgb)

        if not color or not color.startswith('#'):
            color = "#0078D4"  # Цвет по умолчанию, если цвет не задан
    
        # Стили для QLineEdit
        line_edit_style = f"""
            QLineEdit {{
                background-color: #2B2B2B;
                color: #FFFFFF;
                border: none;
                border-radius: 6px;
                 /*padding: 6px; */
            }}
            QLineEdit:focus {{
                border: 1px solid {color};
            }}
        """
    
        # Стили для QCheckBox
        checkbox_style = f"""
            QCheckBox {{
                color: #FFFFFF;
            }}
            QCheckBox::indicator {{
                width: 16px;
                height: 16px;
                background-color: #2B2B2B;
                border: 1px solid #4A4A4A;
                border-radius: 4px;
            }}
            QCheckBox::indicator:checked {{
                background-color: {color};
                border: 1px solid {color};
                color: #FFFFFF;  /* Цвет текста (галочки) */
            }}
        """
    
        # Стили для QMenu
        menu_style = f"""

            QMenu::item:selected {{
                background-color: {color};
                color: {text_color};
                border-radius: 6px;
               
            }}
        """


    
        # Объединяем все стили в одну строку
        combined_style = line_edit_style + checkbox_style + menu_style 
        return combined_style        

        

    def update_back_color(self, back_color):
        if self.custom_colors_enabled:
            self.back_color = self.custom_back_color
        elif back_color:
            self.back_color = QColor(back_color)
        else:
            self.back_color = self.custom_back_color  # Используем цвет из настроек
        self.update()  # Перерисовываем окно


    def paintEvent(self, event):
        # окно 
        painter = QPainter(self)
        painter.setRenderHint(QPainter.RenderHint.Antialiasing)
    
        # Получаем цвет рамки и фона
        border_color      = getattr(self, 'border_color', QColor(49, 113, 49, 150))
        back_color        = getattr(self, 'back_color', QColor(28, 29, 34, 255))  # Цвет по умолчанию
        back_color_top = getattr(self, 'back_color', QColor(28, 29, 34, 255))  # Цвет по умолчанию
        # Рисуем фон с скруглёнными углами -- меню и заголовок
        #brush = QBrush(QColor(28, 29, 34, 255))  # Цвет фона
        back_color =  back_color_top.darker(150)
        brush = QBrush(back_color_top) # Цвет фона
        painter.setBrush(brush)
        painter.setPen(Qt.PenStyle.NoPen)  # Убираем границу
        painter.drawRoundedRect(self.rect().adjusted(9, 90, -9, -9), 15, 15)  # + Скругление углов 

        brush_bottom = QBrush(back_color) # Цвет фона
        painter.setBrush(brush_bottom)
        painter.setPen(Qt.PenStyle.NoPen)  # Убираем границу
        painter.drawRoundedRect(self.rect().adjusted(9, 9, -9, -90), 15, 15)  # + Скругление углов 
    
        # рамка
        pen2 = QPen(border_color, 10)
        painter.setPen(pen2)
        painter.setBrush(Qt.BrushStyle.NoBrush)
        painter.drawRoundedRect(self.rect().adjusted(4, 4, -4, -4), 20, 20)


        # точка 
        point_size = 6  # Диаметр точки
        painter.setBrush(QBrush(border_color))  # Цвет точки 
        painter.setPen(Qt.PenStyle.NoPen)       # Убираем контур
        painter.drawEllipse(
            self.width() - point_size,  # x-координата центра точки
            self.height() - point_size, # y-координата центра точки
            point_size,                                # ширина (диаметр)
            point_size                                 # высота (диаметр)
        )



    def load_page(self, url=None):

        input_url = url if url else self.address_input.text().strip()
        if input_url:
            if not (input_url.startswith("http://") or input_url.startswith("https://")):
                input_url = "http://" + input_url
            original_url = input_url

            device_host = original_url.split('//')[1].split('/')[0]
            is_ip = bool(re.match(r'^\d+\.\d+\.\d+\.\d+$', device_host))

            devices = self.load_devices_from_file()
            if original_url in [d['url'] for d in devices]:
                device = next(d for d in devices if d['url'] == original_url)
                devices = [d for d in devices if d['url'] != original_url]
                devices.insert(0, device)
                with open("discovered_devices.json", 'w') as f:
                    json.dump(devices, f, indent=4)
                self.device_list = self.load_devices_for_autocomplete()
                self.completer.setModel(QStringListModel(self.device_list))
            elif is_ip:
                try:
                    response = requests.get(f"{original_url}settings?action=discover", timeout=0.3, verify=False)
                    if response.status_code == 200:
                        data = response.json()
                        name = data.get("name", f"Unknown_{device_host}")
                        self.update_discovered_devices(name, original_url)
                except requests.RequestException:
                    pass

            def handle_availability(is_available):
                print(f"Доступность {original_url}: {is_available}, is_ip: {is_ip}")
                if not is_available and is_ip:
                    self.current_checking_device = original_url  # Сохраняем полный URL
                    self.check_timer.start(5000)
                    self.browser.settings().setAttribute(QWebEngineSettings.WebAttribute.JavascriptEnabled, True)
                    error_html = f"""
                    <!DOCTYPE html>
                    <html lang="en" >
                    <head>
                        <meta charset="UTF-8">
                        <meta name="viewport" content="width=device-width, initial-scale=1.0">
                        <title>No Devices Found</title>
                        <script type="text/javascript" src="qrc:///qtwebchannel/qwebchannel.js"></script>
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
                                transition: background-color 2s ease;
                            }}
                            .snowman-container {{
                                font-size: 64px;
                                margin-bottom: 20px;
                                color: #150000;
                                width: 10px;
                                height: 10px;
                                display: flex;
                                align-items: center;
                                justify-content: center;
                                position: relative;
                                transition: color 1s ease;
                            }}
                            .snowman-container .eyes {{
                                display: none;
                                position: absolute;
                                top: -5px;
                                width: 15px;
                                height: 10px;
                                background-color: #ff0000;
                                border-radius: 50%;
                                animation: look 5s infinite;
                                z-index: -1;
                            }}
                            .snowman-container .moon {{
                                display: none;
                                position: absolute;
                                top: -25px;
                                left: 20px;
                                width: 20px;
                                height: 20px;
                                background-color: #ffcc008f;
                                box-shadow: 0px 0px 7px 0px #ff0000de;
                                border-radius: 50%;
                                z-index: -2;
                                filter: blur(1px);
                            }}
                            @keyframes look {{
                                0% {{ opacity: 1; filter: brightness(70%); }}
                                20% {{ opacity: 0.3; filter: brightness(10%); }}
                                30% {{ opacity: 0.3; filter: brightness(10%); }}
                                40% {{ opacity: 1; filter: brightness(60%); }}
                                45% {{ opacity: 0.8; filter: brightness(50%); }}
                                60% {{ opacity: 0.2; filter: brightness(20%); }}
                                80% {{ opacity: 1; filter: brightness(70%); }}
                                85% {{ opacity: 0.4; filter: brightness(30%); }}
                                100% {{ opacity: 1; filter: brightness(70%); }}
                            }}
                            .message {{
                               
                                font-size: 24px;
                                text-align: center;
                                color: #5e5e5e;
                            }}
                            .error-icon {{
                                font-size: 52px;
                                margin-bottom: 20px;
                                cursor: pointer;
                                transition: opacity 0.5s ease;
                            }}
                            .snowman {{
                                display: none;
                                opacity: 0;
                                transition: opacity 0.5s ease;
                            }}
                        </style>
                    </head>
                    <body>
                        <div class="snowman-container">
                            <div class="moon"></div>
                            <div class="eyes"></div>
                            <span class="snowman">⛇</span>
                        </div>
                        <div class="error-icon">⚠️</div>
                        <div class="message">Устройство недоступно</div>

                        <script>
                            let bridge;
                            new QWebChannel(qt.webChannelTransport, function(channel) {{
                                bridge = channel.objects.pybridge;
                            }});  


                            const errorIcon = document.querySelector('.error-icon');
                            let clickCount = 0;
                            errorIcon.addEventListener('click', () => {{
                                clickCount++;
                                if (clickCount === 2){{
                                   const message = document.querySelector('.message')
                                   message.style.display = 'none';
                                   if (bridge) {{
                                       bridge.stop_border_animation();
                                   }}
                                }}
                                if (clickCount === 10) {{
                                    errorIcon.style.opacity = '0';
                                    document.body.style.backgroundColor = '#000000';

                                    setTimeout(() => {{
                                        const snowman = document.querySelector('.snowman');
                                        snowman.style.display = 'inline';
                                        setTimeout(() => {{
                                            snowman.style.opacity = '1';
                                        }}, 10);
                                        setTimeout(() => {{
                                                const eyes = document.querySelector('.eyes');
                                                eyes.style.display = 'block';
                                            }}, 2000);

                                        setTimeout(() => {{
                                            const snowmanContainer = document.querySelector('.snowman-container');
                                            snowmanContainer.style.color = '#200000';

                                            setTimeout(() => {{
                                                const eyes = document.querySelector('.eyes');
                                                eyes.style.display = 'block';
                                            }}, 2000);
                                        }}, 7000);
                                    }}, 5000);
                                }}
                            }});
                        </script>
                    </body>
                    </html>
                    """
                    self.browser.setHtml(error_html, QUrl(original_url))
                    self.address_input.setText(original_url if not self.show_names else self.get_name_by_url(original_url))
                    self.start_border_animation()
                else:
                    self.check_timer.stop()
                    self.current_checking_device = None
                    self.stop_border_animation()
                    self.browser.setUrl(QUrl(original_url))
                    self.address_input.setText(original_url if not self.show_names else self.get_name_by_url(original_url))
                    QtCore.QTimer.singleShot(1000, self.get_accent_color)

                self.hide()
                self.show()
                self.activateWindow()

            if is_ip:
                self.check_device_availability(original_url, handle_availability)
            else:
                handle_availability(True)


    def get_name_by_url(self, url):
        devices = self.load_devices_from_file()
        for device in devices:
            if device['url'] == url:
                return device['name']
        return url


    def load_devices_from_file(self):
        if os.path.exists("discovered_devices.json"):
            with open("discovered_devices.json", 'r') as f:
                return json.load(f)
        return []



    def update_discovered_devices(self, name, url):
        devices = self.load_devices_from_file()
        devices = [d for d in devices if d['url'] != url]
        name_count = sum(1 for d in devices if d["name"].startswith(name + "(") or d["name"] == name)
        unique_name = f"{name}({name_count})" if name_count > 0 else name
        devices.insert(0, {"name": unique_name, "url": url})
        with open("discovered_devices.json", 'w') as f:
            json.dump(devices, f, indent=4)
        self.device_list = self.load_devices_for_autocomplete()
        self.completer.setModel(QStringListModel(self.device_list))



    def closeEvent(self, event):
        # Закрываем окно сканирования, если оно открыто
        self.save_settings()
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
        # Обновить URL в поле ввода
        self.address_input.setText(url.toString())

    def load_last_url(self):
        if os.path.exists("discovered_devices.json"):
            with open("discovered_devices.json", 'r') as f:
                devices = json.load(f)
                if devices:
                    url = devices[0]['url']
                    self.browser.setUrl(QUrl(url))
                    self.address_input.setText(devices[0]['name'] if self.show_names else url)
                else:
                    self.show_no_devices_placeholder()
        else:
            self.show_no_devices_placeholder()

    
    def show_no_devices_placeholder(self):
        no_devices_html = """
        <!DOCTYPE html>
        <html lang="en" style="--accent: #150000;">
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>No Devices Found</title>
            <style>
                body {
                    background-color: #1c1d22;
                    color: #ffffff;
                    font-family: Arial, sans-serif;
                    display: flex;
                    flex-direction: column;
                    align-items: center;
                    justify-content: center;
                    height: 100vh;
                    margin: 0;
                    transition: background-color 3s ease; /* Плавный переход фона за 3 секунды */
                }
                .info-icon {
                    font-size: 64px;
                    margin-bottom: 20px;
                    color: #150000;
                    width: 10px;
                    height: 10px;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    position: relative;
                }
                .info-icon .background-circle {
                    display: none; /* Скрыт по умолчанию */
                    position: absolute;
                    top: -7px;
                    width: 15px;
                    height: 10px;
                    background-color: #ff0000;
                    border-radius: 50%;
                    animation: look 5s infinite;
                    z-index: -1;
                }
                .info-icon .background-moon {
                    display: none; /* Скрыт по умолчанию */
                    position: absolute;
                    top: -30px;
                    left: 20px;
                    width: 20px;
                    height: 20px;
                    background-color: #ffcc008f;
                    box-shadow: 0px 0px 7px 0px #ff0000de;
                    border-radius: 50%;
                    z-index: -2;
                    filter: blur(1px);
                    animation: moon 45s infinite;
                }
                @keyframes moon {
                    0% { opacity: 0; filter: brightness(0%); }
                    80% { opacity: 0.2; filter: brightness(90%); }
                    85% { opacity: 0.4; filter: brightness(90%); }
                    88% { opacity: 0.7; filter: brightness(95%); }
                    91% { opacity: 0.8; filter: brightness(97%); }
                    95% { opacity: 0.9; filter: brightness(98%); }
                    100% { opacity: 1; filter: brightness(100%); }
                }
                @keyframes look {
                    0% { opacity: 1; filter: brightness(70%); }
                    20% { opacity: 0.3; filter: brightness(10%); }
                    30% { opacity: 0.3; filter: brightness(10%); }
                    40% { opacity: 1; filter: brightness(60%); }
                    45% { opacity: 0.8; filter: brightness(50%); }
                    60% { opacity: 0.2; filter: brightness(20%); }
                    80% { opacity: 1; filter: brightness(70%); }
                    85% { opacity: 0.4; filter: brightness(30%); }
                    100% { opacity: 1; filter: brightness(70%); }
                }
                .message {
                    margin-top: 30px;
                    font-size: 24px;
                    text-align: center;
                    color: #5e5e5e;
                    transition: opacity 3s ease; /* Плавное исчезновение за 3 секунды */
                }
                .snowman {
                    display: none; /* Скрыт по умолчанию */
                    opacity: 0;
                    transition: opacity 3s ease; /* Плавное появление за 3 секунды */
                }
            </style>
        </head>
        <body>
            <div class="info-icon">
                <div class="background-moon"></div>
                <div class="background-circle"></div>
                <span class="snowman">⛇</span>
            </div>
            <div class="message">Устройства не обнаружены</div>
            <script>
                setTimeout(() => {
                   
                    document.body.style.backgroundColor = '#000000';
                    document.querySelector('.message').style.opacity = '0';
                    setTimeout(() => {
                        const snowman = document.querySelector('.snowman');
                        snowman.style.display = 'inline'; 
                        setTimeout(() => {
                            snowman.style.opacity = '1'; 
                        }, 10); 
                        setTimeout(() => {
                            document.querySelector('.info-icon .background-circle').style.display = 'block';
                        }, 3000);
                    }, 3000);
                }, 25000); 
            </script>
        </body>
        </html>
        """
        self.browser.setHtml(no_devices_html, QUrl("http://no-devices/"))
        self.address_input.clear()


    # Переопределение событий мыши для перемещения окна
    def mousePressEvent(self, event: QMouseEvent):
        if event.button() == Qt.MouseButton.LeftButton:
            # Определяем, попал ли клик в область растягивания (правый нижний угол)
            pos = event.position().toPoint()
            rect = self.rect()
            resize_area = QRect(
                rect.right() - self.resize_area_size,
                rect.bottom() - self.resize_area_size,
                self.resize_area_size,
                self.resize_area_size
            )
            if resize_area.contains(pos):
                self.resizing = True
                self.resize_offset = pos - rect.bottomRight()
                #self.setCursor(QCursor(Qt.CursorShape.SizeFDiagCursor))
            else:
                self.dragging = True
                self.offset = event.position().toPoint()
                #self.setCursor(QCursor(Qt.CursorShape.ClosedHandCursor))

    def mouseMoveEvent(self, event: QMouseEvent):
        if self.resizing:
            # Изменяем размер окна при растягивании
            pos = event.position().toPoint()
            new_width = max(self.MIN_WINDOW_WIDTH, pos.x() - self.resize_offset.x())
            new_height = max(self.MIN_WINDOW_HEIGHT, pos.y() - self.resize_offset.y())
            new_width = min(new_width, self.MAX_WINDOW_WIDTH)
            new_height = min(new_height, self.MAX_WINDOW_HEIGHT)
            self.resize(new_width, new_height)
            #self.setCursor(QCursor(Qt.CursorShape.SizeFDiagCursor))
        elif self.dragging:
            self.move(self.pos() + event.position().toPoint() - self.offset)

    def mouseReleaseEvent(self, event: QMouseEvent):
        if event.button() == Qt.MouseButton.LeftButton:
            self.resizing = False
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
        super().resizeEvent(event)
        # Масштаб из настроек
        self.browser.setZoomFactor(self.zoom_factor)

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
        if self.update_available:
            update_text = f"Есть новая версия ({self.latest_version})"
            update_action = menu.addAction(QIcon("open.png"), update_text)
            update_action.triggered.connect(self.open_latest_release_page)
            menu.addSeparator()


        devices_menu = menu.addMenu(QIcon("swap.png"), "Переключить устройство")
        if os.path.exists("discovered_devices.json"):
            with open("discovered_devices.json", 'r') as f:
                devices = json.load(f)
                for device in devices:
                    device_action = devices_menu.addAction(f"{device['name']} ({device['url']})")
                    # Передаём только URL устройства
                    device_action.triggered.connect(lambda checked, url=device['url']: self.load_page(url))


        menu.addSeparator()
        scan_action = menu.addAction(QIcon("scan.png"), "Поиск и редактирование")
        scan_action.triggered.connect(self.open_menu)
        settings_action = menu.addAction(QIcon("settings.png"), "Настройки")
        settings_action.triggered.connect(self.open_settings_dialog)
        menu.addSeparator()
        refresh_action = menu.addAction(QIcon("refresh.png"), "Обновить")
        refresh_action.triggered.connect(self.refresh_page)
        menu.addSeparator()
        copy_url_action = menu.addAction(QIcon("copy.png"), "Копировать URL")
        copy_url_action.triggered.connect(self.copy_current_url)
        about_action = menu.addAction(QIcon("info.png"), "О программе")
        about_action.triggered.connect(self.show_about_dialog)
        menu.addSeparator()
        minimize_action = menu.addAction(QIcon("minimize.png"), "Свернуть")
        minimize_action.triggered.connect(self.showMinimized)
        close_action = menu.addAction(QIcon("close.png"), "Закрыть")
        close_action.triggered.connect(self.close)
        menu.exec(self.browser.mapToGlobal(position))



    def open_settings_dialog(self):
        dialog = SettingsDialog(self)

        # Получаем размеры экрана
        screen = QApplication.primaryScreen().geometry()
        # Получаем текущее положение и размеры главного окна
        main_window_pos = self.geometry()
        # Вычисляем координаты для окна настроек
        settings_x = 0
        # Проверяем, есть ли место слева от главного окна
        if main_window_pos.left() >= dialog.width() + 10:
            # Размещаем слева
            settings_x = main_window_pos.left() - dialog.width() - 10
        # Проверяем, есть ли место справа от главного окна
        elif main_window_pos.right() + dialog.width() + 10 <= screen.width():
            # Размещаем справа
            settings_x = main_window_pos.right() + 10
        else:
            # Если нет места ни слева, ни справа, размещаем по центру главного окна
            settings_x = main_window_pos.left() + (main_window_pos.width() - dialog.width()) // 2

        # Вычисляем Y координату (выравнивание по вертикали с главным окном)
        settings_y = main_window_pos.top()

        # Устанавливаем позицию окна настроек
        dialog.move(settings_x, settings_y)
        dialog.setWindowFlags(dialog.windowFlags() | Qt.WindowType.WindowStaysOnTopHint)  # флаг "поверх всех"
        dialog.show()  # открываем



    def toggle_show_names_and_update(self):
        # Переключаем показ имён и сохраняем
        self.show_names = not self.show_names
        self.save_settings()  # Сохраняем настройки, чтобы изменение сохранилось
        
        # Обновить список автодополнения
        self.device_list = self.load_devices_for_autocomplete()
        self.completer.setModel(QStringListModel(self.device_list))

        # Обновить текущее значение в address_input из discovered_devices.json
        if os.path.exists("discovered_devices.json"):
            with open("discovered_devices.json", 'r') as f:
                devices = json.load(f)
                if devices:
                    if self.show_names:
                        self.address_input.setText(devices[0]['name'])
                    else:
                        self.address_input.setText(devices[0]['url'])



    def copy_current_url(self):
        url = self.browser.url().toString()
        QApplication.clipboard().setText(url)

    def clear_browser_cache(self):
        self.profile.clearHttpCache()


    def check_device_availability(self, url, callback):
        if not (url.startswith("http://") or url.startswith("https://")):
            url = f"http://{url}"

        signals = WorkerSignals()
        signals.result.connect(callback)
        worker = CheckAvailabilityWorker(url, signals, self.check_timeout)  # Используем таймаут из настроек
        self.threadpool = getattr(self, 'threadpool', QThreadPool())
        self.threadpool.start(worker)
            
    def start_border_animation(self):
        if not hasattr(self, 'border_animation_timer'):
            self.border_animation_timer = QtCore.QTimer(self)
            self.border_animation_timer.timeout.connect(self.animate_border)
            self.border_animation_value = 0
        self.border_animation_timer.start(50)
  
    @pyqtSlot()
    def stop_border_animation(self):
        if hasattr(self, 'border_animation_timer'):
            self.border_animation_timer.stop()
            self.border_color = getattr(self, 'saved_border_color', QColor(49, 113, 49, 150)) #21, 0, 0, 190
            self.update()

    def animate_border(self):
        import math
        self.border_animation_value = (self.border_animation_value + 5) % 360
        if not hasattr(self, 'saved_border_color'):
            self.saved_border_color = QColor(21, 0, 0, 190)
        
        factor = (math.sin(math.radians(self.border_animation_value)) + 1) / 2
        red_color = QColor(255, 0, 0, 150)
        
        r = int(red_color.red() * factor + self.saved_border_color.red() * (1 - factor))
        g = int(red_color.green() * factor + self.saved_border_color.green() * (1 - factor))
        b = int(red_color.blue() * factor + self.saved_border_color.blue() * (1 - factor))
        
        self.border_color = QColor(r, g, b, 150)
        self.update()

    def refresh_page(self):
        current_text = self.address_input.text().strip()
        if current_text:
            if self.show_names and current_text in self.device_map:
                url = self.device_map[current_text]
                self.load_page(url)
            else:
                self.load_page(current_text)


    def check_current_device(self):
        if not self.current_checking_device:
            return

        def on_finished(is_available):
            if is_available:
                device_ip = self.current_checking_device
                self.check_timer.stop()
                self.current_checking_device = None
                self.load_page(f"http://{device_ip}/")

        # Запускаем проверку доступности
       
        self.check_device_availability(self.current_checking_device, on_finished)


    def check_current_device(self):
        if not self.current_checking_device:
            return

        def on_finished(is_available):
            if is_available:
                device_url = self.current_checking_device  
                self.check_timer.stop()
                self.current_checking_device = None
                self.load_page(device_url)  # Передаём как есть

        self.check_device_availability(self.current_checking_device, on_finished)

class Bridge(QObject):
    @pyqtSlot()
    def stop_border_animation(self):
        self.parent().stop_border_animation()

class AboutDialog(QDialog):
    def __init__(self, parent=None):
        super().__init__(parent)
        
        self.setWindowTitle("О программе")
        self.setMinimumSize(400, 400)  # Размер окна

        if NAME == CUSTOM_NAME:
            self.setWindowIcon(QIcon("g_icon.ico"))
        else:
            self.setWindowIcon(QIcon("icon.ico"))

        # Layout для окна
        layout = QVBoxLayout()

        about_text = QTextBrowser()
        about_text.setOpenExternalLinks(True)  # Позволяет открывать ссылки в браузере
        if NAME == CUSTOM_NAME:
            about_text.setText(
                f"""
                <h2>{NAME}</h2>
                <p><b>Версия:</b> {VERSION}</p>
                <p><b>Автор:</b> Vanila</p>
                <p><b>Описание:</b> Программа для отображения и поиска устройств {NAME} в локальной сети 
                <p><b>Ссылка на проект:</b> <a href="https://github.com/TonTon-Macout/APP-for-AlexGyver-Settings">GitHub</a></p>
                <p>Веб интерфейс работает на библиотеке <a href="https://github.com/GyverLibs/Settings">AlexGyver Settings</a></p>
                """
            )
        else: 
            about_text.setText(
                f"""
                <h2>{NAME} App</h2>
                <p><b>Версия:</b> {VERSION}</p>
                <p><b>Автор:</b> Vanila</p>
                <p><b>Описание:</b> программа для поиска и отображения устройств в локальной сети с установленной библиотекой <a href="https://github.com/GyverLibs/Settings">AlexGyver Settings</a></p>
                <p>так же может искать устройства с <a href="https://github.com/wled/WLED">WLED</a></p>
                <p><b>Ссылка на проект:</b> <a href="https://github.com/TonTon-Macout/APP-for-AlexGyver-Settings">GitHub</a></p>
                
                """
            )
                                      

        layout.addWidget(about_text)

        # Кнопка "Закрыть"
        close_button = QPushButton("Закрыть")
        close_button.clicked.connect(self.close)
        layout.addWidget(close_button)

        self.setLayout(layout)

class SettingsDialog(QDialog):
    def __init__(self, parent=None):
        super().__init__(parent)
        self.setWindowTitle("Настройки")
        self.setMinimumSize(300, 300)  
        self.parent = parent  # Ссылка на WebBrowser
        
        # Основной layout
        layout = QVBoxLayout(self)

        # Чекбокс "Показывать имена устройств"
        self.show_names_checkbox = QCheckBox("Имена устройств в строке", self)
        self.show_names_checkbox.setChecked(self.parent.show_names)
        self.show_names_checkbox.stateChanged.connect(self.update_show_names)
        layout.addWidget(self.show_names_checkbox)

        # Чекбокс "Искать устройства WLED"
        self.wled_search_checkbox = QCheckBox("Искать устройства WLED", self)
        self.wled_search_checkbox.setChecked(self.parent.wled_search)
        self.wled_search_checkbox.stateChanged.connect(self.update_wled_search)
        layout.addWidget(self.wled_search_checkbox)

        if NAME == CUSTOM_NAME :
            # Чекбокс "Искать только GLUON"
            self.gluon_only_checkbox = QCheckBox(f"Искать только {CUSTOM_NAME}", self)
            self.gluon_only_checkbox.setChecked(self.parent.gluon_only)
            self.gluon_only_checkbox.stateChanged.connect(self.update_gluon_only)
            layout.addWidget(self.gluon_only_checkbox)


        separator = QFrame()
        separator.setFrameShape(QFrame.Shape.HLine)  # Горизонтальная линия
        separator.setFrameShadow(QFrame.Shadow.Sunken) 
        layout.addWidget(separator)

        # Чекбокс "Поверх окон"
        #self.stay_on_top_checkbox = QCheckBox("Поверх окон", self)
        #self.stay_on_top_checkbox.setChecked(self.parent.checkbox.isChecked())
        #self.stay_on_top_checkbox.stateChanged.connect(self.update_stay_on_top)
        #layout.addWidget(self.stay_on_top_checkbox)



        # Чекбокс "Свои цвета"
        self.custom_colors_checkbox = QCheckBox("Свои цвета всегда", self)
        self.custom_colors_checkbox.setChecked(self.parent.custom_colors_enabled)
        self.custom_colors_checkbox.stateChanged.connect(self.update_custom_colors)
        layout.addWidget(self.custom_colors_checkbox)

        # Кнопка выбора цвета рамки
        self.border_color_button = QPushButton("Цвет рамки по умолчанию", self)
        self.border_color_button.clicked.connect(self.choose_border_color)
        layout.addWidget(self.border_color_button)

        # Кнопка выбора цвета фона
        self.back_color_button = QPushButton("Цвет фона по умолчанию", self)
        self.back_color_button.clicked.connect(self.choose_back_color)
        layout.addWidget(self.back_color_button)


        separator2 = QFrame()
        separator2.setFrameShape(QFrame.Shape.HLine)  # Горизонтальная линия
        separator2.setFrameShadow(QFrame.Shadow.Sunken)  
        layout.addWidget(separator2)
      
        # Поле для ширины окна
        self.width_label = QLabel("Ширина окна:", self)
        layout.addWidget(self.width_label)
        self.width_input = QLineEdit(self)
        self.width_input.setText(str(int(self.parent.window_width)))
        #self.width_input.textChanged.connect(self.update_window_size)
        layout.addWidget(self.width_input)

        # Поле для высоты окна
        self.height_label = QLabel("Высота окна:", self)
        layout.addWidget(self.height_label)
        self.height_input = QLineEdit(self)
        self.height_input.setText(str(int(self.parent.window_height)))
        #self.height_input.textChanged.connect(self.update_window_size)
        layout.addWidget(self.height_input)

        # Поле для масштаба
        self.zoom_label = QLabel("Масштаб браузера (0.1-5.0):", self)
        layout.addWidget(self.zoom_label)
        self.zoom_input = QLineEdit(self)
        self.zoom_input.setText(str(self.parent.zoom_factor))
        #self.zoom_input.textChanged.connect(self.update_zoom_factor)
        layout.addWidget(self.zoom_input)
        
        # Кнопка "Применить"
        self.apply_button = QPushButton("Применить размер", self)
        self.apply_button.clicked.connect(self.apply_settings)
        layout.addWidget(self.apply_button)

        # Кнопка "Сбросить размер"
        reset_button = QPushButton("Сбросить размер", self)
        reset_button.clicked.connect(self.reset_size)
        layout.addWidget(reset_button)



        self.setLayout(layout)

    def load_devices_from_file(self):
        if os.path.exists("discovered_devices.json"):
            with open("discovered_devices.json", 'r') as f:
                return json.load(f)
        return []
    
    def apply_settings(self):
        try:
            width = float(self.width_input.text())
            height = float(self.height_input.text())
            zoom = float(self.zoom_input.text())

            # Проверка и ограничение размеров
            width = max(self.parent.MIN_WINDOW_WIDTH, min(width, self.parent.MAX_WINDOW_WIDTH))
            height = max(self.parent.MIN_WINDOW_HEIGHT, min(height, self.parent.MAX_WINDOW_HEIGHT))
            if not (0.1 <= zoom <= 5.0):
                raise ValueError("Масштаб должен быть в диапазоне от 0.1 до 5.0")

            # Применяем значения
            self.parent.window_width = width
            self.parent.window_height = height
            self.parent.zoom_factor = zoom
            self.parent.resize(int(width), int(height))
            self.parent.browser.setZoomFactor(zoom)

            # Обновить поля ввода с актуальными значениями
            self.width_input.setText(str(int(width)))
            self.height_input.setText(str(int(height)))
            self.zoom_input.setText(str(zoom))

            # Сохранить настройки
            self.parent.save_settings()

        except ValueError as e:
            QMessageBox.warning(
                self, "Ошибка", 
                f"Введите корректные числовые значения.\nШирина: {self.parent.MIN_WINDOW_WIDTH}-{self.parent.MAX_WINDOW_WIDTH}\nВысота: {self.parent.MIN_WINDOW_HEIGHT}-{self.parent.MAX_WINDOW_HEIGHT}\nМасштаб: 0.1-5.0\nОшибка: {str(e)}"
            )
            # Восстанавливаем предыдущие значения в поля ввода
            self.width_input.setText(str(int(self.parent.window_width)))
            self.height_input.setText(str(int(self.parent.window_height)))
            self.zoom_input.setText(str(self.parent.zoom_factor))

    def update_colors(self):
        if self.parent.custom_colors_enabled:
            self.parent.border_color = self.parent.custom_border_color
            self.parent.back_color = self.parent.custom_back_color
        else:
            # Если "Свои цвета" выключены, вызываем получение цветов
            self.parent.get_colors()
        self.parent.update()  # Перерисовываем главное окно

    def choose_border_color(self):
        # Диалог выбора цвета рамки
        color = QColorDialog.getColor(
            self.parent.custom_border_color,
            self,
            "Выберите цвет рамки",
            QColorDialog.ColorDialogOption.ShowAlphaChannel
        )
        if color.isValid():
            self.parent.custom_border_color = color
            self.parent.save_settings()
            self.update_colors()  # Обновить цвета сразу после выбора

    def choose_back_color(self):
       #Диалог выбора цвета фона
        color = QColorDialog.getColor(
            self.parent.custom_back_color,
            self,
            "Выберите цвет фона",
            QColorDialog.ColorDialogOption.ShowAlphaChannel
        )
        if color.isValid():
            self.parent.custom_back_color = color
            self.parent.save_settings()
            self.update_colors()  # Обновить цвета сразу после выбора

    def update_custom_colors(self, state):
        self.parent.custom_colors_enabled = (state == Qt.CheckState.Checked.value)
        self.parent.save_settings()
        self.update_colors()  # Обновить цвета после изменения состояния

    def save_settings(self):
        self.parent.show_names = self.show_names_checkbox.isChecked()  # Пример
        self.parent.save_settings()  # Вызываем метод WebBrowser
        

    def update_show_names(self, state):
        self.parent.show_names = (state == Qt.CheckState.Checked.value)
        self.save_settings()  
        self.parent.device_list = self.parent.load_devices_for_autocomplete()
        self.parent.completer.setModel(QStringListModel(self.parent.device_list))
        if os.path.exists("discovered_devices.json"):
                with open("discovered_devices.json", 'r') as f:
                    devices = json.load(f)
                    if devices:
                        if self.parent.show_names:
                            self.parent.address_input.setText(devices[0]['name'])
                        else:
                            self.parent.address_input.setText(devices[0]['url'])
        

    def update_stay_on_top(self, state):
        stay_on_top = (state == Qt.CheckState.Checked.value)
        self.parent.checkbox.setChecked(stay_on_top)
        if stay_on_top:
            self.parent.setWindowFlags(self.parent.windowFlags() | Qt.WindowType.WindowStaysOnTopHint)
        else:
            self.parent.setWindowFlags(self.parent.windowFlags() & ~Qt.WindowType.WindowStaysOnTopHint)
        self.parent.show()

    def update_gluon_only(self, state):
            # Cостояние gluon_only, нужно для поиска только своих устройств если используется свое имя 
            self.parent.gluon_only = (state == Qt.CheckState.Checked.value)
            self.parent.save_settings()

            print(f"GLUON_only updated to: {self.parent.gluon_only}")




    def update_wled_search(self, state):
        self.parent.wled_search = (state == Qt.CheckState.Checked.value)
        self.parent.save_settings()

        print(f"WLED_search updated to: {self.parent.wled_search}")
    
    def reset_size(self):
        self.parent.window_width = self.parent.default_width
        self.parent.window_height = self.parent.default_height
        self.parent.zoom_factor = self.parent.default_zoom_factor
        self.parent.resize(int(self.parent.window_width), int(self.parent.window_height))
        self.parent.browser.setZoomFactor(self.parent.zoom_factor)
        self.width_input.setText(str(int(self.parent.window_width)))
        self.height_input.setText(str(int(self.parent.window_height)))
        self.zoom_input.setText(str(self.parent.zoom_factor))
        self.parent.save_settings()



class CheckDeviceWorker(QObject):
    finished = pyqtSignal(bool)
    
    def __init__(self, ip):
        super().__init__()
        self.ip = ip
        
    def run(self):
        try:
            response = requests.get(
                f"http://{self.ip}/settings?action=discover",
                timeout=1.3,
                verify=False
            )
            self.finished.emit(response.status_code == 200)
        except:
            self.finished.emit(False)

if __name__ == "__main__":
    print(VERSION)
    app = QApplication(sys.argv)
    # Проверяем, какая тема выбрана в Windows
    #if not is_windows_dark_theme():
        # Если тема светлая, применяем темную тему в приложении
    app.setStyleSheet(DARK_THEME)
    #app.setAttribute(Qt.ApplicationAttribute.AA_DontCreateNativeWidgetSiblings)  # Убираем баг с прозрачностью
    window = WebBrowser()
    window.show()
    sys.exit(app.exec())



