package dk.sdu.mmmi.cbse.main;

import dk.sdu.mmmi.cbse.common.services.IEntityProcessingService;
import dk.sdu.mmmi.cbse.common.services.IGamePluginService;
import dk.sdu.mmmi.cbse.common.services.IPostEntityProcessingService;

import java.util.List;
import java.util.ServiceLoader;
import static java.util.stream.Collectors.toList;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ModuleConfig {

    @Bean
    public Game game() {
        return new Game(gamePluginServices(), entityProcessingServiceList(), postEntityProcessingServiceList(), restTemplate());
    }

    @Bean
    public List<IEntityProcessingService> entityProcessingServiceList() {
        return ServiceLoader.load(IEntityProcessingService.class)
                .stream().map(ServiceLoader.Provider::get).collect(toList());
    }

    @Bean
    public List<IGamePluginService> gamePluginServices() {
        return ServiceLoader.load(IGamePluginService.class)
                .stream().map(ServiceLoader.Provider::get).collect(toList());
    }

    @Bean
    public List<IPostEntityProcessingService> postEntityProcessingServiceList() {
        return ServiceLoader.load(IPostEntityProcessingService.class)
                .stream().map(ServiceLoader.Provider::get).collect(toList());
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}

