import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private final Socket socket;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            
            String mensaje;
            while ((mensaje = in.readLine()) != null) {
                if ("CHAO".equalsIgnoreCase(mensaje.trim())) {
                    out.println("¡Adiós!");
                    break;
                }
                // Devuelve el eco del mensaje recibido
                out.println("ECO: " + mensaje);
            }
        } catch (IOException e) {
            System.err.println("Error manejando al cliente: " + e.getMessage());
        } finally {
            try {
                socket.close();
                System.out.println("Conexión cerrada con el cliente.");
            } catch (IOException e) {
                System.err.println("Error al cerrar el socket: " + e.getMessage());
            }
        }
    }
}