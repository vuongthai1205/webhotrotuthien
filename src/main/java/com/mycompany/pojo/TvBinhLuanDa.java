/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.pojo;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author vuongthai1205
 */
@Entity
@Table(name = "tv_binh_luan_da")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TvBinhLuanDa implements Serializable{
    @PrePersist
    protected void onCreate() {
        this.ngayTao = new Date(System.currentTimeMillis());
    }

    @PreUpdate
    protected void onUpdate() {
        this.ngayCapNhat = new Date(System.currentTimeMillis());
    }
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "MaBinhLuan")
    private Integer maBinhLuan;
    @Lob
    @Size(max = 65535)
    @Column(name = "NoiDung")
    private String noiDung;
    @Column(name = "NgayTao")
    @Temporal(TemporalType.TIMESTAMP)
    private Date ngayTao;
    @Column(name = "NgayCapNhat")
    @Temporal(TemporalType.TIMESTAMP)
    private Date ngayCapNhat;
    @JoinColumn(name = "MaDuAn", referencedColumnName = "MaDuAn")
    @ManyToOne(optional = false)
    private DuAnTuThien duAnTuThien;
    @JoinColumn(name = "MaThanhVien", referencedColumnName = "MaThanhVien")
    @ManyToOne(optional = false)
    private ThanhVien thanhVien;
}
