package com.bbva.p25r.lib.r001;

import com.bbva.elara.domain.transaction.Context;
import com.bbva.elara.domain.transaction.ThreadContext;
import com.bbva.p25r.lib.r001.impl.P25RR001Impl;
import com.datiobd.daas.DaasMongoConnector;
import com.datiobd.daas.conf.EnumOperation;
import com.datiobd.daas.model.AggregateIterableWrapper;
import com.datiobd.daas.model.DocumentWrapper;
import com.datiobd.daas.model.FindIterableWrapper;
import com.datiobd.daas.model.UpdateResultWrapper;
import com.mongodb.client.result.UpdateResult;
import org.bson.BsonValue;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.aop.framework.Advised;

import static org.mockito.Matchers.anyMapOf;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class P25RR001Test {

    @Spy
    private Context context;

    @InjectMocks
    private P25RR001 p25rR001 = new P25RR001Impl();

    @Mock
    private DaasMongoConnector oDaasMongoConnector;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        context = new Context();
        ThreadContext.set(context);
        getObjectIntrospection();
    }

    private Object getObjectIntrospection() throws Exception {
        Object result = this.p25rR001;
        if (this.p25rR001 instanceof Advised) {
            Advised advised = (Advised) this.p25rR001;
            result = advised.getTargetSource().getTarget();
        }
        return result;
    }

    @Test
    public void executeTest() {
        UpdateResultWrapper mockUpdateResult = mock(UpdateResultWrapper.class);
        FindIterableWrapper<DocumentWrapper> mockFindResult = mock(FindIterableWrapper.class);
        AggregateIterableWrapper mockAggregateResult = mock(AggregateIterableWrapper.class);
        long mockCountResult = 0L;
        Mockito.when(oDaasMongoConnector.executeWithReturn(eq(EnumOperation.UPDATE_MANY), Mockito.anyMap())).thenReturn(mockUpdateResult);
        Mockito.when(oDaasMongoConnector.executeWithReturn(eq(EnumOperation.FIND), Mockito.anyMap())).thenReturn(mockFindResult);
        Mockito.when(oDaasMongoConnector.executeWithReturn(eq(EnumOperation.COUNT), Mockito.anyMap())).thenReturn(mockCountResult);
        Mockito.when(oDaasMongoConnector.executeWithReturn(eq(EnumOperation.AGREGGATES), Mockito.anyMap())).thenReturn(mockAggregateResult);

        String fechaEntrada = "2022-12-23";
        p25rR001.executeUpdateProcess(fechaEntrada);

        Assert.assertEquals(0, context.getAdviceList().size());
    }

}
