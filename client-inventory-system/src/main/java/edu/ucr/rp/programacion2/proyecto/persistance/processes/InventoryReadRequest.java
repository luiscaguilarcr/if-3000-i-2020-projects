package edu.ucr.rp.programacion2.proyecto.persistance.processes;

public class InventoryReadRequest {
    // Variables
    String name;
    // Methods

    public String getName() {
        return name;
    }

    public InventoryReadRequest setName(String name) {
        this.name = name;
        return this;
    }
}
