package com.example.test.Loader;

import com.example.test.models.Users;
import com.example.test.respositories.UserRespo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.security.crypto.password.PasswordEncoder; // Dùng PasswordEncoder thay vì BCryptPasswordEncoder

@Component
public class DataLoader implements CommandLineRunner {

    private final UserRespo usersRepository;
    private final PasswordEncoder passwordEncoder; // Đổi từ BCryptPasswordEncoder sang PasswordEncoder

    public DataLoader(UserRespo usersRepository, PasswordEncoder passwordEncoder) {
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        Optional<Users> user = usersRepository.findByRole(Users.Role.ADMIN);

        if (user.isEmpty()) {
            Users admin = new Users();
            admin.setFullname("Admin");
            admin.setUsername("Admin");
            admin.setPassword(passwordEncoder.encode("password456"));
            admin.setEmail("Thang@gmail.com");
            admin.setCreated_at(LocalDate.now());
            admin.setRole(Users.Role.ADMIN);
            usersRepository.save(admin);
        }
    }
}