package com.robuschi;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.Optional;

/**
 * Controller for the main application view.
 * Handles product management operations.
 */
public class MainController {

    @FXML
    private ListView<Product> availableProductsList;

    @FXML
    private ListView<Product> purchasedProductsList;

    @FXML
    private Label userLabel;

    private ProductClient application;
    private ObservableList<Product> availableProducts;
    private ObservableList<Product> purchasedProducts;

    /**
     * Sets the main application reference.
     *
     * @param application the main application
     */
    public void setApplication(ProductClient application) {
        this.application = application;
        requestProductList();
    }

    public void setUserLabel(String usrLbl) {
        this.userLabel.setText(usrLbl);
    }

    /**
     * Initializes the controller.
     * Called automatically by JavaFX.
     */
    @FXML
    private void initialize() {
        availableProducts = FXCollections.observableArrayList();
        purchasedProducts = FXCollections.observableArrayList();

        availableProductsList.setItems(availableProducts);
        purchasedProductsList.setItems(purchasedProducts);
    }

    /**
     * Handles refresh button action.
     */
    @FXML
    private void handleRefresh() {
        requestProductList();
    }

    /**
     * Handles purchase button action.
     */
    @FXML
    private void handlePurchase() {
        Product selectedProduct = availableProductsList.getSelectionModel().getSelectedItem();

        if (selectedProduct == null) {
            showError("Please select a product to purchase");
            return;
        }

        Protocol.Message message = new Protocol.Message(
                Protocol.MessageType.PURCHASE_PRODUCT,
                selectedProduct
        );
        application.getNetworkManager().sendMessage(message);
    }

    /**
     * Handles return button action.
     */
    @FXML
    private void handleReturn() {
        Product selectedProduct = purchasedProductsList.getSelectionModel().getSelectedItem();

        if (selectedProduct == null) {
            showError("Please select a product to return");
            return;
        }

        Protocol.Message message = new Protocol.Message(
                Protocol.MessageType.RETURN_PRODUCT,
                selectedProduct
        );
        application.getNetworkManager().sendMessage(message);
        purchasedProducts.remove(selectedProduct);
    }

    /**
     * Handles add product button action.
     */
    /*@FXML
    private void handleAddProduct() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add New Product");
        dialog.setHeaderText("Add a new product type");
        dialog.setContentText("Product name:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> {
            if (!name.trim().isEmpty()) {
                Protocol.Message message = new Protocol.Message(
                        Protocol.MessageType.ADD_NEW_PRODUCT,
                        name.trim()
                );
                application.getNetworkManager().sendMessage(message);
                showInfo("Product added successfully");
                requestProductList();
            }
        });
    }*/
    /**
     * Handles add product button action.
     */
    @FXML
    private void handleAddProduct() {
        // Create custom dialog
        Dialog<Product> dialog = new Dialog<>();
        dialog.setTitle("Add New Product");
        dialog.setHeaderText("Enter product details");

        // Set button types
        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        // Create the form
        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        TextField nameField = new TextField();
        nameField.setPromptText("Product name");
        TextField priceField = new TextField();
        priceField.setPromptText("Product price");

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Price:"), 0, 1);
        grid.add(priceField, 1, 1);

        dialog.getDialogPane().setContent(grid);

        // Request focus on name field
        javafx.application.Platform.runLater(() -> nameField.requestFocus());

        // Convert result when Add button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                try {
                    String name = nameField.getText().trim();
                    double price = Double.parseDouble(priceField.getText().trim());

                    if (name.isEmpty()) {
                        showError("Product name cannot be empty");
                        return null;
                    }

                    if (price <= 0) {
                        showError("Price must be greater than 0");
                        return null;
                    }

                    // Create temporary product (ID will be assigned by server)
                    return new Product(name, price, 0);
                } catch (NumberFormatException e) {
                    showError("Invalid price format. Please enter a valid number.");
                    return null;
                }
            }
            return null;
        });

        Optional<Product> result = dialog.showAndWait();
        result.ifPresent(product -> {
            // Send product data to server
            Protocol.Message message = new Protocol.Message(
                    Protocol.MessageType.ADD_NEW_PRODUCT,
                    product
            );
            application.getNetworkManager().sendMessage(message);
            showInfo("Product added successfully: " + product.getName() + " - €" +
                    String.format("%.2f", product.getPrice()));
            requestProductList();
        });
    }

    /**
     * Handles logout button action.
     */
    @FXML
    private void handleLogout() {
        application.logout();
    }

    /**
     * Requests the product list from the server.
     */
    private void requestProductList() {
        Protocol.Message message = new Protocol.Message(Protocol.MessageType.GET_PRODUCTS);
        application.getNetworkManager().sendMessage(message);
    }

    /**
     * Updates the available products list.
     *
     * @param products the new product list
     */
    public void updateAvailableProducts(java.util.List<Product> products) {
        availableProducts.clear();
        availableProducts.addAll(products);
    }

    /**
     * Adds a product to the purchased list.
     *
     * @param product the purchased product
     */
    public void addPurchasedProduct(Product product) {
        purchasedProducts.add(product);
    }

    /**
     * Displays an error alert.
     *
     * @param message the error message
     */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Displays an information alert.
     *
     * @param message the information message
     */
    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
