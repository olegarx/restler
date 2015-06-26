package org.restler.itest

import org.restler.Service
import org.restler.ServiceBuilder
import org.restler.http.RestOperationsExecutor
import org.restler.http.SpringDataRestOperationsExecutor
import org.restler.http.security.authentication.CookieAuthenticationStrategy
import org.restler.http.security.authorization.FormAuthorizationStrategy
import org.restler.testserver.Controller
import org.restler.testserver.springdata.Person
import org.restler.testserver.springdata.PersonsRepository
import org.springframework.web.client.RestTemplate
import spock.lang.Specification
import spock.util.concurrent.AsyncConditions

import java.util.concurrent.Executors

class SimpleTest extends Specification {

    def login = "user";
    def password = "password";

    def formAuth = new FormAuthorizationStrategy("http://localhost:8080/login", login, "username", password, "password");
    //def basicAuth = new BasicAuthorizationStrategy(login, password)

    def spySimpleHttpRequestExecutor = Spy(SpringDataRestOperationsExecutor, constructorArgs: [new RestOperationsExecutor(new RestTemplate())])

    Service serviceWithFormAuth = new ServiceBuilder("http://localhost:8080").
            useAuthorizationStrategy(formAuth).
            useCookieBasedAuthentication().
            autoAuthorize(true).
            useThreadExecutor(Executors.newCachedThreadPool()).
            useClassNameExceptionMapper().
            useExecutor(spySimpleHttpRequestExecutor).
            build();

    Service serviceWithFormReAuth = new ServiceBuilder("http://localhost:8080").
            useAuthorizationStrategy(formAuth).
            reauthorizeRequestsOnForbidden(true).
            useCookieBasedAuthentication().
            build();

    Service serviceWithBasicAuth = new ServiceBuilder("http://localhost:8080").
            useHttpBasicAuthentication(login, password).
            autoAuthorize(true).
            build();

    def controller = serviceWithFormAuth.produceClient(Controller.class);
    def controllerWithBasicAuth = serviceWithBasicAuth.produceClient(Controller.class);

    def "test unsecured get"() {
        expect:
        "OK" == controller.publicGet()
    }

    def "test deferred get"() {
        def deferredResult = controller.deferredGet()
        def asyncCondition = new AsyncConditions();

        Thread.start {
            while (!deferredResult.hasResult());
            asyncCondition.evaluate {
                assert deferredResult.getResult() == "Deferred OK"
            }
        }

        expect:
        asyncCondition.await(5)
    }

    def "test callable get"() {
        when:
        def result = controller.callableGet()
        def asyncCondition = new AsyncConditions();
        then:
        0 * spySimpleHttpRequestExecutor.execute(_)
        and:
        when:
        Thread.start {
            asyncCondition.evaluate {
                assert result.call() == "Callable OK"
            }
        }
        then:
        asyncCondition.await(5)
    }

    def "test get with variable"() {
        expect:
        "Variable OK" == controller.getWithVariable("test", "Variable OK")
    }

    def "test secured get authorized with form auth"() {
        expect:
        "Secure OK" == controller.securedGet()
    }

    def "test secured get authorized with basic auth"() {
        expect:
        "Secure OK" == controllerWithBasicAuth.securedGet()
    }

    def "test reauthorization"() {
        given:
        def ctrl = serviceWithFormReAuth.produceClient(Controller)
        ctrl.logout()
        when:
        def response = ctrl.securedGet()
        then:
        response == "Secure OK"
    }

    def "test exception CookieAuthenticationRequestExecutor when cookie name is empty"() {
        when:
        new CookieAuthenticationStrategy("");
        then:
        thrown(IllegalArgumentException)
    }

    def "test PersonRepository findOne"() {
        expect:
        PersonsRepository personRepository = serviceWithFormAuth.produceClient(PersonsRepository.class)
        Person person = personRepository.findOne("0")
        person.getId() == "0"
        person.getName() == "test name"
    }

    def "test query method PersonRepository findById"() {
        expect:
        PersonsRepository personRepository = serviceWithFormAuth.produceClient(PersonsRepository.class)
        Person person = personRepository.findById("0")
        person.getId() == "0"
        person.getName() == "test name"
    }

    def "test query method PersonRepository findByName"() {
        expect:
        PersonsRepository personRepository = serviceWithFormAuth.produceClient(PersonsRepository.class)
        List<Person> persons = personRepository.findByName("test name")
        persons[0].getId() == "0"
        persons[0].getName() == "test name"
    }
}
