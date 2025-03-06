package com.example.projectBackEnd.stripePayment;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.stripe.net.StripeResponse;

import java.io.IOException;

public class StripeResponseSerializer extends JsonSerializer<StripeResponse> {

    @Override
    public void serialize(StripeResponse stripeResponse, JsonGenerator gen, SerializerProvider serializer) throws IOException {
        // Custom serialization logic
        gen.writeStartObject();
        // Add fields to serialize
        gen.writeEndObject();
    }
}
