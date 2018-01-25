package tester;

import it.negste.customcomponents.itemstexteditor.ItemsTextEditor;
import java.util.Arrays;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 *
 * @author ced04u
 */
public class Tester extends Application {

    private static ItemsTextEditor _control;    

    @Override
    public void start(Stage primaryStage) {
        
        _control = new ItemsTextEditor();
        _control.setEditorFilter(c -> {
            String text = c.getControlNewText();            
            return text.length() <= 5 ? c : null;
        });
        _control.setEditorPrefWidth(Font.getDefault().getSize() * 5 + 10);
        addDefaultItems();

        VBox root = new VBox();
        root.getStyleClass().add("content");

        Button btn = new Button("Log items");
        Label lbl = new Label();

        btn.setOnAction(e -> {
            lbl.setText(String.join(", ", _control.itemsProperty()));
        });

        Button btn2 = new Button("Add default items");
        btn2.setOnAction(e -> addDefaultItems());

        root.getChildren().addAll(_control, btn, lbl, btn2);

        Scene scene = new Scene(root, 600, 200);
        scene.getStylesheets().add("tester/tester.css");
        primaryStage.setTitle("ItemsTextEditor tester");
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    private void addDefaultItems() {
        _control.itemsProperty().addAll(Arrays.asList("Item1", "Item2", "Item3"));
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
