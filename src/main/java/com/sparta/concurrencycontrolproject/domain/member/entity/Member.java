package com.sparta.concurrencycontrolproject.domain.member.entity;

import com.sparta.concurrencycontrolproject.domain.auth.dto.AuthMember;
import com.sparta.concurrencycontrolproject.domain.common.entity.Timestamped;
import com.sparta.concurrencycontrolproject.domain.ticket.entity.Ticket;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "members")
public class Member extends Timestamped {
	@Id
	@GeneratedValue (strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String email;

	@Column
	private String name;

	@Column
	private String password;

	@Enumerated(EnumType.STRING)
	private MemberRole role;

	@OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
	private List<Ticket> tickets = new ArrayList<>();

	public Member(String email, String name, String password, MemberRole role) {
		this.email = email;
		this.name = name;
		this.password = password;
		this.role = role;
	}

	private Member(Long id, String email, MemberRole memberRole) {
		this.id = id;
		this.email = email;
		this.role = memberRole;
	}

	public static Member fromAuthMember(AuthMember authUser) {
		return new Member(authUser.getId(), authUser.getEmail(), authUser.getUserRole());
	}

	public void changePassword(String password) {
		this.password = password;
	}

	public void updateRole(MemberRole memberRole) {
		this.role = memberRole;
	}
}
