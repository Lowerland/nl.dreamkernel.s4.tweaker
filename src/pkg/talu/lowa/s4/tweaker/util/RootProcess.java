package pkg.talu.lowa.s4.tweaker.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.apache.http.protocol.HTTP;

public class RootProcess {
	
	private Process mProcess = null;
    private DataOutputStream mOutputStream = null;
    private DataInputStream mInputStream = null;

    public boolean init() {
        try {
            mProcess = Runtime.getRuntime().exec("su");
            mOutputStream = new DataOutputStream(mProcess.getOutputStream());
            mInputStream = new DataInputStream(mProcess.getInputStream());
            if (write("su -v\n")) {
                String[] results = read();
                for (String line : results) {
                    if (line.length() > 0) {
                        return true;
                    }
                }
            }
        } catch (IOException e) {
        }
        return false;
    }

    public void term() {
        if (mInputStream != null) {
            try {
                mInputStream.close();
            } catch (IOException e) {
            }
        }
        if (mOutputStream != null) {
            try {
                if (mProcess != null) {
                    mOutputStream.writeBytes("exit\n");
                    mOutputStream.flush();
                    try {
                        mProcess.waitFor();
                    } catch (InterruptedException e) {
                    }
                }
                mOutputStream.close();
            } catch (IOException e) {
            }
        }
        if(mProcess != null){
            mProcess.destroy();
        }
        mOutputStream = null;
        mInputStream = null;
        mProcess = null;
    }

    public String[] read() {
        if (mInputStream != null) {
            String ret = "";
            int size = 0;
            byte[] buffer = new byte[1024];
            try {
                do {
                    size = mInputStream.read(buffer);
                    if (size > 0) {
                        ret += new String(buffer, 0, size, HTTP.UTF_8);
                    }
                } while (mInputStream.available() > 0);
            } catch (IOException e) {
            }
            return ret.split("\n");
        }
        return null;
    }

    public boolean write(String command) {
        if (mOutputStream != null) {
            try {
                mOutputStream.writeBytes(command);
                mOutputStream.flush();
                return true;
            } catch (IOException e) {
            }
        }
        return false;
    }

}
