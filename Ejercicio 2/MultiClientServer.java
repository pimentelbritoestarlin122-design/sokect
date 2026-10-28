import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultiClientServer {
    private static final int PUERTO = 8084; 
    
    // Creamos un pool de hilos para atender hasta 10 clientes en paralelo
    private static final ExecutorService pool = Executors.newFixedThreadPool(10); 

    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(PUERTO);
        System.out.println("Servidor Multihilo listo en el puerto " + PUERTO + "...");
        
        while (true) {
            Socket socket = server.accept(); // Espera y acepta la conexión de un cliente
            System.out.println("¡Nuevo cliente conectado desde: " + socket.getInetAddress() + "!");
            
            // Delega el socket al ClientHandler para que lo atienda en un hilo separado
            pool.execute(new ClientHandler(socket)); 
        }
    }
}