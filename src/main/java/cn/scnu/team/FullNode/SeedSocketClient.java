package cn.scnu.team.FullNode;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

public class SeedSocketClient extends WebSocketClient {

    public SeedSocketClient(URI serverUri) {
        super(serverUri);
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        System.out.println("Connect seed server success");
    }

    @Override
    public void onMessage(String s) {
        System.out.println(s);
    }

    @Override
    public void onClose(int i, String s, boolean b) {

    }

    @Override
    public void onError(Exception e) {
        System.out.println("Connect seed server failed");
        try {
            throw e;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
