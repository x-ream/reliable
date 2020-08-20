package io.xream.reliable;


import io.xream.x7.EnableReyClient;
import org.springframework.context.annotation.Configuration;


/**
 * @Author Sim
 */
@Configuration
@EnableReyClient(basePackages = ("io.xream.reliable.remote.reliable"))
public class ReliableStarter {


}
