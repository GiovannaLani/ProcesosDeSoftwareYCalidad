package com.spq.client.web;

import com.spq.client.data.User;

public interface IVintedServiceProxy {
    public void createUser(User user);
	public Long login(String email, String password);
	public void logout(long token);
}
