/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.googlecode.jmapper.JMapper;
import com.mycompany.DTO.AuctionStatusDTO;
import com.mycompany.DTO.CommentResponseDTO;
import com.mycompany.DTO.ImagePostDTO;
import com.mycompany.DTO.LikePostDTO;
import com.mycompany.DTO.LinkImageDTO;
import com.mycompany.DTO.PostRequestDTO;
import com.mycompany.DTO.PostResponseDTO;
import com.mycompany.DTO.UserResponseDTO;
import com.mycompany.pojo.BaiViet;
import com.mycompany.pojo.HinhAnhBaiViet;
import com.mycompany.pojo.ThanhVien;
import com.mycompany.pojo.TrangThaiDauGia;
import com.mycompany.pojo.TvBinhLuanBv;
import com.mycompany.pojo.TvThichBv;
import com.mycompany.service.BaiVietService;
import com.mycompany.service.BinhLuanService;
import com.mycompany.service.HinhAnhBaiVietService;
import com.mycompany.service.RedisService;
import com.mycompany.service.ThanhVienService;
import com.mycompany.service.ThichService;
import com.mycompany.service.TrangThaiDauGiaService;
import java.io.IOException;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author vuongthai1205
 */
@RestController
@CrossOrigin
@RequestMapping("/api")
public class ApiBaiVietController {

    public static final SimpleDateFormat F = new SimpleDateFormat("dd-MM-yyyy");
    @Autowired
    private BaiVietService postService;
    @Autowired
    private ThanhVienService userService;
    @Autowired
    private ThichService likeService;
    @Autowired
    private TrangThaiDauGiaService auctionStatusService;
    @Autowired
    private Environment env;
    @Autowired
    private HinhAnhBaiVietService imgPostService;

    @Autowired
    private BinhLuanService commentService;
    @Autowired
    private RedisService redisService;

