/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author vuongthai1205
 */
@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {
    private int id;
    private String username;
    private String avatar;
    private String phone;
    private String email;
    private String firstName;
    private String lastName;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private Date dateOfBirth;
    private Short gender;
    private String address;
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private Date createAt;
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private Date updateAt;
}
