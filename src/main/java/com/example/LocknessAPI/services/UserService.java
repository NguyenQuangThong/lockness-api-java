package com.example.LocknessAPI.services;

import com.example.LocknessAPI.models.User;
import com.example.LocknessAPI.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository usersRepository;

    @Autowired
    public UserService(UserRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    // Create a new user
    public User createUser(User user) {
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
//    // Get all users
//    public List<User> getAllUsers() {
//        return userRepository.findAll();
//    }
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
}
