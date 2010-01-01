/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.filterlogic.OpenCapture.data;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 *
 * @author dnesbitt
 */
@Entity
@Table(name = "USERS")
@NamedQueries({@NamedQuery(name = "Users.findByUserId", query = "SELECT u FROM Users u WHERE u.userId = :userId"), @NamedQuery(name = "Users.findByUserNm", query = "SELECT u FROM Users u WHERE u.userNm = :userNm"), @NamedQuery(name = "Users.findByPassword", query = "SELECT u FROM Users u WHERE u.password = :password"), @NamedQuery(name = "Users.findByAuthType", query = "SELECT u FROM Users u WHERE u.authType = :authType"), @NamedQuery(name = "Users.findByFirstNm", query = "SELECT u FROM Users u WHERE u.firstNm = :firstNm"), @NamedQuery(name = "Users.findByLastNm", query = "SELECT u FROM Users u WHERE u.lastNm = :lastNm"), @NamedQuery(name = "Users.findByDescr", query = "SELECT u FROM Users u WHERE u.descr = :descr")})
public class Users implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "USER_ID", nullable = false)
    private Long userId;
    @Column(name = "USER_NM", nullable = false)
    private String userNm;
    @Column(name = "PASSWORD", nullable = false)
    private String password;
    @Column(name = "AUTH_TYPE", nullable = false)
    private String authType;
    @Column(name = "FIRST_NM")
    private String firstNm;
    @Column(name = "LAST_NM")
    private String lastNm;
    @Column(name = "DESCR")
    private String descr;

    public Users() {
    }

    public Users(Long userId) {
        this.userId = userId;
    }

    public Users(Long userId, String userNm, String password, String authType) {
        this.userId = userId;
        this.userNm = userNm;
        this.password = password;
        this.authType = authType;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserNm() {
        return userNm;
    }

    public void setUserNm(String userNm) {
        this.userNm = userNm;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAuthType() {
        return authType;
    }

    public void setAuthType(String authType) {
        this.authType = authType;
    }

    public String getFirstNm() {
        return firstNm;
    }

    public void setFirstNm(String firstNm) {
        this.firstNm = firstNm;
    }

    public String getLastNm() {
        return lastNm;
    }

    public void setLastNm(String lastNm) {
        this.lastNm = lastNm;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (userId != null ? userId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Users)) {
            return false;
        }
        Users other = (Users) object;
        if ((this.userId == null && other.userId != null) || (this.userId != null && !this.userId.equals(other.userId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "net.filterlogic.OpenCapture.data.Users[userId=" + userId + "]";
    }

}
