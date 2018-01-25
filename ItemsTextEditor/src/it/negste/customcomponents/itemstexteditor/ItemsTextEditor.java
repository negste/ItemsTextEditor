package it.negste.customcomponents.itemstexteditor;

import java.util.function.UnaryOperator;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Control;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.Region;

/**
 * Controllo usato per editare una serie di elementi Stringa; Può essere
 * definita una lunghezza massima per tali elementi.
 * <br>
 * TODO: sarebbe più completo dare la possibilità di definire dei validatori
 * custom per l'edit delle stringhe
 *
 * @author ced04u
 */
public class ItemsTextEditor extends Control {

    private final ObservableList<String> _items = FXCollections.observableArrayList();

    /**
     *
     * @return la lista degli elementi rappresentati
     */
    public final ObservableList<String> itemsProperty() {
        return _items;
    }

    private UnaryOperator<TextFormatter.Change> _editorFilter;

    public UnaryOperator<TextFormatter.Change> getEditorFilter() {
        return _editorFilter;
    }

    public void setEditorFilter(UnaryOperator<TextFormatter.Change> pFilter) {
        _editorFilter = pFilter;
    }
    
    private final DoubleProperty editorPrefWidth = new SimpleDoubleProperty(this, "editorPrefWidth", Region.USE_PREF_SIZE);
    public double getEditorPrefWidth() {
        return editorPrefWidth.doubleValue();
    }
    public DoubleProperty editorPrefWidthProperty() {
        return editorPrefWidth;
    }
    public void setEditorPrefWidth(double pValue) {
        editorPrefWidth.set(pValue);
    }

    public ItemsTextEditor() {
        //skin set via .css
        getStyleClass().add("items-text-editor");
    }

    @Override
    public String getUserAgentStylesheet() {
        return ItemsTextEditor.class.getResource("itemstexteditor.css").toExternalForm();
    }

}
