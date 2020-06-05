
package edu.ucr.rp.programacion2.proyecto.gui.panes.main.records;

import edu.ucr.rp.programacion2.proyecto.domain.Inventory;
import edu.ucr.rp.programacion2.proyecto.domain.InventoryControl;
import edu.ucr.rp.programacion2.proyecto.gui.model.PaneViewer;
import edu.ucr.rp.programacion2.proyecto.gui.panes.main.ManagePane;
import edu.ucr.rp.programacion2.proyecto.logic.CatalogService;
import edu.ucr.rp.programacion2.proyecto.logic.InventoryControlService;
import edu.ucr.rp.programacion2.proyecto.logic.InventoryService;
import edu.ucr.rp.programacion2.proyecto.logic.Service;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.util.Callback;

import java.util.List;

import static edu.ucr.rp.programacion2.proyecto.gui.javafx.LabelConstants.*;
import static edu.ucr.rp.programacion2.proyecto.gui.javafx.util.UIConstants.*;

/**
 * This shows the list of catalogs that belongs to an Inventory.
 *
 * @author Jeison Araya Mena | B90514
 * @version 2.0
 */
public class ShowInventory implements PaneViewer {
    //  Variables  \\
    private TitledPane createTiledPane;
    private HBox createOptionsHBox;
    private Button createInventoryButton;
    private Button createCatalogButton;
    private TextField filterField;
    private TableView tableView;
    private TableColumn inventoryNameColumn;
    private TableColumn catalogNameColumn;
    private TableColumn itemsActionColumn;
    private TableColumn configActionColumn;
    private Pagination pagination;
    private Label resultsLabel;
    private GridPane pane;
    private InventoryService inventoryService;
    private Service catalogService;
    private InventoryControlService inventoryControlService;
    private CatalogConfig catalogConfig = new CatalogConfig();
    //  Methods  \\

    /**
     * This method initialize the services required.
     */
    private void initializeServices() {
        inventoryService = InventoryService.getInstance();
        inventoryControlService = InventoryControlService.getInstance();
    }

    private void updateCatalogService(Inventory inventory) {
        catalogService = new CatalogService(inventory);
    }

    /**
     * Return the pane with all the components and styles added.
     *
     * @return {@code GridPane} pane with components.
     */
    public GridPane createPane() {
        initializeServices();
        pane = BuilderFX.buildRecordsPane();
        setupControls(pane);
        setupTableView(tableView);
        addHandlers();
        setupStyles();
        updateResultsLabel();
        return pane;
    }

    /**
     * Configure and add the required components in the pane.
     *
     * @param pane for add components.
     */
    private void setupControls(GridPane pane) {
        // Create
        BuilderFX.buildLabelTitle(TITLE_VIEW_INVENTORY, pane, 0, 0, 2, 1);
        createInventoryButton = new Button(TITLE_INVENTORY);
        createCatalogButton = new Button(TITLE_CATALOG);
        createOptionsHBox = new HBox(createInventoryButton, createCatalogButton);
        createTiledPane = BuilderFX.buildTitledPane(CREATE_LABEL, createOptionsHBox, pane, 0, 1, 2, 1);
        // Show
        filterField = BuilderFX.buildTextInput(SEARCH_LABEL, pane, 3, 1);
        tableView = BuilderFX.buildTableView(pane, 0, 2, 4, 1);
        resultsLabel = BuilderFX.buildLabelMinimal("", pane, 0, 3, 2);
        pagination = BuilderFX.buildPagination(pane, 2, 3, 2, 1);
    }

