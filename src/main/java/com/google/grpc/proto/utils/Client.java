package com.google.grpc.proto.utils;

import com.google.grpc.proto.generated.MessageDto;

public class Client {

    public static void main(String[] args) {

        MessageDto.Person protocPerson1 = MessageDto.Person.newBuilder()
                .setName("smlee")
                .setAge(10)
                .build();

        MessageDto.Person protocPerson2 = MessageDto.Person.newBuilder()
                .setName("seongmin")
                .setAge(20)
                .build();

        MessageDto.Persons persons = MessageDto.Persons.newBuilder()
                .addPerson(protocPerson1)
                .addPerson(protocPerson2)
                .build();

        for(MessageDto.Person person : persons.getPersonList()) {
            System.out.println("name: " + person.getName());
            System.out.println("age: " + person.getAge());
        }
    }
}
