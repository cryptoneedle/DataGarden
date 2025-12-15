package com.cryptoneedle.garden.common.exception;

import com.bubbles.engine.common.core.exception.PlatformException;

/**
 * <p>description:  </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-23
 */
public class EntityNotFoundException extends PlatformException {
    
    public EntityNotFoundException(String entityName, String key) {
        super("Entity Not Found => " + entityName + ": " + key);
    }
}