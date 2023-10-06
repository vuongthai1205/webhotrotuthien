/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.pojo;

import java.io.Serializable;
import java.util.Set;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author vuongthai1205
 */
@Entity
@Table(name = "the")
@NamedQueries({
    @NamedQuery(name = "The.findAll", query = "SELECT t FROM The t")})
public class The implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "MaThe")
    private Integer maThe;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "TenThe")
    private String tenThe;
    @ManyToMany(mappedBy = "theSet")
    private Set<BaiViet> baiVietSet;

    public The() {
    }

    public The(Integer maThe) {
        this.maThe = maThe;
    }

    public The(Integer maThe, String tenThe) {
        this.maThe = maThe;
        this.tenThe = tenThe;
    }

    public Integer getMaThe() {
        return maThe;
    }

    public void setMaThe(Integer maThe) {
        this.maThe = maThe;
    }

    public String getTenThe() {
        return tenThe;
    }

    public void setTenThe(String tenThe) {
        this.tenThe = tenThe;
    }

    public Set<BaiViet> getBaiVietSet() {
        return baiVietSet;
    }

    public void setBaiVietSet(Set<BaiViet> baiVietSet) {
        this.baiVietSet = baiVietSet;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (maThe != null ? maThe.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof The)) {
            return false;
        }
        The other = (The) object;
        if ((this.maThe == null && other.maThe != null) || (this.maThe != null && !this.maThe.equals(other.maThe))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mycompany.pojo.The[ maThe=" + maThe + " ]";
    }
    
}
