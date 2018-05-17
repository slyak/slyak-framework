package com.slyak.core.util;

import ch.ethz.ssh2.*;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.util.Assert;

import java.io.*;

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

    private String mode = "0750";

    private SSH2() {
    }

    public SSH2(Connection conn) {
        this.conn = conn;
    }

    public SSH2 mode(String mode) {
        this.mode = mode;
        return this;
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

    public SSH2 mkdir(String dir) {
        return execCommand("test -d " + dir + " || mkdir -p " + dir, SimpleStdCallback.INSTANCE);
    }

    @SneakyThrows
    public void scp(File file, String newFileName, String remoteTarget) {
        String fileName = newFileName == null ? FilenameUtils.getName(file.getPath()) : newFileName;
        @Cleanup FileInputStream is = new FileInputStream(file);
        scp(is, fileName, remoteTarget);
    }

    @SneakyThrows
    public void scp(String fileFullPath, String newFileName, String remoteTarget) {
        scp(new File(fileFullPath), newFileName, remoteTarget);
    }


    @SneakyThrows
    public void scp(InputStream is, String newFileName, String remoteDirectory) {
        Assert.notNull(newFileName, "file name must be set");
        mkdir(remoteDirectory);
        @Cleanup SCPOutputStream out = scpClient().put(newFileName, is.available(), remoteDirectory, mode);
        IOUtils.copy(is, out);
        out.flush();
    }

    public void scpDirectory(String local, String remote) {
        File dir = new File(local);
        if (!dir.isDirectory()) {
            return;
        }
        String[] files = dir.list();
        if (files != null) {
            for (String file : files) {
                String fullName = local + File.separator + file;
                if (new File(fullName).isDirectory()) {
                    String rdir = remote + File.separator + file;
                    mkdir(remote + File.separator + file);
                    scpDirectory(fullName, rdir);
                } else {
                    scp(fullName, null, remote);
                }
            }
        }
    }


    public SSH2 execCommand(String command, StdCallback callback) {
        Session session = null;
        try {
            session = conn.openSession();
            @Cleanup InputStream stderr = new StreamGobbler(session.getStderr());
            @Cleanup InputStream stdout = new StreamGobbler(session.getStdout());
            session.execCommand(command);
            @Cleanup BufferedReader brerr = new BufferedReader(new InputStreamReader(stderr));
            @Cleanup BufferedReader brout = new BufferedReader(new InputStreamReader(stdout));
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

    private SCPClient scpClient() {
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

    public static void main(String[] args) throws IOException {
        SSH2 ssh2 = SSH2.connect("192.168.230.8", 22).auth("root", "123456");
        for (int i = 0; i < 10; i++) {
            ssh2.scp("/Users/stormning/Downloads/test.txt", "test" + i + ".txt", "/juesttest");
            System.out.println("scp " + i);
        }
    }
}
