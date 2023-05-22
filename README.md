Для локального запуска: 

Изменить конфигурацию в application.yml:
c

eureka:
    client:
        serviceUrl:
            defaultZone: http://localhost:7001/eureka/
на

eureka:
    client:
        register-with-eureka: false
        fetch-registry: false

Так же для локальной сборки в родительском build.gradle 

изменить

credentials {
    username "$practusUser"
    password "$practusPassword"
}

на свои действующие логин и пароль