package com.bbva.p25r.lib.r001.impl;

import com.bbva.apx.exception.business.BusinessException;
import com.bbva.p25r.lib.r001.util.P25RErrors;
import com.bbva.p25r.lib.r001.util.P25RUtil;
import com.datiobd.daas.Parameters;
import com.datiobd.daas.conf.EnumOperation;
import com.datiobd.daas.conf.UpdateOptionsWrapper;
import com.datiobd.daas.model.AggregateIterableWrapper;
import com.datiobd.daas.model.AggregatesWrapper;
import com.datiobd.daas.model.DocumentWrapper;
import com.datiobd.daas.model.UpdateResultWrapper;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static com.bbva.p25r.lib.r001.constant.P25RConstant.*;
import static com.datiobd.daas.model.json.FiltersWrapper.*;

/**
 * The P25RR001Impl class...
 */
public class P25RR001Impl extends P25RR001Abstract {


    public static final String PROCESS_STATUS = "process_status";
    public static final String CREATION_DATE = "creation_date";
    public static final String TO_SEND = "TO_SEND";

    private static final Logger LOGGER = LoggerFactory.getLogger(P25RR001Impl.class);

    private P25RUtil p25RUtil = new P25RUtil();

    @Override
    public Integer countDocumentsToUpdate(String fechaEntrada) {
        LOGGER.info("[APX] - countDocumentsToUpdate ");
        Date fechaEntradaParam = this.getDateParameter(fechaEntrada);

        Map<String, Object> baseParameters = getConexionMongo();

        baseParameters.put(Parameters.FILTER, and(eq(CREATION_DATE, fechaEntradaParam), eq(PROCESS_STATUS, TO_SEND)));

        LOGGER.info("[APX] - baseParameters : {}", baseParameters.entrySet());

        long nDocuments = daasMongoConnector.executeWithReturn(EnumOperation.COUNT, baseParameters);

        LOGGER.info("[APX] - Total Documents : {}", nDocuments);

        LOGGER.info("[APX] - End countDocumentsToUpdate");
        return (int) nDocuments;
    }

    /**
     * The execute method...
     */
    @Override
    public Map<String, Integer> executeUpdateProcess(String fechaEntrada) {
        LOGGER.info("[APX] - UpdateProcess ");

        UpdateOptionsWrapper updateOptions = new UpdateOptionsWrapper();
        updateOptions.setUpsert(true);
        updateOptions.setBypassDocumentValidation(true);

        Date fechaEntradaParam = this.getDateParameter(fechaEntrada);

        Map<String, Object> baseParameters = getConexionMongo();

        Integer modifiedDocuments = 0;

        ///pagination
        Double totalRegistros = 0.00;
        Integer totalPaginas = 1;
        Integer paginaActual = 0;
        do {
            paginaActual = paginaActual + 1;
            LOGGER.info("[APX] - paginaActual : {}", paginaActual);

            baseParameters.put(Parameters.PIPELINE,
                    Arrays.asList(AggregatesWrapper.match(and(eq(CREATION_DATE, fechaEntradaParam),
                                    eq(PROCESS_STATUS, TO_SEND))),
                            AggregatesWrapper.limit(NUMERO_ITEMS_INICIAL)));
            AggregateIterableWrapper responseQuery = this.daasMongoConnector.executeWithReturn(EnumOperation.AGREGGATES, baseParameters);

            List<Map<Object, Object>> listaDocumentos = new ArrayList<>();
            LOGGER.info("[APX] - Documentos Encontrados a Modificar");
            responseQuery.forEach(y -> {
                Map<Object, Object> map = (Map) y;
                listaDocumentos.add(map);
            });


            if (!listaDocumentos.isEmpty()) {
                if (paginaActual == 1) {
                    totalRegistros = (double) this.countDocumentsToUpdate(fechaEntrada);
                    LOGGER.info("[APX] - totalRegistros : {}", totalRegistros);

                    totalPaginas = (int) (Math.ceil(totalRegistros / NUMERO_ITEMS_INICIAL));
                    LOGGER.info("[APX] - totalPaginas : {}", totalPaginas);
                }


                if (paginaActual >= 1) {
                    modifiedDocuments += processUpdateMany(listaDocumentos, baseParameters, fechaEntradaParam);
                    LOGGER.info("[APX] - Total Modificados : {}", modifiedDocuments);
                } else {
                    totalPaginas = 0;
                }
            }

        } while (paginaActual < totalPaginas);
        ///pagination

        LOGGER.info("[APX] - End UpdateProcess");

        Map<String, Integer> result = new HashMap<>();
        result.put("totalDocumentsUpdated", modifiedDocuments);
        result.put("totalDocumentsToUpdate", totalRegistros.intValue());

        return result;
    }

    private Integer processUpdateMany(List<Map<Object, Object>> listaDocumentos, Map<String, Object> baseParameters, Date fechaEntradaParam) {
        LOGGER.info("[APX] - processUpdateMany ");
        List<ObjectId> idList = new ArrayList<>();

        for (Map<Object, Object> document : listaDocumentos) {
            idList.add((ObjectId) document.get("_id"));
        }

        baseParameters.remove(Parameters.PIPELINE);

        baseParameters.put(Parameters.FILTER, and(eq(CREATION_DATE, fechaEntradaParam), eq(PROCESS_STATUS, TO_SEND), in("_id", idList)));
        baseParameters.put(Parameters.UPDATE_MANY, new DocumentWrapper("$set", new DocumentWrapper().append(PROCESS_STATUS, "SENT").append("audit_date", new Date())));

        LOGGER.info("[APX] - baseParameters : {}", baseParameters.entrySet());

        UpdateResultWrapper res = daasMongoConnector.executeWithReturn(EnumOperation.UPDATE_MANY, baseParameters);

        LOGGER.info("[APX] - END processUpdateMany ");

        return (int) res.getModifiedCount();

    }


    private Date getDateParameter(String fechaEntrada) {
        Date creationDate;
        try {
            LOGGER.info("[APX] - FechaEntrada : {}", fechaEntrada);
            creationDate = p25RUtil.convertStringToDate(fechaEntrada, DATE_PARAMETER_FORMAT, DEFAUL_TIMEZONE);
        } catch (Exception e) {
            LOGGER.info("[APX] - Error UpdateProcess : Formato incorrecto en parametro input_date");
            throw new BusinessException(P25RErrors.INVALID_PARAMETER_FORMAT.getCodeAdvice(), P25RErrors.INVALID_PARAMETER_FORMAT.isRollback());
        }

        if (creationDate != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(creationDate);
            calendar.add(Calendar.DATE, 1);
            return calendar.getTime();
        }
        return null;
    }


    protected Map<String, Object> getConexionMongo() {
        LOGGER.info("[APX] getConexionMongo ...");

        Map<String, Object> params = new HashMap<>();
        params.put(Parameters.DATABASE_PROPERTY_NAME, "apx_local");
        params.put(Parameters.API_OR_REST, Parameters.API);
        LOGGER.info("[APX] getConexionMongo : {}", params);

        return params;
    }


}
