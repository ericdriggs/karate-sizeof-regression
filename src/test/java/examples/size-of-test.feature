Feature: size of test

  Background: types

    * def TestCollections = Java.type("examples.TestCollections")

  Scenario: sizeOf set test

    * def mySet = TestCollections.set()
    * match mySet.size() == 3
    * match TestCollections.sizeOf(mySet) == 3
    * match karate.sizeOf(mySet) == 3

  Scenario: sizeOf byte array test

    * def myBytes = TestCollections.byteArray()
    * match myBytes.length == 7
    * match TestCollections.sizeOf(myBytes) == 7
    * match karate.sizeOf(myBytes) == 7

  Scenario: sizeOf list test

    * def myList = TestCollections.list()
    * match TestCollections.sizeOf(myList) == 4
    * match karate.sizeOf(myList) == 4

  Scenario: sizeOf map test

    * def myMap = TestCollections.map()
    * match TestCollections.sizeOf(myMap) == 1
    * match karate.sizeOf(myMap) == 1


