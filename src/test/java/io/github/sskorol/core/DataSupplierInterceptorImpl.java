package io.github.sskorol.core;

import io.github.sskorol.model.DataSupplierMetaData;
import org.testng.ITestContext;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DataSupplierInterceptorImpl implements DataSupplierInterceptor {

    private static final Map<Method, DataSupplierMetaData> META_DATA = new ConcurrentHashMap<>();

    @Override
    public void beforeDataPreparation(final ITestContext context, final Method method) {
        // not implemented
    }

    @Override
    public void afterDataPreparation(final ITestContext context, final Method method) {
        // not implemented
    }

    @Override
    public void onDataPreparation(final DataSupplierMetaData testMetaData) {
        META_DATA.putIfAbsent(testMetaData.getTestMethod(), testMetaData);
    }

    @Override
    public Collection<DataSupplierMetaData> getMetaData() {
        return META_DATA.values();
    }
}
