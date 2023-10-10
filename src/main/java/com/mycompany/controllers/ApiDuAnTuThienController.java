/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.controllers;

import com.mycompany.DTO.CharityProjectRequestDTO;
import com.mycompany.DTO.CharityProjectResponseDTO;
import com.mycompany.DTO.ImagePostDTO;
import com.mycompany.DTO.LinkImageDTO;
import com.mycompany.DTO.UserResponseDTO;
import com.mycompany.pojo.DuAnTuThien;
import com.mycompany.pojo.HinhAnhDuAn;
import com.mycompany.pojo.ThanhVien;
import com.mycompany.service.DuAnTuThienService;
import com.mycompany.service.HinhAnhDuAnService;
import com.mycompany.service.ThanhVienService;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
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
public class ApiDuAnTuThienController {

    public static final SimpleDateFormat F = new SimpleDateFormat("dd-MM-yyyy");
    @Autowired
    private DuAnTuThienService duAnTuThienService;
    @Autowired
    private HinhAnhDuAnService hinhAnhDuAnService;
    @Autowired
    private ThanhVienService userService;

    @GetMapping("/charity-project/")
    public ResponseEntity<List<CharityProjectResponseDTO>> getPosts(@RequestParam Map<String, String> params) {
        List<CharityProjectResponseDTO> charityProjectResponseDTOs = new ArrayList<>();
        List<DuAnTuThien> duAnTuThiens = this.duAnTuThienService.getDuAnTuThiensWithApproved(params);
        duAnTuThiens.forEach(duAnTuThien -> {
            UserResponseDTO userResponseDTO = new UserResponseDTO();
            CharityProjectResponseDTO charityProjectResponseDTO = new CharityProjectResponseDTO();

            charityProjectResponseDTO.setId(duAnTuThien.getMaDuAn());
            charityProjectResponseDTO.setNameProject(duAnTuThien.getTenDuAn());
            charityProjectResponseDTO.setPurpose(duAnTuThien.getMucDich());
            charityProjectResponseDTO.setAddress(duAnTuThien.getDiaDiem());
            charityProjectResponseDTO.setAmountRaised(duAnTuThien.getSoTienHuyDong());
            if (duAnTuThien.getThoiGianBatDau() != null) {
                charityProjectResponseDTO.setStartTime(F.format(duAnTuThien.getThoiGianBatDau()));
            }

            if (duAnTuThien.getThoiGianKetThuc() != null) {
                charityProjectResponseDTO.setEndTime(F.format(duAnTuThien.getThoiGianKetThuc()));
            }
            charityProjectResponseDTO.setCreateAt(duAnTuThien.getNgayTao());
            charityProjectResponseDTO.setUpdateAt(duAnTuThien.getNgayCapNhat());

            List<HinhAnhDuAn> listImage = this.hinhAnhDuAnService.listHinhAnh(duAnTuThien);
            List<ImagePostDTO> imageDTOs = new ArrayList<>();
            listImage.forEach(img -> {
                ImagePostDTO imagePostDTO = new ImagePostDTO();
                imagePostDTO.setLink(img.getDuongDanHinh());

                imageDTOs.add(imagePostDTO);
            });
            charityProjectResponseDTO.setImages(imageDTOs);

            userResponseDTO.setUsername(duAnTuThien.getMaThanhVienTaoDA().getTenDangNhap());
            userResponseDTO.setAvatar(duAnTuThien.getMaThanhVienTaoDA().getAnhDaiDien());
            userResponseDTO.setId(duAnTuThien.getMaThanhVienTaoDA().getMaThanhVien());
            userResponseDTO.setAddress(duAnTuThien.getMaThanhVienTaoDA().getDiaChi());
            userResponseDTO.setCreateAt(duAnTuThien.getMaThanhVienTaoDA().getNgayTao());
            userResponseDTO.setUpdateAt(duAnTuThien.getMaThanhVienTaoDA().getNgayCapNhat());
            userResponseDTO.setDateOfBirth(duAnTuThien.getMaThanhVienTaoDA().getNgaySinh());
            userResponseDTO.setEmail(duAnTuThien.getMaThanhVienTaoDA().getEmail());
            userResponseDTO.setFirstName(duAnTuThien.getMaThanhVienTaoDA().getTen());
            userResponseDTO.setLastName(duAnTuThien.getMaThanhVienTaoDA().getHo());
            userResponseDTO.setPhone(duAnTuThien.getMaThanhVienTaoDA().getSoDienThoai());
            userResponseDTO.setGender(duAnTuThien.getMaThanhVienTaoDA().getGioiTinh());

            charityProjectResponseDTO.setUser(userResponseDTO);

            charityProjectResponseDTOs.add(charityProjectResponseDTO);

        });
        return new ResponseEntity<>(charityProjectResponseDTOs, HttpStatus.OK);
    }

