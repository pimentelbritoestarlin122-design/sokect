import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

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
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            String linea;
            while ((linea = in.readLine()) != null) {
                linea = linea.trim();
                if (linea.equalsIgnoreCase("QUIT")) {
                    break;
                }

                // Usamos el ProtocolParser para interpretar los comandos
                ProtocolParser.Command cmd = ProtocolParser.parse(linea);

                switch (cmd.getTipo()) {
                    case "CONNECT":
                        this.username = cmd.getArgumento();
                        out.println("200 OK - Usuario registrado: " + this.username);
                        break;

                    case "JOIN":
                        // Si ya estaba en otra sala, lo quitamos de la anterior
                        if (this.currentRoom != null) {
                            roomManager.removerDeSala(this.currentRoom, this);
                        }
                        this.currentRoom = cmd.getArgumento();
                        roomManager.unirseASala(this.currentRoom, this);
                        out.println("JOINED " + this.currentRoom);
                        break;

                    case "MSG":
                        if (this.currentRoom == null || this.username == null) {
                            out.println("401 ERROR - Debes conectarte (CONNECT) y unirte a una sala (JOIN) primero.");
                        } else {
                            // Enviar mensaje a todos los demás en la sala (Broadcast)
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
        } catch (IOException e) {
            System.err.println("Cliente desconectado o con error: " + e.getMessage());
        } finally {
            if (this.currentRoom != null && this.username != null) {
                roomManager.removerDeSala(this.currentRoom, this);
            }
            try {
                socket.close();
            } catch (IOException e) {
                // Ignorado
            }
        }
    }

    // Método clave que usa RoomManager para enviar mensajes al cliente (Broadcast)
    public void enviarMensajeDirecto(String mensaje) {
        if (out != null) {
            out.println(mensaje);
        }
    }
}