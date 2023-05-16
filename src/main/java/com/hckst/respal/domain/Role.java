package com.hckst.respal.domain;

import com.hckst.respal.common.converter.ProviderConverter;
import com.hckst.respal.common.converter.RoleType;
import com.hckst.respal.common.converter.RoleTypeConverter;
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

    @Convert(converter = RoleTypeConverter.class)
    @Column(columnDefinition = "varchar(20)", nullable=false)
    private RoleType roles;

    @ManyToMany(mappedBy="roles")
    private List<Members> membersList;

    public Role(RoleType roles) {
        this.roles = roles;
    }

}
