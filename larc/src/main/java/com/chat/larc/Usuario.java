package com.chat.larc;

/**
 * Classe que inicia a aplicação.
 * 
 * @author Ana
 * @author Luigi
 * @author Rodrigo
 * @author Rossana
 */
public class Usuario {

	private String userid;
	private String username;
	private String wins;

	public Usuario(String userid, String username, String wins) {
		super();
		this.userid = userid;
		this.username = username;
		this.wins = wins;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getWins() {
		return wins;
	}

	public void setWins(String wins) {
		this.wins = wins;
	}

}
