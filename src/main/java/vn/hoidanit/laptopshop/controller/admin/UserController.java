package vn.hoidanit.laptopshop.controller.admin;


import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import vn.hoidanit.laptopshop.domain.User;
import vn.hoidanit.laptopshop.service.UploadService;
import vn.hoidanit.laptopshop.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {

    private final UserService userService;
    private final UploadService uploadService;

    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, UploadService uploadService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.uploadService = uploadService;
        this.passwordEncoder = passwordEncoder;
    }

    @RequestMapping("/")
    public String getHomePage(Model model) {
        List<User> arrUsers = this.userService.getAllUserByEmail("haha@gmail.com");
        System.out.println(arrUsers);
        return "hello";
    }

    @RequestMapping("/admin/user")
    public String getUserPage(Model model) {
        List<User> users = this.userService.getAllUser();
        model.addAttribute("users", users);

        return "admin/user/show";
    }

    @RequestMapping("/admin/user/{id}")
    public String getUserDetailPage(Model model, @PathVariable long id) {
        model.addAttribute("newUser", new User());
        model.addAttribute("id", id);
        User user = this.userService.getUserById(id);
        model.addAttribute("user", user);
        return "/admin/user/detail";
    }

    @GetMapping("/admin/user/create")
    public String getCreateUserPage(Model model) {
        model.addAttribute("newUser", new User());
        return "/admin/user/create";
    }

    @PostMapping("/admin/user/create")
    public String createHomePage(Model model,
            @ModelAttribute("newUser") User user,
            @RequestParam("uploadAvatarFile") MultipartFile file) {
        String avatar = this.uploadService.handleSaveUploadFile(file, "avatar");
        String hashPassword = this.passwordEncoder.encode(user.getPassword());
        user.setPassword(hashPassword);
        user.setAvatar(avatar);
        user.setRole(this.userService.getRoleByName(user.getRole().getName()));
        this.userService.handleSaveUser(user);
        return "redirect:/admin/user";
    }

    @RequestMapping("/admin/user/update/{id}")
    public String getUpdateUserPage(Model model, @PathVariable long id ) {
        User currentUser = this.userService.getUserById(id);
        model.addAttribute("newUser", currentUser);
        model.addAttribute("id", id);
        return "/admin/user/update";
    }

    @PostMapping("/admin/user/update")
    public String PostUpdateUser(Model model, @ModelAttribute("newUser") User user, @RequestParam("uploadAvatarFile") MultipartFile file) {
        User currentUser = this.userService.getUserById(user.getId());
        String avatar = this.uploadService.handleSaveUploadFile(file, "avatar");
        model.addAttribute("newUser", currentUser);
        if (currentUser != null) {
            currentUser.setFullName(user.getFullName());
            currentUser.setAddress(user.getAddress());
            currentUser.setPhoneNumber(user.getPhoneNumber());
            currentUser.setAvatar(avatar);
            currentUser.setRole(this.userService.getRoleByName(user.getRole().getName()));
            this.userService.handleSaveUser(currentUser);
        }
        return "redirect:/admin/user";
    }

    @GetMapping("/admin/user/delete/{id}")
    public String getDeleteUserPage(Model model, @PathVariable long id) {
        model.addAttribute("id", id);
        model.addAttribute("newUser", new User());

        return "admin/user/delete";
    }

    @PostMapping("/admin/user/delete")
    public String postDeleteUser(Model model, @ModelAttribute("newUser") User user) {
        this.userService.deleteAUser(user.getId());

        return "redirect:/admin/user";
    }

}
