package ownwn;

import java.io.File;
import java.net.URL;
import java.util.*;

public class AnnotationFinder {
    public static Map<String, HttpMethod> getAllAnnotatedMethods(String packageName) {
        Map<String, HttpMethod> map = new HashMap<>();

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

            classes.forEach(clazz -> grabMethods(clazz, map));

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

    private static void grabMethods(Class<?> clazz, Map<String, HttpMethod> map) {
        Arrays.stream(clazz.getDeclaredMethods())
                .filter(m -> m.isAnnotationPresent(Anno.class))
                .map(m -> new HttpMethod() {
                    @Override
                    public Handler handler() {
                        return m::invoke;
                    }

                    @Override
                    public String getPath() {
                        return Objects.requireNonNull(m.getAnnotation(Anno.class).value());
                    }
                }).forEach(m -> {
                    if (map.put(m.getPath(), m) != null) {
                        throw new RuntimeException("Duplicate paths " + m.getPath());
                    }
                });
    }
}
