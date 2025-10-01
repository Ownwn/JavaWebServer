import ownwn.Server;

void main() {
    IO.println("hey!");
    try {
        new Server();
    } catch (IOException e) {
        throw new RuntimeException(e);
    }
}