    /**
     * Set the styles of the components.
     */
    private void setupStyles() { //TODO how to simplify. -> Agregar config en el builder de cada uno.
        // Pane
        pane.getStyleClass().add("show-inventory-pane");
        // Row Constraints
        // Row #0
        RowConstraints rowConstraints = new RowConstraints(25, 25, 30);
        rowConstraints.setValignment(VPos.TOP);
        rowConstraints.setVgrow(Priority.SOMETIMES);
        // Row #1
        RowConstraints rowConstraints1 = new RowConstraints(50, 50, 150);
        rowConstraints1.setValignment(VPos.TOP);
        rowConstraints.setVgrow(Priority.ALWAYS);
        // Row #2
        RowConstraints rowConstraints2 = new RowConstraints(500, 600, 600);
        rowConstraints2.setValignment(VPos.TOP);
        rowConstraints.setVgrow(Priority.NEVER);
        // Row #3
        RowConstraints rowConstraints3 = new RowConstraints(25, 25, 40);
        rowConstraints3.setValignment(VPos.TOP);
        rowConstraints.setVgrow(Priority.NEVER);
        // Add Row Constraints
        pane.getRowConstraints().addAll(rowConstraints, rowConstraints1, rowConstraints2, rowConstraints3);

        // Columns Constraints
        ColumnConstraints columnConstraints = new ColumnConstraints(300, 300, 300);
        columnConstraints.setHalignment(HPos.LEFT);
        columnConstraints.setHgrow(Priority.ALWAYS);

        ColumnConstraints columnConstraints2 = new ColumnConstraints(150, 175, 200);
        columnConstraints.setHalignment(HPos.LEFT);
        columnConstraints.setHgrow(Priority.ALWAYS);

        ColumnConstraints columnConstraints3 = new ColumnConstraints(200, 200, 200);
        columnConstraints3.setHalignment(HPos.RIGHT);
        columnConstraints3.setHgrow(Priority.NEVER);

        ColumnConstraints columnConstraints4 = new ColumnConstraints(75, 75, 150);
        columnConstraints4.setHalignment(HPos.RIGHT);
        columnConstraints4.setHgrow(Priority.ALWAYS);
        pane.getColumnConstraints().addAll(columnConstraints, columnConstraints2, columnConstraints3, columnConstraints4);

        // Settings for Table Columns
        // Inventory Column
        inventoryNameColumn.setMinWidth(100);
        // Catalog Column
        catalogNameColumn.setMinWidth(100);
        // Items Column
        itemsActionColumn.getStyleClass().add("table-view-column-buttons");
        itemsActionColumn.setMaxWidth(70);
        itemsActionColumn.setMinWidth(70);
        // Config Column
        configActionColumn.getStyleClass().add("table-view-column-buttons");
        configActionColumn.setMaxWidth(70);
        configActionColumn.setMinWidth(70);
        // Settings for Table View
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableView.setEditable(false);
        tableView.setMinSize(TABLE_VIEW_DEFAULT_MIN_WIDTH, TABLE_VIEW_DEFAULT_MIN_HEIGHT);
        tableView.setMaxSize(TABLE_VIEW_DEFAULT_MAX_WIDTH, TABLE_VIEW_DEFAULT_MAX_HEIGHT);

        // Label
        resultsLabel.getStyleClass().add("results-label");
    }

    /**
     * Add the settings to a table view.
     * It's in charge of:
     * 1. Add required columns.
     * 2. Fill the table.
     * 3. Add buttons with functionalities (some are just for administrators).
     * 4. Allows the table to be editable (for administrators only).
     *
     * @param tableView table configured and full.
     */
    private void setupTableView(TableView tableView) {
        addColumns(tableView);
        fillTable(tableView);
    }

    /**
     * Create a column and place it in a display table.
     * *
     *
     * @param text      Text of the column.
     * @param tableView Shows the table where the column will be added.
     * @return Column configured and placed.
     * @Param Property Property that identifies the column, with an attribute of the object.
     */
    public TableColumn buildTableColumn(String text, String property, TableView tableView) {
        TableColumn tableColumn = new TableColumn(text);
        tableColumn.setId(property);
        tableColumn.setCellValueFactory(new PropertyValueFactory(property));
        tableView.getColumns().add(tableColumn);
        return tableColumn;
    }


    /**
     * Add necessary columns to display object information.
     *
     * @param tableView table where the columns will be added.
     */
    private void addColumns(TableView<InventoryControl> tableView) {
        tableView.getColumns().clear();
        inventoryNameColumn = buildTableColumn(TITLE_INVENTORY, "inventoryName", tableView);
        catalogNameColumn = buildTableColumn(TITLE_CATALOG, "catalogName", tableView);
        itemsActionColumn = buildButtonColumn(ITEMS_COLUMN, ITEMS_ICON, tableView);
        configActionColumn = buildButtonColumn(CONFIG_NAME_COLUMN, CONFIG_ICON, tableView);
    }

