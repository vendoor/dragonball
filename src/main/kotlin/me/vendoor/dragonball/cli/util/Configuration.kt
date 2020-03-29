package me.vendoor.dragonball.cli.util

import com.typesafe.config.ConfigBeanFactory
import com.typesafe.config.ConfigFactory
import me.vendoor.dragonball.api.configuration.Configuration
import java.io.File

fun loadConfigurationFrom(file: File): Configuration {
    val config = ConfigFactory.parseFile(file)

    return ConfigBeanFactory.create(config, Configuration::class.java)
}