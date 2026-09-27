"""Проверка приложения после его запуска в Tomcat."""
import sys
from urllib.request import urlopen
from urllib.error import HTTPError

base = (sys.argv[1] if len(sys.argv) > 1 else 'http://localhost:8081/lab4-webapp').rstrip('/')

def get(path, expected=200, contains=(), absent=()):
    try:
        response = urlopen(base + path, timeout=10)
    except HTTPError as error:
        response = error
    page = response.read().decode('utf-8')
    assert response.status == expected, (path, response.status)
    for value in contains: assert value in page, (path, value)
    for value in absent: assert value not in page, (path, value)
    print('OK', path, response.status)

get('/', contains=('Русский', 'English'))
get('/BooksList?lang=ru', contains=('Список книг читателя', 'Автор', 'Название книги', 'Прочитал', 'Да', 'Нет'))
get('/BooksList?lang=en', contains=('Reader&#39;s book list', 'Author', 'Book title', 'Have read', 'Yes', 'No'),
    absent=('Список книг читателя', 'Название книги'))
get('/BooksList', contains=('Список книг читателя', 'lang=ru'))
get('/BooksList?lang=de', expected=406, contains=('Неизвестный язык',))
get('/BooksList?lang=', contains=('Список книг читателя',))
print('6 checks passed')
