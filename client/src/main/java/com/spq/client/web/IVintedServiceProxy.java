package com.spq.client.web;

import org.springframework.web.multipart.MultipartFile;

import com.spq.client.data.Signup;
import com.spq.client.data.User;

public interface IVintedServiceProxy {
    public void createUser(Signup user);
	public Long login(String email, String password);
	public void logout(long token);
	public void deleteUser(long token);
	public void updateUser(long token, String name, String surname, String description, MultipartFile profileImage);
	public User getUser(long id, long token);
	public Long getUserIdFromToken(Long token);
}
