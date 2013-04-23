datasources = {

  datasource(name: 'ds2') {
    domainClasses([Country, State])
    driverClassName('com.mysql.jdbc.Driver')
    url('jdbc:mysql://localhost/sharding_devel2')
    username('root')
    password('')
    dbCreate('create-drop')
    logSql(true)
    environments(['development'])
    dialect(org.hibernate.dialect.MySQL5InnoDBDialect)
    hibernate {
      cache {
        use_second_level_cache(false)
        use_query_cache(false)
      }
    }
  }

  datasource(name: 'ds3') {
    driverClassName('com.mysql.jdbc.Driver')
    url('jdbc:mysql://localhost/sharding_devel3')
    username('root')
    password('')
    dbCreate('create-drop')
    domainClasses([Visit])
    services(['transactionTest'])
    logSql(true)
    dialect(org.hibernate.dialect.MySQL5InnoDBDialect)
    pooled(true)
    environments(['development'])
    hibernate {
      cache {
        use_second_level_cache(true)
        use_query_cache(true)
      }
    }
  }
}
