package org.restler.testserver

import org.restler.testserver.db.DbConfig
import org.restler.testserver.security.SecurityConfig
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

EnableAutoConfiguration
Configuration
Import(SecurityConfig::class, DbConfig::class)
open class TestServer {

    Bean open fun controller() = Controller()
}

fun main(args: Array<String>) {
    //val configuration  = org.hibernate.cfg.Configuration();
    //configuration.configure("hibernate.cfg.xml");

    SpringApplication.run(javaClass<TestServer>(), *args)
}

