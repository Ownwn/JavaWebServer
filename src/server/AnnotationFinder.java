package server;

import java.io.File;
import java.net.URL;
import java.util.*;

public class AnnotationFinder {
    private AnnotationFinder() {}

    static Map<String, RequestHandler> getAllAnnotatedMethods(String packageName) {
        Map<String, RequestHandler> map = new HashMap<>();

        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            String path = packageName.replace('.', '/');
            Enumeration<URL> resources = classLoader.getResources(path);

            List<Class<?>> classes = new ArrayList<>();
            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                File directory = new File(resource.getFile());
                classes.addAll(findClasses(directory, packageName));
            }

            classes.forEach(clazz -> loadMethods(clazz, map));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return map;
    }

    private static List<Class<?>> findClasses(File directory, String packageName) {
        List<Class<?>> classes = new ArrayList<>();
        if (!directory.exists()) {
            return classes;
        }

        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    classes.addAll(findClasses(file, packageName + "." + file.getName()));
                } else if (file.getName().endsWith(".class")) {
                    String className = (packageName.isEmpty() ? "" : packageName + ".") + file.getName().substring(0, file.getName().length() - 6);
                    try {
                        classes.add(Class.forName(className));
                    } catch (ClassNotFoundException _) {
                    }
                }
            }
        }
        return classes;
    }

    private static void loadMethods(Class<?> clazz, Map<String, RequestHandler> map) {
        Arrays.stream(clazz.getDeclaredMethods())
                .filter(m -> m.isAnnotationPresent(Handle.class))
                .forEach(m -> {
                    Handle annotation = Objects.requireNonNull(m.getAnnotation(Handle.class));
                    RequestHandler handler = RequestHandler.from(m, annotation);
                    String path = annotation.value();

                    if (map.put(path, handler) != null) {
                        throw new RuntimeException("Duplicate paths " + path);
                    }
                });
    }
}
