package es.david.ptc.util;

public class Globals {
	//Constantes para los campos de los mensajes con el servidor GCM
    public static final String MSG_ACTION="action";
    public static final String MSG_ROLE="role";
    public static final String MSG_MAIL="mail";
    public static final String MSG_DOMAIN = "dominio";
    public static final String MSG_PASSWD = "password";
    public static final String MSG_REG_ID = "reg_id";
    public static final String MSG_REQ_ID = "req_id";
    public static final String MSG_SERVER_KEY="serverKey";
    public static final String MSG_USER = "usuario";
    public static final String MSG_SAVED_PASS = "saved";
    public static final String GCM_URL = "https://android.googleapis.com/gcm/send";

    //Constantes para las acciones de GCM
    public static final String ACTION_REGISTER = "register";
    public static final String ACTION_CONTAINER = "container";
    public static final String ACTION_REQUESTER = "requester";
    public static final String ACTION_REGISTERED = "registered";
    public static final String ACTION_REQUEST = "request";
    public static final String ACTION_RESPONSE = "response";
    public static final String ACTION_CLEARNOTIF = "clear_notification";
    public static final String ACTION_GET_URLS = "get_urls";
    public static final String ACTION_SAVE_PASS = "save_pass";
    public static final String ACTION_ADD_PASS = "addpass";
    
    public static final String CONTAINERS_IDS = "containers_ids";
    public static final String REQUESTER_IDS = "requester_ids";
    
    public static final String fichRegistered = "/tmp/users-REST.json";
	public static final String fichRequests = "/tmp/requests-REST.json";
	
	//Tiempo de espera de una peticion de la extensión
	public static final long REQUEST_TIMEOUT = 1000*60*1; //El timeout lo he puesto en 1 min.
}
