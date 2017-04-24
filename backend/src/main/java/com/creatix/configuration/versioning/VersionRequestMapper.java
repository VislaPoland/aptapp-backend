package com.creatix.configuration.versioning;

import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.AbstractRequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;

/**
 * Created by kvimbi on 24/04/2017.
 */
public class VersionRequestMapper extends RequestMappingHandlerMapping {

    private class ApiVersionTypeCondition extends AbstractRequestCondition<ApiVersionTypeCondition> {

        private final double minVersion;
        private final double maxVersion;

        public double getMinVersion() {
            return minVersion;
        }

        public double getMaxVersion() {
            return maxVersion;
        }

        public ApiVersionTypeCondition(double minVersion, double maxVersion) {
            this.minVersion = minVersion;
            this.maxVersion = maxVersion;
        }

        private double getVersionNumberFromRequest(HttpServletRequest request) {
            String requestURI = request.getRequestURI();
            if (! requestURI.matches("/api/v?[0-9_.]+.*") ) {
                return -1.0;
            }
            String[] split = requestURI.split("/");
            if (split.length > 1) {
                String versionNumber = split[2];
                try {
                    return Double.valueOf(versionNumber.replace("_", ".").replace("v", ""));
                } catch (NumberFormatException e) {
                    System.err.println(requestURI);
                    e.printStackTrace();
                }
            }
            return 0.0;
        }

        @Override
        public ApiVersionTypeCondition combine(ApiVersionTypeCondition other) {
            return this.minVersion == other.getMinVersion() && this.maxVersion == other.maxVersion ?
                    this :
                    new ApiVersionTypeCondition(
                            Math.max(this.minVersion, other.getMinVersion()), //upper min version
                            Math.min(
                                    this.maxVersion > 0.0 ? this.maxVersion : other.getMaxVersion(),
                                    other.getMaxVersion() > 0.0 ? other.getMaxVersion() : this.getMaxVersion()
                            ) //lower max version (except 0)
                    );
        }

        @Override
        public ApiVersionTypeCondition getMatchingCondition(HttpServletRequest request) {
            double requestVersionNumber = getVersionNumberFromRequest(request);
            return  requestVersionNumber == -1.0 ||
                    (requestVersionNumber >= this.minVersion && (this.maxVersion < this.minVersion || requestVersionNumber <= this.maxVersion)) ?
                    this :
                    null;
        }

        @Override
        public int compareTo(ApiVersionTypeCondition other, HttpServletRequest request) {
            double versionNumberFromRequest = getVersionNumberFromRequest(request);
            return ((Double) other.getMinVersion()).compareTo(versionNumberFromRequest) +
                    ((Double) other.getMaxVersion()).compareTo(versionNumberFromRequest);
        }

        @Override
        protected Collection<?> getContent() {
            return Arrays.asList(minVersion, maxVersion);
        }

        @Override
        protected String getToStringInfix() {
            return " <= v <= ";
        }
    }

    private RequestCondition<?> getVersionNumber(ApiVersion apiVersion) {
        double classMinVersion = 0.0;
        double classMaxVersion = 0.0;
        classMinVersion = apiVersion.value();
        classMaxVersion = apiVersion.maxVersion();
        return new ApiVersionTypeCondition(classMinVersion, classMaxVersion);
    }

    @Override
    protected RequestCondition<?> getCustomTypeCondition(Class<?> handlerType) {
        if (handlerType.isAnnotationPresent(ApiVersion.class)) {
            return getVersionNumber(handlerType.getAnnotation(ApiVersion.class));
        }
        return null;
    }

    @Override
    protected RequestCondition<?> getCustomMethodCondition(Method method) {
        if (method.isAnnotationPresent(ApiVersion.class)) {
            return getVersionNumber(method.getAnnotation(ApiVersion.class));
        }
        return null;
    }

    @Override
    protected void registerHandlerMethod(Object handler, Method method, RequestMappingInfo mapping) {
        HandlerMethod handlerMethod = createHandlerMethod(handler, method);
        if (handlerMethod.getBeanType().isAnnotationPresent(ApiVersion.class)) {
            if (method.getDeclaringClass().equals(handlerMethod.getBeanType())) {
                super.registerHandlerMethod(handler, method, mapping);
            } else if (mapping.getCustomCondition() instanceof ApiVersionTypeCondition) {
                ApiVersionTypeCondition customCondition = (ApiVersionTypeCondition) mapping.getCustomCondition();
                if (customCondition.getMaxVersion() == -1.0 || (customCondition.getMaxVersion() > 0.0 && customCondition.getMinVersion() < customCondition.getMaxVersion())) {
                    super.registerHandlerMethod(handler, method, mapping);
                }
            }
        } else {
            super.registerHandlerMethod(handler, method, mapping);
        }
    }

}
