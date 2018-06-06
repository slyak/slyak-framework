package com.slyak.core.ssh2;

import com.slyak.core.util.IOUtils;
import com.slyak.core.util.StringUtils;
import com.trilead.ssh2.*;
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

    private String mode = "0755";

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
        return setAuthSuccess(conn.authenticateWithPassword(user, password));
    }

    @SneakyThrows
    public SSH2 authWithPublicKey(String user, String password, char[] pemPrivateKey) {
        return setAuthSuccess(conn.authenticateWithPublicKey(user, pemPrivateKey, password));
    }

    private SSH2 setAuthSuccess(boolean authSuccess) {
        this.authSuccess = authSuccess;
        log.info("Authentication result : {}", authSuccess);
        return this;
    }

    public boolean isAuthSuccess() {
        return authSuccess;
    }

    public SSH2 mkdir(String dir) {
        return execCommand("test -d " + dir + " || mkdir -p " + dir, SimpleStdCallback.INSTANCE);
    }

    public void copy(File file, String newFileName, String remoteTarget) {
        copy(file, newFileName, remoteTarget, true);
    }

    @SneakyThrows
    public void copy(File file, String newFileName, String remoteTarget, boolean overwrite) {
        String fileName = newFileName == null ? FilenameUtils.getName(file.getPath()) : newFileName;
        @Cleanup FileInputStream is = new FileInputStream(file);
        copy(is, fileName, remoteTarget, overwrite);
    }

    public void copy(String fileFullPath, String newFileName, String remoteTarget) {
        copy(new File(fileFullPath), newFileName, remoteTarget, true);
    }

    @SneakyThrows
    public void copy(String fileFullPath, String newFileName, String remoteTarget, boolean overwrite) {
        copy(new File(fileFullPath), newFileName, remoteTarget, overwrite);
    }


    @SneakyThrows
    public void copy(InputStream is, String newFileName, String remoteDirectory) {
        copy(is, newFileName, remoteDirectory, true);
    }

    @SneakyThrows
    public void copy(InputStream is, String newFileName, String remoteDirectory, boolean overwrite) {
        if (!overwrite) {
            String exists = execCommand("test -f " + remoteDirectory + "/" + newFileName + " && echo exists");
            if (StringUtils.contains(exists, "exists")) {
                return;
            }
        }
        Assert.notNull(newFileName, "file name must be set");
        mkdir(remoteDirectory);

        int available = is.available();
        if (available > 1 /*500 * 1024 * 1024*/) {
            //if is gt than 500M ,use sftpClient
            SFTPv3Client sftPv3Client = sftPv3Client();
            String path = remoteDirectory + "/" + newFileName;
            SFTPv3FileHandle handle = sftPv3Client.createFile(path);
            byte[] buffer = new byte[1024 * 4];
            int i;
            long offset = 0;
            while ((i = is.read(buffer)) != -1) {
                sftPv3Client.write(handle, offset, buffer, 0, i);
                offset += i;
            }
            sftPv3Client.chmod(path, Integer.valueOf(mode));
            sftPv3Client.closeFile(handle);
            if (handle.isClosed()) {
                sftPv3Client.close();
            }
        } else {
            scpClient().put(IOUtils.readFully(is, available), newFileName, remoteDirectory, mode);
        }
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
                    copy(fullName, null, remote);
                }
            }
        }
    }

    public String execCommand(String command) {
        StringBuilder builder = new StringBuilder();
        execCommand(command, new StdCallback() {
            boolean hasPreLine = false;

            @Override
            public void processOut(String out) {
                if (hasPreLine) {
                    builder.append("\n");
                }
                builder.append(out);
                hasPreLine = true;
            }

            @Override
            public void processError(String error) {
                if (hasPreLine) {
                    builder.append("\n");
                }
                builder.append(error);
                hasPreLine = true;
            }
        });
        return builder.toString();
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

    @SneakyThrows
    private SFTPv3Client sftPv3Client() {
        return new SFTPv3Client(conn);
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
