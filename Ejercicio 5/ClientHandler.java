import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

public class ClientHandler implements Runnable {
    private final Socket socket;
    private final RoomManager roomManager;
    private PrintWriter out;
    private BufferedReader in;
    private String username;
    private String currentRoom;

    public ClientHandler(Socket socket, RoomManager roomManager) {
        this.socket = socket;
        this.roomManager = roomManager;
    }

    @Override
    public void run() {
        try {
            // Configurar timeout de lectura de 30 segundos (Exigencia Ejercicio 5)
            socket.setSoTimeout(30000);

            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            String linea;
            while ((linea = in.readLine()) != null) {
                linea = linea.trim();
                if (linea.equalsIgnoreCase("QUIT")) {
                    break;
                }

                ProtocolParser.Command cmd = ProtocolParser.parse(linea);

                switch (cmd.getTipo()) {
                    case "CONNECT":
                        this.username = cmd.getArgumento();
                        out.println("200 OK - Usuario registrado: " + this.username);
                        break;

                    case "JOIN":
                        if (this.currentRoom != null) {
                            roomManager.removerDeSala(this.currentRoom, this);
                        }
                        this.currentRoom = cmd.getArgumento();
                        roomManager.unirseASala(this.currentRoom, this);
                        out.println("JOINED " + this.currentRoom);
                        break;

                    case "MSG":
                        if (this.currentRoom == null || this.username == null) {
                            out.println("401 ERROR - Debes conectarte y unirte a una sala primero.");
                        } else {
                            String mensajeFormateado = "MSG [" + this.username + "]: " + cmd.getArgumento();
                            roomManager.broadcast(this.currentRoom, mensajeFormateado, this);
                            out.println("ENVIADO A LA SALA " + this.currentRoom);
                        }
                        break;

                    case "ERROR":
                        out.println("401 ERROR - " + cmd.getArgumento());
                        break;

                    default:
                        out.println("401 ERROR - Comando no reconocido");
                        break;
                }
            }
        } catch (SocketTimeoutException e) {
            System.err.println("Inactividad detectada. Cerrando conexión por timeout para el usuario: " + (username != null ? username : "Anónimo"));
        } catch (SocketException e) {
            System.err.println("El cliente cerró la conexión abruptamente: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error de E/S en el manejador: " + e.getMessage());
        } finally {
            if (this.currentRoom != null && this.username != null) {
                roomManager.removerDeSala(this.currentRoom, this);
            }
            try {
                socket.close();
            } catch (IOException ignored) {}
        }
    }

    public void enviarMensajeDirecto(String mensaje) {
        if (out != null) {
            out.println(mensaje);
        }
    }
}