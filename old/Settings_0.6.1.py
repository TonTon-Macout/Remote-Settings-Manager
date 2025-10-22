VERSION = "0.6.1"
NAME = "RSM" # 

TEST_SPLASH = 0.1 # пауза при заполнении прогрес бара
 
DEBUG = True # вывод в консоль
STAMP = True # префикс со временем
REPO = "TonTon-Macout/APP-for-AlexGyver-Settings"  # Репо


import sys,json, requests, ipaddress, os, socket, math, re
import ctypes, psutil, base64, time , ssl, webbrowser, shutil, inspect
from urllib3.exceptions import InsecureRequestWarning
from datetime import datetime
from bs4 import BeautifulSoup 
requests.packages.urllib3.disable_warnings(InsecureRequestWarning)

from urllib.parse import urlparse
from PyQt6.QtCore import QObject, pyqtSignal, QRunnable
from PyQt6.QtWidgets import ( QDialog, QLabel, QProgressBar, QListWidget, QMessageBox,
                             QListWidgetItem, QApplication, QMainWindow, QVBoxLayout, 
                             QHBoxLayout, QWidget, QLineEdit, QPushButton, QCheckBox, QMenu, 
                             QTextBrowser, QCompleter, QComboBox, QColorDialog, QFrame, 
                             QFormLayout, QGroupBox, QGraphicsDropShadowEffect
                             )
from PyQt6.QtCore import QThreadPool, QRunnable, pyqtSlot, pyqtSignal, QObject, QUrl, Qt, QPoint, QSize, QStringListModel, QRect, QTimer

from PyQt6.QtWebEngineWidgets import QWebEngineView

from PyQt6.QtGui import (QIcon, QMouseEvent, QColor, QPainter, QPen, 
                         QBrush, QFont, QStandardItemModel, QStandardItem,
                         QIcon, QMouseEvent, QColor, QPen, 
                         QBrush, QFont, QStandardItemModel, QStandardItem, QPixmap
                         
                         )

from PyQt6 import QtCore
from PyQt6.QtWebEngineCore import QWebEngineProfile, QWebEnginePage, QWebEngineSettings
from urllib.parse import urlparse
from PyQt6.QtWebChannel import QWebChannel
from datetime import datetime
#from zeroconf import ServiceBrowser, Zeroconf
import threading


def resource_path(relative_path):
    """Получает абсолютный путь к ресурсу, работает как в разработке, так и в собранном exe."""
    if hasattr(sys, '_MEIPASS'):
        # Если программа запущена как exe, используем временную папку PyInstaller
        return os.path.join(sys._MEIPASS, relative_path)
    else:
        # Если программа запущена как скрипт, используем относительный путь
        return os.path.join(os.path.abspath("."), relative_path)

class Debug:
    _log_level = "INFO"

    @classmethod
    def _print_message(cls, message, color_code, prefix_char=""):
        if not DEBUG:
            return
        prefix = "\n" if message.startswith("\n") else ""
        message = message[1:] if message.startswith("\n") else message
        timestamp = f"[{datetime.now().strftime('%H:%M:%S:%f')[:-3]}] " if STAMP else ""
        print(f"{prefix}{timestamp}{color_code}{prefix_char} {message}\033[0m")

    @classmethod
    def info(cls, message):
        cls._print_message(message, "")

    @classmethod
    def success(cls, message):
        cls._print_message(message, "\033[92m", "✔ ")

    @classmethod
    def error(cls, message):
        cls._print_message(message, "\033[91m", "✗ ")

    @classmethod
    def warning(cls, message):
        cls._print_message(message, "\033[38;5;208m")

    @classmethod
    def red(cls, message):
        """Выводит сообщение в консоль приглушенным красным цветом (8-битная палитра)"""
        cls._print_message(message, "\033[38;5;167m")

    @classmethod
    def orange(cls, message):
        """Выводит сообщение в консоль приглушенным оранжевым цветом (8-битная палитра)"""
        cls._print_message(message, "\033[38;5;173m")

    @classmethod
    def yellow(cls, message):
        """Выводит сообщение в консоль приглушенным желтым цветом (8-битная палитра)"""
        cls._print_message(message, "\033[38;5;229m")

    @classmethod
    def green(cls, message):
        """Выводит сообщение в консоль приглушенным зеленым цветом (8-битная палитра)"""
        cls._print_message(message, "\033[38;5;108m")

    @classmethod
    def cyan(cls, message):
        """Выводит сообщение в консоль приглушенным голубым цветом (8-битная палитра)"""
        cls._print_message(message, "\033[38;5;116m")

    @classmethod
    def blue(cls, message):
        """Выводит сообщение в консоль приглушенным синим цветом (8-битная палитра)"""
        cls._print_message(message, "\033[38;5;110m")

    @classmethod
    def purple(cls, message):
        """Выводит сообщение в консоль приглушенным фиолетовым цветом (8-битная палитра)"""
        cls._print_message(message, "\033[38;5;146m")

    @classmethod
    def pink(cls, message):
        """Выводит сообщение в консоль приглушенным розовым цветом (8-битная палитра)"""
        cls._print_message(message, "\033[38;5;218m")

    @classmethod
    def teal(cls, message):
        """Выводит сообщение в консоль приглушенным бирюзовым цветом (8-битная палитра)"""
        cls._print_message(message, "\033[38;5;37m")

    @classmethod
    def magenta(cls, message):
        """Выводит сообщение в консоль приглушенным пурпурным цветом (8-битная палитра)"""
        cls._print_message(message, "\033[38;5;165m")

    @classmethod
    def lime(cls, message):
        """Выводит сообщение в консоль приглушенным лаймовым цветом (8-битная палитра)"""
        cls._print_message(message, "\033[38;5;149m")

    @classmethod
    def brown(cls, message):
        """Выводит сообщение в консоль приглушенным коричневым цветом (8-битная палитра)"""
        cls._print_message(message, "\033[38;5;94m")

