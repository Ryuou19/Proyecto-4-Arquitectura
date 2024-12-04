import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Tablas {
    private final StringProperty codigo;
    private final StringProperty traduccion;

    public Tablas(String codigo, String traduccion) {
        this.codigo = new SimpleStringProperty(codigo);
        this.traduccion = new SimpleStringProperty(traduccion);
    }

    public String getCodigo() {
        return codigo.get();
    }

    public StringProperty codigoProperty() {
        return codigo;
    }

    public String getTraduccion() {
        return traduccion.get();
    }

    public StringProperty traduccionProperty() {
        return traduccion;
    }
}