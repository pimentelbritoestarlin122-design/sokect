import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MultiClientServer {
    private static final int PUERTO = 8084;
    private static final ExecutorService pool = Executors.newFixedThreadPool(10);
    private static final RoomManager roomManager = new RoomManager();

    public static void main(String[] args) {
        // Registrar hook de apagado para liberar recursos limpiamente (Exigencia Ejercicio 5)
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\nIniciando apagado controlado del servidor...");
            pool.shutdown();
            try {
                if (!pool.awaitTermination(3, TimeUnit.SECONDS)) {
                    pool.shutdownNow();
                }
            } catch (InterruptedException e) {
                pool.shutdownNow();
            }
            System.out.println("Servidor detenido correctamente.");
        }));

        try (ServerSocket server = new ServerSocket(PUERTO)) {
            System.out.println("Servidor Robusto de Salas listo en el puerto " + PUERTO + "...");
            
            while (!server.isClosed()) {
                Socket socket = server.accept();
                System.out.println("Nuevo cliente conectado desde: " + socket.getInetAddress());
                pool.execute(new ClientHandler(socket, roomManager));
            }
        } catch (IOException e) {
            System.err.println("Error en el servidor principal: " + e.getMessage());
        }
    }
}