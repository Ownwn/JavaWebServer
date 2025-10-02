package server_files;

public record Response(int status, String body) {
    public static Response of(int status, String body) {
        return new Response(status, body);
    }

    public static Response of(String body) {
        return new Response(200, body);
    }
}