import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class EchoClient {
    public static void main(String[] args) throws IOException {
        int puerto = 8081;
        
        try (Socket socket = new Socket("localhost", puerto)) {
            System.out.println("Conectado al servidor en el puerto " + puerto);
            System.out.println("Escribe un mensaje (escribe 'CHAO' para salir):");

            // 1. Lector para lo que escribes en la consola (teclado)
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
            
            // 2. Escritor para enviar datos al servidor (con autoFlush en true)
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            
            // 3. Lector para recibir la respuesta del servidor
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String linea;
            // Lee continuamente desde la consola
            while ((linea = stdIn.readLine()) != null) {
                // Envía el texto al servidor
                out.println(linea);
                
                // Si escribes CHAO, terminamos el bucle cliente
                if ("CHAO".equalsIgnoreCase(linea.trim())) {
                    break;
                }
                
                // Lee la respuesta que devuelve el servidor y muéstrala en pantalla
                String respuesta = in.readLine();
                System.out.println(respuesta);
            }

        } catch (IOException e) {
            System.err.println("No se pudo conectar al servidor: " + e.getMessage());
        }
    }
}