    @GetMapping("/post/")
    @CrossOrigin
    public ResponseEntity<List<PostResponseDTO>> getPosts(@RequestParam Map<String, String> params) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String responseRedis = redisService.getFromRedis("baiviet");
        if (responseRedis == null) {
            List<PostResponseDTO> postResponseDTOs = new ArrayList<>();
            List<BaiViet> posts = this.postService.getPostList(params);
            JMapper<UserResponseDTO, ThanhVien> userMapper = new JMapper<>(UserResponseDTO.class, ThanhVien.class);
            posts.forEach(post -> {
                UserResponseDTO userResponseDTO = userMapper.getDestination(post.getMaThanhVien());
                PostResponseDTO postResponseDTO = new PostResponseDTO();
                AuctionStatusDTO auctionStatusDTO = new AuctionStatusDTO();

                List<TvThichBv> likePosts = this.likeService.getLikePosts(post);
                List<LikePostDTO> likePostDTOs = new ArrayList<>();
                likePosts.forEach(likePost -> {
                    LikePostDTO likePostDTO = new LikePostDTO();
                    likePostDTO.setId(likePost.getThanhVien().getMaThanhVien());
                    likePostDTO.setImage(likePost.getThanhVien().getAnhDaiDien());
                    likePostDTO.setUsername(likePost.getThanhVien().getTenDangNhap());

                    likePostDTOs.add(likePostDTO);
                });

                List<TvBinhLuanBv> listComments = this.commentService.listCommentPost(post);
                List<CommentResponseDTO> listCommentDTOs = new ArrayList<>();
                listComments.forEach(cmt -> {
                    CommentResponseDTO commentDTO = new CommentResponseDTO();
                    commentDTO.setId(cmt.getMaBinhLuan());
                    commentDTO.setImage(cmt.getThanhVien().getAnhDaiDien());
                    commentDTO.setUsername(cmt.getThanhVien().getTenDangNhap());
                    commentDTO.setContent(cmt.getNoiDung());
                    commentDTO.setIdUser(cmt.getThanhVien().getMaThanhVien());
                    listCommentDTOs.add(commentDTO);
                });
                auctionStatusDTO.setId(post.getTrangThaiDauGia().getMaTrangThaiDauGia());
                auctionStatusDTO.setName(post.getTrangThaiDauGia().getTenTrangThai());

                postResponseDTO.setId(post.getMaBaiViet());
                postResponseDTO.setTitle(post.getTieuDe());
                postResponseDTO.setContent(post.getNoiDung());
                List<HinhAnhBaiViet> listImage = this.imgPostService.listHinhAnh(post);
                List<ImagePostDTO> imagePost = new ArrayList<>();
                listImage.forEach(img -> {
                    ImagePostDTO imagePostDTO = new ImagePostDTO();
                    imagePostDTO.setLink(img.getDuongDanHinh());

                    imagePost.add(imagePostDTO);
                });

                postResponseDTO.setImagesPost(imagePost);
                postResponseDTO.setCreateAt(post.getNgayTao());
                postResponseDTO.setUpdateAt(post.getNgayCapNhat());

                postResponseDTO.setAuctionStatus(auctionStatusDTO);
                postResponseDTO.setLikePost(likePostDTOs);
                postResponseDTO.setListComment(listCommentDTOs);

                if (post.getGiaKhoiDiem() != null) {
                    postResponseDTO.setStartPrice(post.getGiaKhoiDiem());
                }

                postResponseDTO.setUser(userResponseDTO);
                postResponseDTOs.add(postResponseDTO);
                if (post.getThoiGianBatDau() != null) {
                    postResponseDTO.setEndAuctionTime(F.format(post.getThoiGianBatDau()));
                }

                if (post.getThoiGianKetThuc() != null) {
                    postResponseDTO.setStartAuctionTime(F.format(post.getThoiGianKetThuc()));
                }

            });
            String jsonString = objectMapper.writeValueAsString(postResponseDTOs);
            redisService.saveToRedis("baiviet", jsonString);

            return new ResponseEntity<>(postResponseDTOs, HttpStatus.OK);
        } else {
            List<PostResponseDTO> postResponseDTOs = objectMapper.readValue(responseRedis, new TypeReference<List<PostResponseDTO>>() {
            });
            return new ResponseEntity<>(postResponseDTOs, HttpStatus.OK);
        }

    }

    @PostMapping(path = "/post/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> addPost(@RequestBody PostRequestDTO postRequest, Principal user) {
        JMapper<BaiViet, PostRequestDTO> postMapper = new JMapper<>(BaiViet.class, PostRequestDTO.class);
        ThanhVien u = this.userService.getUserByUsername(user.getName());
        BaiViet post = postMapper.getDestination(postRequest);
        post.setMaThanhVien(u);
//        post.setImage(postRequest.getImage());
        List<HinhAnhBaiViet> hinhAnhBaiViets = new ArrayList<>();

        if (postRequest.getImages() != null) {
            for (LinkImageDTO linkImageDTO : postRequest.getImages()) {
                HinhAnhBaiViet hinhAnhBaiViet = new HinhAnhBaiViet();
                hinhAnhBaiViet.setBaiViet(post);
                hinhAnhBaiViet.setDuongDanHinh(linkImageDTO.getLink());
                hinhAnhBaiViets.add(hinhAnhBaiViet);
            }
        }
        post.setHinhAnhBaiViets(hinhAnhBaiViets);
        TrangThaiDauGia auctionStatus = this.auctionStatusService.getAuctionStatus(postRequest.getAuctionStatus());

        post.setTrangThaiDauGia(auctionStatus);
        boolean isAddedOrUpdated = postService.addOrUpdatePost(post);
        if (isAddedOrUpdated) {
            redisService.cleanCache();
            return ResponseEntity.status(HttpStatus.CREATED).body("Post added or updated successfully");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to add or update post");
        }
    }

    @PutMapping(path = "/post/{id}/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updatePost(@RequestBody PostRequestDTO postRequest, Principal user, @PathVariable(value = "id") int id) {

        ThanhVien u = this.userService.getUserByUsername(user.getName());
        JMapper<BaiViet, PostRequestDTO> postMapper = new JMapper<>(BaiViet.class, PostRequestDTO.class);
        BaiViet post = this.postService.getPostById(id);
        post = postMapper.getDestination(postRequest);
        if (post.getMaThanhVien().equals(u)) {
            List<HinhAnhBaiViet> listHinhAnhBaiViets = this.imgPostService.listHinhAnh(post);
            listHinhAnhBaiViets.forEach(i -> {
                this.imgPostService.deleteImage(i);
            });
            List<HinhAnhBaiViet> hinhAnhBaiViets = new ArrayList<>();

            if (postRequest.getImages() != null) {
                for (LinkImageDTO linkImageDTO : postRequest.getImages()) {
                    HinhAnhBaiViet hinhAnhBaiViet = new HinhAnhBaiViet();
                    hinhAnhBaiViet.setBaiViet(post);
                    hinhAnhBaiViet.setDuongDanHinh(linkImageDTO.getLink());
                    hinhAnhBaiViets.add(hinhAnhBaiViet);
                }
            }
            post.setHinhAnhBaiViets(hinhAnhBaiViets);
            TrangThaiDauGia auctionStatus = this.auctionStatusService.getAuctionStatus(postRequest.getAuctionStatus());
            
            post.setTrangThaiDauGia(auctionStatus);
            if (this.postService.addOrUpdatePost(post)) {
                redisService.cleanCache();
                return ResponseEntity.status(HttpStatus.OK).body("Post updated successfully");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to add or update post");
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You can not permission to edit the post");
        }

    }

    @DeleteMapping("/post/{id}/")
    public ResponseEntity<String> delete(@PathVariable(value = "id") int id, Principal user) {

        ThanhVien u = this.userService.getUserByUsername(user.getName());

        BaiViet post = this.postService.getPostById(id);

        if (post.getMaThanhVien().equals(u)) {
            if (this.postService.deletePost(post.getMaBaiViet())) {
                redisService.cleanCache();
                return ResponseEntity.status(HttpStatus.OK).body("Post delete successfully");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete post");
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You can not permission to delete the post");
        }

    }

    @GetMapping("/post/{id}/")
    public ResponseEntity<PostResponseDTO> getPost(@PathVariable(value = "id") int id) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String responseRedis = redisService.getFromRedis("baiviet:" + id);
        if (responseRedis == null) {
            BaiViet post = this.postService.getPostById(id);
            PostResponseDTO postResponseDTO = new PostResponseDTO();

            UserResponseDTO userResponseDTO = new UserResponseDTO();
            AuctionStatusDTO auctionStatusDTO = new AuctionStatusDTO();

            List<TvThichBv> likePosts = this.likeService.getLikePosts(post);
            List<LikePostDTO> likePostDTOs = new ArrayList<>();
            likePosts.forEach(likePost -> {
                LikePostDTO likePostDTO = new LikePostDTO();
                likePostDTO.setId(likePost.getThanhVien().getMaThanhVien());
                likePostDTO.setImage(likePost.getThanhVien().getAnhDaiDien());
                likePostDTO.setUsername(likePost.getThanhVien().getTenDangNhap());

                likePostDTOs.add(likePostDTO);
            });

            auctionStatusDTO.setId(post.getTrangThaiDauGia().getMaTrangThaiDauGia());
            auctionStatusDTO.setName(post.getTrangThaiDauGia().getTenTrangThai());

            postResponseDTO.setId(post.getMaBaiViet());
            postResponseDTO.setTitle(post.getTieuDe());
            postResponseDTO.setContent(post.getNoiDung());
//        postResponseDTO.setImage(post.getImage());
            List<HinhAnhBaiViet> hinhAnhBaiViets = this.imgPostService.listHinhAnh(post);
            List<ImagePostDTO> imagePostDTOs = new ArrayList<>();
            hinhAnhBaiViets.forEach(i -> {
                ImagePostDTO imagePostDTO = new ImagePostDTO();
                imagePostDTO.setLink(i.getDuongDanHinh());
                imagePostDTOs.add(imagePostDTO);
            });
            postResponseDTO.setImagesPost(imagePostDTOs);
            postResponseDTO.setCreateAt(post.getNgayTao());
            postResponseDTO.setUpdateAt(post.getNgayCapNhat());
            if (post.getThoiGianBatDau() != null) {
                postResponseDTO.setStartAuctionTime(F.format(post.getThoiGianBatDau()));
            }
            if (post.getThoiGianKetThuc() != null) {
                postResponseDTO.setEndAuctionTime(F.format(post.getThoiGianKetThuc()));
            }
            postResponseDTO.setAuctionStatus(auctionStatusDTO);
            postResponseDTO.setLikePost(likePostDTOs);
            userResponseDTO.setUsername(post.getMaThanhVien().getTenDangNhap());
            userResponseDTO.setAvatar(post.getMaThanhVien().getAnhDaiDien());

            postResponseDTO.setUser(userResponseDTO);
            if (post.getGiaKhoiDiem() != null) {
                postResponseDTO.setStartPrice(post.getGiaKhoiDiem());
            }

            String jsonString = objectMapper.writeValueAsString(postResponseDTO);
            redisService.saveToRedis("baiviet:" + id, jsonString);
            return new ResponseEntity<>(postResponseDTO, HttpStatus.OK);
        } else {
            // Chuyển đổi chuỗi JSON thành đối tượng PostResponseDTO
            PostResponseDTO postResponseDTO = objectMapper.readValue(responseRedis, PostResponseDTO.class);
            return new ResponseEntity<>(postResponseDTO, HttpStatus.OK);
        }

    }

    @GetMapping("/post/count-pages/")
    public ResponseEntity<?> getPages() {
        int count = this.postService.countPost();
        int pageSize = Integer.parseInt(this.env.getProperty("PAGE_SIZE"));
        int result = (int) Math.ceil(count * 1.0 / pageSize);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
