/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entidad;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author lalo
 */
@Entity
@Table(name = "usuarios")
@NamedQueries({
    @NamedQuery(name = "Usuarios.findAll", query = "SELECT u FROM Usuarios u"),
    @NamedQuery(name = "Usuarios.findByUid", query = "SELECT u FROM Usuarios u WHERE u.uid = :uid"),
    @NamedQuery(name = "Usuarios.findByUnombre", query = "SELECT u FROM Usuarios u WHERE u.unombre = :unombre"),
    @NamedQuery(name = "Usuarios.findByUcontrasenia", query = "SELECT u FROM Usuarios u WHERE u.ucontrasenia = :ucontrasenia")})
public class Usuarios implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "uid")
    private Integer uid;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "unombre")
    private String unombre;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "ucontrasenia")
    private String ucontrasenia;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "uid")
    private Collection<Clientes> clientesCollection;

    public Usuarios() {
    }

    public Usuarios(Integer uid) {
        this.uid = uid;
    }

    public Usuarios(Integer uid, String unombre, String ucontrasenia) {
        this.uid = uid;
        this.unombre = unombre;
        this.ucontrasenia = ucontrasenia;
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public String getUnombre() {
        return unombre;
    }

    public void setUnombre(String unombre) {
        this.unombre = unombre;
    }

    public String getUcontrasenia() {
        return ucontrasenia;
    }

    public void setUcontrasenia(String ucontrasenia) {
        this.ucontrasenia = ucontrasenia;
    }

    public Collection<Clientes> getClientesCollection() {
        return clientesCollection;
    }

    public void setClientesCollection(Collection<Clientes> clientesCollection) {
        this.clientesCollection = clientesCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (uid != null ? uid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Usuarios)) {
            return false;
        }
        Usuarios other = (Usuarios) object;
        if ((this.uid == null && other.uid != null) || (this.uid != null && !this.uid.equals(other.uid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entidad.Usuarios[ uid=" + uid + " ]";
    }
    
}
