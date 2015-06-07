package es.david.ptc.util;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Response {
	private String estado;
	private String passwd;
	private String iv;
	private Long ts;
	private Long nonce;
	private int nresponses;
	
	public Response(){
		super();
	}
	@JsonCreator
	public Response(@JsonProperty(Globals.MSG_STATE)String estado, @JsonProperty(Globals.MSG_PASSWD) String passwd,@JsonProperty(Globals.MSG_IV)String iv, 
			@JsonProperty(Globals.MSG_TS) Long ts, @JsonProperty(Globals.MSG_NONCE) Long nonce) {
		super();
		this.estado = estado;
		this.passwd = passwd;
		this.nresponses = 0;
		this.ts = ts;
		this.nonce = nonce;
		this.iv = iv;
	}
	public String getEstado() {
		return estado;
	}
	public String getPasswd() {
		return passwd;
	}
	public int getNresponses() {
		return nresponses;
	}
	public synchronized void incrementNResponses() {
		this.nresponses++;
	}
	public Long getTs() {
		return ts;
	}
	public Long getNonce() {
		return nonce;
	}
	public String getIv() {
		return iv;
	}
}
