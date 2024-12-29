/*
 * This file is part of Quack and is Licensed under the MIT License.
 */
package net.covers1624.curl4j.httpapi;

import net.covers1624.quack.annotation.Requires;
import net.covers1624.quack.net.httpapi.EngineResponse;

/**
 * Created by covers1624 on 1/11/23.
 */
@Requires (value = "net.covers1624:Quack", minVersion = "0.4.111")
public abstract class Curl4jEngineResponse implements EngineResponse {

    @Override
    public abstract Curl4jEngineRequest request();
}
