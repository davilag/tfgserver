
public class RequestMessage {
	private String iv;
	private String aad;
	private String payload;
	public String getIv() {
		return iv;
	}
	public void setIv(String iv) {
		this.iv = iv;
	}
	public String getAad() {
		return aad;
	}
	public void setAad(String aad) {
		this.aad = aad;
	}
	public String getPayload() {
		return payload;
	}
	public void setPayload(String payload) {
		this.payload = payload;
	}
}
