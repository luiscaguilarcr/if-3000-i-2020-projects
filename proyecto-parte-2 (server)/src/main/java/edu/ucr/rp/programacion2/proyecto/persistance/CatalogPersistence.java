package edu.ucr.rp.programacion2.proyecto.persistance;

import edu.ucr.rp.programacion2.proyecto.domain.Catalog;
import edu.ucr.rp.programacion2.proyecto.domain.Inventory;

import java.util.List;

public interface CatalogPersistence extends Persistence<Catalog, List<Catalog>> {
    void setInventory(Inventory inventory);
    Inventory getInventory();
}
