package pt.bmo.mortalitytable_api.externalservice.exception;

public class ExternalSystemException extends RuntimeException {
    public ExternalSystemException(String message) {
        super(message);
    }
}
