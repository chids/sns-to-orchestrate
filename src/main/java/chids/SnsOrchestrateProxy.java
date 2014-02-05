package chids;

import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Configuration;
import com.yammer.dropwizard.config.Environment;

public class SnsOrchestrateProxy extends Service<Configuration> {

    @Override
    public void initialize(final Bootstrap<Configuration> bootstrap) {}

    @Override
    public void run(final Configuration configuration, final Environment environment) throws Exception {
        environment.addProvider(TextPlainAsJson.class);
        environment.addResource(Proxy.class);
    }

    public static void main(final String[] args) throws Exception {
        new SnsOrchestrateProxy().run(args);
    }
}
