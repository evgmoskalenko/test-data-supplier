package io.github.sskorol.testcases;

import io.github.sskorol.datasuppliers.ExternalDataSuppliers;
import io.github.sskorol.utils.ServiceLoaderUtils;
import io.github.sskorol.utils.ReflectionUtils;
import org.testng.annotations.Test;

import static io.github.sskorol.utils.ReflectionUtils.getMethod;
import static org.assertj.core.api.Assertions.*;
import static org.joor.Reflect.on;

public class ReflectionUtilsTests {

    @Test
    public void shouldThrowAnExceptionOnReflectionUtilsConstructorAccess() {
        assertThatThrownBy(() -> on(ReflectionUtils.class).create())
                .hasStackTraceContaining("java.lang.UnsupportedOperationException: Illegal access to private constructor");
    }

    @Test
    public void shouldThrowAnExceptionOnServiceLoaderUtilsConstructorAccess() {
        assertThatThrownBy(() -> on(ServiceLoaderUtils.class).create())
                .hasStackTraceContaining("java.lang.UnsupportedOperationException: Illegal access to private constructor");
    }

    @Test(expectedExceptions = NoSuchMethodException.class)
    public void shouldThrowAnExceptionOnNonExistingMethodAccess() throws NoSuchMethodException {
        getMethod(ExternalDataSuppliers.class, "missingMethodName");
    }

    @Test
    public void shouldReturnEmptyCollectionInCaseOfException() {
        assertThat(ServiceLoaderUtils.load(null, null)).isEmpty();
    }
}
