package com.slyak.core.util;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.SCPClient;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * .
 *
 * @author stormning 2018/4/25
 * @since 1.3.0
 */
@Slf4j
public class SSH2 {

    private Connection conn;


    private SCPClient scpClient;


    private boolean authSuccess = false;

    private SSH2() {
    }

    public SSH2(Connection conn) {
        this.conn = conn;
    }

    @SneakyThrows
    public SSH2 auth(String user, String password) {
        this.authSuccess = conn.authenticateWithPassword(user, password);
        log.info("Authentication result : {}", authSuccess);
        return this;
    }

    public boolean isAuthSuccess() {
        return authSuccess;
    }

    public SSH2 execCommand(String command, StdCallback callback) {
        Session session = null;
        try {
            session = conn.openSession();
            @Cleanup InputStream stderr = new StreamGobbler(session.getStderr());
            @Cleanup InputStream stdout = new StreamGobbler(session.getStdout());
            session.execCommand(command);
            BufferedReader brerr = new BufferedReader(new InputStreamReader(stderr));
            BufferedReader brout = new BufferedReader(new InputStreamReader(stdout));
            while (true) {
                String out = brout.readLine();
                String error = null;
                if (out == null) {
                    error = brerr.readLine();
                }
                if (out == null && error == null) {
                    break;
                }
                if (out != null) {
                    callback.processOut(out);
                }
                if (error != null) {
                    callback.processError(error);
                }
            }
        } catch (Exception e) {
            log.error("Error occurred {}", e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
        return this;
    }

    public SCPClient scpClient() {
        if (scpClient == null) {
            scpClient = new SCPClient(conn);
        }
        return scpClient;
    }

    public void disconnect() {
        conn.close();
    }

    @SneakyThrows
    public static SSH2 connect(String hostname, int port) {
        Connection conn = new Connection(hostname, port);
        conn.connect();
        return new SSH2(conn);
    }
}
