package me.card.switchv1.component;

public interface ApiCoder<A extends Api, M extends Message> {

  A messageToApi(M m);

  M apiToMessage(A a);

  M errorMessage(M m);

}
