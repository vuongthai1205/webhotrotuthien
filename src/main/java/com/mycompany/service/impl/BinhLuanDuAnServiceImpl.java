/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.service.impl;

import com.mycompany.pojo.DuAnTuThien;
import com.mycompany.pojo.ThanhVien;
import com.mycompany.pojo.TvBinhLuanDa;
import com.mycompany.repository.BinhLuanDuAnRepository;
import com.mycompany.service.BinhLuanDuAnService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author vuongthai1205
 */
@Service
public class BinhLuanDuAnServiceImpl implements BinhLuanDuAnService{
    
    @Autowired
    private BinhLuanDuAnRepository binhLuanDuAnService;
    @Override
    public boolean addComment(TvBinhLuanDa comment) {
        return this.binhLuanDuAnService.addComment(comment);
    }

    @Override
    public boolean checkUserComment(ThanhVien user, DuAnTuThien duAnTuThien) {
        return this.binhLuanDuAnService.checkUserComment(user, duAnTuThien);
    }

    @Override
    public TvBinhLuanDa getCommentPost(ThanhVien user, DuAnTuThien duAnTuThien) {
        return this.binhLuanDuAnService.getCommentPost(user, duAnTuThien);
    }

    @Override
    public List<TvBinhLuanDa> listCommentPost(DuAnTuThien duAnTuThien) {
        return this.binhLuanDuAnService.listCommentPost(duAnTuThien);
    }

    @Override
    public TvBinhLuanDa getCommentById(int id) {
        return this.binhLuanDuAnService.getCommentById(id);
    }

    @Override
    public boolean deleteComment(TvBinhLuanDa comment) {
        return this.binhLuanDuAnService.deleteComment(comment);
    }
    
}
