package kr.oytech.authenticationservice.handler;

import static feign.Util.resolveLastTypeParameter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import feign.RequestTemplate;
import feign.codec.EncodeException;
import feign.codec.Encoder;
import java.lang.reflect.Type;
import java.util.Collections;

public class GsonEncoder implements Encoder {

  private final Gson gson;

  public GsonEncoder(Iterable<TypeAdapter<?>> adapters) {
    GsonBuilder builder = new GsonBuilder().setPrettyPrinting();
    for (TypeAdapter<?> adapter : adapters) {
      Type type = resolveLastTypeParameter(adapter.getClass(), TypeAdapter.class);
      builder.registerTypeAdapter(type, adapter);
    }
    this.gson = builder.create();
  }

  public GsonEncoder() {
    this(Collections.<TypeAdapter<?>>emptyList());
  }

  public GsonEncoder(Gson gson) {
    this.gson = gson;
  }

  @Override
  public void encode(Object o, Type type, RequestTemplate requestTemplate) throws EncodeException {
    requestTemplate.body(gson.toJson(o, type));
  }
}
