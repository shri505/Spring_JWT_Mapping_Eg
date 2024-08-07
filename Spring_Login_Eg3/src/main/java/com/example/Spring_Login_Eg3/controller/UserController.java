package com.example.Spring_Login_Eg3.controller;

import com.example.Spring_Login_Eg3.entity.Course;
import com.example.Spring_Login_Eg3.entity.UserInfo;
import com.example.Spring_Login_Eg3.repository.CourseRepository;
import com.example.Spring_Login_Eg3.repository.UserInfoRepository;
import com.example.Spring_Login_Eg3.config.AuthenticationRequest;
import com.example.Spring_Login_Eg3.service.JwtService;
import com.example.Spring_Login_Eg3.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    @Qualifier("userDetailsService")
    private UserInfoService userService;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserInfoRepository userInfoRepository;

    @PostMapping("/addNewUser")
    public String addNewUser(@RequestBody UserInfo userInfo) {
        return userService.addUser(userInfo);
    }

    @PostMapping("/generateToken")
    public String generateToken(@RequestBody AuthenticationRequest authRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return jwtService.generateToken(userDetails.getUsername());
    }

    @PostMapping("/createUser")
    public UserInfo createUser(@RequestBody UserInfo userInfo) {
        return userService.createUser(userInfo);
    }

    @GetMapping("/getUser/{id}")
    public UserInfo getUserById(@PathVariable int id) {
        return userService.getUserById(id);
    }

    @GetMapping("/getAllUsers")
    public List<UserInfo> getAllUsers() {
        return userService.getAllUsers();
    }

    @PutMapping("/updateUser/{id}")
    public UserInfo updateUser(@PathVariable int id, @RequestBody UserInfo userInfo) {
        return userService.updateUser(id, userInfo);
    }

    @DeleteMapping("/deleteUser/{id}")
    public void deleteUser(@PathVariable int id) {
        userService.deleteUser(id);
    }

    @GetMapping("/courses")
    public List<Course> listCourses() {
        return courseRepository.findAll();
    }

    @PostMapping("/enroll")
    public String enrollInCourse(@RequestParam Integer courseId, Principal principal) {
        UserInfo userInfo = userInfoRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new RuntimeException("Course not found"));
        // Assuming UserInfo has a method to add courses
        // userInfo.getCourses().add(course);
        userInfoRepository.save(userInfo);
        return "redirect:/courses";
    }
}
