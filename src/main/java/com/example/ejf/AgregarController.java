package com.example.ejf;

import com.example.ejf.model.Persona;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AgregarController {

    @FXML
    private TextField txtNombre;

    @FXML
    private TextField txtApellidos;

    @FXML
    private TextField txtEdad;

    @FXML
    private Button btnGuardar;

    @FXML
    private Button btnCancelar;

    private PersonasController helloController;
    private Persona personaOriginal;
    private boolean modoModificar;

    /**
     * Establece el controlador principal de la aplicacion.
     *
     * @param helloController Controlador principal de la aplicacion.
     */
    public void setMainController(PersonasController helloController) {
        this.helloController = helloController;
    }

    /**
     * Establece el modo de modificacion, cambiando el texto del boton.
     *
     * @param modificar Si es verdadero, el boton mostrara "Modificar", si es falso, "Agregar".
     */
    public void setModoModificar(boolean modificar) {
        this.modoModificar = modificar;
        btnGuardar.setText(modificar ? "Modificar" : "Agregar");
    }

    /**
     * Llena los campos con los datos de la persona proporcionada.
     *
     * @param persona Persona cuyos datos se desean mostrar en los campos.
     */
    public void llenarCampos(Persona persona) {
        this.personaOriginal = persona;
        txtNombre.setText(persona.getNombre());
        txtApellidos.setText(persona.getApellidos());
        txtEdad.setText(String.valueOf(persona.getEdad()));
    }

    /**
     * Metodo que se ejecuta al hacer clic en el boton "Guardar". Valida los datos y
     * guarda o modifica la persona en la lista.
     *
     * @param event Evento generado por el clic en el boton.
     */
    @FXML
    void guardar(ActionEvent event) {
        String nombre = txtNombre.getText();
        String apellidos = txtApellidos.getText();
        String edadStr = txtEdad.getText();

        StringBuilder errores = new StringBuilder();
        if (nombre.isEmpty()) {
            errores.append("El campo nombre es obligatorio.\n");
        }

        if (apellidos.isEmpty()) {
            errores.append("El campo apellidos es obligatorio.\n");
        }

        int edad = -1;
        if (edadStr.isEmpty()) {
            errores.append("El campo edad es obligatorio.\n");
        } else {
            try {
                edad = Integer.parseInt(edadStr);
            } catch (NumberFormatException e) {
                errores.append("La edad debe ser un numero entero.\n");
            }
        }

        if (!errores.isEmpty()) {
            mostrarAlertaError("Datos invalidos", errores.toString());
            return;
        }

        Persona nuevaPersona = new Persona(nombre, apellidos, edad);

        if (modoModificar && personaOriginal != null) {
            helloController.modificarPersonaTabla(personaOriginal, nuevaPersona);
            mostrarAlertaExito("Info", "Persona modificada correctamente");
        } else {
            if (helloController.existePersona(nuevaPersona)) {
                mostrarAlertaError("Error", "No puede haber dos personas iguales.");
                return;
            }
            helloController.agregarPersonaTabla(nuevaPersona);
            mostrarAlertaExito("Info", "Persona anadida correctamente");
        }

        Stage stage = (Stage) btnGuardar.getScene().getWindow();
        stage.close();
    }

    /**
     * Metodo que se ejecuta al hacer clic en el boton "Cancelar", cerrando la ventana actual.
     *
     * @param event Evento generado por el clic en el boton.
     */
    @FXML
    void cancelar(ActionEvent event) {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }

    /**
     * Muestra una alerta de exito con el mensaje proporcionado.
     *
     * @param titulo El titulo de la alerta.
     * @param mensaje El mensaje que se muestra en la alerta.
     */
    private void mostrarAlertaExito(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    /**
     * Muestra una alerta de error con el mensaje proporcionado.
     *
     * @param titulo El titulo de la alerta.
     * @param mensaje El mensaje que se muestra en la alerta.
     */
    private void mostrarAlertaError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
