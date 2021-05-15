package core;

import core.interfaces.Controller;
import io.ConsoleReader;
import io.ConsoleWriter;
import io.interfaces.InputReader;
import io.interfaces.OutputWriter;

import java.io.IOException;

import static common.constants.OutputMessages.*;

public class EngineImpl implements Runnable {
    private final InputReader reader;
    private final OutputWriter writer;
    private final Controller controller;

    public EngineImpl() {
        this.controller = new ControllerImpl();
        this.reader = new ConsoleReader();
        this.writer = new ConsoleWriter();
    }

    @Override
    public void run() {
        try {
            processInput();
        } catch (IOException | IllegalArgumentException | NullPointerException e) {
            this.writer.writeLine(e.getMessage());
        }
    }

    private void processInput() throws IOException {
        System.out.println(TO_CHOOSE);
        System.out.println(MENU);
        System.out.println(FIRST_OPTION);
        System.out.println(SECOND_OPTION);
        System.out.print(COMMAND_NUMBER);

        // read the input from the console
        String input = this.reader.readLine();
        int command = Integer.parseInt(input);

        String employeeData, reportData;
        // get two paths
        switch (command) {
            case 1 -> {
                System.out.print(ENTER_PATH);
                employeeData = this.reader.readLine();
                System.out.print(SECOND_PATH);
                reportData = this.reader.readLine();
                this.controller.readJsonData(employeeData.trim());
                this.controller.readJsonReportDefinition(reportData);
            }
            case 2 -> {
                System.out.print(ENTER_PATH);
                reportData = this.reader.readLine();
                System.out.print(FIRST_PATH);
                employeeData = this.reader.readLine();
                this.controller.readJsonData(employeeData.trim());
                this.controller.readJsonReportDefinition(reportData.trim());
            }
            default -> throw new IllegalArgumentException(NOT_VALID_PARAM);
        }
    }
}
