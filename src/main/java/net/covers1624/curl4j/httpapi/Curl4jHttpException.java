/*
 * This file is part of Quack and is Licensed under the MIT License.
 */
package net.covers1624.curl4j.httpapi;

import java.io.IOException;

/**
 * Created by covers1624 on 10/1/24.
 */
public class Curl4jHttpException extends IOException {

    Curl4jHttpException(String message) {
        super(message);
    }

    Curl4jHttpException(String message, Throwable cause) {
        super(message, cause);
    }
}
