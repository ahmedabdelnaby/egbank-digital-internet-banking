package com.egbank.digitalinternetbanking.service;

import com.egbank.digitalinternetbanking.model.user.Admin;
import com.egbank.digitalinternetbanking.model.user.Customer;
import com.egbank.digitalinternetbanking.model.user.Employee;
import com.egbank.digitalinternetbanking.model.user.User;
import com.egbank.digitalinternetbanking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> login(String username, String password) {
        return userRepository.findByUsername(username)
                .filter(user -> user.getPassword().equals(password))
                .filter(user -> user.isActive())
                .map(user -> {
                    user.setLoggedIn(true);
                    return userRepository.save(user);
                });
    }

    public void logout(User user) {
        user.setLoggedIn(false);
        userRepository.save(user);
        System.out.println("Logged out.");
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public String createUser(String adminUsername, String username, String password, String name, String userType) {
        return userRepository.findByUsername(adminUsername)
                .filter(admin -> admin instanceof Admin)
                .map(admin -> {
                    if (userRepository.findByUsername(username).isPresent()) return "Username already exists.";
                    if (password.length() < 6) return "Password too short (Must be at least 6 characters).";

                    User newUser = (userType == "EMPLOYEE") ? new Employee() : new Customer();
                    newUser.setUsername(username);
                    newUser.setPassword(password);
                    newUser.setName(name);
                    newUser.setActive(true);
                    userRepository.save(newUser);

                    return userType + " user created successfully: " + username;
                }).orElse("Access denied: Only admins can create users.");
    }

    public String updateUser(String adminUsername, String targetUsername, String newName, String newPassword) {
        return userRepository.findByUsername(adminUsername)
                .filter(admin -> admin instanceof Admin)
                .map(admin -> {
                    Optional<User> targetOpt = userRepository.findByUsername(targetUsername);
                    if (targetOpt.isEmpty()) return "Target user not found.";

                    User user = targetOpt.get();
                    if (newName != null && !newName.isBlank()) user.setName(newName);
                    if (newPassword != null && newPassword.length() >= 6) user.setPassword(newPassword);
                    userRepository.save(user);
                    return "User '" + targetUsername + "' updated.";
                }).orElse("Access denied. Something went wrong");
    }

    public String switchUser(String adminUsername, String targetUsername, boolean activateUser) {
        return userRepository.findByUsername(adminUsername)
                .filter(admin -> admin instanceof Admin)
                .map(admin -> {
                    Optional<User> targetOpt = userRepository.findByUsername(targetUsername);
                    if (targetOpt.isEmpty()) return "User not found.";
                    User user = targetOpt.get();

                    if (user.isActive() == activateUser) {
                        return "User '" + targetUsername + "' is already " + (activateUser ? "enabled" : "disabled") + ".";
                    }

                    user.setActive(activateUser);
                    userRepository.save(user);

                    return "User '" + targetUsername + "' " + ((activateUser) ? "enabled" : "disabled") + " successfully.";
                }).orElse("Access denied.");
    }

    public String resetPassword(String adminUsername, String targetUsername, String newPassword) {
        Optional<User> user = userRepository.findByUsername(adminUsername);
        if (user.isEmpty() || !(user.get() instanceof Admin))
            return "Access denied: Only admins can reset passwords.";

        return userRepository.findByUsername(targetUsername)
                .map(targetUser -> {
                    if (newPassword.length() < 6) return "Password too short (Must be at least 6 characters).";
                    targetUser.setPassword(newPassword);
                    userRepository.save(targetUser);
                    return "Password reset successfully.";
                })
                .orElse("User not found.");
    }
}