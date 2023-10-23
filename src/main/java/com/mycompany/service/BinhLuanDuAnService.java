/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.service;

import com.mycompany.pojo.DuAnTuThien;
import com.mycompany.pojo.ThanhVien;
import com.mycompany.pojo.TvBinhLuanDa;
import java.util.List;

/**
 *
 * @author vuongthai1205
 */
public interface BinhLuanDuAnService {
    boolean addComment(TvBinhLuanDa comment);

    boolean checkUserComment(ThanhVien user, DuAnTuThien duAnTuThien);

    TvBinhLuanDa getCommentPost(ThanhVien user, DuAnTuThien duAnTuThien);

    List<TvBinhLuanDa> listCommentPost(DuAnTuThien duAnTuThien);
    
    TvBinhLuanDa getCommentById(int id);
    boolean deleteComment(TvBinhLuanDa comment);
}
