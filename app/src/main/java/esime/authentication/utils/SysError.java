package esime.authentication.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by DCMir on 20/11/17.
 */

public class SysError {
    public static final int _SUCCESS = 0;
    public static final int _USER_ERROR_NOT_EXISTS = 1;
    public static final int _USER_ERROR_ISSUE_SAVING_USER = 2;
    public static final int _USER_ERROR_NOT_MATCH_PASSWD = 3;
    public static final int _USER_ERROR_UPDATING_ACTIVE = 4;
    public static final int _USER_ERROR_DELETING = 5;

    public static final int _CREDENTIAL_ERROR_NOT_EXISTS = 21;
    public static final int _CREDENTIAL_ERROR_UPDATING_PASSWD = 22;
    public static final int _CREDENTIAL_ERROR_UPDATING_FINGERPRINT = 23;
    public static final int _CREDENTIAL_ERROR_UPDATING_QRHASH = 24;
    public static final int _CREDENTIAL_ERROR_NONE_QRHASH = 25;
    public static final int _CREDENTIAL_ERROR_NOT_MATCH_QRHASH = 26;
    public static final int _CREDENTIAL_SYSTEM_TRACK_CLEANING_ERROR = 27;
    public static final int _CREDENTIAL_FINGERPRINT_NOT_VALIDATED = 28;
    public static final int _CREDENTIAL_FINGERPRINT_ERROR_VERIFYING = 29;
    public static final int _CREDENTIAL_ERROR_MAKING_OPERATION = 30;

    public static final int _SYSTEMTRACK_ERROR_NOT_EXISTS = 31;
    public static final int _SYSTEMTRACK_ERROR_UPDATING_OPERATION = 32;
    public static final int _SYSTEMTRACK_ERROR_UPDATING_OPERATION_STATE = 33;
    public static final int _SYSTEMTRACK_ERROR_UPDATING_ERROR_CODE = 34;
    public static final int _SYSTEMTRACK_ERROR_COMPLETING_OR_CANCEL_OPERATION = 35;
    public static final int _SYSTEMTRACK_ERROR_OP_CONFLICT = 36;
    public static final int _SYSTEMTRACK_ERROR_DIFERENT_OP_DETECTED = 37;
    public static final int _SYSTEMTRACK_ERROR_OPERATION_NOT_DEFINE = 38;
    public static final int _SYSTEMTRACK_NOT_OPERATION_IN_PROGRESS = 39;

    public static final int _WEBSERVICE_CONTROLLER_NOT_FOUND = 41;
    public static final int _WEBSERVICE_METHOD_NOT_FOUND = 42;
    public static final int _WEBSERVICE_METHOD_NOT_MATCH_SIGNATURE = 43;

    public static final int _ENCRYPTION_ERROR_FAIL_ENCRYPT = 51;
    public static final int _ENCRYPTION_ERROR_FAIL_DECRYPT_GET_PRIVATE_KEY = 52;
    public static final int _ENCRYPTION_ERROR_FAIL_DECRYPT = 53;

    public static final int _EMAIL_ERROR_CAN_NOT_BE_SEND = 61;

