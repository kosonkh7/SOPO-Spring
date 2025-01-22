package com.ai.pj.service;

import com.ai.pj.domain.User;
import com.ai.pj.dto.UserDTO;
import com.ai.pj.mapper.UserMapper;
import com.ai.pj.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;


    @Transactional(readOnly = true)
    public List<UserDTO.Get> findByRole(User.UserRole role) {
        List<User> userList = userRepository.findByRole(role)
                .orElseThrow(() -> new NullPointerException("No User match the role : "));

        List<UserDTO.Get> userDTOList = userList.stream()
                .map(userMapper::EntityToGET)
                .collect(Collectors.toList());

        return userDTOList;
    }

}
