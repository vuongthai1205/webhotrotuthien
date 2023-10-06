/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.controllers;

import com.mycompany.DTO.AuthenticationRequestDTO;
import com.mycompany.DTO.AuthenticationResponseDTO;
import com.mycompany.DTO.UserRequestDTO;
import com.mycompany.DTO.UserResponseDTO;
import com.mycompany.components.JwtService;
import com.mycompany.pojo.Quyen;
import com.mycompany.pojo.ThanhVien;
import com.mycompany.service.QuyenService;
import com.mycompany.service.ThanhVienService;
import java.security.Principal;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author vuongthai1205
 */
@RestController
@CrossOrigin
@RequestMapping("/api")
public class ApiThanhVienController {
    
    @Autowired
    private JwtService jwtService;
    @Autowired
    private ThanhVienService thanhVienService;
    
    @Autowired
    private QuyenService quyenService;
    @PostMapping("/login/")
    public ResponseEntity<AuthenticationResponseDTO> login(@RequestBody AuthenticationRequestDTO authenticationRequest) {
        if (this.thanhVienService.authUser(authenticationRequest.getUsername(), authenticationRequest.getPassword()) == true) {
            String token = this.jwtService.generateTokenLogin(authenticationRequest.getUsername());
            return new ResponseEntity<>(new AuthenticationResponseDTO(token), HttpStatus.OK);
        }

        return new ResponseEntity<>(new AuthenticationResponseDTO("error"), HttpStatus.BAD_REQUEST);
    }
    
    @GetMapping("/users")
    public ResponseEntity<List<ThanhVien>> getUsers(@RequestParam Map<String, String> params) {
        return new ResponseEntity<>(this.thanhVienService.getListUser(params), HttpStatus.OK);
    }
    
    @GetMapping("/user/{id}/")
    public ResponseEntity<?> getUser(@PathVariable(value = "id") int id) {
        try {
            ThanhVien user = this.thanhVienService.getUserById(id);
            UserResponseDTO userResponseDTO = new UserResponseDTO();
            userResponseDTO.setUsername(user.getUsername());
            userResponseDTO.setAvatar(user.getAnhDaiDien());
            userResponseDTO.setId(user.getMaThanhVien());
            userResponseDTO.setAddress(user.getDiaChi());
            userResponseDTO.setCreateAt(user.getNgayTao());
            userResponseDTO.setUpdateAt(user.getNgayCapNhat());
            userResponseDTO.setDateOfBirth(user.getNgaySinh());
            userResponseDTO.setEmail(user.getEmail());
            userResponseDTO.setFirstName(user.getTen());
            userResponseDTO.setLastName(user.getHo());
            userResponseDTO.setPhone(user.getSoDienThoai());
            userResponseDTO.setGender(user.getGioiTinh());
            return new ResponseEntity<>(userResponseDTO, HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>("loi", HttpStatus.BAD_REQUEST);
        }

    }

    @DeleteMapping("/user/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable(value = "id") int id) {
        this.thanhVienService.deleteUser(id);
    }
    
    @PostMapping("/user/{id}/")
    @ResponseStatus(HttpStatus.OK)
    public void editUser(@PathVariable(value = "id" ) int id,@RequestBody UserRequestDTO userRequestDTO ){
        ThanhVien user = this.thanhVienService.getUserById(id);
        user.setTenDangNhap(userRequestDTO.getUsername());
        user.setAnhDaiDien(userRequestDTO.getAvatar());
        user.setSoDienThoai(userRequestDTO.getPhone());
        user.setEmail(userRequestDTO.getEmail());
        user.setTen(userRequestDTO.getFirstName());
        user.setHo(userRequestDTO.getLastName());
        user.setNgaySinh(userRequestDTO.getDateOfBirth());
        user.setGioiTinh(userRequestDTO.getGender());
        user.setDiaChi(userRequestDTO.getAddress());
        
        this.thanhVienService.addOrUpdateUser(user);
    }

    @PostMapping(path = "/user/add/")
    @CrossOrigin
    @ResponseStatus(HttpStatus.CREATED)
    public void createUser(@RequestBody ThanhVien user) {
        // Lưu thông tin user vào userService
        Quyen role = this.quyenService.getRole(3);
        user.addRole(role);
        this.thanhVienService.addOrUpdateUser(user);
    }

    @GetMapping(path = "/current-user/", produces = MediaType.APPLICATION_JSON_VALUE)
    @CrossOrigin
    public ResponseEntity<ThanhVien> details(Principal user) {
        ThanhVien u = this.thanhVienService.getUserByUsername(user.getName());
        return new ResponseEntity<>(u, HttpStatus.OK);
    }
    
}
