package com.oppsci.ngraphstore.web.sec;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;

public class BCryptPasswordEncoder implements PasswordEncoder {

	private int rounds;

	public BCryptPasswordEncoder(int rounds) {
		this.rounds = rounds;
	}

	@Override
	public String encode(CharSequence rawPassword) {
		return BCrypt.hashpw(DigestUtils.sha256Hex(rawPassword.toString()), BCrypt.gensalt(rounds));
	}

	@Override
	public boolean matches(CharSequence rawPassword, String encodedPassword) {
		return BCrypt.checkpw(DigestUtils.sha256Hex(rawPassword.toString()), encodedPassword);
	}
}