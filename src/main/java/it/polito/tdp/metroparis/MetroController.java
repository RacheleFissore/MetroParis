package it.polito.tdp.metroparis;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import it.polito.tdp.metroparis.model.Fermata;
import it.polito.tdp.metroparis.model.Model;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;

public class MetroController {

	private Model model;
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private ComboBox<Fermata> cmbArrivo;

    @FXML
    private ComboBox<Fermata> cmbPartenza;
    
    @FXML
    private TableColumn<Fermata, String> colonnaFermata; // Il primo generic della colonna è sempre uguale a quello della riga (Fermata), il secondo invece è il valore del contenuto
    										  // della colonna nella tabella

    @FXML
    private TableView<Fermata> tablePercorso; // E' il tipo di oggetto che viene rappresentato nella riga

    @FXML
    private TextArea txtRisultato;

    @FXML
    void handleCerca(ActionEvent event) {
    	Fermata partenza = cmbPartenza.getValue();
    	Fermata arrivo = cmbArrivo.getValue();
    	
    	if(partenza != null && arrivo != null && !partenza.equals(arrivo)) {
    		List<Fermata> percorso = model.calcolaPercorso(partenza, arrivo);
    		tablePercorso.setItems(FXCollections.observableArrayList(percorso));
    		txtRisultato.setText("Percorso trovato con " + percorso.size() + " stazioni");
    	}
    	else {
    		txtRisultato.setText("Devi selezionare due stazioni diverse tra loro\n");
    	}
     }

    @FXML
    void initialize() {
        assert cmbArrivo != null : "fx:id=\"cmbArrivo\" was not injected: check your FXML file 'Metro.fxml'.";
        assert cmbPartenza != null : "fx:id=\"cmbPartenza\" was not injected: check your FXML file 'Metro.fxml'.";
        assert txtRisultato != null : "fx:id=\"txtRisultato\" was not injected: check your FXML file 'Metro.fxml'.";

        // Questo lavoro devo farlo per ogni colonna della tabella
        colonnaFermata.setCellValueFactory(new PropertyValueFactory<Fermata, String>("nome")); // COstruisce qualsiasi cosa ci stia dentro una cella
        														// Metto i generics della tabella e tra () il nome dell'attributo di Fermata che voglio rappresentare
    }

	public void setModel(Model m) {
		model = m;
		List<Fermata> fermate = model.getFermate();
		cmbPartenza.getItems().addAll(fermate); // getItems() è l'elenco degli elementi della tendina
		cmbArrivo.getItems().addAll(fermate);
	}

}