    @PostMapping(path = "/charity-project/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> addCharityProject(@RequestBody CharityProjectRequestDTO charityProjectRequestDTO, Principal user) {
        ThanhVien u = this.userService.getUserByUsername(user.getName());
        DuAnTuThien duAnTuThien = new DuAnTuThien();
        duAnTuThien.setMaThanhVienTaoDA(u);
        List<HinhAnhDuAn> listImages = new ArrayList<>();
        if (charityProjectRequestDTO.getImages() != null) {

            for (LinkImageDTO linkImageDTO : charityProjectRequestDTO.getImages()) {
                HinhAnhDuAn hinhAnhDuAn = new HinhAnhDuAn();
                hinhAnhDuAn.setMaDuAn(duAnTuThien);
                hinhAnhDuAn.setDuongDanHinh(linkImageDTO.getLink());
                listImages.add(hinhAnhDuAn);
            }
        }
        duAnTuThien.setHinhAnhDuAnList(listImages);
        duAnTuThien.setTenDuAn(charityProjectRequestDTO.getNameProject());
        duAnTuThien.setMucDich(charityProjectRequestDTO.getPurpose());
        duAnTuThien.setDiaDiem(charityProjectRequestDTO.getAddress());
        duAnTuThien.setSoTienHuyDong(charityProjectRequestDTO.getAmountRaised());
        duAnTuThien.setThoiGianBatDau(charityProjectRequestDTO.getStartTime());
        duAnTuThien.setThoiGianKetThuc(charityProjectRequestDTO.getEndTime());
        duAnTuThien.setIsApproved(false);
        if (this.duAnTuThienService.addOrUpdateCharityProject(duAnTuThien)) {
            return ResponseEntity.status(HttpStatus.CREATED).body("Project added or updated successfully");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to add or update project");
        }
    }

