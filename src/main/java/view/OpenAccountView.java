package view;

import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.util.function.Consumer;

/**
 * Boundary class for opening an account. Collects raw text only;
 * the controller decides what is valid for each account type.
 */
public class OpenAccountView {

    /** Plain data holder - all fields are raw strings from the text boxes. */
    public static class Request {
        public final String type, branch, initialDepositText, employerName, employerAddress;

        public Request(String type, String branch, String initialDepositText,
                       String employerName, String employerAddress) {
            this.type = type;
            this.branch = branch;
            this.initialDepositText = initialDepositText;
            this.employerName = employerName;
            this.employerAddress = employerAddress;
        }
    }

    private final ComboBox<String> typeBox = new ComboBox<>();
    private final TextField branchField = new TextField();
    private final TextField depositField = new TextField();
    private final TextField employerNameField = new TextField();
    private final TextField employerAddressField = new TextField();
    private final Label messageLabel = new Label();
    private final Button openButton = new Button("Open account");
    private final Button backButton = new Button("Back");

    public Parent build() {
        typeBox.getItems().addAll("Savings", "Investment", "Cheque");
        typeBox.getSelectionModel().selectFirst();
        messageLabel.setStyle("-fx-text-fill: red;");

        VBox root = new VBox(8,
                new Label("Account type:"), typeBox,
                new Label("Branch:"), branchField,
                new Label("Initial deposit (Investment only, min BWP 500):"), depositField,
                new Label("Employer name (Cheque only):"), employerNameField,
                new Label("Employer address (Cheque only):"), employerAddressField,
                openButton, backButton, messageLabel);
        root.setPadding(new Insets(20));
        return root;
    }

    public void setOnOpenAccount(Consumer<Request> handler) {
        openButton.setOnAction(e -> handler.accept(new Request(
                typeBox.getValue(), branchField.getText(), depositField.getText(),
                employerNameField.getText(), employerAddressField.getText())));
    }

    public void setOnBack(Runnable handler) {
        backButton.setOnAction(e -> handler.run());
    }

    public void showMessage(String message) {
        messageLabel.setText(message);
    }
}