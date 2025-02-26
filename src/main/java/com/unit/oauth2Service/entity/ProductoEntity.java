package com.unit.oauth2Service.entity;


import jakarta.persistence.*;

import java.util.UUID;

@Table
@Entity(name = "product")
public class ProductoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto")
    private Long idProducto;
    @Column(name = "id_usuario")
    private UUID id;

    private String nombreProdcuto;

    private String precioProducto;

   @ManyToOne
   private UserEntity userEntity;


    public Long getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Long idProducto) {
        this.idProducto = idProducto;
    }

    public String getNombreProdcuto() {
        return nombreProdcuto;
    }

    public void setNombreProdcuto(String nombreProdcuto) {
        this.nombreProdcuto = nombreProdcuto;
    }

    public String getPrecioProducto() {
        return precioProducto;
    }

    public void setPrecioProducto(String precioProducto) {
        this.precioProducto = precioProducto;
    }

    public UserEntity getUserEntity() {
        return userEntity;
    }

    public void setUserEntity(UserEntity userEntity) {
        this.userEntity = userEntity;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}
