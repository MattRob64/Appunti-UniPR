package com.robuschi;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;

import java.util.List;
import java.util.Optional;

/**
 * <p>The {@code MainController} class serves as a controller for the main application view.
 * <p>Handles product management operations and the interaction with the GUI.
**/
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
     * <p>Sets the main application reference.
     * <p>It is called in the {@code ProductClient} class when the main view is shown through the {@code mainController.setApplication(this)} method.
     * <p>It calls the {@code requestProductList()} method to show the products in their respective lists.
     * @param application the main application
    **/
    public void setApplication(ProductClient application) {
        this.application = application;
        requestProductList();
    }

    /**
     * <p>This method sets the label in the top of the main view, making it so that it displays the username.
     * <p>It is called in the {@code ProductClient} class immediately after the user successfully logs in.
     * @param usrLbl is the string that contains the username of the logged user
    **/
    public void setUserLabel(String usrLbl) {
        this.userLabel.setText(usrLbl);
    }

    /**
     * <p>Initializes the controller.
     * <p>Called automatically by JavaFX.
    **/
    @FXML
    private void initialize() {
        availableProducts = FXCollections.observableArrayList();
        purchasedProducts = FXCollections.observableArrayList();

        availableProductsList.setItems(availableProducts);
        purchasedProductsList.setItems(purchasedProducts);
    }

    /**
     * <p>This method handles the purchase button action.
     * <p>If a product in the product list is selected it sends a message of type {@code PURCHASE_PRODUCT} and the selected product as payload to the server.
    **/
    @FXML
    private void handlePurchase() {
        Product selectedProduct = availableProductsList.getSelectionModel().getSelectedItem();

        if (selectedProduct == null) {
            Protocol.InfoDialog.showError("Please select a product to purchase");
            return;
        }

        Protocol.Message message = new Protocol.Message(
                Protocol.MessageType.PURCHASE_PRODUCT,
                selectedProduct
        );
        application.getNetworkManager().sendMessage(message);
    }

    /**
     * <p>This method handles return button action.
     * <p>If a product in the user's product list is selected it sends a message of type {@code PRODUCT_RETURN} and the selected product as payload to the server.
    **/
    @FXML
    private void handleReturn() {
        Product selectedProduct = purchasedProductsList.getSelectionModel().getSelectedItem();

        if (selectedProduct == null) {
            Protocol.InfoDialog.showError("Please select a product to return");
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
     * <p>This method handles add product button action.
     * <p>It creates a dialog window with the CSS style of the other dialogs, then checks the validity of the data inserted by the user
     * [for example if one of the two text fields (the product name or the product price) is empty].
     * <p>If the data inserted is correct it sends a message of type {@code ADD_NEW_PRODUCT} and the inserted product as payload to the server.
     * <p>Lastly it calls the {@code requestProductList()} method to update the products in their respective lists.
    **/
    @FXML
    private void handleAddProduct() {
        // Create custom dialog
        Dialog<Product> dialog = new Dialog<>();
        dialog.getDialogPane().getStylesheets().add(getClass().getResource("alertStyle.css").toExternalForm());
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

        // Get the Add button
        Node addButton = dialog.getDialogPane().lookupButton(addButtonType);

        // Add event filter to prevent closing on validation errors
        addButton.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            String name = nameField.getText().trim();
            String priceText = priceField.getText().trim();

            // Validate name
            if (name.isEmpty()) {
                Protocol.InfoDialog.showError("Product name cannot be empty");
                event.consume(); // Prevent dialog from closing
                return;
            }

            // Validate price format
            double price;
            try {
                price = Double.parseDouble(priceText);
            } catch (NumberFormatException e) {
                Protocol.InfoDialog.showError("Invalid price format. Please enter a valid number.");
                event.consume(); // Prevent dialog from closing
                return;
            }

            // Validate price value
            if (price <= 0) {
                Protocol.InfoDialog.showError("Price must be greater than 0");
                event.consume(); // Prevent dialog from closing
                return;
            }

            // If we reach here, validation passed - dialog will close normally
        });

        // Request focus on name field
        javafx.application.Platform.runLater(() -> nameField.requestFocus());

        // Convert result when Add button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                String name = nameField.getText().trim();
                double price = Double.parseDouble(priceField.getText().trim());
                return new Product(name, price, 0);
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
            Protocol.InfoDialog.showInfo("Product added successfully: " + product.getName() + " - " + String.format("%.2f", product.getPrice()) + "€");
            System.out.println("You added the product: " + product.getName() + " selling at: " + String.format("%.2f", product.getPrice()) + "€");
            requestProductList();
        });
    }

    /**
     * <p>Handles logout button action by calling the {@code application.logout()} method.
     * <p>The application in this case is an instance of the {@code ProductClient} class.
    **/
    @FXML
    private void handleLogout() {
        application.logout();
    }

    /**
     * <p>Requests the product list from the server by sending a message of type {@code GET_PRODUCTS }.
    **/
    private void requestProductList() {
        Protocol.Message message = new Protocol.Message(Protocol.MessageType.GET_PRODUCTS);
        application.getNetworkManager().sendMessage(message);
    }

    /**
     * <p>Updates the available products list by clearing all the previous items that were shown and then replacing them with the updated ones.
     * @param products the new product list
    **/
    public void updateAvailableProducts(List<Product> products) {
        availableProducts.clear();
        availableProducts.addAll(products);
    }

    /**
     * <p>Adds a product to the purchased list.
     * @param product the purchased product
    **/
    public void addPurchasedProduct(Product product) {
        purchasedProducts.add(product);
    }
}
