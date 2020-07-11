package pl.piomin.services.trip.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import pl.piomin.services.trip.repository.TripRepository;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableDiscoveryClient
@EnableFeignClients("pl.piomin.services.trip.client")
@EnableAutoConfiguration
@EnableSwagger2
@ComponentScan(basePackages = { "pl.piomin.services.trip.controller" })
public class TripManagementAppConfig {

	@Bean
	TripRepository repository() {
		TripRepository repository = new TripRepository();
		return repository;
	}

	/*
	 * @Bean
	 * public Docket swaggerPersonApi10() {
	 * new Docket(DocumentationType.SWAGGER_2)
	 * .select()
	 * .apis(RequestHandlerSelectors.basePackage("pl.piomin.services.trip.controller"))
	 * .paths(any())
	 * .build()
	 * .apiInfo(new ApiInfoBuilder().version("1.0").title("Trip Management").description("Documentation Trip API").build());
	 * }
	 */

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

}
