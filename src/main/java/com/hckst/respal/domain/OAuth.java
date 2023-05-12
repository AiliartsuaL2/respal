package com.hckst.respal.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name="OAUTH")
public class OAuth {
    @Id  @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "OAUTH_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name= "MEMBERS_ID")
    private Members membersId;

    @Column(length = 100)
    private String accessToken;

    @Column(length = 20)
    private String socialType;
}
