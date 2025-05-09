package com.example.projectBackEnd.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import com.example.projectBackEnd.stripePayment.StripeResponseSerializer;
import com.stripe.net.StripeResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

@Configuration
public class JacksonConfig {

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();

        // Disable failing on empty beans
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

        // Register Hibernate5Module to handle lazy-loaded entities
        Hibernate5Module hibernate5Module = new Hibernate5Module();
        hibernate5Module.configure(Hibernate5Module.Feature.FORCE_LAZY_LOADING, false);
        mapper.registerModule(hibernate5Module);

        // Add Stripe serializer from the other config
        SimpleModule stripeModule = new SimpleModule();
        stripeModule.addSerializer(StripeResponse.class, new StripeResponseSerializer());
        mapper.registerModule(stripeModule);

        return mapper;
    }

    @Bean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(objectMapper());
        return converter;
    }
}
