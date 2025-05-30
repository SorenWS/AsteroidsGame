package dk.sdu.mmmi.cbse.main;

import javafx.application.Application;
import javafx.stage.Stage;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main extends Application {

    public static void main(String[] args) {
        launch(Main.class);
    }

    @Override
    public void start(Stage window) throws Exception {
        // initialize Spring context (loads beans defined in ModuleConfig)
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(ModuleConfig.class);

        // log to see what beans/components Spring found
        System.out.println("[CBS ASTEROIDS] Spring loaded beans:");
        for (String beanName : ctx.getBeanDefinitionNames()) {
            if (!beanName.startsWith("org.spring")) // skip spring internals
                System.out.println(" - " + beanName);
        }

        // grab the core game logic bean and start the game
        Game game = ctx.getBean(Game.class);
        game.start(window);
        game.render();
    }
}
