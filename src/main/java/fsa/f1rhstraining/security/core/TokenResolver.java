package fsa.f1rhstraining.security.core;

import java.util.List;
import java.util.Map;

public interface TokenResolver {
    String generate(String username, List<String> roles);

    Map<String, Object> verify(String token);
}
