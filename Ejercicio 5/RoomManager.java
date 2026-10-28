import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class RoomManager {
    // Mapa concurrente: NombreSala -> Lista de clientes conectados a esa sala
    private final Map<String, List<ClientHandler>> salas = new ConcurrentHashMap<>();

    // Unir un cliente a una sala específica
    public void unirseASala(String nombreSala, ClientHandler cliente) {
        salas.computeIfAbsent(nombreSala, k -> new CopyOnWriteArrayList<>()).add(cliente);
    }

    // Remover un cliente de una sala (útil cuando se desconecta)
    public void removerDeSala(String nombreSala, ClientHandler cliente) {
        List<ClientHandler> miembros = salas.get(nombreSala);
        if (miembros != null) {
            miembros.remove(cliente);
        }
    }

    // Transmitir un mensaje a todos los miembros de la sala excepto al emisor
    public void broadcast(String nombreSala, String mensaje, ClientHandler emisor) {
        List<ClientHandler> miembros = salas.get(nombreSala);
        if (miembros != null) {
            for (ClientHandler miembro : miembros) {
                if (miembro != emisor) { // Evitar eco al propio emisor
                    miembro.enviarMensajeDirecto(mensaje);
                }
            }
        }
    }
}