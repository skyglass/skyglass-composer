package skyglass.saga.ordersandcustomers.web;

import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import io.eventuate.examples.tram.ordersandcustomers.commonswagger.CommonSwaggerConfiguration;

@Configuration
@Import(CommonSwaggerConfiguration.class)
@ComponentScan
public class OrderWebConfiguration {

	@Bean
	public HttpMessageConverters customConverters() {
		HttpMessageConverter<?> additional = new MappingJackson2HttpMessageConverter();
		return new HttpMessageConverters(additional);
	}

}
