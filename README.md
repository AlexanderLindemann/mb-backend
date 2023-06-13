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

или для подключения к удаленному серверу на

eureka:
    client:
        serviceUrl:
            defaultZone: http://sd-dev:10203/eureka/


Так же для локальной сборки в родительском build.gradle 

изменить

credentials {
    username "$practusUser"
    password "$practusPassword"
}

на свои действующие логин и пароль