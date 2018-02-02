package ru.lumberjackcode.vacls.server.authentication;

import ru.lumberjackcode.vacls.transfere.*;

public class FaceAuthenticatior {
    public FaceAuthenticatior () {}

    public ClientResponse Authentificate(ClientRequest clientRequest) {
        return new ClientResponse(false, "Default message", 0);
    }
}
