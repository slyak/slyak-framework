package com.slyak.core.util;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.SCPClient;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;
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

    private Session session;

    private SCPClient scpClient;

    private InputStream stderr;

    private InputStream stdout;

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

    public void execCommand(String command, StdCallback callback) {
        try {
            if (session == null) {
                session = conn.openSession();
                this.stderr = new StreamGobbler(session.getStderr());
                this.stdout = new StreamGobbler(session.getStdout());
            }
            session.execCommand(command);
            BufferedReader brerr = new BufferedReader(new InputStreamReader(stderr));
            BufferedReader brout = new BufferedReader(new InputStreamReader(stdout));
            while (true) {
                String out = brout.readLine();
                String error = brerr.readLine();
                if (out == null && error == null) {
                    break;
                }
                callback.processOut(out);
                callback.processError(error);
            }
        } catch (Exception e) {
            log.error("Error occurred", e);
        }
    }

    public SCPClient scpClient() {
        if (scpClient == null) {
            scpClient = new SCPClient(conn);
        }
        return scpClient;
    }

    public void close() {
        session.close();
        conn.close();
    }

    @SneakyThrows
    public static SSH2 connect(String hostname, int port) {
        Connection conn = new Connection(hostname, port);
        conn.connect();
        return new SSH2(conn);
    }
}
