package edu.ucr.rp.programacion2.proyecto.gui.manage;

import edu.ucr.rp.programacion2.proyecto.gui.modules.others.ViewMenuBar;
import edu.ucr.rp.programacion2.proyecto.gui.modules.catalog.CatalogForm;
import edu.ucr.rp.programacion2.proyecto.gui.modules.catalog.DeleteCatalog;
import edu.ucr.rp.programacion2.proyecto.gui.modules.inventory.DeleteInventory;
import edu.ucr.rp.programacion2.proyecto.gui.modules.inventory.InventoryForm;
import edu.ucr.rp.programacion2.proyecto.gui.modules.item.CreateItemForm;
import edu.ucr.rp.programacion2.proyecto.gui.modules.item.ManageItem;
import edu.ucr.rp.programacion2.proyecto.gui.modules.others.AboutUI;
import edu.ucr.rp.programacion2.proyecto.gui.modules.others.CreditsUI;
import edu.ucr.rp.programacion2.proyecto.gui.manage.model.PaneName;
import edu.ucr.rp.programacion2.proyecto.gui.manage.model.PaneViewer;
import edu.ucr.rp.programacion2.proyecto.gui.manage.model.SceneName;
import edu.ucr.rp.programacion2.proyecto.gui.modules.catalog.CatalogConfig;
import edu.ucr.rp.programacion2.proyecto.gui.modules.inventory.InventoryView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

public class ManagePane implements PaneViewer {

    // Contains all the Panes.
    private static Map<PaneName, Pane> panes = new HashMap<>();
    private Stage stage;
    private static Pane basePane;
    private static HBox hbox;

    public ManagePane(Stage stage) {
        this.stage = stage;
        this.basePane = buildBasePane();
        initializePanes(stage, basePane);
        setupBasePane(basePane);
    }

    /**
     * Create and store all the scenes.
     * Set up the main pane.
     */
    private void initializePanes(Stage stage, Pane basePane) {
        panes.put(PaneName.BASE, basePane);
        panes.put(PaneName.MENU_BAR, new ViewMenuBar(stage).getPane());
        panes.put(PaneName.ADD_INVENTORY, new InventoryForm().getPane());
        panes.put(PaneName.DELETE_INVENTORY, new DeleteInventory().getPane());
        panes.put(PaneName.ADD_CATALOG, new CatalogForm().getPane());
        panes.put(PaneName.DELETE_CATALOG, new DeleteCatalog().getPane());
        panes.put(PaneName.MANAGE_ITEM, new ManageItem().getPane());
        panes.put(PaneName.SHOW_INVENTORY, new InventoryView().getPane());
        panes.put(PaneName.CATALOG_CONFIG, new CatalogConfig().getPane());
        panes.put(PaneName.CREDITS_UI, CreditsUI.getInstance().getPane());
        panes.put(PaneName.ABOUT_UI, AboutUI.getInstance().getPane());
        panes.put(PaneName.CREATE_ITEM, CreateItemForm.getInstance().getPane());
    }

    @Override
    public Pane getPane() {
        return panes.get(PaneName.BASE);
    }

    /**
     * Just inizialize the pane without children.
     * Build the base pane.
     *
     * @return the base pane.
     */
    private Pane buildBasePane() {
        GridPane gridPane = new GridPane();
        return gridPane;
    }

    /**
     * Configure all the panes for the basePane.
     *
     * @param pane
     */
    private void setupBasePane(Pane pane) {
        ((GridPane) pane).add(panes.get(PaneName.MENU_BAR), 0, 0);
        hbox = new HBox();
        ((GridPane) pane).add(hbox, 0, 1);
    }

    /**
     * Change the current pane
     *
     * @param pane
     */
    public static void setCenterPane(Pane pane) {
        hbox.getChildren().clear();
        hbox.getChildren().add(pane);
    }

    /**
     * Change the current pane
     *
     */
    public static void clearPane() {
        hbox.getChildren().clear();
    }

    /**
     * Returns a Map of the scenes by {@link SceneName}
     *
     * @return
     */
    public static Map<PaneName, Pane> getPanes() {
        return panes;
    }
}
