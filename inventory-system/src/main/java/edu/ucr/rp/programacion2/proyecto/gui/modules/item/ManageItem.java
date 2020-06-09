
package edu.ucr.rp.programacion2.proyecto.gui.modules.item;

import edu.ucr.rp.programacion2.proyecto.domain.Catalog;
import edu.ucr.rp.programacion2.proyecto.domain.Inventory;
import edu.ucr.rp.programacion2.proyecto.domain.Item;
import edu.ucr.rp.programacion2.proyecto.gui.modules.util.PaneUtil;
import edu.ucr.rp.programacion2.proyecto.gui.manage.model.PaneName;
import edu.ucr.rp.programacion2.proyecto.gui.manage.model.PaneViewer;
import edu.ucr.rp.programacion2.proyecto.gui.manage.ManagePane;
import edu.ucr.rp.programacion2.proyecto.util.builders.BuilderFX;
import edu.ucr.rp.programacion2.proyecto.gui.modules.catalog.CatalogConfig;
import edu.ucr.rp.programacion2.proyecto.logic.CatalogService;
import edu.ucr.rp.programacion2.proyecto.util.inventorycontrol.InventoryControlService;
import edu.ucr.rp.programacion2.proyecto.logic.InventoryService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.util.Callback;
import javafx.util.StringConverter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import static edu.ucr.rp.programacion2.proyecto.gui.modules.util.LabelConstants.*;
import static edu.ucr.rp.programacion2.proyecto.gui.modules.util.UIConstants.*;
import static edu.ucr.rp.programacion2.proyecto.util.builders.BuilderFX.setButtonEffect;

public class ManageItem implements PaneViewer {
    private static TitledPane createTiledPane;
    private static HBox createOptionsHBox;
    private static Button createItemButton;
    private static Button deleteAllItemsButton;
    private static Button backButton;
    private static TextField filterField;
    private static TableColumn iDTableColumn;
    private static ObservableList inventoryObservableList;
    private static ObservableList catalogObservableList;
    private static TableView tableView;
    private static TableColumn deleteItemColumn;
    private static Pagination pagination;
    private static Label resultsLabel;
    private static GridPane pane;
    private static InventoryService inventoryService;
    private static CatalogService catalogService;
    private static InventoryControlService inventoryControlService;
    private static CatalogConfig catalogConfig = new CatalogConfig();
    private static List<TableColumn> tableColumns = new ArrayList<>();
    private static ComboBox<String> inventoryComboBox;
    private static ComboBox<String> catalogComboBox;
    private static Alert deleteAlert;
    private static ButtonType buttonTypeYes;
    private static ButtonType buttonTypeNo;

    /**
     * This method initialize the services required.
     */
    private static void initializeServices() {
        inventoryService = InventoryService.getInstance();
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
        addHandlers();
        setupStyles();
        if(catalogComboBox.getValue() != null){
            updateCatalogService(inventoryService.get(inventoryComboBox.getValue()));
        }
        return pane;
    }

    /**
     * Configure and add the required components in the pane.
     *
     * @param pane for add components.
     */
    private void setupControls(GridPane pane) {
        // Create
        BuilderFX.buildLabelTitle(TITLE_MANAGE_ITEM, pane, 0, 0, 2, 1);
        createItemButton = new Button(TITLE_NEW_ITEM);
        deleteAllItemsButton = new Button(TITLE_DELETE_ALL_ITEMS);
        createOptionsHBox = new HBox(createItemButton, deleteAllItemsButton);
        createTiledPane = BuilderFX.buildTitledPane(CONFIG_LABEL, createOptionsHBox, pane, 2, 1, 1, 1);
        createTiledPane.setMaxWidth(20);
        createTiledPane.setAlignment(Pos.TOP_LEFT);
        createTiledPane.setVisible(false);
        inventoryObservableList = FXCollections.observableArrayList(inventoryService.getNamesList());
        inventoryComboBox = PaneUtil.buildComboBox(pane, inventoryObservableList, 0, 1);
        inventoryComboBox.setTooltip(new Tooltip("Select an inventory"));
        catalogObservableList = FXCollections.observableArrayList();
        catalogComboBox = PaneUtil.buildComboBox(pane, catalogObservableList, 1, 1);
        catalogComboBox.setTooltip(new Tooltip("Select a catalog"));
        catalogComboBox.setVisible(false);
        // Show
        filterField = BuilderFX.buildTextInput2(SEARCH_LABEL, pane, 3, 1);
        tableView = BuilderFX.buildTableView(pane, 0, 2, 4, 1);
        resultsLabel = BuilderFX.buildLabelMinimal("", pane, 0, 3, 2);
        //pagination = BuilderFX.buildPagination(pane, 2, 3, 2, 1);// TODO
        backButton = BuilderFX.buildButton(BACK_LABEL, pane, 2, 3);
        // None Row
        buttonTypeYes = new ButtonType(YES_LABEL);
        buttonTypeNo = new ButtonType(NO_LABEL);
        deleteAlert = BuilderFX.buildConfirmDialog(DELETE_LABEL, DELETE_ICON, CONFIG_ICON, buttonTypeYes, buttonTypeNo);

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

        // Settings for Table View
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableView.setEditable(true);
        tableView.setVisible(false);
        tableView.setMinSize(TABLE_VIEW_DEFAULT_MIN_WIDTH, TABLE_VIEW_DEFAULT_MIN_HEIGHT);
        tableView.setMaxSize(TABLE_VIEW_DEFAULT_MAX_WIDTH, TABLE_VIEW_DEFAULT_MAX_HEIGHT);

        // Label
        resultsLabel.getStyleClass().add("results-label");
        createOptionsHBox.setSpacing(20);
        //Button
        setButtonEffect(createItemButton);
        setButtonEffect(deleteAllItemsButton);
    }

