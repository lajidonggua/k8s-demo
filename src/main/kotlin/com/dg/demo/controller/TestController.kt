package com.dg.demo.controller

import org.apache.logging.log4j.LogManager
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class TestController {
    val logger = LogManager.getLogger(this::class.java)
    @GetMapping("/test")
    fun get(): String{
        logger.info("test get")
        return "test get"
    }

    @PutMapping("/test")
    fun update(name: String): Boolean{
        logger.info("test update name: $name")
        return true
    }
}