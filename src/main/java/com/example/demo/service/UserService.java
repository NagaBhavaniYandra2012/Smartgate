package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));
    }

    public User register(User user) {
        if (user.getRoleNumber() == null || user.getRoleNumber().isBlank()) {
            throw new IllegalArgumentException("Role number is required");
        }
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (user.getPassword() == null || user.getPassword().isBlank()) {
            throw new IllegalArgumentException("Password is required");
        }
        if (userRepository.findByRoleNumber(user.getRoleNumber()).isPresent()) {
            throw new IllegalArgumentException("Role number already registered: " + user.getRoleNumber());
        }
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already registered: " + user.getEmail());
        }
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User login(User user) {

        if (user.getUsername() == null || user.getUsername().isBlank()
                || user.getPassword() == null || user.getPassword().isBlank()) {
            throw new IllegalArgumentException("Invalid Username or Password!");
        }
    
        return userRepository.findByRoleNumber(user.getUsername())
                .filter(dbUser -> dbUser.getPassword() != null
                        && dbUser.getPassword().equals(user.getPassword()))
                .orElseThrow(() -> new IllegalArgumentException("Invalid Username or Password!"));
    }
    public User updateProfile(Long id, User profile) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));
    
        user.setName(profile.getName());
        user.setRoleNumber(profile.getRoleNumber());
        user.setDepartment(profile.getDepartment());
        user.setHostelBlock(profile.getHostelBlock());
        user.setRoomNumber(profile.getRoomNumber());
        user.setPhoneNumber(profile.getPhoneNumber());
        user.setFatherName(profile.getFatherName());
        user.setFatherPhone(profile.getFatherPhone());
        user.setMotherPhone(profile.getMotherPhone());
        user.setParentEmail(profile.getParentEmail());
    
        return userRepository.save(user);
    }
}
