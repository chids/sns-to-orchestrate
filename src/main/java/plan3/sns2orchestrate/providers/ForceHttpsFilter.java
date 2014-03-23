package plan3.sns2orchestrate.providers;

import static com.google.common.base.Strings.nullToEmpty;
import static java.lang.System.getenv;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.core.UriBuilder;

import plan3.sns2orchestrate.service.PermanentRedirectException;

import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseFilter;

public class ForceHttpsFilter implements ContainerRequestFilter, ContainerResponseFilter {

    private static final long EXPIRY = TimeUnit.HOURS.toSeconds(4);
    private final boolean forceSSL;

    public ForceHttpsFilter() {
        this(getenv());
    }

    ForceHttpsFilter(final Map<String, String> environment) {
        this.forceSSL = isEnabled(environment);
    }

    @Override
    public ContainerResponse filter(final ContainerRequest request, final ContainerResponse response) {
        if(request.isSecure()) {
            final String value = String.format("max-age=%s; includeSubDomains", EXPIRY);
            response.getHttpHeaders().add("Strict-Transport-Security", value);
        }
        return response;
    }

    @Override
    public ContainerRequest filter(final ContainerRequest request) {
        if(this.forceSSL) {
            throw new PermanentRedirectException(UriBuilder.fromUri(request.getRequestUri()).scheme("https").build());
        }
        return request;
    }

    public static boolean isEnabled() {
        return isEnabled(getenv());
    }

    public static boolean isEnabled(final Map<String, String> environment) {
        return nullToEmpty(environment.get("DO_NOT_FORCE_HTTPS")).isEmpty();
    }

    @SuppressWarnings("unchecked")
    public void addTo(final ResourceConfig config) {
        config.getContainerRequestFilters().add(this);
        config.getContainerResponseFilters().add(this);
    }
}