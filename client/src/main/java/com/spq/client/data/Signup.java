package com.spq.client.data;

public record Signup(
	String email,
    String password,
    String username,
    String name,
    String surname
    ) {}