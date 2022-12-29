package com.bbva.p25r.batch;

import java.util.HashMap;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Test for batch process P25RJ001-01-PE
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/META-INF/spring/batch/beans/P25RJ001-01-PE-beans.xml", "classpath:/META-INF/spring/batch/jobs/jobs-P25RJ001-01-PE-context.xml", "classpath:/META-INF/spring/jobs-P25RJ001-01-PE-runner-context.xml"})
public class P25RJ00101PEFunctionalTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;


    @Test
    public void testLaunchJob() throws Exception {
//		With parameters (use this implementation if job needs parameters comment first implementation)
        /*********************** Parameters Definition ***********************/
//		First parameter
        final JobParameter jobParameter = new JobParameter("2022-12-22");
//		Add parameters to job
        final HashMap<String, JobParameter> parameters = new HashMap<String, JobParameter>();
        parameters.put("input_date", jobParameter);
        final JobParameters jobParameters = new JobParameters(parameters);
        final JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

        Assert.assertEquals(ExitStatus.COMPLETED, jobExecution.getExitStatus());
    }
}
