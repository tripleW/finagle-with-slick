package io.triplew.example

object Models extends {
  val profile = slick.driver.MySQLDriver
} with io.triplew.example.models.Tables
