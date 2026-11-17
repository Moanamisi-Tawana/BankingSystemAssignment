package view;

import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.function.Consumer;


public class AccountView {

    public static class DepositRequest {
        public final String accountNumber, amountText, note;
        public DepositRequest(String accountNumber, String amountText, String note) {
            this.accountNumber = accountNumber;
            this.amountText = amountText;
            this.note = note;
        }
    }

    public static class WithdrawRequest {
        public final String accountNumber, amountText;
        public WithdrawRequest(String accountNumber, String amountText) {
            this.accountNumber = accountNumber;
            this.amountText = amountText;
        }
    }

    private final Label welcomeLabel = new Label("Accounts");
    private final ListView<String> accountList = new ListView<>();
    private final Label detailsLabel = new Label();
    private final TextField amountField = new TextField();
    private final TextField noteField = new TextField();
    private final Label messageLabel = new Label();
    private final TextArea historyArea = new TextArea();
    private final Button depositButton = new Button("Deposit");
    private final Button withdrawButton = new Button("Withdraw");
    private final Button interestButton = new Button("Pay monthly interest");
    private final Button openAccountButton = new Button("Open new account");
    private final Button logoutButton = new Button("Log out");

    private Consumer<String> onAccountSelected;

    public Parent build() {
        accountList.setPrefHeight(120);
        historyArea.setEditable(false);
        historyArea.setPrefHeight(150);

        accountList.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> {
                    if (newVal != null && onAccountSelected != null) {
                        onAccountSelected.accept(newVal);
                    }
                });

        VBox root = new VBox(8,
                welcomeLabel,
                new Label("Your accounts:"), accountList, detailsLabel,
                new HBox(8, new Label("Amount:"), amountField,
                        new Label("Note:"), noteField),
                new HBox(8, depositButton, withdrawButton, interestButton),
                messageLabel,
                new Label("Transaction history:"), historyArea,
                new HBox(8, openAccountButton, logoutButton));
        root.setPadding(new Insets(15));
        return root;
    }

    public void setOnAccountSelected(Consumer<String> handler) {
        this.onAccountSelected = handler;
    }

    public void setOnDeposit(Consumer<DepositRequest> handler) {
        depositButton.setOnAction(e -> handler.accept(new DepositRequest(
                accountList.getSelectionModel().getSelectedItem(),
                amountField.getText(), noteField.getText())));
    }

    public void setOnWithdraw(Consumer<WithdrawRequest> handler) {
        withdrawButton.setOnAction(e -> handler.accept(new WithdrawRequest(
                accountList.getSelectionModel().getSelectedItem(),
                amountField.getText())));
    }

    public void setOnPayInterest(Runnable handler) {
        interestButton.setOnAction(e -> handler.run());
    }

    public void setOnOpenAccountRequested(Runnable handler) {
        openAccountButton.setOnAction(e -> handler.run());
    }

    public void setOnLogout(Runnable handler) {
        logoutButton.setOnAction(e -> handler.run());
    }

    // ---- display methods called by the app/controller ----
    public void setWelcome(String text)      { welcomeLabel.setText(text); }
    public void setAccounts(List<String> numbers) { accountList.getItems().setAll(numbers); }
    public void showAccountDetails(String text)   { detailsLabel.setText(text); }
    public void showHistory(String text)          { historyArea.setText(text); }

    // ---- message helpers (neutral for info, red for errors) ----
    public void showMessage(String message) {
        messageLabel.setStyle("-fx-text-fill: #222;");
        messageLabel.setText(message);
    }

    public void showError(String message) {
        messageLabel.setStyle("-fx-text-fill: red;");
        messageLabel.setText(message);
    }

    public void clearMessage() {
        messageLabel.setText("");
    }
}