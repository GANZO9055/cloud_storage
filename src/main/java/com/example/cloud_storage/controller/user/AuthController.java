package com.example.cloud_storage.controller.user;

import com.example.cloud_storage.api.AuthControllerApi;
import com.example.cloud_storage.dto.request.UserRequestDto;
import com.example.cloud_storage.dto.response.UserResponseDto;
import com.example.cloud_storage.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/auth")
public class AuthController implements AuthControllerApi {

    private UserService userService;

    @PostMapping("/sign-up")
    public ResponseEntity<UserResponseDto> registration(@Valid @RequestBody UserRequestDto userRequestDto,
                                                        HttpServletRequest request,
                                                        HttpServletResponse response) {
        UserResponseDto user = userService.create(userRequestDto, request, response);
        return new ResponseEntity<>(
                new UserResponseDto(user.getUsername()),
                HttpStatus.CREATED
        );
    }

    @PostMapping("/sign-in")
    public ResponseEntity<UserResponseDto> authorization(@Valid @RequestBody UserRequestDto userRequestDto,
                                                         HttpServletRequest request,
                                                         HttpServletResponse response) {
        UserResponseDto user = userService.authenticate(userRequestDto, request, response);
        return new ResponseEntity<>(
                new UserResponseDto(user.getUsername()),
                HttpStatus.OK
        );
    }
}