    /**
     * Fills the table with an list of objects.
     *
     * @param tableView table view to add items.
     */
    private void fillTable(TableView<InventoryControl> tableView) {
        try {
            ObservableList<InventoryControl> listFiltered = getFilteredList();
            tableView.setItems(listFiltered);

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage() + ", in " + e.getCause());
        }
    }


    /**
     * @return
     */
    private FilteredList<InventoryControl> getFilteredList() {
        //  Getting list
        ObservableList<InventoryControl> items = getObservableList(getList());
        FilteredList<InventoryControl> filteredList = new FilteredList<>(items);
        // Adding filters
        // Case #1 -> Show all
        filteredList.setPredicate(b -> true);

        // Case #2 -> Show content searched in filterField.
        filterField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredList.setPredicate(inventoryControl -> {
                // SubCase #1 filter is empty or null
                if (newValue == null || newValue.isEmpty()) return true;

                // Get the input.
                String inputFilter = newValue.toLowerCase();
                // SubCase #2 filter the name of the inventories.
                if (inventoryControl.getInventoryName().toLowerCase().contains(inputFilter)) {
                    return true;
                }
                // SubCase #3 filter the name of the catalogs.
                return inventoryControl.getCatalogName().toLowerCase().contains(inputFilter);

            });
            // Update results message.
            updateResultsLabel();
        });

        return filteredList;
    }

    /**
     * Converts the list of inventory Control into a ObservableList.
     *
     * @return {@code ObservableList} observable list with existing objects.
     */
    private ObservableList<InventoryControl> getObservableList(List<InventoryControl> inventoryControls) {
        return FXCollections.observableArrayList(inventoryControls);
    }

    /**
     * Get the list of all the inventories and catalogs from InventoryControl Service.
     *
     * @return {@code List} list with register in inventory service.
     */
    private List<InventoryControl> getList() {
        return inventoryControlService.getAll();
    }

    /**
     * Add buttons to a tableView.
     *
     * @param label Button text
     */
    private TableColumn buildButtonColumn(String label, String image, TableView tableView) {
        TableColumn tableColumn = new TableColumn(label);
        Callback<TableColumn<InventoryControl, Void>, TableCell<InventoryControl, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<InventoryControl, Void> call(final TableColumn<InventoryControl, Void> param) {
                final TableCell<InventoryControl, Void> cell = new TableCell<>() {
                    private final Button btn = new Button("");

                    {// Definir funciones del botón
                        btn.setGraphic(new ImageView(new Image(image)));
                        btn.getStyleClass().add("table-buttons");
                        switch (label) {
                            case ITEMS_COLUMN -> btn.setOnAction(actionEvent -> {
                                InventoryControl data = getTableView().getItems().get(getIndex());
                                viewItemsAction(data);
                            });
                            case CONFIG_NAME_COLUMN -> btn.setOnAction(actionEvent -> {
                                InventoryControl data = getTableView().getItems().get(getIndex());
                                configAction(data);
                            });
                        }
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty)
                            setGraphic(null);
                        else
                            setGraphic(btn);
                    }
                };
                return cell;
            }
        };
        tableColumn.setCellFactory(cellFactory);
        tableView.getColumns().add(tableColumn);
        return tableColumn;
    }


    //  HANDLERS  \\

    /**
     * Add functionality to buttons or events.
     */
    private void addHandlers() {
        createCatalogButton.setOnAction(e -> createCatalogAction());
        createInventoryButton.setOnAction(e -> createInventoryAction());

    }

    private void createInventoryAction() {// TODO actionEvent
        System.out.println("Create Inventory Button pressed");
    }

    private void createCatalogAction() {// TODO actionEvent
       System.out.println("Create Catalog Button pressed");

    }

    // Table Buttons
    private void viewItemsAction(InventoryControl inventoryControl) {//TODO actionEvent
        System.out.println("Going to items table view.. of " + inventoryControl.getCatalogName());
    }

    private void configAction(InventoryControl inventoryControl) {//TODO actionEvent
        ManagePane.setCenterPane(catalogConfig.getPane());
        CatalogConfig.refresh();
        //CatalogConfig.setCatalog(inventoryControl.getCatalogName());
        //CatalogConfig.setInventory(inventoryControl.getInventoryName());
        System.out.println("Going to config table view.. of " + inventoryControl.getCatalogName());
    }

    private void refreshTable() {
        fillTable(tableView);
    }

    /**
     * Updates the label of the matches and number of items showed in the table.
     */
    private void updateResultsLabel() {
        int total = getList().size();                   // Total of inventories and catalogs.
        int current = tableView.getItems().size();      // Number of inventories and catalogs in the table.
        resultsLabel.setText("Showing " + current + " of " + total + " catalogs.");

    }

    @Override
    public Pane getPane() {
        return createPane();
    }
}