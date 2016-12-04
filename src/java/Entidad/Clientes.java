/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entidad;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author sony
 */
@Entity
@Table(name = "clientes")
@NamedQueries({
    @NamedQuery(name = "Clientes.findAll", query = "SELECT c FROM Clientes c"),
    @NamedQuery(name = "Clientes.findByIdCliente", query = "SELECT c FROM Clientes c WHERE c.idCliente = :idCliente"),
    @NamedQuery(name = "Clientes.findByApellidoPaterno", query = "SELECT c FROM Clientes c WHERE c.apellidoPaterno = :apellidoPaterno"),
    @NamedQuery(name = "Clientes.findByApellidoMaterno", query = "SELECT c FROM Clientes c WHERE c.apellidoMaterno = :apellidoMaterno"),
    @NamedQuery(name = "Clientes.findByNombre", query = "SELECT c FROM Clientes c WHERE c.nombre = :nombre"),
    @NamedQuery(name = "Clientes.findByCorreoE", query = "SELECT c FROM Clientes c WHERE c.correoE = :correoE"),
    @NamedQuery(name = "Clientes.findByEdad", query = "SELECT c FROM Clientes c WHERE c.edad = :edad"),
    @NamedQuery(name = "Clientes.findByNombreFoto", query = "SELECT c FROM Clientes c WHERE c.nombreFoto = :nombreFoto"),
    @NamedQuery(name = "Clientes.findByUid", query = "SELECT c FROM Clientes c WHERE c.uid = :uid")})
public class Clientes implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idCliente")
    private Integer idCliente;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "ApellidoPaterno")
    private String apellidoPaterno;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "ApellidoMaterno")
    private String apellidoMaterno;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "Nombre")
    private String nombre;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 60)
    @Column(name = "correoE")
    private String correoE;
    @Column(name = "edad")
    private Integer edad;
    @Size(max = 20)
    @Column(name = "nombreFoto")
    private String nombreFoto;
    @OneToMany(mappedBy = "idCliente")
    private Collection<Citas> citasCollection;
    @JoinColumn(name = "uid", referencedColumnName = "uid")
    @ManyToOne(optional = false)
    private Usuarios uid;

    public Clientes() {
    }

    public Clientes(Integer idCliente) {
        this.idCliente = idCliente;
    }

    public Clientes(Integer idCliente, String apellidoPaterno, String apellidoMaterno, String nombre, String correoE) {
        this.idCliente = idCliente;
        this.apellidoPaterno = apellidoPaterno;
        this.apellidoMaterno = apellidoMaterno;
        this.nombre = nombre;
        this.correoE = correoE;
    }

    public Integer getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Integer idCliente) {
        this.idCliente = idCliente;
    }

    public String getApellidoPaterno() {
        return apellidoPaterno;
    }

    public void setApellidoPaterno(String apellidoPaterno) {
        this.apellidoPaterno = apellidoPaterno;
    }

    public String getApellidoMaterno() {
        return apellidoMaterno;
    }

    public void setApellidoMaterno(String apellidoMaterno) {
        this.apellidoMaterno = apellidoMaterno;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreoE() {
        return correoE;
    }

    public void setCorreoE(String correoE) {
        this.correoE = correoE;
    }

    public Integer getEdad() {
        return edad;
    }

    public void setEdad(Integer edad) {
        this.edad = edad;
    }

    public String getNombreFoto() {
        return nombreFoto;
    }

    public void setNombreFoto(String nombreFoto) {
        this.nombreFoto = nombreFoto;
    }

    public Collection<Citas> getCitasCollection() {
        return citasCollection;
    }

    public void setCitasCollection(Collection<Citas> citasCollection) {
        this.citasCollection = citasCollection;
    }

    public Usuarios getUid() {
        return uid;
    }

    public void setUid(Usuarios uid) {
        this.uid = uid;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idCliente != null ? idCliente.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Clientes)) {
            return false;
        }
        Clientes other = (Clientes) object;
        if ((this.idCliente == null && other.idCliente != null) || (this.idCliente != null && !this.idCliente.equals(other.idCliente))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entidad.Clientes[ idCliente=" + idCliente + " ]";
    }

}
