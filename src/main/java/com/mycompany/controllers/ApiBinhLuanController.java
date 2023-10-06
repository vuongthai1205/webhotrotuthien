/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.controllers;

import com.mycompany.DTO.CommentRequestDTO;
import com.mycompany.pojo.BaiViet;
import com.mycompany.pojo.ThanhVien;
import com.mycompany.pojo.TvBinhLuanBv;
import com.mycompany.service.BaiVietService;
import com.mycompany.service.BinhLuanService;
import com.mycompany.service.ThanhVienService;
import java.security.Principal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * @author vuongthai1205
 */
@Controller
@CrossOrigin
@RequestMapping("/api")
public class ApiBinhLuanController {
    @Autowired
    private BinhLuanService commentService;
    @Autowired
    private BaiVietService postService;
    @Autowired
    private ThanhVienService userService;

    @PostMapping("/post/comment/{id-post}/")
    public ResponseEntity<String> addComment(Principal user, @PathVariable(value = "id-post") int id, @RequestBody CommentRequestDTO commentRequestDTO) {
        ThanhVien u = this.userService.getUserByUsername(user.getName());
        BaiViet post = this.postService.getPostById(id);

        TvBinhLuanBv newComment = new TvBinhLuanBv();
        newComment.setBaiViet(post);
        newComment.setThanhVien(u);
        newComment.setNoiDung(commentRequestDTO.getContent());
        if (this.commentService.addComment(newComment)) {
            return new ResponseEntity<>("Cmt", HttpStatus.CREATED);
        }

        return new ResponseEntity<>("comment loi", HttpStatus.BAD_REQUEST);
    }
    
    @PutMapping("/post/comment/{id}/")
    public ResponseEntity<String> updateComment(Principal user, @PathVariable(value = "id") int id, @RequestBody CommentRequestDTO commentRequestDTO){
        ThanhVien u = this.userService.getUserByUsername(user.getName());
        TvBinhLuanBv comment = this.commentService.getCommentById(id);
        
        if (comment.getThanhVien().equals(u)) {
            comment.setNoiDung(commentRequestDTO.getContent());
            if (this.commentService.addComment(comment)) {
                return new ResponseEntity<>("comment thanh cong", HttpStatus.OK);
            }
            else{
                return new ResponseEntity<>("comment loi", HttpStatus.BAD_REQUEST);
            }
        }
        else{
            return new ResponseEntity<>("k có quyen sua comment", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @DeleteMapping("/post/comment/{id}/")
    public ResponseEntity<String> deleteComment(Principal user, @PathVariable(value = "id") int id){
        ThanhVien u = this.userService.getUserByUsername(user.getName());
        TvBinhLuanBv comment = this.commentService.getCommentById(id);
        
        if (comment.getThanhVien().equals(u)) {
            if (this.commentService.deleteComment(comment)) {
                return new ResponseEntity<>("xóa comment thanh cong", HttpStatus.OK);
            }
            else{
                return new ResponseEntity<>("comment loi", HttpStatus.BAD_REQUEST);
            }
        }
        else{
            return new ResponseEntity<>("k có quyen xóa comment", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