    public static final Map<Integer, String> systemErr;
    static {
        systemErr = new HashMap<Integer, String>();
        systemErr.put(_USER_ERROR_NOT_EXISTS,  "Usuario no existe");
        systemErr.put(_USER_ERROR_ISSUE_SAVING_USER,  "El usuario ya existe");
        systemErr.put(_USER_ERROR_NOT_MATCH_PASSWD,  "Password incorrecto");
        systemErr.put(_USER_ERROR_UPDATING_ACTIVE,  "Error mientras se actualizaba el usuario");
        systemErr.put(_USER_ERROR_DELETING,  "Error mientras se eliminando usuario");
        systemErr.put(_CREDENTIAL_ERROR_NOT_EXISTS,  "Credenciales de autenticacion no existe");
        systemErr.put(_CREDENTIAL_ERROR_UPDATING_PASSWD,  "Error mientras se actualizaba el password");
        systemErr.put(_CREDENTIAL_ERROR_UPDATING_FINGERPRINT,  "Error mientras se actualizaba huella digital");
        systemErr.put(_CREDENTIAL_ERROR_UPDATING_QRHASH,  "Error mientras se actualiza el codigo QR");
        systemErr.put(_CREDENTIAL_ERROR_NONE_QRHASH,  "No hay un codigo QR registrado");
        systemErr.put(_CREDENTIAL_ERROR_NOT_MATCH_QRHASH,  "Codigo QR es diferente");
        systemErr.put(_CREDENTIAL_SYSTEM_TRACK_CLEANING_ERROR,  "Error mientras se estaba limpiando la operacion actual");
        systemErr.put(_CREDENTIAL_FINGERPRINT_NOT_VALIDATED, "No se ha podido verificar su huella dactilar");
        systemErr.put(_CREDENTIAL_FINGERPRINT_ERROR_VERIFYING, "Error mientras se estaba verificando su huella dactilar");
        systemErr.put(_CREDENTIAL_ERROR_MAKING_OPERATION, "Error mientras se esta realizando la operacion, por favor contacte a soporte");
        systemErr.put(_SYSTEMTRACK_ERROR_NOT_EXISTS,  "Estado del sistema no existe");
        systemErr.put(_SYSTEMTRACK_ERROR_UPDATING_OPERATION,  "Error mientras se estaba actualizando lo que esta realizando el sistema");
        systemErr.put(_SYSTEMTRACK_ERROR_UPDATING_OPERATION_STATE,  "Error mientras se esta actualizando el estado de lo que esta realizando el sistema");
        systemErr.put(_SYSTEMTRACK_ERROR_UPDATING_ERROR_CODE,  "Error mientras se estaba registrando el error detectado");
        systemErr.put(_SYSTEMTRACK_ERROR_COMPLETING_OR_CANCEL_OPERATION, "Error mientras se estaba completando o cancelando la operacion");
        systemErr.put(_SYSTEMTRACK_ERROR_OP_CONFLICT,  "Hay operaciones en conflicto");
        systemErr.put(_SYSTEMTRACK_ERROR_DIFERENT_OP_DETECTED,  "Hay una operacion previa detectada");
        systemErr.put(_SYSTEMTRACK_ERROR_OPERATION_NOT_DEFINE,  "No se reconoce la operacion a realizar");
        systemErr.put(_SYSTEMTRACK_NOT_OPERATION_IN_PROGRESS, "No hay operacion en progreso");
        systemErr.put(_WEBSERVICE_CONTROLLER_NOT_FOUND,  "Controlador del webservice no detectado");
        systemErr.put(_WEBSERVICE_METHOD_NOT_FOUND,  "No se encontro el webservice");
        systemErr.put(_WEBSERVICE_METHOD_NOT_MATCH_SIGNATURE,  "Los parametros no coinciden con los que el webservice requiere");
        systemErr.put(_ENCRYPTION_ERROR_FAIL_ENCRYPT,  "Error detectado durante la encriptacion");
        systemErr.put(_ENCRYPTION_ERROR_FAIL_DECRYPT_GET_PRIVATE_KEY,  "No se pudo encontrar la llave privada");
        systemErr.put(_ENCRYPTION_ERROR_FAIL_DECRYPT,  "Error detectado durante la descriptacion");
        systemErr.put(_EMAIL_ERROR_CAN_NOT_BE_SEND,  "No se pudo enviar el correo");

    }

    public static String _SUCCESS_QR_MSG = "Bienvenido %s, QR validado, por favor introduzca su huella dactilar";
    public static String _SUCCESS_CANCEL_OP_MSG = "Se ha cancelado la operacion %s";
    public static String _SUCCESS_OP_MSG = "Se ha %s satisfactoriamente %s";

    public static Integer _MAXIMUM_NUM_TRIES = 3;

    public static String getMessage(int errCode) {
        String errMsg = "";
        if (systemErr.containsKey(errCode)) {
            errMsg = systemErr.get(errCode);
        }
        return errMsg;
    }
}
