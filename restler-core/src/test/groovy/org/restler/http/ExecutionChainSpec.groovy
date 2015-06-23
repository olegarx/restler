package org.restler.http

import spock.lang.Specification

class ExecutionChainSpec extends Specification{
    def mockExecutor = Mock(org.restler.http.Executor)
    def mockExecutionAdvice = Mock(ExecutionAdvice)

    List<ExecutionAdvice> advices = new ArrayList<ExecutionAdvice>()
    List<ExecutionAdvice> nullAdvices = new ArrayList<ExecutionAdvice>()

    def executionChain;

    def setup() {
        advices.add(mockExecutionAdvice)
        nullAdvices.add(null)

        executionChain = new ExecutionChain(mockExecutor, advices)
    }

    def "test null advices"() {
        when:
        new ExecutionChain(mockExecutor, nullAdvices)
        then:
        thrown(NullPointerException)
    }

    def "test chain execute"() {
        when:
        def request = new Request(null, null, null, null)
        executionChain.execute(request)
        then:
        1 * mockExecutionAdvice.advice(_, _)
    }
}
