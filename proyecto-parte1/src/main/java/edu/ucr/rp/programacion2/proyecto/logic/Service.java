package edu.ucr.rp.programacion2.proyecto.logic;

import java.util.List;

/**
 *
 * @param <E> Element
 * @param <K> key to identified an element
 */
public interface Service<E, K> {
    boolean add(E element);

    boolean edit(E element);

    boolean remove(E element);

    E get(K key);

    List getAll();
}
