package org.sakaiproject.gradebook.gwt.sakai.model;

import java.io.Serializable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

public class RealmRlGroupId implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8372528613724080135L;
	private Long realmKey;
	private Long roleKey;
	private String userId;
	
	
	public Long getRealmKey() {
		return realmKey;
	}
	public void setRealmKey(Long realmKey) {
		this.realmKey = realmKey;
	}
	public Long getRoleKey() {
		return roleKey;
	}
	public void setRoleKey(Long roleKey) {
		this.roleKey = roleKey;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	public String toString() {
		return new ToStringBuilder(this)
		.append("realmKey", realmKey)
		.append("roleKey", roleKey)
		.append("userId", userId)
		.toString();
		
	}
	
	public int hashCode() {
		return new HashCodeBuilder(17, 37)
		  .append(userId)
		  .append(roleKey)
		  .append(realmKey)
		.toHashCode();
		}
	
	public boolean equals(Object o) {
		if (o == null) { return false; }
		if (o == this) { return true; }
		if (o.getClass() != getClass()) {
			return false;
		}
		RealmGroup other = (RealmGroup)o;
		return new EqualsBuilder()
		//.appendSuper(super.equals(o))
		.append(userId, other.getUserId())
		.append(roleKey, other.getRoleKey())
		.append(realmKey, other.getRealmKey())
		.isEquals();
		
	}
	
	

}