    /**
     * Create a column and place it in a display table.
     *
     * @param text      Text of the column.
     * @param tableView Shows the table where the column will be added.
     * @return Column configured and placed.
     * @Param Property Property that identifies the column, with an attribute of the object.
     */
    public TableColumn buildTableColumn(String text, String property, TableView tableView) {
        TableColumn<Map, String> tableColumn = new TableColumn(text);
        tableColumn.setId(property);
        tableColumn.setCellValueFactory(new MapValueFactory<>(property));
        Callback<TableColumn<Map, String>, TableCell<Map, String>> callback = mapStringTableColumn -> new TextFieldTableCell<>(new StringConverter<String>() {
            @Override
            public String toString(String s) {
                return s;
            }

            @Override
            public String fromString(String s) {
                return s;
            }
        });
        tableColumn.setCellFactory(callback);
        setEditableColumn(tableColumn);
        tableView.getColumns().add(tableColumn);
        return tableColumn;
    }

    /**
     * @param tableColumn
     */
    private void setEditableColumn(TableColumn tableColumn) {
        tableColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent>() {
            @Override
            public void handle(TableColumn.CellEditEvent cellEvent) {
                Object x = cellEvent.getOldValue();
                int row = cellEvent.getTablePosition().getRow();
                Map<String, Object> feature = (Map<String, Object>) tableView.getItems().get(row);
                feature.put(tableColumn.getId(), cellEvent.getNewValue());
                System.out.println("Se cambió el la fila " + row + " el " + x + " por " + cellEvent.getNewValue());
                saveChanges();
            }
        });
    }

    /**
     * Add buttons to a tableView.
     *
     * @param label Button text
     */
    private TableColumn buildButtonColumn(String label, String image, TableView tableView) {
        TableColumn tableColumn = new TableColumn(label);
        Callback<TableColumn<Map, Void>, TableCell<Map, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Map, Void> call(final TableColumn<Map, Void> param) {
                final TableCell<Map, Void> cell = new TableCell<>() {
                    private final Button btn = new Button("");

                    {// Definir funciones del botón
                        btn.setGraphic(new ImageView(new Image(image)));
                        btn.getStyleClass().add("table-buttons");
                        switch (label) {
                            case DELETE_COLUMN -> btn.setOnAction(actionEvent -> {
                                Map data = getTableView().getItems().get(getIndex());
                                deleteOneItemAction(new Item(data));
                                refreshTable();
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

    /**
     * Add functionality to buttons or events.
     */
    private void addHandlers() {
        deleteAllItemsButton.setOnAction(e -> deleteItemAction());
        createItemButton.setOnAction(e -> createItemAction());
        inventoryComboBox.setOnAction(event -> {
            refreshCatalogComboBox();
            catalogComboBox.setVisible(true);
        });
        catalogComboBox.setOnAction(event -> {
            refreshTable();
            updateResultsLabel();
            createTiledPane.setVisible(true);
            filterField.setVisible(true);
        });
        backButton.setOnAction(e -> backAction());
    }

    private void backAction() {
        ManagePane.clearPane();
        refresh();
    }

    private void createItemAction() {// TODO actionEvent
        // Validations
        if (inventoryComboBox.getValue() == null) return;
        if (catalogComboBox.getValue() == null) return;
        Inventory inventory = inventoryService.get(inventoryComboBox.getValue());
        Catalog catalog = catalogService.get(catalogComboBox.getValue());
        if (inventory != null && catalog != null) {
            CreateItemForm.refresh();
            CreateItemForm.setInventory(inventory);
            CreateItemForm.setCatalog(catalog);
            CreateItemForm.setPreviousPane(pane);
            ManagePane.setCenterPane(ManagePane.getPanes().get(PaneName.CREATE_ITEM));
        }
        refreshTable();
        System.out.println("Create item Button pressed");
    }

    private void deleteItemAction() {// TODO actionEvent
        System.out.println("deleteAll pressed.");
        // Validate selection
        if (catalogComboBox.getValue() != null) {
            // Set dialog details
            deleteAlert.setHeaderText("Delete Items");
            deleteAlert.setContentText("Are you sure you want to delete all the items of " + catalogComboBox.getValue() + " from " + inventoryComboBox.getValue() + "?");
            // Show alert
            Optional<ButtonType> result = deleteAlert.showAndWait();
            // Wait the result and select
            if (result.isPresent())
                // Case #1 Yes
                if (result.get() == buttonTypeYes) {
                    Catalog catalog = catalogService.get(catalogComboBox.getValue());
                    // Validate edit
                    if (catalog != null) {
                        System.out.println("Before: "+catalog);
                        catalog.getItems().clear();
                        if (catalogService.edit(catalog)) {
                            // Remove -> valid
                            System.out.println("edited, items deleted");
                            System.out.println("After: "+catalog);

                            // Refresh catalogs list
                            refresh();
                        } else // Remove -> invalid
                            System.out.println("Error: No edited");
                    }
                } else if (result.get() == buttonTypeNo) {
                    // Case #2 No or cancel as answer.
                    System.out.println("No");
                }
        }
    }

    private void deleteOneItemAction(Item item) {// TODO actionEvent

        System.out.println("Delete items Button pressed");
        Catalog catalog = catalogService.get(catalogComboBox.getValue());
        if (catalog != null) {
            catalog.getItems().remove(item);
            System.out.println("Se actualizaron los cambios la lista (eliminó)");
            if (catalogService.edit(catalog)) {
                System.out.println("Se guardaron los cambios en la lista");
            }
        }
        refreshTable();
    }

    /**
     * Refresh the pane. Cleans all the components.
     */
    public static void refresh() {
        filterField.clear();
        catalogObservableList.clear();
        filterField.setVisible(false);
        catalogComboBox.setVisible(false);
        createTiledPane.setVisible(false);
        tableView.setVisible(false);
    }

    public static void refreshInventoryComboBox() {
        initializeServices();
        inventoryObservableList.clear();
        inventoryObservableList.addAll(inventoryService.getNamesList());
    }

    private void refreshCatalogComboBox() {
        if (inventoryComboBox.getValue() != null) {
            updateCatalogService(inventoryService.get(inventoryComboBox.getValue()));
            catalogObservableList.clear();
            catalogObservableList.addAll(catalogService.getNamesList());
        }
    }

    private void refreshTable() {
        tableView.getColumns().clear();
        tableView.getItems().clear();
        if (inventoryComboBox.getValue() != null) {
            updateCatalogService(inventoryService.get(inventoryComboBox.getValue()));
        }
        if (catalogService.get(catalogComboBox.getValue()) != null) {
            Catalog catalog = catalogService.get(catalogComboBox.getValue());
            // Validate catalog.
            if (catalog != null) {
                tableView.setVisible(true);
                // Settings for Table Columns
                for (String s : catalog.getSchema()) {
                    buildTableColumn(s, s, tableView); //property es el key para encontrar el valor
                }
                // Delete Column
                deleteItemColumn = buildButtonColumn(DELETE_COLUMN, DELETE_ICON, tableView);
                // Validate items
                if (catalog.getItems() != null) {
                    try {
                        tableView.getItems().addAll(generateDataInMap(catalog.getItems()));
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                }
            }
        }
    }

    /**
     * @param items
     * @return
     */
    private ObservableList<Map> generateDataInMap(List<Item> items) throws Exception {
        ObservableList<Map> observableList = FXCollections.observableArrayList();
        for (int i = 0; i < items.size(); i++) { //por cada item, tener un mapa, y el mapa es features.
            if (items.get(i) instanceof Item) {
                Item item = items.get(i);
                observableList.add(item.getFeatures());
                System.out.println("Item ->" + item.getFeatures());
            }
        }
        System.out.println("observable L:¨" + observableList);
        return observableList;
    }

    private List<Item> getItemsFromTable() {
        List<Item> itemList = new ArrayList<>();
        List<Map> itemTable = tableView.getItems();

        for (Map m : itemTable) {
            itemList.add(new Item(m));
        }
        return itemList;
    }

    private void saveChanges() {
        updateCatalogService(inventoryService.get(inventoryComboBox.getValue()));
        Catalog catalog = catalogService.get(catalogComboBox.getValue());
        if (catalog != null) {
            catalog.setItems(getItemsFromTable());
            System.out.println("Se actualizaron los cambios la lista");
            if (catalogService.edit(catalog)) {
                System.out.println("Se guardaron los cambios en la lista");
            }
        }
    }

    /**
     * Updates the label of the matches and number of items showed in the table.
     */
    private static void updateResultsLabel() {
        if (catalogComboBox.getValue() != null) {
            Catalog catalog = catalogService.get(catalogComboBox.getValue());
            int total = catalog.getItems().size();                   // Total of inventories and catalogs.
            int current = tableView.getItems().size();      // Number of inventories and catalogs in the table.
            resultsLabel.setText("Showing " + current + " of " + total + " results.");
        }
    }

    @Override
    public Pane getPane() {
        return createPane();
    }
}