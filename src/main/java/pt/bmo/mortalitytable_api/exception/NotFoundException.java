package pt.bmo.mortalitytable_api.exception;

public class NotFoundException extends IllegalArgumentException {
    public NotFoundException(String s) {
        super(s);
    }
}
