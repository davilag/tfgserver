package myapp;

public class Globals {
	//Constantes para los campos de los mensajes con el servidor GCM
    public static final String MSG_ACTION="action";
    public static final String MSG_ROLE="role";
    public static final String MSG_MAIL="mail";
    public static final String MSG_DOMAIN = "dominio";
    public static final String MSG_PASSWD = "password";
    public static final String MSG_REG_ID = "reg_id";
    public static final String MSG_REQ_ID = "req_id";
    public static final String GCM_URL = "https://android.googleapis.com/gcm/send";

    //Constantes para las acciones de GCM
    public static final String ACTION_REGISTER = "register";
    public static final String ACTION_CONTAINER = "container";
    public static final String ACTION_REQUESTER = "requester";
    public static final String ACTION_REGISTERED = "registered";
    public static final String ACTION_REQUEST = "request";
    public static final String ACTION_RESPONSE = "response";
    public static final String ACTION_CLEARNOTIF = "clear_notification";
    
    public static final String CONTAINERS_IDS = "containers_ids";
    public static final String REQUESTER_IDS = "requester_ids";
}
