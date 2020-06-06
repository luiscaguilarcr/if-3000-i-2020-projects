package edu.ucr.rp.programacion2.proyecto.gui.javafx.catalog;

import edu.ucr.rp.programacion2.proyecto.domain.Inventory;
import edu.ucr.rp.programacion2.proyecto.gui.javafx.util.PaneUtil;
import edu.ucr.rp.programacion2.proyecto.gui.model.PaneViewer;
import edu.ucr.rp.programacion2.proyecto.gui.panes.main.ManagePane;
import edu.ucr.rp.programacion2.proyecto.logic.CatalogService;
import edu.ucr.rp.programacion2.proyecto.logic.InventoryService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import org.controlsfx.control.CheckComboBox;

public class DeleteCatalog implements PaneViewer {
    private InventoryService inventoryService;
    private CatalogService catalogService;
    private Label inventoryIndicationLabel;
    private Label catalogIndicationLabel;
    private Button confirmInventoryButton;
    private Button deleteCatalogButton;
    private Button refreshButton;
    private Button cancelButton;
    private ComboBox<String> inventoryComboBox;
    private CheckComboBox catalogCheckComboBox;
    private ObservableList inventoryObservableList;
    private ObservableList catalogObservableList;
    GridPane pane;

    public GridPane getDeleteCatalogPane() {
        pane = PaneUtil.buildPane();
        initializeInventoryService();
        addControls();
        addHandlers();
        return pane;
    }

    private void initializeInventoryService() {
        inventoryService = InventoryService.getInstance();
    }

    private void addControls() {
        buildInventoryComboBox();
        buildCatalogCheckComboBox();
        inventoryIndicationLabel = PaneUtil.buildLabel(pane, "Chose an inventory", 0, 0);
        catalogIndicationLabel = PaneUtil.buildLabel(pane, "Select the catalog you want to remove", 0, 1);
        //confirmInventoryButton = PaneUtil.buildButtonImage(new Image("select.png"), pane, 2, 0);
        //cancelButton = PaneUtil.buildButton("Cancel", pane, 4, 1);
        deleteCatalogButton = PaneUtil.buildButtonImage(new Image("delete.png"), pane, 2, 1);
    }

    public void validateShow() {
        initializeInventoryService();
        if (inventoryService.getAll().size() == 0) {
            ManagePane.clearPane();
            PaneUtil.showAlert(Alert.AlertType.INFORMATION, "There are no inventories", "You must add at least one inventory to be able to access this function");
        }
    }

    private void initializeCatalogService(Inventory inventory) {
        catalogService = new CatalogService(inventory);
    }

    private void addHandlers() {
        confirmInventoryButton.setOnAction((event -> {
            if (PaneUtil.addInventoryHandlers(inventoryComboBox)) {
                initializeCatalogService(inventoryService.get(inventoryComboBox.getValue()));
            }
        }));
        deleteCatalogButton.setOnAction((actionEvent) -> {
            ManagePane.clearPane();
            deleteCatalog();
            deleteCatalogButton.setVisible(false);
        });
        cancelButton.setOnAction((actionEvent) -> {
            ManagePane.clearPane();
            deleteCatalogButton.setVisible(false);
        });
    }


    private void buildInventoryComboBox() {
        inventoryObservableList = FXCollections.observableArrayList(inventoryService.getNamesList());
        inventoryComboBox = PaneUtil.buildComboBox(pane, inventoryObservableList, 1, 0);
    }

    private void buildCatalogCheckComboBox() {
        catalogObservableList = FXCollections.observableArrayList(catalogService.getNamesList());
        catalogCheckComboBox = PaneUtil.buildCheckComboBox(pane, catalogObservableList, 1, 1);
    }

    private void deleteCatalog() {
        Boolean removed = true;
        ObservableList<String> list = catalogCheckComboBox.getCheckModel().getCheckedItems();
        for (String s : list) {
            if(!catalogService.remove(catalogService.get(s))){
                removed = false;
            }
        }
        if (removed) {
            PaneUtil.showAlert(Alert.AlertType.INFORMATION, "Catalog removed", "The selected catalog was removed correctly");
        } else {
            PaneUtil.showAlert(Alert.AlertType.INFORMATION, "ERROR when removing", "The selected catalog was not removed");
        }
    }

    @Override
    public Pane getPane() {
        return getDeleteCatalogPane();
    }
}
