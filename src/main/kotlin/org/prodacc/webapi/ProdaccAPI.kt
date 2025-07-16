 package org.prodacc.webapi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.FilterType
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

 @SpringBootApplication
 @EnableJpaRepositories(
     basePackages = ["org.prodacc.webapi.repositories"],
     excludeFilters = [
         ComponentScan.Filter(
             type = FilterType.REGEX,
             pattern = ["org.prodacc.webapi.repositories.products.*"]
         )
     ]
 )
 @EnableJpaAuditing
class ProdaccAPI

fun main(args: Array<String>) {
    runApplication<ProdaccAPI>(*args)
}
