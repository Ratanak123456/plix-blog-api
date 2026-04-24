package co.istad.blogapplication.blog.service;

import co.istad.blogapplication.blog.dto.response.UserResponse;
import java.util.List;
import java.util.UUID;

public interface UserService {
    List<UserResponse> getAllUsers();
    UserResponse getUserById(UUID id);
    void deleteUser(UUID id);
}