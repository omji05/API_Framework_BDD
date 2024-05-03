package runner;


import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
        features="src/test/resources/features",
        glue = {"stepdef"},
        plugin = {"pretty","html:target/cucumber-reports.html"},
        tags="@demoTestFeature"
//        dryRun = true

)
public class RunnerCukes extends AbstractTestNGCucumberTests {


}
