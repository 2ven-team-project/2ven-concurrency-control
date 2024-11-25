package com.sparta.concurrencycontrolproject.domain.member.entity;

import java.util.Arrays;

public enum MemberRole {
	USER(Authority.USER),  // 사용자 권한
	ADMIN(Authority.ADMIN);  // 관리자 권한

	private final String authority;

	MemberRole(String authority) {
		this.authority = authority;
	}

	public String getAuthority() {
		return this.authority;
	}

	public static class Authority {
		public static final String USER = "ROLE_USER";
		public static final String ADMIN = "ROLE_ADMIN";
	}

	public static MemberRole of(String role) {
		return Arrays.stream(MemberRole.values())
				.filter(r -> r.name().equalsIgnoreCase(role))
				.findFirst()
				.orElseThrow(() -> new NullPointerException("유효하지 않은 UserRole"));
	}
}