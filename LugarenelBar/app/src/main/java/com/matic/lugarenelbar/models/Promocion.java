package com.matic.lugarenelbar.models;

/**
 * Created by Matic on 14/05/2016.
 */
public class Promocion {

    private int idPromocion;
    private Bar bar;
    private String nombre;
    private String descripcion;
    private double precio;
    private String tipo;

    public Promocion(int idPromocion, Bar bar, String nombre, String descripcion, double precio, String tipo) {
        this.idPromocion = idPromocion;
        this.bar = bar;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.tipo = tipo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Bar getBar() {
        return bar;
    }

    public void setBar(Bar bar) {
        this.bar = bar;
    }

    public int getIdPromocion() {
        return idPromocion;
    }

    public void setIdPromocion(int idPromocion) {
        this.idPromocion = idPromocion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}