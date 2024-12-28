package com.subha.uri.config.jwt;

import io.jsonwebtoken.LocatorAdapter;
import io.jsonwebtoken.ProtectedHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.Key;

@Component
public class JwtKeyLocator extends LocatorAdapter<Key> {

  @Autowired
  private JwtProperties jwtProperties;

  @Override
  public Key locate(ProtectedHeader header) {
    String keyId = header.getKeyId(); //or any other parameter that you need to inspect
    return jwtProperties.getSecretKey(keyId);
  }
}
