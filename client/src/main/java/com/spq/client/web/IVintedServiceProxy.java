package com.spq.client.web;

import com.spq.client.data.User;
import com.spq.client.data.Purchase;

public interface IVintedServiceProxy {
    public void createUser(User user);
	public Long login(String email, String password);
	public void logout(long token);
	public void createPurchase(Purchase purchase);
	public boolean processPayment(long purchaseId, String paymentMethod);
}
