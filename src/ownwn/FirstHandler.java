package ownwn;

import server_files.Handle;
import server_files.HttpMethod;
import server_files.Request;
import server_files.Response;

public class FirstHandler {

    @Handle("okie")
    public static Response handle(Request request) {
        System.out.println("Handling request...");
        return Response.of("cool response");
    }

    @Handle(value = "testpost", method = HttpMethod.POST)
    public static Response handle2(Request request) {
        System.out.println("Handling post");
        return Response.of("hello post!");
    }
}
