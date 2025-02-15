package com.example.test.services;
import com.example.test.Dto.LoginDto;

import com.example.test.models.Users;
import com.example.test.respositories.UserRespo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRespo userRespo;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRespo userRespo, PasswordEncoder passwordEncoder) {
        this.userRespo = userRespo;
        this.passwordEncoder = passwordEncoder;
    }

    public ResponseEntity<List<Users>>getAll(){
        List<Users> users=userRespo.findAll();
        return new ResponseEntity<>(users,HttpStatus.OK);
    }
    public Optional<Users>getUserId(Long id){
        return userRespo.findById(id);
    }
    @Transactional
    public void deleteUserById(Long id){
        userRespo.deleteById(id);
    }

    @Transactional
    public Users save(Users user) {

        if (userRespo.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists."); // Ném Exception để hủy giao dịch
        }

        //mã hóa password
        if (user.getRole() == null) {
            user.setRole(Users.Role.USER); // Mặc định vai trò USER
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreated_at(LocalDate.now());
        return userRespo.save(user);
    }


    @Transactional
    public ResponseEntity<Users> loginUser(LoginDto loginDto) {
        Optional<Users> userOptional = userRespo.findByUsernameOrEmail(loginDto.getUsername(),loginDto.getUsername());
        System.out.println("Đang tìm người dùng với tên đăng nhập hoặc email: " + userOptional);  // Log thông tin

        if (userOptional.isPresent()) {
            Users user = userOptional.get();
            boolean isPasswordMatch = passwordEncoder.matches(loginDto.getPassword(), user.getPassword());
            if (isPasswordMatch) {
                    return new ResponseEntity<>(user, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    public Optional<Users> getUserByUsername(String username) {
        return userRespo.findByUsername(username);
    }

    public Optional<Users> getUserByEmail(String email) {
        return userRespo.findByEmail(email);
    }


    @Transactional
    public Users update(Long id, Users user) {
        // Tìm người dùng trong cơ sở dữ liệu
        Users existingUser = userRespo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Người dùng không tồn tại"));
        if (user.getFullname() != null) {
            existingUser.setFullname(user.getFullname());
        }
        if (user.getUsername() != null) {
            existingUser.setUsername(user.getUsername());
        }
        if (user.getEmail() != null) {
            existingUser.setEmail(user.getEmail());
        }

        if (user.getPassword() != null && !user.getPassword().isBlank()) {
            // Mã hóa mật khẩu mới
            existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        // Lưu người dùng vào cơ sở dữ liệu
        return userRespo.save(existingUser);
    }

    public Users.Role getRoleByUsername(String username) {
        Users user = userRespo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getRole();  // Trả về role của người dùng
    }

    public String getRoleByEmail(String email) {
        Optional<Users> user = userRespo.findByEmail(email);
        if (user.isPresent()) {
            return user.get().getRole().name(); // Lấy role dưới dạng chuỗi
        }
        throw new RuntimeException("User with email " + email + " not found");
    }

    public String getEmailByUsername(String username) {
        Users user = userRespo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));
        return user.getEmail();
    }

    public boolean changePassword(Long userId, String oldPassword, String newPassword, String confirmPassword) {
        // Kiểm tra mật khẩu mới và mật khẩu xác nhận có trùng nhau không
        if (!newPassword.equals(confirmPassword)) {
            return false; // Mật khẩu xác nhận không khớp
        }

        Optional<Users> userOptional = userRespo.findById(userId);
        if (userOptional.isPresent()) {
            Users user = userOptional.get();

            // Kiểm tra mật khẩu cũ có đúng không
            if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
                return false; // Mật khẩu cũ không đúng
            }

            // Kiểm tra mật khẩu mới có trùng với mật khẩu cũ không
            if (passwordEncoder.matches(newPassword, user.getPassword())) {
                return false; // Mật khẩu mới không được trùng mật khẩu cũ
            }

            // Mã hóa mật khẩu mới và cập nhật
            user.setPassword(passwordEncoder.encode(newPassword));
            userRespo.save(user);

            return true; // Thành công
        }

        return false; // Người dùng không tồn tại
    }






}
