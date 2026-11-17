package view;

import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.util.function.BiConsumer;

public class LoginView {

    private final TextField usernameField = new TextField();
    private final PasswordField passwordField = new PasswordField();
    private final Label messageLabel = new Label();
    private final Button loginButton = new Button("Log in");

    public Parent build() {
        messageLabel.setStyle("-fx-text-fill: red;");
        VBox root = new VBox(10,
                new Label("Username:"), usernameField,
                new Label("Password:"), passwordField,
                loginButton, messageLabel);
        root.setPadding(new Insets(20));
        return root;
    }

    public void setOnLogin(BiConsumer<String, String> handler) {
        loginButton.setOnAction(e ->
                handler.accept(usernameField.getText(), passwordField.getText()));
    }

    public void showMessage(String message) {
        messageLabel.setText(message);
    }
}