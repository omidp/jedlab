package com.jedlab.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.jedlab.framework.StringUtil;

@NamedQueries({
        @NamedQuery(name = LoginActivity.FIND_LOGIN_ACTIVITY_BY_USER_NAME, query = "select la from LoginActivity la where la.username = ? and la.token is not null"),
        @NamedQuery(name = LoginActivity.FIND_LAST_ACTIVITY, query = "select la.lastUsed from LoginActivity la where la.username = ? and la.token is null order by la.createdAt desc"),
        @NamedQuery(name = LoginActivity.UPDATE_LAST_USED, query = "update LoginActivity la set lastUsed = ? where la.token = ?"),
        @NamedQuery(name = LoginActivity.UPDATE_LOGOUT_ALL_SAME_LOGEDIN_USERS, query = "update LoginActivity la set la.token = null, la.ttl = now() where la.token is not null AND (la.username = ? OR la.lastUsed <= ?)"),
        @NamedQuery(name = LoginActivity.ONLINE_USERS, query = "select count(*) from LoginActivity la where la.token is not null"),
        @NamedQuery(name = LoginActivity.FETCH_ACTIVE_USERS, query = "select la from LoginActivity la where la.token is not null and la.lastUsed >= ?") })
@Entity
@Table(name = "login_activity")
public class LoginActivity extends BasePO
{

    public static final int TTL = 55;
    
    public static final String TOKEN = "token";
    public static final String FIND_LOGIN_ACTIVITY_BY_USER_NAME = "users.findLoginActivityByUserName";
    public static final String UPDATE_LOGOUT_ALL_SAME_LOGEDIN_USERS = "users.updateLogoutAllSameLogedInUsers";
    public static final String FETCH_ACTIVE_USERS = "users.fetchActiveUsers";
    public static final String UPDATE_LAST_USED = "users.updateLastUsed";
    public static final String ONLINE_USERS = "users.onlineUsers";
    public static final String FIND_LAST_ACTIVITY = "users.findLastActivity";

    @Column(name = "username")
    private String username;
    
    @Column(name = "last_used")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUsed;
    
    @Column(name = "ip_address")
    private String ipAddress;
    
    @Column(name = "ttl")
    @Temporal(TemporalType.TIMESTAMP)
    private Date ttl;
    
    @Column(name = "device")
    private String device;
    
    @Column(name = "created_date", updatable = false, insertable = false, columnDefinition = " timestamp without time zone DEFAULT now()")
    @Temporal(TemporalType.TIMESTAMP)
    @org.hibernate.annotations.Generated(org.hibernate.annotations.GenerationTime.INSERT)
    private Date createdAt;
    
    @Column(name = "token")
    private String token;

    public LoginActivity()
    {
    }

    public LoginActivity(String username, Date lastUsed, String ipAddress, Date ttl, String device, Date createdAt, String token)
    {
        this.username = username;
        this.lastUsed = lastUsed;
        this.ipAddress = ipAddress;
        this.ttl = ttl;
        this.device = device;
        this.createdAt = createdAt;
        this.token = token;
    }

    
    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    
    public Date getLastUsed()
    {
        return lastUsed;
    }

    public void setLastUsed(Date lastUsed)
    {
        this.lastUsed = lastUsed;
    }

    
    public String getIpAddress()
    {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress)
    {
        this.ipAddress = ipAddress;
    }

    
    public Date getTtl()
    {
        return ttl;
    }

    public void setTtl(Date ttl)
    {
        this.ttl = ttl;
    }

    
    public String getDevice()
    {
        return device;
    }

    public void setDevice(String device)
    {
        this.device = device;
    }

    
    public Date getCreatedAt()
    {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt)
    {
        this.createdAt = createdAt;
    }

    
    public String getToken()
    {
        return token;
    }

    public void setToken(String token)
    {
        this.token = token;
    }

    @Transient
    public boolean isFailedLogin()
    {
        return StringUtil.isEmpty(getToken()) && getLastUsed() == null;
    }

}
