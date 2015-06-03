package org.restler.testserver

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

RestController
public open class Controller {

    RequestMapping("get")
    open fun publicGet() = "OK"

    RequestMapping("secured/get")
    open fun securedGet() = "Secure OK"

    RequestMapping("forceLogout")
    open fun logout(): String {
        SecurityContextHolder.getContext().getAuthentication().setAuthenticated(false)
        return "OK"
    }

}