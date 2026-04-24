package co.istad.blogapplication.blog.service.impl;

import co.istad.blogapplication.blog.dto.response.UserResponse;
import co.istad.blogapplication.blog.entity.User;
import co.istad.blogapplication.blog.exception.NotFoundException;
import co.istad.blogapplication.blog.repository.UserRepository;
import co.istad.blogapplication.blog.service.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .filter(user -> !user.isDeleted())
                .map(user -> modelMapper.map(user, UserResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public UserResponse getUserById(UUID id) {
        return userRepository.findById(id)
                .filter(user -> !user.isDeleted())
                .map(user -> modelMapper.map(user, UserResponse.class))
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    @Override
    @Transactional
    public void deleteUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));
        user.setDeleted(true);
        user.setDeletedAt(LocalDateTime.now());
        userRepository.save(user);
    }
}