package org.half;

import org.half.view.MasterControl;
import com.williamcallahan.tui4j.compat.bubbletea.Program;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        log.info("Bank application started");

        try {
            MasterControl app = new MasterControl();
            Program program = new Program(app);
            program.run();
        } catch (Exception e) {
            log.error("A fatal error occurred", e);
        } finally {
            log.info("Bank application shut down normally.");
        }
    }
}