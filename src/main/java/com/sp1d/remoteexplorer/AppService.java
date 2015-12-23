package com.sp1d.remoteexplorer;

import com.sp1d.remoteexplorer.json.Tasks;
import com.google.gson.Gson;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.FileSystems;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Методы общего назначения, вызываемые из любого другого класса
 *
 * @author sp1d
 */
public class AppService {

    public Path leftPath, rightPath;
//    public static final Path ROOT_PATH = Paths.get("/tmp");
    public static final Path ROOT_PATH = Paths.get(System.getProperty("java.io.tmpdir"));
    final PathMatcher pm = FileSystems.getDefault().getPathMatcher("glob:" + ROOT_PATH.toFile().getAbsolutePath() + "/**");
    static Gson gson = new Gson();

    public enum Pane {

        LEFT, RIGHT, BOTH;
    }
/*
 * Создает или возвращает экземпляр указанного в параметрах класса, доступный, как атрибут сессии
 * Singleton
 */
    public static <T> T inst(HttpSession sess, Class clazz) {
        T inst = (T) sess.getAttribute(clazz.getSimpleName());
        if (inst == null) {
            try {
                if (clazz.equals(TaskExecutionService.class) || clazz.equals(Tasks.class)) {
                    inst = (T) clazz.getConstructor(HttpSession.class).newInstance(sess);
                } else {
                    inst = (T) clazz.newInstance();
                }
                sess.setAttribute(clazz.getSimpleName(), inst);
            } catch (InstantiationException | IllegalAccessException |
                    NoSuchMethodException | InvocationTargetException ex) {

            }
        }
        return inst;
    }
/*
 * Задача метода - не дать пользователю или приложению создать Path, указывающий
 * куда-то вне специально разрешенной директории. Путь к этой директории задается в 
 * константе ROOT_PATH
 */
    private Path createSecuredPath(String p) {

        Path secured = ROOT_PATH;
        if (p != null) {
            try {
                Path path = Paths.get(p);
                if (!path.isAbsolute()) {
                    Path temp = ROOT_PATH.resolve(path);
                    if (!pm.matches(temp) || !temp.toFile().exists()) {
                        secured = ROOT_PATH;
                    } else {
                        secured = temp;
                    }
                }
            } catch (InvalidPathException e) {
                return ROOT_PATH;
            }
        }

        return secured;
    }
    
    /*
     * Возвращает текущий путь, выбранный в указанной в параметрах панели
     */

    private Path getPanePath(Pane pane, HttpServletRequest request) {
        String param = request.getParameter(pane.toString().toLowerCase());
        Path path = pane == Pane.LEFT ? leftPath
                : pane == Pane.RIGHT ? rightPath : null;

        if (param != null) {
            path = createSecuredPath(param);
        } else if (path == null) {
            path = createSecuredPath("");
        }
        return path;
    }
    
/*
 * Устанавливает переменную текущего пути для указанной в параметрах панели
 */
    
    public void setupPanes(HttpServletRequest request, Pane pane) throws IOException {        
        switch (pane) {
            case LEFT:
                leftPath = getPanePath(pane, request);               
                break;
            case RIGHT:
                rightPath = getPanePath(pane, request);                
                break;
            case BOTH:
                leftPath = getPanePath(Pane.LEFT, request);
                rightPath = getPanePath(Pane.RIGHT, request);                
        }        
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
