package edu.ucr.rp.programacion2.proyecto.gui.modules.inventory;

import edu.ucr.rp.programacion2.proyecto.domain.Inventory;
import edu.ucr.rp.programacion2.proyecto.gui.manage.ManagePane;
import edu.ucr.rp.programacion2.proyecto.gui.manage.model.PaneViewer;
import edu.ucr.rp.programacion2.proyecto.gui.modules.util.PaneUtil;
import edu.ucr.rp.programacion2.proyecto.logic.InventoryService;
import edu.ucr.rp.programacion2.proyecto.logic.InventorySocketService;
import edu.ucr.rp.programacion2.proyecto.logic.ServiceException;
import edu.ucr.rp.programacion2.proyecto.util.InventoryConverter;
import edu.ucr.rp.programacion2.proyecto.util.builders.BuilderFX;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import org.controlsfx.control.CheckComboBox;

import static edu.ucr.rp.programacion2.proyecto.gui.modules.util.LabelConstants.TITLE_DELETE_INVENTORY;

/**
 * This class shows the actions to remove 1 or more inventories.
 *
 * @author Luis Carlos Aguilar Morales | B90127
 * @version 2.0
 */
public class DeleteInventory implements PaneViewer {
    private static InventoryService inventoryService;
    private static Button deleteInventoryButton;
    private Button cancelButton;
    private static Label inventoryIndicationLabel;
    private static CheckComboBox<Inventory> checkComboBox;
    private static ObservableList<Inventory> inventoryObservableList;
    private static GridPane pane;

    /**
     * Return the pane with all the components and styles added.
     *
     * @return {@code GridPane} pane with components.
     */
    public GridPane getDeleteInventoryPane() {
        pane = PaneUtil.buildPane();
        initializeInventoryService();
        addControls();
        addHandlers();
        setStyle();
        return pane;
    }

    /**
     * This method initializes the inventory service.
     */
    private static void initializeInventoryService() {
        inventoryService = InventorySocketService.getInstance();
    }

    /**
     * This methods restarts the GridPane to make it reusable and validates whether it is possible to remove an inventory.
     */
    public static void refresh() {
        initializeInventoryService();
        try {
            if (inventoryService.getAll().isEmpty()) {
                ManagePane.clearPane();
                PaneUtil.showAlert(Alert.AlertType.INFORMATION, "There are no inventories", "You must add at least one inventory to be able to access this function");
            }
            refreshItems();
        }catch (ServiceException e){
            System.out.println(e.getMessage());
        }
    }

    private static void refreshItems() {
        initializeInventoryService();
        checkComboBox.getCheckModel().clearChecks();
        inventoryObservableList.clear();
        try {
            inventoryObservableList.addAll(inventoryService.getAll());
        } catch (ServiceException exception) {
            System.out.println(exception.getMessage());
        }
    }

    /**
     * Configure and add the required components in the pane.
     */
    private void addControls() {
        BuilderFX.buildLabelTitleNormal(TITLE_DELETE_INVENTORY, pane, 0, 0);
        inventoryIndicationLabel = PaneUtil.buildLabel(pane, "Select the inventory", 0, 1);
        buildCheckComboBoxComboBox();
        deleteInventoryButton = PaneUtil.buildButtonImage(new Image("delete.png"), pane, 2, 1, false);
        cancelButton = PaneUtil.buildButton("Cancel", pane, 3, 1, false);
    }

    /**
     * Add functionality to buttons or events.
     */
    private void addHandlers() {
        deleteInventoryButton.setOnAction((actionEvent) -> {
            if (!checkComboBox.getCheckModel().isEmpty()) {
                deleteInventory();
            } else {
                PaneUtil.showAlert(Alert.AlertType.INFORMATION, "Error, did not select an inventory", "You must select one inventory to apply this action");
            }
            refreshItems();
        });
        cancelButton.setOnAction((actionEvent) -> {
            ManagePane.clearPane();
            refresh();
        });
    }

    /**
     * This method builds a CheckComboBox that displays the inventory list.
     */
    private void buildCheckComboBoxComboBox() {
        inventoryObservableList = FXCollections.observableArrayList();
        checkComboBox = PaneUtil.buildCheckComboBox(pane, inventoryObservableList, 1, 1);
    }

    /**
     * This method does the job of removing 1 or many user-selected inventories.
     */
    private void deleteInventory() {
        int i = 0;
        Boolean removed = false;
        ObservableList<Inventory> list = checkComboBox.getCheckModel().getCheckedItems();
        try {
            for (Inventory inventory : list) {
                inventoryService.remove(inventory);
                i++;
            }
            if (i == list.size()) {
                removed = true;
            }
            if (removed) {
                PaneUtil.showAlert(Alert.AlertType.INFORMATION, "Inventory removed", "The inventory was removed correctly");
                ManagePane.clearPane();
            } else {
                PaneUtil.showAlert(Alert.AlertType.INFORMATION, "ERROR when removing", "The inventory was not removed");
            }
        }catch (ServiceException e){
            System.out.println(e.getMessage());
        }
    }

    /**
     * This method receives the inventory and checks it in the CheckComboBox from the InventoryView remove button
     * @param inventory selected from InventoryView remove button
     */
    public static void setInventory(Inventory inventory){
        checkComboBox.getCheckModel().check(inventory);
    }

    private void setStyle(){
        checkComboBox.setConverter(new InventoryConverter());
    }

    @Override
    public Pane getPane() {
        return getDeleteInventoryPane();
    }
}
