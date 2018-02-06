package com.example.controller;

import com.example.model.User;
import com.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.List;

@Controller
public class LoginController {
	
	@Autowired
	private UserService userService;
	private int id;

	@RequestMapping(value={"/", "/login"}, method = RequestMethod.GET)
	public ModelAndView login(){
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("login");
		return modelAndView;
	}



	@RequestMapping(value="/registration", method = RequestMethod.GET)
	public ModelAndView registration(){
		ModelAndView modelAndView = new ModelAndView();
		User user = new User();
		modelAndView.addObject("user", user);
		modelAndView.setViewName("registration");
		return modelAndView;
	}

	@RequestMapping(value="/updateUser", method = RequestMethod.GET)
	public ModelAndView updateUser(@RequestParam(value="uid",required=false) String id){
		ModelAndView modelAndView = new ModelAndView();
		User user = userService.findUserByID(Long.parseLong(id));
		modelAndView.addObject("user", user);
		modelAndView.setViewName("updateUser");
		return modelAndView;
	}

	@RequestMapping(value="/updateUser", method = RequestMethod.POST)
	public ModelAndView updateUser(@Valid User user, BindingResult bindingResult){
		ModelAndView modelAndView = new ModelAndView();
		User userExists = userService.findUserByEmail(user.getEmail());

		if (bindingResult.hasErrors()) {
			modelAndView.setViewName("registration");
		} else {
			userService.deleteUser(userExists.getId());
			userService.saveUser(user);
			List<User> userList=userService.findAll();
			modelAndView.addObject("successMessage", "User has been updated successfully");
			modelAndView.addObject("user", user);
			modelAndView.addObject("userList", userList);
			modelAndView.setViewName("admin/home");

		}
		return modelAndView;
	}


	
	@RequestMapping(value = "/registration", method = RequestMethod.POST)
	public ModelAndView createNewUser(@Valid User user, BindingResult bindingResult) {
		ModelAndView modelAndView = new ModelAndView();
		User userExists = userService.findUserByEmail(user.getEmail());
		if (userExists != null) {
			bindingResult
					.rejectValue("email", "error.user",
							"There is already a user registered with the email provided");
		}
		if (bindingResult.hasErrors()) {
			modelAndView.setViewName("registration");
		} else {
			userService.saveUser(user);
			modelAndView.addObject("successMessage", "User has been registered successfully");
			modelAndView.addObject("user", new User());
			modelAndView.setViewName("registration");
			
		}
		return modelAndView;
	}
	
	@RequestMapping(value="/admin/home", method = RequestMethod.GET)
	public ModelAndView home(){
		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByEmail(auth.getName());
		List<User> userList=userService.findAll();
		modelAndView.addObject("userName", "Welcome " + user.getName() + " " + user.getLastName() + " (" + user.getEmail() + ")");
		modelAndView.addObject("adminMessage","Content Available Only for Users with Admin Role");
		modelAndView.addObject("userList", userList);
		modelAndView.setViewName("admin/home");
		return modelAndView;
	}

	/*
	 * This method will delete an employee by it's SSN value.
	 */
	@RequestMapping(value = { "/deleteUser" }, method = RequestMethod.GET)
	public ModelAndView deleteUser(@RequestParam(value="id",required=false) String id) {
		ModelAndView modelAndView = new ModelAndView();
		userService.deleteUser(Integer.parseInt(id));

		modelAndView.setViewName("admin/home");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByEmail(auth.getName());
		List<User> userList=userService.findAll();
		modelAndView.addObject("userName", "Welcome " + user.getName() + " " + user.getLastName() + " (" + user.getEmail() + ")");
		modelAndView.addObject("adminMessage","Content Available Only for Users with Admin Role");
		modelAndView.addObject("userList", userList);
		modelAndView.setViewName("admin/home");
		return modelAndView;
	}
}
