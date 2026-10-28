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

    public static Command parse(String inputLine) {
        if (inputLine == null || inputLine.trim().isEmpty()) {
            return new Command("ERROR", "Comando vacío o nulo");
        }

        String linea = inputLine.trim();
        String[] partes = linea.split(" ", 2);
        String comando = partes[0].toUpperCase();
        String argumento = partes.length > 1 ? partes[1].trim() : "";

        if (comando.equals("CONNECT") || comando.equals("JOIN") || comando.equals("MSG")) {
            if (argumento.isEmpty()) {
                return new Command("ERROR", "El comando " + comando + " requiere un argumento.");
            }
            return new Command(comando, argumento);
        } else if (comando.equals("QUIT")) {
            return new Command("QUIT", "");
        } else {
            return new Command("ERROR", "Comando desconocido: " + comando);
        }
    }
}