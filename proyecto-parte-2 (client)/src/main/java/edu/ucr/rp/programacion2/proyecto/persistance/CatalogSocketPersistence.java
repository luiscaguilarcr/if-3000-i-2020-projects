package edu.ucr.rp.programacion2.proyecto.persistance;

import edu.ucr.rp.programacion2.proyecto.domain.Catalog;
import edu.ucr.rp.programacion2.proyecto.domain.Inventory;
import edu.ucr.rp.programacion2.proyecto.persistance.messages.*;
import edu.ucr.rp.programacion2.proyecto.util.JsonUtil;

import java.io.IOException;
import java.net.Socket;
import java.util.List;

import static edu.ucr.rp.programacion2.proyecto.persistance.messages.RequestType.*;
import static edu.ucr.rp.programacion2.proyecto.util.RequestProcessUtil.receive;
import static edu.ucr.rp.programacion2.proyecto.util.RequestProcessUtil.send;

/**
 * This class sends a receives data to a server.
 */
public class CatalogSocketPersistence implements CatalogPersistence {

    private String host;
    private int port;
    private Socket clientSocket;
    private JsonUtil jsonUtil = new JsonUtil();
    private Inventory inventory;

    /**
     * Information used to establish the connection to the server.
     *
     * @param host server ip.
     * @param port server port.
     */
    public CatalogSocketPersistence(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    @Override
    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    /**
     * Send an insert request to the server and then receives a confirmation.
     *
     * @param catalog catalog to insert.
     * @return {@code true} if the directory have been saved.{@code false} Otherwise.
     * @throws PersistenceException if the catalog is not valid or the connection has failed.
     */
    @Override
    public boolean insert(Catalog catalog) throws PersistenceException {
        try {
            refresh();
            return insertRequest(catalog);
        } catch (IOException | ClassNotFoundException e) {
            throw new PersistenceException(e.getMessage());
        }
    }

    /**
     * Send an update request to the server and then receives a confirmation.
     *
     * @param catalog catalog to be updated.
     * @return {@code true} if the directory have been saved.{@code false} Otherwise.
     * @throws PersistenceException if the catalog is not valid or the connection has failed.
     */
    @Override
    public boolean update(Catalog catalog) throws PersistenceException {
        try {
            refresh();
            return updateRequest(catalog);
        } catch (IOException | ClassNotFoundException e) {
            throw new PersistenceException(e.getMessage());
        }
    }

    /**
     * Send an update request to the server using the name to identify the catalog. Then receives the inventory if exists.
     *
     * @param name of the catalog.
     * @return {Catalog} inventory has been found.
     * @throws PersistenceException if the catalog is not found or the connection has failed.
     */
    @Override
    public Catalog read(String name) throws PersistenceException {
        try {
            refresh();
            return readRequest(name);
        } catch (IOException | ClassNotFoundException e) {
            throw new PersistenceException(e.getMessage());
        }
    }

    /**
     * Search and return a list with inventories.
     *
     * @return {@code List<Catalog>} List of the inventories.
     * @throws PersistenceException if the connection has failed.
     */
    @Override
    public List<Catalog> readAll() throws PersistenceException {
        try {
            refresh();
            return readAllRequest();

        } catch (IOException | ClassNotFoundException e) {
            throw new PersistenceException(e.getMessage());
        }
    }

    /**
     * Deletes an catalog.
     *
     * @param catalog to delete.
     * @return {@code true} if the directory have been removed or doesn't exists.{@code false} Otherwise.
     */
    @Override
    public boolean delete(Catalog catalog) throws PersistenceException {
        try {
            refresh();
            return deleteRequest(catalog);
        } catch (IOException | ClassNotFoundException e) {
            throw new PersistenceException(e.getMessage());
        }
    }

    /**
     * Deletes all the catalogs.
     *
     * @return {@code true} if the directory have been removed or doesn't exists.{@code false} Otherwise.
     */
    @Override
    public boolean deleteAll() throws PersistenceException {
        try {
            refresh();
            return deleteAllRequest();
        } catch (IOException | ClassNotFoundException e) {
            throw new PersistenceException(e.getMessage());
        }
    }

    private boolean insertRequest(Catalog catalog) throws IOException, ClassNotFoundException, PersistenceException {
        Request request = new Request();
        try {
            // Establish the connection with the server.
            clientSocket = new Socket(host, port);
            // Create a insert request.
            request.setType(INSERT_CATALOG);
            // Send and wait the request.
            send(request, clientSocket);

            // Create an catalog request.
            CatalogRequest catalogRequest = new CatalogRequest();
            catalogRequest.setCatalog(catalog);

            // Send and wait the request.
            send(catalogRequest, clientSocket);

            // Receives a ConfirmationRequest.
            ConfirmationRequest confirmationRequest = receive(ConfirmationRequest.class, clientSocket);

            if (!confirmationRequest.isCompleted()) {
                throw new PersistenceException(confirmationRequest.getDetails());
            }

            return true;
        } finally {
            // Send close request
            closeConnection();
        }
    }

    private boolean updateRequest(Catalog catalog) throws IOException, ClassNotFoundException, PersistenceException {
        Request request = new Request();
        try {
            // Establish the connection with the server.
            clientSocket = new Socket(host, port);
            // Create a update request.
            request.setType(UPDATE_CATALOG);
            // Send and wait the request.
            send(request, clientSocket);

            // Create an catalog request.
            CatalogRequest catalogRequest = new CatalogRequest();
            catalogRequest.setCatalog(catalog);

            // Send and wait the request.
            send(catalogRequest, clientSocket);

            // Receives a ConfirmationRequest.
            ConfirmationRequest confirmationRequest = receive(ConfirmationRequest.class, clientSocket);

            if (!confirmationRequest.isCompleted()) {
                throw new PersistenceException(confirmationRequest.getDetails());
            }

            return true;
        } finally {
            // Send close request
            closeConnection();
        }
    }

    private Catalog readRequest(String key) throws IOException, PersistenceException, ClassNotFoundException {
        Request request = new Request();
        try {
            // Establish the connection with the server.
            clientSocket = new Socket(host, port);
            // Create a read all request.
            request.setType(READ_CATALOG);
            // Send and wait the request.
            send(request, clientSocket);

            // Create a read request.
            CatalogReadRequest catalogReadRequest = new CatalogReadRequest();
            catalogReadRequest.setName(key);
            send(catalogReadRequest, clientSocket);

            // Receives a ConfirmationRequest.
            ConfirmationRequest confirmationRequest = receive(ConfirmationRequest.class, clientSocket);

            if (!confirmationRequest.isCompleted()) {
                throw new PersistenceException(confirmationRequest.getDetails());
            }
            // Wait for an catalog
            CatalogRequest catalogRequest = receive(CatalogRequest.class, clientSocket);
            Catalog catalog = catalogRequest.getCatalog();
            return catalog;
        } finally {
            // Send close request
            closeConnection();
        }
    }

    private List<Catalog> readAllRequest() throws IOException, ClassNotFoundException {
        Request request = new Request();
        try {
            // Establish the connection with the server.
            clientSocket = new Socket(host, port);
            // Create a read all request.
            request.setType(READ_ALL_CATALOGS);
            // Send and wait the request.
            send(request, clientSocket);

            // Receives an CatalogListRequest.
            CatalogListRequest catalogListRequest = receive(CatalogListRequest.class, clientSocket);
            List<Catalog> list = catalogListRequest.getList();

            return list;
        } finally {
            // Send close request
            closeConnection();
        }
    }

    private boolean deleteRequest(Catalog catalog) throws IOException, ClassNotFoundException, PersistenceException {
        Request request = new Request();
        try {
            // Establish the connection with the server.
            clientSocket = new Socket(host, port);
            // Create a delete request.
            request.setType(DELETE_CATALOG);
            // Send and wait the request.
            send(request, clientSocket);

            // Create an catalog request.
            CatalogRequest catalogRequest = new CatalogRequest();
            catalogRequest.setCatalog(catalog);
            send(catalogRequest, clientSocket);

            // Receives a ConfirmationRequest.
            ConfirmationRequest confirmationRequest = receive(ConfirmationRequest.class, clientSocket);

            if (!confirmationRequest.isCompleted()) {
                throw new PersistenceException(confirmationRequest.getDetails());
            }
            return true;
        } finally {
            // Send close request
            closeConnection();
        }
    }

    private boolean deleteAllRequest() throws IOException, ClassNotFoundException, PersistenceException {
        Request request = new Request();
        try {
            // Establish the connection with the server.
            clientSocket = new Socket(host, port);
            // Create a read all request.
            request.setType(DELETE_ALL_INVENTORIES);
            // Send and wait the request.
            send(request, clientSocket);

            // Receives a ConfirmationRequest.
            ConfirmationRequest confirmationRequest = receive(ConfirmationRequest.class, clientSocket);

            if (!confirmationRequest.isCompleted()) {
                throw new PersistenceException(confirmationRequest.getDetails());
            }
            return true;
        } finally {
            // Send close request
            closeConnection();
        }
    }

    private boolean refresh() throws IOException, ClassNotFoundException, PersistenceException{
        Request request = new Request();
        try {
            // Establish the connection with the server.
            clientSocket = new Socket(host, port);
            // Create a insert request.
            request.setType(SET_INVENTORY);
            // Send and wait the request.
            send(request, clientSocket);

            // Create an catalog request.
            InventoryRequest inventoryRequest = new InventoryRequest();
            inventoryRequest.setInventory(getInventory());

            // Send and wait the request.
            send(inventoryRequest, clientSocket);

            // Receives a ConfirmationRequest.
            ConfirmationRequest confirmationRequest = receive(ConfirmationRequest.class, clientSocket);

            if (!confirmationRequest.isCompleted()) {
                throw new PersistenceException(confirmationRequest.getDetails());
            }

            return true;
        } finally {
            // Send close request
            closeConnection();
        }
    }

    private void closeConnection() throws IOException {
        Request closeRequest = new Request();
        closeRequest.setType(CLOSE);
        send(closeRequest, clientSocket);

        if (clientSocket != null) {
            clientSocket.close();
        }
    }
}
