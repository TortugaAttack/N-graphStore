package com.oppsci.ngraphstore.web.role;


public class Role {


	 private int id;
	

	 private String roleName; 
	 

	public Role(int id, String roleName) {
		this.id=id;
		this.roleName=roleName;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the RoleName
	 */
	public String getRoleName() {
		return roleName;
	}

	/**
	 * @param RoleName the RoleName to set
	 */
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}


	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Role) {
			return this.roleName.equals(((Role) obj).getRoleName());
		}
		return false;
	}
	
}
