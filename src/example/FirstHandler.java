package example;

import server.Handle;
import server.HttpMethod;
import server.Request;
import server.Response;

public class FirstHandler {

    @Handle("testget")
    public static Response handle(Request request) {
        System.out.println("Handling get request...");
        return Response.of(200, "cool response");
    }

    @Handle(value = "testpost", method = HttpMethod.POST)
    public static Response handle2(Request request) {
        System.out.println("Handling post");
        return Response.of("hello post!");
    }
}
