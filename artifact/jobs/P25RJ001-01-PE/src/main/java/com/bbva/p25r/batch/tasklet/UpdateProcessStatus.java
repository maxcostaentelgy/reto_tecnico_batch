package com.bbva.p25r.batch.tasklet;

import com.bbva.apx.exception.business.BusinessException;
import com.bbva.p25r.lib.r001.P25RR001;
import com.bbva.p25r.lib.r001.util.P25RErrors;
import com.bbva.p25r.lib.r001.util.P25RUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import java.util.Map;

public class UpdateProcessStatus implements Tasklet, StepExecutionListener {

    private String fechaEntrada;

    private P25RR001 p25Rrr01;

    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateProcessStatus.class);


    private P25RUtil p25RUtil = new P25RUtil();

    private Map<String, Integer> resultado;

    @Override
    public void beforeStep(StepExecution stepExecution) {
        if (p25RUtil.validateParameterFormat(this.fechaEntrada)) {
            LOGGER.info("Before Step {}", stepExecution.getStepName());
            LOGGER.info("Job Exec Context {}", stepExecution.getJobExecution().getExecutionContext());
            LOGGER.info("Step Exec Context {}", stepExecution.getExecutionContext());
        } else {
            LOGGER.info("Incorrect Date Format. It should be 'YYYY-MM-DD'.");
            throw new BusinessException(P25RErrors.INVALID_PARAMETER_FORMAT.getCodeAdvice(), P25RErrors.INVALID_PARAMETER_FORMAT.isRollback());
        }
    }

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        this.resultado = p25Rrr01.executeUpdateProcess(this.fechaEntrada);
        return RepeatStatus.FINISHED;
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        LOGGER.info("[APX] - afterStep");
        Integer totalDocumentsToUpdate = this.resultado.get("totalDocumentsToUpdate");
        LOGGER.info("totalDocumentsToUpdate {}", totalDocumentsToUpdate);

        Integer totalDocumentsUpdated = this.resultado.get("totalDocumentsUpdated");
        LOGGER.info("totalDocumentsUpdated {}", totalDocumentsUpdated);

        if (totalDocumentsToUpdate != null && totalDocumentsUpdated != null) {
            if (totalDocumentsUpdated.equals(totalDocumentsToUpdate)) {
                LOGGER.info("After Step {}", stepExecution.getStepName());
                LOGGER.info("Job Exec Context {}", stepExecution.getJobExecution().getExecutionContext());
                LOGGER.info("Step Exec Context {}", stepExecution.getExecutionContext());
                LOGGER.info("All documents modified succesfully.");
                return ExitStatus.COMPLETED;
            } else {
                LOGGER.info("A problem has ocurred.");
                return ExitStatus.FAILED;
            }
        }
        return ExitStatus.COMPLETED;
    }

    public void setDate(String date) {
        this.fechaEntrada = date;
    }

    public void setP25Rrr01(P25RR001 p25Rrr01) {
        this.p25Rrr01 = p25Rrr01;
    }
}
