package core.interfaces;

import models.Employee;
import models.Report;

import java.util.ArrayList;

public interface Controller {
    void readJsonData(String path);

    void readJsonReportDefinition(String path);

    void createReportFile(ArrayList<Employee> employees, Report report);
}
