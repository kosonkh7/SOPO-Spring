package com.ai.pj.controller;

import com.ai.pj.domain.User;
import com.ai.pj.dto.ResponseDTO;
import com.ai.pj.dto.UserDTO;
import com.ai.pj.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/public")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/signUp")
    public String loadSignUpPage(Model model) {
        System.out.println(1);
        model.addAttribute("title", "회원가입");
        model.addAttribute("labels", Map.of(
                "username", "사용자 이름",
                "email", "이메일",
                "password", "비밀번호"
        ));
        model.addAttribute("buttonText", "가입하기");
        return "user/signUpHome";
    }

    @PostMapping("/signUp")
    @ResponseBody   // 화면도 같이 사용하기 때문에 @RestController 대신 @ResponseBody 사용
    public ResponseDTO<?> signUp(@Valid @RequestBody UserDTO userDTO, BindingResult bindingResult) throws JsonProcessingException {
        System.out.println(2);
        if (bindingResult.hasErrors()) {
            Map<String, String> errorMap = new HashMap<>();
            for (FieldError fieldError : bindingResult.getFieldErrors()) {
                errorMap.put("error-" + fieldError.getField(), fieldError.getDefaultMessage());
            }
            return new ResponseDTO<>(errorMap, HttpStatus.BAD_REQUEST);
        }

        boolean isExist = userService.isExistId(userDTO.getId());
        if (isExist) {
            return new ResponseDTO<>("동일한 id 가 존재합니다", HttpStatus.CONFLICT); // 409 상태코드
        }


        System.out.println(userDTO);
        User savedUser = userService.save(userDTO);
        return new ResponseDTO<>(savedUser, HttpStatus.OK);
    }

    @GetMapping("/login")
    public String reqLogin() {
        return "user/login";
    }
}
