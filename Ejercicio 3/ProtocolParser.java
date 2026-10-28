public class ProtocolParser {
    public static class Command {
        private final String tipo;
        private final String argumento;

        public Command(String tipo, String argumento) {
            this.tipo = tipo;
            this.argumento = argumento;
        }

        public String getTipo() {
            return tipo;
        }

        public String getArgumento() {
            return argumento;
        }
    }

    /**
     * Analiza la línea de texto que envía el cliente.
     * Ejemplos esperados:
     * - CONNECT Estarlin
     * - JOIN #general
     * - MSG Hola a todos
     * - QUIT
     */
    public static Command parse(String inputLine) {
        if (inputLine == null || inputLine.trim().isEmpty()) {
            return new Command("ERROR", "Comando vacío o nulo");
        }

        String linea = inputLine.trim();
        // Separamos el comando del resto del texto (máximo 2 partes: comando y argumento)
        String[] partes = linea.split(" ", 2);
        String comando = partes[0].toUpperCase();
        String argumento = partes.length > 1 ? partes[1].trim() : "";

        switch (comando) {
            case "CONNECT":
            case "JOIN":
            case "MSG":
                if (argumento.isEmpty()) {
                    return new Command("ERROR", "El comando " + comando + " requiere un argumento.");
                }
                return new Command(comando, argumento);
                
            case "QUIT":
                return new Command("QUIT", "");

            default:
                return new Command("ERROR", "Comando desconocido: " + comando);
        }
    }
}