package microservices.book.testutils.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Scanner;

public class StreamMonitor implements Runnable {

    private Process process;
    private Scanner scanner;
    private boolean logAsWarning;
    private Logger log = LoggerFactory.getLogger(StreamMonitor.class);

    public static void monitorStream(Process process, InputStream inputStream, boolean logAsWarning) {
        new StreamMonitor(process, new Scanner(new InputStreamReader(inputStream, Charset
                .forName("UTF-8"))), logAsWarning)
                .start();
    }

    public StreamMonitor(Process process, Scanner scanner, boolean logAsWarning) {
        this.process = process;
        this.scanner = scanner;
        this.logAsWarning = logAsWarning;
    }

    private void start() {
        new Thread(this).start();
    }

    public void run() {
        while (process.isAlive()) {
            readLine();
        }
    }

    private void readLine() {
        if (scanner.hasNext()) {
            String line = scanner.nextLine();
            if (!StringUtils.isEmpty(line)) {
                if (logAsWarning) {
                    log.error(line);
                } else {
                    log.info(line);
                }
            }
        }
    }
}