DARK_THEME = """
    /* Базовые стили для всех виджетов */
    QWidget {
        background-color: #202020;  /* Основной фон */
        color: #FFFFFF;  /* Основной цвет текста */
        font-family: "Segoe UI Variable";  /* Шрифт  */
        font-size: 14px;  /* Размер шрифта */
    }


            

    QListView::item {
        padding: 4px 3px 4px 0px;
        color: #FFFFFF;
    }
    QListView::item:selected {
        background-color: #27272f;  /* Начальный цвет по умолчанию */
        color: #FFFFFF;
        }


    /* Стили для кнопок (QPushButton) */
    QPushButton {
        background-color: #27272f;  /* Фон кнопки */
        color: #FFFFFF;  /* Цвет текста */
        border: 1px solid #4A4A4A;  /* Граница кнопки */
        border-radius: 4px;  /* Закругление углов */
         padding: 6px 6px;   /* Внутренние отступы */
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
        border: 1px solid #9e9e9e;  /* Граница индикатора */
        border-radius: 4px;  /* Закругление углов */
         color: #FFFFFF;
    }
    QCheckBox::indicator:checked {
        background-color: #00612a;  /* Фон индикатора при выборе (акцентный цвет ) */
        border: 1px solid #9e9e9e;  /* Граница индикатора при выборе */
        color: #FFFFFF;
        image: url(:/qt-project.org/styles/commonstyle/images/standardbutton-apply-32.png);
         width: 10px;  
         height: 10px; 
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
#deprecated 
def detect_device_type(url):
    """DEPRECATED
    ------
    Проверить тип устройства. Синхронно. Блокирует уии
    
    Returns:
        'wled' | 'settings' | 'n/a'
    """
    Debug.yellow(f"[detect_device_type] Синхронная проверка типа устройства: {url}")
    try:

        base_url = Url.get_base_url(url if url.startswith(('http://', 'https://')) else f'http://{url}').rstrip('/')

        timeout = 1.5 if Url.is_ip_address(base_url) else 3.0
        
        Debug.yellow(f"[detect_device_type] Определяем тип для: {base_url}")
        settings_found = False
        wled_found = False


        # Проверяем Settings API
        try:
            settings_response = requests.get(f"{base_url}/settings?action=discover", timeout=timeout, verify=False)
            if settings_response.status_code == 200:
                try:
                    data = settings_response.json()
                    if data.get('type') == 'discover' and 'name' in data:
                        Debug.yellow(f"[detect_device_type] Найден Settings API: {data.get('name')}")
                        return 'settings'
                except:
                    pass
            elif settings_response.status_code == 401:
                settings_found = True
        except Exception as e:
            Debug.error(f"[detect_device_type] Settings ошибка: {e}")
        
        # Проверяем WLED API
        if not settings_found:
            try:
                wled_response = requests.get(f"{base_url}/json/info", timeout=timeout, verify=False)
                if wled_response.status_code == 200:
                    data = wled_response.json()
                    if data.get('brand') == 'WLED':
                        Debug.yellow(f"[detect_device_type] Найден WLED")
                        return 'wled'
                elif wled_response.status_code == 401:
                    Debug.yellow(f"[detect_device_type] Требуется авторизация")
                    wled_found = True
            except Exception as e:
                Debug.error(f"[detect_device_type] WLED ошибка: {e}")
       
        # Если оба запроса требуют авторизации, загружаем учетные данные и повторяем попытку
        if settings_found or wled_found:
            Debug.yellow(f"[detect_device_type] Требуется авторизация, проверяем учетные данные")
            credentials = {}
            if os.path.exists("settings.json"):
                with open("settings.json", 'r') as f:
                    settings = json.load(f)
                    if "credentials" in settings:
                        for url_key, cred in settings["credentials"].items():
                            if url_key.startswith(base_url):  # Проверяем совпадение базового URL
                                try:
                                    login = base64.b64decode(cred["login"]).decode('utf-8')
                                    password = base64.b64decode(cred["password"]).decode('utf-8')
                                    credentials[base_url] = {"login": login, "password": password}
                                except Exception as e:
                                    Debug.error(f"[detect_device_type] Ошибка расшифровки учетных данных: {e}")
            
            if base_url in credentials:
                # Проверяем Settings API с учетными данными
                try:
                    auth = (credentials[base_url]["login"], credentials[base_url]["password"])
                    settings_response = requests.get(f"{base_url}/settings?action=discover", auth=auth, timeout=timeout, verify=False)
                    if settings_response.status_code == 200:
                        try:
                            data = settings_response.json()
                            if data.get('type') == 'discover' and 'name' in data:
                                Debug.yellow(f"[detect_device_type] Найден Settings API с авторизацией: {data.get('name')}")
                                return 'settings'
                        except:
                            pass
                except Exception as e:
                    Debug.error(f"[detect_device_type] Settings ошибка с авторизацией: {e}")
                
                # Проверяем WLED API с учетными данными
                try:
                    auth = (credentials[base_url]["login"], credentials[base_url]["password"])
                    wled_response = requests.get(f"{base_url}/json/info", auth=auth, timeout=timeout, verify=False)
                    if wled_response.status_code == 200:
                        data = wled_response.json()
                        if data.get('brand') == 'WLED':
                            Debug.yellow(f"[detect_device_type] Найден WLED с авторизацией")
                            return 'wled'
                except Exception as e:
                    Debug.error(f"[detect_device_type] WLED ошибка с авторизацией: {e}")
        
        Debug.error("[detect_device_type] Тип не определен - возвращаем n/a")
        return 'n/a'
    
    except Exception as e:
        Debug.error(f"[detect_device_type] Критическая ошибка: {e}")
        return 'n/a'


class Url:
    real_url = "http://my_device.prnhub"
    
    @classmethod
    def get_base_url(cls, url, circumcision = False):
        """Форматирует URL\n
        -------
         - circumcision = False - вернет без якорей, но с путем\n
         - circumcision = True  - и без пути

        """
        if not circumcision:
            try:
                from urllib.parse import urlparse, urlunparse
                parsed = urlparse(url)
                # Убираем только fragment, оставляем path
                clean_url = urlunparse((
                    parsed.scheme,
                    parsed.netloc,
                    parsed.path,
                    parsed.params,
                    parsed.query,
                    '' 
                ))
                return clean_url
            except:
                return url

        try:
            from urllib.parse import urlparse
            parsed = urlparse(url)
            # Возвращаем только схема + хост + порт
            base_url = f"{parsed.scheme}://{parsed.netloc}/"
            return base_url
        except:
            return url
        
    @classmethod
    def is_ip_address(cls, url):
        """Проверяет, является ли IP-адресом
        -----
        - вернет тру если ip"""
        try:
            # Если передан URL, извлекаем hostname
            if url.startswith(('http://', 'https://')):
                from urllib.parse import urlparse
                hostname = urlparse(url).hostname
            else:
                hostname = url
            ipaddress.ip_address(hostname)
            return True
        except (ValueError, AttributeError):
            return False

    @classmethod    
    def check_url(cls, url):
        """Проверяет, является ли URL допустимым и не заглушка ли это\n
        -----------
        - вернет тру если все ок
        - и фелс если это не урл
        """
        #Debug.info("   ===[check_url]===")
        
        caller = inspect.stack()[1].function
        Debug.info(f"   ===[check_url] ← {caller} ===")


        if not url: 
            Debug.info("      [check_url] \033[91m✗\033[0m  пустой")
            Debug.info("   ^^^[check_url]^^^\n")
            return False

        try:
            if url == "http://no-devices/" or url == "http://loading/":
                Debug.info("      [check_url] \033[91m✗\033[0m  заглушка no or load")
                return False

            if url.startswith('data:text/html'):
                Debug.info("      [check_url] \033[91m✗\033[0m  заглушка")
                Debug.info("   ^^^[check_url]^^^\n")
                return False

            result = urlparse(url)
            if all([result.scheme, result.netloc]): Debug.info(f"      [check_url] \033[38;5;108m✓\033[0m {url}")
            else: Debug.info(f"      [check_url] \033[91m✗\033[0m {url}")
            Debug.info("   ^^^[check_url]^^^\n")

            return all([result.scheme, result.netloc])
        except:
            Debug.error(f"      [check_url] {url}")
            Debug.info("   ^^^[check_url]^^^\n")
            return False

class WebBrowser(QMainWindow):
    url_changed = pyqtSignal(str)  # Сигнал для уведомления об изменении URL
    def __init__(self, splash=None):
        super().__init__()
        self.splash = splash
        
        if self.splash:
            self.splash.update_progress(15, "Инициализация...")
            QApplication.processEvents()
            time.sleep(TEST_SPLASH)

        self.check_timeout = 0.7 # в секундах
        self.update_available = False
        self.latest_version = None
        

        # История навигации для кнопки "Назад"
        self.navigation_history = []
        self.current_history_index = -1
        
        if self.splash:
            self.splash.update_progress(30, "Проверка версии...")
            QApplication.processEvents()
            time.sleep(TEST_SPLASH)
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

       
        # Устанавливаем значения по умолчанию для цветов до загрузки настроек
        self.default_border_color = QColor(49, 113, 49, 150)  # Цвет рамки по умолчанию
        self.default_back_color = QColor(28, 29, 34, 255)     # Цвет шапки по умолчанию
        self.default_bottom_color = QColor(15, 41, 44, 255)   # Цвет подвала по умолчанию
        self.no_color = "no" # no - нет, gray - серый, black - Черный цвет шапки и подвала когда показываем плашки
        self.snow_man_swch = True
        self.snow_man = False
        self.title_device = True
        # Инициализируем переменные с значениями по умолчанию до загрузки из файла
        self.show_names = True         # Показывать имена устройств
        self.gluon_only = True         # Искать только GLUON (предполагается из контекста)
        self.wled_search = True        # Искать устройства WLED
        self.ha_search = True          # Искать устройства Home Assistant
        self.ha_port = 8123            # Порт Home Assistant

        self.window_width = self.default_width  # Значение по умолчанию задано ранее в __init__
        self.window_height = self.default_height  # Значение по умолчанию задано ранее в __init__
        self.zoom_factor = self.default_zoom_factor  # Значение по умолчанию задано ранее в __init__
        self.custom_colors_enabled = False  # Использовать пользовательские цвета всегда
        self.check_timeout = 0.7       # Таймаут проверки доступности (в секундах)
        self.custom_border_color = self.default_border_color  # Изначально используем цвет по умолчанию
        self.custom_back_color = self.default_back_color      # Изначально используем цвет по умолчанию
        self.custom_bottom_color = self.default_bottom_color  # Изначально используем цвет по умолчанию
        self.device_custom_colors = {}  # Словарь для индивидуальных цветов устройств

        if self.splash:
            self.splash.update_progress(45, "Загрузка настроек...")
            QApplication.processEvents()
            time.sleep(TEST_SPLASH)

        # Загружаем настройки из settings.json, если файл существует
        if os.path.exists("settings.json"):
            with open("settings.json", 'r') as f:
                # Загружаем JSON-данные в словарь
                settings = json.load(f)

                # Устанавливаем базовые настройки с учётом значений по умолчанию из экземпляра
                self.show_names     = settings.get("show_names", self.show_names)
                self.gluon_only     = settings.get("gluon_only", self.gluon_only)
                self.wled_search    = settings.get("wled_search", self.wled_search)
                self.ha_search      = settings.get("ha_search", True)
                self.ha_port        = settings.get("ha_port", 8123)
                self.snow_man_swch  = settings.get("snow_man_swch", self.snow_man_swch)
                self.snow_man       = settings.get("snow_man", self.snow_man)
                self.title_device   = settings.get("title_device", self.title_device)
                # Ограничиваем размеры окна в пределах MIN и MAX значений
                self.window_width   = max(self.MIN_WINDOW_WIDTH, min(settings.get("window_width", self.window_width), self.MAX_WINDOW_WIDTH))
                self.window_height  = max(self.MIN_WINDOW_HEIGHT, min(settings.get("window_height", self.window_height), self.MAX_WINDOW_HEIGHT))
                self.zoom_factor    = settings.get("zoom_factor", self.zoom_factor)

                # Загружаем флаг пользовательских цветов
                self.custom_colors_enabled = settings.get("custom_colors_enabled", self.custom_colors_enabled)
                self.check_timeout = settings.get("check_timeout", self.check_timeout)

                # Загружаем пользовательские цвета из файла или используем значения по умолчанию
                border_color = settings.get("custom_border_color", 
                                            [self.default_border_color.red(),
                                             self.default_border_color.green(),
                                             self.default_border_color.blue(),
                                             self.default_border_color.alpha()])  # Цвет рамки
                back_color = settings.get("custom_back_color",
                                          [self.default_back_color.red(),
                                           self.default_back_color.green(),
                                           self.default_back_color.blue(),
                                           self.default_back_color.alpha()])  # Цвет шапки
                bottom_color = settings.get("custom_bottom_color",
                                            [self.default_bottom_color.red(),
                                             self.default_bottom_color.green(),
                                             self.default_bottom_color.blue(),
                                             self.default_bottom_color.alpha()])  # Цвет подвала

                # Устанавливаем пользовательские цвета на основе загруженных значений
                self.custom_border_color = QColor(*border_color)
                self.custom_back_color = QColor(*back_color)
                self.custom_bottom_color = QColor(*bottom_color)

                # Загружаем индивидуальные цвета для устройств
                self.device_custom_colors = settings.get("custom_colors", self.device_custom_colors)

       
        self.credentials = {} # учетные данные для веб 
       # Применяем загруженные размеры окна
        self.resize(int(self.window_width), int(self.window_height))
        self.browser = QWebEngineView()  
        self.browser.setZoomFactor(self.zoom_factor)  # Применяем масштаб
       
       # Устанавливаем начальные цвета
        self.border_color = self.default_border_color
        self.back_color = self.default_back_color
        self.bottom_color = self.default_bottom_color
       
        self.load_settings()
        self.migrate_discovered_devices() # Преобразуем в новый формат хранения

       
        # Устанавливаем флаг для скрытия заголовка окна
        self.setWindowFlags(Qt.WindowType.FramelessWindowHint)

        # Устанавливаем прозрачный фон для окна
        self.setAttribute(Qt.WidgetAttribute.WA_TranslucentBackground)

        if self.splash:
            self.splash.update_progress(65, "Настройка браузера...")
            QApplication.processEvents()
            time.sleep(TEST_SPLASH)

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
            Debug.info(f"Ошибка при настройке профиля браузера: {e}")
            # Используем временный профиль если не удалось создать постоянный
            self.profile = QWebEngineProfile("myprofile", self)
        
        # Создаем браузер с настроенным профилем
        #self.browser = QWebEngineView() переехал выше чтобы применить масштаб
        self.browser.setPage(QWebEnginePage(self.profile, self.browser))
        self.browser.loadFinished.connect(self.finish_loading)
        # Сигнал авторизации
        self.browser.page().authenticationRequired.connect(self.handle_authentication)

        if self.splash:
            self.splash.update_progress(75, "Создание интерфейса...")
            QApplication.processEvents()
            time.sleep(TEST_SPLASH)
        self.accent_color = "#37a93c"
        self.initUI()
        
        if self.splash:
            self.splash.update_progress(90, "Финальные штрихи...")
            QApplication.processEvents()
            time.sleep(TEST_SPLASH)
        


        self.browser.setContextMenuPolicy(Qt.ContextMenuPolicy.CustomContextMenu)
        self.browser.customContextMenuRequested.connect(self.show_context_menu)

        # Устанавливаем начальный масштаб браузера
        self.browser.setZoomFactor(self.zoom_factor)
        # Переменные для перемещения окна
        self.dragging = False
        self.offset = QPoint()

        # Флаг для отслеживания скрытия окна
        self.hidden_flag = False


        

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
        
        self.set_title(True)
        self.set_icon() 

        #self.load_last_url()
        # сигнал изменения URL
        self.browser.urlChanged.connect(self.update_url)
        # Проверяем доступность
        QtCore.QTimer.singleShot(0, self.init_device)

    def finish_loading(self):
        Debug.yellow("=== FINISH LOADING PAGE BROWSER ===")
        
        if getattr(self, 'is_loading_placeholder', False):
            self.is_loading_placeholder = False
            Debug.yellow("    [finish_loading] Загружен плейсхолдер, пропускаем обработку")
            return        
        


        self.update_colors()
        # Запускаем асинхронную проверку типа устройства
        self.check_type_device_async()

    def initUI(self):
        # Кнопка "Назад"
        self.back_button = QPushButton()
        self.back_button.setFixedSize(20, 20)
        self.back_button.setIcon(QIcon(resource_path("back.png")))
        self.back_button.setStyleSheet("background-color: transparent; border: none; margin-right:4px;")
        self.back_button.clicked.connect(self.go_back)
        self.back_button.setVisible(False)
        # строка ввода  
        self.address_input = QLineEdit()
        self.address_input.setPlaceholderText("Введите URL или IP")
        self.address_input.setObjectName("addressInput")
        #self.address_input.returnPressed.connect(self.load_page)
        #self.address_input.setMinimumWidth(170)  # ширина
        self.address_input.setStyleSheet("background-color: rgba(200, 0, 0, 0); border-radius: 9px; ")
        self.address_input.returnPressed.connect(self.load_page)
        self.address_input.textChanged.connect(self.adjust_address_input_width)  # сигнал
        self.adjust_address_input_width()  # начальная ширина
        # Aвтодополнение с устройствами
        self.device_model = self.load_devices_for_autocomplete()
        
        self.completer = QCompleter(self.device_model, self)
        self.completer.setCaseSensitivity(Qt.CaseSensitivity.CaseInsensitive)
        self.completer.setCompletionMode(QCompleter.CompletionMode.PopupCompletion)
        self.completer.setFilterMode(Qt.MatchFlag.MatchContains)
        self.address_input.setCompleter(self.completer)
        self.update_completer_style()
        self.completer.setMaxVisibleItems(20)
        

        # Cигнал для загрузки выбранного устройства из автодополнения
        self.completer.activated.connect(self.load_selected_device)

        # Переопределяем mousePressEvent для показа полного списка при клике
        self.address_input.mousePressEvent = self.show_completer
      
        if os.path.exists("discovered_devices.json"):
                with open("discovered_devices.json", 'r') as f:
                    devices = json.load(f)
                    if devices:
                        if self.show_names:
                            self.address_input.setText(devices[0].get('name', ''))
                            
                            Url.real_url = devices[0].get('url', '')
                            Debug.magenta(f"→ Текущий адрес: {Url.real_url}")
                        else:
                            self.address_input.setText(devices[0].get('url', ''))
        


        #  кнопка ообновить
        self.go_button = QPushButton()
        self.go_button.setFixedSize(20, 20)  #  размер кнопки
        self.go_button.setIcon(QIcon(resource_path("refresh.png")))
        self.go_button.setStyleSheet("background-color: transparent; border: none; margin-right:4px;")  # Прозрачный фон
        self.go_button.clicked.connect(self.refresh_page)

        #  кнопка поиск
        self.scan_button = QPushButton()
        self.scan_button.setFixedSize(20, 20)  #  размер кнопки
        self.scan_button.setIcon(QIcon(resource_path("scan.png")))
        self.scan_button.setStyleSheet("background-color: transparent; border: none;")  # Прозрачный фон
        self.scan_button.clicked.connect(self.open_menu)

        # чекбокс поверх окон
        #self.checkbox = QCheckBox()
        #self.checkbox.stateChanged.connect(self.toggle_on_top)


        self.indicator_visible = False  # Флаг видимости индикатора
        self.indicator_alpha = 0  # Текущая прозрачность (0-150)
        self.indicator_max_alpha = 150  # Максимальная прозрачность
        self.indicator_animation_timer = QTimer(self)
        self.indicator_animation_timer.timeout.connect(self.animate_indicator)
        self.indicator_color = QColor(255, 0, 0, 0)  # Красный цвет с нулевой прозрачностью
        self.indicator_fading_in = True  # анимации (True - включение, False - выключение)

        # Таймер для проверки связи каждые 5 секунд
        self.rssi_check_timer = QTimer(self)
        self.rssi_check_timer.timeout.connect(self.check_rssi)
        self.rssi_check_timer.start(5000)  # 5000 мс = 5 секунд

        # Кнопка "Свернуть"
        self.minimize_button = QPushButton()
        self.minimize_button.setIcon(QIcon.fromTheme("window-minimize"))  # Иконка "Свернуть"
        self.minimize_button.setFixedSize(20, 20)  #  размер кнопки
        self.minimize_button.setIcon(QIcon(resource_path("minimize.png")))
        self.minimize_button.setStyleSheet("background-color: transparent; border: none;")  # Прозрачный фон
        self.minimize_button.clicked.connect(self.showMinimized)  # Действие при нажатии

        # Кнопка "Закрыть"
        self.close_button = QPushButton()
        self.close_button.setFixedSize(20, 20)  #  размер кнопки
        self.close_button.setIcon(QIcon(resource_path("close.png")))
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
        #buttons_layout.addWidget(self.checkbox)
        buttons_layout.addWidget(self.back_button)
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
    
    def set_title(self, start) :
        device_name = ""
        if start == True:
            if not self.snow_man_swch:
                device_name = "Remote Settings Management"
            elif not self.snow_man: 
                    device_name = "Remote Settings Management"
            else:
                device_name = "Red Snow Man"
           
            self.setWindowTitle(device_name)
            return 

        if not self.title_device:
            if not self.snow_man_swch: 
                device_name = "Remote Settings Management"
            elif not self.snow_man:
                    device_name = "Remote Settings Management"
            else :
                device_name = "Red Snow Man"
                
           
            self.setWindowTitle(device_name)
            return
        
      
        if os.path.exists("discovered_devices.json"):
            with open("discovered_devices.json", 'r') as f:
                devices = json.load(f)
                if devices:
                    device_name = devices[0].get('name', '')
                else: 
                    device_name = self.address_input.text().strip()



        
        if device_name == "http://no-devices/" or device_name == "":
            if not self.snow_man_swch: 
                    device_name = "Remote Settings Management"
            
            
            elif not self.snow_man:
                device_name = "Remote Settings Management"
            else :
                device_name = "Red Snow Man"
        else :
            device_name = device_name   + " - " + NAME

        self.setWindowTitle(device_name)


    def add_to_history(self, url):
        """Добавляет URL в историю навигации"""
        if not url or url.startswith('data:text/html') or url in ["http://no-devices/", "http://loading/"]:
            return

        # Удаляем все записи после текущей позиции (если пользователь шел назад)
        if self.current_history_index < len(self.navigation_history) - 1:
            self.navigation_history = self.navigation_history[:self.current_history_index + 1]

        # Добавляем новый URL, если он отличается от последнего
        if not self.navigation_history or self.navigation_history[-1] != url:
            self.navigation_history.append(url)
            self.current_history_index = len(self.navigation_history) - 1

        self.update_back_button_visibility()
    def clear_history_for_new_device(self, new_url):
        """Очищает историю при переходе на новое устройство"""
        if not new_url:
            return

        #new_base_url = Url.get_base_url(new_url)
        new_base_url = new_url

        # Если история пуста или это новое устройство - очищаем историю
        if not self.navigation_history:
            return

        last_base_url = Url.get_base_url(self.navigation_history[-1]) if self.navigation_history else ""

        if new_base_url != last_base_url:
            Debug.info(f"Переход на новое устройство: {new_base_url}, очищаем историю")
            self.navigation_history.clear()
            self.current_history_index = -1
            self.update_back_button_visibility()
    def go_back(self):
        """Переходит к предыдущей странице в истории"""
        if self.current_history_index > 0:
            self.current_history_index -= 1
            previous_url = self.navigation_history[self.current_history_index]
            Debug.info(f"Переход назад к: {previous_url}")

            # Загружаем предыдущую страницу без добавления в историю
            #self.browser.setUrl(QUrl(previous_url))
            #self.address_input.setText(previous_url if not self.show_names else self.get_device_name_from_url(previous_url))

            self.load_page(previous_url)

            self.update_back_button_visibility()
    def update_back_button_visibility(self):
        """Обновляет видимость кнопки "Назад" """
        can_go_back = self.current_history_index > 0
        self.back_button.setVisible(can_go_back)



    def update_completer_style(self):
        """  """
        Debug.info("[update_completer_style] обновление стиля списка устройств")

        back_color = getattr(self, 'back_color', QColor(28, 29, 34, 255))
        back_color_hex = back_color.darker(190).name(QColor.NameFormat.HexRgb)
        current_accent = getattr(self, 'border_color', QColor(55, 169, 60)).name(QColor.NameFormat.HexRgb)
        Debug.info(f"[update_completer_style]: back_color={back_color_hex}, accent={current_accent}")
        
        if self.no_color != "no":
            current_accent = QColor(30, 30, 30, 255).name(QColor.NameFormat.HexRgb)
            back_color_hex = QColor(20, 20, 20, 255).name(QColor.NameFormat.HexRgb)
            Debug.info("[update_completer_style] устройство недоступно цвет списка - серенький")

        
        self.completer.popup().setStyleSheet(f"""
            QListView {{
                background-color: {back_color_hex};
                color: #FFFFFF;
                border: 1px solid #2b2b2b;
                border-radius: 2px;
                padding: 8px 0px 4px 0px;
                max-height: 750px;
                min-width: 260px;
            }}
            QListView::item {{
                padding: 4px 3px 4px 0px;
                color: #FFFFFF;
            }}
            QListView::item:selected {{
                background-color: {current_accent};
                color: #FFFFFF;
            }}
        """)



    def check_rssi(self):
        """ проверка связи с устройством """
        url = Url.get_base_url(self.browser.url().toString())
        if not Url.check_url(url):
            Debug.error(f"[check_rssi] Некорректный URL:  {url}")
            return
        self.type_device = self.get_device_type_from_url(url)
        
        def check_availability():
            try:
                response = requests.get(url, timeout=3)
                if response.status_code < 500:
                    self.stop_indicator_animation()
                else:
                    self.start_indicator_animation()
            except:
                self.start_indicator_animation()


        if self.type_device == "settings":
            script = """
                var rssiElement = document.querySelector('.rssi');
                var titleValue = rssiElement ? rssiElement.getAttribute('title') : 'N/A';
                titleValue;
            """
            self.browser.page().runJavaScript(script, self.handle_rssi)
        elif self.type_device == "wled":
            threading.Thread(target=check_availability, daemon=True).start() 
            
        elif self.type_device == "homeassistant":
            threading.Thread(target=check_availability, daemon=True).start()    
        else: 
            threading.Thread(target=check_availability, daemon=True).start()





    def handle_rssi(self, result):
        if result == "0%":
            self.start_indicator_animation()  # Включаем индикатор, если title = "0%"
        elif result and result != "0%":
            self.stop_indicator_animation()  # Выключаем индикатор, если title > "0%"



    def init_device(self):
        Debug.info("=== ПРОВЕРКА И ОТКРЫТИЕ УСТРОЙСТВА ===")

        # Проверка существования файла устройств
        if os.path.exists("discovered_devices.json"):
            Debug.info("\033[38;5;108m✓\033[0m Файл discovered_devices.json найден")

            try:
                with open("discovered_devices.json", 'r') as f:
                    devices = json.load(f)
                    Debug.info(f"\033[38;5;108m✓\033[0m Файл успешно загружен, найдено устройств: {len(devices)}")

                    if devices:
                        device = devices[0]
                        url = device.get('url', '')
                        name = device.get('name', 'Без имени')

                        Debug.info(f"→ Выбрано первое устройство:")
                        Debug.info(f"  Имя: {name}")
                        Debug.info(f"  URL: {url}")
                        Debug.info(f"→ Начинаем загрузку страницы...")
                        # ОТКРЫТИЕ СТРАНИЦЫ
                        self.load_page(url)

                    else:
                        Debug.warning("⚠ Список устройств пуст")
                        Debug.info("→ Показываем заглушку 'Нет устройств'")
                        self.show_no_devices_placeholder()

            except json.JSONDecodeError as e:
                Debug.error(f"✗ Ошибка чтения JSON: {e}")
                Debug.info("→ Показываем заглушку 'Нет устройств'")
                self.show_no_devices_placeholder()

            except Exception as e:
                Debug.error(f"✗ Неожиданная ошибка: {e}")
                Debug.info("→ Показываем заглушку 'Нет устройств'")
                self.show_no_devices_placeholder()

        else:
            Debug.warning("✗ Файл discovered_devices.json не найден")
            Debug.info("→ Показываем заглушку 'Нет устройств'")
            self.show_no_devices_placeholder()

        Debug.info("=== КОНЕЦ ПРОВЕРКИ И ОТКРЫТИЯ УСТРОЙСТВА ===\n")


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
                Debug.info("Поменян формат файла discovered_devices на новую версию")


    def load_settings(self):
        # Проверяем, существует ли файл настроек settings.json
        if os.path.exists("settings.json"):
            # Открываем файл для чтения
            with open("settings.json", 'r') as f:
                # Загружаем JSON-данные в словарь
                settings = json.load(f)

                # Устанавливаем базовые настройки с значениями по умолчанию
                self.show_names     = settings.get("show_names", True)  # Показывать имена устройств
                self.gluon_only     = settings.get("gluon_only", False)  # Искать только GLUON - нет
                self.wled_search    = settings.get("wled_search", True)  # Искать устройства WLED
                self.ha_search      = settings.get("ha_search", True)
                self.ha_port        = settings.get("ha_port", 8123)
                self.snow_man_swch  = settings.get("snow_man_swch", True)  # Искать устройства WLED
                self.snow_man       = settings.get("snow_man", False)  # Искать устройства WLED
                self.title_device   = settings.get("title_device", True)  # Искать устройства WLED
                self.window_width   = settings.get("window_width", 800)  # Ширина окна
                self.window_height  = settings.get("window_height", 600)  # Высота окна
                self.zoom_factor    = settings.get("zoom_factor", 1.0)  # Масштаб браузера
                self.check_timeout  = settings.get("check_timeout", 0.7)  # Таймаут проверки

                # Устанавливаем флаг использования пользовательских цветов
                self.custom_colors_enabled = settings.get("custom_colors_enabled", False)

                # Определяем цвета по умолчанию из файла или используем значения по умолчанию из класса
                border_color = settings.get("custom_border_color", 
                                            [self.default_border_color.red(),
                                             self.default_border_color.green(),
                                             self.default_border_color.blue(),
                                             self.default_border_color.alpha()])  # Цвет рамки
                back_color = settings.get("custom_back_color",
                                          [self.default_back_color.red(),
                                           self.default_back_color.green(),
                                           self.default_back_color.blue(),
                                           self.default_back_color.alpha()])  # Цвет шапки
                bottom_color = settings.get("custom_bottom_color",
                                            [self.default_bottom_color.red(),
                                             self.default_bottom_color.green(),
                                             self.default_bottom_color.blue(),
                                             self.default_bottom_color.alpha()])  # Цвет подвала

                # Устанавливаем пользовательские цвета на основе загруженных значений
                self.custom_border_color = QColor(*border_color)  # Акцентный цвет и рамка
                self.custom_back_color = QColor(*back_color)      # Цвет шапки
                self.custom_bottom_color = QColor(*bottom_color)  # Цвет подвала

                # Загружаем индивидуальные цвета для устройств
                self.device_custom_colors = settings.get("custom_colors", {})  # Словарь с цветами для устройств

                # Загружаем учетные данные
                if "credentials" in settings:
                    for url, cred in settings["credentials"].items():
                        try:
                            # Декодируем логин и пароль из base64
                            login = base64.b64decode(cred["login"]).decode('utf-8')
                            password = base64.b64decode(cred["password"]).decode('utf-8')
                            self.credentials[url] = {"login": login, "password": password}
                        except Exception as e:
                            Debug.info(f"Ошибка расшифровки учетных данных для {url}: {e}")
        else:
            # Если файла нет, инициализируем пустой словарь учетных данных
            self.credentials = {}



    def get_device_custom_colors(self, url):
        return self.device_custom_colors.get(url, {})

    def credentials_dict(self):
        credentials_encoded = {}
        for url, cred in self.credentials.items():
            try:
                credentials_encoded[url] = {
                    "login": base64.b64encode(cred["login"].encode('utf-8')).decode('utf-8'),
                    "password": base64.b64encode(cred["password"].encode('utf-8')).decode('utf-8')
                }
            except Exception as e:
                Debug.info(f"Ошибка кодирования учетных данных для {url}: {e}")
        return credentials_encoded
    
    def save_settings(self):
        settings = {
            "show_names": self.show_names,
            "gluon_only": self.gluon_only,
            "wled_search": self.wled_search,
            "ha_search": self.ha_search,
            "ha_port": self.ha_port,
            "snow_man_swch": self.snow_man_swch,
            "snow_man":    self.snow_man,
            "title_device":    self.title_device,
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
            "custom_bottom_color": [
                                self.custom_bottom_color.red(),
                                self.custom_bottom_color.green(),
                                self.custom_bottom_color.blue(),
                                self.custom_bottom_color.alpha()
            ],
            "custom_colors": self.device_custom_colors,
            "credentials": self.credentials_dict()
        }
        try:
            with open("settings.json", 'w') as f:
                json.dump(settings, f, indent=4)
            Debug.info("Настройки сохранены")
        except Exception as e:
            Debug.info(f"Ошибка при сохранении настроек: {e}")

    def load_device_colors(self, url):
        """Загружает цвета для конкретного устройства"""
        device_colors = self.device_custom_colors.get(url, {})
        if device_colors and device_colors.get("use_custom_colors", False):
            self.accent_color = QColor(*device_colors["accent"]).name(QColor.NameFormat.HexRgb)
            self.border_color = QColor(*device_colors["accent"])
            self.back_color = QColor(*device_colors["back"])
            self.bottom_color = QColor(*device_colors.get("bottom", device_colors["back"]))
            return True
        return False
    


    def update_colors(self, current_url = None):

        current_url = Url.get_base_url(self.browser.url().toString())
        caller = inspect.stack()[1].function
        Debug.blue(f"===[update_colors] ← {caller} | вызван для URL: {current_url} ===")


        if not Url.check_url(current_url):
            Debug.blue("    [update_colors]  заглушка или неверный урл")
            Debug.blue(f"^^^[update_colors]^^^\n")
            return

        

        


        device_colors = self.get_device_custom_colors(current_url)

        # Приоритет 1: Индивидуальные цвета устройства (независимо от custom_colors_enabled)
        if device_colors and device_colors.get("use_custom_colors", False):
            Debug.blue("    Используем индивидуальные цвета устройства")
            self.border_color = QColor(*device_colors["accent"])
            self.back_color = QColor(*device_colors["back"])
            self.bottom_color = QColor(*device_colors.get("bottom", device_colors["back"]))
            self.accent_color = self.border_color.name(QColor.NameFormat.HexRgb)
        # Приоритет 2: Глобальные пользовательские цвета (когда снята галка "Определять автоматически" И нет индивидуальных цветов)
        elif self.custom_colors_enabled:
            Debug.blue("    Используем глобальные пользовательские цвета")
            self.border_color = self.custom_border_color
            self.back_color = self.custom_back_color
            self.bottom_color = self.custom_bottom_color
            self.accent_color = self.border_color.name(QColor.NameFormat.HexRgb)
        # Приоритет 3: Автоматическое определение
        else:
            device_type = self.get_device_type_from_url(current_url)

            Debug.blue(f"    Автоматическое определение цветов для: {device_type}, URL: {current_url}")
            if device_type == 'wled':
                Debug.blue("    Вызываем WLED методы получения цветов")
                # Для WLED 
                self.get_wled_accent_color()
                self.get_wled_back_color()
            elif device_type == 'settings':
                Debug.blue("    Вызываем Settings методы получения цветов")
                # Для Settings 
                self.get_sett_accent_color()
                self.get_sett_back_color()
            elif device_type == 'homeassistant':
                Debug.blue("    Вызываем Home Assistant методы получения цветов")
                # на самом деле проверяем универсально
                self.get_any_back_color()
                self.get_any_accent_color()
            else:
                Debug.blue("    Вызываем универсальные методы получения цветов")
                self.get_any_back_color()
                self.get_any_accent_color()

            
            
            

            Debug.blue(f"^^^[update_colors]^^^\n")
            return

        # Применяем стили
        brightness = self.calculate_brightness(self.accent_color)
        text_color = "#FFFFFF" if brightness < 128 else "#000000"
        combined_style = self.get_combined_styles(self.accent_color, text_color)
        self.setStyleSheet(combined_style)
        self.update()
        self.update_completer_style()  


        Debug.blue(f"^^^ [update_colors] ^^^\n")

    def check_type_device_async(self, url=None):
        if not url:
            url = Url.get_base_url(self.browser.url().toString())
        
        if not Url.check_url(url):
            Debug.cyan("[check_type_device_async]  заглушка или неверный урл")
            return


        Debug.cyan(f"→→→  [check_type_device_async] Асинхронная проверка типа устройства: {url}")
        # Запускаем асинхронную проверку типа устройства
        worker = CheckDeviceTypeWorker(url, self.check_timeout)
        worker.signals.result.connect(lambda device_type: self.handle_async_device_type_result(url, device_type))
        QThreadPool.globalInstance().start(worker)
        
    def handle_async_device_type_result(self, url, device_type):
        Debug.cyan("=== ОБРАБОТКА ТИПА ===")
        Debug.cyan(f"   URL: {url}, тип: {device_type}")
        
        devices = self.load_devices_from_file()
        if url in [d.get('url', '') for d in devices]:
            device = next(d for d in devices if d.get('url', '') == url)
            if 'type' not in device or device.get('type') == 'n/a' or device.get('type') != device_type:
                device['type'] = device_type
                # Сохраняем изменения
                devices = [d for d in devices if d.get('url', '') != url]
                devices.insert(0, device)
                with open("discovered_devices.json", 'w') as f:
                    json.dump(devices, f, indent=4)
                Debug.warning(f"    → Тип устройства обновлен: {device_type}")
               
                self.update_colors()
                self.load_devices_for_autocomplete()
                self.update_completer_style()

            elif device.get('type') == device_type:
                Debug.cyan(f"   \033[38;5;108m✓\033[38;5;116m  Тип устройства соответсвует сохраненому: {device_type}")
                
            else:
                Debug.error(f"  ✗ Ошибка назначения типа устройства: {device_type}")
                











        Debug.cyan("^^^ ОБРАБОТКА ТИПА ^^^")

    def get_device_type_from_url(self, url):
        """Вернет тип устройства по его URL из сохраненных устройств.
           Если не найдет, то вернет 'n/a'."""

        
        #Debug.info(f"===[GET_DEVICE_TYPE_FROM_URL]===")
        caller = inspect.stack()[1].function
        Debug.info(f"===[GET_DEVICE_TYPE_FROM_URL] ← {caller} ===")

        original_url = url
        circumcised_url = Url.get_base_url(url, True)

        if Url.check_url(url):
            Debug.info(f"   полученный url:  {original_url}")
        else:
            Debug.info(f"   \033[91m✗\033[0m не подходящий урл, тикаем")
            return 'n/a'

        url = Url.get_base_url(url)
        Debug.info(f"   укороченный url: {url}")
        Debug.info(f"   обрезанный url: {circumcised_url}")
        Debug.info(f"   реальный url:   {Url.real_url}")

        devices = self.load_devices_from_file()

        # Нормализуем URL для сравнения (убираем завершающий слеш и http в начале)
        def normalize_url(u):
            u = u.rstrip('/')
            u = u.replace('https://', '').replace('http://', '')
            return u

        normalized_url = normalize_url(url)
        normalized_original = normalize_url(original_url)
        normalized_circumcised = normalize_url(circumcised_url)

        for device in devices:
            device_url = normalize_url(device.get('url', ''))


            if device_url == normalized_url:
                device_type = device.get('type', 'n/a')
                device_name = device.get('name', '---')
                Debug.success(f" укороченный url соответствует устройству {device_name}, тип: {device_type}")
                Debug.info(f"^^^[GET_DEVICE_TYPE_FROM_URL]^^^")
                return device_type

            if device_url == normalized_original:
                device_type = device.get('type', 'n/a')
                device_name = device.get('name', '---')
                Debug.success(f" оригинальный url соответствует устройству, тип: {device_type}")
                Debug.info(f"^^^[GET_DEVICE_TYPE_FROM_URL]^^^")
                return device_type

            if device_url == normalized_circumcised:
                device_type = device.get('type', 'n/a')
                device_name = device.get('name', '---')
                Debug.success(f" обрезанный url соответствует устройству, тип: {device_type}")
                Debug.info(f"^^^[GET_DEVICE_TYPE_FROM_URL]^^^")
                return device_type

        Debug.warning(f"   \033[91m✗\033[0m устройство не найдено, возвращаем: 'n/a'")
        Debug.info(f"^^^[GET_DEVICE_TYPE_FROM_URL]^^^")
        return 'n/a'

    def get_device_name_from_url(self, url):
        """ вернет имя устройства по его url из сохраненных устройств \n
            если не найдет, то вернет url """
        
        
        #Debug.info(f"===[GET_DEVICE_NAME_FROM_URL]===")

        caller = inspect.stack()[1].function
        Debug.info(f"===[GET_DEVICE_NAME_FROM_URL] ← {caller} ===")

        original_url = url
        circumcised_url = Url.get_base_url(url, True)
       
        if Url.check_url(url):
            Debug.info(f"   полученый url:  {original_url}")
        else:
            Debug.info(f"   \033[91m✗\033[0m не подходящий урл, тикаем") 
            return

        url = Url.get_base_url(url)
        Debug.info(f"   укороченый url: {url}")
        Debug.info(f"   обрезанный url: {circumcised_url}")
        Debug.info(f"   реальный url:   {Url.real_url}")

        devices = self.load_devices_from_file()

        # Нормализуем URL для сравнения (убираем завершающий слеш и хтпп в начале)
        def normalize_url(u):
            u = u.rstrip('/')
            u = u.replace('https://', '').replace('http://', '')
            return u

        
        normalized_url = normalize_url(url)
        normalized_original = normalize_url(original_url)
        normalized_circumcised = normalize_url(circumcised_url)

        for device in devices:
            device_url = normalize_url(device.get('url', ''))

            if device_url == normalized_url:
                name = device.get('name', '')
                
                name = self.has_saved_credentials(url, name)
                Debug.success(f" укороченый url соответствует устройству:  {device.get('name', '')}")
                Debug.info(f"^^^[GET_DEVICE_NAME_FROM_URL]^^^")
                return name
            
            if device_url == normalized_original:
                name = device.get('name', '')
 
                name = self.has_saved_credentials(url, name)
                Debug.success(f" оригинальный url соответствует устройству:  {device.get('name', '')}")
                Debug.info(f"^^^[GET_DEVICE_NAME_FROM_URL]^^^")
                return name            
            
            if device_url == normalized_circumcised:
                name = device.get('name', '')
 
                name = self.has_saved_credentials(url, name)
                Debug.success(f" обрезанный url соответствует устройству:  {device.get('name', '')}")
                Debug.info(f"^^^[GET_DEVICE_NAME_FROM_URL]^^^")
                return name 
        
        Debug.warning(f"   \033[91m✗\033[0m устройство не найдено, возращаем url: {original_url}")
        Debug.info(f"^^^[GET_DEVICE_NAME_FROM_URL]^^^")
        return original_url
    
    def get_device_url_from_name(self, name):
        """ вернет url устройства по его имени из сохраненных устройств \n
            если не найдет, то вернет False """

        #Debug.info(f"===[GET_DEVICE_URL_FROM_NAME]===")
        caller = inspect.stack()[1].function
        Debug.info(f"===[GET_DEVICE_URL_FROM_NAME] ← {caller} ===")
        Debug.info(f"   полученный name:  {name}")

        devices = self.load_devices_from_file()

        for device in devices:
            if device.get('name', '') == name:
                url = device.get('url', '')
                Debug.info(f"   \033[38;5;108m✓\033[0m name соответствует устройству:  {device.get('name', '')}")
                Debug.info(f"^^^[GET_DEVICE_URL_FROM_NAME]^^^")
                return url

        Debug.info(f"   \033[91m✗\033[0m устройство не найдено, возращаем name: {name}")
        Debug.info(f"^^^[GET_DEVICE_URL_FROM_NAME]^^^")
        return None








    def get_favicon_async(self, url=None, check=True):
        if not url:
            url = Url.get_base_url(self.browser.url().toString())

        if not Url.check_url(url):
            Debug.green("[get_favicon_async] заглушка или неверный урл")
            return

        Debug.green(f"→→→ [get_favicon_async] Асинхронная загрузка фавикона: {url}")
        worker = AsyncFaviconLoader(url, check)
        worker.signals.result.connect(lambda favicon_path: self.handle_async_favicon_result(url, favicon_path))
        QThreadPool.globalInstance().start(worker)


    def handle_async_favicon_result(self, url, favicon_path):
        Debug.green("=== ОБРАБОТКА ФАВИКОНА ===")
        Debug.green(f"   URL: {url}, путь: {favicon_path}")

        if favicon_path:
            Debug.green(f"   \033[38;5;108m✓\033[38;5;108m Фавикон загружен: {favicon_path}")
            
        else:
            Debug.green("   \033[91m✗\033[38;5;108m Фавикон не загружен")

        Debug.green("^^^ ОБРАБОТКА ФАВИКОНА ^^^")
  


    def has_saved_credentials(self, url, name=None):
        """Возвращает имя с ⚿ 🔑 🗝️ 🔐 если есть учетные данные, иначе исходное имя\n 
        если имя не передали вернет есть ли учетные данные для урл"""
        base_url = Url.get_base_url(url)
        has_creds = base_url in self.credentials

        if name is None:
            return has_creds

        if has_creds and not name.endswith(" 🔑"):
            return name + " 🔑"
        return name


    def get_device_icon(self, device_type, url = None, name = None, check = None):
        """Возвращает путь к иконке в зависимости от типа устройства""" 

        Debug.green(f"===[get_device_icon] тип: {device_type}, для url {url}, name {name}")

        if device_type == 'settings':
            Debug.green(f"   [get_device_icon] return 'gear.png'")
            Debug.green(f"^^^[get_device_icon]^^^")
            return resource_path('gear.png')
            
        elif device_type == 'wled':
            Debug.green(f"   [get_device_icon] return 'wled.png'")
            Debug.green(f"^^^[get_device_icon]^^^")
            return resource_path('wled.png')
        
        elif device_type == 'homeassistant':
            Debug.green(f"   [get_device_icon] return 'ha.png'")
            Debug.green(f"^^^[get_device_icon]^^^")
            return resource_path('ha.png')

        else:  # n/a или любой другой тип
            try:
                if hasattr(self, 'browser') and self.browser:
                    if not url:
                        url = Url.get_base_url(self.browser.url().toString())
                    if not name:
                        name = self.get_device_name_from_url(url)
                    if not Url.check_url(url): 
                        Debug.error(" [get_device_icon] нет имени или адреса устройства" )
                        Debug.error(f" [get_device_icon] url: {url}, name: {name}")
                        Debug.green(f"   [get_device_icon] return 'unknown.png'")
                        Debug.green(f"^^^[get_device_icon]^^^")
                        return resource_path('unknown.png')
                    
                    current_url = Url.get_base_url(url)
                    # Проверяем, есть ли уже загруженный фавикон
                    parsed = urlparse(current_url)
                    domain = parsed.netloc.replace(':', '_')
                    favicon_file = os.path.join("favicons", f"{domain}.ico")

                    if os.path.exists(favicon_file):
                        Debug.green(f"   [get_device_icon] Найден фавикон: {favicon_file}")
                        return favicon_file
                    else:
                        # Запускаем асинхронную загрузку только если файла нет
                        self.get_favicon_async(current_url, check)
                        return resource_path('unknown.png')

                    #self.get_favicon_async(current_url, check)
                    #Debug.green(f"   [get_device_icon] запросили фавикон асинхронно, а покамест unknown.png")
                    #favicon_path = self.get_favicon(current_url, check)
                    #Debug.green(f"   [get_device_icon] URL для фавикона: {current_url}")
                    #Debug.green(f"   [get_device_icon] return {favicon_path if favicon_path else resource_path('unknown.png')}")
                    #Debug.green(f"^^^[get_device_icon]^^^")
                    #return favicon_path if favicon_path else resource_path('unknown.png')
                    return resource_path('unknown.png')
                
            except Exception as e:
                Debug.error(f"  Ошибка при получении иконки для типа устройства: {device_type}, {e}")
            
            Debug.green(f"   [get_device_icon] return 'unknown.png'")
            Debug.green(f"^^^[get_device_icon]^^^")
            return resource_path('unknown.png')

    def get_favicon(self, url, check = None):
        """Получает фавикон сайта и сохраняет его локально"""
        Debug.green(f"===[get_favicon] Получаем фавикон для: {url}")

        if not Url.check_url(url):
            Debug.green("   [get_favicon] URL пустой или about:blank")
            return None

        try:
            from urllib.parse import urlparse
            parsed = urlparse(url)
            base_url = f"{parsed.scheme}://{parsed.netloc}"
            Debug.green(f"   [get_favicon] Базовый URL: {base_url}")

            # Создаем папку для фавиконов
            favicon_dir = "favicons"
            if not os.path.exists(favicon_dir):
                os.makedirs(favicon_dir)
                Debug.green(f"   [get_favicon] Создана папка: {favicon_dir}")

            # Имя файла на основе домена
            domain = parsed.netloc.replace(':', '_')
            favicon_file = os.path.join(favicon_dir, f"{domain}.ico")
            Debug.green(f"   [get_favicon] Файл фавикона: {favicon_file}")

            # Если фавикон уже есть, возвращаем его
            if os.path.exists(favicon_file):
                Debug.green(f"   [get_favicon] Фавикон уже существует: {favicon_file}")
                return favicon_file

            if not check:
                Debug.warning(f"   [get_favicon] Фавикон отсутсвует, не получаем")
                return resource_path('unknown.png')
            
            # Пробуем получить фавикон
            favicon_urls = [
                f"{base_url}/favicon.ico",
                f"{base_url}/favicon.png",
                f"{base_url}/apple-touch-icon.png"
            ]

            for favicon_url in favicon_urls:
                Debug.green(f"   [get_favicon] Пробуем загрузить: {favicon_url}")
                try:
                    response = requests.get(favicon_url, timeout=2, verify=False)
                    Debug.green(f"   [get_favicon] Статус: {response.status_code}, Размер: {len(response.content)}")

                    if response.status_code == 200 and len(response.content) > 100:
                        with open(favicon_file, 'wb') as f:
                            f.write(response.content)
                        Debug.green(f"   [get_favicon] Фавикон сохранен: {favicon_file}")
                        return favicon_file
                except Exception as e:
                    Debug.green(f"   [get_favicon] Ошибка загрузки {favicon_url}: {e}")
                    continue

            Debug.green("   [get_favicon] Не удалось загрузить фавикон")
            return None
        except Exception as e:
            Debug.green(f"   [get_favicon] Общая ошибка: {e}")
            return None


    def handle_authentication(self, url, authenticator):
        url_str = url.toString()
        saved_login = self.credentials.get(url_str, {}).get("login", "")
        saved_password = self.credentials.get(url_str, {}).get("password", "")
        
        Debug.info(f"Запрос авторизации: {url_str}")
        dialog = AuthDialog(url_str, authenticator.realm(), saved_login, saved_password, self)
        
      
        if dialog.exec() == QDialog.DialogCode.Accepted:
            login, password, remember = dialog.get_credentials()
            #Debug.info(f"Введено: login={login}, password={password}, remember={remember}")
            
            if login and password:  # Проверяем что введено
                authenticator.setUser(login)
                authenticator.setPassword(password)
           
            
                if remember and login and password:
                    Debug.info(f"Сохранили учетные данные {url_str}")
                    self.credentials[url_str] = {"login": login, "password": password}
                    self.save_settings()  # Сохраняем все настройки
            else:
             # Если пользователь нажал ок но не ввел данные
                self.show_device_unavailable_placeholder(url_str)
                authenticator.setUser("")  # Отменяем
      
        else:
            authenticator.setUser("")
            Debug.info("Авторизация отменена")
            self.show_device_unavailable_placeholder(url_str)
            authenticator.setUser("")  

    def show_device_unavailable_placeholder(self, url):
        Debug.warning(f"[placeholder] Устройство недоступно: {url}")
        self.swap_header_footer_colors()
        error_html = f"""
        <!DOCTYPE html>
        <html lang="en" style="--accent: #150000;">
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
                    color: #140000;
                    width: 10px;
                    height: 10px;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    position: relative;
                    transition: font-size 2s ease, color 2s ease;
                }}
                .snowman-container .eyes {{
                    display: none;
                    position: absolute;
                    top: -6px;
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
                    transition: opacity 2s, height 2s;
                }}
                .error-icon {{
                    font-size: 48px;
                    
                    cursor: pointer;
                    transition: opacity 1s ease;
                }}
                .snowman {{
                    display: none;
                    opacity: 0;
                    transition: opacity 1s ease;
                }}
                ::selection {{
                                background: #1c1d22; /* Цвет фона выделения */
                                
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
                let bridge;
                new QWebChannel(qt.webChannelTransport, function(channel) {{
                    bridge = channel.objects.pybridge;
                }}); 

                errorIcon.addEventListener('click', () => {{
                    clickCount++;
                    if (clickCount === 2){{
                                   const message = document.querySelector('.message')
                                   message.style.height = '0';
                                   message.style.opacity = '0'; 

                                   const device_info = document.querySelector('.device-info')
                                   device_info.style.display = 'none';

                                   const device_info2 = document.querySelector('.device-info2')
                                   device_info2.style.display = 'none';
                                }}
                    if (clickCount === 10) {{
                        errorIcon.style.opacity = '0';
                        document.body.style.backgroundColor = '#000000';
                        if (bridge) {{
                                bridge.setSnowMan(true);
                                bridge.blck_color();
                            }}

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
                                            snowmanContainer.style.fontSize = "74px";

                                            setTimeout(() => {{
                                                const eyes = document.querySelector('.eyes');
                                                eyes.style.display = 'block';
                                            }}, 2000);

                                            setTimeout(() => {{
                                                snowmanContainer.style.color = '#300000';
                                                snowmanContainer.style.fontSize = "80px"
                                                 setTimeout(() => {{
                                                      msg.textContent = 'Snow Settings {VERSION}'; 
                                                      msg.style.display = 'block'; 
                                                      msg.style.color = '#300000';
                                                      msg.style.height  = 'auto';
                                                      msg.style.opacity = '1'; 
                                                   }}, 2000);
                                            }}, 5000);


                                        }}, 7000);
                                    }}, 5000);


                    }}
                }});
            </script>
        </body>
        </html>
        """

        self.browser.setHtml(error_html, QUrl(url))
        self.address_input.setText(url if not self.show_names else self.get_device_name_from_url(url))
        
        Url.real_url = url
        Debug.magenta(f"→ Текущий адрес: {Url.real_url}")

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
            url = f"https://api.github.com/repos/{REPO}/releases/latest"
            response = requests.get(url, timeout=5)
            if response.status_code == 200:
                data = response.json()
                self.latest_version = data["tag_name"].lstrip("v")  # Убираем 'v' из версии
                if self.latest_version != VERSION:
                    self.update_available = True
                    Debug.info(f"Доступна новая версия: {self.latest_version} (текущая: {VERSION})")
                else:
                    Debug.info(f"Уже последняя версия: {VERSION}")
            else:
                Debug.warning(f"Ошибка проверки версии: {response.status_code}")
        except Exception as e:
            Debug.info(f"Не удалось проверить версию на GitHub: {e}")

    def open_latest_release_page(self):
        if self.latest_version:
            url = f"https://github.com/{REPO}/releases/tag/v{self.latest_version}"

            webbrowser.open(url)

    def load_devices_for_autocomplete(self):
        caller = inspect.stack()[1].function
        Debug.brown(f"\n===[LOAD_DEVICES_FOR_AUTOCOMPLETE]  ← {caller} ===")
        model = QStandardItemModel()
        self.device_map = {}
        name_count = {}
        

        if os.path.exists("discovered_devices.json"):
            with open("discovered_devices.json", 'r') as f:
                devices = json.load(f)
                count = 0
                for device in devices:
                    count += 1
                    check = (count == 1)

                    base_name = device.get('name', '')
                    url = device.get('url', '')
                    device_type = device.get('type', 'n/a')
                    Debug.brown(f"   [load_devices_for_autocomplete] url: {url}, name: {base_name}, type: {device_type}")

                    if base_name in name_count:
                        name_count[base_name] += 1
                        display_name = f"{base_name}({name_count[base_name]})"
                        display_name = self.has_saved_credentials(url, display_name)

                    else:
                        name_count[base_name] = 0
                        display_name = base_name
                        display_name = self.has_saved_credentials(url, display_name)
                    self.device_map[display_name] = url

                    # Создаем элемент с иконкой
                    item = QStandardItem(display_name if self.show_names else url)

                    # Добавляем иконку по типу устройства
                    icon_path = self.get_device_icon(device_type, url, base_name, check)
                    if icon_path and os.path.exists(icon_path):
                        item.setIcon(QIcon(icon_path))

                    model.appendRow(item)
        Debug.brown("^^^[LOAD_DEVICES_FOR_AUTOCOMPLETE]^^^\n")
        return model


    def load_selected_device(self, text):
        """ загрузить выбранное устройство """
        Debug.warning(f"\n=== ВЫБРАНО УСТРОЙСТВО: {text} ===")  
        if text:
            if self.show_names and text in self.device_map:
                url = self.device_map[text]
                #self.show_loading_placeholder(url)
                self.load_page(url, clear=True)
                
            else:
                self.load_page(text, clear=True)
                Debug.error(f" [load_selected_device] неизвестное устройство")
                #self.show_device_unavailable_placeholder(text)
            #self.update_always_on_top_checkbox_style() 
        else: Debug.error(" [load_selected_device] нет устройства")
  
    def show_completer(self, event: QMouseEvent):
        """  """
        self.update_completer_style() 
        self.completer.setCompletionPrefix("")  # Префикс для показа всех устройств
        self.address_input.completer().complete()  #  Список
        QLineEdit.mousePressEvent(self.address_input, event)

    def open_menu(self):
        self.scan_dialog = ScanDialog(self, parent=self)
        self.scan_dialog.wled_search = self.wled_search
        self.scan_dialog.ha_search = self.ha_search
        self.scan_dialog.ha_port = self.ha_port


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
        # Обновить список устройств после закрытия 
        self.scan_dialog.exec()
        
        _load_device_auto = self.load_devices_for_autocomplete()
        self.completer.setModel(_load_device_auto)



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
                        border: 1px solid #9e9e9e;  /* Граница индикатора bababb 4A4A4A */
                        border-radius: 4px;  /* Закругление углов */
                        color: #FFFFFF; /* Белый цвет для стандартной галочки */
                    }}
                    QCheckBox::indicator:checked {{
                        background-color: {color};  /* Фон индикатора при выборе (акцентный цвет) */
                        border: 1px solid #9e9e9e;  /* Граница индикатора при выборе */
                        color: #FFFFFF;  /* Цвет текста (галочки) */
                        image: url(:/qt-project.org/styles/commonstyle/images/standardbutton-apply-32.png);
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
                        border: 1px solid #9e9e9e;
                        border-radius: 4px;
                        color: #FFFFFF;
                    }
                    QCheckBox::indicator:checked {
                        background-color: #00612a;
                        border: 1px solid #9e9e9e;
                        color: #FFFFFF;  /* Цвет текста (галочки) */
                        image: url(:/qt-project.org/styles/commonstyle/images/standardbutton-apply-32.png);
                        
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
                    border: 1px solid #9e9e9e;
                    border-radius: 4px;
                    color: #FFFFFF;
                }
                QCheckBox::indicator:checked {
                    background-color: #00612a;
                    border: 1px solid #9e9e9e;
                    color: #FFFFFF;  /* Цвет текста (галочки) */
                    image: url(:/qt-project.org/styles/commonstyle/images/standardbutton-apply-32.png);
                }
                
            """)

    def update_always_on_top_checkbox_style(self):
        current_url = self.browser.url().toString()
        use_custom_colors = self.custom_colors_enabled or self.device_custom_colors.get(current_url, {}).get("use_custom_colors", False)

        #if use_custom_colors:
            #back_color = self.custom_back_color if self.custom_colors_enabled else QColor(*self.device_custom_colors[current_url]["back"])
        #else:
        #    back_color = QColor(28, 29, 34, 255)  # Цвет по умолчанию
        back_color = getattr(self, 'back_color', QColor(28, 29, 34, 255)) 

        active_color = back_color.darker(150).name(QColor.NameFormat.HexRgb)
        inactive_color = back_color.lighter(110).name(QColor.NameFormat.HexRgb)
        border_color = back_color.lighter(150).name(QColor.NameFormat.HexRgb)
        self.checkbox.setStyleSheet(f"""
            QCheckBox::indicator {{
                background-color: {inactive_color};
                 border: 1px solid #9e9e9e;
                  color: #FFFFFF;
            }}
            QCheckBox::indicator:checked {{
                background-color: {active_color};
                border: 1px solid #9e9e9e;
                color: #FFFFFF;
                image: url(:/qt-project.org/styles/commonstyle/images/standardbutton-apply-32.png);
 
            }}
            
        """)
        self.checkbox.update()
        self.update()

    def show_about_dialog(self):
        about_dialog = AboutDialog(self)
        about_dialog.setWindowFlags(about_dialog.windowFlags() | Qt.WindowType.WindowStaysOnTopHint)  #  флаг "поверх всех"
        about_dialog.exec()



    # упразднено
    def get_colors(self):
        """ получить акцентные цвета для вледа и стеттингс """
   
        # Проверяем тип устройства
        current_url = self.browser.url().toString()
        device_type = self.get_device_type_from_url(current_url)
        if device_type == 'wled':
            self.get_wled_accent_color()
            self.get_wled_back_color()
            return
        elif device_type == 'settings':
            self.get_sett_accent_color()
            self.get_sett_back_color()
        else:
            pass



    def get_sett_back_color(self):
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
        self.browser.page().runJavaScript(script, self.handle_back_color)
    def get_sett_accent_color(self):
        if not hasattr(self, 'browser') or not self.browser:
            return  # Прерываем, если браузер уже удалён
        
        
       
        #if os.path.exists("discovered_devices.json"):
        #    with open("discovered_devices.json", 'r') as f:
        #        devices = json.load(f)
        #        device = next((d for d in devices if d.get('url', '') == current_url), None)
        #        if device and device.get('type') == 'wled':
        #            Debug.info("[get_accent_color] Пропускаем для WLED устройства")
        #            return

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


    def get_wled_back_color(self):
        """Получает цвет шапки/подвала WLED из CSS переменной --c-tb"""
        script = """
        (function() {
            if (document.readyState !== 'complete') {
                setTimeout(arguments.callee, 100);
                return;
            }

            try {
                var rootStyle = window.getComputedStyle(document.documentElement);
                var ctbValue = rootStyle.getPropertyValue('--c-tb');

                if (ctbValue && ctbValue.trim()) {
                    ctbValue = ctbValue.trim();

                    // Конвертируем rgb в hex
                    if (ctbValue.startsWith('rgb')) {
                        var match = ctbValue.match(/rgb\\((\\d+),\\s*(\\d+),\\s*(\\d+)\\)/);
                        if (match) {
                            var r = parseInt(match[1]).toString(16).padStart(2, '0');
                            var g = parseInt(match[2]).toString(16).padStart(2, '0');
                            var b = parseInt(match[3]).toString(16).padStart(2, '0');
                            ctbValue = '#' + r + g + b;
                        }
                    }

                    return ctbValue;
                }
                return null;
            } catch (e) {
                return null;
            }
        })();
        """
        Debug.info("Запускаем get_wled_back_color JavaScript")
        self.browser.page().runJavaScript(script, self.handle_back_color)
    def get_wled_accent_color(self):
        """Получает акцентный цвет WLED из CSS переменной --c-1"""
        script = """
        (function() {
            function tryGetAccentColor() {
                if (document.readyState !== 'complete') {
                    setTimeout(tryGetAccentColor, 100);
                    return;
                }

                try {
                    var rootStyle = window.getComputedStyle(document.documentElement);
                    var c1Value = rootStyle.getPropertyValue('--c-1');

                    if (c1Value && c1Value.trim()) {
                        c1Value = c1Value.trim();

                        if (c1Value.startsWith('rgb')) {
                            var match = c1Value.match(/rgb\\((\\d+),\\s*(\\d+),\\s*(\\d+)\\)/);
                            if (match) {
                                var r = parseInt(match[1]).toString(16).padStart(2, '0');
                                var g = parseInt(match[2]).toString(16).padStart(2, '0');
                                var b = parseInt(match[3]).toString(16).padStart(2, '0');
                                c1Value = '#' + r + g + b;
                            }
                        }

                        return c1Value;
                    }

                    // Если цвет не найден, попробуем еще раз через 300мс
                    setTimeout(tryGetAccentColor, 300);
                    return;
                } catch (e) {
                    return null;
                }
            }

            return tryGetAccentColor();
        })();
        """
        Debug.info("Запускаем get_wled_accent_color JavaScript")
        self.browser.page().runJavaScript(script, self.handle_accent_color)

    def get_any_back_color(self):
        script = """
        (function() {
            if (document.readyState == 'loading') {//loading interactive complete
                setTimeout(arguments.callee, 100);
                return;
            }

            try {
                // 1. Проверяем background-color у root (html)
                var root = document.documentElement;
                if (root) {
                    var style = window.getComputedStyle(root);
                    var backColor = style.backgroundColor.trim();
                    if (backColor && backColor !== 'transparent' && backColor !== 'rgba(0, 0, 0, 0)') {
                        return backColor;
                    }
                }

                // 2. Проверяем background-color у body
                var body = document.body;
                if (body) {
                    var style = window.getComputedStyle(body);
                    var backColor = style.backgroundColor.trim();
                    if (backColor && backColor !== 'transparent' && backColor !== 'rgba(0, 0, 0, 0)') {
                        return backColor;
                    }
                }

                // 3. Проверяем background-color у первого элемента с классом 'container' или 'wrapper'
                var containers = document.querySelectorAll('.container, .wrapper');
                if (containers.length > 0) {
                    var style = window.getComputedStyle(containers[0]);
                    var backColor = style.backgroundColor.trim();
                    if (backColor && backColor !== 'transparent' && backColor !== 'rgba(0, 0, 0, 0)') {
                        return backColor;
                    }
                }

                // 4. Проверяем background-color у элемента main или section
                var main = document.querySelector('main, section');
                if (main) {
                    var style = window.getComputedStyle(main);
                    var backColor = style.backgroundColor.trim();
                    if (backColor && backColor !== 'transparent' && backColor !== 'rgba(0, 0, 0, 0)') {
                        return backColor;
                    }
                }

                // 5. Проверяем background-color у элемента с классом 'content' или 'main-content'
                var content = document.querySelector('.content, .main-content');
                if (content) {
                    var style = window.getComputedStyle(content);
                    var backColor = style.backgroundColor.trim();
                    if (backColor && backColor !== 'transparent' && backColor !== 'rgba(0, 0, 0, 0)') {
                        return backColor;
                    }
                }

                // 6. Если ничего не нашли, возвращаем белый цвет как значение по умолчанию
                return 'rgb(255, 255, 255)';
            } catch (e) {
                console.error('Error getting background color:', e);
                return 'rgb(255, 255, 255)';
            }
        })();
        """

        Debug.info("Запускаем get_any_back_color JavaScript")
        self.browser.page().runJavaScript(script, self.handle_back_color)
    def get_any_accent_color(self):
        script = """
        (function() {
            if (document.readyState === 'loading') {//loading interactive complete
                setTimeout(arguments.callee, 100);
                return;
            }

            try {
                // Ищем элементы с яркими цветами (кнопки, ссылки, акценты)
                var selectors = ['a', 'button', '.btn', '.accent', '.primary', '.highlight'];

                for (var i = 0; i < selectors.length; i++) {
                    var elements = document.querySelectorAll(selectors[i]);
                    for (var j = 0; j < elements.length; j++) {
                        var style = window.getComputedStyle(elements[j]);
                        var color = style.color || style.backgroundColor;
                        if (color && color !== 'transparent' && color !== 'rgba(0, 0, 0, 0)') {
                            return color;
                        }
                    }
                }

                return '#0078D4'; // Цвет по умолчанию
            } catch (e) {
                return '#0078D4';
            }
        })();
        """

        Debug.info("Запускаем get_any_accent_color JavaScript")
        self.browser.page().runJavaScript(script, self.handle_accent_color)




    def handle_back_color(self, color):
        """Универсальный обработчик цвета фона"""
        Debug.blue(f"handle_back_color вызван с цветом: {color}")

        if not color or not color.strip():
            Debug.warning("handle_back_color: цвет пустой или None")
            return

        try:
            if not hasattr(self, 'browser') or not self.browser or self.browser.isHidden():
                return
        except RuntimeError:
            return

        color = color.strip()
        current_url = self.browser.url().toString()
        current_url = Url.get_base_url(current_url)
        device_colors = self.get_device_custom_colors(current_url)

        if not device_colors.get("use_custom_colors", False) and not self.custom_colors_enabled:
            # Конвертируем цвет в QColor
            qcolor = self._convert_to_qcolor(color)
            if qcolor:
                self.back_color = qcolor
                self.bottom_color = qcolor
            else:
                self.back_color = self.custom_back_color
                self.bottom_color = self.custom_bottom_color

    def handle_accent_color(self, color):
        """Универсальный обработчик акцентного цвета"""
        Debug.blue(f"handle_accent_color вызван с цветом: {color}")

        if not color or not color.strip():
            Debug.warning("handle_accent_color: цвет пустой или None")
            return

        try:
            if not hasattr(self, 'browser') or not self.browser or self.browser.isHidden():
                return
        except RuntimeError:
            return

        color = color.strip()
        current_url = self.browser.url().toString()
        current_url = Url.get_base_url(current_url)
        device_colors = self.get_device_custom_colors(current_url)

        if not device_colors.get("use_custom_colors", False) and not self.custom_colors_enabled:
            # Конвертируем цвет в hex
            hex_color = self._convert_to_hex(color)
            if hex_color:
                self.accent_color = hex_color
                self.update_border_color(hex_color)
            else:
                self.accent_color = self.custom_border_color.name(QColor.NameFormat.HexRgb)
                self.border_color = self.custom_border_color

        # Применяем стили
        brightness = self.calculate_brightness(self.accent_color)
        text_color = "#FFFFFF" if brightness < 128 else "#000000"
        combined_style = self.get_combined_styles(self.accent_color, text_color)
        self.setStyleSheet(combined_style)
        self.update()

    def _convert_to_hex(self, color):
        """Конвертирует любой формат цвета в hex"""
        if color.startswith('#'):
            return color
        elif color.startswith('rgb('):
            match = re.match(r'rgb\((\d+),\s*(\d+),\s*(\d+)\)', color)
            if match:
                r, g, b = match.groups()
                return f"#{int(r):02x}{int(g):02x}{int(b):02x}"
        elif color.startswith('rgba('):
            match = re.match(r'rgba\((\d+),\s*(\d+),\s*(\d+),\s*[\d.]+\)', color)
            if match:
                r, g, b = match.groups()
                return f"#{int(r):02x}{int(g):02x}{int(b):02x}"
        return None

    def _convert_to_qcolor(self, color):
        """Конвертирует любой формат цвета в QColor"""
        if color.startswith('#'):
            return QColor(color)
        elif color.startswith('rgb(') or color.startswith('rgba('):
            hex_color = self._convert_to_hex(color)
            return QColor(hex_color) if hex_color else None
        else:
            try:
                return QColor(color)
            except:
                return None


    def update_border_color(self, color):
        if self.custom_colors_enabled:
            self.border_color = self.custom_border_color
        elif color and color.startswith('#'):
            try:
                color = color.strip()
                # Расширяем короткий hex-код #RGB в #RRGGBB
                if len(color) == 4:  # #RGB -> #RRGGBB
                    color = f"#{color[1]}{color[1]}{color[2]}{color[2]}{color[3]}{color[3]}"

                r = int(color[1:3], 16)
                g = int(color[3:5], 16)
                b = int(color[5:7], 16)
                self.border_color = QColor(r, g, b, self.custom_border_color.alpha())
                Debug.info(f"[update_border_color] Установлен цвет: {color} -> {self.border_color}")
            except ValueError as e:
                Debug.info(f"[update_border_color] Ошибка парсинга цвета {color}: {e}")
                self.border_color = self.custom_border_color
        else:
            self.border_color = self.custom_border_color
        self.update()


    def calculate_brightness(self, color):
        """Вычисляет яркость цвета в формате HEX (#RRGGBB). Возвращает значение от 0 до 255."""
        if isinstance(color, QColor):
            r, g, b = color.red(), color.green(), color.blue()
        elif isinstance(color, str) and color.startswith('#'):
            # Проверяем длину цвета и дополняем если нужно
            if len(color) == 4:  # #RGB -> #RRGGBB
                color = f"#{color[1]}{color[1]}{color[2]}{color[2]}{color[3]}{color[3]}"
            elif len(color) < 7:
                return 128  # Возвращаем значение по умолчанию для некорректных цветов

            r = int(color[1:3], 16)
            g = int(color[3:5], 16)
            b = int(color[5:7], 16)
        else:
            return 128  # Значение по умолчанию, если цвет некорректен
        # Формула luma
        return 0.299 * r + 0.587 * g + 0.114 * b

        
    def get_combined_styles(self, color, text_color="#000000"):
        Debug.magenta(f"[get_combined_styles] color: {color}, text_color: {text_color}")
        # объединяем стили для всех элементов котрые меняют цвет динамически 
        # если по отдельности применять стили то они перезаписывают друг друга

        if isinstance(color, QColor):
            color = color.name(QColor.NameFormat.HexRgb)

        if not color or not color.startswith('#'):
            color = "#0078D4"  # Цвет по умолчанию, если цвет не задан
        
        back_color = getattr(self, 'back_color', QColor(30, 30, 30, 255))
        back_color_hex = back_color.name(QColor.NameFormat.HexRgb)
        
        brightness = self.calculate_brightness(self.back_color.darker(150))
        text_address_color = "#e3e3e3" if brightness < 150 else "#545454"
        # Стили для QLineEdit
        line_edit_style = f"""
            QLineEdit {{
                background-color: #2B2B2B;
                color: #FFFFFF;/* */
                border: none;
                border-radius: 6px;
                 /*padding: 6px; */
            }}
            QLineEdit#addressInput {{
                color: {text_address_color};  /* Только для address_input */
                font-weight: 400;
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
                border: 1px solid #9e9e9e;
                border-radius: 4px;
                color: #FFFFFF;
            }}
            QCheckBox::indicator:checked {{
                background-color: {color};
                border: 1px solid #9e9e9e;
                color: #FFFFFF;  /* Цвет текста (галочки) */
                image: url(:/qt-project.org/styles/commonstyle/images/standardbutton-apply-32.png);
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


        # Стили для QListView
        listview_style = f"""
            QListView {{
                background-color: {back_color_hex};
                color: #FFFFFF;
                border: 1px solid #4A4A4A;
                border-radius: 4px;
                padding: 8px 0px 4px 0px;
                max-height: 750px;
                min-width: 160px;
            }}
            QListView::item {{
                padding: 4px 3px 4px 0px;
                color: #FFFFFF;
            }}
            QListView::item:selected {{
                background-color: {color};
                color: {text_color};
            }}
        """
    
        # Объединяем все стили в одну строку
        combined_style = line_edit_style + checkbox_style + menu_style  + listview_style
        return combined_style        
 
    def paintEvent(self, event):
        #Debug.info(f"paintEvent {self.no_color}")
        # окно 
        painter = QPainter(self)
        painter.setRenderHint(QPainter.RenderHint.Antialiasing)
    
        # Получаем цвет рамки и фона
        border_color      = getattr(self, 'border_color', QColor(49, 113, 49, 150))
        top_color         = getattr(self, 'back_color', QColor(28, 29, 34, 255))  # Цвет по умолчанию
   
         # Логика выбора цвета подвала
        # Логика выбора цвета подвала
        current_url = self.browser.url().toString()
        device_colors = self.device_custom_colors.get(current_url, {})
        
        # Приоритет 1: Индивидуальные цвета устройства
        if device_colors.get("use_custom_colors", False):
            bottom_color = QColor(*device_colors.get("bottom", device_colors.get("back", [self.default_bottom_color.red(), self.default_bottom_color.green(), self.default_bottom_color.blue(), self.default_bottom_color.alpha()])))
        # Приоритет 2: Глобальные пользовательские цвета
        elif self.custom_colors_enabled:
            bottom_color = self.custom_bottom_color
        # Приоритет 3: Автоматическое определение (используем цвет шапки)
        else:
            bottom_color = getattr(self, 'back_color', QColor(28, 29, 34, 255))
            top_color = top_color.darker(150)
        
            
        if self.no_color == "black":
            top_color = QColor(0, 0, 0, 255)
            bottom_color = QColor(0, 0, 0, 255)
            
        elif self.no_color == "gray":  
            top_color = QColor(28, 29, 34, 255)
            bottom_color = QColor(28, 29, 34, 255)
            
        # шапка
        brush = QBrush(top_color) # Цвет фона
        painter.setBrush(brush)
        painter.setPen(Qt.PenStyle.NoPen)  # Убираем границу
        painter.drawRoundedRect(self.rect().adjusted(9, 9, -9, -90), 13, 13)  # + Скругление углов 
    
        #подвал
        brush = QBrush(bottom_color) # Цвет фона
        painter.setBrush(brush)
        painter.setPen(Qt.PenStyle.NoPen)  # Убираем границу
        painter.drawRoundedRect(self.rect().adjusted(9, 90, -9, -9), 13, 13)  # + Скругление углов 
        
       
    
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

        indicator_size = 8  # Диаметр индикатора
        #indicator_x = self.minimize_button.x() - indicator_size - 10  # 5 - отступ слева от кнопки
        #indicator_y = self.minimize_button.y() + (self.minimize_button.height() - indicator_size) // 2  # Центрируем по вертикали
        indicator_x = self.scan_button.x() - indicator_size + 13  # 5 - отступ 
        indicator_y = (self.scan_button.y() + (self.scan_button.height() - indicator_size) // 2 ) - 1 # Центрируем по вертикали
        painter.setBrush(QBrush(self.indicator_color))
        painter.setPen(Qt.PenStyle.NoPen)
        painter.drawEllipse(
            indicator_x,
            indicator_y,
            indicator_size,
            indicator_size
        )
        self.set_icon()

    def start_indicator_animation(self):
        if not self.indicator_visible:
            self.indicator_visible = True
            self.indicator_alpha = 5  # Начинаем с прозрачности
            self.indicator_fading_in = True  # Начинаем с увеличения прозрачности
            if not self.indicator_animation_timer.isActive():
                self.indicator_animation_timer.start(50)  # Интервал 50 мс для плавности

    def stop_indicator_animation(self):
        if self.indicator_visible:
            self.indicator_visible = False
            self.indicator_animation_timer.stop()
            self.indicator_alpha = 0
            self.indicator_color.setAlpha(self.indicator_alpha)
            self.update()  # Перерисовываем окно

    def animate_indicator(self):
        fade_step = 5  # Шаг изменения прозрачности (уменьшен для большей плавности)

        if self.indicator_visible:
            if self.indicator_fading_in:
                # Плавное включение
                self.indicator_alpha = min(self.indicator_alpha + fade_step, self.indicator_max_alpha)
                if self.indicator_alpha >= self.indicator_max_alpha:
                    self.indicator_fading_in = False  # Переключаемся на выключение
            else:
                # Плавное выключение
                self.indicator_alpha = max(self.indicator_alpha - fade_step, 5)
                if self.indicator_alpha <= 5:
                    self.indicator_fading_in = True  # Переключаемся на включение

            # Обновляем цвет индикатора
            self.indicator_color.setAlpha(self.indicator_alpha)
            self.update()  # Перерисовываем окно



    def load_page(self, url=None, clear = None):
        caller = inspect.stack()[1].function
        Debug.info(f"\n=== НАЧАЛО ЗАГРУЗКИ СТРАНИЦЫ ← {caller} ===")
        
        Debug.info(f"→ Переданный url: {url}")
        Debug.info(f"→ Имя/url в Input: {self.address_input.text()}")

        if not url or not Url.check_url(url):
            url = self.address_input.text().strip()
            Debug.info("→ адрес не передали, берем из Input")
            # взяли из адресной строки
            if Url.check_url(url): 
               Debug.success(f"Url из адресной строки нам подходит: {url}")
            else: 
               Debug.info(f"→ в Input видимо имя, получаем по имени") 
               url = self.get_device_url_from_name(url)
               if Url.check_url(url):
                  Debug.info(f"→ ✗  вpяли url по имени: {url}")
               else:
                   Debug.error("→ ✗  url'а нет, переходить некуда, тикаем")
                   self.show_no_devices_placeholder()
                   Debug.info("=== КОНЕЦ LOAD_PAGE ===\n")
                   return
        else:
            Debug.success(f"Переданный url нам подходит, переходим дальше")
  
        
#        # Проверяем, является ли введённый текст именем устройства
#        if not (url.startswith("http://") or url.startswith("https://")):
#            # Проверяем, есть ли файл discovered_devices.json
#            if os.path.exists("discovered_devices.json"):
#                try:
#                    with open("discovered_devices.json", 'r') as f:
#                        devices = json.load(f)
#                        # Ищем устройство по имени
#                        for device in devices:
#                            if device.get('name', '') == url:  
#                                url = device.get('url', '')
#                                Debug.info(f"→ Найдено устройство {device.get('name', '')}, используем URL: {url}")
#                                break
#                        else:
#                            Debug.warning(f"→ Устройство с именем '{url}' не найдено, пытаемся загрузить как URL")
#                            url = f"http://{url}"  # Предполагаем, что это IP или хост
#                except json.JSONDecodeError as e:
#                    Debug.error(f"→ Ошибка чтения discovered_devices.json: {e}")
#                    url = f"http://{url}"  # Пробуем как URL
#            else:
#                Debug.warning("→ Файл discovered_devices.json не найден, пытаемся загрузить как URL")
#                url = f"http://{url}"  # Предполагаем, что это IP или хост        
#
        

         
        if url:
            if not (url.startswith("http://") or url.startswith("https://")):
                url = "http://" + url
                Debug.info(f"→ Добавлен протокол: {url}")
            
            original_url = url
            if clear: self.clear_history_for_new_device(original_url)
            
            # Сохраняем АКТУАЛЬНЫЕ цвета текущего устройства
        #    current_url = self.browser.url().toString()
        #    if current_url and current_url != "about:blank":
        #        # Получаем базовый URL текущей страницы
        #        current_base_url = Url.get_base_url(current_url)
        #        Debug.info(f"→ Предыдущий URL: {current_base_url}")

        #        # Проверяем, есть ли устройство с таким базовым URL
        #        devices = self.load_devices_from_file()
        #        device_base_urls = [Url.get_base_url(d.get('url', '')) for d in devices]

        #        

        #        if current_base_url in device_base_urls:
        #            
        #            if current_url == original_url:
        #                Debug.info("→ Устройство найдено, URL не изменился, не сохраняем цвета")
        #                
        #            else:
        #                Debug.info(f"→ Устройство найдено, сохраняем цвета для: {current_base_url}")
        #                # Сохраняем текущие активные цвета интерфейса
        #                current_colors = {
        #                    'border_color': [self.border_color.red(), self.border_color.green(), 
        #                                    self.border_color.blue(), self.border_color.alpha()],
        #                    'back_color': [self.back_color.red(), self.back_color.green(), 
        #                                  self.back_color.blue(), self.back_color.alpha()],
        #                    'bottom_color': [self.bottom_color.red(), self.bottom_color.green(), 
        #                                    self.bottom_color.blue(), self.bottom_color.alpha()]
        #                }

        #                # Сохраняем под БАЗОВЫМ URL
        #                self.device_custom_colors[current_base_url] = current_colors
        #                Debug.info(f"→ Цвета сохранены для: {current_base_url}")
        #                self.save_settings()
        #        else:
        #            Debug.info(f"→ Устройство не в списке, цвета не сохраняем: {current_base_url}")



            #device_host = original_url.split('//')[1].split('/')[0]
            is_ip = Url.is_ip_address(original_url)
            

            def handle_availability(is_available):
                if is_available:
                    self.is_loading_placeholder = False  # Сбрасываем флаг перед загрузкой
                   
                Debug.purple("=== ОБРАБОТКА ДОСТУПНОСТИ ===")
                Debug.purple(f"→ Доступность {original_url}: {is_available}")
                Debug.purple(f"→ Is IP: {is_ip}")
                if not is_available:
                    Debug.warning("⚠  Устройство недоступно")
                    self.current_checking_device = original_url
                    Debug.purple("→ Запускаем таймер проверки (5000ms)")
                    self.check_timer.start(5000)
                    self.browser.settings().setAttribute(QWebEngineSettings.WebAttribute.JavascriptEnabled, True)
                    self.swap_header_footer_colors()
                    Debug.purple("→ Загружаем HTML заглушку...")
                    error_html = f"""
                    <!DOCTYPE html>
                    <html lang="en"  style="--accent: #150000;">
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
                                transition: font-size 2s ease, color 2s ease;
                            }}
                            .snowman-container .eyes {{
                                display: none;
                                position: absolute;
                                top: -6px;
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
                                transition:  opacity 2s, height 2s;
                            }}
                            .error-icon {{
                                font-size: 52px;
                                margin-bottom: 20px;
                                cursor: pointer;
                                transition: opacity 1s ease;
                            }}
                            .snowman {{
                                display: none;
                                opacity: 0;
                                transition: opacity 1s ease;
                            }}
                            ::selection {{
                                background: #1c1d22;
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
                                   message.style.height = '0';
                                   message.style.opacity = '0'; 

                                   if (bridge) {{
                                       bridge.stop_border_animation();
                                   }}
                                }}
                                if (clickCount === 10) {{
                                    errorIcon.style.opacity = '0';
                                    document.body.style.backgroundColor = '#000000';
                                    if (bridge) {{
                                         bridge.setSnowMan(true);
                                         bridge.blck_color();
                                     }}

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
                                            snowmanContainer.style.fontSize = "74px";

                                            setTimeout(() => {{
                                                const eyes = document.querySelector('.eyes');
                                                eyes.style.display = 'block';
                                            }}, 2000);

                                            setTimeout(() => {{
                                                snowmanContainer.style.color = '#300000';
                                                snowmanContainer.style.fontSize = "80px"
                                            }}, 5000);

                                        }}, 7000);
                                    }}, 5000);
                                }}
                            }});
                        </script>
                    </body>
                    </html>
                    """
                    
                    self.browser.setHtml(error_html, QUrl(original_url))
                    self.address_input.setText(original_url if not self.show_names else self.get_device_name_from_url(original_url))
                    
                    Url.real_url = original_url
                    Debug.magenta(f"→ Текущий адрес: {Url.real_url}")
                    self.start_border_animation()
                else:
                    Debug.success(" Устройство доступно")
                    self.check_timer.stop()
                    self.current_checking_device = None
                    self.stop_border_animation()
                    
                    self.browser.setUrl(QUrl(original_url))
                    # Перед загрузкой страницы проверяем, нужно ли очистить историю

                    self.address_input.setText(original_url if not self.show_names else self.get_device_name_from_url(original_url))
                    
                    Url.real_url = original_url
                    Debug.magenta(f"→ Текущий адрес: {Url.real_url}")
                    #QtCore.QTimer.singleShot(1000, self.get_colors)
                    self.no_color = "no"
                    self.set_title(False)

                self.device_list = self.load_devices_for_autocomplete()
                self.completer.setModel(self.device_list)
                
                #self.hide()
                #self.show()
                #self.activateWindow()
                Debug.purple("^^^ ОБРАБОТКА ДОСТУПНОСТИ ^^^")
                QTimer.singleShot(100, self.set_icon)

            if is_ip:
                self.check_device_availability(original_url, handle_availability)
            else:
                self.check_device_availability(original_url, handle_availability)

            devices = self.load_devices_from_file()
           
            original_url = Url.get_base_url(original_url)
            if original_url in [d.get('url', '') for d in devices]:
                
                device = next(d for d in devices if d.get('url', '') == original_url)

                

                devices = [d for d in devices if d.get('url', '') != original_url]
                devices.insert(0, device)
                with open("discovered_devices.json", 'w') as f:
                    json.dump(devices, f, indent=4)

                    
                #self.device_list = self.load_devices_for_autocomplete()
                #self.device_model = self.load_devices_for_autocomplete()
                #self.completer.setModel(self.device_model)
                #Debug.info("Переместили устройство вверх в списке")

                #_load_device_auto = self.load_devices_for_autocomplete()
                #self.completer.setModel(_load_device_auto)                
                Debug.info("Переместили устройство вверх в списке")
            else:
                Debug.warning("--- Устройства нет в списке! ---")

                

        
        if url:
            self.url_changed.emit(url)
        #self.update_checkbox_style(self.accent_color)
        if original_url:
            self.add_to_history(original_url)
            self.show_loading_placeholder(url)
        Debug.info("=== КОНЕЦ LOAD_PAGE ===\n")




#    def load_devices_from_file(self):
#        if os.path.exists("discovered_devices.json"):
#            with open("discovered_devices.json", 'r') as f:
#                return json.load(f)
#        return []

    def load_devices_from_file(self):
        if os.path.exists("discovered_devices.json"):
            try:
                with open("discovered_devices.json", 'r') as f:
                    return json.load(f)
            except (json.JSONDecodeError, IOError, OSError) as e:
                Debug.error(f"Ошибка чтения discovered_devices.json: {e}")
                return []
        return []




#    def update_discovered_devices(self, name, url, device_type="n/a"):
#        devices = self.load_devices_from_file()
#        devices = [d for d in devices if d.get('url', '') != url]
#        name_count = sum(1 for d in devices if d.get("name", "").startswith(name + "(") or d.get("name", "") == name)
#        unique_name = f"{name}({name_count})" if name_count > 0 else name
#        devices.insert(0, {"name": unique_name, "url": url})
#        with open("discovered_devices.json", 'w') as f:
#            json.dump(devices, f, indent=4)
#        _load_device_auto = self.load_devices_for_autocomplete()
#        self.completer.setModel(_load_device_auto)


    def set_icon(self):
        if not self.snow_man or not self.snow_man_swch:
            self.setWindowIcon(QIcon(resource_path("icon.ico")))
            QApplication.instance().setWindowIcon(QIcon(resource_path("icon.ico")))
        else:
            self.setWindowIcon(QIcon(resource_path("icon_sm.ico")))
            QApplication.instance().setWindowIcon(QIcon(resource_path("icon_sm.ico")))


    def closeEvent(self, event):
        # Закрываем окно сканирования, если оно открыто
        self.save_settings()
        if hasattr(self, 'scan_dialog') and self.scan_dialog.isVisible():
            self.scan_dialog.close()

        # Останавливаем таймеры
        if hasattr(self, 'check_timer'):
            self.check_timer.stop()
        if hasattr(self, 'rssi_check_timer'):
            self.rssi_check_timer.stop()
        if hasattr(self, 'border_animation_timer'):
            self.border_animation_timer.stop()
        if hasattr(self, 'indicator_animation_timer'):
            self.indicator_animation_timer.stop()


        
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
        """ изменился урл - попали сюды """
        url_str = url.toString()
        if url_str.startswith('data:text/html'):
            Debug.info("[update_url] HTML заглушка")
        else:
            Debug.info(f"[update_url] {url}")
            self.add_to_history(url_str)

        if self.show_names:
            self.address_input.setText(self.get_device_name_from_url(url.toString()))
            Url.real_url = url.toString()
            Debug.magenta(f"→ Текущий адрес: {Url.real_url}")
        else:
            self.address_input.setText(url.toString())

        self.url_changed.emit(url.toString())  # Испускаем сигнал при изменении URL
        #self.update_always_on_top_checkbox_style()  

    def load_last_url(self):
        if os.path.exists("discovered_devices.json"):
            with open("discovered_devices.json", 'r') as f:
                devices = json.load(f)
                if devices:
                    url = devices[0].get('url', '')
                    self.browser.setUrl(QUrl(url))
                    self.address_input.setText(devices[0].get('name', '') if self.show_names else url)
                    
                    Url.real_url = url
                    Debug.magenta(f"→ Текущий адрес: {Url.real_url}")
                else:
                    self.show_no_devices_placeholder()
        else:
            self.show_no_devices_placeholder()
    



    # черные шапка и подвал когда показываем заглушку
    def swap_header_footer_colors(self):
        self.no_color = "gray"
        Debug.info(f"меняем цвет на серый")


    def show_no_devices_placeholder(self):
        Debug.warning(f"[placeholder] Нет устройств для отображения")
        self.swap_header_footer_colors()
        no_devices_html = f"""
            <!DOCTYPE html>
            <html lang="en" style="--accent: #150000;">
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
                        transition: font-size 2s ease, color 2s ease;
                    }}
                    .snowman-container .eyes {{
                        display: none;
                        position: absolute;
                        top: -6px;
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
                        transition:  opacity 2s, height 2s;
                    }}
                    .error-icon {{
                        font-size: 52px;
                        margin-bottom: 20px;
                        cursor: pointer;
                        transition: opacity 1s ease;
                    }}
                    .snowman {{
                        display: none;
                        opacity: 0;
                        transition: opacity 1s ease;
                    }}
                    ::selection {{
                        background: #1c1d22;
                    }}
                </style>
            </head>
            <body>
                <div class="snowman-container">
                    <div class="moon"></div>
                    <div class="eyes"></div>
                    <span class="snowman">⛇</span>
                </div>
                <div class="error-icon"></div>
                <div class="message">Устройства не обнаружены</div>

                <script>
                        let bridge;
                        new QWebChannel(qt.webChannelTransport, function(channel) {{
                            bridge = channel.objects.pybridge;
                        }}); 

                        setTimeout(() => {{
                            document.body.style.backgroundColor = '#000000';
                             const msg = document.querySelector('.message');
                            msg.style.height = '0';
                            msg.style.opacity = '0'; 

                            if (bridge) {{
                                bridge.setSnowMan(true);
                                bridge.blck_color();
                            }}

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
                                    snowmanContainer.style.fontSize = "74px";

                                    setTimeout(() => {{
                                        const eyes = document.querySelector('.eyes');
                                        eyes.style.display = 'block';
                                    }}, 2000);

                                    setTimeout(() => {{
                                        snowmanContainer.style.color = '#300000';
                                        snowmanContainer.style.fontSize = "80px";
                                        setTimeout(() => {{
                                           msg.textContent = 'Red Snow Men {VERSION}'; 
                                           msg.style.display = 'block'; 
                                           msg.style.color = '#300000';
                                           msg.style.height  = 'auto';
                                           msg.style.opacity = '1'; 
                                        }}, 2000);
                                    }}, 5000);
                                    
                                }}, 7000);
                            }}, 5000);
                        }}, 55000);
                    
                </script>
            </body>
            </html>
        """
        self.browser.setHtml(no_devices_html, QUrl("http://no-devices/"))
        self.address_input.clear()
    
    def show_loading_placeholder(self, url):
        Debug.warning(f"[placeholder] Загрузка устройства: {url}")
        self.is_loading_placeholder = True
        self.swap_header_footer_colors()
        
        loading_html = f"""
            <!DOCTYPE html>
            <html lang="en" style="--accent: #150000;">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Loading Devices</title>
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
                    }}
                    .spinner-container {{
                        font-size: 28px;
                        margin-bottom: 20px;
                        color: #5e5e5e;
                        animation: pulse 2s infinite;
                    }}
                    @keyframes pulse {{
                        0% {{ opacity: 0.3; transform: scale(1); }}
                        50% {{ opacity: 1; transform: scale(1.1); }}
                        100% {{ opacity: 0.3; transform: scale(1); }}
                    }}
                    .message {{
                        font-size: 18px;
                        text-align: center;
                        color: #5e5e5e;
                    }}
                    ::selection {{
                        background: #1c1d22;
                    }}
                </style>
            </head>
            <body>
                <div class="spinner-container">⏳</div>
                <div class="message">
                    Загрузка страницы
                </div>
            </body>
            </html>
        """
        self.browser.setHtml(loading_html, QUrl(url))
        
        self.address_input.setText(url if not self.show_names else self.get_device_name_from_url(url))
        
        Url.real_url = url
        Debug.magenta(f"→ Текущий адрес: {Url.real_url}")
        

    # Перемещение окна
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

  
    def toggle_on_top(self, checked):
        if checked:
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

#    def focusInEvent(self, event):
#        Debug.info("focusInEvent")
#        super(WebBrowser, self).focusInEvent(event)
#
#    def focusOutEvent(self, event):
#        Debug.info("focusOutEvent")
#        super(WebBrowser, self).focusOutEvent(event)

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
        Debug.info("showEvent")
#        self.setWindowFlag(QtCore.Qt.WindowType.FramelessWindowHint)
        super(WebBrowser, self).showEvent(event)

#    def event(self, event):
#         if event.type() == QtCore.QEvent.Type.WindowActivate:
#                 Debug.info("WindowActivate")
#                 if self.hidden_flag:
#         #                self.kostyle()
#                         self.hidden_flag = False  # Опускаем флаг после выполнения kostyle
#         elif event.type() == QtCore.QEvent.Type.WindowDeactivate:
#                 Debug.info("WindowDeactivate")
#                 self.hidden_flag = True  # Поднимаем флаг при сворачивании или уходе на второй план
#        return super(WebBrowser, self).event(event)
   

    def show_context_menu(self, position):
        menu = QMenu(self)
        if self.update_available:
            update_text = f"Есть новая версия ({self.latest_version})"
            update_action = menu.addAction(QIcon("open.png"), update_text)
            update_action.triggered.connect(self.open_latest_release_page)
            menu.addSeparator()


        devices_menu = menu.addMenu(QIcon(resource_path("swap.png")), "Переключить устройство")
        if os.path.exists("discovered_devices.json"):
            with open("discovered_devices.json", 'r') as f:
                devices = json.load(f)
                for device in devices:
                    # При создании пунктов меню устройств
                    device_name = device.get('name', '')
                    original_name = device_name
                    device_name = self.has_saved_credentials(device.get('url', ''), device_name)
                    device_type = device.get('type', '')
                    device_url = device.get('url', '')
                    
                    device_action = devices_menu.addAction(f"{device_name} ({device_url})")
                    icon_path = self.get_device_icon(device_type, device_url, original_name)
                    device_action.setIcon(QIcon(icon_path))

                    device_action.triggered.connect(lambda checked, url=device_url: self.load_page(url, clear=True))


        menu.addSeparator()
        scan_action = menu.addAction(QIcon(resource_path("scan.png")), "Поиск и редактирование")
        scan_action.triggered.connect(self.open_menu)
        settings_action = menu.addAction(QIcon(resource_path("settings.png")), "Настройки")
        settings_action.triggered.connect(self.open_settings_dialog)
        menu.addSeparator()
        refresh_action = menu.addAction(QIcon(resource_path("refresh.png")), "Обновить")
        refresh_action.triggered.connect(self.refresh_page)

        clear_device_data_action = menu.addAction(QIcon(resource_path("trash.png")), "Очистить кеш и данные")
        clear_device_data_action.triggered.connect(self.clear_device_cache_and_data)
        
        current_url = self.browser.url().toString()
        if current_url and current_url != "http://no-devices/" and self.has_saved_credentials(current_url):
            device_name = self.get_device_name_from_url(current_url).replace(" 🔑", "")
            clear_creds_action = menu.addAction(QIcon(resource_path("trash.png")), f"Удалить учетные данные для {device_name}")
            clear_creds_action.triggered.connect(lambda: self.clear_credentials_for_device(current_url))
            

        menu.addSeparator()
        copy_url_action = menu.addAction(QIcon(resource_path("copy.png")), "Копировать URL")
        copy_url_action.triggered.connect(self.copy_current_url)
        about_action = menu.addAction(QIcon(resource_path("info.png")), "О программе")
        about_action.triggered.connect(self.show_about_dialog)
        menu.addSeparator()
        # Добавляем чекбокс "Поверх всех окон"
        stay_on_top_action = menu.addAction("Поверх всех окон")
        stay_on_top_action.setCheckable(True)
        stay_on_top_action.setChecked(self.windowFlags() & Qt.WindowType.WindowStaysOnTopHint)
        stay_on_top_action.triggered.connect(self.toggle_on_top)
            # Настройка стиля галки
        menu.setStyleSheet("""
            QMenu::indicator {
                left: 5px;
            }
        """)

        minimize_action = menu.addAction(QIcon(resource_path("minimize.png")), "Свернуть")
        minimize_action.triggered.connect(self.showMinimized)
        close_action = menu.addAction(QIcon(resource_path("close.png")), "Закрыть")
        close_action.triggered.connect(self.close)
        menu.exec(self.browser.mapToGlobal(position))


    def clear_device_cache_and_data(self):
        current_url = self.browser.url().toString()
        if not Url.check_url(current_url):
            QMessageBox.information(self, "Информация", "Нет устройства для очистки.")
            return
        msg_box = QMessageBox()
        msg_box.setWindowTitle("Подтверждение")
        msg_box.setText(f"Вы уверены, что хотите полностью очистить кеш, куки, локальное хранилище и данные для всех устройств?\nЭто действие нельзя отменить.")
        msg_box.setStandardButtons(QMessageBox.StandardButton.Yes | QMessageBox.StandardButton.No)
        msg_box.button(QMessageBox.StandardButton.Yes).setText("Да")
        msg_box.button(QMessageBox.StandardButton.No).setText("Нет")
        msg_box.setDefaultButton(QMessageBox.StandardButton.No)
        reply = msg_box.exec()
        if reply == QMessageBox.StandardButton.Yes:
            try:
                # Очищаем HTTP-кеш
                self.profile.clearHttpCache()
                # Очищаем все куки
                cookie_store = self.profile.cookieStore()
                cookie_store.deleteAllCookies()
                # Очищаем историю посещённых ссылок
                self.profile.clearAllVisitedLinks()
                # Удаляем каталог хранения данных (включает локальное хранилище)
                storage_path = os.path.abspath("./browser_data")
                if os.path.exists(storage_path):
                    
                    shutil.rmtree(storage_path, ignore_errors=True)
                # Пересоздаём каталог хранения
                os.makedirs(storage_path, mode=0o777, exist_ok=True)
                # Устанавливаем новый путь хранения для профиля
                self.profile.setPersistentStoragePath(storage_path)
                # Очищаем локальное хранилище текущей страницы через JavaScript
                clear_local_storage_js = """
                    localStorage.clear();
                    'Local Storage cleared';
                """
                self.browser.page().runJavaScript(clear_local_storage_js, lambda result: Debug.info(result))
                # Очищаем все учетные данные?? убрать??
                if self.credentials:
                    self.credentials.clear()
                    self.save_settings()
                # Перезагружаем страницу
                self.refresh_page()
                QMessageBox.information(self, "Успех", "Все данные (кеш, куки, локальное хранилище) успешно очищены.")
            except Exception as e:
                QMessageBox.warning(self, "Ошибка", f"Не удалось очистить данные: {str(e)}")
    def clear_credentials_for_device(self, url):
        """Удаляет сохраненные учетные данные для устройства"""
        base_url = Url.get_base_url(url)
        if base_url in self.credentials:
            device_name = self.get_device_name_from_url(url).replace(" 🔑", "")
            reply = QMessageBox.question(self, "Подтверждение", 
                                       f"Удалить сохраненные учетные данные для {device_name}?",
                                       QMessageBox.StandardButton.Yes | QMessageBox.StandardButton.No)
            if reply == QMessageBox.StandardButton.Yes:
                del self.credentials[base_url]
                self.save_settings()
                QMessageBox.information(self, "Успех", "Учетные данные удалены")
                # Обновляем отображение
                self.load_devices_for_autocomplete()
                self.update_completer_style()


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

        self.device_model = self.load_devices_for_autocomplete()
        self.completer.setModel(self.device_model)


        # Обновить текущее значение в address_input из discovered_devices.json
        if os.path.exists("discovered_devices.json"):
            with open("discovered_devices.json", 'r') as f:
                devices = json.load(f)
                if devices:
                    if self.show_names:
                        self.address_input.setText(devices[0].get('name', ''))
                        
                        Url.real_url = devices[0].get('url', '')
                        Debug.magenta(f"→ Текущий адрес: {Url.real_url}")
                    else:
                        self.address_input.setText(devices[0].get('url', ''))

        self.set_title()

    def copy_current_url(self):
        url = self.browser.url().toString()
        QApplication.clipboard().setText(url)

    def clear_browser_cache(self):
        self.profile.clearHttpCache()
        self.refresh_page()


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
        Debug.warning("\n[refresh_page] Обновление страницы === ")
        current_text = self.address_input.text().strip()
        if current_text:
            if self.show_names and current_text in self.device_map:
                url = self.device_map[current_text]
                self.load_page(url)
            else:
                self.load_page(current_text)
        #current_url = self.browser.url().toString()
        #if current_url:
        #    self.load_page(current_url)
        
        #self.update_always_on_top_checkbox_style()


#    def check_current_device(self):
#        if not self.current_checking_device:
#            return
#
#        def on_finished(is_available):
#            if is_available:
#                device_ip = self.current_checking_device
#                self.check_timer.stop()
#                self.current_checking_device = None
#                self.load_page(f"http://{device_ip}/")
#
#        # Запускаем проверку доступности
#       
#        self.check_device_availability(self.current_checking_device, on_finished)


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


class WorkerSignals(QObject):
    """Определяет сигналы, доступные для воркера"""
    progress = pyqtSignal(int)
    result = pyqtSignal(object)
    value = 0  # Для флага остановки
class DiscoverWorker(QRunnable):
    def __init__(self, ip, signals, stop_flag, wled_search=True, ha_search=True, ha_port=8123):
        super().__init__()
        self.ip = ip
        self.signals = signals
        self.stop_flag = stop_flag
        self.timeout = 0.5
        self.wled_search = wled_search
        self.ha_search = ha_search
        self.ha_port = ha_port
    
    def run(self):
        try:
            if self.stop_flag.value == 1:
                return
            # Ищем Settings    
            url = f"http://{self.ip}/settings?action=discover"
            try:
                response = requests.get(url, timeout=self.timeout)
                if response.status_code == 200:
                    data = response.json()
                    name = data.get('name', '')
                    self.signals.result.emit(f"{name} at http://{self.ip}/ settings")
                    return 
            except requests.RequestException:
                pass
            # Wled       
            if self.wled_search:
                url = f"http://{self.ip}/json/info"
                try:
                    response = requests.get(url, timeout=self.timeout)
                    if response.status_code == 200:
                        data = response.json()
                        name = data.get('name', f"WLED_{self.ip}")
                        self.signals.result.emit(f"{name} at http://{self.ip}/ wled")
                except requests.RequestException:
                    pass

            # Home Assistant
            if self.ha_search:
                url = f"http://{self.ip}:{self.ha_port}/"
                try:
                    response = requests.get(url, timeout=self.timeout, allow_redirects=True)
                    if response.status_code == 200:
                        text = response.text.lower()
                        if any(keyword in text for keyword in ['home assistant', 'hass', 'homeassistant']):
                            self.signals.result.emit(f"Home Assistant at http://{self.ip}:{self.ha_port}/ homeassistant")
                            return
                except requests.RequestException:
                    pass
                
                
                    
        except Exception as e:
            Debug.info(f"Error in DiscoverWorker: {e}")
        finally:
            self.signals.progress.emit(1)


""" 
class HomeAssistantDiscovery:
    def __init__(self, callback):
        self.callback = callback
        self.zeroconf = Zeroconf()
        
    def start_discovery(self):
        # Home Assistant анонсирует _home-assistant._tcp.local.
        ServiceBrowser(self.zeroconf, "_home-assistant._tcp.local.", self)
        
    def add_service(self, zc, type_, name):
        info = zc.get_service_info(type_, name)
        if info:
            ip = str(info.addresses[0])
            port = info.port
            hostname = info.server.rstrip('.')
                    # Получаем имя из разных источников
            device_name = "Home Assistant"  # По умолчанию

            # 1. Из hostname (обычно содержит имя устройства)
            if hostname and hostname != 'homeassistant.local.':
                device_name = hostname.replace('.local.', '').replace('-', ' ').title()

            # 2. Из TXT записей (если есть)
            if info.properties:
                # Home Assistant может передавать имя в TXT записях
                location_name = info.properties.get(b'location_name')
                if location_name:
                    device_name = location_name.decode('utf-8')

            # 3. Из service name
            if name and name != '_home-assistant._tcp.local.':
                service_name = name.replace('._home-assistant._tcp.local.', '')
                if service_name:
                    device_name = service_name.replace('-', ' ').title()


            self.callback(f"Home Assistant at http://{ip}:{port}/ homeassistant")
    
    def remove_service(self, zc, type_, name):
        pass
        
    def update_service(self, zc, type_, name):
        pass
        
    def close(self):
        self.zeroconf.close()
