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
    @FXML
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
