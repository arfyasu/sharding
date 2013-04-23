datasources = {

  datasource(name: 'index') {
    domainClasses(["Library"])
    driverClassName('com.mysql.jdbc.Driver')
    url('jdbc:mysql://localhost/sharding_devel')
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
        provider_class('net.sf.ehcache.hibernate.EhCacheProvider')
      }
    }
  }

  datasource(name: 'ds2') {
    driverClassName('com.mysql.jdbc.Driver')
    url('jdbc:mysql://localhost/sharding_devel2')
    username('root')
    password('')
    dbCreate('create-drop')
    domainClasses(["Book"])
    logSql(true)
    dialect(org.hibernate.dialect.MySQL5InnoDBDialect)
    pooled(true)
    environments(['development'])
    hibernate {
      cache {
        use_second_level_cache(true)
        use_query_cache(true)
        provider_class('net.sf.ehcache.hibernate.EhCacheProvider')
      }
    }
  }

}
