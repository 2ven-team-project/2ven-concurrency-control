package com.sparta.concurrencycontrolproject.domain.member.entity;

import java.util.ArrayList;
import java.util.List;

import com.sparta.concurrencycontrolproject.domain.common.entity.Timestamped;
import com.sparta.concurrencycontrolproject.domain.ticket.entity.Ticket;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

	@Column
	private MemberRole role;

	@OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
	private List<Ticket> tickets = new ArrayList<>();

	public Member(String email, String name, String password, MemberRole role) {
		this.email = email;
		this.name = name;
		this.password = password;
		this.role = role;
	}
}
