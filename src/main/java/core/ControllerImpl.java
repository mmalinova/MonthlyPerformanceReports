package core;

import core.interfaces.Controller;
import io.ConsoleWriter;
import io.interfaces.OutputWriter;
import models.Employee;
import models.Report;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.ArrayList;

import static common.constants.ExceptionMessages.*;

public class ControllerImpl implements Controller {
    private final OutputWriter writer = new ConsoleWriter();
    private FileReader fileReader;
    private final JSONParser jsonParser = new JSONParser();
    private final ArrayList<Employee> employees = new ArrayList<>();

    @Override
    public void readJsonData(String data) {
        try {
            fileReader = new FileReader(data.replace('/', '\\'));
            Object object = jsonParser.parse(fileReader);
            // parse to JSONArray because we have array of objects at this JSON file
            JSONArray jsonData = (JSONArray) object;
            // parse each of objects from JSONArray to JSONObjects
            for (Object obj : jsonData) {
                if (obj instanceof JSONObject) {
                    JSONObject jsonObject = ((JSONObject) obj);
                    // get data
                    String name = (String) jsonObject.get("name");
                    long totalSales = (long) jsonObject.get("totalSales");
                    long salesPeriod = (long) jsonObject.get("salesPeriod");
                    double experienceMultiplier = (double) jsonObject.get("experienceMultiplier");

                    // make instance og Employee
                    Employee employee = new Employee(name, totalSales, salesPeriod, experienceMultiplier);
                    // add it to the array of Employees
                    employees.add(employee);
                }
            }
        } catch (FileNotFoundException e) {
            this.writer.writeLine(FILE_NOT_FOUND);
        } catch (ParseException e) {
            this.writer.writeLine(UNABLE_TO_PARSE);
        } catch (IOException e) {
            this.writer.writeLine(IO_EXCEPTION);
        }
    }

    @Override
    public void readJsonReportDefinition(String data) {
        try {
            fileReader = new FileReader(data.replace('/', '\\'));
            Object object = jsonParser.parse(fileReader);
            // parse to JSONObjects
            JSONObject jsonObject = ((JSONObject) object);
            // get data
            long topPerformersThreshold = (long) jsonObject.get("topPerformersThreshold");
            boolean useExprienceMultiplier = (boolean) jsonObject.get("useExprienceMultiplier");
            long periodLimit = (long) jsonObject.get("periodLimit");

            // make instance of Report
            Report report = new Report(topPerformersThreshold, useExprienceMultiplier, periodLimit);
            for (Employee employee : employees) {
                // calculate score of each employee
                double score = report.isUseExprienceMultiplier() ?
                        (employee.getTotalSales() * 1.0 / employee.getSalesPeriod())
                                * employee.getExperienceMultiplier() :
                        employee.getTotalSales() * 1.0 / employee.getSalesPeriod();
                // store score
                employee.setScore(score);
            }

            createReportFile(employees, report);

        } catch (FileNotFoundException e) {
            this.writer.writeLine(FILE_NOT_FOUND);
        } catch (ParseException e) {
            this.writer.writeLine(UNABLE_TO_PARSE);
        } catch (IOException e) {
            this.writer.writeLine(IO_EXCEPTION);
        }
    }

    @Override
    public void createReportFile(ArrayList<Employee> employees, Report report) {
        // calculate results - all scores of employees
        double totalScores = employees.stream().mapToDouble(Employee::getScore).sum();
        for (Employee employee : employees) {
            // check whether sales period is equal or less than the periodLimit property
            if (employee.getSalesPeriod() <= report.getPeriodLimit()) {
                /* check whether score is within the top X percent of the results
                   I'm not very sure about that, maybe I didn't understand this condition well */
                if (employee.getScore() >= totalScores * (report.getTopPerformersThreshold() * 1.0 / 100)) {
                    try {
                        // create file
                        PrintWriter writer = new PrintWriter("result.txt");

                        writer.println("Name, Score");
                        employees
                                .forEach(emp -> writer.println(emp.getName() + ", " + emp.getScore()));

                        // close file
                        writer.close();
                    } catch (IOException e) {
                        this.writer.writeLine(IO_EXCEPTION);
                    }
                }
            }
        }
    }
}
