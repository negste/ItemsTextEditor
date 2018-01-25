package it.negste.customcomponents.itemstexteditor;

import java.util.List;
import java.util.stream.Collectors;
import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.Skin;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;

/**
 * Skin che mostra una label per ciascun elemento, più un textfield per
 * aggiungere nuovi elementi. Ogni label mostra una x per poter eliminare
 * l'elemento. Il doppio click su una label consente di modificare l'elemento.
 *
 * @author ced04u
 */
public class ItemsTextEditorSkin implements Skin<ItemsTextEditor> {

    private ItemsTextEditor _control;
    private HBox _root = new HBox();
    /*
    TODO: avrei preferito usare un FlowPane in modo da poter andare a capo, ma
    usandolo non riuscivo a gestire correttamente il size (era sempre più grande
    del dovuto, ma non ho capito perchè)
     */
//    private FlowPane _root = new FlowPane();
    private final TextField _editor;
    private final ListChangeListener<String> _itemsChangeListener;

    public ItemsTextEditorSkin(ItemsTextEditor itemsTextEditor) {

        super();

        _control = itemsTextEditor;

        _root.getStyleClass().add("root-items-text-editor-pane");

        _control.itemsProperty().forEach(s
                -> _root.getChildren().add(getItemLabel(s)));
        _editor = getItemEditor(Integer.MAX_VALUE, "");
        _root.getChildren().add(_editor);

        _itemsChangeListener = (ListChangeListener.Change<? extends String> c) -> {
            //implementazione minima: cancello e ricreo tutto (tranne l'editor fisso sulla destra)
            _root.getChildren().remove(0, _root.getChildren().size() - 1);
            _control.itemsProperty().forEach(s
                    -> _root.getChildren().add(_root.getChildren().size() - 1, getItemLabel(s)));
        };
        itemsTextEditor.itemsProperty().addListener(_itemsChangeListener);

    }

    private Label getItemLabel(String pText) {

        /*
        A causa di problemi di scaling del SVG non posso usare una shape
        direttamente come graphic della label; devo invece usare almeno una Region
        a cui impostare sia il maxSize che il minSize (è importante impostarli entrambi,
        altrimenti l'immagine non viene scalata correttamente!).
        Qui utilizzo un Button per gestirne il click
        Ho preso spunto da:
        https://stackoverflow.com/questions/40753613/javafx-button-with-svg
         */
        Button btn = new Button();
        btn.getStyleClass().add("ite-x-icon-button");
        Label vResult = new Label(pText, btn);
        vResult.getStyleClass().add("ite-item");

        btn.setOnAction(e -> {
            int vPosition = _root.getChildren().indexOf(vResult);
            _root.getChildren().remove(vResult);
            removeItem(vPosition);
        });

        vResult.setOnMouseClicked(me -> {
            if (me.getClickCount() == 2) {
                /*                
                doppio click: abilitazione editor per l'item
                 */
                int vPostion = _root.getChildren().indexOf(vResult);
                TextField vEditor = getItemEditor(vPostion, pText);
                //invio un ESC a eventuale altro editor aperto
                final List<Node> vTextFields = _root.getChildren().stream()
                        .filter(n -> n instanceof TextField)
                        .collect(Collectors.toList());
                vTextFields.forEach(n -> {
                    KeyEvent ke = new KeyEvent(KeyEvent.KEY_PRESSED,
                            "", "",
                            KeyCode.ESCAPE, false, false, false, false);
                    n.fireEvent(ke);
                });
                _editor.setVisible(false);
                _root.getChildren().add(vPostion, vEditor);
                _root.getChildren().remove(vResult);
                vEditor.selectAll();
                vEditor.requestFocus();
            }
        });

        vResult.setContentDisplay(ContentDisplay.RIGHT);

        return vResult;

    }

    private TextField getItemEditor(int pItemPosition, String pText) {

        TextField vResult = new TextField(pText);
        vResult.getStyleClass().add("ite-editor");

        /*
        Gestione pressione ESC: abbandona le modifiche e chiude l'editor
         */
        vResult.setOnKeyPressed(ke -> {
            if (KeyCode.ESCAPE.equals(ke.getCode()) && !"".equals(pText)) {
                Label vNewLabel = getItemLabel(pText);
                if (pItemPosition < Integer.MAX_VALUE) {
                    _root.getChildren().add(pItemPosition, vNewLabel);
                    _root.getChildren().remove(vResult);
                    _editor.setVisible(true);
                } else {
                    _root.getChildren().add(_root.getChildren().size() - 1, vNewLabel);
                }
                vResult.clear();
            }
        });

        /*
        Gestione pressione ENTER: applica le modifiche e chiude l'editor
         */
        vResult.setOnKeyTyped(ke -> {
            final String vNewText = vResult.getText();
            if ("\r".equals(ke.getCharacter()) && !"".equals(vNewText)) {
                Label vNewLabel = getItemLabel(vNewText);
                if (pItemPosition < Integer.MAX_VALUE) {
                    _root.getChildren().add(pItemPosition, vNewLabel);
                    removeItem(pItemPosition);
                    addItem(pItemPosition, vNewText);
                    _root.getChildren().remove(vResult);
                    _editor.setVisible(true);
                } else {
                    final int vPosition = _root.getChildren().size() - 1;
                    _root.getChildren().add(vPosition, vNewLabel);
                    addItem(vPosition, vNewText);
                }
                vResult.clear();
            }
        });

        TextFormatter<Double> vTF = new TextFormatter<>(_control.getEditorFilter());
        vResult.setTextFormatter(vTF);

        vResult.prefWidthProperty().bind(_control.editorPrefWidthProperty());

        return vResult;

    }

    /**
     * Aggiunge al control un elemento in una specifica posizione, senza far
     * scattare il listener aggiunto internamente per mantenere sincronizzata la
     * skin (questo perchè la modifica è avvenuta proprio a partire dalla skin,
     * che quindi è già aggiornata)
     *
     * @param pPosition la posizione dell'elemento da aggiungere
     * @param pItem l'elemento da aggiungere
     */
    private void addItem(int pPosition, String pItem) {
        _control.itemsProperty().removeListener(_itemsChangeListener);
        try {
            _control.itemsProperty().add(pPosition, pItem);
        } finally {
            _control.itemsProperty().addListener(_itemsChangeListener);
        }
    }

    /**
     * Rimuove dal control l'elemento in una specifica posizione, senza far
     * scattare il listener aggiunto internamente per mantenere sincronizzata la
     * skin (questo perchè la modifica è avvenuta proprio a partire dalla skin,
     * che quindi è già aggiornata)
     *
     * @param pPosition la posizione dell'elemento da aggiungere
     */
    private void removeItem(int pPosition) {
        _control.itemsProperty().removeListener(_itemsChangeListener);
        try {
            _control.itemsProperty().remove(pPosition);
        } finally {
            _control.itemsProperty().addListener(_itemsChangeListener);
        }
    }

    @Override
    public ItemsTextEditor getSkinnable() {
        return _control;
    }

    @Override
    public Node getNode() {
        return _root;
    }

    @Override
    public void dispose() {
        _control = null;
        _root = null;
    }

}
