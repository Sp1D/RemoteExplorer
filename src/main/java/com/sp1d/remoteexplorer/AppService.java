package com.sp1d.remoteexplorer;

import com.google.gson.Gson;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.FileSystems;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Методы общего назначения, вызываемые из любого другого класса
 *
 * @author sp1d
 */
public class AppService {

    public static final String ROOT_PATH_STRING = "/tmp";
    public static Path rootPath;
    public static String SEPARATOR;

//    public Path leftPath, rightPath;
    public Map<Pane, Path> panePaths = new HashMap<>();
    public Set<String> icons;

    static PathMatcher pm;
    static Gson gson = new Gson();

    private static final Logger LOG = LogManager.getLogger(AppService.class);
    private HttpSession session;

    public enum Pane {

        LEFT, RIGHT, BOTH;
    }

    public AppService(HttpSession session) {
        this.session = session;
        SEPARATOR = FileSystems.getDefault().getSeparator();
        try {
            rootPath = Paths.get(ROOT_PATH_STRING);
            String convertedPath = SEPARATOR.equals("\\")
                    ? ROOT_PATH_STRING.replaceAll("\\\\", "\\\\\\\\")
                    : ROOT_PATH_STRING;

            String s = SEPARATOR.equals("\\") ? "\\\\" : SEPARATOR;
            String pattern = ROOT_PATH_STRING.endsWith(SEPARATOR)
                    ? "glob:" + convertedPath + "**"
                    : "regex:" + convertedPath + s + ".*";

            pm = FileSystems.getDefault().getPathMatcher(pattern);

            Pattern patIcon = Pattern.compile(".*[\\/\\\\](.+)\\.png");
            icons = new HashSet<>();
            for (String str : session.getServletContext().getResourcePaths("/static/icons/32px/")) {
                Matcher matchIcon = patIcon.matcher(str);
                if (matchIcon.matches()) {
                    icons.add(matchIcon.group(1));                    
                }
            }

        } catch (InvalidPathException e) {
            LOG.fatal("Root path is incorrect", e);
            throw e;
        } catch (IllegalArgumentException | UnsupportedOperationException e) {
            LOG.fatal("Pathmatcher's pattern is wrong", e);
            throw e;
        }
    }

    /*
  Создает или возвращает экземпляр указанного в параметрах класса, доступный, как атрибут сессии
  Singleton
     */
    public static <T> T inst(HttpSession sess, Class clazz) {
        T inst = (T) sess.getAttribute(clazz.getSimpleName());
        if (inst == null) {
            try {
                Constructor<T> constructor = null;
                try {
                    constructor = clazz.getConstructor(HttpSession.class);
                } catch (NoSuchMethodException e) {                    
//                  Раз нет такого конструктора, попробуем вызвать другой, без аргументов
                } catch (SecurityException e) {
                    LOG.error(e);                    
                }

//                if (clazz.equals(TaskExecutionService.class) || clazz.equals(Tasks.class)) {
                if (constructor != null) {
                    inst = (T) constructor.newInstance(sess);
                } else {
                    inst = (T) clazz.newInstance();
                }
                sess.setAttribute(clazz.getSimpleName(), inst);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                LOG.fatal(e);
            }
        }
        return inst;
    }

    /*
  Задача метода - не дать пользователю или приложению создать Path, указывающий
  куда-то вне специально разрешенной директории. Путь к этой директории задается в 
  константе ROOT_PATH_STRING
     */
    private Path createSecuredPath(String p, Pane pane) {
        Path secured = rootPath;
        if (p != null && !p.isEmpty()) {
            try {
                Path path = Paths.get(p);
//                Путь ДОЛЖЕН быть относительным
                if (!path.isAbsolute()) {
                    /*  Нормализация пути обязательна, иначе будет возможен путь 
                     вида c:\chroot\..\Users, который не подпадает под запирающий паттерн
                    c:\chroot\*
                     */
                    Path temp = panePaths.get(pane).resolve(path).normalize();
                    if (!pm.matches(temp) || !temp.toFile().exists()) {
                        secured = rootPath;
                    } else {
                        secured = temp;
                    }
                }
            } catch (InvalidPathException e) {
                LOG.debug("Unresolvable path given: " + p, e);
                return rootPath;
            }
        }
        return secured;
    }

    /*
     * Возвращает текущий путь, выбранный в указанной в параметрах панели
     */
    private Path getPanePath(Pane pane, HttpServletRequest request) {
        String param = request.getParameter(pane.toString().toLowerCase());
//        Path path = pane == Pane.LEFT ? leftPath
//                : pane == Pane.RIGHT ? rightPath : null;
        Path path = panePaths.get(pane);

        if (param != null) {
            path = createSecuredPath(param, pane);
        } else if (path == null) {
            path = createSecuredPath("", pane);
        }
        return path;
    }

    /*
 * Устанавливает переменную текущего пути для указанной в параметрах панели
     */
    public void setupPanes(HttpServletRequest request, Pane pane) throws IOException {

        if (pane == Pane.BOTH) {
            panePaths.put(Pane.LEFT, getPanePath(Pane.LEFT, request));
            panePaths.put(Pane.RIGHT, getPanePath(Pane.RIGHT, request));
        } else {
            panePaths.put(pane, getPanePath(pane, request));
        }

//        switch (pane) {
//            case LEFT:
//                leftPath = getPanePath(pane, request);
//                break;
//            case RIGHT:
//                rightPath = getPanePath(pane, request);
//                break;
//            case BOTH:
//                leftPath = getPanePath(Pane.LEFT, request);
//                rightPath = getPanePath(Pane.RIGHT, request);
//        }
    }

    /*
     * Метод отправляет строку в JSON сообщении. Создян для сокращения объема 
     * сервлетов
     */
    public void sendJSON(HttpServletResponse resp, String json) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(json);
    }

}
