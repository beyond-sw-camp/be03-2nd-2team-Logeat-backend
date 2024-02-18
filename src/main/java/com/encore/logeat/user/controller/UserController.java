package com.encore.logeat.user.controller;

import com.encore.logeat.common.dto.ResponseDto;
import com.encore.logeat.user.domain.User;
import com.encore.logeat.user.dto.request.UserCreateRequestDto;
import com.encore.logeat.user.dto.request.UserInfoResponseDto;
import com.encore.logeat.user.dto.request.UserInfoUpdateRequestDto;
import com.encore.logeat.user.dto.request.UserLoginRequestDto;
import com.encore.logeat.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

	private final UserService userService;

	@Autowired
	public UserController(UserService userService) {
		this.userService = userService;
	}

	@PostMapping("/user/new")
	public ResponseEntity<ResponseDto> createUser(UserCreateRequestDto userCreateRequestDto) {
		User user = userService.createUser(userCreateRequestDto);
		return new ResponseEntity<>(
			new ResponseDto(HttpStatus.CREATED, "new User Created!", user.getId()),
			HttpStatus.CREATED);
	}

	@PostMapping("/doLogin")
	public ResponseEntity<ResponseDto> userLogin(
		@RequestBody UserLoginRequestDto userLoginRequestDto) {
		ResponseDto responseDto = userService.userLogin(userLoginRequestDto);
		return new ResponseEntity<>(responseDto, HttpStatus.OK);
	}

	@GetMapping("/user/myfollower")
	public ResponseEntity<ResponseDto> myFollower() {
		ResponseDto responseDto = userService.getMyFollower();
		return new ResponseEntity<>(responseDto, HttpStatus.OK);
	}
	@GetMapping("/user/myfollowing")
	public ResponseEntity<ResponseDto> myFollowing() {
		ResponseDto responseDto = userService.getMyFollowing();
		return new ResponseEntity<>(responseDto, HttpStatus.OK);
	}

	@GetMapping("/user/mypage")
	public ResponseEntity<ResponseDto> myPage() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String currentUserName = authentication.getName();
		Long userId = Long.parseLong(currentUserName);
		User user = userService.findUserById(userId);
		UserInfoResponseDto userInfo = new UserInfoResponseDto();
		userInfo.setNickname(user.getNickname());
		userInfo.setProfileImage(null);
		userInfo.setIntroduce(user.getIntroduce());
		return new ResponseEntity<>(new ResponseDto(HttpStatus.OK, "User info loaded successfully", userInfo), HttpStatus.OK);
	}

	@PatchMapping("/user/update")
	public ResponseEntity<ResponseDto> updateUserInfo(UserInfoUpdateRequestDto userInfoupdateDto) {
		userService.updateInfoUser(userInfoupdateDto);
		return new ResponseEntity<>(new ResponseDto(HttpStatus.OK, "User updated successfully", userInfoupdateDto.getNickname()), HttpStatus.OK);
	}

}
