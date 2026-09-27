package ru.opash.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Показывает список книг на русском или английском языке.
 * Переводы интерфейса хранятся в messages.properties и messages_en.properties.
 * Язык выбирается параметром запроса lang со значением ru или en.
 * Названия книг и авторов остаются одинаковыми для обеих локалей.
 *
 * @author Опаш А.Б., группа 4314
 */
public class BooksList extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private String defaultLanguage;
    private static final String[][] BOOKS = {
        {"М. Булгаков", "Мастер и Маргарита", "true"},
        {"В. Пелевин", "Чапаев и Пустота", "false"},
        {"А. Пушкин", "Капитанская дочка", "true"},
        {"Ф. Достоевский", "Преступление и наказание", "false"}
    };

    /** Создаёт сервлет; язык по умолчанию читается из web.xml. */
    public BooksList() { super(); }

    /** Читает параметр defaultLanguage из web.xml. */
    @Override
    public void init() throws ServletException {
        defaultLanguage = getInitParameter("defaultLanguage");
        if (!"ru".equals(defaultLanguage) && !"en".equals(defaultLanguage)) {
            throw new ServletException("В web.xml нужно задать defaultLanguage: ru или en");
        }
    }

    /** Обрабатывает GET-запрос с выбранным языком. */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /** Переключает язык, сохраняя выбранное значение в параметре запроса. */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String lang = request.getParameter("lang");
        if (lang == null || lang.isBlank()) lang = defaultLanguage;
        response.sendRedirect(request.getContextPath() + "/BooksList?lang=" + lang);
    }

    /**
     * Загружает файл ресурсов выбранной локали и выводит локализованный список книг.
     * Если параметр lang пропущен, используется значение из web.xml.
     * Поддерживаются русский (ru) и английский (en) языки.
     *
     * @param request HTTP-запрос с необязательным параметром lang
     * @param response HTML-ответ в UTF-8
     * @throws IOException если запись ответа завершилась ошибкой
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String lang = request.getParameter("lang");
        if (lang == null || lang.isBlank()) lang = defaultLanguage;
        lang = lang.trim().toLowerCase(Locale.ROOT);
        if (!(lang.equals("ru") || lang.equals("en"))) {
            response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
            response.setContentType("text/html;charset=UTF-8");
            String error = ResourceBundle.getBundle("messages", Locale.forLanguageTag(defaultLanguage))
                    .getString(lang.isBlank() ? "missing" : "invalid");
            response.getWriter().println("<!DOCTYPE html><html lang='" + defaultLanguage
                    + "'><meta charset='UTF-8'><title>400</title><h1>"
                    + escapeHtml(error) + "</h1><a href='index.html'>"
                    + escapeHtml(defaultLanguage.equals("ru") ? "Выбрать язык" : "Choose a language")
                    + "</a></html>");
            return;
        }
        Locale locale = Locale.forLanguageTag(lang.equals("en") ? "en-US" : "ru-RU");
        ResourceBundle text;
        try {
            text = ResourceBundle.getBundle("messages", locale);
        } catch (MissingResourceException exception) {
            throw new ServletException("Не найден файл ресурсов для языка " + lang, exception);
        }
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.println("<!DOCTYPE html><html lang='" + lang + "'><head><meta charset='UTF-8'>"
                + "<meta name='viewport' content='width=device-width, initial-scale=1'>"
                + "<title>" + escapeHtml(text.getString("title"))
                + "</title><link rel='stylesheet' href='style.css'></head><body><main>");
        out.println("<p class='info'>" + escapeHtml(text.getString("student")) + "</p>");
        out.println("<h1>" + escapeHtml(text.getString("title")) + "</h1>");
        out.println("<p>" + escapeHtml(text.getString("language")) + ": "
                + (lang.equals("ru") ? "Русский" : "English") + "</p>");
        out.println("<nav aria-label='" + escapeHtml(text.getString("language")) + "'>"
                + "<a class='button' href='BooksList?lang=ru'>"
                + escapeHtml(text.getString("russian")) + "</a>"
                + "<a class='button' href='BooksList?lang=en'>"
                + escapeHtml(text.getString("english")) + "</a></nav>");
        out.println("<table><thead><tr><th>" + escapeHtml(text.getString("author"))
                + "</th><th>" + escapeHtml(text.getString("book.title"))
                + "</th><th>" + escapeHtml(text.getString("read"))
                + "</th></tr></thead><tbody>");
        for (String[] book : BOOKS) {
            out.println("<tr><td>" + escapeHtml(book[0]) + "</td><td>"
                    + escapeHtml(book[1]) + "</td><td>"
                    + escapeHtml(text.getString(Boolean.parseBoolean(book[2]) ? "yes" : "no"))
                    + "</td></tr>");
        }
        out.println("</tbody></table><p>" + escapeHtml(MessageFormat.format(
                text.getString("count"), BOOKS.length)) + "</p>");
        out.println("<p><a href='index.html'>" + escapeHtml(text.getString("back"))
                + "</a></p></main></body></html>");
    }

    private static String escapeHtml(String value) {
        return value.replace("&", "&amp;").replace("<", "&lt;")
                .replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&#39;");
    }
}
