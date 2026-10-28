import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
public class EchoServer {
    public static void main(String[] args) throws IOException {
    int puerto = 8081;
    try (ServerSocket serverSocket = new ServerSocket(puerto)) {
        System.out.println("Servidor iniciado en puerto " + puerto);
        Socket clientSocket = serverSocket.accept(); // Bloqueante
        BufferedReader in = new BufferedReader(
            new InputStreamReader(clientSocket.getInputStream()));
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        
        
        String mensaje;
        while ((mensaje = in.readLine()) != null) {
            if ("CHAO".equalsIgnoreCase(mensaje)) break;
            out.println("ECO: " + mensaje);
        }
    }
    }
}