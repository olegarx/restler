package org.restler.util

import spock.lang.Specification

class UriBuilderSpec extends Specification {

    def "UriBuilder should preserve original url if not changed"() {
        given:
        def originalUrl = new URI("http://localhost:8080/api")
        def builder = new UriBuilder(originalUrl)

        when: "Nothing changed"

        then:
        originalUrl == builder.build()
    }

    def "UriBuilder should allow change path"() {
        given:
        def baseUrl = "http://localhost:8080/api"
        def builder = new UriBuilder(new URI(baseUrl + "/v1"))

        when:
        builder.path("api/v2")

        then:
        new URI(baseUrl + "/v2") == builder.build()
    }

    def "UriBuilder should allow to not specify port for http protocol"() {
        given:
        def baseUrl = "$protocol://localhost"
        def builder = new UriBuilder(new URI(baseUrl))

        when: "Nothing changed"

        then:
        new URI(baseUrl + ":$port") == builder.build()

        where:
        protocol || port
        "http"   || 80
        "https"  || 443
    }
}
