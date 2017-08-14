package io.github.sskorol.model;

import io.github.sskorol.core.DataSupplier;
import io.vavr.Tuple;
import one.util.streamex.DoubleStreamEx;
import one.util.streamex.IntStreamEx;
import one.util.streamex.LongStreamEx;
import one.util.streamex.StreamEx;
import org.testng.ITestContext;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import static io.github.sskorol.utils.ReflectionUtils.getMethod;
import static io.github.sskorol.utils.ReflectionUtils.invoke;
import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Predicates.instanceOf;
import static io.vavr.Predicates.isNull;
import static java.lang.String.format;
import static java.util.Arrays.stream;
import static java.util.Collections.singletonList;
import static java.util.Optional.ofNullable;

/**
 * Base container for DataSupplier meta data.
 */
public class DataSupplierMetaData {

    private final Method testMethod;
    private final Method dataSupplierMethod;
    private final List<Object[]> testData;
    private final boolean isExtractable;
    private final ITestContext context;

    public DataSupplierMetaData(final ITestContext context, final Method testMethod) throws NoSuchMethodException {
        this.context = context;
        this.testMethod = testMethod;

        final Test testAnnotation = testMethod.getDeclaredAnnotation(Test.class);
        final Class<?> dataSupplierClass = testAnnotation.dataProviderClass() != Object.class
                ? testAnnotation.dataProviderClass()
                : testMethod.getDeclaringClass();
        final String dataSupplierName = testAnnotation.dataProvider();

        this.dataSupplierMethod = getMethod(dataSupplierClass, dataSupplierName);
        this.isExtractable = ofNullable(dataSupplierMethod.getDeclaredAnnotation(DataSupplier.class))
                .map(DataSupplier::extractValues)
                .orElse(false);
        this.testData = transform();
    }

    public Method getTestMethod() {
        return testMethod;
    }

    public Method getDataSupplierMethod() {
        return dataSupplierMethod;
    }

    public List<Object[]> getTestData() {
        return testData;
    }

    private List<Object[]> transform() {
        final StreamEx<?> wrappedDataSupplierReturnValue = Match(obtainReturnValue()).of(
                Case($(isNull()), () -> {
                    throw new IllegalArgumentException(format(
                            "Nothing to return from data supplier. The following test will be skipped: %s.%s.",
                            testMethod.getDeclaringClass().getSimpleName(),
                            testMethod.getName()));
                }),
                Case($(instanceOf(Collection.class)), d -> StreamEx.of((Collection<?>) d)),
                Case($(instanceOf(Object[].class)), d -> StreamEx.of((Object[]) d)),
                Case($(instanceOf(double[].class)), d -> DoubleStreamEx.of((double[]) d).boxed()),
                Case($(instanceOf(int[].class)), d -> IntStreamEx.of((int[]) d).boxed()),
                Case($(instanceOf(long[].class)), d -> LongStreamEx.of((long[]) d).boxed()),
                Case($(instanceOf(Stream.class)), d -> StreamEx.of((Stream<?>) d)),
                Case($(instanceOf(Tuple.class)), d -> StreamEx.of(((Tuple) d).toSeq().toJavaArray())),
                Case($(), d -> StreamEx.of(d)));

        return isExtractable
                ? singletonList(wrappedDataSupplierReturnValue.toArray())
                : wrappedDataSupplierReturnValue.map(ob -> new Object[]{ob}).toList();
    }

    private Object obtainReturnValue() {
        return invoke(dataSupplierMethod, () -> stream(dataSupplierMethod.getParameterTypes())
                .map(t -> Match((Class) t).of(
                        Case($(ITestContext.class), () -> context),
                        Case($(Method.class), () -> testMethod),
                        Case($(), () -> null)))
                .toArray());
    }
}
