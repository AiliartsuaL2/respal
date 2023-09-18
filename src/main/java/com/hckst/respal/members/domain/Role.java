package com.hckst.respal.members.domain;

import com.hckst.respal.converter.RoleType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;
@Entity
@Table(name="roles")
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Role {
    @Id
    @Column(name = "ROLE_ID")
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;

    //권한
    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private RoleType roles;

    @ManyToMany(mappedBy="roles")
    private List<Members> membersList;

    public Role(RoleType roles) {
        this.roles = roles;
    }

}
