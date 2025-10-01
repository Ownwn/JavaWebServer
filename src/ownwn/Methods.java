package ownwn;

import java.lang.reflect.InvocationTargetException;
import java.net.http.HttpRequest;

@FunctionalInterface
interface Handler {
    void resolve(HttpRequest request) throws InvocationTargetException, IllegalAccessException;
}

interface HttpMethod {
    Handler handler();
    String getPath();
}
