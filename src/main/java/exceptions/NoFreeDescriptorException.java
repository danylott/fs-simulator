package exceptions;

import file_system.FSConfig;

public class NoFreeDescriptorException extends Exception {
    public NoFreeDescriptorException() {
            super("Couldn't find free file descriptor");
    }
}
