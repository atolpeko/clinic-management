/*
 * Copyright 2002-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package clientservice.service.exception;

/**
 * Thrown to indicate that there was some kind of problem modifying the remote client repository.
 */
public class ClientsModificationException extends RuntimeException {

    /**
     * Constructs a new ClientsModificationException with null as its detail message.
     * The cause is not initialized, and may subsequently be initialized by a call to initCause().
     */
    public ClientsModificationException() {
        super();
    }

    /**
     * Constructs a new ClientsModificationException with the specified detail message.
     * The cause is not initialized, and may subsequently be initialized by a call to initCause().
     *
     * @param message the detail message. The detail message is saved
     *                for later retrieval by the getMessage() method
     */
    public ClientsModificationException(String message) {
        super(message);
    }

    /**
     * Constructs a new ClientsModificationException with the specified detail message and cause.
     *
     * @param message the detail message. The detail message is saved
     *                for later retrieval by the getMessage() method
     * @param cause the cause (which is saved for later retrieval by the getCause() method)
     */
    public ClientsModificationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new ClientsModificationException with the specified cause and a detail message.
     *
     * @param cause the cause (which is saved for later retrieval by the getCause() method)
     */
    public ClientsModificationException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a ClientsModificationException with the specified detail message, cause,
     * suppression enabled or disabled, and writable stack trace enabled or disabled.
     *
     * @param message the detail message. The detail message is saved
     *                for later retrieval by the getMessage() method
     * @param cause the cause (which is saved for later retrieval by the getCause() method)
     * @param enableSuppression whether or not suppression is enabled or disabled
     * @param writableStackTrace  whether or not the stack trace should be writable
     */
    public ClientsModificationException(String message, Throwable cause,
                                        boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
