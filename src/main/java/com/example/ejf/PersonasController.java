package com.example.ejf;

import com.example.ejf.model.Persona;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.stage.FileChooser;

/**
 * Controlador principal para la gestion de personas.
 * Permite agregar, modificar, eliminar, filtrar, exportar e importar personas.
 */
public class PersonasController implements Initializable {

    @FXML
    private TableView<Persona> tableView;

    @FXML
    private TableColumn<Persona, String> nombre;

    @FXML
    private TableColumn<Persona, String> apellidos;

    @FXML
    private TableColumn<Persona, Integer> edad;

    @FXML
    private TextField txtFiltro;

    private ObservableList<Persona> listaPersonas = FXCollections.observableArrayList();

    /**
     * Inicializa las columnas de la tabla y enlaza los datos con sus propiedades.
     *
     * @param location  Ubicacion utilizada para resolver rutas relativas para el objeto raiz, o null si no se conoce.
     * @param resources Recursos utilizados para localizar el objeto raiz, o null si no se especifican.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        nombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        apellidos.setCellValueFactory(new PropertyValueFactory<>("apellidos"));
        edad.setCellValueFactory(new PropertyValueFactory<>("edad"));
    }

    /**
     * Abre una ventana modal para agregar una nueva persona.
     *
     * @param event Evento que dispara la accion.
     */
    @FXML
    void agregar(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("agregar.fxml"));
            Scene scene = new Scene(loader.load());

            AgregarController agregarController = loader.getController();
            agregarController.setMainController(this);
            agregarController.setModoModificar(false);

            Stage stage = new Stage();
            stage.setTitle("Nueva Persona");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Abre una ventana modal para modificar una persona seleccionada.
     *
     * @param event Evento que dispara la accion.
     */
    @FXML
    void modificar(ActionEvent event) {
        Persona personaSeleccionada = tableView.getSelectionModel().getSelectedItem();
        if (personaSeleccionada == null) {
            mostrarAlertaError("Error", "Debes seleccionar una persona para modificarla.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("agregar.fxml"));
            Scene scene = new Scene(loader.load());

            AgregarController agregarController = loader.getController();
            agregarController.setMainController(this);
            agregarController.setModoModificar(true);
            agregarController.llenarCampos(personaSeleccionada);

            Stage stage = new Stage();
            stage.setTitle("Editar persona");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Elimina una persona seleccionada de la tabla.
     *
     * @param event Evento que dispara la accion.
     */
    @FXML
    void eliminar(ActionEvent event) {
        Persona personaSeleccionada = tableView.getSelectionModel().getSelectedItem();
        if (personaSeleccionada == null) {
            mostrarAlertaError("Error", "Debes seleccionar una persona para eliminarla.");
            return;
        }

        tableView.getItems().remove(personaSeleccionada);
        mostrarAlertaExito("Eliminacion", "Persona eliminada correctamente.");
    }

    /**
     * Filtra las personas por nombre basado en el texto ingresado en el campo de filtro.
     */
    @FXML
    public void filtrarPorNombre() {
        String filtro = txtFiltro.getText().toLowerCase();

        if (filtro.isEmpty()) {
            tableView.setItems(listaPersonas);
        } else {
            ObservableList<Persona> listaFiltrada = FXCollections.observableArrayList();
            for (Persona persona : listaPersonas) {
                if (persona.getNombre().toLowerCase().contains(filtro)) {
                    listaFiltrada.add(persona);
                }
            }
            tableView.setItems(listaFiltrada);
        }
    }

    /**
     * Exporta los datos de las personas en formato CSV.
     */
    public void exportar() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar como");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));

        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write("Nombre,Apellidos,Edad");
                writer.newLine();

                ObservableList<Persona> personas = tableView.getItems();
                for (Persona persona : personas) {
                    writer.write(String.format("%s,%s,%d", persona.getNombre(), persona.getApellidos(), persona.getEdad()));
                    writer.newLine();
                }

                mostrarAlertaExito("Exportación exitosa", "Los datos han sido exportados correctamente.");
            } catch (IOException e) {
                mostrarAlertaError("Error", "No se pudo exportar el archivo.");
            }
        }
    }

    /**
     * Importa los datos de personas desde un archivo CSV.
     */
    public void importar() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Abrir archivo");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));

        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                boolean isFirstLine = true;
                while ((line = reader.readLine()) != null) {
                    if (isFirstLine) {
                        isFirstLine = false;
                        continue;
                    }

                    String[] data = line.split(",");
                    if (data.length == 3) {
                        String nombre = data[0].trim();
                        String apellidos = data[1].trim();
                        int edad;

                        try {
                            edad = Integer.parseInt(data[2].trim());
                        } catch (NumberFormatException e) {
                            mostrarAlertaError("Error", "La edad debe ser un numero valido: " + line);
                            continue;
                        }

                        if (tableView.getItems().stream().noneMatch(p -> p.getNombre().equals(nombre) && p.getApellidos().equals(apellidos))) {
                            tableView.getItems().add(new Persona(nombre, apellidos, edad));
                        } else {
                            mostrarAlertaError("Duplicado", "El registro ya existe: " + line);
                        }
                    } else {
                        mostrarAlertaError("Error", "Formato de linea invalido: " + line);
                    }
                }

                mostrarAlertaExito("Info", "Los datos han sido importados correctamente.");
            } catch (IOException e) {
                mostrarAlertaError("Error", "No se pudo importar el archivo.");
            }
        }
    }

    /**
     * Verifica si una persona ya existe en la tabla.
     *
     * @param persona La persona a verificar.
     * @return true si la persona existe, false en caso contrario.
     */
    public boolean existePersona(Persona persona) {
        return tableView.getItems().contains(persona);
    }

    /**
     * Agrega una nueva persona a la tabla.
     *
     * @param persona La nueva persona a agregar.
     */
    public void agregarPersonaTabla(Persona persona) {
        listaPersonas.add(persona);
        tableView.getItems().add(persona);
    }

    /**
     * Modifica una persona existente en la tabla.
     *
     * @param personaOriginal  La persona original a modificar.
     * @param personaModificada Los nuevos datos de la persona.
     */
    public void modificarPersonaTabla(Persona personaOriginal, Persona personaModificada) {
        int indice = tableView.getItems().indexOf(personaOriginal);
        tableView.getItems().set(indice, personaModificada);
    }

    /**
     * Muestra una alerta de exito con un mensaje especifico.
     *
     * @param titulo Titulo de la alerta.
     * @param mensaje Mensaje de exito a mostrar.
     */
    private void mostrarAlertaExito(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    /**
     * Muestra una alerta de error con un mensaje especifico.
     *
     * @param titulo Titulo de la alerta.
     * @param mensaje Mensaje de error a mostrar.
     */
    private void mostrarAlertaError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}