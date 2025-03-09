VERSION = "0.2.2"
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
 




import sys,json, requests, ipaddress, os, socket, ctypes, psutil


from PyQt6.QtWidgets import ( QDialog, QLabel, QProgressBar, QListWidget, QMessageBox,
                             QListWidgetItem, QApplication, QMainWindow, QVBoxLayout, 
                             QHBoxLayout, QWidget, QLineEdit, QPushButton, QCheckBox, QMenu, 
                             QTextBrowser, QCompleter, QComboBox, QColorDialog, QFrame
                             )
from PyQt6.QtCore import QThreadPool, QRunnable, pyqtSlot, pyqtSignal, QObject, QUrl, Qt, QPoint, QSize, QStringListModel, QRect, QTimer

from PyQt6.QtWebEngineWidgets import QWebEngineView

from PyQt6.QtGui import QIcon, QMouseEvent, QColor, QPainter, QPen, QBrush, QFont

from PyQt6 import QtCore

from PyQt6.QtWebEngineCore import QWebEngineProfile, QWebEnginePage, QWebEngineSettings



from PyQt6.QtWidgets import QApplication


DARK_THEME = """
    /* Базовые стили для всех виджетов */
    QWidget {
        background-color: #202020;  /* Основной фон */
        color: #FFFFFF;  /* Основной цвет текста */
        font-family: "Segoe UI Variable";  /* Шрифт  */
        font-size: 14px;  /* Размер шрифта */
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


# Функции для работы с IP и подсетью
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
    result = pyqtSignal(str)
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
                
            # Проверяем сеть на наличие Settings 
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

            # Поиск WLED нсли включен
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


""" 
class ScanDialog(QDialog):
    def __init__(self, web_browser, parent=None):
        super().__init__(parent)
        self.wled_search = web_browser.wled_search
        self.setWindowFlags(self.windowFlags() | Qt.WindowType.Dialog)  # Делаем окно диалоговым
        self.web_browser = web_browser  # Экземпляр WebBrowser
        self.setWindowTitle("Сканировать")
        self.setGeometry(300, 300, 400, 400)
        self.layout = QVBoxLayout(self)

        self.subnet_label = QLabel("Маска:")
        self.layout.addWidget(self.subnet_label)

        # Получаем все подсети
        all_subnets = get_all_local_subnets()
        #  все подсети в историю
        for subnet in all_subnets:
            SUBNET_HISTORY_MANAGER.add(subnet)

        # QComboBox для ввода подсети
        self.subnet_input = QComboBox(self)
        self.subnet_input.setEditable(True)  # Разрешаем ручной ввод
        self.subnet_input.addItems(SUBNET_HISTORY_MANAGER.get_history())  #  историю

        # Устанавливаем первую подсеть как начальное значение
        default_subnet = all_subnets[0] if all_subnets else "127.0.0.1/24"
        self.subnet_input.setCurrentText(default_subnet)

        # Инициализируем историю начальными значениями, если она пуста
        if not SUBNET_HISTORY_MANAGER.get_history():
            SUBNET_HISTORY_MANAGER.add(default_subnet)
            SUBNET_HISTORY_MANAGER.add("192.168.1.1/24")
            SUBNET_HISTORY_MANAGER.add("10.0.0.1/24")

        # Настраиваем автодополнение для ручного ввода
        self.completer = QCompleter(SUBNET_HISTORY_MANAGER.get_history(), self)
        self.completer.setCaseSensitivity(Qt.CaseSensitivity.CaseInsensitive)
        self.completer.setCompletionMode(QCompleter.CompletionMode.PopupCompletion)
        self.completer.setFilterMode(Qt.MatchFlag.MatchContains)
        self.subnet_input.lineEdit().setCompleter(self.completer)

        # Подключаем сигнал для добавления новых значений
        self.subnet_input.lineEdit().returnPressed.connect(self.add_new_input)

        # Переопределяем mousePressEvent для внутреннего QLineEdit
        self.subnet_input.lineEdit().mousePressEvent = self.show_completer

        self.layout.addWidget(self.subnet_input)

        # Поле для таймаута
        self.timeout_label = QLabel("Таймаут (мс):")
        self.layout.addWidget(self.timeout_label)

        self.timeout_input = QLineEdit(self)
        self.timeout_input.setText("500")  # По умолчанию 500 мс
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

    def add_new_input(self):
        new_value = self.subnet_input.currentText().strip()
        SUBNET_HISTORY_MANAGER.add(new_value)
        # Обновить список в QComboBox
        if new_value not in [self.subnet_input.itemText(i) for i in range(self.subnet_input.count())]:
            self.subnet_input.insertItem(0, new_value)
        self.subnet_input.setCurrentText(new_value)

        model = self.completer.model()
        model.setStringList(SUBNET_HISTORY_MANAGER.get_history())

    def show_completer(self, event: QMouseEvent):
        self.completer.setCompletionPrefix("")  # Сбрасываем префикс для показа полного списка
        self.subnet_input.lineEdit().completer().complete()  # Показываем список
        QComboBox.mousePressEvent(self.subnet_input, event)  

    def toggle_scan(self):
        if self.scanning:
            self.stop_scan()
        else:
            self.start_scan()

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
                wled_search=self.wled_search  # Передаем настройку поиска WLED
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
            with open("discovered_devices.json", 'w') as f:
                json.dump(self.discovered_devices, f, indent=4)


    def add_device(self, device_info):
        name, ip = device_info.split(' at http://')
        ip = ip.strip('/')
        for existing_device in self.discovered_devices:
            if existing_device["ip"] == ip:
                self.highlight_last_device()
                return

        # Проверяем, сколько устройств с таким именем уже есть
        # при совпадении  к имени номер
        name_count = sum(1 for d in self.discovered_devices if d["name"].startswith(name + "(") or d["name"] == name)
        if name_count > 0:
            unique_name = f"{name}({name_count})"
        else:
            unique_name = name

        item = QListWidgetItem(f"{unique_name} at http://{ip}/")
        self.device_list.addItem(item)
        self.discovered_devices.append({"name": unique_name, "ip": ip})
        self.highlight_last_device()
        print(f"New device found: {device_info}")

    def load_devices(self):
        if os.path.exists("discovered_devices.json"):
            with open("discovered_devices.json", 'r') as f:
                devices = json.load(f)
                for device in devices:
                    device_info = f"{device['name']} at http://{device['ip']}/"
                    self.device_list.addItem(device_info)
        self.highlight_last_device()


    def select_device(self, item):
        # Сброс стиля для всех элементов
        for index in range(self.device_list.count()):
            self.device_list.item(index).setBackground(QColor(Qt.GlobalColor.transparent))
            self.device_list.item(index).setForeground(QColor(Qt.GlobalColor.white))

        # Извлечение IP и имени
        device_info = item.text()
        name_part, ip = device_info.split(' at http://')
        ip = ip.strip('/')

       
        base_name = name_part.split('(')[0].strip()

        # Перемещаем устройство в начало discovered_devices.json
        devices = []
        if os.path.exists("discovered_devices.json"):
            with open("discovered_devices.json", 'r') as f:
                devices = json.load(f)
        devices = [d for d in devices if d['ip'] != ip]  # Удаляем дубликат
        devices.insert(0, {"name": base_name, "ip": ip})  #  в начало с оригинальным именем
        with open("discovered_devices.json", 'w') as f:
            json.dump(devices, f, indent=4)

        self.discovered_devices = devices
        if self.web_browser.check_device_availability(ip):
            self.web_browser.load_page(f"http://{ip}/")
        else:
            self.web_browser.load_page(f"http://{ip}/")
        self.highlight_last_device()



    def highlight_last_device(self):
        if os.path.exists("discovered_devices.json"):
            with open("discovered_devices.json", 'r') as f:
                devices = json.load(f)
                if devices:
                    last_ip = devices[0]['ip']
                    for index in range(self.device_list.count()):
                        item = self.device_list.item(index)
                        if last_ip in item.text():
                            item.setBackground(QColor(0, 100, 0))  # темно-зеленый фон
                            item.setForeground(QColor(Qt.GlobalColor.white))  # белый текст

    def clear_devices(self):
        self.device_list.clear()
        self.discovered_devices = []
        if os.path.exists("discovered_devices.json"):
            os.remove("discovered_devices.json")

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
            self.highlight_last_device()
 """

class ScanDialog(QDialog):
    def __init__(self, web_browser, parent=None):
        super().__init__(parent)
        self.wled_search = web_browser.wled_search
        self.setWindowFlags(self.windowFlags() | Qt.WindowType.Dialog)
        self.web_browser = web_browser
        self.setWindowTitle("Сканировать")
        self.setGeometry(300, 300, 400, 500)
        
        accent_color = self.web_browser.accent_color if hasattr(self.web_browser, 'accent_color') else "#37a93c"

        if NAME == CUSTOM_NAME:
            self.setWindowIcon(QIcon("g_icon.ico"))
        else:
            self.setWindowIcon(QIcon("icon.ico"))

        self.layout = QVBoxLayout(self)

        self.subnet_label = QLabel("Маска:")
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

        self.timeout_label = QLabel("Таймаут (мс):")
        self.layout.addWidget(self.timeout_label)

        self.timeout_input = QLineEdit(self)
        self.timeout_input.setText("500")
        self.timeout_input.setStyleSheet(f"""
                                            QLineEdit:focus {{
                                                border: 1px solid {accent_color};  
                                            }}
                                        """)
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
        self.ip_input.setPlaceholderText("  IP-адрес")
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

        self.name_input.textChanged.connect(self.update_apply_button)
        self.ip_input.textChanged.connect(self.update_apply_button)


    def add_new_device(self):
        new_name = self.name_input.text().strip()
        new_ip = self.ip_input.text().strip()
        if new_name and new_ip:
            # Проверка на совпадение IP
            for dev in self.discovered_devices:
                if dev["ip"] == new_ip:
                    QMessageBox.warning(self, "Ошибка", "Устройство с таким IP уже существует!")
                    return
            # Проверка на совпадение имени
            for dev in self.discovered_devices:
                if dev["name"] == new_name:
                    QMessageBox.warning(self, "Ошибка", "Устройство с таким именем уже существует!")
                    return
            #  новое устройство
            self.discovered_devices.append({"name": new_name, "ip": new_ip})
            self.device_list.addItem(f"{new_name} at http://{new_ip}/")
            # Сохранить изменения в файл
            with open("discovered_devices.json", 'w') as f:
                json.dump(self.discovered_devices, f, indent=4)
            self.original_devices = self.discovered_devices.copy()
            self.name_input.clear()
            self.ip_input.clear()
            self.update_apply_button()
        else:
            QMessageBox.warning(self, "Ошибка", "Введите имя и IP-адрес!")

    def update_list_style(self):
        accent_color = self.web_browser.accent_color if hasattr(self.web_browser, 'accent_color') else "#37a93c"
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
                color: #000000;
                
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
            with open("discovered_devices.json", 'w') as f:
                json.dump(self.discovered_devices, f, indent=4)

    def add_device(self, device_info):
        name, ip = device_info.split(' at http://')
        ip = ip.strip('/')
        for existing_device in self.discovered_devices:
            if existing_device["ip"] == ip:
                self.highlight_last_device()
                return

        name_count = sum(1 for d in self.discovered_devices if d["name"].startswith(name + "(") or d["name"] == name)
        if name_count > 0:
            unique_name = f"{name}({name_count})"
        else:
            unique_name = name

        item = QListWidgetItem(f"{unique_name} at http://{ip}/")
        self.device_list.addItem(item)
        self.discovered_devices.append({"name": unique_name, "ip": ip})
        self.highlight_last_device()
        print(f"New device found: {device_info}")

    def load_devices(self):
        if os.path.exists("discovered_devices.json"):
            with open("discovered_devices.json", 'r') as f:
                self.discovered_devices = json.load(f)
                for device in self.discovered_devices:
                    self.device_list.addItem(f"{device['name']} at http://{device['ip']}/")
        self.highlight_last_device()

    def select_device(self, item):
        

        device_info = item.text()
        name_part, ip = device_info.split(' at http://')
        ip = ip.strip('/')
        base_name = name_part.split('(')[0].strip()

        # Перемещаем устройство в начало списка
        devices = []
        if os.path.exists("discovered_devices.json"):
            with open("discovered_devices.json", 'r') as f:
                devices = json.load(f)
        devices = [d for d in devices if d['ip'] != ip]
        devices.insert(0, {"name": base_name, "ip": ip})
        with open("discovered_devices.json", 'w') as f:
            json.dump(devices, f, indent=4)

        self.discovered_devices = devices
        if self.web_browser.check_device_availability(ip):
            self.web_browser.load_page(f"http://{ip}/")
        else:
            self.web_browser.load_page(f"http://{ip}/")
        self.highlight_last_device()

        self.name_input.setText(name_part)
        self.ip_input.setText(ip)

        font = QFont()
        font.setWeight(QFont.Weight.Bold)  # Полужирный шрифт (600)
        item.setFont(font)

        # Отложить обновление стиля, иначе не успевает
        QTimer.singleShot(400, self.update_list_style)
        self.update_apply_button()


    def apply_changes(self):
        current_item = self.device_list.currentItem()
        if current_item:
            new_name = self.name_input.text().strip()
            new_ip = self.ip_input.text().strip()
            if new_name and new_ip:
                # Получаем данные текущего устройства из текста
                device_info = current_item.text()
                orig_name, orig_ip = device_info.split(' at http://')
                orig_ip = orig_ip.strip('/')

                # Находим текущее устройство в списке по IP
                current_device = next((dev for dev in self.discovered_devices if dev["ip"] == orig_ip), None)
                if not current_device:
                    QMessageBox.warning(self, "Ошибка", "Не удалось найти устройство в списке!")
                    return

                # Проверка на совпадение IP, исключая текущее устройство
                for dev in self.discovered_devices:
                    if dev["ip"] == new_ip and dev != current_device:
                        QMessageBox.warning(self, "Ошибка", "Устройство с таким IP уже существует!")
                        return
                # Проверка на совпадение имени, исключая текущее устройство
                for dev in self.discovered_devices:
                    if dev["name"] == new_name and dev != current_device:
                        QMessageBox.warning(self, "Ошибка", "Устройство с таким именем уже существует!")
                        return

                # Обновить данные текущего устройства
                current_device["name"] = new_name
                current_device["ip"] = new_ip
                current_item.setText(f"{new_name} at http://{new_ip}/")

                # Сохранить изменения в файл
                with open("discovered_devices.json", 'w') as f:
                    json.dump(self.discovered_devices, f, indent=4)
                self.original_devices = self.discovered_devices.copy()

                # Обновить окно сканирования
                self.device_list.clear()
                self.load_devices()
                self.update_list_style()  # Обновить стиль списка с новым акцентным цветом

                # Обновить основное окно и открываем устройство заново
                self.web_browser.device_list = self.web_browser.load_devices_for_autocomplete()
                self.web_browser.completer.setModel(QStringListModel(self.web_browser.device_list))
                self.web_browser.load_page(f"http://{new_ip}/")

                self.update_apply_button()
            else:
                QMessageBox.warning(self, "Ошибка", "Введите имя и IP-адрес!")
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
        self.update_apply_button()

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
            self.highlight_last_device()
            self.update_apply_button()

    def update_apply_button(self):
        """Обновить стиль кнопки Применить"""
        accent_color = self.web_browser.accent_color if hasattr(self.web_browser, 'accent_color') else "#37a93c"
        current_item = self.device_list.currentItem()
        if current_item:
            name = self.name_input.text().strip()
            ip = self.ip_input.text().strip()
            if name and ip:
                # Извлекаем данные из текста элемента списка
                device_info = current_item.text()
                orig_name, orig_ip = device_info.split(' at http://')
                orig_ip = orig_ip.strip('/')
                # Проверяем изменения только в данных устройства
                if orig_name != name or orig_ip != ip:
                    self.apply_button.setEnabled(True)
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
                    return
        self.apply_button.setEnabled(False)
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
            


# Класс для управления историей
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

class WebBrowser(QMainWindow):
    def __init__(self):
        super().__init__()
        self.setWindowTitle(NAME)


        self.update_available = False
        self.latest_version = None
        self.github_repo = "TonTon-Macout/APP-for-AlexGyver-Settings"  # репо

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

        # Загружаем настройки из settings.json
        self.show_names = True
        self.gluon_only = True
        self.window_width = default_width
        self.window_height = default_height
        self.zoom_factor = default_zoom_factor
        self.wled_search = True  # По умолчанию включен поиск WLED
        self.default_border_color = QColor(49, 113, 49, 150)  # Цвет рамки по умолчанию
        self.default_back_color = QColor(28, 29, 34, 255)

        self.custom_colors_enabled = False
        self.custom_border_color = self.default_border_color
        self.custom_back_color = self.default_back_color
        
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
       
       
       # Применяем загруженные размеры окна
        self.resize(int(self.window_width), int(self.window_height))
        self.browser = QWebEngineView()  
        self.browser.setZoomFactor(self.zoom_factor)  # Применяем масштаб
       
       # Устанавливаем начальные цвета
        self.border_color = self.default_border_color
        self.back_color = self.default_back_color

        # Устанавливаем начальные размеры окна и масштаб
        #self.setGeometry(100, 100, int(self.window_width), int(self.window_height))


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
        #self.browser = QWebEngineView() переехал выше чтобы применить масштаб
        self.browser.setPage(QWebEnginePage(self.profile, self.browser))
        self.browser.loadFinished.connect(self.get_colors)


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

        # Загружаем последний сохранённый URL
        self.load_last_url()

        # Подключаем сигнал изменения URL
        self.browser.urlChanged.connect(self.update_url)

        # 
        container = QWidget()
        container.setStyleSheet("background: transparent;")

        # Таймер для проверки доступности
        self.check_timer = QtCore.QTimer(self)
        self.check_timer.timeout.connect(self.check_current_device)
        self.current_checking_device = None

        # Проверяем доступность
        QtCore.QTimer.singleShot(0, self.check_initial_device)


    def initUI(self):
        # строка ввода  
        self.address_input = QLineEdit()
        self.address_input.setPlaceholderText("Enter IP Address")
        self.address_input.returnPressed.connect(self.load_page)
        self.address_input.setMinimumWidth(170)  # ширина
        self.address_input.setStyleSheet("background-color: rgba(200, 0, 0, 0); border-radius: 9px; ")
        self.address_input.returnPressed.connect(self.load_page)
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
                            self.address_input.setText(f"http://{devices[0]['ip']}/")
        


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
        buttons_layout.addStretch()  # Растягивающееся пространство
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

        # стили виджета браузера
        self.browser.setStyleSheet("""
            QWebEngineView {
                background-color: rgba(0, 0, 0, 255);
                border-radius: 100px;
            }
        """)


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
                    print(f"У вас последняя версия: {VERSION}")
            else:
                print(f"Ошибка проверки версии: {response.status_code}")
        except Exception as e:
            print(f"Не удалось проверить версию на GitHub: {e}")

    def open_latest_release_page(self):
        if self.latest_version:
            url = f"https://github.com/{self.github_repo}/releases/tag/v{self.latest_version}"
            import webbrowser
            webbrowser.open(url)

    def save_settings(self):
        settings = {
            "show_names": self.show_names,
            "gluon_only": self.gluon_only,
            "wled_search": self.wled_search,
            "window_width": self.window_width,
            "window_height": self.window_height,
            "zoom_factor": self.zoom_factor,
            "custom_colors_enabled": self.custom_colors_enabled,
            "custom_border_color": [self.custom_border_color.red(), 
                                  self.custom_border_color.green(), 
                                  self.custom_border_color.blue(), 
                                  self.custom_border_color.alpha()],
            "custom_back_color": [self.custom_back_color.red(), 
                                self.custom_back_color.green(), 
                                self.custom_back_color.blue(), 
                                self.custom_back_color.alpha()]
        }
        with open("settings.json", 'w') as f:
            json.dump(settings, f, indent=4)


    def toggle_show_names(self):
        #Переключаем показ имён и Сохранить
        self.show_names = not self.show_names
        with open("settings.json", 'w') as f:
            json.dump({"show_names": self.show_names}, f, indent=4)
        print(f"Show names toggled to: {self.show_names}")


    def load_devices_for_autocomplete(self):
        #Загружаем устройства из discovered_devices.json для автодополнения
        device_list = []
        self.device_map = {}
        name_count = {}  # Счётчик одинаковых имён
        if os.path.exists("discovered_devices.json"):
            with open("discovered_devices.json", 'r') as f:
                devices = json.load(f)
                for device in devices:
                    base_name = device['name']
                    ip = device['ip']
                    # Подсчитываем, сколько раз встречается базовое имя
                    if base_name in name_count:
                        name_count[base_name] += 1
                        display_name = f"{base_name}({name_count[base_name]})"
                    else:
                        name_count[base_name] = 0
                        display_name = base_name
                    self.device_map[display_name] = ip
                    if self.show_names:
                        device_list.append(display_name)
                    else:
                        device_list.append(f"http://{ip}/")
        return device_list


    def load_selected_device(self, text):
        if text:
            if self.show_names and text in self.device_map:
                ip = self.device_map[text]
                self.update_discovered_devices(text, ip)
                self.load_page(f"http://{ip}/")
            else:
                self.load_page(text)
  
    def show_completer(self, event: QMouseEvent):
        self.completer.setCompletionPrefix("")  # Префикс для показа всех устройств
        self.address_input.completer().complete()  #  Список
        QLineEdit.mousePressEvent(self.address_input, event)  # Сохранить стандартное поведение

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
                        
                    }
                """)
        else:
            # Если цвет не получен, используем стили по умолчанию
            self.setStyleSheet("""

                QMenu::item:selected {
                    background-color: #0078D4;
                    color: #000000;
                             
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
                print(f"Ошибка при обновлении стилей QCheckBox: {e}")
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
            self.accent_color = self.custom_border_color  # Используем цвет как акцент
            self.border_color = self.custom_border_color
            self.back_color = self.custom_back_color
        else:
            if color and isinstance(color, str) and color.startswith('#'):
                self.accent_color = color
                self.update_border_color(color)  # Обновить цвет рамки из полученного цвета
            else:
                self.accent_color = self.custom_border_color.name(QColor.NameFormat.HexRgb)  # Преобразуем QColor в HEX строку
                self.border_color = self.custom_border_color  # Устанавливаем цвет рамки
                self.back_color = self.custom_back_color      # Устанавливаем цвет фона

        # Обновить DARK_THEME с новым акцентным цветом
        updated_dark_theme = DARK_THEME.replace(
            "QListView::item:selected { background-color: #27272f; color: #FFFFFF; }",
            f"QListView::item:selected {{ background-color: {self.accent_color}; color: #000; }}"
        )
        QApplication.instance().setStyleSheet(updated_dark_theme)

        combined_style = self.get_combined_styles(self.accent_color)
        self.setStyleSheet(combined_style)
        self.update()  # Перерисовываем окно



        
    def get_combined_styles(self, color):
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
                /* padding: 6px; */
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
                color: #000000;
               
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

    def load_page(self, ip=None):
        url = ip if ip else self.address_input.text().strip()
        if url:
            original_url = url
            if not url.startswith("http://") and not url.startswith("https://"):
                url = "http://" + url
                original_url = url

            device_ip = url.split('//')[1].split('/')[0]

            # Если это ручной ввод IP (не из device_map), отправляем discover
            if device_ip not in self.device_map.values():
                # Проверяем, является ли device_ip валидным IP-адресом
                try:
                    ipaddress.ip_address(device_ip)  # Проверка валидности IP
                    response = requests.get(f"http://{device_ip}/settings?action=discover", timeout=0.3, verify=False)
                    if response.status_code == 200:
                        data = response.json()
                        name = data.get("name", f"Unknown_{device_ip}")
                        self.update_discovered_devices(name, device_ip)
                except ValueError:
                    # Если device_ip не является IP-адресом (например, имя устройства), игнорируем discover
                    pass
                except requests.RequestException:
                    # Если запрос не удался, продолжаем без добавления
                    pass
                
            if not self.check_device_availability(device_ip):
                self.current_checking_device = device_ip
                self.check_timer.start(5000)
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
                if self.show_names:
                    for name, ip in self.device_map.items():
                        if ip == device_ip:
                            self.address_input.setText(name)
                            break
                    else:
                        self.address_input.setText(original_url)
                else:
                    self.address_input.setText(original_url)
                self.start_border_animation()
            else:
                self.check_timer.stop()
                self.current_checking_device = None
                self.stop_border_animation()
                self.browser.setUrl(QUrl(url))
                if self.show_names:
                    for name, ip in self.device_map.items():
                        if ip == device_ip:
                            self.update_discovered_devices(name, device_ip)
                            self.address_input.setText(name)
                            break
                    else:
                        self.address_input.setText(original_url)
                else:
                    for name, ip in self.device_map.items():
                        if ip == device_ip:
                            self.update_discovered_devices(name, device_ip)
                            break
                    self.address_input.setText(original_url)
                QtCore.QTimer.singleShot(1000, self.get_accent_color)

            self.hide()
            self.show()
            self.activateWindow()




    def update_discovered_devices(self, name, ip):
        # Загружаем список устройств
        devices = []
        if os.path.exists("discovered_devices.json"):
            with open("discovered_devices.json", 'r') as f:
                devices = json.load(f)
    
        # Удаляем устройство с таким IP, если оно уже есть
        devices = [d for d in devices if d['ip'] != ip]
    
        # Проверяем, сколько устройств с таким именем
        name_count = sum(1 for d in devices if d["name"].startswith(name + "(") or d["name"] == name)
        if name_count > 0:
            unique_name = f"{name}({name_count})"
        else:
            unique_name = name
    
        #  устройство в начало списка с уникальным именем
        devices.insert(0, {"name": unique_name, "ip": ip})
    
        # Сохранить обновлённый список
        with open("discovered_devices.json", 'w') as f:
            json.dump(devices, f, indent=4)
    
        # Обновить device_map и автодополнение
        self.device_list = self.load_devices_for_autocomplete()
        self.completer.setModel(QStringListModel(self.device_list))

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
        # Обновить URL в поле ввода
        self.address_input.setText(url.toString())


    def load_last_url(self):
        # Загружаем последний URL из файла
        if os.path.exists("discovered_devices.json"):
            with open("discovered_devices.json", 'r') as f:
                devices = json.load(f)
                if devices:
                    ip = devices[0]['ip']
                    self.browser.setUrl(QUrl(f"http://{ip}/"))
                    if self.show_names:
                        self.address_input.setText(devices[0]['name'])
                    else:
                        self.address_input.setText(f"http://{ip}/")
        else:
            self.browser.setUrl(QUrl("http://192.168.1.132/"))

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
            else:
                # Обычная логика перемещения окна
                self.dragging = True
                self.offset = event.position().toPoint()

    def mouseMoveEvent(self, event: QMouseEvent):
        if self.resizing:
            # Изменяем размер окна при растягивании
            pos = event.position().toPoint()
            new_width = max(self.MIN_WINDOW_WIDTH, pos.x() - self.resize_offset.x())
            new_height = max(self.MIN_WINDOW_HEIGHT, pos.y() - self.resize_offset.y())
            new_width = min(new_width, self.MAX_WINDOW_WIDTH)
            new_height = min(new_height, self.MAX_WINDOW_HEIGHT)
            self.resize(new_width, new_height)
        elif self.dragging:
            # Обычная логика перемещения окна
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
      

        # доступно обновление
        if self.update_available:
            update_text = f"Есть новая версию ({self.latest_version})"
            update_action = menu.addAction(QIcon("open.png"),update_text)
            update_action.triggered.connect(self.open_latest_release_page)
            
            menu.addSeparator()


            

        # Подменю для устройств
        devices_menu = menu.addMenu(QIcon("swap.png"),"Переключить устройство")
        if os.path.exists("discovered_devices.json"):
            with open("discovered_devices.json", 'r') as f:
                devices = json.load(f)
                for device in devices:
                    device_action = devices_menu.addAction(f"{device['name']} ({device['ip']})")
                    def make_handler(ip=device['ip']):
                        return lambda: self.load_page(f"http://{ip}/")
                    device_action.triggered.connect(make_handler())

        menu.addSeparator()

        # Поиск устройств
        scan_action = menu.addAction(QIcon("scan.png"), "Поиск устройств")
        scan_action.triggered.connect(self.open_menu)


        # Настройки
        settings_action = menu.addAction(QIcon("settings.png"),"Настройки")
        settings_action.triggered.connect(self.open_settings_dialog)
        menu.addSeparator()
      
        # Обновить страницу
        refresh_action = menu.addAction(QIcon("refresh.png"), "Обновить")
        refresh_action.triggered.connect(self.refresh_page)
        menu.addSeparator()

        copy_url_action = menu.addAction(QIcon("copy.png"),"Копировать URL")
        copy_url_action.triggered.connect(self.copy_current_url)

        about_action = menu.addAction(QIcon("info.png"),"О программе")
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
        # Переключаем значение show_names
        self.show_names = not self.show_names
        print(f"Show names toggled to: {self.show_names}")

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
                        self.address_input.setText(f"http://{devices[0]['ip']}/")



    def copy_current_url(self):
        url = self.browser.url().toString()
        QApplication.clipboard().setText(url)

    def clear_browser_cache(self):
        self.profile.clearHttpCache()

    def check_device_availability(self, ip):
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

        current_text = self.address_input.text().strip()
        if current_text:
            if self.show_names and current_text in self.device_map:
                # Если отображается имя устройства, берём соответствующий IP
                ip = self.device_map[current_text]
                self.load_page(f"http://{ip}/")
            else:
                # Если это уже URL или IP, используем как есть
                self.load_page(current_text)


    def check_current_device(self):
        
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
        
        # Подключаем сигнал 
        self.check_worker.finished.connect(on_finished)
        
        # Запускаем проверку
        self.check_worker.run()

    def check_initial_device(self):
        #Проверка доступности устройства при запуске
        if os.path.exists("discovered_devices.json"):
            with open("discovered_devices.json", 'r') as f:
                devices = json.load(f)
                if devices:
                    ip = devices[0]['ip']
                    self.load_page(f"http://{ip}/")
        else:
            self.load_page("http://192.168.1.132/")

class AboutDialog(QDialog):
    def __init__(self, parent=None):
        super().__init__(parent)
        
        self.setWindowTitle("О программе")
        self.setFixedSize(400, 250)  # Размер окна

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
                <p><b>Ссылка на проект:</b> <a href="https://github.com/TonTon-Macout/web-server-for-Libre-Hardware-Monitor">GitHub</a></p>
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
        self.setMinimumSize(300, 250)  
        self.parent = parent  # Ссылка на WebBrowser
        
        # Основной layout
        layout = QVBoxLayout(self)

        # Чекбокс "Показывать имена устройств"
        self.show_names_checkbox = QCheckBox("Показывать имена устройств", self)
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



    def update_show_names(self, state):
        self.parent.show_names = (state == Qt.CheckState.Checked.value)
        self.parent.save_settings()
        # Обновить address_input
        if os.path.exists("discovered_devices.json"):
            with open("discovered_devices.json", 'r') as f:
                devices = json.load(f)
                if devices:
                    if self.parent.show_names:
                        self.parent.address_input.setText(devices[0]['name'])
                    else:
                        self.parent.address_input.setText(f"http://{devices[0]['ip']}/")
        self.parent.device_list = self.parent.load_devices_for_autocomplete()
        self.parent.completer.setModel(QStringListModel(self.parent.device_list))

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
                timeout=0.3,
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