class HomeAssistantWorker(QRunnable):
    def __init__(self, signals):
        super().__init__()
        self.signals = signals
        self.discovery = None
        
    def run(self):
        try:
            self.discovery = HomeAssistantDiscovery(self.on_found)
            self.discovery.start_discovery()
            # Ждем 3 секунды для обнаружения
            threading.Event().wait(3)
        finally:
            if self.discovery:
                self.discovery.close()
                
    def on_found(self, device_info):
        self.signals.result.emit(device_info)

 """


class CheckAvailabilityWorker(QRunnable):
    def __init__(self, url, signals, timeout):
        super().__init__()
        try:
            if not isinstance(timeout, (int, float)) or timeout <= 0:
                Debug.error(f"Ошибка: Timeout должен быть положительным числом, получено: {timeout}")
                raise ValueError("Timeout must be a positive number")
            
            if not (url.startswith("http://") or url.startswith("https://")):
                url = f"http://{url}"
            
            try:
                parsed_url = urlparse(url)
                if not parsed_url.scheme or not parsed_url.netloc:
                    Debug.error(f"Ошибка: Неверный формат URL: {url}")
                    raise ValueError("Invalid URL format")
            except Exception as e:
                Debug.error(f"Ошибка при разборе URL {url}: {e}")
                raise ValueError(f"Invalid URL: {e}")

            self.url = url
            self.signals = signals
            self.timeout = timeout*4

        except ValueError as e:
            Debug.error(f"Ошибка в CheckAvailabilityWorker: {e}")
            raise  #



    def run(self):
        try:
            parsed_url = urlparse(self.url)
            is_ip = Url.is_ip_address(self.url)

            timeout = self.timeout if is_ip else max(self.timeout * 10, 3.0)
            Debug.purple(f"→→→  [CheckAvailabilityWorker] Асинхронная проверка доступности {self.url}, с таймаутом {timeout} секунд")
           
            if is_ip:
                # Для IP - HTTP проверка
                response = requests.get(self.url, timeout=timeout, verify=False)
                is_available = response.status_code in [200, 201, 301, 302, 401, 403]
                Debug.purple(f"[CheckAvailabilityWorker] HTTP проверка {self.url}: статус {response.status_code}, доступно: {is_available}")
            else:
                # Для URL - SSL handshake с проверкой hostname
                host = parsed_url.hostname or parsed_url.netloc.split(':')[0]
                port = parsed_url.port or (443 if parsed_url.scheme == 'https' else 80)

                try:
                    if parsed_url.scheme == 'https':
                        context = ssl.create_default_context()
                        context.check_hostname = True  # Включаем проверку hostname
                        context.verify_mode = ssl.CERT_REQUIRED  # Требуем валидный сертификат
                        with socket.create_connection((host, port), timeout) as sock:
                            with context.wrap_socket(sock, server_hostname=host) as ssock:
                                is_available = True
                    else:
                        sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
                        sock.settimeout(timeout)
                        result = sock.connect_ex((host, port))
                        sock.close()
                        is_available = result == 0
                except (ssl.SSLError, ssl.CertificateError):
                    # SSL ошибка означает неправильный сертификат = несуществующий поддомен
                    is_available = False
                    Debug.error(f"[CheckAvailabilityWorker] SSL ошибка для {host}: сертификат не подходит")

                Debug.purple(f"[CheckAvailabilityWorker] SSL/TCP проверка {self.url}: доступно: {is_available}")

            self.signals.result.emit(is_available)
        except Exception as e:
            Debug.error(f"[CheckAvailabilityWorker] Ошибка проверки {self.url}: {e}")
            self.signals.result.emit(False)

class DeviceTypeSignals(QObject):
    result = pyqtSignal(str)
class CheckDeviceTypeWorker(QRunnable):
    def __init__(self, url, timeout, ha_port=8123):
        super().__init__()
        try:
            if not isinstance(timeout, (int, float)) or timeout <= 0:
                Debug.error(f"Ошибка: Timeout должен быть положительным числом, получено: {timeout}")
                raise ValueError("Timeout must be a positive number")
            
            if not (url.startswith("http://") or url.startswith("https://")):
                url = f"http://{url}"
            
            try:
                parsed_url = urlparse(url)
                if not parsed_url.scheme or not parsed_url.netloc:
                    Debug.error(f"Ошибка: Неверный формат URL: {url}")
                    raise ValueError("Invalid URL format")
            except Exception as e:
                Debug.error(f"Ошибка при разборе URL {url}: {e}")
                raise ValueError(f"Invalid URL: {e}")

            self.url = url
            self.timeout = timeout
            self.ha_port = ha_port
            self.signals = DeviceTypeSignals()

        except ValueError as e:
            Debug.error(f"Ошибка в CheckDeviceTypeWorker: {e}")
            raise


    def run(self):
        try:
            # Получаем базовый URL без лишнего
            base_url = Url.get_base_url(self.url).rstrip('/')
            #parsed_url = urlparse(base_url)
            #host = parsed_url.hostname or parsed_url.netloc.split(':')[0]
            timeout = 3.5 if Url.is_ip_address(base_url) else 6.0

            Debug.cyan(f"[CheckDeviceTypeWorker] Определяем тип для: {base_url}")
            settings_found = False
            wled_found = False
            ha_found = False
            auth_fond = False # сервер требует авторизации

            # Проверяем Settings API
            try:
                settings_response = requests.get(f"{base_url}/settings?action=discover", timeout=timeout, verify=False)
                if settings_response.status_code == 200:
                    try:
                        data = settings_response.json()
                        if data.get('type') == 'discover' and 'name' in data:
                            Debug.cyan(f"[CheckDeviceTypeWorker] Найден Settings API: {data.get('name')}")
                            self.signals.result.emit('settings')
                            return
                    except:
                        pass
                elif settings_response.status_code == 401:
                    auth_fond = True
            except Exception as e:
                Debug.error(f"[CheckDeviceTypeWorker] Settings ошибка: {e}")

            # Проверяем WLED API
            if not auth_fond:
                try:
                    # Проверяем /win 
                    wled_response = requests.get(f"{base_url}/win", timeout=2.0, verify=False)
                    if wled_response.status_code == 200 and "<vs>" in wled_response.text:
                        Debug.cyan(f"[CheckDeviceTypeWorker] Найден WLED (по /win XML)")
                        self.signals.result.emit('wled')
                        return
                    
                    wled_response = requests.get(f"{base_url}/json/info", timeout=timeout, verify=False)
                    if wled_response.status_code == 200:
                        data = wled_response.json()
                        if data.get('brand') == 'WLED':
                            Debug.cyan(f"[CheckDeviceTypeWorker] Найден WLED")
                            self.signals.result.emit('wled')
                            return
                    elif wled_response.status_code == 401:
                        Debug.cyan(f"[CheckDeviceTypeWorker] Требуется авторизация")
                        wled_found = True
                except Exception as e:
                    Debug.error(f"[CheckDeviceTypeWorker] WLED ошибка: {e}")
            

            # Проверяем Home Assistant
            if not auth_fond:
                try:
                    ha_response = requests.get(f"{base_url}:{self.ha_port}/", timeout=timeout, verify=False, allow_redirects=True)
                    if ha_response.status_code == 200:
                        text = ha_response.text.lower()
                        if any(keyword in text for keyword in ['home assistant', 'hass', 'homeassistant']):
                            Debug.cyan(f"[CheckDeviceTypeWorker] Найден Home Assistant")
                            self.signals.result.emit('homeassistant')
                            return
                    elif ha_response.status_code == 401:
                        Debug.cyan(f"[CheckDeviceTypeWorker] Требуется авторизация")
                        ha_found = True
                except Exception as e:
                    Debug.error(f"[CheckDeviceTypeWorker] Home Assistant ошибка: {e}")


            # Если требуется авторизация
            if auth_fond:
                Debug.cyan(f"[CheckDeviceTypeWorker] Требуется авторизация, проверяем учетные данные")
                credentials = {}
                if os.path.exists("settings.json"):
                    with open("settings.json", 'r') as f:
                        settings = json.load(f)
                        if "credentials" in settings:
                            for url_key, cred in settings["credentials"].items():
                                if url_key.startswith(base_url):
                                    try:
                                        login = base64.b64decode(cred["login"]).decode('utf-8')
                                        password = base64.b64decode(cred["password"]).decode('utf-8')
                                        credentials[base_url] = {"login": login, "password": password}
                                    except Exception as e:
                                        Debug.error(f"[CheckDeviceTypeWorker] Ошибка расшифровки учетных данных: {e}")

                if base_url in credentials:
                    # Получаем учетные данные для этого адреса
                    login = credentials[base_url]["login"]
                    password = credentials[base_url]["password"]
                    auth = (login, password)
                    # Проверяем Settings API с учетными данными
                    try:
                        #auth = (credentials[base_url]["login"], credentials[base_url]["password"])
                        settings_response = requests.get(f"{base_url}/settings?action=discover", auth=auth, timeout=timeout, verify=False)
                        if settings_response.status_code == 200:
                            try:
                                data = settings_response.json()
                                if data.get('type') == 'discover' and 'name' in data:
                                    Debug.cyan(f"[CheckDeviceTypeWorker] Найден Settings API с авторизацией: {data.get('name')}")
                                    self.signals.result.emit('settings')
                                    return
                            except:
                                pass
                    except Exception as e:
                        Debug.error(f"[CheckDeviceTypeWorker] Settings ошибка с авторизацией: {e}")

                    # Проверяем WLED API с учетными данными
                    try:
                        #auth = (credentials[base_url]["login"], credentials[base_url]["password"])
                        wled_response = requests.get(f"{base_url}/json/info", auth=auth, timeout=timeout, verify=False)
                        if wled_response.status_code == 200:
                            data = wled_response.json()
                            if data.get('brand') == 'WLED':
                                Debug.cyan(f"[CheckDeviceTypeWorker] Найден WLED с авторизацией")
                                self.signals.result.emit('wled')
                                return
                    except Exception as e:
                        Debug.error(f"[CheckDeviceTypeWorker] WLED ошибка с авторизацией: {e}")
                    
                    # Проверяем Home Assistant с учетными данными
                    try:
                        ha_response = requests.get(f"{base_url}:{self.ha_port}/", auth=auth, timeout=timeout, verify=False)
                        if ha_response.status_code == 200:
                            Debug.cyan(f"[CheckDeviceTypeWorker] Найден Home Assistant с авторизацией")
                            self.signals.result.emit('homeassistant')
                            return
                    except Exception as e:
                        Debug.error(f"[CheckDeviceTypeWorker] Home Assistant ошибка с авторизацией: {e}")                    

            Debug.error("[CheckDeviceTypeWorker] Тип не определен - возвращаем n/a")
            self.signals.result.emit('n/a')

        except Exception as e:
            Debug.error(f"[CheckDeviceTypeWorker] Критическая ошибка: {e}")
            self.signals.result.emit('n/a')

class FaviconSignals(QObject):
    result = pyqtSignal(str)
class AsyncFaviconLoader(QRunnable):
    def __init__(self, url, check=None):
        super().__init__()
        try:
            if not url or url == "about:blank":
                Debug.error(f"Ошибка: URL пустой или about:blank: {url}")
                raise ValueError("Invalid URL")
            
            if not (url.startswith("http://") or url.startswith("https://")):
                url = f"http://{url}"
            
            try:
                parsed_url = urlparse(url)
                if not parsed_url.scheme or not parsed_url.netloc:
                    Debug.error(f"Ошибка: Неверный формат URL: {url}")
                    raise ValueError("Invalid URL format")
            except Exception as e:
                Debug.error(f"Ошибка при разборе URL {url}: {e}")
                raise ValueError(f"Invalid URL: {e}")

            self.url = url
            self.check = check
            self.signals = FaviconSignals()

        except ValueError as e:
            Debug.error(f"Ошибка в AsyncFaviconLoader: {e}")
            raise

    def run(self):
        try:
            Debug.green(f"===[AsyncFaviconLoader] Получаем фавикон для: {self.url}")
            
            parsed = urlparse(self.url)
            base_url = f"{parsed.scheme}://{parsed.netloc}"
            Debug.green(f"   [AsyncFaviconLoader] Базовый URL: {base_url}")

            # Создаем папку для фавиконов
            favicon_dir = "favicons"
            if not os.path.exists(favicon_dir):
                os.makedirs(favicon_dir)
                Debug.green(f"   [AsyncFaviconLoader] Создана папка: {favicon_dir}")

            # Имя файла на основе домена
            domain = parsed.netloc.replace(':', '_')
            favicon_file = os.path.join(favicon_dir, f"{domain}.ico")
            Debug.green(f"   [AsyncFaviconLoader] Файл фавикона: {favicon_file}")

            # Если фавикон уже есть, возвращаем его
            if os.path.exists(favicon_file):
                Debug.green(f"   [AsyncFaviconLoader] Фавикон уже существует: {favicon_file}")
                self.signals.result.emit(favicon_file)
                return

            if not self.check:
                Debug.warning(f"   [AsyncFaviconLoader] Фавикон отсутствует, не получаем")
                self.signals.result.emit("")  # Возвращаем пустую строку вместо unknown.png
                return


            # Сначала пробуем найти фавикон через HTML
            try:
                Debug.green(f"   [AsyncFaviconLoader] Анализируем HTML страницы")
                response = requests.get(self.url, timeout=3, verify=False, headers={
                    'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36'
                })

                if response.status_code == 200:
                    html_content = response.text
                    # Ищем теги link с rel="icon", rel="shortcut icon", rel="apple-touch-icon"
                    icon_patterns = [
                        r'<link[^>]*rel=["\'](?:shortcut )?icon["\'][^>]*href=["\']([^"\']+)["\']',
                        r'<link[^>]*href=["\']([^"\']+)["\'][^>]*rel=["\'](?:shortcut )?icon["\']',
                        r'<link[^>]*rel=["\']apple-touch-icon["\'][^>]*href=["\']([^"\']+)["\']',
                        r'<link[^>]*href=["\']([^"\']+)["\'][^>]*rel=["\']apple-touch-icon["\']'
                    ]
                    
                    for pattern in icon_patterns:
                        matches = re.findall(pattern, html_content, re.IGNORECASE)
                        for match in matches:
                            favicon_url = match
                            # Преобразуем относительные URL в абсолютные
                            if favicon_url.startswith('//'):
                                favicon_url = f"{parsed.scheme}:{favicon_url}"
                            elif favicon_url.startswith('/'):
                                favicon_url = f"{base_url}{favicon_url}"
                            elif not favicon_url.startswith(('http://', 'https://')):
                                favicon_url = f"{base_url}/{favicon_url}"
                            
                            Debug.green(f"   [AsyncFaviconLoader] Найден фавикон в HTML: {favicon_url}")
                            try:
                                icon_response = requests.get(favicon_url, timeout=2, verify=False, headers={
                                    'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36'
                                })

                                if icon_response.status_code == 200 and len(icon_response.content) > 100:
                                    with open(favicon_file, 'wb') as f:
                                        f.write(icon_response.content)
                                    Debug.green(f"   [AsyncFaviconLoader] Фавикон из HTML сохранен: {favicon_file}")
                                    self.signals.result.emit(favicon_file)
                                    return
                            except Exception as e:
                                Debug.green(f"   [AsyncFaviconLoader] Ошибка загрузки фавикона из HTML {favicon_url}: {e}")
                                continue
            except Exception as e:
                Debug.green(f"   [AsyncFaviconLoader] Ошибка анализа HTML: {e}")
    
    
            # Пробуем получить фавикон
            favicon_urls = [
                f"{base_url}/favicon.ico",
                f"{base_url}/favicon.png", 
                f"{base_url}/apple-touch-icon.png"
            ]

            for favicon_url in favicon_urls:
                Debug.green(f"   [AsyncFaviconLoader] Пробуем загрузить: {favicon_url}")
                try:
                    response = requests.get(favicon_url, timeout=2, verify=False, headers={
                        'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36'
                    })

                    Debug.green(f"   [AsyncFaviconLoader] Статус: {response.status_code}, Размер: {len(response.content)}")

                    if response.status_code == 200 and len(response.content) > 100:
                        with open(favicon_file, 'wb') as f:
                            f.write(response.content)
                        Debug.green(f"   [AsyncFaviconLoader] Фавикон сохранен: {favicon_file}")
                        self.signals.result.emit(favicon_file)
                        return
                except Exception as e:
                    Debug.green(f"   [AsyncFaviconLoader] Ошибка загрузки {favicon_url}: {e}")
                    continue

            Debug.green("   [AsyncFaviconLoader] Не удалось загрузить фавикон")
            self.signals.result.emit("")

        except Exception as e:
            Debug.error(f"[AsyncFaviconLoader] Критическая ошибка: {e}")
            self.signals.result.emit("")


class ScanDialog(QDialog):
    def __init__(self, web_browser, parent=None):
        super().__init__(parent)
        Debug.warning("\n=== OPEN ScanDialog ===")
        self.wled_search = web_browser.wled_search
        self.ha_search = web_browser.ha_search
        self.ha_port = web_browser.ha_port
        self.setWindowFlags(self.windowFlags() | Qt.WindowType.Dialog)
        self.web_browser = web_browser
        self.setWindowTitle("Поиск и редактирование")
        self.setGeometry(300, 300, 400, 500)
        self.parent = parent   # Ссылка на WebBrowser

        self.last_scan_devices = []  # Список устройств, найденных при последнем сканировании

        accent_color = self.web_browser.accent_color if hasattr(self.web_browser, 'accent_color') else "#37a93c"

        self.parent.set_icon()

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
        self.update_buttons_state()
        self.original_devices = self.discovered_devices.copy()

        self.name_input.textChanged.connect(self.update_buttons_state)
        self.ip_input.textChanged.connect(self.update_buttons_state)







    def add_new_device(self):
        Debug.warning("\n===ADD NEW DEVICE===")
        new_name = self.name_input.text().strip()
        new_url = self.ip_input.text().strip()
        if new_name and new_url:
            if not (new_url.startswith("http://") or new_url.startswith("https://")):
                new_url = "http://" + new_url
            for dev in self.discovered_devices:
                if dev.get('url', '') == new_url:
                    QMessageBox.warning(self, "Ошибка", "Устройство с таким URL уже существует!")
                    return
            for dev in self.discovered_devices:
                if dev.get("name", "") == new_name:
                    QMessageBox.warning(self, "Ошибка", "Устройство с таким именем уже существует!")
                    return

            # Определяем тип устройства
            #device_type = detect_device_type(new_url)
            device_type = 'n/a'
            self.parent.check_type_device_async(new_url)

            self.discovered_devices.insert(0, {"name": new_name, "url": new_url, "type": device_type})

            item = QListWidgetItem(f"{new_name} at {new_url}")
            item.setIcon(QIcon("new.png"))
            self.device_list.insertItem(0, item)

            #self.device_list.setCurrentRow(0)
            Debug.info(f"   [add_new_device] {new_name} at {new_url}")

            self.highlight_last_device()
            self.update_list_style()

            self.parent.load_page(new_url, clear=True)
           
            #self.device_list.addItem(f"{new_name} at {new_url}")


            with open("discovered_devices.json", 'w') as f:
                json.dump(self.discovered_devices, f, indent=4)
            self.original_devices = self.discovered_devices.copy()
            #self.name_input.clear()
            #self.ip_input.clear()
            self.update_buttons_state()
        else:
            QMessageBox.warning(self, "Ошибка", "Введите имя и URL!")


    def update_list_style(self):
        #accent_color = self.web_browser.accent_color if hasattr(self.web_browser, 'accent_color') else "#37a93c"
        accent_color = '#141414'
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
        
        # Применяем стиль к текущему выбранному элементу
        #current_item = self.device_list.currentItem()
        #if current_item:
        #    current_item.setBackground(QColor(accent_color))
        #    current_item.setForeground(QColor(text_color))

        

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
    
    #def start_scan(self):
    #    self.scanning = True
    #    self.scan_button.setText("Стоп")
    #    self.stop_flag.value = 0
    #    if os.path.exists("discovered_devices.json"):
    #        with open("discovered_devices.json", 'r') as f:
    #            self.discovered_devices = json.load(f)
    #    self.scan_network()
    
    def start_scan(self):
        Debug.teal("\nStarting scan...")
        self.scanning = True
        self.scan_button.setText("Стоп")
        self.stop_flag.value = 0
        self.last_scan_devices = []  # Очищаем список устройств последнего сканирования
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
            Debug.error(f"Неверная маска подсети: {e}")
            return

        self.total_ips = network.num_addresses
        self.progress_bar.setMaximum(self.total_ips)
        self.device_list.clear()
        self.load_devices()
        self.completed_ips = 0

        # zeroconf поиск Home Assistant
        #ha_signals = WorkerSignals()
        #ha_signals.result.connect(self.add_device)
        #ha_worker = HomeAssistantWorker(ha_signals)
        #self.threadpool.start(ha_worker)

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
                wled_search=self.wled_search,
                ha_search = self.ha_search,
                ha_port = self.ha_port
            )
            worker.timeout = timeout
            self.threadpool.start(worker)

    def stop_scan(self):
        Debug.info("Stopping scan...")
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
        Debug.success(f"\nНашли устройство: {device_info}")
        # Парсим результат: "name at url type"
        parts = device_info.rsplit(' ', 1)
        if len(parts) == 2:
            name_url_part, device_type = parts
            name, url = name_url_part.split(' at ')
        else:
            name, url = device_info.split(' at ')
            device_type = "n/a"


        for existing_device in self.discovered_devices:
            if existing_device.get('url', '') == url:
                self.highlight_last_device()
                self.update_buttons_state()
                return
        # если имя совпадает добавляем номер в скобках
        name_count = sum(1 for d in self.discovered_devices if d.get("name", "").startswith(name + "(") or d.get("name", "") == name)
        unique_name = f"{name}({name_count})" if name_count > 0 else name
        
        unique_name =  self.parent.has_saved_credentials(url, unique_name)
            


        item = QListWidgetItem(f"{unique_name} at {url}")

        # Создаем двойную иконку только для всех типов устройств
        type_icon_path = self.get_device_icon(device_type, url, name, True)
        if  type_icon_path and os.path.exists(type_icon_path):
            dual_icon = self.create_dual_icon(type_icon_path)
            if dual_icon:
                item.setIcon(dual_icon)
        elif type_icon_path and os.path.exists(type_icon_path):
            item.setIcon(QIcon(type_icon_path))

        self.device_list.addItem(item)
        self.discovered_devices.append({"name": unique_name, "url": url, "type": device_type})
        self.last_scan_devices.append({"name": unique_name, "url": url, "type": device_type})

        with open("discovered_devices.json", 'w') as f:
            json.dump(self.discovered_devices, f, indent=4)

        self.highlight_last_device()
        self.update_buttons_state()
        self.update_list_style()

   
   
    def load_devices_from_file(self):
        if os.path.exists("discovered_devices.json"):
            with open("discovered_devices.json", 'r') as f:
                return json.load(f)
        return []

    
    
    def load_devices(self):
        Debug.brown("===[LOAD_DEVICES ScanDialog]===")
        self.discovered_devices = self.load_devices_from_file()
        self.device_list.clear()
        count = 0
        for device in self.discovered_devices:
            # Проверяем, новое ли устройство
            is_new = any(d.get('url', '') == device.get('url', '') for d in self.last_scan_devices)
            count += 1
            check = (count == 1)
            if is_new: check = True
            # Определяем иконку по типу устройства
            
            #device_type = detect_device_type(device.get('url', ''))
            if is_new: self.parent.check_type_device_async(device.get('url', ''))

            device_type = self.parent.get_device_type_from_url(device.get('url', ''))
            device_name = self.parent.get_device_name_from_url(device.get('url', ''))
           
            type_icon_path = self.get_device_icon(device_type, device.get('url', ''), device_name, check)

            display_name = device.get("name", "")
            display_name =  self.parent.has_saved_credentials(device.get('url', ''), display_name)
                
            item = QListWidgetItem(f"{display_name} at {device.get('url', '')}")


            # Устанавливаем иконку
            if is_new and type_icon_path and os.path.exists(type_icon_path):
                dual_icon = self.create_dual_icon(type_icon_path)
                if dual_icon:
                    item.setIcon(dual_icon)
            elif type_icon_path and os.path.exists(type_icon_path):
                item.setIcon(QIcon(type_icon_path))

            self.device_list.addItem(item)
        
       
        first_item = self.device_list.item(0)
        
        if not first_item: 
            Debug.brown("^^^[LOAD_DEVICES ScanDialog] not first_item ^^^") 
            return

        device_info = first_item.text()
        name, url = device_info.split(' at ')
        base_name = name.strip()
        
        if not base_name or not url:
            Debug.brown("^^^[LOAD_DEVICES ScanDialog] not name or not url ^^^")
            return

        self.update_list_style()
        self.name_input.setText(base_name)
        self.ip_input.setText(url)
        Debug.brown("^^^[LOAD_DEVICES ScanDialog]^^^")

    
    
    def get_device_icon(self, device_type, url, name, check = None):
        return  self.parent.get_device_icon(device_type, url, name, check)

    def create_dual_icon(self, type_icon_path):
        """Создает иконку типа с зеленой точкой в верхнем левом углу"""


        type_pixmap = QPixmap(type_icon_path)
        if type_pixmap.isNull():
            return None

        # Создаем копию иконки типа
        result = type_pixmap.copy()

        painter = QPainter(result)
        # Рисуем зеленую точку в верхнем левом углу
        painter.setBrush(QBrush(QColor(0, 255, 0)))  # Зеленый цвет
        painter.setPen(Qt.PenStyle.NoPen)
        dot_size = min(result.width(), result.height()) // 4  # Размер точки
        painter.drawEllipse(0, 0, dot_size, dot_size)
        painter.end()

        return QIcon(result)



    def update_device_list(self):
        """Обновляет отображение списка устройств"""
        self.device_list.clear()

        Debug.info(f"[update_device_list] with {len(self.discovered_devices)} devices")
        current_name = self.name_input.text().strip()
        current_url = self.ip_input.text().strip()
        
        Debug.info(f"    Current name: {current_name}")
        Debug.info(f"    Original url: {current_url}")
        current_url = Url.get_base_url(current_url)
        Debug.info(f"    Current url: {current_url}")
        

        if current_url and not (current_url.startswith("http://") or current_url.startswith("https://")):
            current_url = f"http://{current_url}"

        text_color = "#FFFFFF"

        Debug.info(f"^^^ [update_device_list] ^^^")
        
        count = 0
        for device in self.discovered_devices:
            # Проверяем, новое ли устройство
            is_new = any(d.get('url', '') == device.get('url', '') for d in self.last_scan_devices)
            
            count += 1
            check = (count == 1)
            if is_new: check = True
            # Определяем иконку по типу устройства
            type_icon_path = self.get_device_icon(device.get('type', 'n/a'), device.get('url', None), device.get('name', None), check)

            display_name = device.get("name", "")
            display_name = self.parent.has_saved_credentials(device.get('url', ''), display_name)

            item = QListWidgetItem(f"{display_name} at {device.get('url', '')}")


            # Устанавливаем иконку
            if is_new and type_icon_path and os.path.exists(type_icon_path):
                # Создаем двойную иконку: new + type
                dual_icon = self.create_dual_icon(type_icon_path)
                if dual_icon:
                    item.setIcon(dual_icon)
            elif type_icon_path and os.path.exists(type_icon_path):
                # Только иконка типа
                item.setIcon(QIcon(type_icon_path))

            self.device_list.addItem(item)

            # Проверяем, соответствует ли устройство текущим значениям в полях ввода
            #if device.get("name", "") == current_name and device.get('url', '') == current_url:
            #    font = QFont()
            #    font.setWeight(QFont.Weight.Bold)
            #    item.setFont(font)
            #    item.setForeground(QColor(text_color))

        
    def select_device(self, item):
        
        Debug.info(f"Selecting device: {item.text() if item else 'none'}")
        """Обрабатывает выбор устройства в списке."""
       
        device_info = item.text()
        name_part, url = device_info.split(' at ')
        base_name = name_part.strip() 

        # Находим устройство в списке и сохраняем его тип
        current_device = None
        for device in self.discovered_devices:
            if device.get('url', '') == url:
                current_device = device
                break

        # Перемещаем устройство в начало списка, сохраняя тип
        devices = self.load_devices_from_file()
        devices = [d for d in devices if d.get('url', '') != url]

        # Создаем новую запись с сохранением типа
        new_device = {"name": base_name, "url": url}
        if current_device and "type" in current_device:
            new_device["type"] = current_device["type"]
        else:
            #device_type = detect_device_type(url)
            self.check_type_device_async(url)
            device_type = 'n/a'
            new_device["type"] = device_type

        devices.insert(0, new_device)
        with open("discovered_devices.json", 'w') as f:
            json.dump(devices, f, indent=4)
        self.discovered_devices = devices

       
        device_model = self.web_browser.load_devices_for_autocomplete()
        self.web_browser.completer.setModel(device_model)


        def handle_availability(is_available):
            if is_available:
                self.web_browser.load_page(url, clear=True)
            else:
                self.web_browser.load_page(url, clear=True)
            self.highlight_last_device()
            self.name_input.setText(base_name)
            self.ip_input.setText(url)
            self.update_buttons_state()
            self.web_browser.update_colors()

        device_ip = url.split('//')[1].split('/')[0]
        full_url = url if url.startswith("http://") or url.startswith("https://") else f"http://{device_ip}"
        self.web_browser.check_device_availability(full_url, handle_availability)

        

#        font = QFont()
#        font.setWeight(QFont.Weight.Bold)
#        item.setFont(font)
#        accent_color = self.web_browser.accent_color if hasattr(self.web_browser, 'accent_color') else "#37a93c"
#        text_color = "#FFFFFF" if self.web_browser.calculate_brightness(accent_color) < 128 else "#000000"
#        item.setBackground(QColor(accent_color))
#        item.setForeground(QColor(text_color))
        
        self.update_device_list()
        self.highlight_last_device()
        self.update_buttons_state()


    def apply_changes(self):
        current_item = self.device_list.currentItem()
        if current_item:
            new_name = self.name_input.text().strip()
            new_url = self.ip_input.text().strip()
            if not (new_url.startswith("http://") or new_url.startswith("https://")):
                new_url = "http://" + new_url


            # Добавляем / в конце если это доменное имя и нет пути
            if new_url and not new_url.endswith('/'):
                from urllib.parse import urlparse
                parsed = urlparse(new_url)
                # Если нет пути (только домен), добавляем /
                if not parsed.path or parsed.path == '':
                    new_url += '/'


            if new_name and new_url:
                device_info = current_item.text()
                orig_name, orig_url = device_info.split(' at ', 1)
                orig_url = orig_url.strip()

                current_device = next((dev for dev in self.discovered_devices if dev.get('url', '') == orig_url), None)
                if not current_device:
                    QMessageBox.warning(self, "Ошибка", "Не удалось найти устройство в списке!")
                    return
                for dev in self.discovered_devices:
                    if dev.get('url', '') == new_url and dev != current_device:
                        QMessageBox.warning(self, "Ошибка", "Устройство с таким URL уже существует!")
                        return
                for dev in self.discovered_devices:
                    if dev.get("name", "") == new_name and dev != current_device:
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
                self.highlight_last_device()

                device_model = self.web_browser.load_devices_for_autocomplete()
                self.web_browser.completer.setModel(device_model)

                self.web_browser.load_page(new_url)
                self.update_buttons_state()
            else:
                QMessageBox.warning(self, "Ошибка", "Введите имя и URL!")
        else:
            QMessageBox.warning(self, "Ошибка", "Выберите устройство для редактирования!")

    def get_darker_color(self, color, factor):
        """Возвращает более тёмный оттенок цвета"""
        if isinstance(color, str) and color.startswith('#'):
            
            qcolor = QColor(color)
            return qcolor.darker(factor).name()
        return color

    def highlight_last_device(self):
        # Сбрасываем стили всех элементов
        #for i in range(self.device_list.count()):
        #    item = self.device_list.item(i)
        #    item.setFont(QFont())  # Сбрасываем шрифт
        #    item.setForeground(QColor())  # Сбрасываем цвет
        #    item.setBackground(QColor())  # Сбрасываем фон

        



        self.device_list.clearSelection()
        if self.device_list.count() > 0:
            self.device_list.setCurrentRow(0)


        
        #self.device_list.setCurrentRow(0)  # выбрать первый элемент
        item = self.device_list.currentItem()
        if not item: return
            
        device_info = item.text()
        name, url = device_info.split(' at ')
        base_name = name.strip()



        if not base_name or not url: return


    def clear_devices(self):
        self.device_list.clear()
        self.discovered_devices = []
        self.last_scan_devices = []  
        if os.path.exists("discovered_devices.json"):
            os.remove("discovered_devices.json")
        self.update_buttons_state()

        # Показываем заглушку в главном окне, если устройств больше нет
        self.web_browser.show_no_devices_placeholder()
        self.parent.set_title(False)


    def delete_device(self):
        current_item = self.device_list.currentItem()
        if current_item:
            device_info = current_item.text()
            self.device_list.takeItem(self.device_list.row(current_item))
            name, url = device_info.split(' at ')
            if name.startswith("🟢 "):
                name = name[2:].strip()  # Удаляем эмодзи для корректного сравнения
            self.discovered_devices = [device for device in self.discovered_devices if device.get('url', '') != url]
            self.last_scan_devices = [device for device in self.last_scan_devices if device.get('url', '') != url]  # Удаляем из последнего сканирования
            with open("discovered_devices.json", 'w') as f:
                json.dump(self.discovered_devices, f, indent=4)
            
            self.name_input.clear()
            self.ip_input.clear()
            self.web_browser.load_last_url()
            self.highlight_last_device()
            self.update_buttons_state()

    def update_buttons_state(self):
        Debug.info("=== [update_buttons_state] === ")
        #accent_color = self.web_browser.accent_color if hasattr(self.web_browser, 'accent_color') else "#37a93c"
        accent_color = "#4a4a4a"
        current_item = self.device_list.currentItem()
        name = self.name_input.text().strip()
        url = self.ip_input.text().strip()
        Debug.info(f"    current_item: {current_item}")
        if current_item: 
            Debug.info(f"    current_item: {current_item.text()}")
        Debug.info(f"    name: {name}")
        

        if url and not (url.startswith("http://") or url.startswith("https://")):
            url = "http://" + url
        Debug.info(f"    url: {url}")

        # Состояние кнопки "Добавить"
        add_enabled = False
        if name and url:
            url_exists = any(dev.get('url', '') == url for dev in self.discovered_devices)
            add_enabled = not url_exists
            Debug.info(f"    добавить: {add_enabled}")

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
            Debug.info(f"    применить: {apply_enabled}")

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


        # кнопка "удалить
        delete_enabled = False
        if name and url and self.discovered_devices:
            first_device = self.discovered_devices[0]
            delete_enabled = (first_device.get('name', '') == name and first_device.get('url', '') == url)

        self.delete_button.setEnabled(delete_enabled)
        self.delete_button.setEnabled(delete_enabled)
        if delete_enabled:
            self.delete_button.setStyleSheet(f"""
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
            self.delete_button.setStyleSheet("""
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
        Debug.info("^^^ [update_buttons_state] ^^^")

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


class Bridge(QObject):
    @pyqtSlot()
    def stop_border_animation(self):
        self.parent().stop_border_animation()
    

    @pyqtSlot()
    def blck_color(self):
        self.parent().no_color = "black" # 
        self.parent().set_title(False)
        self.parent().update() 
        

    @pyqtSlot(bool)
    def setSnowMan(self, value):
        if self.parent():
            self.parent().snow_man = value
            Debug.info(f"snow_man изменён на: {self.parent().snow_man}")
            self.parent().save_settings()
        else:
            Debug.info("Ошибка:setSnowMan родительский объект не найден")

class AboutDialog(QDialog):
    def __init__(self, parent=None):
        super().__init__(parent)
        Debug.warning("=== OPEN AboutDialog ===")

        self.setWindowTitle("О программе")
        self.setMinimumSize(400, 400)  # Размер окна
        self.parent = parent 

        self.parent.set_icon()
            

        # Layout для окна
        layout = QVBoxLayout()

        about_text = QTextBrowser()
        about_text.setOpenExternalLinks(True)  # Позволяет открывать ссылки в браузере
        

        if not self.parent.snow_man_swch  or  not self.parent.snow_man: 
            name = "Remote Settings Management"
        else :
            name = "Red Snow Man"
        
        if  self.parent.snow_man_swch  and  self.parent.snow_man:
            Debug.pink("!!! === Труляля! Посхал очка активна === !!!")

        about_text.setText(
            f"""
            <h2>{name}</h2>
            <p><b>Версия:</b> {VERSION}</p>
            <p><b>Автор:</b> Vanila</p>
            <p><b>Описание:</b> </p>
            <p>программа для поиска и отображения веб-интерфейса устройств в локальной сети и интернет:</p>
            <p> • с установленной библиотекой <a href="https://github.com/GyverLibs/Settings">AlexGyver Settings</a></p>
            <p> • с установленным <a href="https://github.com/wled/WLED">WLED</a></p>
            <p> • устройств добавленных вручную, в том числе из сети "интернет"</p>
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
        Debug.warning("\n=== OPEN SettingsDialog ===")

        self.setWindowTitle("Настройки - RSM")
        self.setMinimumSize(400, 300)  
        self.parent = parent  # Ссылка на WebBrowser
        self.parent.set_icon()
        # Основной layout
        layout = QVBoxLayout(self)

        devices_box = QGroupBox(self)
        devices_box_layout = QVBoxLayout()

          

        # Чекбокс "Показывать имена устройств"
        self.show_names_checkbox = QCheckBox("Имена устройств в строке", self)
        self.show_names_checkbox.setToolTip("Показывать имена устройств вместо url")

        self.show_names_checkbox.setChecked(self.parent.show_names)
        self.show_names_checkbox.stateChanged.connect(self.update_show_names)
        devices_box_layout.addWidget(self.show_names_checkbox)
       
        # Чекбокс "Показывать имена устройств на кнопке на панеле задач"
        self.title_device_checkbox = QCheckBox("Имена устройств на кнопке", self)
        self.title_device_checkbox.setToolTip("Будет добавлять имя устройства на кнопку в панели задач")

        self.title_device_checkbox.setChecked(self.parent.title_device)
        self.title_device_checkbox.stateChanged.connect(self.update_title_device)
        devices_box_layout.addWidget(self.title_device_checkbox)
       
        # Чекбокс "Искать устройства WLED"
        self.wled_search_checkbox = QCheckBox("Искать устройства WLED", self)
        self.wled_search_checkbox.setToolTip("При сканировании будет искать устройства с wled")
        self.wled_search_checkbox.setChecked(self.parent.wled_search)
        self.wled_search_checkbox.stateChanged.connect(self.update_wled_search)
        devices_box_layout.addWidget(self.wled_search_checkbox)

        # Чекбокс "Искать устройства Home Assistant"
        self.homeassistant_search_checkbox = QCheckBox("Искать Home Assistant", self)
        self.homeassistant_search_checkbox.setToolTip("При сканировании будет искать Home Assistant")
        self.homeassistant_search_checkbox.setChecked(self.parent.ha_search)
        self.homeassistant_search_checkbox.stateChanged.connect(self.update_ha_search)
        devices_box_layout.addWidget(self.homeassistant_search_checkbox)
        

        # Поле ввода порта Home Assistant
        ha_port_layout = QHBoxLayout()
        ha_port_layout.setContentsMargins(0, 0, 0, 0)
        self.ha_port_label = QLabel("        Порт HA:", self)
        self.ha_port_input = QLineEdit(str(self.parent.ha_port), self)
        self.ha_port_input.setPlaceholderText("8123")
        self.ha_port_input.setMaximumWidth(80)
        self.ha_port_input.textChanged.connect(self.update_ha_port)
        ha_port_layout.addWidget(self.ha_port_label)
        ha_port_layout.addWidget(self.ha_port_input)
        ha_port_layout.addStretch()

        self.ha_port_widget = QWidget()
        self.ha_port_widget.setLayout(ha_port_layout)
        self.ha_port_widget.setVisible(self.parent.ha_search)
        devices_box_layout.addWidget(self.ha_port_widget)



 
 
        devices_box.setLayout(devices_box_layout)
        layout.addWidget(devices_box)

        # Чекбокс "Поверх окон"
        #self.stay_on_top_checkbox = QCheckBox("Поверх окон", self)
        #self.stay_on_top_checkbox.setChecked(self.parent.checkbox.isChecked())
        #self.stay_on_top_checkbox.stateChanged.connect(self.update_stay_on_top)
        #layout.addWidget(self.stay_on_top_checkbox)
        separator = QFrame()
        separator.setFrameShape(QFrame.Shape.HLine)  # Горизонтальная линия
        separator.setFrameShadow(QFrame.Shadow.Sunken) 
        layout.addWidget(separator)

        colors_box = QGroupBox("Цвета", self)
        colors_box_layout = QVBoxLayout()

        if self.parent.snow_man: 
            self.snow_man_checkbox = QCheckBox(f"Снеговик", self)
            self.snow_man_checkbox.setToolTip("Поздравляю, вы открыли уникальную иконку приложения!")

            self.snow_man_checkbox.setChecked(self.parent.snow_man_swch)
            self.snow_man_checkbox.stateChanged.connect(self.update_snow_man_swch)
            colors_box_layout.addWidget(self.snow_man_checkbox)

        



        # Чекбокс "Свои цвета"
        self.custom_colors_checkbox = QCheckBox("Определять автоматически", self)
        self.custom_colors_checkbox.setToolTip("Цвета рамки, шапки и подвала будут определяться автоматически")
        self.custom_colors_checkbox.setChecked(not self.parent.custom_colors_enabled)

        self.custom_colors_checkbox.stateChanged.connect(self.update_custom_colors)
        colors_box_layout.addWidget(self.custom_colors_checkbox)
        
        # Метка с текущим URL
        current_url = self.parent.browser.url().toString() or "Нет активного устройства"
        #self.device_url_label = QLabel(f"Текущее устройство: {current_url}", self)
        #layout.addWidget(self.device_url_label)

        # Только для открытого устройства
        self.device_specific_colors_checkbox = QCheckBox("Только для этого устройства", self)
        self.device_specific_colors_checkbox.setToolTip("Назначить свои цвета для цвета рамки, шапки и подвала, для этого устройства")

        custom_colors = self.parent.get_device_custom_colors(current_url)
        self.device_specific_colors_checkbox.setChecked(
            custom_colors.get("use_custom_colors", False) if custom_colors else False
        )
        self.device_specific_colors_checkbox.setEnabled(
            current_url != "Нет активного устройства" #and not self.parent.custom_colors_enabled
        )
        self.device_specific_colors_checkbox.stateChanged.connect(self.update_device_specific_colors)
        colors_box_layout.addWidget(self.device_specific_colors_checkbox)

        # Кнопка выбора цвета рамки
        self.border_color_button = QPushButton("", self)
        self.border_color_button.clicked.connect(self.choose_border_color)
        # Кнопка выбора цвета фона
        self.back_color_button = QPushButton("", self)
        self.back_color_button.clicked.connect(self.choose_back_color)
        # Кнопка выбора цвета нижней части
        self.bottom_color_button = QPushButton("", self)
        self.bottom_color_button.clicked.connect(self.choose_bottom_color)

      
        self.update_button_texts()
        self.update_checkbox_styles()

        colors_box_layout.addWidget(self.border_color_button)
        colors_box_layout.addWidget(self.back_color_button)
        colors_box_layout.addWidget(self.bottom_color_button)
       
        colors_box.setLayout(colors_box_layout)
        layout.addWidget(colors_box)

        separator2 = QFrame()
        separator2.setFrameShape(QFrame.Shape.HLine)  # Горизонтальная линия
        separator2.setFrameShadow(QFrame.Shadow.Sunken)  
        layout.addWidget(separator2)
        
        size_box = QGroupBox("Размеры и масштаб", self)
        size_box_layout = QVBoxLayout()
        # Поле для ширины окна
        self.width_label = QLabel("Ширина окна:", self)
        size_box_layout.addWidget(self.width_label)

        self.width_input = QLineEdit(self)
        self.width_input.setText(str(int(self.parent.window_width)))
        #self.width_input.textChanged.connect(self.update_window_size)
        size_box_layout.addWidget(self.width_input)

        # Поле для высоты окна
        self.height_label = QLabel("Высота окна:", self)
        size_box_layout.addWidget(self.height_label)
        self.height_input = QLineEdit(self)
        self.height_input.setText(str(int(self.parent.window_height)))
        #self.height_input.textChanged.connect(self.update_window_size)
        size_box_layout.addWidget(self.height_input)

        # Поле для масштаба
        self.zoom_label = QLabel("Масштаб браузера (0.1-5.0):", self)
        size_box_layout.addWidget(self.zoom_label)
        self.zoom_input = QLineEdit(self)
        self.zoom_input.setText(str(self.parent.zoom_factor))
        #self.zoom_input.textChanged.connect(self.update_zoom_factor)
        size_box_layout.addWidget(self.zoom_input)
        
        # Кнопка "Применить"
        self.apply_button = QPushButton("Применить размер", self)
        self.apply_button.clicked.connect(self.apply_settings)
        size_box_layout.addWidget(self.apply_button)

        # Кнопка "Сбросить размер"
        reset_button = QPushButton("Сбросить размер", self)
        reset_button.clicked.connect(self.reset_size)
        size_box_layout.addWidget(reset_button)
        
        size_box.setLayout(size_box_layout)
        layout.addWidget(size_box)


        self.setLayout(layout)
        self.parent.url_changed.connect(self.update_on_url_change)

    def update_ha_search(self):
        self.parent.ha_search = self.homeassistant_search_checkbox.isChecked()
        self.ha_port_widget.setVisible(self.parent.ha_search)
        self.adjustSize()  
        self.parent.save_settings()




    def update_ha_port(self):
        try:
            port = int(self.ha_port_input.text())
            if 1 <= port <= 65535:
                self.parent.ha_port = port
                self.parent.save_settings()
        except ValueError:
            pass

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
            self.parent.bottom_color = self.parent.custom_bottom_color
        else:
            # Если "Свои цвета" выключены, вызываем получение цветов
            self.parent.update_colors()
        self.parent.update()  # Перерисовываем главное окно
    
    def update_checkbox_styles(self):
        # Определяем цвет текста в зависимости от состояния "Только для этого устройства"
        if self.device_specific_colors_checkbox.isChecked():
            text_color = "#b7b7b7"  # Более темный серый
        else:
            text_color = "#FFFFFF"  # Белый

        # Применяем стиль к чекбоксу "Определять автоматически"
        self.custom_colors_checkbox.setStyleSheet(f"""
            QCheckBox {{
                color: {text_color};
            }}
        """)

    def get_current_device_name(self):
        current_url = self.parent.browser.url().toString()
        return self.parent.get_device_name_from_url(current_url)


    def update_button_texts(self):
        """Обновляет текст кнопок в зависимости от состояния чекбокса."""
        current_url = self.parent.browser.url().toString()
        use_device_specific = (self.device_specific_colors_checkbox.isChecked() and 
                             self.device_specific_colors_checkbox.isEnabled())
        device_name = self.get_current_device_name()
        self.device_specific_colors_checkbox.setText(f"Только для {device_name}")
        if use_device_specific:
            self.border_color_button.setText(f"Цвет рамки устройства {device_name}")
            self.border_color_button.setToolTip("Выбрать индивидуальный цвет рамки для открытого устройства")

            self.back_color_button.setText(f"Цвет шапки этого устройства {device_name}")
            self.back_color_button.setToolTip("Выбрать индивидуальный цвет шапки для открытого устройства")

            self.bottom_color_button.setText(f"Цвет подвала этого устройства {device_name}")
            self.bottom_color_button.setToolTip("Выбрать индивидуальный цвет подвала для открытого устройства")
        else:
            self.border_color_button.setText("Цвет рамки по умолчанию")
            self.border_color_button.setToolTip("Выбрать цвет рамки по умолчанию (если не определяется автоматически)")

            self.back_color_button.setText("Цвет шапки по умолчанию")
            self.back_color_button.setToolTip("Выбрать цвет шапки по умолчанию (если не определяется автоматически)")

            self.bottom_color_button.setText("Цвет подвала по умолчанию")
            self.bottom_color_button.setToolTip("Выбрать цвет подвала по умолчанию (если не определяется автоматически)")

    def choose_border_color(self):
        current_url = self.parent.browser.url().toString()
        use_device_specific = self.device_specific_colors_checkbox.isChecked()
        
        
        initial_color = (QColor(*self.parent.get_device_custom_colors(current_url)["accent"]) 
                        if use_device_specific and self.parent.get_device_custom_colors(current_url)
                        else self.parent.custom_border_color)
        
        color = QColorDialog.getColor(
            initial_color,
            self,
            "Выберите цвет рамки",
            QColorDialog.ColorDialogOption.ShowAlphaChannel
        )
        if color.isValid():
            if use_device_specific and current_url:
                custom_colors = self.parent.get_device_custom_colors(current_url) or {}
                custom_colors["accent"] = [color.red(), color.green(), color.blue(), color.alpha()]
                self.parent.device_custom_colors[current_url] = custom_colors
            else:
                self.parent.custom_border_color = color
            self.parent.save_settings()
            self.parent.update_colors()

    def choose_bottom_color(self):
        current_url = self.parent.browser.url().toString()
        use_device_specific = self.device_specific_colors_checkbox.isChecked() 

        initial_color = (QColor(*self.parent.get_device_custom_colors(current_url)["bottom"])
                        if use_device_specific and self.parent.get_device_custom_colors(current_url)
                        else self.parent.custom_bottom_color)

        color = QColorDialog.getColor(
            initial_color,
            self,
            "Выберите цвет подвала",
            QColorDialog.ColorDialogOption.ShowAlphaChannel
        )
        if color.isValid():
            if use_device_specific and current_url:
                custom_colors = self.parent.get_device_custom_colors(current_url) or {}
                custom_colors["bottom"] = [color.red(), color.green(), color.blue(), color.alpha()]
                self.parent.device_custom_colors[current_url] = custom_colors
            else:
                self.parent.custom_bottom_color = color
            self.parent.save_settings()
            self.parent.update_colors()

    def update_on_url_change(self, new_url):
        current_url = new_url or "Нет активного устройства"
        custom_colors = self.parent.get_device_custom_colors(current_url)
        # Отключаем сигнал перед обновлением галочки
        self.device_specific_colors_checkbox.blockSignals(True)
        self.device_specific_colors_checkbox.setChecked(
            custom_colors.get("use_custom_colors", False) if custom_colors else False
        )
        self.device_specific_colors_checkbox.setEnabled(
            current_url != "Нет активного устройства" #and not self.parent.custom_colors_enabled
        )
        # Включаем сигнал обратно
        self.device_specific_colors_checkbox.blockSignals(False)
        self.update_button_texts()
        self.update_checkbox_styles()

    def choose_back_color(self):
        current_url = self.parent.browser.url().toString()
        use_device_specific = self.device_specific_colors_checkbox.isChecked() 

        initial_color = (QColor(*self.parent.get_device_custom_colors(current_url)["back"])
                        if use_device_specific and self.parent.get_device_custom_colors(current_url)
                        else self.parent.custom_back_color)

        color = QColorDialog.getColor(
            initial_color,
            self,
            "Выберите цвет шапки",
            QColorDialog.ColorDialogOption.ShowAlphaChannel
        )
        if color.isValid():
            if use_device_specific and current_url:
                custom_colors = self.parent.get_device_custom_colors(current_url) or {}
                custom_colors["back"] = [color.red(), color.green(), color.blue(), color.alpha()]
                self.parent.device_custom_colors[current_url] = custom_colors
            else:
                self.parent.custom_back_color = color
            self.parent.save_settings()
            self.parent.update_colors()

    def update_custom_colors(self, state):
        self.parent.custom_colors_enabled = not (state == Qt.CheckState.Checked.value)
        #self.device_specific_colors_checkbox.setEnabled(not self.parent.custom_colors_enabled)
        self.parent.save_settings()
        self.update_button_texts()
        self.update_checkbox_styles()
        self.parent.update_colors()
        #self.parent.update_always_on_top_checkbox_style()  # Обновляем стиль чекбокса
        QTimer.singleShot(100, self.parent.set_icon)

    def update_device_specific_colors(self, state):
        current_url = self.parent.browser.url().toString()
        if not current_url or current_url == "Нет активного устройства":
            return
        
        use_custom = (state == Qt.CheckState.Checked.value)
        custom_colors = self.parent.device_custom_colors.get(current_url, {}).copy()
        
        custom_colors["use_custom_colors"] = use_custom
        if "accent" not in custom_colors:
            custom_colors["accent"] = [
                self.parent.custom_border_color.red(),
                self.parent.custom_border_color.green(),
                self.parent.custom_border_color.blue(),
                self.parent.custom_border_color.alpha()
            ]
        if "back" not in custom_colors:
            custom_colors["back"] = [
                self.parent.custom_back_color.red(),
                self.parent.custom_back_color.green(),
                self.parent.custom_back_color.blue(),
                self.parent.custom_back_color.alpha()
            ]
        if "bottom" not in custom_colors:
            custom_colors["bottom"] = [
                self.parent.custom_bottom_color.red(),
                self.parent.custom_bottom_color.green(),
                self.parent.custom_bottom_color.blue(),
                self.parent.custom_bottom_color.alpha()
            ]

        self.parent.device_custom_colors[current_url] = custom_colors
        self.parent.save_settings()
        self.update_button_texts()  # Обновляем текст кнопок
        
        self.parent.update_colors()
        #self.parent.update_always_on_top_checkbox_style()  # Обновляем стиль чекбокса
        QTimer.singleShot(100, self.parent.set_icon) 

        self.update_checkbox_styles()

    def save_settings(self):
        self.parent.show_names = self.show_names_checkbox.isChecked()  # 
        self.parent.save_settings()  # Вызываем метод WebBrowser
        


    
    def update_show_names(self, state):
        self.parent.show_names = (state == Qt.CheckState.Checked.value)
        Debug.info(f"[update_show_names] show_names set to {self.parent.show_names}")

        # Сохраняем настройки
        self.save_settings()

        # Обновляем модель автодополнения
        self.parent.device_model = self.parent.load_devices_for_autocomplete()

        # Извлекаем данные из QStandardItemModel для отладки
        model_items = []
        for row in range(self.parent.device_model.rowCount()):
            item = self.parent.device_model.item(row)
            if item:
                model_items.append(item.text())
        Debug.lime(f"[update_show_names] device_model updated to {model_items}")

        # Устанавливаем новую модель для автодополнения
        self.parent.completer.setModel(self.parent.device_model)

        # Обновляем стили автодополнения
        self.parent.update_completer_style()

        # Устанавливаем текст в поле ввода для первого устройства
        if os.path.exists("discovered_devices.json"):
            try:
                with open("discovered_devices.json", 'r') as f:
                    devices = json.load(f)
                    Debug.lime(f"[update_show_names] loaded devices: {devices}")
                    if devices:
                        new_text = devices[0].get('name', '') if self.parent.show_names else devices[0].get('url', '')
                        Debug.lime(f"[update_show_names] setting address_input text to {new_text}")
                        self.parent.address_input.setText(new_text)
                        # Сбрасываем префикс и показываем список
                        self.parent.completer.setCompletionPrefix("")
                        self.parent.completer.complete()
                    else:
                        Debug.lime("[update_show_names]: no devices found in discovered_devices.json")
                        self.parent.address_input.clear()
            except json.JSONDecodeError as e:
                Debug.error(f"update_show_names: JSON decode error: {e}")
                self.parent.address_input.clear()
        else:
            Debug.warning("update_show_names: discovered_devices.json not found")
            self.parent.address_input.clear()

    def update_title_device(self, state):
        self.parent.title_device = (state == Qt.CheckState.Checked.value)
        self.save_settings()  
        self.parent.set_title(False)

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
            Debug.info(f"GLUON_only updated to: {self.parent.gluon_only}")
     
    def update_snow_man_swch(self, state):
        self.parent.snow_man_swch = (state == Qt.CheckState.Checked.value)
        self.parent.save_settings()
        self.parent.set_icon()
        self.parent.set_title(False)



    def update_wled_search(self, state):
        self.parent.wled_search = (state == Qt.CheckState.Checked.value)
        self.parent.save_settings()

        Debug.info(f"WLED_search updated to: {self.parent.wled_search}")
    
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

class SplashScreen(QWidget):
    def __init__(self):
        super().__init__()
        self.setWindowFlags(Qt.WindowType.FramelessWindowHint | Qt.WindowType.WindowStaysOnTopHint)
        self.setAttribute(Qt.WidgetAttribute.WA_TranslucentBackground)
        
        # Загружаем настройки для определения splash screen
        self.snow_man = False
        self.snow_man_swch = True
        if os.path.exists("settings.json"):
            with open("settings.json", 'r') as f:
                settings = json.load(f)
                self.snow_man = settings.get("snow_man", False)
                self.snow_man_swch = settings.get("snow_man_swch", True)
        
        # Выбираем изображение splash screen
        splash_image = self.get_splash_image()
        self.pixmap = QPixmap(resource_path(splash_image))
        if not self.pixmap.isNull():
            self.setFixedSize(self.pixmap.size())
        else:
            self.setFixedSize(400, 300)
        
        # Центрируем на экране
        screen = QApplication.primaryScreen().geometry()
        self.move((screen.width() - self.width()) // 2, (screen.height() - self.height()) // 2)
        
        # Создаем виджеты для текста и прогресса
        title_text = "Red Snow Man" if (self.snow_man and self.snow_man_swch) else "Remote Settings Management"
        self.title = QLabel(title_text)
        self.title.setAlignment(Qt.AlignmentFlag.AlignCenter)
        self.title.setStyleSheet("""
            color: white; 
            font-size: 20px; 
            font-weight: 900; 
            background: transparent;
        """)
        
        shadow = QGraphicsDropShadowEffect()
        shadow.setBlurRadius(12)
        shadow.setColor(QColor(255, 0, 0, 230))
        shadow.setOffset(0, 0)
        self.title.setGraphicsEffect(shadow)


        self.version = QLabel(f"Версия {VERSION}")
        self.version.setAlignment(Qt.AlignmentFlag.AlignCenter)
        self.version.setStyleSheet("color: #ccc; font-size: 12px; background: transparent;")
        
        self.progress = QProgressBar()
        self.progress.setRange(0, 100)  # Вместо (0, 0)
        self.progress.setValue(0)
        self.progress.setStyleSheet("""
            QProgressBar { 
                background: rgba(180,0,0,90); 
                border-radius: 9px; 
            }
            QProgressBar::chunk { 
                background: rgba(200,0,0,200); 
                border-radius: 4px; 
            }
        """)
        
        # Размещаем элементы в нижней части
        layout = QVBoxLayout()
        layout.addStretch()
        layout.addWidget(self.title)
        layout.addWidget(self.version)
        layout.addWidget(self.progress)
        layout.setContentsMargins(20, 20, 20, 20)
        self.setLayout(layout)
    
    
    def update_progress(self, value, text=""):
        self.progress.setValue(value)
        if text:
            self.version.setText(text)

    def get_splash_image(self):
        """Возвращает имя файла splash screen на основе флагов"""
        if self.snow_man and self.snow_man_swch:
            return 'splash_sm.png'
        else:
            return 'splash.png'
    
    def paintEvent(self, event):
        painter = QPainter(self)
        if not self.pixmap.isNull():
            painter.drawPixmap(self.rect(), self.pixmap)

class AppLoader(QObject):
    finished = pyqtSignal()
    progress_updated = pyqtSignal(int, str)
    
    def __init__(self, splash):
        super().__init__()
        self.splash = splash
    
    def load_app(self):
        self.progress_updated.emit(20, "Создание окна...")
        QApplication.processEvents()
        
        # Передаем splash в WebBrowser
        self.main_window = WebBrowser(splash=self.splash)
        
        self.progress_updated.emit(100, "Готово!")
        self.finished.emit()


def main():
    Debug.pink(NAME + " " +  VERSION)
    app = QApplication(sys.argv)
    app.setStyleSheet(DARK_THEME)
    
    # Показываем сплеш-скрин
    splash = SplashScreen()
    splash.show()
    app.processEvents()
    
    # Загружаем приложение
    loader = AppLoader(splash)
    loader.progress_updated.connect(splash.update_progress)
    
    def on_loaded():
        splash.close()
        loader.main_window.show()
    
    loader.finished.connect(on_loaded)
    QTimer.singleShot(50, loader.load_app)
    
    sys.exit(app.exec())



if __name__ == "__main__":
    main()