    @PutMapping(path = "/charity-project/{id}/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> addCharityProject(@RequestBody CharityProjectRequestDTO charityProjectRequestDTO, Principal user, @PathVariable(value = "id") int id) {
        ThanhVien u = this.userService.getUserByUsername(user.getName());
        DuAnTuThien duAnTuThien = this.duAnTuThienService.getDuAnTuThienById(id);
        if (duAnTuThien.getMaThanhVienTaoDA().equals(u)) {
            List<HinhAnhDuAn> hinhAnhDuAns = this.hinhAnhDuAnService.listHinhAnh(duAnTuThien);
            hinhAnhDuAns.forEach(i -> {
                this.hinhAnhDuAnService.deleteImage(i);
            });
            List<HinhAnhDuAn> listImages = new ArrayList<>();
            if (charityProjectRequestDTO.getImages() != null) {

                for (LinkImageDTO linkImageDTO : charityProjectRequestDTO.getImages()) {
                    HinhAnhDuAn hinhAnhDuAn = new HinhAnhDuAn();
                    hinhAnhDuAn.setMaDuAn(duAnTuThien);
                    hinhAnhDuAn.setDuongDanHinh(linkImageDTO.getLink());
                    listImages.add(hinhAnhDuAn);
                }
            }
            duAnTuThien.setHinhAnhDuAnList(listImages);
            duAnTuThien.setTenDuAn(charityProjectRequestDTO.getNameProject());
            duAnTuThien.setMucDich(charityProjectRequestDTO.getPurpose());
            duAnTuThien.setDiaDiem(charityProjectRequestDTO.getAddress());
            duAnTuThien.setSoTienHuyDong(charityProjectRequestDTO.getAmountRaised());
            duAnTuThien.setThoiGianBatDau(charityProjectRequestDTO.getStartTime());
            duAnTuThien.setThoiGianKetThuc(charityProjectRequestDTO.getEndTime());
            if (this.duAnTuThienService.addOrUpdateCharityProject(duAnTuThien)) {
                return ResponseEntity.status(HttpStatus.OK).body("Project added or updated successfully");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to add or update project");
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You can not permission to edit the post");
        }

    }

    @DeleteMapping("/charity-project/{id}/")
    public ResponseEntity<String> deleteProject(@PathVariable(value = "id") int id, Principal user) {
        ThanhVien u = this.userService.getUserByUsername(user.getName());
        DuAnTuThien duAnTuThien = this.duAnTuThienService.getDuAnTuThienById(id);

        if (duAnTuThien.getMaThanhVienTaoDA().equals(u)) {
            if (this.duAnTuThienService.deleteProject(duAnTuThien)) {
                return ResponseEntity.status(HttpStatus.OK).body("Project delete successfully");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete project");
            }

        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You can not permission to delete the post");
        }

    }

    @GetMapping("/charity-project/{id}/")
    public ResponseEntity<CharityProjectResponseDTO> getProjecbyId(@PathVariable(value = "id") int id) {
        DuAnTuThien duAnTuThien = this.duAnTuThienService.getDuAnTuThienById(id);
        UserResponseDTO userResponseDTO = new UserResponseDTO();
        CharityProjectResponseDTO charityProjectResponseDTO = new CharityProjectResponseDTO();

        charityProjectResponseDTO.setId(duAnTuThien.getMaDuAn());
        charityProjectResponseDTO.setNameProject(duAnTuThien.getTenDuAn());
        charityProjectResponseDTO.setPurpose(duAnTuThien.getMucDich());
        charityProjectResponseDTO.setAddress(duAnTuThien.getDiaDiem());
        if (duAnTuThien.getThoiGianBatDau() != null) {
            charityProjectResponseDTO.setStartTime(F.format(duAnTuThien.getThoiGianBatDau()));
        }

        if (duAnTuThien.getThoiGianKetThuc() != null) {
            charityProjectResponseDTO.setEndTime(F.format(duAnTuThien.getThoiGianKetThuc()));
        }
        charityProjectResponseDTO.setCreateAt(duAnTuThien.getNgayTao());
        charityProjectResponseDTO.setUpdateAt(duAnTuThien.getNgayCapNhat());
        charityProjectResponseDTO.setAmountRaised(duAnTuThien.getSoTienHuyDong());
        List<HinhAnhDuAn> listImage = this.hinhAnhDuAnService.listHinhAnh(duAnTuThien);
        List<ImagePostDTO> imageDTOs = new ArrayList<>();
        listImage.forEach(img -> {
            ImagePostDTO imagePostDTO = new ImagePostDTO();
            imagePostDTO.setLink(img.getDuongDanHinh());

            imageDTOs.add(imagePostDTO);
        });
        charityProjectResponseDTO.setImages(imageDTOs);

        userResponseDTO.setUsername(duAnTuThien.getMaThanhVienTaoDA().getTenDangNhap());
        userResponseDTO.setAvatar(duAnTuThien.getMaThanhVienTaoDA().getAnhDaiDien());
        userResponseDTO.setId(duAnTuThien.getMaThanhVienTaoDA().getMaThanhVien());
        userResponseDTO.setAddress(duAnTuThien.getMaThanhVienTaoDA().getDiaChi());
        userResponseDTO.setCreateAt(duAnTuThien.getMaThanhVienTaoDA().getNgayTao());
        userResponseDTO.setUpdateAt(duAnTuThien.getMaThanhVienTaoDA().getNgayCapNhat());
        userResponseDTO.setDateOfBirth(duAnTuThien.getMaThanhVienTaoDA().getNgaySinh());
        userResponseDTO.setEmail(duAnTuThien.getMaThanhVienTaoDA().getEmail());
        userResponseDTO.setFirstName(duAnTuThien.getMaThanhVienTaoDA().getTen());
        userResponseDTO.setLastName(duAnTuThien.getMaThanhVienTaoDA().getHo());
        userResponseDTO.setPhone(duAnTuThien.getMaThanhVienTaoDA().getSoDienThoai());
        userResponseDTO.setGender(duAnTuThien.getMaThanhVienTaoDA().getGioiTinh());

        charityProjectResponseDTO.setUser(userResponseDTO);

        return new ResponseEntity<>(charityProjectResponseDTO, HttpStatus.OK);
    }
}
