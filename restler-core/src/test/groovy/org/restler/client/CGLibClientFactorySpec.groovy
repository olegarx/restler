package org.restler.client

import org.restler.testserver.Controller
import spock.lang.Specification
import java.util.function.BiFunction


class CGLibClientFactorySpec extends Specification {
    def mockServiceMethodInvocationExecutor = Mock(ServiceMethodInvocationExecutor)
    def mockInvocationMapper = Mock(BiFunction)
    def mockThreadExecutor = Mock(java.util.concurrent.Executor)

    def clientFactory = new CGLibClientFactory(mockServiceMethodInvocationExecutor, mockInvocationMapper, mockThreadExecutor)

    def "test exception CGLibClient when class not a controller"() {
        when:
        clientFactory.produceClient(CGLibClientFactory.class)
        then:
        thrown(IllegalArgumentException)
    }

    def "test CGLib produce client"() {
        when:
        def client = clientFactory.produceClient(Controller)
        then:
        client instanceof Controller
    }
}
