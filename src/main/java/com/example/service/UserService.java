package com.example.service;

import com.example.model.User;

import java.util.List;

public interface UserService {
	public User findUserByEmail(String email);
	public User findUserByID(long id);
	public void saveUser(User user);
	List<User> findAll();
	public void deleteUser(long uid);
	public void updateUser(long uid);

}
