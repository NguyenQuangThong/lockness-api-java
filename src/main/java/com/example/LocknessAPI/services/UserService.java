package com.example.LocknessAPI.services;

import com.example.LocknessAPI.models.User;
import com.example.LocknessAPI.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Random;

@Service
public class UserService {

    private final UserRepository usersRepository;

    @Autowired
    public UserService(UserRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    // Create a new user
    public User createUser(User user) {
        if (user.getReferralCode() == null || user.getReferralCode().isEmpty()) {
            user.setReferralCode(generateUniqueReferralCode());
        }
        return usersRepository.save(user);
    }

//    // Find a user by id
//    public Optional<User> getUserById(String id) {
//        return userRepository.findById(id);
//    }
//
//    // Find a user by email
//    public User getUserByEmail(String email) {
//        return userRepository.findByEmail(email);
//    }
//
    // Get all users
    public List<User> getAllUsers() {
        return usersRepository.findAll();
    }
//
//    // Update user information
//    public User updateUser(String id, User updatedUser) {
//        if (userRepository.existsById(id)) {
//            updatedUser.setId(id);
//            return userRepository.save(updatedUser);
//        } else {
//            return null; // Or throw exception if user not found
//        }
//    }
//
//    // Delete a user by id
//    public boolean deleteUser(String id) {
//        if (userRepository.existsById(id)) {
//            userRepository.deleteById(id);
//            return true;
//        } else {
//            return false;
//        }
//    }
    private String generateUniqueReferralCode() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        String code;

        // Lặp cho đến khi tìm được một mã không trùng
        do {
            StringBuilder codeBuilder = new StringBuilder();
            for (int i = 0; i < 6; i++) { // Referral code dài 6 ký tự
                codeBuilder.append(characters.charAt(random.nextInt(characters.length())));
            }
            code = codeBuilder.toString();
        } while (usersRepository.existsByReferralCode(code)); // Kiểm tra trùng lặp

        return code;
    }

}
