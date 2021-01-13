package model;

import org.json.simple.JSONObject;

public class User {
	String publicKey;
	String ip;
	String name;
	long id;
	String state;
	public User(String name, int id, String ip, String state, String publicKey) {
		// TODO Auto-generated constructor stub
	}
	
	public User(JSONObject user) {
		// TODO Auto-generated constructor stub
        setName((String)user.get("name"));
        setId((long)user.get("id"));
        setIp((String)user.get("ip"));
        setState((String)user.get("state"));
        setPublicKey((String)user.get("publicKey"));
	}
	
	public User() {
		// TODO Auto-generated constructor stub
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public String getIp() {
		return ip;
	}
	
	public void setIp(String ip) {
		this.ip = ip;
	}
	
	public String getState() {
		return state;
	}
	
	public void setState(String state) {
		this.state = state;
	}
	
	public String getPublicKey() {
		return publicKey;
	}
	
	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}
}
