package com.matic.lugarenelbar.utils;

import com.matic.lugarenelbar.models.Promocion;

import java.util.Comparator;

/**
 * Created by Matic on 27/05/2016.
 */
public class PromocionComparatorPrecio implements Comparator<Promocion> {
    @Override
    public int compare(Promocion lhs, Promocion rhs) {
        return lhs.getPrecio() < rhs.getPrecio() ? -1 : ( lhs.getPrecio() == rhs.getPrecio() ? 0 : 1);
    }
}
