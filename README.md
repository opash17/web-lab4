# Лабораторная работа № 4

Опаш А.Б., группа 4314. Веб-программирование.
Преподаватель: Павловский М.Г.

Тема: интернационализация web-приложений.

## Приложение

Сервлет `BooksList` выводит список книг на русском или английском языке.
Язык можно выбрать на стартовой странице или переключить ссылками над таблицей.
Параметр запроса `lang` принимает `ru` и `en`; если его нет, используется `ru` из `web.xml`.

Переводы загружаются из `messages.properties` и `messages_en.properties`.
Файлы лежат в `src/main/resources`, а после сборки попадают в `WEB-INF/classes`.
Названия книг и авторов оставлены без перевода, тексты интерфейса и отметки о чтении переведены.

## Сборка и запуск

Нужны JDK 17 или новее, Maven и Tomcat 9.
Работа проверена с JDK 24.0.2, Maven 3.9.16 и Tomcat 9.0.121.

```shell
mvn clean install
```

Скопировать `target/lab4-webapp.war` в папку `webapps` Tomcat.
Запустить Tomcat и открыть `http://localhost:8081/lab4-webapp/`.
Для прямого перехода к языковой версии можно открыть:

```text
http://localhost:8081/lab4-webapp/BooksList?lang=ru
http://localhost:8081/lab4-webapp/BooksList?lang=en
```

В работе Tomcat настроен на порт 8081. Если используется стандартная настройка,
заменить его на 8080.

Проект можно импортировать в Eclipse через File → Import → Maven → Existing Maven Projects.
Сборка в Eclipse: Run As → Maven install.

## Javadoc

```shell
mvn javadoc:javadoc
```

Документация будет в `target/site/apidocs/index.html`.
Её копия есть в `docs/javadoc`.

## Проверка

После запуска Tomcat выполнить `python check_app.py`.
Скрипт проверяет стартовую страницу, обе локали, язык по умолчанию,
переключение языка и ошибку при неподдерживаемом языке.

## Отчёт

- [Word](docs/Opash_4314_lab4.docx)
- [PDF](docs/Opash_4314_lab4.pdf)
