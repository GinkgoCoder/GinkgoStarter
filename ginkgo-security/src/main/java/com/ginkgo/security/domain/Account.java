package com.ginkgo.security.domain;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "Users")
@Getter
@Setter
public class Account extends AbstractUuidEntity {
    private String username;
    private String password;
    private String roles;
    private Boolean isValid;
}
