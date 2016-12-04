/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entidad;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author sony
 */
@Entity
@Table(name = "userstemp")
@NamedQueries({
    @NamedQuery(name = "Userstemp.findAll", query = "SELECT u FROM Userstemp u"),
    @NamedQuery(name = "Userstemp.findById", query = "SELECT u FROM Userstemp u WHERE u.id = :id"),
    @NamedQuery(name = "Userstemp.findByAp", query = "SELECT u FROM Userstemp u WHERE u.ap = :ap"),
    @NamedQuery(name = "Userstemp.findByAm", query = "SELECT u FROM Userstemp u WHERE u.am = :am"),
    @NamedQuery(name = "Userstemp.findByNombre", query = "SELECT u FROM Userstemp u WHERE u.nombre = :nombre"),
    @NamedQuery(name = "Userstemp.findByCorreo", query = "SELECT u FROM Userstemp u WHERE u.correo = :correo"),
    @NamedQuery(name = "Userstemp.findByPasswordTemp", query = "SELECT u FROM Userstemp u WHERE u.passwordTemp = :passwordTemp"),
    @NamedQuery(name = "Userstemp.findByHash", query = "SELECT u FROM Userstemp u WHERE u.hash = :hash"),
    @NamedQuery(name = "Userstemp.findByActivo", query = "SELECT u FROM Userstemp u WHERE u.activo = :activo"),
    @NamedQuery(name = "Userstemp.findByEstado", query = "SELECT u FROM Userstemp u WHERE u.estado = :estado")})
public class Userstemp implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 32)
    @Column(name = "ap")
    private String ap;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 32)
    @Column(name = "am")
    private String am;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "nombre")
    private String nombre;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 254)
    @Column(name = "correo")
    private String correo;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "passwordTemp")
    private String passwordTemp;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 32)
    @Column(name = "hash")
    private String hash;
    @Basic(optional = false)
    @NotNull
    @Column(name = "activo")
    private int activo;
    @Basic(optional = false)
    @NotNull
    @Column(name = "estado")
    private int estado;

    public Userstemp() {
    }

    public Userstemp(Integer id) {
        this.id = id;
    }

    public Userstemp(Integer id, String ap, String am, String nombre, String correo, String passwordTemp, String hash, int activo, int estado) {
        this.id = id;
        this.ap = ap;
        this.am = am;
        this.nombre = nombre;
        this.correo = correo;
        this.passwordTemp = passwordTemp;
        this.hash = hash;
        this.activo = activo;
        this.estado = estado;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAp() {
        return ap;
    }

    public void setAp(String ap) {
        this.ap = ap;
    }

    public String getAm() {
        return am;
    }

    public void setAm(String am) {
        this.am = am;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getPasswordTemp() {
        return passwordTemp;
    }

    public void setPasswordTemp(String passwordTemp) {
        this.passwordTemp = passwordTemp;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public int getActivo() {
        return activo;
    }

    public void setActivo(int activo) {
        this.activo = activo;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Userstemp)) {
            return false;
        }
        Userstemp other = (Userstemp) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entidad.Userstemp[ id=" + id + " ]";
    }
    
}
